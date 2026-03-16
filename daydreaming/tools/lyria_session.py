"""Async Lyria RealTime session manager for the APC bridge.

Runs in a background daemon thread. The sync MIDI thread enqueues commands;
the async event loop dispatches them to the Lyria session and receives audio.

Usage (from APC bridge):
    session = LyriaSession(api_key="...", initial_bpm=120)
    session.start()
    session.enqueue_prompts([WeightedPrompt(text="dark ambient", weight=1.0)])
    session.enqueue_play()
    ...
    session.stop()
"""

from __future__ import annotations

import asyncio
import os
import queue
import sys
import threading
import time
import wave
from dataclasses import dataclass
from pathlib import Path
from typing import Any

# ---------------------------------------------------------------------------
# Command types (sent from sync MIDI thread → async consumer)
# ---------------------------------------------------------------------------


@dataclass
class _CmdPrompts:
    weighted_prompts: list[Any]


@dataclass
class _CmdConfig:
    kwargs: dict[str, Any]


@dataclass
class _CmdPlay:
    pass


@dataclass
class _CmdPause:
    pass


@dataclass
class _CmdToggleMute:
    field: str  # "mute_drums" or "mute_bass"


@dataclass
class _CmdResetContext:
    pass


@dataclass
class _CmdChoreographedReset:
    """BPM/scale change with smooth 4-step transition.

    Steps: ramp prompts down → reset_context → apply config → restore prompts.
    """

    config_changes: dict[str, Any]


@dataclass
class _CmdShutdown:
    pass


_Command = (
    _CmdPrompts
    | _CmdConfig
    | _CmdPlay
    | _CmdPause
    | _CmdToggleMute
    | _CmdResetContext
    | _CmdChoreographedReset
    | _CmdShutdown
)

# BPM changes trigger reset_context after a settle period
BPM_DEBOUNCE_S = 0.250

# Default sample rate — Lyria docs say 48kHz; JS examples show 44.1kHz.
# Python SDK doesn't expose sampleRateHz on audio chunks, so we assume 48kHz.
LYRIA_SAMPLE_RATE = 48_000

# Choreographed reset timing (seconds)
CHOREO_RAMP_DOWN_S = 0.5  # time to hold ramped-down prompts before reset
CHOREO_RESTORE_S = 0.5  # time to wait after reset before restoring prompts
CHOREO_RAMP_WEIGHT = 0.1  # scale all prompt weights to this during ramp-down


class LyriaSession:
    """Thread-safe async Lyria session wrapper.

    Public methods are non-blocking and safe to call from the sync MIDI thread.
    Audio playback and API calls happen in a background daemon thread.
    """

    def __init__(
        self,
        api_key: str | None = None,
        initial_bpm: int = 120,
        audio_sink: str = "speaker",
    ):
        key = (
            api_key
            or os.environ.get("GEMINI_API_KEY")
            or os.environ.get("GOOGLE_API_KEY")
        )
        if not key:
            raise ValueError(
                "No API key: pass api_key or set GEMINI_API_KEY / GOOGLE_API_KEY"
            )
        self._api_key = key
        self._audio_sink = audio_sink

        # Full config state — always sent as a complete struct
        self._config: dict[str, Any] = {
            "bpm": initial_bpm,
            "density": 0.5,
            "brightness": 0.5,
            "guidance": 4.0,
            "temperature": 1.1,
            "top_k": 40,
            "mute_drums": False,
            "mute_bass": False,
            "only_bass_and_drums": False,
            "music_generation_mode": "QUALITY",
        }

        self._queue: queue.Queue[_Command] = queue.Queue()
        self._thread: threading.Thread | None = None

        # Observable state (read by bridge for LED display)
        self.playing: bool = False
        self.mute_drums: bool = False
        self.mute_bass: bool = False

        # Last-sent prompts (for choreographed reset restore)
        self._last_prompts: list[Any] | None = None

    # -- Public API (non-blocking, sync) ------------------------------------

    def start(self) -> None:
        """Spawn background thread and connect to Lyria."""
        self._thread = threading.Thread(target=self._run_loop, daemon=True)
        self._thread.start()

    def stop(self) -> None:
        """Enqueue shutdown and wait for thread to finish."""
        self._queue.put(_CmdShutdown())
        if self._thread and self._thread.is_alive():
            self._thread.join(timeout=5.0)

    def enqueue_prompts(self, weighted_prompts: list[Any]) -> None:
        self._queue.put(_CmdPrompts(weighted_prompts))

    def enqueue_config(self, **kwargs: Any) -> None:
        self._queue.put(_CmdConfig(kwargs))

    def enqueue_play(self) -> None:
        self._queue.put(_CmdPlay())

    def enqueue_pause(self) -> None:
        self._queue.put(_CmdPause())

    def enqueue_toggle_mute(self, field_name: str) -> None:
        self._queue.put(_CmdToggleMute(field_name))

    def enqueue_reset_context(self) -> None:
        self._queue.put(_CmdResetContext())

    def enqueue_choreographed_reset(self, **config_changes: Any) -> None:
        """Smooth BPM/scale transition: ramp down → reset → apply → restore."""
        self._queue.put(_CmdChoreographedReset(config_changes))

    # -- Background thread --------------------------------------------------

    def _run_loop(self) -> None:
        """Entry point for daemon thread — runs asyncio event loop."""
        try:
            asyncio.run(self._async_main())
        except Exception as e:
            print(f"[LYRIA] thread error: {e}", file=sys.stderr)

    async def _async_main(self) -> None:
        from google import genai
        from google.genai import types

        client = genai.Client(
            api_key=self._api_key, http_options={"api_version": "v1alpha"}
        )

        print("[LYRIA] connecting...")
        async with client.aio.live.music.connect(
            model="models/lyria-realtime-exp"
        ) as session:
            print("[LYRIA] connected")

            # Send initial config
            await session.set_music_generation_config(
                types.LiveMusicGenerationConfig(**self._config)
            )

            # Set up audio sink
            sink = self._create_audio_sink()

            # Run command consumer and audio receiver concurrently
            await asyncio.gather(
                self._command_consumer(session, types),
                self._audio_receiver(session, sink),
            )

            # Cleanup
            if hasattr(sink, "close"):
                sink.close()
            print("[LYRIA] disconnected")

    async def _command_consumer(self, session: Any, types_mod: Any) -> None:
        """Poll command queue and dispatch to session."""
        bpm_last_change: float = 0.0
        bpm_pending_reset: bool = False
        bpm_pending_value: int | None = None

        while True:
            # Check for pending BPM reset (debounced choreographed transition)
            if bpm_pending_reset:
                elapsed = time.monotonic() - bpm_last_change
                if elapsed >= BPM_DEBOUNCE_S:
                    bpm_pending_reset = False
                    try:
                        # Choreographed: ramp → reset → apply BPM → restore
                        bpm_val = bpm_pending_value or self._config.get("bpm", 120)
                        print(f"[LYRIA] BPM choreographed reset: bpm={bpm_val}")

                        # Step 1: Ramp down
                        if self._last_prompts:
                            ramped = [
                                types_mod.WeightedPrompt(
                                    text=p.text,
                                    weight=max(0.05, p.weight * CHOREO_RAMP_WEIGHT),
                                )
                                for p in self._last_prompts
                            ]
                            await session.set_weighted_prompts(ramped)
                        await asyncio.sleep(CHOREO_RAMP_DOWN_S)

                        # Step 2+3: Reset + apply config
                        await session.reset_context()
                        self._config["bpm"] = bpm_val
                        await session.set_music_generation_config(
                            types_mod.LiveMusicGenerationConfig(**self._config)
                        )
                        await asyncio.sleep(CHOREO_RESTORE_S)

                        # Step 4: Restore prompts
                        if self._last_prompts:
                            await session.set_weighted_prompts(self._last_prompts)
                        print("[LYRIA] BPM choreographed reset complete")
                    except Exception as e:
                        print(f"[LYRIA] BPM reset error: {e}", file=sys.stderr)

            # Drain queue
            try:
                cmd = self._queue.get_nowait()
            except queue.Empty:
                await asyncio.sleep(0.01)
                continue

            try:
                if isinstance(cmd, _CmdShutdown):
                    if self.playing:
                        await session.stop()
                        self.playing = False
                    return

                elif isinstance(cmd, _CmdPlay):
                    await session.play()
                    self.playing = True

                elif isinstance(cmd, _CmdPause):
                    await session.pause()
                    self.playing = False

                elif isinstance(cmd, _CmdPrompts):
                    self._last_prompts = cmd.weighted_prompts
                    await session.set_weighted_prompts(cmd.weighted_prompts)

                elif isinstance(cmd, _CmdConfig):
                    old_bpm = self._config.get("bpm")
                    self._config.update(cmd.kwargs)
                    await session.set_music_generation_config(
                        types_mod.LiveMusicGenerationConfig(**self._config)
                    )
                    changes = " ".join(
                        f"{k}={v}" for k, v in cmd.kwargs.items()
                    )
                    print(f"[LYRIA] config: {changes}")
                    # BPM change → schedule choreographed reset after debounce
                    if cmd.kwargs.get("bpm") and cmd.kwargs["bpm"] != old_bpm:
                        bpm_last_change = time.monotonic()
                        bpm_pending_reset = True
                        bpm_pending_value = cmd.kwargs["bpm"]

                elif isinstance(cmd, _CmdToggleMute):
                    current = self._config.get(cmd.field, False)
                    self._config[cmd.field] = not current
                    # Update observable state
                    if cmd.field == "mute_drums":
                        self.mute_drums = not current
                    elif cmd.field == "mute_bass":
                        self.mute_bass = not current
                    await session.set_music_generation_config(
                        types_mod.LiveMusicGenerationConfig(**self._config)
                    )

                elif isinstance(cmd, _CmdResetContext):
                    await session.reset_context()

                elif isinstance(cmd, _CmdChoreographedReset):
                    # 4-step transition: ramp → reset → config → restore
                    changes_str = " ".join(
                        f"{k}={v}" for k, v in cmd.config_changes.items()
                    )
                    print(f"[LYRIA] choreographed reset: {changes_str}")

                    # Step 1: Ramp prompt weights down to ambient
                    if self._last_prompts:
                        ramped = []
                        for p in self._last_prompts:
                            ramped.append(
                                types_mod.WeightedPrompt(
                                    text=p.text,
                                    weight=max(0.05, p.weight * CHOREO_RAMP_WEIGHT),
                                )
                            )
                        await session.set_weighted_prompts(ramped)
                    await asyncio.sleep(CHOREO_RAMP_DOWN_S)

                    # Step 2: Reset context
                    await session.reset_context()

                    # Step 3: Apply new config
                    self._config.update(cmd.config_changes)
                    await session.set_music_generation_config(
                        types_mod.LiveMusicGenerationConfig(**self._config)
                    )
                    await asyncio.sleep(CHOREO_RESTORE_S)

                    # Step 4: Restore prompt weights
                    if self._last_prompts:
                        await session.set_weighted_prompts(self._last_prompts)
                    print("[LYRIA] choreographed reset complete")

            except Exception as e:
                print(
                    f"[LYRIA] command error ({type(cmd).__name__}): {e}",
                    file=sys.stderr,
                )

    async def _audio_receiver(self, session: Any, sink: Any) -> None:
        """Receive audio chunks and write to sink."""
        try:
            async for message in session.receive():
                if message.server_content and message.server_content.audio_chunks:
                    for chunk in message.server_content.audio_chunks:
                        if hasattr(sink, "write"):
                            sink.write(chunk.data)
        except asyncio.CancelledError:
            return
        except Exception as e:
            print(f"[LYRIA] audio receiver error: {e}", file=sys.stderr)

    # -- Audio sinks --------------------------------------------------------

    def _create_audio_sink(self) -> Any:
        if self._audio_sink == "wav":
            return _WavSink()

        # Try sounddevice first
        try:
            return _SoundDeviceSink()
        except Exception:
            pass

        # Fall back to pyaudio
        try:
            return _PyAudioSink()
        except Exception:
            pass

        print(
            "[LYRIA] No audio output available (install sounddevice or pyaudio),"
            " falling back to WAV dump",
            file=sys.stderr,
        )
        return _WavSink()


class _SoundDeviceSink:
    """Audio sink using sounddevice (preferred)."""

    def __init__(self) -> None:
        import sounddevice as sd

        self._stream = sd.RawOutputStream(
            samplerate=LYRIA_SAMPLE_RATE, channels=2, dtype="int16"
        )
        self._stream.start()
        print(f"[LYRIA] audio: sounddevice ({LYRIA_SAMPLE_RATE}Hz stereo)")

    def write(self, data: bytes) -> None:
        self._stream.write(data)

    def close(self) -> None:
        self._stream.stop()
        self._stream.close()


class _PyAudioSink:
    """Audio sink using PyAudio (fallback)."""

    def __init__(self) -> None:
        import pyaudio

        self._pa = pyaudio.PyAudio()
        self._stream = self._pa.open(
            format=pyaudio.paInt16,
            channels=2,
            rate=LYRIA_SAMPLE_RATE,
            output=True,
        )
        print(f"[LYRIA] audio: pyaudio ({LYRIA_SAMPLE_RATE}Hz stereo)")

    def write(self, data: bytes) -> None:
        self._stream.write(data)

    def close(self) -> None:
        self._stream.stop_stream()
        self._stream.close()
        self._pa.terminate()


class _WavSink:
    """Audio sink that dumps PCM to a WAV file."""

    def __init__(self) -> None:
        Path("outputs").mkdir(exist_ok=True)
        ts = time.strftime("%Y%m%d_%H%M%S")
        self._path = f"outputs/lyria_{ts}.wav"
        self._wf = wave.open(self._path, "wb")
        self._wf.setnchannels(2)
        self._wf.setsampwidth(2)
        self._wf.setframerate(LYRIA_SAMPLE_RATE)
        print(f"[LYRIA] audio: WAV file ({LYRIA_SAMPLE_RATE}Hz) → {self._path}")

    def write(self, data: bytes) -> None:
        self._wf.writeframes(data)

    def close(self) -> None:
        self._wf.close()
        print(f"[LYRIA] wrote {self._path}")

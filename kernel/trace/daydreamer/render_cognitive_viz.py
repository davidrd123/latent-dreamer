#!/usr/bin/env python3
"""Render a standalone cognitive playback HTML page from a kernel trace JSON."""

from __future__ import annotations

import argparse
import html
import json
from pathlib import Path
from typing import Any


GOAL_COLORS = {
    "rationalization": "#2ec4b6",
    "revenge": "#ef476f",
    "roving": "#f4a261",
    "reversal": "#5e60ce",
    "recovery": "#64b5f6",
    "rehearsal": "#84a98c",
    "repercussions": "#bc6c25",
}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("trace", type=Path, help="Kernel trace JSON path")
    parser.add_argument("--out", type=Path, required=True, help="HTML output path")
    parser.add_argument(
        "--title",
        default="Cognitive Trace Playback",
        help="Page title for the generated visualizer",
    )
    return parser.parse_args()


def load_trace(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as handle:
        payload = json.load(handle)
    if not isinstance(payload, dict) or "cycles" not in payload:
        raise ValueError(f"{path} is not a valid trace payload")
    return payload


def escape_json_for_html(payload: dict[str, Any]) -> str:
    raw = json.dumps(payload, ensure_ascii=False)
    return (
        raw.replace("&", "\\u0026")
        .replace("<", "\\u003c")
        .replace(">", "\\u003e")
        .replace("</script", "<\\/script")
    )


def build_html(payload: dict[str, Any], title: str) -> str:
    embedded = escape_json_for_html(payload)
    title = html.escape(title)
    template = """<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>__TITLE__</title>
  <style>
    :root {{
      --bg: #090b10;
      --bg-2: #11151f;
      --panel: rgba(18, 23, 34, 0.88);
      --panel-border: rgba(255, 255, 255, 0.08);
      --text: #f1eadf;
      --muted: #a8b0c0;
      --accent: #d4af6a;
      --danger: #d1495b;
      --dream: #5e60ce;
      --real: #8ecae6;
      --grid: rgba(255, 255, 255, 0.06);
      --shadow: 0 24px 60px rgba(0, 0, 0, 0.35);
      --radius: 18px;
      --mono: "SFMono-Regular", "Menlo", "Monaco", monospace;
      --serif: "Iowan Old Style", "Palatino Linotype", "Book Antiqua", serif;
      --sans: "Inter", "Segoe UI", sans-serif;
    }}

    * {{ box-sizing: border-box; }}
    html, body {{ height: 100%; }}
    body {{
      margin: 0;
      color: var(--text);
      background:
        radial-gradient(circle at top, rgba(94, 96, 206, 0.18), transparent 32%),
        radial-gradient(circle at 20% 80%, rgba(212, 175, 106, 0.12), transparent 24%),
        linear-gradient(180deg, #0a0c12 0%, #05070b 100%);
      font-family: var(--sans);
    }}

    .shell {{
      max-width: 1640px;
      margin: 0 auto;
      padding: 20px 24px 32px;
    }}

    .hero {{
      display: flex;
      align-items: end;
      justify-content: space-between;
      gap: 24px;
      margin-bottom: 18px;
    }}

    .hero h1 {{
      margin: 0;
      font-family: var(--serif);
      font-size: 2rem;
      letter-spacing: 0.01em;
      font-weight: 600;
    }}

    .hero .meta {{
      color: var(--muted);
      font-size: 0.95rem;
      line-height: 1.5;
      max-width: 840px;
    }}

    .controls {{
      display: grid;
      grid-template-columns: 1fr auto auto auto auto;
      gap: 12px;
      align-items: center;
      background: var(--panel);
      border: 1px solid var(--panel-border);
      border-radius: var(--radius);
      box-shadow: var(--shadow);
      padding: 14px 16px;
      margin-bottom: 16px;
      backdrop-filter: blur(10px);
    }}

    .buttons {{
      display: flex;
      gap: 10px;
      align-items: center;
      flex-wrap: wrap;
    }}

    button, select, input[type="range"] {{
      appearance: none;
      border: 1px solid rgba(255, 255, 255, 0.12);
      background: rgba(255, 255, 255, 0.04);
      color: var(--text);
      border-radius: 999px;
      font: inherit;
    }}

    button {{
      padding: 9px 14px;
      cursor: pointer;
      transition: background 120ms ease, transform 120ms ease;
    }}

    button:hover {{
      background: rgba(255, 255, 255, 0.09);
      transform: translateY(-1px);
    }}

    button.primary {{
      background: linear-gradient(135deg, rgba(94, 96, 206, 0.45), rgba(212, 175, 106, 0.24));
      border-color: rgba(212, 175, 106, 0.25);
    }}

    .readout {{
      display: flex;
      align-items: baseline;
      gap: 12px;
      justify-content: flex-end;
      color: var(--muted);
      font-family: var(--mono);
      font-size: 0.92rem;
    }}

    .readout strong {{
      color: var(--text);
      font-size: 1.05rem;
    }}

    .speed {{
      display: flex;
      align-items: center;
      gap: 10px;
      color: var(--muted);
      font-size: 0.92rem;
    }}

    .speed input {{
      width: 180px;
      height: 6px;
      padding: 0;
      border-radius: 999px;
      background: linear-gradient(90deg, rgba(94, 96, 206, 0.7), rgba(212, 175, 106, 0.7));
      border: none;
    }}

    .layout {{
      display: grid;
      grid-template-columns: 1.25fr 1fr;
      grid-template-rows: minmax(420px, 44vh) minmax(250px, 1fr);
      gap: 16px;
    }}

    .panel {{
      background: var(--panel);
      border: 1px solid var(--panel-border);
      border-radius: var(--radius);
      box-shadow: var(--shadow);
      overflow: hidden;
      display: flex;
      flex-direction: column;
      min-height: 0;
      backdrop-filter: blur(10px);
    }}

    .panel.full {{
      grid-column: 1 / -1;
    }}

    .panel-head {{
      display: flex;
      align-items: baseline;
      justify-content: space-between;
      gap: 16px;
      padding: 14px 16px 10px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    }}

    .panel-head h2 {{
      margin: 0;
      font-size: 1rem;
      letter-spacing: 0.04em;
      text-transform: uppercase;
    }}

    .panel-head .hint {{
      color: var(--muted);
      font-size: 0.84rem;
    }}

    .panel-body {{
      flex: 1;
      padding: 14px 16px 16px;
      min-height: 0;
    }}

    #landscape {{
      position: relative;
      overflow: hidden;
      background:
        radial-gradient(circle at 50% 45%, rgba(255, 255, 255, 0.03), transparent 34%),
        linear-gradient(180deg, rgba(255, 255, 255, 0.015), transparent 24%);
      border-radius: 14px;
      min-height: 340px;
    }}

    .situation-node {{
      position: absolute;
      transform: translate(-50%, -50%);
      border-radius: 999px;
      display: flex;
      align-items: center;
      justify-content: center;
      text-align: center;
      color: rgba(255, 255, 255, 0.96);
      font-size: 0.76rem;
      line-height: 1.25;
      transition:
        width 420ms ease,
        height 420ms ease,
        opacity 420ms ease,
        box-shadow 420ms ease,
        background 420ms ease,
        filter 420ms ease;
      box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.12);
      padding: 8px;
    }}

    .situation-label {{
      pointer-events: none;
      text-shadow: 0 2px 8px rgba(0, 0, 0, 0.45);
    }}

    .situation-label strong {{
      display: block;
      font-size: 0.82rem;
      letter-spacing: 0.02em;
    }}

    .situation-label span {{
      display: block;
      color: rgba(255, 255, 255, 0.72);
      font-family: var(--mono);
      margin-top: 2px;
    }}

    .goal-race {{
      display: grid;
      gap: 10px;
      align-content: start;
    }}

    .goal-row {{
      display: grid;
      grid-template-columns: 180px 1fr 48px;
      gap: 12px;
      align-items: center;
      min-height: 28px;
    }}

    .goal-name {{
      font-size: 0.86rem;
      color: var(--muted);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }}

    .goal-bar-wrap {{
      position: relative;
      height: 16px;
      border-radius: 999px;
      background: rgba(255, 255, 255, 0.06);
      overflow: hidden;
    }}

    .goal-bar {{
      position: absolute;
      inset: 0 auto 0 0;
      border-radius: 999px;
      transition: width 320ms ease, background 240ms ease, opacity 240ms ease;
      opacity: 0.62;
    }}

    .goal-row.selected .goal-bar {{
      opacity: 1;
      box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.18), 0 0 18px rgba(255, 255, 255, 0.12);
    }}

    .goal-strength {{
      text-align: right;
      font-family: var(--mono);
      font-size: 0.84rem;
      color: var(--text);
    }}

    .timeline-wrap {{
      display: grid;
      grid-template-columns: 1fr;
      gap: 14px;
    }}

    .lane-labels {{
      display: flex;
      justify-content: space-between;
      color: var(--muted);
      font-size: 0.82rem;
      text-transform: uppercase;
      letter-spacing: 0.08em;
    }}

    .timeline {{
      position: relative;
      height: 170px;
      border-radius: 16px;
      background:
        linear-gradient(180deg, rgba(142, 202, 230, 0.06) 0 46%, transparent 46% 54%, rgba(94, 96, 206, 0.09) 54% 100%),
        linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px);
      background-size: 100% 100%, calc(100% / 11) 100%;
      overflow: hidden;
    }}

    .timeline-center-line {{
      position: absolute;
      left: 0;
      right: 0;
      top: 50%;
      height: 1px;
      background: rgba(255, 255, 255, 0.12);
    }}

    .timeline-node {{
      position: absolute;
      width: 18px;
      height: 18px;
      border-radius: 50%;
      margin-left: -9px;
      margin-top: -9px;
      border: 2px solid rgba(255, 255, 255, 0.85);
      box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.04);
      transition: transform 220ms ease, box-shadow 220ms ease, opacity 220ms ease;
    }}

    .timeline-node.current {{
      transform: scale(1.22);
      box-shadow: 0 0 0 4px rgba(212, 175, 106, 0.25), 0 0 24px rgba(212, 175, 106, 0.18);
    }}

    .timeline-node.future {{
      opacity: 0.18;
    }}

    .timeline-node.past {{
      opacity: 0.55;
    }}

    .timeline-node-label {{
      position: absolute;
      font-family: var(--mono);
      font-size: 0.72rem;
      color: var(--muted);
      transform: translate(-50%, 0);
      white-space: nowrap;
    }}

    .timeline-branch {{
      position: absolute;
      border-left: 1.5px dashed rgba(255, 255, 255, 0.24);
      opacity: 0.8;
    }}

    .thought-stream {{
      display: grid;
      gap: 12px;
      overflow: auto;
      padding-right: 4px;
    }}

    .thought-entry {{
      padding: 14px 14px 12px;
      border-radius: 14px;
      background: rgba(255, 255, 255, 0.04);
      border: 1px solid rgba(255, 255, 255, 0.06);
      transition: border-color 220ms ease, background 220ms ease;
    }}

    .thought-entry.current {{
      border-color: rgba(212, 175, 106, 0.28);
      background: rgba(212, 175, 106, 0.08);
    }}

    .thought-head {{
      display: flex;
      justify-content: space-between;
      gap: 12px;
      margin-bottom: 8px;
      font-family: var(--mono);
      font-size: 0.82rem;
      color: var(--muted);
    }}

    .thought-copy {{
      font-family: var(--serif);
      font-size: 1.02rem;
      line-height: 1.48;
    }}

    .thought-copy strong {{
      color: var(--accent);
      font-weight: 600;
    }}

    .tags {{
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 10px;
    }}

    .tag {{
      padding: 4px 8px;
      border-radius: 999px;
      background: rgba(255, 255, 255, 0.06);
      color: var(--muted);
      font-size: 0.78rem;
      font-family: var(--mono);
    }}

    .legend {{
      display: flex;
      flex-wrap: wrap;
      gap: 10px 14px;
      margin-top: 12px;
      color: var(--muted);
      font-size: 0.8rem;
    }}

    .legend-item {{
      display: inline-flex;
      align-items: center;
      gap: 6px;
    }}

    .legend-swatch {{
      width: 10px;
      height: 10px;
      border-radius: 50%;
      display: inline-block;
    }}

    .footer-note {{
      margin-top: 14px;
      color: var(--muted);
      font-size: 0.84rem;
      line-height: 1.45;
    }}

    @media (max-width: 1200px) {{
      .layout {{
        grid-template-columns: 1fr;
        grid-template-rows: auto;
      }}

      .controls {{
        grid-template-columns: 1fr;
      }}

      .readout {{
        justify-content: flex-start;
      }}
    }}
  </style>
</head>
<body>
  <div class="shell">
    <div class="hero">
      <div>
        <h1>__TITLE__</h1>
        <div class="meta" id="meta"></div>
      </div>
    </div>

    <div class="controls">
      <div class="buttons">
        <button id="playPause" class="primary">Play</button>
        <button id="prevCycle">Back</button>
        <button id="nextCycle">Step</button>
      </div>
      <div class="speed">
        <label for="speedRange">Speed</label>
        <input id="speedRange" type="range" min="0" max="4" step="1" value="2">
        <span id="speedValue">1×</span>
      </div>
      <div class="readout">
        <span>Cycle <strong id="cycleReadout">1</strong></span>
        <span id="goalReadout">goal</span>
      </div>
    </div>

    <div class="layout">
      <section class="panel">
        <div class="panel-head">
          <h2>Situation Landscape</h2>
          <div class="hint">Activation as size, ripeness as focus, emotion as color</div>
        </div>
        <div class="panel-body">
          <div id="landscape"></div>
          <div class="legend" id="situationLegend"></div>
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h2>Goal Race</h2>
          <div class="hint">Top 8 candidates from the full ladder</div>
        </div>
        <div class="panel-body">
          <div class="goal-race" id="goalRace"></div>
          <div class="footer-note">
            The selected goal is highlighted. Bars animate from the trace’s full
            candidate ladder rather than only the winner.
          </div>
        </div>
      </section>

      <section class="panel full">
        <div class="panel-head">
          <h2>Timeline</h2>
          <div class="hint">Reality above, daydreaming below</div>
        </div>
        <div class="panel-body">
          <div class="timeline-wrap">
            <div class="lane-labels">
              <span>Reality / real planning</span>
              <span>Imaginary / daydreaming</span>
            </div>
            <div class="timeline" id="timeline">
              <div class="timeline-center-line"></div>
            </div>
          </div>
        </div>
      </section>

      <section class="panel full">
        <div class="panel-head">
          <h2>Thought Stream</h2>
          <div class="hint">Rule-based narration from current trace fields</div>
        </div>
        <div class="panel-body">
          <div class="thought-stream" id="thoughtStream"></div>
        </div>
      </section>
    </div>
  </div>

  <script id="trace-data" type="application/json">__EMBEDDED_TRACE__</script>
  <script>
    const TRACE = JSON.parse(document.getElementById("trace-data").textContent);
    const GOAL_COLORS = __GOAL_COLORS__;
    const SPEEDS = [4000, 2500, 1600, 950, 550];
    const SPEED_LABELS = ["0.5×", "0.75×", "1×", "2×", "4×"];
    const SITUATION_LAYOUT = {{
      s1_seeing_through: {{ x: 23, y: 30 }},
      s2_the_mission: {{ x: 72, y: 26 }},
      s3_the_edge: {{ x: 30, y: 72 }},
      s4_the_ring: {{ x: 72, y: 68 }},
      s5_the_others: {{ x: 52, y: 52 }},
    }};

    const cycles = TRACE.cycles || [];
    let currentIndex = 0;
    let playing = false;
    let timer = null;
    let speedIndex = 2;

    const metaEl = document.getElementById("meta");
    const cycleReadoutEl = document.getElementById("cycleReadout");
    const goalReadoutEl = document.getElementById("goalReadout");
    const playPauseEl = document.getElementById("playPause");
    const prevCycleEl = document.getElementById("prevCycle");
    const nextCycleEl = document.getElementById("nextCycle");
    const speedRangeEl = document.getElementById("speedRange");
    const speedValueEl = document.getElementById("speedValue");
    const landscapeEl = document.getElementById("landscape");
    const goalRaceEl = document.getElementById("goalRace");
    const timelineEl = document.getElementById("timeline");
    const thoughtStreamEl = document.getElementById("thoughtStream");
    const situationLegendEl = document.getElementById("situationLegend");

    function clamp(value, min, max) {{
      return Math.max(min, Math.min(max, value));
    }}

    function titleCase(text) {{
      return (text || "").replace(/_/g, " ");
    }}

    function shortSituation(id) {{
      return (id || "").replace(/^s\\d+_/, "").replace(/_/g, " ");
    }}

    function titleizeId(value) {{
      return (value || "").replace(/_/g, " ");
    }}

    function branchEventsForCycle(cycle) {{
      return Array.isArray(cycle.branch_events) ? cycle.branch_events : [];
    }}

    function emotionShiftSummary(cycle) {{
      const shifts = Array.isArray(cycle.emotion_shifts) ? cycle.emotion_shifts : [];
      if (!shifts.length) return null;
      const strongest = [...shifts].sort((a, b) => Math.abs(Number(b.delta || 0)) - Math.abs(Number(a.delta || 0)))[0];
      if (!strongest) return null;
      const delta = Number(strongest.delta || 0);
      const sign = delta > 0 ? "+" : "";
      const label = strongest.affect || strongest.emotion_id || strongest.valence || "emotion";
      const situation = strongest.situation_id ? ` in ${{shortSituation(strongest.situation_id)}}` : "";
      return `${{titleizeId(label)}} shifts ${{sign}}${{delta.toFixed(2)}}${{situation}}`;
    }}

    function emotionalStateSummary(cycle) {{
      const states = Array.isArray(cycle.emotional_state) ? cycle.emotional_state : [];
      if (!states.length) return null;
      const strongest = [...states].sort((a, b) => Number(b.strength || 0) - Number(a.strength || 0))[0];
      if (!strongest) return null;
      const label = strongest.affect || strongest.emotion_id || strongest.valence || "emotion";
      const strength = Number(strongest.strength || 0).toFixed(2);
      const role = strongest.role ? ` (${{titleizeId(strongest.role)}})` : "";
      return `Emotional state: ${{titleizeId(label)}} at ${{strength}}${{role}}`;
    }}

    function dominantEmotion(state) {{
      const metrics = [
        ["threat", Number(state.threat || 0)],
        ["anger", Number(state.anger || 0)],
        ["hope", Number(state.hope || 0)],
        ["grief", Number(state.grief || 0)],
        ["waiting", Number(state.waiting || 0)],
      ].sort((a, b) => b[1] - a[1]);
      return metrics[0][0];
    }}

    function situationColor(state) {{
      const emotion = dominantEmotion(state);
      if (emotion === "threat" || emotion === "anger") {{
        const heat = clamp((Number(state.threat || 0) + Number(state.anger || 0)) / 1.7, 0.18, 1);
        return `rgba(209, 73, 91, ${{0.20 + heat * 0.38}})`;
      }}
      if (emotion === "hope") {{
        const glow = clamp(Number(state.hope || 0), 0.18, 1);
        return `rgba(212, 175, 106, ${{0.18 + glow * 0.36}})`;
      }}
      if (emotion === "grief") {{
        const depth = clamp(Number(state.grief || 0), 0.18, 1);
        return `rgba(78, 103, 145, ${{0.18 + depth * 0.34}})`;
      }}
      if (emotion === "waiting") {{
        const wait = clamp(Number(state.waiting || 0), 0.18, 1);
        return `rgba(180, 145, 77, ${{0.18 + wait * 0.3}})`;
      }}
      return "rgba(133, 144, 166, 0.24)";
    }}

    function thoughtForCycle(cycle, previousCycle) {{
      const goal = cycle.selected_goal || {{}};
      const situation = shortSituation(goal.situation_id || "unknown");
      const goalType = goal.goal_type || "unknown";
      const chosen = cycle.chosen_node_id ? ` [${{cycle.chosen_node_id}}]` : "";
      const line1 = [];
      const line2 = [];
      const branchEvents = branchEventsForCycle(cycle);
      line1.push(`<strong>${{titleCase(goalType)}}</strong> takes ${{
        situation ? "the " + situation : "the scene"
      }}.`);
      if (goalType === "reversal") {{
        line1.push("Imagining an alternative past.");
      }} else if (goalType === "roving") {{
        line1.push("Slipping sideways into distraction.");
      }} else if (goalType === "rehearsal") {{
        line1.push("Practicing what still feels possible.");
      }} else if (goalType === "rationalization") {{
        line1.push("Trying to make the failure feel livable.");
      }} else if (goalType === "revenge") {{
        line1.push("Directed anger takes the wheel.");
      }} else if (goalType === "repercussions") {{
        line1.push("Watching consequences spread.");
      }} else if (goalType === "recovery") {{
        line1.push("Trying to repair the goal directly.");
      }}

      if (branchEvents.length) {{
        const branch = branchEvents[0];
        const factIds = (branch.fact_ids || []).map(titleizeId);
        const family = titleizeId(branch.family || "branch");
        line2.push(`Branch event: ${{family}} opens ${{branch.target_context || "branch context"}} with ${{factIds.slice(0, 3).join(", ") || "new facts"}}.`);
        if ((branch.retracted_fact_ids || []).length) {{
          line2.push(`It retracts ${{branch.retracted_fact_ids.map(titleizeId).slice(0, 2).join(", ")}}.`);
        }}
      }} else if (cycle.sprouted_contexts && cycle.sprouted_contexts.length) {{
        line2.push(`Branch sprouting: ${{cycle.sprouted_contexts.join(", ")}}.`);
      }}

      const selection = cycle.selection || {{}};
      if (selection.adapter_selected_situation && selection.adapter_selected_situation !== goal.situation_id) {{
        line2.push(`Adapter consequences wake ${{shortSituation(selection.adapter_selected_situation)}}.`);
      }}

      if (previousCycle && cycle.situations && previousCycle.situations) {{
        const deltas = [];
        for (const [sid, state] of Object.entries(cycle.situations)) {{
          const prev = previousCycle.situations[sid];
          if (!prev) continue;
          const delta = Number(state.activation || 0) - Number(prev.activation || 0);
          if (Math.abs(delta) >= 0.12) {{
            deltas.push(`${{shortSituation(sid)}} ${delta > 0 ? "wakes" : "falls"} (${{
              delta > 0 ? "+" : ""
            }}${{delta.toFixed(2)}})`);
          }}
        }}
        if (deltas.length) {{
          line2.push(deltas.slice(0, 2).join(". ") + ".");
        }}
      }}

      const topRetrieval = (cycle.retrieved || [])[0];
      if (topRetrieval) {{
        line2.push(`Retrieval leads with ${{topRetrieval.node_id}} via ${{topRetrieval.overlap?.slice(0, 3).join(", ") || "active cues"}}.${{chosen ? "" : ""}}`);
      }}

      const shiftLine = emotionShiftSummary(cycle);
      if (shiftLine) {{
        line2.push(shiftLine + ".");
      }}

      const stateLine = emotionalStateSummary(cycle);
      if (stateLine) {{
        line2.push(stateLine + ".");
      }}

      return {{
        headline: `Cycle ${{cycle.cycle}}`,
        copy: `${{line1.join(" ")}} ${{line2.join(" ")}}${{chosen}}`.trim(),
        tags: [
          `${{goal.goal_type || "goal"}} × ${{goal.situation_id || "unknown"}}`,
          branchEvents[0]?.family || selection.edge_kind || selection.goal_family || "highest_score",
        ],
      }};
    }}

    function renderMeta() {{
      const seed = TRACE.seed || "unknown";
      const started = TRACE.started_at || "unknown";
      const cycleCount = cycles.length;
      metaEl.textContent = `Seed ${seed} · ${cycleCount} cycles · started ${started}`;
    }}

    function renderLegend(currentCycle) {{
      const items = Object.entries(currentCycle.situations || {{}}).map(([sid, state]) => {{
        return `<span class="legend-item"><span class="legend-swatch" style="background:${{situationColor(state)}}"></span>${{shortSituation(sid)}}</span>`;
      }});
      situationLegendEl.innerHTML = items.join("");
    }}

    function renderLandscape(currentCycle) {{
      const situations = currentCycle.situations || {{}};
      landscapeEl.innerHTML = "";
      for (const [sid, state] of Object.entries(situations)) {{
        const layout = SITUATION_LAYOUT[sid] || {{ x: 50, y: 50 }};
        const activation = Number(state.activation || 0);
        const ripeness = Number(state.ripeness || 0);
        const size = 70 + activation * 150;
        const opacity = 0.28 + activation * 0.6;
        const blur = (1 - ripeness) * 7;
        const node = document.createElement("div");
        node.className = "situation-node";
        node.style.left = `${{layout.x}}%`;
        node.style.top = `${{layout.y}}%`;
        node.style.width = `${{size}}px`;
        node.style.height = `${{size}}px`;
        node.style.opacity = opacity.toFixed(3);
        node.style.background = `radial-gradient(circle at 35% 35%, rgba(255,255,255,0.22), transparent 32%), ${{situationColor(state)}}`;
        node.style.filter = `blur(${{blur.toFixed(2)}}px) saturate(${{
          (1 + ripeness * 0.45).toFixed(2)
        }})`;
        node.innerHTML = `<div class="situation-label"><strong>${{shortSituation(sid)}}</strong><span>a:${{activation.toFixed(2)}} r:${{ripeness.toFixed(2)}}</span></div>`;
        landscapeEl.appendChild(node);
      }}
      renderLegend(currentCycle);
    }}

    function renderGoalRace(currentCycle) {{
      const selected = currentCycle.selected_goal || {{}};
      const ladder = (currentCycle.top_candidates || []).slice(0, 8);
      goalRaceEl.innerHTML = "";
      ladder.forEach((candidate) => {{
        const row = document.createElement("div");
        const selectedMatch =
          candidate.goal_type === selected.goal_type &&
          candidate.situation_id === selected.situation_id;
        row.className = "goal-row" + (selectedMatch ? " selected" : "");
        const color = GOAL_COLORS[candidate.goal_type] || "#8892a6";
        row.innerHTML = `
          <div class="goal-name">${{candidate.goal_type}} × ${{candidate.situation_id}}</div>
          <div class="goal-bar-wrap"><div class="goal-bar" style="width:${{Math.max(2, (candidate.strength || 0) * 100)}}%; background:${{color}}"></div></div>
          <div class="goal-strength">${{Number(candidate.strength || 0).toFixed(3)}}</div>
        `;
        goalRaceEl.appendChild(row);
      }});
    }}

    function renderTimeline(currentCycle) {{
      timelineEl.querySelectorAll(".timeline-node, .timeline-node-label, .timeline-branch").forEach((node) => node.remove());
      const width = timelineEl.clientWidth || 1000;
      const topY = 42;
      const bottomY = 128;
      cycles.forEach((cycle, index) => {{
        const x = 36 + (width - 72) * (index / Math.max(1, cycles.length - 1));
        const planningType = cycle.selected_goal?.planning_type || "imaginary";
        const y = planningType === "real" ? topY : bottomY;
        const node = document.createElement("div");
        node.className = "timeline-node";
        node.classList.add(index < currentIndex ? "past" : index === currentIndex ? "current" : "future");
        node.style.left = `${{x}}px`;
        node.style.top = `${{y}}px`;
        node.style.background = GOAL_COLORS[cycle.selected_goal?.goal_type] || "#9aa3b2";
        timelineEl.appendChild(node);

        const label = document.createElement("div");
        label.className = "timeline-node-label";
        label.style.left = `${{x}}px`;
        label.style.top = `${{y + (planningType === "real" ? -22 : 18)}}px`;
        label.textContent = `c${{cycle.cycle}}`;
        timelineEl.appendChild(label);

        if (branchEventsForCycle(cycle).length || (cycle.sprouted_contexts && cycle.sprouted_contexts.length)) {{
          const branch = document.createElement("div");
          branch.className = "timeline-branch";
          branch.style.left = `${{x}}px`;
          branch.style.top = `${{y + 10}}px`;
          branch.style.height = `${{34 + 8 * branchEventsForCycle(cycle).length}}px`;
          timelineEl.appendChild(branch);
        }}
      }});
    }}

    function renderThoughts(currentCycle) {{
      thoughtStreamEl.innerHTML = "";
      cycles.forEach((cycle, index) => {{
        const previous = index > 0 ? cycles[index - 1] : null;
        const thought = thoughtForCycle(cycle, previous);
        const entry = document.createElement("article");
        entry.className = "thought-entry" + (index === currentIndex ? " current" : "");
        entry.innerHTML = `
          <div class="thought-head">
            <span>${{thought.headline}}</span>
            <span>${{cycle.selected_goal?.planning_type || "imaginary"}}</span>
          </div>
          <div class="thought-copy">${{thought.copy}}</div>
          <div class="tags">${{thought.tags.map((tag) => `<span class="tag">${{tag}}</span>`).join("")}}</div>
        `;
        thoughtStreamEl.appendChild(entry);
      }});

      const current = thoughtStreamEl.querySelector(".thought-entry.current");
      if (current) {{
        current.scrollIntoView({{ block: "nearest", behavior: "smooth" }});
      }}
    }}

    function render() {{
      const cycle = cycles[currentIndex];
      if (!cycle) return;
      cycleReadoutEl.textContent = cycle.cycle;
      goalReadoutEl.textContent = `${{cycle.selected_goal?.goal_type || "goal"}} × ${{cycle.selected_goal?.situation_id || "unknown"}}`;
      renderLandscape(cycle);
      renderGoalRace(cycle);
      renderTimeline(cycle);
      renderThoughts(cycle);
    }}

    function stopPlayback() {{
      if (timer) {{
        clearTimeout(timer);
        timer = null;
      }}
      playing = false;
      playPauseEl.textContent = "Play";
    }}

    function schedulePlayback() {{
      if (!playing) return;
      timer = window.setTimeout(() => {{
        if (currentIndex >= cycles.length - 1) {{
          stopPlayback();
          return;
        }}
        currentIndex += 1;
        render();
        schedulePlayback();
      }}, SPEEDS[speedIndex]);
    }}

    function togglePlayback() {{
      if (playing) {{
        stopPlayback();
      }} else {{
        playing = true;
        playPauseEl.textContent = "Pause";
        schedulePlayback();
      }}
    }}

    playPauseEl.addEventListener("click", togglePlayback);
    prevCycleEl.addEventListener("click", () => {{
      stopPlayback();
      currentIndex = Math.max(0, currentIndex - 1);
      render();
    }});
    nextCycleEl.addEventListener("click", () => {{
      stopPlayback();
      currentIndex = Math.min(cycles.length - 1, currentIndex + 1);
      render();
    }});
    speedRangeEl.addEventListener("input", (event) => {{
      speedIndex = Number(event.target.value);
      speedValueEl.textContent = SPEED_LABELS[speedIndex];
      if (playing) {{
        stopPlayback();
        togglePlayback();
      }}
    }});

    speedValueEl.textContent = SPEED_LABELS[speedIndex];
    renderMeta();
    render();
  </script>
</body>
</html>
"""
    template = template.replace("{{", "{").replace("}}", "}")
    return (
        template.replace("__TITLE__", title)
        .replace("__EMBEDDED_TRACE__", embedded)
        .replace("__GOAL_COLORS__", json.dumps(GOAL_COLORS, ensure_ascii=False))
    )


def main() -> None:
    args = parse_args()
    payload = load_trace(args.trace)
    html_text = build_html(payload, args.title)
    args.out.parent.mkdir(parents=True, exist_ok=True)
    args.out.write_text(html_text, encoding="utf-8")
    print(args.out.resolve())


if __name__ == "__main__":
    main()

# Visual Pipeline — Future Reference

Date: 2026-03-25. Not a sprint item. A note for later scoping.

## The pipeline

1. World bible (exists: Patrick's vault)
2. Consistent characters in Midjourney (stable refs across poses/scenes)
3. Scene compositions via image edit models (daydream_vision already
   does Gemini edits + segmentation)
4. Perspective variants (different angles/lighting of same scene)
5. Video generation from stills (WAN 2.1 or similar)
6. Fine-tune Krea 14b real-time on stills + videos + prompts
   (bake the world's visual language into model weights)
7. Real-time prompting against the fine-tuned model via Scope,
   driven by the kernel's thought beat image/audio hints

## What already exists toward this

- World bible + character references (vendored in this repo)
- `daydream_vision` with Gemini image gen/edit
- Graffito LoRA (`gr4f1tt0_v1_qwen`)
- `image_hint` / `audio_hint` fields from RuntimeThoughtBeat
- Scope REST endpoints for real-time video
- APC Mini bridge from earlier rounds

## What's missing

- The training loop (generated assets → fine-tuned real-time model)
- Krea 14b integration
- Prompt formatting from kernel hints → real-time model input

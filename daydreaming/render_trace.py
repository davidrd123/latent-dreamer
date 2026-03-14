#!/usr/bin/env python3
"""
Graffito v0 trace renderer.

Reads a dream graph and trace, applies the template passthrough renderer,
and outputs a Scope playlist (one prompt per line) ready for:

    video-cli playlist load <file>

Usage:
    python render_trace.py \
        --graph fixtures/graffito_v0_scenes_3_4.yaml \
        --trace fixtures/graffito_v0_trace.yaml \
        --output out/graffito_v0_playlist.txt
"""

import argparse
from pathlib import Path

import yaml


# --- Template passthrough renderer (doc 17, v0) ---

CAPSTONES = {
    "REVERSAL": "Something has shifted beneath the surface.",
    "ROVING": "The moment feels like an escape, warm and expansive.",
    "RATIONALIZATION": "The scene settles into calm clarity.",
    "REHEARSAL": "The gesture repeats, building toward something.",
    "REVENGE": "The energy sharpens, directed and focused.",
}


def render_v0(node, family=None):
    """Template passthrough renderer. No LLM call."""
    prompt = node["visual_description"].strip()
    if family and family in CAPSTONES:
        prompt += " " + CAPSTONES[family]
    return prompt


def main():
    parser = argparse.ArgumentParser(
        description="Render a dream trace to a Scope playlist"
    )
    parser.add_argument(
        "--graph", required=True, help="Path to dream graph YAML"
    )
    parser.add_argument(
        "--trace", required=True, help="Path to trace YAML"
    )
    parser.add_argument(
        "--output", default="out/graffito_v0_playlist.txt",
        help="Output playlist file (one prompt per line)",
    )
    args = parser.parse_args()

    with open(args.graph) as f:
        graph = yaml.safe_load(f)
    with open(args.trace) as f:
        trace = yaml.safe_load(f)

    # Index nodes by ID
    nodes_by_id = {n["id"]: n for n in graph["nodes"]}

    # Render each trace cycle
    lines = []
    for entry in trace["trace"]:
        node = nodes_by_id[entry["node"]]
        family = entry.get("family")
        prompt = render_v0(node, family)
        lines.append(prompt)

    # Write playlist
    output_path = Path(args.output)
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text("\n".join(lines) + "\n")

    print(f"Wrote {len(lines)} prompts to {output_path}")
    print(f"\nLoad in Scope with:")
    print(f"  video-cli playlist load {output_path}")


if __name__ == "__main__":
    main()

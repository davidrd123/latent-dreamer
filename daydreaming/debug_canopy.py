#!/usr/bin/env python3
"""
Render a kernel reporter-log JSON into an offline debug canopy HTML page.

Usage:
    uv run python daydreaming/debug_canopy.py \
        --trace-path out/graffito_miniworld.json \
        --output out/graffito_miniworld_canopy.html
"""

from __future__ import annotations

import argparse
import html
import json
from collections import Counter
from pathlib import Path
from typing import Any


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Render a debug canopy HTML page from a kernel trace JSON.")
    parser.add_argument("--trace-path", type=Path, required=True, help="Path to reporter-log JSON.")
    parser.add_argument("--output", type=Path, default=None, help="Output HTML path. Defaults beside trace file.")
    parser.add_argument("--title", type=str, default=None, help="Optional page title.")
    return parser.parse_args()


def load_log(path: Path) -> dict[str, Any]:
    with path.open("r", encoding="utf-8") as handle:
        payload = json.load(handle)
    if isinstance(payload, dict) and "cycles" in payload:
        return payload
    if isinstance(payload, dict) and isinstance(payload.get("log"), dict) and "cycles" in payload["log"]:
        return payload["log"]
    raise ValueError(f"{path} does not look like a reporter-log JSON payload")


def cycle_family(cycle: dict[str, Any]) -> str:
    selection = cycle.get("selection") or {}
    selected_goal = cycle.get("selected_goal") or {}
    return (
        selection.get("goal_family")
        or selected_goal.get("goal_type")
        or "unknown"
    )


def summarize(log: dict[str, Any]) -> dict[str, Any]:
    cycles = list(log.get("cycles", []))
    family_counts = Counter(cycle_family(cycle) for cycle in cycles)
    rule_access_events = 0
    promotion_events = 0
    use_records = 0
    episodes_added = 0
    runtime_thought_cycles = 0
    for cycle in cycles:
        delta = cycle.get("world_delta") or {}
        rule_access_events += len(((delta.get("rule_access") or {}).get("changed") or []))
        promotion_events += len(((delta.get("promotion") or {}).get("events") or []))
        use_records += len(((delta.get("episode_use") or {}).get("added") or []))
        episodes_added += len(((delta.get("episodes") or {}).get("added") or []))
        if cycle.get("runtime_thought"):
            runtime_thought_cycles += 1
    return {
        "cycle_count": len(cycles),
        "family_counts": dict(sorted(family_counts.items())),
        "rule_access_events": rule_access_events,
        "promotion_events": promotion_events,
        "use_records": use_records,
        "episodes_added": episodes_added,
        "runtime_thought_cycles": runtime_thought_cycles,
    }


def output_path_for(trace_path: Path, explicit: Path | None) -> Path:
    if explicit is not None:
        return explicit
    return trace_path.with_name(f"{trace_path.stem}_canopy.html")


def build_html(log: dict[str, Any], *, title: str) -> str:
    summary = summarize(log)
    log_json = json.dumps(log, separators=(",", ":"), ensure_ascii=False)
    summary_json = json.dumps(summary, separators=(",", ":"), ensure_ascii=False)
    safe_title = html.escape(title)
    return f"""<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>{safe_title}</title>
  <style>
    :root {{
      color-scheme: dark;
      --bg: #0d1117;
      --panel: #161b22;
      --muted: #8b949e;
      --text: #e6edf3;
      --accent: #58a6ff;
      --accent-soft: rgba(88, 166, 255, 0.14);
      --good: #3fb950;
      --warn: #d29922;
      --bad: #f85149;
      --border: #30363d;
      --code: #0b1220;
    }}
    * {{ box-sizing: border-box; }}
    body {{
      margin: 0;
      font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      background: linear-gradient(180deg, #0d1117 0%, #10151f 100%);
      color: var(--text);
      height: 100vh;
      overflow: hidden;
      display: grid;
      grid-template-rows: auto minmax(0, 1fr);
    }}
    header {{
      padding: 18px 20px 14px;
      border-bottom: 1px solid var(--border);
      position: sticky;
      top: 0;
      z-index: 10;
      backdrop-filter: blur(10px);
      background: rgba(13, 17, 23, 0.86);
    }}
    h1 {{
      margin: 0 0 10px;
      font-size: 22px;
      line-height: 1.2;
    }}
    .summary {{
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
      gap: 10px;
    }}
    .card {{
      background: var(--panel);
      border: 1px solid var(--border);
      border-radius: 12px;
      padding: 12px 14px;
    }}
    .label {{
      color: var(--muted);
      font-size: 11px;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      margin-bottom: 6px;
    }}
    .value {{
      font-size: 24px;
      font-weight: 700;
    }}
    .layout {{
      display: grid;
      grid-template-columns: 340px minmax(0, 1fr);
      min-height: 0;
      height: 100%;
    }}
    aside {{
      border-right: 1px solid var(--border);
      padding: 12px;
      overflow: auto;
      background: rgba(10, 14, 20, 0.76);
      min-height: 0;
    }}
    main {{
      padding: 16px;
      overflow: auto;
      min-height: 0;
    }}
    .cycle-button {{
      width: 100%;
      text-align: left;
      background: var(--panel);
      border: 1px solid var(--border);
      color: var(--text);
      border-radius: 10px;
      padding: 10px 12px;
      margin-bottom: 8px;
      cursor: pointer;
    }}
    .cycle-button:hover,
    .cycle-button.active {{
      border-color: var(--accent);
      background: linear-gradient(180deg, var(--accent-soft), rgba(88,166,255,0.06));
    }}
    .cycle-row {{
      display: flex;
      justify-content: space-between;
      gap: 12px;
      align-items: center;
    }}
    .cycle-family {{
      color: var(--accent);
      font-size: 12px;
      text-transform: uppercase;
      letter-spacing: 0.06em;
    }}
    .badge-row {{
      display: flex;
      gap: 6px;
      flex-wrap: wrap;
      margin-top: 8px;
    }}
    .badge {{
      border-radius: 999px;
      padding: 3px 8px;
      font-size: 11px;
      border: 1px solid var(--border);
      color: var(--muted);
    }}
    .badge.good {{ color: var(--good); border-color: rgba(63,185,80,0.45); }}
    .badge.warn {{ color: var(--warn); border-color: rgba(210,153,34,0.45); }}
    .badge.bad {{ color: var(--bad); border-color: rgba(248,81,73,0.45); }}
    .section {{
      margin-bottom: 16px;
      background: var(--panel);
      border: 1px solid var(--border);
      border-radius: 12px;
      overflow: hidden;
    }}
    .section h2 {{
      margin: 0;
      font-size: 14px;
      padding: 12px 14px;
      border-bottom: 1px solid var(--border);
      letter-spacing: 0.04em;
      text-transform: uppercase;
      color: var(--muted);
    }}
    .section-body {{
      padding: 14px;
    }}
    .grid-two {{
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 14px;
    }}
    table {{
      width: 100%;
      border-collapse: collapse;
    }}
    th, td {{
      text-align: left;
      border-bottom: 1px solid rgba(255,255,255,0.06);
      padding: 8px 0;
      vertical-align: top;
      font-size: 13px;
    }}
    th {{
      color: var(--muted);
      font-weight: 600;
      font-size: 12px;
      text-transform: uppercase;
      letter-spacing: 0.04em;
    }}
    ul {{
      margin: 0;
      padding-left: 18px;
    }}
    li {{
      margin: 6px 0;
    }}
    pre {{
      margin: 0;
      white-space: pre-wrap;
      word-break: break-word;
      background: var(--code);
      border: 1px solid rgba(255,255,255,0.06);
      border-radius: 10px;
      padding: 12px;
      font-size: 12px;
      line-height: 1.5;
    }}
    .muted {{
      color: var(--muted);
    }}
    @media (max-width: 960px) {{
      body {{
        height: auto;
        overflow: auto;
        display: block;
      }}
      .layout {{
        grid-template-columns: 1fr;
        height: auto;
      }}
      aside {{
        border-right: none;
        border-bottom: 1px solid var(--border);
        max-height: 38vh;
      }}
      main {{
        overflow: visible;
      }}
    }}
  </style>
</head>
<body>
  <header>
    <h1>{safe_title}</h1>
    <div class="summary" id="summary-cards"></div>
  </header>
  <div class="layout">
    <aside id="cycle-list"></aside>
    <main id="detail-view"></main>
  </div>
  <script>
    const LOG = {log_json};
    const SUMMARY = {summary_json};

    function el(tag, className, text) {{
      const node = document.createElement(tag);
      if (className) node.className = className;
      if (text !== undefined && text !== null) node.textContent = text;
      return node;
    }}

    function jsonPre(value) {{
      const pre = el("pre");
      pre.textContent = JSON.stringify(value, null, 2);
      return pre;
    }}

    function section(title, body) {{
      const outer = el("section", "section");
      const header = el("h2", null, title);
      const inner = el("div", "section-body");
      if (typeof body === "string") {{
        inner.appendChild(el("div", "muted", body));
      }} else if (body) {{
        inner.appendChild(body);
      }}
      outer.append(header, inner);
      return outer;
    }}

    function renderSummary() {{
      const host = document.getElementById("summary-cards");
      const cards = [
        ["Cycles", SUMMARY.cycle_count],
        ["Episodes Added", SUMMARY.episodes_added],
        ["Use Records", SUMMARY.use_records],
        ["Promotion Events", SUMMARY.promotion_events],
        ["Rule Access", SUMMARY.rule_access_events],
        ["Thought Beats", SUMMARY.runtime_thought_cycles],
      ];
      cards.forEach(([label, value]) => {{
        const card = el("div", "card");
        card.append(el("div", "label", label), el("div", "value", String(value)));
        host.appendChild(card);
      }});
    }}

    function familyFor(cycle) {{
      return (cycle.selection && cycle.selection.goal_family)
        || (cycle.selected_goal && cycle.selected_goal.goal_type)
        || "unknown";
    }}

    function deltaCounts(cycle) {{
      const delta = cycle.world_delta || {{}};
      return {{
        episodes: ((delta.episodes || {{}}).added || []).length,
        uses: ((delta.episode_use || {{}}).added || []).length,
        promotions: ((delta.promotion || {{}}).events || []).length,
        rules: ((delta.rule_access || {{}}).changed || []).length,
        flags: ((delta.flags || {{}}).events || []).length,
      }};
    }}

    function cycleBadges(cycle) {{
      const counts = deltaCounts(cycle);
      const badges = [];
      if (counts.episodes) badges.push(["+ep " + counts.episodes, "good"]);
      if (counts.uses) badges.push(["uses " + counts.uses, "warn"]);
      if (counts.promotions) badges.push(["promo " + counts.promotions, "good"]);
      if (counts.rules) badges.push(["rules " + counts.rules, "good"]);
      if (counts.flags) badges.push(["flags " + counts.flags, "bad"]);
      if (cycle.runtime_thought) badges.push(["thought", "warn"]);
      return badges;
    }}

    function graffitoDebug(cycle) {{
      return ((cycle.debug || {{}}).graffito_miniworld) || {{}};
    }}

    function cycleEmotionalState(cycle) {{
      return cycle.emotional_state || [];
    }}

    function cycleTopCandidates(cycle) {{
      const top = cycle.top_candidates || [];
      if (top.length) return top;
      const debugTop = graffitoDebug(cycle)["top-candidates"];
      return Array.isArray(debugTop) ? debugTop : [];
    }}

    function cycleRetrievals(cycle) {{
      const retrieved = cycle.retrieved || [];
      if (retrieved.length) return retrieved;
      const debug = graffitoDebug(cycle);
      const crossFamily = (debug["cross-family-race"] || {{}})["cross-family-source-candidates"];
      if (Array.isArray(crossFamily) && crossFamily.length) return crossFamily;
      const preplan = (debug["preplan-race"] || {{}})["top-source-candidates"];
      return Array.isArray(preplan) ? preplan : [];
    }}

    function cycleActiveIndices(cycle) {{
      return cycle.active_indices || [];
    }}

    function renderCycleList(selectedIndex) {{
      const host = document.getElementById("cycle-list");
      host.innerHTML = "";
      (LOG.cycles || []).forEach((cycle, idx) => {{
        const button = el("button", "cycle-button" + (idx === selectedIndex ? " active" : ""));
        button.type = "button";
        button.addEventListener("click", () => render(idx));

        const row = el("div", "cycle-row");
        row.append(
          el("strong", null, "Cycle " + cycle.cycle),
          el("span", "cycle-family", familyFor(cycle))
        );
        button.appendChild(row);

        const goal = cycle.selected_goal || {{}};
        button.appendChild(el("div", "muted", (goal.id || "no-goal") + "  " + (goal.situation_id || "")));

        const badgeRow = el("div", "badge-row");
        cycleBadges(cycle).forEach(([label, cls]) => {{
          badgeRow.appendChild(el("span", "badge " + cls, label));
        }});
        button.appendChild(badgeRow);
        host.appendChild(button);
      }});
    }}

    function kvTable(rows) {{
      const table = el("table");
      const tbody = document.createElement("tbody");
      rows.forEach(([key, value]) => {{
        const tr = document.createElement("tr");
        const th = document.createElement("th");
        th.textContent = key;
        const td = document.createElement("td");
        td.textContent = value == null ? "n/a" : String(value);
        tr.append(th, td);
        tbody.appendChild(tr);
      }});
      table.appendChild(tbody);
      return table;
    }}

    function bulletList(items, formatter) {{
      if (!items || !items.length) return el("div", "muted", "none");
      const ul = el("ul");
      items.forEach(item => {{
        const li = el("li");
        li.textContent = formatter(item);
        ul.appendChild(li);
      }});
      return ul;
    }}

    function render(index) {{
      renderCycleList(index);
      const cycle = LOG.cycles[index];
      const detail = document.getElementById("detail-view");
      detail.innerHTML = "";
      detail.scrollTop = 0;
      const emotionalState = cycleEmotionalState(cycle);
      const topCandidates = cycleTopCandidates(cycle);
      const retrievals = cycleRetrievals(cycle);
      const activeIndices = cycleActiveIndices(cycle);

      const top = el("div", "grid-two");
      const goal = cycle.selected_goal || {{}};
      top.appendChild(section("Selection", kvTable([
        ["Goal", goal.id || "n/a"],
        ["Family", familyFor(cycle)],
        ["Situation", goal.situation_id || "n/a"],
        ["Strength", goal.strength || "n/a"],
        ["Chosen Node", cycle.chosen_node_id || "n/a"],
      ])));
      top.appendChild(section("World Delta Counts", kvTable(Object.entries(deltaCounts(cycle)).map(([k, v]) => [k, v]))));
      detail.appendChild(top);

      const middle = el("div", "grid-two");
      middle.appendChild(section(
        "Emotional State",
        emotionalState.length
          ? jsonPre(emotionalState)
          : "none"
      ));
      middle.appendChild(section(
        "Top Candidates",
        topCandidates.length
          ? jsonPre(topCandidates)
          : "none"
      ));
      detail.appendChild(middle);

      const retrieval = el("div", "grid-two");
      retrieval.appendChild(section(
        "Retrievals",
        retrievals.length
          ? jsonPre(retrievals)
          : "none"
      ));
      retrieval.appendChild(section(
        "Active Indices",
        activeIndices.length
          ? jsonPre(activeIndices)
          : "none"
      ));
      detail.appendChild(retrieval);

      const retrievalEpisodes = el("div", "grid-two");
      retrievalEpisodes.appendChild(section(
        "Retrieved Episodes",
        (cycle.retrieved_episodes && cycle.retrieved_episodes.length)
          ? jsonPre(cycle.retrieved_episodes)
          : "none"
      ));
      retrievalEpisodes.appendChild(section(
        "Recent Index Delta",
        kvTable([
          ["Pushed", (((cycle.world_delta || {{}}).recent_indices || {{}}).pushed || []).join(", ") || "none"],
          ["Evicted", (((cycle.world_delta || {{}}).recent_indices || {{}}).evicted || []).join(", ") || "none"],
        ])
      ));
      detail.appendChild(retrievalEpisodes);

      const delta = cycle.world_delta || {{}};
      detail.appendChild(section(
        "Episodes Added",
        bulletList(((delta.episodes || {{}}).added || []), item =>
          `${{item.episode_id}}  ${{item.family || "unknown"}}  ${{item.admission_status || "n/a"}}  ${{
            (item.retrieval_indices || []).join(", ")
          }}`)));
      detail.appendChild(section(
        "Episode Uses",
        bulletList([...
          (((delta.episode_use || {{}}).added || []).map(item => ["added", item])),
          (((delta.episode_use || {{}}).resolved || []).map(item => ["resolved", item]))
        ], ([kind, item]) =>
          `${{kind}}  ${{item.episode_id}}  ${{item.use_id}}  ${{item.source_family || "?"}} -> ${{item.target_family || "?"}}  ${{
            item.outcome || item.to_status || item.status || "n/a"
          }}`)));
      detail.appendChild(section(
        "Promotion",
        bulletList([...
          (((delta.promotion || {{}}).evidence_added || []).map(item => ["evidence", item])),
          (((delta.promotion || {{}}).events || []).map(item => ["promotion", item]))
        ], ([kind, item]) =>
          kind === "evidence"
            ? `${{kind}}  ${{item.episode_id}}  ${{item.type}}  ${{item.source_family || "?"}} -> ${{item.target_family || "?"}}`
            : `${{kind}}  ${{item.episode_id}}  ${{item.from || "?"}} -> ${{item.to || "?"}}  ${{
                item.reason || "n/a"
              }}`)));
      detail.appendChild(section(
        "Flags",
        bulletList(((delta.flags || {{}}).events || []), item =>
          `${{item.episode_id}}  ${{item.flag}}  ${{item.action || "set"}}  ${{item.reason || "n/a"}}`)));
      detail.appendChild(section(
        "Rule Access",
        bulletList(((delta.rule_access || {{}}).changed || []), item =>
          `${{item.rule_id}}  ${{item.from || "?"}} -> ${{item.to || "?"}}  ${{
            item.reason || item.opened_by || "n/a"
          }}`)));

      if (cycle.runtime_thought) {{
        detail.appendChild(section("Runtime Thought", jsonPre(cycle.runtime_thought)));
      }}

      if (cycle.debug && Object.keys(cycle.debug).length) {{
        detail.appendChild(section("Debug Extras", jsonPre(cycle.debug)));
      }}
    }}

    renderSummary();
    render(0);
  </script>
</body>
</html>
"""


def main() -> None:
    args = parse_args()
    log = load_log(args.trace_path)
    output_path = output_path_for(args.trace_path, args.output)
    title = args.title or f"Debug Canopy - {args.trace_path.name}"
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(build_html(log, title=title), encoding="utf-8")
    print(f"Wrote debug canopy to {output_path}")


if __name__ == "__main__":
    main()

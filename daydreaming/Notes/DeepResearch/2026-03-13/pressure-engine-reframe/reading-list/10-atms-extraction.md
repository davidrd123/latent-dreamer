# ATMS — Representation Extraction

Source: de Kleer, "An Assumption-Based TMS" (Artificial
Intelligence, 1986)

Source file: `sources/markdown/ATMS/source.md`

Purpose: Extract the assumption management pattern for L2
hypothetical context branching.

---

## What ATMS is

An Assumption-Based Truth Maintenance System. It tracks
**which assumptions support which beliefs** across multiple
simultaneous hypothetical contexts, without committing to any
single context.

Unlike a standard TMS (which maintains one consistent state),
ATMS maintains **all consistent combinations** simultaneously
and lets the problem solver switch between them cheaply.

---

## Core concepts

### Assumptions

An assumption is a premise that could be true or false.
Each assumption is atomic and has an identity.

### Environments

An environment is a set of assumptions. It represents one
hypothetical context — "what would be true if these assumptions
held?" Multiple environments coexist simultaneously.

### Labels

Every derived node has a label: the set of environments that
support it. A node's label tells you "this conclusion is true
in these contexts."

### Nogoods

A nogood is a set of assumptions that are known to be
inconsistent. If an environment contains a nogood subset,
that environment is invalid.

### Support sets

Each derived node records *why* it was derived — what
assumptions and inference steps produced it.

---

## The key operations

| Operation | What it does |
|-----------|-------------|
| Add assumption | Introduce a new assumption into the system |
| Add justification | Record that node X is supported by nodes Y, Z under assumptions A, B |
| Check consistency | Verify an environment doesn't contain a nogood |
| Context switch | "Show me what's true under assumptions {A, C}" — instant, no recomputation |
| Add nogood | Mark a set of assumptions as inconsistent |
| Find support | For a given node, which assumptions does it depend on? |

### Why context switching is cheap

Traditional systems: switch context by retracting facts,
asserting new ones, recomputing. Expensive.

ATMS: every node already has a label saying which environments
support it. "Switching context" is just changing which
environment you're querying. No recomputation needed.

---

## What maps to L2 contexts

| ATMS concept | L2 equivalent | Current kernel |
|-------------|---------------|----------------|
| Assumption | `assumption_patch` entry | Simple add-obs/remove-obs |
| Environment | A complete `Context` configuration | Sprouting creates a copy |
| Label | "This fact is true in these branches" | Not tracked — fact is local to one context |
| Nogood | "These assumptions are contradictory" | **Not implemented** |
| Support set | Provenance for derived facts | Partially in trace |
| Context switching | Moving between hypothetical branches | Requires full tree navigation |

---

## What's genuinely new vs. current kernel

### 1. Cheap context switching

The kernel's context mechanism (sprouting) copies state into
a new branch. Switching between branches means navigating the
tree. ATMS would let the system maintain multiple hypothetical
states simultaneously and query any of them without copying.

**Implication:** If the kernel needs to compare what happens
under assumption A vs. assumption B, ATMS does this without
creating two full copies of the world state.

### 2. Nogood tracking

The kernel has no concept of "these assumptions are
contradictory." If a branch fails, it's abandoned, but the
system doesn't record *which combination of assumptions*
caused the failure. ATMS nogoods would prevent the system from
re-exploring known-bad assumption combinations.

**Implication:** The kernel's backtracking would become smarter
— instead of walking up the tree and trying siblings blindly,
the system knows which assumption sets to avoid.

### 3. Multi-context fact visibility

Currently: a fact exists in one context. With ATMS: a fact
has a label showing *all* the contexts where it holds. This
enables questions like "is this fact true in every branch
we're considering?" or "which assumption is the only thing
preventing this conclusion?"

**Implication:** Serendipity detection becomes cheaper. Finding
that a fact derived in one branch also holds in another is
a label intersection, not a search.

### 4. Support sets for provenance

ATMS records exactly why each conclusion was reached — which
assumptions and inference steps. This maps directly to the
provenance mandate in doc 11.

---

## Practical considerations

### Implementation complexity

Full ATMS is a significant data structure. The original paper
is concerned with efficient label management, subset checking,
and nogood propagation. For the kernel, a simplified version
might be sufficient:

- **Track assumptions per context** (already partially done)
- **Add a nogood set** (record known-bad assumption combos)
- **Add labels to key derived facts** (which branches support this?)

This doesn't require a full ATMS implementation — just the
three most useful features.

### When to build this

ATMS is a **research-track investment**, not a shipping-path
dependency. The current sprouting mechanism works for the
existing benchmarks. ATMS becomes valuable when:

- The system explores many hypothetical branches simultaneously
- Serendipity detection needs to compare across branches
- Backtracking needs to be smarter about what to avoid
- Mueller's rule-intersection serendipity (§5.3.2) is being
  implemented — that mechanism needs cheap cross-context search

---

## What to take

1. **Nogood tracking** as the minimum useful steal. Record which
   assumption combinations led to dead ends. Prevent re-exploration
   of known-bad combinations.

2. **Labels on key facts** as a provenance mechanism. When a fact
   is derived, record which assumptions support it. This feeds
   trace/export and serendipity detection.

3. **The conceptual model** of multiple simultaneous hypothetical
   states as a design target for context machinery improvements.

What NOT to take:

- Full ATMS implementation as a near-term project
- The complexity of efficient label management algorithms
  (premature until scale demands it)
- Any assumption that ATMS replaces the existing sprouting
  mechanism — it augments it
- ATMS at L3 (doc 11 already settled: L3 does not use
  context forking)

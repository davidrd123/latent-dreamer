# Versu — L2 Extraction

Source: Evans & Short, "Versu — A Simulationist Storytelling
System" (IEEE Trans. CIAIG, 2014)

Source file: `sources/markdown/Versu/source.md`

Purpose: Extract the social practice model for structuring
recurring interpersonal situations in L2.

---

## What Versu actually is

Versu is a text-based simulationist interactive drama. Unlike
Facade (which uses a centralized drama manager selecting beats),
Versu takes the **strong autonomy** approach: each character
decides autonomously based on individual preferences. The drama
manager exists but rarely overrides characters — it operates by
providing suggestions and tweaking desires.

Key design decisions made explicitly against Facade:

1. **Text output** instead of 3D animation — faster to author,
   more expressive for interiority
2. **Exposed simulation state** — player sees emotional states,
   relationships, and social practice status directly
3. **Menu-based choices** instead of NLP — affordances from
   practices displayed explicitly

---

## Social practices (the stealable core)

A social practice describes a **recurring social situation**.
This is the concept the 5 Pro review flagged as the strongest
addition to L2.

```
Social Practice {
  roles[]           // greeter, recipient; host, guest; etc.
  states            // practice can be in different phases
  affordances       // actions available to each role
  preconditions     // when each affordance is available
  effects           // how affordances change world state
  persistent_data   // practice-specific memory (score, turn, etc.)
}
```

### Key properties

1. **Practices are role-agnostic.** Different characters can fill
   the same role. A dinner party practice works whether the host
   is Mr. Quinn or Lady Catherine. This gives replayability.

2. **Multiple practices run concurrently.** During a dinner,
   there's eating/drinking, a conversation about politics, a
   rising flirtation, and a reaction to someone spilling soup —
   all at once. The agent's options are the **union** of
   affordances from all active practices.

3. **Practices suggest, characters decide.** The practice provides
   a menu of affordances. The character scores them using
   utility-based reactive action selection and picks the highest.
   The practice never forces action.

4. **Practices spawn other practices.** An insult during dinner
   can spawn a confrontation practice. Leaving the room can spawn
   a departure practice. Practices are compositional.

5. **The DM is itself a practice.** Not a separate system —
   just a special practice that can tweak desires or suggest
   actions.

---

## Action selection

Each character scores available affordances using:

```
score(action) = sum of weighted desire satisfactions
```

Desires include:
- Character-specific desires (personality-driven)
- Practice-provided desires (role expectations)
- DM-injected desires (authorial nudges)

The highest-scoring action is selected. Ties and near-ties
produce interesting conflict — the shy character who barely
doesn't open the door, then reluctantly does.

### Adverbial modifiers

Versu generates text with **adverbs derived from internal
state**. A character who opens the door with competing desires
(social obligation vs. shyness) opens it "reluctantly." This
is cheap in text, prohibitively expensive in animation.

This is directly relevant to the narration layer — the
companion can describe not just what a character does but
*how* they do it, derived from concern state.

---

## What maps to L2

| Versu concept | L2 equivalent | Notes |
|-------------|---------------|-------|
| Social practice | Recurring interpersonal situation template | confrontation, evasion, confession, rehearsal, status repair |
| Roles | Character slots in a situation | Already implicit in the kernel's agent model |
| Affordances | Available operator activations given situation + role | Currently operators activate from concerns alone, not from situation context |
| Concurrent practices | Multiple active concerns per character | Characters juggle competing pulls — this is concern-level, not graph-level |
| Practice-provided desires | Situation-specific concern modifiers | A confrontation practice could amplify retaliation_pressure |
| DM as practice | **No direct mapping.** In our architecture the conductor sits above L3 as a biasing authority, not as an L2 practice. Versu's "DM as practice" is a Versu-specific design choice that should not be imported. |

---

## What's genuinely new vs. current kernel

### 1. Situations provide affordances, not just concerns

Currently: operators activate based on concern type alone
(retaliation_pressure → reversal). With social practices:
operators activate based on concern type **× situation context**.
A character with retaliation_pressure at a dinner party gets
different affordances (cutting remark, cold silence, pointed
toast) than the same character in a private confrontation
(shouting, physical threat, accusation).

### 2. Role-based interaction structure

Situations have roles (accuser/accused, host/guest,
pursuer/pursued). The role constrains which affordances are
available and what utility they carry. This gives characters
structured interaction without scripting specific behavior.

### 3. Practice lifecycle

Practices spawn, evolve through states, and terminate. A
confrontation practice might go:

```
provocation → response → escalation_or_deflection → resolution
```

Each state offers different affordances. The practice lifecycle
creates arc structure *within* a situation, not just *between*
situations (which is L3's job).

### 4. Compositional situation layering

Multiple practices running simultaneously produce emergent
complexity. A character navigating dinner (social practice) +
flirtation (social practice) + unresolved guilt (concern) has
to balance affordances from all three. The tension comes from
competing pulls, not from hand-authored conflict.

---

## What to take

1. **Social practice as a typed concept in L2.** A small library
   of reusable interaction templates: confrontation, evasion,
   confession, status repair, seduction, alliance, betrayal.

2. **Affordance menus conditioned on role + situation state.**
   Operators don't just check concern type — they check what
   situation the character is in and what role they're playing.

3. **Practice lifecycle with states.** Situations evolve through
   phases, offering different affordances at each phase.

4. **Adverbial modification for narration.** The narration layer
   can derive *how* a character acts from the competing desire
   weights, not just *what* they do.

What NOT to take:

- The Praxis/exclusion logic language
- The full Versu world-as-sentences representation
- The menu-based player interface design
- Rebuilding at Versu's authoring scale
- The specific Regency England content

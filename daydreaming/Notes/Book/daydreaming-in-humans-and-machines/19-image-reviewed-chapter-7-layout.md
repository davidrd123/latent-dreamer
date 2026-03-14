# Image-Reviewed Chapter 7 Layout

This note reconstructs the layout-dependent material on the Chapter 7 implementation pages from the page images, not from the OCR text layer.

Source images:

- [page-187-187.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/chapter-7-layout/page-187-187.jpg)
- [page-188-188.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/chapter-7-layout/page-188-188.jpg)
- [page-189-189.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/chapter-7-layout/page-189-189.jpg)
- [page-190-190.jpg](/Users/daviddickinson/Projects/Lora/latent-dreamer/daydreaming/Notes/Book/daydreaming-in-humans-and-machines/page-review-images/chapter-7-layout/page-190-190.jpg)

## 7.1 Summary Statistics

PDF pages: `187-188`

DAYDREAMER consists of `12,739` lines of code.

| Component | Lines |
| --- | ---: |
| Control | 717 |
| Planner | 2186 |
| Episodic memory | 915 |
| Reversal | 553 |
| Serendipity | 762 |
| Mutation | 447 |
| Misc | 1315 |
| Generator | 2122 |
| Representations | 3722 |
| Total | 12,739 |

Memory note from page `188`:

- DAYDREAMER consumes `2,521,088` bytes of memory, interpreted, in T version `3.0` on an Apollo `DN460`.
- This figure does not include the GATE language, which consumes `346,112` bytes of memory, compiled.

Planner statistics from page `188`:

| Event | Count |
| --- | ---: |
| Rule firings | 322 |
| Context sprouts | 186 |
| Planning rule firings | 186 |
| Inference rule firings | 136 |
| Subgoal relaxations | 15 |
| Input states/actions | 6 |
| Output actions | 20 |
| Side-effect personal goals | 8 |

Planning rule firings breakdown:

| Type | Count | Percentage |
| --- | ---: | ---: |
| Regular planning rules | 130 | 69.9% |
| Analogical planning rules | 44 | 23.6% |
| Coded (Lisp) planning rules | 9 | 4.8% |
| Backward other planning rules | 3 | 1.67% |

Concern statistics:

| Event | Count |
| --- | ---: |
| Initiation | 22 |
| Success | 15 |
| Failure | 4 |

Episodic-memory statistics:

| Event | Count |
| --- | ---: |
| Stored episodes | 12 |
| Retrieved episodes | 8 |
| Activated indices | 71 |

Additional implementation notes from page `188`:

- The program switched from daydreaming mode to performance mode four times.
- It switched from performance mode back to daydreaming mode three times.
- Sixteen hand-coded episodes were initially provided; after adding twelve more, the program contained twenty-eight episodes.
- Five new planning rules were added to the program's collection of rules; the program later used two of them.

## Rule Utilization Distribution

PDF page: `189`

The rule-utilization table on page `189` is layout-dependent and worth preserving directly:

| Times Used | Number of Rules | Sample Rules |
| ---: | ---: | --- |
| 22 | 1 | Believe-plan1 |
| 20 | 2 | Mtrans-plan2; At-plan2 |
| 12 | 1 | Ptrans-plan |
| 10 | 1 | Enable-future-vprox-plan1 |
| 9 | 1 | Vprox-plan2 |
| 7 | 3 | Vprox-plan3; Vprox-plan1; Mtrans-plan1 |
| 5 | 6 | Rprox-plan; M-phone-plan1; M-agree-plan |
| 4 | 10 | Romantic-interest-plan; Reversal-theme |
| 3 | 8 | Know-plan2; Employment-theme-plan |
| 2 | 31 | Under-doorway-plan; Reversal-plan |
| 1 | 52 | Well-dressed-plan2; Star-plan |

Summary sentence from the same page:

- A total of `116` rules were used.
- `114` of those rules were initially provided to the program.
- Since the program was provided with `135` rules initially, `21` rules ended up never being used.

## GATE Object Formatting Examples

PDF page: `190`

This page is image-sensitive because the examples depend on indentation and line breaks.

Slot-filler object example:

```lisp
(PTRANS actor John1
        from (RESIDENCE obj John1)
        to Store1
        obj John1 Mary1)
```

The surrounding explanation says this object is of type `PTRANS` and consists of five pairs. The `actor` slot value is `John1`. The `from` slot value is itself an unnamed slot-filler object, so its full representation is printed recursively. There are two `obj` pairs, with values `John1` and `Mary1`.

Unification example:

```lisp
(PTRANS actor ?Person
        to ?Location)

and

(PTRANS actor John1
        to Store1)

?Person = John1
?Location = Store1
```

The footnote on the same page matters conceptually: in GATE, unification is asymmetrical. The resulting structures do not have to be equivalent; one structure may be a substructure of the other.

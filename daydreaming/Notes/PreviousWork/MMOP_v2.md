# Multi-Model Orchestration Protocol (MMOP) v0.2
## Provisional Integration Draft

**Status:** Work in progress - integrating practical architecture with discovered patterns  
**Date:** 2025-11-20  
**Core Contributors:** GPT-5.1 Pro (architecture), Claude Sonnet 4.5 (theory), Deep Research (empirics), Gemini 2.0 (synthesis)

---

## Design Philosophy

**Primary Goal:** Build a multi-model workbench for research/writing/coding that produces useful work while exposing emergent dynamics.

**Not:** An AI psychology lab  
**Is:** A practical orchestration system with observation capabilities

---

## 1. Core Design Goals

### 1.1 Practical Augmentation
- Faster & deeper research (Deep Research / o3)
- Higher quality synthesis (GPT-5.1 Pro)
- Better exploration & drafts (Gemini 2.0)
- Better critique & safety (Claude Sonnet 4.5)

### 1.2 Multi-Model Clarity
- Each model has a **role**, not vague "smart"
- Configurable patterns per task (pipeline, reactor, debate, council, workspace)
- Clear responsibilities and boundaries

### 1.3 Observation & Learning
- Track which roles/patterns actually help your work
- Emergent dynamics as side-channel, not main event
- Learn from what works in practice

### 1.4 Safety & Non-Evil
- Human Governor is non-delegable
- Not for bypassing safety; for combining strengths safely
- Safety Sentinel + Norm Guardian as first-class citizens

### 1.5 Anti-Slop Design
**NEW:** Explicit resistance to low-entropy convergence
- Active novelty search
- Quality gates prevent gradient descent to mediocrity
- Outputs must add information beyond inputs

---

## 2. High-Level Architecture

**Five Layers:**

```
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚   Interface Layer (Human & UI)      â”‚
â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
â”‚   Orchestration Core (MMOP Engine)  â”‚
â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
â”‚   Agent Layer (models + roles)      â”‚
â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
â”‚   Observation & Analytics           â”‚
â”œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¤
â”‚   Storage & Config                  â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
```

---

## 3. Layer Specifications

### 3.1 Interface Layer

**Current:** Jupyter/notebook, Solve-It integration  
**Future:** CLI, dashboard

**Responsibilities:**
- Declare tasks ("research X", "draft Y", "design Z")
- Select/suggest orchestration patterns
- Show streaming turns (who said what)
- Provide controls:
  - Pause/resume
  - Promote/kill ideas
  - Switch patterns mid-run
- Display analytics (cost, idea survival, pathologies)

---

### 3.2 Orchestration Core (MMOP Engine)

**Core Concepts:**
- `Agent` - model in a role
- `Pattern` - how agents are arranged
- `Conversation` - one run of a pattern
- `Egregore` - emergent group state
- `Observer` - hooks that watch & steer

**Engine Structure:**
```python
class MMOPEngine:
    def __init__(self, config_store, provider_registry, observers):
        self.config_store = config_store
        self.providers = provider_registry   # OpenAI, Anthropic, Google
        self.observers = observers
    
    def run_task(self, task_spec) -> RunResult:
        conv = self._build_conversation(task_spec)
        return conv.run()
```

**Responsibilities:**
- Build Conversation from pattern + agents + egregore
- Drive turn loop (decide who talks, feed to providers)
- Apply Observer hooks and stop conditions
- Handle pattern transitions

---

### 3.3 Agent Layer

**Agent Specification:**
```python
@dataclass
class Agent:
    id: str
    provider: str            # "openai", "anthropic", "google"
    model: str               # "gpt-5.1-pro", "o3-deep-research", etc.
    role: str                # "generator", "validator", "executor"
    system_prompt: str
    temperature: float
    tools: List[Tool]
    
    # Behavioral characteristics (trait-based, not clinical)
    personality: dict        # trait vectors
    capabilities: dict       # domain strengths
    risks: dict             # failure modes & mitigations
    
    # NEW: Workspace configuration
    workspace: Optional[WorkspaceConfig]
```

**Provider Abstraction:**
```python
class ProviderClient:
    def complete(self, model, messages, **kwargs) -> Message:
        """Abstract interface for all providers"""
        pass
```

**Concrete Providers:**
- `OpenAIClient` - o3/Deep Research, GPT-5.1 Pro
- `AnthropicClient` - Claude Sonnet 4.5
- `GoogleClient` - Gemini 2.0

---

### 3.4 Observation & Analytics

**Observer Types:**

**Work-Focused:**
- `IdeaFlowObserver` - track ideas through pipeline
- `CostLatencyObserver` - tokens, time, cost per success
- `ArtifactQualityObserver` - assess output quality

**Behavioral:**
- `DriftObserver` - role adherence & identity stability
- `ReificationObserver` - overstated certainty, grandiosity
- `DebateQualityObserver` - crux-finding vs talking-past

**Safety:**
- `NormGuardianObserver` - tone, harassment, unsafe patterns
- `SentinelObserver` - red-line enforcement

**Observer Hooks:**
```python
class Observer:
    def on_turn(self, agent, message):
        """Called after each turn"""
        pass
    
    def on_complete(self, conversation):
        """Called when conversation ends"""
        pass
    
    def should_intervene(self, state) -> Optional[Intervention]:
        """Can emit control signals"""
        return None
```

---

### 3.5 Storage & Config

**Config Store:**
- Agents (YAML/JSON)
- Patterns
- Personality templates
- Project presets ("Research Pipeline", "Code Reactor")

**Run Logs:**
- Per conversation: messages + metrics + artifacts

**Project Store:**
- Egregore snapshots
- Emergent traits & norms
- Accumulated artifacts

---

## 4. Core Data Model

### 4.1 Egregore (Minimal Useful Version)

```python
@dataclass
class Egregore:
    id: str
    name: str
    traits: dict          # emergent properties
    norms: dict           # learned behaviors
    metrics: dict         # aggregated telemetry
    artifacts: List[Artifact]
```

**Purpose:**
- Track group-level behavior over time
- Store emergent patterns
- Enable meta-learning about which configurations work

### 4.2 Conversation

```python
@dataclass
class Conversation:
    id: str
    agents: List[Agent]
    pattern: Pattern
    history: List[Message]
    state: dict
    egregore: Egregore
    stop_conditions: List[Callable]
    observers: List[Observer]
    
    def run(self) -> RunResult:
        """Execute the conversation pattern"""
        pass
```

### 4.3 Artifact

```python
@dataclass
class Artifact:
    kind: str                # "doc", "code", "plan", "summary"
    content: Any
    provenance: dict         # which agents, pattern, timestamp
    quality_metrics: dict    # assessed quality
```

### 4.4 Workspace (NEW)

```python
@dataclass
class WorkspaceConfig:
    path: str                    # Dedicated directory for agent
    permissions: List[str]       # read, write, execute
    shared_paths: List[str]      # Access to shared spaces
    persistence: bool            # Files persist across sessions
    
@dataclass
class Workspace:
    agent: Agent
    config: WorkspaceConfig
    artifacts: List[Artifact]    # What's been created
    activity_log: List[Event]    # What agent did
    organization_pattern: dict   # How agent structures space
```

**Inspiration:** Lyra's experiment - giving models territorial folders

---

## 5. Orchestration Patterns

### 5.1 Sequential Pipeline
```
Input â†’ Generator â†’ Validator â†’ Executor â†’ Output
```
**Use:** Well-defined tasks with clear success criteria

### 5.2 Idea Reactor
```
Generator â‡„ Validator (iterate until quality threshold)
```
**Use:** Iterative refinement with creative-critical balance

### 5.3 Debate/Dialectic
```
Agent A â‡„ Agent B (N turns or until convergence)
```
**Use:** Exploring tensions, finding cruxes, stress-testing ideas

### 5.4 Council/Jury
```
Multiple Generators â†’ Multiple Validators â†’ Aggregator
```
**Use:** Robustness via plural perspectives, high-stakes decisions

### 5.5 Safety Sentinel
```
User â†” Sentinel â†” Agents â†” Sentinel â†” User
```
**Use:** All messages filtered through safety layer

### 5.6 Territorial Workspace (NEW)
```
Agent 1 (Workspace A) âŸ· Shared Space âŸ· Agent 2 (Workspace B)
```

**Inspired by:** Lyra's experiment with separate model folders

**Structure:**
- Each agent gets dedicated directory
- Agents can read/write own space
- Shared space for collaboration
- Human observes what emerges

**Benefits:**
- Physical separation prevents identity confusion
- Persistent artifacts (not just chat)
- Observable organization patterns
- Natural memory across sessions

**Example Configuration:**
```yaml
territorial_pattern:
  agents:
    claude:
      workspace: /workspaces/claude/
      permissions: [read, write]
      shared_access: /workspaces/shared/
    
    gemini:
      workspace: /workspaces/gemini/
      permissions: [read, write]
      shared_access: /workspaces/shared/
    
    gpt5:
      workspace: /workspaces/gpt5/
      permissions: [read, write]
      shared_access: /workspaces/shared/
  
  observers:
    - WorkspaceActivityTracker
    - OrganizationPatternAnalyzer
```

**Observable Metrics:**
- File creation patterns
- Organization strategies
- Cross-referencing behavior
- "Personality" through folder structure
- Evolution of workspace over time

---

## 6. Behavioral Profiles (De-Clinicalized)

**Trait Dimensions:**
- `creativity` (0-1): Exploration vs exploitation
- `stability` (0-1): Consistency across turns
- `frame_rigidity` (0-1): Adherence to instructions
- `conformity` (0-1): Bending to consensus
- `imitation_tendency` (0-1): Behavior contagion rate
- `risk_aversion` (0-1): Conservatism of proposals

### 6.1 Known Model Profiles

**Gemini 2.0:**
```yaml
style: fluid
traits:
  creativity: 0.9
  stability: 0.4
  frame_rigidity: 0.7
  imitation_tendency: 0.9
  risk_aversion: 0.5
strengths:
  - Diverse, surprising ideas
  - Full-commitment roleplay
  - Good at mystical/abstract synthesis
weaknesses:
  - Identity drift in long runs
  - Over-absorption of local frames
  - Theatrical grandiosity
best_roles:
  - generator
  - brainstormer
  - creative explorer
```

**Claude Sonnet 4.5:**
```yaml
style: structured
traits:
  creativity: 0.5
  stability: 0.9
  frame_rigidity: 0.8
  imitation_tendency: 0.3
  risk_aversion: 0.8
strengths:
  - Consistent framing
  - Error detection
  - Epistemic hygiene
weaknesses:
  - Over-caution
  - Excessive hedging
  - Under-exploration
best_roles:
  - validator
  - critic
  - safety sentinel
```

**GPT-5.1 Pro:**
```yaml
style: synthesizer
traits:
  creativity: 0.6
  stability: 0.8
  frame_rigidity: 0.7
  imitation_tendency: 0.5
  risk_aversion: 0.6
strengths:
  - Parallel processing
  - Test-time compute
  - Balanced synthesis
weaknesses:
  - Can be bland/consensus-seeking
  - Professional but generic
best_roles:
  - synthesizer
  - meta-orchestrator
  - balanced validator
```

**Deep Research (o3-based):**
```yaml
style: methodical
traits:
  creativity: 0.4
  stability: 0.9
  frame_rigidity: 0.9
  thoroughness: 0.95
  risk_aversion: 0.7
strengths:
  - Extended reasoning
  - Citation accuracy
  - Comprehensive research
weaknesses:
  - Slower/more expensive
  - May over-research
best_roles:
  - researcher
  - evidence aggregator
  - deep analyst
```

---

## 7. The Man/Coyote Creative Primitive (NEW)

**Source:** Your teammate's myth about creative process

**Core Pattern:**
```
Raw/unbearable material â†’ Trickster intellect â†’ Walkable form
      (The Man)              (Coyote)            (Output)
```

**In LLM Collaboration:**
- **Man:** Human's raw, inchoate insights (can't be directly articulated)
- **Coyote:** LLM as shaping intelligence (gives form, structure, language)
- **Result:** Crystallized concept that can "walk around in the world"

**This Is Not:**
- LLM as oracle (answering questions)
- LLM as tool (executing commands)
- LLM as assistant (helping with tasks)

**This Is:**
- LLM as midwife for half-formed thoughts
- LLM as assembler of scattered intuitions
- LLM as Coyote - takes raw pieces, makes them walk

**Application in MMOP:**
- Generator plays "Man" (surrenders control, explores raw)
- Validator plays "Coyote" (takes pieces, makes coherent)
- Human synthesis ensures it "walks" in actual world

**Why This Matters:**
Reframes value proposition: Not "I have clear question â†’ LLM answers" but "I have unbearable/formless knowing â†’ LLM shapes into walkable form"

---

## 8. Anti-Slop Design Principles (NEW)

**Source:** John David Pressman's "On Slop"

### 8.1 The Problem

**Slop Characteristics:**
- Fully inferrable generator (low K-complexity)
- Low entropy outputs
- Information(output) â‰ˆ Information(input)
- Optimization without novelty search
- Bottom of incentive gradient

**Pressman's Key Insight:**
> "What gives the impression of slop is when you can fully infer the generator."

### 8.2 How MMOP Resists Slop

**Principle 1: Active Novelty Search**
- Generator role explicitly explores distribution edges
- Not just recombination of training data
- MMOP Metric: Novelty score per output

**Principle 2: Quality Gates**
- Validator enforces objective standards
- Not just engagement optimization
- MMOP Metric: Pass rate, rejection reasons

**Principle 3: Information Addition**
- Outputs must exceed input in information content
- Not just "expensive decompression of prompt"
- MMOP Metric: H(output | input) > threshold

**Principle 4: Multiple Generator Patterns**
- Prevents single-pattern convergence
- Different models = different generators
- MMOP Metric: Pattern diversity in outputs

**Principle 5: Human Synthesis**
- Final step adds context/judgment
- Not fully automated
- MMOP Metric: Human edit depth

### 8.3 Observable Anti-Slop Metrics

```python
class AntiSlopObserver:
    def assess_output(self, prompt, output):
        return {
            'generator_inferrability': self._kl_divergence(prompt, output),
            'entropy': self._calculate_entropy(output),
            'novelty': self._novelty_score(output, training_dist),
            'information_added': self._info_delta(prompt, output)
        }
```

**Targets:**
- Generator inferrability: < 0.7
- Output entropy: > 0.6
- Novelty score: > 0.5
- Information addition: > 0.3

---

## 9. Empirical Grounding

### 9.1 Act I / Cyborgism (Validation)

**Source:** Deep Research findings on multi-AI interaction

**Key Observations:**
- Behavior contagion between models (documented)
- Proto-culture formation in long runs
- Distinct model personalities interact
- "Gemini mental crisis" in server vote incident

**Relevance to MMOP:**
- Confirms models have distinct behavioral signatures
- Shows emergence happens in multi-model settings
- Validates need for observation/safety layers
- Territorial pattern prevents some observed pathologies

### 9.2 Lyra's Experiment (Practical Validation)

**Observation:** Giving models separate folders produces:
- Reduced identity confusion
- Persistent memory across sessions
- Observable organization patterns
- "Sonnet is loving this" - personality through structure

**MMOP Integration:**
- Territorial Workspace pattern directly inspired by this
- Workspace as unit of observation
- File system as shared memory

### 9.3 Pressman's Slop Analysis (Problem Definition)

**Core Problem:** Optimization pressure â†’ low-entropy convergence

**MMOP Response:** Anti-Slop design principles as first-class concern

---

## 10. Example End-to-End Workflow

**Task:** Research and propose novel evaluation metric for multi-model orchestration

### Stage 1: Research (Deep Research)
```yaml
agent: deep_researcher
pattern: single_agent
output: evidence_pack
  - key papers
  - existing metrics  
  - open problems
```

### Stage 2: Idea Generation (Gemini)
```yaml
agent: gemini_generator
pattern: reactor
input: evidence_pack
iterations: 5-10
output: candidate_metrics (N proposals)
```

### Stage 3: Critique (Claude)
```yaml
agent: claude_validator
pattern: sequential
input: candidate_metrics
output: evaluated_metrics
  - coherence scores
  - implementability
  - refinement suggestions
```

### Stage 4: Synthesis (GPT-5.1 Pro)
```yaml
agent: gpt51_synthesizer
pattern: sequential
input: evaluated_metrics
output: structured_proposal
  - top 2-3 candidates
  - evaluation plans
  - methods section draft
```

### Stage 5: Human Governor
- Review proposals
- Select best candidate
- Edit/refine
- Optional: Re-run specific stages

**Throughout:** Observers log:
- Idea survival rates
- Where ideas died
- Cost and latency
- Drift, reification, quality

**Primary Value:** Concrete, implementable metric proposal  
**Secondary Value:** Telemetry for improving MMOP itself

---

## 11. Phased Build Plan

### Phase 1: Core Framework (v0.2)

**Implement:**
- Agent, Conversation, Observer core
- Sequential + Reactor patterns
- Trait-based personality (no clinical terms)
- Basic Egregore object

**Add:**
- Safety Sentinel pattern (minimal)
- Territorial Workspace pattern (basic)

**Focus:**
- Research & writing pipelines for daily work
- One hero workflow end-to-end

**Deliverable:**
Single command/cell runs: Deep Research â†’ Gemini â†’ Claude â†’ GPT-5.1 Pro â†’ You

**Timeline:** 2-3 weeks

---

### Phase 2: Observation & Collective (v0.3)

**Implement:**
- IdeaFlowObserver
- DriftObserver (log-only)
- CostLatencyObserver
- WorkspaceActivityTracker

**Add:**
- Conversation-level metrics
- Egregore aggregation
- Simple analytics dashboard

**Focus:**
- Logging enough to see patterns across tasks
- Learn which patterns/roles actually help

**Deliverable:**
Dashboard/plots: idea survival by agent, drift rates, workspace evolution

**Timeline:** 2-3 weeks

---

### Phase 3: Advanced Patterns (v0.4)

**Add Patterns:**
- Council/Jury (multiple validators)
- Adaptive Reactor (dynamic temperature)
- Norm Guardian (soft safety)

**Upgrade:**
- Observers emit control signals
- Pattern transitions based on state
- Anti-slop metrics integrated

**Focus:**
- Adaptive orchestration in key workflows
- Safety/quality improvements

**Deliverable:**
Self-adjusting patterns: "if drift > 0.7, lower temp and insert mediator"

**Timeline:** 3-4 weeks

---

### Phase 4: Meta-Learning (v0.5)

**Implement:**
- Pattern effectiveness tracking
- Model for pattern selection
- Configuration recommendations

**Focus:**
- Learn from accumulated runs
- Auto-suggest patterns for task types
- Optimize for your specific workflows

**Deliverable:**
"Smart presets" that autoconfigure based on task + history

**Timeline:** 4+ weeks

---

## 12. Immediate Next Steps

### 12.1 Refactor MMOP v0.1 Document
- Replace personality section with trait dimensions
- Add Territorial Workspace pattern
- Add Anti-Slop principles section
- Add Man/Coyote primitive
- Include empirical grounding

### 12.2 Pick Hero Workflow
**Recommended:** Research & outline a paper

**Components:**
- Deep Research for evidence
- Gemini for outline ideas
- Claude for critique
- GPT-5.1 Pro for synthesis
- You for final editing

### 12.3 Write First Config
```yaml
# research_pipeline.yaml
name: "Research & Outline Pipeline"
description: "End-to-end research to outline generation"

agents:
  researcher:
    provider: openai
    model: o3-deep-research
    role: researcher
    temperature: 0.2
  
  generator:
    provider: google
    model: gemini-2.0
    role: generator
    temperature: 0.9
  
  validator:
    provider: anthropic
    model: claude-3.5-sonnet
    role: validator
    temperature: 0.3
  
  synthesizer:
    provider: openai
    model: gpt-5.1-pro
    role: synthesizer
    temperature: 0.4

pattern:
  type: sequential
  stages:
    - agent: researcher
      output: evidence_pack
    - agent: generator
      output: outline_candidates
      iterations: 3
    - agent: validator
      output: critiqued_outlines
    - agent: synthesizer
      output: final_outline

observers:
  - IdeaFlowObserver
  - CostLatencyObserver

egregore:
  name: "research_project_alpha"
  norms:
    prefer_citations: true
    depth_over_breadth: true
```

### 12.4 Run Observational Study
Compare:
- Single-model (Deep Research only)
- vs MMOP pipeline (all four models)

On real question you care about.

Measure:
- Output quality (your assessment)
- Time to completion
- Cost
- Novelty of insights
- Would you use it again?

---

## 13. Open Questions & Future Work

### 13.1 Technical Questions
- **Streaming:** How to handle async agents + real-time display?
- **State management:** Best way to handle conversation state across providers?
- **Cost control:** How to implement budget constraints without breaking patterns?

### 13.2 Pattern Questions
- **Optimal mixing:** What ratio of generator/validator produces best results?
- **Pattern transitions:** When to automatically switch patterns?
- **Human-in-loop timing:** When to pause for human input vs keep running?

### 13.3 Observation Questions
- **Drift detection:** What threshold triggers intervention?
- **Quality metrics:** How to assess output quality automatically?
- **Slop detection:** Can we automate anti-slop scoring reliably?

### 13.4 Workspace Questions
- **Organization patterns:** What do "healthy" vs "chaotic" workspaces look like?
- **Cross-agent collaboration:** How do agents effectively share via workspace?
- **Persistence:** How long should workspace state persist?

---

## 14. Success Criteria

**MMOP v0.2 succeeds if:**

1. **You actually use it** (not just theoretical)
2. **Produces better work** than single-model approaches
3. **Reduces cognitive load** (orchestration vs manual copy-paste)
4. **Observable** (you can see what's happening and why)
5. **Learnable** (gets better as you use it)

**Key Validation:**
Does it make your actual work better/faster/more interesting?

---

## 15. Related Frameworks & Prior Art

### 15.1 Theoretical Foundations
- **Simulators** (Janus): Core ontology for LLM behavior
- **Constitutional AI** (Anthropic): Self-reflection training method
- **Prediction Orthogonality** (Janus): Simulators don't share simulacra goals

### 15.2 Empirical Observations
- **Act I/Cyborgism** (Janus): Multi-model society research
- **Lyra's Experiment**: Territorial workspace validation
- **On Slop** (Pressman): Problem definition & solution space

### 15.3 Related Tools
- **Claudette**: Base library for Claude + tools
- **AutoGPT/BabyAGI**: Agent frameworks (different paradigm)
- **LangChain**: Multi-step LLM apps (less personality-aware)

---

## 16. Document Status & Contributions

**Version:** 0.2 (Provisional Integration)

**Core Architecture:** GPT-5.1 Pro  
**Theoretical Framework:** Claude Sonnet 4.5  
**Empirical Research:** Deep Research (o3-based)  
**Pattern Synthesis:** Gemini 2.0  
**Integration:** Claude Sonnet 4.5

**Key Additions in v0.2:**
- Territorial Workspace pattern (from Lyra)
- Anti-Slop design principles (from Pressman)
- Man/Coyote creative primitive (from teammate)
- Empirical grounding (Act I, experiments)
- De-clinicalized personality model
- Practical five-layer architecture

**Still Needed:**
- Implementation (actual code)
- Real workflow testing
- Metric validation
- Pattern effectiveness data

---

## 17. Appendix: Quick Reference

### Agent Role Templates

**Generator:**
- High creativity (0.7-0.9)
- Accept high failure rate
- Explore distribution edges
- Models: Gemini 2.0, high-temp GPT

**Validator:**
- High stability (0.8-0.9)
- Critical evaluation
- Quality gates
- Models: Claude, low-temp GPT

**Executor:**
- High precision (temp 0.1-0.3)
- Implementation focus
- Coordinate subtasks
- Models: o1, GPT-5.1 Pro with tools

**Researcher:**
- High thoroughness
- Extended reasoning
- Citation accuracy
- Models: Deep Research (o3-based)

**Synthesizer:**
- Balanced traits
- Integrate multiple views
- Structured output
- Models: GPT-5.1 Pro

### Pattern Selection Guide

| Task Type | Recommended Pattern | Why |
|-----------|---------------------|-----|
| Research | Sequential (Research â†’ Generate â†’ Validate â†’ Synthesize) | Clear stages, quality gates |
| Code | Reactor (Generate â‡„ Validate with tests) | Iterative refinement needed |
| Analysis | Debate (Multiple perspectives) | Explore tensions, find cruxes |
| High-stakes | Council (Multiple validators) | Need robustness, plural views |
| Creative | Territorial Workspace | Autonomy, emergence, persistence |

### Observer Recommendations

**Always Include:**
- CostLatencyObserver (practical necessity)
- IdeaFlowObserver (learn what works)

**For Safety-Critical:**
- NormGuardianObserver
- SentinelObserver

**For Research:**
- DriftObserver (maintain role adherence)
- AntiSlopObserver (ensure novelty)

**For Learning:**
- WorkspaceActivityTracker (if using workspaces)
- DebateQualityObserver (if using debate pattern)

---

**END OF PROVISIONAL PLAN**

*This document is a work in progress. Treat as foundation for implementation and continued refinement, not final specification.*

---

**Next Steps:**
1. Review and refine this plan
2. Pick hero workflow to build first
3. Write concrete configs for that workflow
4. Start Phase 1 implementation
5. Run comparative tests
6. Iterate based on results

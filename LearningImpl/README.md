# LearningImpl — Machine Learning and Self-Play

Subproject of **BagaturChess** containing the research / training infrastructure used to **tune Bagatur's evaluation function**. Two evaluation paradigms are represented:

1. **Classical feature-based evaluation** (`learning.goldmiddle.*`) — hand-crafted features with learnable weights (material, PST, pawn structure, mobility, king safety, etc.). This was the engine's main eval before NNUE.
2. **NNUE evaluation** (`deeplearning.impl_nnue_v*`) — successive NNUE network architectures (`v2`, `v2b`, `v3`, `v4`, `v5`, `v7`). Each one is a snapshot of an experiment / training run. Bagatur currently ships with the latest stable version (see the runtime NNUE files under `UCITracker/` and the `NNUE/` module).

In addition there is a **self-play harness** (`selfplay/`) used to generate training data, and a **Deep Netts** integration (`deeplearning_deepnetts/impl1`) — an early Java-native deep-learning attempt that has been kept for reference.

This is **a research bench**, not a production module — it is not on the engine's runtime path. Code here drives offline training and weight generation; the resulting weights / networks are then loaded by the actual engine modules.

## Project Structure

```
LearningImpl/src/bagaturchess/
├── deeplearning/                       — NNUE training experiments
│   ├── api/                            — shared training-side interfaces
│   ├── impl/                           — feature-based (non-NNUE) training reused by NNUE infra
│   │   ├── eval/{allfeatures, pst, pst_and_allfeatures}
│   │   └── visitors/                   — PositionsVisitor implementations
│   ├── impl_nnue/                      — first NNUE generation
│   ├── impl_nnue_v2/{java_eval, jni_eval}
│   ├── impl_nnue_v2b/, impl_nnue_v3/, impl_nnue_v4/,
│   ├── impl_nnue_v5/, impl_nnue_v7/    — successive NNUE architectures
│   └── run/                            — main classes for training runs
├── deeplearning_deepnetts/impl1/       — Java Deep Netts experiment (legacy)
│   ├── eval/                           — network-backed evaluator
│   ├── run/                            — training runners
│   └── visitor/                        — position visitors
├── learning/goldmiddle/                — classical hand-crafted evaluation
│   ├── api/                            — IEvalComponentsProcessor, ILearningInput, factory
│   ├── impl/cfg/                       — feature configurations per eval flavour
│   │   ├── bagatur/                    — original Bagatur feature set
│   │   ├── bagatur_allfeatures/        — expanded feature set
│   │   ├── base_allfeatures/           — minimal baseline
│   │   └── old0..old3/                 — historical snapshots
│   ├── impl1/ .. impl8/                — successive evaluation reworks
│   ├── pesto/                          — PeSTO-style PST evaluation
│   ├── run/
│   │   ├── DumpFeatures.java           — print feature vectors for a set of positions
│   │   └── GenerateWeights.java        — drive a training pass / produce a weight file
│   └── visitors/                       — PositionsVisitor implementations for training input
└── selfplay/                           — self-play training data generation
    ├── logic/                          — game generation, position collection
    └── run/                            — main classes
```

## High-Level Workflow

```
   PGN corpora                    UCITracker self-play              Existing engine weights
   (PGNProcessor)                 (eval traces)                     (NNUE files)
        │                              │                                  │
        └──────────────┬───────────────┴────────────┬─────────────────────┘
                       ▼                            ▼
              Position visitor              Self-play generator
              (LearningImpl.deeplearning,   (LearningImpl.selfplay)
               LearningImpl.learning)
                       │                            │
                       ▼                            ▼
                  Training set                  More training set
                       │
                       ▼
              Train / fit weights
              (Deep Netts, NNUE Python trainer, or
               classical weight-generator GenerateWeights)
                       │
                       ▼
              Weight artefact (NNUE file, feature-weights table)
                       │
                       ▼
              Loaded by Search / NNUE at runtime
```

## Sub-Areas in Detail

### `deeplearning/` — NNUE Training Infrastructure

The NNUE training path. Each `impl_nnue_v*` package is a **frozen snapshot** of a network architecture / feature encoding that was experimented with.

| Sub-package | Purpose |
|---|---|
| `api/` | shared interfaces — position iteration, eval target, feature encoders |
| `impl/` | feature-based reusable parts (PST and "all-features" mixes) shared with classical training |
| `impl/visitors/` | `PositionsVisitor` implementations that feed positions into the training pipeline |
| `impl_nnue/` | first NNUE generation (proof of concept) |
| `impl_nnue_v2/java_eval` | pure-Java reference NNUE evaluator |
| `impl_nnue_v2/jni_eval` | JNI variant calling into native NNUE (faster training inference) |
| `impl_nnue_v2b` | v2 with architecture tweaks |
| `impl_nnue_v3, v4, v5, v7` | successive architecture revisions; v7 was the latest before convergence to the production network |
| `run/` | main classes that wire training runs |

Each `impl_nnue_v*` package follows the same shape — `EvaluationConfig`, `NNUEEvaluator`, `NNUEEvaluatorFactory`.

### `deeplearning_deepnetts/impl1/` — Deep Netts Experiment

An attempt to train an evaluation network using the [Deep Netts](https://www.deepnetts.com) Java framework. Useful as reference for a "pure Java" learning loop without going outside the JVM. Not currently in production.

### `learning/goldmiddle/` — Classical Feature Evaluation

The pre-NNUE evaluation path. "Gold middle" is the project's internal name for the bundle of hand-crafted middle-game features that were tuned together.

| Sub-package | Purpose |
|---|---|
| `api/IEvalComponentsProcessor`, `ILearningInput`, `LearningInputFactory` | public interfaces — convert a position into a feature vector for learning |
| `impl/cfg/bagatur/` | Bagatur's original eval feature set (material, PST, pawn structure, mobility, king safety, threats, ...) |
| `impl/cfg/bagatur_allfeatures/` | expanded variant with extra interaction terms |
| `impl/cfg/base_allfeatures/` | minimal baseline used as a control in experiments |
| `impl/cfg/old0..old3/` | historical configurations preserved for reproducibility |
| `impl1/ .. impl8/` | iterative reworks of the evaluation function — each generation explored a different combination of features and weighting |
| `pesto/` | PeSTO-style piece-square table evaluation (pure PST, no other features) |
| `run/DumpFeatures.java` | inspect the feature vector for a set of positions |
| `run/GenerateWeights.java` | drive a training pass and dump weights to disk |
| `visitors/` | `PositionsVisitor` implementations that select / weigh positions used as training input |

### `selfplay/` — Self-Play Data Generation

| Sub-package | Purpose |
|---|---|
| `logic/` | game-generation pipeline — pair engine instances, play, collect positions and evaluations |
| `run/` | main classes that launch self-play batches |

The selfplay output is the primary training data source for NNUE generations and modern evaluation rework.

## Relationship to Other Bagatur Modules

```
  PGN dumps ─► PGNProcessor ─┐
                              │
  Engine play ─► UCITracker ──┼─► LearningImpl
                              │      │
  Existing weights ───────────┘      │
                                     ▼
                            train weights / NNUE
                                     │
                                     ▼
                         loaded by NNUE / Search at runtime
```

## Notes for Researchers

- Each `impl_nnue_v*` is intentionally **frozen** — do not refactor or rename. Reproducibility of historical experiments depends on the exact code that produced them.
- `impl1` .. `impl8` under `learning.goldmiddle` carry the same warning: they are snapshots, not "evolving" code.
- Adding a new architecture or feature set: create a **new** `impl_nnue_v{N+1}` (or `impl{N+1}`) and write its own factory; do not edit existing ones.
- Production weights live outside this module — see the `NNUE/` project and the `*.nnue` files in `UCITracker/` and the engine distribution.

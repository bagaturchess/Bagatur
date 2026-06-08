# UCITracker

# Overview

As a Java programmer interested in chess, you may want to capture games (move list, principal variations, evaluations) of arbitrary UCI chess engines in a compact **binary format**, so they can be re-iterated later for training, analysis, or supervised-learning experiments. UCITracker is designed for that purpose.

The tool acts as a program that speaks the UCI protocol and drives any UCI engine in self-play, just like Arena or cute-chess. It records the PVs (best lines) and evaluations the engine produced during play to a file on the file system, and exposes a small Java API to traverse the recorded games later. One practical use case is generating large supervised-learning datasets — for [Temporal Difference Learning](https://en.wikipedia.org/wiki/Temporal_difference_learning), NNUE training, or any other [supervised learning](https://en.wikipedia.org/wiki/Supervised_learning) workflow.

# Project Structure

```
UCITracker/
├── LICENSE
├── network_bagatur_v1.nnue            — example NNUE network used by some runners
├── nn-6b4236f2ec01.nnue               — example NNUE network used by some runners
├── readme.md                          — this file
└── src/bagaturchess/ucitracker/
    ├── api/
    │   ├── PositionsVisitor.java      — main callback interface (your hook)
    │   └── PositionsTraverser.java    — iterates a saved game file and invokes the visitor
    ├── impl/                          — production game model + I/O
    │   ├── gamemodel/
    │   │   ├── EvaluatedGame.java
    │   │   ├── EvaluatedMove.java
    │   │   ├── EvaluatedPosition.java
    │   │   └── serialization/{GameModelWriter, GameModelReader}.java
    │   └── travers/PositionsVisitorImpl.java
    ├── impl2/gamemodel/               — alternative game-model variant (experimental)
    ├── impl3/                         — plain-text converters for training pipelines
    │   ├── PlainConverter_ALL.java
    │   └── PlainConverter_Balanced.java
    ├── impl4/                         — placeholder for future experiments
    └── run/                           — main entry points (runnable classes)
        ├── GamesGenerator_1PV.java
        ├── GamesGenerator_MultiPv.java
        ├── GamesGenerator_MultiPv_NNUETraining.java
        ├── GamesGenerator_Evaluator.java
        ├── GamesGenerator_NNUE.java
        └── GamesTraverser.java
```

# Public API

## `PositionsVisitor`

The single callback interface you implement to consume recorded positions.

```java
public interface PositionsVisitor {
    void begin(IBitBoard bitboard) throws Exception;
    void end() throws Exception;
    void visitPosition(IBitBoard bitboard, IGameStatus status, int whitePlayerEval);
}
```

For each position recorded in the file, `visitPosition(...)` is called with:
- a fully maintained `IBitBoard` already advanced to that position
- the terminal `IGameStatus` flag (ongoing, mate, stalemate, ...)
- the engine's evaluation at that point (from white's perspective)

## `PositionsTraverser`

Drives the iteration. Opens a saved game file and invokes your `PositionsVisitor` for every position in every game.

# Runners

## Game Generators (record games to file)

| Runner | What it does |
|---|---|
| `GamesGenerator_1PV` | runs an engine in single-PV mode and saves each game with one principal variation per move |
| `GamesGenerator_MultiPv` | multi-PV variant — captures the top N lines per move |
| `GamesGenerator_MultiPv_NNUETraining` | multi-PV generator tuned for NNUE training set extraction |
| `GamesGenerator_Evaluator` | runs an evaluator over already-recorded games and adds evaluation columns |
| `GamesGenerator_NNUE` | NNUE-specific generator using bundled networks (e.g. `network_bagatur_v1.nnue`) |

Each generator is parameterised with: the engine executable path, the output file path, and the desired number of games. Run with `--help` (or read the source) for the exact CLI arguments.

## Traverser

| Runner | What it does |
|---|---|
| `GamesTraverser` | walks a saved game file and forwards every position to a `PositionsVisitor` you specify |

# Implementation Layers

| Package | Role |
|---|---|
| `impl/gamemodel/` | the canonical `EvaluatedGame` / `EvaluatedMove` / `EvaluatedPosition` data model |
| `impl/gamemodel/serialization/` | `GameModelWriter` / `GameModelReader` — the binary on-disk format |
| `impl/travers/PositionsVisitorImpl` | default in-process visitor used by the traverser |
| `impl2/gamemodel/` | an alternative model used by experimental generators |
| `impl3/` | `PlainConverter_ALL` and `PlainConverter_Balanced` — convert saved games into plain text training files (full set vs. class-balanced) |
| `impl4/` | placeholder for future experiments |

# Typical Workflow

```
   any UCI engine (Houdini, Bagatur, Stockfish, ...)
              │
              ▼
   GamesGenerator_* (record)  ──►  binary games file on disk
                                          │
                                          ▼
                              GamesTraverser + your PositionsVisitor
                                          │
                                          ▼
                  feature extraction, dataset assembly, statistics
```

# Performance

The original benchmark cited in the project history:

> Running `Houdini_15a_w32.exe` for 12 hours generated ~10 000 games,
> containing more than 30 000 000 positions (plus their evaluations).
> Traversing them all afterwards took less than 90 seconds.

Useful for fans of chess, AI, and machine learning.

# Relationship to Other Bagatur Modules

```
   UCITracker
       │
       │  generates / traverses binary games
       ▼
   LearningImpl   ◄── NNUE / classical training input
       │
       ▼
   NNUE weights
       │
       ▼
   Search / Bagatur engine
```

Position iteration depends on `Bitboard` (`IBitBoard`, `IGameStatus`). Recorded games can also feed the `LearningImpl` training pipeline.

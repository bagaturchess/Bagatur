# PGNProcessor — PGN Parsing and Iteration Framework

Subproject of **BagaturChess** that provides a fast, callback-based parser for **PGN** (Portable Game Notation) files. Designed for bulk processing of large game corpora (millions of games), it powers the opening-book generator, NNUE training-set extraction, ELO filtering, and any tool that needs to walk through played positions in a structured way.

The parser turns a folder of `.pgn` (or zipped `.pgn`) files into a stream of board states that you observe through a simple visitor-style callback. Internally it uses the `IBitBoard` interface from `Bitboard`, so you get a fully maintained board (hash keys, attacks, SEE, etc.) for every move without having to reimplement anything.

## Project Structure

```
PGNProcessor/
├── LICENSE
├── readme.txt              — legacy notes
└── src/bagaturchess/tools/pgn/
    ├── api/                — public interfaces and the parser entry point
    │   ├── PGNParser.java
    │   ├── IGameIterator.java
    │   └── IPlyIterator.java
    ├── impl/               — parser internals
    │   ├── PGNInputStream.java          — streaming tokenizer
    │   ├── PGNGameParseHelper.java      — per-game parse logic
    │   ├── PGNGame.java, PGNTurn.java   — parsed-game model
    │   ├── PGNProperty.java, PGNGameProperties.java
    │   ├── PGNConstants.java
    │   ├── PGNUtils.java
    │   ├── PGNGameCounter.java
    │   ├── PGNParseException.java
    │   └── ExcludedGames.java
    └── run/                — example mains (use as templates for your own tools)
        ├── DummyGameIterator.java
        ├── FilterPGNFiles.java
        ├── FilterPGNFiles_ByELO.java
        ├── GameIterator_CollectAllInOne.java
        ├── GameIterator_ELOFilter.java
        ├── GameIterator_ExtractWinners.java
        └── TestBoard.java
```

## Public API

### `IGameIterator`

Top-level callback. Invoked for every game in the input stream.

```java
package bagaturchess.tools.pgn.api;

public interface IGameIterator {
    void preIteration(IBitBoard bitboard);                              // once, at the start
    void postIteration();                                               // once, at the end
    void preGame(int gameCount, PGNGame pgnGame, String pgnGameID, IBitBoard bitboard);
    void postGame();
}
```

Use this when you only care about whole-game properties (e.g. filtering by result, players, ELO).

### `IPlyIterator`

Extends `IGameIterator` and additionally fires per move. Use this when you need to observe positions throughout the game.

```java
public interface IPlyIterator extends IGameIterator {
    void preMove(int colour, int move, IBitBoard bitboard, int moveNumber);
    void postMove();
}
```

The `bitboard` parameter is the same `IBitBoard` instance threaded through the run — already advanced to the current position by the parser.

### `PGNParser`

Entry point. Construct it with your iterator implementation and an input folder; call `run()` to iterate.

```java
PGNParser parser = new PGNParser(myIterator, "/path/to/pgn/folder");
parser.run();
```

The parser handles both extracted `.pgn` files and zipped archives in the folder.

## Implementation Highlights (`impl/`)

| Class | Role |
|---|---|
| `PGNInputStream` | streaming tokenizer that decodes PGN bytes without materialising the whole file in memory |
| `PGNGameParseHelper` | per-game state machine, handles tag pairs, move list, comments, variations |
| `PGNGame` / `PGNTurn` | parsed-game data model (header + ordered turns) |
| `PGNProperty`, `PGNGameProperties` | tag-pair structure (`[Event "..."], [White "..."], ...`) |
| `PGNConstants`, `PGNUtils` | parsing constants and helpers (SAN → move conversion) |
| `PGNGameCounter` | quick count of games in a folder without full parse |
| `PGNParseException` | thrown for malformed inputs; the runner can skip and continue |
| `ExcludedGames` | bookkeeping for games skipped by filters |

## Example Runners (`run/`)

Each main class demonstrates a common pattern — use them as starting templates.

| Runner | What it does |
|---|---|
| `FilterPGNFiles` | retains only games matching a result and ELO threshold; rewrites them into a smaller PGN |
| `FilterPGNFiles_ByELO` | ELO-only filter variant |
| `GameIterator_ELOFilter` | iterator-only implementation of the ELO filter (no I/O writing) |
| `GameIterator_ExtractWinners` | example showing how to count wins per player |
| `GameIterator_CollectAllInOne` | bundles many small PGNs into one large file |
| `DummyGameIterator` | minimal example — useful as a copy-paste skeleton |
| `TestBoard` | sanity-checks the move legality across a sample PGN |

## Typical Usage Recipe

1. **Implement** either `IGameIterator` or `IPlyIterator`. The implementation is usually a few lines.
2. **Construct** `PGNParser` with your iterator and a folder containing PGN files.
3. **Run**.
4. **Inspect** the iterator's accumulated state at `postIteration()`.

```java
class MyVisitor implements IPlyIterator {
    int wPawnPushes;
    public void preIteration(IBitBoard b) { wPawnPushes = 0; }
    public void preGame(...) {}
    public void preMove(int colour, int move, IBitBoard b, int moveNumber) {
        if (colour == 0 && b.getFigureType(b.getMoveOps().getFromFieldID(move)) == /*pawn*/) {
            wPawnPushes++;
        }
    }
    public void postMove() {}
    public void postGame() {}
    public void postIteration() { System.out.println("White pawn pushes: " + wPawnPushes); }
}
```

## Relationship to Other Bagatur Modules

```
   .pgn files (TWIC, lichess dumps, ...)
              │
              ▼
       PGNProcessor (this module)
              │
              ├── OpeningGenerator    ── writes w.ob / b.ob
              ├── UCITracker          ── records eval traces for self-play
              └── LearningImpl        ── feeds NNUE training data
```

The parser depends on `Bitboard` for the `IBitBoard` view of board state. All emitted iterator events carry that board reference.

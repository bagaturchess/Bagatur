# Opening — Opening Book API

Subproject of **BagaturChess** providing the **runtime Opening Book API** the engine queries at the start of every game, plus utilities for inspecting and trimming the book files. Book generation lives in the sister project `OpeningGenerator`.

The book is consulted before the search starts: if the current position is in the book, Bagatur picks a move directly (according to the user-selected `Openning Mode` UCI option) instead of searching.

## Project Structure

```
Opening/
├── LICENSE
├── readme.txt              — legacy notes
└── src/bagaturchess/opening/
    ├── api/                — public interface
    │   ├── OpeningBook.java
    │   ├── OpeningBookFactory.java
    │   ├── IOpeningEntry.java
    │   └── traverser/
    │       ├── OpeningsVisitor.java
    │       └── OpeningTraverser.java
    ├── impl/               — implementations
    │   ├── model/
    │   │   ├── OpeningBookImpl_FullEntries.java   — full book (moves + counts)
    │   │   ├── OpeningBookImpl_OnlyHashkeys.java  — slim variant (hashes only)
    │   │   └── Entry_BaseImpl.java
    │   └── traverser/OpeningsVisitorImpl.java
    └── run/                — utilities (main classes)
        ├── BookTruncater.java
        ├── TraverseBook.java
        └── ShortBookConverter.java
```

## Public API

### `OpeningBook`

Core interface used by Bagatur during play.

```java
public interface OpeningBook extends Serializable {

    // Selection modes (governed by UCI option "Openning Mode")
    public static final int OPENING_BOOK_MODE_POWER0 = 1;  // most-played
    public static final int OPENING_BOOK_MODE_POWER1 = 2;  // mid randomness
    public static final int OPENING_BOOK_MODE_POWER2 = 3;  // full randomness

    public static final int OPENING_BOOK_MIN_MOVES = 7;

    IOpeningEntry getEntry(long hashkey, int colour);
    int[][] getAllMovesAndCounts(long hashkey, int colour);
    // ... see source for the full set
}
```

A position is identified by its Zobrist hash key. The book returns either a single `IOpeningEntry` (chosen by mode-specific selection) or the full list of candidate moves with play counts.

### `OpeningBookFactory`

Entry point for instantiating an `OpeningBook` from the binary `w.ob` / `b.ob` files. Bagatur calls this once on engine startup; the factory expects the files to already exist (they are produced by `OpeningGenerator`).

### `IOpeningEntry`

Small interface returning the chosen move and its statistics (play count, etc.).

### `traverser/`

`OpeningTraverser` + `OpeningsVisitor` form a visitor pattern over every board state stored in the book — used both by maintenance utilities and by external tools that need to enumerate book content.

## Implementations

| Class | Purpose |
|---|---|
| `impl/model/OpeningBookImpl_FullEntries` | full book — stores every position with all played moves and counts. This is what production Bagatur loads. |
| `impl/model/OpeningBookImpl_OnlyHashkeys` | slim variant — keeps only the position hashes (no moves). ~20× smaller on disk, useful for fast "is this position in book?" checks. |
| `impl/model/Entry_BaseImpl` | shared base for `IOpeningEntry` |
| `impl/traverser/OpeningsVisitorImpl` | default visitor implementation used by `run/TraverseBook` |

## Utilities (`run/`)

Main classes for working with `w.ob` / `b.ob` book files outside of the engine.

### `BookTruncater`

Reduces the on-disk size of the book by **removing rarely-played positions** (e.g. those seen fewer than 3 times in the training PGN set). The packaged Bagatur book sits around ~1 MB after truncation. Run with `--help` for current parameters.

### `TraverseBook`

Walks every position in the book and invokes the supplied `OpeningsVisitor` callback. Use it to:
- Print all stored lines for analysis
- Compare two books
- Extract statistics (number of unique positions, move-count distribution, etc.)

### `ShortBookConverter`

Converts a full book into the `OnlyHashkeys` variant (hash-only). The result takes roughly **1/20** of the disk space. Not used by the engine today — kept as reference for future minimal-footprint builds (e.g. Android).

## Relationship to Other Bagatur Modules

```
OpeningGenerator    (generates w.ob / b.ob from PGN)
       │
       ▼
   Opening (this module)  ──►  UCI engine startup
                              (queried before Search)
```

See [OpeningGenerator/README.md](../OpeningGenerator/README.md) for how the binary book files are produced.

## UCI Options That Affect This Module

| Option | Effect |
|---|---|
| `OwnBook` | toggle the opening book on/off |
| `Openning Mode` | `most played first` / `random intermediate` / `random full` — maps to `OPENING_BOOK_MODE_POWER*` |

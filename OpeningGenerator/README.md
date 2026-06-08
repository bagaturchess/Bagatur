# OpeningGenerator — Opening Book Generator

Subproject of **BagaturChess** that **builds the `w.ob` and `b.ob` binary opening books** consumed at runtime by the `Opening` module. Also serves as the canonical example of how to use `PGNProcessor` to extract structured data from large PGN corpora.

## Project Structure

```
OpeningGenerator/
├── LICENSE
├── readme.txt              — legacy notes
└── src/bagaturchess/tools/opening/generator/
    ├── impl/
    │   └── OpeningGamesIterator.java   — IPlyIterator that accumulates move stats
    └── run/
        └── OpeningsGenerator.java       — main entry point
```

Only two source files — generation logic is intentionally thin and delegates parsing to `PGNProcessor`.

## Generation Pipeline

```
       PGN files (e.g. TWIC dumps)
                │
                ▼
   PGNProcessor.FilterPGNFiles
        (extract only the games you want:
         win/loss, ELO threshold, etc.)
                │
                ▼
   OpeningsGenerator (this module)
        ├── walks every move of every game via PGNProcessor.PGNParser
        ├── records position-hash → played-move stats per side
        └── writes w.ob (white-to-move) and b.ob (black-to-move)
                │
                ▼
   Opening.OpeningBookFactory loads the files at engine startup
```

## Source Walkthrough

### `OpeningGamesIterator`

Implements the `IPlyIterator` interface from `PGNProcessor`. On every ply:

1. Capture the Zobrist hash of the current board state.
2. Bump the play-count for the recorded move under that hash, scoped to the side-to-move.

After all games are processed, two maps (white-to-move, black-to-move) are flushed to disk.

### `OpeningsGenerator`

`main(String[])` entry point. Wires together:

- The folder containing PGN files (zipped or plain) — first command-line argument.
- An `OpeningGamesIterator` configured with output paths for `w.ob` and `b.ob`.
- A `PGNParser` from `PGNProcessor` that runs the iterator over every game.

## Typical Workflow

1. **Download PGN games**: e.g. `~1,000,000` games from <https://theweekinchess.com/twic>.
2. **Filter** (in `PGNProcessor`):
   - Use `bagaturchess.tools.pgn.run.FilterPGNFiles` to keep only games that match your criteria (e.g. result `1-0` or `0-1`, both players ELO > 2600).
3. **Generate** (this module):
   - Run `bagaturchess.tools.opening.generator.run.OpeningsGenerator` pointing at the filtered output.
4. **Trim** (in `Opening`):
   - Optionally run `bagaturchess.opening.run.BookTruncater` to drop rarely-played positions and reach the ~1 MB target size shipped with Bagatur.
5. **Ship**: place `w.ob` / `b.ob` under `./data/`.

## Relationship to Other Bagatur Modules

```
   PGNProcessor   ◄────── parses PGN files into a stream of moves
       │                  via IPlyIterator
       │
       ▼
   OpeningGenerator (this module)
       │
       │  produces w.ob / b.ob
       ▼
   Opening   ─────────►  UCI engine startup (book lookup)
```

See [PGNProcessor/README.md](../PGNProcessor/README.md) for the iterator interfaces and [Opening/README.md](../Opening/README.md) for what happens to the generated files at runtime.

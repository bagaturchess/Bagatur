# Bitboard

Subproject of **BagaturChess** responsible for board representation, move generation, and base board-level services (hashing, attacks, SEE, material balance). Built around an abstract `IBitBoard` interface with several independent implementations that can be swapped without touching code in `Search` or other modules.

## Project Structure

```
Bitboard/src/bagaturchess/bitboard/
├── api/                         — public interfaces and contracts
├── common/                      — shared utility classes (Fen, MoveListener, ...)
├── impl/                        — original Bagatur implementation (unused — NPS)
├── impl_kingcaptureallowed/     — allows capturing the king (not used by Bagatur)
├── impl1/                       — currently active implementation
├── impl2/                       — experimental
├── impl3/                       — experimental
├── run/                         — runner / simulation entry points
└── tests/                       — placeholder for tests
```

---

## `api/` — Public Interfaces

This is the contract between the Bitboard module and the rest of Bagatur. Everything in the other modules (Search, NNUE, UCI, etc.) works against these interfaces.

### Core interfaces

| Interface | Role |
|---|---|
| **`IBoard`** | base board API — figures, move generation, make/unmake, hash keys, SEE, castling, game status |
| **`IBitBoard`** | extends `IBoard` with bitboard-specific access (long-based piece masks, attack maps) |
| `IMoveOps` | parsing / classification of encoded move integers (capture, promotion, castling, ...) |
| `IInternalMoveList` | container for generated moves on the hot path (no boxing) |
| `IMoveIterator` | iterator over legal moves |
| `ISEE` | Static Exchange Evaluation |
| `IPiecesLists` | piece lists indexed by type and colour |
| `IMaterialState` / `IMaterialFactor` | material balance and game-phase factor |
| `IBaseEval` | base material+PST evaluation (pre-NNUE) |
| `IGameStatus` | terminal-state detection (mate, stalemate, 50-move, repetition) |
| `IFieldsAttacks` / `IPlayerAttacks` | attack maps for squares / players |
| `IBoardConfig` | board configuration options |
| `IAttackListener` / `MoveListener` (common) | observer pattern for board updates |
| `PawnsEvalCache` | cache for pawn-structure evaluation |

### `IBoard` — main method groups

```java
public interface IBoard {
    // Position state
    int[] getMatrix();
    int getColourToMove();
    int getFigureID(int fieldID);
    int getFigureType(int fieldID);
    int getFigureColour(int fieldID);

    // Hashing
    long getHashKey();
    long getHashKeyAfterMove(int move);
    long getPawnsHashKey();
    long getMaterialHashKey();
    int  getStateRepetition();

    // Move generation
    int genAllMoves(IInternalMoveList list);
    int genCapturePromotionMoves(IInternalMoveList list);
    int genNonCaptureNonPromotionMoves(IInternalMoveList list);
    int genKingEscapes(IInternalMoveList list);
    int genAllMoves_ByFigureID(int fieldID, long excludedTo, IInternalMoveList list);

    // Make / unmake
    void makeMoveForward(int move);
    void makeMoveForward(String ucimove);
    void makeMoveBackward(int move);
    void makeNullMoveForward();
    void makeNullMoveBackward();

    // Static exchange evaluation
    ISEE getSee();
    int  getSEEScore(int move);
    int  getSEEFieldScore(int squareID);

    // Game state
    boolean isInCheck();
    boolean hasMoveInCheck();
    boolean hasMoveInNonCheck();
    boolean isCheckMove(int move);
    boolean isPossible(int move);
    boolean isDraw50movesRule();
    boolean hasSufficientMatingMaterial();

    // Castling
    CastlingType getCastlingType(int colour);
    boolean hasRightsToKingCastle(int colour);
    boolean hasRightsToQueenCastle(int colour);

    // Material / phase
    IMaterialState getMaterialState();
    IMaterialFactor getMaterialFactor();
    IBaseEval getBaseEvaluation();

    // Move history / game flow
    int[]  getPlayedMoves();
    int    getPlayedMovesCount();
    int    getLastMove();
    IGameStatus getStatus();

    // NNUE integration hook
    Object getNNUEInputs();

    // Misc
    String toEPD();
    IMoveOps getMoveOps();
    void mark(); void reset(); void revert();
}
```

### `IBitBoard` — bitboard extension

```java
public interface IBitBoard extends IBoard {

    public static final boolean IMPL1 = true;   // build-time toggle

    // Bitboard accessors
    long getFreeBitboard();
    long getFiguresBitboardByPID(int pid);
    long getFiguresBitboardByColourAndType(int colour, int type);
    long getFiguresBitboardByColour(int colour);

    // Attack tracking (optional, expensive to maintain)
    boolean getAttacksSupport();
    boolean getFieldsStateSupport();
    void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport);
    IPlayerAttacks getPlayerAttacks(int colour);
    IFieldsAttacks getFieldsAttacks();
}
```

### `BoardUtils` — implementation factory

`IBitBoard` is **instantiated through `api/BoardUtils`**. This is the single entry point for creating board instances; callers do not directly `new` an implementation class.

```java
// Default (uses IBitBoard.IMPL1 toggle)
IBitBoard board = BoardUtils.createBoard_WithPawnsCache();

// From a FEN
IBitBoard board = BoardUtils.createBoard_WithPawnsCache(fen);

// Full control
IBitBoard board = BoardUtils.createBoard_WithPawnsCache(
        fen, cacheFactoryClassName, boardConfig, pawnsCacheSize);

// Explicit implementation selection
IBitBoard board = BoardUtils.createBoard_WithPawnsCache(
        fen, cacheFactoryClassName, boardConfig, pawnsCacheSize, impl1);
```

Selection logic (`BoardUtils.createBoard_WithPawnsCache(..., boolean impl1)`):

- `impl1 == true` → constructs `impl1.BoardImpl` (the production magic-bitboard board).
- `impl1 == false` → constructs `impl.Board` wrapped in `impl.BoardProxy_ReversedBBs` together with a `PawnsEvalCache` built from a pluggable `DataObjectFactory<PawnsModelEval>` (so callers can swap the pawn-evaluation backend).

The default flag is `IBitBoard.IMPL1 = true`, so by default Bagatur runs on `impl1`. To experiment with `impl3` (or any other) it is enough to wire a new branch in `BoardUtils` and route the factory to it; no consumer code outside `Bitboard` needs to change.

`BoardUtils` also provides board-aware helpers used by Search, UCI, and tools:

| Method | Purpose |
|---|---|
| `getMoves(String[] pv, IBitBoard)` | parse a PV (array of UCI moves) into `int`-encoded moves while replaying / unwinding the board |
| `getPlayedMoves(IBitBoard)` | dump the played-move stack as a space-separated UCI string |
| `movesToString(int[], IBitBoard)` | format a move list as comma-separated UCI text |
| `playGameUCI(IBitBoard, String)` | apply a sequence of UCI moves onto a board |
| `parseSingleUCIMove(IBitBoard, String)` | resolve one UCI move string against the current legal-move set |

---

## `common/` — Shared Classes

| Class | Role |
|---|---|
| `Fen` | parse / serialize FEN strings |
| `MoveListener` | observer interface for external listeners on board updates |
| `BackupInfo` | snapshot info for the `mark()` / `revert()` flow |
| `BoardStat` | board statistics helper |
| `GlobalConstants` | global constants (board size, piece IDs, ...) |
| `Properties` | configuration parameters |
| `Utils` | miscellaneous helpers |

---

## Implementations

### `impl/` — original Bagatur implementation

The first pure-Java implementation written specifically for Bagatur. It has the richest subpackage structure (`attacks`, `datastructs`, `endgame`, `eval`, `movegen`, `movelist`, `plies`, `state`, `utils`, `zobrist`). Includes the built-in pawn-structure evaluator (`impl/eval/pawns/model/PawnsModelEval`), which is used by the other implementations through `IBoard.getPawnsStructure()`.

**Status**: ❌ **not used currently** — too slow in NPS compared to `impl1`. The code is kept because it contains the rich evaluation (`PawnsModelEval` etc.) and substantial board-level code still required by the other implementations (`api/IBoard.java` imports `impl.eval.pawns.model.PawnsModelEval`).

### `impl_kingcaptureallowed/` — king-capture variant

Implementation that **allows capturing the king** as a legal move (for analyzing illegal positions or special use cases). Small — only `Board3.java` and `Board3_Adapter.java`.

**Status**: ❌ **not used by Bagatur** in standard play. Useful for external tools / analysis.

### `impl1/` — production implementation (24 files)

The active implementation Bagatur uses today. A clean port of a `Chess22k`-style magic-bitboard board representation, optimised for high NPS.

```
impl1/
├── BoardImpl.java                    — IBitBoard implementation (adapter)
├── BaseEvaluation.java               — base material+PST eval
├── NNUE_Input.java                   — NNUE accumulator feed
└── internal/                         — high-perf core
    ├── ChessBoard.java               — main board state struct
    ├── Bitboard.java                 — bitboard constants / utils
    ├── MoveGenerator.java            — magic-bitboard move generation
    ├── MagicUtil.java                — magic numbers / attack tables
    ├── MoveUtil.java, MoveWrapper.java — encoded move ops
    ├── CastlingConfig.java, CastlingUtil.java — castling rules
    ├── CheckUtil.java                — check detection helpers
    ├── Zobrist.java                  — Zobrist hash keys
    ├── SEEUtil.java                  — SEE calculation
    ├── StaticMoves.java              — precomputed knight / king moves
    ├── MaterialUtil.java             — material-key helpers
    ├── PieceToHistory.java           — piece→to indexing for histories
    ├── ChessBoardUtil.java, ChessBoardTestUtil.java
    ├── ChessConstants.java, EngineConstants.java, EvalConstants.java
    ├── Assert.java, Util.java
```

**Status**: ✅ **production implementation**. Selected via `IBitBoard.IMPL1 = true`.

### `impl2/` — experimental (12 files)

A smaller experimental implementation. Includes `ChessBoard`, `Bitboard`, `MoveGeneration`, `MagicUtil`, `ChessBoardBuilder`, and others. No `internal/` subpackage.

**Status**: ❌ **not used** — experimental playground.

### `impl3/` — experimental (24 files)

The structure mirrors `impl1/` — has its own `internal/` subpackage and the same core classes (`ChessBoard`, `MoveGenerator`, `MagicUtil`, ...). Experimental changes are made here without breaking the production `impl1`.

**Status**: ❌ **not used** — research fork for trying new ideas.

---

## Additional Packages

### `run/`

| Class | Description |
|---|---|
| `Simulate` | self-contained entry point for running a simple simulation / perft |

### `tests/`

Placeholder for integration tests of the Bitboard layer. Currently empty (tests live in the Search / UCITournament modules).

---

## Relationship to Other Bagatur Modules

```
            ┌──────────────┐
            │ UCI / Tools  │
            └──────┬───────┘
                   │ (uses IBitBoard)
                   ▼
┌──────────────────────────────────────┐
│         Bitboard (this module)        │
│  ┌──────────┐    ┌────────────────┐   │
│  │   api/   │◀───│   impl1/ ...   │   │
│  └────┬─────┘    └────────────────┘   │
└───────┼──────────────────────────────┘
        │ (IBitBoard, IBoard)
        ▼
┌──────────────┐  ┌──────────────┐
│    Search    │  │     NNUE     │
└──────────────┘  └──────────────┘
```

All consumers work ONLY against the interfaces in `api/` and obtain instances through `api/BoardUtils`. Swapping implementations (for example, testing `impl3` against `impl1`) requires only changing the factory branch in `BoardUtils` — no other module is affected.

## Conventions

- **Move encoding**: all of Bagatur uses `int`-encoded moves; `IMoveOps` decodes them.
- **Coordinates**: `0..63` field indices (a1 = 0, h8 = 63), `0..7` rank / file.
- **Colours**: white = 0, black = 1 (see `Constants` in `Search`).
- **Hash keys**: Zobrist (see `impl1/internal/Zobrist.java`).
- **Hot-path discipline**: no allocation in move generation / make / unmake.
- **Backward compatibility**: `IBoard` methods are stable; new features are added as new methods, not as breaking changes.

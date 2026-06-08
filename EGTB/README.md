# EGTB — Endgame Tablebases

Subproject of **BagaturChess** providing access to **Syzygy Endgame Tablebases** through a JNI bridge. During search, when the position drops to a small piece count covered by the tablebases, the engine queries Syzygy for an exact result (Win/Draw/Loss + DTZ — distance to zeroing). Both **local** Syzygy files and an **online** lichess.org tablebase service are supported.

> The historical text below referenced Gaviota EGTB integration. That path is no longer active; production Bagatur uses **Syzygy** exclusively (see UCI options `SyzygyPath` and `SyzygyOnline` in the project root README).

## Project Structure

```
EGTB/
├── JSyzygy.dll            — Windows JNI native (Syzygy probe shared library)
├── LICENSE
├── src/
│   ├── bagaturchess/egtb/syzygy/        — Bagatur-facing Syzygy API
│   │   ├── SyzygyTBProbing.java         — singleton entry point used by Search
│   │   ├── SyzygyJNIBridge.java         — JNI calls into JSyzygy.dll
│   │   ├── SyzygyConstants.java         — WDL/DTZ constants, error codes
│   │   ├── OnlineSyzygy.java            — fallback to lichess online tablebase
│   │   ├── JSONUtils.java               — minimal JSON parsing for online probing
│   │   └── run/SyzygyTest.java          — standalone test runner
│   └── com/winkelhagen/chess/syzygy/    — third-party JNI helpers (port)
└── readme.txt             — legacy notes (kept for historical context)
```

## Public API — `SyzygyTBProbing`

`bagaturchess.egtb.syzygy.SyzygyTBProbing` is a process-wide singleton that the Search module consults during the engine loop.

Typical lifecycle:

1. **Load native** — `JSyzygy.dll` (Windows) or its `.so` / `.dylib` equivalent on Linux / macOS.
2. **Configure path** — `load(String tbPath)` points the native probe to a folder containing the Syzygy `.rtbw` / `.rtbz` files.
3. **Probe**:
   - `probeWDL(...)` — Win / Draw / Loss result.
   - `probeDTZ(...)` — Distance-to-Zeroing root probe (returns the best move and DTZ value).
4. **Online fallback** — if `OnlineSyzygy` is enabled and local probing misses (e.g. position has up to 7 pieces and you only have 5-piece local files), the call falls back to `http://tablebase.lichess.ovh/standard?fen=...`.

`SyzygyConstants` defines the WDL labels and JNI error codes the caller must translate.

## How It Plugs Into Bagatur

```
Search loop
  └── on shallow-piece positions
        └── SyzygyTBProbing.getSingleton()
              ├── probe local files (via JSyzygy native)
              └── if SyzygyOnline UCI option is true
                    └── OnlineSyzygy.probe(fen) over HTTPS
```

The SyzygyDTZCache (TT-style cache for results) lives in `Search` and avoids re-probing the same position.

## UCI Options That Affect This Module

| Option | Effect |
|---|---|
| `SyzygyPath` | absolute path to the local Syzygy file directory. Default: `./data/egtb` |
| `SyzygyOnline` | when true, falls back to lichess online tablebase for unresolved positions |
| `SyzygyDTZCache` | toggles the in-process DTZ cache (per-thread for SMP builds) |

## Distributed Files

The Bagatur download bundles a **subset of 22 most common 5-piece endgames** under `./data/egtb/`. For deeper analysis, download a fuller Syzygy set (3-4-5-6-7 pieces) and point `SyzygyPath` at it.

## Running the Standalone Test

```bash
java -cp build/classes:lib/* bagaturchess.egtb.syzygy.run.SyzygyTest
```

The test exercises load / probeWDL / probeDTZ on a few canonical endgame positions.

## Notes for Contributors

- The native bridge expects the DLL/SO to be loadable from `java.library.path`. Ensure `JSyzygy.dll` is in the working directory or pass `-Djava.library.path=...`.
- `SyzygyJNIBridge` is the only class that calls `native` methods — all other code talks to it through `SyzygyTBProbing`.
- `com.winkelhagen.chess.syzygy` is an upstream helper port; treat it as third-party (do not modify without coordinating with the upstream project).

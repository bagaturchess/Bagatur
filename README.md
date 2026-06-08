![Bagatur logo](Resources/bin/engine/logo/Bagatur.jpg)

# Overview

Bagatur is one of the strongest Java chess engines in the world.

It runs both on Android and on desktop computers (including virtual machines with many CPU cores in the cloud):

- **Desktop**: a console program that speaks the [UCI protocol](https://www.chessprogramming.org/UCI). It plugs cleanly into any UCI-compatible chess GUI, such as [Arena Chess GUI](http://www.playwitharena.de/).
- **Android**: shipped as a standalone app with its own user interface, available through the [Bagatur Chess Engine with GUI](https://metatransapps.com/bagatur-chess-engine-with-gui-chess-ai/) page. The Android sources are open and live in their own repository: [Android_APK_ChessEngineBagatur](https://github.com/MetatransApps/Android_APK_ChessEngineBagatur).

If you like the project, please give it a star! :-)

# Downloads (desktop)

- New versions are published as standard [GitHub releases](https://github.com/bagaturchess/Bagatur/releases).
- Older versions are bundled in a [legacy zip archive](https://github.com/bagaturchess/Bagatur/archive/refs/tags/ALL-OLD-VERSIONS-DOWNLOADS-BEFORE-BAGATUR-2.2E.zip); inside the archive, all of them are under `/Downloads/Engine/`.

# Running it

Bagatur runs on every operating system with a Java platform:

- **Android**: install the [Bagatur Chess Engine with GUI](https://metatransapps.com/bagatur-chess-engine-with-gui-chess-ai/) app.
- **Windows**: the distribution ships with `.exe` launchers. Steps:
  1. Install any UCI-compatible GUI — the most popular one is [Arena Chess GUI](http://www.playwitharena.de/).
  2. Install a Java Runtime Environment (JRE) 17 or later (JRE, not JDK — the `.exe` launcher requires the runtime). Get it from [java.com](https://java.com/).
  3. Unpack the Bagatur distribution somewhere (Arena has an `engines/` subfolder that works well).
  4. Register the engine inside the GUI (refer to your GUI's documentation if you have not done this before).
  5. e2-e4 and enjoy :-)
- **Linux**: the project root contains `.sh` launchers; just make sure Java 17 (or later) is on your `PATH`.

# UCI Options

## SMP-only options

These apply only to the [SMP (multi-core) version](https://www.chessprogramming.org/SMP). On Windows it is started by `Bagatur_64_2+_cores.exe` or `Bagatur_mcore.bat`; on Linux by `Bagatur_mcore.sh`. SMP has been tested with up to 64 CPU cores / threads. There is a known JVM-related scaling limitation — more details in [Search/SMP.scaling.issue.txt](https://github.com/bagaturchess/Bagatur/blob/master/Search/SMP.scaling.issue.txt).

- **SMP Threads** (`spin`, default `logical_processors/2`, min `1`, max `logical_processors/2`): number of search threads.
- **CountTranspositionTables** (`spin`, default `1`, min `1`, max `SQRT(logical_processors/2)`): number of transposition tables used by the SMP version. The default of `1` is best in most cases; raise it only for experiments. See [Search/SMP.scaling.issue.txt](https://github.com/bagaturchess/Bagatur/blob/master/Search/SMP.scaling.issue.txt).

## Options available in both single-core and SMP

- **Logging Policy** (`combo`, default `single file`, values: `single file` / `multiple files` / `none`): whether Bagatur writes log files describing its actions.
- **OwnBook** (`check`, default `true`): whether to use the bundled opening book under `./data/w.ob` and `./data/b.ob`. The book is built from a few million PGN games played by grandmasters and engines over the past 20 years, filtered down to the most frequently played continuations. Opening-name detection is not yet supported but is on the backlog (and is already available in the Android version).
- **Ponder** (`check`, default `true`): whether to keep thinking on the opponent's time.
- **MultiPV** (`spin`, default `1`, min `1`, max `100`): how many best lines to report — `1` shows only the best line, higher values report the top N.
- **SyzygyPath** (`string`, default `./data/egtb`): path to the Syzygy tablebase files. The full resolved path is printed in response to the `uci` command.
- **SyzygyOnline**: when `true`, if a local tablebase probe fails (e.g. you only have 5-piece files but the position needs 7), the engine falls back to the lichess online tablebase at `http://tablebase.lichess.ovh/standard?fen=...`.
- **Openning Mode** (`combo`, default `most played first`, values: `most played first` / `random intermediate` / `random full`): effective only when `OwnBook` is `true`. `most played first` picks the statistically most popular book move for the current position. `random full` picks uniformly at random from all stored book moves for the position. `random intermediate` is the compromise: a random choice among the top 3 candidates. *(The option label preserves the historical spelling.)*
- **UCI_Chess960** (`check`, default `false`): `false` = classic chess; `true` = Fischer Random Chess (both FRC and DFRC modes are supported).
- **MemoryUsagePercent** (`spin`, default `73`, min `50`, max `90`): a fine-tuning knob — leave it alone unless you know what you are doing. It controls how much of the available heap the engine targets, which keeps the JVM from spending too much time in Garbage Collection.
- **TranspositionTable** (`check`, default `true`): whether to use the Transposition Table.
- **EvalCache** (`check`, default `true`): whether to cache evaluation results. In the SMP build this cache is per thread.
- **SyzygyDTZCache** (`check`, default `true`): whether to cache Syzygy DTZ probe results. Per-thread in the SMP build.

# Syzygy Endgame Tablebases

The Bagatur distribution bundles a subset of Syzygy tablebases under `./data/egtb/` — 22 of the most common endgames with up to 5 pieces. The `SyzygyPath` UCI option points there by default. Change it if you have a fuller or larger Syzygy set installed elsewhere.

# NNUE (Efficiently Updatable Neural Network)

Since version 5.0, Bagatur uses [NNUE](https://www.chessprogramming.org/NNUE) as its evaluation function. The Java port lives in its own subproject — see [NNUE/](https://github.com/bagaturchess/Bagatur/tree/master/NNUE).

# Elo Rating

A chess engine's strength is measured in [Elo](https://en.wikipedia.org/wiki/Elo_rating_system).

According to CCRL 40/15, the latest official Elo ratings of every well-tested version (with more than 300 games at 40/15 time control) are listed on this page: [CCRL — Bagatur family](http://www.computerchess.org.uk/ccrl/4040/cgi/compare_engines.cgi?family=Bagatur).

Special thanks to [Graham Banks](https://www.chessprogramming.org/Graham_Banks), who has put tremendous effort into testing Bagatur versions over the years!

The latest official Elo rating of Bagatur is **~3400 Elo**.

# Technical details — sub-component documentation

- [Bitboard](https://github.com/bagaturchess/Bagatur/blob/master/Bitboard/README.md)
- [NNUE](https://github.com/bagaturchess/Bagatur/tree/master/NNUE)
- [Search Algorithm](https://github.com/bagaturchess/Bagatur/blob/master/Search/readme.md)
- [Endgame Tablebases](https://github.com/bagaturchess/Bagatur/blob/master/EGTB/README.md)
- [Machine Learning](https://github.com/bagaturchess/Bagatur/blob/master/LearningImpl/README.md)
- [Opening API](https://github.com/bagaturchess/Bagatur/blob/master/Opening/README.md)
- [Opening Generator](https://github.com/bagaturchess/Bagatur/blob/master/OpeningGenerator/README.md)
- [PGNProcessor](https://github.com/bagaturchess/Bagatur/blob/master/PGNProcessor/README.md)
- [UCITracker](https://github.com/bagaturchess/Bagatur/blob/master/UCITracker/readme.md)

# When and how the Bagatur Chess Engine project started

The project started as a bet between me and one of my friends from my first two years at university — he liked to play chess, and I could not win a single game against him. Over time this made me restless, and eventually I promised him (we shook on it) that I would write a chess program that would beat him.

Luckily for me, no time frame was agreed on, and... years later, I won the bet! :-) The whole story is captured in this YouTube video: ["How was the idea of Bagatur Chess Engine application born?"](https://www.youtube.com/watch?v=_rNBxbUAbS0).

The first public, open-source version of Bagatur was released on 2011-02-27 and is still available [here](https://sourceforge.net/projects/bagaturchess/files/BagaturEngine/older/). The project itself, however, started about 10 years earlier — somewhere between 1999 and 2000. It went through many private, non-public versions until 2011 (I do not even have their history any more). The early versions were very weak: they would make 2-3 moves and then crash. I spent hours and days hunting bugs and trying to understand why things did not work.

At that time the internet was almost empty, and I had no idea that software like CuteChess or Arena even existed, nor that there was a UCI protocol — so I also wrote my own graphical user interface, in Java AWT and Swing. Chess programming exposes you to a wide range of programming disciplines.

I picked Java mostly because of my (limited) experience with it. It also helps that Java was rather modern and popular at the time — only a few years after its first release, the early days of the Java language and the Java ecosystem.

So, if you are planning to write a chess engine: better start early — it takes time to reach a stable version that can beat you. :-)

# Contact the author

You can reach me on LinkedIn: [Krasimir Topchiyski](https://www.linkedin.com/in/topchiyski/) — or by email at k_topchiyski@yahoo.com.

# Some personal thoughts

According to CCRL, there are fewer than ~500 chess engines in the world. That means there are not that many people genuinely interested in chess-engine programming. The author of a chess engine has typically to be interested not only in programming but also in chess itself — and willing to invest a lot of spare time without any incentive, just for fun and curiosity. For that reason, I am always happy to see new engines and new authors appear!

I cannot speak for other authors, but I would also recommend a small, well-measured dose of craziness. It helps. As you release version after version, each release aiming for a higher Elo, sometimes you get stuck — sometimes for months — and it starts to feel like banging your head against a wall. When I reach that state, I step away from Bagatur for a while and wait for inspiration to come back. Always remember: it should be for fun! :-)

# Bagatur is powered by [YourKit Java Profiler](https://www.yourkit.com/java/profiler/)

![YourKit Logo](https://www.yourkit.com/images/yklogo.png)

This excellent tool is used to find and fix performance, scalability, and memory-allocation issues. YourKit supports open-source projects with innovative and intelligent tools for monitoring and profiling.

# Bagatur flavours

The chess engine [JFish](https://github.com/bagaturchess/JFish) uses Bagatur as its base and plugs in the Stockfish NNUE as its evaluation function. The goal is to have the strongest Java chess engine as a reference.

# Revision history

The full release history is collected [here](https://github.com/bagaturchess/Bagatur/blob/master/Resources/doc/engine/txt/release_notes_BagaturEngine.txt).

# Credits

Fortunately, I am not alone on this project — without the ideas, support, and help from many people and websites, Bagatur would not be what it is today. Many thanks to:

1. My wife and my family, because every now and then I have been borrowing time from our leisure to work on this project.
2. [Serendipity](https://github.com/xu-shawn/Serendipity) — thanks to Shawn for explaining how he trained the NNUE network of the Serendipity chess engine, and for the reference Java code that handles the network.
3. Desislava Chocheva, for her hospitality and willingness to help. Without her support the introductory video could not have happened.
4. Ivo Zhekov, for motivating me to start this project and for accepting the challenge with such a strong opponent in front of the camera.
5. Simeon Stoichkov, for his general support on chess topics in Bulgaria, and for providing the chess pieces and the chess clock used in the introductory video.
6. Varna Sound, for their willingness to support us and to contribute with their great rap music.
7. Iavor Stoimenov, for the endless discussions about chess topics and chess engines.
8. Ivo Simeonov, for all the ideas, support, discussions, tests, and contributed source code (e.g. the initial version of pawn-structure evaluation, the C porting, and the `.exe` launcher).
9. Graham Banks from the [Computer Chess Rating Lists (CCRL)](https://www.computerchess.org.uk/ccrl/) — see also [CCRL 40/40](https://ccrl.chessdom.com/ccrl/4040/) — for organising and broadcasting chess-engine tournaments over the internet for many years.
10. Anton Mihailov, Aloril, and Kan from the [Top Chess Engine Championship (TCEC)](https://tcec-chess.com/), for their invitations to Bagatur and for its participation in chess-engine tournaments for many seasons. Special thanks to Aloril, who contributed a lot to the testing of Bagatur's Symmetric Multiprocessing (SMP) version on a CentOS box with more than 100 CPU cores. Thanks a lot for your support whenever engine issues / bugs surfaced!
11. Olivier Deville, for his great support during ChessWar XVII — [open-aurec.com/chesswar](http://www.open-aurec.com/chesswar/).
12. Zoran Sevarac, author of Neuroph and co-author of Deep Netts, for his great support with our experiments on Neural Networks and Machine Learning in Java.
13. Roelof Berkepeis, for testing, for sharing his chess experience, and for the great ideas captured as [issues on the Bagatur GitHub page](https://github.com/bagaturchess/Bagatur/issues).
14. Sameer Sehrawala, for the latest logo and his general support.
15. Dusan Stamenkovic ([chessmosaic.com](http://www.chessmosaic.com/)), for several earlier Bagatur logos.
16. The Internet itself, for connecting us.
17. The Open Source community!
18. The [MTD(f)](https://en.wikipedia.org/wiki/MTD(f)) algorithm — Bagatur's parallel search is based on this idea.
19. [winrun4j](http://winrun4j.sourceforge.net/), for the Windows executables.
20. All UCI-compatible GUIs, and the UCI protocol itself.
21. [REBEL](http://www.rebel13.nl/) — a very helpful web page.
22. The Glaurung chess engine, for nice ideas inside its evaluation function (e.g. king safety).
23. [Fruit](http://www.fruitchess.com/), the legendary program with a beautifully clean and simple design.
24. [CuckooChess](https://github.com/sauce-code/cuckoo), one of the first Java chess engines.
25. Chess22k, an exciting Java chess engine — strong and well written.
26. The source code of the strongest open-source chess engine — [Stockfish](https://stockfishchess.org/).
27. [SourceForge](https://sourceforge.net/).
28. [GitHub](https://github.com/).
29. [Stack Overflow](https://stackoverflow.com/).
30. ... and many others!


# Overview

Bagatur chess engine is one of the strongest Java chess engines in the world.

It runs on Android as well as on "desktop" Computers (or even on virtual machine with many CPU cores into the Cloud):
 - For desktop computers, it runs as a program whith a console and supports commands of the <a href="http://wbec-ridderkerk.nl/html/UCIProtocol.html">UCI protocol</a>. It could be easily imported in Chess programs with user interfaces, like <a href="http://www.playwitharena.de/">Arena Chess GUI</a>.
 - For Android, the app is available on different app stores <a href="https://metatransapps.com/bagatur-chess-engine-with-gui-chess-ai/">Bagatur Chess Engine with GUI</a>. It has its own user interface. The source code of the Android version is also open source and could be found here: https://github.com/MetatransApps/Android_APK_ChessEngineBagatur

If you like the project, please give it a star! :-)

# Downloads for desktop computer

- The new versions of Bagatur are released as standard github releases <a href="https://github.com/bagaturchess/Bagatur/releases">here</a>.
- The old versions of Bagatur are available on github under this <a href="https://github.com/bagaturchess/Bagatur/archive/refs/tags/ALL-OLD-VERSIONS-DOWNLOADS-BEFORE-BAGATUR-2.2E.zip">zip archive download</a>, which contains all old versions under the path "/Downloads/Engine/" inside the archive file.

# Running it

The program runs under all Operating Systems, which support Java platform:
* **Android**, <a href="https://metatransapps.com/bagatur-chess-engine-with-gui-chess-ai/">Bagatur Chess Engine with GUI</a>.
* **Windows**, there are *.exe files for direct run. Here are the steps necessary to run the engine:
  * Download an arbitrary UCI user interface. For example the most popular one is <a href="http://www.playwitharena.com/">Arena Chess GUI</a>.
  * Install the UCI user interface on your computer.
  * Ensure that the Java Runtime Environment (JRE) 1.8 or later is installed on your computer (it should be the case nowadays but if not, have in mind that JRE is required, only JDK doesn't work). It could be downloaded from <a href="https://java.com/">java.com</a>
  * Unpack this distribution somewhere (Arena has a sub-folder called 'engines', you can extract it there).
  * Open the UCI user interface and register the engine inside (You should become familiar with the installed UCI user interface anyway). You may use win32 or win64 version depending on your choice.
  * E2-E4 and enjoy :-)
* **Linux**, in the bin directory there are *.sh files for running

# UCI Options

Option available only for the <a href="https://www.chessprogramming.org/SMP">SMP version</a> of Bagatur. The SMP (multicore) version can be started by Bagatur_64_2+_cores.exe and Bagatur_mcore.bat for Windows and with Bagatur_mcore.sh under Linux. It is tested with up to 64 CPU cores and threads. There is known sclaing issues, caused by Java (more info here: https://github.com/bagaturchess/Bagatur/blob/master/Search/SMP.scaling.issue.txt).
 - SMP Threads (type spin default [logical_processors/2] min 1 max [logical_processors/2]):
 - CountTranspositionTables type spin default 1 min 1 max [SQRT(logical_processors/2)] - Defines the count of Transposition Tables, which the SMP version will use. In most cases, it should be best to be set to 1 and could be changed only for experiments. See also https://github.com/bagaturchess/Bagatur/blob/master/Search/SMP.scaling.issue.txt

All other options are available for both versions: single core and SMP.
 - Logging Policy (type combo default single file var single file var multiple files var none): whether Bagatur will create log files on the file system with details of its actions.
 - OwnBook (type check default true): whether to use the own book included into the download, which is packed under ./data/w.ob and ./data/b.ob. These are games extracted from a few milions of PGN games played last 20 years by grandmasters and computer programs. They are filtered and the files contain a subset of most often played games. Unfortunatelly the name of the used opening is not supported at the moment but this features is defenitelly in our backlog and will be included in the Android version.
 - Ponder (type check default true): whether to also think when the opponent thinks.
 - MultiPV (type spin default 1 min 1 max 100): whether to show only the best line or to show the best 2-3-N lines.
 - SyzygyPath (type string default ./data/egtb): path to the syzygy tables. If you send 'uci' command to the engine, it will show the full path to the syzygy directory.
 - SyzygyOnline: if true and TB probing with local files is unsuccessful with up to 7 pieces, than it will request lichess server on this url http://tablebase.lichess.ovh/standard?fen=...
 - Openning Mode (type combo default most played first var most played first var random intermediate var random full): Valid only when OwnBook is set to true. The 'most played first' option playes the most often played move (statistically) for given position. 'random full' option playes random move from all available opening moves for this postion. And the 'random intermediate' option is something in the middle and plays random move selected only from the top 3 available moves for this position.
 - UCI_Chess960 (type check default false): false = classic chess, true = Fischer Random Chess (both FRC and DFRC modes are supported). 
 - MemoryUsagePercent (type spin default 73 min 50 max 90): This is option for fine tunning and should not be changeed in general. It is Java specific and prevents the JVM to overdo the Garbage Collection.
 - TranspositionTable (type check default true): whether to use Transposition Table.
 - EvalCache (type check default true): whether to use cache for the evaluation function. (This cache is per thread for the SMP version)
 - SyzygyDTZCache (type check default true): whether to use cache for used syzygy scores. (This cache is per thread for the SMP version)

# Syzygy Endgame Tablebases

The download of Bagatur contains subset of syzygy tablebases placed under ./data/egtb/ directory. It contaiuns 22 of the most common endgames with up to 5 pieces. By default the option 'SyzygyPath' is set to this directory. You could change this UCI option if you have complete or bigger set of syzygy tablebases donwloaded on your computer.
 
# NNUE (Efficiently Updatable Neural Network)

Since version 5.0, Bagatur is using NNUE as evaluation function. There was a Java portings. More info: https://github.com/bagaturchess/Bagatur/tree/master/NNUE

# Elo Rating

Chess strength and rating of a chess engine is measured in Elo.
According to CCRL 40/15, the latest offical Elo ratings of all well tested version (with more than 300 games and time control 40/15), are available on this page: http://www.computerchess.org.uk/ccrl/4040/cgi/compare_engines.cgi?family=Bagatur

Special Thanks to <a href="https://www.chessprogramming.org/Graham_Banks">Graham Banks</a>, who put a huge efforts in testing Bagatur versions through the years!

Latest official Elo rating of Bagatur is ~3000 Elo. To make it easy to check, here is a probably outdated screenshot of the computerchess.org web page above:

<a href="" rel="Bagatur Elo rating"><img src="ELO_2021.11.18.png" alt="" /></a>

# More readings with technical details and explanations for each sub-component of Bagatur
- <a href=https://github.com/bagaturchess/Bagatur/blob/master/Bitboard/readme.txt>Bitboard</a>
- <a href=https://github.com/bagaturchess/Bagatur/blob/master/EGTB/readme.txt>Endgame Tablebases</a>
- <a href=https://github.com/bagaturchess/Bagatur/tree/master/LearningImpl>Machine Learning</a>
- <a href=https://github.com/bagaturchess/Bagatur/edit/master/Opening/readme.txt>Opening API</a>
- <a href=https://github.com/bagaturchess/Bagatur/edit/master/OpeningGenerator/readme.txt>Opening Generator</a>
- <a href=https://github.com/bagaturchess/Bagatur/blob/master/PGNProcessor/readme.txt>PGNProcessor</a>
- <a href=https://github.com/bagaturchess/Bagatur/blob/master/Search/readme.txt>Search Algorithm</a>
- <a href=https://github.com/bagaturchess/Bagatur/tree/master/UCITracker>UCITracker</a>
- <a href=https://github.com/bagaturchess/Bagatur/tree/master/NNUE>NNUE</a>

# When and how Bagatur Chess Engine project has started
The project has started as a bet between myself and one of my friends from my first 2 years in the University, who like to play chess.
I was unable to win a single chess game against him! This made me nervous over the time and at the end I have promised him (we bet) that I will create a chess program, which will win a game against him!
Fortunately the time frame was not mentioned during the bet and ... years later I win it! :-)
The whole story is captured in this youtube video "How was the idea of Bagatur Chess Engine application born?": https://www.youtube.com/watch?v=_rNBxbUAbS0

The first public, open source version of Bagatur was available since 2011-02-27 here: https://sourceforge.net/projects/bagaturchess/files/BagaturEngine/older/
The project actually started ~10 years before this date. The actual start date was between 1999 and 2000.
It had many proprietary non-public versions until 2011. I even don't have history of them but they were quite weak in playing chess. First versions just succeeded to play 2-3 moves and then crashes. I have spend hours and days trying to fix the bugs and find out why it doesn't work correctly.
At this point in time, the internet was almost an empty space and I was not aware of softwares running chess engines like now CuteChess and Arena and I was not aware of the existence of the UCI protocol, so I also have created an own Graphical User Interface (GUI) based on Java AWT and Swing.
Chess porgramming gives you diverse programming experiences ...
I have selected Java as a programming language, mainly because of my little (but existing) experience with it.
I have to also admit that Java was quite modern and polular at this point in time. This was the time a few years after the first releases of the Java itself. The early ages of the Java language and Java technologies.

So, if you plan to work on a chess engine, better start earlier, you need time to achieve stable version which beats you! :-)

# Contact the Author

In order to contact me, you could use LinkedIn <a href="https://www.linkedin.com/in/topchiyski/">Krasimir Topchiyski</a> or email me at k_topchiyski@yahoo.com

Some personal toughts ...

According to CCRL, there are below 500+ chess engines in the world.
This means that not many people are really interested in programming chess engines.
Most probably the author of a chess engine is interested not only in programming but also in chess game. He/she must have enough willingness to invest spare time in chess programing, without any incentives, just for fun and driven by curiosity.

Because of this fact, I am always happy to see new engines and new authors!

I don't know for the other chess engine authors, but I would add small and well balanced portion of craziness ... it helps in chess programming, while you release version after version, targeting better Elo strength of each new release, which actually happens in very rear cases and you cannot release a version long time.
If this continues months, normally I feel it like "to bang your head against the wall". When I reach this state, I exit Bagatur project for a while, awaiting for new inspiration and the muse. Always remember, it should be for fun! :-)

# Bagatur is powered by <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>

![YourKit Logo](https://www.yourkit.com/images/yklogo.png)

This nice tool is used to find out and fix performance, scalability and memory allocation isses.
In general YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling.

# Revisions history
Packed in one place, the release history is available <a href="https://github.com/bagaturchess/Bagatur/blob/master/Resources/doc/engine/txt/release_notes_BagaturEngine.txt">here</a>

# Credits
Fortunately, in this project I am not alone - without the ideas, support and help from many people and web sites, Bagatur would not be as it is now!
Credits and many thanks to:
  1. My wife and my family, because every now and then I have been stealing from our leisure time to work on this project.
  2. https://github.com/xu-shawn/Serendipity, thanks to Shawn for explaining to me how he has trained the NNUE network of Serendipity chess engine as well as for the reference Java code, which handles the network.
  3. Desislava Chocheva, for her hospitality and willingness to help. Without her support this video couldn't be a fact.
  4. Ivo Zhekov, for motivating me to start this project as well as for accepting the challenge with such a strong opponent in front of the camera.
  5. Simeon Stoichkov, for his general support, regarding chess topics in Bulgaria, as well as for the nice chess pieces and the chess clock, used in this video.
  6. Varna Sound, for their willingness to support us and to participate with their great RAP music.
  7. Iavor Stoimenov, for the endless discussions about Chess topics and Chess Engines.
  8. Ivo Simeonov, for all the ideas, support, discussions, tests and contributed source code (e.g. initial version of pawn structure evaluation, C porting, exe launcher).
  9. Graham Banks from Computer Chess Rating Lists (CCRL) website, https://ccrl.chessdom.com/ccrl/4040/ , https://www.computerchess.org.uk/ccrl/ , for all the chess engine tournaments he has been organizing and broadcasting over the Internet for many years as well.
  10. Anton Mihailov, Aloril and Kan from Top Chess Engine Championship (TCEC) website, https://tcec-chess.com/ , for their invitations for Bagatur and its participation in chess engines tournaments for many seasons. Special thanks to Alroil, who contributed a lot in the testing of Bagatur’s Symmetric MultiProcessing (SMP) version, which runs under CentOS on more than 100 CPU cores on the used hardware. Thanks a lot for the support in case of engine issues/bugs!
  11. Olivier Deville, for his great support during ChessWar XVII, http://www.open-aurec.com/chesswar/
  12. Zoran Sevarac, author of Neuroph and co-author of Deep Netts, for his great support with the experiments with Neural Networks and Machine Learning in Java.
  13. Roelof Berkepeis, for his testing, shared chess experience with me, and the great ideas described as issues on Bagatur’s page on github.com, https://github.com/bagaturchess/Bagatur/issues
  14. Sameer Sehrawala, for the latest logo and for his general support.
  15. Dusan Stamenkovic, http://www.chessmosaic.com/, for a few old Bagatur logos.
  16. Internet, Global Web, for connecting us.
  17. The Open Source Community!
  18. MTD(f), https://en.wikipedia.org/wiki/MTD(f) , the parallel search of Bagatur is based on this idea.
  19. winrun4j, http://winrun4j.sourceforge.net/ , for the windows executables.
  20. All UCI compatible GUIs and UCI protocol itself.
  21. REBEL, http://www.rebel13.nl/ , very helpful web page.
  22. Glaurung chess engine, nice ideas inside the evaluation function (e.g. king safety).
  23. Fruit, http://www.fruitchess.com/, legendary program, nice and simple design.
  24. ChuckooChess, https://github.com/sauce-code/cuckoo , one of the first Java chess engines.
  25. Chess22k, exciting java chess engine - strong and well written.
  26. The source code of the strongest open-source Chess Engine – Stockfish, https://stockfishchess.org/
  27. https://sourceforge.net/
  28. https://github.com/ 
  29. https://stackoverflow.com/
  30. ... and many others!

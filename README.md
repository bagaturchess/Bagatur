
# Overview

Bagatur chess engine is one of the strongest Java chess engines in the world.
It runs on both: desktop computer and Android:
 - For desktop computers, it runs as a program whith a console and supports commands of the <a href="http://wbec-ridderkerk.nl/html/UCIProtocol.html">UCI protocol</a>. It could be easily imported in Chess programs with user interfaces, like <a href="http://www.playwitharena.de/">Arena Chess GUI</a>.
 - For Android, the app is available on different app stores <a href="https://metatransapps.com/bagatur-chess-engine-with-gui-chess-ai/">Bagatur Chess Engine with GUI</a>. It has its own user interface. The source code of the Android version is also open source and could be found here: https://github.com/MetatransApps/Android_APK_ChessEngineBagatur

If you like the project, please give it a star.

# Downloads for desktop computer

The <a href="https://github.com/bagaturchess/Bagatur/releases">latest release</a> runs under all Operating Systems, which support Java platform.

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

 - SMP Threads (type spin default 4 min 1 max 256): this option is available only for the <a href="https://www.chessprogramming.org/SMP">SMP version</a> of Bagatur. The SMP (multicore) version can be started by Bagatur_64_2+_cores.exe and Bagatur_mcore.bat for Windows and with Bagatur_mcore.sh under Linux. It is tested with up to 64 CPU cores and threads. Theoretically it has no upper limit abd should work with 128 CPUs/threads, 256 CPUs/threads, ... but this is not proven yet.

All other options are available for both versions: single core and SMP.
 - Logging Policy (type combo default none var single file var multiple files var none): whether Bagatur will create log files on the file system with details of its actions.
 - OwnBook (type check default true): whether to use the own book included into the download, which is packed under ./data/w.ob and ./data/b.ob. These are games extracted from a few milions of PGN games played last 20 years by grandmasters and computer programs. They are filtered and the files contain a subset of most often played games. Unfortunatelly the name of the used opening is not supported at the moment but this features is defenitelly in our backlog and will be included in the Android version.
 - Ponder (type check default true): whether to also think when the opponent thinks.
 - MultiPV (type spin default 1 min 1 max 100): whether to show only the best line or to show the best 2-3-N lines.
 - SyzygyPath (type string default ./data/egtb): path to the syzygy tables. If you send 'uci' command to the engine, it will show the full path to the syzygy directory.
 - Openning Mode (type combo default most played first var most played first var random intermediate var random full): Valid only when OwnBook is set to true. The 'most played first' option playes the most often played move (statistically) for given position. 'random full' option playes random move from all available opening moves for this postion. And the 'random intermediate' option is something in the middle and plays random move selected only from the top 3 available moves for this position.

# Syzygy Endgame Tablebases

The download of Bagatur contains subset of syzygy tablebases placed under ./data/egtb/ directory. It contaiuns 22 of the most common endgames with up to 5 pieces. By default the option 'SyzygyPath' is set to this directory. You could change this UCI option if you have complete or bigger set of syzygy tablebases donwloaded on your computer.
 
# ELO Rating

The <a href="http://www.computerchess.org.uk/ccrl/4040/cgi/compare_engines.cgi?family=Bagatur">ELO rating</a> is an important metric for a chess engine.

# Machine Learning

There are some code examples of Artificial Intelligence / Machine Learning experiments with the <a href="http://neuroph.sourceforge.net/">Neuroph</a> 2.94 Java framework.
The starting point into the source code is located <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/LearningImpl/src/bagaturchess/deeplearning/run">here</a> and the documentation is <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/LearningImpl">here</a>.
Although the ELO strength of the experimential version is with 50 ELO less than the version with the manually tuned evaluation function's parameters, the results are successful meaning that the neural network's <a href="https://en.wikipedia.org/wiki/Backpropagation">backpropagation</a> algorithm works as expected for <a href="https://en.wikipedia.org/wiki/Multilayer_perceptron">multi layer perceptron</a> neural network. This makes the experiments a good showcase of <a href="http://neuroph.sourceforge.net/">Neuroph</a> Java framework.

# Bagatur is powered by <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>

![YourKit Logo](https://www.yourkit.com/images/yklogo.png)

This nice tool is used to find out and fix performance, scalability and memory allocation isses.
In general YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling.

# Sources

The <a href="https://github.com/bagaturchess/Bagatur-Chess-Engine-And-Tools/tree/master/Sources">source code</a> is writen entirely in Java programming language.

# Author

The author of Bagatur engine is <a href="https://www.linkedin.com/in/topchiyski/">Krasimir Topchiyski</a>.

# Links

- <a href="http://bagaturchess.github.io/Bagatur/Sources/">Sources</a>
- <a href="http://bagaturchess.github.io/Bagatur/Sources/LearningImpl/">Machine Learning / LearningImpl</a>
- <a href="http://bagaturchess.github.io/Bagatur/Sources/UCITracker/">Machine Learning / UCITracker</a>



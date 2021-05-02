

# Running the Program

The <a href="https://github.com/bagaturchess/Bagatur/edit/master/Downloads/Engine/">downloads</a> of the program runs under all Operating Systems, which support Java platform:
* **Android**, there is an Andorid game called <a href="https://play.google.com/store/apps/details?id=com.bagaturchess">Bagatur Chess Engine with GUI</a> with own user interface (GUI) and is using Bagatur internally as a chess engine
* **Windows**, there are *.exe files for direct run. Here are the steps necessary to run the engine:
  * Download an arbitrary UCI user interface. For example the most popular one is <a href="http://www.playwitharena.com/">Arena</a>.
  * Install the UCI user interface on your computer.
  * Ensure that the Java Runtime Environment (JRE) 1.6 or later is installed on your computer (it should be the case nowadays but if not, have in mind that JRE is required, only JDK doesn't work). It could be downloaded from <a href="https://java.com/">java.com</a>
  * Unpack this distribution somewhere (Arena has a sub-folder called 'engines', you can extract it there).
  * Open the UCI user interface and register the engine inside (You should become familiar with the installed UCI user interface anyway). You may use win32 or win64 version depending on your choice.
  * E2-E4 and enjoy :-)
* **Linux**, in the bin directory there are *.sh files for running


# Revision History
Version 2.2c (2 May 2021)
  * SMP version: Disable ContinuationHistory as it consumes too much memory per each search thread. When the memory is close to the upper limit, the performance of the Java program is slow.

Version 2.2b (30 April 2021)
  * No changes in the single core version
  * Fixes in nodes counting (NPS) in SMP version (reported by TCEC, Aloril)
  * Optimizations in SMP version (reported by TCEC, Aloril)

Version 2.2a (21 February 2021)
  * Optimizations of search and qsearch with small ELO increase (~15)
  * Bugfixing

Version 2.2 (29 May 2020)
  * Reduced LMR and LMP for moves with high history scores
  * SMP based on threads (not processes), it is now with 65+ ELO stronger than the single core version
  * Injected icon in the executable files and update of WinRun4J to version 0.4.5

Version 2.1a (21 May 2020)
  * Improvements in single core version with small ELO increase
  * Evaluation cache increased to 256M for better performance
  * Additional fixes in SMP version: parallel start and stop of the slave threads

Version 2.1 (1 May 2020)
  * No changes in the single core version
  * Fixed SMP version, which now works better than the single core version :-)
  * In SMP version: increased max threads count from 64 to 256

Version 2.0 (13 March 2019)
  * New evaluation function, based on Chess22k
  
Version 1.9b (7 March 2020)
  * Bugfix: parallel search now works on Linux, just use ./bin/Bagatur_mcore.sh
  
Version 1.9a (14 January 2020)
  * Optimization: don't perform NonPv search in the root position
  * Improvement: add piece-to history supplementary to from-to history heuristic

Version 1.9 (19 December 2019)
  * New search algorithm, based on the Chess22k
  * Search improvements: Use transposition table in quiescence search
  * Search improvements: Disable futility pruning in quiescence search (for positions in check)
  * Search improvements: New beta window generation in MTD(f) implementation 
  
Version 1.8a (20 September 2019)
  * Enable Syzygy tablebases for SMP version
  * Corrections in PV search and collection of the best line
  * Now LMR has new formula

Version 1.8 (30 July 2019)
  * New evaluation function, based on the Stockfish
  * Time control optimization for SMP version - don't use too much time if evaluation changes 

Version 1.7b (2 July 2019)
  * Bugfix: into the time control found during TCEC Season 16 testing by Aloril

Version 1.7a (18 April 2019)
  * Optimizations of move ordering: bad captures are ordered with higher value
  * Optimizations of move ordering: piece-square tables are not used in ordering

Version 1.7 (29 March 2019)
  * Support for Syzygy tablebases, based on the java porting done by ljgw https://github.com/ljgw/syzygy-bridge
  * New evaluation function

Version 1.6c (15 March 2019)
  * Parallel search: now improved with several bug fixes
  * Parallel search: UCI option implemented for Thread Memory (MB)
  * Late move reduction is now more aggressive for all nodes

Version 1.6b (21 February 2019)
  * Late move reduction is now decreased for PV nodes (+30 ELO)

Version 1.6a (11 February 2019)
  * Revert memory management experiments introduced with versions 1.5g and 1.6
  * More deeper internal iterative deepening search
  * Late move reduction is now adjusted with move's history scores
  
Version 1.6 (17 January 2019)
  * Separate transposition tables for search and qsearch for better memory management
  * Use both transposition tables when probing position
  * If presented use transposition table scores before qsearch
  
Version 1.5g (14 January 2019)
  * Memory management improvements - more memory for the transposition table
  * Use Late move reduction for remaining depth > 3

Version 1.5f (31 October 2018)
  * 15 ELO stronger (measured versus 1.5e)
  * Time management improvements
  * Singular move extension implemented
  * Optimizations of quiescence search - it search all capture moves
  
Version 1.5e (20 July 2017)
  * Endgame Tablebases support - switched OFF, because of engine crash errors (JNI and java OutOfMemory related)
  
Version 1.5d (15 July 2017)
  * Search optimizations: added move history pruning and move count pruning
  * Search optimizations: tuning of late move reduction
  * Search optimizations: tuning of null move
  * Search optimizations: switch OFF check extension
  
Version 1.5c (29 May 2017)
  * Search optimizations: improved moves history table (PieceTo implementation)
  
Version 1.5b (25 May 2017)
  * Search optimizations: less aggressive late move reduction
  * Search optimizations: switch ON check extension
  
Version 1.5a (11 May 2017)
  * Endgames without pawns - adding material imbalance to mock-up evaluation
  * Evaluation function - tuning of all features
  
Version 1.5 (3 May 2017)
  * 35+ ELO stronger (measured versus 1.4e)
  * Search optimizations: adjusted best move window
  * More tuning of Endgame's mate search - use endgame table bases after depth 13
  
Version 1.4e (15 April 2017)
  * Evaluation function tunning (versus Stockfish7)
  * Endgame improvements for mate search - use endgame table bases after depth 15

Version 1.4d (31 August 2016)
  * Memory optimizations: increased memory usage percent, hence more entries into the transposition table (now on 40/40 time controls search goes deeper)
  * Fix for endgames without pawns
  * Fix for mate distance prunning
  * Updated readme files
  
Version 1.4c (15 June 2016)
  * 25+ ELO stronger (measured versus 1.4b with 150+ games - 1m+1s and 5m+5s time controls)
  * Memory optimizations: no java objects creation during search, hence less java garbage collection and more engine performance
  * Renamed *.exe files
  
Version 1.4b (31 May 2016)
  * 28+ ELO stronger (measured versus 1.4a)
  * Search optimizations: adjustments of the depth reduction amount used in late move reduction and null move reduction
  * Default memory increased from 256MB to 1024MB. Could be set in the corresponding *.ini files to *exe files (or bat files if you don't use *.exe starters)
  * Fixes in UCI communication with Arena UI (e.g. start-stop issues, MultiPV under Pondering issues)
  
Version 1.4a (24 May 2016)
  * Bugfix: removed bug when in some endgames engine just stop thinking
  * New UCI option Openning Mode - 'random' and 'most played first'
  * Changed logo
  * Simplified distribution structure
  
Version 1.4 (11 May 2016)
  * 35+ ELO stronger!
  * Added Windows 64 support for Gaviota Endgame Tablebases
  * Search optimizations: New depth reduction function used in late move reduction, extensions adjustments, more reliable static pruning, separate transposition table per CPU (preparation for the new parallel search)
  * Evaluation function: support for double bishops, good and bad bishops
  * Draw probability adjustment for endgames with different colored bishops
  * Updated *.exe files to support Java 7 and Java 8
  * Default memory increased from 128MB to 256MB. Could be set in the corresponding *.ini files to *exe files (or bat files if you don't use *.exe starters)
  * Improved memory management
  * Fixed Pondering
  
Version 1.3a (07 December 2012)
  * Endgame Tablebases support - Gaviota EGTB (currently, for win 32 only). Find more details in readme.txt.
  * New UCI option 'Logging Policy' is introduced - 'single file', 'multiple files' and 'none'
  * SMP version: fixed Operation System dependent issue, which causes the engine to use only one thread in some conditions
  
Version 1.3 (05 July 2012)
  * SMP version is finally enabled and now works as expected. Your feedback is welcome, especially if you run it on more than 2 physical CPUs.
  * Fixed new bug which appears in rare cases and cause the engine to exit with error
  
Version 1.2g (5 June 2012)
  * 50 ELO stronger! Improved search algorithm (e.g. better tuned null move pruning)
  * Improved SMP version although there is still room for improvement
  
Version 1.2f (26 May 2012)
  * SMP version is now stronger. It is working with the latest 'single core' searcher algorithm as a basis
  * Fixed bug which appears in rare cases and cause the engine to stop working
  
Version 1.2e (12 May 2012)
  * 40 ELO stronger: optimized search - improved move ordering and search parameters
  
Version 1.2d (9 December 2011)
  * 30 ELO stronger: optimized search - use the data from unsuccessful null move search in order not to reduce the strongest move of opponent
  * bugfix: "loss on time" introduced by version 1.2c
  
Version 1.2c (4 December 2011)
  * 30 ELO stronger: optimized search by making null move heuristic a bit more aggressive
  
Version 1.2b (3 November 2011)
  * bugfix: read and use the option set for threads count of SMP version
  
Version 1.2a (30 October 2011)
  * 50 ELO stronger than version 1.2. Achieved by tuning of search algorithm.
  * Min threads count of SMP version are set to 2. Reported by Olivier Deville during OpenWar 9th Edition test games
  * Default process priority is set to 'normal'
  * Technical: Refactoring of configuration API & Impl code
  
Version 1.2 (10 September 2011)
  * MultiPV search mode implemented! (a.k.a K-Best Moves search)
  * Improved time-control of 'sudden death' playing mode (reported by Lars Hallerstrom during ChessWar XVII)
  * Reduced adaptive extensions in non-pv nodes for better and faster search. Now in most cases the engine succeed to make one additional iteration for the same time.
  * Evaluation: Increased King Safety weight with 10%
  
Version 1.1.3 (27 August 2011)
  * Fixed "loss on time" issue during time-per-move mode. Reported by Olivier Deville during ChessWar XVII test games
  * Fixed "loss on time" issue in 40th move with tournament time controls like 40/X. Again catched and reported by Olivier Deville
  * Fixed issue in 64 bits EXE - again memory issue which appears in some rare cases 
  * Updated readme files (e.g. credits section)
  * UCI: Usage of Transposition table entries' scores in PV nodes is implemented as UCI option (default value is false)
  * Technical: Trust window of MTD search is now adaptive and vary between 0 and 32 centipawn depending on the stability of the best move (bigger stability when one and the same best move appears in more search iterations)
  * Technical: Transposition table's entries (their scores) in PV nodes are used only if the length of the best line is enough (with the expected depth)
  * Technical: Big source code refactoring in regards to the changes of the configuration concept
  
Version 1.1.2 (23 August 2011)
  * Hot-fix of memory issue. Engine hangs and throws OutOfMemory error after last memory tunings in version 1.1.1. Now it should work fine.
  * Decreased trust window of MTD Search from 8 to 0. Now each iteration needs a bit more researches but on the other hand more transposition table's entries has 'exact' type. 
  * Enable the usage of transposition table's entries (their scores) also in PV nodes
  * UCI: Fixed issue in 'setoption' UCI command. Sometimes Arena sends not only name-value pair but also additional information.
  * Changed own opening book. The new one is based on Hitman 5.2.
  
Version 1.1.1 (17 August 2011)
  * Added EXE files for 32 and 64 bits Windows platforms. Now the usage is easy and the engine could be used in different UIs (then Arena) like Fritz and Polyglot.
  * Default memory decreased from 256MB to 128MB. Could be set in the corresponding *.ini files to *exe files (or bat files if you don't use *.exe starters)
  * More efficient cache usage - Transposition Table 40% of the free memory, Eval Cache - 40% of the free memory, Pawns Eval Cache - 20 % of the free memory.
  * Directory structure of the distribution (zip file) is changed again.
  * Fixed 'slow search' problem. It appears sometimes with given combination of hardware/windows/java and is related with the limited speed of I/O operations with the file system and process streams.
  * UCI: uci options for evaluation function are implemented. Now the weights could be set from GUI. The following evaluation's components are included: King Safety, Mobility, Safe Mobility, Cental Space, Piece-Square, Pawns Structure, Passed Pawns.
  * UCI: Send search information more often to UCI GUI (not only with the best line / principal variation)
  * UCI: Go search with count of 'nodes' implemented
  * UCI: Move immediately if mate is found in 3 sequential search depths
  
Version 1.1.0 (2 August 2011)
  * Pondering mode implemented! Mate distance pruning disabled during Pondering.
  * Added to-do list in the distribution. Feel free to contribute. :-)
  * Bagatur's LOGO changed. Many thanks to Dusan Stamenkovic, http://www.chessmosaic.com/
  * Improved time management: work well in tournament mode (with given moves to the next control a.k.a. 'movestogo' property of 'go' UCI command)
  * Fixed bug reported during ChessWar XVII: Use 'long algebraic notation' instead of SAN in order to be UCI compatible
  * Fixed bug reported during ChessWar XVII: Parallel search is now optional. Two separate *.bat files are created for running Bagatur on either signle-core or multi-core.
  * Disable usage of opening book and single reply optimization during analyze mode
  * Improved read-me file: Fixed typos. Added 'Clarifications' section. Added 'Credits' section. 
  * Changed directory structure of the distribution. Added 'bin' and 'dat' folders.
  
Version 1.0.1 (22 July 2011)
  * Fixed bug reported during ChessWar XVII: "Loss on time" in games with fixed time (without time's increment per move)
  * Reduced debug information in the engine's log file
  
Version 1.0.0 (27 Feb 2011)
  * Initial code base
  



For the latest and greatest version of this readme file you can visit the SVN repository and check the Ants sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


As a chess player,
you want to play chess against a computer program (chess engine) which is able to be integrated inside UCI compliant user interfaces (like Arena).
This distribution is exactly what you want.

Here are the steps necessary to run the engine:
1. Download an arbitrary UCI user interface. For example the most popular one is Arena - http://www.playwitharena.com/
2. Install the UCI user interface on your computer
3. Install Java platform (JDK or JRE) 1.6 or later on your computer
4. Unpack this distribution somewhere (Arena has a sub-folder called 'engines', you can extract it there)
6. Open the UCI user interface and register the engine inside (You should become familiar with the installed UCI user interface anyway). You may use the single-core or mutli-core version depending on your choice.
7. E2-E4 and enjoy :-)


Have a nice usage ... and feel free to contribute http://sourceforge.net/projects/bagaturchess/develop


Hints:
  1. Bagatur needs at least 64M of memory to run.
     By default it runs with 128M of memory.
     To change it you can edit the corresponding *.ini or *.bat file.
     
  2. Endgame Tablebases support is based on Gaviota EGTB (Nalimov and Scorpio are not supported) and currently works on win 32 only.
     In order to enable it:
     a) Download this archive: http://sourceforge.net/projects/egtb-in-java/files/latest/download
  	 b) Extract the archive in the same directory where Bagatur engine is extracted 
	 c) Set UCI option GaviotaTbPath, to point to the directory where the Gaviota EGTB files are
	 d) Set UCI option GaviotaTbCache, the size of native EGTB cache in megabytes
	 e) Use one of the win 32 executables - Bagatur_32_1core.exe or Bagatur_32_mcore.exe (alternatively you could use ./bat/*.bat files, called with 32 bits java.exe)
	 
  3. SMP version needs at least 128M of memory to run.
     By default it runs with 256M of memory.
     Have in mind that in short time controls (e.g. all moves or 40 moves in 1 minute), the SMP version performs worse than single core version.
     Here are some important remarks, you sould keep in mind:
  	 a) Bagatur's option 'threads count' is equal to the physical CPUs'/Cores' count (correctness check: your total CPU load during the search should to be close to 100%)
  	 b) (on Windows only) your CPUs are not 'parked': http://pcmichiana.com/disable-cpu-core-parking-for-multi-core-processors-faster-7-episode-20/
  	 !!! On Laptop !!! for optimal performance make sure that: Laptop is connected to power supply (by either cable or docking station) and power plan is set to 'Maximum Performance'
   
  4. You may tune the Evaluation Function.
     In general, the UCI user interfaces has a functionality to change the options supported by the Engine.
     Even more, you may save the different configurations as different engines and to fight them with each other in order to prove that your tuning makes it better.
     All this UCI options are implemented: King Safety, Mobility, Safe Mobility, Cental Space, Piece-Square, Pawns Structure, Passed Pawns.  
     For example, in Arena you can open the UCI options panel in that way: 1) Engines/Manage 2) select engine 3) click on the UCI tab 4) press 'CTRL-1' and/or 'CTRL-2' 
     
  5. If for some reason the EXE files do not work as expected, then there are 2 options:
     A) Edit the corresponding INI file. Find the option 'vm.location', remove semicolons (';' symbol) from the beginning of the line, and set the property to point out the wanted jvm.dll.
        jvm.dll could be found in JRE's 'bin/client' directory. For example, something like 'C:\jdk1.6.0_07\jre\bin\client\jvm.dll'
     B) Use the BAT files inside the 'bat' sub-directory: BagaturEngine_singlecore.bat and BagaturEngine_multicore.bat
        They can be edited so that the full path to java.exe is valid (it is also enough to add the java.exe to the system path variable)


Credits:
  I am working on this program for 10 years in my spare time as a hobby.
  In the beginning there were not a lot in the Internet, although, most of the time I purposely avoided heaving look at the
  other open source softwares and articles regarding the search and game theory.
  Because of that, in many aspects I have re-invented the wheel (for good or bad, it is obvious, if you have a look at the code).
  Anyway, without the ideas, support and help from the following people and web sites, Bagatur would not be as it is now:
  
  1. My Wife, for supporting me in this dissociable and non-profitable hobby.
  2. Ivo Simeonov, for all the ideas, support, discussions, tests and contributed source code (initial version of pawn structure evaluation, C++ portings, exe luncher, etc.).
  3. Iavor Stoimenov, for the support and helpful discussions about the chess stuffs.
  4. Olivier Deville, for his great support during ChessWar XVII, http://www.open-aurec.com/chesswar/
  5. Dusan Stamenkovic, http://www.chessmosaic.com/, for the new Bagatur's Logo.
  6. All UCI compatible GUIs and UCI protocol itself.
  7. REBEL, http://www.top-5000.nl/authors/rebel/chess840.htm, very helpful web page. Unfortunately it appeared after i realized most of the things in the hard way.
  8. MTD(f), http://plaat.nl/mtdf.html, the parallel search of Bagatur is based on this idea.
  9. Glaurung, http://www.glaurungchess.com/, nice ideas inside the evaluation function (e.g. king safety)
  10. Fruit, http://www.fruitchess.com/, legendary program, nice and simple design.
  11. winrun4j, http://winrun4j.sourceforge.net/, for the windows executables
  12. CuckooChess, http://chessprogramming.wikispaces.com/CuckooChess, another java chess program - strong and well written


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
 


/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */


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
  

As a chess player,
you want to play chess against a computer program (chess engine) which is able to be integrated inside UCI compliant user interfaces (like Arena).
This distribution is exactly what you want.

Here are the steps necessary to run the engine:
1. Download an arbitrary UCI user interface. For example the most popular one is Arena - http://www.playwitharena.com/
2. Install the UCI user interface on your computer.
3. Ensure that the Java Runtime Environment (JRE) 1.6 or later is installed on your computer (it should be the case nowadays but if not, have in mind that JRE is required, only JDK doesn't work). It could be downloaded from java.com.
4. Unpack this distribution somewhere (Arena has a sub-folder called 'engines', you can extract it there).
6. Open the UCI user interface and register the engine inside (You should become familiar with the installed UCI user interface anyway). You may use win32 or win64 version depending on your choice.
7. E2-E4 and enjoy :-)

Hints:
  1. Bagatur needs at least 64M of memory to run.
     By default it runs with 1024M of memory, but uses only 70% of it (30% are for transposition table and the rest of 40% is used for other caches).
     Because of java programming language specifics, changes in memory could lead to bad performance, anyway if necessary it could be changed by editing the corresponding *.ini or *.bat file.
     
  2. For now, Syzygy Endgame Tablebases are supported.
	 
  3. On Laptop - for optimal performance make sure that: Laptop is connected to power supply (by either cable or docking station) and power plan is set to 'Maximum Performance'
   
  4. If for some reason the EXE files do not work as expected, then there are 2 options:
     A) Edit the corresponding INI file. Find the option 'vm.location', remove semicolons (';' symbol) from the beginning of the line, and set the property to point out the wanted jvm.dll.
        jvm.dll could be found in JRE's 'bin/client' or 'bin/server' directory. For example, something like 'C:\jdk1.6.0_07\jre\bin\client\jvm.dll'
     B) (Not recommended) Use the BAT files inside the 'bin' sub-directory: Bagatur_1core.bat and Bagatur_mcore.bat
        They can be edited so that the full path to java.exe is valid (it is also enough to add the java.exe to the system path variable)


Credits:
  Without the ideas, support and help from the following people and web sites, Bagatur would not be as it is now:
  
  1. My Wife, for supporting me in this hobby.
  2. Iavor Stoimenov, for the support and helpful discussions about the chess stuffs.
  3. Ivo Simeonov, for all the ideas, support, discussions, tests and contributed source code (initial version of pawn structure evaluation, C++ portings, exe luncher, etc.).
  4. Graham Banks, for all the CCLR tournaments he organize and broadcast in Internet, as well as for his great support in case of engine issues
  5. Olivier Deville, for his great support during ChessWar XVII, http://www.open-aurec.com/chesswar/
  6. Dusan Stamenkovic, http://www.chessmosaic.com/, for the new Bagatur's Logo.
  7. All UCI compatible GUIs and UCI protocol itself.
  8. REBEL, http://www.rebel13.nl/, very helpful web page.
  9. MTD(f), http://plaat.nl/mtdf.html, the parallel search of Bagatur is based on this idea.
  10. Glaurung chess engine, nice ideas inside the evaluation function (e.g. king safety)
  11. Fruit, http://www.fruitchess.com/, legendary program, nice and simple design.
  12. winrun4j, http://winrun4j.sourceforge.net/, for the windows executables
  13. Chess22k, another java chess engine - strong and well written
  
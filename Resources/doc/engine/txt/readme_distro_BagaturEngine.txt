
Short Description:
As a chess player, I want to play chess against a computer program (chess engine) using UCI compatible graphical user interfaces (GUI), like Arena or CuteChess, on my PC/Laptop.

How-to:
1. Download an arbitrary UCI user interface. For example the most popular one is Arena - http://www.playwitharena.com/, https://cutechess.com/
2. Install the UCI user interface on your computer.
3. Ensure that the Java Runtime Environment (JRE) 1.8 or later is installed on your computer (it should be the case nowadays but if not, have in mind that JRE is required, only JDK doesn't work). It could be downloaded from java.com.
4. Download and unpack this Bagatur's distribution somewhere (Arena has a sub-folder called 'engines', you can extract it there).
6. Open the UCI user interface and register the engine inside (You should become familiar with the installed UCI user interface anyway). There is still win32 version, but migrating to and using win64 is recommended.
7. Enjoy the match(s) :-)

Hints:
  1. Bagatur needs at least 128M of memory to run.
     By default it runs with 1024M of memory, but uses only 70% of it (30% are for transposition table and the rest of 40% is used for other caches).
     Because of java programming language specifics, changes in memory could lead to bad performance, anyway if necessary it could be changed by editing the corresponding *.ini or *.bat file.
  
  2. For now, Syzygy Endgame Tablebases are supported.
	 
  3. On Laptop - for optimal performance make sure that: Laptop is connected to power supply (by either cable or docking station) and power plan is set to 'Maximum Performance'
  
  4.1 Under Linux, use Bagatur_1core.sh or Bagatur_mcore.sh placed in a 'bin' sub-directory.
  	  You must extract the distribution in a directory which doesn't contains white spaces. Otherwise the parallel version will not work.
  
  4.2 Under Windows, if for some reason the EXE files do not work as expected, then there are 2 options:
     A) Edit the corresponding INI file. Find the option 'vm.location', remove semicolons (';' symbol) from the beginning of the line, and set the property to point out the wanted jvm.dll.
        jvm.dll could be found in JRE's 'bin/client' or 'bin/server' directory. For example, the path on your disk should be similar to 'C:\jdk1.6.0_07\jre\bin\client\jvm.dll'
     B) (Not recommended) Use the BAT files inside the 'bin' sub-directory: Bagatur_1core.bat and Bagatur_mcore.bat
        They can be edited so that the full path to java.exe is valid (it is also enough to add the java.exe to the system path variable)

If you are interested in the story "How was the idea of Bagatur Chess Engine application born?", than this YouTube video will be most probably interesting for you: https://youtu.be/_rNBxbUAbS0

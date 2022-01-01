
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
  
  4.1 Under Linux, use Bagatur_1core.sh or Bagatur_mcore.sh placed in a 'bin' sub-directory.
  	  You must extract the distribution in a directory which doesn't contains white spaces. Otherwise the parallel version will not work.
  
  4.2 Under Windows, if for some reason the EXE files do not work as expected, then there are 2 options:
     A) Edit the corresponding INI file. Find the option 'vm.location', remove semicolons (';' symbol) from the beginning of the line, and set the property to point out the wanted jvm.dll.
        jvm.dll could be found in JRE's 'bin/client' or 'bin/server' directory. For example, something like 'C:\jdk1.6.0_07\jre\bin\client\jvm.dll'
     B) (Not recommended) Use the BAT files inside the 'bin' sub-directory: Bagatur_1core.bat and Bagatur_mcore.bat
        They can be edited so that the full path to java.exe is valid (it is also enough to add the java.exe to the system path variable)

Fortunately, in this project I am not alone - without the ideas, support and help from many people and web sites, Bagatur would not be as it is now!

If you are interested in the story "How was the idea of Bagatur Chess Engine application born?", than this YouTube video will be most probably interesting for you: https://youtu.be/_rNBxbUAbS0

Credits and many thanks to:
  1. My wife and my family, because every now and then I have been stealing from our leisure time to work on this project.
  2. My Employer, SAP, for the great colleagues, for the company values and the culture we have, for the opportunities to have a global impact and to get better in different topics, out of the assignments, roles and tasks I work on each day, and last but not least, for the flex work model we use.
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
  


Short Description:
As a chess programmer, I want to have the Bagatur's sources in a distribution so that one could easily compile and run the code, inside the Java IDE (e.g. Eclipse development environment).
So this is archived eclipse workspace with source code. There is also main method, inside the EnginesRunner sub-project, which could be used as an entry point to start the Java program in the IDE.

How to run the engine:
1. extract the archive in a new directory
2. import the existing projects in the Eclipse workspace
3. run the main class bagaturchess.engines.run.MTDSchedulerMain (it is inside the EnginesRunner sub-project)

How to build a distribution from the sources:
1. extract the archive in a new directory (<workspace>)
2. download http://sourceforge.net/projects/egtb-in-java/files/latest/download
3. get ./egtbprobe.dll from the archive and copy it to "<workspace>\EGTB" directory
3. get ./egtbprobe.jar from the archive and copy it to "<workspace>\EGTB\res" directory
4. copy w.ob and b.ob files (they are packed in the Bagatur engine distribution, inside 'data' sub directory) to the 'Resources\bin\engine\ob' directory (it is inside your workspace, if it isn't presented than create it)
5. run the ant script Ants/engine/build_BagaturEngine_distro.xml from Ants/ directory either from eclipse or command line
6. the distribution archive file will be generated in the WorkDir directory

Fortunately, in this project I am not alone - without the ideas, support and help from many people and web sites, Bagatur would not be as it is now!
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
  
  
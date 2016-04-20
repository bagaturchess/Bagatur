/*
 *  Java API of Gaviota Tablebases Probing Code
 *  Copyright (c) 2012 Krasimir I. Topchiyski
 *
 *  This Software is distributed with MIT License but
 *  is consisting of other parts distributed under their own (and possibly different) licenses.
 *  See LICENSE in LICENSE.txt for more details
 *  
 *  Open Source project location: http://sourceforge.net/projects/egtb-in-java/
 *  SVN repository https://svn.code.sf.net/p/egtb-in-java/code-0
 */


For the latest and greatest version of this readme file you can visit the
SVN repository https://svn.code.sf.net/p/egtb-in-java/code-0


As a java programmer,
you want to have a java API able to work with Gaviota Tablebases.
This software is designed to help you in that direction.

In order to use it:
1. Extract the jar file (egtbprobe.jar) from the distribution (zip archive) and add it to the classpath of your program.
2. Extract the dynamic library (e.g. egtbprobe_64.dll) file from the distribution and add it to the java library path, so the JVM is able to load it.
3. Obtain an instance of bagaturchess.egtb.gaviota.EGTBProbing class by calling its static method getSingleton().
4. Call 'init' method of the instance to specify the location of Gaviota Tablebases on the file system.
4. Use the instance in your program by calling 'probeHard' method.

As a reference application, you could use Bagatur chess engine and tools: http://sourceforge.net/projects/bagaturchess/files/
Check inside their distribution file(s), there is a sub-project/jar-file called BagaturEGTB (it has readme.txt), which extends the API with: 
 * easy to use representation of the chess board (and examples)
 * java cache based on the LRU discipline for better efficiency
 * asynchronous usage of the java cache for non-blocking operations and better performance

Current version supports win32 and win64.
Your contribution in the creation of dynamic library for linux is very welcome! 

Have a nice usage ... and feel free to contribute http://sourceforge.net/projects/egtb-in-java/


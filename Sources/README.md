

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


As a chess programmer,
you want to have the sources of Bagatur chess engine as a part of the distribution so that you can easily compile and run them inside the Eclipse development environment.
This source distribution is exactly what you want. It is archived eclipse workspace with simple main method inside the EnginesRunner sub-project.


How to run the engine:
1. extract the archive in a new directory
2. import the existing projects in the Eclipse workspace
3. run the main class bagaturchess.engines.run.MTDSchedulerMain (it is inside the EnginesRunner sub-project)


How to build a distribution from the sources:
1. extract the archive in a new directory (<workspace>)
2. download http://sourceforge.net/projects/egtb-in-java/files/latest/download
3. get ./egtbprobe.dll from the archive and copy it to "<workspace>\EGTB" directory
3. get ./bin/egtbprobe.jar from the archive and copy it to "<workspace>\EGTB\res" directory
4. copy w.ob and b.ob files (they are packed in the Bagatur engine distribution, inside 'dat' sub directory) to the 'Resources\bin\engine\ob' directory (it is inside your workspace, if it isn't presented than create it)
5. run the ant script Ants/engine/build_BagaturEngine_distro.xml from Ants/ directory either from eclipse or command line
6. the distribution archive file will be generated in the WorkDir directory


Have a nice usage ... and feel free to contribute http://sourceforge.net/projects/bagaturchess/develop


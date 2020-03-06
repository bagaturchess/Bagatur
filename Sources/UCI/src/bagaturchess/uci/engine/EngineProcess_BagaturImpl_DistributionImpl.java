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
package bagaturchess.uci.engine;


public class EngineProcess_BagaturImpl_DistributionImpl extends EngineProcess_BagaturImpl {
	
	
	public EngineProcess_BagaturImpl_DistributionImpl(String _engineName, String programArgs) {
		this(_engineName, "./", programArgs, 1024);
	}
	
	
	public EngineProcess_BagaturImpl_DistributionImpl(String _engineName, String workdir, String programArgs, int memoryInMB) {
		
		super(_engineName, getJavaPath()
							+ " " + JAVA_OPTIONS + " -Djava.library.path=." + java.io.File.separatorChar + "bin" + java.io.File.separatorChar
							+ " -Xmx" + memoryInMB + "M"
							+ " -cp \"" + getClassPath(workdir) + "\" "
							+ MAIN_CLASS + " "
							+ getMainClassArgs("bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD")
							+ programArgs,
							
							workdir);
	}
	
	
	private static String getClassPath(String workspace) {
		String JAVA_CP = "." + java.io.File.pathSeparator;//Workaround for linux: the first classpath entry is skipped for some reason.
		JAVA_CP += workspace + "bin/BagaturOpening.jar" + java.io.File.pathSeparator;
		JAVA_CP += workspace + "bin/BagaturBoard.jar" + java.io.File.pathSeparator;
		JAVA_CP += workspace + "bin/BagaturSearch.jar" + java.io.File.pathSeparator;
		JAVA_CP += workspace + "bin/BagaturUCI.jar" + java.io.File.pathSeparator;
		JAVA_CP += workspace + "bin/BagaturLearningAPI.jar" + java.io.File.pathSeparator;
		JAVA_CP += workspace + "bin/BagaturEngines.jar" + java.io.File.pathSeparator;
		JAVA_CP += workspace + "bin/BagaturEGTB.jar" + java.io.File.pathSeparator;
		JAVA_CP += workspace + "bin/BagaturLearningImpl.jar" + java.io.File.pathSeparator;
		return JAVA_CP;
	}
}

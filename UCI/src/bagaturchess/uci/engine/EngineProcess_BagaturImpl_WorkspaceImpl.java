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


public class EngineProcess_BagaturImpl_WorkspaceImpl extends EngineProcess_BagaturImpl {
	
	
	public EngineProcess_BagaturImpl_WorkspaceImpl(String _engineName, String programArgs) {
		this(_engineName, "./../", programArgs, 1024);	
	}
	
	
	public EngineProcess_BagaturImpl_WorkspaceImpl(String _engineName, String workspace, String programArgs, int memoryInMB) {
		
		super(_engineName, getJavaPath()
							+ " " + JAVA_OPTIONS + "-Xmx" + memoryInMB + "M"
							+ " -cp \"" + getClassPath(workspace) + "\" "
							+ MAIN_CLASS + " "
							+ getMainClassArgs("bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD")
							+ programArgs,
							
							workspace + "WorkDir");
	}
	
	
	private static String getClassPath(String workspace) {
		String JAVA_CP = "";
		JAVA_CP += workspace + "Bitboard/bin;";
		JAVA_CP += workspace + "Opening/bin;";
		JAVA_CP += workspace + "Search/bin;";
		JAVA_CP += workspace + "UCI/bin;";
		JAVA_CP += workspace + "LearningAPI/bin;";
		JAVA_CP += workspace + "Engines/bin;";
		JAVA_CP += workspace + "EGTB/bin;";
		JAVA_CP += workspace + "LearningImpl/bin;";
		return JAVA_CP;
	}
}

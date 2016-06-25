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


import bagaturchess.uci.engine.EngineProcess;


public class EngineProcess_BagaturImpl extends EngineProcess {
	
	
	private static String JAVA_EXE = "\"C:/Program Files/Java/jdk1.8.0_45/bin/java.exe\"";
	private static String JAVA_OPTIONS = "-Xmx128M";
	private static String JAVA_MAIN = "bagaturchess.uci.run.Boot";
	
	private static String JAVA_WORK = "C:/DATA/OWN/chess/GIT_REPO/Bagatur-Chess-Engine-And-Tools/Sources/UCI";
	
	private static String WORKSPACE = "C:/DATA/OWN/chess/GIT_REPO/Bagatur-Chess-Engine-And-Tools/Sources/";
	private static String JAVA_CP = "";
	static {
		JAVA_CP += WORKSPACE + "Bitboard/bin;";
		JAVA_CP += WORKSPACE + "Opening/bin;";
		JAVA_CP += WORKSPACE + "Search/bin;";
		JAVA_CP += WORKSPACE + "UCI/bin;";
		JAVA_CP += WORKSPACE + "LearningAPI/bin;";
		JAVA_CP += WORKSPACE + "Engines/bin;";
		JAVA_CP += WORKSPACE + "EGTB/bin;";
	}
	
	private static String ARGS = "";
	static {
		ARGS += "bagaturchess.engines.base.cfg.UCIConfig_BaseImpl_DEBUG "; 
		ARGS += "bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove ";
		ARGS += "bagaturchess.engines.base.cfg.UCISearchAdaptorConfig_BaseImpl ";
		ARGS += "bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch ";
		ARGS += "bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl_1Core ";
		ARGS += "bagaturchess.search.impl.alg.impl0.SearchMTD0 ";
		ARGS += "bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD_SMP ";
		ARGS += "bagaturchess.engines.bagatur.cfg.board.BoardConfigImpl ";
		ARGS += "bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2 ";
	}
	
	
	private String engineName;
	
	
	public EngineProcess_BagaturImpl(String _engineName, String programArgs) {
		super(JAVA_EXE + " " + JAVA_OPTIONS +
				" -cp " + JAVA_CP + " " + JAVA_MAIN + " " + ARGS + programArgs, null, JAVA_WORK);
		engineName = _engineName;
	}
	
	@Override
	public String getName() {
		return engineName;
	}
}

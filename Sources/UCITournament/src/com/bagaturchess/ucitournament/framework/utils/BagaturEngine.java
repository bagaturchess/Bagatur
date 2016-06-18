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
package com.bagaturchess.ucitournament.framework.utils;

import bagaturchess.ucitracker.impl.Engine;


public class BagaturEngine extends Engine {
	
	
	private static String JAVA_EXE = "C:/jdk1.6.0_07/bin/java.exe";
	private static String JAVA_OPTIONS = "-Xmx128M";
	private static String JAVA_WORK = "C:/data/own/chess/WS.Chess.SF1/UCITournament/";
	private static String JAVA_MAIN = "bagaturchess.uci.run.Boot";
	
	private static String WORKSPACE = "C:/data/own/chess/WS.Chess.SF1/";
	private static String JAVA_CP = "";
	static {
		JAVA_CP += WORKSPACE + "Bitboard/bin;";
		JAVA_CP += WORKSPACE + "Opening/bin;";
		JAVA_CP += WORKSPACE + "Search/bin;";
		JAVA_CP += WORKSPACE + "UCI/bin;";
		JAVA_CP += WORKSPACE + "LearningAPI/bin;";
		JAVA_CP += WORKSPACE + "Engines/bin;";
	}
	
	private static String ARGS = "";
	static {
		ARGS += "bagaturchess.engines.base.cfg.UCIConfig_BaseImpl "; 
		ARGS += "bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove ";
		ARGS += "bagaturchess.engines.base.cfg.UCISearchAdaptorConfig_BaseImpl ";
		ARGS += "bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch ";
		ARGS += "bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl ";
		//ARGS += "bagaturchess.search.impl.alg.SearchMTD ";
		//ARGS += "bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD ";
		//ARGS += "bagaturchess.learning.impl.eval.cfg.WeightsBoardConfigImpl ";
		//ARGS += "bagaturchess.engines.learning.cfg.weights.EvaluationConfg ";
	}
	
	
	private String engineName;
	
	
	public BagaturEngine(String _engineName, String programArgs) {
		super(JAVA_EXE + " " + JAVA_OPTIONS +
				" -cp " + JAVA_CP + " " + JAVA_MAIN + " " + ARGS + programArgs, null, JAVA_WORK);
		engineName = _engineName;
	}
	
	@Override
	public String getName() {
		return engineName;
	}
}

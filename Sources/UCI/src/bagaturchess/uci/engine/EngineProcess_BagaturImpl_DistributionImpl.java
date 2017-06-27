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

import java.io.File;


public class EngineProcess_BagaturImpl_DistributionImpl extends EngineProcess_BagaturImpl {
	
	
	private static String ARGS = "";
	static {
		ARGS += "bagaturchess.engines.base.cfg.UCIConfig_BaseImpl_DEBUG "; 
		ARGS += "bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove ";
		ARGS += "bagaturchess.engines.base.cfg.UCISearchAdaptorConfig_BaseImpl ";
		ARGS += "bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD ";
		ARGS += "bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl_1Core ";
		ARGS += "bagaturchess.search.impl.alg.impl0.Search_PVS_NWS ";
		ARGS += "bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD_SMP ";
		ARGS += "bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler.Bagatur_ALL_BoardConfigImpl ";
		ARGS += "bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval.EvaluationConfig ";
	}
	
	
	public EngineProcess_BagaturImpl_DistributionImpl(String _engineName, String programArgs) {
		this(_engineName, "./", programArgs, 128);
	}
	
	
	public EngineProcess_BagaturImpl_DistributionImpl(String _engineName, String workdir, String programArgs, int memoryInMB) {
		
		super(_engineName, "\"" + getJavaPath_javawexe() + "\""
							+ " " + JAVA_OPTIONS + " -Djava.library.path=." + File.separator + "bin" + File.pathSeparator
							+ " -Xmx" + memoryInMB + "M"
							+ " -cp " + getClassPath(workdir) + " "
							+ MAIN_CLASS + " "
							+ ARGS
							+ programArgs,
							
							workdir);
	}
	
	
	private static String getClassPath(String workspace) {
		String JAVA_CP = "";
		JAVA_CP += workspace + "bin/BagaturBoard.jar;";
		JAVA_CP += workspace + "bin/BagaturOpening.jar;";
		JAVA_CP += workspace + "bin/BagaturSearch.jar;";
		JAVA_CP += workspace + "bin/BagaturUCI.jar;";
		JAVA_CP += workspace + "bin/BagaturLearningAPI.jar;";
		JAVA_CP += workspace + "bin/BagaturEngines.jar;";
		JAVA_CP += workspace + "bin/BagaturEGTB.jar;";
		JAVA_CP += workspace + "bin/egtbprobe.jar;";
		JAVA_CP += workspace + "bin/BagaturLearningImpl.jar;";
		//JAVA_CP += workspace + "bin/;";//TODO: this doesn't work/load the DLLs, they have to be added in java.library.path
		return JAVA_CP;
	}
}

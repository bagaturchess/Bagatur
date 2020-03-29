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
import java.util.Locale;

import bagaturchess.uci.engine.EngineProcess;


public class EngineProcess_BagaturImpl extends EngineProcess {
	
	
	protected static String MAIN_CLASS = "bagaturchess.uci.run.Boot";
	protected static String JAVA_OPTIONS = "";
	
	private String engineName;
	
	
	public EngineProcess_BagaturImpl(String _engineName, String commandline, String workdir) {
		
		super(commandline, null, workdir);
		
		engineName = _engineName;
		

	}
	

	@Override
	public String getName() {
		return engineName;
	}
	
	
	protected static String getMainClassArgs(String rootSearchClassName) {
		String ARGS = "bagaturchess.engines.cfg.base.UCIConfig_BaseImpl";
		ARGS += " ";
		ARGS += "bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove";
		ARGS += " ";
		ARGS += "bagaturchess.engines.cfg.base.UCISearchAdaptorConfig_BaseImpl";
		ARGS += " ";
		ARGS += rootSearchClassName;
		ARGS += " ";
		ARGS += "bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_1Core";
		ARGS += " ";
		ARGS += "bagaturchess.search.impl.alg.impl1.Search_PVS_NWS";
		ARGS += " ";
		ARGS += "bagaturchess.engines.cfg.base.SearchConfigImpl_AB";
		ARGS += " ";
		ARGS += "bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20";
		ARGS += " ";
		ARGS += "bagaturchess.learning.goldmiddle.impl4.cfg.EvaluationConfig_V20";
		ARGS += " ";
		return ARGS;
	}
	
	
	protected static String getJavaPath() {
		
		String javaHome = System.getProperty("java.home");
	    File f = new File(javaHome);
	    f = new File(f, "bin");
	    
	    String pathToJava;
	    String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
	    if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
	    	f = new File(f, "java");
	    	pathToJava = f.getAbsolutePath();
	    } else {
	    	f = new File(f, "javaw.exe");
	    	pathToJava = "\"" + f.getAbsolutePath() + "\"";
	    }
	    
	    return pathToJava;
	}
}

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
	
	
	protected static String getJavaPath_javawexe() {
		String javaHome = System.getProperty("java.home");
	    File f = new File(javaHome);
	    f = new File(f, "bin");
	    f = new File(f, "javaw.exe");
	    return f.getAbsolutePath();
	}
}

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
package bagaturchess.ucitracker.impl;


import java.util.List;



public class UCIEnginesRunnerTest {
	
	
	public static void main(String[] args) {
		
		UCIEnginesRunner runner = new UCIEnginesRunner();
		
		try {
			
			Engine engine = new Engine("C:\\own\\chess\\ENGINES\\Houdini_15a\\Houdini_15a_w32.exe",
					new String [0],
					"C:\\own\\chess\\ENGINES\\Houdini_15a\\");
			runner.addEngine(engine);
			
			runner.startEngines();
			runner.uciOK();
			
			runner.newGame();
			runner.isReady();
			
			runner.setupPosition("fen 7k/5K2/8/8/8/8/8/8 b - - 575 288");
			
			runner.go(3);
			
			List<String> infos = runner.getInfoLines();
			System.out.println(infos);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

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
package com.bagaturchess.ucitournament.single.runner;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.EngineProcess_BagaturImpl_WorkspaceImpl;
import bagaturchess.uci.impl.Channel_Console;

import com.bagaturchess.ucitournament.framework.match.MatchRunner;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_FixedDepth;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_FixedNodes;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_TimeAndInc;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_TimePerMove;
import com.bagaturchess.ucitournament.single.Tournament;
import com.bagaturchess.ucitournament.single.schedule.ITournamentSchedule;
import com.bagaturchess.ucitournament.single.schedule.TournamentSchedule_2Engines;
import com.bagaturchess.ucitournament.single.schedule.TournamentSchedule_EvenScores;
import com.bagaturchess.ucitournament.single.schedule.TournamentSchedule_OneRound;


public class TournamentRunner {
	
	
	private static final EngineProcess bagatur_workspace 	= new EngineProcess_BagaturImpl_WorkspaceImpl("Bagatur WS", "");
	
	private static final EngineProcess bagatur_dev 			= new EngineProcess("Bagatur DEV", "C:\\DATA\\Engines\\BagaturEngine_DEV\\Bagatur_64_1_core.exe",
															new String [0],
															"C:\\DATA\\Engines\\BagaturEngine_DEV\\");

	private static final EngineProcess bagatur_22 			= new EngineProcess("Bagatur 2.2", "C:\\DATA\\Engines\\BagaturEngine.2.2\\Bagatur_64_1_core.exe",
															new String [0],
															"C:\\DATA\\Engines\\BagaturEngine.2.2\\");
	
	private static final EngineProcess bagatur_20 			= new EngineProcess("Bagatur 2.0", "C:\\DATA\\Engines\\BagaturEngine.2.0\\Bagatur_64_1_core.exe",
															new String [0],
															"C:\\DATA\\Engines\\BagaturEngine.2.0\\");

	private static final EngineProcess bagatur_19a 			= new EngineProcess("Bagatur 1.9a", "C:\\DATA\\Engines\\BagaturEngine.1.9a\\Bagatur_64_1_core.exe",
															new String [0],
															"C:\\DATA\\Engines\\BagaturEngine.1.9a\\");
													
	private static final EngineProcess bagatur_19 			= new EngineProcess("Bagatur 1.9", "C:\\DATA\\Engines\\BagaturEngine.1.9\\Bagatur_64_1_core.exe",
															new String [0],
															"C:\\DATA\\Engines\\BagaturEngine.1.9\\");
	
	private static final EngineProcess bagatur_18a 			= new EngineProcess("Bagatur 1.8a", "C:\\DATA\\Engines\\BagaturEngine.1.8a\\Bagatur_64_1_core.exe",
															new String [0],
															"C:\\DATA\\Engines\\BagaturEngine.1.8a\\");

	
	private static final EngineProcess bagatur_17 			= new EngineProcess("Bagatur 1.7", "C:\\DATA\\Engines\\BagaturEngine.1.7\\Bagatur_64_1_core.exe",
															new String [0],
															"C:\\DATA\\Engines\\BagaturEngine.1.7\\");

	private static final EngineProcess bagatur_17a 			= new EngineProcess("Bagatur 1.7a", "C:\\DATA\\Engines\\BagaturEngine.1.7a\\Bagatur_64_1_core.exe",
															new String [0],
															"C:\\DATA\\Engines\\BagaturEngine.1.7a\\");

	private static final EngineProcess bagatur_16c 			= new EngineProcess("Bagatur 1.6c", "C:\\DATA\\Engines\\BagaturEngine.1.6c\\Bagatur_64_1_core.exe",
															new String [0],
															"C:\\DATA\\Engines\\BagaturEngine.1.6c\\");

	
	private static final EngineProcess komodo_9_02			=  new EngineProcess("C:\\DATA\\Engines\\Komodo9\\Windows\\komodo-9.02-64bit.exe",
															new String [0],
															"C:\\DATA\\Engines\\Komodo9\\Windows\\");

	private static final EngineProcess chess22k_1_13 		=  new EngineProcess("Chess22k.1.13", "C:\\DATA\\Engines\\Chess22k-1.13\\run.bat",
															new String [0],
															"C:\\DATA\\Engines\\Chess22k-1.13\\");

	private static final EngineProcess asparuh 				=  new EngineProcess("Asparuh", "C:\\DATA\\Engines\\Asparuh\\run.bat",
															new String [0],
															"C:\\DATA\\Engines\\Asparuh\\");
	
	private static final EngineProcess cuckoo112 			=  new EngineProcess("Cuckoo.1.12", "C:\\DATA\\Engines\\Cuckoo112\\cuckoo112.bat",
															new String [0],
															"C:\\DATA\\Engines\\Cuckoo112\\");

	private static final EngineProcess stockfish10 			= new EngineProcess("C:\\DATA\\Engines\\stockfish-10-win\\Windows\\stockfish_10_x64.exe",
															new String [0],
															"C:\\DATA\\Engines\\stockfish-10-win\\Windows");


	private static final EngineProcess houdini15a 			= new EngineProcess("C:\\DATA\\Engines\\Houdini_15a\\Houdini_15a_x64.exe",
															new String [0],
															"C:\\DATA\\Engines\\Houdini_15a\\");
	
	
	public static void main(String[] args) {
		
		
		EngineProcess engine1 = bagatur_workspace;
		EngineProcess engine2 = bagatur_22;
		
		
		EngineProcess[] engines = new EngineProcess[] {engine1, engine2};
		
		
		try {
			
			ChannelManager.setChannel(new Channel_Console());
			
			engine1.start();
			engine2.start();
			
			List<String> options = new ArrayList<String>();
			//options.add("setoption name Logging Policy value multiple files");
			options.add("setoption name Ponder value false");
			options.add("setoption name OwnBook value true");
			options.add("setoption name Openning Mode value random intermediate");
			options.add("setoption name Time Control Optimizations value for 1/1");
			options.add("setoption name SyzygyPath value tbd");//C:/Users/i027638/OneDrive - SAP SE/DATA/OWN/chess/EGTB/syzygy
			
			engine1.setOptions(options);
			engine2.setOptions(options);
			
			ITournamentSchedule schedule = new TournamentSchedule_2Engines(engines, 100000);
			
			//MatchRunner matchRunner = new MatchRunner_TimePerMove(50);
			//MatchRunner matchRunner = new MatchRunner_FixedDepth(7);
			//MatchRunner matchRunner = new MatchRunner_FixedNodes(10000);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(10 * 60 * 1000, 10 * 60 * 1000, 10 * 1000, 10 * 1000);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(1 * 60 * 1000, 1 * 60 * 1000, 1 * 1000, 1 * 1000);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(20 * 1000, 20 * 1000, 200, 200);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(10 * 1000, 10 * 1000, 100, 100);
			MatchRunner matchRunner = new MatchRunner_TimeAndInc(5 * 1000, 5 * 1000, 50, 50);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(1 * 1000, 1 * 1000, 10, 10);
			
			Tournament tournament = new Tournament(schedule, matchRunner, false);
			
			tournament.start();
			
		} catch (Throwable t) {
			
			t.printStackTrace();
			
		} finally {
			
			for (int i=0; i<engines.length; i++) {
				try {
					engines[i].destroy();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}
			
			System.exit(0);
		}
	}
}

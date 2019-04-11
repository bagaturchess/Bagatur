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

import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.EngineProcess_BagaturImpl_WorkspaceImpl;

import com.bagaturchess.ucitournament.framework.match.MatchRunner;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_FixedDepth;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_TimePerMove;
import com.bagaturchess.ucitournament.single.Tournament;
import com.bagaturchess.ucitournament.single.schedule.ITournamentSchedule;
import com.bagaturchess.ucitournament.single.schedule.TournamentSchedule_2Engines;
import com.bagaturchess.ucitournament.single.schedule.TournamentSchedule_EvenScores;
import com.bagaturchess.ucitournament.single.schedule.TournamentSchedule_OneRound;


public class TournamentRunner {
	
	public static void main(String[] args) {
				
		EngineProcess engine1 = new EngineProcess("Bagatur DEV Komodo", "C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine_KomodoStyle\\Bagatur_64_1_core.exe",
				new String [0],
				"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine_KomodoStyle\\");

		EngineProcess engine2 = new EngineProcess("Bagatur DEV Stockfish", "C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine_StockfishStyle\\Bagatur_64_1_core.exe",
				new String [0],
				"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine_StockfishStyle\\");
		
		
		/*EngineProcess engine2 = new EngineProcess("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Komodo9\\Windows\\komodo-9.02-64bit.exe",
				new String [0],
				"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\software\\ARENA\\arena_3.5.1\\Engines\\Komodo9\\Windows\\");
		 */
		
		EngineProcess[] engines = new EngineProcess[] {engine1, engine2};
		
		
		List<String> options = new ArrayList<String>();
		options.add("setoption name Logging Policy value multiple files");
		options.add("setoption name OwnBook value true");
		options.add("setoption name Ponder value false");
		options.add("setoption name Openning Mode value random intermediate");
		options.add("setoption name Time Control Optimizations value for 1/1");
		options.add("setoption name SyzygyPath value tbd");
		
		try {
			engine1.start();
			engine2.start();
			engine1.setOptions(options);
			engine2.setOptions(options);
			
			ITournamentSchedule schedule = new TournamentSchedule_2Engines(engines, 50);
			//ITournamentSchedule schedule = new TournamentSchedule_EvenScores(engines);
			
			MatchRunner matchRunner = new MatchRunner_TimePerMove(1 * 500);
			//MatchRunner matchRunner = new MatchRunner_FixedDepth(5);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(60 * 1000, 60 * 1000, 1 * 1000, 1 * 1000);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(6 * 1000, 6 * 1000, 2 * 100, 2 * 100);
			
			Tournament tournament = new Tournament(schedule, matchRunner, false);
			
			tournament.start();
			
		} catch (Exception e) {
			
			for (int i=0; i<engines.length; i++) {
				try {
					engines[i].destroy();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
			
			System.exit(-1);
		}
	}
}

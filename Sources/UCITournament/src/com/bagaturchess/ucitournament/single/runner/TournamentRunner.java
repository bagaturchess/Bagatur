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
				
		EngineProcess engine1 = new EngineProcess("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine.1.6c\\Bagatur_64_1_core.exe",
				new String [0],
				"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine.1.6c\\");

		EngineProcess engine2 = new EngineProcess("C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine.1.7\\Bagatur_1.7.exe",
				new String [0],
				"C:\\Users\\i027638\\OneDrive - SAP SE\\DATA\\OWN\\chess\\SOFTWARE\\ARENA\\arena_3.5.1\\Engines\\BagaturEngine.1.7\\");
		
		EngineProcess[] engines = new EngineProcess[] {engine1, engine2};
		
		try {
			
			ITournamentSchedule schedule = new TournamentSchedule_2Engines(engines, 50);
			//ITournamentSchedule schedule = new TournamentSchedule_EvenScores(engines);
			
			MatchRunner matchRunner = new MatchRunner_TimePerMove(1 * 500);
			//MatchRunner matchRunner = new MatchRunner_FixedDepth(3);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(60 * 1000, 60 * 1000, 1 * 1000, 1 * 1000);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(6 * 1000, 6 * 1000, 2 * 100, 2 * 100);
			
			Tournament tournament = new Tournament(schedule, matchRunner);
			
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

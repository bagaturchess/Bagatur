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

import com.bagaturchess.ucitournament.framework.match.MatchRunner;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_FixedDepth;
import bagaturchess.uci.engine.EngineProcess_BagaturImpl;
import com.bagaturchess.ucitournament.single.Tournament;
import com.bagaturchess.ucitournament.single.schedule.ITournamentSchedule;
import com.bagaturchess.ucitournament.single.schedule.TournamentSchedule_EvenScores;


public class TournamentRunner {
	
	public static void main(String[] args) {
		
		EngineProcess bagatur1 = new EngineProcess_BagaturImpl("com.krasimir.topchiyski.chess.configs.tune.exts.sr.ECFG_NoExts", "");
		EngineProcess bagatur2 = new EngineProcess_BagaturImpl("com.krasimir.topchiyski.chess.configs.tune.exts.sr.ECFG_NoExts", "");
		
		//Engine[] engines = new Engine[] {glaurung, rybka, aice, fruit21};
		EngineProcess[] engines = new EngineProcess[] {bagatur1, bagatur2};
		//Engine[] engines = new Engine[] {bagatur, aice};
		//Engine[] engines = new Engine[] {glaurung, rybka, aice};
		
		try {
			
			//ITournamentSchedule schedule = new ITournamentSchedule_OneRound(engines);
			ITournamentSchedule schedule = new TournamentSchedule_EvenScores(engines);
			
			//MatchRunner matchRunner = new MatchRunner_TimePerMove(1 * 1000);
			MatchRunner matchRunner = new MatchRunner_FixedDepth(3);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(60 * 1000, 60 * 1000, 1 * 1000, 1 * 1000);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(6 * 1000, 6 * 1000, 2 * 100, 2 * 100);
			
			Tournament tournament = new Tournament(schedule, matchRunner);
			
			tournament.start();
			
		} catch (Exception e) {
			
			for (int i=0; i<engines.length; i++) {
				try {
					engines[i].stop();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}
			
			e.printStackTrace();
			
			System.exit(-1);
		}
	}
}

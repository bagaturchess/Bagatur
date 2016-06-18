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


import bagaturchess.ucitracker.impl.Engine;

import com.bagaturchess.ucitournament.framework.match.MatchRunner;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_TimeAndInc;
import com.bagaturchess.ucitournament.framework.utils.BagaturEngine;
import com.bagaturchess.ucitournament.single.Tournament;
import com.bagaturchess.ucitournament.single.schedule.ITournamentSchedule;
import com.bagaturchess.ucitournament.single.schedule.TournamentSchedule_EvenScores;


public class TournamentRunner_EXT_CNP {
	
	
	public static void main(String[] args) {
		
		Engine bagatur = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.ECFG_NoExts", "");
		
		
		Engine bagatur1 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "1");
		Engine bagatur2 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "2");
		Engine bagatur3 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "3");
		Engine bagatur4 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "4");
		Engine bagatur5 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "5");
		Engine bagatur6 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "6");
		Engine bagatur7 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "7");
		Engine bagatur8 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "8");
		Engine bagatur9 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "9");
		Engine bagatur10 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "10");
		Engine bagatur11 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "11");
		Engine bagatur12 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "12");
		Engine bagatur13 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "13");
		Engine bagatur14 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "14");
		Engine bagatur15 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "15");
		Engine bagatur16 = new BagaturEngine("com.krasimir.topchiyski.chess.configs.tune.exts.cnp.extmode_none.ECFG_EXTS_CNP", "16");
		
		Engine[] engines = new Engine[] {bagatur, bagatur1, bagatur2, bagatur3, bagatur4, bagatur5, bagatur6, bagatur7, bagatur8, bagatur9,
				bagatur10, bagatur11, bagatur12, bagatur13, bagatur14, bagatur15, bagatur16};
		
		
		
		try {
			
			for (int i=0; i<engines.length; i++) {
				engines[i].start();
				engines[i].supportsUCI();
				engines[i].stop();
			}
			
			System.out.println("Engines start check: OK");
			
			//ITournamentSchedule schedule = new ITournamentSchedule_OneRound(engines);
			ITournamentSchedule schedule = new TournamentSchedule_EvenScores(engines);
			
			//MatchRunner matchRunner = new MatchRunner_TimePerMove(1 * 1000);
			//MatchRunner matchRunner = new MatchRunner_FixedDepth(3);
			MatchRunner matchRunner = new MatchRunner_TimeAndInc(30 * 1000, 30 * 1000, 3 * 1000, 3 * 1000);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(1 * 60 * 1000, 1 * 60 * 1000, 3 * 1000, 3 * 1000);
			
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

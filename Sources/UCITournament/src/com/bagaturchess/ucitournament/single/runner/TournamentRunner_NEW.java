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
import com.bagaturchess.ucitournament.framework.match.MatchRunner_TimePerMove;
import bagaturchess.uci.engine.EngineProcess_BagaturImpl;
import com.bagaturchess.ucitournament.single.Tournament;
import com.bagaturchess.ucitournament.single.schedule.ITournamentSchedule;
import com.bagaturchess.ucitournament.single.schedule.TournamentSchedule_EvenScores;


public class TournamentRunner_NEW {
	
	
	public static void main(String[] args) {
		
		String programargs_bagatur12 = "bagaturchess.search.impl.alg.SearchMTD ";
		programargs_bagatur12 += "bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD ";
		programargs_bagatur12 += "bagaturchess.engines.bagatur.cfg.board.BoardConfigImpl ";
		programargs_bagatur12 += "bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2 ";
		
		String programargs_bagaturMTD1 = "bagaturchess.search.impl.alg.SearchMTD1 ";
		programargs_bagaturMTD1 += "bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD ";
		programargs_bagaturMTD1 += "bagaturchess.engines.bagatur.cfg.board.BoardConfigImpl ";
		programargs_bagaturMTD1 += "bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2 ";
		
		//programargs_bagatur12 += "bagaturchess.learning.impl.eval.cfg.WeightsBoardConfigImpl ";
		//programargs_bagatur12 += "bagaturchess.engines.learning.cfg.weights.EvaluationConfg ";		
		//arg.6=bagaturchess.search.impl.alg.SearchMTD1
		//arg.7=bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD
		//arg.8=bagaturchess.engines.bagatur.cfg.board.BoardConfigImpl
		//arg.9=bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2
		
		EngineProcess_BagaturImpl bagatur1 = new EngineProcess_BagaturImpl("Bagatur_1.2", programargs_bagatur12);
		EngineProcess_BagaturImpl bagatur2 = new EngineProcess_BagaturImpl("Bagatur_MTD1", programargs_bagaturMTD1);
		
		
		
		EngineProcess[] engines = new EngineProcess[] {bagatur1, bagatur2};
		
		try {
			
			for (int i=0; i<engines.length; i++) {
				engines[i].start();
				engines[i].supportsUCI();
				engines[i].stop();
			}
			
			System.out.println("Engines start check: OK");
			
			//ITournamentSchedule schedule = new ITournamentSchedule_OneRound(engines);
			ITournamentSchedule schedule = new TournamentSchedule_EvenScores(engines);
			
			MatchRunner matchRunner = new MatchRunner_TimePerMove(1 * 100);
			//MatchRunner matchRunner = new MatchRunner_FixedDepth(3);
			//MatchRunner matchRunner = new MatchRunner_TimeAndInc(30 * 1000, 30 * 1000, 3 * 1000, 3 * 1000);
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

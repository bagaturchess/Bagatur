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
package com.bagaturchess.ucitournament.single;


import java.io.IOException;
import java.util.Date;

import bagaturchess.ucitracker.impl.Engine;

import com.bagaturchess.ucitournament.framework.Pair;
import com.bagaturchess.ucitournament.framework.match.MatchRunner;
import com.bagaturchess.ucitournament.single.schedule.ITournamentSchedule;


public class Tournament {
	
	
	private ITournamentSchedule schedule;
	private MatchRunner matchRunner;
	
	
	public Tournament(ITournamentSchedule _schedule, MatchRunner _matchRunner) {
		schedule = _schedule;
		matchRunner = _matchRunner;
	}
	
	
	public void start() throws IOException {
		TournamentResult tournamentResult = new TournamentResult();
		
		System.out.println((new Date()));
		System.out.println(schedule);
		
		int rounds = schedule.getRounds();
		
		for (int round = 0; round < rounds; round++) {
			Pair[] pairs = schedule.getPairsByRound(round);
			for (int pair = 0; pair < pairs.length; pair++) {
				
				Engine engine1 = pairs[pair].getWhiteEngine();
				Engine engine2 = pairs[pair].getBlackEngine();
				String engine1Name = engine1.getName();
				String engine2Name = engine2.getName();
				
				matchRunner.newGame();
				
				engine1.start();
				engine2.start();
				
				System.out.print("Playing " + pairs[pair] + " ... ");
				int result = matchRunner.execute(engine1, engine2);
				System.out.println(" finished. Result is " + result);
				tournamentResult.addResult(engine1Name, engine2Name, result);
				
				
				engine1.stop();
				engine2.stop();
			}
			System.out.println("\r\nRESULT [round " + (round + 1) + "]: " + (new Date()) + "\r\n" + tournamentResult);
		}
	}
}

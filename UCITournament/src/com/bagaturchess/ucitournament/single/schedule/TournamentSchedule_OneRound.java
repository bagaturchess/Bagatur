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
package com.bagaturchess.ucitournament.single.schedule;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.uci.engine.EngineProcess;

import com.bagaturchess.ucitournament.framework.Pair;


public class TournamentSchedule_OneRound implements ITournamentSchedule {

	
	private EngineProcess[] engines;
	private Pair[] pairs;
	
	
	public TournamentSchedule_OneRound(EngineProcess[] _engines) {
		engines = _engines;
		
		List<Pair> all = new ArrayList<Pair>();
		for (int e1_idx = 0; e1_idx<engines.length - 1; e1_idx++) {
			for (int e2_idx = e1_idx + 1; e2_idx<engines.length; e2_idx++) {
				
				EngineProcess engine1 = engines[e1_idx];
				EngineProcess engine2 = engines[e2_idx];
				Pair pair1 = new Pair(engine1, engine2);
				Pair pair2 = new Pair(engine2, engine1);
				all.add(pair1);
				all.add(pair2);
			}
		}
		pairs = all.toArray(new Pair[0]);
	}
	
	
	public Pair[] getPairsByRound(int round) {
		return pairs;
	}

	public int getRounds() {
		return 1;
	}

	public EngineProcess[] getEngines() {
		return engines;
	}

	public String toString() {
		String msg = "";
		
		for (int i=0; i<pairs.length; i++) {
			msg += "" + "ROUND 1/1 -> "  + " PAIR " + (i + 1) + "/" + pairs.length + " " + pairs[i] + "\r\n";
		}
		
		return msg;
	}
}

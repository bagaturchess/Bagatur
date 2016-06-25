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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bagaturchess.uci.engine.EngineProcess;

import com.bagaturchess.ucitournament.framework.Pair;


public class TournamentSchedule_EvenScores implements ITournamentSchedule {
	
	
	private int rounds;
	private EngineProcess[] engines;
	private Pair[][] pairs;
	
	
	public TournamentSchedule_EvenScores(EngineProcess[] _engines) {
		
		engines = _engines;		
		pairs = new Pair[engines.length][];
		
		Set<Pair> unique = new HashSet<Pair>();
		for (int shift = 1; shift<engines.length; shift++) {
			List<Pair> all = new ArrayList<Pair>();
			for (int e1_idx = 0; e1_idx<engines.length; e1_idx++) {
				
				EngineProcess engine1 = engines[e1_idx];
				EngineProcess engine2 = engines[(e1_idx + shift) % engines.length];
				
				Pair pair1 = new Pair(engine1, engine2);
				Pair pair2 = new Pair(engine2, engine1);
				if (!unique.contains(pair1)) {
					all.add(pair1);
					unique.add(pair1);
				}
				if (!unique.contains(pair2)) {
					all.add(pair2);
					unique.add(pair2);
				}
			}
			if (all.size() <= 0) {
				break;
			}
			pairs[shift - 1] = all.toArray(new Pair[0]);
			rounds++;
		}
	}
	
	
	public Pair[] getPairsByRound(int round) {
		return pairs[round];
	}
	
	public int getRounds() {
		return rounds;
	}
	
	public EngineProcess[] getEngines() {
		return engines;
	}
	
	public String toString() {
		String msg = "";
		
		for (int p=0; p<pairs.length; p++) {
			if (pairs[p] != null) {
				for (int n=0; n<pairs[p].length; n++) {
					msg += "" + "ROUND " + (p + 1) + "/" + pairs.length + " -> "  + " PAIR " + (n + 1) + "/" + pairs[p].length + " " + pairs[p][n] + "\r\n";
				}
			}
		}
		
		return msg;
	}
}

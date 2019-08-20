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

import java.util.HashMap;
import java.util.Map;

public class TournamentResult {
	
	
	private Map<String, Integer> map = new HashMap<String, Integer>();
	
	
	public TournamentResult() {
		
	}
	
	private void addScores(String engineName, int scores) {
		Integer old_scores = map.get(engineName);
		if (old_scores == null) {
			map.put(engineName, scores);
		} else {
			map.put(engineName, old_scores + scores);
		}
	}
	
	public void addResult(String whiteEngineName, String blackEngineName, int result) {
		//System.out.println("" + whiteEngineName + " vs " + blackEngineName + " -> " + result);
		
		if (result == 1) {
			addScores(whiteEngineName, 2);
		} else if (result == -1) {
			addScores(blackEngineName, 2);
		} else if (result == 0) {
			addScores(whiteEngineName, 1);
			addScores(blackEngineName, 1);
		} else {
			throw new IllegalStateException("result=" + result);
		}
	}
	
	@Override
	public String toString() {
		String result = "";
		
		for (String name: map.keySet()) {
			Integer scoresObj = map.get(name);
			int scores = scoresObj == null ? 0 : scoresObj;
			result += name + "	" + scores + "  " + getELODiff(scores) + "\r\n";
		}
		
		return result;
	}
	
	
	private int getELODiff(int scores) {
		double log = (sumAllScores() - scores) / (double) scores;
		if (log == 0) return 3500;
		return (int) (-Math.log10(log) * 400D);
	}
	
	
	private int sumAllScores() {
		int result = 0;
		for (String name: map.keySet()) {
			Integer scoresObj = map.get(name);
			int scores = scoresObj == null ? 0 : scoresObj;
			result += scores;
		}
		return result;
	}
}

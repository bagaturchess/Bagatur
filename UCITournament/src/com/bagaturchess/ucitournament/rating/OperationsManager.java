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
package com.bagaturchess.ucitournament.rating;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OperationsManager {
	
	public static final void adjustPlaces(List<EngineMetaInf> enginesMetaInfs) {
		Set<EngineMetaInf> sorted = new TreeSet<EngineMetaInf>(new Comparator_ELO());
		for (EngineMetaInf cur_engine: enginesMetaInfs) {
			sorted.add(cur_engine);
		}
		
		int counter = 0;
		for (EngineMetaInf cur_engine: sorted) {
			cur_engine.setPlace(++counter);
		}
	}
	
	public static final List<EngineMetaInf> get2EngineByPlayedGamesCount_min(List<EngineMetaInf> enginesMetaInfs) {
		
		Set<EngineMetaInf> sorted = new TreeSet<EngineMetaInf>(new Comparator_GAMES_COUNT());
		for (EngineMetaInf cur_engine: enginesMetaInfs) {
			sorted.add(cur_engine);
		}
		
		if (sorted.size() < 2) {
			throw new IllegalStateException("There is not enough engines.");
		}
		
		int maxGames = -1;
		for (EngineMetaInf cur: sorted) {
			maxGames = cur.getPlayedGamesCount();
			break;
		}
		
		int to_index = -1;
		for (EngineMetaInf cur: sorted) {
			if (cur.getPlayedGamesCount() > maxGames) {
				break;
			}
			to_index++;
		}
		
		if (to_index < 1) {
			to_index = 1;
		}
		
		int cur_index = 0;
		List<EngineMetaInf> selection = new ArrayList<EngineMetaInf>();
		for (EngineMetaInf cur: sorted) {
			selection.add(cur);
			if (cur_index >= to_index) {
				break;
			}
			cur_index++;
		}
		
		return selection;
	}
	
	public static final List<EngineMetaInf> get2EngineByMovingDir_max(List<EngineMetaInf> enginesMetaInfs) {
		
		Set<EngineMetaInf> sorted = new TreeSet<EngineMetaInf>(new Comparator_MovingDir());
		for (EngineMetaInf cur_engine: enginesMetaInfs) {
			sorted.add(cur_engine);
		}
		
		if (sorted.size() < 2) {
			throw new IllegalStateException("There is not enough engines.");
		}
		
		
		double maxEloMovingDir = -1;
		for (EngineMetaInf cur: sorted) {
			maxEloMovingDir = cur.getELOMovingDirection();
			break;
		}
		
		int to_index = -1;
		for (EngineMetaInf cur: sorted) {
			if (cur.getELOMovingDirection() < maxEloMovingDir) {
				break;
			}
			to_index++;
		}
		
		if (to_index < 1) {
			to_index = 1;
		}
		
		int cur_index = 0;
		List<EngineMetaInf> selection = new ArrayList<EngineMetaInf>();
		for (EngineMetaInf cur: sorted) {
			selection.add(cur);
			if (cur_index >= to_index) {
				break;
			}
			cur_index++;
		}
		
		return selection;
	}
	
	public static void adjustProperties(EngineMetaInf[] pair, double result, RatingWorkspace workspace, int gamesCount) {
		double result_w = (result + 1) / (double)2;
		double result_b = 1 - result_w;
		
		workspace.getLog().log("Result in [0, 1] -> white " + result_w + ", black " + result_b);
		
		EngineMetaInf engine_w = pair[0];
		EngineMetaInf engine_b = pair[1];
		
		engine_w.setPlayedGamesCount(engine_w.getPlayedGamesCount() + gamesCount);
		engine_b.setPlayedGamesCount(engine_b.getPlayedGamesCount() + gamesCount);
		
		int elo_w = engine_w.getELO();
		int elo_b = engine_b.getELO();
		int adjust_w = adjustELO(elo_w, elo_b, result_w);
		int adjust_b = adjustELO(elo_b, elo_w, result_b);
		engine_w.setELO(engine_w.getELO() + adjust_w);
		engine_b.setELO(engine_b.getELO() + adjust_b);
		
		engine_w.addELOAdjustments_sum(adjust_w);
		engine_w.addELOAdjustments_total(Math.abs(adjust_w));
		engine_b.addELOAdjustments_sum(adjust_b);
		engine_b.addELOAdjustments_total(Math.abs(adjust_b));
		
		workspace.getLog().log("Adjusting ELO: white " + adjust_w + ", black " + adjust_b);
	}
	
	private static int adjustELO(int elo1, int elo2, double result) {
		
		double pow = Math.pow(10, (elo2 - elo1) / (double) 400);
		//System.out.println("pow=" + pow);
		double expected = 1 / (double)(1 + pow);
		//System.out.println("expected=" + expected);
		double adjustment = 16 * (result - expected);
		//System.out.println("adjustment=" + adjustment);
		
		if (Math.abs(adjustment) > 0 && Math.abs(adjustment) < 1) {
			if (adjustment > 0) {
				adjustment = 1;
			} else if (adjustment < 0) {
				adjustment = -1;
			} else {
				throw new IllegalStateException("adjustment=" + adjustment);
			}
		}
		
		return (int) adjustment;
	}
	
	public static void main(String[] args) {
		double result1 = adjustELO(2300, 2700, 0.5);
		double result2 = adjustELO(2700, 2300, 0.5);
		
		System.out.println("result1=" + result1 + ", result2=" + result2);
	}
	
}

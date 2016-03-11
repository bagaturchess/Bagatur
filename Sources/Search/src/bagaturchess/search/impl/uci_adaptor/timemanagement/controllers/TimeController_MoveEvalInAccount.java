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


package bagaturchess.search.impl.uci_adaptor.timemanagement.controllers;


import bagaturchess.uci.api.ITimeConfig;


public abstract class TimeController_MoveEvalInAccount extends TimeController_BaseImpl {
	
	
	private double moveevaldiff_cfg_max_usage_percent_of_total_time	= 0.20;
	private double moveevaldiff_cfg_scores_diff_devider 			= 2;
	private int moveevaldiff_cfg_scores_penalty_on_diff_moves 		= 25;
	private int moveevaldiff_cfg_max_scoress_diff 					= 300;
	
	private double moveevaldiff_accumulated_score_diff;
	private double moveevaldiff_usage_percent_of_total_time;

	private int lastmove;
	private int lasteval;
	private int lastdepth;
	
	
	public TimeController_MoveEvalInAccount(ITimeConfig tconf) {
		moveevaldiff_cfg_max_scoress_diff = tconf.getMoveEvallDiff_MaxScoreDiff();
		moveevaldiff_cfg_max_usage_percent_of_total_time = tconf.getMoveEvallDiff_MaxTotalTimeUsagePercent();
	}
	
	
	protected double getMoveEvalDiff_UsagePercentOfTotalTime() {
		return moveevaldiff_usage_percent_of_total_time;
	}
	
	
	@Override
	public void newPVLine(int eval, int depth, int move) {
		
		if (lastmove != 0) {
			
			int curScoresDiff = (int)Math.abs(lasteval - eval);
			if (lastdepth != depth) {
				moveevaldiff_accumulated_score_diff = moveevaldiff_accumulated_score_diff / moveevaldiff_cfg_scores_diff_devider;
			}
			moveevaldiff_accumulated_score_diff += curScoresDiff;
			if (lastmove != move) {
				moveevaldiff_accumulated_score_diff += moveevaldiff_cfg_scores_penalty_on_diff_moves;
			}
			
			moveevaldiff_usage_percent_of_total_time = moveevaldiff_cfg_max_usage_percent_of_total_time *
					(Math.min(moveevaldiff_cfg_max_scoress_diff, moveevaldiff_accumulated_score_diff) / (double) (moveevaldiff_cfg_max_scoress_diff));
		}
		
		lastmove = move;
		lasteval = eval;
		lastdepth = depth;
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += super.toString();
		result += " moveevaldiff_cfg_max_scoress_diff=" + moveevaldiff_cfg_max_scoress_diff;
		result += ", moveevaldiff_cfg_max_usage_percent_of_total_time=" + moveevaldiff_cfg_max_usage_percent_of_total_time;
		return result;
	}
}

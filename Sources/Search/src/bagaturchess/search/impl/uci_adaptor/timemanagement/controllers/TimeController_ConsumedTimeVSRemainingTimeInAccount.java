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


public abstract class TimeController_ConsumedTimeVSRemainingTimeInAccount extends TimeController_MoveEvalInAccount {
	
	
	private double timeoptimization_cfg_consumed_time_vs_remaining_time = 0.50;
	
	private boolean timeoptimization_terminate_search = false; 
	
	
	public TimeController_ConsumedTimeVSRemainingTimeInAccount(ITimeConfig tconf) {
		super(tconf);
		timeoptimization_cfg_consumed_time_vs_remaining_time = tconf.getTimeoptimization_ConsumedTimeVSRemainingTimePercent();
	}
	
	
	protected boolean getTimeoptimization_TerminateSearch() {
		return timeoptimization_terminate_search;
	}
	
	
	protected abstract long getAvailableTime();
	protected abstract double getMinMoveTime();
	
	
	@Override
	public void newIteration() {
		double tillNow = System.currentTimeMillis() - getStartTime();
		timeoptimization_terminate_search = tillNow >= getMinMoveTime() && tillNow > timeoptimization_cfg_consumed_time_vs_remaining_time * getAvailableTime();
	}


	@Override
	public String toString() {
		String result = "";
		result += super.toString();
		result += ", timeoptimization_cfg_consumed_time_vs_remaining_time=" + timeoptimization_cfg_consumed_time_vs_remaining_time;
		result += ", timeoptimization_terminate_search=" + timeoptimization_terminate_search;
		return result;
	}
}

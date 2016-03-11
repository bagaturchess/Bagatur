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


import bagaturchess.bitboard.impl.Figures;
import bagaturchess.uci.api.ITimeConfig;
import bagaturchess.uci.impl.commands.Go;


public abstract class TimeController_FloatingTime extends TimeController_ConsumedTimeVSRemainingTimeInAccount {
	
	
	private static final long TOTAL_CLOCK_TIME_DEVIDE_FACTOR	= 35;
	
	private static final double TIME_BUFFER_PERCENT 			= 0.10;
	private static final double TIME_BUFFER_GAME_MS				= 5000;
	private static final double TIME_BUFFER_MOVE_MS				= 1000;
	
	private long minMoveTime;
	private long totalClockTime;  
	
	
	public TimeController_FloatingTime(ITimeConfig tconf) {
		super(tconf);
	}
	
	
	public void setupMinMoveTimeAndTotalClockTime(Go goCommand, int colour) {
		
		double wtime_buff = Math.min(TIME_BUFFER_GAME_MS, goCommand.getWtime() * TIME_BUFFER_PERCENT);
		long wtime = (long) Math.max(0, goCommand.getWtime() - wtime_buff);
		
		double btime_buff = Math.min(TIME_BUFFER_GAME_MS, goCommand.getBtime() * TIME_BUFFER_PERCENT);
		long btime = (long) Math.max(0, goCommand.getBtime() - btime_buff);
		
		double wtime_inc_buff = Math.min(TIME_BUFFER_MOVE_MS, goCommand.getWinc() * TIME_BUFFER_PERCENT);
		long wtime_inc = (long) (goCommand.getWinc() - wtime_inc_buff);
		
		double btime_inc_buff = Math.min(TIME_BUFFER_MOVE_MS, goCommand.getBinc() * TIME_BUFFER_PERCENT);
		long btime_inc = (long) (goCommand.getBinc() - btime_inc_buff);
		
		
		if (colour == Figures.COLOUR_WHITE) {
			totalClockTime = (long) (wtime / getTotalClockTime_EmergencyDevideFactor(wtime));
			minMoveTime = (long) Math.max(wtime / (getTotalClockTime_DevideFactor() * getTotalClockTime_EmergencyDevideFactor(wtime)), wtime_inc);
		} else {
			totalClockTime = (long) (btime / getTotalClockTime_EmergencyDevideFactor(btime));
			minMoveTime = (long) Math.max(btime / (getTotalClockTime_DevideFactor() * getTotalClockTime_EmergencyDevideFactor(btime)), btime_inc);
		}
	}
	
	
	@Override
	public boolean hasTime(int futureShift) {
		
		if (getTimeoptimization_TerminateSearch()) {
			return false;
		}
		
		long tillNow = System.currentTimeMillis() + futureShift - getStartTime();
		return tillNow < getAvailableTime();
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += super.toString();
		result += ", totalClockTime_DevideFactor=" + getTotalClockTime_DevideFactor();
		result += ", minMoveTime=" + minMoveTime;
		result += ", totalClockTime=" + totalClockTime;
		result += ", availableMoveTime=" + getAvailableTime();
		
		return result;
	}
	
	
	@Override
	protected double getMinMoveTime() {
		return minMoveTime;
	}
	
	
	@Override
	protected long getAvailableTime() {
		long availableTime = (long) (minMoveTime + totalClockTime * getMoveEvalDiff_UsagePercentOfTotalTime());
		availableTime = Math.min(availableTime, totalClockTime);
		return availableTime;
	}
	
	
	protected long getTotalClockTime_DevideFactor() {
		return TOTAL_CLOCK_TIME_DEVIDE_FACTOR;
	}
	
	protected double getTotalClockTime_EmergencyDevideFactor(long time) {
		return 1;
	}
}

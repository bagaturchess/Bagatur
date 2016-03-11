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


public class TimeController_SuddenDeath extends TimeController_FloatingTime {
	
	private static long DEFAULT_DEVIDE_FACTOR = 35;
	
	
	public TimeController_SuddenDeath() {
		super(new TimeConfig_SuddenDeath());
	}
	
	
	protected double getTotalClockTime_EmergencyDevideFactor(long time) {
		
		/**
		 * linear interpolation between p1_x seconds and p2_x seconds
		 * and sliding corresponding emergency factor from p1_y to p2_y.
		 */
		
		double p1_x = 0;
		double p1_y = 3;
		
		if (time == 0) {
			return p1_y;
		}
		
		double p2_x = 20000;
		double p2_y = 1;
		
		double a = (p1_y - p2_y) / (p1_x - p2_x);
		double b = p1_y - a * p1_x;
		
		double emergencyFactor = a * time + b;
		
		if (emergencyFactor < 1) {
			emergencyFactor = 1;
		}
		
		return emergencyFactor;
	}
	
	
	public static void main(String[] args) {
		
		double p1_x = 0;
		double p1_y = 3;
			
		double p2_x = 20000;
		double p2_y = 1;
		
		double a = (p1_y - p2_y) / (p1_x - p2_x);
		double b = p1_y - a * p1_x;
		
		
		for (int i=0; i<100000; i+=1000) {
			double emergencyFactor = a * i + b;
			
			if (emergencyFactor < 1) {
				emergencyFactor = 1;
			}
			System.out.println("time=" + i + ", emergencyFactor=" + emergencyFactor);
		}
	}
	
	
	@Override
	protected long getTotalClockTime_DevideFactor() {
		return DEFAULT_DEVIDE_FACTOR;
	}
	
	
	private static class TimeConfig_SuddenDeath implements ITimeConfig {
		
		
		@Override
		public int getMoveEvallDiff_MaxScoreDiff() {
			return 300;
		}
		
		
		@Override
		public double getMoveEvallDiff_MaxTotalTimeUsagePercent() {
			return 0.125;
		}
		
		
		@Override
		public double getTimeoptimization_ConsumedTimeVSRemainingTimePercent() {
			return 0.50;
		}
	}
}

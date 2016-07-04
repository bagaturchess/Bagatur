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


import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;


public abstract class TimeController_BaseImpl implements ITimeController {
	
	
	private long startTime;
	
	
	public TimeController_BaseImpl() {
		startTime = System.currentTimeMillis();
	}
	
	
	@Override
	public void newIteration() {
		//Do Nothing
	}
	
	
	@Override
	public void newPVLine(int eval, int depth, int move) {
		//Do Nothing
	}
	
	
	@Override
	public boolean hasTime() {
		return true;
	}
	
	
	@Override
	public long getRemainningTime() {
		return Long.MAX_VALUE;
	}
	
	
	@Override
	public long getStartTime() {
		return startTime;
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += "TimeController[" + this.getClass().getName() + "] " + "tillnow=" + (System.currentTimeMillis() - getStartTime());
		return result;
	}
}

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


public class TimeController_TimePerMove extends TimeController_BaseImpl {
	
	
	private static final double TIME_BUFFER_PERCENT 			= 0.10;
	private static final double TIME_BUFFER_MOVE_MS				= 1000;
	
	
	private long timeToMove;
	
	
	public TimeController_TimePerMove(long _movetime) {
		
		if (_movetime <= 0) {
			throw new IllegalStateException("timeToMove=" + _movetime);
		}
		
		long time_buff = (long) Math.min(TIME_BUFFER_MOVE_MS, _movetime * TIME_BUFFER_PERCENT);

		
		timeToMove = _movetime - time_buff;
	}
	
	
	@Override
	public boolean hasTime() {
		return getRemainningTime() > 0;
	}
	
	
	@Override
	public long getRemainningTime() {
		long tillNow = System.currentTimeMillis() - getStartTime();
		return timeToMove - tillNow;
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += super.toString() + " timeToMove=" + timeToMove;
		return result;
	}
}

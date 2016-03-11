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


public class TimeController_Tournament extends TimeController_FloatingTime {
	
	
	private int movestogo;
	
	
	public TimeController_Tournament(ITimeConfig tconf, int _movestogo) {
		super(tconf);
		if (_movestogo <= 0) {
			throw new IllegalStateException("_movestogo=" + _movestogo);
		}
		movestogo = _movestogo;
	}
	
	
	@Override
	protected long getTotalClockTime_DevideFactor() {
		return (movestogo + 1);
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += super.toString();
		result += ", movestogo=" + movestogo;
		return result;
	}
}

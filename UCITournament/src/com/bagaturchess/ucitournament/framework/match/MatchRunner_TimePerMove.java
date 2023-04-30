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
package com.bagaturchess.ucitournament.framework.match;


import java.io.IOException;

import bagaturchess.uci.engine.EngineProcess;


public class MatchRunner_TimePerMove extends MatchRunner {
	
	
	private int timeInMilis;
	
	
	public MatchRunner_TimePerMove(int _timeInMilis) {
		timeInMilis = _timeInMilis;
	}
	
	@Override
	protected void go(EngineProcess engine) throws IOException {
		engine.go_TimePerMove(timeInMilis);
	}

	@Override
	protected void afterGo(int colourToMove) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void beforeGo(int colourToMove) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getRemainingTime(int colourToMove) {
		return 10;
	}
}

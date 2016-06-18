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

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.ucitracker.impl.Engine;


public class MatchRunner_TimeAndInc extends MatchRunner {
	
	
	private int org_wtime_InMilis;
	private int org_btime_InMilis;
	private int org_winc_InMilis;
	private int org_binc_InMilis;
	
	private int cur_wtime_InMilis;
	private int cur_btime_InMilis;
	private int cur_winc_InMilis;
	private int cur_binc_InMilis;
	
	
	private long startTime;
	
	
	public MatchRunner_TimeAndInc(int _wtime_InMilis, int _btime_InMilis, int _winc_InMilis, int _binc_InMilis) {
		org_wtime_InMilis = _wtime_InMilis;
		org_btime_InMilis = _btime_InMilis;
		org_winc_InMilis = _winc_InMilis;
		org_binc_InMilis = _binc_InMilis;
	}
	
	
	@Override
	public void newGame() {
		cur_wtime_InMilis = org_wtime_InMilis;
		cur_btime_InMilis = org_btime_InMilis;
		cur_winc_InMilis = org_winc_InMilis;
		cur_binc_InMilis = org_binc_InMilis;
	}
	
	@Override
	protected void beforeGo(int colourToMove) {
		startTime = System.currentTimeMillis();
	}
	
	@Override
	protected void go(Engine engine) throws IOException {
		engine.go_TimeAndInc(cur_wtime_InMilis, cur_btime_InMilis, cur_winc_InMilis, cur_binc_InMilis);
	}
	
	@Override
	protected void afterGo(int colourToMove) {
		long endTime = System.currentTimeMillis();
		int thinkTime = (int) (endTime - startTime);
		
		if (colourToMove == Figures.COLOUR_WHITE) {
			cur_wtime_InMilis -= thinkTime;
			cur_wtime_InMilis += cur_winc_InMilis;
		} else {
			cur_btime_InMilis -= thinkTime;
			cur_btime_InMilis += cur_binc_InMilis;
		}
		//System.out.println("cur_wtime_InMilis=" + cur_wtime_InMilis + "	cur_btime_InMilis=" + cur_btime_InMilis);
	}


	@Override
	protected int getRemainingTime(int colourToMove) {
		if (colourToMove == Figures.COLOUR_WHITE) {
			return cur_wtime_InMilis;
		} else {
			return cur_btime_InMilis;
		}
	}
}

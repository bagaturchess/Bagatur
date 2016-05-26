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
package bagaturchess.search.api;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.env.SharedData;


public interface IRootSearch {
	
	public SharedData getSharedData();
	
	public int getTPTUsagePercent();
	public void decreaseTPTDepths(int reduction);
	
	public void newGame(IBitBoard bitboardForSetup);
	
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, boolean useMateDistancePrunning, int[] prevPV);
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, int maxIterations, boolean useMateDistancePrunning, int[] prevPV);
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, int startIteration, int maxIterations, boolean useMateDistancePrunning, int[] prevPV);
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, int startIteration, int maxIterations, boolean useMateDistancePrunning, IFinishCallback finishCallback, int[] prevPV);
	
	public void stopSearchAndWait();
	
	public void shutDown();
}

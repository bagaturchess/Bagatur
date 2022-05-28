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
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.uci.impl.commands.Go;


public interface IRootSearch {
	
	public SharedData getSharedData();
	
	public IBitBoard getBitboardForSetup();
	
	public int getTPTUsagePercent();
	
	public void decreaseTPTDepths(int reduction);
	
	public void createBoard(IBitBoard bitboardForSetup);
	
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, ITimeController timeController, Go go);
	
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, ITimeController timeController, IFinishCallback finishCallback, Go go);
	
	public void stopSearchAndWait();
	
	public boolean isStopped();
	
	public void shutDown();

	public void recreateEvaluator();
}

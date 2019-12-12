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
package bagaturchess.search.impl.rootsearch;


import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.MoveWrapper;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInfoUtils;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public abstract class RootSearch_BaseImpl implements IRootSearch {
	
	
	private IRootSearchConfig rootSearchConfig;
	private SharedData sharedData;
	private IBitBoard bitboardForSetup;
	
	protected ISearchStopper stopper;
	
	
	public RootSearch_BaseImpl(Object[] args) {
		rootSearchConfig = (IRootSearchConfig) args[0];
		sharedData = (SharedData) (args[1] == null ? new SharedData(ChannelManager.getChannel(), rootSearchConfig) : args[1]);
	}
	
	
	public void createBoard(IBitBoard _bitboardForSetup) {
		
		int movesCount = _bitboardForSetup.getPlayedMovesCount();
		int[] moves = Utils.copy(_bitboardForSetup.getPlayedMoves());
		
		_bitboardForSetup.revert();
		
		//bitboardForSetup = new Board3_Adapter(_bitboardForSetup.toEPD(), getRootSearchConfig().getBoardConfig());
		//bitboardForSetup = new Board(_bitboardForSetup.toEPD(), getRootSearchConfig().getBoardConfig());
		
		bitboardForSetup = BoardUtils.createBoard_WithPawnsCache(_bitboardForSetup.toEPD(),
				getRootSearchConfig().getEvalConfig().getPawnsCacheFactoryClassName(),
				getRootSearchConfig().getBoardConfig(),
				10000);
		
		for (int i=0; i<movesCount; i++) {
			_bitboardForSetup.makeMoveForward(moves[i]);
			bitboardForSetup.makeMoveForward(moves[i]);
		}
	}
	
	
	@Override
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator, ITimeController timeController, Go goCommand) {
		negamax(bitboardForSetup, mediator, timeController, null, goCommand);
	}
		
	
	
	@Override
	public SharedData getSharedData() {
		return sharedData;
	}
	
	
	public IRootSearchConfig getRootSearchConfig() {
		return rootSearchConfig;
	}
	
	
	@Override
	public IBitBoard getBitboardForSetup() {
		return bitboardForSetup;
	}
	
	
	protected void setupBoard(IBitBoard _bitboardForSetup) {
		bitboardForSetup.revert();
		int movesCount = _bitboardForSetup.getPlayedMovesCount();
		int[] moves = _bitboardForSetup.getPlayedMoves();
		
		for (int i=0; i<movesCount; i++) {
			bitboardForSetup.makeMoveForward(moves[i]);
		}
	}
	
	
	@Override
	public void stopSearchAndWait() {
		
		if (stopper != null) {
			stopper.markStopped();
		}
		
		if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("stopSearchAndWait - enter");
		
		//if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump(new Exception("just stack dump"));
		
		while (stopper != null) {
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {}
		}
		
		if (DEBUGSearch.DEBUG_MODE) ChannelManager.getChannel().dump("stopSearchAndWait - exit");
	}
	
	
	@Override
	public boolean isStopped() {
		return stopper == null;
	}
	
	
	@Override
	public String toString() {
		return sharedData.toString();
	}
	
	
	protected class Stopper implements ISearchStopper {
		
		private boolean stopped;
		
		public Stopper() {
		}
		
		@Override
		public void markStopped() {
			stopped = true;
		}
		
		@Override
		public boolean isStopped() {
			return stopped;
		}
		
		@Override
		public void stopIfNecessary(int maxdepth, int colour, double alpha,
				double beta) throws SearchInterruptedException {
			
			if (stopped) {
				throw new SearchInterruptedException();
			}
		}
	}
}

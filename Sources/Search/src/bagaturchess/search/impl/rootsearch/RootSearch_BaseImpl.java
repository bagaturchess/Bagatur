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


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.BoardUtils;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.uci.api.ChannelManager;


public abstract class RootSearch_BaseImpl implements IRootSearch {
	
	
	private IRootSearchConfig rootSearchConfig;
	private SharedData sharedData;
	private IBitBoard bitboardForSetup;
	
	
	public RootSearch_BaseImpl(Object[] args) {
		rootSearchConfig = (IRootSearchConfig) args[0];
		sharedData = (SharedData) (args[1] == null ? new SharedData(ChannelManager.getChannel(), rootSearchConfig) : args[1]);
	}

	
	public void newGame(IBitBoard _bitboardForSetup) {
		
		int movesCount = _bitboardForSetup.getPlayedMovesCount();
		int[] moves = Utils.copy(_bitboardForSetup.getPlayedMoves());
		
		_bitboardForSetup.revert();
		
		//bitboardForSetup = new Board3_Adapter(_bitboardForSetup.toEPD(), getRootSearchConfig().getBoardConfig());
		//bitboardForSetup = new Board(_bitboardForSetup.toEPD(), getRootSearchConfig().getBoardConfig());
		
		bitboardForSetup = BoardUtils.createBoard_WithPawnsCache(_bitboardForSetup.toEPD(),
				getRootSearchConfig().getEvalConfig().getPawnsCacheFactoryClassName(),
				getRootSearchConfig().getBoardConfig(),
				1000);
		
		for (int i=0; i<movesCount; i++) {
			_bitboardForSetup.makeMoveForward(moves[i]);
			bitboardForSetup.makeMoveForward(moves[i]);
		}
	}
	
	
	@Override
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator, boolean useMateDistancePrunning) {
		negamax(_bitboardForSetup, mediator, ISearch.MAX_DEPTH, useMateDistancePrunning);
	}


	@Override
	public void negamax(IBitBoard _bitboardForSetup, ISearchMediator mediator, int maxIterations, boolean useMateDistancePrunning) {
		negamax(_bitboardForSetup, mediator, 1, maxIterations, useMateDistancePrunning);
	}
	
	
	@Override
	public void negamax(IBitBoard bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations,
			boolean useMateDistancePrunning) {
		negamax(bitboardForSetup, mediator, startIteration, maxIterations,
				useMateDistancePrunning, new FinishCallback_SendToMediator(mediator));
	}
	
	
	@Override
	public SharedData getSharedData() {
		return sharedData;
	}
	
	
	public IRootSearchConfig getRootSearchConfig() {
		return rootSearchConfig;
	}
	
	protected IBitBoard getBitboardForSetup() {
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
	public String toString() {
		return sharedData.toString();
	}
}

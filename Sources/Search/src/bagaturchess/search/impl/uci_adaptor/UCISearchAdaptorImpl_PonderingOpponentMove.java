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
package bagaturchess.search.impl.uci_adaptor;


import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.impl.info.SearchInfoImpl;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.uci_adaptor.timemanagement.TimeControllerFactory;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.commands.Go;


public class UCISearchAdaptorImpl_PonderingOpponentMove extends UCISearchAdaptorImpl_Base {
	
	
	private int revertedMoveForPondering;
	
	
	public UCISearchAdaptorImpl_PonderingOpponentMove(Object[] args) {
		super(args);
	}
	
	
	@Override
	public synchronized void goSearch(IChannel channel, BestMoveSender sender, Go go) {
		
		//adjust go: with hidden depth
		if (go.getDepth() < go.getDepth() + ((IRootSearchConfig) searchAdaptorCfg.getRootSearchConfig()).getHiddenDepth()) {//Type overflow
			go.setDepth(go.getDepth() + ((IRootSearchConfig) searchAdaptorCfg.getRootSearchConfig()).getHiddenDepth());
		}
		
		bestMoveSender = sender;
		
		if (currentMediator != null) throw new IllegalStateException("mediator is not null");
		int colourToMove = boardForSetup.getColourToMove();
		
		ChannelManager.getChannel().dump("goSearch with command: " + go + ", isPonderSearch(go)=" + isPonderSearch(go));
		
		if (isPonderSearch(go)) {
			
			//One move backward
			revertedMoveForPondering = boardForSetup.getLastMove();
			boardForSetup.makeMoveBackward(revertedMoveForPondering);
			
			//timeController = null;
			currentMediator = new UCISearchMediatorImpl_OpponentPondering(channel, go, boardForSetup.getColourToMove(), new BestMoveSender() {
				
				@Override
				public void sendBestMove() {
					//Don't send move after ponder search
				}
			},
			getSearcher(true), isEndlessSearch(go));
			
			currentGoCommand = go;
			
			goSearch(true, null);
			
		} else {
			
			if (currentGoCommand != null) {
				if (isPonderSearch(currentGoCommand)) {
					
					//Durty code block
					BaseMoveList list = new BaseMoveList();
					boardForSetup.genAllMoves(list);
					int dummyMoveToPrevenIllegalMoveFromGUI = list.next();
					SearchInfoImpl info = (SearchInfoImpl) currentMediator.getLastInfo();
					info.setBestMove(dummyMoveToPrevenIllegalMoveFromGUI);
					info.setPV(new int[] {dummyMoveToPrevenIllegalMoveFromGUI});
					
					currentMediator.getStopper().markStopped();
					
					IRootSearch searcher = getSearcher(isPonderSearch(currentGoCommand));
					searcher.stopSearchAndWait();
					
					sender.sendBestMove();
				} else {
					if (DEBUGSearch.DEBUG_MODE) throw new IllegalStateException("currentGoCommand.isPonder");
				}
			}
			
			ITimeController timeController = TimeControllerFactory.createTimeController(searchAdaptorCfg.getTimeConfig(), colourToMove, go);
			currentMediator = new UCISearchMediatorImpl_NormalSearch(channel, go, timeController, colourToMove, sender, getSearcher(false), isEndlessSearch(go)); 
			currentGoCommand = go;
			
			goSearch(false, timeController);
		}
	}
	
	
	@Override
	public synchronized void ponderHit() {
		
		if (DEBUGSearch.DEBUG_MODE) {
			if (!searchAdaptorCfg.isPonderingEnabled()) {
				throw new IllegalStateException("searchAdaptorCfg.isPonderingEnabled() = " + searchAdaptorCfg.isPonderingEnabled());
			}
			
			if (currentGoCommand == null) {
				throw new IllegalStateException("currentGoCommand == null");
			}
			
			if (!currentGoCommand.isPonder()) {
				throw new IllegalStateException("!currentGoCommand.isPonder()");
			}
		}
		
		UCISearchMediatorImpl_OpponentPondering ponderMediator = (UCISearchMediatorImpl_OpponentPondering) currentMediator;
		
		if (currentGoCommand != null) {
			if (isPonderSearch(currentGoCommand)) {
				
				stopSearch();
				
			} else {
				if (DEBUGSearch.DEBUG_MODE) throw new IllegalStateException("currentGoCommand.isPonder");
			}
		} else {
			if (DEBUGSearch.DEBUG_MODE) throw new IllegalStateException("currentGoCommand != null");
		}
		
		//Should be reverted in order to start the search again with ponder move executed on the board
		boardForSetup.makeMoveForward(revertedMoveForPondering);
		
		Go go = ponderMediator.getGoCommand();
		go.setPonder(false); //Should be after stopSearch, which is above in the if block
		
		ITimeController timeController = TimeControllerFactory.createTimeController(searchAdaptorCfg.getTimeConfig(), boardForSetup.getColourToMove(), go);
		currentMediator = new UCISearchMediatorImpl_NormalSearch(ponderMediator.getChannel(), go, timeController, boardForSetup.getColourToMove(),
				bestMoveSender, getSearcher(false), isEndlessSearch(go)); 
		currentGoCommand = go; //They should be the same anyway
		
		goSearch(false, timeController);
	}
}

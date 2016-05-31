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


import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.uci_adaptor.timemanagement.TimeControllerFactory;
import bagaturchess.search.impl.utils.DEBUGSearch;
import bagaturchess.search.impl.utils.SearchMediatorProxy;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.commands.Go;


public class UCISearchAdaptorImpl_PonderingUCIStandard extends UCISearchAdaptorImpl_Base {
	
	
	public UCISearchAdaptorImpl_PonderingUCIStandard(Object[] args) {
		super(args);
	}
	
	
	@Override
	public synchronized void goSearch(IChannel channel, BestMoveSender sender, Go go) {
		
		if (currentMediator != null) throw new IllegalStateException("mediator is not null");
		int colourToMove = boardForSetup.getColourToMove();
		
		
		if (isPonderSearch(go)) {
			
			if (DEBUGSearch.DEBUG_MODE) {
				if (currentGoCommand != null && currentGoCommand.isPonder()) {
					throw new IllegalStateException("currentGoCommand != null && currentGoCommand.isPonder()");
				}
			}
			
			timeController = null;
			currentMediator = new SearchMediatorProxy(new UCISearchMediatorImpl_StandardPondering(channel, go, colourToMove, new BestMoveSender() {
				
				@Override
				public void sendBestMove() {
					//Don't send move after ponder search
				}
			}, getSearcher(true), isEndlessSearch(go)));
			
			currentGoCommand = go;
			
			goSearch(true);
			
		} else {
			
			if (currentGoCommand != null) {
				if (isPonderSearch(currentGoCommand)) {
					
					currentMediator.getStopper().markStopped();
					
					IRootSearch searcher = getSearcher(isPonderSearch(currentGoCommand));
					searcher.stopSearchAndWait();
					
					sender.sendBestMove();
				} else {
					if (DEBUGSearch.DEBUG_MODE) throw new IllegalStateException("currentGoCommand.isPonder");
				}
			}
			
			timeController = TimeControllerFactory.createTimeController(searchAdaptorCfg.getTimeConfig(), boardForSetup.getColourToMove(), go);
			currentMediator = new UCISearchMediatorImpl_NormalSearch(channel, go, timeController, colourToMove, sender, getSearcher(false), isEndlessSearch(go)); 
			currentGoCommand = go;
			
			goSearch(false);
		}
	}
	
	
	@Override
	public synchronized void ponderHit() {
		
		if (DEBUGSearch.DEBUG_MODE) {
			if (currentGoCommand == null) {
				throw new IllegalStateException("currentGoCommand == null");
			}
			
			if (!currentGoCommand.isPonder()) {
				throw new IllegalStateException("!currentGoCommand.isPonder()");
			}
		}
		
		UCISearchMediatorImpl_StandardPondering ponderMediator = (UCISearchMediatorImpl_StandardPondering) ((SearchMediatorProxy)currentMediator).getParent();
		Go go = ponderMediator.getGoCommand();
		go.setPonder(false);
		
		ITimeController timeController = TimeControllerFactory.createTimeController(searchAdaptorCfg.getTimeConfig(), boardForSetup.getColourToMove(), go);
		UCISearchMediatorImpl_NormalSearch switchedMediator = new UCISearchMediatorImpl_NormalSearch(ponderMediator.getChannel(), go, timeController,
				ponderMediator.getColourToMove(), bestMoveSender, getSearcher(false), isEndlessSearch(go));
		switchedMediator.setLastInfo(ponderMediator.getLastInfo());
		
		((SearchMediatorProxy)currentMediator).setParent(switchedMediator);
	}
}

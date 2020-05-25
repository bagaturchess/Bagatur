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
package bagaturchess.search.impl.rootsearch.parallel;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD;
import bagaturchess.search.impl.rootsearch.sequential.NPSCollectorMediator;
import bagaturchess.search.impl.rootsearch.sequential.mtd.Mediator_AlphaAndBestMoveWindow;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class MTDParallelSearch_ThreadsImpl extends MTDParallelSearch_BaseImpl {
	
	
	public MTDParallelSearch_ThreadsImpl(Object[] args) {
		
		super(args);
		
	}
	
	
	@Override
	protected void sequentialSearchers_Create() {
		
		for (int i = 0; i < getRootSearchConfig().getThreadsCount(); i++ ) {
			
			try {
				SequentialSearch_MTD searcher = (SequentialSearch_MTD)
						ReflectionUtils.createObjectByClassName_ObjectsConstructor(SequentialSearch_MTD.class.getName(), new Object[] {getRootSearchConfig(), getSharedData()});
				
				addSearcher(searcher);
				
			} catch (Throwable t) {
				ChannelManager.getChannel().dump(t);
			}
		}
	}
	
	
	@Override
	protected ISearchMediator sequentialSearchers_WrapMediator(ISearchMediator mediator) {
		return new NPSCollectorMediator(new Mediator_AlphaAndBestMoveWindow(mediator));
	}
	
	
	@Override
	protected void sequentialSearchers_Negamax(IRootSearch searcher, IBitBoard _bitboardForSetup, ISearchMediator mediator, ITimeController timeController,
			final IFinishCallback multiPVCallback, Go go, boolean dont_wrap_mediator) {
		
		((SequentialSearch_MTD)searcher).negamax(_bitboardForSetup, mediator, timeController, multiPVCallback, go, dont_wrap_mediator);
	}
	
	
	@Override
	// Only one thread is enough to finish the depth
	protected SearchersInfo createSearchersInfo(final int startIteration) {
		return new SearchersInfo(startIteration, 0.001d); 
	}
	
	
	@Override
	protected boolean restartSearchersOnNewDepth() {
		return false;
	}
}

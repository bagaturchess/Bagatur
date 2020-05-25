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
import bagaturchess.search.impl.rootsearch.remote.SequentialSearch_SeparateProcess;
import bagaturchess.search.impl.rootsearch.sequential.mtd.Mediator_AlphaAndBestMoveWindow;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class MTDParallelSearch_ProcessesImpl extends MTDParallelSearch_BaseImpl {
	
	
	public MTDParallelSearch_ProcessesImpl(Object[] args) {
		
		super(args);
		
	}
	
	
	@Override
	protected void sequentialSearchers_Create() {
		
		if (getRootSearchConfig().getThreadsCount() < 1) {
			throw new IllegalStateException("MTDParallelSearch_ProcessesImpl: threads count is less than 2 = " + getRootSearchConfig().getThreadsCount());
		}
		
		int count_started_sequentially = 1;
		
		ChannelManager.getChannel().dump("MTDParallelSearch_BaseImpl created search with " + getRootSearchConfig().getThreadsCount() + " sequential searchers." +
				" Started sequentialy = " + count_started_sequentially + " searchers, Starting in parallel = " + (getRootSearchConfig().getThreadsCount() - count_started_sequentially) + " searchers ...");

		
		for (int i = 0; i < getRootSearchConfig().getThreadsCount(); i++ ) {
			
			try {
				if (i < count_started_sequentially) {//Start first searcher sequentially
					
					SequentialSearch_SeparateProcess searcher = (SequentialSearch_SeparateProcess)
							ReflectionUtils.createObjectByClassName_ObjectsConstructor(SequentialSearch_SeparateProcess.class.getName(), new Object[] {getRootSearchConfig(), getSharedData()});
					
					addSearcher(searcher);
					
				} else {//Start the rest in parallel
					
					new Thread(new Runnable() {
						
						//@Override
						public void run() {
							SequentialSearch_SeparateProcess searcher = (SequentialSearch_SeparateProcess)
									ReflectionUtils.createObjectByClassName_ObjectsConstructor(SequentialSearch_SeparateProcess.class.getName(), new Object[] {getRootSearchConfig(), getSharedData()});
							
							if (!isTerminated()) {
								addSearcher(searcher);
							} else {
								searcher.shutDown();
							}
							
						}
					}).start();
							
				}

			} catch (Throwable t) {
				ChannelManager.getChannel().dump(t);
			}
		}
	}
	
	
	@Override
	protected ISearchMediator sequentialSearchers_WrapMediator(ISearchMediator mediator) {
		
		//NPSCollectorMediator is not necessary for parallel search. It is used for single sequential searcher only to collect and sum the nodes from each NullWinSearch inside negamax.
		return new Mediator_AlphaAndBestMoveWindow(mediator);
	}
	
	
	@Override
	protected void sequentialSearchers_Negamax(IRootSearch searcher, IBitBoard _bitboardForSetup, ISearchMediator mediator, ITimeController timeController,
			final IFinishCallback multiPVCallback, Go go, boolean dont_wrap_mediator) {
		
		((SequentialSearch_SeparateProcess)searcher).negamax(_bitboardForSetup, mediator, timeController, multiPVCallback, go);
	}
	
	
	@Override
	// All threads have to finish the depth
	protected SearchersInfo createSearchersInfo(final int startIteration) {
		return new SearchersInfo(startIteration, 1d);
	}
	
	
	@Override
	protected boolean restartSearchersOnNewDepth() {
		return false;
	}
}

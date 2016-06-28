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


import java.util.ArrayList;
import java.util.List;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.rootsearch.remote.SequentialSearch_SeparateProcess;
import bagaturchess.search.impl.rootsearch.sequential.Mediator_AlphaAndBestMoveWindow;
import bagaturchess.uci.api.ChannelManager;


public class MTDParallelSearch_ProcessesImpl extends MTDParallelSearch_BaseImpl {
	
	
	public MTDParallelSearch_ProcessesImpl(Object[] args) {
		
		super(args);
		
	}
	
	
	@Override
	protected List<IRootSearch> sequentialSearchers_Create() {
		
		List<IRootSearch> searchers = new ArrayList<IRootSearch>();
		
		for (int i = 0; i < getRootSearchConfig().getThreadsCount(); i++ ) {
			
			try {
				SequentialSearch_SeparateProcess searcher = (SequentialSearch_SeparateProcess)
						ReflectionUtils.createObjectByClassName_ObjectsConstructor(SequentialSearch_SeparateProcess.class.getName(), new Object[] {getRootSearchConfig(), getSharedData()});
				
				searchers.add(searcher);
			} catch (Throwable t) {
				ChannelManager.getChannel().dump(t);
			}
		}
		
		return searchers;
	}
	
	
	@Override
	protected ISearchMediator sequentialSearchers_WrapMediator(ISearchMediator mediator) {
		return new Mediator_AlphaAndBestMoveWindow(mediator);
	}
	
	
	@Override
	protected void sequentialSearchers_Negamax(IRootSearch searcher, IBitBoard _bitboardForSetup, ISearchMediator mediator,
			int startIteration, int maxIterations, final boolean useMateDistancePrunning, final IFinishCallback multiPVCallback,
			int[] prevPV, boolean dont_wrap_mediator, Integer initialValue) {
		
		((SequentialSearch_SeparateProcess)searcher).negamax(_bitboardForSetup, mediator, startIteration, maxIterations, useMateDistancePrunning, multiPVCallback, prevPV);
	}
}

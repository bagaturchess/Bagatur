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
package bagaturchess.search.impl.rootsearch.sequential.mtd;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.utils.DEBUGSearch;


public class SearchManager {
	
	//private static int MTD_INITIAL_STEP = 25;
	
	private ReadWriteLock lock;
	
	//private long hashkey;
	
	private int maxIterations;
	private int currentdepth;
	
	private IBetaGenerator betasGen;
	private List<Integer> betas;
	
	private ISearchMediator mediator;

	private Integer initialValue;
	//private PVHistory pvHistory;
	
	
	public SearchManager(ISearchMediator _mediator, int _startIteration, int _maxIterations, Integer _initialValue) {

		lock = new ReentrantReadWriteLock();
		
		mediator = _mediator;
		//hashkey = _hashkey;
		maxIterations = _maxIterations;
		
		currentdepth = _startIteration;//1;
		
		//lower_bound = ISearch.MIN;
		//upper_bound = ISearch.MAX;
		//curIterationEval = ISearch.MIN;
		//prevIterationEval = ISearch.MIN;
		//nodes = 0;
		
		betas = new ArrayList<Integer>();
		
		initialValue = _initialValue;
		
		initBetas();
	}
	
	
	/*public IFinishCallback getFinishCallback() {
		return finishCallback;
	}*/
	
	
	/*public void addNodes(long _nodes) {
		//nodes += _nodes;
	}*/
	
	public void writeLock() {
		//System.out.println("lock");
		lock.writeLock().lock();
	}
	
	public void writeUnlock() {
		//System.out.println("unlock");
		lock.writeLock().unlock();
	}
	
	private void initBetas() {
		
		if (betasGen != null) {
			
			betasGen = BetaGeneratorFactory.create(betasGen.getLowerBound(), mediator.getTrustWindow_MTD_Step());
			
		} else {
			
			int staticRootEval = -1;
			if (initialValue != null) {
				staticRootEval = initialValue;
			} else {
				throw new IllegalStateException("initialValue==null");
			}
			
			betasGen = BetaGeneratorFactory.create(staticRootEval, mediator.getTrustWindow_MTD_Step());
		}
		
		
		betas = betasGen.genBetas();
		//System.out.println("initBetas: " + betas);
		
		/*int count = 1;
		betas.add(initialVal);
		
		int cur_step = 1;
		while (true) {
			if (count >= EngineConfigFactory.getSingleton().getThreadsCount()) break;
			betas.add(initialVal + cur_step * MTD_INITIAL_STEP);
			count++;
			if (count >= EngineConfigFactory.getSingleton().getThreadsCount()) break;
			betas.add(initialVal - cur_step * MTD_INITIAL_STEP);
			count++;				
			cur_step++;
		}*/
		
		//System.out.println("initBetas=" + betas + "	initialVal=" + initialVal);
	}
	
	
	private void updateBetas() {
		betas = betasGen.genBetas();
		
		//System.out.println("UPDATE BETAS: " + betas);
		/*if (lower_bound >= upper_bound) {
			throw new IllegalStateException();
		}
		betas.clear();
		int main_frame = upper_bound - lower_bound;
		int frame = main_frame / (1 + EngineConfigFactory.getSingleton().getThreadsCount());
		if (frame == 0) {
			frame = 1;
		}
		for (int i=0; i<EngineConfigFactory.getSingleton().getThreadsCount(); i++) {
			betas.add(lower_bound + (i + 1) * frame);
		}*/
		
		//System.out.println("updateBetas=" + betas + "	upper_bound=" + upper_bound + "	lower_bound=" + lower_bound);
	}
	
	public int nextBeta() {
		//lock.writeLock().lock();
		
		//System.out.println("nextBeta: " + betas);
		
		if (betas.size() == 0) {
			
			//TODO: Consider
			//int beta_fix = betasGen.getLowerBound() + (betasGen.getUpperBound() - betasGen.getLowerBound()) / 2;
			
			if (DEBUGSearch.DEBUG_MODE) mediator.dump("Search instability with distribution: " + this);
			
			/*mediator.dump("THREAD DUMP 1");
			dumpStacks();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mediator.dump("THREAD DUMP 2");
			dumpStacks();
			*/
			if (DEBUGSearch.DEBUG_MODE) mediator.dump("Betagen obj: " + betasGen);
			updateBetas();
			if (DEBUGSearch.DEBUG_MODE) mediator.dump("The new betas are:" + betas);
					
			//throw new IllegalStateException(toString());
		}
		
		int result = betas.remove(0);
		//System.out.println("nextBeta_res: " + result);
		
		/*Iterator<Integer> iter = betas.iterator();
		if (!iter.hasNext()) {
			throw new IllegalStateException(toString());
		}
		
		int result = iter.next();
		betas.remove(result);*/
		
		//lock.writeLock().unlock();
		
		return result;
	}


	private void dumpStacks() {
		Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
		for (Thread cur: stacks.keySet()) {
			mediator.dump("THREAD: " + cur.getName());
			StackTraceElement[] threadStacks = stacks.get(cur);
			for (int i=0;i<threadStacks.length; i++) {
				String line = threadStacks[i].toString();
				mediator.dump("	" + line);
			}
		}
	}
	
	public void increaseLowerBound(ISearchInfo info) {
		
		boolean sentPV = false;
		
		if (info.getEval() >= betasGen.getLowerBound()) {
			
			sentPV = true;
			
			betasGen.increaseLower(info.getEval());
		}
		
		boolean isLast = isLast();
		
		if (isLast) {
			finishDepth();
			initBetas();
		} else {
			updateBetas();
		}
		
		if (sentPV) {
			
			/*if (isLast) {
				info.setLowerBound(false);
				info.setUpperBound(false);
			}*/
			
			//pvHistory.putPV(hashkey, new PVHistoryEntry(info.getPV(), info.getDepth(), info.getEval()));
			
			if (mediator != null) {
				
				mediator.changedMajor(info);
			}
		}
	}
	
	
	public void decreaseUpperBound(ISearchInfo info) {
		
		boolean sentPV = false;
		
		if (info.getEval() <= betasGen.getUpperBound()) {
			
			sentPV = true;
			
			betasGen.decreaseUpper(info.getEval());
		}
		
		boolean isLast = isLast();
		
		if (isLast) {
			finishDepth();
			initBetas();
		} else {
			updateBetas();
		}
		
		if (sentPV) {
			
			if (mediator != null) {
				
				mediator.changedMajor(info);
			}
		}
	}
	
	
	private void finishDepth() {
		
		//System.out.println("FINISHING DEPTH " + maxdepth);
		
		currentdepth++;
		
		//if (curIterationLastInfo != null) {
		//	throw new IllegalStateException("SearchManager: finishDepth - curIterationLastInfo != null");
		//}
		
		/*if (curIterationEval <= prevIterationEval) {
			//Sent pv
			sharedData.getPVs().putPV(hashkey,
					new PVHistoryEntry(curIterationLastInfo.getPV(), curIterationLastInfo.getDepth(), curIterationLastInfo.getEval()));
			
			if (mediator != null) {
				//curIterationLastInfo.setSearchedNodes(nodes);
				mediator.changedMajor(curIterationLastInfo);
				
				try {
					testPV(curIterationLastInfo, bitboardForTesting);
				} catch (Exception e) {
					mediator.dump(e);
				}
			}
		}*/
		
		//prevIterationEval = curIterationEval;
		//curIterationEval = ISearch.MIN;
		//curIterationLastInfo = null;
		
		mediator.startIteration(currentdepth);
	}
	
	public int getCurrentDepth() {
		return currentdepth;
	}

	public long getLowerBound() {
		return betasGen.getLowerBound();
	}
	
	public long getUpperBound() {
		return betasGen.getUpperBound();
	}
	
	private boolean isLast() {
		//boolean last = betasGen.getLowerBound() + mediator.getTrustWindow_BestMove() >= betasGen.getUpperBound();
		boolean last = betasGen.getLowerBound() + mediator.getTrustWindow_BestMove()
				/*+ (((IRootSearchConfig_SMP)sharedData.getEngineConfiguration()).getThreadsCount() - 1)*/ >= betasGen.getUpperBound();
				
		if (!last) {
			if (betasGen.getLowerBound() >= ISearch.MAX_MATERIAL_INTERVAL
					&& SearchUtils.isMateVal(betasGen.getLowerBound())
					&& (betasGen.getUpperBound() - betasGen.getLowerBound() < ISearch.MAX_MATERIAL_INTERVAL)
				) {
				//Mate found
				last = true;
			}
			
			if (betasGen.getUpperBound() <= -ISearch.MAX_MATERIAL_INTERVAL
					&& SearchUtils.isMateVal(betasGen.getUpperBound())
					&& (betasGen.getUpperBound() - betasGen.getLowerBound() < ISearch.MAX_MATERIAL_INTERVAL)
				) {
				//Mate found
				last = true;
			}
		}
		
		//System.out.println("Last=" + last);
		
		return last;
	}
	
	public String toString() {
		String result = "";
		result += "DISTRIBUTION-> Depth:" + currentdepth + ", Bounds: ["
				+ betasGen.getLowerBound() + " <-> " + betasGen.getUpperBound()
				+ "], BETAS: " + betas;
		return result;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public IBetaGenerator getBetasGen() {
		return betasGen;
	}
}

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.search.api.ISearchConfig_MTD;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.alg.BetaGenerator;
import bagaturchess.search.impl.alg.BetaGeneratorFactory;
import bagaturchess.search.impl.alg.IBetaGenerator;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.evalcache.EvalCache;
import bagaturchess.search.impl.pv.PVHistoryEntry;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.tpt.TPTEntry;
import bagaturchess.search.impl.utils.SearchUtils;


public class SearchManager {
	
	//private static int MTD_INITIAL_STEP = 25;
	
	private ReadWriteLock lock;
	
	private long hashkey;
	
	private volatile int maxIterations;
	private volatile int currentdepth;
	//private volatile int lower_bound;
	//private volatile int upper_bound;
	//private volatile int curIterationEval;
	//private volatile int prevIterationEval;
	//private volatile ISearchInfo curIterationLastInfo;
	//private volatile long nodes;
	
	private IBetaGenerator betasGen;
	//private volatile SortedSet<Integer> betas;
	private volatile List<Integer> betas;
	
	private ISearchMediator mediator;
	private SharedData sharedData;
	private IFinishCallback finishCallback;
	

	public SearchManager(ISearchMediator _mediator, SharedData _sharedData, long _hashkey,
			int _startIteration, int _maxIterations, IFinishCallback _finishCallback) {

		lock = new ReentrantReadWriteLock();
		
		sharedData = _sharedData;
		mediator = _mediator;
		hashkey = _hashkey;
		maxIterations = _maxIterations;
		
		currentdepth = _startIteration;//1;
		
		finishCallback = _finishCallback;
		
		//lower_bound = ISearch.MIN;
		//upper_bound = ISearch.MAX;
		//curIterationEval = ISearch.MIN;
		//prevIterationEval = ISearch.MIN;
		//nodes = 0;
		
		betas = new ArrayList<Integer>();
		
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
		
		int initialVal = 0;
		
		sharedData.getTPT().lock();
		TPTEntry entry = sharedData.getTPT().get(hashkey);
		if (entry != null && entry.getBestMove_lower() != 0) {
			initialVal = entry.getLowerBound();
			//if (sharedData.getEngineConfiguration().getSearchConfig().isOther_UseTPTInRoot()) {
				//prevIterationEval = initialVal;
			//}
		}
		sharedData.getTPT().unlock();
		
		int threadsCount = ((IRootSearchConfig_SMP)sharedData.getEngineConfiguration()).getThreadsCount();
		int min_interval = getMinInterval(threadsCount);
		betasGen = BetaGeneratorFactory.create(initialVal, threadsCount, min_interval);
		
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
	
	private int getMinInterval(int threadsCount) {
		/*
		int[] INTERVALS = new int[] {
				-1, 1, 1, 1, 1, 1, 1, 1, //0-7
				32, 32, 32, 32, 32, 32, 32, 32, //8-15
				32, 32, 32, 32, 32, 32, 32, 32, //16-23
				32, 32, 32, 32, 32, 32, 32, 32, //24-31
				16, 16, 16, 16, 16, 16, 16, 16, //32-39
				16, 16, 16, 16, 16, 16, 16, 16, //40-47
				 8,  8,  8,  8,  8,  8,  8,  8, //48-55
				 8,  8,  8,  8,  8,  8,  8,  8, //56-63
				 4,  4,  4,  4,  4,  4,  4,  4, //64-71
		};
		
		
		if (threadsCount >= INTERVALS.length) {
			threadsCount = INTERVALS.length - 1;
		}
		
		int min_interval = INTERVALS[threadsCount];
		
		mediator.dump("SearchManager: MIN_INTERVAL is " + min_interval);
		*/
		
		return 1;
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

			mediator.dump("Search instability with distribution: " + this);
			
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
			mediator.dump("Betagen obj: " + betasGen);
			updateBetas();
			mediator.dump("The new betas are:" + betas);
					
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
	
	public void increaseLowerBound(int eval, ISearchInfo info, IBitBoard bitboardForTesting) {
		//writeLock();
		//System.out.println("increaseLowerBound eval=" + eval + "		" + MoveInt.movesToString(info.getPV()));
		//System.out.println("increaseLowerBound: " + eval);
		
		if (eval > betasGen.getLowerBound()) {
			
			//if (eval > prevIterationEval) { // Sent pv
				sharedData.getPVs().putPV(hashkey, new PVHistoryEntry(info.getPV(), info.getDepth(), info.getEval()));
				if (mediator != null) {
					//info.setSearchedNodes(nodes);
					mediator.changedMajor(info);
					
					try {
						testPV(info, bitboardForTesting);
					} catch (Exception e) {
						mediator.dump(e);
					}
				}
				//curIterationLastInfo = null;
			//} else {
			//	if (eval > curIterationEval) {
			//		curIterationLastInfo = info;	
			//	}
			//}
			
			//if (eval <= curIterationEval) {
			//	throw new IllegalStateException("eval <= curIterationEval");
			//}
			
			//curIterationEval = eval;
			betasGen.increaseLower(eval);
		}
		
		if (isLast()) {
			finishDepth(bitboardForTesting);
			initBetas();
			if (currentdepth > maxIterations && finishCallback != null) {
				finishCallback.ready();
			}
		} else {
			updateBetas();
		}
		
		//writeUnlock();
	}
	
	public void decreaseUpperBound(int eval, IBitBoard bitboardForTesting) {
		//writeLock();
		
		//System.out.println("decreaseUpperBound eval=" + eval);
		//System.out.println("decreaseUpperBound: " + eval);
		
		if (eval < betasGen.getUpperBound()) {
			betasGen.decreaseUpper(eval);
		}
		
		if (isLast()) {
			finishDepth(bitboardForTesting);
			initBetas();
			if (currentdepth > maxIterations && finishCallback != null) {
				finishCallback.ready();
			}
		} else {
			updateBetas();
		}
		
		//writeUnlock();
	}
	
	
	private void testPV(ISearchInfo info, IBitBoard bitboardForTesting) {
		
		if (true) return;
		
		//if (!sharedData.getEngineConfiguration().verifyPVAfterSearch()) return;
		
		int root_colour = bitboardForTesting.getColourToMove();
		
		int sign = 1;
		
		int[] moves = info.getPV();
		
		for (int i=0; i<moves.length; i++) {
			bitboardForTesting.makeMoveForward(moves[i]);
			sign *= -1;
		}

		IEvaluator evaluator = sharedData.getEvaluatorFactory().create(
				bitboardForTesting,
				new EvalCache(100, true, new BinarySemaphore_Dummy()),
				sharedData.getEngineConfiguration().getEvalConfig());
		
		int curEval = (int) (sign * evaluator.fullEval(0, ISearch.MIN, ISearch.MAX, root_colour));
		
		if (curEval != info.getEval()) {
			mediator.dump("SearchManager.testPV FAILED > curEval=" + curEval + ",	eval=" + info.getEval());
		} else {
			mediator.dump("SearchManager.testPV OK > curEval=" + curEval + ",	eval=" + info.getEval());
		}
		
		for (int i=moves.length - 1; i >= 0; i--) {
			bitboardForTesting.makeMoveBackward(moves[i]);
		}
	}
	
	
	private void finishDepth(IBitBoard bitboardForTesting) {
		
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
		
		mediator.startIteration(currentdepth - 1);
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
		boolean last = betasGen.getLowerBound() + mediator.getTrustWindow_BestMove() >= betasGen.getUpperBound();
		
		if (!last) {
			if (betasGen.getLowerBound() >= ISearch.MAX_MAT_INTERVAL
					&& SearchUtils.isMateVal(betasGen.getLowerBound())
					&& (betasGen.getUpperBound() - betasGen.getLowerBound() < ISearch.MAX_MAT_INTERVAL)
				) {
				//Mate found
				last = true;
			}
			
			if (betasGen.getUpperBound() <= -ISearch.MAX_MAT_INTERVAL
					&& SearchUtils.isMateVal(betasGen.getUpperBound())
					&& (betasGen.getUpperBound() - betasGen.getLowerBound() < ISearch.MAX_MAT_INTERVAL)
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
				+ "], ThreadsCount:" + ((IRootSearchConfig_SMP)sharedData.getEngineConfiguration()).getThreadsCount() + ", BETAS: " + betas;
		return result;
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public IBetaGenerator getBetasGen() {
		return betasGen;
	}
}

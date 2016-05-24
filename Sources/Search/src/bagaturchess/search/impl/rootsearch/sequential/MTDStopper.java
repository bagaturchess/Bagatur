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
package bagaturchess.search.impl.rootsearch.sequential;


import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInterruptedException;


public class MTDStopper implements ISearchStopper {
	
	
	private volatile int colour;
	//private ISearchMediator mediator;
	private SearchManager distribution;
	private boolean stopped;
	
	
	public MTDStopper(int _colour, SearchManager _distribution/*, ISearchMediator _mediator*/) {
		colour = _colour;
		distribution = _distribution;
		//mediator = _mediator;
	}
	
	public void markStopped() {
		stopped = true;
	}
	
	public void setSecondaryStopper(ISearchStopper secondaryStopper) {
		throw new UnsupportedOperationException();
	}
	
	public void stopIfNecessary(int _maxdepth, int _colour, double _alpha, double _beta) throws SearchInterruptedException {
		
		//distribution.writeLock();
		if (_maxdepth > distribution.getMaxIterations()) {
			markStopped();
			//if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
			//distribution.writeUnlock();
			throw new SearchInterruptedException();
		}
		
		if (_maxdepth < distribution.getCurrentDepth()) {
			//dummper.dump(Thread.currentThread().getName() + ": _maxdepth=" + _maxdepth + ", maxdepth=" + distribution.getMaxdepth());
			//distribution.writeUnlock();
			throw new SearchInterruptedException();
		}
		
		if (_colour != colour) {
			double tmp = _alpha;
			_alpha = -_beta;
			_beta = -tmp;
		}
		
		if (distribution.getLowerBound() >= _beta
				|| distribution.getUpperBound() <= _alpha
		) {
			//dummper.dump(Thread.currentThread().getName() + ": lower=" + distribution.getLowerBound() + ", _beta=" + _beta
			//		+ ", upper=" + distribution.getUpperBound() + ", _alpha=" + _alpha);
			//distribution.writeUnlock();
			throw new SearchInterruptedException();
		}
		//distribution.writeUnlock();
	}

	public boolean isStopped() {
		return stopped;
	}
}

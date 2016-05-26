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


import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;


public class GlobalStopperImpl implements ISearchStopper {
	
	
	private ITimeController timeController;
	//private BestMoveSender bestMoveSender;
	private volatile long nodes;
	
	private ISearchStopper secondaryStopper;
	private volatile boolean stopped;
	//private volatile boolean bestMoveSent;
	//private ISearchMediator dumpper;
	
	
	GlobalStopperImpl(ITimeController _timeController, long _nodes) {
		timeController = _timeController;
		//bestMoveSender = _bestMoveSender;
		nodes = _nodes;
		//dumpper = _dumpper;
		//dumpper.dump("Creation: " + timeController);
	}
	
	public synchronized void markStopped() {
		stopped = true;
		//dumpper.dump("Marked stopped");
	}
	
	public synchronized void setSecondaryStopper(ISearchStopper _secondaryStopper) {
		secondaryStopper = _secondaryStopper;
	}
	
	public void stopIfNecessary(int maxdepth, int colour, double alpha, double beta) throws SearchInterruptedException {
		
		if (maxdepth <= 1) {
			return;
		}
		
		if (stopped) {
			//dumpper.dump("Stopping: marked as stopped");
			throw new SearchInterruptedException();
		}
		
		nodes--;
		if (nodes <= 0) {
			synchronized (this) {
				markStopped();
				//bestMoveSender.sendBestMove();
			}
			throw new SearchInterruptedException();
		}
		
		if (!timeController.hasTime(0)) {
			synchronized (this) {
				markStopped();
				//bestMoveSender.sendBestMove();
			}
			throw new SearchInterruptedException();
		}
		
		if (secondaryStopper != null) {
			try {
				secondaryStopper.stopIfNecessary(maxdepth, colour, alpha, beta);
			} catch(SearchInterruptedException sie) {
				//dumpper.dump("secondary stopper: " + timeController);
				//dumpper.dump("Stopping: secondaryStopper");
				throw sie;
			}
		}
	}
	
	public synchronized boolean isStopped() {
		if (stopped) return true; 
		if (secondaryStopper != null) secondaryStopper.isStopped();
		return false;
	}
	
}

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
	private long nodes;
	
	private boolean stopped;
	
	
	GlobalStopperImpl(ITimeController _timeController, long _nodes) {
		timeController = _timeController;
		nodes = _nodes;
	}
	
	public void markStopped() {
		stopped = true;
	}
	
	
	public void stopIfNecessary(int maxdepth, int colour, double alpha, double beta) throws SearchInterruptedException {
		
		if (maxdepth <= 1) {
			//Do nothing
			return;
		}
		
		
		if (stopped) {
			
			throw new SearchInterruptedException();
		}
		
		
		nodes--;
		
		if (nodes <= 0) {
			
			markStopped();
			
			throw new SearchInterruptedException();
		}
		
		
		if (!timeController.hasTime()) {
			
			markStopped();
			
			throw new SearchInterruptedException();
		}
	}
	
	
	public boolean isStopped() {
		
		return stopped; 
	}
}

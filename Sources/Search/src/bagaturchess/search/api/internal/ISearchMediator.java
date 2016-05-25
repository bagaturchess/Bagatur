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
package bagaturchess.search.api.internal;

import bagaturchess.uci.api.BestMoveSender;


public interface ISearchMediator {
	
	public ISearchStopper getStopper();
	public void setStopper(ISearchStopper stopper);
	
	public ISearchInfo getLastInfo();
	
	public void registerInfoObject(ISearchInfo info);
	public void changedMajor(ISearchInfo info);
	public void changedMinor(ISearchInfo info);
	
	public void send(String msg);
	public void dump(String msg);
	public void dump(Throwable t);
	
	public BestMoveSender getBestMoveSender();
	
	public void startIteration(int iteration);
	
	public int getTrustWindow_BestMove();
	public int getTrustWindow_AlphaAspiration();
	public int getTrustWindow_MTD_Step();
}

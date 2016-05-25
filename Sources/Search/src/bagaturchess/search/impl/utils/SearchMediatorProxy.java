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
package bagaturchess.search.impl.utils;


import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.uci.api.BestMoveSender;


public class SearchMediatorProxy implements ISearchMediator {
	
	
	protected ISearchMediator parent;
	

	public SearchMediatorProxy(ISearchMediator _parent) {
		parent = _parent;
	}
	
	public ISearchMediator getParent() {
		return parent;
	}
	
	public void setParent(ISearchMediator _parent) {
		parent = _parent;
	}

	public void changedMajor(ISearchInfo info) {
		parent.changedMajor(info);
	}

	public void changedMinor(ISearchInfo info) {
		parent.changedMinor(info);
	}

	public void dump(String msg) {
		parent.dump(msg);		
	}

	public void dump(Throwable t) {
		parent.dump(t);
	}

	public BestMoveSender getBestMoveSender() {
		return parent.getBestMoveSender();
	}

	public ISearchInfo getLastInfo() {
		return parent.getLastInfo();
	}

	public ISearchStopper getStopper() {
		return parent.getStopper();
	}

	@Override
	public void setStopper(ISearchStopper stopper) {
		parent.setStopper(stopper);
	}
	
	public void startIteration(int iteration) {
		parent.startIteration(iteration);
	}

	@Override
	public void send(String msg) {
		parent.send(msg);
	}

	@Override
	public void registerInfoObject(ISearchInfo info) {
		parent.registerInfoObject(info);
	}

	@Override
	public int getTrustWindow_BestMove() {
		return parent.getTrustWindow_BestMove();
	}

	@Override
	public int getTrustWindow_AlphaAspiration() {
		return parent.getTrustWindow_AlphaAspiration();
	}

	@Override
	public int getTrustWindow_MTD_Step() {
		return parent.getTrustWindow_MTD_Step();
	}
}

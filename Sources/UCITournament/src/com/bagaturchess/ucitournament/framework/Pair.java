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
package com.bagaturchess.ucitournament.framework;


import bagaturchess.ucitracker.impl.Engine;


public class Pair {
	
	
	private Engine whiteEngine;
	private Engine blackEngine;
	
	
	public Pair(Engine _whiteEngine, Engine _blackEngine) {
		whiteEngine = _whiteEngine;
		blackEngine = _blackEngine;
	}
	
	
	public Engine getBlackEngine() {
		return blackEngine;
	}
	
	public Engine getWhiteEngine() {
		return whiteEngine;
	}
	
	@Override
	public int hashCode() {
		return getWhiteEngine().getName().hashCode() + getBlackEngine().getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		Pair other = (Pair)obj;
		return other.getWhiteEngine().getName().equals(getWhiteEngine().getName())
				&& other.getBlackEngine().getName().equals(getBlackEngine().getName());
	}
	
	@Override
	public String toString() {
		String msg = "";
		
		msg += "[" + whiteEngine.getName() + " vs. " + blackEngine.getName() + "]";
		
		return msg;
	}
}

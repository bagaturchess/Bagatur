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
package bagaturchess.ucitracker.impl.gamemodel;


import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import bagaturchess.bitboard.impl.movegen.MoveInt;


public class EvaluatedPosition implements Serializable {
	
	private static final long serialVersionUID = -714092980265258749L;
	
	private int originateMove;
	private transient String epd;
	private Set<EvaluatedMove> children;
	
	public EvaluatedPosition(String _epd, int _originateMove) {
		epd = _epd;
		originateMove = _originateMove;
	}
	
	public void setChildren(Set<EvaluatedMove> _children) {
		children = _children;
	}

	public Set<EvaluatedMove> getChildren() {
		return children;
	}

	public String getEpd() {
		return epd;
	}

	public int getOriginateMove() {
		return originateMove;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		result += "Originator: " + MoveInt.moveToString(originateMove) + " EPD: " + epd + "\r\n";
		
		Iterator<EvaluatedMove> iter = children.iterator();
		while (iter.hasNext()) {
			result += iter.next() + "\r\n";
		}
		
		return result;
	}
}

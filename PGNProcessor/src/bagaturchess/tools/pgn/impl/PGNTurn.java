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


package bagaturchess.tools.pgn.impl;


public class PGNTurn {
	
	private long id;
	
	private int moveNumber = -1;
	
	private String whitePly = null;
	private String blackPly = null;
	
	public PGNTurn() {
	
	}
	
	public PGNTurn(int aNumber, String _whiteTurnStr, String _blackTurnStr) {
		//id = PGNPersistency.getTurnSeq();
		moveNumber = aNumber;
		whitePly = _whiteTurnStr;
		blackPly = _blackTurnStr;
	}
	
	public String toString() {
		return moveNumber + ". " +
			whitePly +
			( ( blackPly == null ) ? "" : " " + blackPly );
	}
	
	public String getBlackPly() {
		return blackPly;
	}
	
	public void setBlackPly(String blackPly) {
		this.blackPly = blackPly;
	}
	
	public int getMoveNumber() {
		return moveNumber;
	}
	
	public void setMoveNumber(int moveNumber) {
		this.moveNumber = moveNumber;
	}
	
	public String getWhitePly() {
		return whitePly;
	}
	
	public void setWhitePly(String whitePly) {
		this.whitePly = whitePly;
	}
}

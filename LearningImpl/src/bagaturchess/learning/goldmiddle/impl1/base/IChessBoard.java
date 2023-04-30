/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.learning.goldmiddle.impl1.base;

import bagaturchess.bitboard.api.IBitBoard;


public interface IChessBoard {
	
	public int getColorToMove();
	
	public int getPSQTScore_o();
	public int getPSQTScore_e();
	
	public int getKingIndex(int colour);
	public long getKingArea(int colour);
	
	public long getPieces(int colour, int type);
	public long getAllPieces();
	public long getFriendlyPieces(int colour);
	public long getEmptySpaces();
	
	public long getPinnedPieces();
	public long getDiscoveredPieces();
	public long getCheckingPieces();
	
	public int getPieceType(int index);
	
	public IBitBoard getBoard();
}

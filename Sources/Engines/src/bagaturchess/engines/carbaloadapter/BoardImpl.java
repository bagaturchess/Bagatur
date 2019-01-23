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
package bagaturchess.engines.carbaloadapter;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Constants;


public class BoardImpl implements IBoard {
	
	
	private IBitBoard board;
	
	
	public BoardImpl(IBitBoard _board) {
		board = _board;
	}
	
	
	private static final long convertBB(long bb) {
		//return bb;
		return Bits.reverse(bb);
	}
	
	
	@Override
	public int getColourToMove() {
		return board.getColourToMove() == Constants.COLOUR_WHITE ? 0 : 1;
	}
	
	
	@Override
	public long getPawns() {
		return convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN) | board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN));
	}
	
	
	@Override
	public long getKnights() {
		return convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT) | board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT));
	}
	
	
	@Override
	public long getBishops() {
		return convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP) | board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP));
	}
	
	
	@Override
	public long getRooks() {
		return convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_ROOK) | board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_ROOK));
	}
	
	
	@Override
	public long getQueens() {
		return convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_QUEEN) | board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_QUEEN));
	}
	
	
	@Override
	public long getKings() {
		return convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KING) | board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KING));
	}
	
	
	@Override
	public long getWhites() {
		return convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_WHITE));
	}
	
	
	@Override
	public long getBlacks() {
		return convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_BLACK));
	}
	
	
	@Override
	public long getAll() {
		return convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_WHITE) | board.getFiguresBitboardByColour(Constants.COLOUR_BLACK));
	}
}

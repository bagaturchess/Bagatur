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
package bagaturchess.learning.goldmiddle.impl8.filler;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.impl7.base.EvalInfo;


public class Bagatur_PST_SignalFiller implements ISignalFiller, Bagatur_PST_FeaturesConstants {
	
	
	private final IBitBoard bitboard;
	
	private final ChessBoard board;
	
	private final EvalInfo evalInfo;
	
	
	public Bagatur_PST_SignalFiller(IBitBoard _bitboard) {
		
		bitboard = _bitboard;
		
		board = ((BoardImpl)bitboard).getChessBoard();
		
		evalInfo = new EvalInfo();
	}
	
	
	@Override
	public void fill(ISignals signals) {
		
		
		evalInfo.clearEvals();
		
		evalInfo.fillBoardInfo(board);
		
		
		//Pawns
		long piece = evalInfo.getPieces(WHITE, PAWN);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_PAWN).addStrength(square_index, 1, 0);
			
			piece &= piece - 1;
		}
		
		
		piece = evalInfo.getPieces(BLACK, PAWN);
		
		while (piece != 0) {
			
			final int square_index = EvalConstants.MIRRORED_UP_DOWN[Long.numberOfTrailingZeros(piece)];
			
			signals.getSignal(FEATURE_ID_PST_PAWN).addStrength(square_index, -1, 0);
			
			piece &= piece - 1;
		}
		
		
		//Kings
		piece = evalInfo.getPieces(WHITE, KING);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_KING).addStrength(square_index, 1, 0);
			
			piece &= piece - 1;
		}
		
		
		piece = evalInfo.getPieces(BLACK, KING);
		
		while (piece != 0) {
			
			final int square_index = EvalConstants.MIRRORED_UP_DOWN[Long.numberOfTrailingZeros(piece)];
			
			signals.getSignal(FEATURE_ID_PST_KING).addStrength(square_index, -1, 0);
			
			piece &= piece - 1;
		}
		
		
		//Queens
		piece = evalInfo.getPieces(WHITE, QUEEN);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_QUEEN).addStrength(square_index, 1, 0);
			
			piece &= piece - 1;
		}
		
		
		piece = evalInfo.getPieces(BLACK, QUEEN);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_QUEEN).addStrength(square_index, -1, 0);
			
			piece &= piece - 1;
		}
		
		
		//Rooks
		piece = evalInfo.getPieces(WHITE, ROOK);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_ROOK).addStrength(square_index, 1, 0);
			
			piece &= piece - 1;
		}
		
		
		piece = evalInfo.getPieces(BLACK, ROOK);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_ROOK).addStrength(square_index, -1, 0);
			
			piece &= piece - 1;
		}
		
		
		//Bishops
		piece = evalInfo.getPieces(WHITE, BISHOP);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_BISHOP).addStrength(square_index, 1, 0);
			
			piece &= piece - 1;
		}
		
		
		piece = evalInfo.getPieces(BLACK, BISHOP);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_BISHOP).addStrength(square_index, -1, 0);
			
			piece &= piece - 1;
		}
		
		
		//Knights
		piece = evalInfo.getPieces(WHITE, NIGHT);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_KNIGHT).addStrength(square_index, 1, 0);
			
			piece &= piece - 1;
		}
		
		
		piece = evalInfo.getPieces(BLACK, NIGHT);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			signals.getSignal(FEATURE_ID_PST_KNIGHT).addStrength(square_index, -1, 0);
			
			piece &= piece - 1;
		}
	}
	
	
	@Override
	public void fillByComplexity(int complexity, ISignals signals) {

		throw new UnsupportedOperationException();
	}
}

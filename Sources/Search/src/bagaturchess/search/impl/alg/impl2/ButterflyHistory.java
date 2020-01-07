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
package bagaturchess.search.impl.alg.impl2;


/// ButterflyHistory records how often quiet moves have been successful or
/// unsuccessful during the current search, and is used for reduction and move
/// ordering decisions. It uses 2 tables (one for each color) indexed by
/// the move's from and to squares, see chessprogramming.wikispaces.com/Butterfly+Boards
//typedef Stats<int16_t, 10692, COLOR_NB, int(SQUARE_NB) * int(SQUARE_NB)> ButterflyHistory;
/*
enum PieceType {
NO_PIECE_TYPE, PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING,
ALL_PIECES = 0,
PIECE_TYPE_NB = 8
};

enum Piece {
NO_PIECE,
W_PAWN = 1, W_KNIGHT, W_BISHOP, W_ROOK, W_QUEEN, W_KING,
B_PAWN = 9, B_KNIGHT, B_BISHOP, B_ROOK, B_QUEEN, B_KING,
PIECE_NB = 16
};*/
public class ButterflyHistory {
	
	int[][] array = new int[2][64*64];
	
}

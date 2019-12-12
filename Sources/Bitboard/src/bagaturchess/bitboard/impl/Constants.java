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
package bagaturchess.bitboard.impl;

public class Constants {

	public static final String INITIAL_BOARD = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	/**
	 * Pieces colours
	 */
	public static final int COLOUR_WHITE = 0;
	public static final int COLOUR_BLACK = 1;
	public static final int[] COLOUR_OP = new int[2];
	static {
		//COLOUR_OP[0] = -1;
		COLOUR_OP[COLOUR_WHITE] = COLOUR_BLACK;
		COLOUR_OP[COLOUR_BLACK] = COLOUR_WHITE;
	}
	
	/**
	 * Pieces types
	 */
	public static final int TYPE_NONE = 0;
	public static final int TYPE_PAWN = 1;
	public static final int TYPE_KNIGHT = 2;
	public static final int TYPE_BISHOP = 3;
	public static final int TYPE_ROOK = 4;
	public static final int TYPE_QUEEN = 5;
	public static final int TYPE_KING = 6;
	public static final int TYPE_ALL = 7;
	
	/**
	 * Pieces colours and types
	 */
	public static final int PID_NONE = 0;
	
	public static final int PID_W_PAWN = 1;
	public static final int PID_W_KNIGHT = 2;
	public static final int PID_W_BISHOP = 3;
	public static final int PID_W_ROOK = 4;
	public static final int PID_W_QUEEN = 5;
	public static final int PID_W_KING = 6;
	
	public static final int PID_B_PAWN = 7;
	public static final int PID_B_KNIGHT = 8;
	public static final int PID_B_BISHOP = 9;
	public static final int PID_B_ROOK = 10;
	public static final int PID_B_QUEEN = 11;
	public static final int PID_B_KING = 12;
	
	public static final int PID_MAX = 13;
	
	public static final boolean isWhite(int pid) {
		return pid > PID_NONE && pid < PID_B_PAWN;
	}
	
	public static final boolean isBlack(int pid) {
		return pid >= PID_B_PAWN;
	}
	
	public static final boolean hasSameColour(int pid1, int pid2) {
		if (pid1 == 0 || pid2 == 0) {
			throw new IllegalStateException();
		}
		return isWhite(pid1) == isWhite(pid2);
	}
	
	public static final boolean hasDiffColour(int pid1, int pid2) {
		if (pid1 == 0 || pid2 == 0) {
			throw new IllegalStateException();
		}
		return (isWhite(pid1) == isBlack(pid2)) || (isWhite(pid2) == isBlack(pid1));
	}
	
	/**
	 * Pieces identity to type and colour bidirectional mapping
	 */
	public static final int[] PIECE_IDENTITY_2_TYPE = new int[13];
	static {
		PIECE_IDENTITY_2_TYPE[PID_NONE] = TYPE_NONE;
		
		PIECE_IDENTITY_2_TYPE[PID_W_PAWN] = TYPE_PAWN;
		PIECE_IDENTITY_2_TYPE[PID_W_KNIGHT] = TYPE_KNIGHT;
		PIECE_IDENTITY_2_TYPE[PID_W_BISHOP] = TYPE_BISHOP;
		PIECE_IDENTITY_2_TYPE[PID_W_ROOK] = TYPE_ROOK;
		PIECE_IDENTITY_2_TYPE[PID_W_QUEEN] = TYPE_QUEEN;
		PIECE_IDENTITY_2_TYPE[PID_W_KING] = TYPE_KING;
		
		PIECE_IDENTITY_2_TYPE[PID_B_PAWN] = TYPE_PAWN;
		PIECE_IDENTITY_2_TYPE[PID_B_KNIGHT] = TYPE_KNIGHT;
		PIECE_IDENTITY_2_TYPE[PID_B_BISHOP] = TYPE_BISHOP;
		PIECE_IDENTITY_2_TYPE[PID_B_ROOK] = TYPE_ROOK;
		PIECE_IDENTITY_2_TYPE[PID_B_QUEEN] = TYPE_QUEEN;
		PIECE_IDENTITY_2_TYPE[PID_B_KING] = TYPE_KING;
	}
	
	public static final int[][] COLOUR_AND_TYPE_2_PIECE_IDENTITY = new int[3][7];
	static {
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][TYPE_PAWN] = -1;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][TYPE_KNIGHT] = -1;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][TYPE_BISHOP] = -1;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][TYPE_ROOK] = -1;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][TYPE_QUEEN] = -1;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][TYPE_KING] = -1;
		
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_WHITE][0] = -1;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_WHITE][TYPE_PAWN] = PID_W_PAWN;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_WHITE][TYPE_KNIGHT] = PID_W_KNIGHT;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_WHITE][TYPE_BISHOP] = PID_W_BISHOP;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_WHITE][TYPE_ROOK] = PID_W_ROOK;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_WHITE][TYPE_QUEEN] = PID_W_QUEEN;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_WHITE][TYPE_KING] = PID_W_KING;
		
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_BLACK][0] = -1;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_BLACK][TYPE_PAWN] = PID_B_PAWN;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_BLACK][TYPE_KNIGHT] = PID_B_KNIGHT;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_BLACK][TYPE_BISHOP] = PID_B_BISHOP;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_BLACK][TYPE_ROOK] = PID_B_ROOK;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_BLACK][TYPE_QUEEN] = PID_B_QUEEN;
		COLOUR_AND_TYPE_2_PIECE_IDENTITY[COLOUR_BLACK][TYPE_KING] = PID_B_KING;
	}
	
	public static final String[] PIECE_IDENTITY_2_SIGN = new String[13];
	static {
		PIECE_IDENTITY_2_SIGN[0] = "X";
		
		PIECE_IDENTITY_2_SIGN[PID_W_PAWN] = "P";
		PIECE_IDENTITY_2_SIGN[PID_W_KNIGHT] = "N";
		PIECE_IDENTITY_2_SIGN[PID_W_BISHOP] = "B";
		PIECE_IDENTITY_2_SIGN[PID_W_ROOK] = "R";
		PIECE_IDENTITY_2_SIGN[PID_W_QUEEN] = "Q";
		PIECE_IDENTITY_2_SIGN[PID_W_KING] = "K";
		
		PIECE_IDENTITY_2_SIGN[PID_B_PAWN] = "p";
		PIECE_IDENTITY_2_SIGN[PID_B_KNIGHT] = "n";
		PIECE_IDENTITY_2_SIGN[PID_B_BISHOP] = "b";
		PIECE_IDENTITY_2_SIGN[PID_B_ROOK] = "r";
		PIECE_IDENTITY_2_SIGN[PID_B_QUEEN] = "q";
		PIECE_IDENTITY_2_SIGN[PID_B_KING] = "k";
	}
	
	public static final int getColourByPieceIdentity(int id) {
		if (id >= 7) {
			return COLOUR_BLACK;
		} else if (id >= 1) {
			return COLOUR_WHITE;
		} else {
			throw new IllegalStateException("piece id " + id);
		}
	}

	
	/**
	 * Square bitboards
	 */
	/*public static final long A8_bit = 1L << 63;
	public static final long B8_bit = 1L << 62;
	public static final long C8_bit = 1L << 61;
	public static final long D8_bit = 1L << 60;
	public static final long E8_bit = 1L << 59;
	public static final long F8_bit = 1L << 58;
	public static final long G8_bit = 1L << 57;
	public static final long H8_bit = 1L << 56;
	
	public static final long A7_bit = 1L << 55;
	public static final long B7_bit = 1L << 54;
	public static final long C7_bit = 1L << 53;
	public static final long D7_bit = 1L << 52;
	public static final long E7_bit = 1L << 51;
	public static final long F7_bit = 1L << 50;
	public static final long G7_bit = 1L << 49;
	public static final long H7_bit = 1L << 48;
	
	public static final long A6_bit = 1L << 47;
	public static final long B6_bit = 1L << 46;
	public static final long C6_bit = 1L << 45;
	public static final long D6_bit = 1L << 44;
	public static final long E6_bit = 1L << 43;
	public static final long F6_bit = 1L << 42;
	public static final long G6_bit = 1L << 41;
	public static final long H6_bit = 1L << 40;
	
	public static final long A5_bit = 1L << 39;
	public static final long B5_bit = 1L << 38;
	public static final long C5_bit = 1L << 37;
	public static final long D5_bit = 1L << 36;
	public static final long E5_bit = 1L << 35;
	public static final long F5_bit = 1L << 34;
	public static final long G5_bit = 1L << 33;
	public static final long H5_bit = 1L << 32;
	
	public static final long A4_bit  = 1L << 31;
	public static final long B4_bit = 1L << 30;
	public static final long C4_bit = 1L << 29;
	public static final long D4_bit = 1L << 28;
	public static final long E4_bit = 1L << 27;
	public static final long F4_bit = 1L << 26;
	public static final long G4_bit = 1L << 25;
	public static final long H4_bit = 1L << 24;
	
	public static final long A3_bit = 1L << 23;
	public static final long B3_bit = 1L << 22;
	public static final long C3_bit = 1L << 21;
	public static final long D3_bit = 1L << 20;
	public static final long E3_bit = 1L << 19;
	public static final long F3_bit = 1L << 18;
	public static final long G3_bit = 1L << 17;
	public static final long H3_bit = 1L << 16;
	
	public static final long A2_bit = 1L << 15;
	public static final long B2_bit = 1L << 14;
	public static final long C2_bit = 1L << 13;
	public static final long D2_bit = 1L << 12;
	public static final long E2_bit = 1L << 11;
	public static final long F2_bit = 1L << 10;
	public static final long G2_bit = 1L << 9;
	public static final long H2_bit = 1L << 8;
	
	public static final long A1_bit = 1L << 7;
	public static final long B1_bit = 1L << 6;
	public static final long C1_bit = 1L << 5;
	public static final long D1_bit = 1L << 4;
	public static final long E1_bit = 1L << 3;
	public static final long F1_bit = 1L << 2;
	public static final long G1_bit = 1L << 1;
	public static final long H1_bit = 1L << 0;
	 */

	/**
	 * Square indexes
	 */
	
	/*public static final int A8 = 0;
	public static final int B8 = 1;
	public static final int C8 = 2;
	public static final int D8 = 3;
	public static final int E8 = 4;
	public static final int F8 = 5;
	public static final int G8 = 6;
	public static final int H8 = 7;
	
	public static final int A7 = 16;
	public static final int B7 = 17;
	public static final int C7 = 18;
	public static final int D7 = 19;
	public static final int E7 = 20;
	public static final int F7 = 21;
	public static final int G7 = 22;
	public static final int H7 = 23;
	
	public static final int A6 = 32;
	public static final int B6 = 33;
	public static final int C6 = 34;
	public static final int D6 = 35;
	public static final int E6 = 36;
	public static final int F6 = 37;
	public static final int G6 = 38;
	public static final int H6 = 39;
	
	public static final int A5 = 48;
	public static final int B5 = 49;
	public static final int C5 = 50;
	public static final int D5 = 51;
	public static final int E5 = 52;
	public static final int F5 = 53;
	public static final int G5 = 54;
	public static final int H5 = 55;
	
	public static final int A4 = 64;
	public static final int B4 = 65;
	public static final int C4 = 66;
	public static final int D4 = 67;
	public static final int E4 = 68;
	public static final int F4 = 69;
	public static final int G4 = 70;
	public static final int H4 = 71;
	
	public static final int A3 = 80;
	public static final int B3 = 81;
	public static final int C3 = 82;
	public static final int D3 = 83;
	public static final int E3 = 84;
	public static final int F3 = 85;
	public static final int G3 = 86;
	public static final int H3 = 87;
	
	public static final int A2 = 96;
	public static final int B2 = 97;
	public static final int C2 = 98;
	public static final int D2 = 99;
	public static final int E2 = 100;
	public static final int F2 = 101;
	public static final int G2 = 102;
	public static final int H2 = 103;
	
	public static final int A1 = 112;
	public static final int B1 = 113;
	public static final int C1 = 114;
	public static final int D1 = 115;
	public static final int E1 = 116;
	public static final int F1 = 117;
	public static final int G1 = 118;
	public static final int H1 = 119;
	*/
	
	/**
	 * Squares bitboards by square index
	 */
	/*public static final long[] SQUARES_bits = new long[128];
	static {
		SQUARES_bits[A8] = A8_bit; 
		SQUARES_bits[B8] = B8_bit;
		SQUARES_bits[C8] = C8_bit;
		SQUARES_bits[D8] = D8_bit;
		SQUARES_bits[E8] = E8_bit;
		SQUARES_bits[F8] = F8_bit;
		SQUARES_bits[G8] = G8_bit;
		SQUARES_bits[H8] = H8_bit;
		
		SQUARES_bits[A7] = A7_bit; 
		SQUARES_bits[B7] = B7_bit;
		SQUARES_bits[C7] = C7_bit;
		SQUARES_bits[D7] = D7_bit;
		SQUARES_bits[E7] = E7_bit;
		SQUARES_bits[F7] = F7_bit;
		SQUARES_bits[G7] = G7_bit;
		SQUARES_bits[H7] = H7_bit;
		
		SQUARES_bits[A6] = A6_bit; 
		SQUARES_bits[B6] = B6_bit;
		SQUARES_bits[C6] = C6_bit;
		SQUARES_bits[D6] = D6_bit;
		SQUARES_bits[E6] = E6_bit;
		SQUARES_bits[F6] = F6_bit;
		SQUARES_bits[G6] = G6_bit;
		SQUARES_bits[H6] = H6_bit;
		
		SQUARES_bits[A5] = A5_bit; 
		SQUARES_bits[B5] = B5_bit;
		SQUARES_bits[C5] = C5_bit;
		SQUARES_bits[D5] = D5_bit;
		SQUARES_bits[E5] = E5_bit;
		SQUARES_bits[F5] = F5_bit;
		SQUARES_bits[G5] = G5_bit;
		SQUARES_bits[H5] = H5_bit;
		
		SQUARES_bits[A4] = A4_bit; 
		SQUARES_bits[B4] = B4_bit;
		SQUARES_bits[C4] = C4_bit;
		SQUARES_bits[D4] = D4_bit;
		SQUARES_bits[E4] = E4_bit;
		SQUARES_bits[F4] = F4_bit;
		SQUARES_bits[G4] = G4_bit;
		SQUARES_bits[H4] = H4_bit;
		
		SQUARES_bits[A3] = A3_bit; 
		SQUARES_bits[B3] = B3_bit;
		SQUARES_bits[C3] = C3_bit;
		SQUARES_bits[D3] = D3_bit;
		SQUARES_bits[E3] = E3_bit;
		SQUARES_bits[F3] = F3_bit;
		SQUARES_bits[G3] = G3_bit;
		SQUARES_bits[H3] = H3_bit;
		
		SQUARES_bits[A2] = A2_bit; 
		SQUARES_bits[B2] = B2_bit;
		SQUARES_bits[C2] = C2_bit;
		SQUARES_bits[D2] = D2_bit;
		SQUARES_bits[E2] = E2_bit;
		SQUARES_bits[F2] = F2_bit;
		SQUARES_bits[G2] = G2_bit;
		SQUARES_bits[H2] = H2_bit;
		
		SQUARES_bits[A1] = A1_bit; 
		SQUARES_bits[B1] = B1_bit;
		SQUARES_bits[C1] = C1_bit;
		SQUARES_bits[D1] = D1_bit;
		SQUARES_bits[E1] = E1_bit;
		SQUARES_bits[F1] = F1_bit;
		SQUARES_bits[G1] = G1_bit;
		SQUARES_bits[H1] = H1_bit;
	}
	*/
	
	/*public static final long getSuareBitboard_arr(int suqareIndex) {
		return SQUARES_bits[suqareIndex];
	}*/
	
	/*public static final long getSuareBitboard_switch(int suqareIndex) {
		
		switch(suqareIndex) {
		
			//Rank 8
			case A8:
				return A8_bit;
			case B8:
				return B8_bit;
			case C8:
				return C8_bit;
			case D8:
				return D8_bit;
			case E8:
				return E8_bit;
			case F8:
				return F8_bit;
			case G8:
				return G8_bit;
			case H8:
				return H8_bit;
				
			//Rank 7
			case A7:
				return A7_bit;
			case B7:
				return B7_bit;
			case C7:
				return C7_bit;
			case D7:
				return D7_bit;
			case E7:
				return E7_bit;
			case F7:
				return F7_bit;
			case G7:
				return G7_bit;
			case H7:
				return H7_bit;
				
			//Rank 6
			case A6:
				return A6_bit;
			case B6:
				return B6_bit;
			case C6:
				return C6_bit;
			case D6:
				return D6_bit;
			case E6:
				return E6_bit;
			case F6:
				return F6_bit;
			case G6:
				return G6_bit;
			case H6:
				return H6_bit;
			
			//Rank 5
			case A5:
				return A5_bit;
			case B5:
				return B5_bit;
			case C5:
				return C5_bit;
			case D5:
				return D5_bit;
			case E5:
				return E5_bit;
			case F5:
				return F5_bit;
			case G5:
				return G5_bit;
			case H5:
				return H5_bit;
				
			//Rank 4
			case A4:
				return A4_bit;
			case B4:
				return B4_bit;
			case C4:
				return C4_bit;
			case D4:
				return D4_bit;
			case E4:
				return E4_bit;
			case F4:
				return F4_bit;
			case G4:
				return G4_bit;
			case H4:
				return H4_bit;
			
			//Rank 3
			case A3:
				return A3_bit;
			case B3:
				return B3_bit;
			case C3:
				return C3_bit;
			case D3:
				return D3_bit;
			case E3:
				return E3_bit;
			case F3:
				return F3_bit;
			case G3:
				return G3_bit;
			case H3:
				return H3_bit;
			
			//Rank 2
			case A2:
				return A2_bit;
			case B2:
				return B2_bit;
			case C2:
				return C2_bit;
			case D2:
				return D2_bit;
			case E2:
				return E2_bit;
			case F2:
				return F2_bit;
			case G2:
				return G2_bit;
			case H2:
				return H2_bit;

			//Rank 1
			case A1:
				return A1_bit;
			case B1:
				return B1_bit;
			case C1:
				return C1_bit;
			case D1:
				return D1_bit;
			case E1:
				return E1_bit;
			case F1:
				return F1_bit;
			case G1:
				return G1_bit;
			case H1:
				return H1_bit;

			default:
				throw new IllegalStateException();
		}
	}
	*/
	
	
	/*public static final String getSuareSign(int suqareIndex) {
		
		switch(suqareIndex) {
		
			//Rank 8
			case A8:
				return "a8";
			case B8:
				return "b8";
			case C8:
				return "c8";
			case D8:
				return "d8";
			case E8:
				return "e8";
			case F8:
				return "f8";
			case G8:
				return "g8";
			case H8:
				return "h8";
				
			//Rank 7
			case A7:
				return "a7";
			case B7:
				return "b7";
			case C7:
				return "c7";
			case D7:
				return "d7";
			case E7:
				return "e7";
			case F7:
				return "f7";
			case G7:
				return "g7";
			case H7:
				return "h7";
				
			//Rank 6
			case A6:
				return "a6";
			case B6:
				return "b6";
			case C6:
				return "c6";
			case D6:
				return "d6";
			case E6:
				return "e6";
			case F6:
				return "f6";
			case G6:
				return "g6";
			case H6:
				return "h6";
			
			//Rank 5
			case A5:
				return "a5";
			case B5:
				return "b5";
			case C5:
				return "c5";
			case D5:
				return "d5";
			case E5:
				return "e5";
			case F5:
				return "f5";
			case G5:
				return "g5";
			case H5:
				return "h5";
				
			//Rank 4
			case A4:
				return "a4";
			case B4:
				return "b4";
			case C4:
				return "c4";
			case D4:
				return "d4";
			case E4:
				return "e4";
			case F4:
				return "f4";
			case G4:
				return "g4";
			case H4:
				return "h4";
			
			//Rank 3
			case A3:
				return "a3";
			case B3:
				return "b3";
			case C3:
				return "c3";
			case D3:
				return "d3";
			case E3:
				return "e3";
			case F3:
				return "f3";
			case G3:
				return "g3";
			case H3:
				return "h3";
			
			//Rank 2
			case A2:
				return "a2";
			case B2:
				return "b2";
			case C2:
				return "c2";
			case D2:
				return "d2";
			case E2:
				return "e2";
			case F2:
				return "f2";
			case G2:
				return "g2";
			case H2:
				return "h2";

			//Rank 1
			case A1:
				return "a1";
			case B1:
				return "b1";
			case C1:
				return "c1";
			case D1:
				return "d1";
			case E1:
				return "e1";
			case F1:
				return "f1";
			case G1:
				return "g1";
			case H1:
				return "h1";

			default:
				throw new IllegalStateException();
		}
	}
	*/
	
	public static final String getPieceIDString(int pieceID) {
		switch(pieceID) {
			case PID_W_PAWN:
				return "P";
			case PID_W_KNIGHT:
				return "N";
			case PID_W_BISHOP:
				return "B";
			case PID_W_ROOK:
				return "R";
			case PID_W_QUEEN:
				return "Q";
			case PID_W_KING:
				return "K";
			case PID_B_PAWN:
				return "p";
			case PID_B_KNIGHT:
				return "n";
			case PID_B_BISHOP:
				return "b";
			case PID_B_ROOK:
				return "r";
			case PID_B_QUEEN:
				return "q";
			case PID_B_KING:
				return "k";
			default:
				return "_";
		}
	}
	
	public static final String colourToString(int colour) {
		if (colour == COLOUR_WHITE) {
			return "White";
		} else if (colour == COLOUR_BLACK) {
			return "Black";
		} else {
			throw new IllegalStateException("colour=" + colour);
		}
	}
}

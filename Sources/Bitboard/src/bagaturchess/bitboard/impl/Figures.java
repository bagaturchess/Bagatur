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


public class Figures extends Fields {
	
	public static final byte COLOUR_WHITE = Constants.COLOUR_WHITE;
	public static final byte COLOUR_BLACK = Constants.COLOUR_BLACK;
	public static final byte COLOUR_UNSPECIFIED = -1;
	
	public static final byte COLOUR_MAX = (byte) (Math.max(COLOUR_WHITE, COLOUR_BLACK) + 1);
	
	public static final byte[] OPPONENT_COLOUR = new byte[3];
	static{
		OPPONENT_COLOUR[0] = -1;
		OPPONENT_COLOUR[COLOUR_WHITE] = COLOUR_BLACK;
		OPPONENT_COLOUR[COLOUR_BLACK] = COLOUR_WHITE;
	}
	
	public static final int TYPE_UNDEFINED = Constants.TYPE_NONE;
	public static final int TYPE_PAWN = Constants.TYPE_PAWN;
	public static final int TYPE_KNIGHT = Constants.TYPE_KNIGHT;
	public static final int TYPE_OFFICER = Constants.TYPE_BISHOP;
	public static final int TYPE_CASTLE = Constants.TYPE_ROOK;
	public static final int TYPE_QUEEN = Constants.TYPE_QUEEN;
	public static final int TYPE_KING = Constants.TYPE_KING;
	public static final int TYPE_MAX = TYPE_KING + 1;
	
	public static final int[] TYPES = new int[]{TYPE_KING, TYPE_PAWN, TYPE_KNIGHT, TYPE_OFFICER,
		TYPE_CASTLE, TYPE_QUEEN};
	
	public static final String[] TYPES_SIGN = new String[TYPE_MAX]; 
	static {
		TYPES_SIGN[TYPE_KING] = "K";
		TYPES_SIGN[TYPE_PAWN] = "P";
		TYPES_SIGN[TYPE_KNIGHT] = "N";
		TYPES_SIGN[TYPE_OFFICER] = "B";
		TYPES_SIGN[TYPE_CASTLE] = "R";
		TYPES_SIGN[TYPE_QUEEN] = "Q";
	}
	
	public static final String[] COLOURS_SIGN = new String[COLOUR_MAX]; 
	static {
		COLOURS_SIGN[COLOUR_WHITE] = "W";
		COLOURS_SIGN[COLOUR_BLACK] = "B";
	}
	
	public static final int ID_MAX = PRIME_67;
	public static final int DIR_MAX = 7 + 1; //10 because of king side
	public static final int SEQ_MAX = 6 + 1;
	
	public static final long DUMMY_FIGURE = NUMBER_MINUS_1;
	//public static final int DUMMY_FIGURE_ID = NUMBER_69;
	
  public static final long WHITE_LEFT_CASTLE = BIT_0;
  public static final long WHITE_LEFT_KNIGHT = BIT_1;
  public static final long WHITE_LEFT_OFFICER = BIT_2;
  public static final long WHITE_QUEEN = BIT_3;
  public static final long WHITE_KING = BIT_4;
  public static final long WHITE_RIGHT_OFFICER = BIT_5;
  public static final long WHITE_RIGHT_KNIGHT = BIT_6;
  public static final long WHITE_RIGHT_CASTLE = BIT_7;
  
  public static final long WHITE_PAWN_1 = BIT_8;
  public static final long WHITE_PAWN_2 = BIT_9;
  public static final long WHITE_PAWN_3 = BIT_10;
  public static final long WHITE_PAWN_4 = BIT_11;
  public static final long WHITE_PAWN_5 = BIT_12;
  public static final long WHITE_PAWN_6 = BIT_13;
  public static final long WHITE_PAWN_7 = BIT_14;
  public static final long WHITE_PAWN_8 = BIT_15;

  public static final long BLACK_LEFT_CASTLE = BIT_16;
  public static final long BLACK_LEFT_KNIGHT = BIT_17;
  public static final long BLACK_LEFT_OFFICER = BIT_18;
  public static final long BLACK_QUEEN = BIT_19;
  public static final long BLACK_KING = BIT_20;
  public static final long BLACK_RIGHT_OFFICER = BIT_21;
  public static final long BLACK_RIGHT_KNIGHT = BIT_22;
  public static final long BLACK_RIGHT_CASTLE = BIT_23;
  
  public static final long BLACK_PAWN_1 = BIT_24;
  public static final long BLACK_PAWN_2 = BIT_25;
  public static final long BLACK_PAWN_3 = BIT_26;
  public static final long BLACK_PAWN_4 = BIT_27;
  public static final long BLACK_PAWN_5 = BIT_28;
  public static final long BLACK_PAWN_6 = BIT_29;
  public static final long BLACK_PAWN_7 = BIT_30;
  public static final long BLACK_PAWN_8 = BIT_31;
  
  public static final long WHITE_FREE_KNIGHT_1 = BIT_32;
  public static final long WHITE_FREE_KNIGHT_2 = BIT_33;
  public static final long WHITE_FREE_KNIGHT_3 = BIT_34;
  public static final long WHITE_FREE_KNIGHT_4 = BIT_35;
  public static final long BLACK_FREE_KNIGHT_1 = BIT_36;
  public static final long BLACK_FREE_KNIGHT_2 = BIT_37;
  public static final long BLACK_FREE_KNIGHT_3 = BIT_38;
  public static final long BLACK_FREE_KNIGHT_4 = BIT_39;

  public static final long WHITE_FREE_OFFICER_1 = BIT_40;
  public static final long WHITE_FREE_OFFICER_2 = BIT_41;
  public static final long WHITE_FREE_OFFICER_3 = BIT_42;
  public static final long WHITE_FREE_OFFICER_4 = BIT_43;
  public static final long BLACK_FREE_OFFICER_1 = BIT_44;
  public static final long BLACK_FREE_OFFICER_2 = BIT_45;
  public static final long BLACK_FREE_OFFICER_3 = BIT_46;
  public static final long BLACK_FREE_OFFICER_4 = BIT_47;

  public static final long WHITE_FREE_CASTLE_1 = BIT_48;
  public static final long WHITE_FREE_CASTLE_2 = BIT_49;
  public static final long WHITE_FREE_CASTLE_3 = BIT_50;
  public static final long WHITE_FREE_CASTLE_4 = BIT_51;
  public static final long BLACK_FREE_CASTLE_1 = BIT_52;
  public static final long BLACK_FREE_CASTLE_2 = BIT_53;
  public static final long BLACK_FREE_CASTLE_3 = BIT_54;
  public static final long BLACK_FREE_CASTLE_4 = BIT_55;

  public static final long WHITE_FREE_QUEEN_1 = BIT_56;
  public static final long WHITE_FREE_QUEEN_2 = BIT_57;
  public static final long WHITE_FREE_QUEEN_3 = BIT_58;
  public static final long WHITE_FREE_QUEEN_4 = BIT_59;
  public static final long BLACK_FREE_QUEEN_1 = BIT_60;
  public static final long BLACK_FREE_QUEEN_2 = BIT_61;
  public static final long BLACK_FREE_QUEEN_3 = BIT_62;
  public static final long BLACK_FREE_QUEEN_4 = BIT_63;
    		
  /*public static final int WHITE_KING_ID = get67IDByBitboard(WHITE_KING);
  public static final int BLACK_KING_ID = get67IDByBitboard(BLACK_KING);
  
  public static final int WHITE_LEFT_CASTLE_ID = get67IDByBitboard(WHITE_LEFT_CASTLE);
  public static final int WHITE_RIGHT_CASTLE_ID = get67IDByBitboard(WHITE_RIGHT_CASTLE);
  
  public static final int BLACK_LEFT_CASTLE_ID = get67IDByBitboard(BLACK_LEFT_CASTLE);
  public static final int BLACK_RIGHT_CASTLE_ID = get67IDByBitboard(BLACK_RIGHT_CASTLE);*/
	
  public static final int WHITE_LEFT_CASTLE_ID = get67IDByBitboard(WHITE_LEFT_CASTLE);
  public static final int WHITE_LEFT_KNIGHT_ID = get67IDByBitboard(WHITE_LEFT_KNIGHT);
  public static final int WHITE_LEFT_OFFICER_ID = get67IDByBitboard(WHITE_LEFT_OFFICER);
  public static final int WHITE_QUEEN_ID = get67IDByBitboard(WHITE_QUEEN);
  public static final int WHITE_KING_ID = get67IDByBitboard(WHITE_KING);
  public static final int WHITE_RIGHT_OFFICER_ID = get67IDByBitboard(WHITE_RIGHT_OFFICER);
  public static final int WHITE_RIGHT_KNIGHT_ID = get67IDByBitboard(WHITE_RIGHT_KNIGHT);
  public static final int WHITE_RIGHT_CASTLE_ID = get67IDByBitboard(WHITE_RIGHT_CASTLE);
  
  public static final int WHITE_PAWN_1_ID = get67IDByBitboard(WHITE_PAWN_1);
  public static final int WHITE_PAWN_2_ID = get67IDByBitboard(WHITE_PAWN_2);
  public static final int WHITE_PAWN_3_ID = get67IDByBitboard(WHITE_PAWN_3);
  public static final int WHITE_PAWN_4_ID = get67IDByBitboard(WHITE_PAWN_4);
  public static final int WHITE_PAWN_5_ID = get67IDByBitboard(WHITE_PAWN_5);
  public static final int WHITE_PAWN_6_ID = get67IDByBitboard(WHITE_PAWN_6);
  public static final int WHITE_PAWN_7_ID = get67IDByBitboard(WHITE_PAWN_7);
  public static final int WHITE_PAWN_8_ID = get67IDByBitboard(WHITE_PAWN_8);

  public static final int BLACK_LEFT_CASTLE_ID = get67IDByBitboard(BLACK_LEFT_CASTLE);
  public static final int BLACK_LEFT_KNIGHT_ID = get67IDByBitboard(BLACK_LEFT_KNIGHT);
  public static final int BLACK_LEFT_OFFICER_ID = get67IDByBitboard(BLACK_LEFT_OFFICER);
  public static final int BLACK_QUEEN_ID = get67IDByBitboard(BLACK_QUEEN);
  public static final int BLACK_KING_ID = get67IDByBitboard(BLACK_KING);
  public static final int BLACK_RIGHT_OFFICER_ID = get67IDByBitboard(BLACK_RIGHT_OFFICER);
  public static final int BLACK_RIGHT_KNIGHT_ID = get67IDByBitboard(BLACK_RIGHT_KNIGHT);
  public static final int BLACK_RIGHT_CASTLE_ID = get67IDByBitboard(BLACK_RIGHT_CASTLE);
  
  public static final int BLACK_PAWN_1_ID = get67IDByBitboard(BLACK_PAWN_1);
  public static final int BLACK_PAWN_2_ID = get67IDByBitboard(BLACK_PAWN_2);
  public static final int BLACK_PAWN_3_ID = get67IDByBitboard(BLACK_PAWN_3);
  public static final int BLACK_PAWN_4_ID = get67IDByBitboard(BLACK_PAWN_4);
  public static final int BLACK_PAWN_5_ID = get67IDByBitboard(BLACK_PAWN_5);
  public static final int BLACK_PAWN_6_ID = get67IDByBitboard(BLACK_PAWN_6);
  public static final int BLACK_PAWN_7_ID = get67IDByBitboard(BLACK_PAWN_7);
  public static final int BLACK_PAWN_8_ID = get67IDByBitboard(BLACK_PAWN_8);
  
  public static final int WHITE_CASTLES_START_INDEX = BLACK_PAWN_8_ID + 1;
  public static final int BLACK_CASTLES_START_INDEX = BLACK_PAWN_8_ID + 1;

  public static final long COLOUR_MASK_WHITE =  WHITE_LEFT_CASTLE | WHITE_LEFT_KNIGHT | WHITE_LEFT_OFFICER | WHITE_QUEEN |
  																						  WHITE_KING | WHITE_RIGHT_OFFICER | WHITE_RIGHT_KNIGHT | WHITE_RIGHT_CASTLE |
																								WHITE_PAWN_1 | WHITE_PAWN_2 | WHITE_PAWN_3 | WHITE_PAWN_4 |
																								WHITE_PAWN_5 | WHITE_PAWN_6 | WHITE_PAWN_7 | WHITE_PAWN_8 |
																								WHITE_FREE_KNIGHT_1 | WHITE_FREE_KNIGHT_2 | WHITE_FREE_KNIGHT_3 | WHITE_FREE_KNIGHT_4 |
																								WHITE_FREE_OFFICER_1 | WHITE_FREE_OFFICER_2 | WHITE_FREE_OFFICER_3 | WHITE_FREE_OFFICER_4 |
																								WHITE_FREE_CASTLE_1 | WHITE_FREE_CASTLE_2 | WHITE_FREE_CASTLE_3 | WHITE_FREE_CASTLE_4 |
																								WHITE_FREE_QUEEN_1 | WHITE_FREE_QUEEN_2 | WHITE_FREE_QUEEN_3 | WHITE_FREE_QUEEN_4;
  
  public static final long COLOUR_MASK_BLACK =  BLACK_LEFT_CASTLE | BLACK_LEFT_KNIGHT | BLACK_LEFT_OFFICER | BLACK_QUEEN |
																								BLACK_KING | BLACK_RIGHT_OFFICER | BLACK_RIGHT_KNIGHT | BLACK_RIGHT_CASTLE |
																								BLACK_PAWN_1 | BLACK_PAWN_2 | BLACK_PAWN_3 | BLACK_PAWN_4 |
																								BLACK_PAWN_5 | BLACK_PAWN_6 | BLACK_PAWN_7 | BLACK_PAWN_8 |
																								BLACK_FREE_KNIGHT_1 | BLACK_FREE_KNIGHT_2 | BLACK_FREE_KNIGHT_3 | BLACK_FREE_KNIGHT_4 |
																								BLACK_FREE_OFFICER_1 | BLACK_FREE_OFFICER_2 | BLACK_FREE_OFFICER_3 | BLACK_FREE_OFFICER_4 |
																								BLACK_FREE_CASTLE_1 | BLACK_FREE_CASTLE_2 | BLACK_FREE_CASTLE_3 | BLACK_FREE_CASTLE_4 |
																								BLACK_FREE_QUEEN_1 | BLACK_FREE_QUEEN_2 | BLACK_FREE_QUEEN_3 | BLACK_FREE_QUEEN_4;
  
  public static final long TYPE_MASK_PAWNS =  WHITE_PAWN_1 | WHITE_PAWN_2 | WHITE_PAWN_3 | WHITE_PAWN_4 |
																							WHITE_PAWN_5 | WHITE_PAWN_6 | WHITE_PAWN_7 | WHITE_PAWN_8 |
																							BLACK_PAWN_1 | BLACK_PAWN_2 | BLACK_PAWN_3 | BLACK_PAWN_4 |
																							BLACK_PAWN_5 | BLACK_PAWN_6 | BLACK_PAWN_7 | BLACK_PAWN_8;
  
  public static final long TYPE_MASK_KNIGHTS = WHITE_LEFT_KNIGHT |  WHITE_RIGHT_KNIGHT | BLACK_LEFT_KNIGHT | BLACK_RIGHT_KNIGHT |	
  																						 WHITE_FREE_KNIGHT_1 | WHITE_FREE_KNIGHT_2 | WHITE_FREE_KNIGHT_3 | WHITE_FREE_KNIGHT_4 |
  																						 BLACK_FREE_KNIGHT_1 | BLACK_FREE_KNIGHT_2 | BLACK_FREE_KNIGHT_3 | BLACK_FREE_KNIGHT_4;
  
  public static final long TYPE_MASK_OFFICERS = WHITE_LEFT_OFFICER | WHITE_RIGHT_OFFICER | BLACK_LEFT_OFFICER | BLACK_RIGHT_OFFICER |
																								WHITE_FREE_OFFICER_1 | WHITE_FREE_OFFICER_2 | WHITE_FREE_OFFICER_3 | WHITE_FREE_OFFICER_4 |
																								BLACK_FREE_OFFICER_1 | BLACK_FREE_OFFICER_2 | BLACK_FREE_OFFICER_3 | BLACK_FREE_OFFICER_4;
  
  public static final long TYPE_MASK_CASTLES =  WHITE_LEFT_CASTLE | WHITE_RIGHT_CASTLE | BLACK_LEFT_CASTLE | BLACK_RIGHT_CASTLE |
																								WHITE_FREE_CASTLE_1 | WHITE_FREE_CASTLE_2 | WHITE_FREE_CASTLE_3 | WHITE_FREE_CASTLE_4 |
																								BLACK_FREE_CASTLE_1 | BLACK_FREE_CASTLE_2 | BLACK_FREE_CASTLE_3 | BLACK_FREE_CASTLE_4;
  
  public static final long TYPE_MASK_QUEENS = WHITE_QUEEN | BLACK_QUEEN |
																							WHITE_FREE_QUEEN_1 | WHITE_FREE_QUEEN_2 | WHITE_FREE_QUEEN_3 | WHITE_FREE_QUEEN_4 |
																							BLACK_FREE_QUEEN_1 | BLACK_FREE_QUEEN_2 | BLACK_FREE_QUEEN_3 | BLACK_FREE_QUEEN_4;

  public static final long TYPE_MASK_KINGS = WHITE_KING | BLACK_KING;

	public static final long INITIAL_ARRAY[][] =
	{
		{
			WHITE_LEFT_CASTLE,
			WHITE_PAWN_1,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_8,
			BLACK_RIGHT_CASTLE },
		{
		WHITE_LEFT_KNIGHT,
			WHITE_PAWN_2,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_7,
			BLACK_RIGHT_KNIGHT },
			{
		WHITE_LEFT_OFFICER,
			WHITE_PAWN_3,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_6,
			BLACK_RIGHT_OFFICER },
			{
		WHITE_QUEEN,
			WHITE_PAWN_4,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_5,
			BLACK_QUEEN },
			{
		WHITE_KING,
			WHITE_PAWN_5,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_4,
			BLACK_KING },
			{
		WHITE_RIGHT_OFFICER,
			WHITE_PAWN_6,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_3,
			BLACK_LEFT_OFFICER },
			{
		WHITE_RIGHT_KNIGHT,
			WHITE_PAWN_7,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_2,
			BLACK_LEFT_KNIGHT },
			{
		WHITE_RIGHT_CASTLE,
			WHITE_PAWN_8,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_1,
			BLACK_LEFT_CASTLE }
	};
  
  /*public static final long INITIAL_ARRAY[][] =
	{
		{
			WHITE_LEFT_CASTLE,
			WHITE_PAWN_1,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_8,
			BLACK_RIGHT_CASTLE },
		{
			DUMMY_FIGURE,
			WHITE_PAWN_2,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_7,
			DUMMY_FIGURE },
			{
			DUMMY_FIGURE,
			WHITE_PAWN_3,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_6,
			DUMMY_FIGURE },
			{
			DUMMY_FIGURE,
			WHITE_PAWN_4,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_5,
			DUMMY_FIGURE },
			{
		WHITE_KING,
			WHITE_PAWN_5,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_4,
			BLACK_KING },
			{
			DUMMY_FIGURE,
			WHITE_PAWN_6,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_3,
			DUMMY_FIGURE },
			{
			DUMMY_FIGURE,
			WHITE_PAWN_7,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_2,
			DUMMY_FIGURE },
			{
		WHITE_RIGHT_CASTLE,
			WHITE_PAWN_8,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			DUMMY_FIGURE,
			BLACK_PAWN_1,
			BLACK_LEFT_CASTLE }
	};*/
  
  public static final long[] FIGURES_ORDERED = new long[] {
  	WHITE_LEFT_CASTLE, WHITE_LEFT_KNIGHT, WHITE_LEFT_OFFICER, WHITE_QUEEN,
  	WHITE_KING, WHITE_RIGHT_OFFICER, WHITE_RIGHT_KNIGHT, WHITE_RIGHT_CASTLE,
  	WHITE_PAWN_1, WHITE_PAWN_2, WHITE_PAWN_3, WHITE_PAWN_4,
  	WHITE_PAWN_5, WHITE_PAWN_6, WHITE_PAWN_7, WHITE_PAWN_8,
  	BLACK_LEFT_CASTLE, BLACK_LEFT_KNIGHT, BLACK_LEFT_OFFICER, BLACK_QUEEN,
  	BLACK_KING, BLACK_RIGHT_OFFICER, BLACK_RIGHT_KNIGHT, BLACK_RIGHT_CASTLE,
  	BLACK_PAWN_1, BLACK_PAWN_2, BLACK_PAWN_3, BLACK_PAWN_4,
  	BLACK_PAWN_5, BLACK_PAWN_6, BLACK_PAWN_7, BLACK_PAWN_8,
  	
  	WHITE_FREE_KNIGHT_1, WHITE_FREE_KNIGHT_2, WHITE_FREE_KNIGHT_3, WHITE_FREE_KNIGHT_4,
  	BLACK_FREE_KNIGHT_1, BLACK_FREE_KNIGHT_2, BLACK_FREE_KNIGHT_3, BLACK_FREE_KNIGHT_4,
  	WHITE_FREE_OFFICER_1, WHITE_FREE_OFFICER_2, WHITE_FREE_OFFICER_3, WHITE_FREE_OFFICER_4,
  	BLACK_FREE_OFFICER_1, BLACK_FREE_OFFICER_2, BLACK_FREE_OFFICER_3, BLACK_FREE_OFFICER_4,
  	WHITE_FREE_CASTLE_1, WHITE_FREE_CASTLE_2, WHITE_FREE_CASTLE_3, WHITE_FREE_CASTLE_4,
  	BLACK_FREE_CASTLE_1, BLACK_FREE_CASTLE_2, BLACK_FREE_CASTLE_3, BLACK_FREE_CASTLE_4,
  	WHITE_FREE_QUEEN_1, WHITE_FREE_QUEEN_2, WHITE_FREE_QUEEN_3, WHITE_FREE_QUEEN_4,
  	BLACK_FREE_QUEEN_1, BLACK_FREE_QUEEN_2, BLACK_FREE_QUEEN_3, BLACK_FREE_QUEEN_4
  	};
  
	public static final long[] FIGURES = new long[PRIME_67];
	private static final int[] FIGURES_TYPES = new int[PRIME_67];
	private static final int[] FIGURES_COLOURS = new int[PRIME_67];
	public static final int[] IDX_FIGURE_ID_2_ORDERED_FIGURE_ID = new int[PRIME_67];
	
	static {
		for (int i=0; i<FIGURES_ORDERED.length; i++) {
			long figure = FIGURES_ORDERED[i];
			int figureID = get67IDByBitboard(figure);
			FIGURES[figureID] = figure;
			IDX_FIGURE_ID_2_ORDERED_FIGURE_ID[figureID] = i;
			
			/*if ((figure & COLOUR_MASK_WHITE) != 0L) {
				FIGURES_COLOURS[figureID] = COLOUR_WHITE;
			} else if ((figure & COLOUR_MASK_BLACK) != 0L) {
				FIGURES_COLOURS[figureID] = COLOUR_BLACK;
			} else throw new IllegalStateException("figure=" + figure);
			
			if ((figure & TYPE_MASK_KINGS) != 0L) {
				FIGURES_TYPES[figureID] = TYPE_KING;
			} else if ((figure & TYPE_MASK_KNIGHTS) != 0L) {
				FIGURES_TYPES[figureID] = TYPE_KNIGHT;
			} else if ((figure & TYPE_MASK_PAWNS) != 0L) {
				FIGURES_TYPES[figureID] = TYPE_PAWN;
			} else if ((figure & TYPE_MASK_OFFICERS) != 0L) {
				FIGURES_TYPES[figureID] = TYPE_OFFICER;
			} else if ((figure & TYPE_MASK_CASTLES) != 0L) {
				FIGURES_TYPES[figureID] = TYPE_CASTLE;
			} else if ((figure & TYPE_MASK_QUEENS) != 0L) {
				FIGURES_TYPES[figureID] = TYPE_QUEEN;
			} else throw new IllegalStateException("figure=" + figure);*/
		}
	}
	
	public static final int getFigureID(long figureBitboard) {
		return get67IDByBitboard(figureBitboard);
	}

	public static final int getFigureOrderedID(long figureBitboard) {
		int figureID = getFigureID(figureBitboard);
		return IDX_FIGURE_ID_2_ORDERED_FIGURE_ID[figureID];
	}
	
	public static final long getFigureBitboard(int figureID) {
		throw new IllegalStateException();
		//return FIGURES[figureID];//
	}

  public static void main(String[] args) {	
  	//genMembers();
  	System.out.println("Yo");
  }

	public static int getFigureColour(int pid) {
		return Constants.getColourByPieceIdentity(pid);
		//return FIGURES_COLOURS[figureID];//
	}

	public static int getFigureType(int pid) {
		return Constants.PIECE_IDENTITY_2_TYPE[pid];
		//return FIGURES_TYPES[figureID];
	}
	
	public static boolean isMajorOrMinor(int figurePID) {
		boolean result = false;
		
		int type = getFigureType(figurePID);
		
		if (type == TYPE_OFFICER
				|| type == TYPE_KNIGHT
				|| type == TYPE_CASTLE
				|| type == TYPE_QUEEN
				) {
			result = true;
		}
		
		return result;
	}
	
	public static int nextType(int type) {
			switch(type) {
				case Figures.TYPE_UNDEFINED:
					return Figures.TYPE_PAWN;
				case Figures.TYPE_PAWN:
					return Figures.TYPE_KNIGHT;
				case Figures.TYPE_KNIGHT:
					return Figures.TYPE_OFFICER;
				case Figures.TYPE_OFFICER:
					return Figures.TYPE_CASTLE;
				case Figures.TYPE_CASTLE:
					return Figures.TYPE_QUEEN;
				case Figures.TYPE_QUEEN:
					return Figures.TYPE_KING;
				case Figures.TYPE_KING:
					return Figures.TYPE_MAX;
				default:
					throw new IllegalArgumentException(
							"Figure type " + type + " is undefined!");
			}
	}
	
	public static boolean isPawn(int figureID) {
		boolean result = false;
		
		int type = getFigureType(figureID);
		
		if (type == TYPE_PAWN) {
			result = true;
		}
		
		return result;
	}
	
	public static boolean isTypeGreaterOrEqual(int type1, int type2) {	
		return type1 >= type2;
	}
	
	public static boolean isTypeGreater(int type1, int type2) {	
		return type1 > type2;
	}
	
	/**
	 * Hack for compatibility 
	 */
	public static final int getPidByColourAndType(int colour, int type) {
		if (colour == COLOUR_WHITE) {
			switch(type) {
				case TYPE_PAWN:
					return Constants.PID_W_PAWN;
				case TYPE_KNIGHT:
					return Constants.PID_W_KNIGHT;
				case TYPE_KING:
					return Constants.PID_W_KING;
				case TYPE_OFFICER:
					return Constants.PID_W_BISHOP;
				case TYPE_CASTLE:
					return Constants.PID_W_ROOK;
				case TYPE_QUEEN:
					return Constants.PID_W_QUEEN;
				default :
					throw new IllegalStateException();
			}
		} else if (colour == COLOUR_BLACK) {
			switch(type) {
				case TYPE_PAWN:
					return Constants.PID_B_PAWN;
				case TYPE_KNIGHT:
					return Constants.PID_B_KNIGHT;
				case TYPE_KING:
					return Constants.PID_B_KING;
				case TYPE_OFFICER:
					return Constants.PID_B_BISHOP;
				case TYPE_CASTLE:
					return Constants.PID_B_ROOK;
				case TYPE_QUEEN:
					return Constants.PID_B_QUEEN;
				default :
					throw new IllegalStateException();
			}
		} else {
			throw new IllegalStateException();
		}
	}
	
	public static final int getTypeByPid(int pid) {
		switch(pid) {
			case Constants.PID_W_PAWN:
				return TYPE_PAWN;
			case Constants.PID_W_KNIGHT:
				return TYPE_KNIGHT;
			case Constants.PID_W_BISHOP:
				return TYPE_OFFICER;
			case Constants.PID_W_ROOK:
				return TYPE_CASTLE;
			case Constants.PID_W_QUEEN:
				return TYPE_QUEEN;
			case Constants.PID_W_KING:
				return TYPE_KING;
			case Constants.PID_B_PAWN:
				return TYPE_PAWN;
			case Constants.PID_B_KNIGHT:
				return TYPE_KNIGHT;
			case Constants.PID_B_BISHOP:
				return TYPE_OFFICER;
			case Constants.PID_B_ROOK:
				return TYPE_CASTLE;
			case Constants.PID_B_QUEEN:
				return TYPE_QUEEN;
			case Constants.PID_B_KING:
				return TYPE_KING;
			default:
				return -1;
		}
	}
}

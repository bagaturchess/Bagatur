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
	
	public static final long[] FIGURES = new long[PRIME_67];
	public static final int[] IDX_FIGURE_ID_2_ORDERED_FIGURE_ID = new int[PRIME_67];
	
	
	public static final int getFigureID(long figureBitboard) {
		return get67IDByBitboard(figureBitboard);
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

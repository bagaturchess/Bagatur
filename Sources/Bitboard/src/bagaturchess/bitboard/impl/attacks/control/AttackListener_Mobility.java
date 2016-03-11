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
package bagaturchess.bitboard.impl.attacks.control;


import bagaturchess.bitboard.api.IAttackListener;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IMobility;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Figures;


public class AttackListener_Mobility implements IAttackListener, IMobility {
	
	
	private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial ( new int[]{	
			   0,   1,   2,   3,   4,   5,   6,   7,
			   8,   9,  10,  11,  12,  13,  14,  15,
			  16,  17,  18,  19,  20,  21,  22,  23,
			  24,  25,  26,  27,  28,  29,  30,  31,
			  32,  33,  34,  35,  36,  37,  38,  39,
			  40,  41,  42,  43,  44,  45,  46,  47,
			  48,  49,  50,  51,  52,  53,  54,  55,
			  56,  57,  58,  59,  60,  61,  62,  63,

	});
	
	
	private IBoardConfig boardConfig;
	
	private int w_mobility_o;
	private int w_mobility_e;
	private int b_mobility_o;
	private int b_mobility_e;
	
	
	public AttackListener_Mobility(IBoardConfig _boardConfig) {
		boardConfig = _boardConfig;
	}
	
	public int getMobility_o() {
		return w_mobility_o - b_mobility_o;
	}
	
	public int getMobility_e() {
		return w_mobility_e - b_mobility_e;
	}
	
	@Override
	public void addAttack(int colour, int type, int fieldID, long fieldBitboard) {
		if (colour == Figures.COLOUR_WHITE) {
			w_mobility_o += getScores_O(colour, type, fieldID);
			w_mobility_e += getScores_E(colour, type, fieldID);
		} else {
			b_mobility_o += getScores_O(colour, type, fieldID);
			b_mobility_e += getScores_E(colour, type, fieldID);
		}
	}

	@Override
	public void removeAttack(int colour, int type, int fieldID,
			long fieldBitboard) {
		if (colour == Figures.COLOUR_WHITE) {
			w_mobility_o -= getScores_O(colour, type, fieldID);
			w_mobility_e -= getScores_E(colour, type, fieldID);
		} else {
			b_mobility_o -= getScores_O(colour, type, fieldID);
			b_mobility_e -= getScores_E(colour, type, fieldID);
		}
	}
	
	private int getScores_O(int colour, int type, int fieldID) {
		int result = 0;
		
		if (colour == Figures.COLOUR_BLACK) fieldID = HORIZONTAL_SYMMETRY[fieldID];
		
		switch(type) {
			case Figures.TYPE_PAWN:
				//Nothing
				break;
			case Figures.TYPE_KNIGHT:
				return (int) boardConfig.getPST_KNIGHT_O()[fieldID];
			case Figures.TYPE_OFFICER:
				return (int) boardConfig.getPST_BISHOP_O()[fieldID];
			case Figures.TYPE_CASTLE:
				return (int) boardConfig.getPST_ROOK_O()[fieldID];
			case Figures.TYPE_QUEEN:
				return (int) boardConfig.getPST_QUEEN_O()[fieldID];
			case Figures.TYPE_KING:
				//Nothing
				break;
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
		
		return result;
	}
	
	private int getScores_E(int colour, int type, int fieldID) {
		int result = 0;
		
		if (colour == Figures.COLOUR_BLACK) fieldID = HORIZONTAL_SYMMETRY[fieldID];
		
		switch(type) {
			case Figures.TYPE_PAWN:
				//Nothing
				break;
			case Figures.TYPE_KNIGHT:
				return (int) boardConfig.getPST_KNIGHT_E()[fieldID];
			case Figures.TYPE_OFFICER:
				return (int) boardConfig.getPST_BISHOP_E()[fieldID];
			case Figures.TYPE_CASTLE:
				return (int) boardConfig.getPST_ROOK_E()[fieldID];
			case Figures.TYPE_QUEEN:
				return (int) boardConfig.getPST_QUEEN_E()[fieldID];
			case Figures.TYPE_KING:
				//Nothing
				break;
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
		
		return result;
	}
}

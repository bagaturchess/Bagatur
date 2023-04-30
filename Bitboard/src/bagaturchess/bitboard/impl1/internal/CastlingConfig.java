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
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;

/**
 * Encapsulates the castling generalization logic for FRC-Chess960 support
 */
public class CastlingConfig {
	
	
	public static final int A1 				= 7;
	//public static final int B1 			= 6;
	public static final int C1 				= 5;
	public static final int D1 				= 4;
	public static final int E1 				= 3;
	public static final int F1 				= 2;
	public static final int G1 				= 1;
	public static final int H1 				= 0;
	
	public static final int A8 				= 63;
	//public static final int B8 			= 62;
	public static final int C8 				= 61;
	public static final int D8 				= 60;
	public static final int E8 				= 59;
	public static final int F8 				= 58;
	public static final int G8 				= 57;
	public static final int H8 				= 56;
	
	public static final CastlingConfig CLASSIC_CHESS = new CastlingConfig(E1, H1, A1, E8, H8, A8);
	
	
	//Starting square IDs
	public int from_SquareID_king_w;
	public int from_SquareID_rook_kingside_w;
	public int from_SquareID_rook_queenside_w;
	
	public int from_SquareID_king_b;
	public int from_SquareID_rook_kingside_b;
	public int from_SquareID_rook_queenside_b;
	
	
	//In-between bitboards
	public long bb_inbetween_king_kingside_w;
	public long bb_inbetween_king_queenside_w;
	public long bb_inbetween_rook_kingside_w;
	public long bb_inbetween_rook_queenside_w;
	
	public long bb_inbetween_king_kingside_b;
	public long bb_inbetween_king_queenside_b;
	public long bb_inbetween_rook_kingside_b;
	public long bb_inbetween_rook_queenside_b;
	
	
	public CastlingConfig(int from_SquareID_king_w,
			int from_SquareID_rook_kingside_w,
			int from_SquareID_rook_queenside_w,
			int from_SquareID_king_b,
			int from_SquareID_rook_kingside_b,
			int from_SquareID_rook_queenside_b
			) {
		
		this.from_SquareID_king_w 				= from_SquareID_king_w;
		this.from_SquareID_rook_kingside_w 		= from_SquareID_rook_kingside_w;
		this.from_SquareID_rook_queenside_w 	= from_SquareID_rook_queenside_w;
		
		this.from_SquareID_king_b 				= from_SquareID_king_b;
		this.from_SquareID_rook_kingside_b 		= from_SquareID_rook_kingside_b;
		this.from_SquareID_rook_queenside_b 	= from_SquareID_rook_queenside_b;
		
		bb_inbetween_king_kingside_w 			= ChessConstants.IN_BETWEEN[from_SquareID_king_w][G1] | Util.POWER_LOOKUP[G1];
		bb_inbetween_king_queenside_w 			= ChessConstants.IN_BETWEEN[from_SquareID_king_w][C1] | Util.POWER_LOOKUP[C1];
		bb_inbetween_rook_kingside_w 			= ChessConstants.IN_BETWEEN[from_SquareID_rook_kingside_w][F1] | Util.POWER_LOOKUP[F1];
		bb_inbetween_rook_queenside_w 			= ChessConstants.IN_BETWEEN[from_SquareID_rook_queenside_w][D1] | Util.POWER_LOOKUP[D1];
		
		bb_inbetween_king_kingside_b 			= ChessConstants.IN_BETWEEN[from_SquareID_king_b][G8] | Util.POWER_LOOKUP[G8];
		bb_inbetween_king_queenside_b 			= ChessConstants.IN_BETWEEN[from_SquareID_king_b][C8] | Util.POWER_LOOKUP[C8];
		bb_inbetween_rook_kingside_b 			= ChessConstants.IN_BETWEEN[from_SquareID_rook_kingside_b][F8] | Util.POWER_LOOKUP[F8];
		bb_inbetween_rook_queenside_b 			= ChessConstants.IN_BETWEEN[from_SquareID_rook_queenside_b][D8] | Util.POWER_LOOKUP[D8];
	}
	
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			
			return true;
		}
		
		CastlingConfig other = (CastlingConfig) o;
		
		return from_SquareID_king_w == other.from_SquareID_king_w
						&& from_SquareID_rook_kingside_w == other.from_SquareID_rook_kingside_w
						&& from_SquareID_rook_queenside_w == other.from_SquareID_rook_queenside_w
						&& from_SquareID_king_b == other.from_SquareID_king_b
						&& from_SquareID_rook_kingside_b == other.from_SquareID_rook_kingside_b
						&& from_SquareID_rook_queenside_b == other.from_SquareID_rook_queenside_b;
	}
	
	
	@Override
	public int hashCode() {

		return from_SquareID_king_w ^ from_SquareID_rook_kingside_w ^ from_SquareID_rook_queenside_w ^ from_SquareID_king_b ^ from_SquareID_rook_kingside_b ^ from_SquareID_rook_queenside_b;
	}
	
	
	@Override
	public String toString() {
		
		String msg= "";
		
		msg += "[";
		
		msg += "from_SquareID_king_w = " + from_SquareID_king_w + ", ";
		
		msg += "from_SquareID_rook_kingside_w = " + from_SquareID_rook_kingside_w + ", ";
		
		msg += "from_SquareID_rook_queenside_w = " + from_SquareID_rook_queenside_w + ", ";
		
		msg += "from_SquareID_king_b = " + from_SquareID_king_b + ", ";
		
		msg += "from_SquareID_rook_kingside_b = " + from_SquareID_rook_kingside_b + ", ";
		
		msg += "from_SquareID_rook_queenside_b = " + from_SquareID_rook_queenside_b + "";
		
		msg += "]";
		
		return msg;
	}
}

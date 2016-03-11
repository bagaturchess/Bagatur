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
package bagaturchess.bitboard.impl.plies.specials;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.Move;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.zobrist.ConstantStructure;

public class Promotioning extends Fields {
	public static final long WHITE_PROMOTIONS = A8 | B8 | C8 | D8 | E8 | F8 | G8 | H8;
	public static final long BLACK_PROMOTIONS = A1 | B1 | C1 | D1 | E1 | F1 | G1 | H1;
	
	public static void fillCapturePromotion(final int figureID,
			final int figureColour,
			final int fromFieldID,
			final int[] figuresIDsPerFieldsIDs,
			final int dirID,
			final int toFieldID,
			final int promotionFigurePID,
			final long[] move) {
		
		//move[27] = 1;
		//move[28] = ConstantStructure.getMoveHash(figureID, promotionFigurePID, fromFieldID, toFieldID);
		//move[29] = ConstantStructure.getMoveIndex(fromFieldID, 2 + dirID, 0, Constants.PIECE_IDENTITY_2_TYPE[promotionFigurePID]);
		
		move[0] = Move.MASK_EMPTY | Move.MASK_CAPTURE | Move.MASK_PROMOTION;
		move[1] = figureID;
		//move[2] = figureColour;
		//move[3] = Figures.TYPE_PAWN;
		//move[4] = Figures.TYPE_PAWN;
		move[5] = dirID;
		move[6] = 0;
		//move[7] = fromBitboard;
		//move[8] = toBitboard;	
		move[9] = fromFieldID;
		move[10] = toFieldID;
		
		int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
		if (capturedFigureID == Constants.PID_NONE) {
			throw new IllegalStateException("capturedFigureID=" + capturedFigureID);
		}
		
		move[11] = capturedFigureID;
		//move[12] = opponentColour;
		//move[13] = Figures.FIGURES_TYPES[capturedFigureID];
		
		move[21] = promotionFigurePID;
	}
	
	public static void fillNonCapturePromotion(final int figureID,
			final int figureColour,
			final int fromFieldID,
			final int dirID,
			final int toFieldID,
			final int promotionFigurePID,
			final long[] move) {
		
		//move[27] = 1;
		//move[28] = ConstantStructure.getMoveHash(figureID, promotionFigurePID, fromFieldID, toFieldID);
		//move[29] = ConstantStructure.getMoveIndex(fromFieldID, dirID, 0, Constants.PIECE_IDENTITY_2_TYPE[promotionFigurePID]);
		
		move[0] = Move.MASK_EMPTY | Move.MASK_PROMOTION;
		move[1] = figureID;
		//move[2] = figureColour;
		//move[3] = Figures.TYPE_PAWN;
		//move[4] = Figures.TYPE_PAWN;
		move[5] = dirID;
		move[6] = 0;
		//move[7] = fromBitboard;
		//move[8] = toBitboard;	
		move[9] = fromFieldID;
		move[10] = toFieldID;

		//move[12] = opponentColour;
		
		move[21] = promotionFigurePID;		
	}
}

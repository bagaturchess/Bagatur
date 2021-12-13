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
package bagaturchess.bitboard.impl1;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Figures;


public class BaseEvaluation implements MoveListener, IBaseEval {
	
	
	/*private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial ( new int[]{	
			   0,   1,   2,   3,   4,   5,   6,   7,
			   8,   9,  10,  11,  12,  13,  14,  15,
			  16,  17,  18,  19,  20,  21,  22,  23,
			  24,  25,  26,  27,  28,  29,  30,  31,
			  32,  33,  34,  35,  36,  37,  38,  39,
			  40,  41,  42,  43,  44,  45,  46,  47,
			  48,  49,  50,  51,  52,  53,  54,  55,
			  56,  57,  58,  59,  60,  61,  62,  63,

	});*/
	
	private IBitBoard board;
	
	private IBoardConfig boardConfig;
	
	//private PSTs pst;
	
	private double w_material_nopawns_o;
	private double b_material_nopawns_o;
	private double w_material_nopawns_e;
	private double b_material_nopawns_e;
	
	private double w_material_pawns_o;
	private double b_material_pawns_o;
	private double w_material_pawns_e;
	private double b_material_pawns_e;
	
	private double whitePST_o;
	private double blackPST_o;
	private double whitePST_e;
	private double blackPST_e;
	
	
	public BaseEvaluation(IBoardConfig _boardConfig, IBitBoard _board) {
		
		boardConfig = _boardConfig;
		
		board = _board;
		
		//pst = new PSTs(boardConfig);
		
		w_material_nopawns_o = 0;
		b_material_nopawns_o = 0;
		w_material_nopawns_e = 0;
		b_material_nopawns_e = 0;
		
		w_material_pawns_o = 0;
		b_material_pawns_o = 0;
		w_material_pawns_e = 0;
		b_material_pawns_e = 0;
		
		whitePST_o = 0;
		blackPST_o = 0;
		whitePST_e = 0;
		blackPST_e = 0;
	}
	
	public int getPST_e() {
		return (int) (whitePST_e - blackPST_e);
	}

	public int getPST_o() {
		return (int) (whitePST_o - blackPST_o);
	}
	
	
	public int getMaterial_o() {
		return getWhiteMaterialPawns_o() + getWhiteMaterialNonPawns_o() - getBlackMaterialPawns_o() - getBlackMaterialNonPawns_o();
	}
	
	public int getMaterial_e() {
		return getWhiteMaterialPawns_e() + getWhiteMaterialNonPawns_e() - getBlackMaterialPawns_e() - getBlackMaterialNonPawns_e();
	}
	
	public int getWhiteMaterialPawns_o() {
		return (int) w_material_pawns_o;
	}
	
	public int getWhiteMaterialPawns_e() {
		return (int) w_material_pawns_e;
	}
	
	public int getBlackMaterialPawns_o() {
		return (int) b_material_pawns_o;
	}
	
	public int getBlackMaterialPawns_e() {
		return (int) b_material_pawns_e;
	}
	
	public int getWhiteMaterialNonPawns_o() {
		return (int) w_material_nopawns_o;
	}
	
	public int getWhiteMaterialNonPawns_e() {
		return (int) w_material_nopawns_e;
	}
	
	public int getBlackMaterialNonPawns_o() {
		return (int) b_material_nopawns_o;
	}
	
	public int getBlackMaterialNonPawns_e() {
		return (int) b_material_nopawns_e;
	}
	
	@Override
	public int getMaterial_BARIER_NOPAWNS_O() {
		return (int) boardConfig.getMaterial_BARIER_NOPAWNS_O();
	}

	@Override
	public int getMaterial_BARIER_NOPAWNS_E() {
		return (int) boardConfig.getMaterial_BARIER_NOPAWNS_E();
	}
	
	public void move(int move, int color) {
		
		/*int toFieldID = board.getMoveOps().getToFieldID(move);
		
		if (color == Figures.COLOUR_WHITE) {
			
			whitePST_o += pst.getMoveScores_o(move);
			whitePST_e += pst.getMoveScores_e(move);
			
			if (board.getMoveOps().isEnpassant(move)) {
				int enCapField = board.getMoveOps().getEnpassantCapturedFieldID(move);
				blackPST_o -= pst.getPieceScores_o(HORIZONTAL_SYMMETRY[enCapField], Figures.TYPE_PAWN);
				blackPST_e -= pst.getPieceScores_e(HORIZONTAL_SYMMETRY[enCapField], Figures.TYPE_PAWN);
			} else {
				if (board.getMoveOps().isCapture(move)) {
					int capType = board.getMoveOps().getCapturedFigureType(move);
					blackPST_o -= pst.getPieceScores_o(HORIZONTAL_SYMMETRY[toFieldID], capType);
					blackPST_e -= pst.getPieceScores_e(HORIZONTAL_SYMMETRY[toFieldID], capType);
				}
				
				if (board.getMoveOps().isPromotion(move)) {
					int promType = board.getMoveOps().getPromotionFigureType(move);
					whitePST_o += pst.getPieceScores_o(toFieldID, promType);
					whitePST_e += pst.getPieceScores_e(toFieldID, promType);
				}
				
				if (board.getMoveOps().isCastling(move)) {
					int castFromID = board.getMoveOps().getCastlingRookFromID(move);
					int castToID = board.getMoveOps().getCastlingRookToID(move);
					whitePST_o -= pst.getPieceScores_o(castFromID, Figures.TYPE_CASTLE);
					whitePST_o += pst.getPieceScores_o(castToID, Figures.TYPE_CASTLE);
					whitePST_e -= pst.getPieceScores_e(castFromID, Figures.TYPE_CASTLE);
					whitePST_e += pst.getPieceScores_e(castToID, Figures.TYPE_CASTLE);
				}
			}
		} else {
			
			blackPST_o += pst.getMoveScores_o(move);
			blackPST_e += pst.getMoveScores_e(move);
			
			if (board.getMoveOps().isEnpassant(move)) {
				int enCapField = board.getMoveOps().getEnpassantCapturedFieldID(move);
				whitePST_o -= pst.getPieceScores_o(enCapField, Figures.TYPE_PAWN);
				whitePST_e -= pst.getPieceScores_e(enCapField, Figures.TYPE_PAWN);
			} else {
				if (board.getMoveOps().isCapture(move)) {
					int capType = board.getMoveOps().getCapturedFigureType(move);
					whitePST_o -= pst.getPieceScores_o(toFieldID, capType);
					whitePST_e -= pst.getPieceScores_e(toFieldID, capType);
				}
				
				if (board.getMoveOps().isPromotion(move)) {
					int promType = board.getMoveOps().getPromotionFigureType(move);
					blackPST_o += pst.getPieceScores_o(HORIZONTAL_SYMMETRY[toFieldID], promType);
					blackPST_e += pst.getPieceScores_e(HORIZONTAL_SYMMETRY[toFieldID], promType);
				}
				
				if (board.getMoveOps().isCastling(move)) {
					int castFromID = board.getMoveOps().getCastlingRookFromID(move);
					int castToID = board.getMoveOps().getCastlingRookToID(move);
					blackPST_o -= pst.getPieceScores_o(HORIZONTAL_SYMMETRY[castFromID], Figures.TYPE_CASTLE);
					blackPST_o += pst.getPieceScores_o(HORIZONTAL_SYMMETRY[castToID], Figures.TYPE_CASTLE);
					blackPST_e -= pst.getPieceScores_e(HORIZONTAL_SYMMETRY[castFromID], Figures.TYPE_CASTLE);
					blackPST_e += pst.getPieceScores_e(HORIZONTAL_SYMMETRY[castToID], Figures.TYPE_CASTLE);
				}
			}
		}*/
	}
	
	public void unmove(int move, int color) {

		/*int toFieldID = board.getMoveOps().getToFieldID(move);
		
		if (color == Figures.COLOUR_WHITE) {
			
			whitePST_o -= pst.getMoveScores_o(move);
			whitePST_e -= pst.getMoveScores_e(move);
			
			if (board.getMoveOps().isEnpassant(move)) {
				int enCapField = board.getMoveOps().getEnpassantCapturedFieldID(move);
				blackPST_o += pst.getPieceScores_o(HORIZONTAL_SYMMETRY[enCapField], Figures.TYPE_PAWN);
				blackPST_e += pst.getPieceScores_e(HORIZONTAL_SYMMETRY[enCapField], Figures.TYPE_PAWN);
			} else {
				if (board.getMoveOps().isCapture(move)) {
					int capType = board.getMoveOps().getCapturedFigureType(move);
					blackPST_o += pst.getPieceScores_o(HORIZONTAL_SYMMETRY[toFieldID], capType);
					blackPST_e += pst.getPieceScores_e(HORIZONTAL_SYMMETRY[toFieldID], capType);
				}
				
				if (board.getMoveOps().isPromotion(move)) {
					int promType = board.getMoveOps().getPromotionFigureType(move);
					whitePST_o -= pst.getPieceScores_o(toFieldID, promType);
					whitePST_e -= pst.getPieceScores_e(toFieldID, promType);
				}
				
				if (board.getMoveOps().isCastling(move)) {
					int castFromID = board.getMoveOps().getCastlingRookFromID(move);
					int castToID = board.getMoveOps().getCastlingRookToID(move);
					whitePST_o += pst.getPieceScores_o(castFromID, Figures.TYPE_CASTLE);
					whitePST_o -= pst.getPieceScores_o(castToID, Figures.TYPE_CASTLE);
					whitePST_e += pst.getPieceScores_e(castFromID, Figures.TYPE_CASTLE);
					whitePST_e -= pst.getPieceScores_e(castToID, Figures.TYPE_CASTLE);
				}
			}
		} else {
			blackPST_o -= pst.getMoveScores_o(move);
			blackPST_e -= pst.getMoveScores_e(move);
			
			if (board.getMoveOps().isEnpassant(move)) {
				int enCapField = board.getMoveOps().getEnpassantCapturedFieldID(move);
				whitePST_o += pst.getPieceScores_o(enCapField, Figures.TYPE_PAWN);
				whitePST_e += pst.getPieceScores_e(enCapField, Figures.TYPE_PAWN);
			} else {
				if (board.getMoveOps().isCapture(move)) {
					int capType = board.getMoveOps().getCapturedFigureType(move);
					whitePST_o += pst.getPieceScores_o(toFieldID, capType);
					whitePST_e += pst.getPieceScores_e(toFieldID, capType);
				}
				
				if (board.getMoveOps().isPromotion(move)) {
					int promType = board.getMoveOps().getPromotionFigureType(move);
					blackPST_o -= pst.getPieceScores_o(HORIZONTAL_SYMMETRY[toFieldID], promType);
					blackPST_e -= pst.getPieceScores_e(HORIZONTAL_SYMMETRY[toFieldID], promType);
				}
				
				if (board.getMoveOps().isCastling(move)) {
					int castFromID = board.getMoveOps().getCastlingRookFromID(move);
					int castToID = board.getMoveOps().getCastlingRookToID(move);
					blackPST_o += pst.getPieceScores_o(HORIZONTAL_SYMMETRY[castFromID], Figures.TYPE_CASTLE);
					blackPST_o -= pst.getPieceScores_o(HORIZONTAL_SYMMETRY[castToID], Figures.TYPE_CASTLE);
					blackPST_e += pst.getPieceScores_e(HORIZONTAL_SYMMETRY[castFromID], Figures.TYPE_CASTLE);
					blackPST_e -= pst.getPieceScores_e(HORIZONTAL_SYMMETRY[castToID], Figures.TYPE_CASTLE);
				}
			}
		}*/
	}
	
	
	protected void inc(int color, int type) {
		
		//System.out.println("inc: color=" + color + ", type=" + type);
		
		switch(color) {
			
			case Figures.COLOUR_WHITE:
				
				if (type == Figures.TYPE_PAWN) {
					w_material_pawns_o += getFigureMaterial_O(type);
					w_material_pawns_e += getFigureMaterial_E(type);					
				} else {
					w_material_nopawns_o += getFigureMaterial_O(type);
					w_material_nopawns_e += getFigureMaterial_E(type);
				}
				break;
				
			case Figures.COLOUR_BLACK:
				
				if (type == Figures.TYPE_PAWN) {
					b_material_pawns_o += getFigureMaterial_O(type);
					b_material_pawns_e += getFigureMaterial_E(type);
				} else {
					b_material_nopawns_o += getFigureMaterial_O(type);
					b_material_nopawns_e += getFigureMaterial_E(type);
				}
				break;
				
			default:
				
				throw new IllegalArgumentException(
						"Figure colour " + type + " is undefined!");
		}
	}
	
	protected void dec(int color, int type) {
		
		//System.out.println("inc: color=" + color + ", type=" + type);
		
		switch(color) {
		
			case Figures.COLOUR_WHITE:
				
				if (type == Figures.TYPE_PAWN) {
					w_material_pawns_o -= getFigureMaterial_O(type);
					w_material_pawns_e -= getFigureMaterial_E(type);
				} else {
					w_material_nopawns_o -= getFigureMaterial_O(type);
					w_material_nopawns_e -= getFigureMaterial_E(type);
				}
				break;
				
			case Figures.COLOUR_BLACK:
				
				if (type == Figures.TYPE_PAWN) {
					b_material_pawns_o -= getFigureMaterial_O(type);
					b_material_pawns_e -= getFigureMaterial_E(type);
				} else {
					b_material_nopawns_o -= getFigureMaterial_O(type);
					b_material_nopawns_e -= getFigureMaterial_E(type);
				}
				break;
				
			default:
				
				throw new IllegalArgumentException(
						"Figure colour " + type + " is undefined!");
		}
	}
	
	
	public double getFigureMaterial_O(int type) {
		switch(type) {
			case Figures.TYPE_PAWN:
				return boardConfig.getMaterial_PAWN_O();
			case Figures.TYPE_KNIGHT:
				return boardConfig.getMaterial_KNIGHT_O();
			case Figures.TYPE_OFFICER:
				return boardConfig.getMaterial_BISHOP_O();
			case Figures.TYPE_CASTLE:
				return boardConfig.getMaterial_ROOK_O();
			case Figures.TYPE_QUEEN:
				return boardConfig.getMaterial_QUEEN_O();
			case Figures.TYPE_KING:
				return boardConfig.getMaterial_KING_O();
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
	}
	
	public double getFigureMaterial_E(int type) {
		switch(type) {
			case Figures.TYPE_PAWN:
				return boardConfig.getMaterial_PAWN_E();
			case Figures.TYPE_KNIGHT:
				return boardConfig.getMaterial_KNIGHT_E();
			case Figures.TYPE_OFFICER:
				return boardConfig.getMaterial_BISHOP_E();
			case Figures.TYPE_CASTLE:
				return boardConfig.getMaterial_ROOK_E();
			case Figures.TYPE_QUEEN:
				return boardConfig.getMaterial_QUEEN_E();
			case Figures.TYPE_KING:
				return boardConfig.getMaterial_KING_E();
			default:
				throw new IllegalArgumentException(
						"Figure type " + type + " is undefined!");
		}
	}
	
	
	/**
	 * IMPL of MoveListener ***********************************************
	 */
	
	
	@Override
	public void addPiece_Special(int color, int type) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public void initially_addPiece(int color, int type) {
		inc(color, type);
	}

	
	@Override
	public void preForwardMove(int color, int move) {
		
		if (board.getMoveOps().isCapture(move)) {
			
			int cap_type = board.getMoveOps().getCapturedFigureType(move);
			
			dec(1 - color, cap_type);
		}
		
		if (board.getMoveOps().isPromotion(move)) {
			
			int prom_type = board.getMoveOps().getPromotionFigureType(move);
			
			inc(color, prom_type);
			
			int type = board.getMoveOps().getFigureType(move);
			if (type != Figures.TYPE_PAWN) {
				throw new IllegalStateException();
			}
			
			dec(color, type);
		}
	}
	
	
	@Override
	public void postForwardMove(int color, int move) {	
	}

	
	@Override
	public void preBackwardMove(int color, int move) {
		
		if (board.getMoveOps().isCapture(move)) {
			
			int cap_type = board.getMoveOps().getCapturedFigureType(move);
			
			inc(1 - color, cap_type);
		}
		
		if (board.getMoveOps().isPromotion(move)) {
			
			int prom_type = board.getMoveOps().getPromotionFigureType(move);
			
			dec(color, prom_type);
			
			int type = board.getMoveOps().getFigureType(move);
			if (type != Figures.TYPE_PAWN) {
				throw new IllegalStateException();
			}
			
			inc(color, type);
		}
	}
	
	
	@Override
	public void postBackwardMove(int color, int move) {
	}
	
	
	@Override
	public int getMaterial(int pieceType) {
		switch(pieceType) {
			case Figures.TYPE_PAWN:
				return board.getMaterialFactor().interpolateByFactor(boardConfig.getMaterial_PAWN_O(), boardConfig.getMaterial_PAWN_E());
			case Figures.TYPE_KNIGHT:
				return board.getMaterialFactor().interpolateByFactor(boardConfig.getMaterial_KNIGHT_O(), boardConfig.getMaterial_KNIGHT_E());
			case Figures.TYPE_OFFICER:
				return board.getMaterialFactor().interpolateByFactor(boardConfig.getMaterial_BISHOP_O(), boardConfig.getMaterial_BISHOP_E());
			case Figures.TYPE_CASTLE:
				return board.getMaterialFactor().interpolateByFactor(boardConfig.getMaterial_ROOK_O(), boardConfig.getMaterial_ROOK_E());
			case Figures.TYPE_QUEEN:
				return board.getMaterialFactor().interpolateByFactor(boardConfig.getMaterial_QUEEN_O(), boardConfig.getMaterial_QUEEN_E());
			case Figures.TYPE_KING:
				return board.getMaterialFactor().interpolateByFactor(boardConfig.getMaterial_KING_O(), boardConfig.getMaterial_KING_E());
			default:
				throw new IllegalArgumentException(
						"Figure type " + pieceType + " is undefined!");
		}
	}
	
	
	public int getMaterialGain(int move) {
		
		if (!board.getMoveOps().isCapture(move) && !board.getMoveOps().isPromotion(move)) {
			return 0;
		}
		
		int val = 0;
		
		if (board.getMoveOps().isCapture(move)) {
			
			int captured_type = board.getMoveOps().getCapturedFigureType(move);
			
			val += getMaterial(captured_type);
		}
		
		if (board.getMoveOps().isPromotion(move)) {
			
			int prom_type = board.getMoveOps().getPromotionFigureType(move);
			
			val += getMaterial(prom_type);
			
			val -= getMaterial(Figures.TYPE_PAWN);
		}
		
		return val;
	}
}

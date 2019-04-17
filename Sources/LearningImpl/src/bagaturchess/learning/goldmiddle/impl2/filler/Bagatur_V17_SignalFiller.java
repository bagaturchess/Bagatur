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
package bagaturchess.learning.goldmiddle.impl2.filler;


import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.BISHOP;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.BLACK;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.KING;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.NIGHT;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.PAWN;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.QUEEN;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.ROOK;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.WHITE;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.impl1.base.ChessBoard;
import bagaturchess.learning.goldmiddle.impl1.base.ChessConstants;
import bagaturchess.learning.goldmiddle.impl1.base.Evaluator;


public class Bagatur_V17_SignalFiller extends Evaluator implements ISignalFiller, Bagatur_V17_FeaturesConstants {
	
	
	private PiecesList w_knights;
	private PiecesList b_knights;
	private PiecesList w_bishops;
	private PiecesList b_bishops;
	private PiecesList w_rooks;
	private PiecesList b_rooks;
	private PiecesList w_queens;
	private PiecesList b_queens;
	private PiecesList w_pawns;
	private PiecesList b_pawns;
	private PiecesList w_king;
	private PiecesList b_king;
	
	
	public Bagatur_V17_SignalFiller(IBitBoard board) {
		
		super(new ChessBoard(board));
		
		w_knights = cb.getBoard().getPiecesLists().getPieces(Constants.PID_W_KNIGHT);
		b_knights = cb.getBoard().getPiecesLists().getPieces(Constants.PID_B_KNIGHT);
		w_bishops = cb.getBoard().getPiecesLists().getPieces(Constants.PID_W_BISHOP);
		b_bishops = cb.getBoard().getPiecesLists().getPieces(Constants.PID_B_BISHOP);
		w_rooks = cb.getBoard().getPiecesLists().getPieces(Constants.PID_W_ROOK);
		b_rooks = cb.getBoard().getPiecesLists().getPieces(Constants.PID_B_ROOK);
		w_queens = cb.getBoard().getPiecesLists().getPieces(Constants.PID_W_QUEEN);
		b_queens = cb.getBoard().getPiecesLists().getPieces(Constants.PID_B_QUEEN);
		w_pawns = cb.getBoard().getPiecesLists().getPieces(Constants.PID_W_PAWN);
		b_pawns = cb.getBoard().getPiecesLists().getPieces(Constants.PID_B_PAWN);
		w_king = cb.getBoard().getPiecesLists().getPieces(Constants.PID_W_KING);
		b_king = cb.getBoard().getPiecesLists().getPieces(Constants.PID_B_KING);
	}
	
	
	@Override
	public void fill(ISignals signals) {
		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		evalinfo.clearEvals1();
		
		fillMaterialScore(signals);
		fillImbalances(signals);
		fillPawnScores(signals);
		fillPST(signals);
		
		evalinfo.clearEvals2();
		evalinfo.clearEvalAttacks();
		
		fillMobilityScoresAndSetAttackBoards(signals);
		fillPassedPawnScores(signals);
		fillThreats(signals);
		fillPawnShieldBonus(signals);
		fillOthers(signals);
		fillKingSafetyScores(signals);
		fillSpace(signals);
	}


	private void fillPST(ISignals signals) {
		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		signals.getSignal(FEATURE_ID_PIECE_SQUARE_TABLE).addStrength(interpolateInternal(cb.getPSQTScore_o(), cb.getPSQTScore_e(), openingPart), openingPart);
	}
	
	
	@Override
	public void fillByComplexity(int complexity, ISignals signals) {
		switch(complexity) {
			case IFeatureComplexity.STANDARD:
				fill(signals);
				return;
			case IFeatureComplexity.PAWNS_STRUCTURE:
				//fillPawnSignals(signals);
				return;
			case IFeatureComplexity.PIECES_ITERATION:
				//fillPiecesIterationSignals(signals);
				return;
			case IFeatureComplexity.MOVES_ITERATION:
				//fillMovesIterationSignals(signals);
				return;
			case IFeatureComplexity.FIELDS_STATES_ITERATION:
				//throw new UnsupportedOperationException("FIELDS_STATES_ITERATION");
				return;
			default:
				throw new IllegalStateException("complexity=" + complexity);
		}
	}
	
	
	private double interpolateInternal(double o, double e, double openningPart) {
		return (o * openningPart + e * (1 - openningPart));
	}
	
	
	public void fillMaterialScore(ISignals signals) {
		
		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		signals.getSignal(FEATURE_ID_MATERIAL_PAWN).addStrength(w_pawns.getDataSize() - b_pawns.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_KNIGHT).addStrength(w_knights.getDataSize() - b_knights.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_BISHOP).addStrength(w_bishops.getDataSize() - b_bishops.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_ROOK).addStrength(w_rooks.getDataSize() - b_rooks.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_QUEEN).addStrength(w_queens.getDataSize() - b_queens.getDataSize(), openingPart);
	}
	
	
	private void fillImbalances(ISignals signals) {
		
		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		// knight bonus if there are a lot of pawns
		int value = Long.bitCount(cb.getPieces(WHITE, NIGHT));
		signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_NIGHT_PAWNS).addStrength(Long.bitCount(cb.getPieces(WHITE, PAWN)), value, openingPart);
		
		value = Long.bitCount(cb.getPieces(BLACK, NIGHT));
		signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_NIGHT_PAWNS).addStrength(Long.bitCount(cb.getPieces(BLACK, PAWN)), -value, openingPart);
		
		
		// rook bonus if there are no pawns
		value = Long.bitCount(cb.getPieces(WHITE, ROOK));
		signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS).addStrength(Long.bitCount(cb.getPieces(WHITE, PAWN)), value, openingPart);
		
		value = Long.bitCount(cb.getPieces(BLACK, ROOK));
		signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS).addStrength(Long.bitCount(cb.getPieces(BLACK, PAWN)), -value, openingPart);
		
		
		// double bishop bonus
		if (Long.bitCount(cb.getPieces(WHITE, BISHOP)) == 2) {
			signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE).addStrength(IMBALANCE_SCORES[IX_BISHOP_DOUBLE], openingPart);
		}
		if (Long.bitCount(cb.getPieces(BLACK, BISHOP)) == 2) {
			signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE).addStrength(-IMBALANCE_SCORES[IX_BISHOP_DOUBLE], openingPart);
		}
		
		
		// queen and nights
		if (cb.getPieces(WHITE, QUEEN) != 0) {
			value = Long.bitCount(cb.getPieces(WHITE, NIGHT)) * IMBALANCE_SCORES[IX_QUEEN_NIGHT];
			signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS).addStrength(value, openingPart);
		}
		if (cb.getPieces(BLACK, QUEEN) != 0) {
			value = Long.bitCount(cb.getPieces(BLACK, NIGHT)) * IMBALANCE_SCORES[IX_QUEEN_NIGHT];
			signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS).addStrength(-value, openingPart);
		}
		
		
		// rook pair
		if (Long.bitCount(cb.getPieces(WHITE, ROOK)) > 1) {			
			signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR).addStrength(IMBALANCE_SCORES[IX_ROOK_PAIR], openingPart);
		}
		if (Long.bitCount(cb.getPieces(BLACK, ROOK)) > 1) {
			signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR).addStrength(-IMBALANCE_SCORES[IX_ROOK_PAIR], openingPart);
		}
	}
	
	
	private void fillPawnScores(ISignals signals) {
		
		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		// penalty for doubled pawns
		for (int i = 0; i < 8; i++) {
			if (Long.bitCount(cb.getPieces(WHITE, PAWN) & FILES[i]) > 1) {
				signals.getSignal(FEATURE_ID_PAWN_DOUBLE).addStrength(-PAWN_SCORES[IX_PAWN_DOUBLE], openingPart);
			}
			if (Long.bitCount(cb.getPieces(BLACK, PAWN) & FILES[i]) > 1) {
				signals.getSignal(FEATURE_ID_PAWN_DOUBLE).addStrength(PAWN_SCORES[IX_PAWN_DOUBLE], openingPart);
			}
		}
		
		
		// bonus for connected pawns
		long pawns = getWhitePawnAttacks(cb.getPieces(WHITE, PAWN)) & cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			signals.getSignal(FEATURE_ID_PAWN_CONNECTED).addStrength(Long.numberOfTrailingZeros(pawns) / 8, 1, openingPart);
			pawns &= pawns - 1;
		}
		pawns = getBlackPawnAttacks(cb.getPieces(BLACK, PAWN)) & cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			signals.getSignal(FEATURE_ID_PAWN_CONNECTED).addStrength(7 - Long.numberOfTrailingZeros(pawns) / 8, -1, openingPart);
			pawns &= pawns - 1;
		}
		
		
		// bonus for neighbour pawns
		pawns = getPawnNeighbours(cb.getPieces(WHITE, PAWN)) & cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			signals.getSignal(FEATURE_ID_PAWN_NEIGHBOUR).addStrength(Long.numberOfTrailingZeros(pawns) / 8, 1, openingPart);
			pawns &= pawns - 1;
		}
		pawns = getPawnNeighbours(cb.getPieces(BLACK, PAWN)) & cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			signals.getSignal(FEATURE_ID_PAWN_NEIGHBOUR).addStrength(7 - Long.numberOfTrailingZeros(pawns) / 8, -1, openingPart);
			pawns &= pawns - 1;
		}
		
		
		// set outposts
		evalinfo.passedPawnsAndOutposts = 0;
		pawns = getWhitePawnAttacks(cb.getPieces(WHITE, PAWN)) & ~cb.getPieces(WHITE, PAWN) & ~cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			if ((getWhiteAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.getPieces(BLACK, PAWN)) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}
		pawns = getBlackPawnAttacks(cb.getPieces(BLACK, PAWN)) & ~cb.getPieces(WHITE, PAWN) & ~cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			if ((getBlackAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.getPieces(WHITE, PAWN)) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}
		
		
		int index;
		// white
		pawns = cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);
			
			
			// isolated pawns
			if ((FILES_ADJACENT[index & 7] & cb.getPieces(WHITE, PAWN)) == 0) {
				signals.getSignal(FEATURE_ID_PAWN_ISOLATED).addStrength(-PAWN_SCORES[IX_PAWN_ISOLATED], openingPart);
			}
			
			
			// backward pawns
			else if ((getBlackAdjacentMask(index + 8) & cb.getPieces(WHITE, PAWN)) == 0) {
				if ((PAWN_ATTACKS[WHITE][index + 8] & cb.getPieces(BLACK, PAWN)) != 0) {
					if ((FILES[index & 7] & cb.getPieces(BLACK, PAWN)) == 0) {
						signals.getSignal(FEATURE_ID_PAWN_BACKWARD).addStrength(-PAWN_SCORES[IX_PAWN_BACKWARD], openingPart);
					}
				}
			}
			
			
			// pawn defending 2 pawns
			if (Long.bitCount(PAWN_ATTACKS[WHITE][index] & cb.getPieces(WHITE, PAWN)) == 2) {
				signals.getSignal(FEATURE_ID_PAWN_INVERSE).addStrength(-PAWN_SCORES[IX_PAWN_INVERSE], openingPart);
			}
			
			
			// set passed pawns
			if ((getWhitePassedPawnMask(index) & cb.getPieces(BLACK, PAWN)) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			
			
			// candidate passed pawns (no pawns in front, more friendly pawns behind and adjacent than enemy pawns)
			else if (63 - Long.numberOfLeadingZeros((cb.getPieces(WHITE, PAWN) | cb.getPieces(BLACK, PAWN)) & FILES[index & 7]) == index) {
				if (Long.bitCount(cb.getPieces(WHITE, PAWN) & getBlackPassedPawnMask(index + 8)) >= Long
						.bitCount(cb.getPieces(BLACK, PAWN) & getWhitePassedPawnMask(index))) {
					signals.getSignal(FEATURE_ID_PAWN_PASSED_CANDIDATE).addStrength(index / 8, 1, openingPart);
				}
			}

			pawns &= pawns - 1;
		}
		
		
		// black
		pawns = cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);
			
			
			// isolated pawns
			if ((FILES_ADJACENT[index & 7] & cb.getPieces(BLACK, PAWN)) == 0) {
				signals.getSignal(FEATURE_ID_PAWN_ISOLATED).addStrength(PAWN_SCORES[IX_PAWN_ISOLATED], openingPart);
			}
			
			
			// backward pawns
			else if ((getWhiteAdjacentMask(index - 8) & cb.getPieces(BLACK, PAWN)) == 0) {
				if ((PAWN_ATTACKS[BLACK][index - 8] & cb.getPieces(WHITE, PAWN)) != 0) {
					if ((FILES[index & 7] & cb.getPieces(WHITE, PAWN)) == 0) {
						signals.getSignal(FEATURE_ID_PAWN_BACKWARD).addStrength(PAWN_SCORES[IX_PAWN_BACKWARD], openingPart);
					}
				}
			}
			
			
			// pawn defending 2 pawns
			if (Long.bitCount(PAWN_ATTACKS[BLACK][index] & cb.getPieces(BLACK, PAWN)) == 2) {
				signals.getSignal(FEATURE_ID_PAWN_INVERSE).addStrength(PAWN_SCORES[IX_PAWN_INVERSE], openingPart);
			}
			
			
			// set passed pawns
			if ((getBlackPassedPawnMask(index) & cb.getPieces(WHITE, PAWN)) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			
			
			// candidate passers
			else if (Long.numberOfTrailingZeros((cb.getPieces(WHITE, PAWN) | cb.getPieces(BLACK, PAWN)) & FILES[index & 7]) == index) {
				if (Long.bitCount(cb.getPieces(BLACK, PAWN) & getWhitePassedPawnMask(index - 8)) >= Long
						.bitCount(cb.getPieces(WHITE, PAWN) & getBlackPassedPawnMask(index))) {
					signals.getSignal(FEATURE_ID_PAWN_PASSED_CANDIDATE).addStrength(7 - index / 8, -1, openingPart);
				}
			}
			
			pawns &= pawns - 1;
		}
	}
	
	
	public void fillMobilityScoresAndSetAttackBoards(ISignals signals) {

		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		long moves;

		// white pawns
		evalinfo.attacks[WHITE][PAWN] = getWhitePawnAttacks(cb.getPieces(WHITE, PAWN) & ~cb.getPinnedPieces());
		if ((evalinfo.attacks[WHITE][PAWN] & cb.getKingArea(BLACK)) != 0) {
			evalinfo.kingAttackersFlag[WHITE] = ChessConstants.FLAG_PAWN;
		}
		long pinned = cb.getPieces(WHITE, PAWN) & cb.getPinnedPieces();
		while (pinned != 0) {
			evalinfo.attacks[WHITE][PAWN] |= PAWN_ATTACKS[WHITE][Long.numberOfTrailingZeros(pinned)]
					& ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][cb.getKingIndex(WHITE)];
			pinned &= pinned - 1;
		}
		evalinfo.attacksAll[WHITE] = evalinfo.attacks[WHITE][PAWN];
		// black pawns
		evalinfo.attacks[BLACK][PAWN] = getBlackPawnAttacks(cb.getPieces(BLACK, PAWN) & ~cb.getPinnedPieces());
		if ((evalinfo.attacks[BLACK][PAWN] & cb.getKingArea(WHITE)) != 0) {
			evalinfo.kingAttackersFlag[BLACK] = ChessConstants.FLAG_PAWN;
		}
		pinned = cb.getPieces(BLACK, PAWN) & cb.getPinnedPieces();
		while (pinned != 0) {
			evalinfo.attacks[BLACK][PAWN] |= PAWN_ATTACKS[BLACK][Long.numberOfTrailingZeros(pinned)]
					& ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][cb.getKingIndex(BLACK)];
			pinned &= pinned - 1;
		}
		evalinfo.attacksAll[BLACK] = evalinfo.attacks[BLACK][PAWN];

		//int score = 0;
		for (int color = WHITE; color <= BLACK; color++) {

			//int tempScore = 0;

			final long kingArea = cb.getKingArea(1 - color);
			final long safeMoves = ~cb.getFriendlyPieces(color) & ~evalinfo.attacks[1 - color][PAWN];

			// knights
			long piece = cb.getPieces(color, NIGHT) & ~cb.getPinnedPieces();
			while (piece != 0) {
				moves = KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= ChessConstants.FLAG_NIGHT;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][NIGHT] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT).addStrength(index, 1, openingPart);
				} else {
					signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT).addStrength(index, -1, openingPart);
				}
				piece &= piece - 1;
			}

			// bishops
			piece = cb.getPieces(color, BISHOP);
			while (piece != 0) {
				moves = getBishopMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces() ^ cb.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= ChessConstants.FLAG_BISHOP;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][BISHOP] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					signals.getSignal(FEATURE_ID_MOBILITY_BISHOP).addStrength(index, 1, openingPart);
				} else {
					signals.getSignal(FEATURE_ID_MOBILITY_BISHOP).addStrength(index, -1, openingPart);
				}
				piece &= piece - 1;
			}

			// rooks
			piece = cb.getPieces(color, ROOK);
			while (piece != 0) {
				moves = getRookMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces() ^ cb.getPieces(color, ROOK) ^ cb.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= ChessConstants.FLAG_ROOK;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][ROOK] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					signals.getSignal(FEATURE_ID_MOBILITY_ROOK).addStrength(index, 1, openingPart);
				} else {
					signals.getSignal(FEATURE_ID_MOBILITY_ROOK).addStrength(index, -1, openingPart);
				}
				piece &= piece - 1;
			}

			// queens
			piece = cb.getPieces(color, QUEEN);
			while (piece != 0) {
				moves = getQueenMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces());
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= ChessConstants.FLAG_QUEEN;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][QUEEN] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					signals.getSignal(FEATURE_ID_MOBILITY_QUEEN).addStrength(index, 1, openingPart);
				} else {
					signals.getSignal(FEATURE_ID_MOBILITY_QUEEN).addStrength(index, -1, openingPart);
				}
				piece &= piece - 1;
			}
		}

		// TODO king-attacks with or without enemy attacks?
		// WHITE king
		moves = KING_MOVES[cb.getKingIndex(WHITE)] & ~KING_MOVES[cb.getKingIndex(BLACK)];
		evalinfo.attacks[WHITE][KING] = moves;
		evalinfo.doubleAttacks[WHITE] |= evalinfo.attacksAll[WHITE] & moves;
		evalinfo.attacksAll[WHITE] |= moves;
		int index = Long.bitCount(moves & ~cb.getFriendlyPieces(WHITE) & ~evalinfo.attacksAll[BLACK]);
		signals.getSignal(FEATURE_ID_MOBILITY_KING).addStrength(index, 1, openingPart);
		
		// BLACK king
		moves = KING_MOVES[cb.getKingIndex(BLACK)] & ~KING_MOVES[cb.getKingIndex(WHITE)];
		evalinfo.attacks[BLACK][KING] = moves;
		evalinfo.doubleAttacks[BLACK] |= evalinfo.attacksAll[BLACK] & moves;
		evalinfo.attacksAll[BLACK] |= moves;
		index = Long.bitCount(moves & ~cb.getFriendlyPieces(BLACK) & ~evalinfo.attacksAll[WHITE]);
		signals.getSignal(FEATURE_ID_MOBILITY_KING).addStrength(index, -1, openingPart);
	}

	
	public void fillPassedPawnScores(ISignals signals) {
		
		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		int whitePromotionDistance = SHORT_MAX;
		int blackPromotionDistance = SHORT_MAX;

		// white passed pawns
		long passedPawns = evalinfo.passedPawnsAndOutposts & cb.getPieces(WHITE, ChessConstants.PAWN);
		while (passedPawns != 0) {
			final int index = 63 - Long.numberOfLeadingZeros(passedPawns);

			fillPassedPawnScore(index, WHITE, signals);

			if (whitePromotionDistance == SHORT_MAX) {
				whitePromotionDistance = getWhitePromotionDistance(index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~FILES[index & 7];
		}

		// black passed pawns
		passedPawns = evalinfo.passedPawnsAndOutposts & cb.getPieces(BLACK, ChessConstants.PAWN);
		while (passedPawns != 0) {
			final int index = Long.numberOfTrailingZeros(passedPawns);

			fillPassedPawnScore(index, BLACK, signals);

			if (blackPromotionDistance == SHORT_MAX) {
				blackPromotionDistance = getBlackPromotionDistance(index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~FILES[index & 7];
		}
		
		if (whitePromotionDistance < blackPromotionDistance - 1) {
			signals.getSignal(FEATURE_ID_PAWN_PASSED_UNSTOPPABLE).addStrength(PASSED_UNSTOPPABLE, openingPart);
		} else if (whitePromotionDistance > blackPromotionDistance + 1) {
			signals.getSignal(FEATURE_ID_PAWN_PASSED_UNSTOPPABLE).addStrength(-PASSED_UNSTOPPABLE, openingPart);
		}
	}
	
	
	protected void fillPassedPawnScore(final int index, final int color, ISignals signals) {

		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		final int nextIndex = index + ChessConstants.COLOR_FACTOR_8[color];
		final long square = POWER_LOOKUP[index];
		final long maskNextSquare = POWER_LOOKUP[nextIndex];
		final long maskPreviousSquare = POWER_LOOKUP[index - ChessConstants.COLOR_FACTOR_8[color]];
		final long maskFile = FILES[index & 7];
		final int enemyColor = 1 - color;
		float multiplier = 1;

		// is piece blocked?
		if ((cb.getAllPieces() & maskNextSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[0];
		}

		// is next squared attacked?
		if ((evalinfo.attacksAll[enemyColor] & maskNextSquare) == 0) {

			// complete path free of enemy attacks?
			if ((ChessConstants.PINNED_MOVEMENT[nextIndex][index] & evalinfo.attacksAll[enemyColor]) == 0) {
				multiplier *= PASSED_MULTIPLIERS[7];
			} else {
				multiplier *= PASSED_MULTIPLIERS[1];
			}
		}

		// is next squared defended?
		if ((evalinfo.attacksAll[color] & maskNextSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[3];
		}

		// is enemy king in front?
		if ((ChessConstants.PINNED_MOVEMENT[nextIndex][index] & cb.getPieces(enemyColor, ChessConstants.KING)) != 0) {
			multiplier *= PASSED_MULTIPLIERS[2];
		}

		// under attack?
		if (cb.getColorToMove() != color && (evalinfo.attacksAll[enemyColor] & square) != 0) {
			multiplier *= PASSED_MULTIPLIERS[4];
		}

		// defended by rook from behind?
		if ((maskFile & cb.getPieces(color, ROOK)) != 0 && (evalinfo.attacks[color][ROOK] & square) != 0 && (evalinfo.attacks[color][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[5];
		}

		// attacked by rook from behind?
		else if ((maskFile & cb.getPieces(enemyColor, ROOK)) != 0 && (evalinfo.attacks[enemyColor][ROOK] & square) != 0
				&& (evalinfo.attacks[enemyColor][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[6];
		}

		// king tropism
		multiplier *= PASSED_KING_MULTI[getDistance(cb.getKingIndex(color), index)];
		multiplier *= PASSED_KING_MULTI[8 - getDistance(cb.getKingIndex(enemyColor), index)];

		final int scoreIndex = (7 * color) + ChessConstants.COLOR_FACTOR[color] * index / 8;
		
		if (color == WHITE) {			
			signals.getSignal(FEATURE_ID_PAWN_PASSED).addStrength(scoreIndex, multiplier, openingPart);
		} else {
			signals.getSignal(FEATURE_ID_PAWN_PASSED).addStrength(scoreIndex, -multiplier, openingPart);
			//-interpolateInternal(PASSED_SCORE_MG[scoreIndex] * multiplier, PASSED_SCORE_EG[scoreIndex] * multiplier, openingPart), openingPart
		}
	}
	
	
	public void fillThreats(ISignals signals) {
		
		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		final long whitePawns = cb.getPieces(WHITE, PAWN);
		final long blackPawns = cb.getPieces(BLACK, PAWN);
		final long whiteMinorAttacks = evalinfo.attacks[WHITE][NIGHT] | evalinfo.attacks[WHITE][BISHOP];
		final long blackMinorAttacks = evalinfo.attacks[BLACK][NIGHT] | evalinfo.attacks[BLACK][BISHOP];
		final long whitePawnAttacks = evalinfo.attacks[WHITE][PAWN];
		final long blackPawnAttacks = evalinfo.attacks[BLACK][PAWN];
		final long whiteAttacks = evalinfo.attacksAll[WHITE];
		final long blackAttacks = evalinfo.attacksAll[BLACK];
		final long whites = cb.getFriendlyPieces(WHITE);
		final long blacks = cb.getFriendlyPieces(BLACK);

		
		// double attacked pieces
		long piece = evalinfo.doubleAttacks[WHITE] & blacks;
		while (piece != 0) {
			int index = cb.getPieceType(Long.numberOfTrailingZeros(piece));
			signals.getSignal(FEATURE_ID_THREAT_DOUBLE_ATTACKED).addStrength(index, 1, openingPart);
			piece &= piece - 1;
		}
		piece = evalinfo.doubleAttacks[BLACK] & whites;
		while (piece != 0) {
			int index = cb.getPieceType(Long.numberOfTrailingZeros(piece));
			signals.getSignal(FEATURE_ID_THREAT_DOUBLE_ATTACKED).addStrength(index, -1, openingPart);
			piece &= piece - 1;
		}
		
		
		// unused outposts
		int count = Long.bitCount(evalinfo.passedPawnsAndOutposts & cb.getEmptySpaces() & whiteMinorAttacks & whitePawnAttacks);
		signals.getSignal(FEATURE_ID_THREAT_UNUSED_OUTPOST).addStrength(interpolateInternal(count * THREATS_MG[IX_UNUSED_OUTPOST], count * THREATS_EG[IX_UNUSED_OUTPOST], openingPart), openingPart);
		
		count = Long.bitCount(evalinfo.passedPawnsAndOutposts & cb.getEmptySpaces() & blackMinorAttacks & blackPawnAttacks);
		signals.getSignal(FEATURE_ID_THREAT_UNUSED_OUTPOST).addStrength(-interpolateInternal(count * THREATS_MG[IX_UNUSED_OUTPOST], count * THREATS_EG[IX_UNUSED_OUTPOST], openingPart), openingPart);
		
		
		// pawn push threat
		piece = (whitePawns << 8) & cb.getEmptySpaces() & ~blackAttacks;
		count = Long.bitCount(getWhitePawnAttacks(piece) & blacks);
		signals.getSignal(FEATURE_ID_THREAT_PAWN_PUSH).addStrength(interpolateInternal(count * THREATS_MG[IX_PAWN_PUSH_THREAT], count * THREATS_EG[IX_PAWN_PUSH_THREAT], openingPart), openingPart);
		
		piece = (blackPawns >>> 8) & cb.getEmptySpaces() & ~whiteAttacks;
		count = Long.bitCount(getBlackPawnAttacks(piece) & whites);
		signals.getSignal(FEATURE_ID_THREAT_PAWN_PUSH).addStrength(-interpolateInternal(count * THREATS_MG[IX_PAWN_PUSH_THREAT], count * THREATS_EG[IX_PAWN_PUSH_THREAT], openingPart), openingPart);
		
		
		// piece is attacked by a pawn
		count = Long.bitCount(whitePawnAttacks & blacks & ~blackPawns);
		signals.getSignal(FEATURE_ID_THREAT_PAWN_ATTACKS).addStrength(interpolateInternal(count * THREATS_MG[IX_PAWN_ATTACKS], count * THREATS_EG[IX_PAWN_ATTACKS], openingPart), openingPart);
		
		count = Long.bitCount(blackPawnAttacks & whites & ~whitePawns);
		signals.getSignal(FEATURE_ID_THREAT_PAWN_ATTACKS).addStrength(-interpolateInternal(count * THREATS_MG[IX_PAWN_ATTACKS], count * THREATS_EG[IX_PAWN_ATTACKS], openingPart), openingPart);
		
		
		// multiple pawn attacks possible
		if (Long.bitCount(whitePawnAttacks & blacks) > 1) {
			signals.getSignal(FEATURE_ID_THREAT_MULTIPLE_PAWN_ATTACKS).addStrength(interpolateInternal(THREATS_MG[IX_MULTIPLE_PAWN_ATTACKS], THREATS_EG[IX_MULTIPLE_PAWN_ATTACKS], openingPart), openingPart);
		}
		if (Long.bitCount(blackPawnAttacks & whites) > 1) {
			signals.getSignal(FEATURE_ID_THREAT_MULTIPLE_PAWN_ATTACKS).addStrength(-interpolateInternal(THREATS_MG[IX_MULTIPLE_PAWN_ATTACKS], THREATS_EG[IX_MULTIPLE_PAWN_ATTACKS], openingPart), openingPart);
		}
		
		
		// minors under attack and not defended by a pawn
		count = Long.bitCount(whiteAttacks & (cb.getPieces(BLACK, NIGHT) | cb.getPieces(BLACK, BISHOP) & ~blackAttacks));
		signals.getSignal(FEATURE_ID_THREAT_MAJOR_ATTACKED).addStrength(interpolateInternal(count * THREATS_MG[IX_MAJOR_ATTACKED], count * THREATS_EG[IX_MAJOR_ATTACKED], openingPart), openingPart);
		
		count = Long.bitCount(blackAttacks & (cb.getPieces(WHITE, NIGHT) | cb.getPieces(WHITE, BISHOP) & ~whiteAttacks));
		signals.getSignal(FEATURE_ID_THREAT_MAJOR_ATTACKED).addStrength(-interpolateInternal(count * THREATS_MG[IX_MAJOR_ATTACKED], count * THREATS_EG[IX_MAJOR_ATTACKED], openingPart), openingPart);
		
		
		// pawn attacked
		count = Long.bitCount(whiteAttacks & blackPawns);
		signals.getSignal(FEATURE_ID_THREAT_PAWN_ATTACKED).addStrength(interpolateInternal(count * THREATS_MG[IX_PAWN_ATTACKED], count * THREATS_EG[IX_PAWN_ATTACKED], openingPart), openingPart);
		
		count = Long.bitCount(blackAttacks & whitePawns);
		signals.getSignal(FEATURE_ID_THREAT_PAWN_ATTACKED).addStrength(-interpolateInternal(count * THREATS_MG[IX_PAWN_ATTACKED], count * THREATS_EG[IX_PAWN_ATTACKED], openingPart), openingPart);
		
		
		if (cb.getPieces(BLACK, QUEEN) != 0) {
			// queen under attack by rook
			count = Long.bitCount(evalinfo.attacks[WHITE][ROOK] & cb.getPieces(BLACK, QUEEN));
			signals.getSignal(FEATURE_ID_THREAT_QUEEN_ATTACKED_ROOK).addStrength(interpolateInternal(count * THREATS_MG[IX_QUEEN_ATTACKED], count * THREATS_EG[IX_QUEEN_ATTACKED], openingPart), openingPart);
			
			// queen under attack by minors
			count = Long.bitCount(whiteMinorAttacks & cb.getPieces(BLACK, QUEEN));
			signals.getSignal(FEATURE_ID_THREAT_QUEEN_ATTACKED_MINOR).addStrength(interpolateInternal(count * THREATS_MG[IX_QUEEN_ATTACKED_MINOR], count * THREATS_EG[IX_QUEEN_ATTACKED_MINOR], openingPart), openingPart);
		}

		if (cb.getPieces(WHITE, QUEEN) != 0) {
			// queen under attack by rook
			count = Long.bitCount(evalinfo.attacks[BLACK][ROOK] & cb.getPieces(WHITE, QUEEN));
			signals.getSignal(FEATURE_ID_THREAT_QUEEN_ATTACKED_ROOK).addStrength(-interpolateInternal(count * THREATS_MG[IX_QUEEN_ATTACKED], count * THREATS_EG[IX_QUEEN_ATTACKED], openingPart), openingPart);
			
			// queen under attack by minors
			count = Long.bitCount(blackMinorAttacks & cb.getPieces(WHITE, QUEEN));
			signals.getSignal(FEATURE_ID_THREAT_QUEEN_ATTACKED_MINOR).addStrength(-interpolateInternal(count * THREATS_MG[IX_QUEEN_ATTACKED_MINOR], count * THREATS_EG[IX_QUEEN_ATTACKED_MINOR], openingPart), openingPart);
		}
		
		
		// rook under attack by minors
		count = Long.bitCount(whiteMinorAttacks & cb.getPieces(BLACK, ROOK));
		signals.getSignal(FEATURE_ID_THREAT_ROOK_ATTACKED).addStrength(interpolateInternal(count * THREATS_MG[IX_ROOK_ATTACKED], count * THREATS_EG[IX_ROOK_ATTACKED], openingPart), openingPart);
		
		count = Long.bitCount(blackMinorAttacks & cb.getPieces(WHITE, ROOK));
		signals.getSignal(FEATURE_ID_THREAT_ROOK_ATTACKED).addStrength(-interpolateInternal(count * THREATS_MG[IX_ROOK_ATTACKED], count * THREATS_EG[IX_ROOK_ATTACKED], openingPart), openingPart);
		

		// knight fork
		// skip when testing eval values because we break the loop if any fork has been found
		long forked;
		piece = evalinfo.attacks[WHITE][NIGHT] & ~blackAttacks & cb.getEmptySpaces();
		while (piece != 0) {
			forked = blacks & ~blackPawns & KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
			if (Long.bitCount(forked) > 1) {
				if ((cb.getPieces(BLACK, KING) & forked) == 0) {
					signals.getSignal(FEATURE_ID_THREAT_NIGHT_FORK).addStrength(interpolateInternal(THREATS_MG[IX_NIGHT_FORK], THREATS_EG[IX_NIGHT_FORK], openingPart), openingPart);
				} else {
					signals.getSignal(FEATURE_ID_THREAT_NIGHT_FORK_KING).addStrength(interpolateInternal(THREATS_MG[IX_NIGHT_FORK_KING], THREATS_EG[IX_NIGHT_FORK_KING], openingPart), openingPart);
				}
				break;
			}
			piece &= piece - 1;
		}
		piece = evalinfo.attacks[BLACK][NIGHT] & ~whiteAttacks & cb.getEmptySpaces();
		while (piece != 0) {
			forked = whites & ~whitePawns & KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
			if (Long.bitCount(forked) > 1) {
				if ((cb.getPieces(WHITE, KING) & forked) == 0) {
					signals.getSignal(FEATURE_ID_THREAT_NIGHT_FORK).addStrength(-interpolateInternal(THREATS_MG[IX_NIGHT_FORK], THREATS_EG[IX_NIGHT_FORK], openingPart), openingPart);
				} else {
					signals.getSignal(FEATURE_ID_THREAT_NIGHT_FORK_KING).addStrength(-interpolateInternal(THREATS_MG[IX_NIGHT_FORK_KING], THREATS_EG[IX_NIGHT_FORK_KING], openingPart), openingPart);
				}
				break;
			}
			piece &= piece - 1;
		}
	}
	
	
	public void fillPawnShieldBonus(ISignals signals) {

		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		int file;

		int whiteScore_o = 0;
		int whiteScore_e = 0;
		long piece = cb.getPieces(WHITE, PAWN) & cb.getKingArea(WHITE) & ~evalinfo.attacks[BLACK][PAWN];
		while (piece != 0) {
			file = Long.numberOfTrailingZeros(piece) & 7;
			whiteScore_o += SHIELD_BONUS_MG[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3];
			whiteScore_e += SHIELD_BONUS_EG[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= ~FILES[file];
		}
		if (cb.getPieces(BLACK, QUEEN) == 0) {
			whiteScore_o /= 2;
			whiteScore_e /= 2;
		}

		int blackScore_o = 0;
		int blackScore_e = 0;
		piece = cb.getPieces(BLACK, PAWN) & cb.getKingArea(BLACK) & ~evalinfo.attacks[WHITE][PAWN];
		while (piece != 0) {
			file = (63 - Long.numberOfLeadingZeros(piece)) & 7;
			blackScore_o += SHIELD_BONUS_MG[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8];
			blackScore_e += SHIELD_BONUS_EG[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8];
			piece &= ~FILES[file];
		}
		if (cb.getPieces(WHITE, QUEEN) == 0) {
			whiteScore_o /= 2;
			whiteScore_e /= 2;
		}
		
		signals.getSignal(FEATURE_ID_PAWN_SHIELD).addStrength(interpolateInternal((whiteScore_o - blackScore_o), (whiteScore_e - blackScore_e), openingPart), openingPart);
	}
	
	
	public void fillOthers(ISignals signals) {
		
		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		long piece;

		final long whitePawns = cb.getPieces(WHITE, PAWN);
		final long blackPawns = cb.getPieces(BLACK, PAWN);
		final long whitePawnAttacks = evalinfo.attacks[WHITE][PAWN];
		final long blackPawnAttacks = evalinfo.attacks[BLACK][PAWN];
		final long whiteAttacks = evalinfo.attacksAll[WHITE];
		final long blackAttacks = evalinfo.attacksAll[BLACK];
		final long whites = cb.getFriendlyPieces(WHITE);
		final long blacks = cb.getFriendlyPieces(BLACK);
		
		
		// bonus for side to move
		if (cb.getColorToMove() == WHITE) {
			signals.getSignal(FEATURE_ID_OTHERS_SIDE_TO_MOVE).addStrength(SIDE_TO_MOVE_BONUS, openingPart);
		} else {
			signals.getSignal(FEATURE_ID_OTHERS_SIDE_TO_MOVE).addStrength(-SIDE_TO_MOVE_BONUS, openingPart);
		}
		
		
		int value;
		
		
		// piece attacked and only defended by a rook or queen
		piece = whites & blackAttacks & whiteAttacks & ~(whitePawnAttacks | evalinfo.attacks[WHITE][NIGHT] | evalinfo.attacks[WHITE][BISHOP]);
		while (piece != 0) {
			signals.getSignal(FEATURE_ID_OTHERS_ONLY_MAJOR_DEFENDERS).addStrength(cb.getPieceType(Long.numberOfTrailingZeros(piece)), -1, openingPart);
			
			piece &= piece - 1;
		}
		
		piece = blacks & whiteAttacks & blackAttacks & ~(blackPawnAttacks | evalinfo.attacks[BLACK][NIGHT] | evalinfo.attacks[BLACK][BISHOP]);
		while (piece != 0) {
			signals.getSignal(FEATURE_ID_OTHERS_ONLY_MAJOR_DEFENDERS).addStrength(cb.getPieceType(Long.numberOfTrailingZeros(piece)), 1, openingPart);
			
			piece &= piece - 1;
		}
		
		
		// hanging pieces
		piece = whiteAttacks & blacks & ~blackAttacks;
		int hangingIndex;
		if (piece != 0) {
			if (Long.bitCount(piece) > 1) {
				hangingIndex = ChessConstants.QUEEN;
				while (piece != 0) {
					hangingIndex = Math.min(hangingIndex, cb.getPieceType(Long.numberOfTrailingZeros(piece)));
					piece &= piece - 1;
				}

				signals.getSignal(FEATURE_ID_OTHERS_HANGING_2).addStrength(hangingIndex, 1, openingPart);
				
			} else {
				signals.getSignal(FEATURE_ID_OTHERS_HANGING).addStrength(cb.getPieceType(Long.numberOfTrailingZeros(piece)), 1, openingPart);
			}
		}
		piece = blackAttacks & whites & ~whiteAttacks;
		if (piece != 0) {
			if (Long.bitCount(piece) > 1) {
				hangingIndex = ChessConstants.QUEEN;
				while (piece != 0) {
					hangingIndex = Math.min(hangingIndex, cb.getPieceType(Long.numberOfTrailingZeros(piece)));
					piece &= piece - 1;
				}

				signals.getSignal(FEATURE_ID_OTHERS_HANGING_2).addStrength(hangingIndex, -1, openingPart);
				
			} else {
				signals.getSignal(FEATURE_ID_OTHERS_HANGING).addStrength(cb.getPieceType(Long.numberOfTrailingZeros(piece)), -1, openingPart);
			}
		}
		
		
		// WHITE ROOK
		if (cb.getPieces(WHITE, ROOK) != 0) {

			piece = cb.getPieces(WHITE, ROOK);

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					signals.getSignal(FEATURE_ID_OTHERS_ROOK_BATTERY).addStrength(OTHER_SCORES[IX_ROOK_BATTERY], openingPart);
				}
			}

			// rook on 7th, king on 8th
			if (cb.getKingIndex(BLACK) >= 56) {
				value = Long.bitCount(piece & RANK_7) * OTHER_SCORES[IX_ROOK_7TH_RANK];
				signals.getSignal(FEATURE_ID_OTHERS_ROOK_7TH_RANK).addStrength(value, openingPart);
			}

			// prison
			final long trapped = piece & ROOK_PRISON[cb.getKingIndex(WHITE)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped << i & whitePawns) != 0) {
						signals.getSignal(FEATURE_ID_OTHERS_ROOK_TRAPPED).addStrength(-ROOK_TRAPPED[(i / 8) - 1], openingPart);
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				if ((whitePawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((blackPawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						
						signals.getSignal(FEATURE_ID_OTHERS_ROOK_FILE_OPEN).addStrength(OTHER_SCORES[IX_ROOK_FILE_OPEN], openingPart);
						
					} else if ((blackPawns & blackPawnAttacks & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						
						signals.getSignal(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED).addStrength(OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED], openingPart);
						
					} else {
						
						signals.getSignal(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN).addStrength(OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN], openingPart);
					}
				}

				piece &= piece - 1;
			}
		}
		
		
		// BLACK ROOK
		if (cb.getPieces(BLACK, ROOK) != 0) {

			piece = cb.getPieces(BLACK, ROOK);

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					signals.getSignal(FEATURE_ID_OTHERS_ROOK_BATTERY).addStrength(-OTHER_SCORES[IX_ROOK_BATTERY], openingPart);
				}
			}

			// rook on 2nd, king on 1st
			if (cb.getKingIndex(WHITE) <= 7) {
				value = Long.bitCount(piece & RANK_2) * OTHER_SCORES[IX_ROOK_7TH_RANK];
				signals.getSignal(FEATURE_ID_OTHERS_ROOK_7TH_RANK).addStrength(-value, openingPart);
			}

			// prison
			final long trapped = piece & ROOK_PRISON[cb.getKingIndex(BLACK)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped >>> i & blackPawns) != 0) {
						signals.getSignal(FEATURE_ID_OTHERS_ROOK_TRAPPED).addStrength(ROOK_TRAPPED[(i / 8) - 1], openingPart);
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				// TODO JITWatch unpredictable branch
				if ((blackPawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((whitePawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						
						signals.getSignal(FEATURE_ID_OTHERS_ROOK_FILE_OPEN).addStrength(-OTHER_SCORES[IX_ROOK_FILE_OPEN], openingPart);
						
					} else if ((whitePawns & whitePawnAttacks & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						
						signals.getSignal(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED).addStrength(-OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED], openingPart);
						
					} else {
						
						signals.getSignal(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN).addStrength(-OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN], openingPart);
					}
				}
				piece &= piece - 1;
			}

		}
		
		
		// WHITE BISHOP
		if (cb.getPieces(WHITE, BISHOP) != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.getPieces(WHITE, BISHOP) & evalinfo.passedPawnsAndOutposts & whitePawnAttacks;
			while (piece != 0) {
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_OUTPOST).addStrength(Long.numberOfTrailingZeros(piece) >>> 3, 1, openingPart);
				
				piece &= piece - 1;
			}

			// prison
			piece = cb.getPieces(WHITE, BISHOP);
			while (piece != 0) {
				if (Long.bitCount((BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & blackPawns) == 2) {
					signals.getSignal(FEATURE_ID_OTHERS_BISHOP_PRISON).addStrength(-OTHER_SCORES[IX_BISHOP_PRISON], openingPart);
				}
				piece &= piece - 1;
			}

			if ((cb.getPieces(WHITE, BISHOP) & WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_PAWNS).addStrength(Long.bitCount(whitePawns & WHITE_SQUARES), -1, openingPart);
				
				// bonus for attacking center squares
				value = Long.bitCount(evalinfo.attacks[WHITE][BISHOP] & E4_D5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK).addStrength(value, openingPart);
			}
			if ((cb.getPieces(WHITE, BISHOP) & BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_PAWNS).addStrength(Long.bitCount(whitePawns & BLACK_SQUARES), -1, openingPart);
				
				// bonus for attacking center squares 
				value = Long.bitCount(evalinfo.attacks[WHITE][BISHOP] & D4_E5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK).addStrength(value, openingPart);
			}

		}
		
		
		// BLACK BISHOP
		if (cb.getPieces(BLACK, BISHOP) != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.getPieces(BLACK, BISHOP) & evalinfo.passedPawnsAndOutposts & blackPawnAttacks;
			while (piece != 0) { 
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_OUTPOST).addStrength(7 - Long.numberOfTrailingZeros(piece) / 8, -1, openingPart);
				
				piece &= piece - 1;
			}

			// prison
			piece = cb.getPieces(BLACK, BISHOP);
			while (piece != 0) {
				if (Long.bitCount((BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & whitePawns) == 2) {
					signals.getSignal(FEATURE_ID_OTHERS_BISHOP_PRISON).addStrength(OTHER_SCORES[IX_BISHOP_PRISON], openingPart);
				}
				piece &= piece - 1;
			}

			if ((cb.getPieces(BLACK, BISHOP) & WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_PAWNS).addStrength(Long.bitCount(blackPawns & WHITE_SQUARES), 1, openingPart);
				
				// bonus for attacking center squares 
				value = Long.bitCount(evalinfo.attacks[BLACK][BISHOP] & E4_D5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK).addStrength(-value, openingPart);
			}
			if ((cb.getPieces(BLACK, BISHOP) & BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_PAWNS).addStrength(Long.bitCount(blackPawns & BLACK_SQUARES), 1, openingPart);
				
				// bonus for attacking center squares
				value = Long.bitCount(evalinfo.attacks[BLACK][BISHOP] & D4_E5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
				signals.getSignal(FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK).addStrength(-value, openingPart);
			}
		}
		
		
		// pieces supporting our pawns
		piece = (whitePawns << 8) & whites;
		while (piece != 0) {
			signals.getSignal(FEATURE_ID_OTHERS_PAWN_BLOCKAGE).addStrength(Long.numberOfTrailingZeros(piece) >>> 3, 1, openingPart);
			
			piece &= piece - 1;
		}
		piece = (blackPawns >>> 8) & blacks;
		while (piece != 0) {
			signals.getSignal(FEATURE_ID_OTHERS_PAWN_BLOCKAGE).addStrength(7 - Long.numberOfTrailingZeros(piece) / 8, -1, openingPart);
			
			piece &= piece - 1;
		}
		
		
		// knight outpost: protected by a pawn, cannot be attacked by enemy pawns
		piece = cb.getPieces(WHITE, NIGHT) & evalinfo.passedPawnsAndOutposts & whitePawnAttacks;
		while (piece != 0) {
			signals.getSignal(FEATURE_ID_OTHERS_KNIGHT_OUTPOST).addStrength(Long.numberOfTrailingZeros(piece) >>> 3, 1, openingPart);
			
			piece &= piece - 1;
		}
		piece = cb.getPieces(BLACK, NIGHT) & evalinfo.passedPawnsAndOutposts & blackPawnAttacks;
		while (piece != 0) {
			signals.getSignal(FEATURE_ID_OTHERS_KNIGHT_OUTPOST).addStrength(7 - Long.numberOfTrailingZeros(piece) / 8, -1, openingPart);
			
			piece &= piece - 1;
		}
		
		
		// quiescence search could leave one side in check
		if (cb.getBoard().isInCheck()) {
			if (cb.getColorToMove() == WHITE) {
				signals.getSignal(FEATURE_ID_OTHERS_IN_CHECK).addStrength(-IN_CHECK, openingPart);
			} else {
				signals.getSignal(FEATURE_ID_OTHERS_IN_CHECK).addStrength(IN_CHECK, openingPart);
			}
		}
	}
	
	
	public void fillKingSafetyScores(ISignals signals) {

		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		for (int kingColor = WHITE; kingColor <= BLACK; kingColor++) {
			final int enemyColor = 1 - kingColor;

			if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, ROOK)) == 0) {
				continue;
			}

			int counter = KS_RANK[(7 * kingColor) + ChessConstants.COLOR_FACTOR[kingColor] * cb.getKingIndex(kingColor) / 8];

			counter += KS_NO_FRIENDS[Long.bitCount(cb.getKingArea(kingColor) & ~cb.getFriendlyPieces(kingColor))];
			counter += openFiles(kingColor, cb.getPieces(kingColor, PAWN));

			// king can move?
			if ((evalinfo.attacks[kingColor][KING] & ~cb.getFriendlyPieces(kingColor)) == 0) {
				counter++;
			}
			counter += KS_ATTACKS[Long.bitCount(cb.getKingArea(kingColor) & evalinfo.attacksAll[enemyColor])];
			counter += checks(kingColor);

			counter += KS_DOUBLE_ATTACKS[Long
					.bitCount(KING_MOVES[cb.getKingIndex(kingColor)] & evalinfo.doubleAttacks[enemyColor] & ~evalinfo.attacks[kingColor][PAWN])];

			if ((cb.getCheckingPieces() & cb.getFriendlyPieces(enemyColor)) != 0) {
				counter++;
			}

			// bonus for stm
			counter += 1 - cb.getColorToMove() ^ enemyColor;

			// bonus if there are discovered checks possible
			counter += Long.bitCount(cb.getDiscoveredPieces() & cb.getFriendlyPieces(enemyColor)) * 2;

			// pinned at first rank
			if ((cb.getPinnedPieces() & RANK_FIRST[kingColor]) != 0) {
				counter++;
			}

			if (cb.getPieces(enemyColor, QUEEN) == 0) {
				counter /= 2;
			} else if (Long.bitCount(cb.getPieces(enemyColor, QUEEN)) == 1) {
				// bonus for small king-queen distance
				if ((evalinfo.attacksAll[kingColor] & cb.getPieces(enemyColor, QUEEN)) == 0) {
					counter += KS_QUEEN_TROPISM[getDistance(cb.getKingIndex(kingColor),
							Long.numberOfTrailingZeros(cb.getPieces(enemyColor, QUEEN)))];
				}
			}

			counter += KS_ATTACK_PATTERN[evalinfo.kingAttackersFlag[enemyColor]];

			int value = ChessConstants.COLOR_FACTOR[enemyColor] * KS_SCORES[Math.min(counter, KS_SCORES.length - 1)];
			signals.getSignal(FEATURE_ID_KING_SAFETY).addStrength(value, openingPart);
		}
	}
	
	
	public void fillSpace(ISignals signals) {

		
		double openingPart = cb.getBoard().getMaterialFactor().getOpenningPart();
		
		
		int score = 0;

		score += OTHER_SCORES[IX_SPACE]
				* Long.bitCount((cb.getPieces(WHITE, PAWN) >>> 8) & (cb.getPieces(WHITE, NIGHT) | cb.getPieces(WHITE, BISHOP)) & RANK_234);
		score -= OTHER_SCORES[IX_SPACE]
				* Long.bitCount((cb.getPieces(BLACK, PAWN) << 8) & (cb.getPieces(BLACK, NIGHT) | cb.getPieces(BLACK, BISHOP)) & RANK_567);

		// idea taken from Laser
		long space = cb.getPieces(WHITE, PAWN) >>> 8;
		space |= space >>> 8 | space >>> 16;
		score += SPACE[Long.bitCount(cb.getFriendlyPieces(WHITE))]
				* Long.bitCount(space & ~cb.getPieces(WHITE, PAWN) & ~evalinfo.attacks[BLACK][PAWN] & FILE_CDEF);
		space = cb.getPieces(BLACK, PAWN) << 8;
		space |= space << 8 | space << 16;
		score -= SPACE[Long.bitCount(cb.getFriendlyPieces(BLACK))]
				* Long.bitCount(space & ~cb.getPieces(BLACK, PAWN) & ~evalinfo.attacks[WHITE][PAWN] & FILE_CDEF);

		
		signals.getSignal(FEATURE_ID_SPACE).addStrength(score, openingPart);
	}
}

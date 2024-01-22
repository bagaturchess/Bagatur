package bagaturchess.learning.goldmiddle.impl4.base;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;

import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.impl1.internal.Bitboard;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MagicUtil;
import bagaturchess.bitboard.impl1.internal.StaticMoves;
import bagaturchess.bitboard.impl1.internal.Util;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_V20_FeaturesConstants;
import bagaturchess.search.api.IEvalConfig;


/**
 * The same logic as in Evaluator but doesn't use IEvalComponentsProcessor for faster calculations
 *
 */
public class Evaluator_Fast implements Bagatur_V20_FeaturesConstants, FeatureWeights {
	
	
	private static final int MAX_MATERIAL_FACTOR = 4 * EvalConstants.PHASE[NIGHT] + 4 * EvalConstants.PHASE[BISHOP] + 4 * EvalConstants.PHASE[ROOK] + 2 * EvalConstants.PHASE[QUEEN];
	
	
	public static int eval1(IBoardConfig boardConfig, final ChessBoard cb, final EvalInfo evalInfo) {
		
		evalInfo.eval_o_part1 += cb.psqtScore_mg;
		evalInfo.eval_e_part1 += cb.psqtScore_eg;
		
		calculateMaterialScore(boardConfig, evalInfo);
		
		calculateImbalances(evalInfo);
		
		int total_material_factor = Math.min(MAX_MATERIAL_FACTOR, cb.material_factor_white + cb.material_factor_black);
		
		return (int) (evalInfo.eval_o_part1 * total_material_factor + evalInfo.eval_e_part1 * (MAX_MATERIAL_FACTOR - total_material_factor)) / MAX_MATERIAL_FACTOR;
	}
	
	
	public static int eval2(final ChessBoard cb, final EvalInfo evalInfo) {
		
		calculatePawnScores(evalInfo);
		
		evalInfo.clearAttacks();
		evalInfo.updatePawnAttacks();
		
		calculateMobilityScoresAndSetAttacks(evalInfo);
		
		int total_material_factor = Math.min(MAX_MATERIAL_FACTOR, cb.material_factor_white + cb.material_factor_black);
		
		return (int) (evalInfo.eval_o_part2 * total_material_factor + evalInfo.eval_e_part2 * (MAX_MATERIAL_FACTOR - total_material_factor)) / MAX_MATERIAL_FACTOR;
	}
	
	
	public static int eval3(final ChessBoard cb, final EvalInfo evalInfo) {
		
		calculatePawnShieldBonus(evalInfo);
		calculateKingSafetyScores(evalInfo);
		
		int total_material_factor = Math.min(MAX_MATERIAL_FACTOR, cb.material_factor_white + cb.material_factor_black);
		
		return (int) (evalInfo.eval_o_part3 * total_material_factor + evalInfo.eval_e_part3 * (MAX_MATERIAL_FACTOR - total_material_factor)) / MAX_MATERIAL_FACTOR;
	}
	
	
	public static int eval4(final ChessBoard cb, final EvalInfo evalInfo) {
		
		calculatePassedPawnScores(evalInfo);
		
		int total_material_factor = Math.min(MAX_MATERIAL_FACTOR, cb.material_factor_white + cb.material_factor_black);
		
		return (int) (evalInfo.eval_o_part4 * total_material_factor + evalInfo.eval_e_part4 * (MAX_MATERIAL_FACTOR - total_material_factor)) / MAX_MATERIAL_FACTOR;
	}
	
	
	public static int eval5(final ChessBoard cb, final EvalInfo evalInfo) {
		
		calculateThreats(cb, evalInfo);
		calculateSpace(evalInfo);
		calculateOthers(cb, evalInfo);
		
		int total_material_factor = Math.min(MAX_MATERIAL_FACTOR, cb.material_factor_white + cb.material_factor_black);
		
		return (int) (evalInfo.eval_o_part5 * total_material_factor + evalInfo.eval_e_part5 * (MAX_MATERIAL_FACTOR - total_material_factor)) / MAX_MATERIAL_FACTOR;
	}
	
	
	private static void calculateSpace(final EvalInfo evalInfo) {

		int score1 = 0;
		score1 += EvalConstants.OTHER_SCORES[EvalConstants.IX_SPACE]
				* Long.bitCount((evalInfo.bb_w_pawns >>> 8) & (evalInfo.bb_w_knights | evalInfo.bb_w_bishops) & Bitboard.RANK_234);
		score1 -= EvalConstants.OTHER_SCORES[EvalConstants.IX_SPACE]
				* Long.bitCount((evalInfo.bb_b_pawns << 8) & (evalInfo.bb_b_knights | evalInfo.bb_b_bishops) & Bitboard.RANK_567);
		
		// idea taken from Laser
		int score2 = 0;
		long space = evalInfo.bb_w_pawns >>> 8;
		space |= space >>> 8 | space >>> 16;
		score2 += EvalConstants.SPACE[Math.min(Long.bitCount(evalInfo.getFriendlyPieces(WHITE)), EvalConstants.SPACE.length - 1)]
				* Long.bitCount(space & ~evalInfo.bb_w_pawns & ~evalInfo.attacks[BLACK][PAWN] & Bitboard.FILE_CDEF);
		space = evalInfo.bb_b_pawns << 8;
		space |= space << 8 | space << 16;
		score2 -= EvalConstants.SPACE[Math.min(Long.bitCount(evalInfo.getFriendlyPieces(BLACK)), EvalConstants.SPACE.length - 1)]
				* Long.bitCount(space & ~evalInfo.bb_b_pawns & ~evalInfo.attacks[WHITE][PAWN] & Bitboard.FILE_CDEF);
		
		evalInfo.eval_o_part5 += score1 + score2;
		evalInfo.eval_e_part5 += 0;
	}

	
	private static void calculatePawnScores(final EvalInfo evalInfo) {

		// penalty for doubled pawns
		for (int i = 0; i < 8; i++) {
			if (Long.bitCount(evalInfo.bb_w_pawns & Bitboard.FILES[i]) > 1) {
				int eval = -EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_DOUBLE];
				evalInfo.eval_o_part2 += eval;
				evalInfo.eval_e_part2 += eval;

			}
			if (Long.bitCount(evalInfo.bb_b_pawns & Bitboard.FILES[i]) > 1) {
				int eval = +EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_DOUBLE];
				evalInfo.eval_o_part2 += eval;
				evalInfo.eval_e_part2 += eval;
			}
		}

		// bonus for connected pawns
		long pawns = Bitboard.getWhitePawnAttacks(evalInfo.bb_w_pawns) & evalInfo.bb_w_pawns;
		while (pawns != 0) {
			int eval = +EvalConstants.PAWN_CONNECTED[Long.numberOfTrailingZeros(pawns) / 8];
			evalInfo.eval_o_part2 += eval;
			evalInfo.eval_e_part2 += eval;
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getBlackPawnAttacks(evalInfo.bb_b_pawns) & evalInfo.bb_b_pawns;
		while (pawns != 0) {
			int eval = -EvalConstants.PAWN_CONNECTED[7 - Long.numberOfTrailingZeros(pawns) / 8];
			evalInfo.eval_o_part2 += eval;
			evalInfo.eval_e_part2 += eval;
			pawns &= pawns - 1;
		}

		// bonus for neighbor pawns
		pawns = Bitboard.getPawnNeighbours(evalInfo.bb_w_pawns) & evalInfo.bb_w_pawns;
		while (pawns != 0) {
			int eval = +EvalConstants.PAWN_NEIGHBOUR[Long.numberOfTrailingZeros(pawns) / 8];
			evalInfo.eval_o_part2 += eval;
			evalInfo.eval_e_part2 += eval;
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getPawnNeighbours(evalInfo.bb_b_pawns) & evalInfo.bb_b_pawns;
		while (pawns != 0) {
			int eval = -EvalConstants.PAWN_NEIGHBOUR[7 - Long.numberOfTrailingZeros(pawns) / 8];
			evalInfo.eval_o_part2 += eval;
			evalInfo.eval_e_part2 += eval;
			pawns &= pawns - 1;
		}

		// set outposts
		evalInfo.passedPawnsAndOutposts = 0;
		pawns = Bitboard.getWhitePawnAttacks(evalInfo.bb_w_pawns) & ~evalInfo.bb_w_pawns & ~evalInfo.bb_b_pawns;
		while (pawns != 0) {
			if ((Bitboard.getWhiteAdjacentMask(Long.numberOfTrailingZeros(pawns)) & evalInfo.bb_b_pawns) == 0) {
				evalInfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getBlackPawnAttacks(evalInfo.bb_b_pawns) & ~evalInfo.bb_w_pawns & ~evalInfo.bb_b_pawns;
		while (pawns != 0) {
			if ((Bitboard.getBlackAdjacentMask(Long.numberOfTrailingZeros(pawns)) & evalInfo.bb_w_pawns) == 0) {
				evalInfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}

		int index;

		// white
		pawns = evalInfo.bb_w_pawns;
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);

			// isolated pawns
			if ((Bitboard.FILES_ADJACENT[index & 7] & evalInfo.bb_w_pawns) == 0) {
				int eval = -EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_ISOLATED];
				evalInfo.eval_o_part2 += eval;
				evalInfo.eval_e_part2 += eval;
			}

			// backward pawns
			else if ((Bitboard.getBlackAdjacentMask(index + 8) & evalInfo.bb_w_pawns) == 0) {
				if ((StaticMoves.PAWN_ATTACKS[WHITE][index + 8] & evalInfo.bb_b_pawns) != 0) {
					if ((Bitboard.FILES[index & 7] & evalInfo.bb_b_pawns) == 0) {
						int eval = -EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_BACKWARD];
						evalInfo.eval_o_part2 += eval;
						evalInfo.eval_e_part2 += eval;
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(StaticMoves.PAWN_ATTACKS[WHITE][index] & evalInfo.bb_w_pawns) == 2) {
				int eval = -EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_INVERSE];
				evalInfo.eval_o_part2 += eval;
				evalInfo.eval_e_part2 += eval;
			}

			// set passed pawns
			if ((Bitboard.getWhitePassedPawnMask(index) & evalInfo.bb_b_pawns) == 0) {
				evalInfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passed pawns (no pawns in front, more friendly pawns behind and adjacent than enemy pawns)
			else if (63 - Long.numberOfLeadingZeros((evalInfo.bb_w_pawns | evalInfo.bb_b_pawns) & Bitboard.FILES[index & 7]) == index) {
				if (Long.bitCount(evalInfo.bb_w_pawns & Bitboard.getBlackPassedPawnMask(index + 8)) >= Long
						.bitCount(evalInfo.bb_b_pawns & Bitboard.getWhitePassedPawnMask(index))) {
					int eval = +EvalConstants.PASSED_CANDIDATE[index / 8];
					evalInfo.eval_o_part2 += eval;
					evalInfo.eval_e_part2 += eval;
				}
			}

			pawns &= pawns - 1;
		}

		// black
		pawns = evalInfo.bb_b_pawns;
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);
			
			// isolated pawns
			if ((Bitboard.FILES_ADJACENT[index & 7] & evalInfo.bb_b_pawns) == 0) {
				int eval = +EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_ISOLATED];
				evalInfo.eval_o_part2 += eval;
				evalInfo.eval_e_part2 += eval;
			}

			// backward pawns
			else if ((Bitboard.getWhiteAdjacentMask(index - 8) & evalInfo.bb_b_pawns) == 0) {
				if ((StaticMoves.PAWN_ATTACKS[BLACK][index - 8] & evalInfo.bb_w_pawns) != 0) {
					if ((Bitboard.FILES[index & 7] & evalInfo.bb_w_pawns) == 0) {
						int eval = +EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_BACKWARD];
						evalInfo.eval_o_part2 += eval;
						evalInfo.eval_e_part2 += eval;
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(StaticMoves.PAWN_ATTACKS[BLACK][index] & evalInfo.bb_b_pawns) == 2) {
				int eval = +EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_INVERSE];
				evalInfo.eval_o_part2 += eval;
				evalInfo.eval_e_part2 += eval;
			}

			// set passed pawns
			if ((Bitboard.getBlackPassedPawnMask(index) & evalInfo.bb_w_pawns) == 0) {
				evalInfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passers
			else if (Long.numberOfTrailingZeros((evalInfo.bb_w_pawns | evalInfo.bb_b_pawns) & Bitboard.FILES[index & 7]) == index) {
				if (Long.bitCount(evalInfo.bb_b_pawns & Bitboard.getWhitePassedPawnMask(index - 8)) >= Long
						.bitCount(evalInfo.bb_w_pawns & Bitboard.getBlackPassedPawnMask(index))) {
					int eval = -EvalConstants.PASSED_CANDIDATE[7 - index / 8];
					evalInfo.eval_o_part2 += eval;
					evalInfo.eval_e_part2 += eval;
				}
			}

			pawns &= pawns - 1;
		}
	}
	
	
	private static void calculateImbalances(final EvalInfo evalInfo) {
		
		int eval;
		
		// knights and pawns
		eval = +Long.bitCount(evalInfo.bb_w_knights) * EvalConstants.NIGHT_PAWN[Long.bitCount(evalInfo.bb_w_pawns)];
		eval -= Long.bitCount(evalInfo.bb_b_knights) * EvalConstants.NIGHT_PAWN[Long.bitCount(evalInfo.bb_b_pawns)];
		evalInfo.eval_o_part1 += eval;
		evalInfo.eval_e_part1 += eval;
		
		// rooks and pawns
		eval = +Long.bitCount(evalInfo.bb_w_rooks) * EvalConstants.ROOK_PAWN[Long.bitCount(evalInfo.bb_w_pawns)];
		eval -= Long.bitCount(evalInfo.bb_b_rooks) * EvalConstants.ROOK_PAWN[Long.bitCount(evalInfo.bb_b_pawns)];
		evalInfo.eval_o_part1 += eval;
		evalInfo.eval_e_part1 += eval;
		
		// double bishop
		if (Long.bitCount(evalInfo.bb_w_bishops) == 2) {
			eval = +EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_BISHOP_DOUBLE];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
		}
		if (Long.bitCount(evalInfo.bb_b_bishops) == 2) {
			eval = -EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_BISHOP_DOUBLE];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
		}

		// queen and nights
		if (evalInfo.bb_w_queens != 0) {
			eval = +Long.bitCount(evalInfo.bb_w_knights) * EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_QUEEN_NIGHT];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
		}
		if (evalInfo.bb_b_queens != 0) {
			eval = -Long.bitCount(evalInfo.bb_b_knights) * EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_QUEEN_NIGHT];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
		}

		// rook pair
		if (Long.bitCount(evalInfo.bb_w_rooks) > 1) {
			eval = +EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_ROOK_PAIR];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
		}
		if (Long.bitCount(evalInfo.bb_b_rooks) > 1) {
			eval = -EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_ROOK_PAIR];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
		}
	}

	
	private static void calculateThreats(final ChessBoard cb, final EvalInfo evalInfo) {
		
		final long whitePawns = evalInfo.bb_w_pawns;
		final long blackPawns = evalInfo.bb_b_pawns;
		final long whiteMinorAttacks = evalInfo.attacks[WHITE][NIGHT] | evalInfo.attacks[WHITE][BISHOP];
		final long blackMinorAttacks = evalInfo.attacks[BLACK][NIGHT] | evalInfo.attacks[BLACK][BISHOP];
		final long whitePawnAttacks = evalInfo.attacks[WHITE][PAWN];
		final long blackPawnAttacks = evalInfo.attacks[BLACK][PAWN];
		final long whiteAttacks = evalInfo.attacksAll[WHITE];
		final long blackAttacks = evalInfo.attacksAll[BLACK];
		final long whites = evalInfo.getFriendlyPieces(WHITE);
		final long blacks = evalInfo.getFriendlyPieces(BLACK);

		// double attacked pieces
		long piece = evalInfo.doubleAttacks[WHITE] & blacks;
		while (piece != 0) {
			int eval = +EvalConstants.DOUBLE_ATTACKED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
			evalInfo.eval_o_part5 += eval;
			evalInfo.eval_e_part5 += eval;
			piece &= piece - 1;
		}
		piece = evalInfo.doubleAttacks[BLACK] & whites;
		while (piece != 0) {
			int eval = -EvalConstants.DOUBLE_ATTACKED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
			evalInfo.eval_o_part5 += eval;
			evalInfo.eval_e_part5 += eval;
			piece &= piece - 1;
		}
		
		int count;
		
		if (MaterialUtil.hasPawns(evalInfo.materialKey)) {
			
			// unused outposts
			count = Long.bitCount(evalInfo.passedPawnsAndOutposts & evalInfo.bb_free & whiteMinorAttacks & whitePawnAttacks);
			evalInfo.eval_o_part5 += count * EvalConstants.THREATS_MG[EvalConstants.IX_UNUSED_OUTPOST];
			evalInfo.eval_e_part5 += count * EvalConstants.THREATS_EG[EvalConstants.IX_UNUSED_OUTPOST];
			
			count = Long.bitCount(evalInfo.passedPawnsAndOutposts & evalInfo.bb_free & blackMinorAttacks & blackPawnAttacks);
			evalInfo.eval_o_part5 -= count * EvalConstants.THREATS_MG[EvalConstants.IX_UNUSED_OUTPOST];
			evalInfo.eval_e_part5 -= count * EvalConstants.THREATS_EG[EvalConstants.IX_UNUSED_OUTPOST];
			
			// pawn push threat
			count = Long.bitCount(Bitboard.getWhitePawnAttacks((whitePawns << 8) & evalInfo.bb_free & ~blackAttacks) & blacks);
			evalInfo.eval_o_part5 += count * EvalConstants.THREATS_MG[EvalConstants.IX_PAWN_PUSH_THREAT];
			evalInfo.eval_e_part5 += count * EvalConstants.THREATS_EG[EvalConstants.IX_PAWN_PUSH_THREAT];
			
			count = Long.bitCount(Bitboard.getBlackPawnAttacks((blackPawns >>> 8) & evalInfo.bb_free & ~whiteAttacks) & whites);
			evalInfo.eval_o_part5 -= count * EvalConstants.THREATS_MG[EvalConstants.IX_PAWN_PUSH_THREAT];
			evalInfo.eval_e_part5 -= count * EvalConstants.THREATS_EG[EvalConstants.IX_PAWN_PUSH_THREAT];
			
			// piece attacked by pawn
			count = Long.bitCount(whitePawnAttacks & blacks & ~blackPawns);
			evalInfo.eval_o_part5 += count * EvalConstants.THREATS_MG[EvalConstants.IX_PAWN_ATTACKS];
			evalInfo.eval_e_part5 += count * EvalConstants.THREATS_EG[EvalConstants.IX_PAWN_ATTACKS];
			
			count  = Long.bitCount(blackPawnAttacks & whites & ~whitePawns);
			evalInfo.eval_o_part5 -= count * EvalConstants.THREATS_MG[EvalConstants.IX_PAWN_ATTACKS];
			evalInfo.eval_e_part5 -= count * EvalConstants.THREATS_EG[EvalConstants.IX_PAWN_ATTACKS];
			
			// multiple pawn attacks possible
			if (Long.bitCount(whitePawnAttacks & blacks) > 1) {
				evalInfo.eval_o_part5 += EvalConstants.THREATS_MG[EvalConstants.IX_MULTIPLE_PAWN_ATTACKS];
				evalInfo.eval_e_part5 += EvalConstants.THREATS_EG[EvalConstants.IX_MULTIPLE_PAWN_ATTACKS];
			}
			if (Long.bitCount(blackPawnAttacks & whites) > 1) {
				evalInfo.eval_o_part5 -= EvalConstants.THREATS_MG[EvalConstants.IX_MULTIPLE_PAWN_ATTACKS];
				evalInfo.eval_e_part5 -= EvalConstants.THREATS_EG[EvalConstants.IX_MULTIPLE_PAWN_ATTACKS];
			}
			
			// pawn attacked
			count = Long.bitCount(whiteAttacks & blackPawns);
			evalInfo.eval_o_part5 += count * EvalConstants.THREATS_MG[EvalConstants.IX_PAWN_ATTACKED];
			evalInfo.eval_e_part5 += count * EvalConstants.THREATS_EG[EvalConstants.IX_PAWN_ATTACKED];
			
			count = Long.bitCount(blackAttacks & whitePawns);
			evalInfo.eval_o_part5 -= count * EvalConstants.THREATS_MG[EvalConstants.IX_PAWN_ATTACKED];
			evalInfo.eval_e_part5 -= count * EvalConstants.THREATS_EG[EvalConstants.IX_PAWN_ATTACKED];
		}
		
		// minors attacked and not defended by a pawn
		count = Long.bitCount(whiteAttacks & (evalInfo.bb_b_knights | evalInfo.bb_b_bishops & ~blackAttacks));
		evalInfo.eval_o_part5 += count * EvalConstants.THREATS_MG[EvalConstants.IX_MAJOR_ATTACKED];
		evalInfo.eval_e_part5 += count * EvalConstants.THREATS_EG[EvalConstants.IX_MAJOR_ATTACKED];
		
		count = Long.bitCount(blackAttacks & (evalInfo.bb_w_knights | evalInfo.bb_w_bishops & ~whiteAttacks));
		evalInfo.eval_o_part5 -= count * EvalConstants.THREATS_MG[EvalConstants.IX_MAJOR_ATTACKED];
		evalInfo.eval_e_part5 -= count * EvalConstants.THREATS_EG[EvalConstants.IX_MAJOR_ATTACKED];
		
		if (evalInfo.bb_b_queens != 0) {
			// queen attacked by rook
			count = Long.bitCount(evalInfo.attacks[WHITE][ROOK] & evalInfo.bb_b_queens);
			evalInfo.eval_o_part5 += count * EvalConstants.THREATS_MG[EvalConstants.IX_QUEEN_ATTACKED];
			evalInfo.eval_e_part5 += count * EvalConstants.THREATS_EG[EvalConstants.IX_QUEEN_ATTACKED];
			
			// queen attacked by minors
			count = Long.bitCount(whiteMinorAttacks & evalInfo.bb_b_queens);
			evalInfo.eval_o_part5 += count * EvalConstants.THREATS_MG[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
			evalInfo.eval_e_part5 += count * EvalConstants.THREATS_EG[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
		}

		if (evalInfo.bb_w_queens != 0) {
			// queen attacked by rook
			count = Long.bitCount(evalInfo.attacks[BLACK][ROOK] & evalInfo.bb_w_queens);
			evalInfo.eval_o_part5 -= count * EvalConstants.THREATS_MG[EvalConstants.IX_QUEEN_ATTACKED];
			evalInfo.eval_e_part5 -= count * EvalConstants.THREATS_EG[EvalConstants.IX_QUEEN_ATTACKED];
			
			// queen attacked by minors
			count = Long.bitCount(blackMinorAttacks & evalInfo.bb_w_queens);
			evalInfo.eval_o_part5 -= count * EvalConstants.THREATS_MG[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
			evalInfo.eval_e_part5 -= count * EvalConstants.THREATS_EG[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
		}

		// rook attacked by minors
		count = Long.bitCount(whiteMinorAttacks & evalInfo.bb_b_rooks);
		evalInfo.eval_o_part5 += count * EvalConstants.THREATS_MG[EvalConstants.IX_ROOK_ATTACKED];
		evalInfo.eval_e_part5 += count * EvalConstants.THREATS_EG[EvalConstants.IX_ROOK_ATTACKED];
		
		count = Long.bitCount(blackMinorAttacks & evalInfo.bb_w_rooks);
		evalInfo.eval_o_part5 -= count * EvalConstants.THREATS_MG[EvalConstants.IX_ROOK_ATTACKED];
		evalInfo.eval_e_part5 -= count * EvalConstants.THREATS_EG[EvalConstants.IX_ROOK_ATTACKED];
	}
	
	
	private static void calculateOthers(final ChessBoard cb, final EvalInfo evalInfo) {
		
		long piece;

		final long whitePawns = evalInfo.bb_w_pawns;
		final long blackPawns = evalInfo.bb_b_pawns;
		final long whitePawnAttacks = evalInfo.attacks[WHITE][PAWN];
		final long blackPawnAttacks = evalInfo.attacks[BLACK][PAWN];
		final long whiteAttacks = evalInfo.attacksAll[WHITE];
		final long blackAttacks = evalInfo.attacksAll[BLACK];
		final long whites = evalInfo.getFriendlyPieces(WHITE);
		final long blacks = evalInfo.getFriendlyPieces(BLACK);

		int score;
		
		// side to move
		score = +ChessConstants.COLOR_FACTOR[evalInfo.colorToMove] * EvalConstants.SIDE_TO_MOVE_BONUS;
		evalInfo.eval_o_part5 += score;
		evalInfo.eval_e_part5 += score;
		
		// piece attacked and only defended by a rook or queen
		piece = whites & blackAttacks & whiteAttacks & ~(whitePawnAttacks | evalInfo.attacks[WHITE][NIGHT] | evalInfo.attacks[WHITE][BISHOP]);
		if (piece != 0) {
			score = +Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ONLY_MAJOR_DEFENDERS];
			evalInfo.eval_o_part5 += score;
			evalInfo.eval_e_part5 += score;
		}
		piece = blacks & whiteAttacks & blackAttacks & ~(blackPawnAttacks | evalInfo.attacks[BLACK][NIGHT] | evalInfo.attacks[BLACK][BISHOP]);
		if (piece != 0) {
			score = -Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ONLY_MAJOR_DEFENDERS];
			evalInfo.eval_o_part5 += score;
			evalInfo.eval_e_part5 += score;
		}

		// WHITE ROOK
		if (evalInfo.bb_w_rooks != 0) {

			piece = evalInfo.bb_w_rooks;

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					score = +EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_BATTERY];
					evalInfo.eval_o_part5 += score;
					evalInfo.eval_e_part5 += score;
				}
			}

			// rook on 7th, king on 8th
			if (evalInfo.kingIndex[BLACK] >= 56 && (piece & Bitboard.RANK_7) != 0) {
				score = +Long.bitCount(piece & Bitboard.RANK_7) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_7TH_RANK];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
			}

			// prison
			if ((piece & Bitboard.RANK_1) != 0) {
				final long trapped = piece & EvalConstants.ROOK_PRISON[evalInfo.kingIndex[WHITE]];
				if (trapped != 0) {
					for (int i = 8; i <= 24; i += 8) {
						if ((trapped << i & whitePawns) != 0) {
							score = +EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_TRAPPED];
							evalInfo.eval_o_part5 += score;
							evalInfo.eval_e_part5 += score;
							break;
						}
					}
				}
			}

			// rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				if ((whitePawns & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((blackPawns & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score = +EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_OPEN];
						evalInfo.eval_o_part5 += score;
						evalInfo.eval_e_part5 += score;
					} else if ((blackPawns & blackPawnAttacks & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score = +EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
						evalInfo.eval_o_part5 += score;
						evalInfo.eval_e_part5 += score;
					} else {
						score = +EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_SEMI_OPEN];
						evalInfo.eval_o_part5 += score;
						evalInfo.eval_e_part5 += score;
					}
				}

				piece &= piece - 1;
			}
		}

		// BLACK ROOK
		if (evalInfo.bb_b_rooks != 0) {

			piece = evalInfo.bb_b_rooks;

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					score = -EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_BATTERY];
					evalInfo.eval_o_part5 += score;
					evalInfo.eval_e_part5 += score;
				}
			}

			// rook on 2nd, king on 1st
			if (evalInfo.kingIndex[WHITE] <= 7 && (piece & Bitboard.RANK_2) != 0) {
				score = -Long.bitCount(piece & Bitboard.RANK_2) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_7TH_RANK];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
			}

			// prison
			if ((piece & Bitboard.RANK_8) != 0) {
				final long trapped = piece & EvalConstants.ROOK_PRISON[evalInfo.kingIndex[BLACK]];
				if (trapped != 0) {
					for (int i = 8; i <= 24; i += 8) {
						if ((trapped >>> i & blackPawns) != 0) {
							score = -EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_TRAPPED];
							evalInfo.eval_o_part5 += score;
							evalInfo.eval_e_part5 += score;
							break;
						}
					}
				}
			}

			// rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				// TODO JITWatch unpredictable branch
				if ((blackPawns & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((whitePawns & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score = -EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_OPEN];
						evalInfo.eval_o_part5 += score;
						evalInfo.eval_e_part5 += score;
					} else if ((whitePawns & whitePawnAttacks & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score = -EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
						evalInfo.eval_o_part5 += score;
						evalInfo.eval_e_part5 += score;
					} else {
						score = -EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_SEMI_OPEN];
						evalInfo.eval_o_part5 += score;
						evalInfo.eval_e_part5 += score;
					}
				}
				piece &= piece - 1;
			}

		}

		// WHITE BISHOP
		if (evalInfo.bb_w_bishops != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = evalInfo.bb_w_bishops & evalInfo.passedPawnsAndOutposts & whitePawnAttacks;
			if (piece != 0) {
				score = +Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
			}

			piece = evalInfo.bb_w_bishops;
			if ((piece & Bitboard.WHITE_SQUARES) != 0) {
				// pawns on same color as bishop
				score = +EvalConstants.BISHOP_PAWN[Long.bitCount(whitePawns & Bitboard.WHITE_SQUARES)];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
				
				// attacking center squares
				if (Long.bitCount(evalInfo.attacks[WHITE][BISHOP] & Bitboard.E4_D5) == 2) {
					score = +EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
					evalInfo.eval_o_part5 += score;
					evalInfo.eval_e_part5 += score;
				}
			}
			if ((piece & Bitboard.BLACK_SQUARES) != 0) {
				// pawns on same color as bishop
				score = +EvalConstants.BISHOP_PAWN[Long.bitCount(whitePawns & Bitboard.BLACK_SQUARES)];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
				
				// attacking center squares
				if (Long.bitCount(evalInfo.attacks[WHITE][BISHOP] & Bitboard.D4_E5) == 2) {
					score = +EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
					evalInfo.eval_o_part5 += score;
					evalInfo.eval_e_part5 += score;
				}
			}

			// prison
			piece &= Bitboard.RANK_2;
			while (piece != 0) {
				if (Long.bitCount((EvalConstants.BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & blackPawns) == 2) {
					score = +EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_PRISON];
					evalInfo.eval_o_part5 += score;
					evalInfo.eval_e_part5 += score;
				}
				piece &= piece - 1;
			}

		}

		// BLACK BISHOP
		if (evalInfo.bb_b_bishops != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = evalInfo.bb_b_bishops & evalInfo.passedPawnsAndOutposts & blackPawnAttacks;
			if (piece != 0) {
				score = -Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
			}

			piece = evalInfo.bb_b_bishops;
			if ((piece & Bitboard.WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score = -EvalConstants.BISHOP_PAWN[Long.bitCount(blackPawns & Bitboard.WHITE_SQUARES)];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
				
				// bonus for attacking center squares
				if (Long.bitCount(evalInfo.attacks[BLACK][BISHOP] & Bitboard.E4_D5) == 2) {
					score = -EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
					evalInfo.eval_o_part5 += score;
					evalInfo.eval_e_part5 += score;
				}
			}
			if ((piece & Bitboard.BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score = -EvalConstants.BISHOP_PAWN[Long.bitCount(blackPawns & Bitboard.BLACK_SQUARES)];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
				
				// bonus for attacking center squares
				if (Long.bitCount(evalInfo.attacks[BLACK][BISHOP] & Bitboard.D4_E5) == 2) {
					score = -EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
					evalInfo.eval_o_part5 += score;
					evalInfo.eval_e_part5 += score;
				}
			}

			// prison
			piece &= Bitboard.RANK_7;
			while (piece != 0) {
				if (Long.bitCount((EvalConstants.BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & whitePawns) == 2) {
					score = -EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_PRISON];
					evalInfo.eval_o_part5 += score;
					evalInfo.eval_e_part5 += score;
				}
				piece &= piece - 1;
			}

		}

		// pieces supporting our pawns
		piece = (whitePawns << 8) & whites;
		while (piece != 0) {
			score = +EvalConstants.PAWN_BLOCKAGE[Long.numberOfTrailingZeros(piece) >>> 3];
			evalInfo.eval_o_part5 += score;
			evalInfo.eval_e_part5 += score;
			piece &= piece - 1;
		}
		piece = (blackPawns >>> 8) & blacks;
		while (piece != 0) {
			score = -EvalConstants.PAWN_BLOCKAGE[7 - Long.numberOfTrailingZeros(piece) / 8];
			evalInfo.eval_o_part5 += score;
			evalInfo.eval_e_part5 += score;
			piece &= piece - 1;
		}

		// knight outpost: protected by a pawn, cannot be attacked by enemy pawns
		piece = evalInfo.bb_w_knights & evalInfo.passedPawnsAndOutposts & whitePawnAttacks;
		if (piece != 0) {
			score = +Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
			evalInfo.eval_o_part5 += score;
			evalInfo.eval_e_part5 += score;
		}
		piece = evalInfo.bb_b_knights & evalInfo.passedPawnsAndOutposts & blackPawnAttacks;
		if (piece != 0) {
			score = -Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
			evalInfo.eval_o_part5 += score;
			evalInfo.eval_e_part5 += score;
		}

		// pinned-pieces
		if (evalInfo.pinnedPieces != 0) {
			piece = evalInfo.pinnedPieces & whites;
			while (piece != 0) {
				score = +EvalConstants.PINNED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
				piece &= piece - 1;
			}
			piece = evalInfo.pinnedPieces & blacks;
			while (piece != 0) {
				score = -EvalConstants.PINNED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
				piece &= piece - 1;
			}
		}

		// discovered-pieces
		if (evalInfo.discoveredPieces != 0) {
			piece = evalInfo.discoveredPieces & whites;
			while (piece != 0) {
				score = +EvalConstants.DISCOVERED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
				piece &= piece - 1;
			}
			piece = evalInfo.discoveredPieces & blacks;
			while (piece != 0) {
				score = -EvalConstants.DISCOVERED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
				evalInfo.eval_o_part5 += score;
				evalInfo.eval_e_part5 += score;
				piece &= piece - 1;
			}
		}

		if (cb.castlingRights != 0) {
			score = +Long.bitCount(cb.castlingRights & 12) * EvalConstants.OTHER_SCORES[EvalConstants.IX_CASTLING];
			score -= Long.bitCount(cb.castlingRights & 3) * EvalConstants.OTHER_SCORES[EvalConstants.IX_CASTLING];
			evalInfo.eval_o_part5 += score;
			evalInfo.eval_e_part5 += score;
		}
	}
	
	
	private static void calculatePawnShieldBonus(final EvalInfo evalInfo) {

		int file;

		long piece = evalInfo.bb_w_pawns & ChessConstants.KING_AREA[WHITE][evalInfo.kingIndex[WHITE]] & ~evalInfo.attacks[BLACK][PAWN];
		while (piece != 0) {
			file = Long.numberOfTrailingZeros(piece) & 7;
			evalInfo.eval_o_part3 += EvalConstants.SHIELD_BONUS_MG[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3] / ((evalInfo.bb_b_queens == 0) ? 2 : 1);
			evalInfo.eval_e_part3 += EvalConstants.SHIELD_BONUS_EG[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3] / ((evalInfo.bb_b_queens == 0) ? 2 : 1);			
			piece &= ~Bitboard.FILES[file];
		}

		piece = evalInfo.bb_b_pawns & ChessConstants.KING_AREA[BLACK][evalInfo.kingIndex[BLACK]] & ~evalInfo.attacks[WHITE][PAWN];
		while (piece != 0) {
			file = (63 - Long.numberOfLeadingZeros(piece)) & 7;
			evalInfo.eval_o_part3 -= EvalConstants.SHIELD_BONUS_MG[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8] / ((evalInfo.bb_w_queens == 0) ? 2 : 1);
			evalInfo.eval_e_part3 -= EvalConstants.SHIELD_BONUS_EG[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8] / ((evalInfo.bb_w_queens == 0) ? 2 : 1);
			piece &= ~Bitboard.FILES[file];
		}
	}

	private static void calculateMobilityScoresAndSetAttacks(final EvalInfo evalInfo) {
		
		long moves;
		for (int color = WHITE; color <= BLACK; color++) {

			final long kingArea = ChessConstants.KING_AREA[1 - color][evalInfo.kingIndex[1 - color]];
			final long safeMoves = ~evalInfo.getFriendlyPieces(color) & ~evalInfo.attacks[1 - color][PAWN];

			// knights
			long piece = evalInfo.getPieces(color, NIGHT) & ~evalInfo.pinnedPieces;
			while (piece != 0) {
				moves = StaticMoves.KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
				evalInfo.updateAttacks(moves, NIGHT, color, kingArea);
				int count = Long.bitCount(moves & safeMoves);
				evalInfo.eval_o_part2 += ChessConstants.COLOR_FACTOR[color] * EvalConstants.MOBILITY_KNIGHT_MG[count];
				evalInfo.eval_e_part2 += ChessConstants.COLOR_FACTOR[color] * EvalConstants.MOBILITY_KNIGHT_EG[count];
				piece &= piece - 1;
			}

			// bishops
			piece = evalInfo.getPieces(color, BISHOP);
			while (piece != 0) {
				moves = MagicUtil.getBishopMoves(Long.numberOfTrailingZeros(piece), evalInfo.bb_all ^ evalInfo.getPieces(color, QUEEN));
				evalInfo.updateAttacks(moves, BISHOP, color, kingArea);
				int count = Long.bitCount(moves & safeMoves);
				evalInfo.eval_o_part2 += ChessConstants.COLOR_FACTOR[color] * EvalConstants.MOBILITY_BISHOP_MG[count];
				evalInfo.eval_e_part2 += ChessConstants.COLOR_FACTOR[color] * EvalConstants.MOBILITY_BISHOP_EG[count];
				piece &= piece - 1;
			}

			// rooks
			piece = evalInfo.getPieces(color, ROOK);
			while (piece != 0) {
				moves = MagicUtil.getRookMoves(Long.numberOfTrailingZeros(piece), evalInfo.bb_all ^ evalInfo.getPieces(color, ROOK) ^ evalInfo.getPieces(color, QUEEN));
				evalInfo.updateAttacks(moves, ROOK, color, kingArea);
				int count = Long.bitCount(moves & safeMoves);
				evalInfo.eval_o_part2 += ChessConstants.COLOR_FACTOR[color] * EvalConstants.MOBILITY_ROOK_MG[count];
				evalInfo.eval_e_part2 += ChessConstants.COLOR_FACTOR[color] * EvalConstants.MOBILITY_ROOK_EG[count];
				piece &= piece - 1;
			}

			// queens
			piece = evalInfo.getPieces(color, QUEEN);
			while (piece != 0) {
				moves = MagicUtil.getQueenMoves(Long.numberOfTrailingZeros(piece), evalInfo.bb_all);
				evalInfo.updateAttacks(moves, QUEEN, color, kingArea);
				int count = Long.bitCount(moves & safeMoves);
				evalInfo.eval_o_part2 += ChessConstants.COLOR_FACTOR[color] * EvalConstants.MOBILITY_QUEEN_MG[count];
				evalInfo.eval_e_part2 += ChessConstants.COLOR_FACTOR[color] * EvalConstants.MOBILITY_QUEEN_EG[count];
				piece &= piece - 1;
			}
		}

		// TODO king-attacks with or without enemy attacks?
		// WHITE king
		moves = StaticMoves.KING_MOVES[evalInfo.kingIndex[WHITE]] & ~StaticMoves.KING_MOVES[evalInfo.kingIndex[BLACK]];
		evalInfo.attacks[WHITE][KING] = moves;
		evalInfo.doubleAttacks[WHITE] |= evalInfo.attacksAll[WHITE] & moves;
		evalInfo.attacksAll[WHITE] |= moves;
		int count = Long.bitCount(moves & ~evalInfo.getFriendlyPieces(WHITE) & ~evalInfo.attacksAll[BLACK]);
		evalInfo.eval_o_part2 += EvalConstants.MOBILITY_KING_MG[count];
		evalInfo.eval_e_part2 += EvalConstants.MOBILITY_KING_EG[count];
		
		// BLACK king
		moves = StaticMoves.KING_MOVES[evalInfo.kingIndex[BLACK]] & ~StaticMoves.KING_MOVES[evalInfo.kingIndex[WHITE]];
		evalInfo.attacks[BLACK][KING] = moves;
		evalInfo.doubleAttacks[BLACK] |= evalInfo.attacksAll[BLACK] & moves;
		evalInfo.attacksAll[BLACK] |= moves;
		count = Long.bitCount(moves & ~evalInfo.getFriendlyPieces(BLACK) & ~evalInfo.attacksAll[WHITE]);
		evalInfo.eval_o_part2 -= EvalConstants.MOBILITY_KING_MG[count];
		evalInfo.eval_e_part2 -= EvalConstants.MOBILITY_KING_EG[count];
	}
	
	
	private static void calculateMaterialScore(final IBoardConfig boardConfig, final EvalInfo evalInfo) {
		
		int count_pawns = Long.bitCount(evalInfo.bb_w_pawns) - Long.bitCount(evalInfo.bb_b_pawns);
		int count_knights = Long.bitCount(evalInfo.bb_w_knights) - Long.bitCount(evalInfo.bb_b_knights);
		int count_bishops = Long.bitCount(evalInfo.bb_w_bishops) - Long.bitCount(evalInfo.bb_b_bishops);
		int count_rooks = Long.bitCount(evalInfo.bb_w_rooks) - Long.bitCount(evalInfo.bb_b_rooks);
		int count_queens = Long.bitCount(evalInfo.bb_w_queens) - Long.bitCount(evalInfo.bb_b_queens);
		
		evalInfo.eval_o_part1 += (int) (count_pawns * boardConfig.getMaterial_PAWN_O());
		evalInfo.eval_e_part1 += (int) (count_pawns * boardConfig.getMaterial_PAWN_E());
		
		evalInfo.eval_o_part1 += (int) (count_knights * boardConfig.getMaterial_KNIGHT_O());
		evalInfo.eval_e_part1 += (int) (count_knights * boardConfig.getMaterial_KNIGHT_E());
		
		evalInfo.eval_o_part1 += (int) (count_bishops * boardConfig.getMaterial_BISHOP_O());
		evalInfo.eval_e_part1 += (int) (count_bishops * boardConfig.getMaterial_BISHOP_E());
		
		evalInfo.eval_o_part1 += (int) (count_rooks * boardConfig.getMaterial_ROOK_O());
		evalInfo.eval_e_part1 += (int) (count_rooks * boardConfig.getMaterial_ROOK_E());
		
		evalInfo.eval_o_part1 += (int) (count_queens * boardConfig.getMaterial_QUEEN_O());
		evalInfo.eval_e_part1 += (int) (count_queens * boardConfig.getMaterial_QUEEN_E());
	}
	
	
	private static void calculateKingSafetyScores(final EvalInfo evalInfo) {

		int score = 0;

		for (int kingColor = WHITE; kingColor <= BLACK; kingColor++) {
			final int enemyColor = 1 - kingColor;

			if ((evalInfo.getPieces(enemyColor, QUEEN) | evalInfo.getPieces(enemyColor, ROOK)) == 0) {
				continue;
			}

			final int kingIndex = evalInfo.kingIndex[kingColor];
			int counter = 0;

			counter += EvalConstants.KS_NO_FRIENDS[Long.bitCount(ChessConstants.KING_AREA[kingColor][kingIndex] & ~evalInfo.getFriendlyPieces(kingColor))];
			counter += EvalConstants.KS_ATTACKS[Long.bitCount(ChessConstants.KING_AREA[kingColor][kingIndex] & evalInfo.attacksAll[enemyColor])];

			counter += EvalConstants.KS_DOUBLE_ATTACKS[Long
					.bitCount(StaticMoves.KING_MOVES[kingIndex] & evalInfo.doubleAttacks[enemyColor] & ~evalInfo.attacks[kingColor][PAWN])];

			counter += checks(kingColor, evalInfo);

			// bonus for stm
			counter += 1 - evalInfo.colorToMove ^ enemyColor;

			// bonus if there are discovered checks possible
			if (evalInfo.discoveredPieces != 0) {
				counter += Long.bitCount(evalInfo.discoveredPieces & evalInfo.getFriendlyPieces(enemyColor)) * 2;
			}

			if (evalInfo.getPieces(enemyColor, QUEEN) == 0) {
				counter /= 2;
			} else {
				// bonus for small king-queen distance
				if ((evalInfo.attacksAll[kingColor] & evalInfo.getPieces(enemyColor, QUEEN)) == 0) {
					counter += EvalConstants.KS_QUEEN_TROPISM[Util.getDistance(kingIndex, Long.numberOfTrailingZeros(evalInfo.getPieces(enemyColor, QUEEN)))];
				}
			}
			
			counter += EvalConstants.KS_ATTACK_PATTERN[evalInfo.kingAttackersFlag[enemyColor]];
			score += ChessConstants.COLOR_FACTOR[enemyColor] * EvalConstants.KS_SCORES[Math.min(counter, EvalConstants.KS_SCORES.length - 1)];
		}
		
		evalInfo.eval_o_part3 += score;
		evalInfo.eval_e_part3 += 0;
	}
	
	
	private static int checks(final int kingColor, final EvalInfo evalInfo) {
		final int enemyColor = 1 - kingColor;
		final int kingIndex = evalInfo.kingIndex[kingColor];
		final long notAttacked = ~evalInfo.attacksAll[kingColor];
		final long possibleSquares = ~evalInfo.getFriendlyPieces(enemyColor)
				& (~StaticMoves.KING_MOVES[kingIndex] | evalInfo.doubleAttacks[enemyColor] & ~evalInfo.doubleAttacks[kingColor]);

		int counter = 0;
		if (evalInfo.getPieces(enemyColor, NIGHT) != 0) {
			counter += checkNight(notAttacked, StaticMoves.KNIGHT_MOVES[kingIndex] & possibleSquares & evalInfo.attacks[enemyColor][NIGHT]);
		}

		long moves;
		long queenMoves = 0;
		if ((evalInfo.getPieces(enemyColor, QUEEN) | evalInfo.getPieces(enemyColor, BISHOP)) != 0) {
			moves = MagicUtil.getBishopMoves(kingIndex, evalInfo.bb_all ^ evalInfo.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves = moves;
			counter += checkBishop(notAttacked, moves & evalInfo.attacks[enemyColor][BISHOP]);
		}
		if ((evalInfo.getPieces(enemyColor, QUEEN) | evalInfo.getPieces(enemyColor, ROOK)) != 0) {
			moves = MagicUtil.getRookMoves(kingIndex, evalInfo.bb_all ^ evalInfo.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves |= moves;
			counter += checkRook(kingColor, moves & evalInfo.attacks[enemyColor][ROOK], evalInfo);
		}

		queenMoves &= evalInfo.attacks[enemyColor][QUEEN];
		if (queenMoves != 0) {

			// safe check queen
			if ((queenMoves & notAttacked) != 0) {
				counter += EvalConstants.KS_CHECK_QUEEN[Math.min(Long.bitCount(evalInfo.getFriendlyPieces(kingColor)), EvalConstants.KS_CHECK_QUEEN.length - 1)];
			}
			// safe check queen touch
			if ((queenMoves & StaticMoves.KING_MOVES[kingIndex]) != 0) {
				counter += EvalConstants.KS_OTHER[0];
			}
		}

		return counter;
	}

	private static int checkRook(final int kingColor, final long rookMoves, final EvalInfo evalInfo) {
		if (rookMoves == 0) {
			return 0;
		}

		int counter = 0;
		if ((rookMoves & ~evalInfo.attacksAll[kingColor]) != 0) {
			counter += EvalConstants.KS_OTHER[2];

			// last rank?
			if (kingBlockedAtLastRank(StaticMoves.KING_MOVES[evalInfo.kingIndex[kingColor]] & evalInfo.bb_free & ~evalInfo.attacksAll[1 - kingColor])) {
				counter += EvalConstants.KS_OTHER[1];
			}

		} else {
			counter += EvalConstants.KS_OTHER[3];
		}

		return counter;
	}

	private static int checkBishop(final long safeSquares, final long bishopMoves) {
		if (bishopMoves == 0) {
			return 0;
		}
		if ((bishopMoves & safeSquares) == 0) {
			return EvalConstants.KS_OTHER[3];
		} else {
			return EvalConstants.KS_OTHER[2];
		}
	}

	private static int checkNight(final long safeSquares, final long nightMoves) {
		if (nightMoves == 0) {
			return 0;
		}
		if ((nightMoves & safeSquares) == 0) {
			return EvalConstants.KS_OTHER[3];
		} else {
			return EvalConstants.KS_OTHER[2];
		}
	}

	private static boolean kingBlockedAtLastRank(final long safeKingMoves) {
		return (Bitboard.RANK_234567 & safeKingMoves) == 0;
	}

	
	private static void calculatePassedPawnScores(final EvalInfo evalInfo) {
		
		int whitePromotionDistance = Util.SHORT_MAX;
		int blackPromotionDistance = Util.SHORT_MAX;

		// white passed pawns
		long passedPawns = evalInfo.passedPawnsAndOutposts & evalInfo.bb_w_pawns;
		while (passedPawns != 0) {
			final int index = 63 - Long.numberOfLeadingZeros(passedPawns);

			int score = getPassedPawnScore(index, WHITE, evalInfo);
			
			evalInfo.eval_o_part4 += 0;
			evalInfo.eval_e_part4 += score;
			
			if (whitePromotionDistance == Util.SHORT_MAX) {
				whitePromotionDistance = getWhitePromotionDistance(index, evalInfo);
			}

			// skip all passed pawns at same file
			passedPawns &= ~Bitboard.FILES[index & 7];
		}

		// black passed pawns
		passedPawns = evalInfo.passedPawnsAndOutposts & evalInfo.bb_b_pawns;
		while (passedPawns != 0) {
			final int index = Long.numberOfTrailingZeros(passedPawns);

			int score = getPassedPawnScore(index, BLACK, evalInfo);

			evalInfo.eval_o_part4 -= 0;
			evalInfo.eval_e_part4 -= score;
			
			if (blackPromotionDistance == Util.SHORT_MAX) {
				blackPromotionDistance = getBlackPromotionDistance(index, evalInfo);
			}

			// skip all passed pawns at same file
			passedPawns &= ~Bitboard.FILES[index & 7];
		}

		if (whitePromotionDistance < blackPromotionDistance - 1) {
			evalInfo.eval_o_part4 += 0;
			evalInfo.eval_e_part4 += 350;
		} else if (whitePromotionDistance > blackPromotionDistance + 1) {
			evalInfo.eval_o_part4 -= 0;
			evalInfo.eval_e_part4 -= 350;
		}
	}

	private static int getPassedPawnScore(final int index, final int color, final EvalInfo evalInfo) {

		final int nextIndex = index + ChessConstants.COLOR_FACTOR_8[color];
		final long square = Util.POWER_LOOKUP[index];
		final long maskNextSquare = Util.POWER_LOOKUP[nextIndex];
		final long maskPreviousSquare = Util.POWER_LOOKUP[index - ChessConstants.COLOR_FACTOR_8[color]];
		final long maskFile = Bitboard.FILES[index & 7];
		final int enemyColor = 1 - color;
		float multiplier = 1;

		// is piece blocked?
		if ((evalInfo.bb_all & maskNextSquare) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[0];
		}

		// is next squared attacked?
		if ((evalInfo.attacksAll[enemyColor] & maskNextSquare) == 0) {

			// complete path free of enemy attacks?
			if ((ChessConstants.PINNED_MOVEMENT[nextIndex][index] & evalInfo.attacksAll[enemyColor]) == 0) {
				multiplier *= EvalConstants.PASSED_MULTIPLIERS[7];
			} else {
				multiplier *= EvalConstants.PASSED_MULTIPLIERS[1];
			}
		}

		// is next squared defended?
		if ((evalInfo.attacksAll[color] & maskNextSquare) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[3];
		}

		// is enemy king in front?
		if ((ChessConstants.PINNED_MOVEMENT[nextIndex][index] & evalInfo.getPieces(enemyColor, ChessConstants.KING)) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[2];
		}

		// under attack?
		if (evalInfo.colorToMove != color && (evalInfo.attacksAll[enemyColor] & square) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[4];
		}

		// defended by rook from behind?
		if ((maskFile & evalInfo.getPieces(color, ROOK)) != 0 && (evalInfo.attacks[color][ROOK] & square) != 0 && (evalInfo.attacks[color][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[5];
		}

		// attacked by rook from behind?
		else if ((maskFile & evalInfo.getPieces(enemyColor, ROOK)) != 0 && (evalInfo.attacks[enemyColor][ROOK] & square) != 0
				&& (evalInfo.attacks[enemyColor][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= EvalConstants.PASSED_MULTIPLIERS[6];
		}

		// king tropism
		multiplier *= EvalConstants.PASSED_KING_MULTI[Util.getDistance(evalInfo.kingIndex[color], index)];
		multiplier *= EvalConstants.PASSED_KING_MULTI[8 - Util.getDistance(evalInfo.kingIndex[enemyColor], index)];

		final int scoreIndex = (7 * color) + ChessConstants.COLOR_FACTOR[color] * index / 8;
		return (int) (EvalConstants.PASSED_SCORE_EG[scoreIndex] * multiplier);
	}

	private static int getBlackPromotionDistance(final int index, final EvalInfo evalInfo) {
		// check if it cannot be stopped
		int promotionDistance = index >>> 3;
		if (promotionDistance == 1 && evalInfo.colorToMove == BLACK) {
			if ((Util.POWER_LOOKUP[index - 8] & (evalInfo.attacksAll[WHITE] | evalInfo.bb_all)) == 0) {
				if ((Util.POWER_LOOKUP[index] & evalInfo.attacksAll[WHITE]) == 0) {
					return 1;
				}
			}
		} else if (MaterialUtil.onlyWhitePawnsOrOneNightOrBishop(evalInfo.materialKey)) {

			// check if it is my turn
			if (evalInfo.colorToMove == WHITE) {
				promotionDistance++;
			}

			// check if own pieces are blocking the path
			if (Long.numberOfTrailingZeros(evalInfo.getFriendlyPieces(BLACK) & Bitboard.FILES[index & 7]) < index) {
				promotionDistance++;
			}

			// check if own king is defending the promotion square (including square just below)
			if ((StaticMoves.KING_MOVES[evalInfo.kingIndex[BLACK]] & ChessConstants.KING_AREA[BLACK][index] & Bitboard.RANK_12) != 0) {
				promotionDistance--;
			}

			// check distance of enemy king to promotion square
			if (promotionDistance < Math.max(evalInfo.kingIndex[WHITE] >>> 3, Math.abs((index & 7) - (evalInfo.kingIndex[WHITE] & 7)))) {
				if (!MaterialUtil.hasWhiteNonPawnPieces(evalInfo.materialKey)) {
					return promotionDistance;
				}
				if (evalInfo.bb_w_knights != 0) {
					// check distance of enemy night
					if (promotionDistance < Util.getDistance(Long.numberOfTrailingZeros(evalInfo.bb_w_knights), index)) {
						return promotionDistance;
					}
				} else {
					// can bishop stop the passed pawn?
					if (index >>> 3 == 1) {
						if (((Util.POWER_LOOKUP[index] & Bitboard.WHITE_SQUARES) == 0) == ((evalInfo.bb_w_bishops & Bitboard.WHITE_SQUARES) == 0)) {
							if ((evalInfo.attacksAll[WHITE] & Util.POWER_LOOKUP[index]) == 0) {
								return promotionDistance;
							}
						}
					}
				}
			}
		}
		return Util.SHORT_MAX;
	}

	private static int getWhitePromotionDistance(final int index, final EvalInfo evalInfo) {
		// check if it cannot be stopped
		int promotionDistance = 7 - index / 8;
		if (promotionDistance == 1 && evalInfo.colorToMove == WHITE) {
			if ((Util.POWER_LOOKUP[index + 8] & (evalInfo.attacksAll[BLACK] | evalInfo.bb_all)) == 0) {
				if ((Util.POWER_LOOKUP[index] & evalInfo.attacksAll[BLACK]) == 0) {
					return 1;
				}
			}
		} else if (MaterialUtil.onlyBlackPawnsOrOneNightOrBishop(evalInfo.materialKey)) {

			// check if it is my turn
			if (evalInfo.colorToMove == BLACK) {
				promotionDistance++;
			}

			// check if own pieces are blocking the path
			if (63 - Long.numberOfLeadingZeros(evalInfo.getFriendlyPieces(WHITE) & Bitboard.FILES[index & 7]) > index) {
				promotionDistance++;
			}

			// TODO maybe the enemy king can capture the pawn!!
			// check if own king is defending the promotion square (including square just below)
			if ((StaticMoves.KING_MOVES[evalInfo.kingIndex[WHITE]] & ChessConstants.KING_AREA[WHITE][index] & Bitboard.RANK_78) != 0) {
				promotionDistance--;
			}

			// check distance of enemy king to promotion square
			if (promotionDistance < Math.max(7 - evalInfo.kingIndex[BLACK] / 8, Math.abs((index & 7) - (evalInfo.kingIndex[BLACK] & 7)))) {
				if (!MaterialUtil.hasBlackNonPawnPieces(evalInfo.materialKey)) {
					return promotionDistance;
				}
				if (evalInfo.bb_b_knights != 0) {
					// check distance of enemy night
					if (promotionDistance < Util.getDistance(Long.numberOfTrailingZeros(evalInfo.bb_b_knights), index)) {
						return promotionDistance;
					}
				} else {
					// can bishop stop the passed pawn?
					if (index >>> 3 == 6) { // rank 7
						if (((Util.POWER_LOOKUP[index] & Bitboard.WHITE_SQUARES) == 0) == ((evalInfo.bb_b_bishops & Bitboard.WHITE_SQUARES) == 0)) {
							// other color than promotion square
							if ((evalInfo.attacksAll[BLACK] & Util.POWER_LOOKUP[index]) == 0) {
								return promotionDistance;
							}
						}
					}
				}
			}
		}
		return Util.SHORT_MAX;
	}
}

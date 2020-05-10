package bagaturchess.learning.goldmiddle.impl4.base;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import bagaturchess.bitboard.impl1.internal.Bitboard;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MagicUtil;
import bagaturchess.bitboard.impl1.internal.StaticMoves;


public class EvalUtil {
	
	
	public static final int MG = 0;
	public static final int EG = 1;
	
	public static final int PHASE_TOTAL = 4 * EvalConstants.PHASE[NIGHT] + 4 * EvalConstants.PHASE[BISHOP] + 4 * EvalConstants.PHASE[ROOK]
			+ 2 * EvalConstants.PHASE[QUEEN];
		
	
	public static int getScore1(final ChessBoard cb, final EvalInfo evalInfo) {
		
		return taperedEval1(cb, evalInfo);
	}
	
	
	public static int getScore2(final ChessBoard cb, final EvalInfo evalInfo) {
		
		return taperedEval2(cb, evalInfo);
	}
	
	
	private static int taperedEval1(final ChessBoard cb, final EvalInfo evalInfo) {
		
		getImbalances(cb, evalInfo);
		getPawnScores(cb, evalInfo);

		return ((evalInfo.eval_o_part1 * (PHASE_TOTAL - cb.phase)) + evalInfo.eval_e_part1 * cb.phase) / PHASE_TOTAL / calculateScaleFactor(cb, evalInfo);		
	}
	
	
	private static int taperedEval2(final ChessBoard cb, final EvalInfo evalInfo) {
		final int mgEgScore = calculateMobilityScoresAndSetAttacks(cb, evalInfo) + calculateThreats(cb, evalInfo) + calculatePawnShieldBonus(cb, evalInfo);
		final int others = calculateOthers(cb, evalInfo);
		
		final int scoreMg = cb.phase == PHASE_TOTAL ? 0
				: getMgScore(mgEgScore) + cb.psqtScore_mg + KingSafetyEval.calculateScores(cb, evalInfo) + calculateSpace(cb, evalInfo) + others;
		
		final int scoreEg = getEgScore(mgEgScore) + cb.psqtScore_eg + PassedPawnEval.calculateScores(cb, evalInfo) + others;

		return ((scoreMg * (PHASE_TOTAL - cb.phase)) + scoreEg * cb.phase) / PHASE_TOTAL / calculateScaleFactor(cb, evalInfo);
	}
	
	
	public static int getMgScore(final int score) {
		return (score + 0x8000) >> 16;
	}
	
	
	public static int getEgScore(final int score) {
		return (short) (score & 0xffff);
	}
	
	
	private static int calculateScaleFactor(final ChessBoard cb, final EvalInfo evalInfo) {
		// opposite bishops endgame?
		if (MaterialUtil.oppositeBishops(cb.materialKey)) {
			if (((evalInfo.bb_w_bishops & Bitboard.BLACK_SQUARES) == 0) == ((evalInfo.bb_b_bishops & Bitboard.WHITE_SQUARES) == 0)) {
				return 2;
			}
		}
		return 1;
	}
	
	
	public static int calculateSpace(final ChessBoard cb, final EvalInfo evalInfo) {

		if (!MaterialUtil.hasPawns(cb.materialKey)) {
			return 0;
		}

		int score = 0;

		score += EvalConstants.OTHER_SCORES[EvalConstants.IX_SPACE]
				* Long.bitCount((evalInfo.bb_w_pawns >>> 8) & (evalInfo.bb_w_knights | evalInfo.bb_w_bishops) & Bitboard.RANK_234);
		score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_SPACE]
				* Long.bitCount((evalInfo.bb_b_pawns << 8) & (evalInfo.bb_b_knights | evalInfo.bb_b_bishops) & Bitboard.RANK_567);

		// idea taken from Laser
		long space = evalInfo.bb_w_pawns >>> 8;
		space |= space >>> 8 | space >>> 16;
		score += EvalConstants.SPACE[Long.bitCount(cb.friendlyPieces[WHITE])]
				* Long.bitCount(space & ~evalInfo.bb_w_pawns & ~evalInfo.attacks[BLACK][PAWN] & Bitboard.FILE_CDEF);
		space = evalInfo.bb_b_pawns << 8;
		space |= space << 8 | space << 16;
		score -= EvalConstants.SPACE[Long.bitCount(cb.friendlyPieces[BLACK])]
				* Long.bitCount(space & ~evalInfo.bb_b_pawns & ~evalInfo.attacks[WHITE][PAWN] & Bitboard.FILE_CDEF);

		return score;
	}
	
	
	public static void getPawnScores(final ChessBoard cb, final EvalInfo evalInfo) {
		calculatePawnScores(cb, evalInfo);
	}

	
	private static void calculatePawnScores(final ChessBoard cb, final EvalInfo evalInfo) {

		// penalty for doubled pawns
		for (int i = 0; i < 8; i++) {
			if (Long.bitCount(evalInfo.bb_w_pawns & Bitboard.FILES[i]) > 1) {
				int eval = -EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_DOUBLE];
				evalInfo.eval_o_part1 += eval;
				evalInfo.eval_e_part1 += eval;
			}
			if (Long.bitCount(evalInfo.bb_b_pawns & Bitboard.FILES[i]) > 1) {
				int eval = +EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_DOUBLE];
				evalInfo.eval_o_part1 += eval;
				evalInfo.eval_e_part1 += eval;
			}
		}

		// bonus for connected pawns
		long pawns = Bitboard.getWhitePawnAttacks(evalInfo.bb_w_pawns) & evalInfo.bb_w_pawns;
		while (pawns != 0) {
			int eval = +EvalConstants.PAWN_CONNECTED[Long.numberOfTrailingZeros(pawns) / 8];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getBlackPawnAttacks(evalInfo.bb_b_pawns) & evalInfo.bb_b_pawns;
		while (pawns != 0) {
			int eval = -EvalConstants.PAWN_CONNECTED[7 - Long.numberOfTrailingZeros(pawns) / 8];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
			pawns &= pawns - 1;
		}

		// bonus for neighbour pawns
		pawns = Bitboard.getPawnNeighbours(evalInfo.bb_w_pawns) & evalInfo.bb_w_pawns;
		while (pawns != 0) {
			int eval = +EvalConstants.PAWN_NEIGHBOUR[Long.numberOfTrailingZeros(pawns) / 8];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getPawnNeighbours(evalInfo.bb_b_pawns) & evalInfo.bb_b_pawns;
		while (pawns != 0) {
			int eval = -EvalConstants.PAWN_NEIGHBOUR[7 - Long.numberOfTrailingZeros(pawns) / 8];
			evalInfo.eval_o_part1 += eval;
			evalInfo.eval_e_part1 += eval;
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
				evalInfo.eval_o_part1 += eval;
				evalInfo.eval_e_part1 += eval;
			}

			// backward pawns
			else if ((Bitboard.getBlackAdjacentMask(index + 8) & evalInfo.bb_w_pawns) == 0) {
				if ((StaticMoves.PAWN_ATTACKS[WHITE][index + 8] & evalInfo.bb_b_pawns) != 0) {
					if ((Bitboard.FILES[index & 7] & evalInfo.bb_b_pawns) == 0) {
						int eval = -EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_BACKWARD];
						evalInfo.eval_o_part1 += eval;
						evalInfo.eval_e_part1 += eval;
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(StaticMoves.PAWN_ATTACKS[WHITE][index] & evalInfo.bb_w_pawns) == 2) {
				int eval = -EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_INVERSE];
				evalInfo.eval_o_part1 += eval;
				evalInfo.eval_e_part1 += eval;
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
					evalInfo.eval_o_part1 += eval;
					evalInfo.eval_e_part1 += eval;
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
				evalInfo.eval_o_part1 += eval;
				evalInfo.eval_e_part1 += eval;
			}

			// backward pawns
			else if ((Bitboard.getWhiteAdjacentMask(index - 8) & evalInfo.bb_b_pawns) == 0) {
				if ((StaticMoves.PAWN_ATTACKS[BLACK][index - 8] & evalInfo.bb_w_pawns) != 0) {
					if ((Bitboard.FILES[index & 7] & evalInfo.bb_w_pawns) == 0) {
						int eval = +EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_BACKWARD];
						evalInfo.eval_o_part1 += eval;
						evalInfo.eval_e_part1 += eval;
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(StaticMoves.PAWN_ATTACKS[BLACK][index] & evalInfo.bb_b_pawns) == 2) {
				int eval = +EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_INVERSE];
				evalInfo.eval_o_part1 += eval;
				evalInfo.eval_e_part1 += eval;
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
					evalInfo.eval_o_part1 += eval;
					evalInfo.eval_e_part1 += eval;
				}
			}

			pawns &= pawns - 1;
		}
	}

	public static void getImbalances(final ChessBoard cb, final EvalInfo evalInfo) {
		calculateImbalances(cb, evalInfo);
	}

	private static void calculateImbalances(final ChessBoard cb, final EvalInfo evalInfo) {

		// material
		calculateMaterialScore(evalInfo);
		
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

	public static int calculateThreats(final ChessBoard cb, final EvalInfo evalInfo) {
		int score = 0;
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
			score += EvalConstants.DOUBLE_ATTACKED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
			piece &= piece - 1;
		}
		piece = evalInfo.doubleAttacks[BLACK] & whites;
		while (piece != 0) {
			score -= EvalConstants.DOUBLE_ATTACKED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
			piece &= piece - 1;
		}

		if (MaterialUtil.hasPawns(cb.materialKey)) {

			// unused outposts
			score += Long.bitCount(evalInfo.passedPawnsAndOutposts & cb.emptySpaces & whiteMinorAttacks & whitePawnAttacks)
					* EvalConstants.THREATS[EvalConstants.IX_UNUSED_OUTPOST];
			score -= Long.bitCount(evalInfo.passedPawnsAndOutposts & cb.emptySpaces & blackMinorAttacks & blackPawnAttacks)
					* EvalConstants.THREATS[EvalConstants.IX_UNUSED_OUTPOST];

			// pawn push threat
			piece = (whitePawns << 8) & cb.emptySpaces & ~blackAttacks;
			score += Long.bitCount(Bitboard.getWhitePawnAttacks(piece) & blacks) * EvalConstants.THREATS[EvalConstants.IX_PAWN_PUSH_THREAT];
			piece = (blackPawns >>> 8) & cb.emptySpaces & ~whiteAttacks;
			score -= Long.bitCount(Bitboard.getBlackPawnAttacks(piece) & whites) * EvalConstants.THREATS[EvalConstants.IX_PAWN_PUSH_THREAT];

			// piece attacked by pawn
			score += Long.bitCount(whitePawnAttacks & blacks & ~blackPawns) * EvalConstants.THREATS[EvalConstants.IX_PAWN_ATTACKS];
			score -= Long.bitCount(blackPawnAttacks & whites & ~whitePawns) * EvalConstants.THREATS[EvalConstants.IX_PAWN_ATTACKS];

			// multiple pawn attacks possible
			if (Long.bitCount(whitePawnAttacks & blacks) > 1) {
				score += EvalConstants.THREATS[EvalConstants.IX_MULTIPLE_PAWN_ATTACKS];
			}
			if (Long.bitCount(blackPawnAttacks & whites) > 1) {
				score -= EvalConstants.THREATS[EvalConstants.IX_MULTIPLE_PAWN_ATTACKS];
			}

			// pawn attacked
			score += Long.bitCount(whiteAttacks & blackPawns) * EvalConstants.THREATS[EvalConstants.IX_PAWN_ATTACKED];
			score -= Long.bitCount(blackAttacks & whitePawns) * EvalConstants.THREATS[EvalConstants.IX_PAWN_ATTACKED];

		}

		// minors attacked and not defended by a pawn
		score += Long.bitCount(whiteAttacks & (evalInfo.bb_b_knights | evalInfo.bb_b_bishops & ~blackAttacks))
				* EvalConstants.THREATS[EvalConstants.IX_MAJOR_ATTACKED];
		score -= Long.bitCount(blackAttacks & (evalInfo.bb_w_knights | evalInfo.bb_w_bishops & ~whiteAttacks))
				* EvalConstants.THREATS[EvalConstants.IX_MAJOR_ATTACKED];

		if (evalInfo.bb_b_queens != 0) {
			// queen attacked by rook
			score += Long.bitCount(evalInfo.attacks[WHITE][ROOK] & evalInfo.bb_b_queens) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED];
			// queen attacked by minors
			score += Long.bitCount(whiteMinorAttacks & evalInfo.bb_b_queens) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
		}

		if (evalInfo.bb_w_queens != 0) {
			// queen attacked by rook
			score -= Long.bitCount(evalInfo.attacks[BLACK][ROOK] & evalInfo.bb_w_queens) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED];
			// queen attacked by minors
			score -= Long.bitCount(blackMinorAttacks & evalInfo.bb_w_queens) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
		}

		// rook attacked by minors
		score += Long.bitCount(whiteMinorAttacks & evalInfo.bb_b_rooks) * EvalConstants.THREATS[EvalConstants.IX_ROOK_ATTACKED];
		score -= Long.bitCount(blackMinorAttacks & evalInfo.bb_w_rooks) * EvalConstants.THREATS[EvalConstants.IX_ROOK_ATTACKED];

		return score;
	}

	public static int calculateOthers(final ChessBoard cb, final EvalInfo evalInfo) {
		int score = 0;
		long piece;

		final long whitePawns = evalInfo.bb_w_pawns;
		final long blackPawns = evalInfo.bb_b_pawns;
		final long whitePawnAttacks = evalInfo.attacks[WHITE][PAWN];
		final long blackPawnAttacks = evalInfo.attacks[BLACK][PAWN];
		final long whiteAttacks = evalInfo.attacksAll[WHITE];
		final long blackAttacks = evalInfo.attacksAll[BLACK];
		final long whites = cb.friendlyPieces[WHITE];
		final long blacks = cb.friendlyPieces[BLACK];

		// side to move
		score += ChessConstants.COLOR_FACTOR[cb.colorToMove] * EvalConstants.SIDE_TO_MOVE_BONUS;

		// piece attacked and only defended by a rook or queen
		piece = whites & blackAttacks & whiteAttacks & ~(whitePawnAttacks | evalInfo.attacks[WHITE][NIGHT] | evalInfo.attacks[WHITE][BISHOP]);
		if (piece != 0) {
			score += Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ONLY_MAJOR_DEFENDERS];
		}
		piece = blacks & whiteAttacks & blackAttacks & ~(blackPawnAttacks | evalInfo.attacks[BLACK][NIGHT] | evalInfo.attacks[BLACK][BISHOP]);
		if (piece != 0) {
			score -= Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ONLY_MAJOR_DEFENDERS];
		}

		// WHITE ROOK
		if (evalInfo.bb_w_rooks != 0) {

			piece = evalInfo.bb_w_rooks;

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					score += EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_BATTERY];
				}
			}

			// rook on 7th, king on 8th
			if (cb.kingIndex[BLACK] >= 56 && (piece & Bitboard.RANK_7) != 0) {
				score += Long.bitCount(piece & Bitboard.RANK_7) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_7TH_RANK];
			}

			// prison
			if ((piece & Bitboard.RANK_1) != 0) {
				final long trapped = piece & EvalConstants.ROOK_PRISON[cb.kingIndex[WHITE]];
				if (trapped != 0) {
					for (int i = 8; i <= 24; i += 8) {
						if ((trapped << i & whitePawns) != 0) {
							score += EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_TRAPPED];
							break;
						}
					}
				}
			}

			// rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				if ((whitePawns & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((blackPawns & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score += EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_OPEN];
					} else if ((blackPawns & blackPawnAttacks & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score += EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
					} else {
						score += EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_SEMI_OPEN];
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
					score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_BATTERY];
				}
			}

			// rook on 2nd, king on 1st
			if (cb.kingIndex[WHITE] <= 7 && (piece & Bitboard.RANK_2) != 0) {
				score -= Long.bitCount(piece & Bitboard.RANK_2) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_7TH_RANK];
			}

			// prison
			if ((piece & Bitboard.RANK_8) != 0) {
				final long trapped = piece & EvalConstants.ROOK_PRISON[cb.kingIndex[BLACK]];
				if (trapped != 0) {
					for (int i = 8; i <= 24; i += 8) {
						if ((trapped >>> i & blackPawns) != 0) {
							score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_TRAPPED];
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
						score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_OPEN];
					} else if ((whitePawns & whitePawnAttacks & Bitboard.FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
					} else {
						score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_FILE_SEMI_OPEN];
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
				score += Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
			}

			piece = evalInfo.bb_w_bishops;
			if ((piece & Bitboard.WHITE_SQUARES) != 0) {
				// pawns on same color as bishop
				score += EvalConstants.BISHOP_PAWN[Long.bitCount(whitePawns & Bitboard.WHITE_SQUARES)];

				// attacking center squares
				if (Long.bitCount(evalInfo.attacks[WHITE][BISHOP] & Bitboard.E4_D5) == 2) {
					score += EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
				}
			}
			if ((piece & Bitboard.BLACK_SQUARES) != 0) {
				// pawns on same color as bishop
				score += EvalConstants.BISHOP_PAWN[Long.bitCount(whitePawns & Bitboard.BLACK_SQUARES)];

				// attacking center squares
				if (Long.bitCount(evalInfo.attacks[WHITE][BISHOP] & Bitboard.D4_E5) == 2) {
					score += EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
				}
			}

			// prison
			piece &= Bitboard.RANK_2;
			while (piece != 0) {
				if (Long.bitCount((EvalConstants.BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & blackPawns) == 2) {
					score += EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

		}

		// BLACK BISHOP
		if (evalInfo.bb_b_bishops != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = evalInfo.bb_b_bishops & evalInfo.passedPawnsAndOutposts & blackPawnAttacks;
			if (piece != 0) {
				score -= Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
			}

			piece = evalInfo.bb_b_bishops;
			if ((piece & Bitboard.WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= EvalConstants.BISHOP_PAWN[Long.bitCount(blackPawns & Bitboard.WHITE_SQUARES)];

				// bonus for attacking center squares
				if (Long.bitCount(evalInfo.attacks[BLACK][BISHOP] & Bitboard.E4_D5) == 2) {
					score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
				}
			}
			if ((piece & Bitboard.BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= EvalConstants.BISHOP_PAWN[Long.bitCount(blackPawns & Bitboard.BLACK_SQUARES)];

				// bonus for attacking center squares
				if (Long.bitCount(evalInfo.attacks[BLACK][BISHOP] & Bitboard.D4_E5) == 2) {
					score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
				}
			}

			// prison
			piece &= Bitboard.RANK_7;
			while (piece != 0) {
				if (Long.bitCount((EvalConstants.BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & whitePawns) == 2) {
					score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

		}

		// pieces supporting our pawns
		piece = (whitePawns << 8) & whites;
		while (piece != 0) {
			score += EvalConstants.PAWN_BLOCKAGE[Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= piece - 1;
		}
		piece = (blackPawns >>> 8) & blacks;
		while (piece != 0) {
			score -= EvalConstants.PAWN_BLOCKAGE[7 - Long.numberOfTrailingZeros(piece) / 8];
			piece &= piece - 1;
		}

		// knight outpost: protected by a pawn, cannot be attacked by enemy pawns
		piece = evalInfo.bb_w_knights & evalInfo.passedPawnsAndOutposts & whitePawnAttacks;
		if (piece != 0) {
			score += Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
		}
		piece = evalInfo.bb_b_knights & evalInfo.passedPawnsAndOutposts & blackPawnAttacks;
		if (piece != 0) {
			score -= Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
		}

		// pinned-pieces
		if (cb.pinnedPieces != 0) {
			piece = cb.pinnedPieces & whites;
			while (piece != 0) {
				score += EvalConstants.PINNED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
				piece &= piece - 1;
			}
			piece = cb.pinnedPieces & blacks;
			while (piece != 0) {
				score -= EvalConstants.PINNED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
				piece &= piece - 1;
			}
		}

		// discovered-pieces
		if (cb.discoveredPieces != 0) {
			piece = cb.discoveredPieces & whites;
			while (piece != 0) {
				score += EvalConstants.DISCOVERED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
				piece &= piece - 1;
			}
			piece = cb.discoveredPieces & blacks;
			while (piece != 0) {
				score -= EvalConstants.DISCOVERED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
				piece &= piece - 1;
			}
		}

		if (cb.castlingRights != 0) {
			score += Long.bitCount(cb.castlingRights & 12) * EvalConstants.OTHER_SCORES[EvalConstants.IX_CASTLING];
			score -= Long.bitCount(cb.castlingRights & 3) * EvalConstants.OTHER_SCORES[EvalConstants.IX_CASTLING];
		}

		return score;
	}

	public static int calculatePawnShieldBonus(final ChessBoard cb, final EvalInfo evalInfo) {

		if (!MaterialUtil.hasPawns(cb.materialKey)) {
			return 0;
		}

		int file;

		int whiteScore = 0;
		long piece = evalInfo.bb_w_pawns & ChessConstants.KING_AREA[WHITE][cb.kingIndex[WHITE]] & ~evalInfo.attacks[BLACK][PAWN];
		while (piece != 0) {
			file = Long.numberOfTrailingZeros(piece) & 7;
			whiteScore += EvalConstants.SHIELD_BONUS[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= ~Bitboard.FILES[file];
		}
		if (evalInfo.bb_b_queens == 0) {
			whiteScore /= 2;
		}

		int blackScore = 0;
		piece = evalInfo.bb_b_pawns & ChessConstants.KING_AREA[BLACK][cb.kingIndex[BLACK]] & ~evalInfo.attacks[WHITE][PAWN];
		while (piece != 0) {
			file = (63 - Long.numberOfLeadingZeros(piece)) & 7;
			blackScore += EvalConstants.SHIELD_BONUS[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8];
			piece &= ~Bitboard.FILES[file];
		}
		if (evalInfo.bb_w_queens == 0) {
			blackScore /= 2;
		}

		return whiteScore - blackScore;
	}

	public static int calculateMobilityScoresAndSetAttacks(final ChessBoard cb, final EvalInfo evalInfo) {

		evalInfo.clearEvalAttacks();
		evalInfo.updatePawnAttacks(cb);
		
		int score = 0;
		long moves;
		for (int color = WHITE; color <= BLACK; color++) {

			int tempScore = 0;

			final long kingArea = ChessConstants.KING_AREA[1 - color][cb.kingIndex[1 - color]];
			final long safeMoves = ~cb.friendlyPieces[color] & ~evalInfo.attacks[1 - color][PAWN];

			// knights
			long piece = evalInfo.getPieces(color, NIGHT) & ~cb.pinnedPieces;
			while (piece != 0) {
				moves = StaticMoves.KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
				evalInfo.updateAttacks(moves, NIGHT, color, kingArea);
				tempScore += EvalConstants.MOBILITY_KNIGHT[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// bishops
			piece = evalInfo.getPieces(color, BISHOP);
			while (piece != 0) {
				moves = MagicUtil.getBishopMoves(Long.numberOfTrailingZeros(piece), cb.allPieces ^ evalInfo.getPieces(color, QUEEN));
				evalInfo.updateAttacks(moves, BISHOP, color, kingArea);
				tempScore += EvalConstants.MOBILITY_BISHOP[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// rooks
			piece = evalInfo.getPieces(color, ROOK);
			while (piece != 0) {
				moves = MagicUtil.getRookMoves(Long.numberOfTrailingZeros(piece), cb.allPieces ^ evalInfo.getPieces(color, ROOK) ^ evalInfo.getPieces(color, QUEEN));
				evalInfo.updateAttacks(moves, ROOK, color, kingArea);
				tempScore += EvalConstants.MOBILITY_ROOK[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// queens
			piece = evalInfo.getPieces(color, QUEEN);
			while (piece != 0) {
				moves = MagicUtil.getQueenMoves(Long.numberOfTrailingZeros(piece), cb.allPieces);
				evalInfo.updateAttacks(moves, QUEEN, color, kingArea);
				tempScore += EvalConstants.MOBILITY_QUEEN[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			score += tempScore * ChessConstants.COLOR_FACTOR[color];

		}

		// TODO king-attacks with or without enemy attacks?
		// WHITE king
		moves = StaticMoves.KING_MOVES[cb.kingIndex[WHITE]] & ~StaticMoves.KING_MOVES[cb.kingIndex[BLACK]];
		evalInfo.attacks[WHITE][KING] = moves;
		evalInfo.doubleAttacks[WHITE] |= evalInfo.attacksAll[WHITE] & moves;
		evalInfo.attacksAll[WHITE] |= moves;
		score += EvalConstants.MOBILITY_KING[Long.bitCount(moves & ~cb.friendlyPieces[WHITE] & ~evalInfo.attacksAll[BLACK])];

		// BLACK king
		moves = StaticMoves.KING_MOVES[cb.kingIndex[BLACK]] & ~StaticMoves.KING_MOVES[cb.kingIndex[WHITE]];
		evalInfo.attacks[BLACK][KING] = moves;
		evalInfo.doubleAttacks[BLACK] |= evalInfo.attacksAll[BLACK] & moves;
		evalInfo.attacksAll[BLACK] |= moves;
		score -= EvalConstants.MOBILITY_KING[Long.bitCount(moves & ~cb.friendlyPieces[BLACK] & ~evalInfo.attacksAll[WHITE])];

		return score;
	}
	
	
	public static void calculateMaterialScore(final EvalInfo evalInfo) {
		
		int count_pawns = Long.bitCount(evalInfo.bb_w_pawns) - Long.bitCount(evalInfo.bb_b_pawns);
		int count_knights = Long.bitCount(evalInfo.bb_w_knights) - Long.bitCount(evalInfo.bb_b_knights);
		int count_bishops = Long.bitCount(evalInfo.bb_w_bishops) - Long.bitCount(evalInfo.bb_b_bishops);
		int count_rooks = Long.bitCount(evalInfo.bb_w_rooks) - Long.bitCount(evalInfo.bb_b_rooks);
		int count_queens = Long.bitCount(evalInfo.bb_w_queens) - Long.bitCount(evalInfo.bb_b_queens);
		
		evalInfo.eval_o_part1 += count_pawns * EvalConstants.MATERIAL[PAWN]
				+ count_knights * EvalConstants.MATERIAL[NIGHT]
				+ count_bishops * EvalConstants.MATERIAL[BISHOP]
				+ count_rooks * EvalConstants.MATERIAL[ROOK]
				+ count_queens * EvalConstants.MATERIAL[QUEEN];
		
		evalInfo.eval_e_part1 += count_pawns * EvalConstants.MATERIAL[PAWN]
				+ count_knights * EvalConstants.MATERIAL[NIGHT]
				+ count_bishops * EvalConstants.MATERIAL[BISHOP]
				+ count_rooks * EvalConstants.MATERIAL[ROOK]
				+ count_queens * EvalConstants.MATERIAL[QUEEN];
	}
}

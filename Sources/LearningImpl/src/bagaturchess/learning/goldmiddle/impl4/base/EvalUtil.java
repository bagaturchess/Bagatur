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
		
		evalInfo.clearEvals1();
		evalInfo.clearEvals2();
		evalInfo.clearEvalAttacks();
		evalInfo.fillBB(cb);
		
		return taperedEval2(cb, evalInfo);
	}
	

	private static int taperedEval1(final ChessBoard cb, final EvalInfo evalInfo) {
		final int pawnScore = getPawnScores(cb, evalInfo);
		final int imbalances = getImbalances(cb, evalInfo);
		
		final int scoreMg = cb.phase == PHASE_TOTAL ? 0 : pawnScore + imbalances;
		
		final int scoreEg = pawnScore + imbalances;

		return ((scoreMg * (PHASE_TOTAL - cb.phase)) + scoreEg * cb.phase) / PHASE_TOTAL / calculateScaleFactor(cb);		
	}
	
	
	private static int taperedEval2(final ChessBoard cb, final EvalInfo evalInfo) {
		final int mgEgScore = calculateMobilityScoresAndSetAttacks(cb, evalInfo) + calculateThreats(cb, evalInfo) + calculatePawnShieldBonus(cb, evalInfo);
		final int others = calculateOthers(cb, evalInfo);
		
		final int scoreMg = cb.phase == PHASE_TOTAL ? 0
				: getMgScore(mgEgScore) + cb.psqtScore_mg + KingSafetyEval.calculateScores(cb, evalInfo) + calculateSpace(cb, evalInfo) + others;
		
		final int scoreEg = getEgScore(mgEgScore) + cb.psqtScore_eg + PassedPawnEval.calculateScores(cb, evalInfo) + others;

		return ((scoreMg * (PHASE_TOTAL - cb.phase)) + scoreEg * cb.phase) / PHASE_TOTAL / calculateScaleFactor(cb);
	}
	
	
	public static int getMgScore(final int score) {
		return (score + 0x8000) >> 16;
	}

	public static int getEgScore(final int score) {
		return (short) (score & 0xffff);
	}

	private static int calculateScaleFactor(final ChessBoard cb) {
		// opposite bishops endgame?
		if (MaterialUtil.oppositeBishops(cb.materialKey)) {
			if (((cb.pieces[WHITE][BISHOP] & Bitboard.BLACK_SQUARES) == 0) == ((cb.pieces[BLACK][BISHOP] & Bitboard.WHITE_SQUARES) == 0)) {
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
				* Long.bitCount((cb.pieces[WHITE][PAWN] >>> 8) & (cb.pieces[WHITE][NIGHT] | cb.pieces[WHITE][BISHOP]) & Bitboard.RANK_234);
		score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_SPACE]
				* Long.bitCount((cb.pieces[BLACK][PAWN] << 8) & (cb.pieces[BLACK][NIGHT] | cb.pieces[BLACK][BISHOP]) & Bitboard.RANK_567);

		// idea taken from Laser
		long space = cb.pieces[WHITE][PAWN] >>> 8;
		space |= space >>> 8 | space >>> 16;
		score += EvalConstants.SPACE[Long.bitCount(cb.friendlyPieces[WHITE])]
				* Long.bitCount(space & ~cb.pieces[WHITE][PAWN] & ~cb.attacks[BLACK][PAWN] & Bitboard.FILE_CDEF);
		space = cb.pieces[BLACK][PAWN] << 8;
		space |= space << 8 | space << 16;
		score -= EvalConstants.SPACE[Long.bitCount(cb.friendlyPieces[BLACK])]
				* Long.bitCount(space & ~cb.pieces[BLACK][PAWN] & ~cb.attacks[WHITE][PAWN] & Bitboard.FILE_CDEF);

		return score;
	}

	public static int getPawnScores(final ChessBoard cb, final EvalInfo evalInfo) {
		if (!EngineConstants.TEST_EVAL_CACHES) {
			final int score = PawnEvalCache.updateBoardAndGetScore(cb);
			if (score != ChessConstants.CACHE_MISS) {
				return score;
			}
		}

		return calculatePawnScores(cb, evalInfo);
	}

	private static int calculatePawnScores(final ChessBoard cb, final EvalInfo evalInfo) {

		int score = 0;

		// penalty for doubled pawns
		for (int i = 0; i < 8; i++) {
			if (Long.bitCount(cb.pieces[WHITE][PAWN] & Bitboard.FILES[i]) > 1) {
				score -= EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_DOUBLE];
			}
			if (Long.bitCount(cb.pieces[BLACK][PAWN] & Bitboard.FILES[i]) > 1) {
				score += EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_DOUBLE];
			}
		}

		// bonus for connected pawns
		long pawns = Bitboard.getWhitePawnAttacks(cb.pieces[WHITE][PAWN]) & cb.pieces[WHITE][PAWN];
		while (pawns != 0) {
			score += EvalConstants.PAWN_CONNECTED[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getBlackPawnAttacks(cb.pieces[BLACK][PAWN]) & cb.pieces[BLACK][PAWN];
		while (pawns != 0) {
			score -= EvalConstants.PAWN_CONNECTED[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}

		// bonus for neighbour pawns
		pawns = Bitboard.getPawnNeighbours(cb.pieces[WHITE][PAWN]) & cb.pieces[WHITE][PAWN];
		while (pawns != 0) {
			score += EvalConstants.PAWN_NEIGHBOUR[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getPawnNeighbours(cb.pieces[BLACK][PAWN]) & cb.pieces[BLACK][PAWN];
		while (pawns != 0) {
			score -= EvalConstants.PAWN_NEIGHBOUR[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}

		// set outposts
		cb.passedPawnsAndOutposts = 0;
		pawns = Bitboard.getWhitePawnAttacks(cb.pieces[WHITE][PAWN]) & ~cb.pieces[WHITE][PAWN] & ~cb.pieces[BLACK][PAWN];
		while (pawns != 0) {
			if ((Bitboard.getWhiteAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.pieces[BLACK][PAWN]) == 0) {
				cb.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getBlackPawnAttacks(cb.pieces[BLACK][PAWN]) & ~cb.pieces[WHITE][PAWN] & ~cb.pieces[BLACK][PAWN];
		while (pawns != 0) {
			if ((Bitboard.getBlackAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.pieces[WHITE][PAWN]) == 0) {
				cb.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}

		int index;

		// white
		pawns = cb.pieces[WHITE][PAWN];
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);

			// isolated pawns
			if ((Bitboard.FILES_ADJACENT[index & 7] & cb.pieces[WHITE][PAWN]) == 0) {
				score -= EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_ISOLATED];
			}

			// backward pawns
			else if ((Bitboard.getBlackAdjacentMask(index + 8) & cb.pieces[WHITE][PAWN]) == 0) {
				if ((StaticMoves.PAWN_ATTACKS[WHITE][index + 8] & cb.pieces[BLACK][PAWN]) != 0) {
					if ((Bitboard.FILES[index & 7] & cb.pieces[BLACK][PAWN]) == 0) {
						score -= EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_BACKWARD];
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(StaticMoves.PAWN_ATTACKS[WHITE][index] & cb.pieces[WHITE][PAWN]) == 2) {
				score -= EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_INVERSE];
			}

			// set passed pawns
			if ((Bitboard.getWhitePassedPawnMask(index) & cb.pieces[BLACK][PAWN]) == 0) {
				cb.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passed pawns (no pawns in front, more friendly pawns behind and adjacent than enemy pawns)
			else if (63 - Long.numberOfLeadingZeros((cb.pieces[WHITE][PAWN] | cb.pieces[BLACK][PAWN]) & Bitboard.FILES[index & 7]) == index) {
				if (Long.bitCount(cb.pieces[WHITE][PAWN] & Bitboard.getBlackPassedPawnMask(index + 8)) >= Long
						.bitCount(cb.pieces[BLACK][PAWN] & Bitboard.getWhitePassedPawnMask(index))) {
					score += EvalConstants.PASSED_CANDIDATE[index / 8];
				}
			}

			pawns &= pawns - 1;
		}

		// black
		pawns = cb.pieces[BLACK][PAWN];
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);

			// isolated pawns
			if ((Bitboard.FILES_ADJACENT[index & 7] & cb.pieces[BLACK][PAWN]) == 0) {
				score += EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_ISOLATED];
			}

			// backward pawns
			else if ((Bitboard.getWhiteAdjacentMask(index - 8) & cb.pieces[BLACK][PAWN]) == 0) {
				if ((StaticMoves.PAWN_ATTACKS[BLACK][index - 8] & cb.pieces[WHITE][PAWN]) != 0) {
					if ((Bitboard.FILES[index & 7] & cb.pieces[WHITE][PAWN]) == 0) {
						score += EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_BACKWARD];
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(StaticMoves.PAWN_ATTACKS[BLACK][index] & cb.pieces[BLACK][PAWN]) == 2) {
				score += EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_INVERSE];
			}

			// set passed pawns
			if ((Bitboard.getBlackPassedPawnMask(index) & cb.pieces[WHITE][PAWN]) == 0) {
				cb.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passers
			else if (Long.numberOfTrailingZeros((cb.pieces[WHITE][PAWN] | cb.pieces[BLACK][PAWN]) & Bitboard.FILES[index & 7]) == index) {
				if (Long.bitCount(cb.pieces[BLACK][PAWN] & Bitboard.getWhitePassedPawnMask(index - 8)) >= Long
						.bitCount(cb.pieces[WHITE][PAWN] & Bitboard.getBlackPassedPawnMask(index))) {
					score -= EvalConstants.PASSED_CANDIDATE[7 - index / 8];
				}
			}

			pawns &= pawns - 1;
		}

		if (EngineConstants.TEST_EVAL_CACHES) {
			final int cachedScore = PawnEvalCache.updateBoardAndGetScore(cb);
			if (cachedScore != ChessConstants.CACHE_MISS) {
				if (cachedScore != score) {
					throw new RuntimeException(String.format("Cached pawn eval score != score: %s, %s", cachedScore, score));
				}
			}
		}

		PawnEvalCache.addValue(cb.pawnZobristKey, score, cb.passedPawnsAndOutposts);

		return score;
	}

	public static int getImbalances(final ChessBoard cb, final EvalInfo evalInfo) {
		if (!EngineConstants.TEST_EVAL_CACHES) {
			final int score = MaterialCache.getScore(cb.materialKey);
			if (score != ChessConstants.CACHE_MISS) {
				return score;
			}
		}
		return calculateImbalances(cb, evalInfo);
	}

	private static int calculateImbalances(final ChessBoard cb, final EvalInfo evalInfo) {

		int score = 0;

		// material
		score += calculateMaterialScore(cb);

		// knights and pawns
		score += Long.bitCount(cb.pieces[WHITE][NIGHT]) * EvalConstants.NIGHT_PAWN[Long.bitCount(cb.pieces[WHITE][PAWN])];
		score -= Long.bitCount(cb.pieces[BLACK][NIGHT]) * EvalConstants.NIGHT_PAWN[Long.bitCount(cb.pieces[BLACK][PAWN])];

		// rooks and pawns
		score += Long.bitCount(cb.pieces[WHITE][ROOK]) * EvalConstants.ROOK_PAWN[Long.bitCount(cb.pieces[WHITE][PAWN])];
		score -= Long.bitCount(cb.pieces[BLACK][ROOK]) * EvalConstants.ROOK_PAWN[Long.bitCount(cb.pieces[BLACK][PAWN])];

		// double bishop
		if (Long.bitCount(cb.pieces[WHITE][BISHOP]) == 2) {
			score += EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_BISHOP_DOUBLE];
		}
		if (Long.bitCount(cb.pieces[BLACK][BISHOP]) == 2) {
			score -= EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_BISHOP_DOUBLE];
		}

		// queen and nights
		if (cb.pieces[WHITE][QUEEN] != 0) {
			score += Long.bitCount(cb.pieces[WHITE][NIGHT]) * EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_QUEEN_NIGHT];
		}
		if (cb.pieces[BLACK][QUEEN] != 0) {
			score -= Long.bitCount(cb.pieces[BLACK][NIGHT]) * EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_QUEEN_NIGHT];
		}

		// rook pair
		if (Long.bitCount(cb.pieces[WHITE][ROOK]) > 1) {
			score += EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_ROOK_PAIR];
		}
		if (Long.bitCount(cb.pieces[BLACK][ROOK]) > 1) {
			score -= EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_ROOK_PAIR];
		}

		if (EngineConstants.TEST_EVAL_CACHES) {
			final int cachedScore = MaterialCache.getScore(cb.materialKey);
			if (cachedScore != ChessConstants.CACHE_MISS) {
				if (cachedScore != score) {
					throw new RuntimeException(String.format("Cached material score != score: %s, %s", cachedScore, score));
				}
			}
		}

		MaterialCache.addValue(cb.materialKey, score);

		return score;
	}

	public static int calculateThreats(final ChessBoard cb, final EvalInfo evalInfo) {
		int score = 0;
		final long whitePawns = cb.pieces[WHITE][PAWN];
		final long blackPawns = cb.pieces[BLACK][PAWN];
		final long whiteMinorAttacks = cb.attacks[WHITE][NIGHT] | cb.attacks[WHITE][BISHOP];
		final long blackMinorAttacks = cb.attacks[BLACK][NIGHT] | cb.attacks[BLACK][BISHOP];
		final long whitePawnAttacks = cb.attacks[WHITE][PAWN];
		final long blackPawnAttacks = cb.attacks[BLACK][PAWN];
		final long whiteAttacks = cb.attacksAll[WHITE];
		final long blackAttacks = cb.attacksAll[BLACK];
		final long whites = cb.friendlyPieces[WHITE];
		final long blacks = cb.friendlyPieces[BLACK];

		// double attacked pieces
		long piece = cb.doubleAttacks[WHITE] & blacks;
		while (piece != 0) {
			score += EvalConstants.DOUBLE_ATTACKED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
			piece &= piece - 1;
		}
		piece = cb.doubleAttacks[BLACK] & whites;
		while (piece != 0) {
			score -= EvalConstants.DOUBLE_ATTACKED[cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]];
			piece &= piece - 1;
		}

		if (MaterialUtil.hasPawns(cb.materialKey)) {

			// unused outposts
			score += Long.bitCount(cb.passedPawnsAndOutposts & cb.emptySpaces & whiteMinorAttacks & whitePawnAttacks)
					* EvalConstants.THREATS[EvalConstants.IX_UNUSED_OUTPOST];
			score -= Long.bitCount(cb.passedPawnsAndOutposts & cb.emptySpaces & blackMinorAttacks & blackPawnAttacks)
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
		score += Long.bitCount(whiteAttacks & (cb.pieces[BLACK][NIGHT] | cb.pieces[BLACK][BISHOP] & ~blackAttacks))
				* EvalConstants.THREATS[EvalConstants.IX_MAJOR_ATTACKED];
		score -= Long.bitCount(blackAttacks & (cb.pieces[WHITE][NIGHT] | cb.pieces[WHITE][BISHOP] & ~whiteAttacks))
				* EvalConstants.THREATS[EvalConstants.IX_MAJOR_ATTACKED];

		if (cb.pieces[BLACK][QUEEN] != 0) {
			// queen attacked by rook
			score += Long.bitCount(cb.attacks[WHITE][ROOK] & cb.pieces[BLACK][QUEEN]) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED];
			// queen attacked by minors
			score += Long.bitCount(whiteMinorAttacks & cb.pieces[BLACK][QUEEN]) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
		}

		if (cb.pieces[WHITE][QUEEN] != 0) {
			// queen attacked by rook
			score -= Long.bitCount(cb.attacks[BLACK][ROOK] & cb.pieces[WHITE][QUEEN]) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED];
			// queen attacked by minors
			score -= Long.bitCount(blackMinorAttacks & cb.pieces[WHITE][QUEEN]) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
		}

		// rook attacked by minors
		score += Long.bitCount(whiteMinorAttacks & cb.pieces[BLACK][ROOK]) * EvalConstants.THREATS[EvalConstants.IX_ROOK_ATTACKED];
		score -= Long.bitCount(blackMinorAttacks & cb.pieces[WHITE][ROOK]) * EvalConstants.THREATS[EvalConstants.IX_ROOK_ATTACKED];

		return score;
	}

	public static int calculateOthers(final ChessBoard cb, final EvalInfo evalInfo) {
		int score = 0;
		long piece;

		final long whitePawns = cb.pieces[WHITE][PAWN];
		final long blackPawns = cb.pieces[BLACK][PAWN];
		final long whitePawnAttacks = cb.attacks[WHITE][PAWN];
		final long blackPawnAttacks = cb.attacks[BLACK][PAWN];
		final long whiteAttacks = cb.attacksAll[WHITE];
		final long blackAttacks = cb.attacksAll[BLACK];
		final long whites = cb.friendlyPieces[WHITE];
		final long blacks = cb.friendlyPieces[BLACK];

		// side to move
		score += ChessConstants.COLOR_FACTOR[cb.colorToMove] * EvalConstants.SIDE_TO_MOVE_BONUS;

		// piece attacked and only defended by a rook or queen
		piece = whites & blackAttacks & whiteAttacks & ~(whitePawnAttacks | cb.attacks[WHITE][NIGHT] | cb.attacks[WHITE][BISHOP]);
		if (piece != 0) {
			score += Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ONLY_MAJOR_DEFENDERS];
		}
		piece = blacks & whiteAttacks & blackAttacks & ~(blackPawnAttacks | cb.attacks[BLACK][NIGHT] | cb.attacks[BLACK][BISHOP]);
		if (piece != 0) {
			score -= Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ONLY_MAJOR_DEFENDERS];
		}

		// WHITE ROOK
		if (cb.pieces[WHITE][ROOK] != 0) {

			piece = cb.pieces[WHITE][ROOK];

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
		if (cb.pieces[BLACK][ROOK] != 0) {

			piece = cb.pieces[BLACK][ROOK];

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
		if (cb.pieces[WHITE][BISHOP] != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.pieces[WHITE][BISHOP] & cb.passedPawnsAndOutposts & whitePawnAttacks;
			if (piece != 0) {
				score += Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
			}

			piece = cb.pieces[WHITE][BISHOP];
			if ((piece & Bitboard.WHITE_SQUARES) != 0) {
				// pawns on same color as bishop
				score += EvalConstants.BISHOP_PAWN[Long.bitCount(whitePawns & Bitboard.WHITE_SQUARES)];

				// attacking center squares
				if (Long.bitCount(cb.attacks[WHITE][BISHOP] & Bitboard.E4_D5) == 2) {
					score += EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
				}
			}
			if ((piece & Bitboard.BLACK_SQUARES) != 0) {
				// pawns on same color as bishop
				score += EvalConstants.BISHOP_PAWN[Long.bitCount(whitePawns & Bitboard.BLACK_SQUARES)];

				// attacking center squares
				if (Long.bitCount(cb.attacks[WHITE][BISHOP] & Bitboard.D4_E5) == 2) {
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
		if (cb.pieces[BLACK][BISHOP] != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.pieces[BLACK][BISHOP] & cb.passedPawnsAndOutposts & blackPawnAttacks;
			if (piece != 0) {
				score -= Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
			}

			piece = cb.pieces[BLACK][BISHOP];
			if ((piece & Bitboard.WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= EvalConstants.BISHOP_PAWN[Long.bitCount(blackPawns & Bitboard.WHITE_SQUARES)];

				// bonus for attacking center squares
				if (Long.bitCount(cb.attacks[BLACK][BISHOP] & Bitboard.E4_D5) == 2) {
					score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
				}
			}
			if ((piece & Bitboard.BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= EvalConstants.BISHOP_PAWN[Long.bitCount(blackPawns & Bitboard.BLACK_SQUARES)];

				// bonus for attacking center squares
				if (Long.bitCount(cb.attacks[BLACK][BISHOP] & Bitboard.D4_E5) == 2) {
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
		piece = cb.pieces[WHITE][NIGHT] & cb.passedPawnsAndOutposts & whitePawnAttacks;
		if (piece != 0) {
			score += Long.bitCount(piece) * EvalConstants.OTHER_SCORES[EvalConstants.IX_OUTPOST];
		}
		piece = cb.pieces[BLACK][NIGHT] & cb.passedPawnsAndOutposts & blackPawnAttacks;
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
		long piece = cb.pieces[WHITE][PAWN] & ChessConstants.KING_AREA[WHITE][cb.kingIndex[WHITE]] & ~cb.attacks[BLACK][PAWN];
		while (piece != 0) {
			file = Long.numberOfTrailingZeros(piece) & 7;
			whiteScore += EvalConstants.SHIELD_BONUS[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= ~Bitboard.FILES[file];
		}
		if (cb.pieces[BLACK][QUEEN] == 0) {
			whiteScore /= 2;
		}

		int blackScore = 0;
		piece = cb.pieces[BLACK][PAWN] & ChessConstants.KING_AREA[BLACK][cb.kingIndex[BLACK]] & ~cb.attacks[WHITE][PAWN];
		while (piece != 0) {
			file = (63 - Long.numberOfLeadingZeros(piece)) & 7;
			blackScore += EvalConstants.SHIELD_BONUS[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8];
			piece &= ~Bitboard.FILES[file];
		}
		if (cb.pieces[WHITE][QUEEN] == 0) {
			blackScore /= 2;
		}

		return whiteScore - blackScore;
	}

	public static int calculateMobilityScoresAndSetAttacks(final ChessBoard cb, final EvalInfo evalInfo) {

		cb.clearEvalAttacks();
		cb.updatePawnAttacks();

		int score = 0;
		long moves;
		for (int color = WHITE; color <= BLACK; color++) {

			int tempScore = 0;

			final long kingArea = ChessConstants.KING_AREA[1 - color][cb.kingIndex[1 - color]];
			final long safeMoves = ~cb.friendlyPieces[color] & ~cb.attacks[1 - color][PAWN];

			// knights
			long piece = cb.pieces[color][NIGHT] & ~cb.pinnedPieces;
			while (piece != 0) {
				moves = StaticMoves.KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
				cb.updateAttacks(moves, NIGHT, color, kingArea);
				tempScore += EvalConstants.MOBILITY_KNIGHT[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// bishops
			piece = cb.pieces[color][BISHOP];
			while (piece != 0) {
				moves = MagicUtil.getBishopMoves(Long.numberOfTrailingZeros(piece), cb.allPieces ^ cb.pieces[color][QUEEN]);
				cb.updateAttacks(moves, BISHOP, color, kingArea);
				tempScore += EvalConstants.MOBILITY_BISHOP[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// rooks
			piece = cb.pieces[color][ROOK];
			while (piece != 0) {
				moves = MagicUtil.getRookMoves(Long.numberOfTrailingZeros(piece), cb.allPieces ^ cb.pieces[color][ROOK] ^ cb.pieces[color][QUEEN]);
				cb.updateAttacks(moves, ROOK, color, kingArea);
				tempScore += EvalConstants.MOBILITY_ROOK[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// queens
			piece = cb.pieces[color][QUEEN];
			while (piece != 0) {
				moves = MagicUtil.getQueenMoves(Long.numberOfTrailingZeros(piece), cb.allPieces);
				cb.updateAttacks(moves, QUEEN, color, kingArea);
				tempScore += EvalConstants.MOBILITY_QUEEN[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			score += tempScore * ChessConstants.COLOR_FACTOR[color];

		}

		// TODO king-attacks with or without enemy attacks?
		// WHITE king
		moves = StaticMoves.KING_MOVES[cb.kingIndex[WHITE]] & ~StaticMoves.KING_MOVES[cb.kingIndex[BLACK]];
		cb.attacks[WHITE][KING] = moves;
		cb.doubleAttacks[WHITE] |= cb.attacksAll[WHITE] & moves;
		cb.attacksAll[WHITE] |= moves;
		score += EvalConstants.MOBILITY_KING[Long.bitCount(moves & ~cb.friendlyPieces[WHITE] & ~cb.attacksAll[BLACK])];

		// BLACK king
		moves = StaticMoves.KING_MOVES[cb.kingIndex[BLACK]] & ~StaticMoves.KING_MOVES[cb.kingIndex[WHITE]];
		cb.attacks[BLACK][KING] = moves;
		cb.doubleAttacks[BLACK] |= cb.attacksAll[BLACK] & moves;
		cb.attacksAll[BLACK] |= moves;
		score -= EvalConstants.MOBILITY_KING[Long.bitCount(moves & ~cb.friendlyPieces[BLACK] & ~cb.attacksAll[WHITE])];

		return score;
	}

	public static int[] calculatePositionScores(final ChessBoard cb) {

		int[] score = new int[2];
		for (int color = WHITE; color <= BLACK; color++) {
			for (int pieceType = PAWN; pieceType <= KING; pieceType++) {
				long piece = cb.pieces[color][pieceType];
				while (piece != 0) {
					score[0] += EvalConstants.PSQT_MG[pieceType][color][Long.numberOfTrailingZeros(piece)];
					score[1] += EvalConstants.PSQT_EG[pieceType][color][Long.numberOfTrailingZeros(piece)];
					piece &= piece - 1;
				}
			}
		}
		return score;
	}

	public static int calculateMaterialScore(final ChessBoard cb) {
		return (Long.bitCount(cb.pieces[WHITE][PAWN]) - Long.bitCount(cb.pieces[BLACK][PAWN])) * EvalConstants.MATERIAL[PAWN]
				+ (Long.bitCount(cb.pieces[WHITE][NIGHT]) - Long.bitCount(cb.pieces[BLACK][NIGHT])) * EvalConstants.MATERIAL[NIGHT]
				+ (Long.bitCount(cb.pieces[WHITE][BISHOP]) - Long.bitCount(cb.pieces[BLACK][BISHOP])) * EvalConstants.MATERIAL[BISHOP]
				+ (Long.bitCount(cb.pieces[WHITE][ROOK]) - Long.bitCount(cb.pieces[BLACK][ROOK])) * EvalConstants.MATERIAL[ROOK]
				+ (Long.bitCount(cb.pieces[WHITE][QUEEN]) - Long.bitCount(cb.pieces[BLACK][QUEEN])) * EvalConstants.MATERIAL[QUEEN];
	}

	
	public static class EvalInfo {
		
		
		public final long[][] attacks = new long[2][7];
		public final long[] attacksAll = new long[2];
		public final long[] doubleAttacks = new long[2];
		public final int[] kingAttackersFlag = new int[2];
		
		public long passedPawnsAndOutposts;
		
		public long bb_free;
		public long bb_all;
		public long bb_all_w_pieces;
		public long bb_all_b_pieces;
		public long bb_w_pawns;
		public long bb_b_pawns;
		public long bb_w_bishops;
		public long bb_b_bishops;
		public long bb_w_knights;
		public long bb_b_knights;
		public long bb_w_queens;
		public long bb_b_queens;
		public long bb_w_rooks;
		public long bb_b_rooks;
		public long bb_w_king;
		public long bb_b_king;
		
		public int eval_o_part1;
		public int eval_e_part1;
		public int eval_o_part2;
		public int eval_e_part2;
		
		
		public final void clearEvalAttacks() {
			kingAttackersFlag[WHITE] = 0;
			kingAttackersFlag[BLACK] = 0;
			attacks[WHITE][NIGHT] = 0;
			attacks[BLACK][NIGHT] = 0;
			attacks[WHITE][BISHOP] = 0;
			attacks[BLACK][BISHOP] = 0;
			attacks[WHITE][ROOK] = 0;
			attacks[BLACK][ROOK] = 0;
			attacks[WHITE][QUEEN] = 0;
			attacks[BLACK][QUEEN] = 0;
			attacksAll[WHITE] = 0;
			attacksAll[BLACK] = 0;
			doubleAttacks[WHITE] = 0;
			doubleAttacks[BLACK] = 0;
		}
		
		
		public final void fillBB(ChessBoard cb) {
			bb_w_pawns = cb.pieces[WHITE][PAWN];
			bb_b_pawns = cb.pieces[BLACK][PAWN];
			bb_w_bishops = cb.pieces[WHITE][BISHOP];
			bb_b_bishops = cb.pieces[BLACK][BISHOP];
			bb_w_knights = cb.pieces[WHITE][NIGHT];
			bb_b_knights = cb.pieces[BLACK][NIGHT];
			bb_w_queens = cb.pieces[WHITE][QUEEN];
			bb_b_queens = cb.pieces[BLACK][QUEEN];
			bb_w_rooks = cb.pieces[WHITE][ROOK];
			bb_b_rooks = cb.pieces[BLACK][ROOK];
			bb_w_king = cb.pieces[WHITE][KING];
			bb_b_king = cb.pieces[BLACK][KING];
			bb_all_w_pieces = bb_w_pawns | bb_w_bishops | bb_w_knights | bb_w_queens | bb_w_rooks | bb_w_king;
			bb_all_b_pieces = bb_b_pawns | bb_b_bishops | bb_b_knights | bb_b_queens | bb_b_rooks | bb_b_king;
			bb_all = bb_all_w_pieces | bb_all_b_pieces;
			bb_free = ~bb_all;
		}
		
		
		public final void clearEvals1() {
			eval_o_part1 = 0;
			eval_e_part1 = 0;
		}
		
		
		public final void clearEvals2() {
			eval_o_part2 = 0;
			eval_e_part2 = 0;
		}
		
		
		public final long getFriendlyPieces(int colour) {
			return colour == WHITE ? bb_all_w_pieces : bb_all_b_pieces;
		}
		
		public final long getPieces(int colour, int type) {
			switch (type) {
				case PAWN:
					return colour == WHITE ? bb_w_pawns : bb_b_pawns;
				case NIGHT:
					return colour == WHITE ? bb_w_knights : bb_b_knights;
				case BISHOP:
					return colour == WHITE ? bb_w_bishops : bb_b_bishops;
				case ROOK:
					return colour == WHITE ? bb_w_rooks : bb_b_rooks;
				case QUEEN:
					return colour == WHITE ? bb_w_queens : bb_b_queens;
				case KING:
					return colour == WHITE ? bb_w_king : bb_b_king;
				default:
					throw new IllegalStateException();
			}
		}
	}
}

package bagaturchess.engines.chess22kadapter;


import static bagaturchess.engines.chess22kadapter.ChessConstants.BISHOP;
import static bagaturchess.engines.chess22kadapter.ChessConstants.BLACK;
import static bagaturchess.engines.chess22kadapter.ChessConstants.KING;
import static bagaturchess.engines.chess22kadapter.ChessConstants.NIGHT;
import static bagaturchess.engines.chess22kadapter.ChessConstants.PAWN;
import static bagaturchess.engines.chess22kadapter.ChessConstants.QUEEN;
import static bagaturchess.engines.chess22kadapter.ChessConstants.ROOK;
import static bagaturchess.engines.chess22kadapter.ChessConstants.WHITE;


public class EvalUtil {

	public static final int MG = 0;
	public static final int EG = 1;

	public static final int PHASE_TOTAL =
			
			4 * EvalConstants.PHASE[NIGHT]
			+ 4 * EvalConstants.PHASE[BISHOP]
			+ 4 * EvalConstants.PHASE[ROOK]
			+ 2 * EvalConstants.PHASE[QUEEN];
	

	public static int getScore1(final IChessBoard cb) {
		
		final int pawnScore = getPawnScores(cb);
		final int materialScore = getImbalances(cb);
		
		final int scoreMg = getMgScore(cb.getPSQTScore())
														+ pawnScore
														+ materialScore;
		
		final int scoreEg = getEgScore(cb.getPSQTScore())
														+ pawnScore
														+ materialScore;
		
		return cb.interpolateScore(scoreMg, scoreEg);
	}

	
	public static int getScore2(final IChessBoard cb) {
		
		//final int pawnScore = getPawnScores(cb);
		//final int materialScore = getImbalances(cb);

		final int mgEgScore = calculateMobilityScoresAndSetAttackBoards(cb)
				+ PassedPawnEval.calculatePassedPawnScores(cb)
				+ calculateThreats(cb)
				+ calculatePawnShieldBonus(cb);
		
		final int scoreMg = getMgScore(mgEgScore)
								+ KingSafetyEval.calculateKingSafetyScores(cb)
								+ calculateSpace(cb)
								+ calculateOthers(cb);
		
		final int scoreEg = getEgScore(mgEgScore)
								+ calculateOthers(cb);
		
		return cb.interpolateScore(scoreMg, scoreEg);
	}
	
	
	public static int score(final int mgScore, final int egScore) {
		return (mgScore << 16) + egScore;
	}

	public static int getMgScore(final int score) {
		return (score + 0x8000) >> 16;
	}

	public static int getEgScore(final int score) {
		return (short) (score & 0xffff);
	}

	public static int calculateSpace(final IChessBoard cb) {

		int score = 0;

		score += EvalConstants.OTHER_SCORES[EvalConstants.IX_SPACE]
				* Long.bitCount((cb.getPieces(WHITE, PAWN) >>> 8) & (cb.getPieces(WHITE, NIGHT) | cb.getPieces(WHITE, BISHOP)) & Bitboard.RANK_234);
		score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_SPACE]
				* Long.bitCount((cb.getPieces(BLACK, PAWN) << 8) & (cb.getPieces(BLACK, NIGHT) | cb.getPieces(BLACK, BISHOP)) & Bitboard.RANK_567);

		// idea taken from Laser
		long space = cb.getPieces(WHITE, PAWN) >>> 8;
		space |= space >>> 8 | space >>> 16;
		score += EvalConstants.SPACE[Long.bitCount(cb.getFriendlyPieces(WHITE))]
				* Long.bitCount(space & ~cb.getPieces(WHITE, PAWN) & ~cb.getEvalInfo().attacks[BLACK][PAWN] & Bitboard.FILE_CDEF);
		space = cb.getPieces(BLACK, PAWN) << 8;
		space |= space << 8 | space << 16;
		score -= EvalConstants.SPACE[Long.bitCount(cb.getFriendlyPieces(BLACK))]
				* Long.bitCount(space & ~cb.getPieces(BLACK, PAWN) & ~cb.getEvalInfo().attacks[WHITE][PAWN] & Bitboard.FILE_CDEF);

		return score;
	}

	public static int getPawnScores(final IChessBoard cb) {
		return calculatePawnScores(cb);
	}

	private static int calculatePawnScores(final IChessBoard cb) {

		int score = 0;

		// penalty for doubled pawns
		for (int i = 0; i < 8; i++) {
			if (Long.bitCount(cb.getPieces(WHITE, PAWN) & Bitboard.FILES[i]) > 1) {
				score -= EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_DOUBLE];
			}
			if (Long.bitCount(cb.getPieces(BLACK, PAWN) & Bitboard.FILES[i]) > 1) {
				score += EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_DOUBLE];
			}
		}

		// bonus for connected pawns
		long pawns = Bitboard.getWhitePawnAttacks(cb.getPieces(WHITE, PAWN)) & cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			score += EvalConstants.PAWN_CONNECTED[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getBlackPawnAttacks(cb.getPieces(BLACK, PAWN)) & cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			score -= EvalConstants.PAWN_CONNECTED[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}

		// bonus for neighbour pawns
		pawns = Bitboard.getPawnNeighbours(cb.getPieces(WHITE, PAWN)) & cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			score += EvalConstants.PAWN_NEIGHBOUR[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getPawnNeighbours(cb.getPieces(BLACK, PAWN)) & cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			score -= EvalConstants.PAWN_NEIGHBOUR[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}

		// set outposts
		cb.getEvalInfo().passedPawnsAndOutposts = 0;
		pawns = Bitboard.getWhitePawnAttacks(cb.getPieces(WHITE, PAWN)) & ~cb.getPieces(WHITE, PAWN) & ~cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			if ((Bitboard.getWhiteAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.getPieces(BLACK, PAWN)) == 0) {
				cb.getEvalInfo().passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}
		pawns = Bitboard.getBlackPawnAttacks(cb.getPieces(BLACK, PAWN)) & ~cb.getPieces(WHITE, PAWN) & ~cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			if ((Bitboard.getBlackAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.getPieces(WHITE, PAWN)) == 0) {
				cb.getEvalInfo().passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}

		int index;

		// white
		pawns = cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);

			// isolated pawns
			if ((Bitboard.FILES_ADJACENT[index & 7] & cb.getPieces(WHITE, PAWN)) == 0) {
				score -= EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_ISOLATED];
			}

			// backward pawns
			else if ((Bitboard.getBlackAdjacentMask(index + 8) & cb.getPieces(WHITE, PAWN)) == 0) {
				if ((StaticMoves.PAWN_ATTACKS[WHITE][index + 8] & cb.getPieces(BLACK, PAWN)) != 0) {
					if ((Bitboard.FILES[index & 7] & cb.getPieces(BLACK, PAWN)) == 0) {
						score -= EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_BACKWARD];
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(StaticMoves.PAWN_ATTACKS[WHITE][index] & cb.getPieces(WHITE, PAWN)) == 2) {
				score -= EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_INVERSE];
			}

			// set passed pawns
			if ((Bitboard.getWhitePassedPawnMask(index) & cb.getPieces(BLACK, PAWN)) == 0) {
				cb.getEvalInfo().passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passed pawns (no pawns in front, more friendly pawns behind and adjacent than enemy pawns)
			else if (63 - Long.numberOfLeadingZeros((cb.getPieces(WHITE, PAWN) | cb.getPieces(BLACK, PAWN)) & Bitboard.FILES[index & 7]) == index) {
				if (Long.bitCount(cb.getPieces(WHITE, PAWN) & Bitboard.getBlackPassedPawnMask(index + 8)) >= Long
						.bitCount(cb.getPieces(BLACK, PAWN) & Bitboard.getWhitePassedPawnMask(index))) {
					score += EvalConstants.PASSED_CANDIDATE[index / 8];
				}
			}

			pawns &= pawns - 1;
		}

		// black
		pawns = cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);

			// isolated pawns
			if ((Bitboard.FILES_ADJACENT[index & 7] & cb.getPieces(BLACK, PAWN)) == 0) {
				score += EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_ISOLATED];
			}

			// backward pawns
			else if ((Bitboard.getWhiteAdjacentMask(index - 8) & cb.getPieces(BLACK, PAWN)) == 0) {
				if ((StaticMoves.PAWN_ATTACKS[BLACK][index - 8] & cb.getPieces(WHITE, PAWN)) != 0) {
					if ((Bitboard.FILES[index & 7] & cb.getPieces(WHITE, PAWN)) == 0) {
						score += EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_BACKWARD];
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(StaticMoves.PAWN_ATTACKS[BLACK][index] & cb.getPieces(BLACK, PAWN)) == 2) {
				score += EvalConstants.PAWN_SCORES[EvalConstants.IX_PAWN_INVERSE];
			}

			// set passed pawns
			if ((Bitboard.getBlackPassedPawnMask(index) & cb.getPieces(WHITE, PAWN)) == 0) {
				cb.getEvalInfo().passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passers
			else if (Long.numberOfTrailingZeros((cb.getPieces(WHITE, PAWN) | cb.getPieces(BLACK, PAWN)) & Bitboard.FILES[index & 7]) == index) {
				if (Long.bitCount(cb.getPieces(BLACK, PAWN) & Bitboard.getWhitePassedPawnMask(index - 8)) >= Long
						.bitCount(cb.getPieces(WHITE, PAWN) & Bitboard.getBlackPassedPawnMask(index))) {
					score -= EvalConstants.PASSED_CANDIDATE[7 - index / 8];
				}
			}

			pawns &= pawns - 1;
		}
		
		return score;
	}

	public static int getImbalances(final IChessBoard cb) {
		return calculateImbalances(cb);
	}

	private static int calculateImbalances(final IChessBoard cb) {

		int score = 0;

		// material
		score += calculateMaterialScore(cb);

		// knight bonus if there are a lot of pawns
		score += Long.bitCount(cb.getPieces(WHITE, NIGHT)) * EvalConstants.NIGHT_PAWN[Long.bitCount(cb.getPieces(WHITE, PAWN))];
		score -= Long.bitCount(cb.getPieces(BLACK, NIGHT)) * EvalConstants.NIGHT_PAWN[Long.bitCount(cb.getPieces(BLACK, PAWN))];

		// rook bonus if there are no pawns
		score += Long.bitCount(cb.getPieces(WHITE, ROOK)) * EvalConstants.ROOK_PAWN[Long.bitCount(cb.getPieces(WHITE, PAWN))];
		score -= Long.bitCount(cb.getPieces(BLACK, ROOK)) * EvalConstants.ROOK_PAWN[Long.bitCount(cb.getPieces(BLACK, PAWN))];

		// double bishop bonus
		if (Long.bitCount(cb.getPieces(WHITE, BISHOP)) == 2) {
			score += EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_BISHOP_DOUBLE];
		}
		if (Long.bitCount(cb.getPieces(BLACK, BISHOP)) == 2) {
			score -= EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_BISHOP_DOUBLE];
		}

		// queen and nights
		if (cb.getPieces(WHITE, QUEEN) != 0) {
			score += Long.bitCount(cb.getPieces(WHITE, NIGHT)) * EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_QUEEN_NIGHT];
		}
		if (cb.getPieces(BLACK, QUEEN) != 0) {
			score -= Long.bitCount(cb.getPieces(BLACK, NIGHT)) * EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_QUEEN_NIGHT];
		}

		// rook pair
		if (Long.bitCount(cb.getPieces(WHITE, ROOK)) > 1) {
			score -= EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_ROOK_PAIR];
		}
		if (Long.bitCount(cb.getPieces(BLACK, ROOK)) > 1) {
			score += EvalConstants.IMBALANCE_SCORES[EvalConstants.IX_ROOK_PAIR];
		}
		
		return score;
	}

	public static int calculateThreats(final IChessBoard cb) {
		int score = 0;
		final long whitePawns = cb.getPieces(WHITE, PAWN);
		final long blackPawns = cb.getPieces(BLACK, PAWN);
		final long whiteMinorAttacks = cb.getEvalInfo().attacks[WHITE][NIGHT] | cb.getEvalInfo().attacks[WHITE][BISHOP];
		final long blackMinorAttacks = cb.getEvalInfo().attacks[BLACK][NIGHT] | cb.getEvalInfo().attacks[BLACK][BISHOP];
		final long whitePawnAttacks = cb.getEvalInfo().attacks[WHITE][PAWN];
		final long blackPawnAttacks = cb.getEvalInfo().attacks[BLACK][PAWN];
		final long whiteAttacks = cb.getEvalInfo().attacksAll[WHITE];
		final long blackAttacks = cb.getEvalInfo().attacksAll[BLACK];
		final long whites = cb.getFriendlyPieces(WHITE);
		final long blacks = cb.getFriendlyPieces(BLACK);

		// double attacked pieces
		long piece = cb.getEvalInfo().doubleAttacks[WHITE] & blacks;
		while (piece != 0) {
			score += EvalConstants.DOUBLE_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			piece &= piece - 1;
		}
		piece = cb.getEvalInfo().doubleAttacks[BLACK] & whites;
		while (piece != 0) {
			score -= EvalConstants.DOUBLE_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			piece &= piece - 1;
		}

		// unused outposts
		score += Long.bitCount(cb.getEvalInfo().passedPawnsAndOutposts & cb.getEmptySpaces() & whiteMinorAttacks & whitePawnAttacks)
				* EvalConstants.THREATS[EvalConstants.IX_UNUSED_OUTPOST];
		score -= Long.bitCount(cb.getEvalInfo().passedPawnsAndOutposts & cb.getEmptySpaces() & blackMinorAttacks & blackPawnAttacks)
				* EvalConstants.THREATS[EvalConstants.IX_UNUSED_OUTPOST];

		// pawn push threat
		piece = (whitePawns << 8) & cb.getEmptySpaces() & ~blackAttacks;
		score += Long.bitCount(Bitboard.getWhitePawnAttacks(piece) & blacks) * EvalConstants.THREATS[EvalConstants.IX_PAWN_PUSH_THREAT];
		piece = (blackPawns >>> 8) & cb.getEmptySpaces() & ~whiteAttacks;
		score -= Long.bitCount(Bitboard.getBlackPawnAttacks(piece) & whites) * EvalConstants.THREATS[EvalConstants.IX_PAWN_PUSH_THREAT];

		// piece is attacked by a pawn
		score += Long.bitCount(whitePawnAttacks & blacks & ~blackPawns) * EvalConstants.THREATS[EvalConstants.IX_PAWN_ATTACKS];
		score -= Long.bitCount(blackPawnAttacks & whites & ~whitePawns) * EvalConstants.THREATS[EvalConstants.IX_PAWN_ATTACKS];

		// multiple pawn attacks possible
		if (Long.bitCount(whitePawnAttacks & blacks) > 1) {
			score += EvalConstants.THREATS[EvalConstants.IX_MULTIPLE_PAWN_ATTACKS];
		}
		if (Long.bitCount(blackPawnAttacks & whites) > 1) {
			score -= EvalConstants.THREATS[EvalConstants.IX_MULTIPLE_PAWN_ATTACKS];
		}

		// minors under attack and not defended by a pawn
		score += Long.bitCount(whiteAttacks & (cb.getPieces(BLACK, NIGHT) | cb.getPieces(BLACK, BISHOP) & ~blackAttacks))
				* EvalConstants.THREATS[EvalConstants.IX_MAJOR_ATTACKED];
		score -= Long.bitCount(blackAttacks & (cb.getPieces(WHITE, NIGHT) | cb.getPieces(WHITE, BISHOP) & ~whiteAttacks))
				* EvalConstants.THREATS[EvalConstants.IX_MAJOR_ATTACKED];

		// pawn attacked
		score += Long.bitCount(whiteAttacks & blackPawns) * EvalConstants.THREATS[EvalConstants.IX_PAWN_ATTACKED];
		score -= Long.bitCount(blackAttacks & whitePawns) * EvalConstants.THREATS[EvalConstants.IX_PAWN_ATTACKED];

		if (cb.getPieces(BLACK, QUEEN) != 0) {
			// queen under attack by rook
			score += Long.bitCount(cb.getEvalInfo().attacks[WHITE][ROOK] & cb.getPieces(BLACK, QUEEN)) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED];
			// queen under attack by minors
			score += Long.bitCount(whiteMinorAttacks & cb.getPieces(BLACK, QUEEN)) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
		}

		if (cb.getPieces(WHITE, QUEEN) != 0) {
			// queen under attack by rook
			score -= Long.bitCount(cb.getEvalInfo().attacks[BLACK][ROOK] & cb.getPieces(WHITE, QUEEN)) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED];
			// queen under attack by minors
			score -= Long.bitCount(blackMinorAttacks & cb.getPieces(WHITE, QUEEN)) * EvalConstants.THREATS[EvalConstants.IX_QUEEN_ATTACKED_MINOR];
		}

		// rook under attack by minors
		score += Long.bitCount(whiteMinorAttacks & cb.getPieces(BLACK, ROOK)) * EvalConstants.THREATS[EvalConstants.IX_ROOK_ATTACKED];
		score -= Long.bitCount(blackMinorAttacks & cb.getPieces(WHITE, ROOK)) * EvalConstants.THREATS[EvalConstants.IX_ROOK_ATTACKED];

		// knight fork
		// skip when testing eval values because we break the loop if any fork has been found
		if (!EngineConstants.TEST_EVAL_VALUES) {
			long forked;
			piece = cb.getEvalInfo().attacks[WHITE][NIGHT] & ~blackAttacks & cb.getEmptySpaces();
			while (piece != 0) {
				forked = blacks & ~blackPawns & StaticMoves.KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
				if (Long.bitCount(forked) > 1) {
					if ((cb.getPieces(BLACK, KING) & forked) == 0) {
						score += EvalConstants.THREATS[EvalConstants.IX_NIGHT_FORK];
					} else {
						score += EvalConstants.THREATS[EvalConstants.IX_NIGHT_FORK_KING];
					}
					break;
				}
				piece &= piece - 1;
			}
			piece = cb.getEvalInfo().attacks[BLACK][NIGHT] & ~whiteAttacks & cb.getEmptySpaces();
			while (piece != 0) {
				forked = whites & ~whitePawns & StaticMoves.KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
				if (Long.bitCount(forked) > 1) {
					if ((cb.getPieces(WHITE, KING) & forked) == 0) {
						score -= EvalConstants.THREATS[EvalConstants.IX_NIGHT_FORK];
					} else {
						score -= EvalConstants.THREATS[EvalConstants.IX_NIGHT_FORK_KING];
					}
					break;
				}
				piece &= piece - 1;
			}
		}

		return score;
	}

	public static int calculateOthers(final IChessBoard cb) {
		int score = 0;
		long piece;

		final long whitePawns = cb.getPieces(WHITE, PAWN);
		final long blackPawns = cb.getPieces(BLACK, PAWN);
		final long whitePawnAttacks = cb.getEvalInfo().attacks[WHITE][PAWN];
		final long blackPawnAttacks = cb.getEvalInfo().attacks[BLACK][PAWN];
		final long whiteAttacks = cb.getEvalInfo().attacksAll[WHITE];
		final long blackAttacks = cb.getEvalInfo().attacksAll[BLACK];
		final long whites = cb.getFriendlyPieces(WHITE);
		final long blacks = cb.getFriendlyPieces(BLACK);

		// bonus for side to move
		score += ChessConstants.COLOR_FACTOR[cb.getColorToMove()] * EvalConstants.SIDE_TO_MOVE_BONUS;

		// piece attacked and only defended by a rook or queen
		piece = whites & blackAttacks & whiteAttacks & ~(whitePawnAttacks | cb.getEvalInfo().attacks[WHITE][NIGHT] | cb.getEvalInfo().attacks[WHITE][BISHOP]);
		while (piece != 0) {
			score -= EvalConstants.ONLY_MAJOR_DEFENDERS[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			piece &= piece - 1;
		}
		piece = blacks & whiteAttacks & blackAttacks & ~(blackPawnAttacks | cb.getEvalInfo().attacks[BLACK][NIGHT] | cb.getEvalInfo().attacks[BLACK][BISHOP]);
		while (piece != 0) {
			score += EvalConstants.ONLY_MAJOR_DEFENDERS[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
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

				score += EvalConstants.HANGING_2[hangingIndex];
			} else {
				score += EvalConstants.HANGING[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
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
				score -= EvalConstants.HANGING_2[hangingIndex];
			} else {
				score -= EvalConstants.HANGING[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			}
		}

		// WHITE ROOK
		if (cb.getPieces(WHITE, ROOK) != 0) {

			piece = cb.getPieces(WHITE, ROOK);

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					score += EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_BATTERY];
				}
			}

			// rook on 7th, king on 8th
			if (cb.getKingIndex(BLACK) >= 56) {
				score += Long.bitCount(piece & Bitboard.RANK_7) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_7TH_RANK];
			}

			// prison
			final long trapped = piece & EvalConstants.ROOK_PRISON[cb.getKingIndex(WHITE)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped << i & whitePawns) != 0) {
						score -= EvalConstants.ROOK_TRAPPED[(i / 8) - 1];
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
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
		if (cb.getPieces(BLACK, ROOK) != 0) {

			piece = cb.getPieces(BLACK, ROOK);

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_BATTERY];
				}
			}

			// rook on 2nd, king on 1st
			if (cb.getKingIndex(WHITE) <= 7) {
				score -= Long.bitCount(piece & Bitboard.RANK_2) * EvalConstants.OTHER_SCORES[EvalConstants.IX_ROOK_7TH_RANK];
			}

			// prison
			final long trapped = piece & EvalConstants.ROOK_PRISON[cb.getKingIndex(BLACK)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped >>> i & blackPawns) != 0) {
						score += EvalConstants.ROOK_TRAPPED[(i / 8) - 1];
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
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
		if (cb.getPieces(WHITE, BISHOP) != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.getPieces(WHITE, BISHOP) & cb.getEvalInfo().passedPawnsAndOutposts & whitePawnAttacks;
			while (piece != 0) {
				score += EvalConstants.BISHOP_OUTPOST[Long.numberOfTrailingZeros(piece) >>> 3];
				piece &= piece - 1;
			}

			// prison
			piece = cb.getPieces(WHITE, BISHOP);
			while (piece != 0) {
				if (Long.bitCount((EvalConstants.BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & blackPawns) == 2) {
					score -= EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

			if ((cb.getPieces(WHITE, BISHOP) & Bitboard.WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= EvalConstants.BISHOP_PAWN[Long.bitCount(whitePawns & Bitboard.WHITE_SQUARES)];

				// bonus for attacking center squares
				score += Long.bitCount(cb.getEvalInfo().attacks[WHITE][BISHOP] & Bitboard.E4_D5) / 2 * EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
			}
			if ((cb.getPieces(WHITE, BISHOP) & Bitboard.BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= EvalConstants.BISHOP_PAWN[Long.bitCount(whitePawns & Bitboard.BLACK_SQUARES)];

				// bonus for attacking center squares
				score += Long.bitCount(cb.getEvalInfo().attacks[WHITE][BISHOP] & Bitboard.D4_E5) / 2 * EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
			}

		}

		// BLACK BISHOP
		if (cb.getPieces(BLACK, BISHOP) != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.getPieces(BLACK, BISHOP) & cb.getEvalInfo().passedPawnsAndOutposts & blackPawnAttacks;
			while (piece != 0) {
				score -= EvalConstants.BISHOP_OUTPOST[7 - Long.numberOfTrailingZeros(piece) / 8];
				piece &= piece - 1;
			}

			// prison
			piece = cb.getPieces(BLACK, BISHOP);
			while (piece != 0) {
				if (Long.bitCount((EvalConstants.BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & whitePawns) == 2) {
					score += EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

			if ((cb.getPieces(BLACK, BISHOP) & Bitboard.WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score += EvalConstants.BISHOP_PAWN[Long.bitCount(blackPawns & Bitboard.WHITE_SQUARES)];

				// bonus for attacking center squares
				score -= Long.bitCount(cb.getEvalInfo().attacks[BLACK][BISHOP] & Bitboard.E4_D5) / 2 * EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
			}
			if ((cb.getPieces(BLACK, BISHOP) & Bitboard.BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score += EvalConstants.BISHOP_PAWN[Long.bitCount(blackPawns & Bitboard.BLACK_SQUARES)];

				// bonus for attacking center squares
				score -= Long.bitCount(cb.getEvalInfo().attacks[BLACK][BISHOP] & Bitboard.D4_E5) / 2 * EvalConstants.OTHER_SCORES[EvalConstants.IX_BISHOP_LONG];
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
		piece = cb.getPieces(WHITE, NIGHT) & cb.getEvalInfo().passedPawnsAndOutposts & whitePawnAttacks;
		while (piece != 0) {
			score += EvalConstants.KNIGHT_OUTPOST[Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= piece - 1;
		}
		piece = cb.getPieces(BLACK, NIGHT) & cb.getEvalInfo().passedPawnsAndOutposts & blackPawnAttacks;
		while (piece != 0) {
			score -= EvalConstants.KNIGHT_OUTPOST[7 - Long.numberOfTrailingZeros(piece) / 8];
			piece &= piece - 1;
		}

		// penalty for having pinned-pieces
		if (cb.getPinnedPieces() != 0) {
			piece = cb.getPinnedPieces() & whites & ~blackPawnAttacks;
			while (piece != 0) {
				score -= EvalConstants.PINNED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
			piece = cb.getPinnedPieces() & blacks & ~whitePawnAttacks;
			while (piece != 0) {
				score += EvalConstants.PINNED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}

			piece = cb.getPinnedPieces() & whites & blackPawnAttacks;
			while (piece != 0) {
				score -= EvalConstants.PINNED_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
			piece = cb.getPinnedPieces() & blacks & whitePawnAttacks;
			while (piece != 0) {
				score += EvalConstants.PINNED_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
		}

		// bonus for having discovered-pieces
		if (cb.getDiscoveredPieces() != 0) {
			piece = cb.getDiscoveredPieces() & whites;
			while (piece != 0) {
				score += EvalConstants.DISCOVERED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
			piece = cb.getDiscoveredPieces() & blacks;
			while (piece != 0) {
				score -= EvalConstants.DISCOVERED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
		}

		// quiescence search could leave one side in check
		if (cb.getCheckingPieces() != 0) {
			score += ChessConstants.COLOR_FACTOR[1 - cb.getColorToMove()] * EvalConstants.IN_CHECK;
		}

		return score;
	}

	public static int calculatePawnShieldBonus(final IChessBoard cb) {

		int file;

		int whiteScore = 0;
		long piece = cb.getPieces(WHITE, PAWN) & cb.getKingArea(WHITE) & ~cb.getEvalInfo().attacks[BLACK][PAWN];
		while (piece != 0) {
			file = Long.numberOfTrailingZeros(piece) & 7;
			whiteScore += EvalConstants.SHIELD_BONUS[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= ~Bitboard.FILES[file];
		}
		if (cb.getPieces(BLACK, QUEEN) == 0) {
			whiteScore /= 2;
		}

		int blackScore = 0;
		piece = cb.getPieces(BLACK, PAWN) & cb.getKingArea(BLACK) & ~cb.getEvalInfo().attacks[WHITE][PAWN];
		while (piece != 0) {
			file = (63 - Long.numberOfLeadingZeros(piece)) & 7;
			blackScore += EvalConstants.SHIELD_BONUS[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8];
			piece &= ~Bitboard.FILES[file];
		}
		if (cb.getPieces(WHITE, QUEEN) == 0) {
			blackScore /= 2;
		}

		return whiteScore - blackScore;
	}

	public static int calculateMobilityScoresAndSetAttackBoards(final IChessBoard cb) {

		// clear values
		cb.getEvalInfo().clearEvalAttacks();

		long moves;

		// white pawns
		cb.getEvalInfo().attacks[WHITE][PAWN] = Bitboard.getWhitePawnAttacks(cb.getPieces(WHITE, PAWN) & ~cb.getPinnedPieces());
		if ((cb.getEvalInfo().attacks[WHITE][PAWN] & cb.getKingArea(BLACK)) != 0) {
			cb.getEvalInfo().kingAttackersFlag[WHITE] = SchroderUtil.FLAG_PAWN;
		}
		long pinned = cb.getPieces(WHITE, PAWN) & cb.getPinnedPieces();
		while (pinned != 0) {
			cb.getEvalInfo().attacks[WHITE][PAWN] |= StaticMoves.PAWN_ATTACKS[WHITE][Long.numberOfTrailingZeros(pinned)]
					& ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][cb.getKingIndex(WHITE)];
			pinned &= pinned - 1;
		}
		cb.getEvalInfo().attacksAll[WHITE] = cb.getEvalInfo().attacks[WHITE][PAWN];
		// black pawns
		cb.getEvalInfo().attacks[BLACK][PAWN] = Bitboard.getBlackPawnAttacks(cb.getPieces(BLACK, PAWN) & ~cb.getPinnedPieces());
		if ((cb.getEvalInfo().attacks[BLACK][PAWN] & cb.getKingArea(WHITE)) != 0) {
			cb.getEvalInfo().kingAttackersFlag[BLACK] = SchroderUtil.FLAG_PAWN;
		}
		pinned = cb.getPieces(BLACK, PAWN) & cb.getPinnedPieces();
		while (pinned != 0) {
			cb.getEvalInfo().attacks[BLACK][PAWN] |= StaticMoves.PAWN_ATTACKS[BLACK][Long.numberOfTrailingZeros(pinned)]
					& ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][cb.getKingIndex(BLACK)];
			pinned &= pinned - 1;
		}
		cb.getEvalInfo().attacksAll[BLACK] = cb.getEvalInfo().attacks[BLACK][PAWN];

		int score = 0;
		for (int color = WHITE; color <= BLACK; color++) {

			int tempScore = 0;

			final long kingArea = cb.getKingArea(1 - color);
			final long safeMoves = ~cb.getFriendlyPieces(color) & ~cb.getEvalInfo().attacks[1 - color][PAWN];

			// knights
			long piece = cb.getPieces(color, NIGHT) & ~cb.getPinnedPieces();
			while (piece != 0) {
				moves = StaticMoves.KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
				if ((moves & kingArea) != 0) {
					cb.getEvalInfo().kingAttackersFlag[color] |= SchroderUtil.FLAG_NIGHT;
				}
				cb.getEvalInfo().doubleAttacks[color] |= cb.getEvalInfo().attacksAll[color] & moves;
				cb.getEvalInfo().attacksAll[color] |= moves;
				cb.getEvalInfo().attacks[color][NIGHT] |= moves;
				tempScore += EvalConstants.MOBILITY_KNIGHT[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// bishops
			piece = cb.getPieces(color, BISHOP);
			while (piece != 0) {
				moves = MagicUtil.getBishopMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces() ^ cb.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					cb.getEvalInfo().kingAttackersFlag[color] |= SchroderUtil.FLAG_BISHOP;
				}
				cb.getEvalInfo().doubleAttacks[color] |= cb.getEvalInfo().attacksAll[color] & moves;
				cb.getEvalInfo().attacksAll[color] |= moves;
				cb.getEvalInfo().attacks[color][BISHOP] |= moves;
				tempScore += EvalConstants.MOBILITY_BISHOP[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// rooks
			piece = cb.getPieces(color, ROOK);
			while (piece != 0) {
				moves = MagicUtil.getRookMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces() ^ cb.getPieces(color, ROOK) ^ cb.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					cb.getEvalInfo().kingAttackersFlag[color] |= SchroderUtil.FLAG_ROOK;
				}
				cb.getEvalInfo().doubleAttacks[color] |= cb.getEvalInfo().attacksAll[color] & moves;
				cb.getEvalInfo().attacksAll[color] |= moves;
				cb.getEvalInfo().attacks[color][ROOK] |= moves;
				tempScore += EvalConstants.MOBILITY_ROOK[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			// queens
			piece = cb.getPieces(color, QUEEN);
			while (piece != 0) {
				moves = MagicUtil.getQueenMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces());
				if ((moves & kingArea) != 0) {
					cb.getEvalInfo().kingAttackersFlag[color] |= SchroderUtil.FLAG_QUEEN;
				}
				cb.getEvalInfo().doubleAttacks[color] |= cb.getEvalInfo().attacksAll[color] & moves;
				cb.getEvalInfo().attacksAll[color] |= moves;
				cb.getEvalInfo().attacks[color][QUEEN] |= moves;
				tempScore += EvalConstants.MOBILITY_QUEEN[Long.bitCount(moves & safeMoves)];
				piece &= piece - 1;
			}

			score += tempScore * ChessConstants.COLOR_FACTOR[color];

		}

		// TODO king-attacks with or without enemy attacks?
		// WHITE king
		moves = StaticMoves.KING_MOVES[cb.getKingIndex(WHITE)] & ~StaticMoves.KING_MOVES[cb.getKingIndex(BLACK)];
		cb.getEvalInfo().attacks[WHITE][KING] = moves;
		cb.getEvalInfo().doubleAttacks[WHITE] |= cb.getEvalInfo().attacksAll[WHITE] & moves;
		cb.getEvalInfo().attacksAll[WHITE] |= moves;
		score += EvalConstants.MOBILITY_KING[Long.bitCount(moves & ~cb.getFriendlyPieces(WHITE) & ~cb.getEvalInfo().attacksAll[BLACK])];

		// BLACK king
		moves = StaticMoves.KING_MOVES[cb.getKingIndex(BLACK)] & ~StaticMoves.KING_MOVES[cb.getKingIndex(WHITE)];
		cb.getEvalInfo().attacks[BLACK][KING] = moves;
		cb.getEvalInfo().doubleAttacks[BLACK] |= cb.getEvalInfo().attacksAll[BLACK] & moves;
		cb.getEvalInfo().attacksAll[BLACK] |= moves;
		score -= EvalConstants.MOBILITY_KING[Long.bitCount(moves & ~cb.getFriendlyPieces(BLACK) & ~cb.getEvalInfo().attacksAll[WHITE])];

		return score;
	}

	public static int calculatePositionScores(final IChessBoard cb) {

		int score = 0;
		for (int color = WHITE; color <= BLACK; color++) {
			for (int pieceType = PAWN; pieceType <= KING; pieceType++) {
				long piece = cb.getPieces(color, pieceType);
				while (piece != 0) {
					score += EvalConstants.PSQT[pieceType][color][Long.numberOfTrailingZeros(piece)];
					piece &= piece - 1;
				}
			}
		}
		return score;
	}

	public static int calculateMaterialScore(final IChessBoard cb) {
		return (Long.bitCount(cb.getPieces(WHITE, PAWN)) - Long.bitCount(cb.getPieces(BLACK, PAWN))) * EvalConstants.MATERIAL[PAWN]
				+ (Long.bitCount(cb.getPieces(WHITE, NIGHT)) - Long.bitCount(cb.getPieces(BLACK, NIGHT))) * EvalConstants.MATERIAL[NIGHT]
				+ (Long.bitCount(cb.getPieces(WHITE, BISHOP)) - Long.bitCount(cb.getPieces(BLACK, BISHOP))) * EvalConstants.MATERIAL[BISHOP]
				+ (Long.bitCount(cb.getPieces(WHITE, ROOK)) - Long.bitCount(cb.getPieces(BLACK, ROOK))) * EvalConstants.MATERIAL[ROOK]
				+ (Long.bitCount(cb.getPieces(WHITE, QUEEN)) - Long.bitCount(cb.getPieces(BLACK, QUEEN))) * EvalConstants.MATERIAL[QUEEN];
	}

}

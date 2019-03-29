package bagaturchess.engines.evaladapters.chess22k;


import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.BISHOP;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.BLACK;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.KING;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.NIGHT;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.PAWN;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.QUEEN;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.ROOK;
import static bagaturchess.engines.evaladapters.chess22k.ChessConstants.WHITE;


public class Evaluator extends Evaluator_BaseImpl implements FeatureWeights {
	
	
	//START EvalConstants
	public static final int SIDE_TO_MOVE_BONUS = 16; //cannot be tuned //TODO lower in endgame
	public static final int IN_CHECK = 20;
	
	// other
	public static final int[] OTHER_SCORES = {-8, 12, 18, 8, 18, 12, 150, 12, 56};
	public static final int IX_ROOK_FILE_SEMI_OPEN	 		= 0;
	public static final int IX_ROOK_FILE_SEMI_OPEN_ISOLATED = 1;
	public static final int IX_ROOK_FILE_OPEN 				= 2;
	public static final int IX_ROOK_7TH_RANK 				= 3;
	public static final int IX_ROOK_BATTERY 				= 4;
	public static final int IX_BISHOP_LONG 					= 5;
	public static final int IX_BISHOP_PRISON 				= 6;
	public static final int IX_SPACE 						= 7;
	public static final int IX_DRAWISH 						= 8;
	
	// threats
	public static final int[] THREATS_MG = {38, 68, 100, 16, 56, 144, 66, 52, 8, 16, -6};
	public static final int[] THREATS_EG = {36, 10, -38, 16, 40, 156, 6, -12, 20, 4, 6};
	//public static final int[] THREATS = new int[THREATS_MG.length];
	public static final int IX_MULTIPLE_PAWN_ATTACKS 		= 0;
	public static final int IX_PAWN_ATTACKS 				= 1;
	public static final int IX_QUEEN_ATTACKED 				= 2;
	public static final int IX_PAWN_PUSH_THREAT 			= 3;
	public static final int IX_NIGHT_FORK 					= 4;
	public static final int IX_NIGHT_FORK_KING 				= 5;
	public static final int IX_ROOK_ATTACKED 				= 6;
	public static final int IX_QUEEN_ATTACKED_MINOR			= 7;
	public static final int IX_MAJOR_ATTACKED				= 8;
	public static final int IX_UNUSED_OUTPOST				= 9;
	public static final int IX_PAWN_ATTACKED 				= 10;
	
	// pawn
	public static final int[] PAWN_SCORES = {6, 10, 12, 6};
	public static final int IX_PAWN_DOUBLE 					= 0;
	public static final int IX_PAWN_ISOLATED 				= 1;
	public static final int IX_PAWN_BACKWARD 				= 2;
	public static final int IX_PAWN_INVERSE					= 3;
	
	// imbalance
	public static final int[] IMBALANCE_SCORES = {32, 54, 16};
	public static final int IX_ROOK_PAIR		 			= 0;
	public static final int IX_BISHOP_DOUBLE 				= 1;
	public static final int IX_QUEEN_NIGHT 					= 2;
	
	public static final int[] PHASE 					= {0, 0, 6, 6, 13, 28};
	
	public static final int[] MATERIAL 					= {0, 100, 396, 416, 706, 1302, 3000};
	public static final int[] PINNED 					= {0, -2, 14, 42, 72, 88};
	public static final int[] PINNED_ATTACKED			= {0, 28, 128, 274, 330, 210};
	public static final int[] DISCOVERED		 		= {0, -14, 128, 110, 180, 0, 28};
	public static final int[] KNIGHT_OUTPOST			= {0, 0, 10, 26, 24, 36, 8, 38};
	public static final int[] BISHOP_OUTPOST			= {0, 0, 22, 22, 20, 22, 52, 50};
	public static final int[] DOUBLE_ATTACKED 			= {0, 16, 34, 64, -4, -6, 0};
	public static final int[] HANGING 					= {0, 16, 6, 0, -10, -18, 48}; //qsearch could set the other in check
	public static final int[] HANGING_2 				= {0, 38, 90, 94, 52, -230};
	public static final int[] ROOK_TRAPPED 				= {64, 62, 28};
	public static final int[] ONLY_MAJOR_DEFENDERS 		= {0, 6, 14, 24, 4, 10, 0};
	public static final int[] NIGHT_PAWN				= {68, -14, -2, 2, 8, 12, 20, 30, 36};
	public static final int[] ROOK_PAWN					= {48, -4, -4, -4, -4, 0, 0, 0, 0};
	public static final int[] BISHOP_PAWN 				= {-20, -8, -6, 0, 6, 12, 22, 32, 46};
	public static final int[] SPACE 					= {0, 0, 0, 0, 0, -6, -6, -8, -7, -4, -4, -2, 0, -1, 0, 3, 7};
	
	public static final int[] PAWN_BLOCKAGE 			= {0, 0, -10, 2, 6, 28, 66, 196};
	public static final int[] PAWN_CONNECTED			= {0, 0, 12, 14, 20, 58, 122};
	public static final int[] PAWN_NEIGHBOUR	 		= {0, 0, 4, 10, 26, 88, 326};
	
	public static final int[][] SHIELD_BONUS_MG			= {	{0, 18, 14, 4, -24, -38, -270},
															{0, 52, 36, 6, -44, 114, -250},
															{0, 52, 4, 4, 46, 152, 16},
															{0, 16, 4, 6, -16, 106, 2}};
	public static final int[][] SHIELD_BONUS_EG			= {	{0, -48, -18, -16, 8, -30, -28},
															{0, -16, -26, -10, 42, 6, 20},
															{0, 0, 8, 0, 28, 24, 38},
															{0, -22, -14, 0, 38, 10, 60}};
	//public static final int[][] SHIELD_BONUS 			= new int[4][7];

	public static final int[] PASSED_SCORE_MG			= {0, -4, -2, 0, 18, 22, -6};
	public static final int[] PASSED_SCORE_EG			= {0, 18, 18, 38, 62, 136, 262};
	
	public static final int[] PASSED_CANDIDATE			= {0, 2, 2, 8, 14, 40};
	
	public static final float[] PASSED_KING_MULTI 		= {0, 1.4f, 1.3f, 1.1f, 1.1f, 1.0f, 0.8f, 0.8f};														
	public static final float[] PASSED_MULTIPLIERS	= {
			0.5f,	// blocked
			1.2f,	// next square attacked
			0.4f,	// enemy king in front
			1.2f,	// next square defended
			0.7f,	// attacked
			1.6f,	// defended by rook from behind
			0.6f,	// attacked by rook from behind
			1.7f	// no enemy attacks in front
	};	
	
	//concept borrowed from Ed Schroder
	public static final int[] KS_SCORES = { //TODO negative values? //TODO first values are not used
			0, 0, 0, 0, -140, -150, -120, -90, -40, 40, 70, 
			80, 100, 110, 130, 160, 200, 230, 290, 330, 400, 
			480, 550, 630, 660, 700, 790, 860, 920, 1200 };
	public static final int[] KS_QUEEN_TROPISM 		= {0, 0, 1, 1, 1, 1, 0, 0};	// index 0 and 1 are never evaluated	
	public static final int[] KS_RANK 				= {0, 0, 1, 1, 0, 0, 0, 0};
	public static final int[] KS_CHECK				= {0, 0, 3, 2, 3};
	public static final int[] KS_UCHECK				= {0, 0, 1, 1, 1};
	public static final int[] KS_CHECK_QUEEN 		= {0, 0, 0, 0, 2, 3, 4, 4, 4, 4, 3, 3, 3, 2, 1, 1, 0};
	public static final int[] KS_NO_FRIENDS 		= {6, 4, 0, 5, 5, 5, 6, 6, 7, 8, 9, 9};
	public static final int[] KS_ATTACKS 			= {0, 3, 3, 3, 3, 3, 4, 4, 5, 6, 6, 2, 9};
	public static final int[] KS_DOUBLE_ATTACKS 	= {0, 1, 3, 5, 2, -8, 0, 0, 0};
	public static final int[] KS_ATTACK_PATTERN		= {	
		 //                                                 Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  Q  
		 // 	                    R  R  R  R  R  R  R  R                          R  R  R  R  R  R  R  R  
		 //             B  B  B  B              B  B  B  B              B  B  B  B              B  B  B  B  
		 //       N  N        N  N        N  N        N  N        N  N        N  N        N  N        N  N  
		 //    P     P     P     P     P     P     P     P     P     P     P     P     P     P     P     P
			4, 1, 2, 2, 2, 1, 2, 2, 1, 0, 1, 1, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 3, 3, 1, 2, 3, 3, 3, 3, 4, 4
	};
	
	public static final int[] KS_OTHER	= {		
			3,		// queen-touch check
			4,		// king at blocked first rank check
			1		// open file
	};		
		
	public static final int[] MOBILITY_KNIGHT_MG	= {-34, -16, -6, 0, 12, 16, 26, 28, 56};
	public static final int[] MOBILITY_KNIGHT_EG	= {-98, -34, -12, 0, 4, 12, 12, 14, 4};
	public static final int[] MOBILITY_BISHOP_MG	= {-16, 2, 16, 24, 28, 36, 38, 40, 36, 42, 58, 82, 28, 120};
	public static final int[] MOBILITY_BISHOP_EG	= {-36, -8, 6, 18, 28, 28, 36, 38, 42, 40, 32, 34, 54, 32};
	public static final int[] MOBILITY_ROOK_MG 		= {-34, -24, -18, -14, -12, -4, 0, 8, 16, 26, 30, 40, 52, 68, 66};
	public static final int[] MOBILITY_ROOK_EG 		= {-38, -12, 0, 8, 18, 24, 28, 28, 34, 34, 38, 40, 40, 42, 46};
	public static final int[] MOBILITY_QUEEN_MG		= {-12, -14, -10, -14, -8, -6, -8, -8, -6, -4, -2, -2, -4, 2, 0, 0, 2, 16, 8, 22, 32, 66, 48, 156, 172, 236, 68, 336};
	public static final int[] MOBILITY_QUEEN_EG 	= {-28, -82, -102, -82, -76, -54, -40, -24, -10, -2, 8, 24, 30, 32, 38, 54, 60, 46, 70, 72, 66, 66, 52, 18, -8, -32, 64, -94};
	public static final int[] MOBILITY_KING_MG		= {-10, -12, -8, 0, 10, 26, 36, 70, 122};
	public static final int[] MOBILITY_KING_EG		= {-38, -2, 8, 8, 2, -12, -12, -26, -60};
	/*public static final int[] MOBILITY_KNIGHT		= new int[MOBILITY_KNIGHT_MG.length];
	public static final int[] MOBILITY_BISHOP		= new int[MOBILITY_BISHOP_MG.length];
	public static final int[] MOBILITY_ROOK			= new int[MOBILITY_ROOK_MG.length];
	public static final int[] MOBILITY_QUEEN		= new int[MOBILITY_QUEEN_MG.length];
	public static final int[] MOBILITY_KING			= new int[MOBILITY_KING_MG.length];
	*/
	
	
	public static final long[] ROOK_PRISON = { 
			0, A8, A8_B8, A8B8C8, 0, G8_H8, H8, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, A1, A1_B1, A1B1C1, 0, G1_H1, H1, 0 
	};
	
	public static final long[] BISHOP_PRISON = { 
			0, 0, 0, 0, 0, 0, 0, 0, //8
			B6_C7, 0, 0, 0, 0, 0, 0, G6_F7, //7
			0, 0, 0, 0, 0, 0, 0, 0, //6
			0, 0, 0, 0, 0, 0, 0, 0, //5
			0, 0, 0, 0, 0, 0, 0, 0, //4
			0, 0, 0, 0, 0, 0, 0, 0, //3
			B3_C2, 0, 0, 0, 0, 0, 0, G3_F2, //2
			0, 0, 0, 0, 0, 0, 0, 0  //1
		 // A  B  C  D  E  F  G  H
	};
	
	public static final int[] PROMOTION_SCORE = {
			0,
			0,
			MATERIAL[NIGHT] 	- MATERIAL[PAWN],
			MATERIAL[BISHOP] - MATERIAL[PAWN],
			MATERIAL[ROOK] 	- MATERIAL[PAWN],
			MATERIAL[QUEEN] 	- MATERIAL[PAWN],
	};
	
	
	public static final int[] MIRRORED_LEFT_RIGHT = new int[64];
	static {
		for (int i = 0; i < 64; i++) {
			MIRRORED_LEFT_RIGHT[i] = (i / 8) * 8 + 7 - (i & 7);
		}
	}

	public static final int[] MIRRORED_UP_DOWN = new int[64];
	static {
		for (int i = 0; i < 64; i++) {
			MIRRORED_UP_DOWN[i] = (7 - i / 8) * 8 + (i & 7);
		}
	}
	
	static {
		reverse(ROOK_PRISON);
		reverse(BISHOP_PRISON);
	}
	//END EvalConstants
	
	
	public static final int MG = 0;
	public static final int EG = 1;

	public static final int PHASE_TOTAL =
			
			4 * PHASE[NIGHT]
			+ 4 * PHASE[BISHOP]
			+ 4 * PHASE[ROOK]
			+ 2 * PHASE[QUEEN];
	
	
	private static EvalInfo evalinfo = new EvalInfo();
	
	
	private static EvalInfo getEvalInfo() {
		return evalinfo;
	}
	
	
	public static int getScore1(final IChessBoard cb) {
		
		getEvalInfo().clearEvals();
		
		final int pawnScore = getPawnScores(cb);
		final int materialScore = getImbalances(cb);
		
		final int scoreMg = cb.getPSQTScore()
														+ pawnScore
														+ materialScore;
		
		final int scoreEg = cb.getPSQTScore()
														+ pawnScore
														+ materialScore;
		
		return cb.interpolateScore(scoreMg, scoreEg);
	}

	
	public static int getScore2(final IChessBoard cb) {
		
		// clear values
		getEvalInfo().clearEvalAttacks();
		
		calculateMobilityScoresAndSetAttackBoards(cb);
		calculatePassedPawnScores(cb);
		calculateThreats(cb);
		calculatePawnShieldBonus(cb);
		
		final int othersScore = calculateOthers(cb);
		
		final int scoreMg = calculateKingSafetyScores(cb)
								+ calculateSpace(cb)
								+ othersScore
								+ getEvalInfo().eval_o;
		
		final int scoreEg = othersScore
								+ getEvalInfo().eval_e;
		
		return cb.interpolateScore(scoreMg, scoreEg);
	}
	
	
	public static int calculateSpace(final IChessBoard cb) {

		int score = 0;

		score += OTHER_SCORES[IX_SPACE]
				* Long.bitCount((cb.getPieces(WHITE, PAWN) >>> 8) & (cb.getPieces(WHITE, NIGHT) | cb.getPieces(WHITE, BISHOP)) & RANK_234);
		score -= OTHER_SCORES[IX_SPACE]
				* Long.bitCount((cb.getPieces(BLACK, PAWN) << 8) & (cb.getPieces(BLACK, NIGHT) | cb.getPieces(BLACK, BISHOP)) & RANK_567);

		// idea taken from Laser
		long space = cb.getPieces(WHITE, PAWN) >>> 8;
		space |= space >>> 8 | space >>> 16;
		score += SPACE[Long.bitCount(cb.getFriendlyPieces(WHITE))]
				* Long.bitCount(space & ~cb.getPieces(WHITE, PAWN) & ~getEvalInfo().attacks[BLACK][PAWN] & FILE_CDEF);
		space = cb.getPieces(BLACK, PAWN) << 8;
		space |= space << 8 | space << 16;
		score -= SPACE[Long.bitCount(cb.getFriendlyPieces(BLACK))]
				* Long.bitCount(space & ~cb.getPieces(BLACK, PAWN) & ~getEvalInfo().attacks[WHITE][PAWN] & FILE_CDEF);

		return score;
	}

	public static int getPawnScores(final IChessBoard cb) {
		return calculatePawnScores(cb);
	}

	private static int calculatePawnScores(final IChessBoard cb) {

		int score = 0;

		// penalty for doubled pawns
		for (int i = 0; i < 8; i++) {
			if (Long.bitCount(cb.getPieces(WHITE, PAWN) & FILES[i]) > 1) {
				score -= PAWN_SCORES[IX_PAWN_DOUBLE];
			}
			if (Long.bitCount(cb.getPieces(BLACK, PAWN) & FILES[i]) > 1) {
				score += PAWN_SCORES[IX_PAWN_DOUBLE];
			}
		}

		// bonus for connected pawns
		long pawns = getWhitePawnAttacks(cb.getPieces(WHITE, PAWN)) & cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			score += PAWN_CONNECTED[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = getBlackPawnAttacks(cb.getPieces(BLACK, PAWN)) & cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			score -= PAWN_CONNECTED[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}

		// bonus for neighbour pawns
		pawns = getPawnNeighbours(cb.getPieces(WHITE, PAWN)) & cb.getPieces(WHITE, PAWN);
		while (pawns != 0) {
			score += PAWN_NEIGHBOUR[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = getPawnNeighbours(cb.getPieces(BLACK, PAWN)) & cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			score -= PAWN_NEIGHBOUR[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}

		// set outposts
		getEvalInfo().passedPawnsAndOutposts = 0;
		pawns = getWhitePawnAttacks(cb.getPieces(WHITE, PAWN)) & ~cb.getPieces(WHITE, PAWN) & ~cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			if ((getWhiteAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.getPieces(BLACK, PAWN)) == 0) {
				getEvalInfo().passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}
		pawns = getBlackPawnAttacks(cb.getPieces(BLACK, PAWN)) & ~cb.getPieces(WHITE, PAWN) & ~cb.getPieces(BLACK, PAWN);
		while (pawns != 0) {
			if ((getBlackAdjacentMask(Long.numberOfTrailingZeros(pawns)) & cb.getPieces(WHITE, PAWN)) == 0) {
				getEvalInfo().passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
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
				score -= PAWN_SCORES[IX_PAWN_ISOLATED];
			}

			// backward pawns
			else if ((getBlackAdjacentMask(index + 8) & cb.getPieces(WHITE, PAWN)) == 0) {
				if ((PAWN_ATTACKS[WHITE][index + 8] & cb.getPieces(BLACK, PAWN)) != 0) {
					if ((FILES[index & 7] & cb.getPieces(BLACK, PAWN)) == 0) {
						score -= PAWN_SCORES[IX_PAWN_BACKWARD];
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(PAWN_ATTACKS[WHITE][index] & cb.getPieces(WHITE, PAWN)) == 2) {
				score -= PAWN_SCORES[IX_PAWN_INVERSE];
			}

			// set passed pawns
			if ((getWhitePassedPawnMask(index) & cb.getPieces(BLACK, PAWN)) == 0) {
				getEvalInfo().passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passed pawns (no pawns in front, more friendly pawns behind and adjacent than enemy pawns)
			else if (63 - Long.numberOfLeadingZeros((cb.getPieces(WHITE, PAWN) | cb.getPieces(BLACK, PAWN)) & FILES[index & 7]) == index) {
				if (Long.bitCount(cb.getPieces(WHITE, PAWN) & getBlackPassedPawnMask(index + 8)) >= Long
						.bitCount(cb.getPieces(BLACK, PAWN) & getWhitePassedPawnMask(index))) {
					score += PASSED_CANDIDATE[index / 8];
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
				score += PAWN_SCORES[IX_PAWN_ISOLATED];
			}

			// backward pawns
			else if ((getWhiteAdjacentMask(index - 8) & cb.getPieces(BLACK, PAWN)) == 0) {
				if ((PAWN_ATTACKS[BLACK][index - 8] & cb.getPieces(WHITE, PAWN)) != 0) {
					if ((FILES[index & 7] & cb.getPieces(WHITE, PAWN)) == 0) {
						score += PAWN_SCORES[IX_PAWN_BACKWARD];
					}
				}
			}

			// pawn defending 2 pawns
			if (Long.bitCount(PAWN_ATTACKS[BLACK][index] & cb.getPieces(BLACK, PAWN)) == 2) {
				score += PAWN_SCORES[IX_PAWN_INVERSE];
			}

			// set passed pawns
			if ((getBlackPassedPawnMask(index) & cb.getPieces(WHITE, PAWN)) == 0) {
				getEvalInfo().passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}

			// candidate passers
			else if (Long.numberOfTrailingZeros((cb.getPieces(WHITE, PAWN) | cb.getPieces(BLACK, PAWN)) & FILES[index & 7]) == index) {
				if (Long.bitCount(cb.getPieces(BLACK, PAWN) & getWhitePassedPawnMask(index - 8)) >= Long
						.bitCount(cb.getPieces(WHITE, PAWN) & getBlackPassedPawnMask(index))) {
					score -= PASSED_CANDIDATE[7 - index / 8];
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
		score += Long.bitCount(cb.getPieces(WHITE, NIGHT)) * NIGHT_PAWN[Long.bitCount(cb.getPieces(WHITE, PAWN))];
		score -= Long.bitCount(cb.getPieces(BLACK, NIGHT)) * NIGHT_PAWN[Long.bitCount(cb.getPieces(BLACK, PAWN))];

		// rook bonus if there are no pawns
		score += Long.bitCount(cb.getPieces(WHITE, ROOK)) * ROOK_PAWN[Long.bitCount(cb.getPieces(WHITE, PAWN))];
		score -= Long.bitCount(cb.getPieces(BLACK, ROOK)) * ROOK_PAWN[Long.bitCount(cb.getPieces(BLACK, PAWN))];

		// double bishop bonus
		if (Long.bitCount(cb.getPieces(WHITE, BISHOP)) == 2) {
			score += IMBALANCE_SCORES[IX_BISHOP_DOUBLE];
		}
		if (Long.bitCount(cb.getPieces(BLACK, BISHOP)) == 2) {
			score -= IMBALANCE_SCORES[IX_BISHOP_DOUBLE];
		}

		// queen and nights
		if (cb.getPieces(WHITE, QUEEN) != 0) {
			score += Long.bitCount(cb.getPieces(WHITE, NIGHT)) * IMBALANCE_SCORES[IX_QUEEN_NIGHT];
		}
		if (cb.getPieces(BLACK, QUEEN) != 0) {
			score -= Long.bitCount(cb.getPieces(BLACK, NIGHT)) * IMBALANCE_SCORES[IX_QUEEN_NIGHT];
		}

		// rook pair
		if (Long.bitCount(cb.getPieces(WHITE, ROOK)) > 1) {
			score -= IMBALANCE_SCORES[IX_ROOK_PAIR];
		}
		if (Long.bitCount(cb.getPieces(BLACK, ROOK)) > 1) {
			score += IMBALANCE_SCORES[IX_ROOK_PAIR];
		}
		
		return score;
	}

	public static void calculateThreats(final IChessBoard cb) {
		
		
		final long whitePawns = cb.getPieces(WHITE, PAWN);
		final long blackPawns = cb.getPieces(BLACK, PAWN);
		final long whiteMinorAttacks = getEvalInfo().attacks[WHITE][NIGHT] | getEvalInfo().attacks[WHITE][BISHOP];
		final long blackMinorAttacks = getEvalInfo().attacks[BLACK][NIGHT] | getEvalInfo().attacks[BLACK][BISHOP];
		final long whitePawnAttacks = getEvalInfo().attacks[WHITE][PAWN];
		final long blackPawnAttacks = getEvalInfo().attacks[BLACK][PAWN];
		final long whiteAttacks = getEvalInfo().attacksAll[WHITE];
		final long blackAttacks = getEvalInfo().attacksAll[BLACK];
		final long whites = cb.getFriendlyPieces(WHITE);
		final long blacks = cb.getFriendlyPieces(BLACK);

		
		// double attacked pieces
		long piece = getEvalInfo().doubleAttacks[WHITE] & blacks;
		while (piece != 0) {
			int index = cb.getPieceType(Long.numberOfTrailingZeros(piece));
			int value = DOUBLE_ATTACKED[index];
			getEvalInfo().eval_o += THREAT_DOUBLE_ATTACKED_O * value;
			getEvalInfo().eval_e += THREAT_DOUBLE_ATTACKED_E * value;
			piece &= piece - 1;
		}
		piece = getEvalInfo().doubleAttacks[BLACK] & whites;
		while (piece != 0) {
			int index = cb.getPieceType(Long.numberOfTrailingZeros(piece));
			int value = DOUBLE_ATTACKED[index];
			getEvalInfo().eval_o -= THREAT_DOUBLE_ATTACKED_O * value;
			getEvalInfo().eval_e -= THREAT_DOUBLE_ATTACKED_E * value;
			piece &= piece - 1;
		}
		
		
		// unused outposts
		int count = Long.bitCount(getEvalInfo().passedPawnsAndOutposts & cb.getEmptySpaces() & whiteMinorAttacks & whitePawnAttacks);
		getEvalInfo().eval_o += THREAT_UNUSED_OUTPOST_O * count * THREATS_MG[IX_UNUSED_OUTPOST];
		getEvalInfo().eval_e += THREAT_UNUSED_OUTPOST_E * count * THREATS_EG[IX_UNUSED_OUTPOST];
		
		count = Long.bitCount(getEvalInfo().passedPawnsAndOutposts & cb.getEmptySpaces() & blackMinorAttacks & blackPawnAttacks);
		getEvalInfo().eval_o -= THREAT_UNUSED_OUTPOST_O * count * THREATS_MG[IX_UNUSED_OUTPOST];
		getEvalInfo().eval_e -= THREAT_UNUSED_OUTPOST_E * count * THREATS_EG[IX_UNUSED_OUTPOST];
		
		
		// pawn push threat
		piece = (whitePawns << 8) & cb.getEmptySpaces() & ~blackAttacks;
		count = Long.bitCount(getWhitePawnAttacks(piece) & blacks);
		getEvalInfo().eval_o += THREAT_PAWN_PUSH_O * count * THREATS_MG[IX_PAWN_PUSH_THREAT];
		getEvalInfo().eval_e += THREAT_PAWN_PUSH_E * count * THREATS_EG[IX_PAWN_PUSH_THREAT];
		 
		piece = (blackPawns >>> 8) & cb.getEmptySpaces() & ~whiteAttacks;
		count = Long.bitCount(getBlackPawnAttacks(piece) & whites);
		getEvalInfo().eval_o -= THREAT_PAWN_PUSH_O * count * THREATS_MG[IX_PAWN_PUSH_THREAT];
		getEvalInfo().eval_e -= THREAT_PAWN_PUSH_E * count * THREATS_EG[IX_PAWN_PUSH_THREAT];
		
		
		// piece is attacked by a pawn
		count = Long.bitCount(whitePawnAttacks & blacks & ~blackPawns);
		getEvalInfo().eval_o += THREAT_PAWN_ATTACKS_O * count * THREATS_MG[IX_PAWN_ATTACKS];
		getEvalInfo().eval_e += THREAT_PAWN_ATTACKS_E * count * THREATS_EG[IX_PAWN_ATTACKS];
		
		count = Long.bitCount(blackPawnAttacks & whites & ~whitePawns);
		getEvalInfo().eval_o -= THREAT_PAWN_ATTACKS_O * count * THREATS_MG[IX_PAWN_ATTACKS];
		getEvalInfo().eval_e -= THREAT_PAWN_ATTACKS_E * count * THREATS_EG[IX_PAWN_ATTACKS];
		
		
		// multiple pawn attacks possible
		if (Long.bitCount(whitePawnAttacks & blacks) > 1) {
			getEvalInfo().eval_o += THREAT_MULTIPLE_PAWN_ATTACKS_O * count * THREATS_MG[IX_MULTIPLE_PAWN_ATTACKS];
			getEvalInfo().eval_e += THREAT_MULTIPLE_PAWN_ATTACKS_E * count * THREATS_EG[IX_MULTIPLE_PAWN_ATTACKS];
		}
		if (Long.bitCount(blackPawnAttacks & whites) > 1) {
			getEvalInfo().eval_o -= THREAT_MULTIPLE_PAWN_ATTACKS_O * count * THREATS_MG[IX_MULTIPLE_PAWN_ATTACKS];
			getEvalInfo().eval_e -= THREAT_MULTIPLE_PAWN_ATTACKS_E * count * THREATS_EG[IX_MULTIPLE_PAWN_ATTACKS];
		}
		
		
		// minors under attack and not defended by a pawn
		count = Long.bitCount(whiteAttacks & (cb.getPieces(BLACK, NIGHT) | cb.getPieces(BLACK, BISHOP) & ~blackAttacks));
		getEvalInfo().eval_o += THREAT_MAJOR_ATTACKED_O * count * THREATS_MG[IX_MAJOR_ATTACKED];
		getEvalInfo().eval_e += THREAT_MAJOR_ATTACKED_E * count * THREATS_EG[IX_MAJOR_ATTACKED];
		
		count = Long.bitCount(blackAttacks & (cb.getPieces(WHITE, NIGHT) | cb.getPieces(WHITE, BISHOP) & ~whiteAttacks));
		getEvalInfo().eval_o -= THREAT_MAJOR_ATTACKED_O * count * THREATS_MG[IX_MAJOR_ATTACKED];
		getEvalInfo().eval_e -= THREAT_MAJOR_ATTACKED_E * count * THREATS_EG[IX_MAJOR_ATTACKED];
		
		
		// pawn attacked
		count = Long.bitCount(whiteAttacks & blackPawns);
		getEvalInfo().eval_o += THREAT_PAWN_ATTACKED_O * count * THREATS_MG[IX_PAWN_ATTACKED];
		getEvalInfo().eval_e += THREAT_PAWN_ATTACKED_E * count * THREATS_EG[IX_PAWN_ATTACKED];
		
		count = Long.bitCount(blackAttacks & whitePawns);
		getEvalInfo().eval_o -= THREAT_PAWN_ATTACKED_O * count * THREATS_MG[IX_PAWN_ATTACKED];
		getEvalInfo().eval_e -= THREAT_PAWN_ATTACKED_E * count * THREATS_EG[IX_PAWN_ATTACKED];
		
		
		if (cb.getPieces(BLACK, QUEEN) != 0) {
			// queen under attack by rook
			count = Long.bitCount(getEvalInfo().attacks[WHITE][ROOK] & cb.getPieces(BLACK, QUEEN));
			getEvalInfo().eval_o += THREAT_QUEEN_ATTACKED_ROOK_O * count * THREATS_MG[IX_QUEEN_ATTACKED];
			getEvalInfo().eval_e += THREAT_QUEEN_ATTACKED_ROOK_E * count * THREATS_EG[IX_QUEEN_ATTACKED];
			
			// queen under attack by minors
			count = Long.bitCount(whiteMinorAttacks & cb.getPieces(BLACK, QUEEN));
			getEvalInfo().eval_o += THREAT_QUEEN_ATTACKED_MINOR_O * count * THREATS_MG[IX_QUEEN_ATTACKED_MINOR];
			getEvalInfo().eval_e += THREAT_QUEEN_ATTACKED_MINOR_E * count * THREATS_EG[IX_QUEEN_ATTACKED_MINOR];
		}

		if (cb.getPieces(WHITE, QUEEN) != 0) {
			// queen under attack by rook
			count = Long.bitCount(getEvalInfo().attacks[BLACK][ROOK] & cb.getPieces(WHITE, QUEEN));
			getEvalInfo().eval_o -= THREAT_QUEEN_ATTACKED_ROOK_O * count * THREATS_MG[IX_QUEEN_ATTACKED];
			getEvalInfo().eval_e -= THREAT_QUEEN_ATTACKED_ROOK_E * count * THREATS_EG[IX_QUEEN_ATTACKED];
			
			// queen under attack by minors
			count = Long.bitCount(blackMinorAttacks & cb.getPieces(WHITE, QUEEN));
			getEvalInfo().eval_o -= THREAT_QUEEN_ATTACKED_MINOR_O * count * THREATS_MG[IX_QUEEN_ATTACKED_MINOR];
			getEvalInfo().eval_e -= THREAT_QUEEN_ATTACKED_MINOR_E * count * THREATS_EG[IX_QUEEN_ATTACKED_MINOR];
		}
		
		
		// rook under attack by minors
		count = Long.bitCount(whiteMinorAttacks & cb.getPieces(BLACK, ROOK));
		getEvalInfo().eval_o += THREAT_ROOK_ATTACKED_O * count * THREATS_MG[IX_ROOK_ATTACKED];
		getEvalInfo().eval_e += THREAT_ROOK_ATTACKED_E * count * THREATS_EG[IX_ROOK_ATTACKED];
		
		count = Long.bitCount(blackMinorAttacks & cb.getPieces(WHITE, ROOK));
		getEvalInfo().eval_o -= THREAT_ROOK_ATTACKED_O * count * THREATS_MG[IX_ROOK_ATTACKED];
		getEvalInfo().eval_e -= THREAT_ROOK_ATTACKED_E * count * THREATS_EG[IX_ROOK_ATTACKED];


		// knight fork
		// skip when testing eval values because we break the loop if any fork has been found
		long forked;
		piece = getEvalInfo().attacks[WHITE][NIGHT] & ~blackAttacks & cb.getEmptySpaces();
		while (piece != 0) {
			forked = blacks & ~blackPawns & KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
			if (Long.bitCount(forked) > 1) {
				if ((cb.getPieces(BLACK, KING) & forked) == 0) {
					getEvalInfo().eval_o += THREAT_NIGHT_FORK_O * count * THREATS_MG[IX_NIGHT_FORK];
					getEvalInfo().eval_e += THREAT_NIGHT_FORK_E * count * THREATS_EG[IX_NIGHT_FORK];
				} else {
					getEvalInfo().eval_o += THREAT_NIGHT_FORK_KING_O * count * THREATS_MG[IX_NIGHT_FORK_KING];
					getEvalInfo().eval_e += THREAT_NIGHT_FORK_KING_E * count * THREATS_EG[IX_NIGHT_FORK_KING];
				}
				break;
			}
			piece &= piece - 1;
		}
		piece = getEvalInfo().attacks[BLACK][NIGHT] & ~whiteAttacks & cb.getEmptySpaces();
		while (piece != 0) {
			forked = whites & ~whitePawns & KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
			if (Long.bitCount(forked) > 1) {
				if ((cb.getPieces(WHITE, KING) & forked) == 0) {
					getEvalInfo().eval_o -= THREAT_NIGHT_FORK_O * count * THREATS_MG[IX_NIGHT_FORK];
					getEvalInfo().eval_e -= THREAT_NIGHT_FORK_E * count * THREATS_EG[IX_NIGHT_FORK];
				} else {
					getEvalInfo().eval_o -= THREAT_NIGHT_FORK_KING_O * count * THREATS_MG[IX_NIGHT_FORK_KING];
					getEvalInfo().eval_e -= THREAT_NIGHT_FORK_KING_E * count * THREATS_EG[IX_NIGHT_FORK_KING];
				}
				break;
			}
			piece &= piece - 1;
		}
	}

	public static int calculateOthers(final IChessBoard cb) {
		int score = 0;
		long piece;

		final long whitePawns = cb.getPieces(WHITE, PAWN);
		final long blackPawns = cb.getPieces(BLACK, PAWN);
		final long whitePawnAttacks = getEvalInfo().attacks[WHITE][PAWN];
		final long blackPawnAttacks = getEvalInfo().attacks[BLACK][PAWN];
		final long whiteAttacks = getEvalInfo().attacksAll[WHITE];
		final long blackAttacks = getEvalInfo().attacksAll[BLACK];
		final long whites = cb.getFriendlyPieces(WHITE);
		final long blacks = cb.getFriendlyPieces(BLACK);

		// bonus for side to move
		score += ChessConstants.COLOR_FACTOR[cb.getColorToMove()] * SIDE_TO_MOVE_BONUS;

		// piece attacked and only defended by a rook or queen
		piece = whites & blackAttacks & whiteAttacks & ~(whitePawnAttacks | getEvalInfo().attacks[WHITE][NIGHT] | getEvalInfo().attacks[WHITE][BISHOP]);
		while (piece != 0) {
			score -= ONLY_MAJOR_DEFENDERS[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			piece &= piece - 1;
		}
		piece = blacks & whiteAttacks & blackAttacks & ~(blackPawnAttacks | getEvalInfo().attacks[BLACK][NIGHT] | getEvalInfo().attacks[BLACK][BISHOP]);
		while (piece != 0) {
			score += ONLY_MAJOR_DEFENDERS[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
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

				score += HANGING_2[hangingIndex];
			} else {
				score += HANGING[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
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
				score -= HANGING_2[hangingIndex];
			} else {
				score -= HANGING[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			}
		}

		// WHITE ROOK
		if (cb.getPieces(WHITE, ROOK) != 0) {

			piece = cb.getPieces(WHITE, ROOK);

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					score += OTHER_SCORES[IX_ROOK_BATTERY];
				}
			}

			// rook on 7th, king on 8th
			if (cb.getKingIndex(BLACK) >= 56) {
				score += Long.bitCount(piece & RANK_7) * OTHER_SCORES[IX_ROOK_7TH_RANK];
			}

			// prison
			final long trapped = piece & ROOK_PRISON[cb.getKingIndex(WHITE)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped << i & whitePawns) != 0) {
						score -= ROOK_TRAPPED[(i / 8) - 1];
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				if ((whitePawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((blackPawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score += OTHER_SCORES[IX_ROOK_FILE_OPEN];
					} else if ((blackPawns & blackPawnAttacks & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score += OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
					} else {
						score += OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN];
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
					score -= OTHER_SCORES[IX_ROOK_BATTERY];
				}
			}

			// rook on 2nd, king on 1st
			if (cb.getKingIndex(WHITE) <= 7) {
				score -= Long.bitCount(piece & RANK_2) * OTHER_SCORES[IX_ROOK_7TH_RANK];
			}

			// prison
			final long trapped = piece & ROOK_PRISON[cb.getKingIndex(BLACK)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped >>> i & blackPawns) != 0) {
						score += ROOK_TRAPPED[(i / 8) - 1];
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				// TODO JITWatch unpredictable branch
				if ((blackPawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((whitePawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score -= OTHER_SCORES[IX_ROOK_FILE_OPEN];
					} else if ((whitePawns & whitePawnAttacks & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						score -= OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
					} else {
						score -= OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN];
					}
				}
				piece &= piece - 1;
			}

		}

		// WHITE BISHOP
		if (cb.getPieces(WHITE, BISHOP) != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.getPieces(WHITE, BISHOP) & getEvalInfo().passedPawnsAndOutposts & whitePawnAttacks;
			while (piece != 0) {
				score += BISHOP_OUTPOST[Long.numberOfTrailingZeros(piece) >>> 3];
				piece &= piece - 1;
			}

			// prison
			piece = cb.getPieces(WHITE, BISHOP);
			while (piece != 0) {
				if (Long.bitCount((BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & blackPawns) == 2) {
					score -= OTHER_SCORES[IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

			if ((cb.getPieces(WHITE, BISHOP) & WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= BISHOP_PAWN[Long.bitCount(whitePawns & WHITE_SQUARES)];

				// bonus for attacking center squares
				score += Long.bitCount(getEvalInfo().attacks[WHITE][BISHOP] & E4_D5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
			}
			if ((cb.getPieces(WHITE, BISHOP) & BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score -= BISHOP_PAWN[Long.bitCount(whitePawns & BLACK_SQUARES)];

				// bonus for attacking center squares
				score += Long.bitCount(getEvalInfo().attacks[WHITE][BISHOP] & D4_E5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
			}

		}

		// BLACK BISHOP
		if (cb.getPieces(BLACK, BISHOP) != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = cb.getPieces(BLACK, BISHOP) & getEvalInfo().passedPawnsAndOutposts & blackPawnAttacks;
			while (piece != 0) {
				score -= BISHOP_OUTPOST[7 - Long.numberOfTrailingZeros(piece) / 8];
				piece &= piece - 1;
			}

			// prison
			piece = cb.getPieces(BLACK, BISHOP);
			while (piece != 0) {
				if (Long.bitCount((BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & whitePawns) == 2) {
					score += OTHER_SCORES[IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

			if ((cb.getPieces(BLACK, BISHOP) & WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score += BISHOP_PAWN[Long.bitCount(blackPawns & WHITE_SQUARES)];

				// bonus for attacking center squares
				score -= Long.bitCount(getEvalInfo().attacks[BLACK][BISHOP] & E4_D5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
			}
			if ((cb.getPieces(BLACK, BISHOP) & BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				score += BISHOP_PAWN[Long.bitCount(blackPawns & BLACK_SQUARES)];

				// bonus for attacking center squares
				score -= Long.bitCount(getEvalInfo().attacks[BLACK][BISHOP] & D4_E5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
			}
		}

		// pieces supporting our pawns
		piece = (whitePawns << 8) & whites;
		while (piece != 0) {
			score += PAWN_BLOCKAGE[Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= piece - 1;
		}
		piece = (blackPawns >>> 8) & blacks;
		while (piece != 0) {
			score -= PAWN_BLOCKAGE[7 - Long.numberOfTrailingZeros(piece) / 8];
			piece &= piece - 1;
		}

		// knight outpost: protected by a pawn, cannot be attacked by enemy pawns
		piece = cb.getPieces(WHITE, NIGHT) & getEvalInfo().passedPawnsAndOutposts & whitePawnAttacks;
		while (piece != 0) {
			score += KNIGHT_OUTPOST[Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= piece - 1;
		}
		piece = cb.getPieces(BLACK, NIGHT) & getEvalInfo().passedPawnsAndOutposts & blackPawnAttacks;
		while (piece != 0) {
			score -= KNIGHT_OUTPOST[7 - Long.numberOfTrailingZeros(piece) / 8];
			piece &= piece - 1;
		}

		// penalty for having pinned-pieces
		if (cb.getPinnedPieces() != 0) {
			piece = cb.getPinnedPieces() & whites & ~blackPawnAttacks;
			while (piece != 0) {
				score -= PINNED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
			piece = cb.getPinnedPieces() & blacks & ~whitePawnAttacks;
			while (piece != 0) {
				score += PINNED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}

			piece = cb.getPinnedPieces() & whites & blackPawnAttacks;
			while (piece != 0) {
				score -= PINNED_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
			piece = cb.getPinnedPieces() & blacks & whitePawnAttacks;
			while (piece != 0) {
				score += PINNED_ATTACKED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
		}

		// bonus for having discovered-pieces
		if (cb.getDiscoveredPieces() != 0) {
			piece = cb.getDiscoveredPieces() & whites;
			while (piece != 0) {
				score += DISCOVERED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
			piece = cb.getDiscoveredPieces() & blacks;
			while (piece != 0) {
				score -= DISCOVERED[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				piece &= piece - 1;
			}
		}

		// quiescence search could leave one side in check
		if (cb.getCheckingPieces() != 0) {
			score += ChessConstants.COLOR_FACTOR[1 - cb.getColorToMove()] * IN_CHECK;
		}

		return score;
	}

	public static void calculatePawnShieldBonus(final IChessBoard cb) {

		int file;

		int whiteScore_o = 0;
		int whiteScore_e = 0;
		long piece = cb.getPieces(WHITE, PAWN) & cb.getKingArea(WHITE) & ~getEvalInfo().attacks[BLACK][PAWN];
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
		piece = cb.getPieces(BLACK, PAWN) & cb.getKingArea(BLACK) & ~getEvalInfo().attacks[WHITE][PAWN];
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
		
		getEvalInfo().eval_o += PAWN_SHIELD_O * (whiteScore_o - blackScore_o);	
		getEvalInfo().eval_e += PAWN_SHIELD_E * (whiteScore_e - blackScore_e);
	}
	
	
	public static void calculateMobilityScoresAndSetAttackBoards(final IChessBoard cb) {

		long moves;

		// white pawns
		getEvalInfo().attacks[WHITE][PAWN] = getWhitePawnAttacks(cb.getPieces(WHITE, PAWN) & ~cb.getPinnedPieces());
		if ((getEvalInfo().attacks[WHITE][PAWN] & cb.getKingArea(BLACK)) != 0) {
			getEvalInfo().kingAttackersFlag[WHITE] = ChessConstants.FLAG_PAWN;
		}
		long pinned = cb.getPieces(WHITE, PAWN) & cb.getPinnedPieces();
		while (pinned != 0) {
			getEvalInfo().attacks[WHITE][PAWN] |= PAWN_ATTACKS[WHITE][Long.numberOfTrailingZeros(pinned)]
					& ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][cb.getKingIndex(WHITE)];
			pinned &= pinned - 1;
		}
		getEvalInfo().attacksAll[WHITE] = getEvalInfo().attacks[WHITE][PAWN];
		// black pawns
		getEvalInfo().attacks[BLACK][PAWN] = getBlackPawnAttacks(cb.getPieces(BLACK, PAWN) & ~cb.getPinnedPieces());
		if ((getEvalInfo().attacks[BLACK][PAWN] & cb.getKingArea(WHITE)) != 0) {
			getEvalInfo().kingAttackersFlag[BLACK] = ChessConstants.FLAG_PAWN;
		}
		pinned = cb.getPieces(BLACK, PAWN) & cb.getPinnedPieces();
		while (pinned != 0) {
			getEvalInfo().attacks[BLACK][PAWN] |= PAWN_ATTACKS[BLACK][Long.numberOfTrailingZeros(pinned)]
					& ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][cb.getKingIndex(BLACK)];
			pinned &= pinned - 1;
		}
		getEvalInfo().attacksAll[BLACK] = getEvalInfo().attacks[BLACK][PAWN];

		//int score = 0;
		for (int color = WHITE; color <= BLACK; color++) {

			//int tempScore = 0;

			final long kingArea = cb.getKingArea(1 - color);
			final long safeMoves = ~cb.getFriendlyPieces(color) & ~getEvalInfo().attacks[1 - color][PAWN];

			// knights
			long piece = cb.getPieces(color, NIGHT) & ~cb.getPinnedPieces();
			while (piece != 0) {
				moves = KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
				if ((moves & kingArea) != 0) {
					getEvalInfo().kingAttackersFlag[color] |= ChessConstants.FLAG_NIGHT;
				}
				getEvalInfo().doubleAttacks[color] |= getEvalInfo().attacksAll[color] & moves;
				getEvalInfo().attacksAll[color] |= moves;
				getEvalInfo().attacks[color][NIGHT] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					getEvalInfo().eval_o += MOBILITY_KNIGHT_O * MOBILITY_KNIGHT_MG[index];	
					getEvalInfo().eval_e += MOBILITY_KNIGHT_E * MOBILITY_KNIGHT_EG[index];
				} else {
					getEvalInfo().eval_o -= MOBILITY_KNIGHT_O * MOBILITY_KNIGHT_MG[index];	
					getEvalInfo().eval_e -= MOBILITY_KNIGHT_E * MOBILITY_KNIGHT_EG[index];
				}
				piece &= piece - 1;
			}

			// bishops
			piece = cb.getPieces(color, BISHOP);
			while (piece != 0) {
				moves = getBishopMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces() ^ cb.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					getEvalInfo().kingAttackersFlag[color] |= ChessConstants.FLAG_BISHOP;
				}
				getEvalInfo().doubleAttacks[color] |= getEvalInfo().attacksAll[color] & moves;
				getEvalInfo().attacksAll[color] |= moves;
				getEvalInfo().attacks[color][BISHOP] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					getEvalInfo().eval_o += MOBILITY_BISHOP_O * MOBILITY_BISHOP_MG[index];	
					getEvalInfo().eval_e += MOBILITY_BISHOP_E * MOBILITY_BISHOP_EG[index];
				} else {
					getEvalInfo().eval_o -= MOBILITY_BISHOP_O * MOBILITY_BISHOP_MG[index];	
					getEvalInfo().eval_e -= MOBILITY_BISHOP_E * MOBILITY_BISHOP_EG[index];
				}
				piece &= piece - 1;
			}

			// rooks
			piece = cb.getPieces(color, ROOK);
			while (piece != 0) {
				moves = getRookMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces() ^ cb.getPieces(color, ROOK) ^ cb.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					getEvalInfo().kingAttackersFlag[color] |= ChessConstants.FLAG_ROOK;
				}
				getEvalInfo().doubleAttacks[color] |= getEvalInfo().attacksAll[color] & moves;
				getEvalInfo().attacksAll[color] |= moves;
				getEvalInfo().attacks[color][ROOK] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					getEvalInfo().eval_o += MOBILITY_ROOK_O * MOBILITY_ROOK_MG[index];	
					getEvalInfo().eval_e += MOBILITY_ROOK_E * MOBILITY_ROOK_EG[index];
				} else {
					getEvalInfo().eval_o -= MOBILITY_ROOK_O * MOBILITY_ROOK_MG[index];	
					getEvalInfo().eval_e -= MOBILITY_ROOK_E * MOBILITY_ROOK_EG[index];
				}
				piece &= piece - 1;
			}

			// queens
			piece = cb.getPieces(color, QUEEN);
			while (piece != 0) {
				moves = getQueenMoves(Long.numberOfTrailingZeros(piece), cb.getAllPieces());
				if ((moves & kingArea) != 0) {
					getEvalInfo().kingAttackersFlag[color] |= ChessConstants.FLAG_QUEEN;
				}
				getEvalInfo().doubleAttacks[color] |= getEvalInfo().attacksAll[color] & moves;
				getEvalInfo().attacksAll[color] |= moves;
				getEvalInfo().attacks[color][QUEEN] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					getEvalInfo().eval_o += MOBILITY_QUEEN_O * MOBILITY_QUEEN_MG[index];	
					getEvalInfo().eval_e += MOBILITY_QUEEN_E * MOBILITY_QUEEN_EG[index];
				} else {
					getEvalInfo().eval_o -= MOBILITY_QUEEN_O * MOBILITY_QUEEN_MG[index];	
					getEvalInfo().eval_e -= MOBILITY_QUEEN_E * MOBILITY_QUEEN_EG[index];
				}
				piece &= piece - 1;
			}
		}

		// TODO king-attacks with or without enemy attacks?
		// WHITE king
		moves = KING_MOVES[cb.getKingIndex(WHITE)] & ~KING_MOVES[cb.getKingIndex(BLACK)];
		getEvalInfo().attacks[WHITE][KING] = moves;
		getEvalInfo().doubleAttacks[WHITE] |= getEvalInfo().attacksAll[WHITE] & moves;
		getEvalInfo().attacksAll[WHITE] |= moves;
		int index = Long.bitCount(moves & ~cb.getFriendlyPieces(WHITE) & ~getEvalInfo().attacksAll[BLACK]);
		getEvalInfo().eval_o += MOBILITY_KING_O * MOBILITY_KING_MG[index];	
		getEvalInfo().eval_e += MOBILITY_KING_E * MOBILITY_KING_EG[index];
		
		// BLACK king
		moves = KING_MOVES[cb.getKingIndex(BLACK)] & ~KING_MOVES[cb.getKingIndex(WHITE)];
		getEvalInfo().attacks[BLACK][KING] = moves;
		getEvalInfo().doubleAttacks[BLACK] |= getEvalInfo().attacksAll[BLACK] & moves;
		getEvalInfo().attacksAll[BLACK] |= moves;
		index = Long.bitCount(moves & ~cb.getFriendlyPieces(BLACK) & ~getEvalInfo().attacksAll[WHITE]);
		getEvalInfo().eval_o -= MOBILITY_KING_O * MOBILITY_KING_MG[index];	
		getEvalInfo().eval_e -= MOBILITY_KING_E * MOBILITY_KING_EG[index];
	}
	
	
	public static int calculateMaterialScore(final IChessBoard cb) {
		return (Long.bitCount(cb.getPieces(WHITE, PAWN)) - Long.bitCount(cb.getPieces(BLACK, PAWN))) * MATERIAL[PAWN]
				+ (Long.bitCount(cb.getPieces(WHITE, NIGHT)) - Long.bitCount(cb.getPieces(BLACK, NIGHT))) * MATERIAL[NIGHT]
				+ (Long.bitCount(cb.getPieces(WHITE, BISHOP)) - Long.bitCount(cb.getPieces(BLACK, BISHOP))) * MATERIAL[BISHOP]
				+ (Long.bitCount(cb.getPieces(WHITE, ROOK)) - Long.bitCount(cb.getPieces(BLACK, ROOK))) * MATERIAL[ROOK]
				+ (Long.bitCount(cb.getPieces(WHITE, QUEEN)) - Long.bitCount(cb.getPieces(BLACK, QUEEN))) * MATERIAL[QUEEN];
	}

	
	public static int calculateKingSafetyScores(final IChessBoard cb) {

		int score = 0;

		for (int kingColor = WHITE; kingColor <= BLACK; kingColor++) {
			final int enemyColor = 1 - kingColor;

			if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, ROOK)) == 0) {
				continue;
			}

			int counter = KS_RANK[(7 * kingColor) + ChessConstants.COLOR_FACTOR[kingColor] * cb.getKingIndex(kingColor) / 8];

			counter += KS_NO_FRIENDS[Long.bitCount(cb.getKingArea(kingColor) & ~cb.getFriendlyPieces(kingColor))];
			counter += openFiles(cb, kingColor, cb.getPieces(kingColor, PAWN));

			// king can move?
			if ((getEvalInfo().attacks[kingColor][KING] & ~cb.getFriendlyPieces(kingColor)) == 0) {
				counter++;
			}
			counter += KS_ATTACKS[Long.bitCount(cb.getKingArea(kingColor) & getEvalInfo().attacksAll[enemyColor])];
			counter += checks(cb, kingColor);

			counter += KS_DOUBLE_ATTACKS[Long
					.bitCount(KING_MOVES[cb.getKingIndex(kingColor)] & getEvalInfo().doubleAttacks[enemyColor] & ~getEvalInfo().attacks[kingColor][PAWN])];

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
				if ((getEvalInfo().attacksAll[kingColor] & cb.getPieces(enemyColor, QUEEN)) == 0) {
					counter += KS_QUEEN_TROPISM[getDistance(cb.getKingIndex(kingColor),
							Long.numberOfTrailingZeros(cb.getPieces(enemyColor, QUEEN)))];
				}
			}

			counter += KS_ATTACK_PATTERN[getEvalInfo().kingAttackersFlag[enemyColor]];
			score += ChessConstants.COLOR_FACTOR[enemyColor] * KS_SCORES[Math.min(counter, KS_SCORES.length - 1)];
		}

		return score;
	}

	private static int openFiles(final IChessBoard cb, final int kingColor, final long pawns) {

		if (cb.getPieces(1 - kingColor, QUEEN) == 0) {
			return 0;
		}
		if (Long.bitCount(cb.getPieces(1 - kingColor, ROOK)) < 2) {
			return 0;
		}

		if ((RANK_FIRST[kingColor] & cb.getPieces(kingColor, KING)) != 0) {
			if ((KING_SIDE & cb.getPieces(kingColor, KING)) != 0) {
				if ((FILE_G & pawns) == 0 || (FILE_H & pawns) == 0) {
					return KS_OTHER[2];
				}
			} else if ((QUEEN_SIDE & cb.getPieces(kingColor, KING)) != 0) {
				if ((FILE_A & pawns) == 0 || (FILE_B & pawns) == 0) {
					return KS_OTHER[2];
				}
			}
		}
		return 0;
	}

	private static int checks(final IChessBoard cb, final int kingColor) {
		final int enemyColor = 1 - kingColor;
		final int kingIndex = cb.getKingIndex(kingColor);
		final long possibleSquares = ~cb.getFriendlyPieces(enemyColor)
				& (~KING_MOVES[kingIndex] | KING_MOVES[kingIndex] & getEvalInfo().doubleAttacks[enemyColor] & ~getEvalInfo().doubleAttacks[kingColor]);

		int counter = checkNight(cb, kingColor, KNIGHT_MOVES[kingIndex] & possibleSquares & getEvalInfo().attacks[enemyColor][NIGHT]);

		long moves;
		long queenMoves = 0;
		if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, BISHOP)) != 0) {
			moves = getBishopMoves(kingIndex, cb.getAllPieces() ^ cb.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves = moves;
			counter += checkBishop(cb, kingColor, moves & getEvalInfo().attacks[enemyColor][BISHOP]);
		}
		if ((cb.getPieces(enemyColor, QUEEN) | cb.getPieces(enemyColor, ROOK)) != 0) {
			moves = getRookMoves(kingIndex, cb.getAllPieces() ^ cb.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves |= moves;
			counter += checkRook(cb, kingColor, moves & getEvalInfo().attacks[enemyColor][ROOK]);
		}

		if (Long.bitCount(cb.getPieces(enemyColor, QUEEN)) == 1) {
			counter += safeCheckQueen(cb, kingColor, queenMoves & ~getEvalInfo().attacksAll[kingColor] & getEvalInfo().attacks[enemyColor][QUEEN]);
			counter += safeCheckQueenTouch(cb, kingColor);
		}

		return counter;
	}

	private static int safeCheckQueenTouch(final IChessBoard cb, final int kingColor) {
		if ((getEvalInfo().kingAttackersFlag[1 - kingColor] & ChessConstants.FLAG_QUEEN) == 0) {
			return 0;
		}
		final int enemyColor = 1 - kingColor;
		if ((KING_MOVES[cb.getKingIndex(kingColor)] & ~cb.getFriendlyPieces(enemyColor) & getEvalInfo().attacks[enemyColor][QUEEN] & ~getEvalInfo().doubleAttacks[kingColor]
				& getEvalInfo().doubleAttacks[enemyColor]) != 0) {
			return KS_OTHER[0];
		}
		return 0;
	}

	private static int safeCheckQueen(final IChessBoard cb, final int kingColor, final long safeQueenMoves) {
		if (safeQueenMoves != 0) {
			return KS_CHECK_QUEEN[Long.bitCount(cb.getFriendlyPieces(kingColor))];
		}

		return 0;
	}

	private static int checkRook(final IChessBoard cb, final int kingColor, final long rookMoves) {
		if (rookMoves == 0) {
			return 0;
		}

		int counter = 0;
		if ((rookMoves & ~getEvalInfo().attacksAll[kingColor]) != 0) {
			counter += KS_CHECK[ROOK];

			// last rank?
			if (kingBlockedAtLastRank(kingColor, cb, KING_MOVES[cb.getKingIndex(kingColor)] & cb.getEmptySpaces() & ~getEvalInfo().attacksAll[1 - kingColor])) {
				counter += KS_OTHER[1];
			}

		} else {
			counter += KS_UCHECK[ROOK];
		}

		return counter;
	}

	private static int checkBishop(final IChessBoard cb, final int kingColor, final long bishopMoves) {
		if (bishopMoves != 0) {
			if ((bishopMoves & ~getEvalInfo().attacksAll[kingColor]) != 0) {
				return KS_CHECK[BISHOP];
			} else {
				return KS_UCHECK[BISHOP];
			}
		}
		return 0;
	}

	private static int checkNight(final IChessBoard cb, final int kingColor, final long nightMoves) {
		if (nightMoves != 0) {
			if ((nightMoves & ~getEvalInfo().attacksAll[kingColor]) != 0) {
				return KS_CHECK[NIGHT];
			} else {
				return KS_UCHECK[NIGHT];
			}
		}
		return 0;
	}

	private static boolean kingBlockedAtLastRank(final int kingColor, final IChessBoard cb, final long safeKingMoves) {
		return (RANKS[7 * kingColor] & cb.getPieces(kingColor, KING)) != 0 && (safeKingMoves & RANKS[7 * kingColor]) == safeKingMoves;
	}
	
	public static void calculatePassedPawnScores(final IChessBoard cb) {

		int whitePromotionDistance = SHORT_MAX;
		int blackPromotionDistance = SHORT_MAX;

		// white passed pawns
		long passedPawns = getEvalInfo().passedPawnsAndOutposts & cb.getPieces(WHITE, ChessConstants.PAWN);
		while (passedPawns != 0) {
			final int index = 63 - Long.numberOfLeadingZeros(passedPawns);

			getPassedPawnScore(cb, index, WHITE);

			if (whitePromotionDistance == SHORT_MAX) {
				whitePromotionDistance = getWhitePromotionDistance(cb, index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~FILES[index & 7];
		}

		// black passed pawns
		passedPawns = getEvalInfo().passedPawnsAndOutposts & cb.getPieces(BLACK, ChessConstants.PAWN);
		while (passedPawns != 0) {
			final int index = Long.numberOfTrailingZeros(passedPawns);

			getPassedPawnScore(cb, index, BLACK);

			if (blackPromotionDistance == SHORT_MAX) {
				blackPromotionDistance = getBlackPromotionDistance(cb, index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~FILES[index & 7];
		}
		
		if (whitePromotionDistance < blackPromotionDistance - 1) {
			getEvalInfo().eval_o += PASSED_UNSTOPPABLE_O;
			getEvalInfo().eval_e += PASSED_UNSTOPPABLE_E;
		} else if (whitePromotionDistance > blackPromotionDistance + 1) {
			getEvalInfo().eval_o -= PASSED_UNSTOPPABLE_O;
			getEvalInfo().eval_e -= PASSED_UNSTOPPABLE_E;
		}
	}

	private static void getPassedPawnScore(final IChessBoard cb, final int index, final int color) {

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
		if ((getEvalInfo().attacksAll[enemyColor] & maskNextSquare) == 0) {

			// complete path free of enemy attacks?
			if ((ChessConstants.PINNED_MOVEMENT[nextIndex][index] & getEvalInfo().attacksAll[enemyColor]) == 0) {
				multiplier *= PASSED_MULTIPLIERS[7];
			} else {
				multiplier *= PASSED_MULTIPLIERS[1];
			}
		}

		// is next squared defended?
		if ((getEvalInfo().attacksAll[color] & maskNextSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[3];
		}

		// is enemy king in front?
		if ((ChessConstants.PINNED_MOVEMENT[nextIndex][index] & cb.getPieces(enemyColor, ChessConstants.KING)) != 0) {
			multiplier *= PASSED_MULTIPLIERS[2];
		}

		// under attack?
		if (cb.getColorToMove() != color && (getEvalInfo().attacksAll[enemyColor] & square) != 0) {
			multiplier *= PASSED_MULTIPLIERS[4];
		}

		// defended by rook from behind?
		if ((maskFile & cb.getPieces(color, ROOK)) != 0 && (getEvalInfo().attacks[color][ROOK] & square) != 0 && (getEvalInfo().attacks[color][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[5];
		}

		// attacked by rook from behind?
		else if ((maskFile & cb.getPieces(enemyColor, ROOK)) != 0 && (getEvalInfo().attacks[enemyColor][ROOK] & square) != 0
				&& (getEvalInfo().attacks[enemyColor][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[6];
		}

		// king tropism
		multiplier *= PASSED_KING_MULTI[getDistance(cb.getKingIndex(color), index)];
		multiplier *= PASSED_KING_MULTI[8 - getDistance(cb.getKingIndex(enemyColor), index)];

		final int scoreIndex = (7 * color) + ChessConstants.COLOR_FACTOR[color] * index / 8;
		
		if (color == WHITE) {
			getEvalInfo().eval_o += PASSED_PAWNS_O * PASSED_SCORE_MG[scoreIndex] * multiplier;
			getEvalInfo().eval_e += PASSED_PAWNS_E * PASSED_SCORE_EG[scoreIndex] * multiplier;
		} else {
			getEvalInfo().eval_o -= PASSED_PAWNS_O * PASSED_SCORE_MG[scoreIndex] * multiplier;
			getEvalInfo().eval_e -= PASSED_PAWNS_E * PASSED_SCORE_EG[scoreIndex] * multiplier;	
		}
	}

	private static int getBlackPromotionDistance(final IChessBoard cb, final int index) {
		// check if it cannot be stopped
		int promotionDistance = index >>> 3;
		if (promotionDistance == 1 && cb.getColorToMove() == BLACK) {
			if ((POWER_LOOKUP[index - 8] & (getEvalInfo().attacksAll[WHITE] | cb.getAllPieces())) == 0) {
				if ((POWER_LOOKUP[index] & getEvalInfo().attacksAll[WHITE]) == 0) {
					return 1;
				}
			}
		}
		return SHORT_MAX;
	}

	private static int getWhitePromotionDistance(final IChessBoard cb, final int index) {
		// check if it cannot be stopped
		int promotionDistance = 7 - index / 8;
		if (promotionDistance == 1 && cb.getColorToMove() == WHITE) {
			if ((POWER_LOOKUP[index + 8] & (getEvalInfo().attacksAll[BLACK] | cb.getAllPieces())) == 0) {
				if ((POWER_LOOKUP[index] & getEvalInfo().attacksAll[BLACK]) == 0) {
					return 1;
				}
			}
		}
		return SHORT_MAX;
	}
	
	
	private static class EvalInfo {

		// attack boards
		public final long[][] attacks = new long[2][7];
		public final long[] attacksAll = new long[2];
		public final long[] doubleAttacks = new long[2];
		public final int[] kingAttackersFlag = new int[2];

		public long passedPawnsAndOutposts;
		
		public int eval_o;
		public int eval_e;
		
		public void clearEvalAttacks() {
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
			doubleAttacks[WHITE] = 0;
			doubleAttacks[BLACK] = 0;
		}
		
		
		public void clearEvals() {
			eval_o = 0;
			eval_e = 0;
		}
	}
}

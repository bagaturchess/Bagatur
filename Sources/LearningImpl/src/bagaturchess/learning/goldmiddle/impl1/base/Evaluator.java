package bagaturchess.learning.goldmiddle.impl1.base;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.BISHOP;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.KING;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.NIGHT;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.PAWN;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.QUEEN;
import static bagaturchess.learning.goldmiddle.impl1.base.ChessConstants.ROOK;
import bagaturchess.bitboard.api.IBaseEval;


public class Evaluator extends Evaluator_BaseImpl implements FeatureWeights {
	
	
	//START EvalConstants
	public static final int SIDE_TO_MOVE_BONUS = 16; //cannot be tuned //TODO lower in endgame
	public static final int IN_CHECK = 20;
	public static final int PASSED_UNSTOPPABLE = 350;
	
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
	
	public static final int[] KNIGHT_OUTPOST_MG			= {0,   0,   0,   34,   48,   75,   85,   111};
	public static final int[] KNIGHT_OUTPOST_EG			= {0,   0,   0,   2,   3,   5,   13,   20};
	public static final int[] BISHOP_OUTPOST_MG			= {1, 	1, 	1, 	1, 	1, 	1, 	1, 	1};
	public static final int[] BISHOP_OUTPOST_EG			= {0,   0,   0,   1,   2,   4,   8,   16};
	public static final int[] DOUBLE_ATTACKED_MG		= {0,   7,   35,   54,   36,   58,   0};
	public static final int[] DOUBLE_ATTACKED_EG		= {0,   19,   5,   7,   16,   1,   0};
	public static final int[] HANGING_MG				= {0,   61,   61,   56,   50,   51,   0};
	public static final int[] HANGING_EG				= {0,   21,   21,   20,   32,   72,   0};
	public static final int[] HANGING_2_MG				= {0,   92,   75,   57,   0,   0};
	public static final int[] HANGING_2_EG				= {0,   48,   27,   57,   106,   124};
	public static final int[] ROOK_TRAPPED 				= {64, 62, 28};
	public static final int[] ONLY_MAJOR_DEFENDERS_MG	= {0,   8,   11,   15,   3,   0,   0};
	public static final int[] ONLY_MAJOR_DEFENDERS_EG	= {0,   0,   1,   28,   11,   301,   0};
	public static final int[] BISHOP_PAWN_MG			= {20,   15,   10,   5,   0,   -5,   -10,   -15,   -20};
	public static final int[] BISHOP_PAWN_EG			= {20,   15,   10,   5,   0,   -5,   -10,   -15,   -20};
	public static final int[] NIGHT_PAWN_MG				= {-20,   -15,   -10,   -5,   0,   5,   10,   15,   20};
	public static final int[] NIGHT_PAWN_EG				= {-20,   -15,   -10,   -5,   0,   5,   10,   15,   20};
	public static final int[] ROOK_PAWN_MG				= {20,   15,   10,   5,   0,   -5,   -10,   -15,   -20};
	public static final int[] ROOK_PAWN_EG				= {20,   15,   10,   5,   0,   -5,   -10,   -15,   -20};
	
	public static final int[] SPACE 					= {0, 0, 0, 0, 0, -6, -6, -8, -7, -4, -4, -2, 0, -1, 0, 3, 7};
	
	public static final int[] PAWN_BLOCKAGE_MG 			= {1,   1,   1,   1,   1,   1,   1,   1};
	public static final int[] PAWN_BLOCKAGE_EG 			= {1,   1,   1,   1,   1,   1,   1,   1};
	public static final int[] PAWN_CONNECTED_MG			= {0,   0,   12,   11,   14,   33,   123};
	public static final int[] PAWN_CONNECTED_EG			= {0,   0,   1,   1,   20,   52,   123};
	public static final int[] PAWN_NEIGHBOUR_MG	 		= {0,   0,   0,   6,   39,   110,   187};
	public static final int[] PAWN_NEIGHBOUR_EG	 		= {0,   0,   7,   8,   13,   32,   112};
	
	public static final int[][] SHIELD_BONUS_MG			= {	{0, 18, 14, 4, -24, -38, -270},
															{0, 52, 36, 6, -44, 114, -250},
															{0, 52, 4, 4, 46, 152, 16},
															{0, 16, 4, 6, -16, 106, 2}};
	public static final int[][] SHIELD_BONUS_EG			= {	{0, -48, -18, -16, 8, -30, -28},
															{0, -16, -26, -10, 42, 6, 20},
															{0, 0, 8, 0, 28, 24, 38},
															{0, -22, -14, 0, 38, 10, 60}};

	public static final int[] PASSED_SCORE_MG			= {0,   5,   10,   15,   20,   45,   56};
	public static final int[] PASSED_SCORE_EG			= {0,   10,   15,   22,   35,   75,   211};
	
	public static final int[] PASSED_CANDIDATE_MG		= {0,   3,   5,   7,   10,   14};
	public static final int[] PASSED_CANDIDATE_EG		= {0,   5,   10,   15,   20,   29};
	
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
	
	public static final int[] MOBILITY_KNIGHT_MG	= {-24,   -28,   -11,   -1,   11,   19,   30,   32,   38};
	public static final int[] MOBILITY_KNIGHT_EG	= {-46,   -28,   -19,   -14,   0,   5,   5,   5,   5};
	public static final int[] MOBILITY_BISHOP_MG	= {-29,   0,   0,   2,   17,   26,   29,   36,   37,   43,   47,   56,   72,   80};
	public static final int[] MOBILITY_BISHOP_EG	= {-30,   -27,   0,   0,   0,   0,   2,   3,   4,   5,   6,   10,   17,   37};
	public static final int[] MOBILITY_ROOK_MG 		= {-28,   0,   1,   1,   0,   0,   13,   21,   24,   24,   32,   34,   60,   70,   118};
	public static final int[] MOBILITY_ROOK_EG 		= {-91,   -55,   -34,   0,   0,   0,   1,   2,   3,   13,   15,   16,   17,  18,   20};
	public static final int[] MOBILITY_QUEEN_MG		= {-47,   -26,   -20,   -20,   -15,   -6,   1,   1,   5,   11,   16,   20,   23,   29,   31,   35,   39,   42,   46,   50,   55,   60,   65,   70,   75,  80,   85,   90,};
	public static final int[] MOBILITY_QUEEN_EG 	= {-47,   -26,   -20,   -20,   -15,   -6,   1,   2,   3,   4,   5,   6,   7,   8,   9,   10,   11,   12,   13,   14,   15,   16,   17,   18,   19,   20,   21,   22,};
	public static final int[] MOBILITY_KING_MG		= {-5,   -4,   0,   3,   8,   19,   35,   49,   80};
	public static final int[] MOBILITY_KING_EG		= {-1,   -1,   -1,   -1,   1,   1,   1,   1,   1};
	
	
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
	
	protected final IChessBoard cb;
	private final IBaseEval baseEval;
	protected final EvalInfo evalinfo;
	
	
	public Evaluator(IChessBoard _board) {
		cb = _board;
		baseEval = cb.getBoard().getBaseEvaluation();
		evalinfo = new EvalInfo();
	}
	
	
	public int getScore1() {
		
		evalinfo.clearEvals1();
		evalinfo.fillBB(cb);
		
		calculatePawnScores();
		calculateMaterialScore();
		calculateImbalances();
		
		int psqt_o = (int) (PIECE_SQUARE_TABLE_O * cb.getPSQTScore_o());
		int psqt_e = (int) (PIECE_SQUARE_TABLE_E * cb.getPSQTScore_e());
		
		return cb.getBoard().getMaterialFactor().interpolateByFactor(psqt_o + evalinfo.eval_o_part1, psqt_e + evalinfo.eval_e_part1);
	}
	
	
	public int getScore2() {
		
		// clear values
		evalinfo.clearEvals2();
		evalinfo.clearEvalAttacks();
		
		calculateMobilityScoresAndSetAttackBoards();
		calculatePassedPawnScores();
		calculateThreats();
		calculatePawnShieldBonus();
		calculateOthers();
		calculateKingSafetyScores();
		calculateSpace();
		
		return cb.getBoard().getMaterialFactor().interpolateByFactor(evalinfo.eval_o_part2, evalinfo.eval_e_part2);
	}
	

	private void calculatePawnScores() {
		
		
		// penalty for doubled pawns
		for (int i = 0; i < 8; i++) {
			if (Long.bitCount(evalinfo.bb_w_pawns & FILES[i]) > 1) {
				evalinfo.eval_o_part1 -= PAWN_DOUBLE_O * PAWN_SCORES[IX_PAWN_DOUBLE];
				evalinfo.eval_e_part1 -= PAWN_DOUBLE_E * PAWN_SCORES[IX_PAWN_DOUBLE];
			}
			if (Long.bitCount(evalinfo.bb_b_pawns & FILES[i]) > 1) {
				evalinfo.eval_o_part1 += PAWN_DOUBLE_O * PAWN_SCORES[IX_PAWN_DOUBLE];
				evalinfo.eval_e_part1 += PAWN_DOUBLE_E * PAWN_SCORES[IX_PAWN_DOUBLE];
			}
		}
		
		
		// bonus for connected pawns
		long pawns = getWhitePawnAttacks(evalinfo.bb_w_pawns) & evalinfo.bb_w_pawns;
		while (pawns != 0) {
			evalinfo.eval_o_part1 += PAWN_CONNECTED_O * PAWN_CONNECTED_MG[Long.numberOfTrailingZeros(pawns) / 8];
			evalinfo.eval_e_part1 += PAWN_CONNECTED_E * PAWN_CONNECTED_EG[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = getBlackPawnAttacks(evalinfo.bb_b_pawns) & evalinfo.bb_b_pawns;
		while (pawns != 0) {
			evalinfo.eval_o_part1 -= PAWN_CONNECTED_O * PAWN_CONNECTED_MG[7 - Long.numberOfTrailingZeros(pawns) / 8];
			evalinfo.eval_e_part1 -= PAWN_CONNECTED_E * PAWN_CONNECTED_EG[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		
		
		// bonus for neighbour pawns
		pawns = getPawnNeighbours(evalinfo.bb_w_pawns) & evalinfo.bb_w_pawns;
		while (pawns != 0) {
			evalinfo.eval_o_part1 += PAWN_NEIGHBOUR_O * PAWN_NEIGHBOUR_MG[Long.numberOfTrailingZeros(pawns) / 8];
			evalinfo.eval_e_part1 += PAWN_NEIGHBOUR_E * PAWN_NEIGHBOUR_EG[Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		pawns = getPawnNeighbours(evalinfo.bb_b_pawns) & evalinfo.bb_b_pawns;
		while (pawns != 0) {
			evalinfo.eval_o_part1 -= PAWN_NEIGHBOUR_O * PAWN_NEIGHBOUR_MG[7 - Long.numberOfTrailingZeros(pawns) / 8];
			evalinfo.eval_e_part1 -= PAWN_NEIGHBOUR_E * PAWN_NEIGHBOUR_EG[7 - Long.numberOfTrailingZeros(pawns) / 8];
			pawns &= pawns - 1;
		}
		
		
		// set outposts
		evalinfo.passedPawnsAndOutposts = 0;
		pawns = getWhitePawnAttacks(evalinfo.bb_w_pawns) & ~evalinfo.bb_w_pawns & ~evalinfo.bb_b_pawns;
		while (pawns != 0) {
			if ((getWhiteAdjacentMask(Long.numberOfTrailingZeros(pawns)) & evalinfo.bb_b_pawns) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}
		pawns = getBlackPawnAttacks(evalinfo.bb_b_pawns) & ~evalinfo.bb_w_pawns & ~evalinfo.bb_b_pawns;
		while (pawns != 0) {
			if ((getBlackAdjacentMask(Long.numberOfTrailingZeros(pawns)) & evalinfo.bb_w_pawns) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			pawns &= pawns - 1;
		}
		
		
		int index;
		// white
		pawns = evalinfo.bb_w_pawns;
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);
			
			
			// isolated pawns
			if ((FILES_ADJACENT[index & 7] & evalinfo.bb_w_pawns) == 0) {
				evalinfo.eval_o_part1 -= PAWN_ISOLATED_O * PAWN_SCORES[IX_PAWN_ISOLATED];
				evalinfo.eval_e_part1 -= PAWN_ISOLATED_E * PAWN_SCORES[IX_PAWN_ISOLATED];
			}
			
			
			// backward pawns
			else if ((getBlackAdjacentMask(index + 8) & evalinfo.bb_w_pawns) == 0) {
				if ((PAWN_ATTACKS[WHITE][index + 8] & evalinfo.bb_b_pawns) != 0) {
					if ((FILES[index & 7] & evalinfo.bb_b_pawns) == 0) {
						evalinfo.eval_o_part1 -= PAWN_BACKWARD_O * PAWN_SCORES[IX_PAWN_BACKWARD];
						evalinfo.eval_e_part1 -= PAWN_BACKWARD_E * PAWN_SCORES[IX_PAWN_BACKWARD];
					}
				}
			}
			
			
			// pawn defending 2 pawns
			if (Long.bitCount(PAWN_ATTACKS[WHITE][index] & evalinfo.bb_w_pawns) == 2) {
				evalinfo.eval_o_part1 -= PAWN_INVERSE_O * PAWN_SCORES[IX_PAWN_INVERSE];
				evalinfo.eval_e_part1 -= PAWN_INVERSE_E * PAWN_SCORES[IX_PAWN_INVERSE];
			}
			
			
			// set passed pawns
			if ((getWhitePassedPawnMask(index) & evalinfo.bb_b_pawns) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			
			
			// candidate passed pawns (no pawns in front, more friendly pawns behind and adjacent than enemy pawns)
			else if (63 - Long.numberOfLeadingZeros((evalinfo.bb_w_pawns | evalinfo.bb_b_pawns) & FILES[index & 7]) == index) {
				if (Long.bitCount(evalinfo.bb_w_pawns & getBlackPassedPawnMask(index + 8)) >= Long
						.bitCount(evalinfo.bb_b_pawns & getWhitePassedPawnMask(index))) {
					evalinfo.eval_o_part1 += PAWN_PASSED_CANDIDATE_O * PASSED_CANDIDATE_MG[index / 8];
					evalinfo.eval_e_part1 += PAWN_PASSED_CANDIDATE_E * PASSED_CANDIDATE_EG[index / 8];
				}
			}

			pawns &= pawns - 1;
		}
		
		
		// black
		pawns = evalinfo.bb_b_pawns;
		while (pawns != 0) {
			index = Long.numberOfTrailingZeros(pawns);
			
			
			// isolated pawns
			if ((FILES_ADJACENT[index & 7] & evalinfo.bb_b_pawns) == 0) {
				evalinfo.eval_o_part1 += PAWN_ISOLATED_O * PAWN_SCORES[IX_PAWN_ISOLATED];
				evalinfo.eval_e_part1 += PAWN_ISOLATED_E * PAWN_SCORES[IX_PAWN_ISOLATED];
			}
			
			
			// backward pawns
			else if ((getWhiteAdjacentMask(index - 8) & evalinfo.bb_b_pawns) == 0) {
				if ((PAWN_ATTACKS[BLACK][index - 8] & evalinfo.bb_w_pawns) != 0) {
					if ((FILES[index & 7] & evalinfo.bb_w_pawns) == 0) {
						evalinfo.eval_o_part1 += PAWN_BACKWARD_O * PAWN_SCORES[IX_PAWN_BACKWARD];
						evalinfo.eval_e_part1 += PAWN_BACKWARD_E * PAWN_SCORES[IX_PAWN_BACKWARD];
					}
				}
			}
			
			
			// pawn defending 2 pawns
			if (Long.bitCount(PAWN_ATTACKS[BLACK][index] & evalinfo.bb_b_pawns) == 2) {
				evalinfo.eval_o_part1 += PAWN_INVERSE_O * PAWN_SCORES[IX_PAWN_INVERSE];
				evalinfo.eval_e_part1 += PAWN_INVERSE_E * PAWN_SCORES[IX_PAWN_INVERSE];
			}
			
			
			// set passed pawns
			if ((getBlackPassedPawnMask(index) & evalinfo.bb_w_pawns) == 0) {
				evalinfo.passedPawnsAndOutposts |= Long.lowestOneBit(pawns);
			}
			
			
			// candidate passers
			else if (Long.numberOfTrailingZeros((evalinfo.bb_w_pawns | evalinfo.bb_b_pawns) & FILES[index & 7]) == index) {
				if (Long.bitCount(evalinfo.bb_b_pawns & getWhitePassedPawnMask(index - 8)) >= Long
						.bitCount(evalinfo.bb_w_pawns & getBlackPassedPawnMask(index))) {
					evalinfo.eval_o_part1 -= PAWN_PASSED_CANDIDATE_O * PASSED_CANDIDATE_MG[7 - index / 8];
					evalinfo.eval_e_part1 -= PAWN_PASSED_CANDIDATE_E * PASSED_CANDIDATE_EG[7 - index / 8];
				}
			}
			
			pawns &= pawns - 1;
		}
	}
	
	
	public void calculateMaterialScore() {
		
		int countPawns = Long.bitCount(evalinfo.bb_w_pawns) - Long.bitCount(evalinfo.bb_b_pawns);
		int countKnights = Long.bitCount(evalinfo.bb_w_knights) - Long.bitCount(evalinfo.bb_b_knights);
		int countBishops = Long.bitCount(evalinfo.bb_w_bishops) - Long.bitCount(evalinfo.bb_b_bishops);
		int countRooks = Long.bitCount(evalinfo.bb_w_rooks) - Long.bitCount(evalinfo.bb_b_rooks);
		int countQueens = Long.bitCount(evalinfo.bb_w_queens) - Long.bitCount(evalinfo.bb_b_queens);
		
		int eval_o = (int) (countPawns * cb.getBoard().getBoardConfig().getMaterial_PAWN_O()
				+ countKnights * cb.getBoard().getBoardConfig().getMaterial_KNIGHT_O()
				+ countBishops * cb.getBoard().getBoardConfig().getMaterial_BISHOP_O()
				+ countRooks * cb.getBoard().getBoardConfig().getMaterial_ROOK_O()
				+ countQueens * cb.getBoard().getBoardConfig().getMaterial_QUEEN_O());
		
		int eval_e = (int) (countPawns * cb.getBoard().getBoardConfig().getMaterial_PAWN_E()
				+ countKnights * cb.getBoard().getBoardConfig().getMaterial_KNIGHT_E()
				+ countBishops * cb.getBoard().getBoardConfig().getMaterial_BISHOP_E()
				+ countRooks * cb.getBoard().getBoardConfig().getMaterial_ROOK_E()
				+ countQueens * cb.getBoard().getBoardConfig().getMaterial_QUEEN_E());

		evalinfo.eval_o_part1 += eval_o;
		evalinfo.eval_e_part1 += eval_e;
	}
	

	/*public void calculateMaterialScore() {
		
		
		int w_eval_nopawns_o = baseEval.getWhiteMaterialNonPawns_o();
		int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
		int b_eval_nopawns_o = baseEval.getBlackMaterialNonPawns_o();
		int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
		
		int w_eval_pawns_o = baseEval.getWhiteMaterialPawns_o();
		int w_eval_pawns_e = baseEval.getWhiteMaterialPawns_e();
		int b_eval_pawns_o = baseEval.getBlackMaterialPawns_o();
		int b_eval_pawns_e = baseEval.getBlackMaterialPawns_e();

		evalinfo.eval_o_part1 += (w_eval_nopawns_o - b_eval_nopawns_o) + (w_eval_pawns_o - b_eval_pawns_o);
		evalinfo.eval_e_part1 += (w_eval_nopawns_e - b_eval_nopawns_e) + (w_eval_pawns_e - b_eval_pawns_e);
	}*/
	
	
	private void calculateImbalances() {
		
		
		// knight bonus if there are a lot of pawns
		int value = Long.bitCount(evalinfo.bb_w_knights);
		evalinfo.eval_o_part1 += MATERIAL_IMBALANCE_NIGHT_PAWNS_O * value * NIGHT_PAWN_MG[Long.bitCount(evalinfo.bb_w_pawns)];
		evalinfo.eval_e_part1 += MATERIAL_IMBALANCE_NIGHT_PAWNS_E * value * NIGHT_PAWN_EG[Long.bitCount(evalinfo.bb_w_pawns)];
		
		value = Long.bitCount(evalinfo.bb_b_knights);
		evalinfo.eval_o_part1 -= MATERIAL_IMBALANCE_NIGHT_PAWNS_O * value * NIGHT_PAWN_MG[Long.bitCount(evalinfo.bb_b_pawns)];
		evalinfo.eval_e_part1 -= MATERIAL_IMBALANCE_NIGHT_PAWNS_E * value * NIGHT_PAWN_EG[Long.bitCount(evalinfo.bb_b_pawns)];
		
		
		// rook bonus if there are no pawns
		value = Long.bitCount(evalinfo.bb_w_rooks);
		evalinfo.eval_o_part1 += MATERIAL_IMBALANCE_ROOK_PAWNS_O * value * ROOK_PAWN_MG[Long.bitCount(evalinfo.bb_w_pawns)];
		evalinfo.eval_e_part1 += MATERIAL_IMBALANCE_ROOK_PAWNS_E * value * ROOK_PAWN_EG[Long.bitCount(evalinfo.bb_w_pawns)];
		
		value = Long.bitCount(evalinfo.bb_b_rooks);
		evalinfo.eval_o_part1 -= MATERIAL_IMBALANCE_ROOK_PAWNS_O * value * ROOK_PAWN_MG[Long.bitCount(evalinfo.bb_b_pawns)];
		evalinfo.eval_e_part1 -= MATERIAL_IMBALANCE_ROOK_PAWNS_E * value * ROOK_PAWN_EG[Long.bitCount(evalinfo.bb_b_pawns)];
		
		
		// double bishop bonus
		if (Long.bitCount(evalinfo.bb_w_bishops) == 2) {
			evalinfo.eval_o_part1 += MATERIAL_IMBALANCE_BISHOP_DOUBLE_O * IMBALANCE_SCORES[IX_BISHOP_DOUBLE];
			evalinfo.eval_e_part1 += MATERIAL_IMBALANCE_BISHOP_DOUBLE_E * IMBALANCE_SCORES[IX_BISHOP_DOUBLE];
		}
		if (Long.bitCount(evalinfo.bb_b_bishops) == 2) {
			evalinfo.eval_o_part1 -= MATERIAL_IMBALANCE_BISHOP_DOUBLE_O * IMBALANCE_SCORES[IX_BISHOP_DOUBLE];
			evalinfo.eval_e_part1 -= MATERIAL_IMBALANCE_BISHOP_DOUBLE_E * IMBALANCE_SCORES[IX_BISHOP_DOUBLE];
		}
		
		
		// queen and nights
		if (evalinfo.bb_w_queens != 0) {
			value = Long.bitCount(evalinfo.bb_w_knights) * IMBALANCE_SCORES[IX_QUEEN_NIGHT];
			evalinfo.eval_o_part1 += MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O * value;
			evalinfo.eval_e_part1 += MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E * value;
		}
		if (evalinfo.bb_b_queens != 0) {
			value = Long.bitCount(evalinfo.bb_b_knights) * IMBALANCE_SCORES[IX_QUEEN_NIGHT];
			evalinfo.eval_o_part1 -= MATERIAL_IMBALANCE_QUEEN_KNIGHTS_O * value;
			evalinfo.eval_e_part1 -= MATERIAL_IMBALANCE_QUEEN_KNIGHTS_E * value;
		}
		
		
		// rook pair
		if (Long.bitCount(evalinfo.bb_w_rooks) > 1) {
			evalinfo.eval_o_part1 += MATERIAL_IMBALANCE_ROOK_PAIR_O * IMBALANCE_SCORES[IX_ROOK_PAIR];
			evalinfo.eval_e_part1 += MATERIAL_IMBALANCE_ROOK_PAIR_E * IMBALANCE_SCORES[IX_ROOK_PAIR];
		}
		if (Long.bitCount(evalinfo.bb_b_rooks) > 1) {
			evalinfo.eval_o_part1 -= MATERIAL_IMBALANCE_ROOK_PAIR_O * IMBALANCE_SCORES[IX_ROOK_PAIR];
			evalinfo.eval_e_part1 -= MATERIAL_IMBALANCE_ROOK_PAIR_E * IMBALANCE_SCORES[IX_ROOK_PAIR];
		}
	}
	
	
	public void calculateMobilityScoresAndSetAttackBoards() {

		long moves;

		// white pawns
		evalinfo.attacks[WHITE][PAWN] = getWhitePawnAttacks(evalinfo.bb_w_pawns & ~cb.getPinnedPieces());
		if ((evalinfo.attacks[WHITE][PAWN] & cb.getKingArea(BLACK)) != 0) {
			evalinfo.kingAttackersFlag[WHITE] = ChessConstants.FLAG_PAWN;
		}
		long pinned = evalinfo.bb_w_pawns & cb.getPinnedPieces();
		while (pinned != 0) {
			evalinfo.attacks[WHITE][PAWN] |= PAWN_ATTACKS[WHITE][Long.numberOfTrailingZeros(pinned)]
					& ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][cb.getKingIndex(WHITE)];
			pinned &= pinned - 1;
		}
		evalinfo.attacksAll[WHITE] = evalinfo.attacks[WHITE][PAWN];
		// black pawns
		evalinfo.attacks[BLACK][PAWN] = getBlackPawnAttacks(evalinfo.bb_b_pawns & ~cb.getPinnedPieces());
		if ((evalinfo.attacks[BLACK][PAWN] & cb.getKingArea(WHITE)) != 0) {
			evalinfo.kingAttackersFlag[BLACK] = ChessConstants.FLAG_PAWN;
		}
		pinned = evalinfo.bb_b_pawns & cb.getPinnedPieces();
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
			final long safeMoves = ~evalinfo.getFriendlyPieces(color) & ~evalinfo.attacks[1 - color][PAWN];

			// knights
			long piece = evalinfo.getPieces(color, NIGHT) & ~cb.getPinnedPieces();
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
					evalinfo.eval_o_part2 += MOBILITY_KNIGHT_O * MOBILITY_KNIGHT_MG[index];	
					evalinfo.eval_e_part2 += MOBILITY_KNIGHT_E * MOBILITY_KNIGHT_EG[index];
				} else {
					evalinfo.eval_o_part2 -= MOBILITY_KNIGHT_O * MOBILITY_KNIGHT_MG[index];	
					evalinfo.eval_e_part2 -= MOBILITY_KNIGHT_E * MOBILITY_KNIGHT_EG[index];
				}
				piece &= piece - 1;
			}

			// bishops
			piece = evalinfo.getPieces(color, BISHOP);
			while (piece != 0) {
				moves = getBishopMoves(Long.numberOfTrailingZeros(piece), evalinfo.bb_all ^ evalinfo.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= ChessConstants.FLAG_BISHOP;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][BISHOP] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					evalinfo.eval_o_part2 += MOBILITY_BISHOP_O * MOBILITY_BISHOP_MG[index];	
					evalinfo.eval_e_part2 += MOBILITY_BISHOP_E * MOBILITY_BISHOP_EG[index];
				} else {
					evalinfo.eval_o_part2 -= MOBILITY_BISHOP_O * MOBILITY_BISHOP_MG[index];	
					evalinfo.eval_e_part2 -= MOBILITY_BISHOP_E * MOBILITY_BISHOP_EG[index];
				}
				piece &= piece - 1;
			}

			// rooks
			piece = evalinfo.getPieces(color, ROOK);
			while (piece != 0) {
				moves = getRookMoves(Long.numberOfTrailingZeros(piece), evalinfo.bb_all ^ evalinfo.getPieces(color, ROOK) ^ evalinfo.getPieces(color, QUEEN));
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= ChessConstants.FLAG_ROOK;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][ROOK] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					evalinfo.eval_o_part2 += MOBILITY_ROOK_O * MOBILITY_ROOK_MG[index];	
					evalinfo.eval_e_part2 += MOBILITY_ROOK_E * MOBILITY_ROOK_EG[index];
				} else {
					evalinfo.eval_o_part2 -= MOBILITY_ROOK_O * MOBILITY_ROOK_MG[index];	
					evalinfo.eval_e_part2 -= MOBILITY_ROOK_E * MOBILITY_ROOK_EG[index];
				}
				piece &= piece - 1;
			}

			// queens
			piece = evalinfo.getPieces(color, QUEEN);
			while (piece != 0) {
				moves = getQueenMoves(Long.numberOfTrailingZeros(piece), evalinfo.bb_all);
				if ((moves & kingArea) != 0) {
					evalinfo.kingAttackersFlag[color] |= ChessConstants.FLAG_QUEEN;
				}
				evalinfo.doubleAttacks[color] |= evalinfo.attacksAll[color] & moves;
				evalinfo.attacksAll[color] |= moves;
				evalinfo.attacks[color][QUEEN] |= moves;
				int index = Long.bitCount(moves & safeMoves);
				if (color == WHITE) {
					evalinfo.eval_o_part2 += MOBILITY_QUEEN_O * MOBILITY_QUEEN_MG[index];	
					evalinfo.eval_e_part2 += MOBILITY_QUEEN_E * MOBILITY_QUEEN_EG[index];
				} else {
					evalinfo.eval_o_part2 -= MOBILITY_QUEEN_O * MOBILITY_QUEEN_MG[index];	
					evalinfo.eval_e_part2 -= MOBILITY_QUEEN_E * MOBILITY_QUEEN_EG[index];
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
		int index = Long.bitCount(moves & ~evalinfo.bb_all_w_pieces & ~evalinfo.attacksAll[BLACK]);
		evalinfo.eval_o_part2 += MOBILITY_KING_O * MOBILITY_KING_MG[index];	
		evalinfo.eval_e_part2 += MOBILITY_KING_E * MOBILITY_KING_EG[index];
		
		// BLACK king
		moves = KING_MOVES[cb.getKingIndex(BLACK)] & ~KING_MOVES[cb.getKingIndex(WHITE)];
		evalinfo.attacks[BLACK][KING] = moves;
		evalinfo.doubleAttacks[BLACK] |= evalinfo.attacksAll[BLACK] & moves;
		evalinfo.attacksAll[BLACK] |= moves;
		index = Long.bitCount(moves & ~evalinfo.bb_all_b_pieces & ~evalinfo.attacksAll[WHITE]);
		evalinfo.eval_o_part2 -= MOBILITY_KING_O * MOBILITY_KING_MG[index];	
		evalinfo.eval_e_part2 -= MOBILITY_KING_E * MOBILITY_KING_EG[index];
	}

	
	public void calculatePassedPawnScores() {

		int whitePromotionDistance = SHORT_MAX;
		int blackPromotionDistance = SHORT_MAX;

		// white passed pawns
		long passedPawns = evalinfo.passedPawnsAndOutposts & evalinfo.bb_w_pawns;
		while (passedPawns != 0) {
			final int index = 63 - Long.numberOfLeadingZeros(passedPawns);

			getPassedPawnScore(index, WHITE);

			if (whitePromotionDistance == SHORT_MAX) {
				whitePromotionDistance = getWhitePromotionDistance(index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~FILES[index & 7];
		}

		// black passed pawns
		passedPawns = evalinfo.passedPawnsAndOutposts & evalinfo.bb_b_pawns;
		while (passedPawns != 0) {
			final int index = Long.numberOfTrailingZeros(passedPawns);

			getPassedPawnScore(index, BLACK);

			if (blackPromotionDistance == SHORT_MAX) {
				blackPromotionDistance = getBlackPromotionDistance(index);
			}

			// skip all passed pawns at same file
			passedPawns &= ~FILES[index & 7];
		}
		
		if (whitePromotionDistance < blackPromotionDistance - 1) {
			evalinfo.eval_o_part2 += PAWN_PASSED_UNSTOPPABLE_O * PASSED_UNSTOPPABLE;
			evalinfo.eval_e_part2 += PAWN_PASSED_UNSTOPPABLE_E * PASSED_UNSTOPPABLE;
		} else if (whitePromotionDistance > blackPromotionDistance + 1) {
			evalinfo.eval_o_part2 -= PAWN_PASSED_UNSTOPPABLE_O * PASSED_UNSTOPPABLE;
			evalinfo.eval_e_part2 -= PAWN_PASSED_UNSTOPPABLE_E * PASSED_UNSTOPPABLE;
		}
	}
	
	
	public void calculateThreats() {
		
		
		final long whitePawns = evalinfo.bb_w_pawns;
		final long blackPawns = evalinfo.bb_b_pawns;
		final long whiteMinorAttacks = evalinfo.attacks[WHITE][NIGHT] | evalinfo.attacks[WHITE][BISHOP];
		final long blackMinorAttacks = evalinfo.attacks[BLACK][NIGHT] | evalinfo.attacks[BLACK][BISHOP];
		final long whitePawnAttacks = evalinfo.attacks[WHITE][PAWN];
		final long blackPawnAttacks = evalinfo.attacks[BLACK][PAWN];
		final long whiteAttacks = evalinfo.attacksAll[WHITE];
		final long blackAttacks = evalinfo.attacksAll[BLACK];
		final long whites = evalinfo.bb_all_w_pieces;
		final long blacks = evalinfo.bb_all_b_pieces;

		
		// double attacked pieces
		long piece = evalinfo.doubleAttacks[WHITE] & blacks;
		while (piece != 0) {
			int index = cb.getPieceType(Long.numberOfTrailingZeros(piece));
			evalinfo.eval_o_part2 += THREAT_DOUBLE_ATTACKED_O * DOUBLE_ATTACKED_MG[index];
			evalinfo.eval_e_part2 += THREAT_DOUBLE_ATTACKED_E * DOUBLE_ATTACKED_EG[index];
			piece &= piece - 1;
		}
		piece = evalinfo.doubleAttacks[BLACK] & whites;
		while (piece != 0) {
			int index = cb.getPieceType(Long.numberOfTrailingZeros(piece));
			evalinfo.eval_o_part2 -= THREAT_DOUBLE_ATTACKED_O * DOUBLE_ATTACKED_MG[index];
			evalinfo.eval_e_part2 -= THREAT_DOUBLE_ATTACKED_E * DOUBLE_ATTACKED_EG[index];
			piece &= piece - 1;
		}
		
		
		// unused outposts
		int count = Long.bitCount(evalinfo.passedPawnsAndOutposts & evalinfo.bb_free & whiteMinorAttacks & whitePawnAttacks);
		evalinfo.eval_o_part2 += THREAT_UNUSED_OUTPOST_O * count * THREATS_MG[IX_UNUSED_OUTPOST];
		evalinfo.eval_e_part2 += THREAT_UNUSED_OUTPOST_E * count * THREATS_EG[IX_UNUSED_OUTPOST];
		
		count = Long.bitCount(evalinfo.passedPawnsAndOutposts & evalinfo.bb_free & blackMinorAttacks & blackPawnAttacks);
		evalinfo.eval_o_part2 -= THREAT_UNUSED_OUTPOST_O * count * THREATS_MG[IX_UNUSED_OUTPOST];
		evalinfo.eval_e_part2 -= THREAT_UNUSED_OUTPOST_E * count * THREATS_EG[IX_UNUSED_OUTPOST];
		
		
		// pawn push threat
		piece = (whitePawns << 8) & evalinfo.bb_free & ~blackAttacks;
		count = Long.bitCount(getWhitePawnAttacks(piece) & blacks);
		evalinfo.eval_o_part2 += THREAT_PAWN_PUSH_O * count * THREATS_MG[IX_PAWN_PUSH_THREAT];
		evalinfo.eval_e_part2 += THREAT_PAWN_PUSH_E * count * THREATS_EG[IX_PAWN_PUSH_THREAT];
		 
		piece = (blackPawns >>> 8) & evalinfo.bb_free & ~whiteAttacks;
		count = Long.bitCount(getBlackPawnAttacks(piece) & whites);
		evalinfo.eval_o_part2 -= THREAT_PAWN_PUSH_O * count * THREATS_MG[IX_PAWN_PUSH_THREAT];
		evalinfo.eval_e_part2 -= THREAT_PAWN_PUSH_E * count * THREATS_EG[IX_PAWN_PUSH_THREAT];
		
		
		// piece is attacked by a pawn
		count = Long.bitCount(whitePawnAttacks & blacks & ~blackPawns);
		evalinfo.eval_o_part2 += THREAT_PAWN_ATTACKS_O * count * THREATS_MG[IX_PAWN_ATTACKS];
		evalinfo.eval_e_part2 += THREAT_PAWN_ATTACKS_E * count * THREATS_EG[IX_PAWN_ATTACKS];
		
		count = Long.bitCount(blackPawnAttacks & whites & ~whitePawns);
		evalinfo.eval_o_part2 -= THREAT_PAWN_ATTACKS_O * count * THREATS_MG[IX_PAWN_ATTACKS];
		evalinfo.eval_e_part2 -= THREAT_PAWN_ATTACKS_E * count * THREATS_EG[IX_PAWN_ATTACKS];
		
		
		// multiple pawn attacks possible
		if (Long.bitCount(whitePawnAttacks & blacks) > 1) {
			evalinfo.eval_o_part2 += THREAT_MULTIPLE_PAWN_ATTACKS_O * THREATS_MG[IX_MULTIPLE_PAWN_ATTACKS];
			evalinfo.eval_e_part2 += THREAT_MULTIPLE_PAWN_ATTACKS_E * THREATS_EG[IX_MULTIPLE_PAWN_ATTACKS];
		}
		if (Long.bitCount(blackPawnAttacks & whites) > 1) {
			evalinfo.eval_o_part2 -= THREAT_MULTIPLE_PAWN_ATTACKS_O * THREATS_MG[IX_MULTIPLE_PAWN_ATTACKS];
			evalinfo.eval_e_part2 -= THREAT_MULTIPLE_PAWN_ATTACKS_E * THREATS_EG[IX_MULTIPLE_PAWN_ATTACKS];
		}
		
		
		// minors under attack and not defended by a pawn
		count = Long.bitCount(whiteAttacks & (evalinfo.bb_b_knights | evalinfo.bb_b_bishops & ~blackAttacks));
		evalinfo.eval_o_part2 += THREAT_MAJOR_ATTACKED_O * count * THREATS_MG[IX_MAJOR_ATTACKED];
		evalinfo.eval_e_part2 += THREAT_MAJOR_ATTACKED_E * count * THREATS_EG[IX_MAJOR_ATTACKED];
		
		count = Long.bitCount(blackAttacks & (evalinfo.bb_w_knights | evalinfo.bb_w_bishops & ~whiteAttacks));
		evalinfo.eval_o_part2 -= THREAT_MAJOR_ATTACKED_O * count * THREATS_MG[IX_MAJOR_ATTACKED];
		evalinfo.eval_e_part2 -= THREAT_MAJOR_ATTACKED_E * count * THREATS_EG[IX_MAJOR_ATTACKED];
		
		
		// pawn attacked
		count = Long.bitCount(whiteAttacks & blackPawns);
		evalinfo.eval_o_part2 += THREAT_PAWN_ATTACKED_O * count * THREATS_MG[IX_PAWN_ATTACKED];
		evalinfo.eval_e_part2 += THREAT_PAWN_ATTACKED_E * count * THREATS_EG[IX_PAWN_ATTACKED];
		
		count = Long.bitCount(blackAttacks & whitePawns);
		evalinfo.eval_o_part2 -= THREAT_PAWN_ATTACKED_O * count * THREATS_MG[IX_PAWN_ATTACKED];
		evalinfo.eval_e_part2 -= THREAT_PAWN_ATTACKED_E * count * THREATS_EG[IX_PAWN_ATTACKED];
		
		
		if (evalinfo.bb_b_queens != 0) {
			// queen under attack by rook
			count = Long.bitCount(evalinfo.attacks[WHITE][ROOK] & evalinfo.bb_b_queens);
			evalinfo.eval_o_part2 += THREAT_QUEEN_ATTACKED_ROOK_O * count * THREATS_MG[IX_QUEEN_ATTACKED];
			evalinfo.eval_e_part2 += THREAT_QUEEN_ATTACKED_ROOK_E * count * THREATS_EG[IX_QUEEN_ATTACKED];
			
			// queen under attack by minors
			count = Long.bitCount(whiteMinorAttacks & evalinfo.bb_b_queens);
			evalinfo.eval_o_part2 += THREAT_QUEEN_ATTACKED_MINOR_O * count * THREATS_MG[IX_QUEEN_ATTACKED_MINOR];
			evalinfo.eval_e_part2 += THREAT_QUEEN_ATTACKED_MINOR_E * count * THREATS_EG[IX_QUEEN_ATTACKED_MINOR];
		}

		if (evalinfo.bb_w_queens != 0) {
			// queen under attack by rook
			count = Long.bitCount(evalinfo.attacks[BLACK][ROOK] & evalinfo.bb_w_queens);
			evalinfo.eval_o_part2 -= THREAT_QUEEN_ATTACKED_ROOK_O * count * THREATS_MG[IX_QUEEN_ATTACKED];
			evalinfo.eval_e_part2 -= THREAT_QUEEN_ATTACKED_ROOK_E * count * THREATS_EG[IX_QUEEN_ATTACKED];
			
			// queen under attack by minors
			count = Long.bitCount(blackMinorAttacks & evalinfo.bb_w_queens);
			evalinfo.eval_o_part2 -= THREAT_QUEEN_ATTACKED_MINOR_O * count * THREATS_MG[IX_QUEEN_ATTACKED_MINOR];
			evalinfo.eval_e_part2 -= THREAT_QUEEN_ATTACKED_MINOR_E * count * THREATS_EG[IX_QUEEN_ATTACKED_MINOR];
		}
		
		
		// rook under attack by minors
		count = Long.bitCount(whiteMinorAttacks & evalinfo.bb_b_rooks);
		evalinfo.eval_o_part2 += THREAT_ROOK_ATTACKED_O * count * THREATS_MG[IX_ROOK_ATTACKED];
		evalinfo.eval_e_part2 += THREAT_ROOK_ATTACKED_E * count * THREATS_EG[IX_ROOK_ATTACKED];
		
		count = Long.bitCount(blackMinorAttacks & evalinfo.bb_w_rooks);
		evalinfo.eval_o_part2 -= THREAT_ROOK_ATTACKED_O * count * THREATS_MG[IX_ROOK_ATTACKED];
		evalinfo.eval_e_part2 -= THREAT_ROOK_ATTACKED_E * count * THREATS_EG[IX_ROOK_ATTACKED];


		// knight fork
		// skip when testing eval values because we break the loop if any fork has been found
		long forked;
		piece = evalinfo.attacks[WHITE][NIGHT] & ~blackAttacks & evalinfo.bb_free;
		while (piece != 0) {
			forked = blacks & ~blackPawns & KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
			if (Long.bitCount(forked) > 1) {
				if ((evalinfo.bb_b_king & forked) == 0) {
					evalinfo.eval_o_part2 += THREAT_NIGHT_FORK_O * THREATS_MG[IX_NIGHT_FORK];
					evalinfo.eval_e_part2 += THREAT_NIGHT_FORK_E * THREATS_EG[IX_NIGHT_FORK];
				} else {
					evalinfo.eval_o_part2 += THREAT_NIGHT_FORK_KING_O * THREATS_MG[IX_NIGHT_FORK_KING];
					evalinfo.eval_e_part2 += THREAT_NIGHT_FORK_KING_E * THREATS_EG[IX_NIGHT_FORK_KING];
				}
				break;
			}
			piece &= piece - 1;
		}
		piece = evalinfo.attacks[BLACK][NIGHT] & ~whiteAttacks & evalinfo.bb_free;
		while (piece != 0) {
			forked = whites & ~whitePawns & KNIGHT_MOVES[Long.numberOfTrailingZeros(piece)];
			if (Long.bitCount(forked) > 1) {
				if ((evalinfo.bb_w_king & forked) == 0) {
					evalinfo.eval_o_part2 -= THREAT_NIGHT_FORK_O * THREATS_MG[IX_NIGHT_FORK];
					evalinfo.eval_e_part2 -= THREAT_NIGHT_FORK_E * THREATS_EG[IX_NIGHT_FORK];
				} else {
					evalinfo.eval_o_part2 -= THREAT_NIGHT_FORK_KING_O * THREATS_MG[IX_NIGHT_FORK_KING];
					evalinfo.eval_e_part2 -= THREAT_NIGHT_FORK_KING_E * THREATS_EG[IX_NIGHT_FORK_KING];
				}
				break;
			}
			piece &= piece - 1;
		}
	}
	
	
	public void calculatePawnShieldBonus() {

		int file;

		int whiteScore_o = 0;
		int whiteScore_e = 0;
		long piece = evalinfo.bb_w_pawns & cb.getKingArea(WHITE) & ~evalinfo.attacks[BLACK][PAWN];
		while (piece != 0) {
			file = Long.numberOfTrailingZeros(piece) & 7;
			whiteScore_o += SHIELD_BONUS_MG[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3];
			whiteScore_e += SHIELD_BONUS_EG[Math.min(7 - file, file)][Long.numberOfTrailingZeros(piece) >>> 3];
			piece &= ~FILES[file];
		}
		if (evalinfo.bb_b_queens == 0) {
			whiteScore_o /= 2;
			whiteScore_e /= 2;
		}

		int blackScore_o = 0;
		int blackScore_e = 0;
		piece = evalinfo.bb_b_pawns & cb.getKingArea(BLACK) & ~evalinfo.attacks[WHITE][PAWN];
		while (piece != 0) {
			file = (63 - Long.numberOfLeadingZeros(piece)) & 7;
			blackScore_o += SHIELD_BONUS_MG[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8];
			blackScore_e += SHIELD_BONUS_EG[Math.min(7 - file, file)][7 - (63 - Long.numberOfLeadingZeros(piece)) / 8];
			piece &= ~FILES[file];
		}
		if (evalinfo.bb_w_queens == 0) {
			whiteScore_o /= 2;
			whiteScore_e /= 2;
		}
		
		evalinfo.eval_o_part2 += PAWN_SHIELD_O * (whiteScore_o - blackScore_o);	
		evalinfo.eval_e_part2 += PAWN_SHIELD_E * (whiteScore_e - blackScore_e);
	}
	
	
	public void calculateOthers() {
		
		
		long piece;

		final long whitePawns = evalinfo.bb_w_pawns;
		final long blackPawns = evalinfo.bb_b_pawns;
		final long whitePawnAttacks = evalinfo.attacks[WHITE][PAWN];
		final long blackPawnAttacks = evalinfo.attacks[BLACK][PAWN];
		final long whiteAttacks = evalinfo.attacksAll[WHITE];
		final long blackAttacks = evalinfo.attacksAll[BLACK];
		final long whites = evalinfo.bb_all_w_pieces;
		final long blacks = evalinfo.bb_all_b_pieces;
		
		
		// bonus for side to move
		if (cb.getColorToMove() == WHITE) {
			evalinfo.eval_o_part2 += OTHERS_SIDE_TO_MOVE_O * SIDE_TO_MOVE_BONUS;
			evalinfo.eval_e_part2 += OTHERS_SIDE_TO_MOVE_E * SIDE_TO_MOVE_BONUS;
		} else {
			evalinfo.eval_o_part2 -= OTHERS_SIDE_TO_MOVE_O * SIDE_TO_MOVE_BONUS;
			evalinfo.eval_e_part2 -= OTHERS_SIDE_TO_MOVE_E * SIDE_TO_MOVE_BONUS;
		}
		
		
		int value;
		
		
		// piece attacked and only defended by a rook or queen
		piece = whites & blackAttacks & whiteAttacks & ~(whitePawnAttacks | evalinfo.attacks[WHITE][NIGHT] | evalinfo.attacks[WHITE][BISHOP]);
		while (piece != 0) {
			evalinfo.eval_o_part2 -= OTHERS_ONLY_MAJOR_DEFENDERS_O * ONLY_MAJOR_DEFENDERS_MG[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			evalinfo.eval_e_part2 -= OTHERS_ONLY_MAJOR_DEFENDERS_E * ONLY_MAJOR_DEFENDERS_EG[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			
			piece &= piece - 1;
		}
		piece = blacks & whiteAttacks & blackAttacks & ~(blackPawnAttacks | evalinfo.attacks[BLACK][NIGHT] | evalinfo.attacks[BLACK][BISHOP]);
		while (piece != 0) {
			evalinfo.eval_o_part2 += OTHERS_ONLY_MAJOR_DEFENDERS_O * ONLY_MAJOR_DEFENDERS_MG[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			evalinfo.eval_e_part2 += OTHERS_ONLY_MAJOR_DEFENDERS_E * ONLY_MAJOR_DEFENDERS_EG[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			
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

				evalinfo.eval_o_part2 += OTHERS_HANGING_2_O * HANGING_2_MG[hangingIndex];
				evalinfo.eval_e_part2 += OTHERS_HANGING_2_E * HANGING_2_EG[hangingIndex];
				
			} else {
				evalinfo.eval_o_part2 += OTHERS_HANGING_O * HANGING_MG[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				evalinfo.eval_e_part2 += OTHERS_HANGING_E * HANGING_EG[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
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

				evalinfo.eval_o_part2 -= OTHERS_HANGING_2_O * HANGING_2_MG[hangingIndex];
				evalinfo.eval_e_part2 -= OTHERS_HANGING_2_E * HANGING_2_EG[hangingIndex];
			} else {
				evalinfo.eval_o_part2 -= OTHERS_HANGING_O * HANGING_MG[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
				evalinfo.eval_e_part2 -= OTHERS_HANGING_E * HANGING_EG[cb.getPieceType(Long.numberOfTrailingZeros(piece))];
			}
		}
		
		
		// WHITE ROOK
		if (evalinfo.bb_w_rooks != 0) {

			piece = evalinfo.bb_w_rooks;

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					evalinfo.eval_o_part2 += OTHERS_ROOK_BATTERY_O * OTHER_SCORES[IX_ROOK_BATTERY];
					evalinfo.eval_e_part2 += OTHERS_ROOK_BATTERY_E * OTHER_SCORES[IX_ROOK_BATTERY];
				}
			}

			// rook on 7th, king on 8th
			if (cb.getKingIndex(BLACK) >= 56) {
				value = Long.bitCount(piece & RANK_7) * OTHER_SCORES[IX_ROOK_7TH_RANK];
				evalinfo.eval_o_part2 += OTHERS_ROOK_7TH_RANK_O * value;
				evalinfo.eval_e_part2 += OTHERS_ROOK_7TH_RANK_E * value;
			}

			// prison
			final long trapped = piece & ROOK_PRISON[cb.getKingIndex(WHITE)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped << i & whitePawns) != 0) {
						evalinfo.eval_o_part2 -= OTHERS_ROOK_TRAPPED_O * ROOK_TRAPPED[(i / 8) - 1];
						evalinfo.eval_e_part2 -= OTHERS_ROOK_TRAPPED_E * ROOK_TRAPPED[(i / 8) - 1];
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				if ((whitePawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((blackPawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						
						evalinfo.eval_o_part2 += OTHERS_ROOK_FILE_OPEN_O * OTHER_SCORES[IX_ROOK_FILE_OPEN];
						evalinfo.eval_e_part2 += OTHERS_ROOK_FILE_OPEN_E * OTHER_SCORES[IX_ROOK_FILE_OPEN];
						
					} else if ((blackPawns & blackPawnAttacks & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						
						evalinfo.eval_o_part2 += OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O * OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
						evalinfo.eval_e_part2 += OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E * OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
						
					} else {
						
						evalinfo.eval_o_part2 += OTHERS_ROOK_FILE_SEMI_OPEN_O * OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN];
						evalinfo.eval_e_part2 += OTHERS_ROOK_FILE_SEMI_OPEN_E * OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN];
					}
				}

				piece &= piece - 1;
			}
		}
		
		
		// BLACK ROOK
		if (evalinfo.bb_b_rooks != 0) {

			piece = evalinfo.bb_b_rooks;

			// rook battery (same file)
			if (Long.bitCount(piece) == 2) {
				if ((Long.numberOfTrailingZeros(piece) & 7) == (63 - Long.numberOfLeadingZeros(piece) & 7)) {
					evalinfo.eval_o_part2 -= OTHERS_ROOK_BATTERY_O * OTHER_SCORES[IX_ROOK_BATTERY];
					evalinfo.eval_e_part2 -= OTHERS_ROOK_BATTERY_E * OTHER_SCORES[IX_ROOK_BATTERY];
				}
			}

			// rook on 2nd, king on 1st
			if (cb.getKingIndex(WHITE) <= 7) {
				value = Long.bitCount(piece & RANK_2) * OTHER_SCORES[IX_ROOK_7TH_RANK];
				evalinfo.eval_o_part2 -= OTHERS_ROOK_7TH_RANK_O * value;
				evalinfo.eval_e_part2 -= OTHERS_ROOK_7TH_RANK_E * value;
			}

			// prison
			final long trapped = piece & ROOK_PRISON[cb.getKingIndex(BLACK)];
			if (trapped != 0) {
				for (int i = 8; i <= 24; i += 8) {
					if ((trapped >>> i & blackPawns) != 0) {
						evalinfo.eval_o_part2 += OTHERS_ROOK_TRAPPED_O * ROOK_TRAPPED[(i / 8) - 1];
						evalinfo.eval_e_part2 += OTHERS_ROOK_TRAPPED_E * ROOK_TRAPPED[(i / 8) - 1];
						break;
					}
				}
			}

			// bonus for rook on open-file (no pawns) and semi-open-file (no friendly pawns)
			while (piece != 0) {
				// TODO JITWatch unpredictable branch
				if ((blackPawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
					if ((whitePawns & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						
						evalinfo.eval_o_part2 -= OTHERS_ROOK_FILE_OPEN_O * OTHER_SCORES[IX_ROOK_FILE_OPEN];
						evalinfo.eval_e_part2 -= OTHERS_ROOK_FILE_OPEN_E * OTHER_SCORES[IX_ROOK_FILE_OPEN];
						
					} else if ((whitePawns & whitePawnAttacks & FILES[Long.numberOfTrailingZeros(piece) & 7]) == 0) {
						
						evalinfo.eval_o_part2 -= OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_O * OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
						evalinfo.eval_e_part2 -= OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED_E * OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN_ISOLATED];
						
					} else {
						
						evalinfo.eval_o_part2 -= OTHERS_ROOK_FILE_SEMI_OPEN_O * OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN];
						evalinfo.eval_e_part2 -= OTHERS_ROOK_FILE_SEMI_OPEN_E * OTHER_SCORES[IX_ROOK_FILE_SEMI_OPEN];
					}
				}
				piece &= piece - 1;
			}

		}
		
		
		// WHITE BISHOP
		if (evalinfo.bb_w_bishops != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = evalinfo.bb_w_bishops & evalinfo.passedPawnsAndOutposts & whitePawnAttacks;
			while (piece != 0) {
				evalinfo.eval_o_part2 += OTHERS_BISHOP_OUTPOST_O * BISHOP_OUTPOST_MG[Long.numberOfTrailingZeros(piece) >>> 3];
				evalinfo.eval_e_part2 += OTHERS_BISHOP_OUTPOST_E * BISHOP_OUTPOST_EG[Long.numberOfTrailingZeros(piece) >>> 3];
				
				piece &= piece - 1;
			}

			// prison
			piece = evalinfo.bb_w_bishops;
			while (piece != 0) {
				if (Long.bitCount((BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & blackPawns) == 2) {
					evalinfo.eval_o_part2 -= OTHERS_BISHOP_PRISON_O * OTHER_SCORES[IX_BISHOP_PRISON];
					evalinfo.eval_e_part2 -= OTHERS_BISHOP_PRISON_E * OTHER_SCORES[IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

			if ((evalinfo.bb_w_bishops & WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				evalinfo.eval_o_part2 -= OTHERS_BISHOP_PAWNS_O * BISHOP_PAWN_MG[Long.bitCount(whitePawns & WHITE_SQUARES)];
				evalinfo.eval_e_part2 -= OTHERS_BISHOP_PAWNS_E * BISHOP_PAWN_EG[Long.bitCount(whitePawns & WHITE_SQUARES)];
				
				// bonus for attacking center squares
				value = Long.bitCount(evalinfo.attacks[WHITE][BISHOP] & E4_D5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
				evalinfo.eval_o_part2 += OTHERS_BISHOP_CENTER_ATTACK_O * value;
				evalinfo.eval_e_part2 += OTHERS_BISHOP_CENTER_ATTACK_E * value;
			}
			if ((evalinfo.bb_w_bishops & BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				evalinfo.eval_o_part2 -= OTHERS_BISHOP_PAWNS_O * BISHOP_PAWN_MG[Long.bitCount(whitePawns & BLACK_SQUARES)];
				evalinfo.eval_e_part2 -= OTHERS_BISHOP_PAWNS_E * BISHOP_PAWN_EG[Long.bitCount(whitePawns & BLACK_SQUARES)];
				
				// bonus for attacking center squares 
				value = Long.bitCount(evalinfo.attacks[WHITE][BISHOP] & D4_E5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
				evalinfo.eval_o_part2 += OTHERS_BISHOP_CENTER_ATTACK_O * value;
				evalinfo.eval_e_part2 += OTHERS_BISHOP_CENTER_ATTACK_E * value;
			}

		}
		
		
		// BLACK BISHOP
		if (evalinfo.bb_b_bishops != 0) {

			// bishop outpost: protected by a pawn, cannot be attacked by enemy pawns
			piece = evalinfo.bb_b_bishops & evalinfo.passedPawnsAndOutposts & blackPawnAttacks;
			while (piece != 0) { 
				evalinfo.eval_o_part2 -= OTHERS_BISHOP_OUTPOST_O * BISHOP_OUTPOST_MG[7 - Long.numberOfTrailingZeros(piece) / 8];
				evalinfo.eval_e_part2 -= OTHERS_BISHOP_OUTPOST_E * BISHOP_OUTPOST_EG[7 - Long.numberOfTrailingZeros(piece) / 8];
				
				piece &= piece - 1;
			}

			// prison
			piece = evalinfo.bb_b_bishops;
			while (piece != 0) {
				if (Long.bitCount((BISHOP_PRISON[Long.numberOfTrailingZeros(piece)]) & whitePawns) == 2) {
					evalinfo.eval_o_part2 += OTHERS_BISHOP_PRISON_O * OTHER_SCORES[IX_BISHOP_PRISON];
					evalinfo.eval_e_part2 += OTHERS_BISHOP_PRISON_E * OTHER_SCORES[IX_BISHOP_PRISON];
				}
				piece &= piece - 1;
			}

			if ((evalinfo.bb_b_bishops & WHITE_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				evalinfo.eval_o_part2 += OTHERS_BISHOP_PAWNS_O * BISHOP_PAWN_MG[Long.bitCount(blackPawns & WHITE_SQUARES)];
				evalinfo.eval_e_part2 += OTHERS_BISHOP_PAWNS_E * BISHOP_PAWN_EG[Long.bitCount(blackPawns & WHITE_SQUARES)];
				
				// bonus for attacking center squares 
				value = Long.bitCount(evalinfo.attacks[BLACK][BISHOP] & E4_D5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
				evalinfo.eval_o_part2 -= OTHERS_BISHOP_CENTER_ATTACK_O * value;
				evalinfo.eval_e_part2 -= OTHERS_BISHOP_CENTER_ATTACK_E * value;
			}
			if ((evalinfo.bb_b_bishops & BLACK_SQUARES) != 0) {
				// penalty for many pawns on same color as bishop
				evalinfo.eval_o_part2 += OTHERS_BISHOP_PAWNS_O * BISHOP_PAWN_MG[Long.bitCount(blackPawns & BLACK_SQUARES)];
				evalinfo.eval_e_part2 += OTHERS_BISHOP_PAWNS_E * BISHOP_PAWN_EG[Long.bitCount(blackPawns & BLACK_SQUARES)];
				
				// bonus for attacking center squares
				value = Long.bitCount(evalinfo.attacks[BLACK][BISHOP] & D4_E5) / 2 * OTHER_SCORES[IX_BISHOP_LONG];
				evalinfo.eval_o_part2 -= OTHERS_BISHOP_CENTER_ATTACK_O * value;
				evalinfo.eval_e_part2 -= OTHERS_BISHOP_CENTER_ATTACK_E * value;
			}
		}
		
		
		// pieces supporting our pawns
		piece = (whitePawns << 8) & whites;
		while (piece != 0) {
			evalinfo.eval_o_part2 += OTHERS_PAWN_BLOCKAGE_O * PAWN_BLOCKAGE_MG[Long.numberOfTrailingZeros(piece) >>> 3];
			evalinfo.eval_e_part2 += OTHERS_PAWN_BLOCKAGE_E * PAWN_BLOCKAGE_EG[Long.numberOfTrailingZeros(piece) >>> 3];
			
			piece &= piece - 1;
		}
		piece = (blackPawns >>> 8) & blacks;
		while (piece != 0) {
			evalinfo.eval_o_part2 -= OTHERS_PAWN_BLOCKAGE_O * PAWN_BLOCKAGE_MG[7 - Long.numberOfTrailingZeros(piece) / 8];
			evalinfo.eval_e_part2 -= OTHERS_PAWN_BLOCKAGE_E * PAWN_BLOCKAGE_EG[7 - Long.numberOfTrailingZeros(piece) / 8];
			
			piece &= piece - 1;
		}
		
		
		// knight outpost: protected by a pawn, cannot be attacked by enemy pawns
		piece = evalinfo.bb_w_knights & evalinfo.passedPawnsAndOutposts & whitePawnAttacks;
		while (piece != 0) {
			evalinfo.eval_o_part2 += OTHERS_KNIGHT_OUTPOST_O * KNIGHT_OUTPOST_MG[Long.numberOfTrailingZeros(piece) >>> 3];
			evalinfo.eval_e_part2 += OTHERS_KNIGHT_OUTPOST_E * KNIGHT_OUTPOST_EG[Long.numberOfTrailingZeros(piece) >>> 3];
			
			piece &= piece - 1;
		}
		piece = evalinfo.bb_b_knights & evalinfo.passedPawnsAndOutposts & blackPawnAttacks;
		while (piece != 0) {
			evalinfo.eval_o_part2 -= OTHERS_KNIGHT_OUTPOST_O * KNIGHT_OUTPOST_MG[7 - Long.numberOfTrailingZeros(piece) / 8];
			evalinfo.eval_e_part2 -= OTHERS_KNIGHT_OUTPOST_E * KNIGHT_OUTPOST_EG[7 - Long.numberOfTrailingZeros(piece) / 8];
			
			piece &= piece - 1;
		}
		
		
		// quiescence search could leave one side in check
		if (cb.getBoard().isInCheck()) {
			if (cb.getColorToMove() == WHITE) {
				evalinfo.eval_o_part2 -= OTHERS_IN_CHECK_O * IN_CHECK;
				evalinfo.eval_e_part2 -= OTHERS_IN_CHECK_E * IN_CHECK;
			} else {
				evalinfo.eval_o_part2 += OTHERS_IN_CHECK_O * IN_CHECK;
				evalinfo.eval_e_part2 += OTHERS_IN_CHECK_E * IN_CHECK;
			}
		}
	}
	
	
	public void calculateKingSafetyScores() {

		for (int kingColor = WHITE; kingColor <= BLACK; kingColor++) {
			final int enemyColor = 1 - kingColor;

			if ((evalinfo.getPieces(enemyColor, QUEEN) | evalinfo.getPieces(enemyColor, ROOK)) == 0) {
				continue;
			}

			int counter = KS_RANK[(7 * kingColor) + ChessConstants.COLOR_FACTOR[kingColor] * cb.getKingIndex(kingColor) / 8];

			counter += KS_NO_FRIENDS[Long.bitCount(cb.getKingArea(kingColor) & ~evalinfo.getFriendlyPieces(kingColor))];
			counter += openFiles(kingColor, evalinfo.getPieces(kingColor, PAWN));

			// king can move?
			if ((evalinfo.attacks[kingColor][KING] & ~evalinfo.getFriendlyPieces(kingColor)) == 0) {
				counter++;
			}
			counter += KS_ATTACKS[Long.bitCount(cb.getKingArea(kingColor) & evalinfo.attacksAll[enemyColor])];
			counter += checks(kingColor);

			counter += KS_DOUBLE_ATTACKS[Long
					.bitCount(KING_MOVES[cb.getKingIndex(kingColor)] & evalinfo.doubleAttacks[enemyColor] & ~evalinfo.attacks[kingColor][PAWN])];

			if ((cb.getCheckingPieces() & evalinfo.getFriendlyPieces(enemyColor)) != 0) {
				counter++;
			}

			// bonus for stm
			counter += 1 - cb.getColorToMove() ^ enemyColor;

			// bonus if there are discovered checks possible
			counter += Long.bitCount(cb.getDiscoveredPieces() & evalinfo.getFriendlyPieces(enemyColor)) * 2;

			// pinned at first rank
			if ((cb.getPinnedPieces() & RANK_FIRST[kingColor]) != 0) {
				counter++;
			}

			if (evalinfo.getPieces(enemyColor, QUEEN) == 0) {
				counter /= 2;
			} else if (Long.bitCount(evalinfo.getPieces(enemyColor, QUEEN)) == 1) {
				// bonus for small king-queen distance
				if ((evalinfo.attacksAll[kingColor] & evalinfo.getPieces(enemyColor, QUEEN)) == 0) {
					counter += KS_QUEEN_TROPISM[getDistance(cb.getKingIndex(kingColor),
							Long.numberOfTrailingZeros(evalinfo.getPieces(enemyColor, QUEEN)))];
				}
			}

			counter += KS_ATTACK_PATTERN[evalinfo.kingAttackersFlag[enemyColor]];

			int value = ChessConstants.COLOR_FACTOR[enemyColor] * KS_SCORES[Math.min(counter, KS_SCORES.length - 1)];
			evalinfo.eval_o_part2 += KING_SAFETY_O * value;	
			evalinfo.eval_e_part2 += KING_SAFETY_E * value;
		}
	}
	
	
	public void calculateSpace() {

		int score = 0;

		score += OTHER_SCORES[IX_SPACE]
				* Long.bitCount((evalinfo.bb_w_pawns >>> 8) & (evalinfo.bb_w_knights | evalinfo.bb_w_bishops) & RANK_234);
		score -= OTHER_SCORES[IX_SPACE]
				* Long.bitCount((evalinfo.bb_b_pawns << 8) & (evalinfo.bb_b_knights | evalinfo.bb_b_bishops) & RANK_567);

		// idea taken from Laser
		long space = evalinfo.bb_w_pawns >>> 8;
		space |= space >>> 8 | space >>> 16;
		score += SPACE[Long.bitCount(evalinfo.bb_all_w_pieces)]
				* Long.bitCount(space & ~evalinfo.bb_w_pawns & ~evalinfo.attacks[BLACK][PAWN] & FILE_CDEF);
		space = evalinfo.bb_b_pawns << 8;
		space |= space << 8 | space << 16;
		score -= SPACE[Long.bitCount(evalinfo.bb_all_b_pieces)]
				* Long.bitCount(space & ~evalinfo.bb_b_pawns & ~evalinfo.attacks[WHITE][PAWN] & FILE_CDEF);

		evalinfo.eval_o_part2 += SPACE_O * score;
		evalinfo.eval_e_part2 += SPACE_E * score;
	}
	
	
	protected int openFiles(final int kingColor, final long pawns) {

		if (evalinfo.getPieces(1 - kingColor, QUEEN) == 0) {
			return 0;
		}
		if (Long.bitCount(evalinfo.getPieces(1 - kingColor, ROOK)) < 2) {
			return 0;
		}

		if ((RANK_FIRST[kingColor] & evalinfo.getPieces(kingColor, KING)) != 0) {
			if ((KING_SIDE & evalinfo.getPieces(kingColor, KING)) != 0) {
				if ((FILE_G & pawns) == 0 || (FILE_H & pawns) == 0) {
					return KS_OTHER[2];
				}
			} else if ((QUEEN_SIDE & evalinfo.getPieces(kingColor, KING)) != 0) {
				if ((FILE_A & pawns) == 0 || (FILE_B & pawns) == 0) {
					return KS_OTHER[2];
				}
			}
		}
		return 0;
	}

	protected int checks(final int kingColor) {
		final int enemyColor = 1 - kingColor;
		final int kingIndex = cb.getKingIndex(kingColor);
		final long possibleSquares = ~evalinfo.getFriendlyPieces(enemyColor)
				& (~KING_MOVES[kingIndex] | KING_MOVES[kingIndex] & evalinfo.doubleAttacks[enemyColor] & ~evalinfo.doubleAttacks[kingColor]);

		int counter = checkNight(kingColor, KNIGHT_MOVES[kingIndex] & possibleSquares & evalinfo.attacks[enemyColor][NIGHT]);

		long moves;
		long queenMoves = 0;
		if ((evalinfo.getPieces(enemyColor, QUEEN) | evalinfo.getPieces(enemyColor, BISHOP)) != 0) {
			moves = getBishopMoves(kingIndex, evalinfo.bb_all ^ evalinfo.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves = moves;
			counter += checkBishop(kingColor, moves & evalinfo.attacks[enemyColor][BISHOP]);
		}
		if ((evalinfo.getPieces(enemyColor, QUEEN) | evalinfo.getPieces(enemyColor, ROOK)) != 0) {
			moves = getRookMoves(kingIndex, evalinfo.bb_all ^ evalinfo.getPieces(kingColor, QUEEN)) & possibleSquares;
			queenMoves |= moves;
			counter += checkRook(kingColor, moves & evalinfo.attacks[enemyColor][ROOK]);
		}

		if (Long.bitCount(evalinfo.getPieces(enemyColor, QUEEN)) == 1) {
			counter += safeCheckQueen(kingColor, queenMoves & ~evalinfo.attacksAll[kingColor] & evalinfo.attacks[enemyColor][QUEEN]);
			counter += safeCheckQueenTouch(kingColor);
		}

		return counter;
	}

	private int safeCheckQueenTouch(final int kingColor) {
		if ((evalinfo.kingAttackersFlag[1 - kingColor] & ChessConstants.FLAG_QUEEN) == 0) {
			return 0;
		}
		final int enemyColor = 1 - kingColor;
		if ((KING_MOVES[cb.getKingIndex(kingColor)] & ~evalinfo.getFriendlyPieces(enemyColor) & evalinfo.attacks[enemyColor][QUEEN] & ~evalinfo.doubleAttacks[kingColor]
				& evalinfo.doubleAttacks[enemyColor]) != 0) {
			return KS_OTHER[0];
		}
		return 0;
	}

	private int safeCheckQueen(final int kingColor, final long safeQueenMoves) {
		if (safeQueenMoves != 0) {
			return KS_CHECK_QUEEN[Long.bitCount(evalinfo.getFriendlyPieces(kingColor))];
		}

		return 0;
	}

	private int checkRook(final int kingColor, final long rookMoves) {
		if (rookMoves == 0) {
			return 0;
		}

		int counter = 0;
		if ((rookMoves & ~evalinfo.attacksAll[kingColor]) != 0) {
			counter += KS_CHECK[ROOK];

			// last rank?
			if (kingBlockedAtLastRank(kingColor, KING_MOVES[cb.getKingIndex(kingColor)] & evalinfo.bb_free & ~evalinfo.attacksAll[1 - kingColor])) {
				counter += KS_OTHER[1];
			}

		} else {
			counter += KS_UCHECK[ROOK];
		}

		return counter;
	}

	private int checkBishop(final int kingColor, final long bishopMoves) {
		if (bishopMoves != 0) {
			if ((bishopMoves & ~evalinfo.attacksAll[kingColor]) != 0) {
				return KS_CHECK[BISHOP];
			} else {
				return KS_UCHECK[BISHOP];
			}
		}
		return 0;
	}

	private int checkNight(final int kingColor, final long nightMoves) {
		if (nightMoves != 0) {
			if ((nightMoves & ~evalinfo.attacksAll[kingColor]) != 0) {
				return KS_CHECK[NIGHT];
			} else {
				return KS_UCHECK[NIGHT];
			}
		}
		return 0;
	}

	private boolean kingBlockedAtLastRank(final int kingColor, final long safeKingMoves) {
		return (RANKS[7 * kingColor] & evalinfo.getPieces(kingColor, KING)) != 0 && (safeKingMoves & RANKS[7 * kingColor]) == safeKingMoves;
	}
	

	protected void getPassedPawnScore(final int index, final int color) {

		
		final int nextIndex = index + ChessConstants.COLOR_FACTOR_8[color];
		final long square = POWER_LOOKUP[index];
		final long maskNextSquare = POWER_LOOKUP[nextIndex];
		final long maskPreviousSquare = POWER_LOOKUP[index - ChessConstants.COLOR_FACTOR_8[color]];
		final long maskFile = FILES[index & 7];
		final int enemyColor = 1 - color;
		float multiplier = 1;

		// is piece blocked?
		if ((evalinfo.bb_all & maskNextSquare) != 0) {
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
		if ((ChessConstants.PINNED_MOVEMENT[nextIndex][index] & evalinfo.getPieces(enemyColor, ChessConstants.KING)) != 0) {
			multiplier *= PASSED_MULTIPLIERS[2];
		}

		// under attack?
		if (cb.getColorToMove() != color && (evalinfo.attacksAll[enemyColor] & square) != 0) {
			multiplier *= PASSED_MULTIPLIERS[4];
		}

		// defended by rook from behind?
		if ((maskFile & evalinfo.getPieces(color, ROOK)) != 0 && (evalinfo.attacks[color][ROOK] & square) != 0 && (evalinfo.attacks[color][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[5];
		}

		// attacked by rook from behind?
		else if ((maskFile & evalinfo.getPieces(enemyColor, ROOK)) != 0 && (evalinfo.attacks[enemyColor][ROOK] & square) != 0
				&& (evalinfo.attacks[enemyColor][ROOK] & maskPreviousSquare) != 0) {
			multiplier *= PASSED_MULTIPLIERS[6];
		}

		// king tropism
		multiplier *= PASSED_KING_MULTI[getDistance(cb.getKingIndex(color), index)];
		multiplier *= PASSED_KING_MULTI[8 - getDistance(cb.getKingIndex(enemyColor), index)];

		final int scoreIndex = (7 * color) + ChessConstants.COLOR_FACTOR[color] * index / 8;
		
		if (color == WHITE) {			
			evalinfo.eval_o_part2 += PAWN_PASSED_O * PASSED_SCORE_MG[scoreIndex] * multiplier;
			evalinfo.eval_e_part2 += PAWN_PASSED_E * PASSED_SCORE_EG[scoreIndex] * multiplier;
		} else {
			evalinfo.eval_o_part2 -= PAWN_PASSED_O * PASSED_SCORE_MG[scoreIndex] * multiplier;
			evalinfo.eval_e_part2 -= PAWN_PASSED_E * PASSED_SCORE_EG[scoreIndex] * multiplier;	
		}
	}

	protected int getBlackPromotionDistance(final int index) {
		// check if it cannot be stopped
		int promotionDistance = index >>> 3;
		if (promotionDistance == 1 && cb.getColorToMove() == BLACK) {
			if ((POWER_LOOKUP[index - 8] & (evalinfo.attacksAll[WHITE] | evalinfo.bb_all)) == 0) {
				if ((POWER_LOOKUP[index] & evalinfo.attacksAll[WHITE]) == 0) {
					return 1;
				}
			}
		}
		return SHORT_MAX;
	}

	protected int getWhitePromotionDistance(final int index) {
		// check if it cannot be stopped
		int promotionDistance = 7 - index / 8;
		if (promotionDistance == 1 && cb.getColorToMove() == WHITE) {
			if ((POWER_LOOKUP[index + 8] & (evalinfo.attacksAll[BLACK] | evalinfo.bb_all)) == 0) {
				if ((POWER_LOOKUP[index] & evalinfo.attacksAll[BLACK]) == 0) {
					return 1;
				}
			}
		}
		return SHORT_MAX;
	}
	
	
	protected static class EvalInfo {
		
		
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
		
		
		public void clearEvalAttacks() {
			kingAttackersFlag[WHITE] = 0;
			kingAttackersFlag[BLACK] = 0;
			attacks[WHITE][PAWN] = 0;
			attacks[BLACK][PAWN] = 0;
			attacks[WHITE][NIGHT] = 0;
			attacks[BLACK][NIGHT] = 0;
			attacks[WHITE][BISHOP] = 0;
			attacks[BLACK][BISHOP] = 0;
			attacks[WHITE][ROOK] = 0;
			attacks[BLACK][ROOK] = 0;
			attacks[WHITE][QUEEN] = 0;
			attacks[BLACK][QUEEN] = 0;
			attacks[WHITE][KING] = 0;
			attacks[BLACK][KING] = 0;
			attacksAll[WHITE] = 0;
			attacksAll[BLACK] = 0;
			doubleAttacks[WHITE] = 0;
			doubleAttacks[BLACK] = 0;
			passedPawnsAndOutposts = 0;
		}
		
		
		public void fillBB(IChessBoard cb) {
			bb_w_pawns = cb.getPieces(WHITE, PAWN);
			bb_b_pawns = cb.getPieces(BLACK, PAWN);
			bb_w_bishops = cb.getPieces(WHITE, BISHOP);
			bb_b_bishops = cb.getPieces(BLACK, BISHOP);
			bb_w_knights = cb.getPieces(WHITE, NIGHT);
			bb_b_knights = cb.getPieces(BLACK, NIGHT);
			bb_w_queens = cb.getPieces(WHITE, QUEEN);
			bb_b_queens = cb.getPieces(BLACK, QUEEN);
			bb_w_rooks = cb.getPieces(WHITE, ROOK);
			bb_b_rooks = cb.getPieces(BLACK, ROOK);
			bb_w_king = cb.getPieces(WHITE, KING);
			bb_b_king = cb.getPieces(BLACK, KING);
			bb_all_w_pieces = bb_w_pawns | bb_w_bishops | bb_w_knights | bb_w_queens | bb_w_rooks | bb_w_king;
			bb_all_b_pieces = bb_b_pawns | bb_b_bishops | bb_b_knights | bb_b_queens | bb_b_rooks | bb_b_king;
			bb_all = bb_all_w_pieces | bb_all_b_pieces;
			bb_free = ~bb_all;
		}
		
		
		public void clearEvals1() {
			eval_o_part1 = 0;
			eval_e_part1 = 0;
		}
		
		
		public void clearEvals2() {
			eval_o_part2 = 0;
			eval_e_part2 = 0;
		}
		
		
		public long getFriendlyPieces(int colour) {
			return colour == WHITE ? bb_all_w_pieces : bb_all_b_pieces;
		}
		
		public long getPieces(int colour, int type) {
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

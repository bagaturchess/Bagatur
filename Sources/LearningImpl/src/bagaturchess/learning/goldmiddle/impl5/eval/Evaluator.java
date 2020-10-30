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
package bagaturchess.learning.goldmiddle.impl5.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;


public class Evaluator extends Evaluator_BaseImpl {
	
	
	// Connected pawn bonus by opposed, phalanx, #support and rank
	public static int[][][][] Connected_O = new int[2][2][3][Rank8 + 1];
	public static int[][][][] Connected_E = new int[2][2][3][Rank8 + 1];
	  
	static {
		
		int[] Seed = { 0, 13, 24, 18, 65, 100, 175, 330 };
		
		for (int opposed = 0; opposed <= 1; ++opposed) {
			for (int phalanx = 0; phalanx <= 1; ++phalanx) {
				for (int support = 0; support <= 2; ++support) {
					for (int rankID = Rank2; rankID < Rank8; ++rankID) {
						
						int v = 17 * support;
						v += (Seed[rankID] + (phalanx != 0 ? (Seed[rankID + 1] - Seed[rankID]) / 2 : 0)) >>> opposed;
						
					  	Connected_O[opposed][phalanx][support][rankID] = v;
					  	Connected_E[opposed][phalanx][support][rankID] = v * (rankID - 2) / 4;
					}
				}
			}
		}
	}
	
	public static final int Backward_O = 9;
	public static final int Backward_E = 24;
	public static final int Doubled_O = 11;
	public static final int Doubled_E = 56;
	public static final int Isolated_O = 5;
	public static final int Isolated_E = 15;
	
	
	public static int[][] ShelterStrength = {
		  {-6, 81, 93, 58, 39, 18, 25},
		  {-43, 61, 35, -49, -29, -11, -63},
		  {-10, 75, 23, -2, 32, 3, -45},
		  {-39, -13, -29, -52, -48, -67, -166}
	};
		
	public static int[][] UnblockedStorm = {
		  {89, 107, 123, 93, 57, 45, 51},
		  {44, -18, 123, 46, 39, -7, 23},
		  {4, 52, 162, 37, 7, -14, -2},
		  {-10, -14, 90, 15, 2, -7, -16}
	};
	
	
	// PassedRank[Rank] contains a bonus according to the rank of a passed pawn
	public static final int[] PassedRank_O = {make_score_o(0, 0), make_score_o(5, 18), make_score_o(12, 23), make_score_o(10, 31), make_score_o(57, 62), make_score_o(163, 167), make_score_o(271, 250)};
	public static final int[] PassedRank_E = {make_score_e(0, 0), make_score_e(5, 18), make_score_e(12, 23), make_score_e(10, 31), make_score_e(57, 62), make_score_e(163, 167), make_score_e(271, 250)};
	
	
	// PassedFile[File] contains a bonus according to the file of a passed pawn
	public static final int[] PassedFile_O = {make_score_o(-1, 7), make_score_o(0, 9), make_score_o(-9, -8), make_score_o(-30, -14), make_score_o(-30, -14), make_score_o(-9, -8), make_score_o(0, 9), make_score_o(-1, 7)};
	public static final int[] PassedFile_E = {make_score_e(-1, 7), make_score_e(0, 9), make_score_e(-9, -8), make_score_e(-30, -14), make_score_e(-30, -14), make_score_e(-9, -8), make_score_e(0, 9), make_score_e(-1, 7)};
	
	
	// KingAttackWeights[PieceType] contains king attack weights by piece type
	public static final int[] KingAttackWeights = {0, 0, 81, 52, 44, 10};
	
	
	public static final int RookOnKingRing_O = 16;
	public static final int RookOnKingRing_E = 0;
	public static final int BishopOnKingRing_O = 24;
	public static final int BishopOnKingRing_E = 0;
	
	
	// MobilityBonus[PieceType-2][attacked] contains bonuses for middle and end game,
	// indexed by piece type and number of attacked squares in the mobility area.
	public static final int[][] MobilityBonus_O = {
		{make_score_o(-62, -81), make_score_o(-53, -56), make_score_o(-12, -30), make_score_o(-4, -14), make_score_o(3, 8), make_score_o(13, 15), make_score_o(22, 23), make_score_o(28, 27), make_score_o(33, 33), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{make_score_o(-48, -59), make_score_o(-20, -23), make_score_o(16, -3), make_score_o(26, 13), make_score_o(38, 24), make_score_o(51, 42), make_score_o(55, 54), make_score_o(63, 57), make_score_o(63, 65), make_score_o(68, 73), make_score_o(81, 78), make_score_o(81, 86), make_score_o(91, 88), make_score_o(98, 97), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{make_score_o(-58, -76), make_score_o(-27, -18), make_score_o(-15, 28), make_score_o(-10, 55), make_score_o(-5, 69), make_score_o(-2, 82), make_score_o(9, 112), make_score_o(16, 118), make_score_o(30, 132), make_score_o(29, 142), make_score_o(32, 155), make_score_o(38, 165), make_score_o(46, 166), make_score_o(48, 169), make_score_o(58, 171), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{make_score_o(-39, -36), make_score_o(-21, -15), make_score_o(3, 8), make_score_o(3, 18), make_score_o(14, 34), make_score_o(22, 54), make_score_o(28, 61), make_score_o(41, 73), make_score_o(43, 79), make_score_o(48, 92), make_score_o(56, 94), make_score_o(60, 104), make_score_o(60, 113), make_score_o(66, 120), make_score_o(67, 123), make_score_o(70, 126), make_score_o(71, 133), make_score_o(73, 136), make_score_o(79, 140), make_score_o(88, 143), make_score_o(88, 148), make_score_o(99, 166), make_score_o(102, 170), make_score_o(102, 175), make_score_o(106, 184), make_score_o(109, 191), make_score_o(113, 206), make_score_o(116, 212), 0, 0, 0, 0}
	};
	public static final int[][] MobilityBonus_E = {
		{make_score_e(-62, -81), make_score_e(-53, -56), make_score_e(-12, -30), make_score_e(-4, -14), make_score_e(3, 8), make_score_e(13, 15), make_score_e(22, 23), make_score_e(28, 27), make_score_e(33, 33), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{make_score_e(-48, -59), make_score_e(-20, -23), make_score_e(16, -3), make_score_e(26, 13), make_score_e(38, 24), make_score_e(51, 42), make_score_e(55, 54), make_score_e(63, 57), make_score_e(63, 65), make_score_e(68, 73), make_score_e(81, 78), make_score_e(81, 86), make_score_e(91, 88), make_score_e(98, 97), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{make_score_e(-58, -76), make_score_e(-27, -18), make_score_e(-15, 28), make_score_e(-10, 55), make_score_e(-5, 69), make_score_e(-2, 82), make_score_e(9, 112), make_score_e(16, 118), make_score_e(30, 132), make_score_e(29, 142), make_score_e(32, 155), make_score_e(38, 165), make_score_e(46, 166), make_score_e(48, 169), make_score_e(58, 171), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{make_score_e(-39, -36), make_score_e(-21, -15), make_score_e(3, 8), make_score_e(3, 18), make_score_e(14, 34), make_score_e(22, 54), make_score_e(28, 61), make_score_e(41, 73), make_score_e(43, 79), make_score_e(48, 92), make_score_e(56, 94), make_score_e(60, 104), make_score_e(60, 113), make_score_e(66, 120), make_score_e(67, 123), make_score_e(70, 126), make_score_e(71, 133), make_score_e(73, 136), make_score_e(79, 140), make_score_e(88, 143), make_score_e(88, 148), make_score_e(99, 166), make_score_e(102, 170), make_score_e(102, 175), make_score_e(106, 184), make_score_e(109, 191), make_score_e(113, 206), make_score_e(116, 212), 0, 0, 0, 0}
	};
	
	
	// Penalties for enemy's safe checks
	public static final int QueenSafeCheck = 780;
	public static final int RookSafeCheck = 880;
	public static final int BishopSafeCheck = 435;
	public static final int KnightSafeCheck = 790;
	
	public static final int PawnlessFlank_O = 18;
	public static final int PawnlessFlank_E = 94;
	public static final int CloseEnemies_O = 7;
	public static final int CloseEnemies_E = 0;
	
	public static final int SafeCheck[][] = {
			{}, {}, {792, 1283}, {645, 967}, {1084, 1897}, {772, 1119}
	};
	  
	public static final int FlankAttacks_O = 8;
	public static final int FlankAttacks_E = 0;
	
	
	// ThreatByMinor/ByRook[attacked PieceType] contains bonuses according to
	// which piece type attacks which one. Attacks on lesser pieces which are
	// pawn-defended are not considered.
	public static final int[] ThreatByMinor_O = {make_score_o(0, 0), make_score_o(0, 31), make_score_o(39, 42), make_score_o(57, 44), make_score_o(68, 112), make_score_o(62, 120), make_score_o(0, 0)};
	public static final int[] ThreatByMinor_E = {make_score_e(0, 0), make_score_e(0, 31), make_score_e(39, 42), make_score_e(57, 44), make_score_e(68, 112), make_score_e(62, 120), make_score_e(0, 0)};
	public static final int[] ThreatByRook_O = {make_score_o(0, 0), make_score_o(0, 24), make_score_o(38, 71), make_score_o(38, 61), make_score_o(0, 38), make_score_o(51, 38), make_score_o(0, 0)};
	public static final int[] ThreatByRook_E = {make_score_e(0, 0), make_score_e(0, 24), make_score_e(38, 71), make_score_e(38, 61), make_score_e(0, 38), make_score_e(51, 38), make_score_e(0, 0)};
	public static final int ThreatByRank_O = 14;
	public static final int ThreatByRank_E = 3;
	public static final int ThreatByKing_O = 21;
	public static final int ThreatByKing_E = 84;
	public static final int ThreatByPawnPush_O = 48;
	public static final int ThreatByPawnPush_E = 42;
	public static final int ThreatBySafePawn_O = 169;
	public static final int ThreatBySafePawn_E = 99;
	public static final int WeakUnopposedPawn_O = 14;
	public static final int WeakUnopposedPawn_E = 20;
	public static final int Hanging_O = 62;
	public static final int Hanging_E = 34;
	public static final int Overload_O = 12;
	public static final int Overload_E = 6;
	public static final int RestrictedPiece_O = 7;
	public static final int RestrictedPiece_E = 6;
	public static final int KnightOnQueen_O = 20;
	public static final int KnightOnQueen_E = 12;
	public static final int SliderOnQueen_O = 49;
	public static final int SliderOnQueen_E = 21;
	
	
	protected final IBitBoard bitboard;
	protected final EvalInfo evalinfo;
	private final Pawns pawns;
	protected final Material material;
	
	
	public Evaluator(IBitBoard _bitboard) {
		bitboard = _bitboard;
		evalinfo = new EvalInfo();
		pawns = new Pawns();
		material = new Material();
	}
	
	
	public double calculateScore1() {
		
		evalinfo.clearEvals1();
		
		evalinfo.fillBB(bitboard);
		
		calculateMaterialScore();
		evalinfo.eval_o_part1 += bitboard.getBaseEvaluation().getPST_o();
		evalinfo.eval_e_part1 += bitboard.getBaseEvaluation().getPST_e();
		
		int eval = bitboard.getMaterialFactor().interpolateByFactor(evalinfo.eval_o_part1, evalinfo.eval_e_part1);
		
		eval = eval * 100 / 213;
		
		return eval;
	}
	
	
	public double calculateScore2() {
		
		evalinfo.clearEvals2();
		
		pawns.evaluate(bitboard, evalinfo, Constants.COLOUR_WHITE, evalinfo.bb_pawns[Constants.COLOUR_WHITE]);
		pawns.evaluate(bitboard, evalinfo, Constants.COLOUR_BLACK, evalinfo.bb_pawns[Constants.COLOUR_BLACK]);		
		
		initialize(Constants.COLOUR_WHITE);
		initialize(Constants.COLOUR_BLACK);
		
		pieces(Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT, evalinfo.bb_knights[Constants.COLOUR_WHITE]);
		pieces(Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT, evalinfo.bb_knights[Constants.COLOUR_BLACK]);
		pieces(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP, evalinfo.bb_bishops[Constants.COLOUR_WHITE]);
		pieces(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP, evalinfo.bb_bishops[Constants.COLOUR_BLACK]);
		pieces(Constants.COLOUR_WHITE, Constants.TYPE_ROOK, evalinfo.bb_rooks[Constants.COLOUR_WHITE]);
		pieces(Constants.COLOUR_BLACK, Constants.TYPE_ROOK, evalinfo.bb_rooks[Constants.COLOUR_BLACK]);
		pieces(Constants.COLOUR_WHITE, Constants.TYPE_QUEEN, evalinfo.bb_queens[Constants.COLOUR_WHITE]);
		pieces(Constants.COLOUR_BLACK, Constants.TYPE_QUEEN, evalinfo.bb_queens[Constants.COLOUR_BLACK]);
		
		//King shelter and enemy pawns storm
		pawns.do_king_safety(bitboard, evalinfo, Constants.COLOUR_WHITE);
		pawns.do_king_safety(bitboard, evalinfo, Constants.COLOUR_BLACK);
		
		king(Constants.COLOUR_WHITE);
		king(Constants.COLOUR_BLACK);
		
		threats(Constants.COLOUR_WHITE);
		threats(Constants.COLOUR_BLACK);
		
		pawns.passed(bitboard, evalinfo, Constants.COLOUR_WHITE);
		pawns.passed(bitboard, evalinfo, Constants.COLOUR_BLACK);
		
		int eval = bitboard.getMaterialFactor().interpolateByFactor(evalinfo.eval_o_part2, evalinfo.eval_e_part2);
		
		eval = eval * 100 / 213;
		
		return eval;
	}
	
	
	public void calculateMaterialScore() {
		
		int countPawns = Long.bitCount(evalinfo.bb_pawns[Constants.COLOUR_WHITE]) - Long.bitCount(evalinfo.bb_pawns[Constants.COLOUR_BLACK]);
		int countKnights = Long.bitCount(evalinfo.bb_knights[Constants.COLOUR_WHITE]) - Long.bitCount(evalinfo.bb_knights[Constants.COLOUR_BLACK]);
		int countBishops = Long.bitCount(evalinfo.bb_bishops[Constants.COLOUR_WHITE]) - Long.bitCount(evalinfo.bb_bishops[Constants.COLOUR_BLACK]);
		int countRooks = Long.bitCount(evalinfo.bb_rooks[Constants.COLOUR_WHITE]) - Long.bitCount(evalinfo.bb_rooks[Constants.COLOUR_BLACK]);
		int countQueens = Long.bitCount(evalinfo.bb_queens[Constants.COLOUR_WHITE]) - Long.bitCount(evalinfo.bb_queens[Constants.COLOUR_BLACK]);
		
		int eval_o = (int) (countPawns * bitboard.getBoardConfig().getMaterial_PAWN_O()
				+ countKnights * bitboard.getBoardConfig().getMaterial_KNIGHT_O()
				+ countBishops * bitboard.getBoardConfig().getMaterial_BISHOP_O()
				+ countRooks * bitboard.getBoardConfig().getMaterial_ROOK_O()
				+ countQueens * bitboard.getBoardConfig().getMaterial_QUEEN_O());
		
		int eval_e = (int) (countPawns * bitboard.getBoardConfig().getMaterial_PAWN_E()
				+ countKnights * bitboard.getBoardConfig().getMaterial_KNIGHT_E()
				+ countBishops * bitboard.getBoardConfig().getMaterial_BISHOP_E()
				+ countRooks * bitboard.getBoardConfig().getMaterial_ROOK_E()
				+ countQueens * bitboard.getBoardConfig().getMaterial_QUEEN_E());

		evalinfo.eval_o_part1 += eval_o;
		evalinfo.eval_e_part1 += eval_e;
	}
	
	
	// Evaluation::initialize() computes king and pawn attacks, and the king ring
	// bitboard for a given color. This is done at the beginning of the evaluation.
	private void initialize(int Us) {
		
		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		//final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
		final int Direction_Down = (Us == Constants.COLOUR_WHITE ? Direction_SOUTH : Direction_NORTH);
		final long LowRanks = (Us == Constants.COLOUR_WHITE ? Rank2BB | Rank3BB : Rank7BB | Rank6BB);
		
		int ksq_us = getKingSquareID(evalinfo, Us);
		
	    long dblAttackByPawn = pawn_double_attacks_bb(evalinfo.bb_pawns[Us], Us);

		// Find our pawns that are blocked or on the first two ranks
		long b = evalinfo.bb_pawns[Us] & (shiftBB(evalinfo.bb_all, Direction_Down) | LowRanks);

	    // Squares occupied by those pawns, by our king or queen, by blockers to attacks on our king
	    // or controlled by enemy pawns are excluded from the mobility area.
	    evalinfo.mobilityArea[Us] = ~(b | evalinfo.bb_king[Us] | evalinfo.bb_queens[Us] | pawns.pawnAttacks[Them]);
	    
	    // Initialize attackedBy[] for king and pawns
		evalinfo.attackedBy[Us][Constants.TYPE_KING] = attacks_from(bitboard, ksq_us, Constants.TYPE_KING);
		evalinfo.attackedBy[Us][Constants.TYPE_PAWN] = pawns.pawnAttacks[Us];
		evalinfo.attackedBy[Us][Constants.TYPE_ALL] = evalinfo.attackedBy[Us][Constants.TYPE_KING] | evalinfo.attackedBy[Us][Constants.TYPE_PAWN];
		evalinfo.attackedBy2[Us] = dblAttackByPawn | (evalinfo.attackedBy[Us][Constants.TYPE_KING] & evalinfo.attackedBy[Us][Constants.TYPE_PAWN]);
		
		// Init our king safety tables
	    int ksq_us_adjusted = make_square(Math.min(Math.max(file_of(ksq_us), FileB), FileG), Math.min(Math.max(rank_of(ksq_us), Rank2), Rank7));
	    evalinfo.kingRing[Us] = attacks_from(bitboard, ksq_us_adjusted, Constants.TYPE_KING) | SquareBB[ksq_us_adjusted];

	    evalinfo.kingAttackersCount[Them] = Long.bitCount(evalinfo.kingRing[Us] & pawns.pawnAttacks[Them]);
	    evalinfo.kingAttacksCount[Them] = evalinfo.kingAttackersWeight[Them] = 0;
	    
	    // Remove from kingRing[] the squares defended by two pawns
	    evalinfo.kingRing[Us] &= ~dblAttackByPawn;
	}
	
	
	private void pieces(int Us, int pieceType, long Us_pieces)
	{
		
		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		//final int Direction_Down = (Us == Constants.COLOUR_WHITE ? Direction_SOUTH : Direction_NORTH);
		//final long OutpostRanks = (Us == Constants.COLOUR_WHITE ? Rank4BB | Rank5BB | Rank6BB : Rank5BB | Rank4BB | Rank3BB);
		
		long b;
		//long bb;
		
		evalinfo.attackedBy[Us][pieceType] = 0;
		
        while (Us_pieces != 0) {
            
    		int squareID = Long.numberOfTrailingZeros(Us_pieces);
        	//long squareBB = SquareBB[squareID];
        	
      		// Find attacked squares, including x-ray attacks for bishops and rooks
      		b = pieceType == Constants.TYPE_BISHOP ? attacks_bb(Constants.TYPE_BISHOP, squareID,
      														evalinfo.bb_all ^ (evalinfo.bb_queens[Us] | evalinfo.bb_bishops[Us])
      													)
      			: pieceType == Constants.TYPE_ROOK ? attacks_bb(Constants.TYPE_ROOK, squareID,
      														evalinfo.bb_all	^ (evalinfo.bb_queens[Us] | evalinfo.bb_rooks[Us])
      													)
      	      	: pieceType == Constants.TYPE_QUEEN ? attacks_bb(Constants.TYPE_QUEEN, squareID,
															evalinfo.bb_all	^ (evalinfo.bb_queens[Us] | evalinfo.bb_rooks[Us] | evalinfo.bb_bishops[Us])
														)
      			: attacks_from(bitboard, squareID, pieceType);
      		
      		evalinfo.attackedBy2[Us] |= evalinfo.attackedBy[Us][Constants.TYPE_ALL] & b;
      		evalinfo.attackedBy[Us][pieceType] |= b;
      		evalinfo.attackedBy[Us][Constants.TYPE_ALL] |= b;
      		
      		if ((b & evalinfo.kingRing[Them]) != 0) {
      			
      			evalinfo.kingAttackersCount[Us]++;
      			evalinfo.kingAttackersWeight[Us] += KingAttackWeights[pieceType];
      			evalinfo.kingAttacksCount[Us] += Long.bitCount(b & evalinfo.attackedBy[Them][Constants.TYPE_KING]);
      			
      		} else if (pieceType == Constants.TYPE_ROOK && (file_bb_bySquare(squareID) & evalinfo.kingRing[Them]) != 0) {
                evalinfo.addEvalsInPart2(Us, RookOnKingRing_O, RookOnKingRing_E);
                
      		} else if (pieceType == Constants.TYPE_BISHOP && (attacks_bb(Constants.TYPE_BISHOP, squareID, evalinfo.bb_pawns[Us] | evalinfo.bb_pawns[Them]) & evalinfo.kingRing[Them]) != 0) {
      			evalinfo.addEvalsInPart2(Us, BishopOnKingRing_O, BishopOnKingRing_E);
        	}
        	
      		int mob = Long.bitCount(b & evalinfo.mobilityArea[Us]);
      		
      		evalinfo.addEvalsInPart2(Us, MobilityBonus_O[pieceType - 2][mob], MobilityBonus_E[pieceType - 2][mob]);
      		
      		
      		Us_pieces &= Us_pieces - 1;
        }
	}
	
	
	// Evaluation::king() assigns bonuses and penalties to a king of a given color
	private void king(int Us) {

		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		final long Camp = (Us == Constants.COLOUR_WHITE ? AllSquares ^ Rank6BB ^ Rank7BB ^ Rank8BB : AllSquares ^ Rank1BB ^ Rank2BB ^ Rank3BB);

		final int squareID_ksq = getKingSquareID(evalinfo, Us);
		
	    long weak, b1, b2, b3, safe, unsafeChecks = 0;
	    long rookChecks, queenChecks, bishopChecks, knightChecks;
	    int kingDanger = 0;

	    // Attacked squares defended at most once by our queen or king
	    weak =  evalinfo.attackedBy[Them][Constants.TYPE_ALL]
	          & ~evalinfo.attackedBy2[Us]
	          & (~evalinfo.attackedBy[Us][Constants.TYPE_ALL] | evalinfo.attackedBy[Us][Constants.TYPE_KING] | evalinfo.attackedBy[Us][Constants.TYPE_QUEEN]);

	    // Analyse the safe enemy's checks which are possible on next move
	    safe  = ~evalinfo.bb_all_pieces[Them];
	    safe &= ~evalinfo.attackedBy[Us][Constants.TYPE_ALL] | (weak & evalinfo.attackedBy2[Them]);

	    b1 = attacks_bb(Constants.TYPE_ROOK, squareID_ksq, evalinfo.bb_all ^ evalinfo.bb_queens[Us]);
	    b2 = attacks_bb(Constants.TYPE_BISHOP, squareID_ksq, evalinfo.bb_all ^ evalinfo.bb_queens[Us]);

	    // Enemy rooks checks
	    rookChecks = b1 & evalinfo.attackedBy[Them][Constants.TYPE_ROOK] & safe;
	    if (rookChecks != 0)
	        kingDanger += SafeCheck[Constants.TYPE_ROOK][more_than_one(rookChecks) ? 1 : 0];
	    else
	        unsafeChecks |= b1 & evalinfo.attackedBy[Them][Constants.TYPE_ROOK];

	    // Enemy queen safe checks: count them only if the checks are from squares from
	    // which opponent cannot give a rook check, because rook checks are more valuable.
	    queenChecks =  (b1 | b2) & evalinfo.attackedBy[Them][Constants.TYPE_QUEEN] & safe
	                 & ~(evalinfo.attackedBy[Us][Constants.TYPE_QUEEN] | rookChecks);
	    if (queenChecks != 0)
	        kingDanger += SafeCheck[Constants.TYPE_QUEEN][more_than_one(queenChecks) ? 1 : 0];

	    // Enemy bishops checks: count them only if they are from squares from which
	    // opponent cannot give a queen check, because queen checks are more valuable.
	    bishopChecks =  b2 & evalinfo.attackedBy[Them][Constants.TYPE_BISHOP] & safe
	                  & ~queenChecks;
	    if (bishopChecks != 0)
	        kingDanger += SafeCheck[Constants.TYPE_BISHOP][more_than_one(bishopChecks) ? 1 : 0];

	    else
	        unsafeChecks |= b2 & evalinfo.attackedBy[Them][Constants.TYPE_BISHOP];

	    // Enemy knights checks
	    knightChecks = attacks_bb(Constants.TYPE_KNIGHT, squareID_ksq, 0L) & evalinfo.attackedBy[Them][Constants.TYPE_KNIGHT];
	    if ((knightChecks & safe) != 0)
	        kingDanger += SafeCheck[Constants.TYPE_KNIGHT][more_than_one(knightChecks & safe) ? 1 : 0];
	    else
	        unsafeChecks |= knightChecks;

	    // Find the squares that opponent attacks in our king flank, the squares
	    // which they attack twice in that flank, and the squares that we defend.
	    b1 = evalinfo.attackedBy[Them][Constants.TYPE_ALL] & KingFlank[file_of(squareID_ksq)] & Camp;
	    b2 = b1 & evalinfo.attackedBy2[Them];
	    b3 = evalinfo.attackedBy[Us][Constants.TYPE_ALL] & KingFlank[file_of(squareID_ksq)] & Camp;

	    int kingFlankAttack  = Long.bitCount(b1) + Long.bitCount(b2);
	    int kingFlankDefense = Long.bitCount(b3);

	    kingDanger +=        evalinfo.kingAttackersCount[Them] * evalinfo.kingAttackersWeight[Them]
	                 + 185 * Long.bitCount(evalinfo.kingRing[Us] & weak)
	                 + 148 * Long.bitCount(unsafeChecks)
	                 //+  98 * popcount(pos.blockers_for_king(Us))
	                 +  69 * evalinfo.kingAttacksCount[Them]
	                 +   3 * kingFlankAttack * kingFlankAttack / 8
	                 //+       mg_value(mobility[Them] - mobility[Us])
	                 - 873 * (evalinfo.bb_queens[Them] == 0 ? 1 : 0)
	                 - 100 * ((evalinfo.attackedBy[Us][Constants.TYPE_KNIGHT] & evalinfo.attackedBy[Us][Constants.TYPE_KING]) != 0 ? 1 : 0)
	                 //-   6 * mg_value(score) / 8
	                 -   4 * kingFlankDefense
	                 +  37;
	    
		// Transform the kingDanger units into a Score, and subtract it from the evaluation
		if (kingDanger > 100) {
			evalinfo.addEvalsInPart2(Us, -kingDanger * kingDanger / 4096, -kingDanger / 16);
		}
		
		// Penalty when our king is on a pawnless flank
		if (((evalinfo.bb_pawns[Us] | evalinfo.bb_pawns[Them]) & KingFlank[file_of(squareID_ksq)]) == 0) {
			evalinfo.addEvalsInPart2(Us, -PawnlessFlank_O, -PawnlessFlank_E);
		}
	
		// King tropism bonus, to anticipate slow motion attacks on our king
		evalinfo.addEvalsInPart2(Us, -FlankAttacks_O * kingFlankAttack, -FlankAttacks_E * kingFlankAttack);
	}
	
	
	// Evaluation::threats() assigns bonuses according to the types of the
	// attacking and the attacked pieces.
	private void threats(int Us) {

		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
		final long TRank3BB = (Us == Constants.COLOUR_WHITE ? Rank3BB : Rank6BB);

		long b;
		long weak;
		long defended;
		long nonPawnEnemies;
		long stronglyProtected;
		long safe;
		long restricted;

		// Non-pawn enemies
		nonPawnEnemies = evalinfo.bb_all_pieces[Them] ^ evalinfo.bb_pawns[Them];

		// Squares strongly protected by the enemy, either because they defend the
		// square with a pawn, or because they defend the square twice and we don't.
		stronglyProtected = evalinfo.attackedBy[Them][Constants.TYPE_PAWN] | (evalinfo.attackedBy2[Them] & ~evalinfo.attackedBy2[Us]);

		// Non-pawn enemies, strongly protected
		defended = nonPawnEnemies & stronglyProtected;

		// Enemies not strongly protected and under our attack
		weak = evalinfo.bb_all_pieces[Them] & ~stronglyProtected & evalinfo.attackedBy[Us][Constants.TYPE_ALL];
	  
		// Safe or protected squares
		safe = ~evalinfo.attackedBy[Them][Constants.TYPE_ALL] | evalinfo.attackedBy[Us][Constants.TYPE_ALL];

		// Bonus according to the kind of attacking pieces
		if ((defended | weak) != 0) {
			b = (defended | weak) & (evalinfo.attackedBy[Us][Constants.TYPE_KNIGHT] | evalinfo.attackedBy[Us][Constants.TYPE_BISHOP]);
			while (b != 0) {
				
				int squareID = Long.numberOfTrailingZeros(b);
				int pieceType = bitboard.getFigureType(squareID);
				
				evalinfo.addEvalsInPart2(Us, ThreatByMinor_O[pieceType], ThreatByMinor_E[pieceType]);
				
				if (pieceType != Constants.TYPE_PAWN) {
					int multiplier = relative_rank_bySquare(Them, squareID);
					evalinfo.addEvalsInPart2(Us, ThreatByRank_O * multiplier, ThreatByRank_E * multiplier);
				}
				
				b &= b - 1;
			}

			b = weak & evalinfo.attackedBy[Us][Constants.TYPE_ROOK];
			while (b != 0) {
				
				int squareID = Long.numberOfTrailingZeros(b);
				int pieceType = bitboard.getFigureType(squareID);
				
				evalinfo.addEvalsInPart2(Us, ThreatByRook_O[pieceType], ThreatByRook_E[pieceType]);
				
				if (pieceType != Constants.TYPE_PAWN) {
					int multiplier = relative_rank_bySquare(Them, squareID);
					evalinfo.addEvalsInPart2(Us, ThreatByRank_O * multiplier, ThreatByRank_E * multiplier);
				}
				
				b &= b - 1;
			}

			if ((weak & evalinfo.attackedBy[Us][Constants.TYPE_KING]) != 0) {
				evalinfo.addEvalsInPart2(Us, ThreatByKing_O, ThreatByKing_E);
			}

			int multiplier = Long.bitCount(weak & ~evalinfo.attackedBy[Them][Constants.TYPE_ALL]);
			evalinfo.addEvalsInPart2(Us, Hanging_O * multiplier, Hanging_E * multiplier);

			b = weak & nonPawnEnemies & evalinfo.attackedBy[Them][Constants.TYPE_ALL];
			multiplier = Long.bitCount(b);
			evalinfo.addEvalsInPart2(Us, Overload_O * multiplier, Overload_E * multiplier);
		}

		// Bonus for restricting their piece moves
		restricted = evalinfo.attackedBy[Them][Constants.TYPE_ALL] & ~evalinfo.attackedBy[Them][Constants.TYPE_PAWN] & ~evalinfo.attackedBy2[Them] & evalinfo.attackedBy[Us][Constants.TYPE_ALL];
		int multiplier = Long.bitCount(restricted);
		evalinfo.addEvalsInPart2(Us, RestrictedPiece_O * multiplier, RestrictedPiece_E * multiplier);

		// Bonus for enemy unopposed weak pawns
		if ((evalinfo.bb_rooks[Us] | evalinfo.bb_queens[Us]) != 0) {
			evalinfo.addEvalsInPart2(Us, WeakUnopposedPawn_O * pawns.weakUnopposed[Them], WeakUnopposedPawn_E * pawns.weakUnopposed[Them]);
		}

		// Find squares where our pawns can push on the next move
		b = shiftBB(evalinfo.bb_pawns[Us], Direction_Up) & evalinfo.bb_free;
		b |= shiftBB(b & TRank3BB, Direction_Up) & evalinfo.bb_free;

		// Keep only the squares which are relatively safe
		b &= ~evalinfo.attackedBy[Them][Constants.TYPE_PAWN] & safe;

		// Bonus for safe pawn threats on the next move
		b = pawn_attacks_bb(b, Us) & evalinfo.bb_all_pieces[Them];
		multiplier = Long.bitCount(b);
		evalinfo.addEvalsInPart2(Us, ThreatByPawnPush_O * multiplier, ThreatByPawnPush_E * multiplier);

		// Our safe or protected pawns
		b = evalinfo.bb_pawns[Us] & safe;

		b = pawn_attacks_bb(b, Us) & nonPawnEnemies;
		multiplier = Long.bitCount(b);
		evalinfo.addEvalsInPart2(Us, ThreatBySafePawn_O * multiplier, ThreatBySafePawn_E * multiplier);

		// Bonus for threats on the next moves against enemy queen
		if (Long.bitCount(evalinfo.bb_queens[Them]) == 1) {
			
			int squareID = Long.numberOfTrailingZeros(evalinfo.bb_queens[Them]);
			safe = evalinfo.mobilityArea[Us] & ~stronglyProtected;

			b = evalinfo.attackedBy[Us][Constants.TYPE_KNIGHT] & attacks_from(bitboard, squareID, Constants.TYPE_KNIGHT);
			multiplier = Long.bitCount(b & safe);
			evalinfo.addEvalsInPart2(Us, KnightOnQueen_O * multiplier, KnightOnQueen_E * multiplier);

			b = (evalinfo.attackedBy[Us][Constants.TYPE_BISHOP] & attacks_from(bitboard, squareID, Constants.TYPE_BISHOP) | (evalinfo.attackedBy[Us][Constants.TYPE_ROOK] & attacks_from(bitboard, squareID, Constants.TYPE_ROOK)));
			multiplier = Long.bitCount(b & safe & evalinfo.attackedBy2[Us]);
			evalinfo.addEvalsInPart2(Us, SliderOnQueen_O * multiplier, SliderOnQueen_E * multiplier);
		}
	}
	
	
	public static final int make_score_o(int mg, int eg) {
		return mg;
	}
	
	
	public static final int make_score_e(int mg, int eg) {
		return eg;
	}
	
	
	protected static final int getKingSquareID(EvalInfo evalinfo, int Us) {
		long bb_king = evalinfo.bb_king[Us];
		int squareID = Long.numberOfTrailingZeros(bb_king);
		return squareID;
	}
	
	
	protected static final long attacks_from(IBitBoard bitboard, int squareID, int pieceType) {
		
		if (pieceType == Constants.TYPE_PAWN){
			throw new IllegalStateException();
		}
		
		return pieceType == Constants.TYPE_BISHOP || pieceType == Constants.TYPE_ROOK ? attacks_bb(pieceType, squareID, ~bitboard.getFreeBitboard())
				: pieceType == Constants.TYPE_QUEEN ? attacks_from(bitboard, squareID, Constants.TYPE_ROOK) | attacks_from(bitboard, squareID, Constants.TYPE_BISHOP)
				: PseudoAttacks[pieceType][squareID];
	}
	
	
	public static class EvalInfo {
		
		
		public long[] mobilityArea = new long[Constants.COLOUR_BLACK + 1];
	    //private int[] mobility_o = new int[Constants.COLOUR_BLACK + 1];
	    //private int[] mobility_e = new int[Constants.COLOUR_BLACK + 1];
	    
	    // attackedBy[color][piece type] is a bitboard representing all squares
	    // attacked by a given color and piece type. Special "piece types" which
	    // is also calculated is ALL_PIECES.
		public long[][] attackedBy = new long[Constants.COLOUR_BLACK + 1][Constants.TYPE_ALL + 1];

	    // attackedBy2[color] are the squares attacked by 2 pieces of a given color,
	    // possibly via x-ray or by one pawn and one piece. Diagonal x-ray through
	    // pawn or squares attacked by 2 pawns are not explicitly added.
		public long[] attackedBy2 = new long[Constants.COLOUR_BLACK + 1];

	    // kingRing[color] are the squares adjacent to the king, plus (only for a
	    // king on its first rank) the squares two ranks in front. For instance,
	    // if black's king is on g8, kingRing[BLACK] is f8, h8, f7, g7, h7, f6, g6
	    // and h6. It is set to 0 when king safety evaluation is skipped.
		public long[] kingRing = new long[Constants.COLOUR_BLACK + 1];

	    // kingAttackersCount[color] is the number of pieces of the given color
	    // which attack a square in the kingRing of the enemy king.
		public int[] kingAttackersCount = new int[Constants.COLOUR_BLACK + 1];

	    // kingAttackersWeight[color] is the sum of the "weights" of the pieces of
	    // the given color which attack a square in the kingRing of the enemy king.
	    // The weights of the individual piece types are given by the elements in
	    // the KingAttackWeights array.
		public int[] kingAttackersWeight = new int[Constants.COLOUR_BLACK + 1];

	    // kingAttacksCount[color] is the number of attacks by the given color to
	    // squares directly adjacent to the enemy king. Pieces which attack more
	    // than one square are counted multiple times. For instance, if there is
	    // a white knight on g5 and black's king is on g8, this white knight adds 2
	    // to kingAttacksCount[WHITE].
		public int[] kingAttacksCount = new int[Constants.COLOUR_BLACK + 1];
	    
		public long bb_free;
		public long bb_all;
		public long[] bb_all_pieces 	= new long[Constants.COLOUR_BLACK + 1];
		public long[] bb_pawns 			= new long[Constants.COLOUR_BLACK + 1];
		public long[] bb_knights		= new long[Constants.COLOUR_BLACK + 1];
		public long[] bb_bishops 		= new long[Constants.COLOUR_BLACK + 1];
		public long[] bb_queens 		= new long[Constants.COLOUR_BLACK + 1];
		public long[] bb_rooks 			= new long[Constants.COLOUR_BLACK + 1];
		public long[] bb_king 			= new long[Constants.COLOUR_BLACK + 1];
		
		private int eval_o_part1;
		private int eval_e_part1;
		private int eval_o_part2;
		private int eval_e_part2;
		
		
		public void clearEvals1() {
			eval_o_part1 = 0;
			eval_e_part1 = 0;
		}
		
		
		public void clearEvals2() {
			eval_o_part2 = 0;
			eval_e_part2 = 0;
		}
		
		
		public void fillBB(IBitBoard bitboard) {
			bb_pawns[Constants.COLOUR_WHITE] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN);
			bb_pawns[Constants.COLOUR_BLACK] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN);
			bb_knights[Constants.COLOUR_WHITE] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT);
			bb_knights[Constants.COLOUR_BLACK] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT);
			bb_bishops[Constants.COLOUR_WHITE] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP);
			bb_bishops[Constants.COLOUR_BLACK] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP);
			bb_rooks[Constants.COLOUR_WHITE] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_ROOK);
			bb_rooks[Constants.COLOUR_BLACK] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_ROOK);
			bb_queens[Constants.COLOUR_WHITE] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_QUEEN);
			bb_queens[Constants.COLOUR_BLACK] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_QUEEN);
			bb_king[Constants.COLOUR_WHITE] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KING);
			bb_king[Constants.COLOUR_BLACK] = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KING);
			bb_all_pieces[Constants.COLOUR_WHITE] = bb_pawns[Constants.COLOUR_WHITE] | bb_bishops[Constants.COLOUR_WHITE] | bb_knights[Constants.COLOUR_WHITE] | bb_queens[Constants.COLOUR_WHITE] | bb_rooks[Constants.COLOUR_WHITE] | bb_king[Constants.COLOUR_WHITE];
			bb_all_pieces[Constants.COLOUR_BLACK] = bb_pawns[Constants.COLOUR_BLACK] | bb_bishops[Constants.COLOUR_BLACK] | bb_knights[Constants.COLOUR_BLACK] | bb_queens[Constants.COLOUR_BLACK] | bb_rooks[Constants.COLOUR_BLACK] | bb_king[Constants.COLOUR_BLACK];
			bb_all = bb_all_pieces[Constants.COLOUR_WHITE] | bb_all_pieces[Constants.COLOUR_BLACK];
			bb_free = ~bb_all;
		}
		
		
		private void addEvalsInPart2(int colour, int o, int e) {
			if (colour == Constants.COLOUR_WHITE) {
				eval_o_part2 += o;
				eval_e_part2 += e;
			} else {
				eval_o_part2 -= o;
				eval_e_part2 -= e;
			}
		}
	}
	
	
	protected static class Pawns {
		
		
		//public int[] scores = new int[Constants.COLOUR_BLACK + 1];
		public long[] passedPawns = new long[Constants.COLOUR_BLACK + 1];
		public long[] pawnAttacks = new long[Constants.COLOUR_BLACK + 1];
		public long[] pawnAttacksSpan = new long[Constants.COLOUR_BLACK + 1];
		public int[] kingSquares = new int[Constants.COLOUR_BLACK + 1];
		//private int[] kingSafety = new int[Constants.COLOUR_BLACK + 1];
		public int[] weakUnopposed = new int[Constants.COLOUR_BLACK + 1];
		//private int[] castlingRights = new int[Constants.COLOUR_BLACK + 1];
		public int[] semiopenFiles = new int[Constants.COLOUR_BLACK + 1];
		public int[][] pawnsOnSquares = new int[Constants.COLOUR_BLACK + 1][Constants.COLOUR_BLACK + 1]; // [color][light/dark squares]
		//public int openFiles;
		
		
		private void evaluate(IBitBoard bitboard, EvalInfo evalinfo, int Us, long Us_pawns) {
			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
			
			long neighbours;
			long stoppers;
			long doubled;
			long supported;
			long phalanx;
			long lever;
			long leverPush;
			boolean opposed;
			boolean backward;
			
			long ourPawns = evalinfo.bb_pawns[Us];
			long theirPawns = evalinfo.bb_pawns[Them];

			passedPawns[Us] = pawnAttacksSpan[Us] = weakUnopposed[Us] = 0;
			semiopenFiles[Us] = 0xFF;
			kingSquares[Us] = -1;
			pawnAttacks[Us] = pawn_attacks_bb(ourPawns, Us);
			pawnsOnSquares[Us][Constants.COLOUR_BLACK] = Long.bitCount(ourPawns & DarkSquares);
			pawnsOnSquares[Us][Constants.COLOUR_WHITE] = Long.bitCount(ourPawns & LightSquares);
			
            while (Us_pawns != 0) {
	            
            	int squareID = Long.numberOfTrailingZeros(Us_pawns);
            	
            	int fileID = file_of(squareID);
            	int rankID = rank_of(squareID);
            	if (rankID == 0 || rankID == 7) {
            		throw new IllegalStateException();
            	}
            	
				semiopenFiles[Us] &= ~(1 << fileID);
				pawnAttacksSpan[Us] |= pawn_attack_span(Us, squareID);
				
				// Flag the pawn
				opposed = (theirPawns & forward_file_bb(Us, squareID)) != 0;
				stoppers = theirPawns & passed_pawn_mask(Us, squareID);
				lever = (theirPawns & PawnAttacks[Us][squareID]);
				leverPush = theirPawns & PawnAttacks[Us][squareID + Direction_Up];
				doubled = ourPawns & SquareBB[squareID - Direction_Up];
				neighbours = ourPawns & adjacent_files_bb(fileID);
				phalanx = neighbours & rank_bb(squareID);
				supported = neighbours & rank_bb(squareID - Direction_Up);
				
				// A pawn is backward when it is behind all pawns of the same color
				// on the adjacent files and cannot be safely advanced.
				backward = (ourPawns & pawn_attack_span(Them, squareID + Direction_Up)) == 0 && (stoppers & (leverPush | (SquareBB[squareID + Direction_Up]))) != 0;
				
				// Passed pawns will be properly scored in evaluation because we need
				// full attack info to evaluate them. Include also not passed pawns
				// which could become passed after one or two pawn pushes when are
				// not attacked more times than defended.
				if ((stoppers ^ lever ^ leverPush) == 0 && (Long.bitCount(supported) >= Long.bitCount(lever) - 1) && Long.bitCount(phalanx) >= Long.bitCount(leverPush)) {
					
					passedPawns[Us] |= SquareBB[squareID];
					
				} else if (stoppers == SquareBB[squareID + Direction_Up] && relative_rank_bySquare(Us, squareID) >= Rank5) {
					
					long b = shiftBB(supported, Direction_Up) & ~theirPawns;
					while (b != 0) {
						if (!more_than_one(theirPawns & PawnAttacks[Us][Long.numberOfTrailingZeros(b)])) {
							passedPawns[Us] |= SquareBB[squareID];
						}
						b &= b - 1;
					}
				}
				
				// Score this pawn
				if ((supported | phalanx) != 0) {
					
					int supportedCount = Long.bitCount(supported);
					int o = Connected_O[opposed ? 1 : 0][phalanx == 0 ? 0 : 1][supportedCount][relative_rank_bySquare(Us, squareID)];
					int e = Connected_E[opposed ? 1 : 0][phalanx == 0 ? 0 : 1][supportedCount][relative_rank_bySquare(Us, squareID)];
					evalinfo.addEvalsInPart2(Us, o, e);
					
				} else if (neighbours == 0) {
					
					evalinfo.addEvalsInPart2(Us, -Isolated_O, -Isolated_E);
					weakUnopposed[Us] += (!opposed ? 1 : 0);
					
				} else if (backward) {
					
					evalinfo.addEvalsInPart2(Us, -Backward_O, -Backward_E);
					weakUnopposed[Us] += (!opposed ? 1 : 0);
				}

				if (doubled != 0 && supported == 0) {
					evalinfo.addEvalsInPart2(Us, -Doubled_O, -Doubled_E);
				}
				
				Us_pawns &= Us_pawns - 1;
            }
		}


		/// Entry::do_king_safety() calculates a bonus for king safety. It is called only
		/// when king square changes, which is about 20% of total king_safety() calls.
		private final void do_king_safety(IBitBoard bitboard, EvalInfo evalinfo, int Us) {
			
			int squareID_ksq = getKingSquareID(evalinfo, Us);
			kingSquares[Us] = squareID_ksq;
			//castlingRights[Us] = bitboard.hasRightsToKingCastle(Us) || bitboard.hasRightsToQueenCastle(Us)//pos.can_castle(Us);
			int minKingPawnDistance = 0;
			
			long pawns = evalinfo.bb_pawns[Us];
			if (pawns != 0) {
				while ((DistanceRingBB[squareID_ksq][minKingPawnDistance] & pawns) == 0) {
					minKingPawnDistance++;	
				}
			}
			
			int bonus = evaluate_shelter(evalinfo, Us, squareID_ksq);
			
			// If we can castle use the bonus after the castling if it is bigger
			if (bitboard.hasRightsToKingCastle(Us)) {
				bonus = Math.max(bonus, evaluate_shelter(evalinfo, Us, relative_square(Us, Fields.G1_ID)));
			}
			
			if (bitboard.hasRightsToQueenCastle(Us)) {
				bonus = Math.max(bonus, evaluate_shelter(evalinfo, Us, relative_square(Us, Fields.C1_ID)));
			}
			
			evalinfo.addEvalsInPart2(Us, bonus, -16 * minKingPawnDistance);
		}
		
		
		/// Entry::evaluate_shelter() calculates the shelter bonus and the storm
		/// penalty for a king, looking at the king file and the two closest files.
		protected final int evaluate_shelter(EvalInfo evalinfo, int Us, int squareID_ksq) {
			
			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			final int Direction_Down = (Us == Constants.COLOUR_WHITE ? Direction_SOUTH : Direction_NORTH);
			final long BlockRanks = (Us == Constants.COLOUR_WHITE ? Rank1BB | Rank2BB : Rank8BB | Rank7BB);
		
			long b = (evalinfo.bb_pawns[Us] | evalinfo.bb_pawns[Them]) & ~forward_ranks_bb(Them, squareID_ksq);
			long ourPawns = b & evalinfo.bb_pawns[Us];
			long theirPawns = b & evalinfo.bb_pawns[Them];
		
			int safety = (shiftBB(theirPawns, Direction_Down) & (FileABB | FileHBB) & BlockRanks & SquareBB[squareID_ksq]) != 0 ? 374 : 5;
			
			int center = Math.max(FileB, Math.min(FileG, file_of(squareID_ksq)));
			for (int fileID = center - 1; fileID <= center + 1; ++fileID) {
				
				long fileBB = file_bb_byFile(fileID);
				
				b = ourPawns & fileBB;
				int ourRank = (b != 0 ? relative_rank_bySquare(Us, backmost_sq(Us, b)) : 0);
				
				b = theirPawns & fileBB;
				int theirRank = (b != 0 ? relative_rank_bySquare(Us, frontmost_sq(Them, b)) : 0);
				
				int d = Math.min(fileID, fileID ^ FileH);
				safety += ShelterStrength[d][ourRank];
				safety -= (ourRank != 0 && (ourRank == theirRank - 1)) ? 66 * (theirRank == Rank3 ? 1 : 0) : UnblockedStorm[d][theirRank];
			}
		
			return safety;
		}
		
		
		private void passed(IBitBoard bitboard, EvalInfo evalinfo, int Us) {
			
			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
			int squareID_ksq = getKingSquareID(evalinfo, Us);
			
			long b;
			long bb;
			long squaresToQueen;
			long defendedSquares;
			long unsafeSquares;
			
			b = passedPawns[Us];
			
			while (b != 0) {
				
				int squareID = Long.numberOfTrailingZeros(b);
            	
				int rankID = relative_rank_bySquare(Us, squareID);
				
				int bonus_o = PassedRank_O[rankID];
				int bonus_e = PassedRank_E[rankID];
				
				if (rankID > Rank3)
				{
					int w = (rankID - 2) * (rankID - 2) + 2;
					int blockSq = squareID + Direction_Up;
					long blockBB = SquareBB[blockSq];
					
					// Adjust bonus based on the king's proximity
					bonus_o += 0;
					bonus_e += (king_proximity(Them, blockSq, squareID_ksq) * 5 - king_proximity(Us, blockSq, squareID_ksq) * 2) * w;

					// If blockSq is not the queening square then consider also a second push
					if (rankID != Rank7) {
						bonus_o -= 0;
						bonus_e -= king_proximity(Us, blockSq + Direction_Up, squareID_ksq) * w;
					}
					
					// If the pawn is free to advance, then increase the bonus
					if (bitboard.getFigureType(blockSq) == Constants.TYPE_NONE) {//pos.empty(blockSq))
						
						// If there is a rook or queen attacking/defending the pawn from behind,
						// consider all the squaresToQueen. Otherwise consider only the squares
						// in the pawn's path attacked or occupied by the enemy.
						defendedSquares = unsafeSquares = squaresToQueen = forward_file_bb(Us, squareID);
						
						bb = forward_file_bb(Them, squareID)
								& (evalinfo.bb_queens[Constants.COLOUR_WHITE] | evalinfo.bb_queens[Constants.COLOUR_BLACK] | evalinfo.bb_rooks[Constants.COLOUR_WHITE] | evalinfo.bb_rooks[Constants.COLOUR_BLACK])
								& attacks_from(bitboard, squareID, Constants.TYPE_ROOK);

						if ((evalinfo.bb_all_pieces[Us] & bb) == 0) {
							defendedSquares &= evalinfo.attackedBy[Us][Constants.TYPE_ALL];
						}

						if ((evalinfo.bb_all_pieces[Them] & bb) == 0) {
							unsafeSquares &= evalinfo.attackedBy[Them][Constants.TYPE_ALL] | evalinfo.bb_all_pieces[Them];
						}
						
						// If there aren't any enemy attacks, assign a big bonus. Otherwise
						// assign a smaller bonus if the block square isn't attacked.
						int k = unsafeSquares == 0 ? 20 : (unsafeSquares & blockBB) == 0 ? 9 : 0;
						
						// If the path to the queen is fully defended, assign a big bonus.
						// Otherwise assign a smaller bonus if the block square is defended.
						if (defendedSquares == squaresToQueen) {
							k += 6;
						} else if ((defendedSquares & blockBB) != 0) {
							k += 4;
						}
	
						bonus_o += k * w;
						bonus_e += k * w;
					}
				} // rank > RANK_3

				// Scale down bonus for candidate passers which need more than one
				// pawn push to become passed, or have a pawn in front of them.
				if (!pawn_passed(evalinfo, Us, squareID + Direction_Up)
						|| ((evalinfo.bb_pawns[Constants.COLOUR_WHITE] | evalinfo.bb_pawns[Constants.COLOUR_BLACK]) & forward_file_bb(Us, squareID)) != 0) {
					bonus_o = bonus_o / 2;
					bonus_e = bonus_e / 2;
				}
				
				evalinfo.addEvalsInPart2(Us, bonus_o + PassedFile_O[file_of(squareID)], bonus_e + PassedFile_E[file_of(squareID)]);
				
				b &= b - 1;
			}
		}
		
		
		public final int semiopen_file(int colour, int fileID) {
			return semiopenFiles[colour] & (1 << fileID);
		}
		
		
		public static final int king_proximity(int colour, int squareID, int kingSquareID) {
			return Math.min(distance(kingSquareID, squareID), 5);
		}
		
		
		public static final boolean pawn_passed(EvalInfo evalinfo, int Us, int squareID) {
			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			return (evalinfo.bb_pawns[Them] & passed_pawn_mask(Us, squareID)) == 0;
		}
	}
	
	
	protected static class Material {
		
		
		// Polynomial material imbalance parameters
		private static final int QuadraticOurs[][] = {
			//            OUR PIECES
		    // pair pawn knight bishop rook queen
		    {1438                               }, // Bishop pair
		    {  40,   38                         }, // Pawn
		    {  32,  255, -62                    }, // Knight      OUR PIECES
		    {   0,  104,   4,    0              }, // Bishop
		    { -26,   -2,  47,   105,  -208      }, // Rook
		    {-189,   24, 117,   133,  -134, -6  }  // Queen
		};

		private static final int QuadraticTheirs[][] = {
			//           THEIR PIECES
		    // pair pawn knight bishop rook queen
		    {   0                               }, // Bishop pair
		    {  36,    0                         }, // Pawn
		    {   9,   63,   0                    }, // Knight      OUR PIECES
		    {  59,   65,  42,     0             }, // Bishop
		    {  46,   39,  24,   -24,    0       }, // Rook
		    {  97,  100, -42,   137,  268,    0 }  // Queen
		};
		
		
		// Evaluate the material imbalance. We use PIECE_TYPE_NONE as a place holder
		// for the bishop pair "extended piece", which allows us to be more flexible
		// in defining bishop pair bonuses.
		int[][] pieceCount = new int[Constants.COLOUR_BLACK + 1][Constants.TYPE_QUEEN + 1];
		
		
		public void initialize(EvalInfo evalinfo) {
			
			pieceCount[Constants.COLOUR_WHITE][0] = (Long.bitCount(evalinfo.bb_bishops[Constants.COLOUR_WHITE]) > 1) ? 1 : 0;
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_PAWN] = Long.bitCount(evalinfo.bb_pawns[Constants.COLOUR_WHITE]);
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_KNIGHT] = Long.bitCount(evalinfo.bb_knights[Constants.COLOUR_WHITE]);
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_BISHOP] = Long.bitCount(evalinfo.bb_bishops[Constants.COLOUR_WHITE]);
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_ROOK] = Long.bitCount(evalinfo.bb_rooks[Constants.COLOUR_WHITE]);
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_QUEEN] = Long.bitCount(evalinfo.bb_queens[Constants.COLOUR_WHITE]);
			
			pieceCount[Constants.COLOUR_BLACK][0] = (Long.bitCount(evalinfo.bb_bishops[Constants.COLOUR_BLACK]) > 1) ? 1 : 0;
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_PAWN] = Long.bitCount(evalinfo.bb_pawns[Constants.COLOUR_BLACK]);
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_KNIGHT] = Long.bitCount(evalinfo.bb_knights[Constants.COLOUR_BLACK]);
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_BISHOP] = Long.bitCount(evalinfo.bb_bishops[Constants.COLOUR_BLACK]);
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_ROOK] = Long.bitCount(evalinfo.bb_rooks[Constants.COLOUR_BLACK]);
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_QUEEN] = Long.bitCount(evalinfo.bb_queens[Constants.COLOUR_BLACK]);
			
			/*
			pieceCount[Constants.COLOUR_WHITE][0] = (bitboard.getPiecesLists().getPieces(Constants.PID_W_BISHOP).getDataSize() > 1) ? 1 : 0;
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_PAWN] = bitboard.getPiecesLists().getPieces(Constants.PID_W_PAWN).getDataSize();
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_KNIGHT] = bitboard.getPiecesLists().getPieces(Constants.PID_W_KNIGHT).getDataSize();
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_BISHOP] = bitboard.getPiecesLists().getPieces(Constants.PID_W_BISHOP).getDataSize();
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_ROOK] = bitboard.getPiecesLists().getPieces(Constants.PID_W_ROOK).getDataSize();
			pieceCount[Constants.COLOUR_WHITE][Constants.TYPE_QUEEN] = bitboard.getPiecesLists().getPieces(Constants.PID_W_QUEEN).getDataSize();
			
			pieceCount[Constants.COLOUR_BLACK][0] = (bitboard.getPiecesLists().getPieces(Constants.PID_B_BISHOP).getDataSize() > 1) ? 1 : 0;
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_PAWN] = bitboard.getPiecesLists().getPieces(Constants.PID_B_PAWN).getDataSize();
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_KNIGHT] = bitboard.getPiecesLists().getPieces(Constants.PID_B_KNIGHT).getDataSize();
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_BISHOP] = bitboard.getPiecesLists().getPieces(Constants.PID_B_BISHOP).getDataSize();
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_ROOK] = bitboard.getPiecesLists().getPieces(Constants.PID_B_ROOK).getDataSize();
			pieceCount[Constants.COLOUR_BLACK][Constants.TYPE_QUEEN] = bitboard.getPiecesLists().getPieces(Constants.PID_B_QUEEN).getDataSize();
			*/
		}
		
		
		public int imbalance(int Us) {
			
			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			
			int bonus = 0;

		    // Second-degree polynomial material imbalance, by Tord Romstad
		    for (int pt1 = 0; pt1 <= Constants.TYPE_QUEEN; ++pt1)
		    {
		        if (pieceCount[Us][pt1] == 0)
		            continue;

		        int v = 0;

		        for (int pt2 = 0; pt2 <= pt1; ++pt2)
		            v +=  QuadraticOurs[pt1][pt2] * pieceCount[Us][pt2] + QuadraticTheirs[pt1][pt2] * pieceCount[Them][pt2];

		        bonus += pieceCount[Us][pt1] * v;
		    }

		    return bonus;
		}
	}
}

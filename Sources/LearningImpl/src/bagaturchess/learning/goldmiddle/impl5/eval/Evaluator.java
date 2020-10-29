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


public class Evaluator extends Evaluator_BaseImpl {
	
	
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
		
		/*pawns.evaluate(bitboard, evalinfo, Constants.COLOUR_WHITE, evalinfo.bb_pawns[Constants.COLOUR_WHITE]);
		pawns.evaluate(bitboard, evalinfo, Constants.COLOUR_BLACK, evalinfo.bb_pawns[Constants.COLOUR_BLACK]);
		pawns.openFiles = Long.bitCount(pawns.semiopenFiles[Constants.COLOUR_WHITE] & pawns.semiopenFiles[Constants.COLOUR_BLACK]);
		pawns.asymmetry = Long.bitCount((pawns.passedPawns[Constants.COLOUR_WHITE] | pawns.passedPawns[Constants.COLOUR_BLACK])
											| (pawns.semiopenFiles[Constants.COLOUR_WHITE] ^ pawns.semiopenFiles[Constants.COLOUR_BLACK])
										);
		*/
		
		initialize(Constants.COLOUR_WHITE);
		initialize(Constants.COLOUR_BLACK);
		
		
		return 0;
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
		final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
		final int Direction_Down = (Us == Constants.COLOUR_WHITE ? Direction_SOUTH : Direction_NORTH);
		final long LowRanks = (Us == Constants.COLOUR_WHITE ? Rank2BB | Rank3BB : Rank7BB | Rank6BB);
		
		int ksq_us = getKingSquareID(evalinfo, Us);
		
	    long dblAttackByPawn = pawn_double_attacks_bb(evalinfo.bb_pawns[Us], Us);

		// Find our pawns that are blocked or on the first two ranks
		long b = evalinfo.bb_pawns[Us] & (shiftBB(evalinfo.bb_all, Direction_Down) | LowRanks);

	    // Squares occupied by those pawns, by our king or queen, by blockers to attacks on our king
	    // or controlled by enemy pawns are excluded from the mobility area.
	    evalinfo.mobilityArea[Us] = ~(b | evalinfo.bb_king[Us] | evalinfo.bb_queens[Us] | blockers_for_king(Us) | pawns.pawnAttacks[Them]);
	    
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
	
	
	/*private void pieces(int Us, int pieceType, long Us_pieces)
	{

		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		final int Direction_Down = (Us == Constants.COLOUR_WHITE ? Direction_SOUTH : Direction_NORTH);
		final long OutpostRanks = (Us == Constants.COLOUR_WHITE ? Rank4BB | Rank5BB | Rank6BB : Rank5BB | Rank4BB | Rank3BB);

		long b;
		long bb;
		
		evalinfo.attackedBy[Us][pieceType] = 0;
		
        while (Us_pieces != 0) {
            
    		int squareID = Long.numberOfTrailingZeros(Us_pieces);
        	long squareBB = SquareBB[squareID];
        	
      		// Find attacked squares, including x-ray attacks for bishops and rooks
      		b = pieceType == Constants.TYPE_BISHOP ? attacks_bb(Constants.TYPE_BISHOP, squareID,
      									evalinfo.bb_all
      									^ (evalinfo.bb_queens[Constants.COLOUR_WHITE] | evalinfo.bb_queens[Constants.COLOUR_BLACK]))
      						: pieceType == Constants.TYPE_ROOK ? attacks_bb(Constants.TYPE_ROOK, squareID,
      									evalinfo.bb_all
	      								^ (evalinfo.bb_queens[Constants.COLOUR_WHITE] | evalinfo.bb_queens[Constants.COLOUR_BLACK])
	      								^ (evalinfo.bb_rooks[Constants.COLOUR_WHITE] | evalinfo.bb_rooks[Constants.COLOUR_BLACK])
      								)
      						: attacks_bb(squareID, pieceType, evalinfo.bb_all);
      		
      		//TODO implement blockers_for_king
      		if ((blockers_for_king(Us) & squareBB) != 0) {
      			//b &= LineBB[pos.<PieceType.KING.getValue().getValue()>square(Us)][squareID];
      		}
      		
      		evalinfo.attackedBy2[Us] |= evalinfo.attackedBy[Us][Constants.TYPE_ALL] & b;
      		evalinfo.attackedBy[Us][pieceType] |= b;
      		evalinfo.attackedBy[Us][Constants.TYPE_ALL] |= b;
      		
      		if ((b & evalinfo.kingRing[Them]) != 0) {
      			evalinfo.kingAttackersCount[Us]++;
      			evalinfo.kingAttackersWeight[Us] += KingAttackWeights[pieceType];
      			evalinfo.kingAttacksCount[Us] += Long.bitCount(b & evalinfo.attackedBy[Them][Constants.TYPE_KING]);
      		}
      		
      		int mob = Long.bitCount(b & evalinfo.mobilityArea[Us]);
      		
      		evalinfo.addEvalsInPart2(Us, MobilityBonus_O[pieceType - 2][mob], MobilityBonus_E[pieceType - 2][mob]);
      		
      		if (pieceType == Constants.TYPE_BISHOP || pieceType == Constants.TYPE_KNIGHT) {
      			
      			// Bonus if piece is on an outpost square or can reach one
      			bb = (OutpostRanks & ~pawns.pawnAttacksSpan[Them]);
      			if ((bb & squareBB) != 0) {
      				int o = Outpost_O[(pieceType == Constants.TYPE_BISHOP) ? 1 : 0][(evalinfo.attackedBy[Us][Constants.TYPE_PAWN] & squareBB) == 0 ? 0 : 1] * 2;
      				int e = Outpost_E[(pieceType == Constants.TYPE_BISHOP) ? 1 : 0][(evalinfo.attackedBy[Us][Constants.TYPE_PAWN] & squareBB) == 0 ? 0 : 1] * 2;
      				evalinfo.addEvalsInPart2(Us, o, e);
      			} else if ((bb &= (b & ~evalinfo.bb_all_pieces[Us])) != 0) {
      				int o = Outpost_O[(pieceType == Constants.TYPE_BISHOP) ? 1 : 0][(evalinfo.attackedBy[Us][Constants.TYPE_PAWN] & bb) == 0 ? 0 : 1 ];
      				int e = Outpost_E[(pieceType == Constants.TYPE_BISHOP) ? 1 : 0][(evalinfo.attackedBy[Us][Constants.TYPE_PAWN] & bb) == 0 ? 0 : 1 ];
      				evalinfo.addEvalsInPart2(Us, o, e);
      			}
      			  
      			// Knight and Bishop bonus for being right behind a pawn
      			if ((shiftBB(evalinfo.bb_pawns[Constants.COLOUR_WHITE] | evalinfo.bb_pawns[Constants.COLOUR_BLACK], Direction_Down) & squareBB) != 0) {
      				evalinfo.addEvalsInPart2(Us, MinorBehindPawn_O, MinorBehindPawn_E);
      			}

      			// Penalty if the piece is far from the king
      			int multiplier = distance(squareID, getKingSquareID(evalinfo, Us));
      			evalinfo.addEvalsInPart2(Us, -KingProtector_O * multiplier, -KingProtector_E * multiplier);
      			
      			if (pieceType == Constants.TYPE_BISHOP) {
      				// Penalty according to number of pawns on the same color square as the
      				// bishop, bigger when the center files are blocked with pawns.
      				long blocked = evalinfo.bb_pawns[Us] & shiftBB(evalinfo.bb_all, Direction_Down);
      				
      				multiplier = pawns.pawns_on_same_color_squares(Us, squareID) * (1 + Long.bitCount(blocked & CenterFiles));
      				evalinfo.addEvalsInPart2(Us, -BishopPawns_O * multiplier, -BishopPawns_E * multiplier);

      				// Bonus for bishop on a long diagonal which can "see" both center squares
      				if (more_than_one(attacks_bb(Constants.TYPE_BISHOP, squareID, evalinfo.bb_pawns[Constants.COLOUR_WHITE] | evalinfo.bb_pawns[Constants.COLOUR_BLACK]) & Center)) {
      					evalinfo.addEvalsInPart2(Us, LongDiagonalBishop_O, LongDiagonalBishop_E);
      				}
      			}
      		}

      		if (pieceType == Constants.TYPE_ROOK)
      		{
      			// Bonus for aligning rook with enemy pawns on the same rank/file
      			if (relative_rank_bySquare(Us, squareID) >= Rank5) {
      				int multiplier = Long.bitCount(evalinfo.bb_pawns[Them] & PseudoAttacks[Constants.TYPE_ROOK][squareID]);
      				evalinfo.addEvalsInPart2(Us, RookOnPawn_O * multiplier, RookOnPawn_E * multiplier);
      			}

      			// Bonus for rook on an open or semi-open file
      			if (pawns.semiopen_file(Us, file_of(squareID)) != 0) {
      				int index = pawns.semiopen_file(Them, file_of(squareID)) == 0 ? 0 : 1;
      				evalinfo.addEvalsInPart2(Us, RookOnFile_O[index], RookOnFile_E[index]);
      			}

      			// Penalty when trapped by the king, even more if the king cannot castle
      			else if (mob <= 3) {
      				int kf = file_of(getKingSquareID(evalinfo, Us));
      				if ((kf < FileE) == (file_of(squareID) < kf)) {
      					int o = (TrappedRook_O - mob * 22) * (1 + ((bitboard.hasRightsToKingCastle(Us) || bitboard.hasRightsToQueenCastle(Us)) ? 0 : 1));
      					int e = (TrappedRook_E - 0) * (1 + ((bitboard.hasRightsToKingCastle(Us) || bitboard.hasRightsToQueenCastle(Us)) ? 0 : 1));
      					evalinfo.addEvalsInPart2(Us, -o, -e);
      				}
      			}
      		}

      		if (pieceType == Constants.TYPE_QUEEN)
      		{
      			// Penalty if any relative pin or discovered attack against the queen
      			//TODO implement slider_blockers
      			//long queenPinners;
      			//if (pos.slider_blockers(pos.pieces(Them, PieceType.ROOK, PieceType.BISHOP), squareID, queenPinners) != null)
      			//{
      			//	score -= WeakQueen;
      			//}
      		}
      		
      		Us_pieces &= Us_pieces - 1;
        }
	}
*/
	
	
	//TODO
	private long blockers_for_king(int Us) {
		return 0;
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
		public int asymmetry;
		public int openFiles;
		
		
		private void evaluate(IBitBoard bitboard, EvalInfo evalinfo, int Us, long Us_pawns) {
			
		}


		/// Entry::do_king_safety() calculates a bonus for king safety. It is called only
		/// when king square changes, which is about 20% of total king_safety() calls.
		private final void do_king_safety(IBitBoard bitboard, EvalInfo evalinfo, int Us) {
			
		}
		
		
		/// Entry::evaluate_shelter() calculates the shelter bonus and the storm
		/// penalty for a king, looking at the king file and the two closest files.
		protected final int evaluate_shelter(EvalInfo evalinfo, int Us, int squareID_ksq) {
			return 0;
		}
		
		
		private void passed(IBitBoard bitboard, EvalInfo evalinfo, int Us) {
			
		}
		
		
		public final int semiopen_file(int colour, int fileID) {
			return semiopenFiles[colour] & (1 << fileID);
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

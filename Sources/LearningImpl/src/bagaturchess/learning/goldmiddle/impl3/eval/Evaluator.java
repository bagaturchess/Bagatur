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
package bagaturchess.learning.goldmiddle.impl3.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.state.PiecesList;


public class Evaluator extends Evaluator_BaseImpl {
	
	
	public static final int Backward = make_score(9, 24);
	public static final int Doubled = make_score(11, 56);
	public static final int Isolated = make_score(5, 15);
		
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
	
	// Connected pawn bonus by opposed, phalanx, #support and rank
	public static int[][][][] Connected = new int[2][2][3][Rank8 + 1];
	  
	static {
		
		int[] Seed = { 0, 13, 24, 18, 65, 100, 175, 330 };
		
		for (int opposed = 0; opposed <= 1; ++opposed) {
			for (int phalanx = 0; phalanx <= 1; ++phalanx) {
				for (int support = 0; support <= 2; ++support) {
					for (int rankID = Rank2; rankID < Rank8; ++rankID) {
						
						int v = 17 * support;
						v += (Seed[rankID] + (phalanx != 0 ? (Seed[rankID + 1] - Seed[rankID]) / 2 : 0)) >>> opposed;
						
					  	Connected[opposed][phalanx][support][rankID] = make_score(v, v * (rankID - 2) / 4);
					}
				}
			}
		}
	}
	
	// PassedRank[Rank] contains a bonus according to the rank of a passed pawn
	public static final int[] PassedRank = {make_score(0, 0), make_score(5, 18), make_score(12, 23), make_score(10, 31), make_score(57, 62), make_score(163, 167), make_score(271, 250)};

	// PassedFile[File] contains a bonus according to the file of a passed pawn
	public static final int[] PassedFile = {make_score(-1, 7), make_score(0, 9), make_score(-9, -8), make_score(-30, -14), make_score(-30, -14), make_score(-9, -8), make_score(0, 9), make_score(-1, 7)};
	  
	
	private final IBitBoard bitboard;
	private final EvalInfo evalinfo;
	private final Pawns pawns;
	
	
	public Evaluator(IBitBoard _bitboard) {
		bitboard = _bitboard;
		evalinfo = new EvalInfo();
		pawns = new Pawns();
	}
	
	
	public double calculateScore1() {
		int eval = 0;
		
		evalinfo.clearEvals1();
		calculateMaterialScore();
		
		eval = bitboard.getMaterialFactor().interpolateByFactor(
				bitboard.getBaseEvaluation().getPST_o() + evalinfo.eval_o_part1,
				bitboard.getBaseEvaluation().getPST_e() + evalinfo.eval_e_part1
				);
		
		eval += pawns.evaluate(bitboard, Constants.COLOUR_WHITE, bitboard.getPiecesLists().getPieces(Constants.PID_W_PAWN));
		eval -= pawns.evaluate(bitboard, Constants.COLOUR_BLACK, bitboard.getPiecesLists().getPieces(Constants.PID_B_PAWN));
		
		return eval;
	}
	
	
	public double calculateScore2() {
		int eval = 0;
		
		initialize(Constants.COLOUR_WHITE);
		initialize(Constants.COLOUR_BLACK);
		
		eval += pawns.do_king_safety(bitboard, Constants.COLOUR_WHITE);
		eval -= pawns.do_king_safety(bitboard, Constants.COLOUR_BLACK);
		
		eval += pawns.passed(bitboard, Constants.COLOUR_WHITE);
		eval -= pawns.passed(bitboard, Constants.COLOUR_BLACK);
		
		return eval;
	}
	
	
	public void calculateMaterialScore() {
		
		
		int w_eval_nopawns_o = bitboard.getBaseEvaluation().getWhiteMaterialNonPawns_o();
		int w_eval_nopawns_e = bitboard.getBaseEvaluation().getWhiteMaterialNonPawns_e();
		int b_eval_nopawns_o = bitboard.getBaseEvaluation().getBlackMaterialNonPawns_o();
		int b_eval_nopawns_e = bitboard.getBaseEvaluation().getBlackMaterialNonPawns_e();
		
		int w_eval_pawns_o = bitboard.getBaseEvaluation().getWhiteMaterialPawns_o();
		int w_eval_pawns_e = bitboard.getBaseEvaluation().getWhiteMaterialPawns_e();
		int b_eval_pawns_o = bitboard.getBaseEvaluation().getBlackMaterialPawns_o();
		int b_eval_pawns_e = bitboard.getBaseEvaluation().getBlackMaterialPawns_e();

		evalinfo.eval_o_part1 += (w_eval_nopawns_o - b_eval_nopawns_o) + (w_eval_pawns_o - b_eval_pawns_o);
		evalinfo.eval_e_part1 += (w_eval_nopawns_e - b_eval_nopawns_e) + (w_eval_pawns_e - b_eval_pawns_e);
	}
	
	
	
	// Evaluation::initialize() computes king and pawn attacks, and the king ring
	// bitboard for a given color. This is done at the beginning of the evaluation.
	private void initialize(int Us) {

		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
		final int Direction_Down = (Us == Constants.COLOUR_WHITE ? Direction_SOUTH : Direction_NORTH);
		final long LowRanks = (Us == Constants.COLOUR_WHITE ? Rank2BB | Rank3BB: Rank7BB | Rank6BB);
		
		// Find our pawns that are blocked or on the first two ranks
		long b = bitboard.getFiguresBitboardByColourAndType(Us, Constants.TYPE_PAWN) & (shiftBB(~bitboard.getFreeBitboard(), Direction_Down) | LowRanks);
		
		// Squares occupied by those pawns, by our king or queen, or controlled by enemy pawns
		// are excluded from the mobility area.
		evalinfo.mobilityArea[Us] = ~(b | bitboard.getFiguresBitboardByColourAndType(Us, Constants.TYPE_KING) | bitboard.getFiguresBitboardByColourAndType(Us, Constants.TYPE_QUEEN) | pawns.pawnAttacks[Them]);
		
		// Initialise attackedBy bitboards for kings and pawns
		evalinfo.attackedBy[Us][Constants.TYPE_KING] = attacks_from(getKingSquareID(bitboard, Us), Constants.TYPE_KING);
		evalinfo.attackedBy[Us][Constants.TYPE_PAWN] = pawns.pawnAttacks[Us];
		evalinfo.attackedBy[Us][Constants.TYPE_ALL] = evalinfo.attackedBy[Us][Constants.TYPE_KING] | evalinfo.attackedBy[Us][Constants.TYPE_PAWN];
		evalinfo.attackedBy2[Us] = evalinfo.attackedBy[Us][Constants.TYPE_KING] & evalinfo.attackedBy[Us][Constants.TYPE_PAWN];
		
		evalinfo.kingRing[Us] = evalinfo.kingAttackersCount[Them] = 0;
		
		// Init our king safety tables only if we are going to use them
		//TODO if (pos.non_pawn_material(Them) >= Value.RookValueMg.getValue() + Value.KnightValueMg)
		//{
			evalinfo.kingRing[Us] = evalinfo.attackedBy[Us][Constants.TYPE_KING];
			if (relative_rank_bySquare(Us, getKingSquareID(bitboard, Us)) == Rank1)
			{
				evalinfo.kingRing[Us] |= shiftBB(evalinfo.kingRing[Us], Direction_Up);
			}
		
			if (file_of(getKingSquareID(bitboard, Us)) == FileH)
			{
				evalinfo.kingRing[Us] |= shiftBB(evalinfo.kingRing[Us], Direction_WEST);
			}
		
			else if (file_of(getKingSquareID(bitboard, Us)) == FileA)
			{
				evalinfo.kingRing[Us] |= shiftBB(evalinfo.kingRing[Us], Direction_EAST);
			}
		
			evalinfo.kingAttackersCount[Them] = Long.bitCount(evalinfo.kingRing[Us] & pawns.pawnAttacks[Them]);
			evalinfo.kingAttacksCount[Them] = evalinfo.kingAttackersWeight[Them] = 0;
		//}
	}
	
	
	// Evaluation::pieces() scores pieces of a given color and type
	private int pieces(int Us, int pieceType)
	{

	  final Color Them = (Us == Color.WHITE ? Color.BLACK : Color.WHITE);
	  final Direction Down = (Us == Color.WHITE ? Direction.SOUTH : Direction.NORTH);
	  final long OutpostRanks = (Us == Color.WHITE ? GlobalMembers.Rank4BB | GlobalMembers.Rank5BB | GlobalMembers.Rank6BB : GlobalMembers.Rank5BB | GlobalMembers.Rank4BB | GlobalMembers.Rank3BB);
//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged:
	  Square * pl = pos.<Pt>squares(Us);

	  uint64_t b = new uint64_t();
	  uint64_t bb = new uint64_t();
	  Square s;
	  Score score = Score.SCORE_ZERO;

	  attackedBy[Us][Pt] = 0;

	  while ((s = *pl++) != Square.SQ_NONE)
	  {
		  // Find attacked squares, including x-ray attacks for bishops and rooks
		  b = Pt == PieceType.BISHOP ? GlobalMembers.<PieceType.BISHOP.getValue()>attacks_bb(s, pos.pieces() ^ pos.pieces(PieceType.QUEEN)) : Pt == PieceType.ROOK ? GlobalMembers.< PieceType.ROOK.getValue()>attacks_bb(s, pos.pieces() ^ pos.pieces(PieceType.QUEEN) ^ pos.pieces(Us, PieceType.ROOK)) : pos.<Pt>attacks_from(s);

		  if (pos.blockers_for_king(Us) & s != null)
		  {
			  b &= LineBB[pos.<PieceType.KING.getValue().getValue()>square(Us)][s.getValue()];
		  }

		  attackedBy2[Us] |= attackedBy[Us][PieceType.ALL_PIECES.getValue()] & b;
		  attackedBy[Us][Pt] |= b;
		  attackedBy[Us][PieceType.ALL_PIECES.getValue()] |= b;

		  if (b & kingRing[Them.getValue()] != null)
		  {
			  kingAttackersCount[Us]++;
			  kingAttackersWeight[Us] += GlobalMembers.KingAttackWeights[Pt];
			  kingAttacksCount[Us] += GlobalMembers.popcount(b & attackedBy[Them.getValue()][PieceType.KING.getValue()]);
		  }

		  int mob = GlobalMembers.popcount(b & mobilityArea[Us]);

		  mobility[Us] += GlobalMembers.MobilityBonus[Pt - 2][mob];

		  if (Pt == PieceType.BISHOP || Pt == PieceType.KNIGHT)
		  {
			  // Bonus if piece is on an outpost square or can reach one
//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
//ORIGINAL LINE: bb = OutpostRanks & ~pe->pawn_attacks_span(Them);
			  bb.copyFrom(OutpostRanks & ~pe.pawn_attacks_span(Them));
			  if (bb & s != null)
			  {
				  score += GlobalMembers.Outpost[Pt == PieceType.BISHOP][(boolean)(attackedBy[Us][PieceType.PAWN.getValue()] & s)] * 2;
			  }

			  else if (bb &= b & ~pos.pieces(Us))
			  {
				  score += GlobalMembers.Outpost[Pt == PieceType.BISHOP][(boolean)(attackedBy[Us][PieceType.PAWN.getValue()] & bb)];
			  }

			  // Knight and Bishop bonus for being right behind a pawn
			  if (GlobalMembers.<Down>shift(pos.pieces(PieceType.PAWN)) & s != null)
			  {
				  score += GlobalMembers.MinorBehindPawn;
			  }

			  // Penalty if the piece is far from the king
			  score -= GlobalMembers.KingProtector * GlobalMembers.distance(s, pos.<PieceType.KING.getValue()>square(Us));

			  if (Pt == PieceType.BISHOP)
			  {
				  // Penalty according to number of pawns on the same color square as the
				  // bishop, bigger when the center files are blocked with pawns.
				  uint64_t blocked = pos.pieces(Us, PieceType.PAWN) & GlobalMembers.<Down>shift(pos.pieces());

				  score -= GlobalMembers.BishopPawns * pe.pawns_on_same_color_squares(Us, s) * (1 + GlobalMembers.popcount(blocked & GlobalMembers.CenterFiles));

				  // Bonus for bishop on a long diagonal which can "see" both center squares
				  if (GlobalMembers.more_than_one(GlobalMembers.<PieceType.BISHOP.getValue()>attacks_bb(s, pos.pieces(PieceType.PAWN)) & GlobalMembers.Center))
				  {
					  score += GlobalMembers.LongDiagonalBishop;
				  }
			  }

			  // An important Chess960 pattern: A cornered bishop blocked by a friendly
			  // pawn diagonally in front of it is a very serious problem, especially
			  // when that pawn is also blocked.
			  if (Pt == PieceType.BISHOP && pos.is_chess960() && (s == GlobalMembers.relative_square(Us, Square.SQ_A1) || s == GlobalMembers.relative_square(Us, Square.SQ_H1)))
			  {
				  Direction d = GlobalMembers.pawn_push(Us) + (GlobalMembers.file_of(s) == File.FILE_A ? Direction.EAST : Direction.WEST);
				  if (pos.piece_on(s + d) == GlobalMembers.make_piece(Us, PieceType.PAWN))
				  {
					  score -= !pos.empty(s + d + GlobalMembers.pawn_push(Us)) ? GlobalMembers.CorneredBishop * 4 : pos.piece_on(s + d + d) == GlobalMembers.make_piece(Us, PieceType.PAWN) ? GlobalMembers.CorneredBishop * 2 : GlobalMembers.CorneredBishop;
				  }
			  }
		  }

		  if (Pt == PieceType.ROOK)
		  {
			  // Bonus for aligning rook with enemy pawns on the same rank/file
			  if (GlobalMembers.relative_rank(Us, s) >= Rank.RANK_5.getValue())
			  {
				  score += GlobalMembers.RookOnPawn * GlobalMembers.popcount(pos.pieces(Them, PieceType.PAWN) & PseudoAttacks[PieceType.ROOK.getValue()][s.getValue()]);
			  }

			  // Bonus for rook on an open or semi-open file
			  if (pe.semiopen_file(Us, GlobalMembers.file_of(s)) != 0)
			  {
				  score += GlobalMembers.RookOnFile[(boolean)pe.semiopen_file(Them, GlobalMembers.file_of(s))];
			  }

			  // Penalty when trapped by the king, even more if the king cannot castle
			  else if (mob <= 3)
			  {
				  File kf = GlobalMembers.file_of(pos.<PieceType.KING.getValue()>square(Us));
				  if ((kf.getValue() < File.FILE_E.getValue()) == (GlobalMembers.file_of(s) < kf.getValue()))
				  {
					  score -= (GlobalMembers.TrappedRook - GlobalMembers.make_score(mob * 22, 0)) * (1 + !pos.can_castle(Us));
				  }
			  }
		  }

		  if (Pt == PieceType.QUEEN)
		  {
			  // Penalty if any relative pin or discovered attack against the queen
			  uint64_t queenPinners = new uint64_t();
			  if (pos.slider_blockers(pos.pieces(Them, PieceType.ROOK, PieceType.BISHOP), s, queenPinners) != null)
			  {
				  score -= GlobalMembers.WeakQueen;
			  }
		  }
	  }
	  if (T)
	  {
		  Trace.GlobalMembers.add(Pt, Us, score);
	  }

	  return score;
	}
	
	
	private long attacks_from(int squareID, int pieceType) {
		
		if (pieceType == Constants.TYPE_PAWN){
			throw new IllegalStateException();
		}
		
		return pieceType == Constants.TYPE_BISHOP || pieceType == Constants.TYPE_ROOK ? attacks_bb(pieceType, squareID, ~bitboard.getFreeBitboard())
				: pieceType == Constants.TYPE_QUEEN ? attacks_from(squareID, Constants.TYPE_ROOK) | attacks_from(squareID, Constants.TYPE_BISHOP)
				: PseudoAttacks[pieceType][squareID];
	}
	
	
	private static final int getKingSquareID(IBitBoard bitboard, int Us) {
		return bitboard.getPiecesLists().getPieces(Us == Constants.COLOUR_WHITE ? Constants.PID_W_KING : Constants.PID_B_KING).getData()[0];
	}
	
	
	public static final int make_score(int mg, int eg) {
		//return (eg << 16) + mg;
		return (eg + mg) / 2;
	}
	
	
	protected static class EvalInfo {
		
		
	    long[] mobilityArea = new long[Constants.COLOUR_BLACK + 1];

	    // attackedBy[color][piece type] is a bitboard representing all squares
	    // attacked by a given color and piece type. Special "piece types" which
	    // is also calculated is ALL_PIECES.
	    long[][] attackedBy = new long[Constants.COLOUR_BLACK + 1][Constants.TYPE_ALL + 1];

	    // attackedBy2[color] are the squares attacked by 2 pieces of a given color,
	    // possibly via x-ray or by one pawn and one piece. Diagonal x-ray through
	    // pawn or squares attacked by 2 pawns are not explicitly added.
	    long[] attackedBy2 = new long[Constants.COLOUR_BLACK + 1];

	    // kingRing[color] are the squares adjacent to the king, plus (only for a
	    // king on its first rank) the squares two ranks in front. For instance,
	    // if black's king is on g8, kingRing[BLACK] is f8, h8, f7, g7, h7, f6, g6
	    // and h6. It is set to 0 when king safety evaluation is skipped.
	    long[] kingRing = new long[Constants.COLOUR_BLACK + 1];

	    // kingAttackersCount[color] is the number of pieces of the given color
	    // which attack a square in the kingRing of the enemy king.
	    int[] kingAttackersCount = new int[Constants.COLOUR_BLACK + 1];

	    // kingAttackersWeight[color] is the sum of the "weights" of the pieces of
	    // the given color which attack a square in the kingRing of the enemy king.
	    // The weights of the individual piece types are given by the elements in
	    // the KingAttackWeights array.
	    int[] kingAttackersWeight = new int[Constants.COLOUR_BLACK + 1];

	    // kingAttacksCount[color] is the number of attacks by the given color to
	    // squares directly adjacent to the enemy king. Pieces which attack more
	    // than one square are counted multiple times. For instance, if there is
	    // a white knight on g5 and black's king is on g8, this white knight adds 2
	    // to kingAttacksCount[WHITE].
	    int[] kingAttacksCount = new int[Constants.COLOUR_BLACK + 1];
	    
	    
		public int eval_o_part1;
		public int eval_e_part1;
		public int eval_o_part2;
		public int eval_e_part2;
		
		
		public void clearEvals1() {
			eval_o_part1 = 0;
			eval_e_part1 = 0;
		}
		
		
		public void clearEvals2() {
			eval_o_part2 = 0;
			eval_e_part2 = 0;
		}
	}
	
	
	protected static class Pawns {
		
		
		//public int[] scores = new int[Constants.COLOUR_BLACK + 1];
		public long[] passedPawns = new long[Constants.COLOUR_BLACK + 1];
		public long[] pawnAttacks = new long[Constants.COLOUR_BLACK + 1];
		public long[] pawnAttacksSpan = new long[Constants.COLOUR_BLACK + 1];
		public int[] kingSquares = new int[Constants.COLOUR_BLACK + 1];
		//public int[] kingSafety = new int[Constants.COLOUR_BLACK + 1];
		public int[] weakUnopposed = new int[Constants.COLOUR_BLACK + 1];
		//public int[] castlingRights = new int[Constants.COLOUR_BLACK + 1];
		public int[] semiopenFiles = new int[Constants.COLOUR_BLACK + 1];
		public int[][] pawnsOnSquares = new int[Constants.COLOUR_BLACK + 1][Constants.COLOUR_BLACK + 1]; // [color][light/dark squares]
		//public int asymmetry;
		//public int openFiles;
		  
		  
		public int evaluate(IBitBoard bitboard, int Us, PiecesList Us_pawns_list) {
			
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
			int score = 0;
			
			long ourPawns = bitboard.getFiguresBitboardByColourAndType(Us, Constants.TYPE_PAWN);
			long theirPawns = bitboard.getFiguresBitboardByColourAndType(Them, Constants.TYPE_PAWN);

			passedPawns[Us] = pawnAttacksSpan[Us] = weakUnopposed[Us] = 0;
			semiopenFiles[Us] = 0xFF;
			kingSquares[Us] = -1;
			pawnAttacks[Us] = pawn_attacks_bb(ourPawns, Us);
			pawnsOnSquares[Us][Constants.COLOUR_BLACK] = Long.bitCount(ourPawns & DarkSquares);
			pawnsOnSquares[Us][Constants.COLOUR_WHITE] = Long.bitCount(ourPawns & LightSquares);
			
            int pawns_count = Us_pawns_list.getDataSize();
            if (pawns_count > 0) {
	            int[] pawns_fields = Us_pawns_list.getData();
	            for (int i=0; i<pawns_count; i++) {
	            	
	            	int squareID = pawns_fields[i];
	            	
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
					doubled = ourPawns & (squareID - Direction_Up);
					neighbours = ourPawns & adjacent_files_bb(fileID);
					phalanx = neighbours & rank_bb(squareID);
					supported = neighbours & rank_bb(squareID - Direction_Up);
	
					// A pawn is backward when it is behind all pawns of the same color
					// on the adjacent files and cannot be safely advanced.
					backward = (ourPawns & pawn_attack_span(Them, squareID + Direction_Up)) == 0 && (stoppers & (leverPush | (squareID + Direction_Up))) != 0;
					
					// Passed pawns will be properly scored in evaluation because we need
					// full attack info to evaluate them. Include also not passed pawns
					// which could become passed after one or two pawn pushes when are
					// not attacked more times than defended.
					if ((stoppers ^ lever ^ leverPush) == 0 && Long.bitCount(supported) >= Long.bitCount(lever) - 1 && Long.bitCount(phalanx) >= Long.bitCount(leverPush)) {
						
						passedPawns[Us] |= SquareBB[squareID];
						
					} else if (stoppers == SquareBB[squareID + Direction_Up] && relative_rank_bySquare(Us, squareID) >= Rank5) {
						
						long b = shiftBB(supported, Direction_Up) & ~theirPawns;
						while (b != 0) {
							//TODO: CHECK pop_lsb if (!more_than_one(theirPawns & PawnAttacks[Us][pop_lsb(b).getValue()]))
							if (!more_than_one(theirPawns & PawnAttacks[Us][Long.numberOfTrailingZeros(b)])) {
							//if (!more_than_one(theirPawns & PawnAttacks[Us][Long.numberOfLeadingZeros(b)])) {
								passedPawns[Us] |= SquareBB[squareID];
							}
							b &= b - 1;
						}
					}
					
					// Score this pawn
					if ((supported | phalanx) != 0) {
						
						score += Connected[opposed ? 1 : 0][phalanx == 0 ? 0 : 1][Long.bitCount(supported)][relative_rank_bySquare(Us, squareID)];
						
					} else if (neighbours == 0) {
						
						score -= Isolated;
						weakUnopposed[Us] += (!opposed ? 1 : 0);
						
					} else if (backward) {
						
						score -= Backward;
						weakUnopposed[Us] += (!opposed ? 1 : 0);
					}
	
					if (doubled != 0 && supported == 0) {
						score -= Doubled;
					}
	            }
			}

			return score;
		}
		
		
		/// Entry::do_king_safety() calculates a bonus for king safety. It is called only
		/// when king square changes, which is about 20% of total king_safety() calls.
		public final int do_king_safety(IBitBoard bitboard, int Us) {
			
			int squareID_ksq = getKingSquareID(bitboard, Us);
			kingSquares[Us] = squareID_ksq;
			//castlingRights[Us] = bitboard.hasRightsToKingCastle(Us) || bitboard.hasRightsToQueenCastle(Us)//pos.can_castle(Us);
			int minKingPawnDistance = 0;
			
			long pawns = bitboard.getFiguresBitboardByColourAndType(Us, Constants.TYPE_PAWN);
			if (pawns != 0) {
				while ((DistanceRingBB[squareID_ksq][minKingPawnDistance] & pawns) == 0) {
					minKingPawnDistance++;
					//System.out.println(minKingPawnDistance);
				}
			}
			
			int bonus = evaluate_shelter(bitboard, Us, squareID_ksq);
			
			// If we can castle use the bonus after the castling if it is bigger
			if (bitboard.hasRightsToKingCastle(Us)) {
				bonus = Math.max(bonus, evaluate_shelter(bitboard, Us, relative_square(Us, Fields.G1_ID)));
			}
			
			if (bitboard.hasRightsToQueenCastle(Us)) {
				bonus = Math.max(bonus, evaluate_shelter(bitboard, Us, relative_square(Us, Fields.C1_ID)));
			}
			
			return make_score(bonus, -16 * minKingPawnDistance);
		}
		
		
		/// Entry::evaluate_shelter() calculates the shelter bonus and the storm
		/// penalty for a king, looking at the king file and the two closest files.
		private final int evaluate_shelter(IBitBoard bitboard, int Us, int squareID_ksq) {
		
			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			final int Direction_Down = (Us == Constants.COLOUR_WHITE ? Direction_SOUTH : Direction_NORTH);
			final long BlockRanks = (Us == Constants.COLOUR_WHITE ? Rank1BB | Rank2BB : Rank8BB | Rank7BB);
		
			long b = (bitboard.getFiguresBitboardByColourAndType(Us, Constants.TYPE_PAWN) | bitboard.getFiguresBitboardByColourAndType(Them, Constants.TYPE_PAWN))
						& ~forward_ranks_bb(Them, squareID_ksq);//pos.pieces(PieceType.PAWN) & ~forward_ranks_bb(Them, squareID_ksq);
			long ourPawns = b & bitboard.getFiguresBitboardByColourAndType(Us, Constants.TYPE_PAWN);
			long theirPawns = b & bitboard.getFiguresBitboardByColourAndType(Them, Constants.TYPE_PAWN);
		
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
		
		
		public int passed(IBitBoard bitboard, int Us) {

			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
			int squareID_ksq = getKingSquareID(bitboard, Us);
			
			long b;
			long bb;
			long squaresToQueen;
			long defendedSquares;
			long unsafeSquares;
			int score = 0;
			
			b = passedPawns[Us];

			while (b != 0) {
				
				int squareID = Long.numberOfTrailingZeros(b);
				
				if (is_ok(squareID + Direction_Up)) {
					if ((bitboard.getFiguresBitboardByColourAndType(Them, Constants.TYPE_PAWN) & forward_file_bb(Us, squareID + Direction_Up)) != 0) {
						throw new IllegalStateException();
					}
				}
            	int rankID_check = rank_of(squareID);
            	if (rankID_check == 0 || rankID_check == 7) {
            		throw new IllegalStateException();
            	}
            	
				int rankID = relative_rank_bySquare(Us, squareID);
				
				int bonus = PassedRank[rankID];

				if (rankID > Rank3)
				{
					int w = (rankID - 2) * (rankID - 2) + 2;
					int blockSq = squareID + Direction_Up;

					// Adjust bonus based on the king's proximity
					bonus += make_score(0, (king_proximity(Them, blockSq, squareID_ksq) * 5 - king_proximity(Us, blockSq, squareID_ksq) * 2) * w);

					// If blockSq is not the queening square then consider also a second push
					if (rankID != Rank7)
					{
						bonus -= make_score(0, king_proximity(Us, blockSq + Direction_Up, squareID_ksq) * w);
					}

					// If the pawn is free to advance, then increase the bonus
					if (bitboard.getFigureID(blockSq) == Constants.PID_NONE) {//pos.empty(blockSq))
						
						// If there is a rook or queen attacking/defending the pawn from behind,
						// consider all the squaresToQueen. Otherwise consider only the squares
						// in the pawn's path attacked or occupied by the enemy.
						defendedSquares = unsafeSquares = squaresToQueen = forward_file_bb(Us, squareID);

						//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
						//ORIGINAL LINE: bb = forward_file_bb(Them, s) & pos.pieces(ROOK, QUEEN) & pos.attacks_from<ROOK>(s);
						
						/*TODO 
						bb.copyFrom(GlobalMembers.forward_file_bb(Them, squareID) & pos.pieces(PieceType.ROOK, PieceType.QUEEN) & pos.<PieceType.ROOK.getValue()>attacks_from(squareID));

						if ((pos.pieces(Us) & bb) == null)
						{
							defendedSquares &= attackedBy[Us][PieceType.ALL_PIECES.getValue()];
						}

						if ((pos.pieces(Them) & bb) == null)
						{
							unsafeSquares &= attackedBy[Them.getValue()][PieceType.ALL_PIECES.getValue()] | pos.pieces(Them);
						}

						// If there aren't any enemy attacks, assign a big bonus. Otherwise
						// assign a smaller bonus if the block square isn't attacked.
						int k = unsafeSquares == null ? 20 : (unsafeSquares & blockSq) == null ? 9 : 0;

						// If the path to the queen is fully defended, assign a big bonus.
						// Otherwise assign a smaller bonus if the block square is defended.
						if (defendedSquares == squaresToQueen)
						{
							k += 6;
						}
	
						else if (defendedSquares & blockSq)
						{
							k += 4;
						}
	
						bonus += make_score(k * w, k * w);
						*/
					}
				} // rank > RANK_3

				// Scale down bonus for candidate passers which need more than one
				// pawn push to become passed, or have a pawn in front of them.
				/*TODO
				if (!pos.pawn_passed(Us, squareID + Direction_Up) || (pos.pieces(PieceType.PAWN) & forward_file_bb(Us, squareID)) != 0) {
					bonus = bonus / 2;
				}*/

				score += bonus + PassedFile[file_of(squareID)];
				
				b &= b - 1;
			}

			return score;
		}
		
		
		private static final int king_proximity(int colour, int squareID, int kingSquareID) {
			return Math.min(distance(kingSquareID, squareID), 5);
		};
	}
}

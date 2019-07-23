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
	
	
	private IBitBoard bitboard;
	private final EvalInfo evalinfo;
	
	
	public Evaluator(IBitBoard _bitboard) {
		bitboard = _bitboard;
		evalinfo = new EvalInfo();
	}
	
	
	protected double calculateScore() {
		int eval = 0;
		
		evalinfo.clearEvals1();
		calculateMaterialScore();
		
		eval = bitboard.getMaterialFactor().interpolateByFactor(
				bitboard.getBaseEvaluation().getPST_o() + evalinfo.eval_o_part1,
				bitboard.getBaseEvaluation().getPST_e() + evalinfo.eval_e_part1
				);
		
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
	
	
	protected static class EvalInfo {
		
		
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
		
		
		public int[] scores = new int[Constants.COLOUR_BLACK + 1];
		public long[] passedPawns = new long[Constants.COLOUR_BLACK + 1];
		public long[] pawnAttacks = new long[Constants.COLOUR_BLACK + 1];
		public long[] pawnAttacksSpan = new long[Constants.COLOUR_BLACK + 1];
		public int[] kingSquares = new int[Constants.COLOUR_BLACK + 1];
		public int[] kingSafety = new int[Constants.COLOUR_BLACK + 1];
		public int[] weakUnopposed = new int[Constants.COLOUR_BLACK + 1];
		public int[] castlingRights = new int[Constants.COLOUR_BLACK + 1];
		public int[] semiopenFiles = new int[Constants.COLOUR_BLACK + 1];
		public int[][] pawnsOnSquares = new int[Constants.COLOUR_BLACK + 1][Constants.COLOUR_BLACK + 1]; // [color][light/dark squares]
		public int asymmetry;
		public int openFiles;
		  
		  
		public int evaluate(IBitBoard bitboard, int Us, PiecesList Us_pawns_list) {

			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			final int Up_direction = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);

			//long b;
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
			
			//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged:
			//Square * pl = pos.<PieceType.PAWN.getValue()>squares(Us);
            int pawns_count = Us_pawns_list.getDataSize();
            if (pawns_count > 0) {
	            int[] pawns_fields = Us_pawns_list.getData();
	            for (int i=0; i<pawns_count; i++) {
	            	
	            	int squareID = pawns_fields[i];
	            	
	            	int fileID = file_of(squareID);
	            	
	            	
					semiopenFiles[Us] &= ~(1 << fileID);
					pawnAttacksSpan[Us] |= pawn_attack_span(Us, squareID);
					
					// Flag the pawn
					opposed = (theirPawns & forward_file_bb(Us, squareID)) != 0;
					stoppers = theirPawns & passed_pawn_mask(Us, squareID);
					lever = (theirPawns & PawnAttacks[Us][squareID]);
					leverPush = theirPawns & PawnAttacks[Us][squareID + Up_direction];
					doubled = ourPawns & (squareID - Up_direction);
					neighbours = ourPawns & adjacent_files_bb(fileID);
					phalanx = neighbours & rank_bb(squareID);
					supported = neighbours & rank_bb(squareID - Up_direction);
	
					// A pawn is backward when it is behind all pawns of the same color
					// on the adjacent files and cannot be safely advanced.
					backward = (ourPawns & pawn_attack_span(Them, squareID + Up_direction)) == 0 && (stoppers & (leverPush | (squareID + Up_direction))) != 0;
					
					// Passed pawns will be properly scored in evaluation because we need
					// full attack info to evaluate them. Include also not passed pawns
					// which could become passed after one or two pawn pushes when are
					// not attacked more times than defended.
					if ((stoppers ^ lever ^ leverPush) == 0 && Long.bitCount(supported) >= (Long.bitCount(lever) - 1) && Long.bitCount(phalanx) >= Long.bitCount(leverPush))
					{
						passedPawns[Us] |= squareID;
						
					} else if (stoppers == SquareBB[squareID + Up_direction] && relative_rank_bySquare(Us, squareID) >= Rank5) {
						
						long b = shiftBB(supported, Up_direction) & ~theirPawns;
						while (b != 0)
						{
							if (!more_than_one(theirPawns & PawnAttacks[Us][pop_lsb(b).getValue()]))
							{
								passedPawns[Us] |= squareID;
							}
						}
					}
					
					// Score this pawn
					if ((supported | phalanx) != 0)
					{
						score += Connected[opposed][(boolean)phalanx][popcount(new uint64_t(supported))][relative_rank(Us, s).getValue()];
					}
	
					else if (!neighbours)
					{
						score -= Isolated, e.weakUnopposed[Us] += !opposed;
					}
	
					else if (backward)
					{
						score -= Backward, e.weakUnopposed[Us] += !opposed;
					}
	
					if (doubled != null && supported == null)
					{
						score -= Doubled;
					}
					*/
	            }
			}

			return score;
		}
	}
}

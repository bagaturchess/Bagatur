/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
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
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.bitboard.impl.eval.pawns.model.pst;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.pawns.model.ModelBuilder;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class PSTGenBishops extends PSTGen {

	
	public static int genAttacks(int colour, int fromFieldID,
			long opPawnsAttacks,
			int opColour, long myKing, long opKing, long myPawns, long opPawns,
			PawnsModel model, IBitBoard bitboard) {
		
		long myAll = myKing | myPawns;
		long opAll = opKing | opPawns;


		int attacks_all = 0;
		int safe_attacks = 0;
		int attack_undefended_pawns = 0;
		int attack_king = 0;
		
		//final int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fromFieldID];
		//final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fromFieldID];
		final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fromFieldID];
		final int[][] dirFieldIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromFieldID];
		final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int dir=0; dir<size; dir++) {
			int dirID = validDirIDs[dir];
			long[] dirBitboards = dirs[dirID];
			
			for (int seq=0; seq<dirBitboards.length; seq++) {
				int toFieldID = dirFieldIDs[dirID][seq];
				long toBitboard = dirs[dirID][seq];
				
				if ((toBitboard & myAll) != 0L) {
					break;
				} else if ((toBitboard & opAll) != 0L) {
					if ((toBitboard & opKing) != 0L) {
						attack_king++;
					} else if ((toBitboard & opPawns) != 0L) {
						if ((toBitboard & opPawnsAttacks) != 0L) {
							attacks_all++;
						} else {
							attack_undefended_pawns++;
						}
					} else {
						throw new IllegalStateException();
					}
					break;
				} else {
					if ((toBitboard & opPawnsAttacks) != 0L) {
						attacks_all++;
					} else {
						safe_attacks++;
					}
				}
			}
		}
		
		int score = 0;
		
		if (safe_attacks <= 1) {
			int[] trapped = colour == Figures.COLOUR_WHITE ? W_TRAPPED_MINOR : B_TRAPPED_MINOR;
			score = trapped[fromFieldID] / (safe_attacks + 1);
		} else {
			score += BONUS_ATTACK * attacks_all;
			score += BONUS_ATTACK_SAFE * safe_attacks;
			score += BONUS_ATTACK_UNDEFENDED_PAWN * attack_undefended_pawns;
		}
		
		return score;
	}
	
	public static void fillPST(int[] pst, int colour, PawnsModel model, IBitBoard bitboard) {
		
		int opColour = Figures.OPPONENT_COLOUR[colour];
		long myKing = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_KING);
		long opKing = bitboard.getFiguresBitboardByColourAndType(opColour, Figures.TYPE_KING);
		long myPawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
		long opPawns = bitboard.getFiguresBitboardByColourAndType(opColour, Figures.TYPE_PAWN);
		long all = myKing | opKing | myPawns | opPawns;
			
		for (int fromFieldID=0; fromFieldID<64; fromFieldID++) {
			
			long fromBitboard = Figures.ALL_ORDERED_A1H1[fromFieldID];
			
			if ((fromBitboard & all) != 0L) {
				pst[fromFieldID] = 0;
				continue;
			}
			
			long opPawnsAttacks = colour == Figures.COLOUR_WHITE ? model.getBattacks() : model.getWattacks();
			if ((opPawnsAttacks & fromBitboard) != 0L) {
				pst[fromFieldID] = PENALTY_ON_ATTACKED_SQUARE;
			} else {
				pst[fromFieldID] = genAttacks(colour, fromFieldID,
						opPawnsAttacks,
						opColour, myKing, opKing, myPawns, opPawns,
						model, bitboard);
			}
		}
	}
	
	public static void main(String[] args) {
			
			//BoardWithAttacks bitBoard = new BoardWithAttacks();
			//IBitBoard bitboard = new Board();
			//IBitBoard bitboard = new Board("1r3r2/4q1kp/b1pp2p1/5p2/pPn1N3/6P1/P3PPBP/2QRR1K1 w - - bm Nxd6");
			//IBitBoard bitboard = new Board("4k3/pppppppp/8/8/8/8/PPPPPPPP/4K3 w -");
			//IBitBoard bitboard = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - bm Rxb2");
			//IBitBoard bitboard  = new Board("rn1b2rk/1pp3p1/qp1p2R1/5Q2/3RN2P/1PP5/3PbP2/4K3 w - -");
			
			//IBitBoard bitboard  = new Board("nbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQ c6 0 2"); //en passant
			//IBitBoard bitboard  = new Board("8/8/8/8/bB1N4/Np2p2n/p2pP3/k2K4 w - "); //M9
			//IBitBoard bitboard  = new Board("4b3/5kpp/8/1p3P2/pr2B3/4KP2/3R2PP/8 w - - 2 42");
			//IBitBoard bitboard  = new Board("1r3rk1/p1qn1ppp/4p3/1N1p4/1P1B4/P2Q4/2P2PPb/1R2R2K b - - 4 23"); //Move to b7 instead of c4 or f4
			//IBitBoard bitboard  = new Board("1rq5/Qpp5/p1p1bpk1/4p2p/8/1PP1N1PP/2P3P1/5RK1 w - - 9 40"); //Trapped Queen, 1 move to trap
			//IBitBoard bitboard  = new Board("1rq5/Q1p5/ppp1bpk1/4p2p/8/1PP1N1PP/2P3P1/3R2K1 w - - 0 41"); //Trapped Queen, in the trap
			IBitBoard bitboard  = new Board("r1bq1r2/pp4k1/4p2p/3pPp1Q/3N1R1P/2PB4/6P1/6K1 w - - bm Rg4+"); //From WFC
			
			//bitboard.setAttacksSupport(false, false);
			//BoardUtils.playGame(bitboard, "c3-c4, f6-f5, c2-c3, e5-e4, f1-f4, c8-h8, f4-f1, h8-d8");
			//BoardUtils.playGame(bitboard, "c3-c4, b7-b6, g1-h2, b8-a8");
			System.out.println(bitboard);
			
			int[] pst = new int[64];
			fillPST(pst, Figures.COLOUR_WHITE, ModelBuilder.build(bitboard), bitboard);
			Utils.reverseSpecial(pst);
			
			for (int i=0; i<64; i++) {
				//int count = genAttacks(Figures.COLOUR_WHITE, i, ModelBuilder.build(bitboard), bitboard);
				if (i % 8 == 0) {
					System.out.println("");
				}
				System.out.print(pst[i] + "	");
			}
	}
}

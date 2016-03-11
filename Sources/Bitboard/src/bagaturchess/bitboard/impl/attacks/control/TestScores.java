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
package bagaturchess.bitboard.impl.attacks.control;

import java.util.Comparator;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movegen.Move;


public class TestScores {
	
	public static void dumpAll(IBitBoard board, int colour) {
		
		long[][] all = new long[128][Move.MOVE_LONGS_COUNT];
		if (board.isInCheck(colour)) {
			throw new IllegalStateException("in check");
		}
		
		int scores_b = board.getFieldsAttacks().getScore_BeforeMove(colour);
		System.out.println("Starting scores " + scores_b);
				
		/*int count = board.genAllMoves(colour, all);
		for (int i=0; i<count; i++) {
			long[] move = all[i];
			
			//System.out.println("\r\n" + Move.moveToString(move) + " FORWARD\r\n");
			
			board.makeMoveForward(move);
			int scores_a = board.getFieldsAttacks().getScore_AfterMove(colour);
			
			//System.out.println("\r\n" + Move.moveToString(move) + " BACKWARD\r\n");
			
			board.makeMoveBackward(move);
			
			//System.out.println("scores_b=" + scores_b + ", scores_a=" + scores_a);
			int scoresDelta = scores_a - scores_b;
			move[23] = scoresDelta;
			
			if (scores_b != board.getFieldsAttacks().getScore_BeforeMove(colour)) {
				throw new IllegalStateException("scores_b=" + scores_b + " board.getFieldsAttacks().getScore(colour)=" + board.getFieldsAttacks().getScore_BeforeMove(colour));
			}
		}
		
		bubbleSort(0, count, all, new Comparator23History());
		
		for (int i=0; i<count; i++) {
			long[] move = all[i];
			System.out.println("" + Move.moveToString(move) + " -> " + move[23]);
		}*/
	}
	
	public static void bubbleSort(int from, int to, long[][] moves, Comparator comp) {
		
		for (int i = from; i < to; i++) {
			for (int j= i + 1; j < to; j++) {
				long[] i_move = moves[i];
				long[] j_move = moves[j];
				if (comp.compare(j_move, i_move) < 0) {
					moves[i] = j_move;
					moves[j] = i_move;
				}
			}
		}
	}
	
	public static class Comparator23History implements Comparator {
		
		public int compare(Object o1, Object o2) {
			return compare1((long[])o1, (long[])o2);
		}
		
		private int compare1(long[] move1, long[] move2) {

			final long eval1 = move1[23];
			final long eval2 = move2[23];
			
			if (eval1 > eval2) {
				return -1;
			}
				
			if (eval1 < eval2) {
				return 1;
			}
			
			return -1;
		}
	}
	
}

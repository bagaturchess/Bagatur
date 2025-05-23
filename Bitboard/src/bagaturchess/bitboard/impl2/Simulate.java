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
package bagaturchess.bitboard.impl2;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.common.GlobalConstants;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl1.internal.ChessBoardUtil;


/**
After 1 ply: ~20 moves

After 2 plies: ~400 positions

After 3 plies: ~8,902 positions

After 4 plies: ~197,281 positions

After 5 plies: ~4,865,609 positions

After 6 plies: ~119,060,324 positions

After 7 plies: ~3,195,901,860 positions

After 8 plies: ~84,998,978,956 positions

After 9 plies: ~2,439,530,234,167 positions
*/

public class Simulate {

	
	public static void main(String[] args) {

		ChessBoard board = ChessBoardBuilder.getNewCB(Constants.INITIAL_BOARD);
		
		ChessBoard board_copy = board.clone();
		
		
		IInternalMoveList[] lists = new IInternalMoveList[64];
		for (int i = 0; i < lists.length; i++) {
			
			lists[i] = new BaseMoveList();
		}
		
		SearchInfo info = new SearchInfo();
		
		long start_time = System.currentTimeMillis();
		
		simulate(board, 7, lists, info);
		
		System.out.println("Leafs: " + info.leafs);
		if (System.currentTimeMillis() - start_time > 1000) {
			
			System.out.println(info.nodes / ((System.currentTimeMillis() - start_time) / 1000) + " NPS");
		}
		
		if (!board.equals(board_copy)) {
			
			throw new IllegalStateException();
		}
		
		/*IInternalMoveList list = new BaseMoveList();
		
		long start_time = System.currentTimeMillis();
		
		int counter = 0;
		while (true) {
			
			list.reserved_clear();
			MoveGeneration.generateMoves(board, list);
			MoveGeneration.generateAttacks(board, list);
			
			for (int i = 0; i < list.reserved_getCurrentSize(); i++){
				
				int move = list.reserved_getMovesBuffer()[i];
				
				board.doMove(move);
				board.undoMove(move);
				counter++;
				
				System.out.println(counter);
				
				if (!board.equals(board_copy)) {
					
					throw new IllegalStateException();
				}
			}
			
			if (counter % 10000000 == 0 && System.currentTimeMillis() - start_time > 1000) {
				System.out.println(counter / ((System.currentTimeMillis() - start_time) / 1000));
			}
		}*/
	}
	
	
	private static final void simulate(ChessBoard board, int depth, IInternalMoveList[] lists, SearchInfo info) {
	
		info.nodes++;
		
		if (depth == 0) {
		
			info.leafs++;
			
			return;
		}
		
		IInternalMoveList list = lists[depth];
				
		list.reserved_clear();
		MoveGeneration.generateMoves(board, list);
		MoveGeneration.generateAttacks(board, list);
		
		for (int i = 0; i < list.reserved_getCurrentSize(); i++){
			
			int move = list.reserved_getMovesBuffer()[i];
			
			int attacked_type = MoveUtil.getAttackedPieceIndex(move);
			if (attacked_type == ChessConstants.KING) {
				
				continue;
			}
			
			board.doMove(move);
			
			simulate(board, depth - 1, lists, info);
			
			board.undoMove(move);
		}
	}
	
	
	public static void bubbleSort(int from, int to, int[] moves) {
		
		for (int i = from; i < to; i++) {
			boolean change = false;
			for (int j= i + 1; j < to; j++) {
				int i_move = moves[i];
				int j_move = moves[j];
				if (j_move > i_move) {
					moves[i] = j_move;
					moves[j] = i_move;
					change = true;
				}
			}
			if (!change) {
				return;
			}
		}
		
		//check(from, to, moves);
	}
	
	static class SearchInfo {
		public long leafs;
		public long nodes;
	}
}

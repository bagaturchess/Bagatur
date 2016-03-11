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
package bagaturchess.search.impl.utils;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;



public class Tactics {
	
	private IBitBoard bitboard;
	
	private IInternalMoveList myMoves;
	private IInternalMoveList opMoves;
	
	private int myWinMovesCount;
	private int opWinMovesCount;
	private int[] myWinMoves;
	private int[] opWinMoves;
	private int[] myWinMovesSee;
	private int[] opWinMovesSee;
	
	//private Set<Integer> myToFields;
	//private Set<Integer> opToFields;
	
	public Tactics(IBitBoard _bitboard) {
		bitboard = _bitboard;
		
		myMoves = new BaseMoveList();
		opMoves = new BaseMoveList();
		
		myWinMoves = new int[32];
		opWinMoves = new int[32];
		myWinMovesSee = new int[32];
		opWinMovesSee = new int[32];
		
		//myToFields = new HashSet<Integer>();
		///opToFields = new HashSet<Integer>();
	}
	
	public boolean silentButDeadly() {
		
		//if (true) return false; 
		
		if (bitboard.isInCheck()) {
			return false;
		}
		
		genWinCaps();
		
		if (opWinMovesCount <= 0) {
			return false;
		}
		
		/*if (opWinMovesCount >= 3) {
			return true;
		}*/
		
		/**
		 * TODO: Remove duplicated captures - to the same to field id
		 */
		
		Utils.bubbleSort(opWinMovesSee, opWinMoves, opWinMovesCount);
				
		if (myWinMovesCount > 0) {
			
			Utils.bubbleSort(myWinMovesSee, myWinMoves, myWinMovesCount);
			
			if (myWinMovesSee[0] >= opWinMovesSee[0]) {
				return false;
			}
		}
		
		//if (true) return false;
		
		if (opWinMovesCount <= 0 || myWinMovesCount < 0) {
			throw new IllegalStateException();
		}
		
		int opmove = opWinMoves[0];
		int opsee = opWinMovesSee[0];
		int bestEscapeSEE = -opsee;
		
		/*if (bestEscapeSEE < 0) {
			throw new IllegalStateException();
		}*/
		
		if (!MoveInt.isCapture(opmove) || MoveInt.isEnpassant(opmove)) {
			return true;
		}
		
		//if (MoveInt.isCapture(opmove) && !MoveInt.isEnpassant(opmove)) {
		int toFieldID = MoveInt.getToFieldID(opmove);
		
		myMoves.reserved_clear();
		bitboard.genAllMoves_ByFigureID(toFieldID, 0L, myMoves);
		
		int size = myMoves.reserved_getCurrentSize();
		int[] moves = myMoves.reserved_getMovesBuffer();
		
		for (int j=0; j<size; j++) {
			int cur_my_move = moves[j];
			int cur_move_see = bitboard.getSee().evalExchange(cur_my_move);
			if (cur_move_see >= bestEscapeSEE) {
				bestEscapeSEE = cur_move_see;
			}
		}
		//System.out.println("Move=" + MoveInt.moveToString(bestEscapeMove) + ", see=" + bestEscapeSEE);
		//}
		
		return bestEscapeSEE < 0;
	}
	
	private void genWinCaps() {
		
		{
			myWinMovesCount = 0;
			myMoves.reserved_clear();
			
			bitboard.genCapturePromotionMoves(myMoves);
			int size = myMoves.reserved_getCurrentSize();
			int[] moves = myMoves.reserved_getMovesBuffer();
			
			for (int i=0; i<size; i++) {
				int cur_my_move = moves[i];
				if (MoveInt.isCapture(cur_my_move)) {
					int cur_move_see = bitboard.getSee().evalExchange(cur_my_move);
					if (cur_move_see >= 0) {
						myWinMoves[myWinMovesCount] = cur_my_move;
						myWinMovesSee[myWinMovesCount] = cur_move_see;
						myWinMovesCount++;
					}
				}
			}
		}
		
		bitboard.makeNullMoveForward();
		{
			opWinMovesCount = 0;
			opMoves.reserved_clear();
			
			bitboard.genCapturePromotionMoves(opMoves);
			
			int opsize = opMoves.reserved_getCurrentSize();
			int[] opmoves = opMoves.reserved_getMovesBuffer();
			
			for (int i=0; i<opsize; i++) {
				int cur_op_move = opmoves[i];
				if (MoveInt.isCapture(cur_op_move)) {
					int cur_move_see = bitboard.getSee().evalExchange(cur_op_move);
					if (cur_move_see >= 0) {
						opWinMoves[opWinMovesCount] = cur_op_move;
						opWinMovesSee[opWinMovesCount] = cur_move_see;
						opWinMovesCount++;
					}
				}
			}
		}
		bitboard.makeNullMoveBackward();
	}
	
	private static final boolean contains(int from, int to, int[] arr, int el) {
		for (int i=from; i<to; i++) {
			if (arr[i] == el) return true;
		}
		return false;
	}
	
}

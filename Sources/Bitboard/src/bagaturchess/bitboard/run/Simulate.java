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
package bagaturchess.bitboard.run;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.common.GlobalConstants;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.cfg.BoardConfigImpl;
import bagaturchess.bitboard.impl.dummy.DummyBoard;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl1.Board3;
import bagaturchess.bitboard.impl1.Board3_Adapter;
import bagaturchess.bitboard.impl2.Board4_Adapter;

/**
After the 1st ply (White's move) -- 20 possible positions 
After the 2nd ply (Black's move) -- 400 possible positions 
After the 3rd ply (White's move) -- 8,000 possible positions 
After the 4th ply (Black's move) -- 160,000 possible positions 
After the 5th ply (White's move) -- 3,200,000 possible positions 
After the 6th ply (Black's move) -- 64,000,000 possible positions 
After the 7th ply (White's move) -- 1,280,000,000 possible positions 
After the 8th ply (Black's move) -- 25,600,000,000 possible positions 
*/

public class Simulate {
	
	
	private static long genMoves(Board bitBoard) {
		long count = 0;
		//long moves[][] = new long[EngineConstants.MAX_MOVES_ON_LEVEL][14];
		long moves[][] = new long[GlobalConstants.MAX_MOVES_ON_LEVEL][26];
		IInternalMoveList lists[] = new IInternalMoveList[GlobalConstants.MAX_MOVES_ON_LEVEL];
		for (int i=0;i<lists.length;i++) {
			lists[i] = new BaseMoveList();
		}
		
	  	long start = System.currentTimeMillis();
			for (int i=0; i<1000000; i++) {
				//count += bitBoard.genAllMoves(Figures.COLOUR_WHITE, moves, null);
				//count += bitBoard.genAllMoves(Figures.COLOUR_BLACK, moves, null);
			}
	  	long end = System.currentTimeMillis();
	  	
	  	System.out.println("After: " + bitBoard);
	  	
	  	System.out.printf("Gen moves: " + count
				+ ", Time " + (end-start) + "ms, Moves per second %f", (count/((end-start)/(double)1000)));
			
		return count;
	}
	
	
	private static void simulate(SearchInfo info, IBoard bitBoard, final int colour, final int depth, int branching) {
		
		//Create board
		/*final Board board = new Board();
		long whiteKingBitBoard = Long.parseLong("0000000000001000000000000000000000000000000000000000000000000000", 2);
		long blackKingBitBoard = Long.parseLong("0000000000001000000000000000000000000000000000000000000000000000", 2);
		board.white_king = whiteKingBitBoard;
		board.black_king = blackKingBitBoard;*/
		
		//Create moves holders
		//final long[][][] movesPerLevel = new long[40][GlobalConstants.MAX_MOVES_ON_LEVEL][Move.MOVE_LONGS_COUNT];
		IInternalMoveList lists[] = new IInternalMoveList[40];
		for (int i=0;i<lists.length;i++) {
			lists[i] = new BaseMoveList();
		}

		
		IBoard clone = (IBoard) bitBoard.clone();
		
		System.out.println("Before: " + bitBoard);
			
	  	long start = System.currentTimeMillis();
	  	simulate1(info, bitBoard, colour, lists, depth, branching);
	  	long end = System.currentTimeMillis();
		
	  	System.out.println("After: " + bitBoard);
	  	
	  	System.out.printf("Gen moves: " + info.nodes
				+ ", Time " + (end-start) + "ms, Moves per second %f", (info.nodes/((end-start)/(double)1000)));
	  	
	  	if (clone.equals(bitBoard)) {
	  		System.out.println("\r\nOK");
	  	} else {
	  		System.out.println("\r\nERROR");
	  	}
	}
	
	
	//private static long simulate1(String line, final Board board, final int colour, final long[][][] movesPerLevel, final int depth) {
	private static void simulate1(SearchInfo info, final IBoard board, final int colour, IInternalMoveList[] lists, final int depth, int branching) {
		
		info.nodes++;
		
		if (board.isInCheck()) return;
		
		if (board.isInCheck(Constants.COLOUR_OP[board.getColourToMove()])) {
			//King will be captured
		}
		
		if (depth != 0) {
		
			lists[depth].reserved_clear();
			board.genAllMoves(lists[depth]);

			int curCount = lists[depth].reserved_getCurrentSize();
			
			Utils.randomize(lists[depth].reserved_getMovesBuffer(), 0, curCount);
			bubbleSort(0, curCount, lists[depth].reserved_getMovesBuffer());
			
			int movenumber = (int) Math.min(branching, curCount);
			
			for (int i=0; i<movenumber; i++) {
				int move = lists[depth].reserved_getMovesBuffer()[i];
				
				//int ex = board.getSee().evalExchange(move);
				/*if (ex > 400) {
					System.out.println("pin");
				}*/
				//System.out.println("move=" + move[10]);
				
				//String newLine = line;
				//String newLine = line + ", " + Moves.moveToString(move);
				
				//if (board.isInCheck());
				
				int see = 0;
				if (MoveInt.isCaptureOrPromotion(move)) {
					see = board.getSee().evalExchange(move);
				}
				
				if (MoveInt.isCapture(move) && MoveInt.getCapturedFigureType(move) == Constants.TYPE_KING) {
					continue;
				}
				
				board.makeMoveForward(move);
				
				/*count += simulate1(newLine, board,
								Figures.OPPONENT_COLOUR[colour],
								movesPerLevel, depth - 1 );*/
				simulate1(info, board,
							Figures.OPPONENT_COLOUR[colour],
							lists, depth - 1, branching);
				
				board.makeMoveBackward(move);
			}
		} else {
			//board.getBaseEvaluation().getWhiteMaterial() - board.getBaseEvaluation().getBlackMaterial();
			//System.out.println(line);
		}
	}
	
	
	public static void main(String[] args) {
		
		IBoardConfig boardConfig = new BoardConfigImpl();
		
		//BoardWithAttacks bitBoard = new BoardWithAttacks();
		//String BOARD = "4k3/P7/8/8/8/8/7p/4K3 w - 0 0";
		//Board bitBoard = new Board(boardConfig);
		//IBoard bitBoard = new Board(boardConfig);
		//IBoard bitBoard = new Board1(boardConfig);
		//IBoard bitBoard = new Board2(boardConfig);
		IBoard bitBoard = new Board3_Adapter();
		//IBoard bitBoard = new Board4_Adapter();
		//IBoard bitBoard = new DummyBoard();
		//bitBoard.setAttacksSupport(false, false);
		
		SearchInfo info = new SearchInfo();
		
		//simulate(info, bitBoard, Figures.COLOUR_WHITE, 22, 2);
		simulate(info, bitBoard, Figures.COLOUR_WHITE, 11, 4);
		//simulate(info, bitBoard, Figures.COLOUR_WHITE, 10, 5);
		
		//System.out.println(((Board)bitBoard).statistics.toString());
		
		//System.out.println("puts: " + HashMapLongInt.puts);
		//System.out.println("collisions: " + HashMapLongInt.collisions);
		//System.out.println("size: " + bitBoard.getPlayedBoardStates().getArraySize());
		
		/*int[] arr = new int[Integer.MAX_VALUE / 9];
		
		try {
			Thread.sleep(10000);
			int h = arr.length;
			System.out.println(h);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
}

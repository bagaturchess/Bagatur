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
package bagaturchess.bitboard.api;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.BoardProxy_ReversedBBs;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.bitboard.impl1.BoardImpl;


public class BoardUtils {
	
	
	public static boolean isFRC = false;
	
	
	public static IBitBoard createBoard_WithPawnsCache() {
		return createBoard_WithPawnsCache(Constants.INITIAL_BOARD, bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEvalFactory.class.getName(), null, 1000);
	}
	
	
	public static IBitBoard createBoard_WithPawnsCache(IBoardConfig boardConfig) {
		return createBoard_WithPawnsCache(Constants.INITIAL_BOARD, boardConfig);
	}
	
	
	public static IBitBoard createBoard_WithPawnsCache(String fen) {
		return createBoard_WithPawnsCache(fen, null);
	}
	
	
	public static IBitBoard createBoard_WithPawnsCache(String fen, IBoardConfig boardConfig) {
		return createBoard_WithPawnsCache(fen, bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEvalFactory.class.getName(), boardConfig, 1000);
	}
	
	
	public static IBitBoard createBoard_WithPawnsCache(String fen, String cacheFactoryClassName, IBoardConfig boardConfig, int pawnsCacheSize) {
		return createBoard_WithPawnsCache(fen, cacheFactoryClassName, boardConfig, pawnsCacheSize, IBitBoard.IMPL1);
	}
	
	
	public static IBitBoard createBoard_WithPawnsCache(String fen, String cacheFactoryClassName, IBoardConfig boardConfig, int pawnsCacheSize, boolean impl1) {
		
		IBitBoard bitboard;
		
		if (impl1) {
			
			bitboard = new BoardImpl(fen, boardConfig, isFRC);
			
		} else {
			
			DataObjectFactory<PawnsModelEval> pawnsCacheFactory = null;
			
			try {
				pawnsCacheFactory = (DataObjectFactory<PawnsModelEval>) 
				BoardUtils.class.getClassLoader().loadClass(cacheFactoryClassName).newInstance();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			
			//PawnsEvalCache pawnsCache = new PawnsEvalCache(pawnsCacheFactory, EngineConfigFactory.getDefaultEngineConfiguration().getPawnsCacheSize());
			PawnsEvalCache pawnsCache = new PawnsEvalCache(pawnsCacheFactory, pawnsCacheSize, false, new BinarySemaphore_Dummy());
			 
			bitboard = new BoardProxy_ReversedBBs(new Board(fen, pawnsCache, boardConfig));
		}
		
		if (boardConfig != null) {
			bitboard.setAttacksSupport(boardConfig.getFieldsStatesSupport(), boardConfig.getFieldsStatesSupport());
		}	
		
		return bitboard;
	}
	
	
	public static final int[] getMoves(String[] pv, IBitBoard board) {
		
		int[] result = null;
		
		if (pv != null && pv.length > 0) {
			
			result = new int[pv.length];
			
			int cur = 0;
			for (String move: pv) {
				result[cur++] = board.getMoveOps().stringToMove(move.trim());
				board.makeMoveForward(result[cur - 1]);
			}
			
			for (int i = pv.length - 1; i >= 0; i--) {
				board.makeMoveBackward(result[i]);
			}
		}
		
		
		return result;
	}
	
	
	public static final String getPlayedMoves(IBitBoard bitboard) {
		
		String result = "";
		
		int count = bitboard.getPlayedMovesCount();
		int[] moves = bitboard.getPlayedMoves();
		for (int i=0; i<count; i++) {
			int curMove = moves[i];
			StringBuilder message = new StringBuilder(32);
			message.append(bitboard.getMoveOps().moveToString(curMove));
			result += message.toString() + " ";
		}
	
		return result;
	}
	
	
	public static String movesToString(int[] pv, IBitBoard bitboard) {
		String pvStr = "";
		
		for (int i=0; i<pv.length; i++) {
			pvStr += bitboard.getMoveOps().moveToString(pv[i]);
			if (i != pv.length - 1) {
				pvStr += ", ";
			}
		}
		
		return pvStr;
	}
	
	
	public static void playGameUCI(IBitBoard board, String movesSign) {
		
		List<String> moves = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(movesSign, " ");
		while(st.hasMoreTokens()) {
			moves.add(st.nextToken());
		}
		
		//int colour = Figures.COLOUR_WHITE;
		int size = moves.size();
		for (int i = 0; i < size; i++ ) {
			
			String moveSign = moves.get(i);
			
			if (!moveSign.equals("...")) {
				
				//System.out.println(moveSign);
				
				int move = board.getMoveOps().stringToMove(moveSign);
				
				if (board.getMoveOps().isCastling(move)) {
					
					//System.out.println("CASTLING");
				}
				
				board.makeMoveForward(move);
				
				//System.out.println(board);
			}
		}
	}
	
	
	public static int parseSingleUCIMove(IBitBoard board, String moveSign) {
		int move = 0;
		
		IInternalMoveList moves_list = new BaseMoveList();
		int movesCount = board.genAllMoves(moves_list);
		
		String fromFieldSign = moveSign.substring(0, 2).toLowerCase();
		String toFieldSign = moveSign.substring(2, 4).toLowerCase();
		String promTypeSign = moveSign.length() == 5 ? moveSign.substring(4, 5).toLowerCase() : null;
		//System.out.println("CONSOLE: " + fromFieldSign);
		//System.out.println("CONSOLE: " + toFieldSign);
			
		int fromFieldID = Fields.getFieldID(fromFieldSign);
		int toFieldID = Fields.getFieldID(toFieldSign);
		//System.out.println("CONSOLE: " + fromFieldID);
		//System.out.println("CONSOLE: " + toFieldID);
			
		int[] moves = moves_list.reserved_getMovesBuffer();
		for (int i=0; i<movesCount; i++) {
			int curMove = moves[i];
			//System.out.println(Move.moveToString(curMove));
			int curFromID = board.getMoveOps().getFromFieldID(curMove);
			int curToID = board.getMoveOps().getToFieldID(curMove);
			if (fromFieldID == curFromID && toFieldID == curToID) {
				
				if (promTypeSign == null) {
					move = curMove;
					break;
				} else { //Promotion move
					if (getPromotionTypeUCI(promTypeSign) == board.getMoveOps().getPromotionFigureType(curMove)) {
						move = curMove;
						break;
					}
				}
			}
		}
		
		if (move == 0) {
			throw new IllegalStateException("moveSign=" + moveSign + "\r\n" + board);
		}
		
		return move;
	}

	private static int getPromotionTypeUCI(String promTypeSign) {
		int type = -1;
		
		if (promTypeSign.equals("n")) {
			type = Figures.TYPE_KNIGHT;
		} else if (promTypeSign.equals("b")) {
			type = Figures.TYPE_OFFICER;
		} else if (promTypeSign.equals("r")) {
			type = Figures.TYPE_CASTLE;
		} else if (promTypeSign.equals("q")) {
			type = Figures.TYPE_QUEEN;
		} else {
			throw new IllegalStateException("Invalid promotion figure type '" + promTypeSign + "'");
		}
		
		return type;
	}
}

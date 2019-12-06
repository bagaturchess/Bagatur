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
package bagaturchess.bitboard.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.bitboard.impl1.BoardImpl;


public class BoardUtils {
	
	
	public static IBitBoard createBoard_WithPawnsCache() {
		return createBoard_WithPawnsCache(Constants.INITIAL_BOARD, bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEvalFactory.class.getName(), null, 1000);
	}

	public static IBitBoard createBoard_WithPawnsCache(IBoardConfig boardConfig) {
		return createBoard_WithPawnsCache(Constants.INITIAL_BOARD, boardConfig);
	}
	
	public static IBitBoard createBoard_WithPawnsCache(String fen, IBoardConfig boardConfig) {
		return createBoard_WithPawnsCache(fen, bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEvalFactory.class.getName(), boardConfig, 1000);
	}
	
	public static IBitBoard createBoard_WithPawnsCache(String fen, String cacheFactoryClassName, IBoardConfig boardConfig, int pawnsCacheSize) {
		
		IBitBoard bitboard;
		
		if (IBitBoard.IMPL1) {
			
			bitboard = new BoardImpl(fen, boardConfig);
			
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
			 
			bitboard = new Board(fen, pawnsCache, boardConfig);
		}
		
		if (boardConfig != null) {
			bitboard.setAttacksSupport(boardConfig.getFieldsStatesSupport(), boardConfig.getFieldsStatesSupport());
		}	
		
		return bitboard;
	}
	
	/*public static IBitBoard createBoard(String movesSign) {
		IBitBoard board = new Board();
		playGame(board, movesSign);
		return board;
	}*/
	
	
	public static int uciStrToMove(IBitBoard bitboard, String moveStr) {
		
		int fromFieldID = Fields.getFieldID(moveStr.substring(0, 2));
		int toFieldID = Fields.getFieldID(moveStr.substring(2, 4));
		
		IMoveList mlist = new BaseMoveList();
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(mlist);
		} else {
			bitboard.genAllMoves(mlist);
		}
		
		int cur_move = 0;
		while ((cur_move = mlist.next()) != 0) {
			if (fromFieldID == MoveInt.getFromFieldID(cur_move)
					&& toFieldID == MoveInt.getToFieldID(cur_move)
				) {
				
				if (MoveInt.isPromotion(cur_move)) {
					if (moveStr.endsWith("q")) {
						if (MoveInt.getPromotionFigureType(cur_move) == Figures.TYPE_QUEEN) {
							return cur_move;
						}
					} else if (moveStr.endsWith("r")) {
						if (MoveInt.getPromotionFigureType(cur_move) == Figures.TYPE_CASTLE) {
							return cur_move;
						}
					} else if (moveStr.endsWith("b")) {
						if (MoveInt.getPromotionFigureType(cur_move) == Figures.TYPE_OFFICER) {
							return cur_move;
						}
					} else if (moveStr.endsWith("n")) {
						if (MoveInt.getPromotionFigureType(cur_move) == Figures.TYPE_KNIGHT) {
							return cur_move;
						}
					} else {
						throw new IllegalStateException(moveStr);
					}
				} else {
					return cur_move;
				}
			}
		}
		
		throw new IllegalStateException(bitboard + "\r\n moveStr=" + moveStr);
	}
	
	public static void playGame(IBitBoard board, String movesSign) {
		
		//int colourToMove = Figures.COLOUR_WHITE;
		
		StringTokenizer st = new StringTokenizer(movesSign, ",");
		while (st.hasMoreTokens()) {
			
			String moveSign = st.nextToken().trim();
			//String message = moveSign;
			//System.out.println("colour=" + board.getColourToMove());
			
			int move = parseSingleMove(board, moveSign);
			if (move == 0) {
				board.makeNullMoveForward();
				move = parseSingleMove(board, moveSign);
				if (move == 0) {
					//parseSingleMove(board, moveSign);
					throw new IllegalStateException("move=" + move);
				} else {
					board.makeMoveForward(move);
				}
			} else {
				board.makeMoveForward(move);
				//colourToMove = Figures.OPPONENT_COLOUR[colourToMove];
			}
			
			//message += " -> ";
			//message += board.getHashKey();
			//message += "	" + board.getPawnsHashKey();
			
			//System.out.println(message);
		}
	}

	private static int parseSingleMove(IBitBoard board, String moveSign) {
		int move = 0;
		
		IInternalMoveList moves_list = new BaseMoveList();
		int movesCount = board.genAllMoves(moves_list);
		
		int[] moves = moves_list.reserved_getMovesBuffer();
		
		if (moveSign.startsWith("O-O-O")) {
			for (int i=0; i<movesCount; i++) {
				int curMove = moves[i];
				if (MoveInt.isCastleQueenSide(curMove)) {
					move = curMove;
					break;
				}
			}
		} else if (moveSign.startsWith("O-O")) {
			for (int i=0; i<movesCount; i++) {
				int curMove = moves[i];
				if (MoveInt.isCastleKingSide(curMove)) {
					move = curMove;
					break;
				}
			}
		} else {
			String fromFieldSign = moveSign.substring(0, 2).toLowerCase();
			String toFieldSign = moveSign.substring(3, 5).toLowerCase();
			String promTypeSign = moveSign.length() == 7 ? moveSign.substring(6, 7).toLowerCase() : null;
			//System.out.println("CONSOLE: " + fromFieldSign);
			//System.out.println("CONSOLE: " + toFieldSign);
			
			int fromFieldID = Fields.getFieldID(fromFieldSign);
			int toFieldID = Fields.getFieldID(toFieldSign);
			//System.out.println("CONSOLE: " + fromFieldID);
			//System.out.println("CONSOLE: " + toFieldID);
			
			for (int i=0; i<movesCount; i++) {
				int curMove = moves[i];
				//System.out.println(Move.moveToString(curMove));
				int curFromID = MoveInt.getFromFieldID(curMove);
				int curToID = MoveInt.getToFieldID(curMove);
				if (fromFieldID == curFromID && toFieldID == curToID) {
					
					if (promTypeSign == null) {
						move = curMove;
						break;
					} else { //Promotion move
						if (getPromotionTypeUCI(promTypeSign) == MoveInt.getPromotionFigureType(curMove)) {
							move = curMove;
							break;
						}
					}
				}
			}
		}
		
		
		
		return move;
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
				//int move = BoardUtils.parseSingleUCIMove(board, moveSign);
				//colour = Figures.OPPONENT_COLOUR[colour];
				
				board.makeMoveForward(moveSign);
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
			int curFromID = MoveInt.getFromFieldID(curMove);
			int curToID = MoveInt.getToFieldID(curMove);
			if (fromFieldID == curFromID && toFieldID == curToID) {
				
				if (promTypeSign == null) {
					move = curMove;
					break;
				} else { //Promotion move
					if (getPromotionTypeUCI(promTypeSign) == MoveInt.getPromotionFigureType(curMove)) {
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

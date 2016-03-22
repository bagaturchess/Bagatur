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


public class BoardUtils {
	
	
	public static IBitBoard createBoard_WithPawnsCache() {
		return createBoard_WithPawnsCache(Constants.INITIAL_BOARD, "bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEvalFactory", null, 1000);
	}

	public static IBitBoard createBoard_WithPawnsCache(IBoardConfig boardConfig) {
		return createBoard_WithPawnsCache(Constants.INITIAL_BOARD, boardConfig);
	}
	
	public static IBitBoard createBoard_WithPawnsCache(String fen, IBoardConfig boardConfig) {
		return createBoard_WithPawnsCache(fen, "bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEvalFactory", boardConfig, 1000);
	}
	
	public static IBitBoard createBoard_WithPawnsCache(String fen, String cacheFactoryClassName, IBoardConfig boardConfig, int pawnsCacheSize) {
		
		DataObjectFactory<PawnsModelEval> pawnsCacheFactory = null;
		try {
			pawnsCacheFactory = (DataObjectFactory<PawnsModelEval>) 
			BoardUtils.class.getClassLoader().loadClass(cacheFactoryClassName).newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
		//PawnsEvalCache pawnsCache = new PawnsEvalCache(pawnsCacheFactory, EngineConfigFactory.getDefaultEngineConfiguration().getPawnsCacheSize());
		PawnsEvalCache pawnsCache = new PawnsEvalCache(pawnsCacheFactory, pawnsCacheSize, false, new BinarySemaphore_Dummy());
		 
		IBitBoard bitboard = new Board(fen, pawnsCache, boardConfig);
		if (boardConfig != null) {
			bitboard.setAttacksSupport(boardConfig.getFieldsStatesSupport(), boardConfig.getFieldsStatesSupport());
		}
		
		return bitboard;
	}
	
	public static IBitBoard createBoard(String movesSign) {
		IBitBoard board = new Board();
		playGame(board, movesSign);
		return board;
	}
	
	
	public static int uciStrToMove(IBitBoard bitboard, String moveStr) {
		
		int fromFieldID = Fields.getFieldID(moveStr.substring(0, 2));
		int toFieldID = Fields.getFieldID(moveStr.substring(2, 4));
		
		IMoveList mlist = new BaseMoveList();
		bitboard.genAllMoves(mlist);
		
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
		
		int colourToMove = Figures.COLOUR_WHITE;
		
		StringTokenizer st = new StringTokenizer(movesSign, ",");
		while (st.hasMoreTokens()) {
			
			String moveSign = st.nextToken().trim();
			String message = moveSign;
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
			
			message += " -> ";
			message += board.getHashKey();
			message += "	" + board.getPawnsHashKey();
			
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
	
	public static int parseSingleUCIMove(IBitBoard board, int colourToMove, String moveSign) {
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
			throw new IllegalStateException("moveSign");
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
	
	public static void main(String[] args) {
		//playGame(new Board(), );
	}
/**
 Identifiers
8  6    69   69   69   48   69   49   31   
7  33   66   65   69   69   69   35   3    
6  69   69   69   69   69   51   69   69   
5  69   69   69   69   12   69   69   69   
4  69   58   69   69   69   57   69   69   
3  69   69   21   54   69   69   69   69   
2  30   15   41   44   69   69   69   62   
1  42   69   69   69   11   39   69   60   
   A    B    C    D    E    F    G    H    

Types & Colours
8  BC 0  0  0  BK 0  BN BC 
7  BP BP BP 0  0  0  BP BP 
6  0  0  0  0  0  BP 0  0  
5  0  0  0  0  BN 0  0  0  
4  0  BO 0  0  0  WP 0  0  
3  0  0  WN WP 0  0  0  0  
2  WP WP WP WO 0  0  0  WP 
1  WC 0  0  0  WK WO 0  WC 
   A  B  C  D  E  F  G  H  
D2-D3, E7-E5, F2-F4, B8-C6, G1-F3, F7-F6, C1-D2, D7-D5, E2-E4, C8-G4, E4-D5X, D8-D5X, B1-C3, G4-F3X, D1-F3X, D5-F3X, G2-F3X, F8-B4, F4-E5X, C6-E5X, F3-F4, 
2007-9-8 0:03:04 game.chess.engine.impl1.bitboards.play.PlayerRunnable run
INFO: Colour 1: Ply is F3-F4

2007-9-8 0:03:04 game.chess.engine.impl1.bitboards.alg.starter.SearchConfigurator setup
INFO: EVAL_WHITE> MATERIAL (0: 15101), PAWN_STRUCTURE (1: 400), DEVELOPMENT (2: 170), SPACE (4: 100), 
EVAL_BLACK> MATERIAL (0: 15101), PAWN_STRUCTURE (1: 400), DEVELOPMENT (2: 170), SPACE (4: 100), 
2007-9-8 0:03:04 game.chess.engine.impl1.bitboards.alg.brain.ThinkRunnable pvGenerated
INFO: 0 ms -> (D=1, L=5, E=146) -> [SEARCH] E5-F3, [SEARCH] O-O-O, [SEARCH] O-O-O, [SEARCH] F1-H3, [SEARCH] D8-D3X, [EVAL] null, 
2007-9-8 0:03:04 game.chess.engine.impl1.bitboards.alg.brain.ThinkRunnable pvGenerated
INFO: 0 ms -> (D=2, L=3, E=147) -> [SEARCH] B4-C3X, [SEARCH] D2-C3X, [SEARCH] E5-D3X, [EVAL] null, 
2007-9-8 0:03:05 game.chess.engine.impl1.bitboards.alg.brain.ThinkRunnable pvGenerated
INFO: 78 ms -> (D=3, L=3, E=104) -> [SEARCH] B4-C3X, [SEARCH] D2-C3X, [SEARCH] G8-E7, [EVAL] null, 
2007-9-8 0:03:05 game.chess.engine.impl1.bitboards.alg.brain.ThinkRunnable pvGenerated
INFO: 94 ms -> (D=4, L=4, E=92) -> [SEARCH] B4-C3X, [SEARCH] D2-C3X, [SEARCH] E5-F3, [SEARCH] E1-F2, [EVAL] null, 
2007-9-8 0:03:05 game.chess.engine.impl1.bitboards.alg.brain.ThinkRunnable pvGenerated
INFO: 171 ms -> (D=5, L=8, E=94) -> [SEARCH] E5-D3X, [SEARCH] F1-D3X, [SEARCH] G8-E7, [SEARCH] D3-H7X, [SEARCH] H8-H7X, [SEARCH] C3-E4, [QSEARCH] B4-D2X, [QSEARCH] E4-D2X, [EVAL] null, 
2007-9-8 0:03:06 game.chess.engine.impl1.bitboards.alg.brain.ThinkRunnable pvGenerated
INFO: 1094 ms -> (D=6, L=7, E=105) -> [SEARCH] E5-F3, [SEARCH] O-O-O, [SEARCH] O-O-O, [SEARCH] F1-G2, [SEARCH] F3-D2X, [SEARCH] D1-D2X, [SEARCH] D8-D3X, [EVAL] null, 
2007-9-8 0:03:08 game.chess.engine.impl1.bitboards.alg.brain.ThinkRunnable pvGenerated
INFO: 1703 ms -> (D=7, L=8, E=80) -> [SEARCH] E5-D3X, [SEARCH] F1-D3X, [SEARCH] G8-H6, [SEARCH] D3-H7X, [SEARCH] O-O-O, [SEARCH] O-O-O, [SEARCH] H8-H7X, [SEARCH] D1-E1, [EVAL] null, 

 */
}

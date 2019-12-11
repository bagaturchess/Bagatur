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


import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
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
}

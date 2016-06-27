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
package bagaturchess.search.impl.info;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.BoardUtils;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.uci.impl.commands.info.Info;


public final class SearchInfoFactory {
	
	
	private static SearchInfoFactory instance;
	
	
	private SearchInfoFactory() {
	}
	
	
	public static final SearchInfoFactory getFactory() {
		if (instance == null) {
			instance = new SearchInfoFactory();
		}
		return instance;
	}
	
	
	public ISearchInfo createSearchInfo() {
		return new SearchInfoImpl();
	}
	
	
	public ISearchInfo createSearchInfo_Minor(Info info, IBitBoard board) {
		
		SearchInfoImpl result = new SearchInfoImpl();
		
		result.setDepth(info.getDepth());
		result.setSelDepth(info.getSelDepth());
		result.setSearchedNodes(info.getNodes());
		
		//result.setCurrentMove(info.getCurrmove());
		result.setCurrentMoveNumber(info.getCurrmoveNumber());
		
		return result;
	}
	
	
	public ISearchInfo createSearchInfo(Info info, IBitBoard board) {
		
		SearchInfoImpl result = new SearchInfoImpl();
		
		result.setDepth(info.getDepth());
		result.setSelDepth(info.getSelDepth());
		result.setSearchedNodes(info.getNodes());
		
		result.setEval(info.getEval());

		
		if (info.getPv() != null && info.getPv().length > 0) {
			
			int cur = 0;
			int[] pv = new int[info.getPv().length];
			for (String move: info.getPv()) {
				
				//System.out.println("pv line move["+ cur + "]=" + move);
				
				pv[cur++] = BoardUtils.uciStrToMove(board, move.trim());
				board.makeMoveForward(pv[cur - 1]);
			}
			
			for (int i = pv.length - 1; i >= 0; i--) {
				board.makeMoveBackward(pv[i]);
			}
			
			result.setPV(pv);
		}
		
		
		if (result.getPV() != null && result.getPV().length > 0) {
			result.setBestMove(result.getPV()[0]);
		}
		
		
		return result;
	}
}

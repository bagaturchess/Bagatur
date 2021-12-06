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


import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.impl.alg.SearchUtils;
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
		
		String currmove = info.getCurrmove();
		if (currmove != null) {
			result.setCurrentMove(board.getMoveOps().stringToMove(currmove));
			result.setCurrentMoveNumber(info.getCurrmoveNumber());
		}
		
		return result;
	}
	
	
	public ISearchInfo createSearchInfo(Info info, IBitBoard board) {
		
		SearchInfoImpl result = new SearchInfoImpl();
		
		result.setDepth(info.getDepth());
		result.setSelDepth(info.getSelDepth());
		result.setSearchedNodes(info.getNodes());
		
		if (info.isMate()) {
			
			int mateIn = info.getEval();
			int mateVal = (mateIn > 0 ? 1: -1) * SearchUtils.getMateVal(Math.abs(mateIn));
			
			result.setEval(mateVal);
			
		} else {
			
			result.setEval(info.getEval());
		}
		
		result.setPV(BoardUtils.getMoves(info.getPv(), board));
		
		
		if (result.getPV() != null && result.getPV().length > 0) {
			result.setBestMove(result.getPV()[0]);
		}
		
		String currmove = info.getCurrmove();
		if (currmove != null) {
			result.setCurrentMove(board.getMoveOps().stringToMove(currmove));
			result.setCurrentMoveNumber(info.getCurrmoveNumber());
		}
		
		return result;
	}
}

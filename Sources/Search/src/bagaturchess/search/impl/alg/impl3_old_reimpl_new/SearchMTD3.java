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
package bagaturchess.search.impl.alg.impl3_old_reimpl_new;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.internal.ISearchMoveListFactory;
import bagaturchess.search.impl.alg.impl0.SearchMTD0;
import bagaturchess.search.impl.env.SearchEnv;


public class SearchMTD3 extends SearchMTD0 {
	
	
	public SearchMTD3(Object[] args) {
		super(args);
	}
	
	
	public SearchMTD3(SearchEnv _env) {
		super(_env);
	}
	
	
	@Override
	protected ISearchMoveListFactory getMoveListFactory() {
		return new SearchMoveListFactory3();
	}
	
	
	@Override
	protected boolean allowIllegalMoves() {
		return true;
	}
}

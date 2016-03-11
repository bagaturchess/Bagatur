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

import bagaturchess.search.api.internal.ISearchInfo;


public final class SearchInfoFactory {
	
	private static SearchInfoFactory instance;
	//private static String className = "game.chess.engine.impl1.bitboards.uci.SearchInfoImpl";
	//private static Class infoClass;
	
	private SearchInfoFactory() {
	}

	public static final SearchInfoFactory getFactory() {
		if (instance == null) {
			instance = new SearchInfoFactory();
			/*try {
				infoClass = infoClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}*/
		}
		return instance;
	}
	
	public ISearchInfo createSearchInfo() {
		return new SearchInfoImpl();
		/*try {
			return (ISearchInfo) infoClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;*/
	}
}

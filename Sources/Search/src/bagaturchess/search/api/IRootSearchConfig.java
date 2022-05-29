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
package bagaturchess.search.api;


import bagaturchess.bitboard.api.IBoardConfig;


public interface IRootSearchConfig {
	
	
	public double get_MEMORY_USAGE_PERCENT();
	
	public boolean useTPT();
	
	public boolean useGlobalTPT();
	
	public boolean useEvalCache();
	
	public boolean useSyzygyDTZCache();
	
	public int getMultiPVsCount();
	
	public String getSearchClassName();
	
	public ISearchConfig_AB getSearchConfig();

	
	/**
	 * EGTB Settings
	 */
	public String getTbPath();
	
	public boolean useOnlineSyzygy();
	
	
	/**
	 * Memory Settings
	 */
	public boolean initCaches();
	
	public double getTPTUsagePercent();
	
	public double getEvalCacheUsagePercent();
	
	public double getPawnsCacheUsagePercent();
	
	
	public int getThreadsCount();
	
	public int getThreadMemory_InMegabytes();
	
	
	public IBoardConfig getBoardConfig();
	
	public IEvalConfig getEvalConfig();
	
	public String getBoardFactoryClassName();
	
	public String getSemaphoreFactoryClassName();
	
	public int getHiddenDepth();
}

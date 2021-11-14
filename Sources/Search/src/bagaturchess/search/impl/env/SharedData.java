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
package bagaturchess.search.impl.env;


import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import bagaturchess.search.impl.tpt.TranspositionTableProvider;
import bagaturchess.uci.api.IChannel;


public class SharedData {
	
	
	private IEvaluatorFactory evaluatorFactory;
	private IRootSearchConfig engineConfiguration;
	private ISearchConfig_AB searchConfig;
	private MemoryConsumers memoryConsumers;
	
	
	public SharedData(IChannel _channel, IRootSearchConfig _engineConfiguration) {
		this(_engineConfiguration, new MemoryConsumers(_channel, _engineConfiguration, false));
	}
	
	
	public SharedData(IRootSearchConfig _engineConfiguration, MemoryConsumers _memoryConsumers) {
		
		init(_engineConfiguration);

		setMemoryConsumers(_memoryConsumers);
	}
	
	
	private void init(IRootSearchConfig _engineConfiguration) {
		engineConfiguration = _engineConfiguration;
		searchConfig = engineConfiguration.getSearchConfig();
		
		try {
			String className = engineConfiguration.getEvalConfig().getEvaluatorFactoryClassName();
			evaluatorFactory = (IEvaluatorFactory) SharedData.class.getClassLoader().loadClass(className).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}
	
	
	public void setMemoryConsumers(MemoryConsumers _memoryConsumers) {
		memoryConsumers = _memoryConsumers;
	}
	
	
	public IEvaluatorFactory getEvaluatorFactory() {
		return evaluatorFactory;
	}
	
	
	public TranspositionTableProvider getTranspositionTableProvider() {
		return memoryConsumers.getTPTProvider();
	}
	
	
	public PawnsEvalCache getAndRemovePawnsCache() {
		return memoryConsumers.getPawnsCache().remove(0);
	}

	
	public IEvalCache getAndRemoveEvalCache() {
		return memoryConsumers.getEvalCache().remove(0);
	}
	
	public IEvalCache getSyzygyDTZCache() {
		return memoryConsumers.getSyzygyDTZCache().remove(0);
	}
	
	
	@Override
	public String toString() {
		String msg = "";//"TPT HIT RATE is: " + getTPT().getHitRate();
		return msg;
	}
	
	
	public void clear() {
		//history_check.clear();
		//history_all.clear();
		//pvs_history.clear();
		
		memoryConsumers.clear();
	}

	public IRootSearchConfig getEngineConfiguration() {
		return engineConfiguration;
	}
	
	public ISearchConfig_AB getSearchConfig() {
		return searchConfig;
	}
	
	public OpeningBook getOpeningBook() {
		return memoryConsumers.getOpeningBook();
	}
}

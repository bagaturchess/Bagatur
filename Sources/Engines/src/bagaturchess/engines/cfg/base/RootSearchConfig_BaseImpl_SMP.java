/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.engines.cfg.base;


import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin_Integer;


public abstract class RootSearchConfig_BaseImpl_SMP extends RootSearchConfig_BaseImpl implements IRootSearchConfig_SMP, IUCIOptionsProvider {
	
	
	private int currentThreadsCount = getDefaultThreadsCount();
	
	//setoption name SMP Threads value 16
	private UCIOption[] options = new UCIOption[] {
			new UCIOptionSpin_Integer("SMP Threads", currentThreadsCount,
					"type spin default " + currentThreadsCount
											+ " min 1"
											+ " max 256"),
	};
	
	
	public RootSearchConfig_BaseImpl_SMP(String[] args) {
		super(args);
	}
	
	
	@Override
	public String getSemaphoreFactoryClassName() {
		return bagaturchess.bitboard.impl.utils.BinarySemaphoreFactory.class.getName();
	}
	
	
	@Override
	public int getThreadsCount() {
		return currentThreadsCount;
	}
	
	
	@Override
	public UCIOption[] getSupportedOptions() {
		UCIOption[] parentOptions = super.getSupportedOptions();
		
		UCIOption[] result = new UCIOption[parentOptions.length + options.length];
		
		System.arraycopy(options, 0, result, 0, options.length);
		System.arraycopy(parentOptions, 0, result, options.length, parentOptions.length);
		
		return result;
	}
	
	
	@Override
	public boolean applyOption(UCIOption option) {
		if ("SMP Threads".equals(option.getName())) {
			currentThreadsCount = (Integer) option.getValue();
			return true;
		}
		
		return super.applyOption(option);
	}
	
	
	private static final int getDefaultThreadsCount() {
		
		int threads = Runtime.getRuntime().availableProcessors();
		
		threads /= 2;//2 logical processors for 1 core in most hardware architectures
		//threads--;//One thread for the OS
		
		if (threads < 1) {
			threads = 1;
		}
		/*if (threads > 8) {//Limit for testing
			threads = 8;
		}*/
		
		return threads;
		
		//return 1;
	}
}

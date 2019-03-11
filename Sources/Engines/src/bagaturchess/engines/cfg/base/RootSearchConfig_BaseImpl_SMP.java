package bagaturchess.engines.cfg.base;


import bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl;
import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin_Double;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin_Integer;


public class RootSearchConfig_BaseImpl_SMP extends RootSearchConfig_BaseImpl implements IRootSearchConfig_SMP, IUCIOptionsProvider {
	
	
	private int currentThreadsCount = getDefaultThreadsCount();
	
	
	private UCIOption[] options = new UCIOption[] {
			new UCIOptionSpin_Integer("SMP Threads", currentThreadsCount,
					"type spin default " + currentThreadsCount
											+ " min 1"
											+ " max 64"),
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
	public double getTPTUsagePercent() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public double getTPTQSUsagePercent() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public double getGTBUsagePercent() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public double getEvalCacheUsagePercent() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public double getPawnsCacheUsagePercent() {
		throw new UnsupportedOperationException();
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
		threads--;//One thread for the OS
		
		if (threads < 1) {
			threads = 1;
		}
		/*if (threads > 8) {//Limit for testing
			threads = 8;
		}*/
		
		return threads;
		
		//return 1;
	}
	
	
	@Override
	public boolean initCaches() {
		return false;
	}
}

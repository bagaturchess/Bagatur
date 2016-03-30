package bagaturchess.engines.bagatur.cfg.rootsearch;


import bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD;
import bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl;
import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin;


public class RootSearchConfig_SMP extends RootSearchConfig_BaseImpl implements IRootSearchConfig_SMP, IUCIOptionsProvider {
	
	
	private UCIOption[] options = new UCIOption[] {
			new UCIOptionSpin("Search SMP [Threads count]", (double) getDefaultThreadsCount(),
								"type spin default " + getDefaultThreadsCount()
											+ " min " + 1
											+ " max " + 10 * getDefaultThreadsCount(), 1),
			new UCIOptionSpin("Hidden Depth", 0d, "type spin default 0 min 0 max 10", 1),
	};
	
	
	private int currentThreadsCount = getDefaultThreadsCount();
	private int hiddenDepth = 0;
	
	
	public RootSearchConfig_SMP() {
		this(new String[0]);
	}
			
	public RootSearchConfig_SMP(String[] args) {
		super(new String[] {"bagaturchess.search.impl.alg.SearchMTD", SearchConfigImpl_MTD.class.getName(), "bagaturchess.engines.bagatur.cfg.board.BoardConfigImpl", "bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2"});
	}
	
	
	@Override
	public int getThreadsCount() {
		return currentThreadsCount;
	}
	
	
	@Override
	public int getHiddenDepth() {
		return hiddenDepth;
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
		if ("Search SMP [Threads count]".equals(option.getName())) {
			currentThreadsCount = (int) ((Double) option.getValue()).doubleValue();
			return true;
		} else if ("Hidden Depth".equals(option.getName())) {
			hiddenDepth = (int) ((Double) option.getValue()).doubleValue();
			return true;
		}
		
		return super.applyOption(option);
	}
	
		
	private static final int getDefaultThreadsCount() {
		int threads = Runtime.getRuntime().availableProcessors();
		if (threads < 2) {
			threads = 2;
		}		
		return threads;
	}
}

package bagaturchess.engines.base.cfg;


import bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl;
import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin;


public class RootSearchConfig_BaseImpl_SMP extends RootSearchConfig_BaseImpl implements IRootSearchConfig_SMP, IUCIOptionsProvider {
	
	
	private UCIOption[] options = new UCIOption[] {
			new UCIOptionSpin("Search SMP [Threads count]", (double) getDefaultThreadsCount(),
								"type spin default " + getDefaultThreadsCount()
											+ " min 1"
											+ " max 32", 1),
	};
	
	
	private int currentThreadsCount = getDefaultThreadsCount();
	
	protected double SMP_MEM_USAGE_TPT;
	protected double SMP_MEM_USAGE_GTB;
	protected double SMP_MEM_USAGE_EVALCACHE;
	protected double SMP_MEM_USAGE_PAWNCACHE;
	
	
	/*public RootSearchConfig_BaseImpl_SMP() {
		this(new String[0]);
	}*/
	
	public RootSearchConfig_BaseImpl_SMP(String[] args) {
		super(args);
		
		calcMemoryUsagePercents();
	}
	
	
	private void calcMemoryUsagePercents() {
		
		double mem_usage_norm = 1 / (double)(getThreadsCount() * (MEM_USAGE_TPT + MEM_USAGE_GTB + MEM_USAGE_EVALCACHE + MEM_USAGE_PAWNCACHE));
		
		SMP_MEM_USAGE_TPT = MEM_USAGE_TPT * mem_usage_norm;
		SMP_MEM_USAGE_GTB = MEM_USAGE_GTB * mem_usage_norm;
		SMP_MEM_USAGE_EVALCACHE = MEM_USAGE_EVALCACHE * mem_usage_norm;
		SMP_MEM_USAGE_PAWNCACHE = MEM_USAGE_PAWNCACHE * mem_usage_norm;
		
		
		//System.out.println("SMP_MEM_USAGE_TPT=" + SMP_MEM_USAGE_TPT);
		//System.out.println("SMP_MEM_USAGE_EVALCACHE=" + SMP_MEM_USAGE_EVALCACHE);
		//System.out.println("SMP_MEM_USAGE_PAWNCACHE=" + SMP_MEM_USAGE_PAWNCACHE);
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
		return SMP_MEM_USAGE_TPT;
	}
	
	@Override
	public double getGTBUsagePercent() {
		return SMP_MEM_USAGE_GTB;
	}
	
	@Override
	public double getEvalCacheUsagePercent() {
		return SMP_MEM_USAGE_EVALCACHE;
	}
	
	
	@Override
	public double getPawnsCacheUsagePercent() {
		return SMP_MEM_USAGE_PAWNCACHE;
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
			calcMemoryUsagePercents();
			return true;
		}
		
		return super.applyOption(option);
	}
	
	
	private static final int getDefaultThreadsCount() {
		int threads = Runtime.getRuntime().availableProcessors() / 2;
		if (threads < 2) {
			threads = 2;
		}
		if (threads > 8) {
			threads = 8;
		}
		
		return threads;
		
		//return 1;
	}
}

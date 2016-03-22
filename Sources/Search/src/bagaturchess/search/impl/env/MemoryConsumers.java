package bagaturchess.search.impl.env;


import java.util.List;
import java.util.Vector;

import bagaturchess.bitboard.api.IBinarySemaphoreFactory;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.attacks.control.metadata.SeeMetadata;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.utils.BinarySemaphore;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.egtb.gaviota.GTBProbing_NativeWrapper;
import bagaturchess.egtb.gaviota.cache.GTBCache_IN;
import bagaturchess.egtb.gaviota.cache.GTBCache_OUT;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.api.OpeningBookFactory;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.impl.evalcache.EvalCache;
import bagaturchess.search.impl.evalcache.IEvalCache;
import bagaturchess.search.impl.tpt.TPTable;
import bagaturchess.uci.api.IChannel;


public class MemoryConsumers {
	
	
	private static int JVMDLL_MEMORY_CONSUMPTION = 20 * 1024 * 1024;
	//private static final int EGTBDLL_MEMORY_CONSUMPTION = 10 * 1024 * 1024;
	private static int MIN_MEMORY_BUFFER;
	private static double MEMORY_USAGE_PERCENT; 
	
	
	public static void set_JVMDLL_MEMORY_CONSUMPTION(int val) {
		JVMDLL_MEMORY_CONSUMPTION = val;	
	}
	
	public static void set_MIN_MEMORY_BUFFER(int val) {
		MIN_MEMORY_BUFFER = val;	
	}
	
	public static void set_MEMORY_USAGE_PERCENT(double val) {
		MEMORY_USAGE_PERCENT = val;	
	}
	
	static {
		if (getJVMBitmode() == 64) {
			MIN_MEMORY_BUFFER = 5 * 1024 * 1024;
			MEMORY_USAGE_PERCENT = 0.79;//Multiple cpus, e.g. 64
		} else { //32
			MIN_MEMORY_BUFFER = 5 * 1024 * 1024;
			MEMORY_USAGE_PERCENT =  0.85;
		}
	}
	
	
	private IRootSearchConfig engineConfiguration;
	
	private SeeMetadata seeMetadata;
	private OpeningBook openingBook;
	
	private IBinarySemaphoreFactory semaphoreFactory;
	
	private TPTable tpt;
	private List<IEvalCache> evalCache;
	private List<PawnsEvalCache> pawnsCache;
	private GTBCache_IN gtbCache_in;
	private GTBCache_OUT gtbCache_out;
	
	private IChannel channel;
	
	
	public MemoryConsumers(IChannel _channel, IRootSearchConfig _engineConfiguration, boolean ownBookEnabled) {
		
		channel = _channel;
		
		engineConfiguration = _engineConfiguration;
		
		channel.dump("OS arch: " + getJVMBitmode() + " bits");
		
		//if (getAvailableMemory() / (1024 * 1024) < 63 - (JVMDLL_MEMORY_CONSUMPTION / (1024 * 1024))) {
		//	throw new IllegalStateException("Not enough memory. The engine needs from at least 64MB to run. Please increase the -Xmx option of Java VM");
		//}
		
		long availableMemory = (long) (MEMORY_USAGE_PERCENT * getAvailableMemory());
		long memoryBuffer = getAvailableMemory() - availableMemory;
		if (memoryBuffer < MIN_MEMORY_BUFFER) {
			memoryBuffer = MIN_MEMORY_BUFFER;
			availableMemory = getAvailableMemory() - MIN_MEMORY_BUFFER;
		}
		
		channel.dump("JVM DLL memory consumption: " + (JVMDLL_MEMORY_CONSUMPTION / (1024 * 1024)) + "MB");
		
		channel.dump("Available memory for the java process " + (getAvailableMemory() / (1024 * 1024)) + "MB");
		channel.dump("Defined memory usage percent " + (MEMORY_USAGE_PERCENT * 100) + "%");
		channel.dump("Memory the Engine will use " + (availableMemory / (1024 * 1024)) + "MB");
		
		channel.dump("Initializing Memory Consumers ...");
		
		channel.dump("SEE Metadata ... ");
		long lastAvailable_in_MB = ((getAvailableMemory() - memoryBuffer) / (1024 * 1024));
		seeMetadata = SeeMetadata.getSingleton();
		channel.dump("SEE Metadata OK => " + (lastAvailable_in_MB - ((getAvailableMemory() - memoryBuffer) / (1024 * 1024))) + "MB");
		
		channel.dump("Openning Book enabled: " + ownBookEnabled);
		if (ownBookEnabled) {
			lastAvailable_in_MB = ((getAvailableMemory() - memoryBuffer) / (1024 * 1024));
			channel.dump("Openning Book ... ");
			try {
				openingBook = OpeningBookFactory.getBook();
				channel.dump("Openning Book OK => " + (lastAvailable_in_MB - ((getAvailableMemory() - memoryBuffer) / (1024 * 1024))) + "MB");
			} catch(Exception e) {
				channel.dump("Unable to load Openning Book. Error is:");
				channel.dump(e);
			}
		}
		
		channel.dump("Endgame Tablebases ... ");
		lastAvailable_in_MB = ((getAvailableMemory() - memoryBuffer) / (1024 * 1024));
		if (GTBProbing_NativeWrapper.getInstance() != null) {
			
			GTBProbing_NativeWrapper.getInstance().setPath_Sync(
					engineConfiguration.getGaviotaTbPath(),
					engineConfiguration.getGaviotaTbCache());
			
			//try {Thread.sleep(10000);} catch (InterruptedException e1) {}
			channel.dump("Endgame Tablebases OK => " + (lastAvailable_in_MB - ((getAvailableMemory() - memoryBuffer) / (1024 * 1024))) + "MB");
		} else {
			//TODO: set percent to 0 and log corresponding message for the sizes
			//Can't load IA 32-bit .dll on a AMD 64-bit platform
			//throw new IllegalStateException("egtbprobe dynamic library could not be loaded (or not found)");
			channel.dump("egtbprobe dynamic library could not be loaded (or not found)");
		}
		
		
		channel.dump("Caches (Transposition Table, Eval Cache and Pawns Eval Cache) ...");
		channel.dump("Transposition Table usage percent from the free memory 1 X : " + engineConfiguration.getThreadsCount() * (100 * engineConfiguration.getTPTUsagePercent()) + "%");
		channel.dump("Endgame Table Bases Cache usage percent from the free memory 1 X : " + engineConfiguration.getThreadsCount() * (100 * engineConfiguration.getGTBUsagePercent()) + "%");
		channel.dump("Eval Cache usage percent from the free memory " + engineConfiguration.getThreadsCount() + " X : " + (100 * engineConfiguration.getEvalCacheUsagePercent()) + "%");
		channel.dump("Pawns Eval Cache usage percent from the free memory " + engineConfiguration.getThreadsCount() + " X : " + (100 * engineConfiguration.getPawnsCacheUsagePercent()) + "%");
		
		double percents_sum = engineConfiguration.getThreadsCount() * engineConfiguration.getTPTUsagePercent()
							+ engineConfiguration.getThreadsCount() * engineConfiguration.getGTBUsagePercent()
							+ engineConfiguration.getThreadsCount() * engineConfiguration.getEvalCacheUsagePercent()
							+ engineConfiguration.getThreadsCount() * engineConfiguration.getPawnsCacheUsagePercent();
		
		if (percents_sum < 0.9999 || percents_sum > 1.0001) {
			throw new IllegalStateException("Percents sum is not near to 1. It is " + percents_sum);
		}
		
		try {
			semaphoreFactory = (IBinarySemaphoreFactory) SharedData.class.getClassLoader().loadClass(engineConfiguration.getSemaphoreFactoryClassName()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		
		
		initCaches(getAvailableMemory() - memoryBuffer);
		
		
		channel.dump("Memory Consumers are initialized. Final available memory buffer is: " + (memoryBuffer / (1024 * 1024)) + "MB");
	}
	
	
	private void initCaches(long availableMemory) {
		
		//if (availableMemory / (1024 * 1024) < 10) {
		//	throw new IllegalStateException("Not enough memory. At least 10 MB are necessary for caches (only "
		//			+ (availableMemory / (1024 * 1024)) + " are available). Please increase the -Xmx option of Java VM");
		//}
		
		int size_tpt = getTPTEntrySize(availableMemory);
		channel.dump("Transposition Table size is " + size_tpt);

		int threadsCount = engineConfiguration.getThreadsCount();
		
		int size_ec = getEvalCacheSize(availableMemory);
		channel.dump("Eval Cache size is " + size_ec);
		int size_pc = 0;
		
		
		String pawnsCacheName = engineConfiguration.getEvalConfig().getPawnsCacheFactoryClassName();
		if (pawnsCacheName != null) {
			size_pc = getPawnsEvalCacheSize(availableMemory, pawnsCacheName);
		}
		channel.dump("Pawns Eval Cache size is " + size_pc);
		
		int size_gtb_in = 0;
		if (GTBProbing_NativeWrapper.getInstance() != null) {
			size_gtb_in = getGTBEntrySize_IN(availableMemory);
			channel.dump("Endgame Table Bases cache (IN) size is " + size_gtb_in);
		}

		int size_gtb_out = 0;
		if (GTBProbing_NativeWrapper.getInstance() != null) {
			size_gtb_out = getGTBEntrySize_OUT(availableMemory);
			channel.dump("Endgame Table Bases cache (OUT) size is " + size_gtb_out);
		}
		
		//Create 
		tpt = new TPTable(size_tpt, false, semaphoreFactory.createSempahore());
		
		evalCache = new Vector<IEvalCache>();
		pawnsCache = new Vector<PawnsEvalCache>();
		for (int i=0; i<threadsCount; i++) {
			evalCache.add(new EvalCache(size_ec, false, new BinarySemaphore_Dummy()));
			//evalCache.add(new EvalCache1(5, size_ec, false, new BinarySemaphore_Dummy()));
			if (size_pc != 0) {
				DataObjectFactory<PawnsModelEval> pawnsCacheFactory = (DataObjectFactory<PawnsModelEval>) ReflectionUtils.createObjectByClassName_NoArgsConstructor(pawnsCacheName);
				pawnsCache.add(new PawnsEvalCache(pawnsCacheFactory, size_pc, false, new BinarySemaphore_Dummy()));
			}
		}
		
		if (GTBProbing_NativeWrapper.getInstance() != null) {
			gtbCache_in = new GTBCache_IN(size_gtb_in, true, new BinarySemaphore());//null;//new GTBCache_IN(1000);
			gtbCache_out = new GTBCache_OUT(size_gtb_out, true, new BinarySemaphore());
		}
	}
	
	
	private int getTPTEntrySize(long availableMemory) {
		int availableMemory_in_MB = (int) (availableMemory / (1024 * 1024));
		if (availableMemory_in_MB < 1) {
			throw new IllegalStateException("Not enough memory for initializing Transposition Table. Please increase the -Xmx option of Java VM");
		}
		int test_size = availableMemory_in_MB * 1000;
		
		System.gc();
		int memory_before = getUsedMemory();
		TPTable test_tpt = new TPTable(test_size, true, null);
		int size = getEntrySize(availableMemory, engineConfiguration.getThreadsCount() * engineConfiguration.getTPTUsagePercent(), test_size, memory_before);
		test_tpt.clear();
		return size;
	}
	
	private int getGTBEntrySize_IN(long availableMemory) {
		int availableMemory_in_MB = (int) (availableMemory / (1024 * 1024));
		if (availableMemory_in_MB < 1) {
			throw new IllegalStateException("Not enough memory for initializing Endgame Table Bases cache (IN). Please increase the -Xmx option of Java VM");
		}
		int test_size = availableMemory_in_MB * 1000;
		
		System.gc();
		int memory_before = getUsedMemory();
		GTBCache_IN gtbCache = new GTBCache_IN(test_size, true, null);
		int size = getEntrySize(availableMemory, engineConfiguration.getThreadsCount() * (1 * engineConfiguration.getGTBUsagePercent()) / 7, test_size, memory_before);
		gtbCache.clear();
		return size;
	}

	private int getGTBEntrySize_OUT(long availableMemory) {
		int availableMemory_in_MB = (int) (availableMemory / (1024 * 1024));
		if (availableMemory_in_MB < 1) {
			throw new IllegalStateException("Not enough memory for initializing Endgame Table Bases cache (OUT). Please increase the -Xmx option of Java VM");
		}
		int test_size = availableMemory_in_MB * 1000;
		
		System.gc();
		int memory_before = getUsedMemory();
		GTBCache_OUT gtbCache = new GTBCache_OUT(test_size, true, null);
		int size = getEntrySize(availableMemory, engineConfiguration.getThreadsCount() * (6 * engineConfiguration.getGTBUsagePercent()) / 7, test_size, memory_before);
		gtbCache.clear();
		return size;
	}

	private int getEvalCacheSize(long availableMemory) {
		int availableMemory_in_MB = (int) (availableMemory / (1024 * 1024));
		if (availableMemory_in_MB < 1) {
			throw new IllegalStateException("Not enough memory for initializing Eval Cache. Please increase the -Xmx option of Java VM");
		}
		int test_size = availableMemory_in_MB * 1000;
		
		System.gc();
		int memory_before = getUsedMemory();
		IEvalCache test_ec = new EvalCache(test_size, true, null);
		//IEvalCache test_ec = new EvalCache1(5, test_size, true, null);
		int size = getEntrySize(availableMemory, engineConfiguration.getEvalCacheUsagePercent(), test_size, memory_before);
		test_ec.clear();
		return size;
	}
	
	private int getPawnsEvalCacheSize(long availableMemory, String pawnsCacheName) {
		int availableMemory_in_MB = (int) (availableMemory / (1024 * 1024));
		if (availableMemory_in_MB < 1) {
			throw new IllegalStateException("Not enough memory for initializing Pawns Eval Cache. Please increase the -Xmx option of Java VM");
		}
		int test_size = availableMemory_in_MB * 100;
		
		System.gc();
		int memory_before = getUsedMemory();
		DataObjectFactory<PawnsModelEval> pawnsCacheFactory = (DataObjectFactory<PawnsModelEval>) ReflectionUtils.createObjectByClassName_NoArgsConstructor(pawnsCacheName);
		PawnsEvalCache test_pc = new PawnsEvalCache(pawnsCacheFactory, test_size, true, null);
		int size = getEntrySize(availableMemory, engineConfiguration.getPawnsCacheUsagePercent(), test_size, memory_before);
		test_pc.clear();
		return size;
	}
	
	private int getEntrySize(long availableMemory, double usagePercent, int test_size, int memory_before) {
		int memory_after = getUsedMemory();
		int size_per_entry = (memory_after - memory_before) / test_size;
		int size = (int) ((usagePercent * availableMemory) / size_per_entry);
		return size;
	}
	
	
	private int getAvailableMemory() {
		
		System.gc();
		System.gc();
		System.gc();
		
		int max_mem = (int) Runtime.getRuntime().maxMemory();
		
		int total_mem = (int) Runtime.getRuntime().totalMemory();
		int free_mem = (int) Runtime.getRuntime().freeMemory();
		int used_mem = total_mem - free_mem;
		
		int available_mem = max_mem - used_mem;
		
		return available_mem - getStaticMemory();
	}
	
	
	private static int getUsedMemory() {
		
		System.gc();
		System.gc();
		System.gc();
		
		int total_mem = (int) Runtime.getRuntime().totalMemory();
		int free_mem = (int) Runtime.getRuntime().freeMemory();
		int used_mem = total_mem - free_mem;
		return used_mem + getStaticMemory();
	}
	
	
	private static int getStaticMemory() {
		return JVMDLL_MEMORY_CONSUMPTION;// + EGTBDLL_MEMORY_CONSUMPTION;
	}
	
	
	private static int getJVMBitmode() {
		
	    String vendorKeys [] = {
		        "sun.arch.data.model",
		        "com.ibm.vm.bitmode",
		        "os.arch",
		};
	    
        for (String key : vendorKeys ) {
            String property = System.getProperty(key);
            if (property != null) {
                int code = (property.indexOf("64") >= 0) ? 64 : 32;
                return code;
            }
        }
        return 32;
	}
	
	
	/*public SeeMetadata getSeeMetadata() {
		return seeMetadata;
	}*/


	public OpeningBook getOpeningBook() {
		return openingBook;
	}


	public TPTable getTPT() {
		return tpt;
	}


	public List<IEvalCache> getEvalCache() {
		return evalCache;
	}


	public List<PawnsEvalCache> getPawnsCache() {
		return pawnsCache;
	}
	
	public GTBCache_OUT getGTBCache_OUT() {
		return gtbCache_out;
	}
	
	public GTBCache_IN getGTBCache_IN() {
		return gtbCache_in;
	}
	
	public void clear() {
		tpt.clear();
		pawnsCache.clear();
		evalCache.clear();
		
		if (gtbCache_out != null) gtbCache_out.clear();
		if (gtbCache_in != null) gtbCache_in.clear();
	}
}

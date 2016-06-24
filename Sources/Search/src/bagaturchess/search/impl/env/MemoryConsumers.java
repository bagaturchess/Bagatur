package bagaturchess.search.impl.env;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.attacks.control.metadata.SeeMetadata;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.egtb.gaviota.GTBProbing;
import bagaturchess.egtb.gaviota.GTBProbing_NativeWrapper;
import bagaturchess.egtb.gaviota.cache.GTBCache_OUT;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.api.OpeningBookFactory;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.impl.evalcache.EvalCache;
import bagaturchess.search.impl.evalcache.IEvalCache;
import bagaturchess.search.impl.tpt.TPTable;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.api.IChannel;


public class MemoryConsumers {
	
	
	private static int JVMDLL_MEMORY_CONSUMPTION 						= 20 * 1024 * 1024;
	private static int MIN_MEMORY_BUFFER;
	private static double MEMORY_USAGE_PERCENT; 
	
	private static final int SIZE_MIN_ENTRIES_MULTIPLIER				= 111;
	private static final int SIZE_MIN_ENTRIES_TPT						= 27 * SIZE_MIN_ENTRIES_MULTIPLIER;
	private static final int SIZE_MIN_ENTRIES_EC						= 21 * SIZE_MIN_ENTRIES_MULTIPLIER;
	private static final int SIZE_MIN_ENTRIES_PEC						= 1 * SIZE_MIN_ENTRIES_MULTIPLIER;
	private static final int SIZE_MIN_ENTRIES_GTB						= 9 * SIZE_MIN_ENTRIES_MULTIPLIER;
	
	private static InputStream is_w_openning_book;
	private static InputStream is_b_openning_book;
	
	
	public static void set_JVMDLL_MEMORY_CONSUMPTION(int val) {
		JVMDLL_MEMORY_CONSUMPTION = val;	
	}
	
	public static void set_MIN_MEMORY_BUFFER(int val) {
		MIN_MEMORY_BUFFER = val;	
	}
	
	public static void set_MEMORY_USAGE_PERCENT(double val) {
		MEMORY_USAGE_PERCENT = val;	
	}
	
	public static void set_OpenningBook_Streams(InputStream is_w, InputStream is_b) {
		is_w_openning_book = is_w;
		is_b_openning_book = is_b;
	}
	
	static {
		if (getJVMBitmode() == 64) {
			MIN_MEMORY_BUFFER = 5 * 1024 * 1024;
			MEMORY_USAGE_PERCENT = 0.23;
		} else { //32
			MIN_MEMORY_BUFFER = 5 * 1024 * 1024;
			MEMORY_USAGE_PERCENT =  0.23;
		}
		
		try {
			is_w_openning_book = new FileInputStream("./data/w.ob");
			is_b_openning_book = new FileInputStream("./data/b.ob");
			set_OpenningBook_Streams(is_w_openning_book, is_b_openning_book);
		} catch(Throwable t) {
			ChannelManager.getChannel().dump("Unable to load Openning Book. Error while openning file streams: " + t.getMessage());
		}
	}
	
	
	private IRootSearchConfig engineConfiguration;
	
	private SeeMetadata seeMetadata;
	private OpeningBook openingBook;
	
	//private IBinarySemaphoreFactory semaphoreFactory;
	
	private List<IEvalCache> evalCache;
	private List<PawnsEvalCache> pawnsCache;
	private List<TPTable> tpt;
	private List<GTBProbing> gtbs;
	private List<GTBCache_OUT> gtbCache_out;
	
	private IChannel channel;
	
	
	public MemoryConsumers(IChannel _channel, IRootSearchConfig _engineConfiguration, boolean ownBookEnabled) {
		
		channel = _channel;
		
		engineConfiguration = _engineConfiguration;
		
		ChannelManager.getChannel().dump("OS arch: " + getJVMBitmode() + " bits");
		
		//ChannelManager.getChannel().dump(new Exception());
		
		//if (getAvailableMemory() / (1024 * 1024) < 63 - (JVMDLL_MEMORY_CONSUMPTION / (1024 * 1024))) {
		//	throw new IllegalStateException("Not enough memory. The engine needs from at least 64MB to run. Please increase the -Xmx option of Java VM");
		//}
		
		long availableMemory = (long) (MEMORY_USAGE_PERCENT * getAvailableMemory());
		long memoryBuffer = getAvailableMemory() - availableMemory;
		if (memoryBuffer < MIN_MEMORY_BUFFER) {
			memoryBuffer = MIN_MEMORY_BUFFER;
			availableMemory = getAvailableMemory() - MIN_MEMORY_BUFFER;
		}
		
		ChannelManager.getChannel().dump("JVM DLL memory consumption: " + (JVMDLL_MEMORY_CONSUMPTION / (1024 * 1024)) + "MB");
		
		ChannelManager.getChannel().dump("Available memory for the java process " + (getAvailableMemory() / (1024 * 1024)) + "MB");
		ChannelManager.getChannel().dump("Defined memory usage percent " + (MEMORY_USAGE_PERCENT * 100) + "%");
		ChannelManager.getChannel().dump("Memory the Engine will use " + (availableMemory / (1024 * 1024)) + "MB");
		
		ChannelManager.getChannel().dump("Initializing Memory Consumers ...");
		
		ChannelManager.getChannel().dump("SEE Metadata ... ");
		long lastAvailable_in_MB = ((getAvailableMemory() - memoryBuffer) / (1024 * 1024));
		seeMetadata = SeeMetadata.getSingleton();
		ChannelManager.getChannel().dump("SEE Metadata OK => " + (lastAvailable_in_MB - ((getAvailableMemory() - memoryBuffer) / (1024 * 1024))) + "MB");
		
		
		ChannelManager.getChannel().dump("Openning Book enabled: " + ownBookEnabled);
		//if (ownBookEnabled) {

			lastAvailable_in_MB = ((getAvailableMemory()) / (1024 * 1024));
			ChannelManager.getChannel().dump("Openning Book ... ");
			if (is_w_openning_book == null || is_b_openning_book == null) {
				ChannelManager.getChannel().dump("No file streams to openning book data");
			} else {
				try {
					openingBook = OpeningBookFactory.getBook(is_w_openning_book, is_b_openning_book);
					ChannelManager.getChannel().dump("Openning Book OK => " + (lastAvailable_in_MB - ((getAvailableMemory()) / (1024 * 1024))) + "MB");
				} catch(Exception e) {
					ChannelManager.getChannel().dump("Unable to load Openning Book. Error is:");
					channel.dump(e);
				}
			}

		//}
		

		ChannelManager.getChannel().dump("Loading modules for Gaviota Endgame Tablebases support ... ");
		
		gtbs			 = new Vector<GTBProbing>();
		
		int threadsCount = engineConfiguration.getThreadsCount();
		
		if (GTBProbing_NativeWrapper.tryToCreateInstance() != null) {
			
			for (int i=0; i<threadsCount; i++) {
				GTBProbing gtb = new GTBProbing();
				gtb.setPath_Sync(
						engineConfiguration.getGaviotaTbPath(),
						engineConfiguration.getGaviotaTbCache());
				gtbs.add(gtb);
			}
			
			//try {Thread.sleep(10000);} catch (InterruptedException e1) {}
			ChannelManager.getChannel().dump("Modules for Gaviota Endgame Tablebases OK. Will try to load Gaviota Tablebases from => " + engineConfiguration.getGaviotaTbPath());
		} else {
			//TODO: set percent to 0 and log corresponding message for the sizes
			//Can't load IA 32-bit .dll on a AMD 64-bit platform
			//throw new IllegalStateException("egtbprobe dynamic library could not be loaded (or not found)");
			ChannelManager.getChannel().dump(GTBProbing_NativeWrapper.getErrorMessage());
			
			for (int i=0; i<threadsCount; i++) {
				gtbs.add(null);
			}
		}
		
		
		ChannelManager.getChannel().dump("Caches (Transposition Table, Eval Cache and Pawns Eval Cache) ...");
		ChannelManager.getChannel().dump("Transposition Table usage percent from the free memory " + engineConfiguration.getThreadsCount() 			+ " X : " + (100 * engineConfiguration.getTPTUsagePercent()) + "%");
		ChannelManager.getChannel().dump("Endgame Table Bases Cache usage percent from the free memory " + engineConfiguration.getThreadsCount() 	+ " X : " + (100 * engineConfiguration.getGTBUsagePercent()) + "%");
		ChannelManager.getChannel().dump("Eval Cache usage percent from the free memory " + engineConfiguration.getThreadsCount() 					+ " X : " + (100 * engineConfiguration.getEvalCacheUsagePercent()) + "%");
		ChannelManager.getChannel().dump("Pawns Eval Cache usage percent from the free memory " + engineConfiguration.getThreadsCount() 			+ " X : " + (100 * engineConfiguration.getPawnsCacheUsagePercent()) + "%");
		
		double percents_sum = engineConfiguration.getThreadsCount() * engineConfiguration.getTPTUsagePercent()
							+ engineConfiguration.getThreadsCount() * engineConfiguration.getGTBUsagePercent()
							+ engineConfiguration.getThreadsCount() * engineConfiguration.getEvalCacheUsagePercent()
							+ engineConfiguration.getThreadsCount() * engineConfiguration.getPawnsCacheUsagePercent();
		
		if (percents_sum < 0.9999 || percents_sum > 1.0001) {
			throw new IllegalStateException("Percents sum is not near to 1. It is " + percents_sum);
		}
		
		/*
		try {
			semaphoreFactory = (IBinarySemaphoreFactory) SharedData.class.getClassLoader().loadClass(engineConfiguration.getSemaphoreFactoryClassName()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		*/
		
		initCaches(getAvailableMemory() - memoryBuffer);
		
		
		ChannelManager.getChannel().dump("Memory Consumers are initialized. Final available memory buffer is: " + (memoryBuffer / (1024 * 1024)) + "MB");
	}
	
	
	private void initCaches(long availableMemory) {
		
		ChannelManager.getChannel().dump("Initializing caches inside " + (int) (availableMemory / (1024 * 1024)) + "MB");
		
		//if (availableMemory / (1024 * 1024) < 10) {
		//	throw new IllegalStateException("Not enough memory. At least 10 MB are necessary for caches (only "
		//			+ (availableMemory / (1024 * 1024)) + " are available). Please increase the -Xmx option of Java VM");
		//}
		
		int size_tpt = Math.max(SIZE_MIN_ENTRIES_TPT, getTPTEntrySize(availableMemory, SIZE_MIN_ENTRIES_TPT));
		ChannelManager.getChannel().dump("Transposition Table size is " + size_tpt);
		
		int size_ec = Math.max(SIZE_MIN_ENTRIES_EC, getEvalCacheSize(availableMemory, SIZE_MIN_ENTRIES_EC));
		ChannelManager.getChannel().dump("Eval Cache size is " + size_ec);
		
		int size_pc = Math.max(SIZE_MIN_ENTRIES_PEC, getPawnsEvalCacheSize(availableMemory, engineConfiguration.getEvalConfig().getPawnsCacheFactoryClassName(), SIZE_MIN_ENTRIES_PEC));
		ChannelManager.getChannel().dump("Pawns Eval Cache size is " + size_pc);

		int size_gtb_out = 0;
		if (GTBProbing_NativeWrapper.tryToCreateInstance() != null) {
			size_gtb_out = Math.max(SIZE_MIN_ENTRIES_GTB, getGTBEntrySize_OUT(availableMemory, SIZE_MIN_ENTRIES_GTB));
			ChannelManager.getChannel().dump("Endgame Table Bases cache (OUT) size is " + size_gtb_out);
		}
		
		
		//Create
		
		evalCache 		= new Vector<IEvalCache>();
		pawnsCache		= new Vector<PawnsEvalCache>();
		tpt 			= new Vector<TPTable>();
		gtbCache_out 	= new Vector<GTBCache_OUT>();
		
		
		int threadsCount = engineConfiguration.getThreadsCount();
		for (int i=0; i<threadsCount; i++) {
			
			evalCache.add(new EvalCache(size_ec, false, new BinarySemaphore_Dummy()));
			//evalCache.add(new EvalCache1(5, size_ec, false, new BinarySemaphore_Dummy()));
			
			tpt.add(new TPTable(size_tpt, false, new BinarySemaphore_Dummy()));
			
			DataObjectFactory<PawnsModelEval> pawnsCacheFactory = (DataObjectFactory<PawnsModelEval>) ReflectionUtils.createObjectByClassName_NoArgsConstructor(engineConfiguration.getEvalConfig().getPawnsCacheFactoryClassName());
			pawnsCache.add(new PawnsEvalCache(pawnsCacheFactory, size_pc, false, new BinarySemaphore_Dummy()));
		
			if (GTBProbing_NativeWrapper.tryToCreateInstance() != null) {
				gtbCache_out.add(new GTBCache_OUT(size_gtb_out, false, new BinarySemaphore_Dummy()));
			} else {
				gtbCache_out.add(null);
			}
		}		
	}
	
	
	private int getTPTEntrySize(long availableMemory, int test_size) {
		int availableMemory_in_MB = (int) (availableMemory / (1024 * 1024));
		if (availableMemory_in_MB < 1) {
			//throw new IllegalStateException("Not enough memory for initializing Transposition Table. Please increase the -Xmx option of Java VM");
			availableMemory_in_MB = 1;
		}
		
		System.gc();
		int memory_before = getUsedMemory();
		TPTable test_tpt = new TPTable(test_size, true, null);
		int size = getEntrySize(availableMemory, engineConfiguration.getTPTUsagePercent(), test_size, memory_before);
		test_tpt.clear();
		return size;
	}
	
	
	private int getGTBEntrySize_OUT(long availableMemory, int test_size) {
		int availableMemory_in_MB = (int) (availableMemory / (1024 * 1024));
		if (availableMemory_in_MB < 1) {
			//throw new IllegalStateException("Not enough memory for initializing Endgame Table Bases cache (OUT). Please increase the -Xmx option of Java VM");
			availableMemory_in_MB = 1;
		}
		
		System.gc();
		int memory_before = getUsedMemory();
		GTBCache_OUT gtbCache = new GTBCache_OUT(test_size, true, null);
		int size = getEntrySize(availableMemory, engineConfiguration.getGTBUsagePercent(), test_size, memory_before);
		gtbCache.clear();
		return size;
	}

	private int getEvalCacheSize(long availableMemory, int test_size) {
		int availableMemory_in_MB = (int) (availableMemory / (1024 * 1024));
		if (availableMemory_in_MB < 1) {
			//throw new IllegalStateException("Not enough memory for initializing Eval Cache. Please increase the -Xmx option of Java VM");
			availableMemory_in_MB = 1;
		}
		
		System.gc();
		int memory_before = getUsedMemory();
		IEvalCache test_ec = new EvalCache(test_size, true, null);
		//IEvalCache test_ec = new EvalCache1(5, test_size, true, null);
		int size = getEntrySize(availableMemory, engineConfiguration.getEvalCacheUsagePercent(), test_size, memory_before);
		test_ec.clear();
		return size;
	}
	
	private int getPawnsEvalCacheSize(long availableMemory, String pawnsCacheName, int test_size) {
		int availableMemory_in_MB = (int) (availableMemory / (1024 * 1024));
		if (availableMemory_in_MB < 1) {
			//throw new IllegalStateException("Not enough memory for initializing Pawns Eval Cache. Please increase the -Xmx option of Java VM");
			availableMemory_in_MB = 1;
		}
		
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


	public List<TPTable> getTPT() {
		return tpt;
	}


	public List<IEvalCache> getEvalCache() {
		return evalCache;
	}


	public List<PawnsEvalCache> getPawnsCache() {
		return pawnsCache;
	}
	
	
	public List<GTBProbing> getGTBProbing() {
		return gtbs;
	}
	
	
	public List<GTBCache_OUT> getGTBCache_OUT() {
		return gtbCache_out;
	}
	
	
	public void clear() {
		tpt.clear();
		pawnsCache.clear();
		evalCache.clear();
		
		if (gtbCache_out != null) gtbCache_out.clear();
		if (gtbs != null) gtbs.clear();
	}
}

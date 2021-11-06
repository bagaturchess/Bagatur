package bagaturchess.search.impl.env;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import bagaturchess.bitboard.api.PawnsEvalCache;
//import bagaturchess.bitboard.impl.attacks.control.metadata.SeeMetadata;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.api.OpeningBookFactory;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.impl.eval.cache.EvalCache_Impl1;
import bagaturchess.search.impl.eval.cache.EvalCache_Impl2;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import bagaturchess.search.impl.tpt.ITTable;
import bagaturchess.search.impl.tpt.TTable_Impl1;
import bagaturchess.search.impl.tpt.TTable_Impl2;
import bagaturchess.search.impl.tpt.TranspositionTableProvider;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.api.IChannel;


public class MemoryConsumers {
	
	
	private static int JVMDLL_MEMORY_CONSUMPTION 						= 20 * 1024 * 1024;
	private static int MIN_MEMORY_BUFFER 								= 0;
	private static double MEMORY_USAGE_PERCENT 							= 0; 
	
	private static final int SIZE_MIN_ENTRIES_MULTIPLIER				= 111;
	private static final int SIZE_MIN_ENTRIES_TPT						= 8;
	private static final int SIZE_MIN_ENTRIES_EC						= 4;
	private static final int SIZE_MIN_ENTRIES_PEC						= 1 * SIZE_MIN_ENTRIES_MULTIPLIER;
	
	
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
		try {
			if (OpeningBookFactory.getBook() == null) {
				InputStream is_w_openning_book = new FileInputStream("./data/w.ob");
				InputStream is_b_openning_book = new FileInputStream("./data/b.ob");
				OpeningBookFactory.initBook(is_w_openning_book, is_b_openning_book);				
			}
		} catch(Throwable t) {
			ChannelManager.getChannel().dump("Unable to load Openning Book. Error while openning file streams: " + t.getMessage());
		}
	}
	
	
	private IRootSearchConfig engineConfiguration;
	
	//private SeeMetadata seeMetadata;
	private OpeningBook openingBook;
	
	private TranspositionTableProvider ttable_provider;
	private List<IEvalCache> evalCache;
	private List<PawnsEvalCache> pawnsCache;
	
	private IChannel channel;
	
	
	public MemoryConsumers(IChannel _channel, IRootSearchConfig _engineConfiguration, boolean ownBookEnabled) {
		
		channel = _channel;
		
		engineConfiguration = _engineConfiguration;
		
		ChannelManager.getChannel().dump("OS arch: " + getJVMBitmode() + " bits");
		
		/**
		 * The memory usage percent is very important because of 2 main reason:
		 * 1. Java VM runs GC (Garbage Collector) for less or more time (including "Stop the world" GC)
		 *   depending on the amount of used memory from the java process.
		 * 2. ELO strength is affected from the size of TPT, because of the used search algorithm MTD(f),
		 *    which visits same positions multiple times.
		 * The general rule is: the size of TPT should be enough for the engine to think one whole move without being filled on 100%
		 * In short time controls (fast games like 1/1) the size could be small and in long controls (long games 40/40) should be bigger.
		 * The selection bellow is optimized for long games.
		 */
		
		if (MIN_MEMORY_BUFFER == 0) {
			MIN_MEMORY_BUFFER 		= 5 * 1024 * 1024;//Set only if not set statically
		}
		if (MEMORY_USAGE_PERCENT == 0) {
			//0.29 for short games (e.g. 1/1), 0.69 for long games (e.g. 40/40)
			double memoryUsagePercent = 0.75;//(engineConfiguration.getTimeControlOptimizationType() == IRootSearchConfig.TIME_CONTROL_OPTIMIZATION_TYPE_40_40) ? 0.69 : 0.29;
			MEMORY_USAGE_PERCENT = memoryUsagePercent;//Set only if not set statically
		}
		
		
		//ChannelManager.getChannel().dump(new Exception());
		
		//if (getAvailableMemory() / (1024 * 1024) < 63 - (JVMDLL_MEMORY_CONSUMPTION / (1024 * 1024))) {
		//	throw new IllegalStateException("Not enough memory. The engine needs from at least 64MB to run. Please increase the -Xmx option of Java VM");
		//}
		
		
		ChannelManager.getChannel().dump("Defined memory usage percent " + (MEMORY_USAGE_PERCENT * 100) + "%");
		ChannelManager.getChannel().dump("Memory the Engine will use " + (getAvailableMemory() / (1024 * 1024)) + "MB");
		
		ChannelManager.getChannel().dump("Initializing Memory Consumers ...");
		
		//ChannelManager.getChannel().dump("SEE Metadata ... ");
		long lastAvailable_in_MB = ((getAvailableMemory()) / (1024 * 1024));
		//seeMetadata = SeeMetadata.getSingleton();
		//ChannelManager.getChannel().dump("SEE Metadata OK => " + (lastAvailable_in_MB - ((getAvailableMemory()) / (1024 * 1024))) + "MB");
		
		
		ChannelManager.getChannel().dump("Openning Book enabled: " + ownBookEnabled);
		//if (ownBookEnabled) {

			lastAvailable_in_MB = ((getAvailableMemory()) / (1024 * 1024));
			ChannelManager.getChannel().dump("Openning Book ... ");
			if (OpeningBookFactory.getBook() == null) {
				ChannelManager.getChannel().dump("No openning book");
			} else {
				try {
					openingBook = OpeningBookFactory.getBook();
					ChannelManager.getChannel().dump("Openning Book OK => " + (lastAvailable_in_MB - ((getAvailableMemory()) / (1024 * 1024))) + "MB");
				} catch(Exception e) {
					ChannelManager.getChannel().dump("Unable to load Openning Book. Error is:");
					channel.dump(e);
				}
			}
		//}
		
		
		ChannelManager.getChannel().dump("Loading modules for Endgame Tablebases support ... ");
		
		//int threadsCount = engineConfiguration.getThreadsCount();
		
		if (SyzygyTBProbing.getSingleton() != null) {
			
			//SyzygyTBProbing.getSingleton().load("C:/Users/i027638/OneDrive - SAP SE/DATA/OWN/chess/EGTB/syzygy");
			SyzygyTBProbing.getSingleton().load(engineConfiguration.getTbPath());
			
			//try {Thread.sleep(10000);} catch (InterruptedException e1) {}
			ChannelManager.getChannel().dump("Modules for Endgame Tablebases OK. Will try to load Tablebases from => " + engineConfiguration.getTbPath());
		} else {
			//TODO: set percent to 0 and log corresponding message for the sizes
			//Can't load IA 32-bit .dll on a AMD 64-bit platform
			//throw new IllegalStateException("egtbprobe dynamic library could not be loaded (or not found)");
			//ChannelManager.getChannel().dump(GTBProbing_NativeWrapper.getErrorMessage());
		}
		
		
		if (engineConfiguration.initCaches()) {
			
			ChannelManager.getChannel().dump("Caches (Transposition Table, Eval Cache and Pawns Eval Cache) ...");
			ChannelManager.getChannel().dump("Transposition Table usage percent from the free memory " + (100 * engineConfiguration.getTPTUsagePercent()) + "%");
			ChannelManager.getChannel().dump("Eval Cache usage percent from the free memory " + (100 * engineConfiguration.getEvalCacheUsagePercent()) + "%");
			ChannelManager.getChannel().dump("Pawns Eval Cache usage percent from the free memory " + (100 * engineConfiguration.getPawnsCacheUsagePercent()) + "%");
			
			double percents_sum = engineConfiguration.getTPTUsagePercent()
								+ engineConfiguration.getEvalCacheUsagePercent()
								+ engineConfiguration.getPawnsCacheUsagePercent();
			
			if (percents_sum < 0.95 || percents_sum > 1.05) {
				throw new IllegalStateException("Percents sum is not near to 1. It is " + percents_sum);
			}
			
			initCaches(getAvailableMemory());
		}
		
		ChannelManager.getChannel().dump("Memory Consumers are initialized.");
	}
	
	
	private void initCaches(long availableMemory) {
		
		
		ChannelManager.getChannel().dump("Initializing caches inside " + (int) (availableMemory / (1024 * 1024)) + "MB");
		
		
		int THREADS_COUNT 				= engineConfiguration.getThreadsCount();
		int TRANSPOSITION_TABLES_COUNT 	= Math.max(1, THREADS_COUNT / 32);
		
		ChannelManager.getChannel().dump("Threads are " + THREADS_COUNT);
		ChannelManager.getChannel().dump(TRANSPOSITION_TABLES_COUNT + " Transposition Table will be created.");
		
		
		int availableMemory_in_MB 		= (int) (availableMemory / (1024 * 1024));
		
		int size_tpt = Math.max(SIZE_MIN_ENTRIES_TPT, getPowerOf2SizeInMegabytes((int) (engineConfiguration.getTPTUsagePercent() * availableMemory_in_MB) / TRANSPOSITION_TABLES_COUNT));
		ChannelManager.getChannel().dump("Transposition Table size will be " + size_tpt + "MB"); 
		
		
		List<ITTable> ttables = new ArrayList<ITTable>();
		
		for (int i = 0; i < TRANSPOSITION_TABLES_COUNT; i++) {
			
			ChannelManager.getChannel().dump("Creating Transposition Table for the current Threads Group ...");
			ITTable current_ttable = new TTable_Impl2(size_tpt);
			ChannelManager.getChannel().dump("Transposition Table created.");
			
			ttables.add(current_ttable);
		}
		
		ttable_provider = new TranspositionTableProvider(ttables);
		
		
		int size_ec = 2 * Math.max(SIZE_MIN_ENTRIES_EC, getPowerOf2SizeInMegabytes((int) (engineConfiguration.getEvalCacheUsagePercent() * availableMemory_in_MB) / THREADS_COUNT));
		ChannelManager.getChannel().dump("Eval Cache size is " + size_ec + "MB");
		
		int size_pc = SIZE_MIN_ENTRIES_PEC;
		ChannelManager.getChannel().dump("Pawns Eval Cache size is " + size_pc + " entries.");
		
		/*int size_gtb_out = 0;
		if (GTBProbing_NativeWrapper.tryToCreateInstance() != null) {
			size_gtb_out = Math.max(SIZE_MIN_ENTRIES_GTB, getGTBEntrySize_OUT(availableMemory, 	Math.max(test_size1, SIZE_MIN_ENTRIES_GTB)));
			ChannelManager.getChannel().dump("Endgame Table Bases cache (OUT) size is " + size_gtb_out);
		}*/
		
		
		//Create
		evalCache 		= new Vector<IEvalCache>();
		pawnsCache		= new Vector<PawnsEvalCache>();
		
		
		for (int i = 0; i < THREADS_COUNT; i++) {
			
			int ttable_index = i % ttables.size();
			
			evalCache.add(new EvalCache_Impl2(size_ec));
			
			DataObjectFactory<PawnsModelEval> pawnsCacheFactory = (DataObjectFactory<PawnsModelEval>) ReflectionUtils.createObjectByClassName_NoArgsConstructor(engineConfiguration.getEvalConfig().getPawnsCacheFactoryClassName());
			pawnsCache.add(new PawnsEvalCache(pawnsCacheFactory, size_pc, false, new BinarySemaphore_Dummy()));
		}		
	}
	
	
	private int getPowerOf2SizeInMegabytes(int availableMemory_in_MB) {
		int size = (int) Math.pow(2, (int) (Math.log(availableMemory_in_MB) / Math.log(2)));
		if (size < 32) {
			size = 32;
		}
		return size;
	}
	
	
	/*private int getPowerOf2SizeInMegabytes_Minus(int availableMemory_in_MB) {
		int size = (int) Math.pow(2, -2 + (int) (Math.log(availableMemory_in_MB) / Math.log(2)));
		if (size < 32) {
			size = 32;
		}
		return size;
	}*/
	
	
	private long getAvailableMemory() {
		
		System.gc();
		
		return (long) (MEMORY_USAGE_PERCENT * Runtime.getRuntime().maxMemory());
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


	public OpeningBook getOpeningBook() {
		return openingBook;
	}


	public TranspositionTableProvider getTPTProvider() {
		return ttable_provider;
	}
	
	
	public List<IEvalCache> getEvalCache() {
		return evalCache;
	}


	public List<PawnsEvalCache> getPawnsCache() {
		return pawnsCache;
	}
	
	public void clear() {
		if (ttable_provider != null) ttable_provider.clear();
		if (evalCache != null) evalCache.clear();
		if (pawnsCache != null) pawnsCache.clear();
	}
}

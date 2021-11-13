package bagaturchess.egtb.cache;


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;


public class EGTBCache extends LRUMapLongObject<EGTBProbeOutput>{
	
	
	public EGTBCache(int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new GTPProbeDataFactory_OUT(), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	
	public EGTBProbeOutput get(long key) {
		EGTBProbeOutput result =  (EGTBProbeOutput) super.getAndUpdateLRU(key);
		return result;
	}
	
	
	public void put(long hashkey, int result, int move_to_mate) {
		EGTBProbeOutput entry = super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			//Multithreaded access
		} else {
			entry = associateEntry(hashkey);
		}
		((EGTBProbeOutput)entry).result = result;
		((EGTBProbeOutput)entry).movesToMate = move_to_mate;
	}
}

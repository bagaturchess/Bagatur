package bagaturchess.egtb.gaviota.cache;


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.egtb.gaviota.GTBProbeOutput;


public class GTBCache_OUT extends LRUMapLongObject<GTBProbeOutput>{
	
	
	public GTBCache_OUT(int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new GTPProbeDataFactory_OUT(), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	
	public GTBProbeOutput get(long key) {
		GTBProbeOutput result =  (GTBProbeOutput) super.getAndUpdateLRU(key);
		return result;
	}
	
	
	public void put(long hashkey, int result, int move_to_mate) {
		GTBProbeOutput entry = super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			//Multithreaded access
		} else {
			entry = associateEntry(hashkey);
		}
		((GTBProbeOutput)entry).result = result;
		((GTBProbeOutput)entry).movesToMate = move_to_mate;
	}
}

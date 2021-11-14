package bagaturchess.egtb.cache;


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.datastructs.list.ListNodeObject;
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.bitboard.impl.utils.BinarySemaphore;


public class GTBCache_IN extends LRUMapLongObject<EGTBProbeInput>{
	
	
	public GTBCache_IN(int _maxSize) {
		super(new GTPProbeDataFactory_IN(), _maxSize, true, new BinarySemaphore(), true);
	}
	
	
	public GTBCache_IN(int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new GTPProbeDataFactory_IN(), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	
	public EGTBProbeInput getEntryForFilling(long hashkey) {
		EGTBProbeInput entry =  (EGTBProbeInput) super.getAndUpdateLRU(hashkey);
		return entry;
	}

	
	public EGTBProbeInput reuseEntryForFilling(long hashkey) {
		EGTBProbeInput entry =  (EGTBProbeInput) super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			throw new IllegalStateException("hashkey=" + hashkey + ", entry=" + entry);
		} else {
			entry = associateEntry(hashkey);
		}
		return entry;
	}
	
	
	public ListNodeObject<EGTBProbeInput> removeHeadEntry() {
		return super.removeHeadEntry();
	}
	
	
	public void addHeadEntry(ListNodeObject<EGTBProbeInput> node) {
		super.addHeadEntry(node);
	}}

package bagaturchess.egtb.gaviota.cache;


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.datastructs.list.ListNodeObject;
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.bitboard.impl.utils.BinarySemaphore;
import bagaturchess.egtb.gaviota.GTBProbeInput;


public class GTBCache_IN extends LRUMapLongObject<GTBProbeInput>{
	
	
	public GTBCache_IN(int _maxSize) {
		super(new GTPProbeDataFactory_IN(), _maxSize, true, new BinarySemaphore(), true);
	}
	
	public GTBCache_IN(int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
		super(new GTPProbeDataFactory_IN(), _maxSize, fillWithDummyEntries, _semaphore);
	}
	
	public GTBProbeInput getEntryForFilling(long hashkey) {
		GTBProbeInput entry =  (GTBProbeInput) super.getAndUpdateLRU(hashkey);
		return entry;
	}

	
	public GTBProbeInput reuseEntryForFilling(long hashkey) {
		GTBProbeInput entry =  (GTBProbeInput) super.getAndUpdateLRU(hashkey);
		if (entry != null) {
			throw new IllegalStateException("hashkey=" + hashkey + ", entry=" + entry);
		} else {
			entry = associateEntry(hashkey);
		}
		return entry;
	}

	public ListNodeObject<GTBProbeInput> removeHeadEntry() {
		return super.removeHeadEntry();
	}
	
	
	public void addHeadEntry(ListNodeObject<GTBProbeInput> node) {
		super.addHeadEntry(node);
	}}

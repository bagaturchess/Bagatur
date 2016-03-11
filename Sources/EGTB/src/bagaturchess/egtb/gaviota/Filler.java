package bagaturchess.egtb.gaviota;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.impl.datastructs.list.ListNodeObject;
import bagaturchess.egtb.gaviota.cache.GTBCache_IN;
import bagaturchess.egtb.gaviota.cache.GTBCache_OUT;


public class Filler {
	
	private List<ListNodeObject<GTBProbeInput>> free;
	
	private GTBCache_OUT cache_out;
	private GTBCache_IN cache_in;
	
	private Thread thread;
	
	private int[] tmp_result;
	
	
	public Filler(GTBCache_OUT _cache_out, GTBCache_IN _cache_in) {
		
		cache_out = _cache_out;
		cache_in = _cache_in;
		
		tmp_result = new int[2];
		
		free = new ArrayList<ListNodeObject<GTBProbeInput>>();
		
		if (GTBProbing_NativeWrapper.getInstance() != null) {
			thread = new Thread(new Runnable() {
		        public void run() {
		        	fill();
		        }
		    });
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();			
		}
	}
	
	
	public ListNodeObject<GTBProbeInput> getFreeEntry() {
		synchronized (free) {
			if (free.size() == 0) {
				return null;
			}
			return free.remove(0);
		}
	}
	
	
	public void returnFreeEntry(ListNodeObject<GTBProbeInput> node) {
		if (cache_in.getCurrentSize() < cache_in.getMaxSize()) {
			node.setKey(node.getValue().hashkey);
			cache_in.addHeadEntry(node);
		} else {//dismiss this node in order not to overflow the list
			//TODO: Investigate further.
			//The overflow is cased by the parallelism - the usage of cache_in and free list in parallel.
		}
	}
	
	long dump_timestamp = System.currentTimeMillis();
	
	private void fill() {
		while (true) {
			
			cache_in.lock();
			ListNodeObject<GTBProbeInput> head = cache_in.removeHeadEntry();
			cache_in.unlock();
			
			/*if (System.currentTimeMillis() - dump_timestamp > 1000) {
				dump_timestamp = System.currentTimeMillis();
				System.out.println("FILLER: free size " + free.size() + ", cache_in size " + cache_in.getCurrentSize());
			}*/
			
			
			if (head == null) {
				try {Thread.sleep(100);} catch (InterruptedException e) {}
			} else {
				GTBProbeInput input = head.getValue();
				GTBProbing_NativeWrapper.getInstance().probeHard(input, tmp_result);
				
				cache_out.lock();
				GTBProbeOutput out = cache_out.get(input.hashkey);
				if (out == null) {
					cache_out.put(head.getKey(), tmp_result[0], tmp_result[1]);
				} else {
					out.result = tmp_result[0];
					out.movesToMate = tmp_result[1];
				}
				cache_out.unlock();
				
				
				synchronized (free) {
					free.add(head);
				}
			}
		}
	}
	
	
	public void stop() {
		if (thread != null) thread.destroy();
	}
}

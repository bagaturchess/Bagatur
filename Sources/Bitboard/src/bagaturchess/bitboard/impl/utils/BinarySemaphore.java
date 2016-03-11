package bagaturchess.bitboard.impl.utils;

import bagaturchess.bitboard.api.IBinarySemaphore;


public class BinarySemaphore implements IBinarySemaphore {
	
	
	private Object lock = new Object();
	private volatile boolean inUse = false;
	
	
	public void lock() {
		synchronized (lock) {
			while (inUse) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("INTERRUPTED: " + e);
				}
			}
			inUse = true;
		}
	}
	
	
	public void unlock() {
		synchronized (lock) {
			if (!inUse) {
				throw new IllegalStateException();
			}
			inUse = false;
			lock.notifyAll();
		}
	}
}

package bagaturchess.bitboard.impl.utils;


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.api.IBinarySemaphoreFactory;


public class BinarySemaphoreFactory_Dummy implements IBinarySemaphoreFactory {

	
	@Override
	public IBinarySemaphore createSempahore() {
		return new BinarySemaphore_Dummy();
	}
}

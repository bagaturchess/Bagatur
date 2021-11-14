package bagaturchess.egtb.gaviota;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.egtb.cache.EGTBProbing;
import bagaturchess.egtb.cache.EGTBProbeInput;


public class GTBProbing extends EGTBProbing {
	
	
	//private GTBProbing_NativeWrapper egtb_native_wrapper;
	
	
	public GTBProbing() {

		super();
		
		/*egtb_native_wrapper = GTBProbing_NativeWrapper.createInstance();
		
		if (egtb_native_wrapper == null) {
			
			throw new IllegalStateException(GTBProbing_NativeWrapper.getErrorMessage());
		}*/
	}
	
	
	@Override
	public void setPath_Sync(String tbPath, int memInMegabytes) {
		
		//egtb_native_wrapper.setPath_Sync(tbPath, memInMegabytes);
	}
	
	
	@Override
	public void fill(IBitBoard board, EGTBProbeInput input) {
		
		//egtb_native_wrapper.fill(board, input);
	}
	
	
	@Override
	public void probeHard(EGTBProbeInput input, int[] temp_out) {
		
		//egtb_native_wrapper.probeHard(input, temp_out);
	}
}

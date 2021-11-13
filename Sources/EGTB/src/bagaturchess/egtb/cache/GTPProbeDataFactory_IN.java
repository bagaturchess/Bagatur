package bagaturchess.egtb.cache;


import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;


public class GTPProbeDataFactory_IN implements DataObjectFactory<EGTBProbeInput> {
	
	
	public GTPProbeDataFactory_IN() {
	}
	
	
	public EGTBProbeInput createObject() {
		return new EGTBProbeInput();
	}
}

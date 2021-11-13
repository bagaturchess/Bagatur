package bagaturchess.egtb.cache;


import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;


public class GTPProbeDataFactory_OUT implements DataObjectFactory<EGTBProbeOutput> {
	
	
	public EGTBProbeOutput createObject() {
		return new EGTBProbeOutput();
	}
}

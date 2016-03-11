package bagaturchess.egtb.gaviota.cache;


import java.util.List;

import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.egtb.gaviota.GTBProbeInput;


public class GTPProbeDataFactory_IN implements DataObjectFactory<GTBProbeInput> {

	
	private List<Object> entries;
	
	
	public GTPProbeDataFactory_IN() {
	}
	
	
	public GTBProbeInput createObject() {
		return new GTBProbeInput();
	}
}

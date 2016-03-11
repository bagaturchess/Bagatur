package bagaturchess.egtb.gaviota.cache;

import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.egtb.gaviota.GTBProbeOutput;

public class GTPProbeDataFactory_OUT implements DataObjectFactory<GTBProbeOutput> {

	public GTBProbeOutput createObject() {
		return new GTBProbeOutput();
	}
}

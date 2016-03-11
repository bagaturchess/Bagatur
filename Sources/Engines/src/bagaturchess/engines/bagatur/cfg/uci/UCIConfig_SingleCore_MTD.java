package bagaturchess.engines.bagatur.cfg.uci;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.engines.bagatur.cfg.adaptor.UCISearchAdaptorConfig_SingleCore_MTD;
import bagaturchess.engines.base.cfg.UCIConfig_BaseImpl;


public class UCIConfig_SingleCore_MTD extends UCIConfig_BaseImpl {
	public UCIConfig_SingleCore_MTD(String[] args) {
		super(Utils.concat(
							new String[] {"bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove",
										  UCISearchAdaptorConfig_SingleCore_MTD.class.getName()
							},
							args
						)
		);
	}
}

package bagaturchess.engines.bagatur.cfg.uci;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.engines.bagatur.cfg.adaptor.UCISearchAdaptorConfig_SMP;
import bagaturchess.engines.base.cfg.UCIConfig_BaseImpl;


public class UCIConfig_SMP extends UCIConfig_BaseImpl {
	public UCIConfig_SMP(String[] args) {
		super(Utils.concat(
							new String[] {"bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove",
										//"bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingUCIStandard";
										  UCISearchAdaptorConfig_SMP.class.getName()
							},
							args
						)
		);
	}
}

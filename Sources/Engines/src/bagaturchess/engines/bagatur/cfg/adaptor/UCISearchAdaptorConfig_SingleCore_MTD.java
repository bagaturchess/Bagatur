package bagaturchess.engines.bagatur.cfg.adaptor;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.engines.bagatur.cfg.rootsearch.RootSearchConfig_SingleCore_MTD;
import bagaturchess.engines.base.cfg.UCISearchAdaptorConfig_BaseImpl;


public class UCISearchAdaptorConfig_SingleCore_MTD extends UCISearchAdaptorConfig_BaseImpl {

	public UCISearchAdaptorConfig_SingleCore_MTD(String[] args) {
		super(Utils.concat(
					new String[] {"bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch",
								RootSearchConfig_SingleCore_MTD.class.getName()
					},
					args
				)
		);
	}
}

package cfg;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.engines.base.cfg.UCISearchAdaptorConfig_BaseImpl;


public class UCISearchAdaptorConfig_SingleCore_MTD extends UCISearchAdaptorConfig_BaseImpl {

	public UCISearchAdaptorConfig_SingleCore_MTD(String[] args) {
		super(Utils.concat(
					new String[] {"bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch",
								RootSearchConfig_SingleCore_MTD_LEARNING.class.getName()
					},
					args
				)
		);
	}
}

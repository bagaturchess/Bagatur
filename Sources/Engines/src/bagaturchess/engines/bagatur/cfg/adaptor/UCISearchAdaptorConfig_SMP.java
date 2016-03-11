package bagaturchess.engines.bagatur.cfg.adaptor;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.engines.bagatur.cfg.rootsearch.RootSearchConfig_SMP;
import bagaturchess.engines.base.cfg.UCISearchAdaptorConfig_BaseImpl;


public class UCISearchAdaptorConfig_SMP extends UCISearchAdaptorConfig_BaseImpl {

	public UCISearchAdaptorConfig_SMP(String[] args) {
		super(Utils.concat(
					new String[] {"bagaturchess.search.impl.rootsearch.parallel.MTDParallelSearch",
								RootSearchConfig_SMP.class.getName()
					},
					args
				)
		);
	}
}

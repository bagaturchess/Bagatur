package cfg;

import bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl;



public class RootSearchConfig_SingleCore_MTD_LEARNING extends RootSearchConfig_BaseImpl {
	
	public RootSearchConfig_SingleCore_MTD_LEARNING() {
		this(new String[0]);
	}
	
	public RootSearchConfig_SingleCore_MTD_LEARNING(String[] args) {
		super(new String[] {"bagaturchess.search.impl.alg.SearchMTD", SearchConfigImpl_MTD.class.getName(), "cfg.BoardConfigImpl", "cfg.EvaluationConfg"});
	}
}

package bagaturchess.engines.bagatur.cfg.rootsearch;


import bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD;
import bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl;


public class RootSearchConfig_SingleCore_MTD extends RootSearchConfig_BaseImpl {
	
	public RootSearchConfig_SingleCore_MTD() {
		this(new String[0]);
	}
	
	public RootSearchConfig_SingleCore_MTD(String[] args) {
		super(new String[] {"bagaturchess.search.impl.alg.SearchMTD", SearchConfigImpl_MTD.class.getName(), "bagaturchess.engines.bagatur.cfg.board.BoardConfigImpl", "bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2"});
	}
}

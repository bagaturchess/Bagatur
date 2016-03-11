package bagaturchess.engines.bagatur.cfg.rootsearch;


import bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_AB;
import bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl;


public class RootSearchConfig_SingleCore_AB extends RootSearchConfig_BaseImpl {
	
	public RootSearchConfig_SingleCore_AB() {
		this(new String[0]);
	}
	
	public RootSearchConfig_SingleCore_AB(String[] args) {
		super(new String[] {"bagaturchess.search.impl.alg.SearchAB", SearchConfigImpl_AB.class.getName(), "bagaturchess.engines.bagatur.cfg.board.BoardConfigImpl", "bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2"});
	}
}

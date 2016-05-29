package bagaturchess.engines.base.cfg;


import bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl;
import bagaturchess.search.api.IRootSearchConfig_Single;
import bagaturchess.uci.api.IUCIOptionsProvider;


public class RootSearchConfig_BaseImpl_1Core extends RootSearchConfig_BaseImpl implements IRootSearchConfig_Single, IUCIOptionsProvider {
	
	
	public RootSearchConfig_BaseImpl_1Core(String[] args) {
		super(args);
	}
}
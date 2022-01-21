package bagaturchess.engines.cfg.base;


import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.IRootSearchConfig_Single;
import bagaturchess.uci.api.IUCIOptionsProvider;


public class RootSearchConfig_BaseImpl_1Core extends RootSearchConfig_BaseImpl implements IRootSearchConfig_Single, IUCIOptionsProvider {
	
	
	public static final IRootSearchConfig EVALIMPL4 = new RootSearchConfig_BaseImpl_1Core(
			
			new String[] {
							bagaturchess.search.impl.alg.impl1.Search_PVS_NWS.class.getName(),
							bagaturchess.engines.cfg.base.SearchConfigImpl_AB.class.getName(),
							bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20.class.getName(),
							bagaturchess.learning.goldmiddle.impl4.cfg.EvaluationConfig_V20.class.getName(),
				}
			);
	
	public static final IRootSearchConfig NNUE = new RootSearchConfig_BaseImpl_1Core(
			
			new String[] {
							bagaturchess.search.impl.alg.impl1.Search_PVS_NWS.class.getName(),
							bagaturchess.engines.cfg.base.SearchConfigImpl_AB_SkipTTable.class.getName(),
							bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20.class.getName(),
							bagaturchess.deeplearning.impl_nnue.eval.EvaluationConfig.class.getName(),
				}
			);
	
	public static final IRootSearchConfig NNUE_EVALIMPL4 = new RootSearchConfig_BaseImpl_1Core(
			
			new String[] {
							bagaturchess.search.impl.alg.impl1.Search_PVS_NWS.class.getName(),
							bagaturchess.engines.cfg.base.SearchConfigImpl_AB_SkipTTable.class.getName(),
							bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20.class.getName(),
							bagaturchess.learning.goldmiddle.impl4.cfg.EvaluationConfig_V20_SkipEvalCache.class.getName(),
				}
			);

	
	
	public RootSearchConfig_BaseImpl_1Core(String[] args) {
		super(args);
	}
}
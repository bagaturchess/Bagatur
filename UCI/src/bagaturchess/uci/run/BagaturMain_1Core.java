package bagaturchess.uci.run;


public class BagaturMain_1Core {
	
	
	public static void main(String[] _args) {

		String[] args = new String[] {
			"bagaturchess.engines.cfg.base.UCIConfig_BaseImpl",
			"bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove",
			"bagaturchess.engines.cfg.base.UCISearchAdaptorConfig_BaseImpl",
			"bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD",
			"bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_1Core",
			"bagaturchess.search.impl.alg.impl1.Search_PVS_NWS",
			"bagaturchess.engines.cfg.base.SearchConfigImpl_AB",
			"bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20",
			"bagaturchess.deeplearning.impl_nnue_v3.EvaluationConfig"
			//"bagaturchess.deeplearning.impl_nnue_v2b.EvaluationConfig"
		};
		
		Boot.main(args);
	}

}

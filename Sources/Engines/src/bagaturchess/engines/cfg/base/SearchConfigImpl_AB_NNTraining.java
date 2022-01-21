package bagaturchess.engines.cfg.base;


public class SearchConfigImpl_AB_NNTraining extends SearchConfigImpl_AB {
	
	
	@Override
	public boolean isOther_UseTPTScores() {
		return false;
	}
	
	
	@Override
	public boolean isOther_UseAlphaOptimizationInQSearch() {
		return false;
	}
}

package bagaturchess.engines.material;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.evalcache.EvalCache;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class MaterialEvaluator extends BaseEvaluator {
	
	
	public MaterialEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		super(_bitboard, _evalCache, _evalConfig);
	}
	
	
	@Override
	public void beforeSearch() {
		super.beforeSearch();
	}
	
	
	@Override
	public int getMaterialQueen() {
		return 50 + baseEval.getMaterialQueen();
	}
	
	
	@Override
	protected double phase2_opening() {
		return 0;
	}
	
	
	@Override
	protected double phase3_opening() {
		return 0;
	}
	
	
	@Override
	protected double phase4_opening() {
		return 0;
	}
	
	
	@Override
	protected double phase5_opening() {
		return 0;
	}
	
	
	@Override
	protected double phase2_endgame() {
		return 0;
	}
	
	
	@Override
	protected double phase3_endgame() {
		return 0;
	}
}

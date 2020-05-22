package bagaturchess.engines.cfg.materialonly;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class MaterialEvaluator extends BaseEvaluator {
	
	
	public MaterialEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		super(_bitboard, _evalCache, _evalConfig);
	}
	
	
	@Override
	protected double phase1() {
		return eval_material_nopawnsdrawrule() + interpolator.interpolateByFactor(baseEval.getPST_o(), baseEval.getPST_e());
	}
	
	
	@Override
	protected double phase2() {
		return 0;
	}
	
	
	@Override
	protected double phase3() {
		return 0;
	}
	
	
	@Override
	protected double phase4() {
		return 0;
	}
	
	
	@Override
	protected double phase5() {
		return 0;
	}
}

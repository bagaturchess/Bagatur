package bagaturchess.deeplearning.impl_nnue.visitors;


import bagaturchess.search.api.IEvaluator;


public class ActivationFunctions {
	
	
	private static final double SIGMOID_LOG_BASE = 1.005;
	
	
	public static final float sigmoid_gety(float x) {
		
		float y = 1f / (float) (1f + 1f / Math.pow(SIGMOID_LOG_BASE, x));
		
		if (y < IEvaluator.MIN_EVAL) {
			
			y = IEvaluator.MIN_EVAL;
		}
		
		if (y > IEvaluator.MAX_EVAL) {
			
			y = IEvaluator.MAX_EVAL;
		}
		
		return y;
	}
	
	
	public static final float sigmoid_getx(float y) {
		
		if (y < 0 || y > 1) {
			
			throw new IllegalStateException("y=" + y);
		}
		
		float x = (float) (Math.log(y / (1d - y)) / Math.log(SIGMOID_LOG_BASE));
		
		return x;
	}
}

package bagaturchess.deeplearning;


import bagaturchess.search.api.IEvaluator;


class ActivationFunctions {
	
	
	private static final double SIGMOID_LOG_BASE = 1.005;
	
	
	public static final float sigmoid_gety(float x) {
		
		float y = 1f / (float) (1f + 1f / Math.pow(SIGMOID_LOG_BASE, x));
		
		return y;
	}
	
	
	public static final float sigmoid_getx(float y) {
		
		if (y < 0 || y > 1) {
			
			throw new IllegalStateException("y=" + y);
		}
		
		float x = (float) (Math.log(y / (1d - y)) / Math.log(SIGMOID_LOG_BASE));
		
		if (x < IEvaluator.MIN_EVAL) {
			
			x = IEvaluator.MIN_EVAL;
		}
		
		if (x > IEvaluator.MAX_EVAL) {
			
			x = IEvaluator.MAX_EVAL;
		}
		
		return x;
	}
}

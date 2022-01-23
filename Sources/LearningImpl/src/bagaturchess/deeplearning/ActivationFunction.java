package bagaturchess.deeplearning;


import bagaturchess.search.api.IEvaluator;


public abstract class ActivationFunction {
	
	
	public static final ActivationFunction SIGMOID = new Sigmoid();
	
	public static final ActivationFunction LINEAR = new Linear();
	
	
	public abstract float gety(float x);
	
	public abstract float getx(float y);
	
	
	private static final class Sigmoid extends ActivationFunction {

		
		private static final double SIGMOID_LOG_BASE 	= 1.005;
		
		/*public static final float SIGMOID_WIN_BLACK 	= ActivationFunctions.sigmoid_gety(IEvaluator.MIN_EVAL);
		
		public static final float SIGMOID_DRAW 			= 0.5f;
		
		public static final float SIGMOID_WIN_WHITE 	= ActivationFunctions.sigmoid_gety(IEvaluator.MAX_EVAL);
		*/
		
		
		@Override
		public float gety(float x) {
			
			float y = 1f / (float) (1f + 1f / Math.pow(SIGMOID_LOG_BASE, x));
			
			return y;
		}
		
		
		@Override
		public float getx(float y) {
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
	
	
	private static final class Linear extends ActivationFunction {
		
		
		@Override
		public float gety(float x) {
			return x;
		}
		
		
		@Override
		public float getx(float y) {
			return y;
		}
	}
}

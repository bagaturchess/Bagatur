package bagaturchess.deeplearning;


public abstract class ActivationFunction {
	
	
	public static final ActivationFunction SIGMOID = new Sigmoid(7777);
	
	public static final ActivationFunction LINEAR = new Linear(7777);
	
	
	protected float max_x;
	
	
	protected ActivationFunction(float max_x) {
		
		this.max_x = max_x;
	}
	
	
	public abstract float gety(float x);
	
	public abstract float getx(float y);
	
	
	public static final class Sigmoid extends ActivationFunction {

		
		private static final double SIGMOID_LOG_BASE 	= 1.005;
		
		
		protected Sigmoid(float max) {
			
			super(max);
		}
		
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
			
			if (x < -max_x) {
				
				x = -max_x;
			}
			
			if (x > max_x) {
				
				x = max_x;
			}
			
			return x;
		}
	}
	
	
	public static final class Linear extends ActivationFunction {
		
		
		protected Linear(float max) {
			
			super(max);
		}
		
		
		@Override
		public float gety(float x) {
			
			if (x < -max_x) {
				
				x = -max_x;
			}
			
			if (x > max_x) {
				
				x = max_x;
			}
			
			return x;
		}
		
		
		@Override
		public float getx(float y) {
			
			if (y < -max_x) {
				
				y = -max_x;
			}
			
			if (y > max_x) {
				
				y = max_x;
			}
			
			return y;
		}
	}
}

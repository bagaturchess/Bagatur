package bagaturchess.learning.impl.features.advanced;


import java.io.Serializable;

import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.bitboard.impl.utils.VarStatistic;


class Weight implements Serializable {
	
	
	private static final long serialVersionUID 	= 3805221518234137798L;
	
	
	private static final double LEARNING_RATE 	= 0.25f; //1f; //0.5f; //0.01; //0.02; //0.1; //1;
	
	private static final double MIN_WEIGHT 		= 0.1;
	
	private static final int STEPS_COUNT 		= 100;
	
	
	private double initial;
	
	private double min;
	
	private double max;
	
	private VarStatistic total;
	
	private VarStatistic total_movements;
	
	private VarStatistic current;
	
	private double STEP = 1 / (double) STEPS_COUNT;
	
	
	public Weight(double min, double max, double _initialVal, boolean _norm_adjustment) {
		
		this(min, max, _initialVal);
	}
	
	
	public Weight(double min, double max, double _initialVal) {
		
		if (min > max)	{
			
			throw new IllegalStateException("min > max: min=" + min + ", max=" + max);
		}
		
		if (min < 0) {
			
			throw new IllegalStateException("min < 0: min=" + min);
		}
		
		if (_initialVal < 0) {
			
			throw new IllegalStateException("initialVal < 0: initialVal=" + initial);
		}
		
		initial = _initialVal;
		
		
		this.min = min;
		
		this.max = max;

		STEP = Math.abs(max - min) / (double) STEPS_COUNT;
		
		
		if (initial < min || initial > max) throw new IllegalStateException("initialVal=" + initial);
		
		
		total = new VarStatistic();
		
		total_movements = new VarStatistic();
		
		reset();
		
		
		current = new VarStatistic();
	}
	
	
	protected void merge(Weight other) {
		
		throw new UnsupportedOperationException();
	}
	
	
	public void clear() {
		
		current = new VarStatistic();
	}
	
	
	public void multiplyCurrentWeightByAmountAndDirection() {
		
		
		if (current.getTotalAmount() == 0) {
			
			return;
		}
		
		
		double current_movement = (current.getTotalDirection() / current.getTotalAmount());
		//double current_movement = current.getEntropy() / 2;
		
		//System.out.println("current_movement=" + current_movement);
		
		
		//total_movements.addValue(current_movement);
		
		if (current_movement > 0) {
			
			total_movements.addValue(+1);
			
			//current_movement = STEP;
			
		} else if (current_movement < 0) {
			
			total_movements.addValue(-1);
			
			//current_movement = -STEP;
		}
		
		current_movement *= getLearningSpeed();
		//current_movement *= LEARNING_RATE;
		
		
		double avg = total.getEntropy();
		
		if (avg > 0) {
			
			total.addValue(avg + avg * current_movement);
			
		} else if (avg < 0) {
			
			if (true) throw new IllegalStateException();
			
			total.addValue(avg - -current_movement);
			
		} else {
			
			if (true) throw new IllegalStateException();
			
			reset();
		}
		
		
		if (total.getEntropy() < min) {
			
			if (true) throw new IllegalStateException();
			
			total = new VarStatistic();
			
			reset();
		}
	}
	
	
	private void reset() {
		
		if (initial == 0) {
			
			total.addValue(Math.max(MIN_WEIGHT, Math.random()));
			
		} else {
			
			total.addValue(Math.max(MIN_WEIGHT, initial));
		}
	}
	
	
	public double getWeight() {
		
		return total.getEntropy();
	}
	
	
	public double getLearningSpeed() {
		
		if (total_movements.getTotalAmount() == 0) {
			
			return 1;
		}
		
		return Math.abs(total_movements.getTotalDirection() / total_movements.getTotalAmount());
	}
	
	
	strictfp void adjust(double amount) {
		
		//1 and -1 are probably derivatives of the liner function. we use 1 and -1 and we apply them in Epochs of at least 100 games into the dataset and also use learning rate.
		if (amount != 1 && amount != -1) {
			
			throw new IllegalStateException();
		}
		
		current.addValue(amount);
	}
	
	
	@Override
	public String toString() {
		
		String result = "";
		
		result += StringUtils.fill("[" + min + "-" + max + "] ", 8);
		result += "initial: " + StringUtils.align(initial);
		//result += ", avg: " + StringUtils.align(avg());
		result += ", current: " + StringUtils.align(total.getEntropy());
		//result += ", [" + current + "]";
		/*result += " cur=" + cut("" + cur_weight) + ", avgidx=" + avg + ", avg=" + cut("" + (max_adjustment * avg));
		result += ", prec=" + max_adjustment;
		result += "	[";
		for (int i=0; i<distribution.length; i++) {
			result += distribution[i] + ", ";
		}
		result += "]";*/
		
		return result;
	}
}

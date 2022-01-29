package bagaturchess.learning.impl.features.advanced;


import java.io.Serializable;

import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.bitboard.impl.utils.VarStatistic;


class Weight implements Serializable {
	
	
	private static final long serialVersionUID 	= 3805221518234137798L;
	
	private static final double LEARNING_RATE 	= 0.0333; //0.1; //1;
	
	private static final double MIN_WEIGHT 		= 0.1;
	
	
	private double initialVal;
	private double min_weight;
	private double max_weight;
	
	private VarStatistic total;
	private VarStatistic current;
	
	
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
			
			throw new IllegalStateException("initialVal < 0: initialVal=" + initialVal);
		}
		
		initialVal = _initialVal;
		
		
		min_weight = min;
		max_weight = max;

		if (initialVal < min_weight || initialVal > max_weight) throw new IllegalStateException("initialVal=" + initialVal);
		
		
		total = new VarStatistic();
		
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
		
		
		double multiplier = (current.getTotalDirection() / current.getTotalAmount());
		
		//System.out.println("multiplier=" + multiplier);
		//System.out.println("total.getDisperse()=" + total.getDisperse());
		
		multiplier *= LEARNING_RATE;
		//multiplier *= Math.max(1, total.getDisperse());
		
		//Multiply the weight
		
		double avg = total.getEntropy();
		
		if (avg > 0) {
			
			total.addValue(avg + avg * multiplier);
			
		} else if (avg < 0) {
			
			total.addValue(avg - avg * multiplier);
			
		} else {
			
			reset();
		}
		
		if (total.getEntropy() < min_weight) {
			
			total = new VarStatistic();
			
			reset();
		}
	}
	
	
	private void reset() {
		
		if (initialVal == 0) {
			
			total.addValue(Math.max(MIN_WEIGHT, Math.random()));
			
		} else {
			
			total.addValue(Math.max(MIN_WEIGHT, initialVal));
		}
	}
	
	
	public double getWeight() {
		
		return total.getEntropy();
	}
	
	
	strictfp void adjust(double amount) {
		
		//For liner function derivatives we use 1 and -1. We apply them in Epochs of at least 100 elements into the dataset and also use learning rate.
		if (amount != 1 && amount != -1) {
			
			throw new IllegalStateException();
		}
		
		current.addValue(amount);
	}
	
	
	@Override
	public String toString() {
		
		String result = "";
		
		result += StringUtils.fill("[" + min_weight + "-" + max_weight + "] ", 8);
		result += "initial: " + StringUtils.align(initialVal);
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

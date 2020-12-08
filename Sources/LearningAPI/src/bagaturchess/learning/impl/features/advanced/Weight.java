package bagaturchess.learning.impl.features.advanced;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.bitboard.impl.utils.VarStatistic;


class Weight implements Serializable {
	
	
	private static final long serialVersionUID = 3805221518234137798L;
	
	//private static final double DELTA = 0.000001;
	private static final double DELTA = 0.000001;
	
	private static final double MAX_ADJUSTMENT = 100;
	private boolean norm_adjustment = false;
	
	private double initialVal;
	private double min_weight;
	private double max_weight;
	private double cur_weight;
	
	private double norm;
	
	private double max_adjustment;
	
	private VarStatistic varstat;
	private List<Double> appliedMultipliers;
	
	
	public Weight(double min, double max, double _initialVal, boolean _norm_adjustment) {
		this(min, max, _initialVal);
		norm_adjustment = _norm_adjustment;
	}
	
	
	public Weight(double min, double max, double _initialVal) {
		
		//min = -2000;
		//max = 2000;
		
		initialVal = _initialVal;
		
		if (min > max)	throw new IllegalStateException();
		//if (min < 0)	throw new IllegalStateException();
		
		min_weight = min;
		max_weight = max;
		norm = Math.max(Math.abs(min_weight), Math.abs(max_weight));
		max_adjustment = (max_weight - min_weight) / MAX_ADJUSTMENT;
		
		if (max_adjustment < 0) throw new IllegalStateException();
		
		cur_weight = initialVal;
		
		if (initialVal < min_weight || initialVal > max_weight) throw new IllegalStateException("initialVal=" + initialVal);
		
		if (cur_weight < min) throw new IllegalStateException("cur_weight=" + cur_weight + " min=" + min);
		if (cur_weight > max) throw new IllegalStateException();
		
		varstat = new VarStatistic(false);
		
		if (min == max) {
			varstat.setEntropy(min);
		}
		
		appliedMultipliers = new ArrayList<Double>();
	}
	
	
	protected void merge(Weight other) {
		if (other.min_weight != min_weight) min_weight = other.min_weight;
		if (other.max_weight != max_weight) max_weight = other.max_weight;
	}
	
	
	public void clear() {
		varstat = new VarStatistic(false);
	}
	
	
	public void multiplyCurrentWeightByAmountAndDirection() {
		
		if (varstat.getTotalAmount() == 0) {
			return;
		}
		
		double multiplier = (varstat.getTotalDirection() / varstat.getTotalAmount());
		
		//Should be added before changing
		appliedMultipliers.add(multiplier);
		
		while (appliedMultipliers.size() > 10) {
			appliedMultipliers.remove(0);
		}
		
		double all = 0;
		double dir = 0;
		for (Double cur: appliedMultipliers) {
			all += Math.abs(cur);
			dir += cur;
		}
		
		if (all > 0 && dir != 0) {
			multiplier *= Math.abs(dir / all);
		}
		
		
		if (multiplier == 0) {
			return;
		}
		
		
		//Multiply
		if (cur_weight > 0) {
			cur_weight += cur_weight * multiplier;
		} else if (cur_weight < 0) {
			cur_weight -= cur_weight * multiplier;
		} else {
			//Initialize
			//cur_weight = multiplier;
			if (multiplier > 0) {
				cur_weight = 1;
			} else if (multiplier < 0) {
				cur_weight = -1;
			}

		}
		
		double LOWEST = 1 / 100.0;
		if (cur_weight > 0 && cur_weight < LOWEST) {
			cur_weight = -LOWEST;
		}
		if (cur_weight < 0 && cur_weight > -LOWEST) {
			cur_weight = LOWEST;
		}
		
		//Norm
		if (cur_weight < min_weight) {
			cur_weight = min_weight;
		}
		if (cur_weight > max_weight) {
			cur_weight = max_weight;
		}
	}
	
	
	public double getWeight() {
		//if (useAverageWeights) {
		//	return varstat.getEntropy();
		//} else {
		return cur_weight;
		//}
	}
	
	strictfp void adjust(double amount) {
		
		/*if (amount != 1 && amount != -1) {
			throw new IllegalStateException();
		}*/
		
		varstat.addValue(amount, amount);
	}
	
	
	@Override
	public String toString() {
		String result = "";
		
		result += StringUtils.fill("[" + min_weight + "-" + max_weight + "] ", 8);
		result += "init: " + StringUtils.align(initialVal);
		//result += ", avg: " + StringUtils.align(avg());
		result += ", cur: " + StringUtils.align(cur_weight);
		result += ", [" + varstat + "]";
		/*result += " cur=" + cut("" + cur_weight) + ", avgidx=" + avg + ", avg=" + cut("" + (max_adjustment * avg));
		result += ", prec=" + max_adjustment;
		result += "	[";
		for (int i=0; i<distribution.length; i++) {
			result += distribution[i] + ", ";
		}
		result += "]";*/
		
		return result;
	}

	public VarStatistic getVarstat() {
		return varstat;
	}
}

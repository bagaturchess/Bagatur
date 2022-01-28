package bagaturchess.learning.impl.features.advanced;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.bitboard.impl.utils.VarStatistic;


class Weight1 implements Serializable {
	
	
	private static final long serialVersionUID = 3805221518234137798L;
	
	private static final double MAX_ADJUSTMENT = 10000;
	
	
	private double initialVal;
	private double min_weight;
	private double max_weight;
	private double cur_weight;
	
	private double step_adjustment;
	
	private VarStatistic varstat;
	
	private List<Double> appliedMultipliers;
	
	
	public Weight1(double min, double max, double _initialVal, boolean _norm_adjustment) {
		
		this(min, max, _initialVal);
	}
	
	
	public Weight1(double min, double max, double _initialVal) {
		
		if (min < 0) {
			
			throw new IllegalStateException("min=" + min);
		}
		
		initialVal = _initialVal;
		
		if (initialVal < 0) {
			
			throw new IllegalStateException("initialVal=" + initialVal);
		}
		
		if (min > max)	throw new IllegalStateException();
		
		min_weight = min;
		max_weight = max;
		step_adjustment = (max_weight - min_weight) / MAX_ADJUSTMENT;
		
		if (step_adjustment < 0) throw new IllegalStateException();
		
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
	
	
	protected void merge(Weight1 other) {
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
		
		if (multiplier == 0) {
			
			return;
			
		} else if (multiplier > 0) {
			
			cur_weight += step_adjustment;
			
		} else {
			
			cur_weight -= step_adjustment;
		}
		
		//System.out.println("multiplier=" + multiplier);
		
		//multiplier /= Math.max(1, appliedMultipliers.size());
		
		//Should be added before changing
		//appliedMultipliers.add(multiplier);
		
		/*while (appliedMultipliers.size() > 15) {
			
			appliedMultipliers.remove(0);
		}*/
		
		
		/*double all = 0;
		
		double dir = 0;
		
		for (Double cur: appliedMultipliers) {
			
			all += Math.abs(cur);
			
			dir += cur;
		}
		
		if (all > 0 && dir != 0) {
			
			multiplier *= Math.abs(dir / all);
			//multiplier *= (dir / all);
		}*/
		
		
		//Multiply the weight
		/*if (cur_weight > 0) {
			
			cur_weight += cur_weight * multiplier;
			
		} else if (cur_weight < 0) {
			
			cur_weight -= cur_weight * multiplier;
			
		} else {
			
			//Initialize the weight
			if (multiplier > 0) {
				
				cur_weight = 1;
				
			} else if (multiplier < 0) {
				
				cur_weight = -1;
			}
		}*/
		
		
		//Make sign change if necessary
		//SWITCHED ON:  +7%, +9%, +2%, +0.5%, +0.4%, -1.6%, +0.8%, -0.35%, +0.35%, -0.26%
		//SWITCHED OFF: +7%, +9%, +1.7%, -0.26%, 
		
		double LOWEST = 1 / MAX_ADJUSTMENT;
		
		if (cur_weight > 0 && cur_weight < LOWEST) {
			
			/*cur_weight = -LOWEST;
			
			throw new IllegalStateException("cur_weight < LOWEST");*/
			
			cur_weight = 0;
		}
		
		if (cur_weight < 0 && cur_weight > -LOWEST) {
			
			/*cur_weight = LOWEST;
			
			throw new IllegalStateException("cur_weight > -LOWEST");*/
			
			cur_weight = 0;
		}
		
		
		//Keep it in bounds
		if (cur_weight < min_weight) {
			
			cur_weight = min_weight;
		}
		
		if (cur_weight > max_weight) {
			
			cur_weight = max_weight;
		}
	}
	
	
	public double getWeight() {
		
		return cur_weight;
	}
	
	
	strictfp void adjust(double amount) {
		
		if (amount != 1 && amount != -1) {
			
			throw new IllegalStateException();
		}
		
		varstat.addValue(amount);
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
}



package bagaturchess.learning.impl.features;


import java.io.Serializable;

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
	}
	
	public void clearStat() {
		varstat = new VarStatistic(false);
	}
	
	public boolean fitBounds(double buffer) {
		if (min_weight > 0) {
			if (min_weight != max_weight) {
				throw new IllegalStateException(this.toString());
			}
			return false;
		}
		if (max_weight < 0) {
			if (min_weight != max_weight) {
				throw new IllegalStateException(this.toString());
			}
			return false;
		}
		
		boolean changed = false;
		if (cur_weight <= 0) {
			double minBound = Math.abs(min_weight);
			double curBound = Math.abs(cur_weight);
			double curFill = curBound / minBound;
			
			if (1 - curFill < buffer) {
				min_weight = min_weight * (curFill + buffer);
				changed = true;
			}
		}
		
		if (cur_weight >= 0) {
			double maxBound = Math.abs(max_weight);
			double curBound = Math.abs(cur_weight);
			double curFill = curBound / maxBound;
			
			if (1 - curFill < buffer) {
				max_weight = max_weight * (curFill + buffer);
				changed = true;
			}
		}
		
		return changed;
	}
	
	public void set(double _min, double _max, double _initialVal) {
		
		//_min = -2000;
		//_max = 2000;
		
		if (_min == _max && _max == _initialVal) {
			//Do nothing
			min_weight = 1;
			max_weight = 1;
			cur_weight = 1;
			norm = 0;
			max_adjustment = 0;
			varstat.setEntropy(1);
		} else {
			
			if (_min > _max) {
				throw new IllegalStateException();
			}
			
			/*if (_min < min_weight) {
				_min = min_weight;
				//throw new IllegalStateException();
			}
			if (_max > max_weight) {
				_max = max_weight;
				//throw new IllegalStateException();
			}*/
			if (_initialVal > _max || _initialVal < _min) {
				throw new IllegalStateException();
			}
			
			min_weight = _min;
			max_weight = _max;
			norm = Math.max(Math.abs(min_weight), Math.abs(max_weight));
			cur_weight = _initialVal;
			max_adjustment = (max_weight - min_weight) / MAX_ADJUSTMENT;
		}
	}
	
	public void multiplyAvarageAndCopyToCurrent(Weight weight) {
		
		//if (varstat.getDisperse() >= weight.varstat.getDisperse()) {
		
			double newEntropy = getAverage() * weight.getAverage();
			
			/*double newEntropy = 0;
			
			if (getAverage() >= 0 && weight.getAverage() >= 0) {
				newEntropy = getAverage() * weight.getAverage();
			} else if (getAverage() <= 0 && weight.getAverage() <= 0) {
				newEntropy = getAverage() * weight.getAverage();
			} else if (getAverage() >= 0 && weight.getAverage() <= 0) {
				//throw new IllegalStateException("1 getAverage()=" + getAverage() + ", weight.getAverage()=" + weight.getAverage());
				newEntropy = getAverage() * weight.getAverage();
			} else if (getAverage() <= 0 && weight.getAverage() >= 0) {
				//throw new IllegalStateException("2 getAverage()=" + getAverage() + ", weight.getAverage()=" + weight.getAverage());
				newEntropy = - getAverage() * weight.getAverage();
			} else {
				throw new IllegalStateException();
			}*/
			
			if (newEntropy > max_weight) {
				newEntropy = max_weight;// - max_adjustment;
			}
			if (newEntropy < min_weight) {
				newEntropy = min_weight;// + max_adjustment;
			}
			
			setAverageAndCurrent(newEntropy, weight.varstat);
		//}
	}
	
	private void setAverageAndCurrent(double value, VarStatistic new_varstat) {
		cur_weight = value;
		
		varstat = new_varstat;//new VarStatistic(false);
		varstat.setEntropy(cur_weight);
	}
	
	public void setAverageToCurrent() {
		cur_weight = varstat.getEntropy();
		
		//varstat = new VarStatistic(false);
		//varstat.setEntropy(cur_weight);
	}
	
	public void makeAtLeast(Weight other) {
		if (getWeight() < other.getWeight()) {
			varstat.setEntropy(other.getAverage());
			cur_weight = other.cur_weight;
		}
	}
	
	public void makeAtMost(Weight other) {
		if (getWeight() > other.getWeight()) {
			varstat.setEntropy(other.getAverage());
			cur_weight = other.cur_weight;
		}
	}
	
	public double getAverage() {
		return varstat.getEntropy();
	}
	
	//public void setAverage(double average) {
	//	varstat.setEntropy(average);
	//}
	
	public double getWeight() {
		//if (useAverageWeights) {
		//	return varstat.getEntropy();
		//} else {
		return cur_weight;
		//}
	}
	
	strictfp void adjust(double amount) {
		
		
		double adjustmentPercent = DELTA * norm;
		//adjustmentPercent *= (1 / varstat.getChaos());
		
		if (amount > 0) {
			
			if (norm_adjustment) {
				if (amount > max_adjustment) {
					amount = max_adjustment;
				}
			}
			
			amount = amount * adjustmentPercent;
			
			if (cur_weight + amount <= max_weight) {
				cur_weight += amount;
			} else {
				cur_weight = max_weight;
			}
			
			varstat.addValue(cur_weight, amount);
			
		} else if (amount < 0) {
			
			amount = -amount;
			
			if (norm_adjustment) {
				if (amount > max_adjustment) {
					amount = max_adjustment;
				}
			}
			
			amount = amount * adjustmentPercent;
			
			if (cur_weight - amount >= min_weight) {
				cur_weight -= amount;
			} else {
				cur_weight = min_weight;
			}
			
			varstat.addValue(cur_weight, -amount);
			
		} else {
			//throw new IllegalStateException();
			varstat.addValue(cur_weight, 0);
		}
	}
	
	/*strictfp void adjust(double amount) {
		adjust(amount, varstat.getChaos());
	}*/
	
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

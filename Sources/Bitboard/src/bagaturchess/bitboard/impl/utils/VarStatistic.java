/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.bitboard.impl.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class VarStatistic implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3762105781046761947L;
	
	private static int HISTORY_LENGTH = 100;
	
	private double count;
	private double countNonNull;
	private double entropy;
	private double disperse;
	private double total_amount;
	private double total_direction;
	private double max_val;
	private double old_val;
	private double path;
	private boolean keepHistory;
	private List<Double> history_short;
	private List<Double> history_long;
	
	
	public VarStatistic(boolean _keepHistory) {
		keepHistory = _keepHistory;
		clear();
	}
	
	public void norm() {
		count /= 2;
		if (count < 1) {
			count = 1;
		}
		countNonNull /= 2;
		if (countNonNull < 1) {
			countNonNull = 1;
		}
		total_amount /= 2;
		if (total_amount < 1) {
			total_amount = 1;
		}
		total_direction /= 2;
	}
	
	public double getEntropy() {
		return entropy;
	}
	
	public double getDisperse() {
		return Math.sqrt(disperse);
	}
	
	public double getTotalAmount() {
		return total_amount;
	}
	
	public double getStability() {
		
		if (count == 0) {
			return 0;
		}
		
		if (path == 0) {
			//throw new IllegalStateException("count=" + count);
			return 1;
		}
		
		return 1 / path;
		//return count / path;
		//return 1 / (path / Math.max(1, max_val));
		//return count / (path / max_val);
		//return (count / path) / Math.max(1, max_val);
	}
	
	public double getTotalDirection() {
		return total_direction;
	}
	
	public void clear() {
		count = 0;
		countNonNull = 0;
		entropy = 0;
		disperse = 0;
		total_amount = 0;
		total_direction = 0;
		old_val = 0;
		path = 0;
		
		history_short = new ArrayList<Double>();
		history_long = new ArrayList<Double>();
	}
	
	public void devideMax(int factor) {
		max_val = max_val / factor;
	}
	
	public double getChaos() {
		if (total_amount == 0) return 1; 
		return total_amount / Math.max(Math.abs(total_direction), 0.0000000001);
	}
	
	public void addValue(double nv, double adjustment) {
		/*if (nv < 0) {
			throw new IllegalStateException();
		}*/
		
		if (nv != 0) {
			countNonNull++;
		}
		
		entropy = (count * entropy + nv) / (count + 1);
		if (count > 0) {
			disperse = (1 / count) * (	(count - 1) * disperse	 + 	(nv - entropy) * (nv - entropy)	);
		}
		
		//path += Math.sqrt(0.01 + Math.pow(nv - old_val, 2));
		path += Math.abs(nv - old_val);
		
		if (nv > max_val) {
			max_val = nv;
		}
		
		if (Math.abs(nv) > max_val) {
			max_val = Math.abs(nv);
		}
		
		total_amount += Math.abs(adjustment);
		total_direction += adjustment;
		
		count++;
		old_val = nv;
		
		/*
		 * Update history
		 */
		if (keepHistory) {
			if (history_short.size() >= HISTORY_LENGTH) {
				
				double sum = 0;
				for (Double cur: history_short) {
					sum += cur;
				}
				double avg = sum / history_short.size();
				
				history_short.clear();
				
				if (history_long.size() >= HISTORY_LENGTH) {
					history_long.remove(0);
				}
				
				history_long.add(avg);
				
			} else {
				history_short.add(nv);
			}
		}
	}
	
	@Override
	public String toString() {
		String result = "";
		
		result += StringUtils.fill("" + count, 6);
		result += "  " + StringUtils.align(entropy);
		result += "  " + StringUtils.align(getDisperse());
		result += "  " + StringUtils.align(getTotalAmount());
		result += "  " + StringUtils.align(getTotalDirection());
		result += "  " + StringUtils.align(getChaos());
		
		return result;
	}
	
	public static void main(String[] args) {
		VarStatistic a = new VarStatistic(true);
		
		a.addValue(1, 1);
		a.addValue(2, 2);
		a.addValue(3, 3);
		
		System.out.println(a);
	}
	
	public double getMaxVal() {
		return max_val;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}

	public void setDisperse(double disperse) {
		this.disperse = disperse;
	}

	public double getCountNonNull() {
		return countNonNull;
	}
}

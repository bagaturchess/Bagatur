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


package bagaturchess.search.impl.exts;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.search.api.IExtensionMode;
import bagaturchess.search.api.internal.ISearch;



public abstract class ExtensionInfo {
	
	
	private String name;
	private IExtensionMode extensionMode;
	private int updateInterval;
	
	private int accepted_score_diff;
	
	private static int count_global_total = 0;
	private int count_total = 0;
	private int count_good = 0;
	private int count_all = 1;
	
	private int cur_ext_value = 1;
	
	private int stats_size = 17;
	private VarStatistic[] stats;
	
	//private int NORM;
	private int norm_count;// = NORM;
	
	boolean updateCurrentLevel = false;
	
	public ExtensionInfo(String _name, IExtensionMode _extensionMode, int _updateInterval, int _accepted_score_diff, boolean _updateCurrentLevel) {
		this(_name, _extensionMode, _updateInterval, _accepted_score_diff);
		updateCurrentLevel = _updateCurrentLevel;
	}
	
	public ExtensionInfo(String _name, IExtensionMode _extensionMode, int _updateInterval, int _accepted_score_diff) {
		name = _name;
		extensionMode = _extensionMode;
		updateInterval = _updateInterval;
		accepted_score_diff = _accepted_score_diff;
		
		norm_count = updateInterval * getObservationFrequency();
		
		stats = new VarStatistic[stats_size];
		for (int i=0; i<stats.length; i++) {
			stats[i] = new VarStatistic(false);
		}
	}
	
	protected abstract double getStaticRating(int level);
	
	protected abstract int getObservationFrequency();
	
	public void inc(int score_diff, int ext_depth) {
		
		count_global_total++;
		count_total++;
		
		count_all++;
		if (Math.abs(score_diff) >= accepted_score_diff) {
			count_good++;
		}
		
		if (ext_depth > ISearch.PLY) {
			ext_depth = ISearch.PLY;
		}
		
		double rate = getRate();
		if (updateCurrentLevel) {
			stats[cur_ext_value].addValue(rate, rate);
		} else {
			stats[ext_depth].addValue(rate, rate);
		}
				
		//double cur_entropy = dynamic_entropy;
		//double cur_entropy = static_entropy;
		double cur_entropy = 0;
		
		if (extensionMode == IExtensionMode.STATIC) {
			double static_entropy = getStaticRating(cur_ext_value);
			cur_entropy = static_entropy;
		} else if (extensionMode == IExtensionMode.DYNAMIC) {
			double dynamic_entropy = stats[cur_ext_value].getEntropy();
			cur_entropy = dynamic_entropy;
		} else if (extensionMode == IExtensionMode.MIXED) {
			double dynamic_entropy = stats[cur_ext_value].getEntropy();
			double static_entropy = getStaticRating(cur_ext_value);
			cur_entropy = (dynamic_entropy + static_entropy) / 2;
		} else {
			cur_entropy = 0;
		}
		
		if (rate >= cur_entropy) {
			if (cur_ext_value < stats.length - 1) {
				if (stats[cur_ext_value].getCount() > 55) {
					cur_ext_value++;
				}
			}
		} else if (rate < cur_entropy) {
			if (cur_ext_value > 1) {
				cur_ext_value--;
			}
		}
		
		if (norm_count <= 0) {
			norm_count = updateInterval * getObservationFrequency();
			normalize();
			//System.out.println("normalize " +  name);
		}
		norm_count--;
		
		/*dump_count--;
		if (dump_count <= 0) {
			dump_count = 10000;
			System.out.println(this);
		}
		*/
	}
	
	protected double getRate() {
		return count_good / (double)count_all;
	}
	
	public void normalize() {
		
		/*if (this instanceof Extension_MoveEval) {
			System.out.println("OPA " + new java.util.Date(System.currentTimeMillis()));
			System.out.println(this);
		}*/
		
		count_good /= 2;
		count_all /= 2;
		
		if (count_all < 1) {
			count_all = 1;
		}
	}
	
	public int calc(int ext) {
		if (extensionMode == IExtensionMode.NONE) {
			return 0;
		} else if (extensionMode == IExtensionMode.STATIC) {
			return ext;
		} else {
			return (ext * cur_ext_value) / ISearch.PLY;
		}
	}
	
	public String toString() {
		String result = "" + name;
		result += " -> " + ", calc=" + calc(ISearch.PLY) + ", rate: " + getRate() + ", count_total: " + count_total + ", count_ration: " + (count_total / (double)count_global_total) + ", STATS: \r\n";
		for (int i=0; i<stats.length; i++) {
			result += stats[i] + "\r\n";
		}
		return result;
	}

	/*protected int getUpdateInterval() {
		return updateInterval;
	}*/
}

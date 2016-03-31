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
package bagaturchess.search.impl.alg;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.search.api.internal.ISearch;


public class BetaGenerator2 implements IBetaGenerator {
	
	
	private static final boolean DUMP = false;
	
	private static final int TREND_INIT = 0;
	private static final int TREND_UP   = 1;
	private static final int TREND_DOWN = -1;
	
	private static final int MAX_TREND_MULTIPLIER = 1000000;
	
	
	private int betasCount;
	
	private volatile int lower_bound;
	private volatile int upper_bound;
	private int trend;
	private int trend_multiplier;
	private int initial_interval;
	private int lastVal;
	
	private int aspiration_window = 25; 
	
	
	public BetaGenerator2(int _initialVal, int _betasCount, int _initial_interval) {
		lower_bound = ISearch.MIN;
		upper_bound = ISearch.MAX;
		
		betasCount = _betasCount;
		trend = TREND_INIT;
		trend_multiplier = 1;
		lastVal = _initialVal;
		
		initial_interval = _initial_interval;
	}
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.alg.IBetaGenerator#decreaseUpper(int)
	 */
	@Override
	public void decreaseUpper(int val) {
		if (DUMP) System.out.println("decreaseUpper called with " + val);
		
		if (val < upper_bound) {
			upper_bound = val;
			if (trend == TREND_DOWN) {
				if (trend_multiplier < MAX_TREND_MULTIPLIER) {
					trend_multiplier *= 2;
				}
			} else {
				trend_multiplier = 1;
			}
			trend = TREND_DOWN;
			lastVal = val;
		}
		//genBetas();
	}
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.alg.IBetaGenerator#increaseLower(int)
	 */
	@Override
	public void increaseLower(int val) {
		if (DUMP) System.out.println("increaseLower called with " + val);
		
		if (val > lower_bound) {
			lower_bound = val;
			if (trend == TREND_UP) {
				if (trend_multiplier < MAX_TREND_MULTIPLIER / 2) {
					trend_multiplier *= 2;
				}
			} else {
				trend_multiplier = 1;
			}
			trend = TREND_UP;
			lastVal = val;
		}
		//genBetas();
	}
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.alg.IBetaGenerator#genBetas()
	 */
	@Override
	public List<Integer> genBetas() {
		
		if (DUMP) System.out.println(this);
		
		boolean firstTime = lower_bound == ISearch.MIN && upper_bound == ISearch.MAX;
		
		List<Integer> betas = new ArrayList<Integer>();
		
		/*if (SearchUtils.isMateVal(lastVal)) {
			if (DUMP) System.out.println("MATE value");
			betas.add(lastVal - 1);
			if (DUMP) System.out.println(betas);
			return betas;
		}*/
		
		/*if (SearchUtils.isMateVal(lastVal)) {
			if (DUMP) System.out.println("MATE value");
			
			if (lower_bound == lastVal) {
				int depth = SearchUtils.getMateDepth(lastVal);
				if (DUMP) System.out.println("(lower) MATE depth " + depth);
				
				int new_val = -1;
				if (depth < 0) {
					depth = -depth;
					new_val = -SearchUtils.getMateVal(depth - 1);
				} else if (depth > 0) {
					new_val = SearchUtils.getMateVal(depth - 1);
				} else {
					throw new IllegalStateException();
				}

				if (DUMP) System.out.println("MATE new value  " + new_val);
				betas.add(new_val);
				if (DUMP) System.out.println(betas);
				return betas;
				
			} else if (upper_bound == lastVal) {
				int depth = SearchUtils.getMateDepth(lastVal);
				if (DUMP) System.out.println("(upper) MATE depth " + depth);
				
				int new_val = -1;
				if (depth < 0) {
					depth = -depth;
					new_val = -SearchUtils.getMateVal(depth + 1);
				} else if (depth > 0) {
					new_val = SearchUtils.getMateVal(depth + 1);
				} else {
					throw new IllegalStateException();
				}
				
				if (DUMP) System.out.println("MATE new value  " + new_val);
				betas.add(new_val);
				if (DUMP) System.out.println(betas);
				return betas;
				
			} else {
				//throw new IllegalStateException("lastVal=" + lastVal + ", lower_bound=" + lower_bound + ", upper_bound=" + upper_bound);
				
				int depth = SearchUtils.getMateDepth(lastVal);
				if (DUMP) System.out.println("(upper1) MATE depth " + depth);
				
				int new_val = -1;
				if (depth < 0) {
					depth = -depth;
					new_val = -SearchUtils.getMateVal(depth + 1);
				} else if (depth > 0) {
					new_val = SearchUtils.getMateVal(depth + 1);
				} else {
					throw new IllegalStateException();
				}
				
				if (DUMP) System.out.println("MATE new value  " + new_val);
				betas.add(new_val);
				if (DUMP) System.out.println(betas);
				return betas;
			}
		}
		*/
		
		if (lower_bound != ISearch.MIN && upper_bound != ISearch.MAX) {
			
			if (DUMP) System.out.println("WINDOWS will be used");
			
			for (int i=1; i<= betasCount; i++) {
				betas.add(lower_bound + Math.abs(upper_bound - lower_bound) / 2 + 1);
			}
			
		} else {
			
			if (DUMP) System.out.println("INTERVAL will be used");
			
			if (firstTime) {
				
				if (DUMP) System.out.println("FIRST time");
				
				for (int i=1; i<= betasCount; i++) {
					betas.add(lastVal + aspiration_window);
				}
				
			} else {
				
				if (trend == TREND_UP) {
					for (int i=1; i<= betasCount; i++) {
						betas.add(lastVal + aspiration_window + trend_multiplier);
						if (DUMP) System.out.println("lastVal = " + lastVal + " i=" + i + " trend_multiplier=" + trend_multiplier);
					}
				} else {
					for (int i=1; i<= betasCount; i++) {
						betas.add(lastVal - trend_multiplier);
						if (DUMP) System.out.println("lastVal = " + lastVal + " i=" + i + " trend_multiplier=" + trend_multiplier);
					}
				}
			}
		}
		
		if (DUMP) System.out.println("BETAS: " + betas);
		
		return betas;
	}
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.alg.IBetaGenerator#getLowerBound()
	 */
	@Override
	public int getLowerBound() {
		return lower_bound;
	}

	/*public void setLowerBound(int lower_bound) {
		this.lower_bound = lower_bound;
	}*/

	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.alg.IBetaGenerator#getUpperBound()
	 */
	@Override
	public int getUpperBound() {
		return upper_bound;
	}

	/*public void setUpperBound(int upper_bound) {
		this.upper_bound = upper_bound;
	}*/

	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.alg.IBetaGenerator#toString()
	 */
	@Override
	public String toString() {
		String result = "";
		result += "[" + lower_bound + ", " + upper_bound + "]	trend="
			+ trend + ", trend_multiplier=" + trend_multiplier + ", lastVal=" + lastVal;		
		return result;
	}
	
	public static void main(String[] args) {
		BetaGenerator2 gen = new BetaGenerator2(0, 4, 1);
		//Betagen obj: [0, 13]	trend=-1, trend_multiplier=33554432, lastVal=13
		
		gen.decreaseUpper(300);
		//gen.decreaseUpper(200);
		//gen.decreaseUpper(231);
		gen.increaseLower(223);
		
		System.out.println(gen);
		
		System.out.println("BETAS: " + gen.genBetas());
		
		/*gen.increaseLower(-300);
		gen.increaseLower(-200);
		gen.increaseLower(-100);
		gen.decreaseUpper(0);*/
		
		/*gen.increaseLower(-300);
		gen.decreaseUpper(300);
		gen.increaseLower(-200);
		gen.decreaseUpper(200);*/
	}
	
}

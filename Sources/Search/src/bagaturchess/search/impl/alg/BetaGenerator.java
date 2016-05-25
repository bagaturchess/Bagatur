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


public class BetaGenerator implements IBetaGenerator {
	
	
	private static final boolean DUMP = false;
	
	private static final int MAX_TREND_MULTIPLIER = 1000000;
	
	private static final int TREND_INIT = 0;
	private static final int TREND_UP   = 1;
	private static final int TREND_DOWN = -1;
	
	
	private int betasCount;
	
	private volatile int lower_bound;
	private volatile int upper_bound;
	private volatile int trend;
	private int trend_multiplier;
	private int initial_interval;
	private volatile int lastVal;
	
	
	public BetaGenerator(int _initialVal, int _betasCount, int _initial_interval) {
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
			if (trend == TREND_DOWN) {
				if (trend_multiplier < MAX_TREND_MULTIPLIER) {
					trend_multiplier *= 2;
				}
			} else {
				trend_multiplier = 1;
			}
			upper_bound = val;
			trend = TREND_DOWN;
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
			if (trend == TREND_UP) {
				if (trend_multiplier < MAX_TREND_MULTIPLIER) {
					trend_multiplier *= 2;
				}
			} else {
				trend_multiplier = 1;
			}
			lower_bound = val;
			trend = TREND_UP;
		}
		//genBetas();
	}
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.alg.IBetaGenerator#genBetas()
	 */
	@Override
	public List<Integer> genBetas() {
		
		if (DUMP) System.out.println(this);
		
		List<Integer> betas = new ArrayList<Integer>();
		
		if (lower_bound != ISearch.MIN && upper_bound != ISearch.MAX) {
			
			if (DUMP) System.out.println("WINDOWS will be used");
			
			int win = Math.abs(upper_bound - lower_bound) / betasCount;
			if (win <= 0) {
				throw new IllegalStateException("win=" + win + " (upper_bound - lower_bound)=" + (upper_bound - lower_bound) );
			}
			
			for (int i=1; i<= betasCount; i++) {
				betas.add(lower_bound + i * win);
			}
			
		} else {
			
			boolean firstTime = lower_bound == ISearch.MIN && upper_bound == ISearch.MAX;
			
			if (DUMP) System.out.println("INTERVAL will be used");
			
			if (firstTime) {
				
				betas.add(lastVal);
				
				int start_val = lastVal - (betasCount / 2) * initial_interval;
				for (int i=1; i<=betasCount; i++) {
					int beta = start_val + i * initial_interval;
					if (beta != lastVal) {
						betas.add(beta);
					}
					if (DUMP) System.out.println("start_val = " + start_val + " i=" + i);
				}
				
			} else {
				if (lower_bound != ISearch.MIN) {
					for (int i=1; i<= betasCount; i++) {
						int beta = lower_bound + i * initial_interval * trend_multiplier;
						betas.add(beta);
						if (DUMP) System.out.println(" i=" + i);
					}
				} else if (upper_bound != ISearch.MAX) {
					for (int i=1; i<= betasCount; i++) {
						int beta = upper_bound - i * initial_interval * trend_multiplier;
						betas.add(beta);
						if (DUMP) System.out.println(" i=" + i);
					}
				} else {
					throw new IllegalStateException("Nor upper nor lower bound");
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
			+ trend + ", lastVal=" + lastVal;		
		return result;
	}
	
	public static void main(String[] args) {
		BetaGenerator gen = new BetaGenerator(0, 4, 1);
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

	@Override
	public boolean hasLowerBound() {
		return lower_bound != ISearch.MIN;
	}

	@Override
	public boolean hasUpperBound() {
		return upper_bound != ISearch.MAX;
	}
	
}

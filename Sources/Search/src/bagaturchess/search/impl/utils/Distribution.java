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
package bagaturchess.search.impl.utils;

public class Distribution {

	private static final boolean DEBUG = false;
	
	private final double threashold;
	private final int scoresDistribution[];
	
	private int pointer;
	
	private long wholeIntegral;
	private long leftIntegral;
	private long rightIntegral;
	
	public Distribution(int _threashold, int[] _scoresDistribution) {
		threashold = _threashold;
		scoresDistribution = _scoresDistribution;
		pointer = 0;
		leftIntegral = 0;
		rightIntegral = 0;
		wholeIntegral = 0;
	}
	
	public int getPointer() {
		return pointer;
	}
	
	public void update(int decIndex, int incIndex) {

		long leftIntegral_backup = leftIntegral;
		long rightIntegral_backup = rightIntegral;
		long wholeIntegral_backup = wholeIntegral;
		int pointer_backup = pointer;
		
		wholeIntegral -= decIndex;
		wholeIntegral += incIndex;
		
		if (decIndex < incIndex) {
			if (pointer > decIndex && pointer <= incIndex) {
				if (leftIntegral < decIndex) {
					//throw new IllegalStateException();
					rightIntegral += leftIntegral;
					leftIntegral = 0;
				} else {
					leftIntegral -= decIndex;
					rightIntegral += incIndex;
				}
				pointer2Right();
			} else if (pointer > incIndex) {
				leftIntegral -= decIndex;
				leftIntegral += incIndex;
				pointer2Left();
			} else if (pointer <= decIndex) {
				rightIntegral -= decIndex;
				rightIntegral += incIndex;
				pointer2Right();
			}
		} else if (decIndex > incIndex) {
			if (pointer > incIndex && pointer <= decIndex) {
				if (rightIntegral < decIndex) {
					//throw new IllegalStateException();
					leftIntegral += rightIntegral;
					rightIntegral = 0;
				} else {
					rightIntegral -= decIndex;
					leftIntegral += incIndex;
				}
				pointer2Left();
			} else if (pointer > decIndex) {
				leftIntegral -= decIndex;
				leftIntegral += incIndex;
				pointer2Left();
			} else if (pointer <= incIndex) {
				rightIntegral -= decIndex;
				rightIntegral += incIndex;
				pointer2Right();
			}
		} else if (incIndex != 0) {
			//throw new IllegalStateException();
		}

		if (DEBUG) {
			verify();
		}
	}

	private void pointer2Right() {
		while (pointer < scoresDistribution.length
				&& rightIntegral / (double) wholeIntegral > (threashold / (double) 100)) {
			leftIntegral += pointer * scoresDistribution[pointer];
			rightIntegral -= pointer * scoresDistribution[pointer];
			pointer++;
		}
	}

	private void pointer2Left() {
		if (pointer >= scoresDistribution.length) {
			pointer = scoresDistribution.length - 1;
		}
		
		while (leftIntegral / (double) wholeIntegral >= (1 - threashold / (double) 100)) {
			leftIntegral -= pointer * scoresDistribution[pointer];
			rightIntegral += pointer * scoresDistribution[pointer];
			pointer--;
		}
		
		/*if (rightIntegral / (double) wholeIntegral > (threashold / (double) 100)) {
			leftIntegral += pointer * scoresDistribution[pointer];
			rightIntegral -= pointer * scoresDistribution[pointer];
			pointer++;
		}*/
	}

	void normalize() {
		
		wholeIntegral = 0;
		for (int i=0; i<scoresDistribution.length; i++) {
			wholeIntegral += i * scoresDistribution[i];
		}
		
		pointer = scoresDistribution.length - 1;
		leftIntegral = wholeIntegral;
		rightIntegral = 0;
		int i = scoresDistribution.length - 1;
		while (leftIntegral / (double) wholeIntegral >= (1 - threashold / (double) 100)) {
			leftIntegral -= pointer * scoresDistribution[pointer];
			rightIntegral += pointer * scoresDistribution[pointer];
			pointer--;
			i--;
		}
		
		if (DEBUG) {
			verify();
		}
	}
	
	private void verify() {
		
		for (int i=0; i<scoresDistribution.length; i++) {
			int cur = scoresDistribution[i];
			if (cur < 0) {
				throw new IllegalStateException("cur=" + cur);
			}
		}
		
		long wholeIntegral_test = 0;
		for (int i=0; i<scoresDistribution.length; i++) {
			wholeIntegral_test += i * scoresDistribution[i];
		}
		
		if (wholeIntegral_test != wholeIntegral) {
			throw new IllegalStateException("");
		}
		
		if (wholeIntegral != (leftIntegral + rightIntegral)) {
			throw new IllegalStateException("");
		}
		
		long leftIntegral_test = 0;
		for (int i=0; i<pointer; i++) {
			leftIntegral_test += i * scoresDistribution[i];
		}
		if (leftIntegral_test != leftIntegral) {
			throw new IllegalStateException();
		}
		
		long rightIntegral_test = 0;
		for (int i=pointer; i<scoresDistribution.length; i++) {
			rightIntegral_test += i * scoresDistribution[i];
		}
		if (rightIntegral_test != rightIntegral) {
			throw new IllegalStateException();
		}
		
		double target = threashold / (double) 100;
		double current = 0;
		for (int i=pointer + 1 ;i<scoresDistribution.length; i++) {
			current += scoresDistribution[i];
		}
		
		current = current/wholeIntegral;
		if (current > target) {
			throw new IllegalStateException();
		}
		
		//System.out.println(current);
	}
}

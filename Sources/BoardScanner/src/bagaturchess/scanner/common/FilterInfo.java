/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.scanner.common;


public class FilterInfo {
	
	public int minX = Integer.MAX_VALUE;
	public int minY = Integer.MAX_VALUE;
	public int maxX = Integer.MIN_VALUE;
	public int maxY = Integer.MIN_VALUE;
	public float angleInDegrees = 0;
	public int[][] source;
	
	public boolean isSmaller(FilterInfo info) {
		return maxX - minX > info.maxX - info.minX && maxY - minY > info.maxY - info.minY;
	}
	
	
	public boolean isInitialized() {
		return minX != Integer.MAX_VALUE && minY != Integer.MAX_VALUE && maxX != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE;
	}
}
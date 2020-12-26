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
package bagaturchess.scanner.learning;


import java.util.ArrayList;
import java.util.List;


public class DataSetInitPair {

	
	protected List<int[][]> grayImages;
	protected List<Integer> pids;
	
	
	public DataSetInitPair() {
		grayImages = new ArrayList<int[][]>();
		pids = new ArrayList<Integer>();
	}
	
	
	public List<int[][]> getGrayImages() {
		return grayImages;
	}
	
	
	public List<Integer> getPIDs() {
		return pids;
	}
}

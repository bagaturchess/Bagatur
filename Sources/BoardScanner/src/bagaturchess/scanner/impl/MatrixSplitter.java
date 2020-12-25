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
package bagaturchess.scanner.impl;


import java.util.HashMap;
import java.util.Map;


public class MatrixSplitter {

	
	public static Map<Integer, Object> splitTo64Squares(int[][] matrix) {
		
		int size = matrix.length;
		if (matrix.length != matrix[0].length) {
			throw new IllegalStateException("matrix is not square");
		}
		
		Map<Integer, Object> result = new HashMap<Integer, Object>();
		for (int i = 0; i < size; i += size / 8) {
			for (int j = 0; j < size; j += size / 8) {
				int file = i / (size / 8);
				int rank = j / (size / 8);
				int filedID = 63 - (file + 8 * rank);
				int[][] arr = getSquarePixelsMatrix(matrix, i, j);
				result.put(filedID, arr);
			}
		}
		
		return result;
	}
	
	
	public static int[][] getSquarePixelsMatrix(int[][] matrix, int i1, int j1) {
		
		if (matrix.length % 8 != 0) {
			throw new IllegalStateException("size is not devidable by 8");
		}
		
		int size = matrix.length / 8;
		int[][] result = new int[size][size];
		
		int ic = 0;
		for (int i = i1; i < i1 + size; i++) {
			int jc = 0;
			for (int j = j1; j < j1 + size; j++) {
				result[ic][jc] = matrix[i][j];
				jc++;
			}
			ic++;
		}
		
		return result;
	}
}

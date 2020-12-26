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

import java.util.HashSet;
import java.util.Set;

public class MatrixUtils {
	
	
	public static int[][] moveLeftWithN(int[][] matrix, int N) {
		int[][] result = matrix;
		for (int i = 0; i < N; i++) {
			result = moveLeftWith1(result);
		}
		return result;
	}
	
	
	public static int[][] moveRightWithN(int[][] matrix, int N) {
		int[][] result = matrix;
		for (int i = 0; i < N; i++) {
			result = moveRightWith1(result);
		}
		return result;
	}
	
	
	public static int[][] moveUpWithN(int[][] matrix, int N) {
		int[][] result = matrix;
		for (int i = 0; i < N; i++) {
			result = moveUpWith1(result);
		}
		return result;
	}
	
	
	public static int[][] moveDownWithN(int[][] matrix, int N) {
		int[][] result = matrix;
		for (int i = 0; i < N; i++) {
			result = moveDownWith1(result);
		}
		return result;
	}
	
	
	private static int[][] moveLeftWith1(int[][] matrix) {
		
		int[][] result = new int[matrix.length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			int first = matrix[0][i];
			for(int j = 1;j < matrix.length; j++) {
				result[j - 1][i] = matrix[j][i];
			}
			result[matrix.length-1][i] = first;
		}
		
		return result;
	}
	
	
	private static int[][] moveRightWith1(int[][] matrix) {
		
		int[][] result = new int[matrix.length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			int last = matrix[matrix.length - 1][i];
			for(int j = 0;j < matrix.length - 1; j++) {
				result[j + 1][i] = matrix[j][i];
			}
			result[0][i] = last;
		}
		
		return result;
	}
	
	
	private static int[][] moveUpWith1(int[][] matrix) {
		
		int[][] result = new int[matrix.length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			int first = matrix[i][0];
			for(int j = 1;j < matrix.length; j++) {
				result[i][j-1] = matrix[i][j];
			}
			result[i][matrix.length-1] = first;
		}
		
		return result;
	}
	
	
	private static int[][] moveDownWith1(int[][] matrix) {
		
		int[][] result = new int[matrix.length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			int last = matrix[i][matrix.length - 1];
			for(int j = 0;j < matrix.length - 1; j++) {
				result[i][j+1] = matrix[i][j];
			}
			result[i][0] = last;
		}
		
		return result;
	}
	
	
	private static Set<Translation> generateCircles(int radius) {
		
		Set<Translation> result = new HashSet<Translation>();
		
	    double PI = 3.1415926535;
	    for (double angle = 0; angle < 360; angle++) {
	        double x = radius * Math.cos(angle * PI / 180);
	        double y = radius * Math.sin(angle * PI / 180);

	        result.add(new Translation((int)x, (int)y));
	    }
	    
	    return result;
	}
	
	
	private static class Translation {
		
		
		int x;
		int y;
		
		
		Translation(int _x, int _y) {
			x = _x;
			y = _y;
		}
		
		
	    @Override
	    public int hashCode() {
	        return x + y;
	    }
	    
	    
	    @Override
	    public boolean equals(final Object obj) {
	        
	    	if (this == obj)
	            return true;
	        
	        if (getClass() != obj.getClass())
	            return false;
	        
	        final Translation other = (Translation) obj;
	        if (x == other.x && y == other.y) {
	        	return true;
	        }
	        
	        return false;
	    }
	    
	    
	    @Override
	    public String toString(){  
	    	return "[" + x + ", " + y + "]";  
	    }
	}
	
	
	public static void main(String[] args) {
		System.out.println(generateCircles(2));
	}
}

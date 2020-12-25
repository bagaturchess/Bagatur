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


import java.io.IOException;

import bagaturchess.bitboard.impl.Constants;
import deepnetts.net.NeuralNetwork;
import deepnetts.util.Tensor;


public class BoardScanner {
	
	
	private NeuralNetwork<?> network;
	
	
	public BoardScanner(NeuralNetwork<?> _network) throws ClassNotFoundException, IOException {
		network = _network;
	}
	
	
	public String scan(float[] flatGrayImage) {
		
		int size = (int) Math.sqrt(flatGrayImage.length);
		if (size * size != flatGrayImage.length) {
			throw new IllegalStateException("size is not exact sqrt " + size);
		}
		
		int[][] matrix = new int[size][size];
		for (int i = 0; i < flatGrayImage.length; i++) {
			matrix[i / size][i % size] = (int) flatGrayImage[i];
		}
		
		int[] pids = new int[64];
		for (int i = 0; i < size; i += size / 8) {
			for (int j = 0; j < size; j += size / 8) {
				int file = i / (size / 8);
				int rank = j / (size / 8);
				int filedID = 63 - (file + 8 * rank);
				int pid = getPID(matrix, i, j, filedID);
				pids[filedID] = pid;
			}
		}
		
		return ScannerUtils.createFENFromPIDs(pids);
	}


	private int getPID(int[][] matrix, int i1, int j1, int filedID) {
		
		int[][] arr = getSquarePixelsMatrix(matrix, i1, j1);
		float[] inputs = ScannerUtils.convertToFlatGrayArray(arr);
		
		//BufferedImage image = ScannerUtils.createGrayImage(arr);
		//ScannerUtils.saveImage("" + filedID, image);
		
		//BufferedImage image = ScannerUtils.createGrayImage(inputs);
		//ScannerUtils.saveImage("" + filedID, image);
		
		network.setInput(new Tensor(inputs));
		network.forward();
		float[] output = network.getOutput();
		
		float maxValue = 0;
		int maxIndex = 0;
		for (int j = 0; j < output.length; j++) {
			if (maxValue < output[j]) {
				maxValue = output[j];
				maxIndex = j;
			}
		}
		
		int pid = (maxIndex == 13 ? Constants.PID_NONE : maxIndex);
		
		return pid;
	}
	
	
	private static int[][] getSquarePixelsMatrix(int[][] matrix, int i1, int j1) {
		
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

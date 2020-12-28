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


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.impl.MatrixUtils;
import bagaturchess.scanner.impl.ScannerUtils;


public class DataSetInitPair_ByBoardImage_RGB extends DataSetInitPair {
	
	
	public DataSetInitPair_ByBoardImage_RGB(BufferedImage boardImage) {
		
		super();
		
		//ScannerUtils.saveImage(fileName + "_resized", boardImage, "png");
		
		int[][][] matrixOfInitialBoard = ScannerUtils.convertToRGBMatrix(boardImage);
		
		Map<Integer, int[][][]> result = MatrixUtils.splitTo64Squares(matrixOfInitialBoard);
		
		for (Integer fieldID : result.keySet()) {
			
			List<int[][][]> translations = new ArrayList<int[][][]>();
			translations.add(result.get(fieldID));
			//translations.addAll(MatrixUtils.generateTranslations(result.get(fieldID), 1));
			//translations.addAll(MatrixUtils.generateTranslations(result.get(fieldID), 2));
			
			//System.out.println(translations.size());
			
			for (int[][][] matrix : translations) {
				
				//BufferedImage image = ScannerUtils.createGrayImage(matrix);
				//ScannerUtils.saveImage("" + fieldID + "_" + (int)(100 * Math.random()), image, "png");
			
				switch (fieldID) {
					case 0:
						images.add(matrix);
						pids.add(Constants.PID_W_ROOK);
						break;
					case 1:
						images.add(matrix);
						pids.add(Constants.PID_W_KNIGHT);
						break;
					case 2:
						images.add(matrix);
						pids.add(Constants.PID_W_BISHOP);
						break;
					case 3:
						images.add(matrix);
						pids.add(Constants.PID_W_KING);
						break;
					case 4:
						images.add(matrix);
						pids.add(Constants.PID_W_QUEEN);
						break;
					case 5:
						images.add(matrix);
						pids.add(Constants.PID_W_BISHOP);
						break;
					case 6:
						images.add(matrix);
						pids.add(Constants.PID_W_KNIGHT);
						break;
					case 7:
						images.add(matrix);
						pids.add(Constants.PID_W_ROOK);
						break;
					case 8:
						images.add(matrix);
						pids.add(Constants.PID_W_PAWN);
						break;
					case 9:
						images.add(matrix);
						pids.add(Constants.PID_W_PAWN);
						break;
					case 16:
						images.add(matrix);
						pids.add(Constants.PID_NONE);
						break;
					case 17:
						images.add(matrix);
						pids.add(Constants.PID_NONE);
						break;
					case 54:
						images.add(matrix);
						pids.add(Constants.PID_B_PAWN);
						break;
					case 55:
						images.add(matrix);
						pids.add(Constants.PID_B_PAWN);
						break;
					case 56:
						images.add(matrix);
						pids.add(Constants.PID_B_ROOK);
						break;
					case 57:
						images.add(matrix);
						pids.add(Constants.PID_B_KNIGHT);
						break;
					case 58:
						images.add(matrix);
						pids.add(Constants.PID_B_BISHOP);
						break;
					case 59:
						images.add(matrix);
						pids.add(Constants.PID_B_KING);
						break;
					case 60:
						images.add(matrix);
						pids.add(Constants.PID_B_QUEEN);
						break;
					case 61:
						images.add(matrix);
						pids.add(Constants.PID_B_BISHOP);
						break;
					case 62:
						images.add(matrix);
						pids.add(Constants.PID_B_KNIGHT);
						break;
					case 63:
						images.add(matrix);
						pids.add(Constants.PID_B_ROOK);
						break;
				}
			}
		}
	}
}

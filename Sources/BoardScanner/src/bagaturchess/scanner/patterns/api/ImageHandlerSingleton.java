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
package bagaturchess.scanner.patterns.api;


import java.awt.image.BufferedImage;
import java.io.IOException;

import bagaturchess.scanner.common.MatrixUtils.PatternMatchingData;


public class ImageHandlerSingleton implements ImageHandler<BufferedImage, String> {
	
	
	private static final ImageHandler<BufferedImage, String> instance;
	
	
    static {
    	
    	instance = new bagaturchess.scanner.patterns.api.ImageHandlerImpl_AWT();
    }
    
    
    public static ImageHandler getInstance(){
        return instance;
    }


	@Override
	public BufferedImage loadImageFromFS(String path) throws IOException {
		return instance.loadImageFromFS(path);
	}


	@Override
	public BufferedImage resizeImage(BufferedImage source, int newsize) {
		return instance.resizeImage(source, newsize);
	}


	@Override
	public void saveImage(String fileName, String formatName, BufferedImage image) throws IOException {
		instance.saveImage(fileName, formatName, image);
	}


	@Override
	public int[][] convertToGrayMatrix(BufferedImage image) {
		return instance.convertToGrayMatrix(image);
	}


	@Override
	public BufferedImage createGrayImage(int[][] matrix) {
		return instance.createGrayImage(matrix);
	}


	@Override
	public BufferedImage loadPieceImageFromMemory(int pid, String piecesSetName, int size) {
		return instance.loadPieceImageFromMemory(pid, piecesSetName, size);
	}


	@Override
	public void printInfo(int[][] source, PatternMatchingData matcherData, String fileName) {
		instance.printInfo(source, matcherData, fileName);
	}


	@Override
	public void printInfo(PatternMatchingData matcherData, String fileName) {
		instance.printInfo(matcherData, fileName);
	}


	@Override
	public int[][] createSquareImage(int bgcolor, int size) {
		return instance.createSquareImage(bgcolor, size);
	}


	@Override
	public int[][] createPieceImage(String pieceSetName, int pid, int bgcolor, int size) {
		return instance.createPieceImage(pieceSetName, pid, bgcolor, size);
	}
}

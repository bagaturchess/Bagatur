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
package bagaturchess.scanner.patterns.impl1.awt;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.patterns.api.ImageHandler;


public class ImageHandlerSingleton implements ImageHandler<BufferedImage, String> {
	
	
	private static ImageHandlerSingleton instance;
	
	
    static{
    	instance = new ImageHandlerSingleton();
    }
    
    
    public static ImageHandlerSingleton getInstance(){
        return instance;
    }
    
    
	private ImageHandlerSingleton() {
		
	}
	
	
	@Override
	public BufferedImage loadImageFromFS(String path) throws IOException {
		return ImageIO.read(new File(path));
	}
	
	
	@Override
	public BufferedImage resizeImage(BufferedImage source, int newsize) {
		return ScannerUtils.resizeImage(source, newsize);
	}
	
	
	@Override
	public void saveImage(String fileName, String formatName, BufferedImage image) throws IOException {
		ScannerUtils.saveImage(fileName, image, formatName);
	}
	
	
	@Override
	public int[][] convertToGrayMatrix(BufferedImage image) {
		return ScannerUtils.convertToGrayMatrix(image);
	}


	@Override
	public BufferedImage createGrayImage(int[][] matrix) {
		return ScannerUtils.createGrayImage(matrix);
	}


	@Override
	public BufferedImage loadPieceImageFromFS(int pid, String piecesSetName) throws IOException {
		
		String suffix = "";
		
		switch (pid) {
		
			case Constants.PID_W_PAWN:
				suffix = "_w_p.png";
				break;
			case Constants.PID_W_KNIGHT:
				suffix = "_w_n.png";
				break;
			case Constants.PID_W_BISHOP:
				suffix = "_w_b.png";
				break;
			case Constants.PID_W_ROOK:
				suffix = "_w_r.png";
				break;
			case Constants.PID_W_QUEEN:
				suffix = "_w_q.png";
				break;
			case Constants.PID_W_KING:
				suffix = "_w_k.png";
				break;
				
			case Constants.PID_B_PAWN:
				suffix = "_b_p.png";
				break;
			case Constants.PID_B_KNIGHT:
				suffix = "_b_n.png";
				break;
			case Constants.PID_B_BISHOP:
				suffix = "_b_b.png";
				break;
			case Constants.PID_B_ROOK:
				suffix = "_b_r.png";
				break;
			case Constants.PID_B_QUEEN:
				suffix = "_b_q.png";
				break;
			case Constants.PID_B_KING:
				suffix = "_b_k.png";
				break;
			default:
				throw new IllegalStateException("pid=" + pid);
		}
		return ImageIO.read(new File("./res/" + piecesSetName + suffix));
	}

}

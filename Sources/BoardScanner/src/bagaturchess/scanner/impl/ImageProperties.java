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


import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.Constants;


public class ImageProperties {
	
	
	private int imageSize;
	private int squareSize;
	
	private String piecesSetFileNamePrefix;
	private Image[] piecesImages = new Image[13];
	
	private Color colorBlackSquare = new Color(120, 120, 120);
	private Color colorWhiteSquare = new Color(220, 220, 220);
	
	
	public ImageProperties(int _imageSize) throws IOException {
		this(_imageSize, "set1");
	}
	
	
	public ImageProperties(int _imageSize, String _piecesSetFileNamePrefix) throws IOException {
		
		imageSize = _imageSize;
		squareSize = getImageSize() / 8;
		
		piecesSetFileNamePrefix = _piecesSetFileNamePrefix;
		
		loadPiecesImages();
	}
	
	
	private void loadPiecesImages() throws IOException{
		
		piecesImages[Constants.PID_W_KING] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_w_k.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_QUEEN] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_w_q.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_ROOK] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_w_r.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_BISHOP] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_w_b.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_KNIGHT] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_w_n.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_PAWN] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_w_p.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		
		piecesImages[Constants.PID_B_KING] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_b_k.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_QUEEN] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_b_q.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_ROOK] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_b_r.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_BISHOP] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_b_b.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_KNIGHT] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_b_n.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_PAWN] = ImageIO.read(new File("./res/" + piecesSetFileNamePrefix + "_b_p.png")).getScaledInstance(getSquareSize(), getSquareSize(), Image.SCALE_REPLICATE);
	}
	
	
	public int getImageSize() {
		return imageSize;
	}


	public int getSquareSize() {
		return squareSize;
	}


	public Image[] getPiecesImages() {
		return piecesImages;
	}


	public Color getColorBlackSquare() {
		return colorBlackSquare;
	}


	public Color getColorWhiteSquare() {
		return colorWhiteSquare;
	}
}

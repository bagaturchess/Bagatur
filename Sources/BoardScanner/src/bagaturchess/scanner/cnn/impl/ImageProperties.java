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
package bagaturchess.scanner.cnn.impl;


import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.patterns.impl1.awt.ImageHandlerSingleton;


public class ImageProperties {
	
	
	private int imageSize;
	private int squareSize;
	
	private String piecesSetFileNamePrefix;
	private BufferedImage[] piecesImages = new BufferedImage[13];
	
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
	
	
	private void loadPiecesImages() throws IOException {
		
		piecesImages[Constants.PID_W_KING] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_W_KING, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_W_QUEEN] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_W_QUEEN, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_W_ROOK] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_W_ROOK, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_W_BISHOP] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_W_BISHOP, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_W_KNIGHT] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_W_KNIGHT, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_W_PAWN] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_W_PAWN, piecesSetFileNamePrefix);
		
		piecesImages[Constants.PID_B_KING] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_B_KING, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_B_QUEEN] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_B_QUEEN, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_B_ROOK] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_B_ROOK, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_B_BISHOP] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_B_BISHOP, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_B_KNIGHT] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_B_KNIGHT, piecesSetFileNamePrefix);
		piecesImages[Constants.PID_B_PAWN] = ImageHandlerSingleton.getInstance().loadPieceImageFromFS(Constants.PID_B_PAWN, piecesSetFileNamePrefix);
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
	
	
	public void setColorBlackSquare(Color colorBlackSquare) {
		this.colorBlackSquare = colorBlackSquare;
	}


	public void setColorWhiteSquare(Color colorWhiteSquare) {
		this.colorWhiteSquare = colorWhiteSquare;
	}
}

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
import java.io.IOException;


public class ImageProperties {
	
	
	private int imageSize;
	private int squareSize;
	
	private String piecesSetFileNamePrefix;

	private Color colorBlackSquare = new Color(120, 120, 120);
	private Color colorWhiteSquare = new Color(220, 220, 220);


	public ImageProperties(int _imageSize) throws IOException {
		this(_imageSize, "set1");
	}
	
	
	public ImageProperties(int _imageSize, String _piecesSetFileNamePrefix) throws IOException {
		
		imageSize = _imageSize;
		squareSize = getImageSize() / 8;
		
		piecesSetFileNamePrefix = _piecesSetFileNamePrefix;
	}
	
	
	public int getImageSize() {
		return imageSize;
	}


	public int getSquareSize() {
		return squareSize;
	}


	/*public Image[] getPiecesImages() {
		return piecesImages;
	}*/


	public Color getColorBlackSquare() {
		return colorBlackSquare;
	}


	public Color getColorWhiteSquare() {
		return colorWhiteSquare;
	}
	
	
	public String getPiecesSetFileNamePrefix() {
		return piecesSetFileNamePrefix;
	}
	
	
	public void setColorBlackSquare(Color colorBlackSquare) {
		this.colorBlackSquare = colorBlackSquare;
	}


	public void setColorWhiteSquare(Color colorWhiteSquare) {
		this.colorWhiteSquare = colorWhiteSquare;
	}
}

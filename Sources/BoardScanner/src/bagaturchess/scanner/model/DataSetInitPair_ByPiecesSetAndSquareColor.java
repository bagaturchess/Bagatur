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
package bagaturchess.scanner.model;


import java.awt.image.BufferedImage;

import bagaturchess.scanner.impl.ImageProperties;
import bagaturchess.scanner.impl.ScannerUtils;


public class DataSetInitPair_ByPiecesSetAndSquareColor extends DataSetInitPair {
	
	
	protected ImageProperties imageProperties;
	
	
	DataSetInitPair_ByPiecesSetAndSquareColor(ImageProperties _imageProperties) {
		
		super();
		
		imageProperties = _imageProperties;
		
		for (int pid = 0; pid <= 12; pid++) {
			BufferedImage whiteImage = ScannerUtils.createSquareImage(imageProperties, pid, imageProperties.getColorWhiteSquare());
			BufferedImage blackImage = ScannerUtils.createSquareImage(imageProperties, pid, imageProperties.getColorBlackSquare());
			whiteImage = ScannerUtils.convertToGrayScale(whiteImage);
			blackImage = ScannerUtils.convertToGrayScale(blackImage);
			images.add(ScannerUtils.convertToGrayMatrix(whiteImage));
			images.add(ScannerUtils.convertToGrayMatrix(blackImage));
			if (pid == 0) {
				pids.add(0);
				pids.add(13);
			} else {
				pids.add(pid);
				pids.add(pid);
			}
		}
	}
}

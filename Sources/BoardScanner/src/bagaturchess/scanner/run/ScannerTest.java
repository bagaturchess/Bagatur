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
package bagaturchess.scanner.run;


import java.awt.image.BufferedImage;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.scanner.utils.BoardScanner;
import bagaturchess.scanner.utils.ImageProperties;
import bagaturchess.scanner.utils.ScannerUtils;


public class ScannerTest {
	public static void main(String[] args) {
		try {
			
			IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache();
			
			ImageProperties imageProperties = new ImageProperties(192);
			BufferedImage boardImage = ScannerUtils.createBoardImage(imageProperties, bitboard.toEPD());
			
			BoardScanner scanner = new BoardScanner();
			String fen = scanner.scan(ScannerUtils.convertToFlatGrayArray(boardImage));
			
			System.out.println(fen);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

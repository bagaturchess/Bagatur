/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
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
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.scanner.visitors;


import java.awt.image.BufferedImage;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.scanner.utils.BoardScanner;
import bagaturchess.scanner.utils.ImageProperties;
import bagaturchess.scanner.utils.ScannerUtils;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class ScannerCheckVisitor implements PositionsVisitor {
	
	
	private int iteration = 0;
	
	private int counter;
	
	private long startTime;
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private ImageProperties imageProperties;
	
	private BoardScanner scanner;
	
	
	public ScannerCheckVisitor() throws Exception {
		imageProperties = new ImageProperties(192);
		scanner = new BoardScanner();
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
        
		BufferedImage image = ScannerUtils.createBoardImage(imageProperties, bitboard.toEPD());
		image = ScannerUtils.convertToGrayScale(image);
		
		//ScannerUtils.saveImage(bitboard.toEPD(), image);
		float[] expected_input = ScannerUtils.convertToFlatGrayArray(image);
		String recognized_fen = scanner.scan(expected_input);
		
		String expected_fen_prefix = bitboard.toEPD().split(" ")[0];
		
		sumDiffs1++;
		if (!recognized_fen.equals(expected_fen_prefix)) {
			sumDiffs2++;
		}
		
		counter++;
		if ((counter % 100) == 0) {
			
			System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
		}
	}
	
	
	@Override
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		iteration++;
		
		sumDiffs1 = 0;
		sumDiffs2 = 0;
	}
	
	
	@Override
	public void end() {
		System.out.println("END Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
		//network.save(NET_FILE);
	}
}

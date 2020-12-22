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
package bagaturchess.scanner.utils;


import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl1.internal.ChessConstants;


public class ScannerUtils {
	
	public static String convertOutputToFEN(double[] actual_output) {
		
		//double[] signals = new double[64];
		int[] pids = new int[64];
		for (int i = 0; i < actual_output.length; i++) {
			
			int pid = i / 64;
			if (pid == Constants.PID_NONE) {
				continue;
			}
			
			int squareID = i % 64;
			
			if (actual_output[i] >= 0.5d) {
				pids[squareID] = pid;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 63; i >= 0; i--) {
			if (pids[i] >= 1 && pids[i] <= 6) {
				sb.append(ChessConstants.FEN_WHITE_PIECES[Constants.PIECE_IDENTITY_2_TYPE[pids[i]]]);
			} else {
				sb.append(ChessConstants.FEN_BLACK_PIECES[Constants.PIECE_IDENTITY_2_TYPE[pids[i]]]);
			}
			
			if (i % 8 == 0 && i != 0) {
				sb.append("/");
			}
		}
		
		String fen = sb.toString();
		fen = fen.replaceAll("11111111", "8");
		fen = fen.replaceAll("1111111", "7");
		fen = fen.replaceAll("111111", "6");
		fen = fen.replaceAll("11111", "5");
		fen = fen.replaceAll("1111", "4");
		fen = fen.replaceAll("111", "3");
		fen = fen.replaceAll("11", "2");
		
		return fen;
	}
}

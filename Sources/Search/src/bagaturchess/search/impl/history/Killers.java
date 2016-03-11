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
package bagaturchess.search.impl.history;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;


public class Killers {
	
	private static final boolean USE_MOVE_SCORES = true;
	
	private int KILLERS_COUNT = -1;
	
	private int[] whiteKillers;
	private int[] blackKillers;
	private long[] whiteKillersRate;
	private long[] blackKillersRate;
	
	private int[][][] rates = new int[3][64][64];
	
	public Killers(int size) {
		
		KILLERS_COUNT = size;
		
		whiteKillers = new int[KILLERS_COUNT];
		blackKillers = new int[KILLERS_COUNT];
		whiteKillersRate = new long[KILLERS_COUNT];
		blackKillersRate = new long[KILLERS_COUNT];
	}
	
	public void bestMove(int bestmove) {
		
		int colour = MoveInt.getColour(bestmove);
		int from = MoveInt.getFromFieldID(bestmove);
		int to = MoveInt.getToFieldID(bestmove);
		rates[colour][from][to]++;
		
		int rate = rates[colour][from][to];
		
		if (MoveInt.isWhite(bestmove)) {
			if (USE_MOVE_SCORES) {
				for (int i = 0; i < KILLERS_COUNT; i++) {
					long curRate = whiteKillersRate[i];
					if (curRate <= rate) {
						int curMove = whiteKillers[i];
						if (curMove == 0 || !MoveInt.isEquals(curMove, bestmove)) {
							for (int j = KILLERS_COUNT - 1; j > i; j--) {
								whiteKillersRate[j] = whiteKillersRate[j - 1];
								whiteKillers[j] = whiteKillers[j - 1];
							}
							whiteKillers[i] = bestmove;
						}
						whiteKillersRate[i] = rate;
						break;
					}
				}
			} else {
				for (int j = KILLERS_COUNT - 1; j > 0; j--) {
					whiteKillers[j] = whiteKillers[j - 1];
				}
				whiteKillers[0] = bestmove;
			}
		} else {
			if (USE_MOVE_SCORES) {
				for (int i = 0; i < KILLERS_COUNT; i++) {
					long curRate = blackKillersRate[i];
					if (curRate <= rate) {
						if (curRate <= rate) {
							int curMove = blackKillers[i];
							if (curMove == 0 || !MoveInt.isEquals(curMove, bestmove)) {
								for (int j = KILLERS_COUNT - 1; j > i; j--) {
									blackKillersRate[j] = blackKillersRate[j - 1];
									blackKillers[j] = blackKillers[j - 1];
								}
								blackKillers[i] = bestmove;
							}
							blackKillersRate[i] = rate;
							break;
						}
					}
				}
			} else {
				for (int j = KILLERS_COUNT - 1; j > 0; j--) {
					blackKillers[j] = blackKillers[j - 1];
				}
				blackKillers[0] = bestmove;
			}
		}
	}
	
	public int[] getKillers(int colour) {
		return colour == Figures.COLOUR_WHITE ? whiteKillers : blackKillers;
	}
	
	public void clear() {
		for (int j=0; j < KILLERS_COUNT; j++) {
			whiteKillers[j] = 0;
			blackKillers[j] = 0;
			whiteKillersRate[j] = 0;
			blackKillersRate[j] = 0;
		}
	}
}

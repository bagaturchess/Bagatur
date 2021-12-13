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
package bagaturchess.bitboard.impl.endgame;


import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movegen.MoveInt;


public class MaterialState implements MoveListener, IMaterialState {
	
	
	private int piecesCount;
	private int[] pidsCounts;
	
	
	public MaterialState() {
		init();
	}
	
	
	private void init() {
		piecesCount = 0;
		pidsCounts = new int[Constants.PID_MAX];
	}
	
	
	public int getPiecesCount() {
		return piecesCount;
	}


	public int[] getPIDsCounts() {
		return pidsCounts;
	}
	
	
	public void addPiece_Special(int pid, int fieldID) {
		throw new UnsupportedOperationException();
	}
	
	
	public void initially_addPiece(int pid, int fieldID) {
		added(pid);
	}

	
	public void postBackwardMove(int color, int move) {
		if (MoveInt.isCapture(move)) {
			int cap_pid = MoveInt.getCapturedFigurePID(move);
			added(cap_pid);
		}
		
		if (MoveInt.isPromotion(move)) {
			int prom_pid = MoveInt.getPromotionFigurePID(move);
			removed(prom_pid);
			int pid = MoveInt.getFigurePID(move);
			added(pid);
		}
	}
	
	
	public void postForwardMove(int color, int move) {		
		if (MoveInt.isCapture(move)) {
			int cap_pid = MoveInt.getCapturedFigurePID(move);
			removed(cap_pid);
		}
		
		if (MoveInt.isPromotion(move)) {
			int prom_pid = MoveInt.getPromotionFigurePID(move);
			added(prom_pid);
			int pid = MoveInt.getFigurePID(move);
			removed(pid);
		}
	}
	
	
	public void preBackwardMove(int color, int move) {
		//Do nothing
	}
	
	
	public void preForwardMove(int color, int move) {
		//Do nothing
	}
	
	
	protected void added(int figurePID) {
		inc(figurePID);
	}
	
	
	protected void removed(int figurePID) {
		dec(figurePID);
	}
	
	
	private void inc(int pid) {
		piecesCount++;
		pidsCounts[pid]++;
		//String pieceid_str = Constants.PIECE_IDENTITY_2_SIGN[pid];
	}
	
	
	protected void dec(int pid) {
		piecesCount--;
		pidsCounts[pid]--;
		//String pieceid_str = Constants.PIECE_IDENTITY_2_SIGN[pid];
	}
}

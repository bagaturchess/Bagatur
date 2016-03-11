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
package bagaturchess.bitboard.common;


public class BackupInfo {
	
	public long enpassantPawnBitboard;
	public int enpassantPawnFieldID;
	public boolean w_kingSideAvailable = false;
	public boolean w_queenSideAvailable = false;
	public boolean b_kingSideAvailable = false;
	public boolean b_queenSideAvailable = false;
	public int lastCaptureOrPawnMoveBefore = 0;
	public int lastCaptureFieldID = -1;
	
	public long hashkey;
	public long pawnshash;
	
	public long getHashkey() {
		return hashkey;
	}

	public BackupInfo() {
		enpassantPawnBitboard = 0;
		enpassantPawnFieldID = -1;
		w_kingSideAvailable = false;
		w_queenSideAvailable = false;
		b_kingSideAvailable = false;
		b_queenSideAvailable = false;
		lastCaptureOrPawnMoveBefore = 0;
		lastCaptureFieldID = -1;
	}
}

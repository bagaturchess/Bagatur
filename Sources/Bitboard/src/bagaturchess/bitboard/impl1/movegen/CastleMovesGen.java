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
package bagaturchess.bitboard.impl1.movegen;


import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.plies.checking.CastleChecks;


public class CastleMovesGen extends CastleChecks {
	
	
	static final int[][] validDirsIDs = ALL_CASTLE_VALID_DIRS;
	static final int[][][] dirsFieldIDs = ALL_CASTLE_DIRS_WITH_FIELD_IDS;
	
	
	public static final void genAllMoves(final int pid,	final int fromFieldID,
				final int[] figuresIDsPerFieldsIDs,
				final IInternalMoveList list
			) {
		
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		final int[][] dirs_ids = dirsFieldIDs[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			final int dirID = validDirIDs[i];
			final int[] dirIDs = dirs_ids[dirID];
			
			for (int seq=0; seq<dirIDs.length; seq++) {
				
				final int toFieldID = dirIDs[seq];
				final int targetPID = figuresIDsPerFieldsIDs[toFieldID];
				
				if (targetPID == Constants.PID_NONE) {
					list.reserved_add(MoveInt.createNonCapture(pid, fromFieldID, toFieldID));
				} else {
					if (Constants.hasSameColour(pid, targetPID)) {
						break;
					} else {
						list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
						break;
					}
				}
			}
		}
	}
	
	public static final void genCaptureMoves(final int pid,	final int fromFieldID,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list
			) {
		
		
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		final int[][] dirs_ids = dirsFieldIDs[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			final int[] dirIDs = dirs_ids[dirID];
			
			for (int seq=0; seq<dirIDs.length; seq++) {
				
				final int toFieldID = dirIDs[seq];
				int targetPID = figuresIDsPerFieldsIDs[toFieldID];
				
				if (targetPID == Constants.PID_NONE) {
					continue;
				} else {
					if (Constants.hasSameColour(pid, targetPID)) {
						break;
					} else {
						list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
						break;
					}
				}
			}
		}
	}
}

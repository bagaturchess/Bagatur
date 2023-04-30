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
package bagaturchess.bitboard.impl_kingcaptureallowed.movegen;


import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.plies.checking.QueenUniqueChecks;


/**
 * Do not use OOP in moves generators,
 * because of performance degradation caused by the impossibility to inline abstract methods at compile time. 
 */
public class QueenMovesGen extends QueenUniqueChecks {

	
	public static final void genAllMoves(final int pid,	final int fromFieldID,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list
			) {
		OfficerMovesGen.genAllMoves(pid, fromFieldID, figuresIDsPerFieldsIDs, list);
		CastleMovesGen.genAllMoves(pid, fromFieldID, figuresIDsPerFieldsIDs, list);
	}
	
	
	public static final void genCaptureMoves(final int pid,	final int fromFieldID,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list
			) {
		OfficerMovesGen.genCaptureMoves(pid, fromFieldID, figuresIDsPerFieldsIDs, list);
		CastleMovesGen.genCaptureMoves(pid, fromFieldID, figuresIDsPerFieldsIDs, list);
	}
}

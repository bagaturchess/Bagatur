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
package bagaturchess.bitboard.impl.attacks.fast;


import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.plies.CastlePlies;


public class Castles extends Fields {
	
	public static final long genAttacks(int colour, int castleFieldID, int figureType, int dirID, Board bitboard, FieldsStateMachine fac, boolean add) {
		
		long attacks = NUMBER_0;
		
		final long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[castleFieldID];
		final int[][] dirFieldIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[castleFieldID];
		
		if (dirID == -1) {
			final int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[castleFieldID];
			
			final int size = validDirIDs.length;
			for (int dir=0; dir<size; dir++) {
				
				dirID = validDirIDs[dir];
				long[] dirBitboards = dirs[dirID];
				
				for (int seq=0; seq<dirBitboards.length; seq++) {
					long field = dirs[dirID][seq];
					attacks |= field;
					if (add) { 
						fac.addAttack(colour, figureType, dirFieldIDs[dirID][seq], field);
					} else {
						fac.removeAttack(colour, figureType, dirFieldIDs[dirID][seq], field);
					}
					
					if ((field & bitboard.free) == NUMBER_0) {
						
						if (IPlayerAttacks.SUPPORT_HIDDEN_ATTACKS) {
							/**
							 * Handle hidden attacks over castles
							 */
							long ownCastles = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_CASTLE);
							if ((field & ownCastles) != 0L) {
								continue;
							}
						}
						
						break;
					}
				}
			}
		} else {
			long[] dirBitboards = dirs[dirID];
			
			for (int seq=0; seq<dirBitboards.length; seq++) {
				long field = dirs[dirID][seq];
				attacks |= field;
				if (add) { 
					fac.addAttack(colour, figureType, dirFieldIDs[dirID][seq], field);
				} else {
					fac.removeAttack(colour, figureType, dirFieldIDs[dirID][seq], field);
				}
				
				if ((field & bitboard.free) == NUMBER_0) {
					
					if (IPlayerAttacks.SUPPORT_HIDDEN_ATTACKS) {
						/**
						 * Handle hidden attacks over castles
						 */
						long ownCastles = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_CASTLE);
						if ((field & ownCastles) != 0L) {
							continue;
						}
					}
					
					break;
				}
			}
		}
		
		return attacks;
	}

	public static long genAttacks(int colour, int castleFieldID, int dirID, final Board bitboard) {
		
		long attacks = NUMBER_0;
		
		final long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[castleFieldID];
		
		if (dirID == -1) {
			final int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[castleFieldID];
			
			final int size = validDirIDs.length;
			for (int dir=0; dir<size; dir++) {
				
				dirID = validDirIDs[dir];
				long[] dirBitboards = dirs[dirID];
				
				for (int seq=0; seq<dirBitboards.length; seq++) {
					long field = dirs[dirID][seq];
					attacks |= field;
					
					if ((field & bitboard.free) == NUMBER_0) {
						
						if (IPlayerAttacks.SUPPORT_HIDDEN_ATTACKS) {
							/**
							 * Handle hidden attacks over castles
							 */
							long ownCastles = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_CASTLE);
							if ((field & ownCastles) != 0L) {
								continue;
							}
						}
						
						break;
					}
				}
			}
		} else {
			long[] dirBitboards = dirs[dirID];
			
			for (int seq=0; seq<dirBitboards.length; seq++) {
				long field = dirs[dirID][seq];
				attacks |= field;
				
				if ((field & bitboard.free) == NUMBER_0) {
					
					if (IPlayerAttacks.SUPPORT_HIDDEN_ATTACKS) {
						/**
						 * Handle hidden attacks over castles
						 */
						long ownCastles = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_CASTLE);
						if ((field & ownCastles) != 0L) {
							continue;
						}
					}
					
					break;
				}
			}
		}
		
		return attacks;
	}
}

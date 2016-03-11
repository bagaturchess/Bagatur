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
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class Officers extends Fields {
	
	public static final long genAttacks(int colour, int officerFieldID, int figureType, int dirID, Board bitboard, FieldsStateMachine fac, boolean add) {
		
		long attacks = NUMBER_0;
		
		final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[officerFieldID];
		final int[][] dirFieldIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[officerFieldID];
		
		if (dirID == -1) {
			final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[officerFieldID];
			
			final int size = validDirIDs.length;
			for (int dir=0; dir<size; dir++) {
				
				dirID = validDirIDs[dir];
				long[] dirBitboards = dirs[dirID];
				
				boolean stop = false;
				for (int seq=0; seq<dirBitboards.length; seq++) {
					long field = dirs[dirID][seq];
					attacks |= field;
					if (add) { 
						fac.addAttack(colour, figureType, dirFieldIDs[dirID][seq], field);
					} else {
						fac.removeAttack(colour, figureType, dirFieldIDs[dirID][seq], field);
					}
					
					if (stop) {
						break;
					}
					
					if ((field & bitboard.free) == NUMBER_0) {
						
						if (IPlayerAttacks.SUPPORT_HIDDEN_ATTACKS) {
							/**
							 * Handle hidden attacks over officers
							 */
							long ownOfficers = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_OFFICER);
							if ((field & ownOfficers) != 0L) {
								continue;
							}
							
							/**
							 * Handle hidden attacks over pawns
							 */
							if (colour == Figures.COLOUR_WHITE) {
								if (dirID == OfficerPlies.UP_LEFT_DIR || dirID == OfficerPlies.UP_RIGHT_DIR) {
									long ownPawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
									if ((field & ownPawns) != 0L) {
										stop = true;
										continue;
									}
								}
							} else {
								if (dirID == OfficerPlies.DOWN_LEFT_DIR || dirID == OfficerPlies.DOWN_RIGHT_DIR) {
									long ownPawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
									if ((field & ownPawns) != 0L) {
										stop = true;
										continue;
									}
								}
							}
						}
						
						if (!stop) {
							break;
						}
					}
				}
			}
		} else {
			long[] dirBitboards = dirs[dirID];
			
			boolean stop = false;
			for (int seq=0; seq<dirBitboards.length; seq++) {
				long field = dirs[dirID][seq];
				attacks |= field;
				if (add) { 
					fac.addAttack(colour, figureType, dirFieldIDs[dirID][seq], field);
				} else {
					fac.removeAttack(colour, figureType, dirFieldIDs[dirID][seq], field);
				}
				
				if (stop) {
					break;
				}
				
				if ((field & bitboard.free) == NUMBER_0) {
					
					if (IPlayerAttacks.SUPPORT_HIDDEN_ATTACKS) {
						/**
						 * Handle hidden attacks over officers
						 */
						long ownOfficers = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_OFFICER);
						if ((field & ownOfficers) != 0L) {
							continue;
						}
						
						/**
						 * Handle hidden attacks over pawns
						 */
						if (colour == Figures.COLOUR_WHITE) {
							if (dirID == OfficerPlies.UP_LEFT_DIR || dirID == OfficerPlies.UP_RIGHT_DIR) {
								long ownPawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
								if ((field & ownPawns) != 0L) {
									stop = true;
									continue;
								}
							}
						} else {
							if (dirID == OfficerPlies.DOWN_LEFT_DIR || dirID == OfficerPlies.DOWN_RIGHT_DIR) {
								long ownPawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
								if ((field & ownPawns) != 0L) {
									stop = true;
									continue;
								}
							}
						}
					}
					
					if (!stop) {
						break;
					}
				}
			}
		}
		
		return attacks;
	}

	public static long genAttacks(int colour, int officerFieldID, int dirID, Board bitboard) {
		
		long attacks = NUMBER_0;
		
		final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[officerFieldID];
		
		if (dirID == -1) {
			final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[officerFieldID];
			
			final int size = validDirIDs.length;
			for (int dir=0; dir<size; dir++) {
				
				dirID = validDirIDs[dir];
				long[] dirBitboards = dirs[dirID];
				
				boolean stop = false;
				for (int seq=0; seq<dirBitboards.length; seq++) {
					long field = dirs[dirID][seq];
					attacks |= field;
					
					if (stop) {
						break;
					}
					
					if ((field & bitboard.free) == NUMBER_0) {
						
						if (IPlayerAttacks.SUPPORT_HIDDEN_ATTACKS) {
							/**
							 * Handle hidden attacks over officers
							 */
							long ownOfficers = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_OFFICER);
							if ((field & ownOfficers) != 0L) {
								continue;
							}
							
							/**
							 * Handle hidden attacks over pawns
							 */
							if (colour == Figures.COLOUR_WHITE) {
								if (dirID == OfficerPlies.UP_LEFT_DIR || dirID == OfficerPlies.UP_RIGHT_DIR) {
									long ownPawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
									if ((field & ownPawns) != 0L) {
										stop = true;
										continue;
									}
								}
							} else {
								if (dirID == OfficerPlies.DOWN_LEFT_DIR || dirID == OfficerPlies.DOWN_RIGHT_DIR) {
									long ownPawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
									if ((field & ownPawns) != 0L) {
										stop = true;
										continue;
									}
								}
							}
						}
						
						if (!stop) {
							break;
						}
					}
				}
			}
		} else {
			long[] dirBitboards = dirs[dirID];
			
			boolean stop = false;
			for (int seq=0; seq<dirBitboards.length; seq++) {
				long field = dirs[dirID][seq];
				attacks |= field;
				
				if (stop) {
					break;
				}
				
				if ((field & bitboard.free) == NUMBER_0) {
					
					if (IPlayerAttacks.SUPPORT_HIDDEN_ATTACKS) {
						/**
						 * Handle hidden attacks over officers
						 */
						long ownOfficers = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_OFFICER);
						if ((field & ownOfficers) != 0L) {
							continue;
						}
						
						/**
						 * Handle hidden attacks over pawns
						 */
						if (colour == Figures.COLOUR_WHITE) {
							if (dirID == OfficerPlies.UP_LEFT_DIR || dirID == OfficerPlies.UP_RIGHT_DIR) {
								long ownPawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
								if ((field & ownPawns) != 0L) {
									stop = true;
									continue;
								}
							}
						} else {
							if (dirID == OfficerPlies.DOWN_LEFT_DIR || dirID == OfficerPlies.DOWN_RIGHT_DIR) {
								long ownPawns = bitboard.getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN);
								if ((field & ownPawns) != 0L) {
									stop = true;
									continue;
								}
							}
						}
					}
					
					if (!stop) {
						break;
					}
				}
			}
		}
		
		return attacks;
	}
}

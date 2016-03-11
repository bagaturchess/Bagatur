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

import bagaturchess.bitboard.common.Properties;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.state.PiecesList;

public class TypeAttacks {
	
	
	private int pid;
	private int colour;
	public int type;
	private Board bitboard;
	
	//public long all;
	public long[] unintersected;
	public int unintersected_size;
	
	private PiecesList aliveFigureIDs;
	private long[] attacksByFieldID;
	
	private FieldsStateMachine fieldAttacksCollector;
	
	public TypeAttacks(int _pid, Board _bitboard, FieldsStateMachine _fieldAttacksCollector) {
		pid = _pid;
		colour = Figures.getFigureColour(pid);
		type = Figures.getFigureType(pid);
		bitboard = _bitboard;
		fieldAttacksCollector = _fieldAttacksCollector;
		
		aliveFigureIDs = bitboard.pieces.getPieces(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][type]);//(colour)[type];
		attacksByFieldID = new long[Figures.ID_MAX];
		
		init();
	}
	
	private void init() {
		
		unintersected = new long[8];
		
		clear();
		genAllAttacks();
	}
	
	private void clear() {
		unintersected_size = 0;
		unintersected[0] = 0L;
	}
	
	private void genAllAttacks() {
		int size = aliveFigureIDs.getDataSize();
		int[] ids = aliveFigureIDs.getData();
		for (int i=0; i<size; i++) {
			int curFieldID = ids[i];
			
			if (fieldAttacksCollector != null) fieldAttacksCollector.addFigure(colour, type, curFieldID);
			
			long attacks = buildFigureAttacks(curFieldID, -1, -1, true);
			addFigureAttacks(curFieldID, attacks, false);
		}
	}

	private long buildFigureAttacks(int fieldID, int dirID, int dirType, boolean add) {
		if (fieldAttacksCollector != null) {
			return AttacksBuilder.genAttacks(bitboard,
				colour,
				type,
				fieldID,
				dirID, dirType, fieldAttacksCollector, add);
		} else {
			return AttacksBuilder.genAttacks(bitboard,
					colour,
					type,
					fieldID,
					dirID, dirType);
		}
	}
	
	public void addFigure(int fieldID, int dirID, int dirType) {
		
		if (Properties.DEBUG_MODE) {
			if (!aliveFigureIDs.contains(fieldID)) {
				/**
				 * Should be already created
				 */
				throw new IllegalStateException("Figure is dead");
			}
		}
		
		long attacks = buildFigureAttacks(fieldID, dirID, dirType, true);
		addFigureAttacks(fieldID, attacks, dirID != -1);
	}
	
	public void removeFigure(int fieldID, int dirID, int dirType) {
		
		if (Properties.DEBUG_MODE) {
			if (!aliveFigureIDs.contains(fieldID)) {
				/**
				 * Should not be already removed
				 */
				throw new IllegalStateException("Figure not dead");
			}
		}
		
		long attacks = buildFigureAttacks(fieldID, dirID, dirType, false);
		removeFigureAttacks(fieldID, attacks, dirID != -1);
	}
	
	private void addFigureAttacks(int fieldID, long attacks, boolean partial) {
		
		if (partial) {
			attacksByFieldID[fieldID] |= attacks;
		} else {
			attacksByFieldID[fieldID] = attacks;
		}
		
		long allBackup = unintersected[0];
		//long allBackup = all;
		//all |= attacks;
		long intersection = attacks & allBackup;
		if (intersection != 0L) {
			if (unintersected_size == 0) {
				unintersected[unintersected_size++] = attacks;
				unintersected[unintersected_size++] = intersection;
			} else {
				for (int i=0; i<unintersected_size; i++) {
					long cur = unintersected[i];
					intersection = attacks & cur;
					unintersected[i] = cur | attacks;
					attacks = intersection;
					if (attacks == 0L) {
						break;
					}
					if (i == unintersected_size - 1) {
						unintersected[unintersected_size++] = attacks;
						break;
					}
				}
			}
		} else {
			if (unintersected_size == 0) {
				unintersected_size++;
			}
			unintersected[0] |= attacks;
		}
	}
	
	private void removeFigureAttacks(int fieldID, long attacks, boolean partial) {
		/*if (attacks != attacksByFigureID[figureID]) {
			throw new IllegalStateException();
		}*/
		if (unintersected_size < 1) {
			throw new IllegalStateException();
		}
		
		if (partial) {
			attacksByFieldID[fieldID] &= ~attacks;
		} else {
			attacksByFieldID[fieldID] = 0L;
		}
		
		int size = unintersected_size;
		for (int i = size-1; i >= 0; i--) {
			//long section = unintersected[1] & attacks;
			long cur = unintersected[i];
			long intersection = attacks & cur;
			unintersected[i] = unintersected[i] & ~attacks;
			if (unintersected[i] == 0) {
				unintersected_size--;
			}
			attacks = attacks & ~intersection;
			if (attacks == 0L) {
				break;
			}
			/*intersection = attacks & cur;
			unintersected[i] = cur | attacks;
			attacks = intersection;
			if (attacks == 0L) {
				break;
			}
			if (i == unintersected_size - 1) {
				unintersected[unintersected_size++] = attacks;
				break;
			}*/
		}
		/*switch (unintersected_size) {
			case 1:
				unintersected[0] = unintersected[0] & ~attacks;
				if (unintersected[0] == 0) {
					unintersected_size--;
				}
				break;
			case 2:
				long section = unintersected[1] & attacks;
				unintersected[1] = unintersected[1] & ~attacks;
				if (unintersected[1] == 0) {
					unintersected_size--;
				}
				attacks = attacks & ~section;
				unintersected[0] = unintersected[0] & ~attacks;
				break;
			case 3:
				throw new IllegalStateException("Unsupported: 3 attacks");
				//break;
			default:
				throw new IllegalStateException();
		}*/
	}
	
	public int attacksByTypeUnintersectedSize() {
		return unintersected_size;
	}
	
	public long[] attacksByTypeUnintersected() {
		return unintersected;
	}
	
	public long attacksByFieldID(int fieldID) {
		
		/*if (type != Figures.getFigureType(fieldID)) {
			throw new IllegalStateException("type=" + type+ "Figures.getFigureType(figureID)=" + Figures.getFigureType(fieldID));
		}*/
		
		return attacksByFieldID[fieldID];
	}
	
	public long allAttacks() {
		//return all;
		if (unintersected_size < 1 && unintersected[0] != 0) {
			throw new IllegalStateException();
		}
		return unintersected[0];
	}
	
	public void checkConsistency() {
		if (unintersected_size < 0) {
			throw new IllegalStateException();
		}
		
		if (type != Figures.TYPE_PAWN) {
			long testAll = 0L;
			int size = aliveFigureIDs.getDataSize();
			int[] ids = aliveFigureIDs.getData();
			for (int i=0; i<size; i++) {
				int curFigureID = ids[i];
				long curAttacks = attacksByFieldID[curFigureID];
				if (curAttacks == 0L) {
					throw new IllegalStateException("attacks is 0");
				}
				testAll |= curAttacks;
			}
			if (testAll != unintersected[0]) {
				throw new IllegalStateException();
			}
		}
		
		if (unintersected[0] != 0L && unintersected_size <= 0) {
			throw new IllegalStateException();
		}
		
		if (unintersected_size > 0) {
			long testAll = 0L;
			for (int i=0; i<unintersected_size; i++) {
				long curAttacks = unintersected[i];
				if (curAttacks == 0L) {
					throw new IllegalStateException("attacks is 0");
				}
				testAll |= curAttacks;
			}
			if (testAll != unintersected[0]) {
				throw new IllegalStateException();
			}
			
			for (int i=unintersected_size - 1; i>=1; i--) {
				long iAttacks = unintersected[i];
				if (iAttacks == 0L) {
					throw new IllegalStateException("attacks is 0");
				}
				long beforiAttacks = unintersected[i - 1];
				if (beforiAttacks == 0L) {
					throw new IllegalStateException("attacks is 0");
				}
				if ((iAttacks & beforiAttacks) != iAttacks) {
					throw new IllegalStateException();
				}
			}
		}
	}
}

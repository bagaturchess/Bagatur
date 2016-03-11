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
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;

public class FastPlayerAttacks implements IPlayerAttacks {
	
	
	private int colour;
	private Board bitboard;
	
	private TypeAttacks king;
	private TypeAttacks pawns;
	private TypeAttacks knights;
	private TypeAttacks officers;
	private TypeAttacks castles;
	private TypeAttacks queens;
	
	private FieldsStateMachine fieldAttacksCollector;
	
	
	public FastPlayerAttacks(int _colour, Board _bitboard, FieldsStateMachine _fieldAttacksCollector) {
		colour = _colour;
		bitboard = _bitboard;
		fieldAttacksCollector = _fieldAttacksCollector;
		
		if (colour == Figures.COLOUR_WHITE) {
			king = new TypeAttacks(Constants.PID_W_KING, bitboard, fieldAttacksCollector);
			pawns = new TypeAttacks(Constants.PID_W_PAWN, bitboard, fieldAttacksCollector);
			knights = new TypeAttacks(Constants.PID_W_KNIGHT, bitboard, fieldAttacksCollector);
			officers = new TypeAttacks(Constants.PID_W_BISHOP, bitboard, fieldAttacksCollector);
			castles = new TypeAttacks(Constants.PID_W_ROOK, bitboard, fieldAttacksCollector);
			queens = new TypeAttacks(Constants.PID_W_QUEEN, bitboard, fieldAttacksCollector);
		} else {
			king = new TypeAttacks(Constants.PID_B_KING, bitboard, fieldAttacksCollector);
			pawns = new TypeAttacks(Constants.PID_B_PAWN, bitboard, fieldAttacksCollector);
			knights = new TypeAttacks(Constants.PID_B_KNIGHT, bitboard, fieldAttacksCollector);
			officers = new TypeAttacks(Constants.PID_B_BISHOP, bitboard, fieldAttacksCollector);
			castles = new TypeAttacks(Constants.PID_B_ROOK, bitboard, fieldAttacksCollector);
			queens = new TypeAttacks(Constants.PID_B_QUEEN, bitboard, fieldAttacksCollector);
		}
	}
	
	
	private TypeAttacks getTypeAttacks(int pid) {
		int type = Figures.getFigureType(pid);
		return getTypeAttacksByType(type);
	}

	private TypeAttacks getTypeAttacksByType(int type) {
		switch (type) {
			case Figures.TYPE_KING:
				return king;
			case Figures.TYPE_KNIGHT:
				return knights;
			case Figures.TYPE_PAWN:
				return pawns;
			case Figures.TYPE_OFFICER:
				return officers;
			case Figures.TYPE_CASTLE:
				return castles;
			case Figures.TYPE_QUEEN:
				return queens;
			default:
				throw new IllegalStateException();
		}
	}

	/**
	 * Implementation of IPlayerAttacks
	 */
	public long allAttacks() {
		return pawns.allAttacks() | knights.allAttacks() | officers.allAttacks()
						| castles.allAttacks() | queens.allAttacks() | king.allAttacks();
	}

	public long attacksByType(int type) {
		return getTypeAttacksByType(type).allAttacks();
	}

	public long[] attacksByTypeUnintersected(int type) {
		return getTypeAttacksByType(type).attacksByTypeUnintersected();
	}

	public int attacksByTypeUnintersectedSize(int type) {
		return getTypeAttacksByType(type).attacksByTypeUnintersectedSize();
	}
	
	public long attacksByFieldID(int type, int figureID) {
		
		//int type = Figures.getFigureType(figureID);
		switch(type) {
			case Figures.TYPE_PAWN:
				return pawns.attacksByFieldID(figureID);
			case Figures.TYPE_KNIGHT:
				return knights.attacksByFieldID(figureID);
			case Figures.TYPE_OFFICER:
				return officers.attacksByFieldID(figureID);
			case Figures.TYPE_CASTLE:
				return castles.attacksByFieldID(figureID);
			case Figures.TYPE_QUEEN:
				return queens.attacksByFieldID(figureID);
			case Figures.TYPE_KING:
				return king.attacksByFieldID(figureID);
			default:
				throw new IllegalStateException(); 
		}
	}

	public void removeFigure(int pid, int fieldID, int dirID, int dirType) {
		TypeAttacks attacks = getTypeAttacks(pid);
		attacks.removeFigure(fieldID, dirID, dirType);
		if (fieldAttacksCollector != null) fieldAttacksCollector.removeFigure(colour, Figures.getFigureType(pid), fieldID);
	}
	
	public void introduceFigure(int pid, int fieldID, int dirID, int dirType) {
		TypeAttacks attacks = getTypeAttacks(pid);
		attacks.addFigure(fieldID, dirID, dirType);	
		if (fieldAttacksCollector != null) fieldAttacksCollector.addFigure(colour, Figures.getFigureType(pid), fieldID);
	}
	
	public int countAttacks(int type, long field) {
		int size = attacksByTypeUnintersectedSize(type);
		long[] attacks = attacksByTypeUnintersected(type);
		int count = 0;
		for (int i=0; i<size; i++) {
			long a = attacks[i];
			if ((a & field) != 0L) {
				count++;
			}
		}
		return count;
	}

	public void checkConsistency() {
		king.checkConsistency();
		pawns.checkConsistency();
		knights.checkConsistency();
		officers.checkConsistency();
		castles.checkConsistency();
		queens.checkConsistency();
	}	
}

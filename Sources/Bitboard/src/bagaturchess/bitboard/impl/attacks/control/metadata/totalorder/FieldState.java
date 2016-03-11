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
package bagaturchess.bitboard.impl.attacks.control.metadata.totalorder;

import java.io.Serializable;

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacks;


public class FieldState implements Cloneable, Serializable {
	
	private static final long serialVersionUID = -5974777238348745497L;
	
	int id;
	int figureOnFieldColour;
	int figureOnFieldType;
	FieldAttacks whiteAttacks;
	FieldAttacks blackAttacks;
	
	public FieldState(int id, int figureOnFieldColour, int figureOnFieldType,
			FieldAttacks whiteAttacks, FieldAttacks blackAttacks) {
		super();
		this.id = id;
		this.figureOnFieldColour = figureOnFieldColour;
		this.figureOnFieldType = figureOnFieldType;
		this.whiteAttacks = whiteAttacks;
		this.blackAttacks = blackAttacks;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		if (figureOnFieldType == Figures.TYPE_UNDEFINED) {
			result += "OnField: " + "--" + " ";
		} else {
			result += "OnField: " + Figures.COLOURS_SIGN[figureOnFieldColour] + Figures.TYPES_SIGN[figureOnFieldType] + " ";
		}
		
		result += ", WhiteAttacks: " + whiteAttacks + " ";
		result += ", BlackAttacks: " + blackAttacks + " ";
		
		return result;
	}
	
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		FieldState other = (FieldState) o;
		
		return figureOnFieldColour == other.figureOnFieldColour
						&& figureOnFieldType == other.figureOnFieldType
						&& whiteAttacks.equals(other.whiteAttacks)
						&& blackAttacks.equals(other.blackAttacks);
	}
	
	public int hashCode() {
		int hash = (figureOnFieldColour << 29) + (figureOnFieldType << 26) + (whiteAttacks.hashCode() ^ blackAttacks.hashCode());
		//System.out.println(hash);
		return hash;
	}
	
	@Override
	public FieldState clone() {
		FieldState result = null;
		try {
			result = (FieldState) super.clone();
			
			result.id = id;
			result.figureOnFieldColour = figureOnFieldColour;
			result.figureOnFieldType = figureOnFieldType;
			result.whiteAttacks = whiteAttacks;
			result.blackAttacks = blackAttacks;
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	public FieldAttacks getBlackAttacks() {
		return blackAttacks;
	}

	public int getId() {
		return id;
	}

	public FieldAttacks getWhiteAttacks() {
		return whiteAttacks;
	}

	public int getFigureOnFieldColour() {
		return figureOnFieldColour;
	}

	public int getFigureOnFieldType() {
		return figureOnFieldType;
	}
}

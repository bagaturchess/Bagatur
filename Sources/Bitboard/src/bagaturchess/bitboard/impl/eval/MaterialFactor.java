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
package bagaturchess.bitboard.impl.eval;

import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class MaterialFactor implements MoveListener, IMaterialFactor, Cloneable {

	
	protected int whiteMaterialFactor;
	protected int blackMaterialFactor;	
	
	
	public MaterialFactor() {
		init();
	}
	
	private void init() {
		whiteMaterialFactor = 0;
		blackMaterialFactor = 0;
	}
	
	public int getBlackFactor() {
		return blackMaterialFactor;
	}

	public int getWhiteFactor() {
		return whiteMaterialFactor;
	}
	
	public int getTotalFactor() {
		return blackMaterialFactor + whiteMaterialFactor;
	}
	
	public double getOpenningPart() {
		double openningPart = getTotalFactor() / (double) BaseEvalWeights.getMaxMaterialFactor();
		if (openningPart > 1) {
			openningPart = 1;
		}
		return openningPart;
	}
	
	public int interpolateByFactor(int val_o, int val_e) {
		double openningPart = getOpenningPart();
		return (int) (val_o * openningPart + val_e * (1 - openningPart));
	}
	
	/*public int interpolateByFactorAndColour(int colour, int val_o, int val_e) {
		if (colour == Figures.COLOUR_WHITE) {
			return BaseEvalWeights.interpolateByFactorAndColour(val_o, val_e, getWhiteFactor());
		} else {
			return BaseEvalWeights.interpolateByFactorAndColour(val_o, val_e, getBlackFactor());
		}
	}*/
	
	
	public int interpolateByFactor(double val_o, double val_e) {
		return interpolateByFactor((int)val_o, (int)val_e);
	}

	
	public void addPiece_Special(int pid, int fieldID) {
		throw new UnsupportedOperationException();
	}
	
	
	public void initially_addPiece(int pid, int fieldID, long bb_pieces) {
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
		// TODO Auto-generated method stub
		
	}
	
	public void preForwardMove(int color, int move) {
		// TODO Auto-generated method stub
		
	}
	
	protected void added(int figurePID) {
		inc(figurePID);
	}
	
	protected void removed(int figurePID) {
		dec(figurePID);
	}
	
	private void inc(int figurePID) {
		
		int figureColour = Figures.getFigureColour(figurePID);
		int figureType = Figures.getFigureType(figurePID);
		int factor = BaseEvalWeights.getFigureMaterialFactor(figureType);
		
		switch(figureColour) {
			case Figures.COLOUR_WHITE:
				whiteMaterialFactor += factor;
				break;
			case Figures.COLOUR_BLACK:
				blackMaterialFactor += factor;
				break;
			default:
				throw new IllegalArgumentException(
						"Figure colour " + figureColour + " is undefined!");
		}
	}
	
	protected void dec(int figurePID) {
		int figureColour = Figures.getFigureColour(figurePID);
		int figureType = Figures.getFigureType(figurePID);
		int factor = BaseEvalWeights.getFigureMaterialFactor(figureType);
		
		switch(figureColour) {
			case Figures.COLOUR_WHITE:
				whiteMaterialFactor -= factor;
				break;
			case Figures.COLOUR_BLACK:
				blackMaterialFactor -= factor;
				break;
			default:
				throw new IllegalArgumentException(
						"Figure colour " + figureColour + " is undefined!");
		}
	}
	
	@Override
	public MaterialFactor clone() {
		MaterialFactor clone = null;
		try {
			clone = (MaterialFactor) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		clone.whiteMaterialFactor = whiteMaterialFactor;
		clone.blackMaterialFactor = blackMaterialFactor;
		
		return clone;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof MaterialFactor) {
			MaterialFactor other = (MaterialFactor) obj;
			result = other.whiteMaterialFactor == whiteMaterialFactor
							&& other.blackMaterialFactor == blackMaterialFactor;			
		}
		return result;
	}
}

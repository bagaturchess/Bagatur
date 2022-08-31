/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.bitboard.api;


public interface IMoveOps {
	public boolean isCapture(int move);
	public boolean isPromotion(int move);
	public boolean isCaptureOrPromotion(int move);
	public boolean isEnpassant(int move);
	public boolean isCastling(int move);
	public boolean isCastlingKingSide(int move);
	public boolean isCastlingQueenSide(int move);
	public int getFigurePID(int move);
	public int getToFieldID(int move);
	public int getToField_File(int move);
	public int getToField_Rank(int move);
	public int getFromFieldID(int move);
	public int getFromField_File(int move);
	public int getFromField_Rank(int move);
	public int getFigureType(int move);
	public int getCapturedFigureType(int move);
	public int getPromotionFigureType(int move);
	public String moveToString(int move);
	public void moveToString(int move, StringBuilder text_buffer);
	public int stringToMove(String move);
}

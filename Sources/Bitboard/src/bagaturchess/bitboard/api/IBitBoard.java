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
package bagaturchess.bitboard.api;


public interface IBitBoard extends IBoard {
	
	
	public void mark();
	public void reset();
	public void revert();
	
	public void setPawnsCache(PawnsEvalCache pawnsCache);
	
	/**
	 * Base engine's methods  
	 */
	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields, final IInternalMoveList list);
	public int genAllMoves(final IInternalMoveList list, long excludedToFieldsBoard);
	public int genKingEscapes(final IInternalMoveList list);
	
	public int getCastlingType(int colour);
	public boolean hasRightsToKingCastle(int colour);
	public boolean hasRightsToQueenCastle(int colour);
	
	public boolean hasMoveInNonCheck();
	public boolean hasMoveInCheck();
	public boolean hasSingleMove();
	
	public boolean isInCheck();
	public boolean isInCheck(int colour);
	public boolean isCheckMove(int move);
	
	public boolean isPossible(int move);
	
	/**
	 * Game related methods
	 */
	public int getPlayedMovesCount();
	public int[] getPlayedMoves();
	public int getLastMove();
	public IGameStatus getStatus();
	
	/**
	 * Birboards
	 */
	public long getFreeBitboard();
	
	public boolean getAttacksSupport();
	public boolean getFieldsStateSupport();
	public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport);
	public IPlayerAttacks getPlayerAttacks(int colour);
	public IFieldsAttacks getFieldsAttacks();
}

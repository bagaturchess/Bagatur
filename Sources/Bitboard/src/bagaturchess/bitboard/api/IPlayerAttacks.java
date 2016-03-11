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


public interface IPlayerAttacks {
	
	public static final boolean DEBUG_ATTACKS = false;//Properties.DEBUG_MODE;
	public static final boolean SUPPORT_HIDDEN_ATTACKS = false;
	
	public long allAttacks();
	public long attacksByType(int type);
	public int attacksByTypeUnintersectedSize(int type);
	public long[] attacksByTypeUnintersected(int type);
	public long attacksByFieldID(int type, int fieldID);
	public int countAttacks(int type, long field);
	public void checkConsistency();
}

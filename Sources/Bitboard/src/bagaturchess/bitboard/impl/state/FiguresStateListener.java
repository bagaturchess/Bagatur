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
package bagaturchess.bitboard.impl.state;

public interface FiguresStateListener {
	
	public void added(int figureID);
	
	public void killed(int figureID);
	public void revived(int figureID);
	
	public void produced(int figureID);
	public void released(int figureID);
	
	public void moveForward(long[] move);
	
	public void moveBackward(long[] move);

	public long getWhiteMaterial();

	public long getBlackMaterial();
	
	public void clearSameFigureMoves();
	
	public int getBlackSameFigureMoves();

	public int getWhiteSameFigureMoves();
	
	public int getMovesCount(int figureID);

	public int getWhiteFactor();

	public int getBlackFactor();
}

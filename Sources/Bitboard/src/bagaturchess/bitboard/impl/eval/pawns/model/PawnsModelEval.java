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
package bagaturchess.bitboard.impl.eval.pawns.model;


import bagaturchess.bitboard.api.IBoard;


public class PawnsModelEval {
	
	private static final int[] PASSER_RANK_SCALE = new int[] {0, 2, 2, 4, 7, 10, 12, 0};
	//private static final int[] PASSER_RANK_SCALE = new int[] {0, 1, 1, 2, 4, 6, 8, 0};
	
	private PawnsModel model = new PawnsModel();
	
	public PawnsModelEval() {
	}
	
	public void rebuild(IBoard bitboard) {
		model.rebuild(bitboard);
		eval();
	}

	public PawnsModel getModel() {
		return model;
	}
	
	protected void eval() {
		//Override this
	}
	
	public static int getPasserRankBonus(int rank) {
		return PASSER_RANK_SCALE[rank];
	}
}

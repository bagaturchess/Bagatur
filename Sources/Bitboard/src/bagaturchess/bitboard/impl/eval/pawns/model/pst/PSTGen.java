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
package bagaturchess.bitboard.impl.eval.pawns.model.pst;

import bagaturchess.bitboard.common.Utils;

public class PSTGen {
	
	public static final int[] W_TRAPPED_MINOR = Utils.reverseSpecial ( new int[]{	
			 -50, -40, -35, -35, -35, -35, -40, -50, 
			 -40, -35, -30, -30, -30, -30, -35, -40, 
			 -35, -30, -25, -25, -25, -25, -30, -35, 
			 -30, -25, -20, -20, -20, -20, -25, -30, 
			 -25, -20, -15, -10, -10, -15, -20, -25, 
			 -25, -20, -15, -10, -10, -15, -20, -25, 
			 -25, -20, -15, -15, -15, -15, -20, -25, 
			 -30, -25, -20, -20, -20, -20, -25, -30, 
		});
	
	public static final int[] B_TRAPPED_MINOR = Utils.reverseSpecial ( new int[]{	
			 -30, -25, -20, -20, -20, -20, -25, -30, 
			 -25, -20, -15, -15, -15, -15, -20, -25, 
			 -25, -20, -15, -10, -10, -15, -20, -25, 
			 -25, -20, -15, -10, -10, -15, -20, -25, 
			 -30, -25, -20, -20, -20, -20, -25, -30, 
			 -35, -30, -25, -25, -25, -25, -30, -35, 
			 -40, -35, -30, -30, -30, -30, -35, -40, 
			 -50, -40, -35, -35, -35, -35, -40, -50, 
		});
	
	public static int PENALTY_ON_ATTACKED_SQUARE = -20;
	public static int BONUS_ATTACK = 2;
	public static int BONUS_ATTACK_SAFE = 4;
	public static int BONUS_ATTACK_UNDEFENDED_PAWN = 8;
	//private static int BONUS_ATTACK_OP_KING = 20;
}

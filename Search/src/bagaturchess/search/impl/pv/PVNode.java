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
package bagaturchess.search.impl.pv;


import java.util.List;


public class PVNode {
	
	public static final int TYPE_NORMAL_SEARCH			= 1;
	public static final int TYPE_NORMAL_QSEARCH 		= 2;
	public static final int TYPE_DRAW 					= 3;
	public static final int TYPE_MATE 					= 4;
	public static final int TYPE_MAXDEPTH				= 5;
	public static final int TYPE_MATE_DISTANCE_PRUNING 	= 6;
	public static final int TYPE_TT 					= 7;
	public static final int TYPE_TB 					= 8;
	public static final int TYPE_STATIC_NULL_MOVE 		= 9;
	public static final int TYPE_NULL_MOVE 				= 10;
	public static final int TYPE_RAZORING 				= 11;
	public static final int TYPE_PROBECUT 				= 12;
	public static final int TYPE_MULTICUT 				= 13;
	public static final int TYPE_BETA_CUTOFF_QSEARCH 	= 14;
	public static final int TYPE_ALPHA_RESTORE_QSEARCH 	= 15;
	
	
	public PVNode parent;
	public PVNode child;
	
	public int eval;
	public int bestmove;
	public boolean leaf;
	public int type;
	
	
	public PVNode() {
		bestmove = 0;
		leaf = true;
	}
	
	
	public static int[] convertPV(PVNode line, List<Integer> buff) {
		
		extractPV(line, buff);
		
		int[] result = new int[buff.size()];
		for (int i=0; i<result.length; i++) {
			result[i] = buff.get(i);
		}
		return result;
	}
	
	
	private static void extractPV(PVNode res, List<Integer> result) {
		PVNode cur = res;
		while(cur != null && cur.bestmove != 0) {
			result.add(cur.bestmove);
			if (cur.leaf) {
				break;
			}
			cur = cur.child;
		}
	}
}


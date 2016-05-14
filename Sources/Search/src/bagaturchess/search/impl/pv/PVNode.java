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


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.search.api.internal.ISearchMoveList;


public class PVNode {
	
	public static long MASK_INIT = 0L;
	public static long MASK_NOMOVES = 1L;
	public static long MASK_REPETITION = 2L; 
	public static long MASK_DRAW = 4L;
	public static long MASK_MAXDEPTH = 8L;
	public static long MASK_TPT = 16L;
	public static long MASK_NULLMOVE = 32L;
	public static long MASK_NULLMOVEVER = 64L;
	public static long MASK_REDUCED = 128L;
	public static long MASK_PASSED_PAWN = 256L;
	
	public long masks;
	
	public PVNode parent;
	public PVNode child;
	
	public int eval;
	public int bestmove;
	
	public boolean leaf;
	public boolean nullmove;
	
	public long hashkey;
	public ISearchMoveList moves;
	
	
	public PVNode() {
		bestmove = 0;
		leaf = true;
	}
	
	public static List<Integer> extractPV(PVNode res) {
		List<Integer> list = new ArrayList<Integer>();
		PVNode cur = res;
		while(cur != null && (cur.bestmove != 0 || cur.nullmove)) {
			list.add( cur.nullmove ? 0 : cur.bestmove);
			cur = cur.child;
			if (cur != null && cur.leaf) {
				break;
			}
		}
		return list;
	}
	
	public static int[] convertPV(List<Integer> list) {
		int[] result = new int[list.size()];
		for (int i=0; i<result.length; i++) {
			result[i] = list.get(i);
		}
		return result;
	}
	
	public static boolean checkLine(int length, PVNode node) {
		
		if (!valid(node)) {
			return true;
		}
		
		int counter = 0;
		
		while (node != null && !node.leaf) {
			counter++;
			node = node.child;
			if (!valid(node)) {
				return true;
			}
		}
		if (counter < length) {
			//throw new IllegalStateException("expected: " + length + ", actual:" + counter);
			return false;
		}
		return true;
	}
	
	public static boolean valid(PVNode node) {
		return (node.masks == PVNode.MASK_INIT); 
	}
	@Override
	public String toString() {
		String result = "";
		
		PVNode cur = this;
		while(cur != null && (cur.bestmove != 0 || cur.nullmove)) {
			result += cur.nullmove ? "OOOO, " : MoveInt.moveToString(cur.bestmove) + ", ";
			cur = cur.child;
		}
		
		return result;
	}
	
	public String propsToString() { 
		String result = "";
		result += "nullmove=" + nullmove;
		result += ", leaf=" + leaf;
		result += ", bestmove=" + bestmove;
		result += ", eval=" + eval;
		return result;
	}
}


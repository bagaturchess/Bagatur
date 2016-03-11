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
package bagaturchess.opening.api.traverser;


import java.util.HashSet;
import java.util.Set;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.impl.model.Entry_BaseImpl;



public class OpeningTraverser {
	
	public static void traverseAll(OpeningBook ob, OpeningsVisitor visitor) {
		visitor.begin();
		
		IBitBoard bitboard = new Board();
		traverseAll(ob, visitor, bitboard, new HashSet<Long>());
		
		visitor.end();
	}
	
	private static void traverseAll(OpeningBook ob, OpeningsVisitor visitor, IBitBoard bitboard, Set<Long> keys) {
		
		long hashkey = bitboard.getHashKey();
		
		if (keys.contains(hashkey)) {
			return;
		}
		keys.add(hashkey);
		
		IOpeningEntry entry = ob.getEntry(hashkey, bitboard.getColourToMove());
		if (entry != null) {
			int[] moves = entry.getMoves();	
			for (int i=0; i<moves.length; i++) {
				int move = moves[i];
				
				bitboard.makeMoveForward(move);
				visitor.visitPosition(bitboard);
				traverseAll(ob, visitor, bitboard, keys);	
				bitboard.makeMoveBackward(move);
			}
		}
	}
	
	public static void traverseLeafs(OpeningBook ob, OpeningsVisitor visitor) {
		visitor.begin();
		
		IBitBoard bitboard = new Board();
		traverseLeafs(ob, visitor, bitboard, new HashSet<Long>());
		
		visitor.end();
	}
	
	private static void traverseLeafs(OpeningBook ob, OpeningsVisitor visitor, IBitBoard bitboard, Set<Long> keys) {
		
		long hashkey = bitboard.getHashKey();
		
		if (keys.contains(hashkey)) {
			return;
		}
		keys.add(hashkey);
		
		IOpeningEntry entry = ob.getEntry(hashkey, bitboard.getColourToMove());
		if (entry != null) {
			int[] moves = entry.getMoves();	
			for (int i=0; i<moves.length; i++) {
				int move = moves[i];
				bitboard.makeMoveForward(move);
				traverseLeafs(ob, visitor, bitboard, keys);
				bitboard.makeMoveBackward(move);
			}
		} else {
			visitor.visitPosition(bitboard);
		}
	}
	
	public static void traverseDepth(OpeningBook ob, OpeningsVisitor visitor, int depth) {
		visitor.begin();
		IBitBoard bitboard = new Board();
		traverseDepth(ob, visitor, bitboard, depth, 0, new HashSet<Long>());
		visitor.end();
	}
	
	private static void traverseDepth(OpeningBook ob, OpeningsVisitor visitor, IBitBoard bitboard, int depth, int curDepth, Set<Long> keys) {
		
		if (curDepth >= depth) {
			return;
		}
		
		long hashkey = bitboard.getHashKey();
		
		if (keys.contains(hashkey)) {
			return;
		}
		keys.add(hashkey);
		
		IOpeningEntry entry = ob.getEntry(hashkey, bitboard.getColourToMove());
		if (entry != null) {
			int[] moves = entry.getMoves();	
			for (int i=0; i<moves.length; i++) {
				int move = moves[i];
				
				bitboard.makeMoveForward(move);
				if (curDepth == depth - 1) {
					visitor.visitPosition(bitboard);
				}
				traverseDepth(ob, visitor, bitboard, depth, curDepth + 1, keys);		
				bitboard.makeMoveBackward(move);
			}
		} else {
			visitor.visitPosition(bitboard);
		}
	}
}

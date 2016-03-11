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
package bagaturchess.bitboard.impl.movelist;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class DividedMoveList implements IMoveList {

	private static final int PHASE_PRIMARY = 0;
	private static final int PHASE_SECONDARY = 1;
	
	private IBitBoard bitboard;

	private long[] primary; 
	private long[] secondary;
	private int primary_size; 
	private int secondary_size;

	private int size;
	private int cur;
	private int phase;
	
	private boolean in_iteration;
	
	public DividedMoveList(IBitBoard _bitboard) {
		bitboard = _bitboard;
		primary = new long[62];
		secondary = new long[128];
		clear();
	}
	
	public void clear() {
		primary_size = 0;
		secondary_size = 0;
		size = 0;
		cur = 0;
		phase = PHASE_PRIMARY;
		in_iteration = false;
	}
	
	public int next() {
		in_iteration = true;
		while (true) {
			switch(phase) {
				case PHASE_PRIMARY:
					if (cur < primary_size) {
						return (int) primary[cur++];
					} else {
						cur = 0;
						phase = PHASE_SECONDARY;
					}
					break;
				case PHASE_SECONDARY:
					if (cur < secondary_size) {
						return (int) secondary[cur++];
					} else {
						return 0;
					}
					//break;
				default:
					throw new IllegalStateException();
			}
		}
	}

	public int size() {
		if (in_iteration) {
			throw new IllegalStateException();
		}
		return size;
	}

	public void reserved_add(int move) {
		if (in_iteration) {
			throw new IllegalStateException();
		}
		if (MoveInt.isCapture(move)) {
			addPrimary(move);
		} else {
			addSecondary(move);
		}
	}

	private void addPrimary(int move) {
		long see = bitboard.getSee().evalExchange(move);
		long move_ord = (see << 32) | move;
		
		if (primary_size == 0) {
			primary[primary_size++] = move_ord;
			size++;
		} else {
			if (move_ord > primary[0]) {
				primary[primary_size] = primary[0];
				primary[0] = move_ord;
				primary_size++;
				size++;
			} else {
				primary[primary_size++] = move_ord;
				size++;
			}
		}
	}
	
	private void addSecondary(int move) {
		secondary[secondary_size++] = move;
		size++;
		
		/*long see = bitboard.getSee().evalExchange(move);
		long move_ord = (see << 32) | move;
		
		if (secondary_size == 0) {
			secondary[secondary_size++] = move_ord;
			size++;
		} else {
			if (move_ord > secondary[0]) {
				secondary[secondary_size] = secondary[0];
				secondary[0] = move_ord;
				secondary_size++;
				size++;
			} else {
				secondary[secondary_size++] = move_ord;
				size++;
			}
		}*/
	}
	
	/**
	 * Unsupported operations 
	 */
	
	public void reserved_clear() {
		throw new IllegalStateException();
	}
	
	public int reserved_getCurrentSize() {
		throw new IllegalStateException();
	}

	public int[] reserved_getMovesBuffer() {
		throw new IllegalStateException();
	}

	public void reserved_removeLast() {
		throw new IllegalStateException();
	}
}

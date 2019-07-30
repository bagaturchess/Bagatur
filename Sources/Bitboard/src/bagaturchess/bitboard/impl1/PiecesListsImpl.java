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
package bagaturchess.bitboard.impl1;

import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.impl.state.PiecesList;

/**
 * @author i027638
 *
 */
public class PiecesListsImpl implements IPiecesLists {
	
	
	private PiecesList list;
	
	
	PiecesListsImpl(IBoard board) {
		list = new PiecesList(board, 10);
		list.add(16);
	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IPiecesLists#rem(int, int)
	 */
	@Override
	public void rem(int pid, int fieldID) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IPiecesLists#add(int, int)
	 */
	@Override
	public void add(int pid, int fieldID) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IPiecesLists#move(int, int, int)
	 */
	@Override
	public void move(int pid, int fromFieldID, int toFieldID) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IPiecesLists#getPieces(int)
	 */
	@Override
	public PiecesList getPieces(int pid) {
		return list;
	}

}

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


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
/**
 * Created by Krasimir Topchiyski
 * Date: 2004-2-18
 * Time: 19:56:21
 *
 */
public class PiecesLists extends Figures implements IPiecesLists {
	
	private PiecesList[] pieces;
	private IBoard board;
	
	public PiecesLists(IBoard _board) {
		
		board = _board;
		
		pieces = new PiecesList[Constants.PID_MAX];

		pieces[Constants.PID_W_PAWN] = new PiecesList(board, 8);
		pieces[Constants.PID_W_KNIGHT] = new PiecesList(board, 8);
		pieces[Constants.PID_W_KING] = new PiecesList(board, 8);
		pieces[Constants.PID_W_BISHOP] = new PiecesList(board, 8);
		pieces[Constants.PID_W_ROOK] = new PiecesList(board, 8);
		pieces[Constants.PID_W_QUEEN] = new PiecesList(board, 8);
		
		pieces[Constants.PID_B_PAWN] = new PiecesList(board, 8);
		pieces[Constants.PID_B_KNIGHT] = new PiecesList(board, 8);
		pieces[Constants.PID_B_KING] = new PiecesList(board, 8);
		pieces[Constants.PID_B_BISHOP] = new PiecesList(board, 8);
		pieces[Constants.PID_B_ROOK] = new PiecesList(board, 8);
		pieces[Constants.PID_B_QUEEN] = new PiecesList(board, 8);
	}
	
	public void rem(int pid, int fieldID) {
		pieces[pid].remove(fieldID);
	}

	public void add(int pid, int fieldID) {
		pieces[pid].add(fieldID);
	}

	public void move(int pid, int fromFieldID, int toFieldID) {
		//pieces[pid].remove(fromFieldID);
		//pieces[pid].add(toFieldID);
		pieces[pid].set(fromFieldID, toFieldID);
	}	

	public PiecesList getPieces(int pid) {
		return pieces[pid];
	}
}

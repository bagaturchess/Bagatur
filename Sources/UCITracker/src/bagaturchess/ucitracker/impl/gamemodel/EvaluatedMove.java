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
package bagaturchess.ucitracker.impl.gamemodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.BoardUtils;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;


public class EvaluatedMove implements Comparable<EvaluatedMove>, Serializable{
	
	private static final long serialVersionUID = -3973149070109725527L;

	public static final int DUMMY_EVAL = -123456;
	
	int eval_ofOriginatePlayer;
	int[] moves;
	IGameStatus status;
	
	public IGameStatus getStatus() {
		return status;
	}

	public int eval_ofOriginatePlayer() {
		return eval_ofOriginatePlayer;
	}

	public int[] getMoves() {
		return moves;
	}
	
	public EvaluatedMove(int cur_move, IGameStatus _status) {
		eval_ofOriginatePlayer = DUMMY_EVAL;
		moves = new int[] {cur_move};
		status = _status;
	}
	
	public EvaluatedMove(int _eval, int _status, int[] _moves) {
		eval_ofOriginatePlayer = _eval;
		status = IGameStatus.values()[_status];
		moves = _moves;
	}
	
	
	public EvaluatedMove(IBitBoard bitboard, int originate_move, String infoLine/*, ForcedCombinationFinder fcf*/) {
		
		bitboard.makeMoveForward(originate_move);
		
		if (bitboard.getStatus() != IGameStatus.NONE) {
			throw new IllegalStateException("status=" + bitboard.getStatus());
		}
		
		/**
		 * Extract pricipal variation
		 */
		int pvStart = infoLine.indexOf(" pv ");
		if (pvStart <= 0) {
			throw new IllegalStateException();
		}
		
		String pv = infoLine.substring(pvStart + 4, infoLine.length());
		//System.out.println(infoLine + "	'" + eval + "' '" + pv + "'");
		
		boolean inCheck = bitboard.isInCheck();
		List<Integer> movesList = new ArrayList<Integer>();
		StringTokenizer movesString = new StringTokenizer(pv, " ");
		while (movesString.hasMoreElements()) {
			
			String moveStr = movesString.nextToken();
			
			int move = bitboard.getMoveOps().stringToMove(moveStr);
			bitboard.makeMoveForward(move);
			
			if (bitboard.isInCheck()) {
				inCheck = true;
			}
			movesList.add(move);
			//System.out.println(moveStr + "=" + move);
		}
		
		Integer[] movesInteger = movesList.toArray(new Integer[0]);
		moves = new int[movesInteger.length + 1];
		moves[0] = originate_move;
		
		for (int i=0; i<movesInteger.length; i++) {
			moves[i + 1] = movesInteger[i];
		}
		
		boolean hasForcedWin = false;
		status = bitboard.getStatus();
		if (status != IGameStatus.NONE) {
			eval_ofOriginatePlayer = DUMMY_EVAL;
		} else {
			hasForcedWin = false;//fcf.hasFastWin();
			if (hasForcedWin) {
				int g = 0;
			}
		}
		
		//Revert game
		for (int i=moves.length - 1; i>=0; i--) {
			bitboard.makeMoveBackward(moves[i]);
		}
		
		if (status != IGameStatus.NONE) {
			return;
		} else if (hasForcedWin) {
			eval_ofOriginatePlayer = DUMMY_EVAL;
			status = IGameStatus.UNDEFINED;
			return;
		} else if (inCheck) {
			eval_ofOriginatePlayer = DUMMY_EVAL;
			status = IGameStatus.UNDEFINED;
			return;
		}
		
		//Example for mate: info depth 1 seldepth 7 score mate 1 time 0 nodes 22 pv f6e4
		//int scoreStart = infoLine.indexOf("score cp ");
		int scoreStart = infoLine.indexOf(" score ");
		if (scoreStart <= 0) {
			throw new IllegalStateException();
		}
		int cpOrMateStart = infoLine.indexOf(" ", scoreStart + 6);
		
		if (infoLine.indexOf(" mate ", cpOrMateStart) > 0) {
			status = IGameStatus.UNDEFINED;
			eval_ofOriginatePlayer = DUMMY_EVAL;
		} else if (infoLine.indexOf(" cp ", cpOrMateStart) > 0) {
			int scoreEnd = infoLine.indexOf(" ", cpOrMateStart + 4);
			String number = infoLine.substring(cpOrMateStart + 4, scoreEnd);
			
			//Minus because the originate move is played and then the engine is started.
			//So the evaluation inside the info line is from opponent player perspective (opponent of the root player).
			eval_ofOriginatePlayer = -Integer.parseInt(number);
		} else {
			throw new IllegalStateException(infoLine);
		}
		
		//System.out.println(this);
	}

	private static boolean hasWinCaps(IBitBoard bitboard) {
		
		boolean result = false;
		
		IMoveList moves = new BaseMoveList(150);
		
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(moves);
		} else {
			bitboard.genAllMoves(moves);
		}
		
		int cur_move = 0;
		while ((cur_move = moves.next()) != 0) {
			if (bitboard.getSee().evalExchange(cur_move) > 0) {
				return true;
			}
		}
		
		return result;
	}
	
	/*private int uciStrToMove1(IBitBoard bitboard, String moveStr) {
		
		int fromFieldID = Fields.getFieldID(moveStr.substring(0, 2));
		int toFieldID = Fields.getFieldID(moveStr.substring(2, 4));
		int pieceID = bitboard.getFigureID(fromFieldID);
		int toPieceID = bitboard.getFigureID(toFieldID);
		
		if (toPieceID != Constants.PID_NONE) {
			moveStr = moveStr.substring(0, 2) + "x" + moveStr.substring(2, moveStr.length());
		}
		
		//long enpassantTargetPawn = 0L;
		//if (bitboard.toEPD().getEnpassantTargetSquare() != null) {
		//	enpassantTargetPawn = Enpassanting.getEnemyBitboard(fen.getEnpassantTargetSquare());
		//}
		
		if (pieceID == Constants.PID_W_PAWN && (Fields.ALL_A1H1[toFieldID] & Promotioning.WHITE_PROMOTIONS) != 0L) {
			if (moveStr.endsWith("q")) {
				moveStr = moveStr.substring(0, moveStr.length() - 1) + "=Q";
			} else if (moveStr.endsWith("r")) {
				moveStr = moveStr.substring(0, moveStr.length() - 1) + "=R";
			} else if (moveStr.endsWith("b")) {
				moveStr = moveStr.substring(0, moveStr.length() - 1) + "=B";
			} else if (moveStr.endsWith("n")) {
				moveStr = moveStr.substring(0, moveStr.length() - 1) + "=N";
			} else {
				throw new IllegalStateException(moveStr);
			}
		} else if (pieceID == Constants.PID_B_PAWN && (Fields.ALL_A1H1[toFieldID] & Promotioning.BLACK_PROMOTIONS) != 0L) {
			if (moveStr.endsWith("q")) {
				moveStr = moveStr.substring(0, moveStr.length() - 1) + "=Q";
			} else if (moveStr.endsWith("r")) {
				moveStr = moveStr.substring(0, moveStr.length() - 1) + "=R";
			} else if (moveStr.endsWith("b")) {
				moveStr = moveStr.substring(0, moveStr.length() - 1) + "=B";
			} else if (moveStr.endsWith("n")) {
				moveStr = moveStr.substring(0, moveStr.length() - 1) + "=N";
			} else {
				throw new IllegalStateException(moveStr);
			}
		}
		
		String pieceIDStr = Constants.getPieceIDString(pieceID).toUpperCase();
		if (!pieceIDStr.equals("P")) {
			moveStr = pieceIDStr + moveStr;
		}

		int move = 0;
		try {
			move = PGNUtils.translatePGNMove(bitboard, bitboard.getColourToMove(), moveStr, true);
		} catch(IllegalArgumentException iae) {
			//Enpassant fix
			if (pieceID == Constants.PID_W_PAWN || pieceID == Constants.PID_B_PAWN) {
				moveStr = moveStr.substring(0, 2) + "x" + moveStr.substring(2, moveStr.length());
				move = PGNUtils.translatePGNMove(bitboard, bitboard.getColourToMove(), moveStr, true);
			} else {
				throw iae;
			}
		}
		
		return move;
	}*/
	
	
	@Override
	public String toString() {
		return "Eval: " + eval_ofOriginatePlayer + " status: " + status + " " + MoveInt.movesToString(moves);
	}
	
	
	/**
	 * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     * 
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this Object.
	 */
	public int compareTo(EvaluatedMove other) {
		if (other.status != IGameStatus.NONE && status != IGameStatus.NONE) {
			return -1;
		}
		
		if (other.status != IGameStatus.NONE && status == IGameStatus.NONE) {
			return -1;
		}
		
		if (other.status == IGameStatus.NONE && status != IGameStatus.NONE) {
			return 1;
		}
		
		if (eval_ofOriginatePlayer - other.eval_ofOriginatePlayer == 0) {
			return -1;
		}
		
		return other.eval_ofOriginatePlayer - eval_ofOriginatePlayer;
	}
}

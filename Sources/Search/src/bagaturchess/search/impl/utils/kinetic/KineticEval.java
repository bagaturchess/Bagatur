package bagaturchess.search.impl.utils.kinetic;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.utils.SearchUtils;


public class KineticEval {
	
	
	private static boolean filter_moves = true;
	private static int ACCEPTABLE_DIFF;
	private static int MAX_DEPTH = 10;
	
	
	private IBitBoard board;
	private IKineticEval eval;
	
	private IMoveList[] movelists;
	
	
	public KineticEval(IBitBoard _board, IKineticEval _eval) {
		
		board = _board;
		eval = _eval;
		
		movelists = new IMoveList[MAX_DEPTH + 1];
		for (int i=0; i<movelists.length; i++) {
			movelists[i] = new BaseMoveList();
		}
		
		//ACCEPTABLE_DIFF = -35;
		//ACCEPTABLE_DIFF = 0;
		ACCEPTABLE_DIFF = 35;
	}
	
	
	private IMoveList getMoveList(int depth) {
		IMoveList list = movelists[depth];
		list.clear();
		return list;
	}
	
	
	protected boolean isDraw() {
		return board.getStateRepetition() >= 2
				|| board.isDraw50movesRule()
				|| !board.hasSufficientMaterial();
	}
	
	
	private int getDrawScores() {
		return 0;
	}
	
	
	private int handleMateScore(int score) {
		if (SearchUtils.isMateVal(score)) {
			if (score > 0) {
				score = 2 * eval.getMaterialQueen();
			} else {
				score = - 2 * eval.getMaterialQueen();
			}
		}
		return score;
	}
	
	
	private int getDepthByPieceType(int pieceType) {
		switch(pieceType) {
			case Constants.TYPE_PAWN:
				return 5;
			case Constants.TYPE_KING:
				return 4;
			case Constants.TYPE_KNIGHT:
				return 4;
			case Constants.TYPE_BISHOP:
				return 3;
			case Constants.TYPE_ROOK:
				return 3;
			case Constants.TYPE_QUEEN:
				return 2;
			default:
				throw new IllegalStateException();
		}
	}
	
	
	private KineticData_Position iterateAllByColour_InDraw() {
		KineticData_Position positiondata = new KineticData_Position();
		for (int i=0; i<positiondata.counts.length; i++) {
			positiondata.evals[i] = getDrawScores();
			positiondata.counts[i] = 1;
		}
		return positiondata;
	}
	

	public KineticData_Position iterateAllByColour_InCheck(ISearchMediator mediator, ISearchInfo info, int root_distance, int maxdepth) {
		
		boolean inCheck = board.isInCheck();
		if (isDraw() || (inCheck && !board.hasMoveInNonCheck())) {
			return iterateAllByColour_InDraw();
		}
		
		KineticData_Position positiondata = new KineticData_Position();
		
		int root_eval = eval.evalPosition(mediator, info, root_distance, maxdepth);
		positiondata.setEvals0(handleMateScore(root_eval));
		positiondata.setRootEval(root_eval);
		positiondata.fillVector();
		
		return positiondata;
	}
	
	
	public KineticData_Position iterateAllByColour(ISearchMediator mediator, ISearchInfo info, int root_distance, int maxdepth) {
		
		
		KineticData_Position positiondata = new KineticData_Position();
		
		
		boolean inCheck = board.isInCheck();
		
		if (inCheck) {
			throw new IllegalStateException("In check inside iterateAllByColour");
		}
		
		if (isDraw() || (inCheck && !board.hasMoveInNonCheck())) {
			return iterateAllByColour_InDraw();
		}
		
		int root_eval = eval.evalPosition(mediator, info, root_distance, maxdepth);

		positiondata.setEvals0(handleMateScore(root_eval));
		positiondata.setRootEval(root_eval);

		if (SearchUtils.isMateVal(root_eval)) {
			//Do nothing
		} else {
			
			for (int fieldID = 0; fieldID < 64; fieldID++) {
				
				int pid = board.getFigureID(fieldID);
				
				if (pid != Constants.PID_NONE && Constants.getColourByPieceIdentity(pid) == board.getColourToMove()) {
					
					int pieceType = Constants.PIECE_IDENTITY_2_TYPE[pid];
					
					positiondata.addRootEval(eval.notMovedPenalty(fieldID));
					
					int depthByPiece = getDepthByPieceType(pieceType);
					
					KineticData_Piece piecedata = positiondata.createNewPieceData(depthByPiece, fieldID, Fields.ALL_A1H1[fieldID], pieceType);
					
					for (int depth = 1; depth <= depthByPiece; depth++) {
						iterateSinglePiece(fieldID, piecedata.getLevel(depth), 1, depth, mediator, info, root_distance);
					}
				}
			}
		}
		
		positiondata.fillVector();
		
		return positiondata;
	}
	
	
	private void iterateSinglePiece(int fieldID, KineticData_PieceLevel data, int depth, int maxdepth, ISearchMediator mediator, ISearchInfo info, int root_distance) {
		
		if (board.isInCheck()) {
			throw new IllegalStateException();
		}
		
		IMoveList list = getMoveList(depth);
		board.genAllMoves_ByFigureID(fieldID, data.getAll(depth), list);
		
		if (depth >= maxdepth) {	
			
			int cur_move = 0;
			while ((cur_move = list.next()) != 0) {
					
					
					data.add(depth, MoveInt.getToFieldBitboard(cur_move));
					
					
					int eval_before = eval.evalBeforeMove(mediator, info, root_distance, maxdepth, cur_move);
					
					board.makeMoveForward(cur_move);

					int[] result = evalAfterMove(mediator, info, root_distance, maxdepth, eval_before, cur_move);
					int eval_diff = result[0];
					int eval_after = result[1];
					
					board.makeMoveBackward(cur_move);
					
					
					if (!filter_moves || eval_diff >= - ACCEPTABLE_DIFF) {
						eval_after = handleMateScore(result[1]);
						
						data.addEval(depth, eval_after);
						
						//System.out.println("D:" + depth + " FID:" + fieldID + " -> " + MoveInt.moveToString(cur_move) + " eval_delta=" + qsearch_eval_diff + ", qsearch_eval_after=" + qsearch_eval_after);
					}
				}
			
		} else {
			
			int cur_move = 0;
			while ((cur_move = list.next()) != 0) {

				
				data.add(depth, MoveInt.getToFieldBitboard(cur_move));
				
				
				int eval_before = eval.evalBeforeMove(mediator, info, root_distance, maxdepth, cur_move);
				
				board.makeMoveForward(cur_move);
				
				int[] result = evalAfterMove(mediator, info, root_distance, maxdepth, eval_before, cur_move);
				int eval_diff = result[0];
				//int eval_after = result[1] + eval_move;
				
				
				if (!filter_moves || eval_diff >= - ACCEPTABLE_DIFF) {
					
					//eval_after = handleMateScore(eval_after);
					
					boolean opInCheck = board.isInCheck();
					boolean draw = isDraw() || (opInCheck && !board.hasMoveInNonCheck());
					
					if (draw || opInCheck || MoveInt.isPromotion(cur_move)) {
						
						//Already traversed on prev iteration
						
						//data.addEval(depth, qsearch_eval_after);
						//System.out.println("CHECK D:" + depth + " FID:" + fieldID + " -> " + MoveInt.moveToString(cur_move) + " eval_delta=" + qsearch_eval_diff);
					} else {
						board.makeNullMoveForward();
						iterateSinglePiece(MoveInt.getToFieldID(cur_move), data, depth + 1, maxdepth, mediator, info, root_distance + 2);
						board.makeNullMoveBackward();
					}
				}
				
				board.makeMoveBackward(cur_move);
			}
		}
	}
	
	
	protected int[] evalAfterMove(ISearchMediator mediator, ISearchInfo info, int root_distance, int maxdepth, int eval_before, int move) {
		
		int eval_after;
		if (isDraw() || (board.isInCheck() && !board.hasMoveInNonCheck())) {
			eval_after = getDrawScores();
		} else {
			if (filter_moves) {
				eval_after = -eval.evalAfterMove(mediator, info, root_distance + 1, maxdepth, move);					
			} else {
				eval_after = eval_before;
			}
		}
		
		int eval_diff = eval_after -  eval_before;
		
		return new int[] {eval_diff, eval_after};
	}
}

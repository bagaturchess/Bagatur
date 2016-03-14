package bagaturchess.search.impl.utils.kinetic;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;


public class StaticKineticEval implements IKineticEval {
	
	
	private IBitBoard board;
	private IEvaluator eval;
	
	
	public StaticKineticEval(IBitBoard _board, IEvaluator _eval) {
		board = _board;
		eval = _eval;
	}
	
	
	@Override
	public int getMaterialQueen() {
		return eval.getMaterialQueen();
	}
		

	@Override
	public int evalBeforeMove(ISearchMediator mediator, ISearchInfo info,
			int depth, int maxdepth, int move) {
		
		int scores = evalPosition(mediator, info, depth, maxdepth);
		
		return scores;
	}


	@Override
	public int notMovedPenalty(int fieldID) {
		return board.getSee().seeField(fieldID);
	}
	
	
	@Override
	public int evalAfterMove(ISearchMediator mediator, ISearchInfo info,
			int depth, int maxdepth, int move) {
		
		int scores = evalPosition(mediator, info, depth, maxdepth);
		
		if (MoveInt.isCapture(move)) {
			//TODO: Get material from Evaluator
			scores -= eval.getMaterial(MoveInt.getCapturedFigureType(move));
		}

		scores += board.getSee().evalExchange(move);
		scores -= board.getSee().seeField(MoveInt.getFromFieldID(move)) / 2;
		
		return scores;
	}
	
	
	@Override
	public int evalPosition(ISearchMediator mediator, ISearchInfo info, int depth, int maxdepth) {
		return (int) eval.fullEval(depth, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, board.getColourToMove());
	}
	
	
	private int doit(ISearchMediator mediator, ISearchInfo info, int depth, int maxdepth, int move) {
		
		int scoresBefore = evalPosition(mediator, info, depth, maxdepth);
		int see = board.getSee().evalExchange(move);
		int penalty = notMovedPenalty(MoveInt.getFromFieldID(move));
		int pieceScores = board.getBaseEvaluation().getMaterialGain(move);
		
		scoresBefore = scoresBefore + penalty + see;
		
		board.makeMoveForward(move);

		int scoresAfter = evalPosition(mediator, info, depth, maxdepth);
		
		int eval_diff = 0;
		int eval_after = 0;
		
		board.makeMoveBackward(move);
		
		return 0;
	}
}

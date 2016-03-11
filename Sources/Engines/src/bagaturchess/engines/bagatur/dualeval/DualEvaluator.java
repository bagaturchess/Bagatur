package bagaturchess.engines.bagatur.dualeval;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.EvaluatorAdapter;


public class DualEvaluator extends EvaluatorAdapter {
	
	
	private static final int ENDGAME_FACTOR = 20; 
	
	
	private IBoard board;
	private IEvaluator eval_o;
	private IEvaluator eval_e;
	
	
	public DualEvaluator(IBoard _board, IEvaluator _eval_o, IEvaluator _eval_e) {
		board = _board;
		eval_o = _eval_o;
		eval_e = _eval_e;
	}
	
	
	private IEvaluator getEval() {
		int factor = board.getMaterialFactor().getTotalFactor();
		if (factor >= ENDGAME_FACTOR) {
			return eval_o;
		} else {
			return eval_e;
		}
	}
	
	
	public int getMaterialQueen() {
		return getEval().getMaterialQueen();
	}
	
	
	public void beforeSearch() {
		getEval().beforeSearch();
	}
	
	
	public int eval(int depth, int alpha, int beta, boolean pvNode, int rootColour) {
		return getEval().eval(depth, alpha, beta, pvNode, rootColour);
	}
	
	
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		return getEval().fullEval(depth, alpha, beta, rootColour);
	}
	
	
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		return getEval().lazyEval(depth, alpha, beta, rootColour);
	}
	
	
	public int roughEval(int depth, int rootColour) {
		return getEval().roughEval(depth, rootColour);
	}
}

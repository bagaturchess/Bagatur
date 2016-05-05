package bagaturchess.engines.bagatur.v14;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.internal.EvaluatorAdapter;
import bagaturchess.search.impl.evalcache.IEvalCache;
import bagaturchess.search.impl.evalcache.IEvalEntry;


public class BagaturEvaluator extends EvaluatorAdapter {
	
	
	private IBitBoard bitboard;	
	
	private IPosition pos;
	private Evaluate evaluator;
	private IEvalCache evalCache;
	
	
	public BagaturEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		//super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		pos = new PositionImpl(bitboard);
		evaluator = new Evaluate();
		
		evalCache = _evalCache;
	}
	
	
	public int getMaterialQueen() {
		return 1244;
	}
	
	
	public void beforeSearch() {
		//throw new UnsupportedOperationException("Not implemented");
	}
	
	
	public int eval(int depth, int alpha, int beta, boolean pvNode, int rootColour) {
		return evaluator.evalPos(pos);
	}
	
	
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		long hashkey = bitboard.getHashKey();
		
		evalCache.lock();
		IEvalEntry cached = evalCache.get(hashkey);
		
		if (cached != null) {
			int eval = (int) cached.getEval();
			evalCache.unlock();
			return eval;
		}
		evalCache.unlock();

		int eval = evaluator.evalPos(pos);
		
		evalCache.lock();
		evalCache.put(hashkey, (int) eval, false);
		//evalCache.put(hashkey, 1, (int) eval, alpha, beta);
		evalCache.unlock();
		
		return eval;
	}
	
	
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		long hashkey = bitboard.getHashKey();
		
		evalCache.lock();
		IEvalEntry cached = evalCache.get(hashkey);
		
		if (cached != null) {
			int eval = (int) cached.getEval();
			evalCache.unlock();
			return eval;
		}
		evalCache.unlock();

		int eval = evaluator.evalPos(pos);
		
		evalCache.lock();
		evalCache.put(hashkey, (int) eval, false);
		//evalCache.put(hashkey, 1, (int) eval, alpha, beta);
		evalCache.unlock();
		
		return eval;
	}
	
	
	public int roughEval(int depth, int rootColour) {
		long hashkey = bitboard.getHashKey();
		
		evalCache.lock();
		IEvalEntry cached = evalCache.get(hashkey);
		
		if (cached != null) {
			int eval = (int) cached.getEval();
			evalCache.unlock();
			return eval;
		}
		evalCache.unlock();
		
		return evaluator.evalPos_rough(pos);
	}
}

package bagaturchess.search.impl.eval;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.internal.EvaluatorAdapter;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.impl.evalcache.EvalCache;
import bagaturchess.search.impl.evalcache.EvalEntry;
import bagaturchess.search.impl.evalcache.IEvalCache;
import bagaturchess.search.impl.evalcache.IEvalEntry;


public abstract class BaseEvaluator_cache extends EvaluatorAdapter {
	
	
	protected static final int ENDGAME_FACTOR = 20; 
	
	
	private static final boolean USE_CACHE = true;
	private static final boolean USE_LAZY = true;
	
	//private static final int CACHE_LEVEL_1 = 1;
	//private static final int CACHE_LEVEL_2 = 2;
	//private static final int CACHE_LEVEL_3 = 3;
	//private static final int CACHE_LEVEL_4 = 4;
	//private static final int CACHE_LEVEL_5 = 5;
	//private static final int CACHE_LEVEL_MAX = CACHE_LEVEL_5;
	
	private static double INT_MIN = 25;
	private double[] INTERVALS;
	private double[] INTERVALS_TMP;
	
	
	protected IBitBoard bitboard;	
	
	protected PiecesList w_knights;
	protected PiecesList b_knights;
	protected PiecesList w_bishops;
	protected PiecesList b_bishops;
	protected PiecesList w_rooks;
	protected PiecesList b_rooks;
	protected PiecesList w_queens;
	protected PiecesList b_queens;
	protected PiecesList w_king;
	protected PiecesList b_king;
	protected PiecesList w_pawns;
	protected PiecesList b_pawns;
	
	protected IMaterialFactor interpolator;
	protected IBaseEval baseEval;
	
	private IEvalCache evalCache;
		
	
	public BaseEvaluator_cache(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		bitboard = _bitboard;
		
		w_knights = bitboard.getPiecesLists().getPieces(Constants.PID_W_KNIGHT);
		b_knights = bitboard.getPiecesLists().getPieces(Constants.PID_B_KNIGHT);
		w_bishops = bitboard.getPiecesLists().getPieces(Constants.PID_W_BISHOP);
		b_bishops = bitboard.getPiecesLists().getPieces(Constants.PID_B_BISHOP);
		w_rooks = bitboard.getPiecesLists().getPieces(Constants.PID_W_ROOK);
		b_rooks = bitboard.getPiecesLists().getPieces(Constants.PID_B_ROOK);
		w_queens = bitboard.getPiecesLists().getPieces(Constants.PID_W_QUEEN);
		b_queens = bitboard.getPiecesLists().getPieces(Constants.PID_B_QUEEN);
		w_king = bitboard.getPiecesLists().getPieces(Constants.PID_W_KING);
		b_king = bitboard.getPiecesLists().getPieces(Constants.PID_B_KING);
		w_pawns = bitboard.getPiecesLists().getPieces(Constants.PID_W_PAWN);
		b_pawns = bitboard.getPiecesLists().getPieces(Constants.PID_B_PAWN);
		
		interpolator = _bitboard.getMaterialFactor();
		baseEval = _bitboard.getBaseEvaluation();
		
		evalCache = _evalCache;
		
		int size = 5;//getMaxLevel() + 1;
		INTERVALS = new double[size];
		INTERVALS_TMP = new double[size];
		
		beforeSearch();
	}
	
	
	public void beforeSearch() {
		for (int i=0; i< INTERVALS.length; i++) {
			INTERVALS[i] = Math.max(INT_MIN, INTERVALS[i] / 2);
		}
	}
	
	
	public int getMaterialQueen() {
		return 50 + baseEval.getMaterialQueen();
	}
	
	
	protected int getMinLevel() {
		return 0;
	}
	
	protected int getMaxLevel() {
		
		int factor = bitboard.getMaterialFactor().getTotalFactor();
		
		if (factor >= ENDGAME_FACTOR) {
			return 4;
		} else {
			return 3;
		}
	}
	
	//Return evaluation from white player's perspective
	protected int evalLevel(int level) {
		
		int factor = bitboard.getMaterialFactor().getTotalFactor();
		
		switch(level) {
			
			case 0:
				throw new IllegalStateException();
				
			case 1:
				return (int) phase1();
				
			case 2:
				if (factor >= ENDGAME_FACTOR) {
					return (int) phase2_opening();
				} else {
					return (int) phase2_endgame();
				}
				
			case 3:
				if (factor >= ENDGAME_FACTOR) {
					return (int) phase3_opening();
				} else {
					return (int) phase3_endgame();
				}
				
			case 4:
				if (factor >= ENDGAME_FACTOR) {
					return (int) phase4_opening();
				} else {
					throw new IllegalStateException();
				}
				
			case 5:
				if (factor >= ENDGAME_FACTOR) {
					return (int) phase5_opening();
				} else {
					throw new IllegalStateException();
				}
				
			default:
				throw new IllegalStateException();
		}
	}
	
	
	protected double phase1() {
		return eval_material_nopawnsdrawrule() + interpolator.interpolateByFactor(baseEval.getPST_o(), baseEval.getPST_e());
	}
	
	protected abstract double phase2_opening();
	protected abstract double phase2_endgame();
	
	protected abstract double phase3_opening();
	protected abstract double phase3_endgame();
	
	protected abstract double phase4_opening();
	
	protected abstract double phase5_opening();
	
	
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		
		
		long hashkey = bitboard.getHashKey();
		
		int cache_level = getMinLevel();
		double eval = 0;
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			
			IEvalEntry cached = evalCache.get(hashkey);
			
			if (cached != null) {
				
				cache_level = cached.getLevel();
				eval = (int) cached.getEval();
				
				//TODO: Consider
				if (cache_level == getMaxLevel()) {
					evalCache.unlock();
					return (int) returnVal(eval);
				}
			}
			
			evalCache.unlock();
		}
		
		
		
		for (int cur_level = cache_level + 1; cur_level<=getMaxLevel(); cur_level++) {
			int eval_level = evalLevel(cur_level);
			
			INTERVALS_TMP[cur_level] = eval_level;
			
			eval += eval_level;
		}
		
		
		int eval_delta = 0;
		for (int i=getMaxLevel(); i > 0 ; i--) {
			eval_delta += INTERVALS_TMP[i];
			if (Math.abs(eval_delta) > INTERVALS[i]) {
				INTERVALS[i] = Math.abs(eval_delta);
			}
		}
		
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			evalCache.put(hashkey, getMaxLevel(), (int) eval, alpha, beta);
			evalCache.unlock();
		}
		
		return returnVal(eval);
	}
	
	
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		
		
		long hashkey = bitboard.getHashKey();
		
		int cache_level = getMinLevel();
		int eval = 0;
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			
			IEvalEntry cached = evalCache.get(hashkey);
			
			if (cached != null) {
				
				cache_level = cached.getLevel();
				eval = cached.getEval();
				
				if (cache_level == getMaxLevel()) {
					evalCache.unlock();
					return (int) returnVal(eval);
				}
			}
			
			evalCache.unlock();
		}
		
		
		for (int cur_level = cache_level + 1; cur_level<=getMaxLevel(); cur_level++) {
			
			//Try cache eval
			int eval_test = (int) returnVal(eval);
			if (eval_test <= alpha - INTERVALS[cur_level] || eval_test >= beta + INTERVALS[cur_level]) {
				if (cur_level != cache_level + 1) {
					evalCache.lock();
					evalCache.put(hashkey, cur_level - 1, (int) eval, alpha, beta);
					evalCache.unlock();
				}
				return eval_test;
			}
			
			//Evaluate next level
			int eval_level = evalLevel(cur_level);
			INTERVALS_TMP[cur_level] = eval_level;
			
			//Add eval
			eval += eval_level;
		}
		
		
		
		//TODO: Calculate only if last cur_level is equal to max level?
		int eval_delta = 0;
		for (int i=getMaxLevel(); i > 0 ; i--) {
			eval_delta += INTERVALS_TMP[i];
			if (Math.abs(eval_delta) > INTERVALS[i]) {
				INTERVALS[i] = Math.abs(eval_delta);
			}
		}
		
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			evalCache.put(hashkey, getMaxLevel(), (int) eval, alpha, beta);
			evalCache.unlock();
		}
		
		
		return (int) returnVal(eval);
	}
	
	
	public int roughEval(int depth, int rootColour) {
		
		long hashkey = bitboard.getHashKey();
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			IEvalEntry cached = evalCache.get(hashkey);
			
			if (cached != null) {
				int eval = cached.getEval();
				evalCache.unlock();
				return (int) returnVal(eval);
			}
			evalCache.unlock();
		}
		
		double eval = phase1();
		
		//TODO: Cache???
		
		return (int) returnVal(eval);
	}
	
	private double returnVal(double eval) {
		
		double result = eval;
		
		result = drawProbability(result);
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			result = -result;
		}
		return result;
	}
	
	
	private double drawProbability(double eval) {
		
		double abs = Math.abs(eval);
		
		/**
		 * Differently coloured bishops
		 */
		if (w_bishops.getDataSize() == 1 && b_bishops.getDataSize() == 1
				&& bitboard.getMaterialFactor().getTotalFactor() == 6 //Only the bishops
			) {
			
			long w_colour = (bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER) & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			long b_colour = (bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER) & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			
			if (w_colour != b_colour) {
				if (eval >= 0) {
					abs -= Math.min(90, abs / 2);	
				} else {
					abs += Math.min(90, abs / 2);
				}
			}
		}
		
		/**
		 * 50 moves rule
		 */
		int movesBeforeDraw = 100 - bitboard.getDraw50movesRule();
		double percents = movesBeforeDraw / (double)100;
		abs = ((abs + percents * abs) / (double)2);
		
		/**
		 * Return value
		 */
		return eval >= 0 ? abs : -abs;
	}
	
	
	public int eval_material_nopawnsdrawrule() {
		
		int w_eval_nopawns_o = baseEval.getWhiteMaterialNonPawns_o();
		int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
		int b_eval_nopawns_o = baseEval.getBlackMaterialNonPawns_o();
		int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
		
		int w_eval_pawns_o = baseEval.getWhiteMaterialPawns_o();
		int w_eval_pawns_e = baseEval.getWhiteMaterialPawns_e();
		int b_eval_pawns_o = baseEval.getBlackMaterialPawns_o();
		int b_eval_pawns_e = baseEval.getBlackMaterialPawns_e();
		
		
		if (w_pawns.getDataSize() == 0) {
			if (w_eval_pawns_o != 0 || w_eval_pawns_e != 0) {
				throw new IllegalStateException();
			}
			
			if (w_eval_nopawns_o > b_eval_nopawns_o) {
				if (w_eval_nopawns_o < b_eval_nopawns_o + baseEval.getMaterial_BARIER_NOPAWNS_O()) {
					w_eval_nopawns_o = b_eval_nopawns_o;
				}
			}
			
			if (w_eval_nopawns_e > b_eval_nopawns_e) {
				if (w_eval_nopawns_e < b_eval_nopawns_e + baseEval.getMaterial_BARIER_NOPAWNS_E()) {
					w_eval_nopawns_e = b_eval_nopawns_e;
				}
			}
		}
		
		if (b_pawns.getDataSize() == 0) {
			if (b_eval_pawns_o != 0 || b_eval_pawns_e != 0) {
				throw new IllegalStateException();
			}
			
			if (b_eval_nopawns_o > w_eval_nopawns_o) {
				if (b_eval_nopawns_o < w_eval_nopawns_o + baseEval.getMaterial_BARIER_NOPAWNS_O()) {
					b_eval_nopawns_o = w_eval_nopawns_o;
				}
			}
			
			if (b_eval_nopawns_e > w_eval_nopawns_e) {
				if (b_eval_nopawns_e < w_eval_nopawns_e + baseEval.getMaterial_BARIER_NOPAWNS_E()) {
					b_eval_nopawns_e = w_eval_nopawns_e;
				}
			}
		}
		
		return interpolator.interpolateByFactor(
					(w_eval_nopawns_o - b_eval_nopawns_o) + (w_eval_pawns_o - b_eval_pawns_o),
					(w_eval_nopawns_e - b_eval_nopawns_e) + (w_eval_pawns_e - b_eval_pawns_e)
				);

	}
	
	protected static final int axisSymmetry(int fieldID) {
		return HORIZONTAL_SYMMETRY[fieldID];
	}
}

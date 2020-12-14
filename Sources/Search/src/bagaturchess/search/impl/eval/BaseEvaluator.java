package bagaturchess.search.impl.eval;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.search.api.FullEvalFlag;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.impl.eval.cache.EvalEntry_BaseImpl;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import bagaturchess.search.impl.eval.cache.IEvalEntry;


public abstract class BaseEvaluator implements IEvaluator {
	
	
	private static final boolean USE_CACHE = true;
	private static final boolean USE_LAZY = true;
	
	private static final int CACHE_LEVEL_1 = 1;
	private static final int CACHE_LEVEL_2 = 2;
	private static final int CACHE_LEVEL_3 = 3;
	private static final int CACHE_LEVEL_4 = 4;
	private static final int CACHE_LEVEL_5 = 5;
	private static final int CACHE_LEVEL_MAX = CACHE_LEVEL_5;
	
	private static double INT_MIN = 1;
	private static double INT1 = INT_MIN;
	private static double INT2 = INT_MIN;
	private static double INT3 = INT_MIN;
	private static double INT4 = INT_MIN;
	
	
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
	private IEvalEntry cached = new EvalEntry_BaseImpl();
	
	
	public BaseEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
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
	}
	
	
	protected abstract double phase1();
	
	protected abstract double phase2();
	
	protected abstract double phase3();
	
	protected abstract double phase4();
	
	protected abstract double phase5();
	
	
	public void beforeSearch() {
		INT1 = Math.max(INT_MIN, INT1 / 2);
		INT2 = Math.max(INT_MIN, INT2 / 2);
		INT3 = Math.max(INT_MIN, INT3 / 2);
		INT4 = Math.max(INT_MIN, INT4 / 2);
	}
	
	
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		return fullEval(depth, alpha, beta, rootColour, true);
	}
	
	
	protected double fullEval(int depth, int alpha, int beta, int rootColour, boolean useCache) {
		
		if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			int material_imbalance = w_eval_nopawns_e - b_eval_nopawns_e;
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[b_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return (int) returnVal(material_imbalance + 3 * (int) (4.7 * CMD + 1.6 * MD));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[w_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return (int) returnVal(material_imbalance - 3 * (int) (4.7 * CMD + 1.6 * MD));
				
			}
		}
		
		
		long hashkey = bitboard.getHashKey();
		
		if (USE_CACHE && evalCache != null && useCache) {
			
			evalCache.get(hashkey, cached);
			
			if (!cached.isEmpty()) {
				int level = cached.getLevel();
				switch (level) {
					case CACHE_LEVEL_5:
						int eval = (int) cached.getEval();
						return (int) returnVal(eval);
					default:
						//Do Nothing
				}
			}
		}
		
		
		double eval = 0;
		
		eval += phase1();
		eval += phase2();
		eval += phase3();
		eval += phase4();
		eval += phase5();
		
		if (USE_CACHE && evalCache != null && useCache) {
			evalCache.put(hashkey, CACHE_LEVEL_MAX, (int) eval);
		}
		
		return returnVal(eval);
	}
	
	
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		return lazyEval(depth, alpha, beta, rootColour, null);
	}
	
	
	public int lazyEval(int depth, int alpha, int beta, int rootColour, FullEvalFlag flag) {
		
		if (flag != null) flag.value = false;
		
		long hashkey = bitboard.getHashKey();
		
		if (USE_CACHE && evalCache != null) {
			
			evalCache.get(hashkey, cached);
			
			if (!cached.isEmpty()) {
				int level = cached.getLevel();
				switch (level) {
					case CACHE_LEVEL_5:
						int eval = (int) cached.getEval();
						if (flag != null) flag.value = true;
						return (int) returnVal(eval);
					case CACHE_LEVEL_4:
						int lower = cached.getEval();
						int upper = cached.getEval();
						int alpha_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha;
						int beta_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta;
						if (upper + INT4 <= alpha_test) {
							return (int) returnVal(upper);
						}
						if (lower - INT4 >= beta_test) {
							return (int) returnVal(lower);
						}
						break;
					case CACHE_LEVEL_3:
						lower = cached.getEval();
						upper = cached.getEval();
						alpha_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha;
						beta_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta;
						if (upper + INT3 <= alpha_test) {
							return (int) returnVal(upper);
						}
						if (lower - INT3 >= beta_test) {
							return (int) returnVal(lower);
						}
						break;
					case CACHE_LEVEL_2:
						lower = cached.getEval();
						upper = cached.getEval();
						alpha_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha;
						beta_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta;
						if (upper + INT2 <= alpha_test) {
							return (int) returnVal(upper);
						}
						if (lower - INT2 >= beta_test) {
							return (int) returnVal(lower);
						}
						break;
					case CACHE_LEVEL_1:
						lower = cached.getEval();
						upper = cached.getEval();
						alpha_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha;
						beta_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta;
						if (upper + INT1 <= alpha_test) {
							return (int) returnVal(upper);
						}
						if (lower - INT1 >= beta_test) {
							return (int) returnVal(lower);
						}
						break;
					default:
						//Do Nothing
						throw new IllegalStateException();
				}
			}
		}
		
		
		if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			int material_imbalance = w_eval_nopawns_e - b_eval_nopawns_e;
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[b_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return (int) returnVal(material_imbalance + 3 * (int) (4.7 * CMD + 1.6 * MD));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[w_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return (int) returnVal(material_imbalance - 3 * (int) (4.7 * CMD + 1.6 * MD));
				
			}
		}
		
		
		double eval = 0;
		
		
		double eval1 = phase1();
		eval += eval1;
		int eval_test = (int) returnVal(eval);
		if (eval_test + INT1 <= alpha || eval_test - INT1 >= beta) {
			if (USE_LAZY) {
				if (USE_CACHE && evalCache != null) {
					evalCache.put(hashkey, CACHE_LEVEL_1, (int) eval);
				}
				return eval_test;
			}
		}
		
			
		double eval2 = phase2();
		eval += eval2;
		eval_test = (int) returnVal(eval);
		if (eval_test + INT2 <= alpha || eval_test - INT2 >= beta) {
			if (USE_LAZY) {
				if (USE_CACHE && evalCache != null) {
					evalCache.put(hashkey, CACHE_LEVEL_2, (int) eval);
				}
				return eval_test;
			}
		}
		
		double eval3 = phase3();
		eval += eval3;
		eval_test = (int) returnVal(eval);
		if (eval_test + INT3 <= alpha || eval_test - INT3 >= beta) {
			if (USE_LAZY) {
				if (USE_CACHE && evalCache != null) {
					evalCache.put(hashkey, CACHE_LEVEL_3, (int) eval);
				}
				return eval_test;
			}
		}
		
		double eval4 = phase4();
		eval += eval4;
		eval_test = (int) returnVal(eval);
		if (eval_test + INT4 <= alpha || eval_test - INT4 >= beta) {
			if (USE_LAZY) {
				if (USE_CACHE && evalCache != null) {
					evalCache.put(hashkey, CACHE_LEVEL_4, (int) eval);
				}
				return eval_test;
			}
		}
		
		double eval5 = phase5();
		eval += eval5;
		
		
		int int1 = (int) Math.abs(eval2 + eval3 + eval4 + eval5);
		int int2 = (int) Math.abs(eval3 + eval4 + eval5);
		int int3 = (int) Math.abs(eval4 + eval5);
		int int4 = (int) Math.abs(eval5);
		
		if (int1 > INT1) {
			INT1 = int1;
		}
		if (int2 > INT2) {
			INT2 = int2;
		}
		if (int3 > INT3) {
			INT3 = int3;
		}
		if (int4 > INT4) {
			INT4 = int4;
		}
			
		
		if (eval >= ISearch.MAX_MAT_INTERVAL || eval <= -ISearch.MAX_MAT_INTERVAL) {
			throw new IllegalStateException();
		}
		
		if (USE_CACHE && evalCache != null) {
			evalCache.put(hashkey, CACHE_LEVEL_MAX, (int) eval);
		}
		
		if (flag != null) flag.value = true;
		
		return (int) returnVal(eval);
	}
	
	
	public int roughEval(int depth, int rootColour) {
		
		long hashkey = bitboard.getHashKey();
		
		if (USE_CACHE && evalCache != null) {
			
			evalCache.get(hashkey, cached);
			
			if (!cached.isEmpty()) {
				int level = cached.getLevel();
				switch (level) {
					case CACHE_LEVEL_5:
						int eval = (int) cached.getEval();
						return (int) returnVal(eval);
					default:
						//Do Nothing
				}
			}
		}
		
		
		if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			int material_imbalance = w_eval_nopawns_e - b_eval_nopawns_e;
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[b_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return (int) returnVal(material_imbalance + 3 * (int) (4.7 * CMD + 1.6 * MD));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[w_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return (int) returnVal(material_imbalance - 3 * (int) (4.7 * CMD + 1.6 * MD));
				
			}
		}
		
		
		double eval = phase1();
		
		return (int) returnVal(eval);
	}
	
	protected double returnVal(double eval) {
		
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
		 * Differently colored bishops, no other pieces except pawns
		 */
		if (w_bishops.getDataSize() == 1
				&& b_bishops.getDataSize() == 1
				&& bitboard.getMaterialFactor().getWhiteFactor() == 3
				&& bitboard.getMaterialFactor().getBlackFactor() == 3) {
			
			long w_colour = (bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER) & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			long b_colour = (bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER) & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			if (w_colour != b_colour) {
				
				//If one of the sides has advantage of 2-3 pawns, than let it know the game goes to draw
				if (abs <= 200) {
					abs = abs / 4;
				} else if (abs <= 400) {
					abs = abs / 2;
				} else if (abs <= 600) {
					abs = (2 * abs) / 3;
				}
			}
		}
		
		/**
		 * 50 moves rule
		 */
		int movesBeforeDraw = 100 - bitboard.getDraw50movesRule();
		double percents = movesBeforeDraw / (double)100;
		abs = (int) (percents * abs);//(int) ((abs + percents * abs) / (double)2);
		
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
			
			if (w_eval_nopawns_o < baseEval.getMaterial_BARIER_NOPAWNS_O()) {
				w_eval_nopawns_o = w_eval_nopawns_o / 2;
			}
			
			if (w_eval_nopawns_e < baseEval.getMaterial_BARIER_NOPAWNS_E()) {
				w_eval_nopawns_e = w_eval_nopawns_e / 2;
			}
		}
		
		if (b_pawns.getDataSize() == 0) {
			
			if (b_eval_pawns_o != 0 || b_eval_pawns_e != 0) {
				throw new IllegalStateException();
			}
			
			if (b_eval_nopawns_o < baseEval.getMaterial_BARIER_NOPAWNS_O()) {
				b_eval_nopawns_o = b_eval_nopawns_o / 2;
			}
			
			if (b_eval_nopawns_e < baseEval.getMaterial_BARIER_NOPAWNS_E()) {
				b_eval_nopawns_e = b_eval_nopawns_e / 2;
			}
		}
		
		return interpolator.interpolateByFactor(
				(w_eval_nopawns_o - b_eval_nopawns_o) + (w_eval_pawns_o - b_eval_pawns_o),
				(w_eval_nopawns_e - b_eval_nopawns_e) + (w_eval_pawns_e - b_eval_pawns_e));

	}

	
	protected static final int axisSymmetry(int fieldID) {
		return Fields.HORIZONTAL_SYMMETRY[fieldID];
	}
}

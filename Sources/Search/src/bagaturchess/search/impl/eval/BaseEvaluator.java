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
import bagaturchess.search.impl.evalcache.IEvalCache;
import bagaturchess.search.impl.evalcache.IEvalEntry;


public abstract class BaseEvaluator extends EvaluatorAdapter {
	
	
	private static final int ENDGAME_FACTOR = 30; 
	
	
	private static final boolean USE_CACHE = true;
	private static final boolean USE_LAZY = true;
	
	private static final int CACHE_LEVEL_1 = 1;
	private static final int CACHE_LEVEL_2 = 2;
	private static final int CACHE_LEVEL_3 = 3;
	private static final int CACHE_LEVEL_4 = 4;
	private static final int CACHE_LEVEL_5 = 5;
	private static final int CACHE_LEVEL_MAX = CACHE_LEVEL_5;
	
	private static double INT_MIN = 25;
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
	
	
	protected double phase1() {
		return eval_material_nopawnsdrawrule() + interpolator.interpolateByFactor(baseEval.getPST_o(), baseEval.getPST_e());
	}
	
	protected abstract double phase2_opening();
	protected abstract double phase2_endgame();
	
	protected abstract double phase3_opening();
	protected abstract double phase3_endgame();
	
	protected abstract double phase4_opening();
	
	protected abstract double phase5_opening();
	
	
	public void beforeSearch() {
		INT1 = Math.max(INT_MIN, INT1 / 2);
		INT2 = Math.max(INT_MIN, INT2 / 2);
		INT3 = Math.max(INT_MIN, INT3 / 2);
		INT4 = Math.max(INT_MIN, INT4 / 2);
	}
	
	
	@Override
	public int getMaterialQueen() {
		return 50 + baseEval.getMaterialQueen();
	}
	
	
	@Override
	public int getMaterial(int pieceType) {
		return baseEval.getMaterial(pieceType);
	}
	
	
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		
		
		long hashkey = bitboard.getHashKey();
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			IEvalEntry cached = evalCache.get(hashkey);
			
			if (cached != null) {
				int level = cached.getLevel();
				switch (level) {
					case CACHE_LEVEL_5:
						int eval = (int) cached.getEval();
						evalCache.unlock();
						return (int) returnVal(eval);
					case CACHE_LEVEL_4:
						break;
					default:
						//Do Nothing
				}
			}
			evalCache.unlock();
		}
		
		
		double eval = 0;
		
		eval += phase1();
		
		int factor = bitboard.getMaterialFactor().getTotalFactor();
		if (factor >= ENDGAME_FACTOR) {
			eval += phase2_opening();
			eval += phase3_opening();
			eval += phase4_opening();
			eval += phase5_opening();
		} else {
			eval += phase2_endgame();
			eval += phase3_endgame();
		}
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			//evalCache.put(hashkey, (int) eval, false);
			evalCache.put(hashkey, CACHE_LEVEL_MAX, (int) eval, alpha, beta);
			evalCache.unlock();
		}
		
		return returnVal(eval);
	}
	
	
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		
		
		long hashkey = bitboard.getHashKey();
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			IEvalEntry cached = evalCache.get(hashkey);
			
			if (cached != null) {
				int level = cached.getLevel();
				switch (level) {
					case CACHE_LEVEL_5:
						int eval = (int) cached.getEval();
						evalCache.unlock();
						return (int) returnVal(eval);
					case CACHE_LEVEL_4:
						int lower = cached.getEval();
						int upper = cached.getEval();
						int alpha_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha;
						int beta_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta;
						if (upper + INT4 <= alpha_test) {
							evalCache.unlock();
							return (int) returnVal(upper);
						}
						if (lower - INT4 >= beta_test) {
							evalCache.unlock();
							return (int) returnVal(lower);
						}
						break;
					case CACHE_LEVEL_3:
						lower = cached.getEval();
						upper = cached.getEval();
						alpha_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha;
						beta_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta;
						if (upper + INT3 <= alpha_test) {
							evalCache.unlock();
							return (int) returnVal(upper);
						}
						if (lower - INT3 >= beta_test) {
							evalCache.unlock();
							return (int) returnVal(lower);
						}
						break;
					case CACHE_LEVEL_2:
						lower = cached.getEval();
						upper = cached.getEval();
						alpha_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha;
						beta_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta;
						if (upper + INT2 <= alpha_test) {
							evalCache.unlock();
							return (int) returnVal(upper);
						}
						if (lower - INT2 >= beta_test) {
							evalCache.unlock();
							return (int) returnVal(lower);
						}
						break;
					case CACHE_LEVEL_1:
						lower = cached.getEval();
						upper = cached.getEval();
						alpha_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha;
						beta_test = (bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta;
						if (upper + INT1 <= alpha_test) {
							evalCache.unlock();
							return (int) returnVal(upper);
						}
						if (lower - INT1 >= beta_test) {
							evalCache.unlock();
							return (int) returnVal(lower);
						}
						break;
					default:
						//Do Nothing
						throw new IllegalStateException();
				}
			}
				
			evalCache.unlock();
		}
		
		
		double eval = 0;
		
		
		double eval1 = phase1();
		eval += eval1;
		int eval_test = (int) returnVal(eval);
		if (eval_test + INT1 <= alpha || eval_test - INT1 >= beta) {
			if (USE_LAZY) {
				if (USE_CACHE && evalCache != null) {
					evalCache.lock();
					evalCache.put(hashkey, CACHE_LEVEL_1, (int) eval,
				
														(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha,
														(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta
													);
					evalCache.unlock();
				}
				return eval_test;
			}
		}
		
		int factor = bitboard.getMaterialFactor().getTotalFactor();
		if (factor >= ENDGAME_FACTOR) {
			
			double eval2 = phase2_opening();
			eval += eval2;
			eval_test = (int) returnVal(eval);
			if (eval_test + INT2 <= alpha || eval_test - INT2 >= beta) {
				if (USE_LAZY) {
					if (USE_CACHE && evalCache != null) {
						evalCache.lock();
						evalCache.put(hashkey, CACHE_LEVEL_2, (int) eval,
									(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha,
									(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta
								);
						evalCache.unlock();
					}
					return eval_test;
				}
			}
			
			double eval3 = phase3_opening();
			eval += eval3;
			eval_test = (int) returnVal(eval);
			if (eval_test + INT3 <= alpha || eval_test - INT3 >= beta) {
				if (USE_LAZY) {
					if (USE_CACHE && evalCache != null) {
						evalCache.lock();
						evalCache.put(hashkey, CACHE_LEVEL_3, (int) eval,
									(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha,
									(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta
							);
						evalCache.unlock();
					}
					return eval_test;
				}
			}
			
			double eval4 = phase4_opening();
			eval += eval4;
			eval_test = (int) returnVal(eval);
			if (eval_test + INT4 <= alpha || eval_test - INT4 >= beta) {
				if (USE_LAZY) {
					if (USE_CACHE && evalCache != null) {
						evalCache.lock();
						evalCache.put(hashkey, CACHE_LEVEL_4, (int) eval,
									(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha,
									(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta
							);
						evalCache.unlock();
					}
					return eval_test;
				}
			}
			
			double eval5 = phase5_opening();
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
		} else {
			
			double eval2 = phase2_endgame();
			eval += eval2;
			eval_test = (int) returnVal(eval);
			if (eval_test + INT2 <= alpha || eval_test - INT2 >= beta) {
				if (USE_LAZY) {
					if (USE_CACHE && evalCache != null) {
						/*evalCache.lock();
						evalCache.put(hashkey, CACHE_LEVEL_1, (int) eval,
									(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -beta : alpha,
									(bitboard.getColourToMove() == Figures.COLOUR_BLACK) ? -alpha : beta
							);
						evalCache.unlock();*/
					}
					return eval_test;
				}
			}
			
			double eval3 = phase3_endgame();
			eval += eval3;
			/*if (eval + INT3 <= alpha || eval - INT3 >= beta) {
				if (USE_LAZY) {
					evalCache.put(hashkey, CACHE_LEVEL_2, (int) eval, alpha, beta);
					return (int) returnVal(eval);
				}
			}*/
			
			
			int int1 = (int) Math.abs(eval2 + eval3/* + eval4 + eval5*/);
			int int2 = (int) Math.abs(eval3/* + eval4 + eval5*/);
			//int int3 = Math.abs(eval4 + eval5);
			//int int4 = Math.abs(eval5);
			
			if (int1 > INT1) {
				INT1 = int1;
			}
			if (int2 > INT2) {
				INT2 = int2;
			}
			/*if (int3 > INT3) {
				INT3 = int3;
			}
			if (int4 > INT4) {
				INT4 = int4;
			}*/
		}
		
		if (eval >= ISearch.MAX_MAT_INTERVAL || eval <= -ISearch.MAX_MAT_INTERVAL) {
			throw new IllegalStateException();
		}
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			//evalCache.put(hashkey, (int) eval, false);
			evalCache.put(hashkey, CACHE_LEVEL_MAX, (int) eval, alpha, beta);
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
				int level = cached.getLevel();
				switch (level) {
					case CACHE_LEVEL_5:
						int eval = (int) cached.getEval();
						evalCache.unlock();
						return (int) returnVal(eval);
					default:
						//Do Nothing
				}
			}
			evalCache.unlock();
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

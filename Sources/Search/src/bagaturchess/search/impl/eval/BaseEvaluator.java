package bagaturchess.search.impl.eval;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	
	
	private static final int MAX_MATERIAL_FACTOR = 9 + 2 * 5 + 2 * 3 + 2 * 3;
	
	private static double[] material_exchange_motivation = new double[32]; //Between 0 and 1 (0 = all pieces, 1 = no pieces)
	
	private static Map<Integer, Set<Integer>> states_transitions = new HashMap<Integer, Set<Integer>>();
	
	
	static {
		
		generateAllPossibleMaterialFactorStates(1, 2, 2, 2, MAX_MATERIAL_FACTOR, 0);
		
		material_exchange_motivation[0] = 1;
		material_exchange_motivation[1] = 0.5;
		material_exchange_motivation[2] = 0.5;
		material_exchange_motivation[4] = 0.5;
		material_exchange_motivation[7] = 0.5;
		material_exchange_motivation[24] = 0.03125;
		material_exchange_motivation[27] = 0.015625;
		material_exchange_motivation[29] = 0.0078125;
		material_exchange_motivation[30] = 0.0078125;
		
		int state_counter = 0;
		
		for (int i = 0; i < material_exchange_motivation.length; i++) {
			
			if (material_exchange_motivation[i] != 0) {
				
				state_counter++;
				
				if (states_transitions.get(i) != null) {
					
					System.out.println("material_exchange_motivation[" + i + "]=" + material_exchange_motivation[i]
							+ " state_counter=" + state_counter
							+ " states_transitions count " + states_transitions.get(i).size()
							+ " states_transitions=" + states_transitions.get(i));
					
				} else {
					
					System.out.println("material_exchange_motivation[" + i + "]=" + material_exchange_motivation[i]
							+ " state_counter=" + state_counter);
				}
			}
		}
	}
	
	
	protected IBitBoard bitboard;	
	
	protected IMaterialFactor interpolator;
	protected IBaseEval baseEval;
	
	private IEvalCache evalCache;
	private IEvalEntry cached = new EvalEntry_BaseImpl();
	
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
	
	
	public BaseEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		bitboard = _bitboard;
		
		interpolator = _bitboard.getMaterialFactor();
		baseEval = _bitboard.getBaseEvaluation();
		
		evalCache = _evalCache;
		
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
		
		int count_pawns_w = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN));
		int count_pawns_b = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN));
		
		/*if (count_pawns_w == 0 && count_pawns_b == 0) {
			
			int king_sq_w = 63 - Long.numberOfLeadingZeros(Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KING)));
			int king_sq_b = 63 - Long.numberOfLeadingZeros(Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KING)));
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			int material_imbalance = w_eval_nopawns_e - b_eval_nopawns_e;
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[king_sq_b];
				int MD = Fields.getTropismPoint(king_sq_w, king_sq_b);
				
				return (int) returnVal(material_imbalance + 3 * (int) (4.7 * CMD + 1.6 * MD));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[king_sq_w];
				int MD = Fields.getTropismPoint(king_sq_w, king_sq_b);
				
				return (int) returnVal(material_imbalance - 3 * (int) (4.7 * CMD + 1.6 * MD));
				
			}
		}*/
		
		
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
		//eval += eval_material_nopawnsdrawrule();
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
		
		
		/*if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
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
		}*/
		
		
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
		
		
		/*if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
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
		}*/
		
		
		double eval = phase1();
		
		return (int) returnVal(eval);
	}
	
	
	protected double returnVal(double white_eval) {
		
		
		white_eval = applyExchangeMotivation(white_eval);
		
		
		double result = white_eval;
		
		result = drawProbability(result);
		
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			
			result = -result;
		}
		
		
		return result;
	}
	
	
	private double applyExchangeMotivation(double white_eval) {
		
		int material_factor_white = Math.min(MAX_MATERIAL_FACTOR, bitboard.getMaterialFactor().getWhiteFactor());
		
		int material_factor_black = Math.min(MAX_MATERIAL_FACTOR, bitboard.getMaterialFactor().getBlackFactor());
		
		if (material_factor_white >= material_exchange_motivation.length || material_factor_black >= material_exchange_motivation.length) {
			
			throw new IllegalStateException("material_factor_white=" + material_factor_black + " material_factor_black=" + material_factor_black);
		}
		
		
		//EXCHANGE_MOTIVATION_WEIGHT must be between 0 and 1
		double EXCHANGE_MOTIVATION_WEIGHT = 0.75;
		
		if (material_factor_white == material_factor_black) {
			
			double white_material_scale = material_exchange_motivation[material_factor_white];
			
			if (white_material_scale == 0) {
				
				throw new IllegalStateException("factor=" + white_material_scale + " material_factor_white=" + material_factor_white);
			}
			
			//Increase/Decrease WHITE evaluation
			white_eval *= (1 + EXCHANGE_MOTIVATION_WEIGHT * white_material_scale);
			
		} else if (material_factor_white > material_factor_black) {
		
			//Here the goal of the WHITE player is to exchange pieces and reach white_material_scale = 1. This happens when material_factor_black = 0.
			double white_material_scale = material_exchange_motivation[material_factor_white]; //Between 0 and 1 (0 = all pieces, 1 = no pieces)
			
			if (white_eval > 0) {
				
				//Increase WHITE evaluation - make it bigger positive number (closer to +Infinity) by multiplying with number >= 1
				white_eval *= (1 + EXCHANGE_MOTIVATION_WEIGHT * white_material_scale);
				
			} else {
				
				//Increase WHITE evaluation - make it bigger negative number (closer to 0) by multiplying with number between 0.5 and 1
				white_eval *= Math.max(0.5, 1 - EXCHANGE_MOTIVATION_WEIGHT * white_material_scale);
			}
			
		} else if (material_factor_white < material_factor_black) {
			
			//Here the goal of the BLACK player is to exchange pieces and reach black_material_scale = 1. This happens when material_factor_white = 0.
			double black_material_scale = material_exchange_motivation[material_factor_black]; //Between 0 and 1 (0 = all pieces, 1 = no pieces)
			
			if (white_eval > 0) {
				
				//Decrease WHITE evaluation
				white_eval *= Math.max(0.5, 1 - EXCHANGE_MOTIVATION_WEIGHT * black_material_scale);
				
			} else {
				
				//Decrease WHITE evaluation
				white_eval *= (1 + EXCHANGE_MOTIVATION_WEIGHT * black_material_scale);
			}
			
		} else {
			
			throw new IllegalStateException("material_factor_white=" + material_factor_white + " material_factor_black=" + material_factor_black);
		}
		
		return white_eval;
	}
	
	
	private double drawProbability(double eval) {
		
		
		double abs = Math.abs(eval);
		
		
		/**
		 * Differently colored bishops, no other pieces except pawns
		 */
		int count_bishops_w = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER));
		int count_bishops_b = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER));
		
		if (count_bishops_w == 1
					&& count_bishops_b == 1
					&& bitboard.getMaterialFactor().getWhiteFactor() == 3
					&& bitboard.getMaterialFactor().getBlackFactor() == 3
				) {
			
			long w_colour = (bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER) & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			
			long b_colour = (bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER) & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			
			if (w_colour != b_colour) {
				
				//If one of the sides has advantage, than let it know the probability of draw increases
				abs = abs / 2;
			}
		}
		
		
		/**
		 * 50 moves rule - evaluation goes down / up to 0, move after move.
		 */
		int movesBeforeDraw = 100 - bitboard.getDraw50movesRule();
		double percents = movesBeforeDraw / (double) 100;
		abs = (int) (percents * abs);
		
		
		return eval >= 0 ? abs : -abs;
	}
	
	
	private static void generateAllPossibleMaterialFactorStates(int q, int r, int b, int n, int factor_parent_state, int captures_count) {
		
		int factor_current_state = q * 9 + r * 5 + b * 3 + n * 3;
		//int factor_current_state = q * 43 + r * 23 + b * 13 + n * 11;
		//int factor_current_state = prime[q] * prime[r] * prime[b] * prime[n];
		//int factor_current_state = n + (b << 4) + (r << 8) + (q << 12);
		
		//System.out.println("factor_current_state=" + factor_current_state);
		
		//System.out.println("exchanges_count=" + exchanges_count + " factor_current_state=" + factor_current_state + " q=" + q + " r=" + r + " b=" + b +" n=" + n);
		
		
		material_exchange_motivation[factor_current_state] = Math.pow(2, captures_count) / 128;
		
		
		Set<Integer> parents = states_transitions.get(factor_current_state);
		
		if (parents == null) {
			
			parents = new HashSet<Integer>();
			
			states_transitions.put(factor_current_state, parents);
		}
		
		parents.add((int) factor_parent_state);
		
		
		if (q >= 1) generateAllPossibleMaterialFactorStates(q - 1, r, b, n, factor_current_state, captures_count + 1);
		
		if (r >= 1) generateAllPossibleMaterialFactorStates(q, r - 1, b, n, factor_current_state, captures_count + 1);
		
		if (b >= 1) generateAllPossibleMaterialFactorStates(q, r, b - 1, n, factor_current_state, captures_count + 1);
		
		if (n >= 1) generateAllPossibleMaterialFactorStates(q, r, b, n - 1, factor_current_state, captures_count + 1);
		
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
		
		int count_pawns_w = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN));
		int count_pawns_b = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN));
		
		if (count_pawns_w == 0) {
			
			if (w_eval_pawns_o != 0 || w_eval_pawns_e != 0) {
				throw new IllegalStateException("w_eval_pawns_o=" + w_eval_pawns_o + ", w_eval_pawns_e=" + w_eval_pawns_e);
			}
			
			if (w_eval_nopawns_o < baseEval.getMaterial_BARIER_NOPAWNS_O()) {
				w_eval_nopawns_o = w_eval_nopawns_o / 2;
			}
			
			if (w_eval_nopawns_e < baseEval.getMaterial_BARIER_NOPAWNS_E()) {
				w_eval_nopawns_e = w_eval_nopawns_e / 2;
			}
		}
		
		if (count_pawns_b == 0) {
			
			if (b_eval_pawns_o != 0 || b_eval_pawns_e != 0) {
				throw new IllegalStateException("b_eval_pawns_o=" + b_eval_pawns_o + ", b_eval_pawns_e=" + b_eval_pawns_e);
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

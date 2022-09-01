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
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.search.api.FullEvalFlag;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.impl.eval.cache.EvalEntry_BaseImpl;
import bagaturchess.search.impl.eval.cache.IEvalCache;
import bagaturchess.search.impl.eval.cache.IEvalEntry;
import bagaturchess.uci.api.ChannelManager;


public abstract class BaseEvaluator implements IEvaluator {
	
	
	private static final boolean USE_CACHE = true;
	
	
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
					
					/*System.out.println("material_exchange_motivation[" + i + "]=" + material_exchange_motivation[i]
							+ " state_counter=" + state_counter
							+ " states_transitions count " + states_transitions.get(i).size()
							+ " states_transitions=" + states_transitions.get(i));*/
					
				} else {
					
					/*System.out.println("material_exchange_motivation[" + i + "]=" + material_exchange_motivation[i]
							+ " state_counter=" + state_counter);*/
				}
			}
		}
	}
	
	
	private static final double[] MATERIAL_CORRECTION_BY_PAWNS		= {0.90, 0.95, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00};
	
	private static final int DOUBLE_BISHOP				= 48;
	
	private static final int ROOKS_PAIR					= 25;
	
	private static final int KNIGHTS_AND_QUEEN			= 25;
	
	
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
	
	protected IEvalConfig evalConfig;
	
	
	public BaseEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		bitboard = _bitboard;
		
		interpolator = _bitboard.getMaterialFactor();
		
		baseEval = _bitboard.getBaseEvaluation();
		
		evalCache = _evalCache;
		
		evalConfig = _evalConfig;
		
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
		
		if (ChannelManager.getChannel() != null) {
			
			ChannelManager.getChannel().dump("BaseEvaluator.constructor: evalConfig=" + evalConfig + ", evalCache=" + evalCache);
		}
	}
	
	
	protected void phase0_init() {
		
		//Do nothing
	}
	
	protected abstract int phase1();
	
	protected abstract int phase2();
	
	protected abstract int phase3();
	
	protected abstract int phase4();
	
	protected abstract int phase5();
	
	
	public void beforeSearch() {
		//Do nothing
	}
	
	
	public int fullEval(int depth, int alpha, int beta, int rootColour) {
		
		return fullEval(depth, alpha, beta, rootColour, true);
	}
	
	
	protected int fullEval(int depth, int alpha, int beta, int rootColour, boolean useCache) {
		
		if (evalConfig != null && !evalConfig.isTrainingMode()) {
			
			int count_pawns_w = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN));
			int count_pawns_b = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN));
			
			if (count_pawns_w == 0 && count_pawns_b == 0) {
				
				int king_sq_w = 63 - Long.numberOfLeadingZeros(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KING));
				int king_sq_b = 63 - Long.numberOfLeadingZeros(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KING));
				
				int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
				int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
				
				//Mop-up evaluation
				//PosEval=4.7*CMD + 1.6*(14 - MD)
				//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
				if (w_eval_nopawns_e >= b_eval_nopawns_e) { //White can win
					
					int CMD = Fields.CENTER_MANHATTAN_DISTANCE[king_sq_b];
					
					int file_w = king_sq_w & 7; //[0-7]
					int rank_w = king_sq_w >>> 3; //[0-7]
					int file_b = king_sq_b & 7; //[0-7]
					int rank_b = king_sq_b >>> 3; //[0-7]
					
					int delta_file = Math.abs(file_w - file_b);
					int delta_rank = Math.abs(rank_w - rank_b);
					int delta_sum  = delta_file + delta_rank;
					
					if (14 - delta_sum < 0) {
						throw new IllegalStateException("delta_sum=" + delta_sum);
					}
					
					int MD = 14 - delta_sum;
					
					int material_imbalance = w_eval_nopawns_e - b_eval_nopawns_e;
					
					
					
					if (canWin(Constants.COLOUR_WHITE)) {
						
						return (int) returnVal(material_imbalance + 3 * (int) (4.7 * CMD + 1.6 * MD));
						
					} else {
						
						return 0;
					}
					
				} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
					
					int CMD = Fields.CENTER_MANHATTAN_DISTANCE[king_sq_w];
					
					int file_w = king_sq_w & 7; //[0-7]
					int rank_w = king_sq_w >>> 3; //[0-7]
					int file_b = king_sq_b & 7; //[0-7]
					int rank_b = king_sq_b >>> 3; //[0-7]
					
					int delta_file = Math.abs(file_w - file_b);
					int delta_rank = Math.abs(rank_w - rank_b);
					int delta_sum  = delta_file + delta_rank;
					
					if (14 - delta_sum < 0) {
						throw new IllegalStateException("delta_sum=" + delta_sum);
					}
					
					int MD = 14 - delta_sum;
					
					int material_imbalance = w_eval_nopawns_e - b_eval_nopawns_e;
					
					if (canWin(Constants.COLOUR_BLACK)) {
						
						return (int) returnVal(material_imbalance - 3 * (int) (4.7 * CMD + 1.6 * MD));
						
					} else {
						
						return 0;
					}				
					
				} else {
					
					throw new IllegalStateException();
				}
			}
		}
		
		
		long hashkey = bitboard.getHashKey();
		
		if (USE_CACHE && evalCache != null && useCache && evalConfig.useEvalCache()) {
			
			evalCache.get(hashkey, cached);
			
			if (!cached.isEmpty()) {
				
				int eval = (int) cached.getEval();
				
				return (int) returnVal(eval);
			}
		}
		
		
		phase0_init();
		
		int white_eval = 0;
		
		if (useDefaultMaterial()) {
			
			if (evalConfig != null && !evalConfig.isTrainingMode()) {
				
				white_eval += eval_material_nopawnsdrawrule();
			}
			
			//eval += eval_material_imbalances();
		}
		
		white_eval += phase1();
		white_eval += phase2();
		white_eval += phase3();
		white_eval += phase4();
		white_eval += phase5();	
		
		if (evalConfig != null && !evalConfig.isTrainingMode()) {
		
			white_eval = applyExchangeMotivation(white_eval);
			
			//white_eval = applyMaterialCorrectionByPawnsCount(white_eval);
			
			if (white_eval > 0 && !canWin(Constants.COLOUR_WHITE)) {
				
				return 0;
				
			} else if (white_eval < 0 && !canWin(Constants.COLOUR_BLACK)) {
				
				return 0;
			}
		}
		
		
		if (USE_CACHE && evalCache != null && useCache && evalConfig.useEvalCache()) {
			
			evalCache.put(hashkey, 5, (int) white_eval);
		}
		
		
		return returnVal(white_eval);
	}
	
	
	protected boolean useDefaultMaterial() {
		
		return true;
	}
	
	
	protected int returnVal(int white_eval) {
		
		if (evalConfig != null && !evalConfig.isTrainingMode()) {
			
			white_eval = drawProbability(white_eval);
		}
		
		return setSign(white_eval);
	}


	private int setSign(int white_eval) {
		
		if (bitboard.getColourToMove() == Constants.COLOUR_WHITE) {
			
			return white_eval;
			
		} else {
			
			int black_eval = -white_eval;
			
			return black_eval;
		}
	}
	
	
	private int drawProbability(int eval) {
		
		
		int abs = Math.abs(eval);
		
		
		/**
		 * Differently colored bishops, no other pieces except pawns
		 */
		int count_bishops_w = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP));
		
		int count_bishops_b = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP));
		
		if (count_bishops_w == 1
					&& count_bishops_b == 1
					&& bitboard.getMaterialFactor().getWhiteFactor() == 3
					&& bitboard.getMaterialFactor().getBlackFactor() == 3
				) {
			
			long w_colour = (bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP) & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			
			long b_colour = (bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP) & Fields.ALL_WHITE_FIELDS) != 0 ?
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
		
		//int percents = movesBeforeDraw / (double) 100;
		
		abs = (int) (movesBeforeDraw * abs) / 100;
		
		
		return eval >= 0 ? abs : -abs;
	}
	
	
	private int applyExchangeMotivation(int white_eval) {
		
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
	
	
	private boolean canWin(int color) {
		
		if (color == Constants.COLOUR_WHITE) {
			
			long pawns_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN);
			
			if (pawns_w != 0) {
				
				return true;
			}
			
			long queens_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_QUEEN);
			
			if (queens_w != 0) {
				
				return true;
			}
			
			long rooks_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_ROOK);
			
			if (rooks_w != 0) {
				
				return true;
			}

			long knights_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT);
			long bishops_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP);
			
			if (knights_w != 0 && bishops_w != 0) {
				
				return true;
			}
			
			if (Long.bitCount(knights_w) >= 3) {
				
				return true;
			}
			
			if (Long.bitCount(bishops_w) >= 2) {
				
				return true;
			}
			
			
			return false;
			
		} else {
			
			long pawns_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN);
			
			if (pawns_b != 0) {
				
				return true;
			}
			
			long queens_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_QUEEN);
			
			if (queens_b != 0) {
				
				return true;
			}
			
			long rooks_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_ROOK);
			
			if (rooks_b != 0) {
				
				return true;
			}

			long knights_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT);
			long bishops_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP);
			
			if (knights_b != 0 && bishops_b != 0) {
				
				return true;
			}
			
			if (Long.bitCount(knights_b) >= 3) {
				
				return true;
			}
			
			if (Long.bitCount(bishops_b) >= 2) {
				
				return true;
			}
			
			
			return false;
		}
	}
	
	
	private double applyMaterialCorrectionByPawnsCount(double white_eval) {
		
		int count_pawns_w = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN));
		int count_pawns_b = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN));
		
		if (white_eval > 0) {
			
			white_eval *= MATERIAL_CORRECTION_BY_PAWNS[count_pawns_w];
			
		} else if (white_eval < 0) {
			
			white_eval *= MATERIAL_CORRECTION_BY_PAWNS[count_pawns_b];
		}

		
		return white_eval;
	}
	
	
	public int eval_material_nopawnsdrawrule() {
		
		
		int w_eval_nopawns_o = baseEval.getWhiteMaterialNonPawns_o();
		int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
		int w_eval_pawns_o = baseEval.getWhiteMaterialPawns_o();
		int w_eval_pawns_e = baseEval.getWhiteMaterialPawns_e();
		
		
		int b_eval_nopawns_o = baseEval.getBlackMaterialNonPawns_o();
		int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
		int b_eval_pawns_o = baseEval.getBlackMaterialPawns_o();
		int b_eval_pawns_e = baseEval.getBlackMaterialPawns_e();
		
		
		/*int count_pawns_w = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN));
		int count_pawns_b = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN));
		
		/*if (count_pawns_w == 0) {
			
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
		}*/
		
		return interpolator.interpolateByFactor(
					(w_eval_nopawns_o + w_eval_pawns_o) - (b_eval_nopawns_o + b_eval_pawns_o),
					(w_eval_nopawns_e + w_eval_pawns_e) - (b_eval_nopawns_e + b_eval_pawns_e)
				);

	}
	
	
	private int eval_material_imbalances() {
		
		long pawns_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN);
		long pawns_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN);
		
		long knights_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT);
		long knights_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT);
		
		long bishops_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP);
		long bishops_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP);
		
		long rooks_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_ROOK);
		long rooks_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_ROOK);
		
		long queens_w = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_QUEEN);
		long queens_b = bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_QUEEN);
		
		
		int eval = 0;
		
		
		// knights and pawns
		/*eval += 3 * Long.bitCount(knights_w) * Long.bitCount(pawns_w);
		eval -= 3 * Long.bitCount(knights_b) * Long.bitCount(pawns_b);*/
		
		
		// double bishop
		if (Long.bitCount(bishops_w) >= 2) {
			
			eval += DOUBLE_BISHOP;
		}
		
		if (Long.bitCount(bishops_b) >= 2) {
			
			eval -= DOUBLE_BISHOP;
		}
		
		
		// queen and nights
		/*if (queens_w != 0) {
			
			eval += KNIGHTS_AND_QUEEN * Long.bitCount(knights_w);
		}
		
		if (queens_b != 0) {
			
			eval -= KNIGHTS_AND_QUEEN * Long.bitCount(knights_b);
		}*/
		
		
		// rooks and pawns
		//eval += Long.bitCount(rooks_w) * ((-3) * Long.bitCount(pawns_w));
		//eval -= Long.bitCount(rooks_b) * ((-3) * Long.bitCount(pawns_w));
		
		
		// rook pair
		/*if (Long.bitCount(rooks_w) >= 2) {
			eval += ROOKS_PAIR;
		}
		if (Long.bitCount(rooks_b) >= 2) {
			eval -= ROOKS_PAIR;
		}*/
		
		
		return eval;
	}
	
	
	protected static final int axisSymmetry(int fieldID) {
		return Fields.HORIZONTAL_SYMMETRY[fieldID];
	}
	
	
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		
		return lazyEval(depth, alpha, beta, rootColour, null);
	}
	
	
	public int lazyEval(int depth, int alpha, int beta, int rootColour, FullEvalFlag flag) {
		
		throw new UnsupportedOperationException();
	}
	
	
	public int roughEval(int depth, int rootColour) {
		
		throw new UnsupportedOperationException();
	}
}

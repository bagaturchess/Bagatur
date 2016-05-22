package bagaturchess.engines.bagatur.eval;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.common.CastlingType;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnStructureConstants;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.internal.EvaluatorAdapter;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.impl.evalcache.IEvalCache;
import bagaturchess.search.impl.evalcache.IEvalEntry;


public class BagaturEvaluator extends EvaluatorAdapter implements FeatureWeights {
	
	
	private static final boolean USE_CACHE = true;
	private static final boolean USE_LAZY = true;
	
	
	private static double INT_MIN = 25;
	private static double INT1 = INT_MIN;
	private static double INT2 = INT_MIN;
	private static double INT3 = INT_MIN;
	private static double INT4 = INT_MIN;
	private static double INT_DEVIDE_FACTOR = 1;
	
	
	private IBitBoard bitboard;	
	
	private PiecesList w_knights;
	private PiecesList b_knights;
	private PiecesList w_bishops;
	private PiecesList b_bishops;
	private PiecesList w_rooks;
	private PiecesList b_rooks;
	private PiecesList w_queens;
	private PiecesList b_queens;
	private PiecesList w_king;
	private PiecesList b_king;
	private PiecesList w_pawns;
	private PiecesList b_pawns;
	
	private IMaterialFactor interpolator;
	private IBaseEval baseEval;
	
	private IEvalCache evalCache;
	
	private EvalInfo evalInfo;
	
	private IBagaturEvalConfig evalConfig;
	
	private int MATERIAL_DOUBLE_BISHOP_O = 40;
	private int MATERIAL_DOUBLE_BISHOP_E = 50;
	
	
	static {
		/*-10
		+ Math.max(2 * MATERIAL_ROOK_E, MATERIAL_DOUBLE_ROOK_E)
		- Math.max(MATERIAL_DOUBLE_BISHOP_E,
				Math.max(2 * Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E),
						MATERIAL_DOUBLE_KNIGHT_E
				)
		);*/
		/*System.out.println("" + Math.max(2 * MATERIAL_ROOK_E, MATERIAL_DOUBLE_ROOK_E));
		System.out.println("" + Math.min(2 * Math.max(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E),
				MATERIAL_DOUBLE_KNIGHT_E
		));
		System.out.println("" + Math.min(MATERIAL_DOUBLE_BISHOP_E,
				Math.max(2 * Math.min(MATERIAL_KNIGHT_E, MATERIAL_BISHOP_E),
						MATERIAL_DOUBLE_KNIGHT_E
				)));*/
		
	}
	
	
	BagaturEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
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
		
		evalInfo = new EvalInfo(bitboard);

		
		evalConfig = (IBagaturEvalConfig) _evalConfig;
	}
	
	public String dump(int rootColour) { 
		String msg = "";
		
		int eval = (int) fullEval(0, 0, 0, rootColour);
		msg += evalInfo;
		msg += eval;
		
		return msg;
	}
	
	
	public void beforeSearch() {
		//INT1 = Math.max(INT_MIN, INT1 / 2);
		//INT2 = Math.max(INT_MIN, INT2 / 2);
		//INT3 = Math.max(INT_MIN, INT3 / 2);
		//INT4 = Math.max(INT_MIN, INT4 / 2);
	}
	
	
	public int getMaterialQueen() {
		return 50 + baseEval.getMaterialQueen();
	}
	
	
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		
		long hashkey = bitboard.getHashKey();
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			IEvalEntry cached = evalCache.get(hashkey);
			
			if (cached != null) {
				int eval = (int) cached.getEval();
				evalCache.unlock();
				return returnVal(eval);
			}
			evalCache.unlock();
		}
		
		if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[b_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal(20 * (int) (4.7 * CMD + 1.6 * (14 - MD)));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[w_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal( - 20 * (int) (4.7 * CMD + 1.6 * (14 - MD)));
				
			}
		}
		
		int eval = 0;
		
		evalInfo.clear_short();
		evalInfo.clear();
		
		if (rootColour == Figures.COLOUR_WHITE) {
			if (b_queens.getDataSize() == 0) {
				evalInfo.eval_NoQueen_o += STANDARD_NOQUEEN_O;
				evalInfo.eval_NoQueen_e += STANDARD_NOQUEEN_E;
			}
		} else {
			if (w_queens.getDataSize() == 0) {
				evalInfo.eval_NoQueen_o -= STANDARD_NOQUEEN_O;
				evalInfo.eval_NoQueen_e -= STANDARD_NOQUEEN_E;
			}
		}
		
		
		//if (evalConfig.useRule_NoPawnsDrawByMaterialDiff()) {
			eval_material_nopawnsdrawrule();
		//} else {
		//	eval_material();
		//}
		eval_trading();
		eval_standard();
		eval_pawns();
		evalInfo.eval_Material_o *= WEIGHT_MATERIAL_O;
		evalInfo.eval_Material_e *= WEIGHT_MATERIAL_E;
		evalInfo.eval_Standard_o *= WEIGHT_STANDARD_O;
		evalInfo.eval_Standard_e *= WEIGHT_STANDARD_E;
		evalInfo.eval_PST_o *= evalConfig.get_WEIGHT_PST_O();
		evalInfo.eval_PST_e *= evalConfig.get_WEIGHT_PST_E();
		evalInfo.eval_PawnsStandard_o *= evalConfig.get_WEIGHT_PAWNS_STANDARD_O();
		evalInfo.eval_PawnsStandard_e *= evalConfig.get_WEIGHT_PAWNS_STANDARD_E();
		evalInfo.eval_PawnsPassed_o *= evalConfig.get_WEIGHT_PAWNS_PASSED_O();
		evalInfo.eval_PawnsPassed_e *= evalConfig.get_WEIGHT_PAWNS_PASSED_E();
		evalInfo.eval_PawnsPassedKing_o *= evalConfig.get_WEIGHT_PAWNS_PASSED_KING_O();
		evalInfo.eval_PawnsPassedKing_e *= evalConfig.get_WEIGHT_PAWNS_PASSED_KING_E();
		eval += interpolator.interpolateByFactor(evalInfo.eval_Material_o +
												evalInfo.eval_Standard_o +
												evalInfo.eval_PST_o +
												evalInfo.eval_PawnsStandard_o +
												evalInfo.eval_PawnsPassed_o +
												evalInfo.eval_PawnsPassedKing_o +
												evalInfo.eval_PawnsUnstoppable_o +
												evalInfo.eval_NoQueen_o,
												
												evalInfo.eval_Material_e +
												evalInfo.eval_Standard_e +
												evalInfo.eval_PST_e +
												evalInfo.eval_PawnsStandard_e +
												evalInfo.eval_PawnsPassed_e +
												evalInfo.eval_PawnsPassedKing_e +
												evalInfo.eval_PawnsUnstoppable_e +
												evalInfo.eval_NoQueen_e);
		
		
		initEvalInfo1();
		eval_pawns_RooksAndQueens();
		evalInfo.eval_PawnsPassedStoppers_o *= evalConfig.get_WEIGHT_PAWNS_PSTOPPERS_O();
		evalInfo.eval_PawnsPassedStoppers_e *= evalConfig.get_WEIGHT_PAWNS_PSTOPPERS_E();
		evalInfo.eval_PawnsRooksQueens_o *= WEIGHT_PAWNS_ROOKQUEEN_O;
		evalInfo.eval_PawnsRooksQueens_e *= WEIGHT_PAWNS_ROOKQUEEN_E;
		eval += interpolator.interpolateByFactor(evalInfo.eval_PawnsPassedStoppers_o +
												evalInfo.eval_PawnsRooksQueens_o,
												
												evalInfo.eval_PawnsPassedStoppers_e +
												evalInfo.eval_PawnsRooksQueens_e);

		initEvalInfo2();
		eval_mobility();
		evalInfo.eval_Mobility_o *= evalConfig.get_WEIGHT_MOBILITY_O();
		evalInfo.eval_Mobility_e *= evalConfig.get_WEIGHT_MOBILITY_E();
		eval += interpolator.interpolateByFactor(evalInfo.eval_Mobility_o,
												
												evalInfo.eval_Mobility_e);
		
		//if (interpolator.getTotalFactor() > 16) {
			initEvalInfo3();
			eval_king_safety();
			eval_space();
			eval_hunged();
			eval_TrapsAndSafeMobility();
			eval_PassersFrontAttacks();
			evalInfo.eval_Kingsafety_o *= evalConfig.get_WEIGHT_KINGSAFETY_O();
			evalInfo.eval_Kingsafety_e *= evalConfig.get_WEIGHT_KINGSAFETY_E();
			evalInfo.eval_Space_o *= evalConfig.get_WEIGHT_SPACE_O();
			evalInfo.eval_Space_e *= evalConfig.get_WEIGHT_SPACE_E();
			evalInfo.eval_Hunged_o *= evalConfig.get_WEIGHT_HUNGED_O();
			evalInfo.eval_Hunged_e *= evalConfig.get_WEIGHT_HUNGED_E();
			evalInfo.eval_Trapped_o *= evalConfig.get_WEIGHT_TRAPPED_O();
			evalInfo.eval_Trapped_e *= evalConfig.get_WEIGHT_TRAPPED_E();
			evalInfo.eval_Mobility_Safe_o *= evalConfig.get_WEIGHT_MOBILITY_S_O();
			evalInfo.eval_Mobility_Safe_e *= evalConfig.get_WEIGHT_MOBILITY_S_E();
			evalInfo.eval_PawnsPassedStoppers_a_o *= evalConfig.get_WEIGHT_PAWNS_PSTOPPERS_A_O();
			evalInfo.eval_PawnsPassedStoppers_a_e *= evalConfig.get_WEIGHT_PAWNS_PSTOPPERS_A_E();
			eval += interpolator.interpolateByFactor(evalInfo.eval_Kingsafety_o +
													evalInfo.eval_Space_o +
													evalInfo.eval_Hunged_o +
													evalInfo.eval_Trapped_o +
													evalInfo.eval_Mobility_Safe_o +
													evalInfo.eval_PawnsPassedStoppers_a_o,
					
													evalInfo.eval_Kingsafety_e +
													evalInfo.eval_Space_e +
													evalInfo.eval_Hunged_e +
													evalInfo.eval_Trapped_e +
													evalInfo.eval_Mobility_Safe_e +
													evalInfo.eval_PawnsPassedStoppers_a_e);
		//}
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			evalCache.put(hashkey, eval, false);
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
				int eval = (int) cached.getEval();
				evalCache.unlock();
				return returnVal(eval);
			}
			evalCache.unlock();
		}
		
		
		if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[b_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal(20 * (int) (4.7 * CMD + 1.6 * (14 - MD)));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[w_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal( - 20 * (int) (4.7 * CMD + 1.6 * (14 - MD)));
				
			}
		}
		
		
		int eval = 0;
		
		evalInfo.clear_short();
		
		if (rootColour == Figures.COLOUR_WHITE) {
			if (b_queens.getDataSize() == 0) {
				evalInfo.eval_NoQueen_o += STANDARD_NOQUEEN_O;
				evalInfo.eval_NoQueen_e += STANDARD_NOQUEEN_E;
			}
		} else {
			if (w_queens.getDataSize() == 0) {
				evalInfo.eval_NoQueen_o -= STANDARD_NOQUEEN_O;
				evalInfo.eval_NoQueen_e -= STANDARD_NOQUEEN_E;
			}
		}
		
		
		eval_material_nopawnsdrawrule();
		eval_trading();
		eval_standard();
		evalInfo.eval_Material_o *= WEIGHT_MATERIAL_O;
		evalInfo.eval_Material_e *= WEIGHT_MATERIAL_E;
		evalInfo.eval_Standard_o *= WEIGHT_STANDARD_O;
		evalInfo.eval_Standard_e *= WEIGHT_STANDARD_E;
		evalInfo.eval_PST_o *= evalConfig.get_WEIGHT_PST_O();
		evalInfo.eval_PST_e *= evalConfig.get_WEIGHT_PST_E();
		int eval1 = interpolator.interpolateByFactor(evalInfo.eval_Material_o +
				evalInfo.eval_Standard_o +
				evalInfo.eval_PST_o +
				evalInfo.eval_NoQueen_o,
				
				evalInfo.eval_Material_e +
				evalInfo.eval_Standard_e +
				evalInfo.eval_PST_e +
				evalInfo.eval_NoQueen_e);
		eval += eval1;
		
		//INT1 = INT1_stat.getEntropy() + 2 * INT1_stat.getDisperse();
		
		int eval_tmp = returnVal(eval);
		if (eval_tmp + INT1 <= alpha || eval_tmp - INT1 >= beta) {
			if (USE_LAZY) {
				return eval_tmp;
			}
		}
		
		eval_pawns();
		evalInfo.eval_PawnsStandard_o *= evalConfig.get_WEIGHT_PAWNS_STANDARD_O();
		evalInfo.eval_PawnsStandard_e *= evalConfig.get_WEIGHT_PAWNS_STANDARD_E();
		evalInfo.eval_PawnsPassed_o *= evalConfig.get_WEIGHT_PAWNS_PASSED_O();
		evalInfo.eval_PawnsPassed_e *= evalConfig.get_WEIGHT_PAWNS_PASSED_E();
		evalInfo.eval_PawnsPassedKing_o *= evalConfig.get_WEIGHT_PAWNS_PASSED_KING_O();
		evalInfo.eval_PawnsPassedKing_e *= evalConfig.get_WEIGHT_PAWNS_PASSED_KING_E();
		int eval2 = interpolator.interpolateByFactor(evalInfo.eval_PawnsStandard_o +
												evalInfo.eval_PawnsPassed_o +
												evalInfo.eval_PawnsPassedKing_o +
												evalInfo.eval_PawnsUnstoppable_o,
												
												evalInfo.eval_PawnsStandard_e +
												evalInfo.eval_PawnsPassed_e +
												evalInfo.eval_PawnsPassedKing_e +
												evalInfo.eval_PawnsUnstoppable_e);
		eval += eval2;
		
		//INT2 = INT2_stat.getEntropy() + 2 * INT2_stat.getDisperse();
		
		eval_tmp = returnVal(eval);
		if (eval_tmp + INT2 <= alpha || eval_tmp - INT2 >= beta) {
			if (USE_LAZY) {
				return eval_tmp;
			}
		}
		
		evalInfo.clear();
		
		initEvalInfo1();
		eval_pawns_RooksAndQueens();
		evalInfo.eval_PawnsPassedStoppers_o *= evalConfig.get_WEIGHT_PAWNS_PSTOPPERS_O();
		evalInfo.eval_PawnsPassedStoppers_e *= evalConfig.get_WEIGHT_PAWNS_PSTOPPERS_E();
		evalInfo.eval_PawnsRooksQueens_o *= WEIGHT_PAWNS_ROOKQUEEN_O;
		evalInfo.eval_PawnsRooksQueens_e *= WEIGHT_PAWNS_ROOKQUEEN_E;
		int eval3 = interpolator.interpolateByFactor(evalInfo.eval_PawnsPassedStoppers_o +
												evalInfo.eval_PawnsRooksQueens_o,
												
												evalInfo.eval_PawnsPassedStoppers_e +
												evalInfo.eval_PawnsRooksQueens_e);
		eval += eval3;
		
		//INT3 = INT3_stat.getEntropy() + 2 * INT3_stat.getDisperse();
		
		eval_tmp = returnVal(eval);
		if (eval_tmp + INT3 <= alpha || eval_tmp - INT3 >= beta) {
			if (USE_LAZY) {
				return eval_tmp;
			}
		}
		
		initEvalInfo2();
		eval_mobility();
		evalInfo.eval_Mobility_o *= evalConfig.get_WEIGHT_MOBILITY_O();
		evalInfo.eval_Mobility_e *= evalConfig.get_WEIGHT_MOBILITY_E();
		int eval4 = interpolator.interpolateByFactor(evalInfo.eval_Mobility_o,
												
												evalInfo.eval_Mobility_e);
		eval += eval4;
		
		//INT4 = INT4_stat.getEntropy() + 2 * INT4_stat.getDisperse();
		
		eval_tmp = returnVal(eval);
		if (eval_tmp + INT4 <= alpha || eval_tmp - INT4 >= beta) {
			if (USE_LAZY) {
				return eval_tmp;
			}
		}
		
		int eval5 = 0;
		//if (interpolator.getTotalFactor() > 16) {
			initEvalInfo3();
			eval_king_safety();
			eval_space();
			eval_hunged();
			eval_TrapsAndSafeMobility();
			eval_PassersFrontAttacks();
			evalInfo.eval_Kingsafety_o *= evalConfig.get_WEIGHT_KINGSAFETY_O();
			evalInfo.eval_Kingsafety_e *= evalConfig.get_WEIGHT_KINGSAFETY_E();
			evalInfo.eval_Space_o *= evalConfig.get_WEIGHT_SPACE_O();
			evalInfo.eval_Space_e *= evalConfig.get_WEIGHT_SPACE_E();
			evalInfo.eval_Hunged_o *= evalConfig.get_WEIGHT_HUNGED_O();
			evalInfo.eval_Hunged_e *= evalConfig.get_WEIGHT_HUNGED_E();
			evalInfo.eval_Trapped_o *= evalConfig.get_WEIGHT_TRAPPED_O();
			evalInfo.eval_Trapped_e *= evalConfig.get_WEIGHT_TRAPPED_E();
			evalInfo.eval_Mobility_Safe_o *= evalConfig.get_WEIGHT_MOBILITY_S_O();
			evalInfo.eval_Mobility_Safe_e *= evalConfig.get_WEIGHT_MOBILITY_S_E();
			evalInfo.eval_PawnsPassedStoppers_a_o *= evalConfig.get_WEIGHT_PAWNS_PSTOPPERS_A_O();
			evalInfo.eval_PawnsPassedStoppers_a_e *= evalConfig.get_WEIGHT_PAWNS_PSTOPPERS_A_E();
			eval5 = interpolator.interpolateByFactor(evalInfo.eval_Kingsafety_o +
													evalInfo.eval_Space_o +
													evalInfo.eval_Hunged_o +
													evalInfo.eval_Trapped_o +
													evalInfo.eval_Mobility_Safe_o +
													evalInfo.eval_PawnsPassedStoppers_a_o,
					
													evalInfo.eval_Kingsafety_e +
													evalInfo.eval_Space_e +
													evalInfo.eval_Hunged_e + 
													evalInfo.eval_Trapped_e +
													evalInfo.eval_Mobility_Safe_e +
													evalInfo.eval_PawnsPassedStoppers_a_e);
			eval += eval5;
		//}
		
		if (eval >= ISearch.MAX_MAT_INTERVAL || eval <= -ISearch.MAX_MAT_INTERVAL) {
			throw new IllegalStateException();
		}
		
		double int1 = Math.abs(eval2 + eval3 + eval4 + eval5);
		double int2 = Math.abs(eval3 + eval4 + eval5);
		double int3 = Math.abs(eval4 + eval5);
		double int4 = Math.abs(eval5);
		
		//INT1_stat.addValue(int1, int1);
		//INT2_stat.addValue(int2, int2);
		//INT3_stat.addValue(int3, int3);
		//INT4_stat.addValue(int4, int4);
		
		if (int1 > INT1) {
			INT1 = int1 / INT_DEVIDE_FACTOR;
		}
		if (int2 > INT2) {
			INT2 = int2 / INT_DEVIDE_FACTOR;
		}
		if (int3 > INT3) {
			INT3 = int3 / INT_DEVIDE_FACTOR;
		}
		if (int4 > INT4) {
			INT4 = int4 / INT_DEVIDE_FACTOR;
		}
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			evalCache.put(hashkey, eval, false);
			evalCache.unlock();
		}
		
		return returnVal(eval);
	}
	
	
	public int roughEval(int depth, int rootColour) {
		
		long hashkey = bitboard.getHashKey();
		
		if (USE_CACHE && evalCache != null) {
			evalCache.lock();
			IEvalEntry cached = evalCache.get(hashkey);
			
			if (cached != null) {
				int eval = (int) cached.getEval();
				evalCache.unlock();
				return returnVal(eval);
			}
			evalCache.unlock();
		}
		
		
		if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[b_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal(20 * (int) (4.7 * CMD + 1.6 * (14 - MD)));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[w_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal( - 20 * (int) (4.7 * CMD + 1.6 * (14 - MD)));
				
			}
		}
		
		
		int eval = 0;
		
		evalInfo.clear_short();
		
		if (rootColour == Figures.COLOUR_WHITE) {
			if (b_queens.getDataSize() == 0) {
				evalInfo.eval_NoQueen_o += STANDARD_NOQUEEN_O;
				evalInfo.eval_NoQueen_e += STANDARD_NOQUEEN_E;
			}
		} else {
			if (w_queens.getDataSize() == 0) {
				evalInfo.eval_NoQueen_o -= STANDARD_NOQUEEN_O;
				evalInfo.eval_NoQueen_e -= STANDARD_NOQUEEN_E;
			}
		}
		
		
		eval_material_nopawnsdrawrule();
		eval_trading();
		eval_standard();
		eval_pawns();
		evalInfo.eval_Material_o *= WEIGHT_MATERIAL_O;
		evalInfo.eval_Material_e *= WEIGHT_MATERIAL_E;
		evalInfo.eval_Standard_o *= WEIGHT_STANDARD_O;
		evalInfo.eval_Standard_e *= WEIGHT_STANDARD_E;
		evalInfo.eval_PST_o *= evalConfig.get_WEIGHT_PST_O();
		evalInfo.eval_PST_e *= evalConfig.get_WEIGHT_PST_E();
		evalInfo.eval_PawnsStandard_o *= evalConfig.get_WEIGHT_PAWNS_STANDARD_O();
		evalInfo.eval_PawnsStandard_e *= evalConfig.get_WEIGHT_PAWNS_STANDARD_E();
		evalInfo.eval_PawnsPassed_o *= evalConfig.get_WEIGHT_PAWNS_PASSED_O();
		evalInfo.eval_PawnsPassed_e *= evalConfig.get_WEIGHT_PAWNS_PASSED_E();
		evalInfo.eval_PawnsPassedKing_o *= evalConfig.get_WEIGHT_PAWNS_PASSED_KING_O();
		evalInfo.eval_PawnsPassedKing_e *= evalConfig.get_WEIGHT_PAWNS_PASSED_KING_E();
		
		eval += interpolator.interpolateByFactor(evalInfo.eval_Material_o +
												evalInfo.eval_Standard_o +
												evalInfo.eval_PST_o +
												evalInfo.eval_PawnsStandard_o +
												evalInfo.eval_PawnsPassed_o +
												evalInfo.eval_PawnsPassedKing_o +
												evalInfo.eval_PawnsUnstoppable_o +
												evalInfo.eval_NoQueen_o,
												
												evalInfo.eval_Material_e +
												evalInfo.eval_Standard_e +
												evalInfo.eval_PST_e +
												evalInfo.eval_PawnsStandard_e +
												evalInfo.eval_PawnsPassed_e +
												evalInfo.eval_PawnsPassedKing_e +
												evalInfo.eval_PawnsUnstoppable_e +
												evalInfo.eval_NoQueen_e);
		
		
		return returnVal(eval);
	}
	
	private int returnVal(int eval) {
		
		int result = eval;
		
		result = drawProbability(result);
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			result = -result;
		}
		return result;
	}
	
	
	private int drawProbability(int eval) {
		
		int abs = Math.abs(eval);
		
		/**
		 * No pawns
		 */
		/*if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			abs = abs / 2;
		}*/
		
		/**
		 * Differently colored bishops, no other pieces except pawns
		 */
		if (w_bishops.getDataSize() == 1
				&& b_bishops.getDataSize() == 1
				&& bitboard.getMaterialFactor().getWhiteFactor() == 3
				&& bitboard.getMaterialFactor().getBlackFactor() == 3) {
			
			long w_colour = (evalInfo.bb_w_bishops & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			long b_colour = (evalInfo.bb_b_bishops & Fields.ALL_WHITE_FIELDS) != 0 ?
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
	
	
    /** When ahead trade pieces and pawns, don't do it otherwise */
    private void eval_trading() {
    	
    	if (true) {
    		return;
    	}
    	
    	//Calculates rates difference of both pawns material and total material for the white and black player. The numbers are of type double in [0, 1]
        double w_material_rate = Math.min(1, bitboard.getMaterialFactor().getWhiteFactor() / (double) (BaseEvalWeights.getMaxMaterialFactor() / (double) 2));
        double b_material_rate = Math.min(1, bitboard.getMaterialFactor().getBlackFactor() / (double) (BaseEvalWeights.getMaxMaterialFactor() / (double) 2));
        //double diff_material_rate = w_material_rate - b_material_rate;
        
        //double w_pawns_rate = w_pawns.getDataSize() / (double) 8;
        //double b_pawns_rate = b_pawns.getDataSize() / (double) 8;
        //double diff_pawns_rate = w_pawns_rate - b_pawns_rate;
    	
        
        /*Openning*/
        
        //Calculates material difference of both pawns material and total material for the white and black player. The numbers are real evaluations of type integer.
        final int w_material_all_o = baseEval.getWhiteMaterialPawns_o() + baseEval.getWhiteMaterialNonPawns_o();
        final int b_material_all_o = baseEval.getBlackMaterialPawns_o() + baseEval.getBlackMaterialNonPawns_o();
        //final int w_material_pawns_o = w_material_all_o - baseEval.getWhiteMaterialNonPawns_o();
        //final int b_material_pawns_o = b_material_all_o - baseEval.getBlackMaterialNonPawns_o();
        double diff_material_all_o = w_material_all_o - b_material_all_o;
        //double diff_material_pawns_o = w_material_pawns_o - b_material_pawns_o;
        
        
        //Calculates the trading bonus or penalty
        int eval_trading_all_o = (int) ( 0.125 * -diff_material_all_o * ( w_material_rate + b_material_rate) );
        //int eval_trading_pawns_o = (int) ( 0.333 * diff_material_pawns_o * ( w_material_rate + b_material_rate - 2 ) );
        
		evalInfo.eval_Material_o += eval_trading_all_o;
		//evalInfo.eval_Material_o += eval_trading_pawns_o;
		
		
		/*Endgame*/
		
		
        //Calculates material difference of both pawns material and total material for the white and black player. The numbers are real evaluations of type integer.
        final int w_material_all_e = baseEval.getWhiteMaterialPawns_e() + baseEval.getWhiteMaterialNonPawns_e();
        final int b_material_all_e = baseEval.getBlackMaterialPawns_e() + baseEval.getBlackMaterialNonPawns_e();
        //final int w_material_pawns_e = w_material_all_e - baseEval.getWhiteMaterialNonPawns_e();
        //final int b_material_pawns_e = b_material_all_e - baseEval.getBlackMaterialNonPawns_e();
        double diff_material_all_e = w_material_all_e - b_material_all_e;
        //double diff_material_pawns_e = w_material_pawns_e - b_material_pawns_e;
        
        
        //Calculates the trading bonus or penalty
        int eval_trading_all_e = (int) ( 0.125 * -diff_material_all_e * ( w_material_rate + b_material_rate) );
        //int eval_trading_pawns_e = (int) ( 0.333 * diff_material_pawns_e * ( w_material_rate + b_material_rate - 2 ) );
        
		evalInfo.eval_Material_e += eval_trading_all_e;
		//evalInfo.eval_Material_e += eval_trading_pawns_e;
        
		
		/*
        final int wM = pos.getwMtrl();
        final int bM = pos.getbMtrl();
        final int wPawn = pos.getwMtrlPawns();
        final int bPawn = pos.getbMtrlPawns();
        final int deltaScore = wM - bM;

        int pBonus = 0;
        pBonus += interpolate((deltaScore > 0) ? wPawn : bPawn, 0, -30 * deltaScore / 100, 6 * pV, 0);
        pBonus += interpolate((deltaScore > 0) ? bM : wM, 0, 30 * deltaScore / 100, qV + 2 * rV + 2 * bV + 2 * nV, 0);

        return pBonus;
        */
    }
    
    
	/*public int eval_material() {
		
		int eval_o = 0;
		int eval_e = 0;
		
		int MATERIAL_KNIGHT_E_W_fixed = MATERIAL_KNIGHT_E;
		int MATERIAL_BISHOP_E_W_fixed = MATERIAL_BISHOP_E;
		int MATERIAL_KNIGHT_E_B_fixed = MATERIAL_KNIGHT_E;
		int MATERIAL_BISHOP_E_B_fixed = MATERIAL_BISHOP_E;
		if (w_pawns.getDataSize() == 0) {//No white pawns
			if (w_queens.getDataSize() == 0 && w_rooks.getDataSize() == 0) {
				if (w_bishops.getDataSize() == 0) {
					MATERIAL_KNIGHT_E_W_fixed = (int) (evalConfig.get_WEIGHT_MATERIAL_PAWNS_E() * (MATERIAL_PAWN_E / 2));
				}
				if (w_knights.getDataSize() == 0 && w_bishops.getDataSize() == 1) {
					MATERIAL_BISHOP_E_W_fixed = (int) (evalConfig.get_WEIGHT_MATERIAL_PAWNS_E() * (MATERIAL_PAWN_E / 2));
				}
			}
		}
		if (b_pawns.getDataSize() == 0) {//No black pawns
			if (b_queens.getDataSize() == 0 && b_rooks.getDataSize() == 0) {
				if (b_bishops.getDataSize() == 0) {
					MATERIAL_KNIGHT_E_B_fixed = (int) (evalConfig.get_WEIGHT_MATERIAL_PAWNS_E() * (MATERIAL_PAWN_E / 2));
				}
				if (b_knights.getDataSize() == 0 && b_bishops.getDataSize() == 1) {
					MATERIAL_BISHOP_E_B_fixed =(int) (evalConfig.get_WEIGHT_MATERIAL_PAWNS_E() * (MATERIAL_PAWN_E / 2));
				}
			}
		}
		
		int pawns = w_pawns.getDataSize() - b_pawns.getDataSize();
		eval_o += MATERIAL_PAWN_O * pawns * evalConfig.get_WEIGHT_MATERIAL_PAWNS_O();
		eval_e += MATERIAL_PAWN_E * pawns * evalConfig.get_WEIGHT_MATERIAL_PAWNS_E();
		
		eval_o += MATERIAL_KNIGHT_O * (w_knights.getDataSize() % 2)
					- MATERIAL_KNIGHT_O * (b_knights.getDataSize() % 2);
		eval_e += MATERIAL_KNIGHT_E_W_fixed * (w_knights.getDataSize() % 2)
					- MATERIAL_KNIGHT_E_B_fixed * (b_knights.getDataSize() % 2);
		
		eval_o += MATERIAL_BISHOP_O * (w_bishops.getDataSize() % 2)
					- MATERIAL_BISHOP_O * (b_bishops.getDataSize() % 2);
		eval_e += MATERIAL_BISHOP_E_W_fixed * (w_bishops.getDataSize() % 2)
					- MATERIAL_BISHOP_E_B_fixed * (b_bishops.getDataSize() % 2);
		
		int rooks = (w_rooks.getDataSize() % 2) - (b_rooks.getDataSize() % 2);
		eval_o += MATERIAL_ROOK_O * rooks;
		eval_e += MATERIAL_ROOK_E * rooks;
		int queens = w_queens.getDataSize() - b_queens.getDataSize();
		eval_o += MATERIAL_QUEEN_O * queens;
		eval_e += MATERIAL_QUEEN_E * queens;
		
		//TODO: Fix material if there are only two knights
		int dknights = (w_knights.getDataSize() / 2) - (b_knights.getDataSize() / 2);
		eval_o += MATERIAL_DOUBLE_KNIGHT_O * dknights;
		eval_e += MATERIAL_DOUBLE_KNIGHT_E * dknights;
		int dbishops = (w_bishops.getDataSize() / 2) - (b_bishops.getDataSize() / 2);
		eval_o += MATERIAL_DOUBLE_BISHOP_O * dbishops;
		eval_e += MATERIAL_DOUBLE_BISHOP_E * dbishops;
		int drooks = (w_rooks.getDataSize() / 2) - (b_rooks.getDataSize() / 2);
		eval_o += MATERIAL_DOUBLE_ROOK_O * drooks;
		eval_e += MATERIAL_DOUBLE_ROOK_E * drooks;
		
		evalInfo.eval_Material_o += eval_o;
		evalInfo.eval_Material_e += eval_e;
		
		return interpolator.interpolateByFactor(eval_o, eval_e);

	}*/
	
	
	public int eval_material_nopawnsdrawrule() {
		
		int w_eval_nopawns_o = baseEval.getWhiteMaterialNonPawns_o();
		int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
		int b_eval_nopawns_o = baseEval.getBlackMaterialNonPawns_o();
		int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
		
		int w_eval_pawns_o = baseEval.getWhiteMaterialPawns_o();
		int w_eval_pawns_e = baseEval.getWhiteMaterialPawns_e();
		int b_eval_pawns_o = baseEval.getBlackMaterialPawns_o();
		int b_eval_pawns_e = baseEval.getBlackMaterialPawns_e();
		
		/*w_eval_pawns_o += MATERIAL_PAWN_O * w_pawns.getDataSize() * evalConfig.get_WEIGHT_MATERIAL_PAWNS_O();
		w_eval_pawns_e += MATERIAL_PAWN_E * w_pawns.getDataSize() * evalConfig.get_WEIGHT_MATERIAL_PAWNS_E();
		b_eval_pawns_o += MATERIAL_PAWN_O * b_pawns.getDataSize() * evalConfig.get_WEIGHT_MATERIAL_PAWNS_O();
		b_eval_pawns_e += MATERIAL_PAWN_E * b_pawns.getDataSize() * evalConfig.get_WEIGHT_MATERIAL_PAWNS_E();
		
		w_eval_nopawns_o += MATERIAL_KNIGHT_O * (w_knights.getDataSize() % 2) + MATERIAL_DOUBLE_KNIGHT_O * (w_knights.getDataSize() / 2);
		w_eval_nopawns_e += MATERIAL_KNIGHT_E * (w_knights.getDataSize() % 2) + MATERIAL_DOUBLE_KNIGHT_E * (w_knights.getDataSize() / 2);
		b_eval_nopawns_o += MATERIAL_KNIGHT_O * (b_knights.getDataSize() % 2) + MATERIAL_DOUBLE_KNIGHT_O * (b_knights.getDataSize() / 2);
		b_eval_nopawns_e += MATERIAL_KNIGHT_E * (b_knights.getDataSize() % 2) + MATERIAL_DOUBLE_KNIGHT_E * (b_knights.getDataSize() / 2);
		
		w_eval_nopawns_o += MATERIAL_BISHOP_O * (w_bishops.getDataSize() % 2) + MATERIAL_DOUBLE_BISHOP_O * (w_bishops.getDataSize() / 2);
		w_eval_nopawns_e += MATERIAL_BISHOP_E * (w_bishops.getDataSize() % 2) + MATERIAL_DOUBLE_BISHOP_E * (w_bishops.getDataSize() / 2);
		b_eval_nopawns_o += MATERIAL_BISHOP_O * (b_bishops.getDataSize() % 2) + MATERIAL_DOUBLE_BISHOP_O * (b_bishops.getDataSize() / 2);
		b_eval_nopawns_e += MATERIAL_BISHOP_E * (b_bishops.getDataSize() % 2) + MATERIAL_DOUBLE_BISHOP_E * (b_bishops.getDataSize() / 2);
		
		w_eval_nopawns_o += MATERIAL_ROOK_O * (w_rooks.getDataSize() % 2) + MATERIAL_DOUBLE_ROOK_O * (w_rooks.getDataSize() / 2);
		w_eval_nopawns_e += MATERIAL_ROOK_E * (w_rooks.getDataSize() % 2) + MATERIAL_DOUBLE_ROOK_E * (w_rooks.getDataSize() / 2);
		b_eval_nopawns_o += MATERIAL_ROOK_O * (b_rooks.getDataSize() % 2) + MATERIAL_DOUBLE_ROOK_O * (b_rooks.getDataSize() / 2);
		b_eval_nopawns_e += MATERIAL_ROOK_E * (b_rooks.getDataSize() % 2) + MATERIAL_DOUBLE_ROOK_E * (b_rooks.getDataSize() / 2);
		
		w_eval_nopawns_o += MATERIAL_QUEEN_O * w_queens.getDataSize();
		w_eval_nopawns_e += MATERIAL_QUEEN_E * w_queens.getDataSize();
		b_eval_nopawns_o += MATERIAL_QUEEN_O * b_queens.getDataSize();
		b_eval_nopawns_e += MATERIAL_QUEEN_E * b_queens.getDataSize();*/
		
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
			
			/*
			if (w_eval_nopawns_o > b_eval_nopawns_o) {
				if (w_eval_nopawns_o < b_eval_nopawns_o + baseEval.getMaterial_BARIER_NOPAWNS_O()) {
					w_eval_nopawns_o = w_eval_nopawns_o / 2;//b_eval_nopawns_o;
				}
			}
			
			if (w_eval_nopawns_e > b_eval_nopawns_e) {
				if (w_eval_nopawns_e < b_eval_nopawns_e + baseEval.getMaterial_BARIER_NOPAWNS_E()) {
					w_eval_nopawns_e = w_eval_nopawns_e / 2;//b_eval_nopawns_e;
				}
			}
			*/
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
			
			/*
			if (b_eval_nopawns_o > w_eval_nopawns_o) {
				if (b_eval_nopawns_o < w_eval_nopawns_o + baseEval.getMaterial_BARIER_NOPAWNS_O()) {
					b_eval_nopawns_o = b_eval_nopawns_o / 2;//w_eval_nopawns_o;
				}
			}
			
			if (b_eval_nopawns_e > w_eval_nopawns_e) {
				if (b_eval_nopawns_e < w_eval_nopawns_e + baseEval.getMaterial_BARIER_NOPAWNS_E()) {
					b_eval_nopawns_e = b_eval_nopawns_e / 2;//w_eval_nopawns_e;
				}
			}
			*/
		}
		
		
		int w_double_bishops_o = 0;
		int w_double_bishops_e = 0;
		int b_double_bishops_o = 0;
		int b_double_bishops_e = 0;
		if (w_bishops.getDataSize() >= 2) {
			w_double_bishops_o += MATERIAL_DOUBLE_BISHOP_O;
			w_double_bishops_e += MATERIAL_DOUBLE_BISHOP_E;
		}
		if (b_bishops.getDataSize() >= 2) {
			b_double_bishops_o += MATERIAL_DOUBLE_BISHOP_O;
			b_double_bishops_e += MATERIAL_DOUBLE_BISHOP_E;
		}
		
		evalInfo.eval_Material_o += (w_eval_nopawns_o - b_eval_nopawns_o) + (w_eval_pawns_o - b_eval_pawns_o) + (w_double_bishops_o - b_double_bishops_o);
		evalInfo.eval_Material_e += (w_eval_nopawns_e - b_eval_nopawns_e) + (w_eval_pawns_e - b_eval_pawns_e) + (w_double_bishops_e - b_double_bishops_e);
		
		return interpolator.interpolateByFactor(evalInfo.eval_Material_o, evalInfo.eval_Material_e);

	}
	
	public void eval_standard() {
		int eval_o = 0;
		int eval_e = 0;
		
		int tempo = (bitboard.getColourToMove() == Figures.COLOUR_WHITE ? 1 : -1);
		eval_o += STANDARD_TEMPO_O * tempo;
		eval_e += STANDARD_TEMPO_E * tempo;
		
		int castling = (castling(Figures.COLOUR_WHITE) - castling(Figures.COLOUR_BLACK));
		eval_o += STANDARD_CASTLING_O * castling;
		eval_e += STANDARD_CASTLING_E * castling;
		
		int fianchetto = fianchetto();
		eval_o += FIANCHETTO * fianchetto;
		
		int patterns = eval_patterns();
		eval_o += patterns;
		eval_e += patterns;
		
		int kingsDistance = Fields.getDistancePoints(w_king.getData()[0], b_king.getData()[0]);
		
		//King Opposition
		if (bitboard.getMaterialFactor().getWhiteFactor() == 0
				&& bitboard.getMaterialFactor().getBlackFactor() == 0) {
			
			if (kingsDistance == 2) {
				
				boolean kingsOnSameColourSquares = ((Fields.ALL_WHITE_FIELDS & evalInfo.bb_w_king) != 0 &&  (Fields.ALL_WHITE_FIELDS & evalInfo.bb_b_king) != 0)
						|| ((Fields.ALL_BLACK_FIELDS & evalInfo.bb_w_king) != 0 &&  (Fields.ALL_BLACK_FIELDS & evalInfo.bb_b_king) != 0);
				
				if (kingsOnSameColourSquares) {
					//The side moved last has the opposition. In this case the side which is not on move.
					
					if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
						//Black has the opposition
						eval_o -= STANDARD_KINGS_OPPOSITION_O;
						eval_e -= STANDARD_KINGS_OPPOSITION_E;
					} else {
						//White has the opposition
						eval_o += STANDARD_KINGS_OPPOSITION_O;
						eval_e += STANDARD_KINGS_OPPOSITION_E;
					}
				}
			} else if (kingsDistance > 2 && kingsDistance % 2 == 0) {
				//TODO: Implement the opposition when the kings have more than 2 square distance
				//e.g. on the same line (can use Fields.areOnTheSameLine(f1, f2))
			}
		}
		
		if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
			eval_o += STANDARD_DIST_KINGS_O[kingsDistance];
			eval_e += STANDARD_DIST_KINGS_E[kingsDistance];
		} else {
			eval_o -= STANDARD_DIST_KINGS_O[kingsDistance];
			eval_e -= STANDARD_DIST_KINGS_E[kingsDistance];
		}
		
		if (w_queens.getDataSize() == 1 && b_queens.getDataSize() == 1) {
			int queensDistance = Fields.getDistancePoints(w_queens.getData()[0], b_queens.getData()[0]);
			eval_o += STANDARD_DIST_QUEENS_O[queensDistance];
			eval_e += STANDARD_DIST_QUEENS_E[queensDistance];
		}
		
		
		evalInfo.eval_PST_o += baseEval.getPST_o();
		evalInfo.eval_PST_e += baseEval.getPST_e();

		evalInfo.eval_Standard_o += eval_o;
		evalInfo.eval_Standard_e += eval_e;
	}
	
	
	public void eval_pawns() {
		
		bitboard.getPawnsCache().lock();
		PawnsModelEval pawnsModelEval = bitboard.getPawnsStructure();
		
		evalInfo.eval_PawnsStandard_o += ((BagaturPawnsEval)pawnsModelEval).getStandardEval_o();
		evalInfo.eval_PawnsStandard_e += ((BagaturPawnsEval)pawnsModelEval).getStandardEval_e();
		
		evalInfo.eval_PawnsPassed_o += ((BagaturPawnsEval)pawnsModelEval).getPassersEval_o();
		evalInfo.eval_PawnsPassed_e += ((BagaturPawnsEval)pawnsModelEval).getPassersEval_e();
		
		evalInfo.eval_PawnsPassedKing_o += ((BagaturPawnsEval)pawnsModelEval).getPassersKingEval_o();
		evalInfo.eval_PawnsPassedKing_e += ((BagaturPawnsEval)pawnsModelEval).getPassersKingEval_e();
		
		/*boolean unstoppablePasser = bitboard.hasUnstoppablePasser();
		if (unstoppablePasser) {
			if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
				evalInfo.eval_PawnsUnstoppable_o += PAWNS_PASSED_UNSTOPPABLE;
				evalInfo.eval_PawnsUnstoppable_e += PAWNS_PASSED_UNSTOPPABLE;
			} else {
				evalInfo.eval_PawnsUnstoppable_o -= PAWNS_PASSED_UNSTOPPABLE;
				evalInfo.eval_PawnsUnstoppable_e -= PAWNS_PASSED_UNSTOPPABLE;
			}
		}*/
		
		int PAWNS_PASSED_UNSTOPPABLE = 100 + baseEval.getMaterialRook();
		
		int unstoppablePasser = bitboard.getUnstoppablePasser();
		if (unstoppablePasser > 0) {
			evalInfo.eval_PawnsUnstoppable_o += PAWNS_PASSED_UNSTOPPABLE;
			evalInfo.eval_PawnsUnstoppable_e += PAWNS_PASSED_UNSTOPPABLE;
		} else if (unstoppablePasser < 0) {
			evalInfo.eval_PawnsUnstoppable_o -= PAWNS_PASSED_UNSTOPPABLE;
			evalInfo.eval_PawnsUnstoppable_e -= PAWNS_PASSED_UNSTOPPABLE;
		}
		
		bitboard.getPawnsCache().unlock();
	}
	
	
	private void initEvalInfo1() {
		evalInfo.bb_all_w_pieces = bitboard.getFiguresBitboardByColour(Figures.COLOUR_WHITE);
		evalInfo.bb_all_b_pieces = bitboard.getFiguresBitboardByColour(Figures.COLOUR_BLACK);
		evalInfo.bb_all = evalInfo.bb_all_w_pieces | evalInfo.bb_all_b_pieces;
		evalInfo.bb_w_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		evalInfo.bb_b_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
		evalInfo.bb_w_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
		evalInfo.bb_b_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);
		evalInfo.bb_w_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT);
		evalInfo.bb_b_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT);		
		evalInfo.bb_w_queens = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN);
		evalInfo.bb_b_queens = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN);
		evalInfo.bb_w_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE);
		evalInfo.bb_b_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE);
		evalInfo.bb_w_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KING);
		evalInfo.bb_b_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KING);
		
	}
	
	public void eval_pawns_RooksAndQueens() {
		
		
		bitboard.getPawnsCache().lock();
		PawnsModelEval pawnsModelEval = bitboard.getPawnsStructure();
		PawnsModel pmodel = pawnsModelEval.getModel();
		
		long bb_wpawns_attacks = pmodel.getWattacks();
		long bb_bpawns_attacks = pmodel.getBattacks();
		evalInfo.bb_wpawns_attacks = bb_wpawns_attacks;
		evalInfo.bb_bpawns_attacks = bb_bpawns_attacks;
		evalInfo.w_kingOpened = pmodel.getWKingOpenedFiles() + pmodel.getWKingSemiOpOpenedFiles() + pmodel.getWKingSemiOwnOpenedFiles();
		evalInfo.b_kingOpened = pmodel.getBKingOpenedFiles() + pmodel.getBKingSemiOpOpenedFiles() + pmodel.getBKingSemiOwnOpenedFiles();
		evalInfo.w_gards = ((BagaturPawnsEval)pawnsModelEval).getWGardsScores();
		evalInfo.b_gards = ((BagaturPawnsEval)pawnsModelEval).getBGardsScores();
		evalInfo.open_files = pmodel.getOpenedFiles();
		evalInfo.half_open_files_w = pmodel.getWHalfOpenedFiles();
		evalInfo.half_open_files_b = pmodel.getBHalfOpenedFiles();
		
		int w_passed_pawns_count = pmodel.getWPassedCount();
		if (w_passed_pawns_count > 0) {
			Pawn[] w_passed_pawns = pmodel.getWPassed();
			for (int i=0; i<w_passed_pawns_count; i++) {
				Pawn p = w_passed_pawns[i];
				long stoppers = p.getFront() & evalInfo.bb_all;
				if (stoppers != 0) {
					int stoppersCount = Utils.countBits_less1s(stoppers);
					evalInfo.eval_PawnsPassedStoppers_o -= (stoppersCount * PAWNS_PASSED_O[p.getRank()]) / 4;
					evalInfo.eval_PawnsPassedStoppers_e -= (stoppersCount * PAWNS_PASSED_E[p.getRank()]) / 4;
				}
			}
		}
		int b_passed_pawns_count = pmodel.getBPassedCount();
		if (b_passed_pawns_count > 0) {
			Pawn[] b_passed_pawns = pmodel.getBPassed();
			for (int i=0; i<b_passed_pawns_count; i++) {
				Pawn p = b_passed_pawns[i];
				long stoppers = p.getFront() & evalInfo.bb_all;
				if (stoppers != 0) {
					int stoppersCount = Utils.countBits_less1s(stoppers);
					evalInfo.eval_PawnsPassedStoppers_o += (stoppersCount * PAWNS_PASSED_O[p.getRank()]) / 4;
					evalInfo.eval_PawnsPassedStoppers_e += (stoppersCount * PAWNS_PASSED_E[p.getRank()]) / 4;
				}
			}
		}
		bitboard.getPawnsCache().unlock();
		
		
		int rooks_opened = 0;
		int rooks_semiopened = 0;
		int rooks_7th2th = 0;
		
		int w_rooks_count = w_rooks.getDataSize();
		if (w_rooks_count > 0) {
			int[] w_rooks_fields = w_rooks.getData();
			for (int i=0; i<w_rooks_count; i++) {
				
				int fieldID = w_rooks_fields[i];
				long bb_field = Fields.ALL_A1H1[fieldID];
				
				// Open and half-open files:
				if ((bb_field & evalInfo.open_files) != 0) {
					rooks_opened++;
				} else if ((bb_field & evalInfo.half_open_files_w) != 0) {
					rooks_semiopened++;
				}
				
				// Rook on 7th rank:
				if ((bb_field & RANK_7TH) != 0L) {
					//If there are pawns on 7th rank or king on 8th rank
					if ((evalInfo.bb_b_pawns & RANK_7TH) != 0L || (evalInfo.bb_b_king & RANK_8TH) != 0L) {
						rooks_7th2th++;
					}
				}
			}
		}
		
		int b_rooks_count = b_rooks.getDataSize();
		if (b_rooks_count > 0) {
			int[] b_rooks_fields = b_rooks.getData();
			for (int i=0; i<b_rooks_count; i++) {
				
				int fieldID = b_rooks_fields[i];
				long bb_field = Fields.ALL_A1H1[fieldID];
				
				// Open and half-open files:
				if ((bb_field & evalInfo.open_files) != 0) {
					rooks_opened--;
				} else if ((bb_field & evalInfo.half_open_files_b) != 0) {
					rooks_semiopened--;
				}
				
				// Rook on 2th rank:
				if ((bb_field & RANK_2TH) != 0L) {
					//If there are pawns on 2th rank or king on 1th rank
					if ((evalInfo.bb_w_pawns & RANK_2TH) != 0L || (evalInfo.bb_w_king & RANK_1TH) != 0L) {
						rooks_7th2th--;
					}
				}
			}
		}
		
		evalInfo.eval_PawnsRooksQueens_o += rooks_opened * PAWNS_ROOK_OPENED_O;
		evalInfo.eval_PawnsRooksQueens_e += rooks_opened * PAWNS_ROOK_OPENED_E;
		
		evalInfo.eval_PawnsRooksQueens_o += rooks_semiopened * PAWNS_ROOK_SEMIOPENED_O;
		evalInfo.eval_PawnsRooksQueens_e += rooks_semiopened * PAWNS_ROOK_SEMIOPENED_E;
		
		evalInfo.eval_PawnsRooksQueens_o += rooks_7th2th * PAWNS_ROOK_7TH2TH_O;
		evalInfo.eval_PawnsRooksQueens_e += rooks_7th2th * PAWNS_ROOK_7TH2TH_E;
		
		
		int queens_7th2th = 0;
		int w_queens_count = w_queens.getDataSize();
		if (w_queens_count > 0) {
			int[] w_queens_fields = w_queens.getData();
			for (int i=0; i<w_queens_count; i++) {
				
				int fieldID = w_queens_fields[i];
				long bb_field = Fields.ALL_A1H1[fieldID];
				
				// Queen on 7th rank:
				if ((bb_field & RANK_7TH) != 0L) {
					//If there are pawns on 7th rank or king on 8th rank
					if ((evalInfo.bb_b_pawns & RANK_7TH) != 0L || (evalInfo.bb_b_king & RANK_8TH) != 0L) {
						queens_7th2th++;
					}
				}				
			}
		}
		
		int b_queens_count = b_queens.getDataSize();
		if (b_queens_count > 0) {
			int[] b_queens_fields = b_queens.getData();
			for (int i=0; i<b_queens_count; i++) {
				
				int fieldID = b_queens_fields[i];
				long bb_field = Fields.ALL_A1H1[fieldID];
				
				// Queen on 1th rank:
				if ((bb_field & RANK_2TH) != 0L) {
					//If there are pawns on 2th rank or king on 1th rank
					if ((evalInfo.bb_w_pawns & RANK_2TH) != 0L || (evalInfo.bb_w_king & RANK_1TH) != 0L) {
						queens_7th2th--;
					}
				}
			}
		}
		
		evalInfo.eval_PawnsRooksQueens_o += queens_7th2th * PAWNS_QUEEN_7TH2TH_O;
		evalInfo.eval_PawnsRooksQueens_e += queens_7th2th * PAWNS_QUEEN_7TH2TH_E;
		
		
		int kingOpened = 0;
		if (b_rooks.getDataSize() > 0 || b_queens.getDataSize() > 0) {
			kingOpened += evalInfo.w_kingOpened;
		}
	    if (w_rooks.getDataSize() > 0 || w_queens.getDataSize() > 0) {
	    	kingOpened -= evalInfo.b_kingOpened;
	    }
	    evalInfo.eval_PawnsRooksQueens_o += kingOpened * PAWNS_KING_OPENED;
	}
	
	
	private void initEvalInfo2() {
		
		// Initialize king attack bitboards and king attack zones for both sides:
		long w_king_zone = KingPlies.ALL_KING_MOVES[w_king.getData()[0]];
		long b_king_zone = KingPlies.ALL_KING_MOVES[b_king.getData()[0]];
		if ((w_king_zone & Fields.LETTER_H) != 0 & (w_king_zone & Fields.LETTER_F) == 0) {
			w_king_zone |= w_king_zone << 1;
		}
		if ((b_king_zone & Fields.LETTER_H) != 0 & (b_king_zone & Fields.LETTER_F) == 0) {
			b_king_zone |= b_king_zone << 1;
		}
		if ((w_king_zone & Fields.LETTER_A) != 0 & (w_king_zone & Fields.LETTER_C) == 0) {
			w_king_zone |= w_king_zone >> 1;
		}
		if ((b_king_zone & Fields.LETTER_A) != 0 & (b_king_zone & Fields.LETTER_C) == 0) {
			b_king_zone |= b_king_zone >> 1;
		}
		evalInfo.attackedBy[Constants.PID_W_KING] = w_king_zone;
		evalInfo.attackedBy[Constants.PID_B_KING] = b_king_zone;
		
		evalInfo.attackZone[Figures.COLOUR_WHITE]
		                    = evalInfo.attackedBy[Constants.PID_B_KING] | evalInfo.attackedBy[Constants.PID_B_KING] << 8;
		
		evalInfo.attackZone[Figures.COLOUR_BLACK]
		                    = evalInfo.attackedBy[Constants.PID_W_KING] | evalInfo.attackedBy[Constants.PID_W_KING] >> 8;
		
		
		// Initialize pawn attack bitboards for both sides:
		evalInfo.attackedBy[Constants.PID_W_PAWN] = evalInfo.bb_wpawns_attacks;
		evalInfo.attackedBy[Constants.PID_B_PAWN] = evalInfo.bb_bpawns_attacks;
		long bb_wpawns_kingAttacks = evalInfo.bb_wpawns_attacks & evalInfo.attackedBy[Constants.PID_B_KING];
    	if (bb_wpawns_kingAttacks != 0) {
    		evalInfo.attacked[Figures.COLOUR_WHITE] += Utils.countBits_less1s(bb_wpawns_kingAttacks);
    	}
		long bb_bpawns_kingAttacks = evalInfo.bb_bpawns_attacks & evalInfo.attackedBy[Constants.PID_W_KING];
    	if (bb_bpawns_kingAttacks != 0) {
    		evalInfo.attacked[Figures.COLOUR_BLACK] += Utils.countBits_less1s(bb_bpawns_kingAttacks);
    	}
    	
		evalInfo.attackCount[Figures.COLOUR_WHITE] += Utils.countBits_less1s(
				evalInfo.attackedBy[Constants.PID_W_PAWN] & evalInfo.attackedBy[Constants.PID_B_KING]) / 2;
		evalInfo.attackCount[Figures.COLOUR_BLACK] += Utils.countBits_less1s(
				evalInfo.attackedBy[Constants.PID_B_PAWN] & evalInfo.attackedBy[Constants.PID_W_KING]) / 2;
	}
	
	
	private void eval_mobility() {
		
		int eval_o = 0;
		int eval_e = 0;
		
		
		//Knights
		int w_knights_count = w_knights.getDataSize();
		if (w_knights_count > 0) {
			int[] w_knights_fields = w_knights.getData();
			for (int i=0; i<w_knights_count; i++) {
				
				int fieldID = w_knights_fields[i];
				long bb_field = Fields.ALL_A1H1[fieldID];
				
				long bb_moves = KnightPlies.ALL_KNIGHT_MOVES[fieldID];
				evalInfo.attackedBy[Constants.PID_W_KNIGHT] |= bb_moves;
				evalInfo.attacksByFieldID[fieldID] = bb_moves;
				
			    // King attack
			    if((bb_moves & evalInfo.attackZone[Figures.COLOUR_WHITE]) != 0) {
			    	evalInfo.attackCount[Figures.COLOUR_WHITE]++;
			    	evalInfo.attackWeight[Figures.COLOUR_WHITE] += KnightAttackWeight;
			    	
			    	long bb = bb_moves & evalInfo.attackedBy[Constants.PID_B_KING];
			    	if (bb != 0) {
			    		evalInfo.attacked[Figures.COLOUR_WHITE] += Utils.countBits_less1s(bb);
			    	}
			    }
			    
			    // Mobility
			    long attacks = bb_moves & ~evalInfo.bb_all_w_pieces;
			    int mob = Utils.countBits(attacks);
			    eval_o += MOBILITY_KNIGHT_O[mob];
			    eval_e += MOBILITY_KNIGHT_E[mob];
			    
			    
			    // Knight outposts:
			    if ((Fields.SPACE_BLACK & bb_field) != 0) {
				    long bb_neighbors = ~PawnStructureConstants.WHITE_FRONT_FULL[fieldID] & PawnStructureConstants.WHITE_PASSED[fieldID];
				    if ((bb_neighbors & evalInfo.bb_b_pawns) == 0) { // Weak field
				    	
				    	int scores = 1;
				    	
			    		if ((BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_MOVES[fieldID] & evalInfo.bb_w_pawns) != 0) {
			    			scores += 1;
			    			if (b_knights.getDataSize() == 0) {
			    				long colouredFields = (bb_field & Fields.ALL_WHITE_FIELDS) != 0 ?
			    						Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			    				if ((colouredFields & evalInfo.bb_b_bishops) == 0) {
			    					scores += 1;
			    				}
			    			}
			    		}
			    		
			    		eval_o += scores * KNIGHT_OUTPOST_O[fieldID];
					    eval_e += scores * KNIGHT_OUTPOST_E[fieldID];
				    }
			    }
			}
		}
		
		int b_knights_count = b_knights.getDataSize();
		if (b_knights_count > 0) {
			int[] b_knights_fields = b_knights.getData();
			for (int i=0; i<b_knights_count; i++) {
				
				int fieldID = b_knights_fields[i];
				long bb_field = Fields.ALL_A1H1[fieldID];
				
				long bb_moves = KnightPlies.ALL_KNIGHT_MOVES[fieldID];
				evalInfo.attackedBy[Constants.PID_B_KNIGHT] |= bb_moves;
				evalInfo.attacksByFieldID[fieldID] = bb_moves;
				
			    // King attack
			    if((bb_moves & evalInfo.attackZone[Figures.COLOUR_BLACK]) != 0) {
			    	evalInfo.attackCount[Figures.COLOUR_BLACK]++;
			    	evalInfo.attackWeight[Figures.COLOUR_BLACK] += KnightAttackWeight;
			    	
			    	long bb = bb_moves & evalInfo.attackedBy[Constants.PID_W_KING];
			    	if (bb != 0) {
			    		evalInfo.attacked[Figures.COLOUR_BLACK] += Utils.countBits_less1s(bb);
			    	}
			    }
			    
			    // Mobility
			    long attacks = bb_moves & ~evalInfo.bb_all_b_pieces;
			    int mob = Utils.countBits(attacks);
			    eval_o -= MOBILITY_KNIGHT_O[mob];
			    eval_e -= MOBILITY_KNIGHT_E[mob];
			    
			    
			    // Knight outposts:
			    if ((Fields.SPACE_WHITE & bb_field) != 0) {
				    long bb_neighbors = ~PawnStructureConstants.BLACK_FRONT_FULL[fieldID] & PawnStructureConstants.BLACK_PASSED[fieldID];
				    if ((bb_neighbors & evalInfo.bb_w_pawns) == 0) { // Weak field
				      
				    	int scores = 1;

				    	if ((WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_MOVES[fieldID] & evalInfo.bb_b_pawns) != 0) {
			    			scores += 1;
			    			if (w_knights.getDataSize() == 0) {
			    				long colouredFields = (bb_field & Fields.ALL_WHITE_FIELDS) != 0 ?
			    						Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			    				if ((colouredFields & evalInfo.bb_w_bishops) == 0) {
			    					scores += 1;
			    				}
			    			}
			    		}
				    		
			    		eval_o -= scores * KNIGHT_OUTPOST_O[HORIZONTAL_SYMMETRY[fieldID]];
					    eval_e -= scores * KNIGHT_OUTPOST_E[HORIZONTAL_SYMMETRY[fieldID]];
				    }
			    }
			}
		}
		
		int w_bishops_count = w_bishops.getDataSize();
		if (w_bishops_count > 0) {
			
			int[] w_bishops_fields = w_bishops.getData();
			for (int i=0; i<w_bishops_count; i++) {
				
				int fieldID = w_bishops_fields[i];
				long bb_field = Fields.ALL_A1H1[fieldID];
				
				long bb_moves = bishopAttacks(fieldID, evalInfo.bb_all & ~evalInfo.bb_w_queens);
				evalInfo.attackedBy[Constants.PID_W_BISHOP] |= bb_moves;
				evalInfo.attacksByFieldID[fieldID] = bb_moves;
				
			    // King attack
			    if((bb_moves & evalInfo.attackZone[Figures.COLOUR_WHITE]) != 0) {
			    	evalInfo.attackCount[Figures.COLOUR_WHITE]++;
			    	evalInfo.attackWeight[Figures.COLOUR_WHITE] += BishopAttackWeight;
			    	
			    	long bb = bb_moves & evalInfo.attackedBy[Constants.PID_B_KING];
			    	if (bb != 0) {
			    		evalInfo.attacked[Figures.COLOUR_WHITE] += Utils.countBits_less1s(bb);
			    	}
			    }
			    
			    // Mobility
			    long attacks = bb_moves & ~evalInfo.bb_all_w_pieces;
			    int mob = Utils.countBits(attacks);
			    eval_o += MOBILITY_BISHOP_O[mob];
			    eval_e += MOBILITY_BISHOP_E[mob];

			    
			    // Bishop outposts:
			    if ((Fields.SPACE_BLACK & bb_field) != 0) {
				    long bb_neighbors = ~PawnStructureConstants.WHITE_FRONT_FULL[fieldID] & PawnStructureConstants.WHITE_PASSED[fieldID];
				    if ((bb_neighbors & evalInfo.bb_b_pawns) == 0) { // Weak field
				      
				    	int scores = 1;
	
			    		if ((BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_MOVES[fieldID] & evalInfo.bb_w_pawns) != 0) {
			    			scores += 1;
			    			if (b_knights.getDataSize() == 0) {
			    				long colouredFields = (bb_field & Fields.ALL_WHITE_FIELDS) != 0 ?
			    						Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			    				if ((colouredFields & evalInfo.bb_b_bishops) == 0) {
			    					scores += 1;
			    				}
			    			}
			    		}
			    		
			    		eval_o += scores * BISHOP_OUTPOST_O[fieldID];
					    eval_e += scores * BISHOP_OUTPOST_E[fieldID];
				    }
			    }
			    
			    //Bad bishop penalty
			    int bad_bishop_score = 0;
			    if ((bb_field & Fields.ALL_WHITE_FIELDS) != 0) { //White bishop
			    	
			    	bad_bishop_score += 2 * Utils.countBits_less1s(Fields.ALL_WHITE_FIELDS & Fields.CENTER_2 & (evalInfo.bb_w_pawns | evalInfo.bb_b_pawns));
			    	bad_bishop_score += Utils.countBits_less1s(Fields.ALL_WHITE_FIELDS & ~Fields.CENTER_2 & evalInfo.bb_w_pawns);
			    } else { //Black bishop
			    	
			    	bad_bishop_score += 2 * Utils.countBits_less1s(Fields.ALL_BLACK_FIELDS & Fields.CENTER_2 & (evalInfo.bb_w_pawns | evalInfo.bb_b_pawns));
			    	bad_bishop_score += Utils.countBits_less1s(Fields.ALL_BLACK_FIELDS & ~Fields.CENTER_2 & evalInfo.bb_w_pawns);
			    }
			    
	    		eval_o -= bad_bishop_score * 5;
			    eval_e -= bad_bishop_score * 10;
			}
		}
		
		int b_bishops_count = b_bishops.getDataSize();
		if (b_bishops_count > 0) {
			int[] b_bishops_fields = b_bishops.getData();
			for (int i=0; i<b_bishops_count; i++) {
				
				int fieldID = b_bishops_fields[i];
				long bb_field = Fields.ALL_A1H1[fieldID];
				
				long bb_moves = bishopAttacks(fieldID, evalInfo.bb_all & ~evalInfo.bb_b_queens);
				evalInfo.attackedBy[Constants.PID_B_BISHOP] |= bb_moves;
				evalInfo.attacksByFieldID[fieldID] = bb_moves;
				
			    // King attack
			    if((bb_moves & evalInfo.attackZone[Figures.COLOUR_BLACK]) != 0) {
			    	evalInfo.attackCount[Figures.COLOUR_BLACK]++;
			    	evalInfo.attackWeight[Figures.COLOUR_BLACK] += BishopAttackWeight;
			    	
			    	long bb = bb_moves & evalInfo.attackedBy[Constants.PID_W_KING];
			    	if (bb != 0) {
			    		evalInfo.attacked[Figures.COLOUR_BLACK] += Utils.countBits_less1s(bb);
			    	}
			    }
			    
			    // Mobility
			    long attacks = bb_moves & ~evalInfo.bb_all_b_pieces;
			    int mob = Utils.countBits(attacks);
			    eval_o -= MOBILITY_BISHOP_O[mob];
			    eval_e -= MOBILITY_BISHOP_E[mob];
			    
			    
			    // Bishop outposts:
			    if ((Fields.SPACE_WHITE & bb_field) != 0) {
				    long bb_neighbors = ~PawnStructureConstants.BLACK_FRONT_FULL[fieldID] & PawnStructureConstants.BLACK_PASSED[fieldID];
				    if ((bb_neighbors & evalInfo.bb_w_pawns) == 0) { // Weak field
				    	
				    	int scores = 1;
				    	
			    		if ((WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_MOVES[fieldID] & evalInfo.bb_b_pawns) != 0) {
			    			scores += 1;
			    			if (w_knights.getDataSize() == 0) {
			    				long colouredFields = (bb_field & Fields.ALL_WHITE_FIELDS) != 0 ?
			    						Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			    				if ((colouredFields & evalInfo.bb_w_bishops) == 0) {
			    					scores += 1;
			    				}
			    			}
			    		}
			    		
			    		eval_o -= scores * BISHOP_OUTPOST_O[HORIZONTAL_SYMMETRY[fieldID]];
					    eval_e -= scores * BISHOP_OUTPOST_E[HORIZONTAL_SYMMETRY[fieldID]];
				    }
			    }
			    
			    //Bad bishop penalty
			    int bad_bishop_score = 0;
			    if ((bb_field & Fields.ALL_WHITE_FIELDS) != 0) { //White bishop
			    	
			    	bad_bishop_score += 2 * Utils.countBits_less1s(Fields.ALL_WHITE_FIELDS & Fields.CENTER_2 & (evalInfo.bb_w_pawns | evalInfo.bb_b_pawns));
			    	bad_bishop_score += Utils.countBits_less1s(Fields.ALL_WHITE_FIELDS & ~Fields.CENTER_2 & evalInfo.bb_b_pawns);
			    } else { //Black bishop
			    	
			    	bad_bishop_score += 2 * Utils.countBits_less1s(Fields.ALL_BLACK_FIELDS & Fields.CENTER_2 & (evalInfo.bb_w_pawns | evalInfo.bb_b_pawns));
			    	bad_bishop_score += Utils.countBits_less1s(Fields.ALL_BLACK_FIELDS & ~Fields.CENTER_2 & evalInfo.bb_b_pawns);
			    }
			    
	    		eval_o += bad_bishop_score * 5;
			    eval_e += bad_bishop_score * 10;
			}
		}
		
		
		int w_rooks_count = w_rooks.getDataSize();
		if (w_rooks_count > 0) {
			int[] w_rooks_fields = w_rooks.getData();
			for (int i=0; i<w_rooks_count; i++) {
				
				int fieldID = w_rooks_fields[i];
				
				long bb_moves = rookAttacks(fieldID, evalInfo.bb_all & ~(evalInfo.bb_w_queens | evalInfo.bb_w_rooks));
				evalInfo.attackedBy[Constants.PID_W_ROOK] |= bb_moves;
				evalInfo.attacksByFieldID[fieldID] = bb_moves;
				
			    // King attack
			    if((bb_moves & evalInfo.attackZone[Figures.COLOUR_WHITE]) != 0) {
			    	evalInfo.attackCount[Figures.COLOUR_WHITE]++;
			    	evalInfo.attackWeight[Figures.COLOUR_WHITE] += RookAttackWeight;
			    	
			    	long bb = bb_moves & evalInfo.attackedBy[Constants.PID_B_KING];
			    	if (bb != 0) {
			    		evalInfo.attacked[Figures.COLOUR_WHITE] += Utils.countBits_less1s(bb);
			    	}
			    }
			    
			    // Mobility
			    long attacks = bb_moves & ~evalInfo.bb_all_w_pieces;
			    int mob = Utils.countBits(attacks);
			    eval_o += MOBILITY_ROOK_O[mob];
			    eval_e += MOBILITY_ROOK_E[mob];
			    
			    // Penalize rooks which are trapped inside a king which has lost the
			    // right to castle:
			    //TODO: Implement
			}
		}
		
		int b_rooks_count = b_rooks.getDataSize();
		if (b_rooks_count > 0) {
			int[] b_rooks_fields = b_rooks.getData();
			for (int i=0; i<b_rooks_count; i++) {
				
				int fieldID = b_rooks_fields[i];
				
				long bb_moves = rookAttacks(fieldID, evalInfo.bb_all & ~(evalInfo.bb_b_queens | evalInfo.bb_b_rooks));
				evalInfo.attackedBy[Constants.PID_B_ROOK] |= bb_moves;
				evalInfo.attacksByFieldID[fieldID] = bb_moves;
				
			    // King attack
			    if((bb_moves & evalInfo.attackZone[Figures.COLOUR_BLACK]) != 0) {
			    	evalInfo.attackCount[Figures.COLOUR_BLACK]++;
			    	evalInfo.attackWeight[Figures.COLOUR_BLACK] += RookAttackWeight;
			    	
			    	long bb = bb_moves & evalInfo.attackedBy[Constants.PID_W_KING];
			    	if (bb != 0) {
			    		evalInfo.attacked[Figures.COLOUR_BLACK] += Utils.countBits_less1s(bb);
			    	}
			    }
			    
			    // Mobility
			    long attacks = bb_moves & ~evalInfo.bb_all_b_pieces;
			    int mob = Utils.countBits(attacks);
			    eval_o -= MOBILITY_ROOK_O[mob];
			    eval_e -= MOBILITY_ROOK_E[mob];
			    
			    // Penalize rooks which are trapped inside a king which has lost the
			    // right to castle:
			    //TODO: Implement
			}
		}
		
		
		int w_queens_count = w_queens.getDataSize();
		if (w_queens_count > 0) {
			int[] w_queens_fields = w_queens.getData();
			for (int i=0; i<w_queens_count; i++) {
				
				int fieldID = w_queens_fields[i];				
				
				long blockers = evalInfo.bb_all & ~evalInfo.bb_w_queens;
				long bb_moves = rookAttacks(fieldID, blockers & ~evalInfo.bb_w_rooks);
				bb_moves |= bishopAttacks(fieldID, blockers & ~evalInfo.bb_w_bishops);
				evalInfo.attackedBy[Constants.PID_W_QUEEN] |= bb_moves;
				evalInfo.attacksByFieldID[fieldID] = bb_moves;
				
			    // King attack
			    if((bb_moves & evalInfo.attackZone[Figures.COLOUR_WHITE]) != 0) {
			    	evalInfo.attackCount[Figures.COLOUR_WHITE]++;
			    	evalInfo.attackWeight[Figures.COLOUR_WHITE] += QueenAttackWeight;
			    	
			    	long bb = bb_moves & evalInfo.attackedBy[Constants.PID_B_KING];
			    	if (bb != 0) {
			    		evalInfo.attacked[Figures.COLOUR_WHITE] += Utils.countBits_less1s(bb);
			    	}
			    }
			    
			    // Mobility
			    long attacks = bb_moves & ~evalInfo.bb_all_w_pieces;
			    int mob = Utils.countBits(attacks);
			    eval_o += MOBILITY_QUEEN_O[mob];
			    eval_e += MOBILITY_QUEEN_E[mob];
			}
		}
		
		int b_queens_count = b_queens.getDataSize();
		if (b_queens_count > 0) {
			int[] b_queens_fields = b_queens.getData();
			for (int i=0; i<b_queens_count; i++) {
				
				int fieldID = b_queens_fields[i];
				
				long blockers = evalInfo.bb_all & ~evalInfo.bb_b_queens;
				long bb_moves = rookAttacks(fieldID, blockers & ~evalInfo.bb_b_rooks);
				bb_moves |= bishopAttacks(fieldID, blockers & ~evalInfo.bb_b_bishops);
				evalInfo.attackedBy[Constants.PID_B_QUEEN] |= bb_moves;
				evalInfo.attacksByFieldID[fieldID] = bb_moves;
				
			    // King attack
			    if((bb_moves & evalInfo.attackZone[Figures.COLOUR_BLACK]) != 0) {
			    	evalInfo.attackCount[Figures.COLOUR_BLACK]++;
			    	evalInfo.attackWeight[Figures.COLOUR_BLACK] += QueenAttackWeight;
			    	
			    	long bb = bb_moves & evalInfo.attackedBy[Constants.PID_W_KING];
			    	if (bb != 0) {
			    		evalInfo.attacked[Figures.COLOUR_BLACK] += Utils.countBits_less1s(bb);
			    	}
			    }
			    
			    // Mobility
			    long attacks = bb_moves & ~evalInfo.bb_all_b_pieces;
			    int mob = Utils.countBits(attacks);
			    eval_o -= MOBILITY_QUEEN_O[mob];
			    eval_e -= MOBILITY_QUEEN_E[mob];
			}
		}
		
		evalInfo.eval_Mobility_o += eval_o;
		evalInfo.eval_Mobility_e += eval_e;
	}
	

	private void initEvalInfo3() {
		
		evalInfo.attackedByWhite = evalInfo.attackedBy[Constants.PID_W_PAWN]
		                           | evalInfo.attackedBy[Constants.PID_W_KNIGHT]
		                           | evalInfo.attackedBy[Constants.PID_W_BISHOP]
		                           | evalInfo.attackedBy[Constants.PID_W_ROOK]
		                           | evalInfo.attackedBy[Constants.PID_W_QUEEN]
		                           | evalInfo.attackedBy[Constants.PID_W_KING];
		
		evalInfo.attackedByBlack = evalInfo.attackedBy[Constants.PID_B_PAWN]
		                           | evalInfo.attackedBy[Constants.PID_B_KNIGHT]
		                           | evalInfo.attackedBy[Constants.PID_B_BISHOP]
		                           | evalInfo.attackedBy[Constants.PID_B_ROOK]
		                           | evalInfo.attackedBy[Constants.PID_B_QUEEN]
		                           | evalInfo.attackedBy[Constants.PID_B_KING];
		
		evalInfo.bb_attackedByBlackOnly = evalInfo.attackedByBlack & ~evalInfo.attackedByWhite;
		evalInfo.bb_unsafe_for_w_minors = evalInfo.attackedBy[Constants.PID_B_PAWN] | evalInfo.bb_attackedByBlackOnly;
		evalInfo.bb_unsafe_for_w_rooks = evalInfo.bb_unsafe_for_w_minors | evalInfo.attackedBy[Constants.PID_B_KNIGHT] | evalInfo.attackedBy[Constants.PID_B_BISHOP];
		evalInfo.bb_unsafe_for_w_queens = evalInfo.bb_unsafe_for_w_rooks | evalInfo.attackedBy[Constants.PID_B_ROOK];
		
		evalInfo.bb_attackedByWhiteOnly = evalInfo.attackedByWhite & ~evalInfo.attackedByBlack;
		evalInfo.bb_unsafe_for_b_minors = evalInfo.attackedBy[Constants.PID_W_PAWN] | evalInfo.bb_attackedByWhiteOnly;
		evalInfo.bb_unsafe_for_b_rooks = evalInfo.bb_unsafe_for_b_minors | evalInfo.attackedBy[Constants.PID_W_KNIGHT] | evalInfo.attackedBy[Constants.PID_W_BISHOP];
		evalInfo.bb_unsafe_for_b_queens = evalInfo.bb_unsafe_for_b_rooks | evalInfo.attackedBy[Constants.PID_W_ROOK];
		
	}
	
	
	private void eval_king_safety() {

		/**
		 * KING SAFETY
		 */
		//int fieldID = w_king.getData()[0];
		
		int kingSafety = 0;
			     
	    // King safety.
	    if(/*b_queens.getDataSize() >= 1
	    		&&evalInfo.attackCount[Figures.COLOUR_BLACK] >= 2*/
	    		//&& p.non_pawn_material(them) >= QueenValueMidgame + RookValueMidgame
	    		evalInfo.attacked[Figures.COLOUR_BLACK] > 0
	                      ) {
	    	// Find the attacked squares around the king which has no defenders apart from the king itself:
	    	long undefended = evalInfo.attackedBy[Constants.PID_W_KING] & evalInfo.attackedByBlack
	    		               & ~evalInfo.attackedBy[Constants.PID_W_PAWN]
	    		               & ~evalInfo.attackedBy[Constants.PID_W_KNIGHT]
  	    		               & ~evalInfo.attackedBy[Constants.PID_W_BISHOP]
  	    		               & ~evalInfo.attackedBy[Constants.PID_W_ROOK]
  	    		               & ~evalInfo.attackedBy[Constants.PID_W_QUEEN];
	    	
	        // Initialize the 'attackUnits' variable, which is used later on as an
	        // index to the SafetyTable[] array.  The initial value is based on the
	        // number and types of the attacking pieces, the number of attacked and
	        // undefended squares around the king, the square of the king, and the
	        // quality of the pawn shelter.
	        int attackUnits = Math.min((evalInfo.attackCount[Figures.COLOUR_BLACK] * evalInfo.attackWeight[Figures.COLOUR_BLACK]) / 2, 50)
	          				+ (evalInfo.attacked[Figures.COLOUR_BLACK] + Utils.countBits_less1s(undefended)) * 3
	        				//+ PSTConstants.KING_DANGER[w_king.getData()[0]]
	        				- evalInfo.w_gards;
	        
	        if(attackUnits < 0) attackUnits = 0;
	        if(attackUnits >= 100) attackUnits = 99;
	        
	        //if (attackUnits > max_attackUnits) {
	        //	max_attackUnits = attackUnits;
	        //}
	        
	        kingSafety += KING_SAFETY[attackUnits];
	    }
	    
	    
	    //int fieldID = b_king.getData()[0];
		//long bb_field = Fields.ALL_A1H1[fieldID];
	    
	    // King safety.
	    if(/*w_queens.getDataSize() >= 1
	    		&& evalInfo.attackCount[Figures.COLOUR_WHITE] >= 2*/
	    		//&& p.non_pawn_material(them) >= QueenValueMidgame + RookValueMidgame
	    		evalInfo.attacked[Figures.COLOUR_WHITE] > 0
	                      ) {
	    	// Find the attacked squares around the king which has no defenders apart from the king itself:
	    	long undefended = evalInfo.attackedBy[Constants.PID_B_KING] & evalInfo.attackedByWhite
	    		               & ~evalInfo.attackedBy[Constants.PID_B_PAWN]
	    		               & ~evalInfo.attackedBy[Constants.PID_B_KNIGHT]
  	    		               & ~evalInfo.attackedBy[Constants.PID_B_BISHOP]
  	    		               & ~evalInfo.attackedBy[Constants.PID_B_ROOK]
  	    		               & ~evalInfo.attackedBy[Constants.PID_B_QUEEN];
	    	
	        // Initialize the 'attackUnits' variable, which is used later on as an
	        // index to the SafetyTable[] array.  The initial value is based on the
	        // number and types of the attacking pieces, the number of attacked and
	        // undefended squares around the king, the square of the king, and the
	        // quality of the pawn shelter.
	        int attackUnits = Math.min((evalInfo.attackCount[Figures.COLOUR_WHITE] * evalInfo.attackWeight[Figures.COLOUR_WHITE]) / 2, 50)
	          				+ (evalInfo.attacked[Figures.COLOUR_WHITE] + Utils.countBits_less1s(undefended)) * 3
	        				//+ PSTConstants.KING_DANGER[HORIZONTAL_SYMMETRY[b_king.getData()[0]]]
	        				- evalInfo.b_gards;
	        
	        if(attackUnits < 0) attackUnits = 0;
	        if(attackUnits >= 100) attackUnits = 99;
	        
	        //if (attackUnits > max_attackUnits) {
	        //	max_attackUnits = attackUnits;
	        //}
	        
	        //evalInfo.eval_Kingsafety += SafetyTable[attackUnits];
	        kingSafety -= KING_SAFETY[attackUnits];
	    }
	    
	    evalInfo.eval_Kingsafety_o += kingSafety;
	    evalInfo.eval_Kingsafety_e += kingSafety;
	}
	
	
	private void eval_space() {
		
		int w_space = 0;
		
		int w_spaceWeight = w_knights.getDataSize() + w_bishops.getDataSize(); 
		if (w_spaceWeight > 0) {
			
			w_spaceWeight *= w_spaceWeight;
			
			long w_safe = Fields.SPACE_WHITE
							& ~evalInfo.bb_w_pawns
							& ~evalInfo.attackedBy[Constants.PID_B_PAWN]
							& (evalInfo.attackedByWhite | ~evalInfo.attackedByBlack);
			w_space += Utils.countBits_less1s(w_safe);
			
			
			// Find all squares which are at most three squares behind some friendly pawn.
			long w_safe_behindFriendlyPawns = evalInfo.bb_w_pawns;
			w_safe_behindFriendlyPawns |= w_safe_behindFriendlyPawns << 8;
			w_safe_behindFriendlyPawns |= w_safe_behindFriendlyPawns << 16;
			w_space += Utils.countBits_less1s(w_safe & w_safe_behindFriendlyPawns);
			
			w_space = w_spaceWeight * w_space;
			
		}
		
		int b_space = 0;
		
		int b_spaceWeight = b_knights.getDataSize() + b_bishops.getDataSize();
		if (b_spaceWeight > 0) {
			
			b_spaceWeight *= b_spaceWeight;
			
			long b_safe = Fields.SPACE_BLACK
							& ~evalInfo.bb_b_pawns
							& ~evalInfo.attackedBy[Constants.PID_W_PAWN]
							& (evalInfo.attackedByBlack | ~evalInfo.attackedByWhite);
			b_space += Utils.countBits_less1s(b_safe);
			
			// Find all squares which are at most three squares behind some friendly pawn.
			long b_safe_behindFriendlyPawns = evalInfo.bb_b_pawns;
			b_safe_behindFriendlyPawns |= b_safe_behindFriendlyPawns >> 8;
			b_safe_behindFriendlyPawns |= b_safe_behindFriendlyPawns >> 16;
			b_space += Utils.countBits_less1s(b_safe & b_safe_behindFriendlyPawns);
			
			b_space = b_spaceWeight * b_space;
		}
		
		int space = w_space - b_space;
		
		evalInfo.eval_Space_o += space * SPACE_O;
		evalInfo.eval_Space_e += space * SPACE_E;
	}
	
	
	private void eval_hunged() {
		
		int eval_o = 0;
		int eval_e = 0;
		
		
		long bb_w_hunged = 0;
		
		//White pawns
		long w_hunged_pawns = evalInfo.bb_w_pawns & evalInfo.bb_attackedByBlackOnly;
		if (w_hunged_pawns != 0) {
			bb_w_hunged |= w_hunged_pawns;
		}
		
		//White minors
		long w_hunged_minor = (evalInfo.bb_w_knights | evalInfo.bb_w_bishops) & evalInfo.bb_unsafe_for_w_minors;
		if (w_hunged_minor != 0) {
			bb_w_hunged |= w_hunged_minor;
		}
		
		//White rooks
		long w_hunged_rooks = evalInfo.bb_w_rooks & evalInfo.bb_unsafe_for_w_rooks;
		if (w_hunged_rooks != 0) {
			bb_w_hunged |= w_hunged_rooks;
		}
		
		//White queens
		long w_hunged_queens = evalInfo.bb_w_queens & evalInfo.bb_unsafe_for_w_queens;
		if (w_hunged_queens != 0) {
			bb_w_hunged |= w_hunged_queens;
		}
		
		
		long bb_b_hunged = 0;
		
		//Black pawns
		long b_hunged_pawns = evalInfo.bb_b_pawns & evalInfo.bb_attackedByWhiteOnly;
		if (b_hunged_pawns != 0) {
			bb_b_hunged |= b_hunged_pawns;
		}
		
		//Black minors
		long b_hunged_minor = (evalInfo.bb_b_knights | evalInfo.bb_b_bishops) & evalInfo.bb_unsafe_for_b_minors;
		if (b_hunged_minor != 0) {
			bb_b_hunged |= b_hunged_minor;
		}
		
		//Black rooks
		long b_hunged_rooks = evalInfo.bb_b_rooks & evalInfo.bb_unsafe_for_b_rooks;
		if (b_hunged_rooks != 0) {
			bb_b_hunged |= b_hunged_rooks;
		}
		
		//Black queens
		long b_hunged_queens = evalInfo.bb_b_queens & evalInfo.bb_unsafe_for_b_queens;
		if (b_hunged_queens != 0) {
			bb_b_hunged |= b_hunged_queens;
		}
		
		
		int w_hungedCount = Utils.countBits_less1s(bb_w_hunged);
		int b_hungedCount = Utils.countBits_less1s(bb_b_hunged);
		
		
		if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
			eval_o += HUNGED_O[w_hungedCount];
			eval_e += HUNGED_E[w_hungedCount];
		} else {
			eval_o -= HUNGED_O[b_hungedCount];
			eval_e -= HUNGED_E[b_hungedCount];
		}
		
		evalInfo.eval_Hunged_o += eval_o;
		evalInfo.eval_Hunged_e += eval_e;
	}
	
	
	public void eval_TrapsAndSafeMobility() {
		
		int eval_o = 0;
		int eval_e = 0;
		
		int trapped_all = 0;
		
		int w_knights_count = w_knights.getDataSize();
		if (w_knights_count > 0) {
			int[] w_knights_fields = w_knights.getData();
			for (int i=0; i<w_knights_count; i++) {
				int fieldID = w_knights_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
				
			    int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_w_minors);
			    eval_o += MOBILITY_KNIGHT_S_O[mob_safe];
			    eval_e += MOBILITY_KNIGHT_S_E[mob_safe];
			    
			    int rank = Fields.DIGITS[fieldID];
			    int trapped = getTrappedScores(mob_safe, 3);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		int w_bishops_count = w_bishops.getDataSize();
		if (w_bishops_count > 0) {
			int[] w_bishops_fields = w_bishops.getData();
			for (int i=0; i<w_bishops_count; i++) {
				int fieldID = w_bishops_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
				
				int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_w_minors);
				eval_o += MOBILITY_BISHOP_S_O[mob_safe];
				eval_e += MOBILITY_BISHOP_S_E[mob_safe];
			    
			    int rank = Fields.DIGITS[fieldID];
			    int trapped = getTrappedScores(mob_safe, 3);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		int w_rooks_count = w_rooks.getDataSize();
		if (w_rooks_count > 0) {
			int[] w_rooks_fields = w_rooks.getData();
			for (int i=0; i<w_rooks_count; i++) {
				int fieldID = w_rooks_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
				
				int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_w_rooks);
				eval_o += MOBILITY_ROOK_S_O[mob_safe];
				eval_e += MOBILITY_ROOK_S_E[mob_safe];
				
			    int rank = Fields.DIGITS[fieldID];
			    int trapped = getTrappedScores(mob_safe, 5);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		int w_queens_count = w_queens.getDataSize();
		if (w_queens_count > 0) {
			int[] w_queens_fields = w_queens.getData();
			for (int i=0; i<w_queens_count; i++) {
				int fieldID = w_queens_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
				
				int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_w_queens);
				eval_o += MOBILITY_QUEEN_S_O[mob_safe];
				eval_e += MOBILITY_QUEEN_S_E[mob_safe];
				
			    int rank = Fields.DIGITS[fieldID];
			    int trapped = getTrappedScores(mob_safe, 9);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		
		/**
		 * BLACK
		 */
		
		int b_knights_count = b_knights.getDataSize();
		if (b_knights_count > 0) {
			int[] b_knights_fields = b_knights.getData();
			for (int i=0; i<b_knights_count; i++) {
				int fieldID = b_knights_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
				
			    int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_b_minors);			    
			    eval_o -= MOBILITY_KNIGHT_S_O[mob_safe];
			    eval_e -= MOBILITY_KNIGHT_S_E[mob_safe];
			    
			    int rank = 7 - Fields.DIGITS[fieldID];
			    int trapped = -getTrappedScores(mob_safe, 3);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		int b_bishops_count = b_bishops.getDataSize();
		if (b_bishops_count > 0) {
			int[] b_bishops_fields = b_bishops.getData();
			for (int i=0; i<b_bishops_count; i++) {
				int fieldID = b_bishops_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
			    
				int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_b_minors);
				eval_o -= MOBILITY_BISHOP_S_O[mob_safe];
				eval_e -= MOBILITY_BISHOP_S_E[mob_safe];
				
				int rank = 7 - Fields.DIGITS[fieldID];
			    int trapped = -getTrappedScores(mob_safe, 3);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		int b_rooks_count = b_rooks.getDataSize();
		if (b_rooks_count > 0) {
			int[] b_rooks_fields = b_rooks.getData();
			for (int i=0; i<b_rooks_count; i++) {
				int fieldID = b_rooks_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
				
				int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_b_rooks);
				eval_o -= MOBILITY_ROOK_S_O[mob_safe];
				eval_e -= MOBILITY_ROOK_S_E[mob_safe];
				
				int rank = 7 - Fields.DIGITS[fieldID];
			    int trapped = -getTrappedScores(mob_safe, 5);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		int b_queens_count = b_queens.getDataSize();
		if (b_queens_count > 0) {
			int[] b_queens_fields = b_queens.getData();
			for (int i=0; i<b_queens_count; i++) {
				int fieldID = b_queens_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
				
				int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_b_queens);
				eval_o -= MOBILITY_QUEEN_S_O[mob_safe];
				eval_e -= MOBILITY_QUEEN_S_E[mob_safe];

				
				int rank = 7 - Fields.DIGITS[fieldID];
			    int trapped = -getTrappedScores(mob_safe, 9);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		
		trapped_all /= 2;
		evalInfo.eval_Trapped_o += trapped_all * TRAPED_O;
		evalInfo.eval_Trapped_e += trapped_all * TRAPED_E;
		
		evalInfo.eval_Mobility_Safe_o += eval_o;
		evalInfo.eval_Mobility_Safe_e += eval_e;
	}
	
	
	private void eval_PassersFrontAttacks() {
		
		int eval_o = 0;
		int eval_e = 0;
		
		bitboard.getPawnsCache().lock();
		
		PawnsModelEval pawnsModelEval = bitboard.getPawnsStructure();
		PawnsModel pmodel = pawnsModelEval.getModel();
		
		int w_passed_pawns_count = pmodel.getWPassedCount();
		if (w_passed_pawns_count > 0) {
			Pawn[] w_passed_pawns = pmodel.getWPassed();
			for (int i=0; i<w_passed_pawns_count; i++) {
				Pawn p = w_passed_pawns[i];
				long stoppers = p.getFront() & evalInfo.bb_all;
				if (stoppers != 0) {
					long front = p.getFront();
					int unsafeFieldsInFront = Utils.countBits_less1s(front & evalInfo.bb_attackedByBlackOnly);
					eval_o -= (unsafeFieldsInFront * PAWNS_PASSED_O[p.getRank()]) / 8;
					eval_e -= (unsafeFieldsInFront * PAWNS_PASSED_E[p.getRank()]) / 8;
				}
			}
		}
		int b_passed_pawns_count = pmodel.getBPassedCount();
		if (b_passed_pawns_count > 0) {
			Pawn[] b_passed_pawns = pmodel.getBPassed();
			for (int i=0; i<b_passed_pawns_count; i++) {
				Pawn p = b_passed_pawns[i];
				long stoppers = p.getFront() & evalInfo.bb_all;
				if (stoppers != 0) {
					long front = p.getFront();
					int unsafeFieldsInFront = Utils.countBits_less1s(front & evalInfo.bb_attackedByWhiteOnly);
					eval_o += (unsafeFieldsInFront * PAWNS_PASSED_O[p.getRank()]) / 8;
					eval_e += (unsafeFieldsInFront * PAWNS_PASSED_E[p.getRank()]) / 8;
				}
			}
		}
		
		bitboard.getPawnsCache().unlock();
		
		evalInfo.eval_PawnsPassedStoppers_a_o += eval_o;
		evalInfo.eval_PawnsPassedStoppers_a_e += eval_e;
	}

	
	private int getTrappedScores(int mob_safe, int piece_weight) {
		if (mob_safe == 0) { 
			return 4 * piece_weight;
		} else if (mob_safe == 1) {
			return 2 * piece_weight;
		} else if (mob_safe == 2) {
			return 1 * piece_weight;
		} else {
			return 0;
		}
	}
	
	
	private int castling(int colour) {
		int result = 0;
		if (bitboard.getCastlingType(colour) != CastlingType.NONE) {
			result += 3;
		} else {
			if (bitboard.hasRightsToKingCastle(colour)) {
				result += 1;
			}
			if (bitboard.hasRightsToQueenCastle(colour)) {
				result += 1;
			}
		}
		return result;
	}
	
	private int fianchetto() {
		int fianchetto = 0;
		
		long w_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		long b_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
		long w_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
		long b_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);
		long w_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KING);
		long b_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KING);

		
		long w_fianchetto_pawns = Fields.G3 | Fields.F2 | Fields.H2;
		if ((w_king & Fields.G1) != 0) {
			if ((w_bishops & Fields.G2) != 0) {
				if ((w_pawns & w_fianchetto_pawns) == w_fianchetto_pawns) {
					fianchetto++;
				}
			}
		}
		
		long b_fianchetto_pawns = Fields.G6 | Fields.F7 | Fields.H7;
		if ((b_king & Fields.G8) != 0) {
			if ((b_bishops & Fields.G7) != 0) {
				if ((b_pawns & b_fianchetto_pawns) == b_fianchetto_pawns) {
					fianchetto--;
				}
			}
		}
		
		return fianchetto;
	}
	
	protected int eval_patterns() {
		
		int minor_trap = 0;
		int blocked_pawns = 0;
		
		long w_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
		long b_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);
		long w_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT);
		long b_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT);
		long w_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		long b_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
		
		
		/**
		 * trapedBishopsA7H7
		 */
		if (w_bishops != 0) {
			if ((w_bishops & Fields.A7) != 0) {
				if  ((b_pawns & Fields.B6) != 0) {
					minor_trap++;
				}
			}
			
			if ((w_bishops & Fields.H7) != 0) {
				if  ((b_pawns & Fields.G6) != 0) {
					minor_trap++;
				}
			}
		}
		
		if (b_bishops != 0) {
			if ((b_bishops & Fields.A2) != 0) {
				if  ((w_pawns & Fields.B3) != 0) {
					minor_trap--;
				}
			}
			
			if ((b_bishops & Fields.H2) != 0) {
				if  ((w_pawns & Fields.G3) != 0) {
					minor_trap--;
				}
			}
		}
		
		
		/**
		 * trapedBishopsA6H6
		 */
		if (w_bishops != 0) {
			if ((w_bishops & Fields.A6) != 0) {
				if  ((b_pawns & Fields.B5) != 0) {
					minor_trap++;
				}
			}
			
			if ((w_bishops & Fields.H6) != 0) {
				if  ((b_pawns & Fields.G5) != 0) {
					minor_trap++;
				}
			}
		}
		
		if (b_bishops != 0) {
			if ((b_bishops & Fields.A3) != 0) {
				if  ((w_pawns & Fields.B4) != 0) {
					minor_trap--;
				}
			}
			
			if ((b_bishops & Fields.H3) != 0) {
				if  ((w_pawns & Fields.G4) != 0) {
					minor_trap--;
				}
			}
		}
		
		/**
		 * trapedKnightsA1H1
		 */
		if (w_knights != 0) {
			if ((w_knights & Fields.A8) != 0) {
				if  ((b_pawns & Fields.A7) != 0) {
					minor_trap++;
				}
			}
			
			if ((w_knights & Fields.H8) != 0) {
				if  ((b_pawns & Fields.H7) != 0) {
					minor_trap++;
				}
			}
		}
		
		if (b_knights != 0) {
			if ((b_knights & Fields.A1) != 0) {
				if  ((w_pawns & Fields.A2) != 0) {
					minor_trap--;
				}
			}
			
			if ((b_knights & Fields.H1) != 0) {
				if  ((w_pawns & Fields.H2) != 0) {
					minor_trap--;
				}
			}
		}
		
		/**
		 * blockedPawnsOnD2E2
		 */
		if ((w_pawns & Fields.E2) != 0) {
			if  ((w_bishops & Fields.E3) != 0) {
				blocked_pawns++;
			}
		}
		
		if ((w_pawns & Fields.D2) != 0) {
			if  ((w_bishops & Fields.D3) != 0) {
				blocked_pawns++;
			}
		}
		
		if ((b_pawns & Fields.E7) != 0) {
			if  ((b_bishops & Fields.E6) != 0) {
				blocked_pawns--;
			}
		}
		
		if ((b_pawns & Fields.D7) != 0) {
			if  ((b_bishops & Fields.D6) != 0) {
				blocked_pawns--;
			}
		}
		
		int eval = 0;
		
		eval += STANDARD_TRAP_BISHOP * minor_trap;
		eval += STANDARD_BLOCKED_PAWN * blocked_pawns;
		
		return eval;
	}
	
	private long bishopAttacks(int fieldID, long blockers) {
		
		long attacks = 0;
		
		final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
		final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
		
		final int size = validDirIDs.length;
		for (int dir=0; dir<size; dir++) {
			
			int dirID = validDirIDs[dir];
			long[] dirBitboards = dirs[dirID];
			
			for (int seq=0; seq<dirBitboards.length; seq++) {
				long toBitboard = dirs[dirID][seq];
				attacks |= toBitboard;
				if ((toBitboard & blockers) != 0L) {
					break;
				}
			}
		}
		
		return attacks;
	}
	
	
	private long rookAttacks(int fieldID, long blockers) {
		
		long attacks = 0;
		
		final long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
		final int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
		
		final int size = validDirIDs.length;
		for (int dir=0; dir<size; dir++) {
			
			int dirID = validDirIDs[dir];
			long[] dirBitboards = dirs[dirID];
			
			for (int seq=0; seq<dirBitboards.length; seq++) {
				long toBitboard = dirs[dirID][seq];
				attacks |= toBitboard;
				if ((toBitboard & blockers) != 0L) {
					break;
				}
			}
		}
		
		return attacks;
	}
}

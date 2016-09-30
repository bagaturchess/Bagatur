package bagaturchess.engines.bagatur.eval_v15;


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
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.engines.bagatur.eval.BagaturPawnsEval;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.internal.EvaluatorAdapter;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.impl.evalcache.IEvalCache;
import bagaturchess.search.impl.evalcache.IEvalEntry;


public class Eval_V15 extends EvaluatorAdapter implements Weights_V15 {
	
	
	private static final boolean USE_CACHE = true;
	private static final boolean USE_LAZY = true;
	
	
	private static double INT_MIN = 25;
	private static double INT1 = INT_MIN;
	private static double INT2 = INT_MIN;
	private static double INT3 = INT_MIN;
	private static double INT4 = INT_MIN;
	private static double INT_DEVIDE_FACTOR = 1;
	
	
	public static final long RANK_7TH = Fields.DIGIT_7;
	public static final long RANK_8TH = Fields.DIGIT_8;
	public static final long RANK_2TH = Fields.DIGIT_2;
	public static final long RANK_1TH = Fields.DIGIT_1;
	
	
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
	
	private EvalInfo_V15 evalInfo;
	
	private EvalConfig_V15 evalConfig;
	
	
	Eval_V15(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
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
		
		evalInfo = new EvalInfo_V15(bitboard);

		
		evalConfig = (EvalConfig_V15) _evalConfig;
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
		
		
		/**
		 * No pawns
		 */
		if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[b_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal(20 * (int) (4.7 * CMD + 1.6 * MD));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[w_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal( - 20 * (int) (4.7 * CMD + 1.6 * MD));
				
			}
		}
		
		
		int eval = 0;
		
		
		evalInfo.clear();
		
		eval_material_nopawnsdrawrule();
		eval_trading();
		eval_standard();
		eval_pawns();
		
		
		eval += interpolator.interpolateByFactor(evalInfo.eval_Material_o +
												evalInfo.eval_Standard_o +
												evalInfo.eval_PST_o +
												evalInfo.eval_PawnsStandard_o +
												evalInfo.eval_PawnsPassed_o +
												evalInfo.eval_PawnsPassedKing_o +
												evalInfo.eval_PawnsUnstoppable_o,
												
												evalInfo.eval_Material_e +
												evalInfo.eval_Standard_e +
												evalInfo.eval_PST_e +
												evalInfo.eval_PawnsStandard_e +
												evalInfo.eval_PawnsPassed_e +
												evalInfo.eval_PawnsPassedKing_e +
												evalInfo.eval_PawnsUnstoppable_e);
		
		evalInfo.clear();
		initEvalInfo1();
		eval_pawns_RooksAndQueens();
		eval += interpolator.interpolateByFactor(evalInfo.eval_PawnsPassedStoppers_o +
												evalInfo.eval_PawnsRooksQueens_o,
												
												evalInfo.eval_PawnsPassedStoppers_e +
												evalInfo.eval_PawnsRooksQueens_e);
		
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
		
		
		/**
		 * No pawns
		 */
		if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[b_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal(20 * (int) (4.7 * CMD + 1.6 * MD));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[w_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal( - 20 * (int) (4.7 * CMD + 1.6 * MD));
				
			}
		}
		
		
		int eval = 0;
		
		
		evalInfo.clear_short();
		
		eval_material_nopawnsdrawrule();
		eval_trading();
		eval_standard();
		
		
		int eval1 = interpolator.interpolateByFactor(evalInfo.eval_Material_o +
				evalInfo.eval_Standard_o +
				evalInfo.eval_PST_o,
				
				evalInfo.eval_Material_e +
				evalInfo.eval_Standard_e +
				evalInfo.eval_PST_e);
		eval += eval1;
		
		//INT1 = INT1_stat.getEntropy() + 2 * INT1_stat.getDisperse();
		
		int eval_tmp = returnVal(eval);
		if (eval_tmp + INT1 <= alpha || eval_tmp - INT1 >= beta) {
			if (USE_LAZY) {
				return eval_tmp;
			}
		}
		
		
		eval_pawns();
		
		
		int eval2 = interpolator.interpolateByFactor(evalInfo.eval_PawnsStandard_o +
												evalInfo.eval_PawnsPassed_o +
												evalInfo.eval_PawnsPassedKing_o +
												evalInfo.eval_PawnsUnstoppable_o,
												
												evalInfo.eval_PawnsStandard_e +
												evalInfo.eval_PawnsPassed_e +
												evalInfo.eval_PawnsPassedKing_e +
												evalInfo.eval_PawnsUnstoppable_e);
		eval += eval2;
		
		
		eval_tmp = returnVal(eval);
		if (eval_tmp + INT2 <= alpha || eval_tmp - INT2 >= beta) {
			if (USE_LAZY) {
				return eval_tmp;
			}
		}
		
		
		evalInfo.clear();
		
		initEvalInfo1();
		eval_pawns_RooksAndQueens();
		int eval3 = interpolator.interpolateByFactor(evalInfo.eval_PawnsPassedStoppers_o +
												evalInfo.eval_PawnsRooksQueens_o,
												
												evalInfo.eval_PawnsPassedStoppers_e +
												evalInfo.eval_PawnsRooksQueens_e);
		eval += eval3;
		
		eval_tmp = returnVal(eval);
		if (eval_tmp + INT3 <= alpha || eval_tmp - INT3 >= beta) {
			if (USE_LAZY) {
				return eval_tmp;
			}
		}
		
		
		if (eval >= ISearch.MAX_MAT_INTERVAL || eval <= -ISearch.MAX_MAT_INTERVAL) {
			throw new IllegalStateException();
		}
		
		
		double int1 = Math.abs(eval2 + eval3 /* + eval4 + eval5*/);
		double int2 = Math.abs(eval3 /* + eval4 + eval5*/);
		//double int3 = Math.abs(eval4 + eval5);
		//double int4 = Math.abs(eval5);
		
		if (int1 > INT1) {
			INT1 = int1 / INT_DEVIDE_FACTOR;
		}
		if (int2 > INT2) {
			INT2 = int2 / INT_DEVIDE_FACTOR;
		}
		/*if (int3 > INT3) {
			INT3 = int3 / INT_DEVIDE_FACTOR;
		}
		if (int4 > INT4) {
			INT4 = int4 / INT_DEVIDE_FACTOR;
		}*/
		
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
		
		
		/**
		 * No pawns
		 */
		if (w_pawns.getDataSize() == 0 && b_pawns.getDataSize() == 0) {
			
			int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
			int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
			
			//Mop-up evaluation
			//PosEval=4.7*CMD + 1.6*(14 - MD)
			//CMD is the Center Manhattan distance of the losing king and MD the Manhattan distance between both kings.
			if (w_eval_nopawns_e > b_eval_nopawns_e) { //White can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[b_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal(20 * (int) (4.7 * CMD + 1.6 * MD));
				
			} else if (w_eval_nopawns_e < b_eval_nopawns_e) {//Black can win
				
				int CMD = Fields.CENTER_MANHATTAN_DISTANCE[w_king.getData()[0]];
				int MD = Fields.getTropismPoint(w_king.getData()[0], b_king.getData()[0]);
				
				return returnVal( - 20 * (int) (4.7 * CMD + 1.6 * MD));
				
			}
		}
		
		
		int eval = 0;
		
		
		evalInfo.clear_short();
		
		eval_material_nopawnsdrawrule();
		eval_trading();
		eval_standard();
		eval_pawns();
		
		
		eval += interpolator.interpolateByFactor(evalInfo.eval_Material_o +
												evalInfo.eval_Standard_o +
												evalInfo.eval_PST_o +
												evalInfo.eval_PawnsStandard_o +
												evalInfo.eval_PawnsPassed_o +
												evalInfo.eval_PawnsPassedKing_o +
												evalInfo.eval_PawnsUnstoppable_o,
												
												evalInfo.eval_Material_e +
												evalInfo.eval_Standard_e +
												evalInfo.eval_PST_e +
												evalInfo.eval_PawnsStandard_e +
												evalInfo.eval_PawnsPassed_e +
												evalInfo.eval_PawnsPassedKing_e +
												evalInfo.eval_PawnsUnstoppable_e);
		
		evalInfo.clear();
		initEvalInfo1();
		eval_pawns_RooksAndQueens();
		eval += interpolator.interpolateByFactor(evalInfo.eval_PawnsPassedStoppers_o +
												evalInfo.eval_PawnsRooksQueens_o,
												
												evalInfo.eval_PawnsPassedStoppers_e +
												evalInfo.eval_PawnsRooksQueens_e);
		
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
		 * Differently colored bishops, no other pieces except pawns
		 */
		if (w_bishops.getDataSize() == 1
				&& b_bishops.getDataSize() == 1
				&& bitboard.getMaterialFactor().getWhiteFactor() == 3
				&& bitboard.getMaterialFactor().getBlackFactor() == 3) {
			
			long bb_w_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
			long bb_b_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);
			
			long w_colour = (bb_w_bishops & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			long b_colour = (bb_b_bishops & Fields.ALL_WHITE_FIELDS) != 0 ?
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
		eval_o += STANDARD_FIANCHETTO * fianchetto;
		
		int patterns = eval_patterns();
		eval_o += patterns;
		eval_e += patterns;
		
		int kingsDistance = Fields.getDistancePoints(w_king.getData()[0], b_king.getData()[0]);
		
		//King Opposition
		if (bitboard.getMaterialFactor().getWhiteFactor() == 0
				&& bitboard.getMaterialFactor().getBlackFactor() == 0) {
			
			if (kingsDistance == 2) {
				
				long bb_w_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KING);
				long bb_b_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KING);

				boolean kingsOnSameColourSquares = ((Fields.ALL_WHITE_FIELDS & bb_w_king) != 0 &&  (Fields.ALL_WHITE_FIELDS & bb_b_king) != 0)
						|| ((Fields.ALL_BLACK_FIELDS & bb_w_king) != 0 &&  (Fields.ALL_BLACK_FIELDS & bb_b_king) != 0);
				
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
		
		evalInfo.eval_PST_o += baseEval.getPST_o();
		evalInfo.eval_PST_e += baseEval.getPST_e();

		evalInfo.eval_Standard_o += eval_o;
		evalInfo.eval_Standard_e += eval_e;
	}
	
	
	public void eval_pawns() {
		
		bitboard.getPawnsCache().lock();
		PawnsModelEval pawnsModelEval = bitboard.getPawnsStructure();
		
		evalInfo.eval_PawnsStandard_o += ((PawnsEval_V15)pawnsModelEval).getStandardEval_o();
		evalInfo.eval_PawnsStandard_e += ((PawnsEval_V15)pawnsModelEval).getStandardEval_e();
		
		evalInfo.eval_PawnsPassed_o += ((PawnsEval_V15)pawnsModelEval).getPassersEval_o();
		evalInfo.eval_PawnsPassed_e += ((PawnsEval_V15)pawnsModelEval).getPassersEval_e();
		
		evalInfo.eval_PawnsPassedKing_o += ((PawnsEval_V15)pawnsModelEval).getPassersKingEval_o();
		evalInfo.eval_PawnsPassedKing_e += ((PawnsEval_V15)pawnsModelEval).getPassersKingEval_e();
		
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
	
	
	private void eval_pawns_RooksAndQueens() {
		
		
		bitboard.getPawnsCache().lock();
		PawnsModelEval pawnsModelEval = bitboard.getPawnsStructure();
		PawnsModel pmodel = pawnsModelEval.getModel();
		
		long bb_wpawns_attacks = pmodel.getWattacks();
		long bb_bpawns_attacks = pmodel.getBattacks();
		evalInfo.bb_wpawns_attacks = bb_wpawns_attacks;
		evalInfo.bb_bpawns_attacks = bb_bpawns_attacks;
		evalInfo.w_kingOpened = pmodel.getWKingOpenedFiles() + pmodel.getWKingSemiOpOpenedFiles() + pmodel.getWKingSemiOwnOpenedFiles();
		evalInfo.b_kingOpened = pmodel.getBKingOpenedFiles() + pmodel.getBKingSemiOpOpenedFiles() + pmodel.getBKingSemiOwnOpenedFiles();
		//evalInfo.w_gards = ((BagaturPawnsEval)pawnsModelEval).getWGardsScores();
		//evalInfo.b_gards = ((BagaturPawnsEval)pawnsModelEval).getBGardsScores();
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
	    evalInfo.eval_PawnsRooksQueens_o += kingOpened * PAWNS_KING_OPENED_O;
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

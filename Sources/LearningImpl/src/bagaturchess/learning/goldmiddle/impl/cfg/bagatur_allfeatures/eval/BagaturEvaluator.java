package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
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
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler.Bagatur_ALL_SignalFillerConstants;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator extends BaseEvaluator implements FeatureWeights {
	
	
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
	
	private EvalInfo evalInfo;
	
	// Attack weights for each piece type.
	public static final int QueenAttackWeight	 		= 5;
	public static final int RookAttackWeight 			= 3;
	public static final int BishopAttackWeight 			= 2;
	public static final int KnightAttackWeight	 		= 2;
	
	
	BagaturEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
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
		
		evalInfo = new EvalInfo(bitboard);
	}
	
	
	@Override
	protected int phase1() {
		int eval = 0;
		
		evalInfo.clear_short();
		
		eval_material_nopawnsdrawrule();
		eval_PST();
		eval += interpolator.interpolateByFactor(evalInfo.eval_Material_o +
												evalInfo.eval_PST_o,
												
												evalInfo.eval_Material_e +
												evalInfo.eval_PST_e);
		return eval;
	}
	
	
	@Override
	protected int phase2() {

		int eval = 0;
		
		eval_pawns();
		eval += interpolator.interpolateByFactor(
												evalInfo.eval_PawnsStandard_o +
												evalInfo.eval_PawnsPassed_o +
												evalInfo.eval_PawnsPassedKing_o +
												evalInfo.eval_PawnsUnstoppable_o,
												
												evalInfo.eval_PawnsStandard_e +
												evalInfo.eval_PawnsPassed_e +
												evalInfo.eval_PawnsPassedKing_e +
												evalInfo.eval_PawnsUnstoppable_e);
			
		return eval;
	}
	
	
	@Override
	protected int phase3() {
		
		int eval = 0;
				
		evalInfo.clear_rest();
		
		initEvalInfo1();
		
		eval_standard();
		eval += interpolator.interpolateByFactor(evalInfo.eval_Standard_o,
												evalInfo.eval_Standard_e);
		
		eval_pawns_PassedStoppers_RooksAndQueens();
		eval += interpolator.interpolateByFactor(evalInfo.eval_PawnsPassedStoppers_o +
												evalInfo.eval_PawnsRooksQueens_o,
												
												evalInfo.eval_PawnsPassedStoppers_e +
												evalInfo.eval_PawnsRooksQueens_e);
		
		return eval;
	}
	
	
	@Override
	protected int phase4() {
		
		int eval = 0;
		
		initEvalInfo2();
		eval_mobility();
		eval += interpolator.interpolateByFactor(evalInfo.eval_Mobility_o,
												
												evalInfo.eval_Mobility_e);
		
		initEvalInfo3();
		eval_king_safety();
		eval_space();
		eval_hunged();
		eval += interpolator.interpolateByFactor(evalInfo.eval_Kingsafety_o +
				evalInfo.eval_Space_o +
				evalInfo.eval_Hunged_o,

				evalInfo.eval_Kingsafety_e +
				evalInfo.eval_Space_e +
				evalInfo.eval_Hunged_e);
		
		return eval;
	}
	
	
	@Override
	protected int phase5() {
		
		int eval = 0;
		
		eval_TrapsAndSafeMobility();
		eval_PassersFrontAttacks();
		eval += interpolator.interpolateByFactor(evalInfo.eval_Trapped_o +
												evalInfo.eval_Mobility_Safe_o +
												evalInfo.eval_PawnsPassedStoppers_a_o,
												
												evalInfo.eval_Trapped_e +
												evalInfo.eval_Mobility_Safe_e +
												evalInfo.eval_PawnsPassedStoppers_a_e);
		
		return eval;
	}
	
	
	public String dump(int rootColour) { 
		String msg = "";
		
		int eval = (int) fullEval(0, 0, 0, rootColour);
		msg += evalInfo;
		msg += eval;
		
		return msg;
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
			
			w_double_bishops_o += MATERIAL_DOUBLE_BISHOPS_O;
			w_double_bishops_e += MATERIAL_DOUBLE_BISHOPS_E;
		}
		
		if (b_bishops.getDataSize() >= 2) {
			
			b_double_bishops_o += MATERIAL_DOUBLE_BISHOPS_O;
			b_double_bishops_e += MATERIAL_DOUBLE_BISHOPS_E;
		}
		
		evalInfo.eval_Material_o += (w_eval_nopawns_o - b_eval_nopawns_o) + (w_eval_pawns_o - b_eval_pawns_o) + (w_double_bishops_o - b_double_bishops_o);
		evalInfo.eval_Material_e += (w_eval_nopawns_e - b_eval_nopawns_e) + (w_eval_pawns_e - b_eval_pawns_e) + (w_double_bishops_e - b_double_bishops_e);
		
		return interpolator.interpolateByFactor(evalInfo.eval_Material_o, evalInfo.eval_Material_e);

	}
	
	
	private void eval_PST() {
		evalInfo.eval_PST_o += PST_O * baseEval.getPST_o();
		evalInfo.eval_PST_e += PST_E * baseEval.getPST_e();
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
		eval_o += STANDARD_FIANCHETTO_O * fianchetto;
		
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
		
		int unstoppablePasser = bitboard.getUnstoppablePasser();
		evalInfo.eval_PawnsUnstoppable_o += unstoppablePasser * PASSED_UNSTOPPABLE_O;
		evalInfo.eval_PawnsUnstoppable_e += unstoppablePasser * PASSED_UNSTOPPABLE_E;
		
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
	
	public void eval_pawns_PassedStoppers_RooksAndQueens() {
		
		
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
					evalInfo.eval_PawnsPassedStoppers_o -= PAWNS_PASSED_STOPPERS_O * (stoppersCount * Bagatur_ALL_SignalFillerConstants.PAWNS_PASSED_O[p.getRank()]) / 4;
					evalInfo.eval_PawnsPassedStoppers_e -= PAWNS_PASSED_STOPPERS_E * (stoppersCount * Bagatur_ALL_SignalFillerConstants.PAWNS_PASSED_E[p.getRank()]) / 4;
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
					evalInfo.eval_PawnsPassedStoppers_o += PAWNS_PASSED_STOPPERS_O * (stoppersCount * Bagatur_ALL_SignalFillerConstants.PAWNS_PASSED_O[p.getRank()]) / 4;
					evalInfo.eval_PawnsPassedStoppers_e += PAWNS_PASSED_STOPPERS_E * (stoppersCount * Bagatur_ALL_SignalFillerConstants.PAWNS_PASSED_E[p.getRank()]) / 4;
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
				if ((bb_field & Fields.DIGIT_7) != 0L) {
					//If there are pawns on 7th rank or king on 8th rank
					if ((evalInfo.bb_b_pawns & Fields.DIGIT_7) != 0L || (evalInfo.bb_b_king & Fields.DIGIT_8) != 0L) {
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
				if ((bb_field & Fields.DIGIT_2) != 0L) {
					//If there are pawns on 2th rank or king on 1th rank
					if ((evalInfo.bb_w_pawns & Fields.DIGIT_2) != 0L || (evalInfo.bb_w_king & Fields.DIGIT_1) != 0L) {
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
				if ((bb_field & Fields.DIGIT_7) != 0L) {
					//If there are pawns on 7th rank or king on 8th rank
					if ((evalInfo.bb_b_pawns & Fields.DIGIT_7) != 0L || (evalInfo.bb_b_king & Fields.DIGIT_8) != 0L) {
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
				if ((bb_field & Fields.DIGIT_2) != 0L) {
					//If there are pawns on 2th rank or king on 1th rank
					if ((evalInfo.bb_w_pawns & Fields.DIGIT_2) != 0L || (evalInfo.bb_w_king & Fields.DIGIT_1) != 0L) {
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
			    eval_o += MOBILITY_KNIGHT_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_KNIGHT_O[mob];
			    eval_e += MOBILITY_KNIGHT_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_KNIGHT_E[mob];
			    
			    
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
			    		
			    		eval_o += scores * KNIGHT_OUTPOST_O * Bagatur_ALL_SignalFillerConstants.KNIGHT_OUTPOST_O[fieldID];
					    eval_e += scores * KNIGHT_OUTPOST_E * Bagatur_ALL_SignalFillerConstants.KNIGHT_OUTPOST_E[fieldID];
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
			    eval_o -= MOBILITY_KNIGHT_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_KNIGHT_O[mob];
			    eval_e -= MOBILITY_KNIGHT_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_KNIGHT_E[mob];
			    
			    
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
				    		
			    		eval_o -= scores * KNIGHT_OUTPOST_O * Bagatur_ALL_SignalFillerConstants.KNIGHT_OUTPOST_O[Fields.HORIZONTAL_SYMMETRY[fieldID]];
					    eval_e -= scores * KNIGHT_OUTPOST_E * Bagatur_ALL_SignalFillerConstants.KNIGHT_OUTPOST_E[Fields.HORIZONTAL_SYMMETRY[fieldID]];
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
			    eval_o += MOBILITY_BISHOP_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_BISHOP_O[mob];
			    eval_e += MOBILITY_BISHOP_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_BISHOP_E[mob];

			    
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
			    		
			    		eval_o += scores * BISHOP_OUTPOST_O * Bagatur_ALL_SignalFillerConstants.BISHOP_OUTPOST_O[fieldID];
					    eval_e += scores * BISHOP_OUTPOST_E * Bagatur_ALL_SignalFillerConstants.BISHOP_OUTPOST_E[fieldID];
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
			    
	    		eval_o -= bad_bishop_score * BISHOP_BAD_O;
			    eval_e -= bad_bishop_score * BISHOP_BAD_E;
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
			    eval_o -= MOBILITY_BISHOP_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_BISHOP_O[mob];
			    eval_e -= MOBILITY_BISHOP_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_BISHOP_E[mob];
			    
			    
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
			    		
			    		eval_o -= scores * BISHOP_OUTPOST_O * Bagatur_ALL_SignalFillerConstants.BISHOP_OUTPOST_O[Fields.HORIZONTAL_SYMMETRY[fieldID]];
					    eval_e -= scores * BISHOP_OUTPOST_E * Bagatur_ALL_SignalFillerConstants.BISHOP_OUTPOST_E[Fields.HORIZONTAL_SYMMETRY[fieldID]];
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
			    
	    		eval_o += bad_bishop_score * BISHOP_BAD_O;
			    eval_e += bad_bishop_score * BISHOP_BAD_E;
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
			    eval_o += MOBILITY_ROOK_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_ROOK_O[mob];
			    eval_e += MOBILITY_ROOK_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_ROOK_E[mob];
			    
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
			    eval_o -= MOBILITY_ROOK_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_ROOK_O[mob];
			    eval_e -= MOBILITY_ROOK_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_ROOK_E[mob];
			    
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
			    eval_o += MOBILITY_QUEEN_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_QUEEN_O[mob];
			    eval_e += MOBILITY_QUEEN_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_QUEEN_E[mob];
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
			    eval_o -= MOBILITY_QUEEN_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_QUEEN_O[mob];
			    eval_e -= MOBILITY_QUEEN_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_QUEEN_E[mob];
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
	        
	        kingSafety += Bagatur_ALL_SignalFillerConstants.KING_SAFETY[attackUnits];
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
	        kingSafety -= Bagatur_ALL_SignalFillerConstants.KING_SAFETY[attackUnits];
	    }
	    
	    evalInfo.eval_Kingsafety_o += KING_SAFETY_O * kingSafety;
	    evalInfo.eval_Kingsafety_e += KING_SAFETY_E * kingSafety;
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
		
		
		if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
			
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
			
			int w_hungedCount = Utils.countBits_less1s(bb_w_hunged);
			
			eval_o += HUNGED_O * Bagatur_ALL_SignalFillerConstants.HUNGED_O[w_hungedCount];
			eval_e += HUNGED_E * Bagatur_ALL_SignalFillerConstants.HUNGED_E[w_hungedCount];
			
		} else {
			
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
			
			int b_hungedCount = Utils.countBits_less1s(bb_b_hunged);
			
			eval_o -= HUNGED_O * Bagatur_ALL_SignalFillerConstants.HUNGED_O[b_hungedCount];
			eval_e -= HUNGED_E * Bagatur_ALL_SignalFillerConstants.HUNGED_E[b_hungedCount];
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
			    eval_o += MOBILITY_KNIGHT_S_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_KNIGHT_S_O[mob_safe];
			    eval_e += MOBILITY_KNIGHT_S_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_KNIGHT_S_E[mob_safe];
			    
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
				eval_o += MOBILITY_BISHOP_S_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_BISHOP_S_O[mob_safe];
				eval_e += MOBILITY_BISHOP_S_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_BISHOP_S_E[mob_safe];
			    
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
				eval_o += MOBILITY_ROOK_S_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_ROOK_S_O[mob_safe];
				eval_e += MOBILITY_ROOK_S_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_ROOK_S_E[mob_safe];
				
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
				eval_o += MOBILITY_QUEEN_S_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_QUEEN_S_O[mob_safe];
				eval_e += MOBILITY_QUEEN_S_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_QUEEN_S_E[mob_safe];
				
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
			    eval_o -= MOBILITY_KNIGHT_S_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_KNIGHT_S_O[mob_safe];
			    eval_e -= MOBILITY_KNIGHT_S_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_KNIGHT_S_E[mob_safe];
			    
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
				eval_o -= MOBILITY_BISHOP_S_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_BISHOP_S_O[mob_safe];
				eval_e -= MOBILITY_BISHOP_S_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_BISHOP_S_E[mob_safe];
				
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
				eval_o -= MOBILITY_ROOK_S_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_ROOK_S_O[mob_safe];
				eval_e -= MOBILITY_ROOK_S_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_ROOK_S_E[mob_safe];
				
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
				eval_o -= MOBILITY_QUEEN_S_O * Bagatur_ALL_SignalFillerConstants.MOBILITY_QUEEN_S_O[mob_safe];
				eval_e -= MOBILITY_QUEEN_S_E * Bagatur_ALL_SignalFillerConstants.MOBILITY_QUEEN_S_E[mob_safe];

				
				int rank = 7 - Fields.DIGITS[fieldID];
			    int trapped = -getTrappedScores(mob_safe, 9);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		
		trapped_all /= 2;
		evalInfo.eval_Trapped_o -= trapped_all * TRAPED_O;
		evalInfo.eval_Trapped_e -= trapped_all * TRAPED_E;
		
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
					eval_o -= PASSERS_FRONT_ATTACKS_O * (unsafeFieldsInFront * Bagatur_ALL_SignalFillerConstants.PAWNS_PASSED_O[p.getRank()]) / 8;
					eval_e -= PASSERS_FRONT_ATTACKS_E * (unsafeFieldsInFront * Bagatur_ALL_SignalFillerConstants.PAWNS_PASSED_E[p.getRank()]) / 8;
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
					eval_o += PASSERS_FRONT_ATTACKS_O * (unsafeFieldsInFront * Bagatur_ALL_SignalFillerConstants.PAWNS_PASSED_O[p.getRank()]) / 8;
					eval_e += PASSERS_FRONT_ATTACKS_E * (unsafeFieldsInFront * Bagatur_ALL_SignalFillerConstants.PAWNS_PASSED_E[p.getRank()]) / 8;
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
		if (bitboard.getCastlingType(colour) != IBoard.CastlingType.NONE) {
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
		
		long w_fianchetto_pawns = Fields.G3 | Fields.F2 | Fields.H2;
		if ((evalInfo.bb_w_king & Fields.G1) != 0) {
			if ((evalInfo.bb_w_bishops & Fields.G2) != 0) {
				if ((evalInfo.bb_w_pawns & w_fianchetto_pawns) == w_fianchetto_pawns) {
					fianchetto++;
				}
			}
		}
		
		long b_fianchetto_pawns = Fields.G6 | Fields.F7 | Fields.H7;
		if ((evalInfo.bb_b_king & Fields.G8) != 0) {
			if ((evalInfo.bb_b_bishops & Fields.G7) != 0) {
				if ((evalInfo.bb_b_pawns & b_fianchetto_pawns) == b_fianchetto_pawns) {
					fianchetto--;
				}
			}
		}
		
		return fianchetto;
	}
	
	protected int eval_patterns() {
		
		int minor_trap = 0;
		int blocked_pawns = 0;
		
		/**
		 * trapedBishopsA7H7
		 */
		if (evalInfo.bb_w_bishops != 0) {
			if ((evalInfo.bb_w_bishops & Fields.A7) != 0) {
				if  ((evalInfo.bb_b_pawns & Fields.B6) != 0) {
					minor_trap++;
				}
			}
			
			if ((evalInfo.bb_w_bishops & Fields.H7) != 0) {
				if  ((evalInfo.bb_b_pawns & Fields.G6) != 0) {
					minor_trap++;
				}
			}
		}
		
		if (evalInfo.bb_b_bishops != 0) {
			if ((evalInfo.bb_b_bishops & Fields.A2) != 0) {
				if  ((evalInfo.bb_w_pawns & Fields.B3) != 0) {
					minor_trap--;
				}
			}
			
			if ((evalInfo.bb_b_bishops & Fields.H2) != 0) {
				if  ((evalInfo.bb_w_pawns & Fields.G3) != 0) {
					minor_trap--;
				}
			}
		}
		
		
		/**
		 * trapedBishopsA6H6
		 */
		if (evalInfo.bb_w_bishops != 0) {
			if ((evalInfo.bb_w_bishops & Fields.A6) != 0) {
				if  ((evalInfo.bb_b_pawns & Fields.B5) != 0) {
					minor_trap++;
				}
			}
			
			if ((evalInfo.bb_w_bishops & Fields.H6) != 0) {
				if  ((evalInfo.bb_b_pawns & Fields.G5) != 0) {
					minor_trap++;
				}
			}
		}
		
		if (evalInfo.bb_b_bishops != 0) {
			if ((evalInfo.bb_b_bishops & Fields.A3) != 0) {
				if  ((evalInfo.bb_w_pawns & Fields.B4) != 0) {
					minor_trap--;
				}
			}
			
			if ((evalInfo.bb_b_bishops & Fields.H3) != 0) {
				if  ((evalInfo.bb_w_pawns & Fields.G4) != 0) {
					minor_trap--;
				}
			}
		}
		
		/**
		 * trapedKnightsA1H1
		 */
		if (evalInfo.bb_w_knights != 0) {
			if ((evalInfo.bb_w_knights & Fields.A8) != 0) {
				if  ((evalInfo.bb_b_pawns & Fields.A7) != 0) {
					minor_trap++;
				}
			}
			
			if ((evalInfo.bb_w_knights & Fields.H8) != 0) {
				if  ((evalInfo.bb_b_pawns & Fields.H7) != 0) {
					minor_trap++;
				}
			}
		}
		
		if (evalInfo.bb_b_knights != 0) {
			if ((evalInfo.bb_b_knights & Fields.A1) != 0) {
				if  ((evalInfo.bb_w_pawns & Fields.A2) != 0) {
					minor_trap--;
				}
			}
			
			if ((evalInfo.bb_b_knights & Fields.H1) != 0) {
				if  ((evalInfo.bb_w_pawns & Fields.H2) != 0) {
					minor_trap--;
				}
			}
		}
		
		/**
		 * blockedPawnsOnD2E2
		 */
		if ((evalInfo.bb_w_pawns & Fields.E2) != 0) {
			if  ((evalInfo.bb_w_bishops & Fields.E3) != 0) {
				blocked_pawns++;
			}
		}
		
		if ((evalInfo.bb_w_pawns & Fields.D2) != 0) {
			if  ((evalInfo.bb_w_bishops & Fields.D3) != 0) {
				blocked_pawns++;
			}
		}
		
		if ((evalInfo.bb_b_pawns & Fields.E7) != 0) {
			if  ((evalInfo.bb_b_bishops & Fields.E6) != 0) {
				blocked_pawns--;
			}
		}
		
		if ((evalInfo.bb_b_pawns & Fields.D7) != 0) {
			if  ((evalInfo.bb_b_bishops & Fields.D6) != 0) {
				blocked_pawns--;
			}
		}
		
		int eval = 0;
		
		eval += STANDARD_TRAP_BISHOP_O * minor_trap;
		eval += STANDARD_BLOCKED_PAWN_O * blocked_pawns;
		
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

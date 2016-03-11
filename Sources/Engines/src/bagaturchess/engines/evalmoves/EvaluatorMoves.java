package bagaturchess.engines.evalmoves;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.common.CastlingType;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnStructureConstants;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingSurrounding;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;
import bagaturchess.learning.impl.eval.cfg.Weights;
import bagaturchess.learning.impl.filler.SignalFillerConstants;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.evalcache.EvalCache;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class EvaluatorMoves extends BaseEvaluator implements Weights {
	
	
	long RANK_7TH = Fields.DIGIT_7;
	long RANK_2TH = Fields.DIGIT_2;
	
	
	private IInternalMoveList w_moves_iter = new BaseMoveList();
	private IInternalMoveList b_moves_iter = new BaseMoveList();
	
	
	public EvaluatorMoves(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		super(_bitboard, _evalCache, _evalConfig);
	}
	
	
	@Override
	public void beforeSearch() {
		super.beforeSearch();
	}
	
	
	@Override
	public int getMaterialQueen() {
		return 50 + baseEval.getMaterialQueen();
	}
	
	
	@Override
	protected double phase2_opening() {
		
		double eval = 0;
		
		eval += eval_pawns();
		
		return eval;
	}
	
	
	@Override
	protected double phase3_opening() {
		double eval = 0;
		
		eval += eval_standard();
		
		//Clear PST scores and calculate them from scratch in eval_pieces
		eval -= interpolator.interpolateByFactor(baseEval.getPST_o(), baseEval.getPST_e());
		eval += eval_pieces();
		
		return eval;
	}
	
	
	@Override
	protected double phase4_opening() {
		double eval = 0;
		
		eval += eval_moves();
		
		return eval;
	}
	
	
	@Override
	protected double phase5_opening() {
		double eval = 0;
		//eval += safeMobilityTraps();
		return eval;
	}
	
	
	@Override
	protected double phase2_endgame() {
		
		double eval = 0;
		
		eval += eval_pawns();
		
		return eval;
	}
	
	
	@Override
	protected double phase3_endgame() {
		
		double eval = 0;
		
		eval += eval_standard();
		
		//Clear PST scores and calculate them from scratch in eval_pieces
		eval -= interpolator.interpolateByFactor(baseEval.getPST_o(), baseEval.getPST_e());
		eval += eval_pieces();
		
		return eval;
	}
	
	
	private double eval_standard() {
		
		double eval_o = 0;
		double eval_e = 0;
		
		/**
		 * Opening features
		 */
		int castling = castling(Figures.COLOUR_WHITE) - castling(Figures.COLOUR_BLACK);
		eval_o += KINGSAFE_CASTLING_O * castling;
		eval_e += KINGSAFE_CASTLING_E * castling;
		
		int fianchetto = fianchetto();
		eval_o += KINGSAFE_FIANCHETTO_O * fianchetto;
		eval_e += KINGSAFE_FIANCHETTO_E * fianchetto;
		
		double movedFGPawns = movedFGPawns();
		
		
		/**
		 * Mid-game and End-game features
		 */
		int double_bishop = ((w_bishops.getDataSize() >= 2) ? 1 : 0) - ((b_bishops.getDataSize() >= 2) ? 1 : 0);
		eval_o += BISHOPS_DOUBLE_O * double_bishop;
		eval_e += BISHOPS_DOUBLE_E * double_bishop;

		int double_rooks = ((w_rooks.getDataSize() >= 2) ? 1 : 0) - ((b_rooks.getDataSize() >= 2) ? 1 : 0);
		eval_o += ROOKS_DOUBLE_O * double_rooks;
		eval_e += ROOKS_DOUBLE_E * double_rooks;

		int double_knights = ((w_knights.getDataSize() >= 2) ? 1 : 0) - ((b_knights.getDataSize() >= 2) ? 1 : 0);
		eval_o += KNIGHTS_DOUBLE_O * double_knights;
		eval_e += KNIGHTS_DOUBLE_E * double_knights;
		
		
		//Kings Distance
		int kingFieldID_white = w_king.getData()[0];
		int kingFieldID_black = b_king.getData()[0];
		int kingDistance = Fields.getDistancePoints(kingFieldID_white, kingFieldID_black);
		if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
			eval_o += SignalFillerConstants.KING_DISTANCE_O[kingDistance] * KINGS_DISTANCE_O;
			eval_e += SignalFillerConstants.KING_DISTANCE_E[kingDistance] * KINGS_DISTANCE_E;
		} else {
			eval_o -= SignalFillerConstants.KING_DISTANCE_O[kingDistance] * KINGS_DISTANCE_O;
			eval_e -= SignalFillerConstants.KING_DISTANCE_E[kingDistance] * KINGS_DISTANCE_E;
		}
		
		//Refers to http://home.comcast.net/~danheisman/Articles/evaluation_of_material_imbalance.htm
		//
		//A further refinement would be to raise the knight's value by 1/16 and lower the rook's value by 1/8
		//for each pawn above five of the side being valued, with the opposite adjustment for each pawn short of five.
		int w_pawns_above5 = w_pawns.getDataSize() - 5;
		int b_pawns_above5 = b_pawns.getDataSize() - 5;
		
		int pawns5_rooks = w_pawns_above5 * w_rooks.getDataSize() - b_pawns_above5 * b_rooks.getDataSize();
		eval_o += pawns5_rooks * PAWNS5_ROOKS_O;
		eval_e += pawns5_rooks * PAWNS5_ROOKS_E;
		
		int pawns5_knights = w_pawns_above5 * w_knights.getDataSize() - b_pawns_above5 * b_knights.getDataSize();
		eval_o += pawns5_knights * PAWNS5_KNIGHTS_O;
		eval_e += pawns5_knights * PAWNS5_KNIGHTS_E;
		
		
		return movedFGPawns + interpolator.interpolateByFactor(eval_o, eval_e);
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
		
		int w_fianchetto = 0;
		int b_fianchetto = 0;
		
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
					w_fianchetto++;
				}
			}
		}
		
		long b_fianchetto_pawns = Fields.G6 | Fields.F7 | Fields.H7;
		if ((b_king & Fields.G8) != 0) {
			if ((b_bishops & Fields.G7) != 0) {
				if ((b_pawns & b_fianchetto_pawns) == b_fianchetto_pawns) {
					b_fianchetto--;
				}
			}
		}
		
		int fianchetto = w_fianchetto - b_fianchetto;
		return fianchetto;
	}
	
	
	private double movedFGPawns() {
		
		long bb_white_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		long bb_black_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);

		
		int w_cast_type = bitboard.getCastlingType(Figures.COLOUR_WHITE);
		int b_cast_type = bitboard.getCastlingType(Figures.COLOUR_BLACK);
		
		int movedFPawn = 0;
		int missingGPawn = 0;
		if (bitboard.hasRightsToKingCastle(Figures.COLOUR_WHITE)
			|| w_cast_type == CastlingType.KING_SIDE) {
			movedFPawn += (Fields.F2 & bb_white_pawns) == 0L ? 1 : 0;
			missingGPawn += (Fields.LETTER_G & bb_white_pawns) == 0L ? 1 : 0;
		}
		if (bitboard.hasRightsToKingCastle(Figures.COLOUR_BLACK)
				|| b_cast_type == CastlingType.KING_SIDE) {
			movedFPawn += ((Fields.F7 & bb_black_pawns) == 0L ? -1 : 0);
			missingGPawn += (Fields.LETTER_G & bb_black_pawns) == 0L ? -1 : 0;
		}
		

		double scores_o = movedFPawn * KINGSAFE_F_O + missingGPawn * KINGSAFE_G_O;
		double scores_e = movedFPawn * KINGSAFE_F_E + missingGPawn * KINGSAFE_G_E;
		
		return interpolator.interpolateByFactor(scores_o, scores_e);
	}
	
	
	private int eval_pieces() {
		
		
		double eval_o = 0;
		double eval_e = 0;
		
		
		long bb_white_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		long bb_black_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
		long bb_white_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
		long bb_black_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);
		int w_pawns_on_w_squares = Utils.countBits(bb_white_pawns & Fields.ALL_WHITE_FIELDS);
		int w_pawns_on_b_squares = Utils.countBits(bb_white_pawns & Fields.ALL_BLACK_FIELDS);
		int b_pawns_on_w_squares = Utils.countBits(bb_black_pawns & Fields.ALL_WHITE_FIELDS);
		int b_pawns_on_b_squares = Utils.countBits(bb_black_pawns & Fields.ALL_BLACK_FIELDS);
		
		
		/**
		 * Kings iteration
		 */
		int kingFieldID_white = w_king.getData()[0];
		int kingFieldID_black = b_king.getData()[0];
		
		double pst_w_king = interpolator.interpolateByFactor(SignalFillerConstants.KING_O[kingFieldID_white], SignalFillerConstants.KING_E[kingFieldID_white]);
		double pst_b_king = interpolator.interpolateByFactor(SignalFillerConstants.KING_O[axisSymmetry(kingFieldID_black)], SignalFillerConstants.KING_E[axisSymmetry(kingFieldID_black)]);
		double pst_king = pst_w_king - pst_b_king;
		
		eval_o += PST_KING_O * pst_king;
		eval_e += PST_KING_E * pst_king;
		
		
		bitboard.getPawnsCache().lock();
		
		PawnsEval pawnsModelEval = (PawnsEval) bitboard.getPawnsStructure();
		/**
		 * Pawns iteration
		 */
		{
			int w_pawns_count = w_pawns.getDataSize();
			if (w_pawns_count > 0) {
				int[] pawns_fields = w_pawns.getData();
				for (int i=0; i<w_pawns_count; i++) {
					int fieldID = pawns_fields[i];
					
					boolean isPassed = false;
					int passedCount = pawnsModelEval.getModel().getWPassedCount();
					if (passedCount > 0) {
						Pawn[] passed = pawnsModelEval.getModel().getWPassed();
						for (int j=0; j<passedCount; j++) {
							if (fieldID == passed[j].getFieldID()) {
								isPassed = true;
								break;
							}
						}
					}
					
					if (!isPassed) {
						double pst = interpolator.interpolateByFactor(SignalFillerConstants.PAWN_O[fieldID], SignalFillerConstants.PAWN_E[fieldID]);
						eval_o += PST_PAWN_O * pst;
						eval_e += PST_PAWN_E * pst;
					}
				}
			}
		}
		{
			int b_pawns_count = b_pawns.getDataSize();
			if (b_pawns_count > 0) {
				int[] pawns_fields = b_pawns.getData();
				for (int i=0; i<b_pawns_count; i++) {
					int fieldID = pawns_fields[i];
					
					boolean isPassed = false;
					int passedCount = pawnsModelEval.getModel().getBPassedCount();
					if (passedCount > 0) {
						Pawn[] passed = pawnsModelEval.getModel().getBPassed();
						for (int j=0; j<passedCount; j++) {
							if (fieldID == passed[j].getFieldID()) {
								isPassed = true;
								break;
							}
						}
					}
					
					if (!isPassed) {
						double pst = interpolator.interpolateByFactor(SignalFillerConstants.PAWN_O[axisSymmetry(fieldID)], SignalFillerConstants.PAWN_E[axisSymmetry(fieldID)]);
						eval_o -= PST_PAWN_O * pst;
						eval_e -= PST_PAWN_E * pst;
					}
				}
			}
		}
		
		bitboard.getPawnsCache().unlock();
		
		
		/**
		 * Knights iteration
		 */
		{
			int w_knights_count = w_knights.getDataSize();
			if (w_knights_count > 0) {
				int[] knights_fields = w_knights.getData();
				for (int i=0; i<w_knights_count; i++) {
					
					int fieldID = knights_fields[i];
					
					double pst = interpolator.interpolateByFactor(SignalFillerConstants.KNIGHT_O[fieldID], SignalFillerConstants.KNIGHT_E[fieldID]);
					eval_o += PST_KNIGHTS_O * pst;
					eval_e += PST_KNIGHTS_E * pst;
					
					long fieldBB = Fields.ALL_A1H1[fieldID];
										
				    // Knight outposts:
				    if ((Fields.SPACE_BLACK & fieldBB) != 0) {
					    long bb_neighbors = ~PawnStructureConstants.WHITE_FRONT_FULL[fieldID] & PawnStructureConstants.WHITE_PASSED[fieldID];
					    if ((bb_neighbors & bb_black_pawns) == 0) { // Weak field
					    	
					    	eval_o += KNIGHT_OUTPOST_O;
							eval_e += KNIGHT_OUTPOST_E;
					    	
				    		if ((BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_MOVES[fieldID] & bb_white_pawns) != 0) {
						    	
				    			eval_o += KNIGHT_OUTPOST_O;
								eval_e += KNIGHT_OUTPOST_E;
								
				    			if (b_knights.getDataSize() == 0) {
				    				long colouredFields = (fieldBB & Fields.ALL_WHITE_FIELDS) != 0 ?
				    						Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
				    				if ((colouredFields & bb_black_bishops) == 0) {
								    	eval_o += KNIGHT_OUTPOST_O;
										eval_e += KNIGHT_OUTPOST_E;
				    				}
				    			}
				    		}
					    }
				    }
				    
					int tropism = Fields.getTropismPoint(fieldID, kingFieldID_black);
					eval_o += TROPISM_KNIGHT_O * tropism;
					eval_e += TROPISM_KNIGHT_E * tropism;
				}
			}
		}
		
		{
			int b_knights_count = b_knights.getDataSize();		
			if (b_knights_count > 0) {
				int[] knights_fields = b_knights.getData();
				for (int i=0; i<b_knights_count; i++) {
					
					int fieldID = knights_fields[i];
					
					double pst = interpolator.interpolateByFactor(SignalFillerConstants.KNIGHT_O[axisSymmetry(fieldID)], SignalFillerConstants.KNIGHT_E[axisSymmetry(fieldID)]);
					eval_o -= PST_KNIGHTS_O * pst;
					eval_e -= PST_KNIGHTS_E * pst;
					
					long fieldBB = Fields.ALL_A1H1[fieldID];
					
				    // Knight outposts:
				    if ((Fields.SPACE_WHITE & fieldBB) != 0) {
					    long bb_neighbors = ~PawnStructureConstants.BLACK_FRONT_FULL[fieldID] & PawnStructureConstants.BLACK_PASSED[fieldID];
					    if ((bb_neighbors & bb_white_pawns) == 0) { // Weak field
					    	
					    	eval_o -= KNIGHT_OUTPOST_O;
							eval_e -= KNIGHT_OUTPOST_E;
							
					    	if ((WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_MOVES[fieldID] & bb_black_pawns) != 0) {
					    		
					    		eval_o -= KNIGHT_OUTPOST_O;
								eval_e -= KNIGHT_OUTPOST_E;
								
				    			if (w_knights.getDataSize() == 0) {
				    				long colouredFields = (fieldBB & Fields.ALL_WHITE_FIELDS) != 0 ?
				    						Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
				    				if ((colouredFields & bb_white_bishops) == 0) {
				    					eval_o -= KNIGHT_OUTPOST_O;
										eval_e -= KNIGHT_OUTPOST_E;
				    				}
				    			}
				    		}
					    }
				    }
				    
					int tropism = Fields.getTropismPoint(fieldID, kingFieldID_white);
					eval_o -= TROPISM_KNIGHT_O * tropism;
					eval_e -= TROPISM_KNIGHT_E * tropism;
				}
			}
		}
		
		
		/**
		 * Bishops iteration - bad bishops
		 */
		{
			int w_bishops_count = w_bishops.getDataSize();
			if (w_bishops_count > 0) {
				int[] bishops_fields = w_bishops.getData();
				for (int i=0; i<w_bishops_count; i++) {
					
					int fieldID = bishops_fields[i];
					
					double pst = interpolator.interpolateByFactor(SignalFillerConstants.BISHOP_O[fieldID], SignalFillerConstants.BISHOP_E[fieldID]);
					eval_o += PST_BISHOPS_O * pst;
					eval_e += PST_BISHOPS_E * pst;
					
					if ((Fields.ALL_WHITE_FIELDS & Fields.ALL_A1H1[fieldID]) != 0L) {
						eval_o += w_pawns_on_w_squares * BISHOPS_BAD_O;
						eval_e += w_pawns_on_w_squares * BISHOPS_BAD_E;
					} else {
						eval_o += w_pawns_on_b_squares * BISHOPS_BAD_O;
						eval_e += w_pawns_on_b_squares * BISHOPS_BAD_E;
					}
					
					int tropism = Fields.getTropismPoint(fieldID, kingFieldID_black);
					eval_o += TROPISM_BISHOP_O * tropism;
					eval_e += TROPISM_BISHOP_E * tropism;
				}
			}
		}
		
		{
			int b_bishops_count = b_bishops.getDataSize();
			if (b_bishops_count > 0) {
				int[] bishops_fields = b_bishops.getData();
				for (int i=0; i<b_bishops_count; i++) {
					
					int fieldID = bishops_fields[i];
					
					double pst = interpolator.interpolateByFactor(SignalFillerConstants.BISHOP_O[axisSymmetry(fieldID)], SignalFillerConstants.BISHOP_E[axisSymmetry(fieldID)]);
					eval_o -= PST_BISHOPS_O * pst;
					eval_e -= PST_BISHOPS_E * pst;
					
					if ((Fields.ALL_WHITE_FIELDS & Fields.ALL_A1H1[fieldID]) != 0L) {
						eval_o -= b_pawns_on_w_squares * BISHOPS_BAD_O;
						eval_e -= b_pawns_on_w_squares * BISHOPS_BAD_E;
					} else {
						eval_o -= b_pawns_on_b_squares * BISHOPS_BAD_O;
						eval_e -= b_pawns_on_b_squares * BISHOPS_BAD_E;
					}
					
					int tropism = Fields.getTropismPoint(fieldID, kingFieldID_white);
					eval_o -= TROPISM_BISHOP_O * tropism;
					eval_e -= TROPISM_BISHOP_E * tropism;
				}
			}
		}
		
		
		/**
		 * Rooks iteration
		 */
		bitboard.getPawnsCache().lock();
		
		pawnsModelEval = (PawnsEval) bitboard.getPawnsStructure();
		//PawnsModel model = pawnsModelEval.getModel();
		
		long openedFiles_all = pawnsModelEval.getModel().getOpenedFiles();
		long openedFiles_white = pawnsModelEval.getModel().getWHalfOpenedFiles();
		long openedFiles_black = pawnsModelEval.getModel().getBHalfOpenedFiles();
		
		bitboard.getPawnsCache().unlock();
		
		int w_rooks_count = w_rooks.getDataSize();
		if (w_rooks_count > 0) {
			int[] rooks_fields = w_rooks.getData();
			for (int i=0; i<w_rooks_count; i++) {
				
				int fieldID = rooks_fields[i];
				
				double pst = interpolator.interpolateByFactor(SignalFillerConstants.ROOK_O[fieldID], SignalFillerConstants.ROOK_E[fieldID]);
				eval_o += PST_ROOKS_O * pst;
				eval_e += PST_ROOKS_E * pst;
				
				long fieldBitboard = Fields.ALL_A1H1[fieldID];
				if ((fieldBitboard & openedFiles_all) != 0L) {
					eval_o += ROOKS_OPENED_O;
					eval_e += ROOKS_OPENED_E;
				} else if ((fieldBitboard & openedFiles_white) != 0L) {
					eval_o += ROOKS_SEMIOPENED_O;
					eval_e += ROOKS_SEMIOPENED_E;
				}
				if ((fieldBitboard & RANK_7TH) != 0L) {
					eval_o += ROOKS_7TH_2TH_O;
					eval_e += ROOKS_7TH_2TH_E;
				}
				
				int tropism = Fields.getTropismPoint(fieldID, kingFieldID_black);
				eval_o += TROPISM_ROOK_O * tropism;
				eval_e += TROPISM_ROOK_E * tropism;
			}
		}
		
		int b_rooks_count = b_rooks.getDataSize();
		if (b_rooks_count > 0) {
			int[] rooks_fields = b_rooks.getData();
			for (int i=0; i<b_rooks_count; i++) {
				
				
				int fieldID = rooks_fields[i];
				
				double pst = interpolator.interpolateByFactor(SignalFillerConstants.ROOK_O[axisSymmetry(fieldID)], SignalFillerConstants.ROOK_E[axisSymmetry(fieldID)]);
				eval_o -= PST_ROOKS_O * pst;
				eval_e -= PST_ROOKS_E * pst;
				
				long fieldBitboard = Fields.ALL_A1H1[fieldID];
				if ((fieldBitboard & openedFiles_all) != 0L) {
					eval_o -= ROOKS_OPENED_O;
					eval_e -= ROOKS_OPENED_E;
				} else if ((fieldBitboard & openedFiles_black) != 0L) {
					eval_o -= ROOKS_SEMIOPENED_O;
					eval_e -= ROOKS_SEMIOPENED_E;
				}
				if ((fieldBitboard & RANK_2TH) != 0L) {
					eval_o -= ROOKS_7TH_2TH_O;
					eval_e -= ROOKS_7TH_2TH_E;
				}
				
				int tropism = Fields.getTropismPoint(fieldID, kingFieldID_white);
				eval_o -= TROPISM_ROOK_O * tropism;
				eval_e -= TROPISM_ROOK_E * tropism;
			}
		}
		
		
		/**
		 * Queens iteration
		 */
		{
			int w_queens_count = w_queens.getDataSize();
			if (w_queens_count > 0) {
				int[] queens_fields = w_queens.getData();
				for (int i=0; i<w_queens_count; i++) {
					
					int fieldID = queens_fields[i];
					
					double pst = interpolator.interpolateByFactor(SignalFillerConstants.QUEEN_O[fieldID], SignalFillerConstants.QUEEN_E[fieldID]);
					eval_o += PST_QUEENS_O * pst;
					eval_e += PST_QUEENS_E * pst;
					
					long fieldBitboard = Fields.ALL_A1H1[fieldID];
					if ((fieldBitboard & RANK_7TH) != 0L) {
						eval_o += QUEENS_7TH_2TH_O;
						eval_e += QUEENS_7TH_2TH_E;
					}
					
					int tropism = Fields.getTropismPoint(fieldID, kingFieldID_black);
					eval_o += TROPISM_QUEEN_O * tropism;
					eval_e += TROPISM_QUEEN_E * tropism;
				}
			}
		}
		
		{
			int b_queens_count = b_queens.getDataSize();
			if (b_queens_count > 0) {
				int[] queens_fields = b_queens.getData();
				for (int i=0; i<b_queens_count; i++) {
					
					int fieldID = queens_fields[i];
					
					double pst = interpolator.interpolateByFactor(SignalFillerConstants.QUEEN_O[axisSymmetry(fieldID)], SignalFillerConstants.QUEEN_E[axisSymmetry(fieldID)]);
					eval_o -= PST_QUEENS_O * pst;
					eval_e -= PST_QUEENS_E * pst;
					
					long fieldBitboard = Fields.ALL_A1H1[fieldID];
					if ((fieldBitboard & RANK_2TH) != 0L) {
						eval_o -= QUEENS_7TH_2TH_O;
						eval_e -= QUEENS_7TH_2TH_E;
					}
					
					int tropism = Fields.getTropismPoint(fieldID, kingFieldID_white);
					eval_o -= TROPISM_QUEEN_O * tropism;
					eval_e -= TROPISM_QUEEN_E * tropism;
				}
			}
		}
		
		
		return interpolator.interpolateByFactor(eval_o, eval_e);
	}
	
	
	private int eval_pawns() {
		
		long bb_w_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE);
		long bb_b_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE);
		
		bitboard.getPawnsCache().lock();
		
		PawnsEval pawnsModelEval = (PawnsEval) bitboard.getPawnsStructure();
		PawnsModel model = pawnsModelEval.getModel();
		
		double eval_o = pawnsModelEval.getEval_o();
		double eval_e = pawnsModelEval.getEval_e();
		
		//int PAWNS_PASSED_UNSTOPPABLE = 100 + baseEval.getMaterialRook();
		int unstoppablePasser = bitboard.getUnstoppablePasser();
		if (unstoppablePasser > 0) {
			eval_o += PAWNS_UNSTOPPABLE_PASSER_O;
			eval_e += PAWNS_UNSTOPPABLE_PASSER_E;
		} else if (unstoppablePasser < 0) {
			eval_o -= PAWNS_UNSTOPPABLE_PASSER_O;
			eval_e -= PAWNS_UNSTOPPABLE_PASSER_E;
		}
		
		int space = space(model);
		eval_o += space * SPACE_O;
		eval_e += space * SPACE_E;
		
		
		int w_kingID = model.getWKingFieldID();
		int b_kingID = model.getBKingFieldID();
		
		
		int w_passed_count = model.getWPassedCount();
		if (w_passed_count > 0) {
			
			Pawn[] w_passed = model.getWPassed();
			for (int i=0; i<w_passed_count; i++) {
				
				Pawn p = w_passed[i];
				
				int rank = p.getRank();
				int stoppersCount = Utils.countBits(p.getFront() & ~bitboard.getFreeBitboard());
				rank = rank - stoppersCount;
				if (rank <= 0) {
					rank = 1;
				}
				
				eval_o += PAWNS_PASSED_O;
				eval_e += PAWNS_PASSED_E;
				
				int passer = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.PASSERS_RANK_O[rank], SignalFillerConstants.PASSERS_RANK_E[rank]);
				eval_o += PAWNS_PASSED_RNK_O * passer;
				eval_e += PAWNS_PASSED_RNK_E * passer;
				
		        int frontFieldID = p.getFieldID() + 8;
		        int frontFrontFieldID = frontFieldID + 8;
		        if (frontFrontFieldID >= 64) {
		        	frontFrontFieldID = frontFieldID;
		        }
		        
		        int dist_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD[Fields.getDistancePoints(w_kingID, frontFieldID)];
		        eval_o += KINGS_PASSERS_F_O * dist_f;
		        eval_e += KINGS_PASSERS_F_E * dist_f;
		        
		        int dist_ff = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[Fields.getDistancePoints(w_kingID, frontFrontFieldID)];
		        eval_o += KINGS_PASSERS_FF_O * dist_ff;
		        eval_e += KINGS_PASSERS_FF_E * dist_ff;
		        
		        int dist_op_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD_OP[Fields.getDistancePoints(b_kingID, frontFieldID)];
		        eval_o += KINGS_PASSERS_F_OP_O * dist_op_f;
		        eval_e += KINGS_PASSERS_F_OP_E * dist_op_f;
				
				
				long front = p.getFront();
				if ((front & bb_w_rooks) != 0L) {
					eval_o += 1 * ROOK_INFRONT_PASSER_O;
					eval_e += 1 * ROOK_INFRONT_PASSER_E;
				}
				
				long behind = p.getVertical() & ~front;
				if ((behind & bb_w_rooks) != 0L) {
					eval_o += 1 * ROOK_BEHIND_PASSER_O;
					eval_e += 1 * ROOK_BEHIND_PASSER_E;
				}
			}
		}
		
		int b_passed_count = model.getBPassedCount();
		if (b_passed_count > 0) {

			Pawn[] b_passed = model.getBPassed();
			for (int i=0; i<b_passed_count; i++) {
				
				Pawn p = b_passed[i];

				int rank = p.getRank();
				int stoppersCount = Utils.countBits(p.getFront() & ~bitboard.getFreeBitboard());
				rank = rank - stoppersCount;
				if (rank <= 0) {
					rank = 1;
				}
				
				eval_o -= PAWNS_PASSED_O;
				eval_e -= PAWNS_PASSED_E;
				
				int passer = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.PASSERS_RANK_O[rank], SignalFillerConstants.PASSERS_RANK_E[rank]);
				eval_o -= PAWNS_PASSED_RNK_O * passer;
				eval_e -= PAWNS_PASSED_RNK_E * passer;
				
		        int frontFieldID = p.getFieldID() - 8;
		        int frontFrontFieldID = frontFieldID - 8;
		        if (frontFrontFieldID < 0) {
		        	frontFrontFieldID = frontFieldID;
		        }
		        
		        int dist_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD[Fields.getDistancePoints(b_kingID, frontFieldID)];
		        eval_o -= KINGS_PASSERS_F_O * dist_f;
		        eval_e -= KINGS_PASSERS_F_E * dist_f;
		        
		        int dist_ff = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[Fields.getDistancePoints(b_kingID, frontFrontFieldID)];
		        eval_o -= KINGS_PASSERS_FF_O * dist_ff;
		        eval_e -= KINGS_PASSERS_FF_E * dist_ff;
		        
		        int dist_op_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD_OP[Fields.getDistancePoints(w_kingID, frontFieldID)];
		        eval_o -= KINGS_PASSERS_F_OP_O * dist_op_f;
		        eval_e -= KINGS_PASSERS_F_OP_E * dist_op_f;	
		        
				
				long front = p.getFront();
				if ((front & bb_b_rooks) != 0L) {
					eval_o -= 1 * ROOK_INFRONT_PASSER_O;
					eval_e -= 1 * ROOK_INFRONT_PASSER_E;
				}
				
				long behind = p.getVertical() & ~front;
				if ((behind & bb_b_rooks) != 0L) {
					eval_o -= 1 * ROOK_BEHIND_PASSER_O;
					eval_e -= 1 * ROOK_BEHIND_PASSER_E;
				}
			}
		}
		
		
		int w_pawns_count = model.getWCount();
		if (w_pawns_count > 0) {
			
			Pawn[] w_pawns = model.getWPawns();
			for (int i=0; i<w_pawns_count; i++) {
				Pawn p = w_pawns[i];
				
				if (p.isCandidate()) {
					
					int rank = p.getRank();
					/*int stoppersCount = Utils.countBits(p.getFront() & ~bitboard.getFreeBitboard());
					rank = rank - stoppersCount;
					if (rank <= 0) {
						rank = 1;
					}*/
					
					int passerCandidate = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.PASSERS_CANDIDATE_RANK_O[rank], SignalFillerConstants.PASSERS_CANDIDATE_RANK_E[rank]);
					eval_o += PAWNS_CANDIDATE_RNK_O * passerCandidate;
					eval_e += PAWNS_CANDIDATE_RNK_E * passerCandidate;
				}
			}
		}
		
		int b_pawns_count = model.getBCount();
		if (b_pawns_count > 0) {
			
			Pawn[] b_pawns = model.getBPawns();
			for (int i=0; i<b_pawns_count; i++) {
				Pawn p = b_pawns[i];
				
				if (p.isCandidate()) {
					
					int rank = p.getRank();
					/*int stoppersCount = Utils.countBits(p.getFront() & ~bitboard.getFreeBitboard());
					rank = rank - stoppersCount;
					if (rank <= 0) {
						rank = 1;
					}*/
					
					int passerCandidate = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.PASSERS_CANDIDATE_RANK_O[rank], SignalFillerConstants.PASSERS_CANDIDATE_RANK_E[rank]);
					eval_o -= PAWNS_CANDIDATE_RNK_O * passerCandidate;
					eval_e -= PAWNS_CANDIDATE_RNK_E * passerCandidate;
				}
			}
		}
		
		bitboard.getPawnsCache().unlock();
		
		
		return interpolator.interpolateByFactor(eval_o, eval_e);
	}
	
	
	private int space(PawnsModel model) {
		
		int w_space = 0;
		int w_spaceWeight = w_knights.getDataSize() + w_bishops.getDataSize(); 
		if (w_spaceWeight > 0) {
			w_space = w_spaceWeight * Utils.countBits_less1s(model.getWspace());
		}
		
		int b_space = 0;
		int b_spaceWeight = b_knights.getDataSize() + b_bishops.getDataSize();
		if (b_spaceWeight > 0) {
			b_space = b_spaceWeight * Utils.countBits_less1s(model.getBspace());
		}
		
		int space = w_space - b_space;
		
		return space;
	}
	
	
	private double eval_moves() {
		
		double eval_o = 0;
		double eval_e = 0;
		
		
		int kingFieldID_white = w_king.getData()[0];
		int kingFieldID_black = b_king.getData()[0];
		long kingSurrounding_L1_white = KingSurrounding.SURROUND_LEVEL1[kingFieldID_white];
		long kingSurrounding_L2_white = (~kingSurrounding_L1_white) & KingSurrounding.SURROUND_LEVEL2[kingFieldID_white];
		long kingSurrounding_L1_black = KingSurrounding.SURROUND_LEVEL1[kingFieldID_black];
		long kingSurrounding_L2_black = (~kingSurrounding_L1_black) & KingSurrounding.SURROUND_LEVEL2[kingFieldID_black];

		
		int w_movesCount = -1;
		int b_movesCount = -1;
		if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
			w_moves_iter.reserved_clear();
			w_movesCount = bitboard.genAllMoves(w_moves_iter);
			bitboard.makeNullMoveForward();
			b_moves_iter.reserved_clear();
			b_movesCount = bitboard.genAllMoves(b_moves_iter);
			bitboard.makeNullMoveBackward();
		} else {
			b_moves_iter.reserved_clear();
			b_movesCount = bitboard.genAllMoves(b_moves_iter);
			bitboard.makeNullMoveForward();
			w_moves_iter.reserved_clear();
			w_movesCount = bitboard.genAllMoves(w_moves_iter);
			bitboard.makeNullMoveBackward();
		}
		
		
		int w_attacksking_l1 = 0;
		int b_attacksking_l1 = 0;
		int w_attacksking_l2 = 0;
		int b_attacksking_l2 = 0;
		
		int[] w_moves = w_moves_iter.reserved_getMovesBuffer();
		for (int i=0; i<w_movesCount; i++) {
			
			int curMove = w_moves[i];
			long toBitboard = MoveInt.getToFieldBitboard(curMove);
			int pid = MoveInt.getFigurePID(curMove);
			
			boolean check = (pid != Constants.PID_W_PAWN && pid != Constants.PID_W_KING) ? bitboard.isCheckMove(curMove): false;
			boolean capture = MoveInt.isCapture(curMove);
			boolean promotion = MoveInt.isPromotion(curMove);
			boolean attackKingL1 = (toBitboard & kingSurrounding_L1_black) != 0L;
			boolean attackKingL2 = (toBitboard & kingSurrounding_L2_black) != 0L;
			
			if (check) {
				eval_o += 50;
				eval_e += 50;
			}
			
			if (promotion) {
				eval_o += 20;
				eval_e += 20;
			}
			
			if (capture) {
				eval_o += 20;
				eval_e += 20;
				int see = bitboard.getSee().evalExchange(curMove);
				if (see >= 0) {
					if (see == 0) {
						eval_o += 20;
						eval_e += 20;
					} else {
						eval_o += 50;
						eval_e += 50;
					}
				}
			}
			
			if (attackKingL1) {
				w_attacksking_l1++;
			}
			if (attackKingL2) {
				w_attacksking_l2++;
			}
		}
		
		int[] b_moves = b_moves_iter.reserved_getMovesBuffer();
		for (int i=0; i<b_movesCount; i++) {
			
			int curMove = b_moves[i];
			long toBitboard = MoveInt.getToFieldBitboard(curMove);
			int pid = MoveInt.getFigurePID(curMove);
			
			boolean check = (pid != Constants.PID_B_PAWN && pid != Constants.PID_B_KING) ? bitboard.isCheckMove(curMove) : false;
			boolean capture = MoveInt.isCapture(curMove);
			boolean promotion = MoveInt.isPromotion(curMove);
			boolean attackKingL1 = (toBitboard & kingSurrounding_L1_white) != 0L;
			boolean attackKingL2 = (toBitboard & kingSurrounding_L2_white) != 0L;
			
			if (check) {
				eval_o -= 50;
				eval_e -= 50;
			}
			
			if (promotion) {
				eval_o -= 20;
				eval_e -= 20;
			}
			
			if (capture) {
				eval_o -= 20;
				eval_e -= 20;
				int see = bitboard.getSee().evalExchange(curMove);
				if (see >= 0) {
					if (see == 0) {
						eval_o -= 20;
						eval_e -= 20;
					} else {
						eval_o -= 50;
						eval_e -= 50;
					}
				}
			}
			
			if (attackKingL1) {
				b_attacksking_l1++;
			}
			if (attackKingL2) {
				b_attacksking_l2++;
			}
		}
		
		
		double kingsafety_l1 = 2 * (Math.pow(w_attacksking_l1, 2) - Math.pow(b_attacksking_l1, 2)) / (double) 1;
		/*if (kingsafety_l1 > max) {
			max = kingsafety_l1;
			System.out.println("kingsafety_l1=" + kingsafety_l1);
		}
		if (kingsafety_l1 < min) {
			min = kingsafety_l1;
			System.out.println("kingsafety_l1=" + kingsafety_l1);
		}*/
		eval_o += kingsafety_l1;
		eval_e += 0;
		
		double kingsafety_l2 = 1 * (Math.pow(w_attacksking_l2, 2) - Math.pow(b_attacksking_l2, 2)) / (double) 2;
		/*if (kingsafety_l2 > max) {
			max = kingsafety_l2;
			System.out.println("kingsafety_l2=" + kingsafety_l2);
		}
		if (kingsafety_l2 < min) {
			min = kingsafety_l2;
			System.out.println("kingsafety_l2=" + kingsafety_l2);
		}*/
		eval_o += kingsafety_l2;
		eval_e += 0;
		
		
		return interpolator.interpolateByFactor(eval_o, eval_e);
	}
	
	double max = -1000000; 
	double min = 1000000;
}

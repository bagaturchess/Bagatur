package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.CastlingType;
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
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEval;


public class Bagatur_ALL_SignalFiller implements ISignalFiller, Bagatur_ALL_FeaturesConstants, Bagatur_ALL_SignalFillerConstants {
	
	
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
	
	private IBaseEval baseEval;
	
	private EvalInfo evalInfo;
	
	// Attack weights for each piece type.
	public static final int QueenAttackWeight	 		= 5;
	public static final int RookAttackWeight 			= 3;
	public static final int BishopAttackWeight 			= 2;
	public static final int KnightAttackWeight	 		= 2;
	
	
	public Bagatur_ALL_SignalFiller(IBitBoard _bitboard) {
		
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
		
		baseEval = _bitboard.getBaseEvaluation();
		
		evalInfo = new EvalInfo(bitboard);

		
		//evalConfig = (IBagaturEvalConfig) _evalConfig;
	}
	
	
	public void fill(ISignals signals) {
		fillStandardSignals(signals);
		fillPawnSignals(signals);
		fillPiecesIterationSignals(signals);
		fillMovesIterationSignals(signals);
	}
	
	
	public void fillByComplexity(int complexity, ISignals signals) {
		switch(complexity) {
			case IFeatureComplexity.STANDARD:
				fillStandardSignals(signals);
				return;
			case IFeatureComplexity.PAWNS_STRUCTURE:
				fillPawnSignals(signals);
				return;
			case IFeatureComplexity.PIECES_ITERATION:
				fillPiecesIterationSignals(signals);
				return;
			case IFeatureComplexity.MOVES_ITERATION:
				fillMovesIterationSignals(signals);
				return;
			case IFeatureComplexity.FIELDS_STATES_ITERATION:
				//throw new UnsupportedOperationException("FIELDS_STATES_ITERATION");
				return;
			default:
				throw new IllegalStateException("complexity=" + complexity);
		}
	}
	
	
	/**
	 * @param signals
	 */
	private void fillStandardSignals(ISignals signals) {
		evalInfo.clear_short();
		evalInfo.clear();
		
		eval_material(signals);
		eval_PST(signals);
		eval_standard(signals);
	}


	/**
	 * @param signals
	 */
	private void fillPawnSignals(ISignals signals) {
		
		eval_pawns(signals);	
		
		initEvalInfo1();
		eval_pawns_RooksAndQueens(signals);		
		
	}


	/**
	 * @param signals
	 */
	private void fillPiecesIterationSignals(ISignals signals) {
		//N/A
	}


	/**
	 * @param signals
	 */
	private void fillMovesIterationSignals(ISignals signals) {

		initEvalInfo2();
		eval_mobility(signals);
		
		
		initEvalInfo3();
		eval_king_safety(signals);
		eval_space(signals);
		eval_hunged(signals);
		
		eval_TrapsAndSafeMobility(signals);
		eval_PassersFrontAttacks(signals);
	}
	
	
	private double interpolateInternal(double o, double e, double openningPart) {
		return (o * openningPart + e * (1 - openningPart));
	}
	
	
	public void eval_material(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		signals.getSignal(FEATURE_ID_MATERIAL_PAWN).addStrength(w_pawns.getDataSize() - b_pawns.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_KNIGHT).addStrength(w_knights.getDataSize() - b_knights.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_BISHOP).addStrength(w_bishops.getDataSize() - b_bishops.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_ROOK).addStrength(w_rooks.getDataSize() - b_rooks.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_QUEEN).addStrength(w_queens.getDataSize() - b_queens.getDataSize(), openingPart);
		
		signals.getSignal(FEATURE_ID_MATERIAL_DOUBLE_BISHOP).addStrength(((w_bishops.getDataSize() >= 2) ? 1 : 0) - ((b_bishops.getDataSize() >= 2) ? 1 : 0), openingPart);
	}
	
	
	private void eval_PST(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		signals.getSignal(FEATURE_ID_PST).addStrength(interpolateInternal(baseEval.getPST_o(), baseEval.getPST_e(), openingPart), openingPart);
	}
	
	
	public void eval_standard(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		int tempo = (bitboard.getColourToMove() == Figures.COLOUR_WHITE ? 1 : -1);
		signals.getSignal(FEATURE_ID_STANDARD_TEMPO).addStrength(tempo, openingPart);
		
		int castling = (castling(Figures.COLOUR_WHITE) - castling(Figures.COLOUR_BLACK));
		signals.getSignal(FEATURE_ID_STANDARD_CASTLING).addStrength(castling, openingPart);
		
		int fianchetto = fianchetto();
		signals.getSignal(FEATURE_ID_STANDARD_FIANCHETTO).addStrength(fianchetto, openingPart);
		
		eval_patterns(signals);
		
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
						signals.getSignal(FEATURE_ID_STANDARD_KINGS_OPPOSITION).addStrength(-1, openingPart);
					} else {
						//White has the opposition
						signals.getSignal(FEATURE_ID_STANDARD_KINGS_OPPOSITION).addStrength(1, openingPart);
					}
				}
			} else if (kingsDistance > 2 && kingsDistance % 2 == 0) {
				//TODO: Implement the opposition when the kings have more than 2 square distance
				//e.g. on the same line (can use Fields.areOnTheSameLine(f1, f2))
			}
		}
	}
	
	
	public void eval_pawns(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		bitboard.getPawnsCache().lock();
		
		PawnsModelEval pawnsModelEval = bitboard.getPawnsStructure();
		PawnsModel model = pawnsModelEval.getModel();

		int w_kingID = model.getWKingFieldID();
		int b_kingID = model.getBKingFieldID();
		
		int w_pawns_count = model.getWCount();
		if (w_pawns_count > 0) {
			
			Pawn[] w_pawns = model.getWPawns();
			
			for (int i=0; i<w_pawns_count; i++) {
				
				Pawn p = w_pawns[i];
				
				if (p.isGuard() && p.getRank() <= 3) {
					int scores = 0;
					switch(p.getRank()) {
						case 1:
							scores = 4;
							break;
						case 2:
							scores = 2;
							break;
						case 3:
							scores = 1;
							break;
						default:
							throw new IllegalStateException();
					}
					
					signals.getSignal(FEATURE_ID_PAWNS_KING_GUARDS).addStrength(scores, openingPart);
					
				}
				
				
				int rank = p.getRank();
				int file = Fields.LETTERS[p.getFieldID()];
				int file_symmetry = Fields.FILE_SYMMETRY[file];
				
				
				if (p.isDoubled()) {
					double val = interpolateInternal(PAWNS_DOUBLED_O[file_symmetry], PAWNS_DOUBLED_E[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_DOUBLED).addStrength(val, openingPart);
				}
				
				if (p.isIsolated()) {
					double val = interpolateInternal(PAWNS_ISOLATED_O[file_symmetry], PAWNS_ISOLATED_E[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_ISOLATED).addStrength(val, openingPart);
				}
				
				if (p.isBackward()) {
					double val = interpolateInternal(PAWNS_BACKWARD_O[file_symmetry], PAWNS_BACKWARD_O[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_BACKWARD).addStrength(val, openingPart);
				}
				
				if (p.isSupported() && !p.isPassed()) {
					double val = interpolateInternal(PAWNS_SUPPORTED_O[file_symmetry], PAWNS_SUPPORTED_E[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_SUPPORTED).addStrength(val, openingPart);
				}
				
				if (p.isCandidate()) {
					double val = interpolateInternal(PAWNS_CANDIDATE_O[file_symmetry], PAWNS_CANDIDATE_E[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_CANDIDATE).addStrength(val, openingPart);
				} else if (p.isPassed()) {
					if (p.isSupported()) {
						double val = interpolateInternal(PAWNS_PASSED_SUPPORTED_O[file_symmetry], PAWNS_PASSED_SUPPORTED_E[file_symmetry], openingPart);
						signals.getSignal(FEATURE_ID_PAWNS_PASSED_SUPPORTED).addStrength(val, openingPart);
					} else {
						double val = interpolateInternal(PAWNS_PASSED_O[file_symmetry], PAWNS_PASSED_E[file_symmetry], openingPart);
						signals.getSignal(FEATURE_ID_PAWNS_PASSED).addStrength(val, openingPart);
					}
					
			        // Adjust bonus based on king proximity:
			        int frontFieldID = p.getFieldID() + 8;
			        int frontFrontFieldID = frontFieldID + 8;
			        if (frontFrontFieldID >= 64) {
			        	frontFrontFieldID = frontFieldID;
			        }
			        
			        int dist_f = Fields.getDistancePoints_reversed(w_kingID, frontFieldID);
			        double val = interpolateInternal((PAWNS_PASSED_O[rank] * dist_f * 25) / (7 * 100), (PAWNS_PASSED_E[rank] * dist_f * 38) / (7 * 100), openingPart);
			        signals.getSignal(FEATURE_ID_PAWNS_KING_F).addStrength(val, openingPart);
			        
			        int dist_ff = Fields.getDistancePoints_reversed(w_kingID, frontFrontFieldID);
			        val = interpolateInternal((PAWNS_PASSED_O[rank] * dist_ff * 25) / (7 * 100), (PAWNS_PASSED_E[rank] * dist_ff * 38) / (7 * 100), openingPart);
			        signals.getSignal(FEATURE_ID_PAWNS_KING_FF).addStrength(val, openingPart);
			        
			        int dist_op_f = Fields.getDistancePoints_reversed(b_kingID, frontFieldID);
			        val = interpolateInternal((PAWNS_PASSED_O[rank] * dist_op_f * 0) / (7 * 100), (PAWNS_PASSED_E[rank] * dist_op_f * 95) / (7 * 100), openingPart);
			        signals.getSignal(FEATURE_ID_PAWNS_KING_OP_F).addStrength(-val, openingPart);
				}
			}
		}
		
		
		int b_pawns_count = model.getBCount();
		if (b_pawns_count > 0) {
			
			Pawn[] b_pawns = model.getBPawns();
			
			for (int i=0; i<b_pawns_count; i++) {
				
				Pawn p = b_pawns[i];
				
				if (p.isGuard() && p.getRank() <= 3) {
					int scores = 0;
					switch(p.getRank()) {
						case 1:
							scores = 4;
							break;
						case 2:
							scores = 2;
							break;
						case 3:
							scores = 1;
							break;
						default:
							throw new IllegalStateException();
					}
					
					signals.getSignal(FEATURE_ID_PAWNS_KING_GUARDS).addStrength(-scores, openingPart);
					
				}
				
				
				int rank = p.getRank();
				int file = Fields.LETTERS[p.getFieldID()];
				int file_symmetry = Fields.FILE_SYMMETRY[file];
				
				if (p.isDoubled()) {
					double val = interpolateInternal(PAWNS_DOUBLED_O[file_symmetry], PAWNS_DOUBLED_E[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_DOUBLED).addStrength(-val, openingPart);
				}
				
				if (p.isIsolated()) {
					double val = interpolateInternal(PAWNS_ISOLATED_O[file_symmetry], PAWNS_ISOLATED_E[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_ISOLATED).addStrength(-val, openingPart);
				}
				
				if (p.isBackward()) {
					double val = interpolateInternal(PAWNS_BACKWARD_O[file_symmetry], PAWNS_BACKWARD_O[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_BACKWARD).addStrength(-val, openingPart);
				}
				
				if (p.isSupported() && !p.isPassed()) {
					double val = interpolateInternal(PAWNS_SUPPORTED_O[file_symmetry], PAWNS_SUPPORTED_E[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_SUPPORTED).addStrength(-val, openingPart);
				}
				
				if (p.isCandidate()) {
					double val = interpolateInternal(PAWNS_CANDIDATE_O[file_symmetry], PAWNS_CANDIDATE_E[file_symmetry], openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_CANDIDATE).addStrength(-val, openingPart);
				} else if (p.isPassed()) {
					if (p.isSupported()) {
						double val = interpolateInternal(PAWNS_PASSED_SUPPORTED_O[file_symmetry], PAWNS_PASSED_SUPPORTED_E[file_symmetry], openingPart);
						signals.getSignal(FEATURE_ID_PAWNS_PASSED_SUPPORTED).addStrength(-val, openingPart);
					} else {
						double val = interpolateInternal(PAWNS_PASSED_O[file_symmetry], PAWNS_PASSED_E[file_symmetry], openingPart);
						signals.getSignal(FEATURE_ID_PAWNS_PASSED).addStrength(-val, openingPart);
					}
					
			        // Adjust bonus based on king proximity:
			        int frontFieldID = p.getFieldID() - 8;
			        int frontFrontFieldID = frontFieldID - 8;
			        if (frontFrontFieldID < 0) {
			        	frontFrontFieldID = frontFieldID;
			        }
			        
			        int dist_f = Fields.getDistancePoints_reversed(b_kingID, frontFieldID);
			        double val = interpolateInternal((PAWNS_PASSED_O[rank] * dist_f * 25) / (7 * 100), (PAWNS_PASSED_E[rank] * dist_f * 38) / (7 * 100), openingPart);
			        signals.getSignal(FEATURE_ID_PAWNS_KING_F).addStrength(-val, openingPart);
			        
			        int dist_ff = Fields.getDistancePoints_reversed(b_kingID, frontFrontFieldID);
			        val = interpolateInternal((PAWNS_PASSED_O[rank] * dist_ff * 25) / (7 * 100), (PAWNS_PASSED_E[rank] * dist_ff * 38) / (7 * 100), openingPart);
			        signals.getSignal(FEATURE_ID_PAWNS_KING_FF).addStrength(-val, openingPart);
			        
			        int dist_op_f = Fields.getDistancePoints_reversed(w_kingID, frontFieldID);
			        val = interpolateInternal((PAWNS_PASSED_O[rank] * dist_op_f * 0) / (7 * 100), (PAWNS_PASSED_E[rank] * dist_op_f * 95) / (7 * 100), openingPart);
			        signals.getSignal(FEATURE_ID_PAWNS_KING_OP_F).addStrength(val, openingPart);
			     }
			}
		}
		
		
		int unstoppablePasser = bitboard.getUnstoppablePasser();
		signals.getSignal(FEATURE_ID_PASSED_UNSTOPPABLE).addStrength(unstoppablePasser, openingPart);
		
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
	
	
	public void eval_pawns_RooksAndQueens(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
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
					long front = p.getFront();
					int unsafeFieldsInFront = Utils.countBits_less1s(front & evalInfo.bb_attackedByBlackOnly);
					double val = interpolateInternal((unsafeFieldsInFront * PAWNS_PASSED_O[p.getRank()]) / 8, (unsafeFieldsInFront * PAWNS_PASSED_E[p.getRank()]) / 8, openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_PASSED_STOPPERS).addStrength(-val, openingPart);
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
					double val = interpolateInternal((unsafeFieldsInFront * PAWNS_PASSED_O[p.getRank()]) / 8, (unsafeFieldsInFront * PAWNS_PASSED_E[p.getRank()]) / 8, openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_PASSED_STOPPERS).addStrength(val, openingPart);
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
		
		
		signals.getSignal(FEATURE_ID_PAWNS_ROOK_OPENED).addStrength(rooks_opened, openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_ROOK_SEMIOPENED).addStrength(rooks_semiopened, openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_ROOK_7TH2TH).addStrength(rooks_7th2th, openingPart);
		
		
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
		
		
		signals.getSignal(FEATURE_ID_PAWNS_QUEEN_7TH2TH).addStrength(queens_7th2th, openingPart);
		
		
		int kingOpened = 0;
		if (b_rooks.getDataSize() > 0 || b_queens.getDataSize() > 0) {
			kingOpened += evalInfo.w_kingOpened;
		}
	    if (w_rooks.getDataSize() > 0 || w_queens.getDataSize() > 0) {
	    	kingOpened -= evalInfo.b_kingOpened;
	    }
	    signals.getSignal(FEATURE_ID_PAWNS_KING_OPENED).addStrength(kingOpened, openingPart);
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
	
	
	private void eval_mobility(ISignals signals) {
		
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		
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
				signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT).addStrength(interpolateInternal(MOBILITY_KNIGHT_O[mob], MOBILITY_KNIGHT_E[mob], openingPart), openingPart);
			    
			    
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
			    		
					    signals.getSignal(FEATURE_ID_KNIGHT_OUTPOST).addStrength(interpolateInternal(scores * KNIGHT_OUTPOST_O[fieldID], scores * KNIGHT_OUTPOST_E[fieldID], openingPart), openingPart);
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
			    signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT).addStrength(-interpolateInternal(MOBILITY_KNIGHT_O[mob], MOBILITY_KNIGHT_E[mob], openingPart), openingPart);
			    
			    
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
				    	
					    signals.getSignal(FEATURE_ID_KNIGHT_OUTPOST).addStrength(-interpolateInternal(scores * KNIGHT_OUTPOST_O[Fields.HORIZONTAL_SYMMETRY[fieldID]], scores * KNIGHT_OUTPOST_E[Fields.HORIZONTAL_SYMMETRY[fieldID]], openingPart), openingPart);
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
			    signals.getSignal(FEATURE_ID_MOBILITY_BISHOP).addStrength(interpolateInternal(MOBILITY_BISHOP_O[mob], MOBILITY_BISHOP_E[mob], openingPart), openingPart);
			    
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
			    		
					    signals.getSignal(FEATURE_ID_BISHOP_OUTPOST).addStrength(interpolateInternal(scores * BISHOP_OUTPOST_O[fieldID], scores * BISHOP_OUTPOST_E[fieldID], openingPart), openingPart);
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
			    
			    signals.getSignal(FEATURE_ID_BISHOP_BAD).addStrength(-interpolateInternal(bad_bishop_score, bad_bishop_score, openingPart), openingPart);
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
			    signals.getSignal(FEATURE_ID_MOBILITY_BISHOP).addStrength(-interpolateInternal(MOBILITY_BISHOP_O[mob], MOBILITY_BISHOP_E[mob], openingPart), openingPart);
			    
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
			    		
					    signals.getSignal(FEATURE_ID_BISHOP_OUTPOST).addStrength(-interpolateInternal(scores * BISHOP_OUTPOST_O[fieldID], scores * BISHOP_OUTPOST_E[fieldID], openingPart), openingPart);
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
			    
			    signals.getSignal(FEATURE_ID_BISHOP_BAD).addStrength(interpolateInternal(bad_bishop_score, bad_bishop_score, openingPart), openingPart);
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
			    signals.getSignal(FEATURE_ID_MOBILITY_ROOK).addStrength(interpolateInternal(MOBILITY_ROOK_O[mob], MOBILITY_ROOK_E[mob], openingPart), openingPart);
			    
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
			    signals.getSignal(FEATURE_ID_MOBILITY_ROOK).addStrength(-interpolateInternal(MOBILITY_ROOK_O[mob], MOBILITY_ROOK_E[mob], openingPart), openingPart);
			    
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
			    signals.getSignal(FEATURE_ID_MOBILITY_QUEEN).addStrength(interpolateInternal(MOBILITY_QUEEN_O[mob], MOBILITY_QUEEN_E[mob], openingPart), openingPart);
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
			    signals.getSignal(FEATURE_ID_MOBILITY_QUEEN).addStrength(-interpolateInternal(MOBILITY_QUEEN_O[mob], MOBILITY_QUEEN_E[mob], openingPart), openingPart);
			}
		}
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
	
	
	private void eval_king_safety(ISignals signals) {

		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		//KING SAFETY
		
		//int fieldID = w_king.getData()[0];
		
		int kingSafety = 0;
			     
	    // King safety.
	    if(//b_queens.getDataSize() >= 1
	    		//&&evalInfo.attackCount[Figures.COLOUR_BLACK] >= 2
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
	        
	        signals.getSignal(FEATURE_ID_KING_SAFETY).addStrength(interpolateInternal(KING_SAFETY[attackUnits], KING_SAFETY[attackUnits], openingPart), openingPart);
	    }
	    
	    
	    //int fieldID = b_king.getData()[0];
		//long bb_field = Fields.ALL_A1H1[fieldID];
	    
	    // King safety.
	    if(//w_queens.getDataSize() >= 1
	    		//&& evalInfo.attackCount[Figures.COLOUR_WHITE] >= 2
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
	        signals.getSignal(FEATURE_ID_KING_SAFETY).addStrength(-interpolateInternal(KING_SAFETY[attackUnits], KING_SAFETY[attackUnits], openingPart), openingPart);
	    }
	    
	    evalInfo.eval_Kingsafety_o += kingSafety;
	    evalInfo.eval_Kingsafety_e += kingSafety;
	}
	
	
	private void eval_space(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
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
		
		signals.getSignal(FEATURE_ID_SPACE).addStrength(interpolateInternal(space, space, openingPart), openingPart);
	}
	
	
	private void eval_hunged(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
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
			signals.getSignal(FEATURE_ID_HUNGED).addStrength(interpolateInternal(HUNGED_O[w_hungedCount], HUNGED_E[w_hungedCount], openingPart), openingPart);
		} else {
			signals.getSignal(FEATURE_ID_HUNGED).addStrength(-interpolateInternal(HUNGED_O[b_hungedCount], HUNGED_E[b_hungedCount], openingPart), openingPart);
		}
	}
	
	
	public void eval_TrapsAndSafeMobility(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		int trapped_all = 0;
		
		int w_knights_count = w_knights.getDataSize();
		if (w_knights_count > 0) {
			int[] w_knights_fields = w_knights.getData();
			for (int i=0; i<w_knights_count; i++) {
				int fieldID = w_knights_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
				
			    int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_w_minors);
			    signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT_S).addStrength(interpolateInternal(MOBILITY_KNIGHT_S_O[mob_safe], MOBILITY_KNIGHT_S_E[mob_safe], openingPart), openingPart);
			    
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
				signals.getSignal(FEATURE_ID_MOBILITY_BISHOP_S).addStrength(interpolateInternal(MOBILITY_BISHOP_S_O[mob_safe], MOBILITY_BISHOP_S_E[mob_safe], openingPart), openingPart);
				
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
				signals.getSignal(FEATURE_ID_MOBILITY_ROOK_S).addStrength(interpolateInternal(MOBILITY_ROOK_S_O[mob_safe], MOBILITY_ROOK_S_E[mob_safe], openingPart), openingPart);
				
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
				signals.getSignal(FEATURE_ID_MOBILITY_QUEEN_S).addStrength(interpolateInternal(MOBILITY_QUEEN_S_O[mob_safe], MOBILITY_QUEEN_S_E[mob_safe], openingPart), openingPart);
				
			    int rank = Fields.DIGITS[fieldID];
			    int trapped = getTrappedScores(mob_safe, 9);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		
		
		//BLACK
		
		
		int b_knights_count = b_knights.getDataSize();
		if (b_knights_count > 0) {
			int[] b_knights_fields = b_knights.getData();
			for (int i=0; i<b_knights_count; i++) {
				int fieldID = b_knights_fields[i];
				long attacks = evalInfo.attacksByFieldID[fieldID];
				
			    int mob_safe = Utils.countBits(attacks & ~evalInfo.bb_unsafe_for_b_minors);			    
			    signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT_S).addStrength(-interpolateInternal(MOBILITY_KNIGHT_S_O[mob_safe], MOBILITY_KNIGHT_S_E[mob_safe], openingPart), openingPart);
			    
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
				signals.getSignal(FEATURE_ID_MOBILITY_BISHOP_S).addStrength(-interpolateInternal(MOBILITY_BISHOP_S_O[mob_safe], MOBILITY_BISHOP_S_E[mob_safe], openingPart), openingPart);
				
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
				signals.getSignal(FEATURE_ID_MOBILITY_ROOK_S).addStrength(-interpolateInternal(MOBILITY_ROOK_S_O[mob_safe], MOBILITY_ROOK_S_E[mob_safe], openingPart), openingPart);
				
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
				signals.getSignal(FEATURE_ID_MOBILITY_QUEEN_S).addStrength(-interpolateInternal(MOBILITY_QUEEN_S_O[mob_safe], MOBILITY_QUEEN_S_E[mob_safe], openingPart), openingPart);
				
				int rank = 7 - Fields.DIGITS[fieldID];
			    int trapped = -getTrappedScores(mob_safe, 9);
			    trapped_all += (7 + rank) * trapped;
			}
		}
		
		
		trapped_all /= 2;
		
		signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT_S).addStrength(interpolateInternal(-trapped_all, -trapped_all, openingPart), openingPart);
	}
	
	
	private void eval_PassersFrontAttacks(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
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
					signals.getSignal(FEATURE_ID_PASSERS_FRONT_ATTACKS).addStrength(-interpolateInternal((unsafeFieldsInFront * PAWNS_PASSED_O[p.getRank()]) / 8, (unsafeFieldsInFront * PAWNS_PASSED_E[p.getRank()]) / 8, openingPart), openingPart);
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
					signals.getSignal(FEATURE_ID_PASSERS_FRONT_ATTACKS).addStrength(interpolateInternal((unsafeFieldsInFront * PAWNS_PASSED_O[p.getRank()]) / 8, (unsafeFieldsInFront * PAWNS_PASSED_E[p.getRank()]) / 8, openingPart), openingPart);
				}
			}
		}
		
		bitboard.getPawnsCache().unlock();
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
	
	protected int eval_patterns(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		int minor_trap = 0;
		int blocked_pawns = 0;
		
		long w_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
		long b_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);
		long w_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT);
		long b_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT);
		long w_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		long b_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
		
		
		
		// trapedBishopsA7H7
		
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
		
		
		
		// trapedBishopsA6H6
		
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
		
		
		// trapedKnightsA1H1
		
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
		
		
		// blockedPawnsOnD2E2
		
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
		
		signals.getSignal(FEATURE_ID_STANDARD_TRAP_BISHOP).addStrength(minor_trap, openingPart);
		signals.getSignal(FEATURE_ID_STANDARD_BLOCKED_PAWN).addStrength(blocked_pawns, openingPart);
		
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

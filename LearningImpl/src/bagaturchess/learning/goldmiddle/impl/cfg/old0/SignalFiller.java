package bagaturchess.learning.goldmiddle.impl.cfg.old0;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.SeeMetadata;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnStructureConstants;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingSurrounding;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;


public class SignalFiller extends SignalFillerConstants implements FeaturesConstants, ISignalFiller {
	
	
	private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial ( new int[]{	
			   0,   1,   2,   3,   4,   5,   6,   7,
			   8,   9,  10,  11,  12,  13,  14,  15,
			  16,  17,  18,  19,  20,  21,  22,  23,
			  24,  25,  26,  27,  28,  29,  30,  31,
			  32,  33,  34,  35,  36,  37,  38,  39,
			  40,  41,  42,  43,  44,  45,  46,  47,
			  48,  49,  50,  51,  52,  53,  54,  55,
			  56,  57,  58,  59,  60,  61,  62,  63,

	});
	
	
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
	
	
	public SignalFiller(IBitBoard _bitboard) {
		
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
	}
	
	
	private static final int axisSymmetry(int fieldID) {
		return HORIZONTAL_SYMMETRY[fieldID];
	}
	
	
	public void fill(ISignals signals) {
		fillStandardSignals(signals);
		fillPawnSignals(signals);
		fillPiecesIterationSignals(signals);
		fillMovesIterationSignals(signals);
		//fillFieldsStatesIterationSignals(signals);
	}
	
	
	public void fillByComplexity(int complexity, ISignals signals) {
		switch(complexity) {
			case IFeatureComplexity.GROUP1:
				fillStandardSignals(signals);
				return;
			case IFeatureComplexity.GROUP2:
				fillPawnSignals(signals);
				return;
			case IFeatureComplexity.GROUP3:
				fillPiecesIterationSignals(signals);
				return;
			case IFeatureComplexity.GROUP4:
				fillMovesIterationSignals(signals);
				return;
			case IFeatureComplexity.GROUP5:
				//fillFieldsStatesIterationSignals(signals);
				//throw new IllegalStateException();
				return;
			default:
				throw new IllegalStateException("complexity=" + complexity);
		}
	}
	
	
	public void fillStandardSignals(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		signals.getSignal(FEATURE_ID_MATERIAL_PAWN).addStrength(w_pawns.getDataSize() - b_pawns.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_KNIGHT).addStrength(w_knights.getDataSize() - b_knights.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_BISHOP).addStrength(w_bishops.getDataSize() - b_bishops.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_ROOK).addStrength(w_rooks.getDataSize() - b_rooks.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_MATERIAL_QUEEN).addStrength(w_queens.getDataSize() - b_queens.getDataSize(), openingPart);
		
		signals.getSignal(FEATURE_ID_BISHOPS_DOUBLE).addStrength(((w_bishops.getDataSize() >= 2) ? 1 : 0) - ((b_bishops.getDataSize() >= 2) ? 1 : 0), openingPart);
		signals.getSignal(FEATURE_ID_KNIGHTS_DOUBLE).addStrength(((w_knights.getDataSize() >= 2) ? 1 : 0) - ((b_knights.getDataSize() >= 2) ? 1 : 0), openingPart);
		signals.getSignal(FEATURE_ID_ROOKS_DOUBLE).addStrength(((w_rooks.getDataSize() >= 2) ? 1 : 0) - ((b_rooks.getDataSize() >= 2) ? 1 : 0), openingPart);
		
		signals.getSignal(FEATURE_ID_KINGSAFE_CASTLING).addStrength(castling(Figures.COLOUR_WHITE) - castling(Figures.COLOUR_BLACK), openingPart);
		fianchetto(signals);
		movedFGPawns(signals);
		
		
		//Kings Distance
		int kingFieldID_white = w_king.getData()[0];
		int kingFieldID_black = b_king.getData()[0];
		int kingDistance = Fields.getDistancePoints(kingFieldID_white, kingFieldID_black);
		int kingsDistanceScores = bitboard.getMaterialFactor().interpolateByFactor(KING_DISTANCE_O[kingDistance], KING_DISTANCE_E[kingDistance]);
		if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
			signals.getSignal(FEATURE_ID_KINGS_DISTANCE).addStrength(kingsDistanceScores, openingPart);
		} else {
			signals.getSignal(FEATURE_ID_KINGS_DISTANCE).addStrength(-kingsDistanceScores, openingPart);
		}
		
		//Refers to http://home.comcast.net/~danheisman/Articles/evaluation_of_material_imbalance.htm
		//
		//A further refinement would be to raise the knight's value by 1/16 and lower the rook's value by 1/8
		//for each pawn above five of the side being valued, with the opposite adjustment for each pawn short of five.
		int w_pawns_above5 = w_pawns.getDataSize() - 5;
		int b_pawns_above5 = b_pawns.getDataSize() - 5;
		
		signals.getSignal(FEATURE_ID_5PAWNS_ROOKS).addStrength(w_pawns_above5 * w_rooks.getDataSize() - b_pawns_above5 * b_rooks.getDataSize(), openingPart);
		signals.getSignal(FEATURE_ID_5PAWNS_KNIGHTS).addStrength(w_pawns_above5 * w_knights.getDataSize() - b_pawns_above5 * b_knights.getDataSize(), openingPart);
	}
	
	
	private void movedFGPawns(ISignals signals) {
		
		long bb_white_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		long bb_black_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);

		
		IBoard.CastlingType w_cast_type = bitboard.getCastlingType(Figures.COLOUR_WHITE);
		IBoard.CastlingType b_cast_type = bitboard.getCastlingType(Figures.COLOUR_BLACK);
		
		int movedFPawn = 0;
		int missingGPawn = 0;
		if (bitboard.hasRightsToKingCastle(Figures.COLOUR_WHITE)
			|| w_cast_type == IBoard.CastlingType.KINGSIDE) {
			movedFPawn += (Fields.F2 & bb_white_pawns) == 0L ? 1 : 0;
			missingGPawn += (Fields.LETTER_G & bb_white_pawns) == 0L ? 1 : 0;
		}
		if (bitboard.hasRightsToKingCastle(Figures.COLOUR_BLACK)
				|| b_cast_type == IBoard.CastlingType.KINGSIDE) {
			movedFPawn += ((Fields.F7 & bb_black_pawns) == 0L ? -1 : 0);
			missingGPawn += (Fields.LETTER_G & bb_black_pawns) == 0L ? -1 : 0;
		}
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();

		signals.getSignal(FEATURE_ID_KINGSAFE_F_PAWN).addStrength(movedFPawn, openingPart);
		signals.getSignal(FEATURE_ID_KINGSAFE_G_PAWN).addStrength(missingGPawn, openingPart);
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
	
	
	private void fianchetto(ISignals signals) {
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
		
		double opening_part = bitboard.getMaterialFactor().getOpenningPart();
		signals.getSignal(FEATURE_ID_KINGSAFE_FIANCHETTO).addStrength(w_fianchetto - b_fianchetto, opening_part);
	}
	
	
	public void fillPawnSignals(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		long bb_w_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE);
		long bb_b_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE);
		
		bitboard.getPawnsCache().lock();
		
		PawnsModelEval pawnsModelEval = bitboard.getPawnsStructure();
		PawnsModel model = pawnsModelEval.getModel();
		
		int w_kingID = model.getWKingFieldID();
		int b_kingID = model.getBKingFieldID();

		
		Pawn[] w_pawns_m = model.getWPawns();
		int w_count = model.getWCount();
		if (w_count != w_pawns.getDataSize()) {
			throw new IllegalStateException();
		}
		
		
		int w_doubled = 0;
		int w_isolated = 0;
		int w_backward = 0;
		int w_supported = 0;
		int w_cannotbs = 0;
		int w_passed = 0;
		
		int b_doubled = 0;
		int b_isolated = 0;
		int b_backward = 0;
		int b_supported = 0;
		int b_cannotbs = 0;
		int b_passed = 0;
		
		if (w_count > 0) {
			
			for (int i=0; i<w_count; i++) {
				
				Pawn p = w_pawns_m[i];
				
				//int fieldID = p.getFieldID();
				
				if (p.isPassed()) {
					w_passed++;
					
					int rank = p.getRank();
					int stoppersCount = Utils.countBits(p.getFront() & ~bitboard.getFreeBitboard());
					rank = rank - stoppersCount;
					if (rank <= 0) {
						rank = 1;
					}
					
					int passer = bitboard.getMaterialFactor().interpolateByFactor(PASSERS_RANK_O[rank], PASSERS_RANK_E[rank]);
					signals.getSignal(FEATURE_ID_PAWNS_PASSED_RNK).addStrength(passer, openingPart);
					
					long front = p.getFront();
					if ((front & bb_w_rooks) != 0L) {
						signals.getSignal(FEATURE_ID_ROOK_INFRONT_PASSER).addStrength(1, openingPart);
					}
					
					long behind = p.getVertical() & ~front;
					if ((behind & bb_w_rooks) != 0L) {
						signals.getSignal(FEATURE_ID_ROOK_BEHIND_PASSER).addStrength(1, openingPart);
					}
					
			        int frontFieldID = p.getFieldID() + 8;
			        int frontFrontFieldID = frontFieldID + 8;
			        if (frontFrontFieldID >= 64) {
			        	frontFrontFieldID = frontFieldID;
			        }
			        
			        int dist_f = Fields.getDistancePoints(w_kingID, frontFieldID);
			        signals.getSignal(FEATURE_ID_KING_PASSERS_F).addStrength(rank * PASSERS_KING_CLOSENESS_FRONTFIELD[dist_f], openingPart);
			        
			        int dist_ff = Fields.getDistancePoints(w_kingID, frontFrontFieldID);
			        signals.getSignal(FEATURE_ID_KING_PASSERS_FF).addStrength(rank * PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[dist_ff], openingPart);
			        
			        int dist_op_f = Fields.getDistancePoints(b_kingID, frontFieldID);
			        signals.getSignal(FEATURE_ID_KING_PASSERS_F_OP).addStrength(rank * PASSERS_KING_CLOSENESS_FRONTFIELD_OP[dist_op_f], openingPart);
				}
				
				if (p.isCandidate()) {
					int rank = p.getRank();
					int passerCandidate = bitboard.getMaterialFactor().interpolateByFactor(PASSERS_CANDIDATE_RANK_O[rank], PASSERS_CANDIDATE_RANK_E[rank]);
					signals.getSignal(FEATURE_ID_PAWNS_CANDIDATE).addStrength(passerCandidate, openingPart);
				}
				
				if (p.isDoubled()) {
					w_doubled++;
				}
				
				if (p.isIsolated()) {
					w_isolated++;
				}
				
				if (p.isBackward()) {
					w_backward++;
				}
				
				if (p.isSupported()) {
					w_supported++;
				} else if (p.cannotBeSupported()) {
					w_cannotbs++;
				}
				
				if (p.isGuard()) {
					signals.getSignal(FEATURE_ID_PAWNS_GARDS).addStrength(1, openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_GARDS_REM).addStrength(p.getGuardRemoteness(), openingPart);
				}
				
				if (p.isStorm()) {
					signals.getSignal(FEATURE_ID_PAWNS_STORMS).addStrength(1, openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_STORMS_CLS).addStrength(8 - p.getStormCloseness(), openingPart);
				}
			}
		}
		
		Pawn[] b_pawns_m = model.getBPawns();
		int b_count = model.getBCount();
		if (b_count != b_pawns.getDataSize()) {
			throw new IllegalStateException();
		}
		if (b_count > 0) {
			
			for (int i=0; i<b_count; i++) {
				
				Pawn p = b_pawns_m[i];
				
				//int fieldID = axisSymmetry(p.getFieldID());
				
				if (p.isPassed()) {
					b_passed++;
					
					int rank = p.getRank();
					int stoppersCount = Utils.countBits(p.getFront() & ~bitboard.getFreeBitboard());
					rank = rank - stoppersCount;
					if (rank <= 0) {
						rank = 1;
					}
					
					int passer = bitboard.getMaterialFactor().interpolateByFactor(PASSERS_RANK_O[rank], PASSERS_RANK_E[rank]);
					signals.getSignal(FEATURE_ID_PAWNS_PASSED_RNK).addStrength(-passer, openingPart);
					
					long front = p.getFront();
					if ((front & bb_b_rooks) != 0L) {
						signals.getSignal(FEATURE_ID_ROOK_INFRONT_PASSER).addStrength(-1, openingPart);
					}
					
					long behind = p.getVertical() & ~front;
					if ((behind & bb_b_rooks) != 0L) {
						signals.getSignal(FEATURE_ID_ROOK_BEHIND_PASSER).addStrength(-1, openingPart);
					}
					
			        int frontFieldID = p.getFieldID() - 8;
			        int frontFrontFieldID = frontFieldID - 8;
			        if (frontFrontFieldID < 0) {
			        	frontFrontFieldID = frontFieldID;
			        }
			        
			        int dist_f = Fields.getDistancePoints(b_kingID, frontFieldID);
			        signals.getSignal(FEATURE_ID_KING_PASSERS_F).addStrength( - rank * PASSERS_KING_CLOSENESS_FRONTFIELD[dist_f], openingPart);
			        
			        int dist_ff = Fields.getDistancePoints(b_kingID, frontFrontFieldID);
			        signals.getSignal(FEATURE_ID_KING_PASSERS_FF).addStrength( - rank * PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[dist_ff], openingPart);
			        
			        int dist_op_f = Fields.getDistancePoints(w_kingID, frontFieldID);
			        signals.getSignal(FEATURE_ID_KING_PASSERS_F_OP).addStrength( - rank * PASSERS_KING_CLOSENESS_FRONTFIELD_OP[dist_op_f], openingPart);
				}
				
				if (p.isCandidate()) {
					int rank = p.getRank();
					int passerCandidate = bitboard.getMaterialFactor().interpolateByFactor(PASSERS_CANDIDATE_RANK_O[rank], PASSERS_CANDIDATE_RANK_E[rank]);
					signals.getSignal(FEATURE_ID_PAWNS_CANDIDATE).addStrength(-passerCandidate, openingPart);
				}
				
				if (p.isDoubled()) {
					b_doubled++;
				}
				
				if (p.isIsolated()) {
					b_isolated++;
				}
				
				if (p.isBackward()) {
					b_backward++;
				}
				
				if (p.isSupported()) {
					b_supported++;
				} else if (p.cannotBeSupported()) {
					b_cannotbs++;
				}
				
				if (p.isGuard()) {
					signals.getSignal(FEATURE_ID_PAWNS_GARDS).addStrength(-1, openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_GARDS_REM).addStrength(-p.getGuardRemoteness(), openingPart);
				}
				
				if (p.isStorm()) {
					signals.getSignal(FEATURE_ID_PAWNS_STORMS).addStrength(-1, openingPart);
					signals.getSignal(FEATURE_ID_PAWNS_STORMS_CLS).addStrength(- (8 - p.getStormCloseness()), openingPart);
				}
			}
		}
		
		
		signals.getSignal(FEATURE_ID_PAWNS_OPENNED).addStrength(model.getWKingOpenedFiles() - model.getBKingOpenedFiles(), openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_SEMIOP_OWN).addStrength(model.getWKingSemiOwnOpenedFiles() - model.getBKingSemiOwnOpenedFiles(), openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_SEMIOP_OP).addStrength(model.getWKingSemiOpOpenedFiles() - model.getBKingSemiOpOpenedFiles(), openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_WEAK).addStrength(model.getWWeakFields() - model.getBWeakFields(), openingPart);
		
		signals.getSignal(FEATURE_ID_PAWNS_DOUBLED).addStrength(w_doubled - b_doubled, openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_ISOLATED).addStrength(w_isolated - b_isolated, openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_BACKWARD).addStrength(w_backward - b_backward, openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_SUPPORTED).addStrength(w_supported - b_supported, openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_CANNOTBS).addStrength(w_cannotbs - b_cannotbs, openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_PASSED).addStrength(w_passed - b_passed, openingPart);
		signals.getSignal(FEATURE_ID_PAWNS_ISLANTS).addStrength(pawnsModelEval.getModel().getWIslandsCount() - pawnsModelEval.getModel().getBIslandsCount(), openingPart);
		
		space(model, signals);
		
		/**
		 * Unstoppable passer
		 */
		//int PAWNS_PASSED_UNSTOPPABLE = 100 + bitboard.getBaseEvaluation().getMaterialRook();
		int unstoppablePasser = bitboard.getUnstoppablePasser();
		if (unstoppablePasser > 0) {
			signals.getSignal(FEATURE_ID_UNSTOPPABLE_PASSER).addStrength(1/*PAWNS_PASSED_UNSTOPPABLE*/, openingPart);
		} else if (unstoppablePasser < 0) {
			signals.getSignal(FEATURE_ID_UNSTOPPABLE_PASSER).addStrength(-1/*-PAWNS_PASSED_UNSTOPPABLE*/, openingPart);
		}
		
		
		bitboard.getPawnsCache().unlock();
	}
	
	
	private void space(PawnsModel model, ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
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
		
		signals.getSignal(FEATURE_ID_SPACE).addStrength(space, openingPart);
	}
	
	
	public void fillPiecesIterationSignals(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		long bb_white_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		long bb_black_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
		long bb_white_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
		long bb_black_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);

		int kingFieldID_white = w_king.getData()[0];
		int kingFieldID_black = b_king.getData()[0];
		
		int w_pawns_on_w_squares = Utils.countBits(bb_white_pawns & Fields.ALL_WHITE_FIELDS);
		int w_pawns_on_b_squares = Utils.countBits(bb_white_pawns & Fields.ALL_BLACK_FIELDS);
		int b_pawns_on_w_squares = Utils.countBits(bb_black_pawns & Fields.ALL_WHITE_FIELDS);
		int b_pawns_on_b_squares = Utils.countBits(bb_black_pawns & Fields.ALL_BLACK_FIELDS);

		bitboard.getPawnsCache().lock();
		PawnsModelEval pawnsModelEval = bitboard.getPawnsStructure();
		long openedFiles_all = pawnsModelEval.getModel().getOpenedFiles();
		long openedFiles_white = pawnsModelEval.getModel().getWHalfOpenedFiles();
		long openedFiles_black = pawnsModelEval.getModel().getBHalfOpenedFiles();
		bitboard.getPawnsCache().unlock();
		
		long RANK_7TH = Fields.DIGIT_7;
		long RANK_2TH = Fields.DIGIT_2;
		
		int w_tropism_knights = 0;
		int b_tropism_knights = 0;
		int w_tropism_bishops = 0;
		int b_tropism_bishops = 0;
		int w_tropism_rooks = 0;
		int b_tropism_rooks = 0;
		int w_tropism_queens = 0;
		int b_tropism_queens = 0;

		int w_centred_pawns = 0;
		int b_centred_pawns = 0;
		int w_centred_king = 0;
		int b_centred_king = 0;
		int w_centred_knights = 0;
		int b_centred_knights = 0;
		int w_centred_bishops = 0;
		int b_centred_bishops = 0;
		int w_centred_rooks = 0;
		int b_centred_rooks = 0;
		int w_centred_queens = 0;
		int b_centred_queens = 0;
		
		int w_bad_bishops = 0;
		int b_bad_bishops = 0;
		
		int w_knight_outpost = 0;
		int b_knight_outpost = 0;
		
		int w_rooks_opened = 0;
		int b_rooks_opened = 0;
		int w_rooks_semiopened = 0;
		int b_rooks_semiopened = 0;
		int w_rooks_7th = 0;
		int b_rooks_2th = 0;
		
		int w_queens_7th = 0;
		int b_queens_2th = 0;
		
		int w_lettersSum = 0;
		int w_digitsSum = 0;
		int w_piecesCount = 0;
		//int w_letterMiddle = 0;
		//int w_digitMiddle = 0;
		//int w_piecesDispersion = 0;
		
		int b_lettersSum = 0;
		int b_digitsSum = 0;
		int b_piecesCount = 0;
		//int b_letterMiddle = 0;
		//int b_digitMiddle = 0;
		//int b_piecesDispersion = 0;
		
		
		/**
		 * Knights iteration
		 */
		{
			int w_knights_count = w_knights.getDataSize();
			if (w_knights_count > 0) {
				int[] knights_fields = w_knights.getData();
				for (int i=0; i<w_knights_count; i++) {
					
					
					int fieldID = knights_fields[i];
					long fieldBB = Fields.ALL_A1H1[fieldID];
					
					
					int pst = bitboard.getMaterialFactor().interpolateByFactor(KNIGHT_O[fieldID], KNIGHT_E[fieldID]);
					signals.getSignal(FEATURE_ID_PST_KNIGHT).addStrength(pst, openingPart);
					w_tropism_knights += Fields.getTropismPoint(fieldID, kingFieldID_black);
					w_centred_knights += Fields.getCenteredPoint(fieldID);
					
					w_lettersSum += Fields.LETTERS[fieldID];
					w_digitsSum += Fields.DIGITS[fieldID];
					w_piecesCount++;
					
				    // Knight outposts:
				    if ((Fields.SPACE_BLACK & fieldBB) != 0) {
					    long bb_neighbors = ~PawnStructureConstants.WHITE_FRONT_FULL[fieldID] & PawnStructureConstants.WHITE_PASSED[fieldID];
					    if ((bb_neighbors & bb_black_pawns) == 0) { // Weak field
					    	
					    	w_knight_outpost += 1;
					    	
				    		if ((BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_MOVES[fieldID] & bb_white_pawns) != 0) {
				    			w_knight_outpost += 1;
				    			if (b_knights.getDataSize() == 0) {
				    				long colouredFields = (fieldBB & Fields.ALL_WHITE_FIELDS) != 0 ?
				    						Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
				    				if ((colouredFields & bb_black_bishops) == 0) {
				    					w_knight_outpost += 1;
				    				}
				    			}
				    		}
					    }
				    }
				}
			}
		}
		
		{
			int b_knights_count = b_knights.getDataSize();		
			if (b_knights_count > 0) {
				int[] knights_fields = b_knights.getData();
				for (int i=0; i<b_knights_count; i++) {
					
					
					int fieldID = knights_fields[i];
					long fieldBB = Fields.ALL_A1H1[fieldID];
					
					int pst = bitboard.getMaterialFactor().interpolateByFactor(KNIGHT_O[axisSymmetry(fieldID)], KNIGHT_E[axisSymmetry(fieldID)]);
					signals.getSignal(FEATURE_ID_PST_KNIGHT).addStrength(-pst, openingPart);
					b_tropism_knights += Fields.getTropismPoint(fieldID, kingFieldID_white);
					b_centred_knights += Fields.getCenteredPoint(fieldID);
					
					b_lettersSum += Fields.LETTERS[fieldID];
					b_digitsSum += Fields.DIGITS[fieldID];
					b_piecesCount++;
					
				    // Knight outposts:
				    if ((Fields.SPACE_WHITE & fieldBB) != 0) {
					    long bb_neighbors = ~PawnStructureConstants.BLACK_FRONT_FULL[fieldID] & PawnStructureConstants.BLACK_PASSED[fieldID];
					    if ((bb_neighbors & bb_white_pawns) == 0) { // Weak field
					      
					    	b_knight_outpost += 1;

					    	if ((WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_MOVES[fieldID] & bb_black_pawns) != 0) {
					    		b_knight_outpost += 1;
				    			if (w_knights.getDataSize() == 0) {
				    				long colouredFields = (fieldBB & Fields.ALL_WHITE_FIELDS) != 0 ?
				    						Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
				    				if ((colouredFields & bb_white_bishops) == 0) {
				    					b_knight_outpost += 1;
				    				}
				    			}
				    		}
					    }
				    }
				}
			}
		}
		
		/**
		 * Bishops iteration
		 */
		{
			int w_bishops_count = w_bishops.getDataSize();
			if (w_bishops_count > 0) {
				int[] bishops_fields = w_bishops.getData();
				for (int i=0; i<w_bishops_count; i++) {
					
					
					int fieldID = bishops_fields[i];
					
					int pst = bitboard.getMaterialFactor().interpolateByFactor(BISHOP_O[fieldID], BISHOP_E[fieldID]);
					signals.getSignal(FEATURE_ID_PST_BISHOP).addStrength(pst, openingPart);
					w_tropism_bishops += Fields.getTropismPoint(fieldID, kingFieldID_black);
					w_centred_bishops += Fields.getCenteredPoint(fieldID);
					
					if ((Fields.ALL_WHITE_FIELDS & Fields.ALL_A1H1[fieldID]) != 0L) {
						w_bad_bishops += w_pawns_on_w_squares;
					} else {
						w_bad_bishops += w_pawns_on_b_squares;
					}
					
					w_lettersSum += Fields.LETTERS[fieldID];
					w_digitsSum += Fields.DIGITS[fieldID];
					w_piecesCount++;
				}
			}
		}
		
		{
			int b_bishops_count = b_bishops.getDataSize();
			if (b_bishops_count > 0) {
				int[] bishops_fields = b_bishops.getData();
				for (int i=0; i<b_bishops_count; i++) {
					
					int fieldID = bishops_fields[i];
					
					int pst = bitboard.getMaterialFactor().interpolateByFactor(BISHOP_O[axisSymmetry(fieldID)], BISHOP_E[axisSymmetry(fieldID)]);
					signals.getSignal(FEATURE_ID_PST_BISHOP).addStrength(-pst, openingPart);
					b_tropism_bishops += Fields.getTropismPoint(fieldID, kingFieldID_white);
					b_centred_bishops += Fields.getCenteredPoint(fieldID);
					
					if ((Fields.ALL_WHITE_FIELDS & Fields.ALL_A1H1[fieldID]) != 0L) {
						b_bad_bishops += b_pawns_on_w_squares;
					} else {
						b_bad_bishops += b_pawns_on_b_squares;
					}
					
					b_lettersSum += Fields.LETTERS[fieldID];
					b_digitsSum += Fields.DIGITS[fieldID];
					b_piecesCount++;
				}
			}
		}
		
		/**
		 * Rooks iteration
		 */
		{
			int w_rooks_count = w_rooks.getDataSize();
			if (w_rooks_count > 0) {
				int[] rooks_fields = w_rooks.getData();
				for (int i=0; i<w_rooks_count; i++) {
					
					int fieldID = rooks_fields[i];
					
					long fieldBitboard = Fields.ALL_A1H1[fieldID];
					if ((fieldBitboard & openedFiles_all) != 0L) {
						w_rooks_opened++;
					} else if ((fieldBitboard & openedFiles_white) != 0L) {
						w_rooks_semiopened++;
					}
					if ((fieldBitboard & RANK_7TH) != 0L) {
						w_rooks_7th++;
					}
					
					int pst = bitboard.getMaterialFactor().interpolateByFactor(ROOK_O[fieldID], ROOK_E[fieldID]);
					signals.getSignal(FEATURE_ID_PST_ROOK).addStrength(pst, openingPart);
					w_tropism_rooks += Fields.getTropismPoint(fieldID, kingFieldID_black);
					w_centred_rooks += Fields.getCenteredPoint(fieldID);
					
					w_lettersSum += Fields.LETTERS[fieldID];
					w_digitsSum += Fields.DIGITS[fieldID];
					w_piecesCount++;
				}
			}
		}
		
		{
			int b_rooks_count = b_rooks.getDataSize();
			if (b_rooks_count > 0) {
				int[] rooks_fields = b_rooks.getData();
				for (int i=0; i<b_rooks_count; i++) {
					
					
					int fieldID = rooks_fields[i];
					
					long fieldBitboard = Fields.ALL_A1H1[fieldID];
					if ((fieldBitboard & openedFiles_all) != 0L) {
						b_rooks_opened++;
					} else if ((fieldBitboard & openedFiles_black) != 0L) {
						b_rooks_semiopened++;
					}
					if ((fieldBitboard & RANK_2TH) != 0L) {
						b_rooks_2th++;
					}
					
					int pst = bitboard.getMaterialFactor().interpolateByFactor(ROOK_O[axisSymmetry(fieldID)], ROOK_E[axisSymmetry(fieldID)]);
					signals.getSignal(FEATURE_ID_PST_ROOK).addStrength(-pst, openingPart);
					b_tropism_rooks += Fields.getTropismPoint(fieldID, kingFieldID_white);
					b_centred_rooks += Fields.getCenteredPoint(fieldID);
					
					b_lettersSum += Fields.LETTERS[fieldID];
					b_digitsSum += Fields.DIGITS[fieldID];
					b_piecesCount++;
				}
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
					
					long fieldBitboard = Fields.ALL_A1H1[fieldID];
					if ((fieldBitboard & RANK_7TH) != 0L) {
						w_queens_7th++;
					}
					
					int pst = bitboard.getMaterialFactor().interpolateByFactor(QUEEN_O[fieldID], QUEEN_E[fieldID]);
					signals.getSignal(FEATURE_ID_PST_QUEEN).addStrength(pst, openingPart);
					w_tropism_queens += Fields.getTropismPoint(fieldID, kingFieldID_black);
					w_centred_queens += Fields.getCenteredPoint(fieldID);
					
					w_lettersSum += Fields.LETTERS[fieldID];
					w_digitsSum += Fields.DIGITS[fieldID];
					w_piecesCount++;
				}
			}
		}
		{
			int b_queens_count = b_queens.getDataSize();
			if (b_queens_count > 0) {
				int[] queens_fields = b_queens.getData();
				for (int i=0; i<b_queens_count; i++) {
					
					int fieldID = queens_fields[i];
					
					long fieldBitboard = Fields.ALL_A1H1[fieldID];
					if ((fieldBitboard & RANK_2TH) != 0L) {
						b_queens_2th++;
					}
					
					int pst = bitboard.getMaterialFactor().interpolateByFactor(QUEEN_O[axisSymmetry(fieldID)], QUEEN_E[axisSymmetry(fieldID)]);
					signals.getSignal(FEATURE_ID_PST_QUEEN).addStrength(-pst, openingPart);
					b_tropism_queens += Fields.getTropismPoint(fieldID, kingFieldID_white);
					b_centred_queens += Fields.getCenteredPoint(fieldID);
					
					b_lettersSum += Fields.LETTERS[fieldID];
					b_digitsSum += Fields.DIGITS[fieldID];
					b_piecesCount++;
				}
			}
		}
		
		
		/**
		 * Kings iteration
		 */
		{
			int w_king_count = w_king.getDataSize();
			if (w_king_count > 0) {
				int[] w_king_fields = w_king.getData();
				for (int i=0; i<w_king_count; i++) {
					int fieldID = w_king_fields[i];
					int pst = bitboard.getMaterialFactor().interpolateByFactor(KING_O[fieldID], KING_E[fieldID]);
					signals.getSignal(FEATURE_ID_PST_KING).addStrength(pst, openingPart);
					w_centred_king += Fields.getCenteredPoint(fieldID);
				}
			}
			int b_king_count = b_king.getDataSize();
			if (b_king_count > 0) {
				int[] b_king_fields = b_king.getData();
				for (int i=0; i<b_king_count; i++) {
					int fieldID = b_king_fields[i];
					int pst = bitboard.getMaterialFactor().interpolateByFactor(KING_O[axisSymmetry(fieldID)], KING_E[axisSymmetry(fieldID)]);
					signals.getSignal(FEATURE_ID_PST_KING).addStrength(-pst, openingPart);
					b_centred_king += Fields.getCenteredPoint(fieldID);
				}
			}
		}
		
		/**
		 * Pawns iteration
		 */
		{
			int w_pawns_count = w_pawns.getDataSize();
			if (w_pawns_count > 0) {
				int[] w_pawns_fields = w_pawns.getData();
				for (int i=0; i<w_pawns_count; i++) {
					int fieldID = w_pawns_fields[i];
					
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
						int pst = bitboard.getMaterialFactor().interpolateByFactor(PAWN_O[fieldID], PAWN_E[fieldID]);
						signals.getSignal(FEATURE_ID_PST_PAWN).addStrength(pst, openingPart);
						w_centred_pawns += Fields.getCenteredPoint(fieldID);
					}
				}
			}
			
			int b_pawns_count = b_pawns.getDataSize();
			if (b_pawns_count > 0) {
				int[] b_pawns_fields = b_pawns.getData();
				for (int i=0; i<b_pawns_count; i++) {
					int fieldID = b_pawns_fields[i];
					
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
						int pst = bitboard.getMaterialFactor().interpolateByFactor(PAWN_O[axisSymmetry(fieldID)], PAWN_E[axisSymmetry(fieldID)]);
						signals.getSignal(FEATURE_ID_PST_PAWN).addStrength(-pst, openingPart);
						b_centred_pawns += Fields.getCenteredPoint(fieldID);
					}
				}
			}
		}
		
		
		signals.getSignal(FEATURE_ID_ROOKS_OPENED).addStrength(w_rooks_opened - b_rooks_opened, openingPart);
		signals.getSignal(FEATURE_ID_ROOKS_SEMIOPENED).addStrength(w_rooks_semiopened - b_rooks_semiopened, openingPart);

		signals.getSignal(FEATURE_ID_BISHOPS_BAD).addStrength(w_bad_bishops - b_bad_bishops, openingPart);
		signals.getSignal(FEATURE_ID_KNIGHTS_OUTPOST).addStrength(w_knight_outpost - b_knight_outpost, openingPart);
		
		signals.getSignal(FEATURE_ID_TROPISM_KNIGHT).addStrength(w_tropism_knights - b_tropism_knights, openingPart);
		signals.getSignal(FEATURE_ID_TROPISM_BISHOP).addStrength(w_tropism_bishops - b_tropism_bishops, openingPart);
		signals.getSignal(FEATURE_ID_TROPISM_ROOK).addStrength(w_tropism_rooks - b_tropism_rooks, openingPart);
		signals.getSignal(FEATURE_ID_TROPISM_QUEEN).addStrength(w_tropism_queens - b_tropism_queens, openingPart);		
		
		signals.getSignal(FEATURE_ID_ROOKS_7TH_2TH).addStrength(w_rooks_7th - b_rooks_2th, openingPart);
		signals.getSignal(FEATURE_ID_QUEENS_7TH_2TH).addStrength(w_queens_7th - b_queens_2th, openingPart);
		
		/*
		signals.getSignal(FEATURE_ID_PIECES_DISPERSION).setStrength(w_piecesDispersion - b_piecesDispersion);
		*/
		
	}
	
	
	public void fillMovesIterationSignals(ISignals signals) {
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		int kingFieldID_white = w_king.getData()[0];
		int kingFieldID_black = b_king.getData()[0];
		
		int w_penetration_op_area = 0;
		int b_penetration_op_area = 0;
		int w_penetration_op_area_safe = 0;
		int b_penetration_op_area_safe = 0;
		int w_penetration_king_area = 0;
		int b_penetration_king_area = 0;
		int w_penetration_king_area_safe = 0;
		int b_penetration_king_area_safe = 0;
		
		int w_mobility_knights_all = 0;
		int b_mobility_knights_all = 0;
		int w_mobility_bishops_all = 0;
		int b_mobility_bishops_all = 0;
		int w_mobility_rooks_all = 0;
		int b_mobility_rooks_all = 0;
		int w_mobility_queens_all = 0;
		int b_mobility_queens_all = 0;
		
		int w_mobility_knights_safe = 0;
		int b_mobility_knights_safe = 0;
		int w_mobility_bishops_safe = 0;
		int b_mobility_bishops_safe = 0;
		int w_mobility_rooks_safe = 0;
		int b_mobility_rooks_safe = 0;
		int w_mobility_queens_safe = 0;
		int b_mobility_queens_safe = 0;
		
		int w_trap_knights = 0;
		int b_trap_knights = 0;
		int w_trap_bishops = 0;
		int b_trap_bishops = 0;
		int w_trap_rooks = 0;
		int b_trap_rooks = 0;
		int w_trap_queens = 0;
		int b_trap_queens = 0;
		
		int w_hanging_nonpawn = 0;
		int b_hanging_nonpawn = 0;
		int w_hanging_pawn = 0;
		int b_hanging_pawn = 0;

		
		int w_rooks_paired_h = 0;
		int b_rooks_paired_h = 0;
		int w_rooks_paired_v = 0;
		int b_rooks_paired_v = 0;
		
		int w_attacking_pieces_to_black_king_1 = 0;
		int b_attacking_pieces_to_white_king_1 = 0;
		int w_attacking_pieces_to_black_king_2 = 0;
		int b_attacking_pieces_to_white_king_2 = 0;
		
		int w_knights_attacks_to_black_king_1 = 0;
		int b_knights_attacks_to_white_king_1 = 0;
		int w_bishops_attacks_to_black_king_1 = 0;
		int b_bishops_attacks_to_white_king_1 = 0;
		int w_rooks_attacks_to_black_king_1 = 0;
		int b_rooks_attacks_to_white_king_1 = 0;
		int w_queens_attacks_to_black_king_1 = 0;
		int b_queens_attacks_to_white_king_1 = 0;
		//int w_pawns_attacks_to_black_king_1 = 0;//TODO
		//int b_pawns_attacks_to_white_king_1 = 0;//TODO
		
		int w_knights_attacks_to_black_king_2 = 0;
		int b_knights_attacks_to_white_king_2 = 0;
		int w_bishops_attacks_to_black_king_2 = 0;
		int b_bishops_attacks_to_white_king_2 = 0;
		int w_rooks_attacks_to_black_king_2 = 0;
		int b_rooks_attacks_to_white_king_2 = 0;
		int w_queens_attacks_to_black_king_2 = 0;
		int b_queens_attacks_to_white_king_2 = 0;
		//int w_pawns_attacks_to_black_king_2 = 0;//TODO
		//int b_pawns_attacks_to_white_king_2 = 0;//TODO
				
		int pin_bk = 0;
		int pin_bq = 0;
		int pin_br = 0;
		int pin_bn = 0;

		int pin_rk = 0;
		int pin_rq = 0;
		int pin_rb = 0;
		int pin_rn = 0;

		int pin_qk = 0;
		//int pin_qq = 0;
		int pin_qn = 0;
		int pin_qr = 0;
		int pin_qb = 0;
		
		int attack_nb = 0;
		int attack_nr = 0;
		int attack_nq = 0;
		
		int attack_bn = 0;
		int attack_br = 0;
		
		int attack_rb = 0;
		int attack_rn = 0;

		int attack_qn = 0;
		int attack_qb = 0;
		int attack_qr = 0;
		
		
		/**
		 * Initialize necessary data 
		 */
		long bb_white_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_WHITE);
		long bb_black_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_BLACK);
		//long bb_white_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		//long bb_black_pawns = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
		long bb_white_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE);
		long bb_black_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE);
		long bb_white_queens = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN);
		long bb_black_queens = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN);
		long bb_white_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
		long bb_black_bishops = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);
		long bb_white_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT);
		long bb_black_knights = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT);
		long bb_white_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KING);
		long bb_black_king = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KING);
		long bb_white_QandB = bb_white_queens | bb_white_bishops;
		long bb_black_QandB = bb_black_queens | bb_black_bishops;
		long bb_white_QandR = bb_white_queens | bb_white_rooks;
		long bb_black_QandR = bb_black_queens | bb_black_rooks;
		long bb_white_MM = bb_white_QandR | bb_white_QandB | bb_white_knights;
		long bb_black_MM = bb_black_QandR | bb_black_QandB | bb_black_knights;
		
		long kingSurrounding_L1_white = KingSurrounding.SURROUND_LEVEL1[kingFieldID_white];
		long kingSurrounding_L2_white = (~kingSurrounding_L1_white) & KingSurrounding.SURROUND_LEVEL2[kingFieldID_white];
		long kingSurrounding_L1_black = KingSurrounding.SURROUND_LEVEL1[kingFieldID_black];
		long kingSurrounding_L2_black = (~kingSurrounding_L1_black) & KingSurrounding.SURROUND_LEVEL2[kingFieldID_black];
		
		
		/**
		 * Pawns iteration
		 */
		{
			int w_pawns_count = w_pawns.getDataSize();
			if (w_pawns_count > 0) {
				int[] pawns_fields = w_pawns.getData();
				for (int i=0; i<w_pawns_count; i++) {
					int fieldID = pawns_fields[i];
					if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							w_hanging_pawn++;
						}
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
					if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							b_hanging_pawn++;
						}
					}
				}
			}
		}
		
		
		/**
		 * Knights iteration
		 */
		{
			int w_knights_count = w_knights.getDataSize();
			if (w_knights_count > 0) {
				int[] knights_fields = w_knights.getData();
				for (int i=0; i<w_knights_count; i++) {
					
					int fieldID = knights_fields[i];
					
					
					if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							w_hanging_nonpawn++;
						}
					}
					
					
					w_mobility_knights_all = 0;
					
					w_mobility_knights_safe = 0;
					
					final int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
					final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fieldID];
					final int[][] fids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
					
					int opking_attacks_counter_1 = 0;
					int opking_attacks_counter_2 = 0;
					final int size = validDirIDs.length;
					for (int j=0; j<size; j++) {
						
						int dirID = validDirIDs[j];
						long toBitboard = dirs[dirID][0];
						int toFieldID = fids[dirID][0];
						
						if ((toBitboard & kingSurrounding_L1_black) != 0L) {
							if (opking_attacks_counter_1 == 0) {
								w_attacking_pieces_to_black_king_1++;
							}
							opking_attacks_counter_1++;
						}
						if ((toBitboard & kingSurrounding_L2_black) != 0L) {
							if (opking_attacks_counter_2 == 0) {
								w_attacking_pieces_to_black_king_2++;
							}
							opking_attacks_counter_2++;
						}
						
						
						if ((toBitboard & bb_white_all) != 0L) {
							continue;
						}
						
						if ((toBitboard & bb_black_all) != 0L) {
							if ((toBitboard & bb_black_bishops) != 0L) {
								attack_nb++;
							} else if ((toBitboard & bb_black_rooks) != 0L) {
								attack_nr++;
							} else if ((toBitboard & bb_black_queens) != 0L) {
								attack_nq++;
							}
						}
						
						w_mobility_knights_all++;
						w_penetration_op_area += Fields.getRank_W(toFieldID);
						w_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
						
						boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT, toFieldID) >= 0;
						if (safe) {
							w_mobility_knights_safe++;
							w_penetration_op_area_safe += Fields.getRank_W(toFieldID);
							w_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
						}
					}
					
					int mob = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_KNIGHT_O[w_mobility_knights_all], MOBILITY_KNIGHT_E[w_mobility_knights_all]);
					signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT).addStrength(mob, openingPart);
					int mob_safe = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_KNIGHT_O[w_mobility_knights_safe], MOBILITY_KNIGHT_E[w_mobility_knights_safe]);
					signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT_S).addStrength(mob_safe, openingPart);
					if (w_mobility_knights_safe == 2) {
						w_trap_knights += 1 * (Fields.getRank_W(fieldID) + 1);
					} else if (w_mobility_knights_safe == 1) {
						w_trap_knights += 2 * (Fields.getRank_W(fieldID) + 1);
					} else if (w_mobility_knights_safe == 0) {
						w_trap_knights += 4 * (Fields.getRank_W(fieldID) + 1);
					}
					
					w_knights_attacks_to_black_king_1 += KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_1];
					w_knights_attacks_to_black_king_2 += KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_2];
				}
			}
		}
		
		{
			int b_knights_count = b_knights.getDataSize();		
			if (b_knights_count > 0) {
				int[] knights_fields = b_knights.getData();
				for (int i=0; i<b_knights_count; i++) {
										
					
					int fieldID = knights_fields[i];
					
					if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							b_hanging_nonpawn++;
						}
					}
					
					
					b_mobility_knights_all = 0;
					
					b_mobility_knights_safe = 0;
					
					final int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
					final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fieldID];
					final int[][] fids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
					
					int opking_attacks_counter_1 = 0;
					int opking_attacks_counter_2 = 0;
					final int size = validDirIDs.length;
					for (int j=0; j<size; j++) {
						
						int dirID = validDirIDs[j];
						long toBitboard = dirs[dirID][0];
						int toFieldID = fids[dirID][0];
						
						if ((toBitboard & kingSurrounding_L1_white) != 0L) {
							if (opking_attacks_counter_1 == 0) {
								b_attacking_pieces_to_white_king_1++;
							}
							opking_attacks_counter_1++;
						}
						if ((toBitboard & kingSurrounding_L2_white) != 0L) {
							if (opking_attacks_counter_2 == 0) {
								b_attacking_pieces_to_white_king_2++;
							}
							opking_attacks_counter_2++;
						}
						
						
						if ((toBitboard & bb_black_all) != 0L) {
							continue;
						}
						
						if ((toBitboard & bb_white_all) != 0L) {
							if ((toBitboard & bb_white_bishops) != 0L) {
								attack_nb--;
							} else if ((toBitboard & bb_white_rooks) != 0L) {
								attack_nr--;
							} else if ((toBitboard & bb_white_queens) != 0L) {
								attack_nq--;
							}
						}
						
						b_mobility_knights_all++;
						b_penetration_op_area += Fields.getRank_B(toFieldID);
						b_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
						
						boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT, toFieldID) >= 0;
						if (safe) {
							b_mobility_knights_safe++;
							b_penetration_op_area_safe += Fields.getRank_B(toFieldID);
							b_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
						}
					}
					
					int mob = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_KNIGHT_O[b_mobility_knights_all], MOBILITY_KNIGHT_E[b_mobility_knights_all]);
					signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT).addStrength(-mob, openingPart);
					int mob_safe = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_KNIGHT_O[b_mobility_knights_safe], MOBILITY_KNIGHT_E[b_mobility_knights_safe]);
					signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT_S).addStrength(-mob_safe, openingPart);
					if (b_mobility_knights_safe == 2) {
						b_trap_knights += 1 * (Fields.getRank_B(fieldID) + 1);
					} else if (b_mobility_knights_safe == 1) {
						b_trap_knights += 2 * (Fields.getRank_B(fieldID) + 1);
					} else if (b_mobility_knights_safe == 0) {
						b_trap_knights += 4 * (Fields.getRank_B(fieldID) + 1);
					}
					
					b_knights_attacks_to_white_king_1 += KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_1];
					b_knights_attacks_to_white_king_2 += KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_2];
				}				
			}
		}
		
		
		/**
		 * Bishops iteration
		 */
		{
			int w_bishops_count = w_bishops.getDataSize();
			if (w_bishops_count > 0) {
				int[] bishops_fields = w_bishops.getData();
				for (int i=0; i<w_bishops_count; i++) {
										
					
					int fieldID = bishops_fields[i];
					
					if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							w_hanging_nonpawn++;
						}
					}
					
					w_mobility_bishops_all = 0;
					
					w_mobility_bishops_safe = 0;
					
					final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
					final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
					final int[][] fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
					
					int opking_attacks_counter_1 = 0;
					int opking_attacks_counter_2 = 0;
					final int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						boolean pinned = false;
						boolean hidden = false;
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if (pinned) {
								if ((toBitboard & bb_white_all) != 0L) {
									break;
								}
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_king) != 0L) {
										pin_bk++;
									} else if ((toBitboard & bb_black_queens) != 0L) {
										pin_bq++;
									} else if ((toBitboard & bb_black_rooks) != 0L) {
										pin_br++;
									} else if ((toBitboard & bb_black_knights) != 0L) {
										pin_bn++;
									}
									break;
								}
							} else {
								if ((toBitboard & kingSurrounding_L1_black) != 0L) {
									if (opking_attacks_counter_1 == 0) {
										w_attacking_pieces_to_black_king_1++;
									}
									opking_attacks_counter_1++;
								}
								if ((toBitboard & kingSurrounding_L2_black) != 0L) {
									if (opking_attacks_counter_2 == 0) {
										w_attacking_pieces_to_black_king_2++;
									}
									opking_attacks_counter_2++;
								}
								
								
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_QandB) != 0L) {
										//Bishop can attack over other friendly bishop or queen - continue the iteration
										hidden = true;
									} else {
										break;
									}
								}
	
								if (!pinned) {
									if (!hidden) {
										w_mobility_bishops_all++;
										boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER, toFieldID) >= 0;
										if (safe) {
											w_mobility_bishops_safe++;
											w_penetration_op_area_safe += Fields.getRank_W(toFieldID);
											w_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
										}
									} else {
										w_mobility_bishops_all++;
									}
									w_penetration_op_area += Fields.getRank_W(toFieldID);
									w_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
								}
								
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_MM) != 0L) {
										
										pinned = true;
										
										if ((toBitboard & bb_black_knights) != 0L) {
											attack_bn++;
										} else if ((toBitboard & bb_black_rooks) != 0L) {
											attack_br++;
										}
										
									} else {
										break;
									}
								}
							}
						}
					}
					
					int mob = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_BISHOP_O[w_mobility_bishops_all], MOBILITY_BISHOP_E[w_mobility_bishops_all]);
					signals.getSignal(FEATURE_ID_MOBILITY_BISHOP).addStrength(mob, openingPart);
					int mob_safe = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_BISHOP_O[w_mobility_bishops_safe], MOBILITY_BISHOP_E[w_mobility_bishops_safe]);
					signals.getSignal(FEATURE_ID_MOBILITY_BISHOP_S).addStrength(mob_safe, openingPart);
					if (w_mobility_bishops_safe == 2) {
						w_trap_bishops += 1 * (Fields.getRank_W(fieldID) + 1);
					} else if (w_mobility_bishops_safe == 1) {
						w_trap_bishops += 2 * (Fields.getRank_W(fieldID) + 1);
					} else if (w_mobility_bishops_safe == 0) {
						w_trap_bishops += 4 * (Fields.getRank_W(fieldID) + 1);
					}
					
					w_bishops_attacks_to_black_king_1 += KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_1];
					w_bishops_attacks_to_black_king_2 += KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_2];
				}
			}
		}
		
		{
			int b_bishops_count = b_bishops.getDataSize();
			if (b_bishops_count > 0) {
				int[] bishops_fields = b_bishops.getData();
				for (int i=0; i<b_bishops_count; i++) {
					
					
					int fieldID = bishops_fields[i];
					
					if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							b_hanging_nonpawn++;
						}
					}
					
					b_mobility_bishops_all = 0;
					
					b_mobility_bishops_safe = 0;
					
					final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
					final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
					final int[][] fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
					
					int opking_attacks_counter_1 = 0;
					int opking_attacks_counter_2 = 0;
					final int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						boolean pinned = false;
						boolean hidden = false;
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if (pinned) {
								if ((toBitboard & bb_black_all) != 0L) {
									break;
								}
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_king) != 0L) {
										pin_bk--;
									} else if ((toBitboard & bb_white_queens) != 0L) {
										pin_bq--;
									} else if ((toBitboard & bb_white_rooks) != 0L) {
										pin_br--;
									} else if ((toBitboard & bb_white_knights) != 0L) {
										pin_bn--;
									}
									break;
								}
							} else {
								if ((toBitboard & kingSurrounding_L1_white) != 0L) {
									if (opking_attacks_counter_1 == 0) {
										b_attacking_pieces_to_white_king_1++;
									}
									opking_attacks_counter_1++;
								}
								if ((toBitboard & kingSurrounding_L2_white) != 0L) {
									if (opking_attacks_counter_2 == 0) {
										b_attacking_pieces_to_white_king_2++;
									}
									opking_attacks_counter_2++;
								}
								
								
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_QandB) != 0L) {
										//Bishop can attack over other friendly bishop or queen - continue the iteration
										hidden = true;
									} else {
										break;
									}
								}
								
								if (!pinned) {
									if (!hidden) {
										b_mobility_bishops_all++;
										boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER, toFieldID) >= 0;
										if (safe) {
											b_mobility_bishops_safe++;
											b_penetration_op_area_safe += Fields.getRank_B(toFieldID);
											b_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
										}
									} else {
										b_mobility_bishops_all++;
									}
									b_penetration_op_area += Fields.getRank_B(toFieldID);
									b_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
								}
								
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_MM) != 0L) {
										pinned = true;
										
										if ((toBitboard & bb_white_knights) != 0L) {
											attack_bn--;
										} else if ((toBitboard & bb_white_rooks) != 0L) {
											attack_br--;
										}
										
									} else {
										break;
									}
								}
							}
						}
					}
					
					int mob = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_BISHOP_O[b_mobility_bishops_all], MOBILITY_BISHOP_E[b_mobility_bishops_all]);
					signals.getSignal(FEATURE_ID_MOBILITY_BISHOP).addStrength(-mob, openingPart);
					int mob_safe = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_BISHOP_O[b_mobility_bishops_safe], MOBILITY_BISHOP_E[b_mobility_bishops_safe]);
					signals.getSignal(FEATURE_ID_MOBILITY_BISHOP_S).addStrength(-mob_safe, openingPart);
					if (b_mobility_bishops_safe == 2) {
						b_trap_bishops += 1 * (Fields.getRank_B(fieldID) + 1);
					} else if (b_mobility_bishops_safe == 1) {
						b_trap_bishops += 2 * (Fields.getRank_B(fieldID) + 1);
					} else if (b_mobility_bishops_safe == 0) {
						b_trap_bishops += 4 * (Fields.getRank_B(fieldID) + 1);
					}
					
					b_bishops_attacks_to_white_king_1 += KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_1];
					b_bishops_attacks_to_white_king_2 += KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_2];
				}
			}
		}
		

		/**
		 * Rooks iteration
		 */
		{
			int w_rooks_count = w_rooks.getDataSize();
			if (w_rooks_count > 0) {
				int[] rooks_fields = w_rooks.getData();
				for (int i=0; i<w_rooks_count; i++) {
					
					
					int fieldID = rooks_fields[i];
					
					if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							w_hanging_nonpawn++;
						}
					}
					
					w_mobility_rooks_all = 0;
					
					w_mobility_rooks_safe = 0;
					
					final long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
					final int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
					final int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
					
					int opking_attacks_counter_1 = 0;
					int opking_attacks_counter_2 = 0;
					final int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						boolean pinned = false;
						boolean hidden = false;
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if (pinned) {
								if ((toBitboard & bb_white_all) != 0L) {
									break;
								}
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_king) != 0L) {
										pin_rk++;
									} else if ((toBitboard & bb_black_queens) != 0L) {
										pin_rq++;
									} else if ((toBitboard & bb_black_bishops) != 0L) {
										pin_rb++;
									} else if ((toBitboard & bb_black_knights) != 0L) {
										pin_rn++;
									}
									break;
								}
							} else {
								if ((toBitboard & kingSurrounding_L1_black) != 0L) {
									if (opking_attacks_counter_1 == 0) {
										w_attacking_pieces_to_black_king_1++;
									}
									opking_attacks_counter_1++;
								}
								if ((toBitboard & kingSurrounding_L2_black) != 0L) {
									if (opking_attacks_counter_2 == 0) {
										w_attacking_pieces_to_black_king_2++;
									}
									opking_attacks_counter_2++;
								}
								
								
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_QandR) != 0L) {
										//Rook can attack over other friendly rooks or queens - continue the iteration
										if ((toBitboard & bb_white_rooks) != 0L) {
											if (dirID == CastlePlies.UP_DIR || dirID == CastlePlies.DOWN_DIR) {
												w_rooks_paired_v++;
											} else {
												w_rooks_paired_h++;
											}
										}
										hidden = true;
									} else {
										break;
									}
								}
	
								if (!pinned) {
									if (!hidden) {
										w_mobility_rooks_all++;
										boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE, toFieldID) >= 0;
										if (safe) {
											w_mobility_rooks_safe++;
											w_penetration_op_area_safe += Fields.getRank_W(toFieldID);
											w_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
										}
									} else {
										w_mobility_rooks_all++;
									}
									w_penetration_op_area += Fields.getRank_W(toFieldID);
									w_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
								}
								
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_MM) != 0L) {
										pinned = true;
										
										if ((toBitboard & bb_black_bishops) != 0L) {
											attack_rb++;
										} else if ((toBitboard & bb_black_knights) != 0L) {
											attack_rn++;
										}
										
									} else {
										break;
									}
								}
							}
						}
					}
					
					int mob = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_ROOK_O[w_mobility_rooks_all], MOBILITY_ROOK_E[w_mobility_rooks_all]);
					signals.getSignal(FEATURE_ID_MOBILITY_ROOK).addStrength(mob, openingPart);
					int mob_safe = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_ROOK_O[w_mobility_rooks_safe], MOBILITY_ROOK_E[w_mobility_rooks_safe]);
					signals.getSignal(FEATURE_ID_MOBILITY_ROOK_S).addStrength(mob_safe, openingPart);
					if (w_mobility_rooks_safe == 2) {
						w_trap_rooks += 1 * (Fields.getRank_W(fieldID) + 1);
					} else if (w_mobility_rooks_safe == 1) {
						w_trap_rooks += 2 * (Fields.getRank_W(fieldID) + 1);
					} else if (w_mobility_rooks_safe == 0) {
						w_trap_rooks += 4 * (Fields.getRank_W(fieldID) + 1);
					}	
					
					w_rooks_attacks_to_black_king_1 += KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_1];
					w_rooks_attacks_to_black_king_2 += KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_2];
				}
			}
		}
		
		{
			int b_rooks_count = b_rooks.getDataSize();
			if (b_rooks_count > 0) {
				int[] rooks_fields = b_rooks.getData();
				for (int i=0; i<b_rooks_count; i++) {
					
					
					int fieldID = rooks_fields[i];

					if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							b_hanging_nonpawn++;
						}
					}
					
					b_mobility_rooks_all = 0;
					
					b_mobility_rooks_safe = 0;
					
					final long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
					final int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
					final int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
					
					int opking_attacks_counter_1 = 0;
					int opking_attacks_counter_2 = 0;
					final int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						boolean pinned = false;
						boolean hidden = false;
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if (pinned) {
								if ((toBitboard & bb_black_all) != 0L) {
									break;
								}
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_king) != 0L) {
										pin_rk--;
									} else if ((toBitboard & bb_white_queens) != 0L) {
										pin_rq--;
									} else if ((toBitboard & bb_white_bishops) != 0L) {
										pin_rb--;
									} else if ((toBitboard & bb_white_knights) != 0L) {
										pin_rn--;
									}
									break;
								}
							} else {
								if ((toBitboard & kingSurrounding_L1_white) != 0L) {
									if (opking_attacks_counter_1 == 0) {
										b_attacking_pieces_to_white_king_1++;
									}
									opking_attacks_counter_1++;
								}
								if ((toBitboard & kingSurrounding_L2_white) != 0L) {
									if (opking_attacks_counter_2 == 0) {
										b_attacking_pieces_to_white_king_2++;
									}
									opking_attacks_counter_2++;
								}
								
								
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_QandR) != 0L) {
										//Rook can attack over other friendly rooks or queens - continue the iteration
										if ((toBitboard & bb_black_rooks) != 0L) {
											if (dirID == CastlePlies.UP_DIR || dirID == CastlePlies.DOWN_DIR) {
												b_rooks_paired_v++;
											} else {
												b_rooks_paired_h++;
											}
										}
										hidden = true;
									} else {
										break;
									}
								}
	
								if (!pinned) {
									if (!hidden) {
										b_mobility_rooks_all++;
										boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE, toFieldID) >= 0;
										if (safe) {
											b_mobility_rooks_safe++;
											b_penetration_op_area_safe += Fields.getRank_B(toFieldID);
											b_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
										}
									} else {
										b_mobility_rooks_all++;
									}
									b_penetration_op_area += Fields.getRank_B(toFieldID);
									b_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
								}
								
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_MM) != 0L) {
										pinned = true;
										
										if ((toBitboard & bb_white_bishops) != 0L) {
											attack_rb--;
										} else if ((toBitboard & bb_white_knights) != 0L) {
											attack_rn--;
										}
										
									} else {
										break;
									}
								}
							}
						}
					}
					
					int mob = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_ROOK_O[b_mobility_rooks_all], MOBILITY_ROOK_E[b_mobility_rooks_all]);
					signals.getSignal(FEATURE_ID_MOBILITY_ROOK).addStrength(-mob, openingPart);
					int mob_safe = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_ROOK_O[b_mobility_rooks_safe], MOBILITY_ROOK_E[b_mobility_rooks_safe]);
					signals.getSignal(FEATURE_ID_MOBILITY_ROOK_S).addStrength(-mob_safe, openingPart);
					if (b_mobility_rooks_safe == 2) {
						b_trap_rooks += 1 * (Fields.getRank_B(fieldID) + 1);
					} else if (b_mobility_rooks_safe == 1) {
						b_trap_rooks += 2 * (Fields.getRank_B(fieldID) + 1);
					} else if (b_mobility_rooks_safe == 0) {
						b_trap_rooks += 4 * (Fields.getRank_B(fieldID) + 1);
					}	
					
					b_rooks_attacks_to_white_king_1 += KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_1];
					b_rooks_attacks_to_white_king_2 += KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_2];
				}
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
					
					if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							w_hanging_nonpawn++;
						}
					}
					
					w_mobility_queens_all = 0;
					
					w_mobility_queens_safe = 0;
					
					int opking_attacks_counter_1 = 0;
					int opking_attacks_counter_2 = 0;
					
					/**
					 * Move like a rook
					 */
					long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
					int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
					int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
					
					int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						boolean pinned = false;
						boolean hidden = false;
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if (pinned) {
								if ((toBitboard & bb_white_all) != 0L) {
									break;
								}
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_king) != 0L) {
										pin_qk++;
									} else if ((toBitboard & bb_black_queens) != 0L) {
										//pin_qq++;
									} else if ((toBitboard & bb_black_bishops) != 0L) {
										pin_qb++;
									} else if ((toBitboard & bb_black_knights) != 0L) {
										pin_qn++;
									}
									break;
								}
							} else {
								if ((toBitboard & kingSurrounding_L1_black) != 0L) {
									if (opking_attacks_counter_1 == 0) {
										w_attacking_pieces_to_black_king_1++;
									}
									opking_attacks_counter_1++;
								}
								if ((toBitboard & kingSurrounding_L2_black) != 0L) {
									if (opking_attacks_counter_2 == 0) {
										w_attacking_pieces_to_black_king_2++;
									}
									opking_attacks_counter_2++;
								}
								
								
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_QandR) != 0L) {
										//Queen can attack over other friendly rooks or queens - continue the iteration
										hidden = true;
									} else {
										break;
									}
								}
	
								if (!pinned) {
									if (!hidden) {
										w_mobility_queens_all++;
										boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN, toFieldID) >= 0;
										if (safe) {
											w_mobility_queens_safe++;
											w_penetration_op_area_safe += Fields.getRank_W(toFieldID);
											w_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
										}
									} else {
										w_mobility_queens_all++;
									}
									w_penetration_op_area += Fields.getRank_W(toFieldID);
									w_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
								}
								
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_MM) != 0L) {
										
										pinned = true;
										
										if ((toBitboard & bb_black_knights) != 0L) {
											attack_qn++;
										} else if ((toBitboard & bb_black_bishops) != 0L) {
											attack_qb++;
										}
										
									} else {
										break;
									}
								}
							}
						}
					}
					
					
					/**
					 * Move like a bishop
					 */
					dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
					validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
					fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
					
					size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						boolean pinned = false;
						boolean hidden = false;
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if (pinned) {
								if ((toBitboard & bb_white_all) != 0L) {
									break;
								}
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_king) != 0L) {
										pin_qk++;
									} else if ((toBitboard & bb_black_queens) != 0L) {
										pin_qk++;
									} else if ((toBitboard & bb_black_rooks) != 0L) {
										pin_qr++;
									} else if ((toBitboard & bb_black_knights) != 0L) {
										pin_qn++;
									}
									break;
								}
							} else {
								if ((toBitboard & kingSurrounding_L1_black) != 0L) {
									if (opking_attacks_counter_1 == 0) {
										w_attacking_pieces_to_black_king_1++;
									}
									opking_attacks_counter_1++;
								}
								if ((toBitboard & kingSurrounding_L2_black) != 0L) {
									if (opking_attacks_counter_2 == 0) {
										w_attacking_pieces_to_black_king_2++;
									}
									opking_attacks_counter_2++;
								}
								
								
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_QandB) != 0L) {
										//Queen can attack over other friendly bishop or queen - continue the iteration
										hidden = true;
									} else {
										break;
									}
								}
	
								if (!pinned) {
									if (!hidden) {
										w_mobility_queens_all++;
										boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN, toFieldID) >= 0;
										if (safe) {
											w_mobility_queens_safe++;
											w_penetration_op_area_safe += Fields.getRank_W(toFieldID);
											w_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
										}
									} else {
										w_mobility_queens_all++;
									}
									w_penetration_op_area += Fields.getRank_W(toFieldID);
									w_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_black, toFieldID);
								}
								
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_MM) != 0L) {
										pinned = true;
										
										if ((toBitboard & bb_black_knights) != 0L) {
											attack_qn++;
										} else if ((toBitboard & bb_black_rooks) != 0L) {
											attack_qr++;
										}
										
									} else {
										break;
									}
								}
							}
						}
					}
					
					int mob = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_QUEEN_O[w_mobility_queens_all], MOBILITY_QUEEN_E[w_mobility_queens_all]);
					signals.getSignal(FEATURE_ID_MOBILITY_QUEEN).addStrength(mob, openingPart);
					int mob_safe = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_QUEEN_O[w_mobility_queens_safe], MOBILITY_QUEEN_E[w_mobility_queens_safe]);
					signals.getSignal(FEATURE_ID_MOBILITY_QUEEN_S).addStrength(mob_safe, openingPart);
					if (w_mobility_queens_safe == 2) {
						w_trap_queens += 1 * (Fields.getRank_W(fieldID) + 1);
					} else if (w_mobility_queens_safe == 1) {
						w_trap_queens += 2 * (Fields.getRank_W(fieldID) + 1);
					} else if (w_mobility_queens_safe == 0) {
						w_trap_queens += 4 * (Fields.getRank_W(fieldID) + 1);
					}	
					
					w_queens_attacks_to_black_king_1 += KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_1];
					w_queens_attacks_to_black_king_2 += KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_2];
				}
			}
		}
		
		{
			int b_queens_count = b_queens.getDataSize();
			if (b_queens_count > 0) {
				int[] queens_fields = b_queens.getData();
				for (int i=0; i<b_queens_count; i++) {
					
					
					int fieldID = queens_fields[i];
					
					if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
						int see = bitboard.getSee().seeField(fieldID);
						if (see < 0) {
							b_hanging_nonpawn++;
						}
					}
					
					b_mobility_queens_all = 0;
					
					b_mobility_queens_safe = 0;
					
					int opking_attacks_counter_1 = 0;
					int opking_attacks_counter_2 = 0;
					
					/**
					 * Move like a rook
					 */
					long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
					int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
					int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
					
					int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						boolean pinned = false;
						boolean hidden = false;
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if (pinned) {
								if ((toBitboard & bb_black_all) != 0L) {
									break;
								}
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_king) != 0L) {
										pin_qk--;
									} else if ((toBitboard & bb_white_queens) != 0L) {
										//pin_qq--;
									} else if ((toBitboard & bb_white_bishops) != 0L) {
										pin_qb--;
									} else if ((toBitboard & bb_white_knights) != 0L) {
										pin_qn--;
									}
									break;
								}
							} else {
								if ((toBitboard & kingSurrounding_L1_white) != 0L) {
									if (opking_attacks_counter_1 == 0) {
										b_attacking_pieces_to_white_king_1++;
									}
									opking_attacks_counter_1++;
								}
								if ((toBitboard & kingSurrounding_L2_white) != 0L) {
									if (opking_attacks_counter_2 == 0) {
										b_attacking_pieces_to_white_king_2++;
									}
									opking_attacks_counter_2++;
								}
								
								
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_QandR) != 0L) {
										//Queen can attack over other friendly rooks or queens - continue the iteration
										hidden = true;
									} else {
										break;
									}
								}
	
								if (!pinned) {
									if (!hidden) {
										b_mobility_queens_all++;
										boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN, toFieldID) >= 0;
										if (safe) {
											b_mobility_queens_safe++;
											b_penetration_op_area_safe += Fields.getRank_B(toFieldID);
											b_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
										}
									} else {
										b_mobility_queens_all++;
									}
									b_penetration_op_area += Fields.getRank_B(toFieldID);
									b_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
								}
								
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_MM) != 0L) {
										
										pinned = true;
										
										if ((toBitboard & bb_white_knights) != 0L) {
											attack_qn--;
										} else if ((toBitboard & bb_white_bishops) != 0L) {
											attack_qb--;
										}
										
									} else {
										break;
									}
								}
							}
						}
					}
					
					
					/**
					 * Move like a bishop
					 */
					dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
					validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
					fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
					
					size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						boolean pinned = false;
						boolean hidden = false;
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if (pinned) {
								if ((toBitboard & bb_black_all) != 0L) {
									break;
								}
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_king) != 0L) {
										pin_qk--;
									} else if ((toBitboard & bb_white_queens) != 0L) {
										//pin_qq--;
									} else if ((toBitboard & bb_white_rooks) != 0L) {
										pin_qr--;
									} else if ((toBitboard & bb_white_knights) != 0L) {
										pin_qn--;
									}
									break;
								}
							} else {
								if ((toBitboard & kingSurrounding_L1_white) != 0L) {
									if (opking_attacks_counter_1 == 0) {
										b_attacking_pieces_to_white_king_1++;
									}
									opking_attacks_counter_1++;
								}
								if ((toBitboard & kingSurrounding_L2_white) != 0L) {
									if (opking_attacks_counter_2 == 0) {
										b_attacking_pieces_to_white_king_2++;
									}
									opking_attacks_counter_2++;
								}
								
								
								if ((toBitboard & bb_black_all) != 0L) {
									if ((toBitboard & bb_black_QandB) != 0L) {
										//Queen can attack over other friendly bishop or queen - continue the iteration
										hidden = true;
									} else {
										break;
									}
								}
	
								if (!pinned) {
									if (!hidden) {
										b_mobility_queens_all++;
										boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN, toFieldID) >= 0;
										if (safe) {
											b_mobility_queens_safe++;
											b_penetration_op_area_safe += Fields.getRank_B(toFieldID);
											b_penetration_king_area_safe += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
										}
									} else {
										b_mobility_queens_all++;
									}
									b_penetration_op_area += Fields.getRank_B(toFieldID);
									b_penetration_king_area += Fields.getDistancePoints_reversed(kingFieldID_white, toFieldID);
								}
								
								if ((toBitboard & bb_white_all) != 0L) {
									if ((toBitboard & bb_white_MM) != 0L) {
										
										pinned = true;
										
										if ((toBitboard & bb_white_knights) != 0L) {
											attack_qn--;
										} else if ((toBitboard & bb_white_rooks) != 0L) {
											attack_qr--;
										}
										
									} else {
										break;
									}
								}
							}
						}
					}
					
					int mob = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_QUEEN_O[b_mobility_queens_all], MOBILITY_QUEEN_E[b_mobility_queens_all]);
					signals.getSignal(FEATURE_ID_MOBILITY_QUEEN).addStrength(-mob, openingPart);
					int mob_safe = bitboard.getMaterialFactor().interpolateByFactor(MOBILITY_QUEEN_O[b_mobility_queens_safe], MOBILITY_QUEEN_E[b_mobility_queens_safe]);
					signals.getSignal(FEATURE_ID_MOBILITY_QUEEN_S).addStrength(-mob_safe, openingPart);
					if (b_mobility_queens_safe == 2) {
						b_trap_queens += 1 * (Fields.getRank_B(fieldID) + 1);
					} else if (b_mobility_queens_safe == 1) {
						b_trap_queens += 2 * (Fields.getRank_B(fieldID) + 1);
					} else if (b_mobility_queens_safe == 0) {
						b_trap_queens += 4 * (Fields.getRank_B(fieldID) + 1);
					}	
					
					b_queens_attacks_to_white_king_1 += KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_1];
					b_queens_attacks_to_white_king_2 += KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_2];
				}
			}
		}
		
		
		signals.getSignal(FEATURE_ID_ROOKS_PAIR_H).addStrength(w_rooks_paired_h - b_rooks_paired_h, openingPart);
		signals.getSignal(FEATURE_ID_ROOKS_PAIR_V).addStrength(w_rooks_paired_v - b_rooks_paired_v, openingPart);
		
		int w_attack_to_black_king_1 = Math.max(1, w_knights_attacks_to_black_king_1)
				* Math.max(1, w_bishops_attacks_to_black_king_1)
				* Math.max(1, w_rooks_attacks_to_black_king_1)
				* Math.max(1, w_queens_attacks_to_black_king_1);
		int b_attack_to_white_king_1 = Math.max(1, b_knights_attacks_to_white_king_1)
				* Math.max(1, b_bishops_attacks_to_white_king_1)
				* Math.max(1, b_rooks_attacks_to_white_king_1)
				* Math.max(1, b_queens_attacks_to_white_king_1);
		
		int w_attack_to_black_king_2 = Math.max(1, w_knights_attacks_to_black_king_2)
				* Math.max(1, w_bishops_attacks_to_black_king_2)
				* Math.max(1, w_rooks_attacks_to_black_king_2)
				* Math.max(1, w_queens_attacks_to_black_king_2);
		int b_attack_to_white_king_2 = Math.max(1, b_knights_attacks_to_white_king_2)
				* Math.max(1, b_bishops_attacks_to_white_king_2)
				* Math.max(1, b_rooks_attacks_to_white_king_2)
				* Math.max(1, b_queens_attacks_to_white_king_2);
		
		int kingsafe_l1 = (w_attacking_pieces_to_black_king_1 * w_attack_to_black_king_1 - b_attacking_pieces_to_white_king_1 * b_attack_to_white_king_1) / (4 * 2);
		signals.getSignal(FEATURE_ID_KINGSAFE_L1).addStrength(kingsafe_l1, openingPart);
		
		int kingsafe_l2 = (w_attacking_pieces_to_black_king_2 * w_attack_to_black_king_2 - b_attacking_pieces_to_white_king_2 * b_attack_to_white_king_2) / (8 * 8);
		signals.getSignal(FEATURE_ID_KINGSAFE_L2).addStrength(kingsafe_l2, openingPart);
		
		
		signals.getSignal(FEATURE_ID_PIN_KING).addStrength(pin_bk + pin_rk + pin_qk, openingPart);
		signals.getSignal(FEATURE_ID_PIN_BIGGER_PIECE).addStrength(pin_bq + pin_br + pin_rq, openingPart);
		signals.getSignal(FEATURE_ID_PIN_EQUAL_PIECE).addStrength(pin_bn, openingPart);
		signals.getSignal(FEATURE_ID_PIN_LOWER_PIECE).addStrength(pin_rb + pin_rn + pin_qn + pin_qr + pin_qb, openingPart);
		
		
		signals.getSignal(FEATURE_ID_ATTACK_BIGGER_PIECE).addStrength(attack_nr + attack_nq + attack_br, openingPart);
		signals.getSignal(FEATURE_ID_ATTACK_EQUAL_PIECE).addStrength(attack_nb + attack_bn, openingPart);
		signals.getSignal(FEATURE_ID_ATTACK_LOWER_PIECE).addStrength(attack_rb + attack_rn + attack_qn + attack_qb + attack_qr, openingPart);
		
		
		signals.getSignal(FEATURE_ID_TRAP).addStrength(
					(w_trap_knights - b_trap_knights) +
					(w_trap_bishops - b_trap_bishops) +
					(w_trap_rooks - b_trap_rooks) +
					(w_trap_queens - b_trap_queens)
					, openingPart);
		
		
		if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
			
			if (b_hanging_nonpawn != 0) {
				throw new IllegalStateException("b_hanging_nonpawn=" + b_hanging_nonpawn);
			}
			if (w_hanging_nonpawn < 0) {
				throw new IllegalStateException("w_hanging_nonpawn=" + w_hanging_nonpawn);
			}
			if (b_hanging_pawn != 0) {
				throw new IllegalStateException("b_hanging_pawn=" + b_hanging_pawn);
			}
			if (w_hanging_pawn < 0) {
				throw new IllegalStateException("w_hanging_pawn=" + w_hanging_pawn);
			}


			if (w_hanging_nonpawn >= HUNGED_PIECES_O.length) {
				w_hanging_nonpawn = HUNGED_PIECES_O.length - 1;
			}
			double hunged_pieces = bitboard.getMaterialFactor().interpolateByFactor(HUNGED_PIECES_O[w_hanging_nonpawn], HUNGED_PIECES_E[w_hanging_nonpawn]);
			signals.getSignal(FEATURE_ID_HUNGED_PIECES).addStrength(hunged_pieces, openingPart);
			
			if (w_hanging_pawn >= HUNGED_PAWNS_O.length) {
				w_hanging_pawn = HUNGED_PAWNS_O.length - 1;
			}
			double hunged_pawns = bitboard.getMaterialFactor().interpolateByFactor(HUNGED_PAWNS_O[w_hanging_pawn], HUNGED_PAWNS_E[w_hanging_pawn]);
			signals.getSignal(FEATURE_ID_HUNGED_PAWNS).addStrength(hunged_pawns, openingPart);
			
			int w_hanging_all = w_hanging_nonpawn + w_hanging_pawn;
			if (w_hanging_all >= HUNGED_ALL_O.length) {
				w_hanging_all = HUNGED_ALL_O.length - 1;
			}
			double hunged_all = bitboard.getMaterialFactor().interpolateByFactor(HUNGED_ALL_O[w_hanging_all], HUNGED_ALL_E[w_hanging_all]);
			signals.getSignal(FEATURE_ID_HUNGED_ALL).addStrength(hunged_all, openingPart);
			
		} else {
			
			if (w_hanging_nonpawn != 0) {
				throw new IllegalStateException("w_hanging_nonpawn=" + w_hanging_nonpawn);
			}
			if (b_hanging_nonpawn < 0) {
				throw new IllegalStateException("b_hanging_nonpawn=" + b_hanging_nonpawn);
			}
			if (w_hanging_pawn != 0) {
				throw new IllegalStateException("w_hanging_pawn=" + w_hanging_pawn);
			}
			if (b_hanging_pawn < 0) {
				throw new IllegalStateException("b_hanging_pawn=" + b_hanging_pawn);
			}
			
			if (b_hanging_nonpawn >= HUNGED_PIECES_O.length) {
				b_hanging_nonpawn = HUNGED_PIECES_O.length - 1;
			}
			double hunged_pieces = bitboard.getMaterialFactor().interpolateByFactor(HUNGED_PIECES_O[b_hanging_nonpawn], HUNGED_PIECES_E[b_hanging_nonpawn]);
			signals.getSignal(FEATURE_ID_HUNGED_PIECES).addStrength(-hunged_pieces, openingPart);
			
			if (b_hanging_pawn >= HUNGED_PAWNS_O.length) {
				b_hanging_pawn = HUNGED_PAWNS_O.length - 1;
			}
			double hunged_pawns = bitboard.getMaterialFactor().interpolateByFactor(HUNGED_PAWNS_O[b_hanging_pawn], HUNGED_PAWNS_E[b_hanging_pawn]);
			signals.getSignal(FEATURE_ID_HUNGED_PAWNS).addStrength(-hunged_pawns, openingPart);
			
			int b_hanging_all = b_hanging_nonpawn + b_hanging_pawn;
			if (b_hanging_all >= HUNGED_ALL_O.length) {
				b_hanging_all = HUNGED_ALL_O.length - 1;
			}
			double hunged_all = bitboard.getMaterialFactor().interpolateByFactor(HUNGED_ALL_O[b_hanging_all], HUNGED_ALL_E[b_hanging_all]);
			signals.getSignal(FEATURE_ID_HUNGED_ALL).addStrength(-hunged_all, openingPart);
		}
		
		
		/*signals.getSignal(FEATURE_ID_PENETRATION_OP).addStrength(w_penetration_op_area - b_penetration_op_area, openingPart);
		signals.getSignal(FEATURE_ID_PENETRATION_OP_S).addStrength(w_penetration_op_area_safe - b_penetration_op_area_safe, openingPart);
		signals.getSignal(FEATURE_ID_PENETRATION_KING).addStrength(w_penetration_king_area - b_penetration_king_area, openingPart);
		signals.getSignal(FEATURE_ID_PENETRATION_KING_S).addStrength(w_penetration_king_area_safe - b_penetration_king_area_safe, openingPart);
		*/
	}
	
	
	public void fillFieldsStatesIterationSignals(ISignals signals) {
		
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		int w_mobility_knights_safe = 0;
		int b_mobility_knights_safe = 0;
		int w_mobility_bishops_safe = 0;
		int b_mobility_bishops_safe = 0;
		int w_mobility_rooks_safe = 0;
		int b_mobility_rooks_safe = 0;
		int w_mobility_queens_safe = 0;
		int b_mobility_queens_safe = 0;
		
		int w_overprotection_pawns = 0;
		int b_overprotection_pawns = 0;
		int w_overprotection_knights = 0;
		int b_overprotection_knights = 0;
		int w_overprotection_bishops = 0;
		int b_overprotection_bishops = 0;
		int w_overprotection_rooks = 0;
		int b_overprotection_rooks = 0;
		int w_overprotection_queens = 0;
		int b_overprotection_queens = 0;
		
		int w_trap_knights = 0;
		int b_trap_knights = 0;
		int w_trap_bishops = 0;
		int b_trap_bishops = 0;
		int w_trap_rooks = 0;
		int b_trap_rooks = 0;
		int w_trap_queens = 0;
		int b_trap_queens = 0;
		
		long bb_white_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_WHITE);
		long bb_black_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_BLACK);

		IFieldsAttacks fa = bitboard.getFieldsAttacks();
		int[] w_control = fa.getControlArray(Figures.COLOUR_WHITE);
		int[] b_control = fa.getControlArray(Figures.COLOUR_BLACK);
		
		
		/**
		 * Knights iteration
		 */
		{
			int w_knights_count = w_knights.getDataSize();
			if (w_knights_count > 0) {
				int[] knights_fields = w_knights.getData();
				for (int i=0; i<w_knights_count; i++) {
					
					w_mobility_knights_safe = 0;
					
					int fieldID = knights_fields[i];
					
					if (w_control[fieldID] > b_control[fieldID]) {
						w_overprotection_knights++;
					}
					
					final int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
					final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fieldID];
					final int[][] fids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
					
					final int size = validDirIDs.length;
					for (int j=0; j<size; j++) {
						
						int dirID = validDirIDs[j];
						long toBitboard = dirs[dirID][0];
						int toFieldID = fids[dirID][0];
						
						if ((toBitboard & bb_white_all) != 0L) {
							continue;
						}
						
						if ((-SeeMetadata.getSingleton().seeMove(Figures.TYPE_KNIGHT, Figures.TYPE_KNIGHT,
								w_control[toFieldID], b_control[toFieldID])) >= 0) {
							w_mobility_knights_safe++;
						}
					}
					
					signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT_S).addStrength(w_mobility_knights_safe, 1, openingPart);
					
					if (w_mobility_knights_safe == 2) {
						w_trap_knights -= 1;
					} else if (w_mobility_knights_safe == 1) {
						w_trap_knights -= 2;
					} else if (w_mobility_knights_safe == 0) {
						w_trap_knights -= 3;
					}
				}
			}
		}
		
		{
			int b_knights_count = b_knights.getDataSize();		
			if (b_knights_count > 0) {
				int[] knights_fields = b_knights.getData();
				for (int i=0; i<b_knights_count; i++) {
					
					b_mobility_knights_safe = 0;
					
					int fieldID = knights_fields[i];
					
					if (b_control[fieldID] > w_control[fieldID]) {
						b_overprotection_knights++;
					}
					
					final int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
					final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fieldID];
					final int[][] fids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
					
					final int size = validDirIDs.length;
					for (int j=0; j<size; j++) {
						
						int dirID = validDirIDs[j];
						long toBitboard = dirs[dirID][0];
						int toFieldID = fids[dirID][0];
						
						if ((toBitboard & bb_black_all) != 0L) {
							continue;
						}
						
						if ((-SeeMetadata.getSingleton().seeMove(Figures.TYPE_KNIGHT, Figures.TYPE_KNIGHT,
								b_control[toFieldID], w_control[toFieldID])) >= 0) {
							b_mobility_knights_safe++;
						}
					}
					
					signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT_S).addStrength(b_mobility_knights_safe, -1, openingPart);
					
					if (b_mobility_knights_safe == 2) {
						b_trap_knights -= 1;
					} else if (b_mobility_knights_safe == 1) {
						b_trap_knights -= 2;
					} else if (b_mobility_knights_safe == 0) {
						b_trap_knights -= 3;
					}
				}				
			}
		}
		
		
		/**
		 * Bishops iteration
		 */
		{
			int w_bishops_count = w_bishops.getDataSize();
			if (w_bishops_count > 0) {
				int[] bishops_fields = w_bishops.getData();
				for (int i=0; i<w_bishops_count; i++) {
					
					w_mobility_bishops_safe = 0;
					
					int fieldID = bishops_fields[i];
					
					if (w_control[fieldID] > b_control[fieldID]) {
						w_overprotection_bishops++;
					}
					
					final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
					final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
					final int[][] fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
					
					final int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if ((toBitboard & bb_white_all) != 0L) {
								break;
							}

							boolean safe = (-SeeMetadata.getSingleton().seeMove(Figures.TYPE_OFFICER, Figures.TYPE_OFFICER,
										w_control[toFieldID], b_control[toFieldID])) >= 0;
							if (safe) {
								w_mobility_bishops_safe++;
							}
							
							if ((toBitboard & bb_black_all) != 0L) {
								break;
							}
						}
					}
					
					signals.getSignal(FEATURE_ID_MOBILITY_BISHOP_S).addStrength(w_mobility_bishops_safe, 1, openingPart);
					
					if (w_mobility_bishops_safe == 2) {
						w_trap_bishops -= 1;
					} else if (w_mobility_bishops_safe == 1) {
						w_trap_bishops -= 2;
					} else if (w_mobility_bishops_safe == 0) {
						w_trap_bishops -= 3;
					}
				}
			}
		}
		
		{
			int b_bishops_count = b_bishops.getDataSize();
			if (b_bishops_count > 0) {
				int[] bishops_fields = b_bishops.getData();
				for (int i=0; i<b_bishops_count; i++) {
					
					b_mobility_bishops_safe = 0;
					
					int fieldID = bishops_fields[i];
					
					if (b_control[fieldID] > w_control[fieldID]) {
						b_overprotection_bishops++;
					}
					
					final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
					final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
					final int[][] fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
					
					final int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						for (int seq=0; seq<dirBitboards.length; seq++) {
							
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if ((toBitboard & bb_black_all) != 0L) {
								break;
							}

							boolean safe = (-SeeMetadata.getSingleton().seeMove(Figures.TYPE_OFFICER, Figures.TYPE_OFFICER,
										b_control[toFieldID], w_control[toFieldID])) >= 0;
							if (safe) {
								b_mobility_bishops_safe++;
							}
							
							if ((toBitboard & bb_white_all) != 0L) {
								break;
							}
							
						}
					}
					
					signals.getSignal(FEATURE_ID_MOBILITY_BISHOP_S).addStrength(b_mobility_bishops_safe, -1, openingPart);
					
					if (b_mobility_bishops_safe == 2) {
						b_trap_bishops -= 1;
					} else if (b_mobility_bishops_safe == 1) {
						b_trap_bishops -= 2;
					} else if (b_mobility_bishops_safe == 0) {
						b_trap_bishops -= 3;
					}
				}
			}
		}

		
		/**
		 * Rooks iteration
		 */
		{
			int w_rooks_count = w_rooks.getDataSize();
			if (w_rooks_count > 0) {
				int[] rooks_fields = w_rooks.getData();
				for (int i=0; i<w_rooks_count; i++) {
					
					w_mobility_rooks_safe = 0;
					
					int fieldID = rooks_fields[i];
					
					if (w_control[fieldID] > b_control[fieldID]) {
						w_overprotection_rooks++;
					}
					
					final long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
					final int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
					final int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
					
					final int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						for (int seq=0; seq<dirBitboards.length; seq++) {
							
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
								
							if ((toBitboard & bb_white_all) != 0L) {
								break;
							}
								
							boolean safe = (-SeeMetadata.getSingleton().seeMove(Figures.TYPE_CASTLE, Figures.TYPE_CASTLE,
										w_control[toFieldID], b_control[toFieldID])) >= 0;
							if (safe) {
								w_mobility_rooks_safe++;
							}
							
							if ((toBitboard & bb_black_all) != 0L) {
								break;
							}
						}
					}
					
					signals.getSignal(FEATURE_ID_MOBILITY_ROOK_S).addStrength(w_mobility_rooks_safe, 1, openingPart);
					
					if (w_mobility_rooks_safe == 2) {
						w_trap_rooks -= 1;
					} else if (w_mobility_rooks_safe == 1) {
						w_trap_rooks -= 2;
					} else if (w_mobility_rooks_safe == 0) {
						w_trap_rooks -= 3;
					}					
				}
			}
		}
		
		{
			int b_rooks_count = b_rooks.getDataSize();
			if (b_rooks_count > 0) {
				int[] rooks_fields = b_rooks.getData();
				for (int i=0; i<b_rooks_count; i++) {
					
					b_mobility_rooks_safe = 0;
					
					int fieldID = rooks_fields[i];
					
					if (b_control[fieldID] > w_control[fieldID]) {
						b_overprotection_rooks++;
					}
					
					final long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
					final int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
					final int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
					
					final int size = validDirIDs.length;
					for (int dir=0; dir<size; dir++) {
						int dirID = validDirIDs[dir];
						long[] dirBitboards = dirs[dirID];
						
						for (int seq=0; seq<dirBitboards.length; seq++) {
							long toBitboard = dirs[dirID][seq];
							int toFieldID = fids[dirID][seq];
							
							if ((toBitboard & bb_black_all) != 0L) {
								break;
							}


							boolean safe = (-SeeMetadata.getSingleton().seeMove(Figures.TYPE_CASTLE, Figures.TYPE_CASTLE,
									b_control[toFieldID], w_control[toFieldID])) >= 0;
							if (safe) {
								b_mobility_rooks_safe++;
							}
							
							if ((toBitboard & bb_white_all) != 0L) {
								break;
							}
						}
					}
					
					signals.getSignal(FEATURE_ID_MOBILITY_ROOK_S).addStrength(b_mobility_rooks_safe, -1, openingPart);
					
					if (b_mobility_rooks_safe == 2) {
						b_trap_rooks -= 1;
					} else if (b_mobility_rooks_safe == 1) {
						b_trap_rooks -= 2;
					} else if (b_mobility_rooks_safe == 0) {
						b_trap_rooks -= 3;
					}
				}
			}
		}

		
		
		/**
		 * field states iteration
		 */
		int hangingCount = 0;
		for (int fieldID=0; fieldID<w_control.length; fieldID++) {
			
			/**
			 * Hunged pieces
			 */
			int pieceID = bitboard.getFigureID(fieldID);
			if (pieceID != Constants.PID_NONE) {
				int pieceType = Figures.getFigureType(pieceID); 
				int pieceColour = Figures.getFigureColour(pieceID);
				
				if (pieceType != Figures.TYPE_KING && pieceColour == bitboard.getColourToMove()) {
					
					int see = -SeeMetadata.getSingleton().seeMove(Figures.TYPE_UNDEFINED, pieceType,
							pieceColour == Figures.COLOUR_WHITE ? w_control[fieldID] : b_control[fieldID],
							pieceColour == Figures.COLOUR_WHITE ? b_control[fieldID] : w_control[fieldID]);
					
					if (see < 0) {
						hangingCount++;
					} else if (see > 0) {
						throw new IllegalStateException("" + see);
					}
				}
			}
			
			/**
			 * Fields control
			 */
			if (w_control[fieldID] >= b_control[fieldID] && b_control[fieldID] > 0) {
				if (w_control[fieldID] == b_control[fieldID]) {
					signals.getSignal(FEATURE_ID_PST_CONTROL_EQ).addStrength(fieldID, 1, openingPart);	
				} else {
					signals.getSignal(FEATURE_ID_PST_CONTROL_MORE).addStrength(fieldID, 1, openingPart);
				}
			} else if (b_control[fieldID] >= w_control[fieldID] && w_control[fieldID] > 0) {
				if (b_control[fieldID] == w_control[fieldID]) {
					signals.getSignal(FEATURE_ID_PST_CONTROL_EQ).addStrength(axisSymmetry(fieldID), -1, openingPart);
				} else {
					signals.getSignal(FEATURE_ID_PST_CONTROL_MORE).addStrength(axisSymmetry(fieldID), -1, openingPart);
				}
			}
		}
				
		/*if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
			signals.getSignal(FEATURE_ID_HUNGED_1_PIECES).setStrength(hangingCount == 1 ? -1 : 0);
			signals.getSignal(FEATURE_ID_HUNGED_2_PIECES).setStrength(hangingCount == 2 ? -1 : 0);
			signals.getSignal(FEATURE_ID_HUNGED_3_PIECES).setStrength(hangingCount >= 3 ? -1: 0);
		} else {
			signals.getSignal(FEATURE_ID_HUNGED_1_PIECES).setStrength(hangingCount == 1 ? 1 : 0);
			signals.getSignal(FEATURE_ID_HUNGED_2_PIECES).setStrength(hangingCount == 2 ? 1 : 0);
			signals.getSignal(FEATURE_ID_HUNGED_3_PIECES).setStrength(hangingCount >= 3 ? 1 : 0);
		}
		
		signals.getSignal(FEATURE_ID_OVERPROT_PAWN).setStrength(w_overprotection_pawns - b_overprotection_pawns);
		signals.getSignal(FEATURE_ID_OVERPROT_KNIGHT).setStrength(w_overprotection_knights - b_overprotection_knights);
		signals.getSignal(FEATURE_ID_OVERPROT_BISHOP).setStrength(w_overprotection_bishops - b_overprotection_bishops);
		signals.getSignal(FEATURE_ID_OVERPROT_ROOK).setStrength(w_overprotection_rooks - b_overprotection_rooks);
		signals.getSignal(FEATURE_ID_OVERPROT_QUEEN).setStrength(w_overprotection_queens - b_overprotection_queens);
		
		signals.getSignal(FEATURE_ID_TRAP_KNIGHT).setStrength(w_trap_knights - b_trap_knights);
		signals.getSignal(FEATURE_ID_TRAP_BISHOP).setStrength(w_trap_bishops - b_trap_bishops);
		signals.getSignal(FEATURE_ID_TRAP_ROOK).setStrength(w_trap_rooks - b_trap_rooks);
		signals.getSignal(FEATURE_ID_TRAP_QUEEN).setStrength(w_trap_queens - b_trap_queens);*/
	}

}

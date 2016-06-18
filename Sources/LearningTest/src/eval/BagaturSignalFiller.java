package eval;


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
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;


public class BagaturSignalFiller implements IFeaturesConstants, ISignalFiller {
	
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
	
	
	public BagaturSignalFiller(IBitBoard _bitboard) {
		
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
	
	
	public void fill(ISignals signals) {
		fillStandardSignals(signals);
		fillPawnSignals(signals);
		fillPiecesIterationSignals(signals);
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
				//throw new IllegalStateException();
				return;
			case IFeatureComplexity.FIELDS_STATES_ITERATION:
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
		
		signals.getSignal(FEATURE_ID_KINGSAFE_CASTLING).addStrength(castling(Figures.COLOUR_WHITE) - castling(Figures.COLOUR_BLACK), openingPart);
		signals.getSignal(FEATURE_ID_BISHOPS_DOUBLE).addStrength(((w_bishops.getDataSize() >= 2) ? 1 : 0) - ((b_bishops.getDataSize() >= 2) ? 1 : 0), openingPart);
		
		movedFGPawns(signals);
		fianchetto(signals);
		
	}
	
	
	private void movedFGPawns(ISignals signals) {
		
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
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();

		signals.getSignal(FEATURE_ID_KINGSAFE_F_PAWN).addStrength(movedFPawn, openingPart);
		signals.getSignal(FEATURE_ID_KINGSAFE_G_PAWN).addStrength(missingGPawn, openingPart);
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
		
		bitboard.getPawnsCache().lock();
		
		PawnsModelEval pawnsModelEval = bitboard.getPawnsStructure();
		PawnsModel model = pawnsModelEval.getModel();
		
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
					
					signals.getSignal(FEATURE_ID_PAWNS_PASSED_RNK).addStrength(p.getRank(), 1, openingPart);
				}
				
				if (p.isCandidate()) {
					signals.getSignal(FEATURE_ID_PAWNS_CANDIDATE).addStrength(p.getRank(), 1, openingPart);
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
				}
				
				if (p.cannotBeSupported()) {
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
					
					signals.getSignal(FEATURE_ID_PAWNS_PASSED_RNK).addStrength(p.getRank(), -1, openingPart);
				}
				
				if (p.isCandidate()) {
					signals.getSignal(FEATURE_ID_PAWNS_CANDIDATE).addStrength(p.getRank(), -1, openingPart);
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
				}
				
				if (p.cannotBeSupported()) {
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
		int PAWNS_PASSED_UNSTOPPABLE = 100 + bitboard.getBaseEvaluation().getMaterialRook();
		int unstoppablePasser = bitboard.getUnstoppablePasser();
		if (unstoppablePasser > 0) {
			signals.getSignal(FEATURE_ID_UNSTOPPABLE_PASSER).addStrength(PAWNS_PASSED_UNSTOPPABLE, openingPart);
		} else if (unstoppablePasser < 0) {
			signals.getSignal(FEATURE_ID_UNSTOPPABLE_PASSER).addStrength(-PAWNS_PASSED_UNSTOPPABLE, openingPart);
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
		
		double opening_part = bitboard.getMaterialFactor().getOpenningPart();
		
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
					
					signals.getSignal(FEATURE_ID_PST_KNIGHT).addStrength(fieldID, 1, opening_part);
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
					
					signals.getSignal(FEATURE_ID_PST_KNIGHT).addStrength(axisSymmetry(fieldID), -1, opening_part);
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
					
					signals.getSignal(FEATURE_ID_PST_BISHOP).addStrength(fieldID, 1, opening_part);
					w_tropism_bishops += Fields.getTropismPoint(fieldID, kingFieldID_black);
					w_centred_bishops += Fields.getCenteredPoint(fieldID);
					
					if ((Fields.ALL_WHITE_FIELDS & Fields.ALL_A1H1[fieldID]) != 0L) {
						w_bad_bishops -= w_pawns_on_w_squares;
					} else {
						w_bad_bishops -= w_pawns_on_b_squares;
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
					
					signals.getSignal(FEATURE_ID_PST_BISHOP).addStrength(axisSymmetry(fieldID), -1, opening_part);
					b_tropism_bishops += Fields.getTropismPoint(fieldID, kingFieldID_white);
					b_centred_bishops += Fields.getCenteredPoint(fieldID);
					
					if ((Fields.ALL_WHITE_FIELDS & Fields.ALL_A1H1[fieldID]) != 0L) {
						b_bad_bishops -= b_pawns_on_w_squares;
					} else {
						b_bad_bishops -= b_pawns_on_b_squares;
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
					
					signals.getSignal(FEATURE_ID_PST_ROOK).addStrength(fieldID, 1, opening_part);
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
					
					
					signals.getSignal(FEATURE_ID_PST_ROOK).addStrength(axisSymmetry(fieldID), -1, opening_part);
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
					
					signals.getSignal(FEATURE_ID_PST_QUEEN).addStrength(fieldID, 1, opening_part);
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
					if ((fieldBitboard & RANK_7TH) != 0L) {
						b_queens_2th++;
					}
					
					signals.getSignal(FEATURE_ID_PST_QUEEN).addStrength(axisSymmetry(fieldID), -1, opening_part);
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
					signals.getSignal(FEATURE_ID_PST_KING).addStrength(fieldID, 1, opening_part);
					w_centred_king += Fields.getCenteredPoint(fieldID);
				}
			}
			int b_king_count = b_king.getDataSize();
			if (b_king_count > 0) {
				int[] b_king_fields = b_king.getData();
				for (int i=0; i<b_king_count; i++) {
					int fieldID = b_king_fields[i];
					signals.getSignal(FEATURE_ID_PST_KING).addStrength(axisSymmetry(fieldID), -1, opening_part);
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
						signals.getSignal(FEATURE_ID_PST_PAWN).addStrength(fieldID, 1, opening_part);
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
						signals.getSignal(FEATURE_ID_PST_PAWN).addStrength(axisSymmetry(fieldID), -1, opening_part);
						b_centred_pawns += Fields.getCenteredPoint(fieldID);
					}
				}
			}
		}
		
		
		signals.getSignal(FEATURE_ID_ROOKS_OPENED).addStrength(w_rooks_opened - b_rooks_opened, opening_part);
		signals.getSignal(FEATURE_ID_ROOKS_SEMIOPENED).addStrength(w_rooks_semiopened - b_rooks_semiopened, opening_part);

		signals.getSignal(FEATURE_ID_BISHOPS_BAD).addStrength(w_bad_bishops - b_bad_bishops, opening_part);
		signals.getSignal(FEATURE_ID_KNIGHTS_OUTPOST).addStrength(w_knight_outpost - b_knight_outpost, opening_part);
		
		signals.getSignal(FEATURE_ID_TROPISM_KNIGHT).addStrength(w_tropism_knights - b_tropism_knights, opening_part);
		signals.getSignal(FEATURE_ID_TROPISM_BISHOP).addStrength(w_tropism_bishops - b_tropism_bishops, opening_part);
		signals.getSignal(FEATURE_ID_TROPISM_ROOK).addStrength(w_tropism_rooks - b_tropism_rooks, opening_part);
		signals.getSignal(FEATURE_ID_TROPISM_QUEEN).addStrength(w_tropism_queens - b_tropism_queens, opening_part);		
		
		/*
		signals.getSignal(FEATURE_ID_PIECES_DISPERSION).setStrength(w_piecesDispersion - b_piecesDispersion);
		signals.getSignal(FEATURE_ID_ROOKS_7TH_2TH).setStrength(w_rooks_7th - b_rooks_2th);
		signals.getSignal(FEATURE_ID_QUEENS_7TH_2TH).setStrength(w_queens_7th - b_queens_2th);				
		 */
		
	}
	
	private static final int axisSymmetry(int fieldID) {
		return HORIZONTAL_SYMMETRY[fieldID];
	}
}

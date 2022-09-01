package bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnStructureConstants;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingSurrounding;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;
import bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.filler.ALL_SignalFillerConstants;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class WeightsEvaluator extends BaseEvaluator implements Weights {
    
    
    private long passedPawnsFronts_white = 0;
    private long passedPawnsFronts_black = 0;
    
    
    public WeightsEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
            super(_bitboard, _evalCache, _evalConfig);
    }
    
    
	@Override
	protected int phase1() {
		
		double eval = 0;
		
		eval += eval_material_nopawnsdrawrule();
        
        return (int) eval;
	}
	
	
    @Override
    protected int phase2() {
        
        double eval = 0;
        
        //eval_pawns is before eval_standard and eval_pieces, because it is faster compared to them
        //as well as in endgames the pawns evaluation affects more the final evaluation than the evaluation of eval_standard and eval_pieces
        eval += eval_pawns();
        
        return (int) eval;
    }
    
    
    @Override
    protected int phase3() {
    	
        double eval = 0;
        
        eval += eval_standard();
        eval += eval_pieces();
        
        return (int) eval;
    }
    
    
    @Override
    protected int phase4() {
    	
        double eval = 0;
        
        eval += mobilityKingSafetyPinsAttacks();
        
        return (int) eval;
    }
    
    
    @Override
    protected int phase5() {
    	
        double eval = 0;
        
        
        //The performance degradation caused by the huge number of calculations in safeMobilityTrapsHanging method
        //cannot compensate the potential benefits obtained from the evaluation.
        //That is why currently the method is not called
        //eval += safeMobilityTrapsHanging();
        
        return (int) eval;
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
                    eval_o += ALL_SignalFillerConstants.KING_DISTANCE_O[kingDistance] * KINGS_DISTANCE_O;
                    eval_e += ALL_SignalFillerConstants.KING_DISTANCE_E[kingDistance] * KINGS_DISTANCE_E;
            } else {
                    eval_o -= ALL_SignalFillerConstants.KING_DISTANCE_O[kingDistance] * KINGS_DISTANCE_O;
                    eval_e -= ALL_SignalFillerConstants.KING_DISTANCE_E[kingDistance] * KINGS_DISTANCE_E;
            }
            
            //Refers to http://home.comcast.net/~danheisman/Articles/evaluation_of_material_imbalance.htm
            //
            //A further refinement would be to raise the knight's value by 1/16 and lower the rook's value by 1/8
            //for each pawn above five of the side being valued, with the opposite adjustment for each pawn short of five.
            int w_pawns_above5 = w_pawns.getDataSize() - 5;
            int b_pawns_above5 = b_pawns.getDataSize() - 5;
            
            int pawns5_rooks = w_pawns_above5 * w_rooks.getDataSize() - b_pawns_above5 * b_rooks.getDataSize();
            eval_o += pawns5_rooks * ROOKS_5PAWNS_O;
            eval_e += pawns5_rooks * ROOKS_5PAWNS_E;
            
            int pawns5_knights = w_pawns_above5 * w_knights.getDataSize() - b_pawns_above5 * b_knights.getDataSize();
            eval_o += pawns5_knights * KNIGHTS_5PAWNS_O;
            eval_e += pawns5_knights * KNIGHTS_5PAWNS_E;
            
            
            return movedFGPawns + interpolator.interpolateByFactor(eval_o, eval_e);
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
            

            double scores_o = movedFPawn * KINGSAFE_F_O +
        missingGPawn * KINGSAFE_G_O;
            double scores_e = movedFPawn * KINGSAFE_F_E +
        missingGPawn * KINGSAFE_G_E;
            
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
            
            double pst_w_king = interpolator.interpolateByFactor(ALL_SignalFillerConstants.KING_O[kingFieldID_white], ALL_SignalFillerConstants.KING_E[kingFieldID_white]);
            double pst_b_king = interpolator.interpolateByFactor(ALL_SignalFillerConstants.KING_O[axisSymmetry(kingFieldID_black)], ALL_SignalFillerConstants.KING_E[axisSymmetry(kingFieldID_black)]);
            double pst_king = pst_w_king - pst_b_king;
            
            eval_o += bitboard.getBoardConfig().getWeight_PST_KING_O() * pst_king;
            eval_e += bitboard.getBoardConfig().getWeight_PST_KING_E() * pst_king;
            
            
            bitboard.getPawnsCache().lock();
            
            WeightsPawnsEval pawnsModelEval = (WeightsPawnsEval) bitboard.getPawnsStructure();
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
                                            double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.PAWN_O[fieldID], ALL_SignalFillerConstants.PAWN_E[fieldID]);
                                            eval_o += bitboard.getBoardConfig().getWeight_PST_PAWN_O() * pst;
                                            eval_e += bitboard.getBoardConfig().getWeight_PST_PAWN_E() * pst;
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
                                            double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.PAWN_O[axisSymmetry(fieldID)], ALL_SignalFillerConstants.PAWN_E[axisSymmetry(fieldID)]);
                                            eval_o -= bitboard.getBoardConfig().getWeight_PST_PAWN_O() * pst;
                                            eval_e -= bitboard.getBoardConfig().getWeight_PST_PAWN_E() * pst;
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
                                    
                                    double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.KNIGHT_O[fieldID], ALL_SignalFillerConstants.KNIGHT_E[fieldID]);
                                    eval_o += bitboard.getBoardConfig().getWeight_PST_KNIGHT_O() * pst;
                                    eval_e += bitboard.getBoardConfig().getWeight_PST_KNIGHT_E() * pst;
                                    
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
                                    
                                    double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.KNIGHT_O[axisSymmetry(fieldID)], ALL_SignalFillerConstants.KNIGHT_E[axisSymmetry(fieldID)]);
                                    eval_o -= bitboard.getBoardConfig().getWeight_PST_KNIGHT_O() * pst;
                                    eval_e -= bitboard.getBoardConfig().getWeight_PST_KNIGHT_E() * pst;
                                    
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
                                    
                                    double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.BISHOP_O[fieldID], ALL_SignalFillerConstants.BISHOP_E[fieldID]);
                                    eval_o += bitboard.getBoardConfig().getWeight_PST_BISHOP_O() * pst;
                                    eval_e += bitboard.getBoardConfig().getWeight_PST_BISHOP_E() * pst;
                                    
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
                                    
                                    double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.BISHOP_O[axisSymmetry(fieldID)], ALL_SignalFillerConstants.BISHOP_E[axisSymmetry(fieldID)]);
                                    eval_o -= bitboard.getBoardConfig().getWeight_PST_BISHOP_O() * pst;
                                    eval_e -= bitboard.getBoardConfig().getWeight_PST_BISHOP_E() * pst;
                                    
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
            
            pawnsModelEval = (WeightsPawnsEval) bitboard.getPawnsStructure();
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
                            
                            double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.ROOK_O[fieldID], ALL_SignalFillerConstants.ROOK_E[fieldID]);
                            eval_o += bitboard.getBoardConfig().getWeight_PST_ROOK_O() * pst;
                            eval_e += bitboard.getBoardConfig().getWeight_PST_ROOK_E() * pst;
                            
                            long fieldBitboard = Fields.ALL_A1H1[fieldID];
                            if ((fieldBitboard & openedFiles_all) != 0L) {
                                    eval_o += ROOKS_OPENED_O;
                                    eval_e += ROOKS_OPENED_E;
                            } else if ((fieldBitboard & openedFiles_white) != 0L) {
                                    eval_o += ROOKS_SEMIOPENED_O;
                                    eval_e += ROOKS_SEMIOPENED_E;
                            }
                            if ((fieldBitboard & Fields.DIGIT_7) != 0L) {
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
                            
                            double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.ROOK_O[axisSymmetry(fieldID)], ALL_SignalFillerConstants.ROOK_E[axisSymmetry(fieldID)]);
                            eval_o -= bitboard.getBoardConfig().getWeight_PST_ROOK_O() * pst;
                            eval_e -= bitboard.getBoardConfig().getWeight_PST_ROOK_E() * pst;
                            
                            long fieldBitboard = Fields.ALL_A1H1[fieldID];
                            if ((fieldBitboard & openedFiles_all) != 0L) {
                                    eval_o -= ROOKS_OPENED_O;
                                    eval_e -= ROOKS_OPENED_E;
                            } else if ((fieldBitboard & openedFiles_black) != 0L) {
                                    eval_o -= ROOKS_SEMIOPENED_O;
                                    eval_e -= ROOKS_SEMIOPENED_E;
                            }
                            if ((fieldBitboard & Fields.DIGIT_2) != 0L) {
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
                                    
                                    double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.QUEEN_O[fieldID], ALL_SignalFillerConstants.QUEEN_E[fieldID]);
                                    eval_o += bitboard.getBoardConfig().getWeight_PST_QUEEN_O() * pst;
                                    eval_e += bitboard.getBoardConfig().getWeight_PST_QUEEN_E() * pst;
                                    
                                    long fieldBitboard = Fields.ALL_A1H1[fieldID];
                                    if ((fieldBitboard & Fields.DIGIT_7) != 0L) {
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
                                    
                                    double pst = interpolator.interpolateByFactor(ALL_SignalFillerConstants.QUEEN_O[axisSymmetry(fieldID)], ALL_SignalFillerConstants.QUEEN_E[axisSymmetry(fieldID)]);
                                    eval_o -= bitboard.getBoardConfig().getWeight_PST_QUEEN_O() * pst;
                                    eval_e -= bitboard.getBoardConfig().getWeight_PST_QUEEN_E() * pst;
                                    
                                    long fieldBitboard = Fields.ALL_A1H1[fieldID];
                                    if ((fieldBitboard & Fields.DIGIT_2) != 0L) {
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
            
            WeightsPawnsEval pawnsModelEval = (WeightsPawnsEval) bitboard.getPawnsStructure();
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
            
            
            passedPawnsFronts_white = 0;
            
            int w_passed_count = model.getWPassedCount();
            if (w_passed_count > 0) {
                    
                    Pawn[] w_passed = model.getWPassed();
                    for (int i=0; i<w_passed_count; i++) {
                            
                            Pawn p = w_passed[i];
                            
                            passedPawnsFronts_white |= p.getFront();
                            
                            int rank = p.getRank();
                            int stoppersCount = Utils.countBits(p.getFront() & ~bitboard.getFreeBitboard());
                            /*rank = rank - stoppersCount;
                            if (rank <= 0) {
                                    rank = 1;
                            }*/
                            
                            eval_o += PAWNS_PASSED_O;
                            eval_e += PAWNS_PASSED_E;
                            
                            int passer = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.PASSERS_RANK_O[rank], ALL_SignalFillerConstants.PASSERS_RANK_E[rank]);
                            eval_o += PAWNS_PASSED_RNK_O * passer;
                            eval_e += PAWNS_PASSED_RNK_E * passer;
                            
        					if (stoppersCount > 0) {
                                eval_o += PAWNS_PSTOPPERS_O * (-(stoppersCount * passer) / 4);
                                eval_e += PAWNS_PSTOPPERS_E * (-(stoppersCount * passer) / 4);
        					}
                            
                    int frontFieldID = p.getFieldID() + 8;
                    int frontFrontFieldID = frontFieldID + 8;
                    if (frontFrontFieldID >= 64) {
                     frontFrontFieldID = frontFieldID;
                    }
                    
                    int dist_f = rank * ALL_SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD[Fields.getDistancePoints(w_kingID, frontFieldID)];
                    eval_o += KINGS_PASSERS_F_O * dist_f;
                    eval_e += KINGS_PASSERS_F_E * dist_f;
                    
                    int dist_ff = rank * ALL_SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[Fields.getDistancePoints(w_kingID, frontFrontFieldID)];
                    eval_o += KINGS_PASSERS_FF_O * dist_ff;
                    eval_e += KINGS_PASSERS_FF_E * dist_ff;
                    
                    int dist_op_f = rank * ALL_SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD_OP[Fields.getDistancePoints(b_kingID, frontFieldID)];
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
            
            passedPawnsFronts_black = 0;
            
            int b_passed_count = model.getBPassedCount();
            if (b_passed_count > 0) {

                    Pawn[] b_passed = model.getBPassed();
                    for (int i=0; i<b_passed_count; i++) {
                            
                            Pawn p = b_passed[i];

                            passedPawnsFronts_black |= p.getFront();
                            
                            int rank = p.getRank();
                            int stoppersCount = Utils.countBits(p.getFront() & ~bitboard.getFreeBitboard());
                            /*rank = rank - stoppersCount;
                            if (rank <= 0) {
                                    rank = 1;
                            }*/
                            
                            eval_o -= PAWNS_PASSED_O;
                            eval_e -= PAWNS_PASSED_E;
                            
                            int passer = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.PASSERS_RANK_O[rank], ALL_SignalFillerConstants.PASSERS_RANK_E[rank]);
                            eval_o -= PAWNS_PASSED_RNK_O * passer;
                            eval_e -= PAWNS_PASSED_RNK_E * passer;
                            
        					if (stoppersCount > 0) {
                                eval_o += PAWNS_PSTOPPERS_O * (+(stoppersCount * passer) / 4);
                                eval_e += PAWNS_PSTOPPERS_E * (+(stoppersCount * passer) / 4);
        					}
                            
                    int frontFieldID = p.getFieldID() - 8;
                    int frontFrontFieldID = frontFieldID - 8;
                    if (frontFrontFieldID < 0) {
                     frontFrontFieldID = frontFieldID;
                    }
                    
                    int dist_f = rank * ALL_SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD[Fields.getDistancePoints(b_kingID, frontFieldID)];
                    eval_o -= KINGS_PASSERS_F_O * dist_f;
                    eval_e -= KINGS_PASSERS_F_E * dist_f;
                    
                    int dist_ff = rank * ALL_SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[Fields.getDistancePoints(b_kingID, frontFrontFieldID)];
                    eval_o -= KINGS_PASSERS_FF_O * dist_ff;
                    eval_e -= KINGS_PASSERS_FF_E * dist_ff;
                    
                    int dist_op_f = rank * ALL_SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD_OP[Fields.getDistancePoints(w_kingID, frontFieldID)];
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
                                    
                                    int passerCandidate = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.PASSERS_CANDIDATE_RANK_O[rank], ALL_SignalFillerConstants.PASSERS_CANDIDATE_RANK_E[rank]);
                                    eval_o += PAWNS_CANDIDATE_RNK_O *
                        passerCandidate;
                                    eval_e += PAWNS_CANDIDATE_RNK_E *
                        passerCandidate;
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
                                    
                                    int passerCandidate = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.PASSERS_CANDIDATE_RANK_O[rank], ALL_SignalFillerConstants.PASSERS_CANDIDATE_RANK_E[rank]);
                                    eval_o -= PAWNS_CANDIDATE_RNK_O *
                        passerCandidate;
                                    eval_e -= PAWNS_CANDIDATE_RNK_E *
                        passerCandidate;
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
    
    
    private int mobilityKingSafetyPinsAttacks() {

            double eval_o = 0;
            double eval_e = 0;
            
            int kingFieldID_white = w_king.getData()[0];
            int kingFieldID_black = b_king.getData()[0];

            int w_mobility_knights_all = 0;
            int b_mobility_knights_all = 0;
            int w_mobility_bishops_all = 0;
            int b_mobility_bishops_all = 0;
            int w_mobility_rooks_all = 0;
            int b_mobility_rooks_all = 0;
            int w_mobility_queens_all = 0;
            int b_mobility_queens_all = 0;
                        
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
            int pin_qq = 0;
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
            
            int passed_pawns_fronts_attacks = 0;
            
            
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
            * Knights iteration
            */
            {
                    int w_knights_count = w_knights.getDataSize();
                    if (w_knights_count > 0) {
                            int[] knights_fields = w_knights.getData();
                            for (int i=0; i<w_knights_count; i++) {
                                    
                                    int fieldID = knights_fields[i];
                                    
                                    
                                    w_mobility_knights_all = 0;
                                    
                                    final int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
                                    final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fieldID];
                                    //final int[][] fids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
                                    
                                    int opking_attacks_counter_1 = 0;
                                    int opking_attacks_counter_2 = 0;
                                    final int size = validDirIDs.length;
                                    for (int j=0; j<size; j++) {
                                            
                                            int dirID = validDirIDs[j];
                                            long toBitboard = dirs[dirID][0];
                                            
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
                                            
                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                    						}
                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                    }
                                    
                                    eval_o += MOBILITY_KNIGHT_O *
                        ALL_SignalFillerConstants.MOBILITY_KNIGHT_O[w_mobility_knights_all];
                                    eval_e += MOBILITY_KNIGHT_E *
                        ALL_SignalFillerConstants.MOBILITY_KNIGHT_E[w_mobility_knights_all];
                                    
                                    w_knights_attacks_to_black_king_1 += ALL_SignalFillerConstants.KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_1];
                                    w_knights_attacks_to_black_king_2 += ALL_SignalFillerConstants.KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            {
                    int b_knights_count = b_knights.getDataSize();          
                    if (b_knights_count > 0) {
                            int[] knights_fields = b_knights.getData();
                            for (int i=0; i<b_knights_count; i++) {
                                                                            
                                    
                                    int fieldID = knights_fields[i];
                                    
                                    
                                    b_mobility_knights_all = 0;
                                    
                                    final int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
                                    final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fieldID];
                                    //final int[][] fids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
                                    
                                    int opking_attacks_counter_1 = 0;
                                    int opking_attacks_counter_2 = 0;
                                    final int size = validDirIDs.length;
                                    for (int j=0; j<size; j++) {
                                            
                                            int dirID = validDirIDs[j];
                                            long toBitboard = dirs[dirID][0];
                                            
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
                                            
                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                    						}
                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                    }
                                    
                                    eval_o -= MOBILITY_KNIGHT_O *
                        ALL_SignalFillerConstants.MOBILITY_KNIGHT_O[b_mobility_knights_all];
                                    eval_e -= MOBILITY_KNIGHT_E *
                        ALL_SignalFillerConstants.MOBILITY_KNIGHT_E[b_mobility_knights_all];
                                    
                                    b_knights_attacks_to_white_king_1 += ALL_SignalFillerConstants.KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_1];
                                    b_knights_attacks_to_white_king_2 += ALL_SignalFillerConstants.KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_2];
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
                                    
                                    
                                    w_mobility_bishops_all = 0;
                                    
                                    final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
                                    final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
                                    //final int[][] fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
                                    
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
                                                            
                                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                                    						}
                                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                                                    } else {
                                                                            w_mobility_bishops_all++;
                                                                    }
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
                                    
                                    eval_o += MOBILITY_BISHOP_O *
                        ALL_SignalFillerConstants.MOBILITY_BISHOP_O[w_mobility_bishops_all];
                                    eval_e += MOBILITY_BISHOP_E *
                        ALL_SignalFillerConstants.MOBILITY_BISHOP_E[w_mobility_bishops_all];
                                    
                                    w_bishops_attacks_to_black_king_1 += ALL_SignalFillerConstants.KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_1];
                                    w_bishops_attacks_to_black_king_2 += ALL_SignalFillerConstants.KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            {
                    int b_bishops_count = b_bishops.getDataSize();
                    if (b_bishops_count > 0) {
                            int[] bishops_fields = b_bishops.getData();
                            for (int i=0; i<b_bishops_count; i++) {
                                    
                                    
                                    int fieldID = bishops_fields[i];
                                    
                                    b_mobility_bishops_all = 0;
                                    
                                    final long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
                                    final int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
                                    //final int[][] fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
                                    
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
                                                            
                                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                                    						}
                                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                                                    } else {
                                                                            b_mobility_bishops_all++;
                                                                    }
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
                                    
                                    eval_o -= MOBILITY_BISHOP_O *
                        ALL_SignalFillerConstants.MOBILITY_BISHOP_O[b_mobility_bishops_all];
                                    eval_e -= MOBILITY_BISHOP_E *
                        ALL_SignalFillerConstants.MOBILITY_BISHOP_E[b_mobility_bishops_all];
                                    
                                    b_bishops_attacks_to_white_king_1 += ALL_SignalFillerConstants.KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_1];
                                    b_bishops_attacks_to_white_king_2 += ALL_SignalFillerConstants.KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_2];
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
                                    
                                    w_mobility_rooks_all = 0;
                                    
                                    final long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
                                    final int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
                                    //final int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
                                    
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
                                                            
                                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                                    						}
                                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                                                    } else {
                                                                            w_mobility_rooks_all++;
                                                                    }
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
                                    
                                    eval_o += MOBILITY_ROOK_O *
                        ALL_SignalFillerConstants.MOBILITY_ROOK_O[w_mobility_rooks_all];
                                    eval_e += MOBILITY_ROOK_E *
                        ALL_SignalFillerConstants.MOBILITY_ROOK_E[w_mobility_rooks_all];
                                    
                                    w_rooks_attacks_to_black_king_1 += ALL_SignalFillerConstants.KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_1];
                                    w_rooks_attacks_to_black_king_2 += ALL_SignalFillerConstants.KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            {
                    int b_rooks_count = b_rooks.getDataSize();
                    if (b_rooks_count > 0) {
                            int[] rooks_fields = b_rooks.getData();
                            for (int i=0; i<b_rooks_count; i++) {
                                    
                                    
                                    int fieldID = rooks_fields[i];
                                    
                                    b_mobility_rooks_all = 0;
                                    
                                    final long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
                                    final int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
                                    //final int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
                                    
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
                                                            
                                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                                    						}
                                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                                                    } else {
                                                                            b_mobility_rooks_all++;
                                                                    }
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
                                    
                                    eval_o -= MOBILITY_ROOK_O *
                        ALL_SignalFillerConstants.MOBILITY_ROOK_O[b_mobility_rooks_all];
                                    eval_e -= MOBILITY_ROOK_E *
                        ALL_SignalFillerConstants.MOBILITY_ROOK_E[b_mobility_rooks_all];
                                    
                                    b_rooks_attacks_to_white_king_1 += ALL_SignalFillerConstants.KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_1];
                                    b_rooks_attacks_to_white_king_2 += ALL_SignalFillerConstants.KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_2];
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
                                    
                                    w_mobility_queens_all = 0;
                                    
                                    int opking_attacks_counter_1 = 0;
                                    int opking_attacks_counter_2 = 0;
                                    
                                    /**
                                    * Move like a rook
                                    */
                                    long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
                                    int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
                                    //int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
                                    
                                    int size = validDirIDs.length;
                                    for (int dir=0; dir<size; dir++) {
                                            int dirID = validDirIDs[dir];
                                            long[] dirBitboards = dirs[dirID];
                                            
                                            boolean pinned = false;
                                            boolean hidden = false;
                                            for (int seq=0; seq<dirBitboards.length; seq++) {
                                                    long toBitboard = dirs[dirID][seq];
                                                    
                                                    if (pinned) {
                                                            if ((toBitboard & bb_white_all) != 0L) {
                                                                    break;
                                                            }
                                                            if ((toBitboard & bb_black_all) != 0L) {
                                                                    if ((toBitboard & bb_black_king) != 0L) {
                                                                            pin_qk++;
                                                                    } else if ((toBitboard & bb_black_queens) != 0L) {
                                                                            pin_qq++;
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
                                                            
                                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                                    						}
                                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                                                    } else {
                                                                            w_mobility_queens_all++;
                                                                    }
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
                                    //fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
                                    
                                    size = validDirIDs.length;
                                    for (int dir=0; dir<size; dir++) {
                                            int dirID = validDirIDs[dir];
                                            long[] dirBitboards = dirs[dirID];
                                            
                                            boolean pinned = false;
                                            boolean hidden = false;
                                            for (int seq=0; seq<dirBitboards.length; seq++) {
                                                    long toBitboard = dirs[dirID][seq];
                                                    
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
                                                            
                                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                                    						}
                                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                                    							passed_pawns_fronts_attacks += Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                                                    } else {
                                                                            w_mobility_queens_all++;
                                                                    }
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
                                    
                                    eval_o += MOBILITY_QUEEN_O *
                        ALL_SignalFillerConstants.MOBILITY_QUEEN_O[w_mobility_queens_all];
                                    eval_e += MOBILITY_QUEEN_E *
                        ALL_SignalFillerConstants.MOBILITY_QUEEN_E[w_mobility_queens_all];
                                    
                                    w_queens_attacks_to_black_king_1 += ALL_SignalFillerConstants.KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_1];
                                    w_queens_attacks_to_black_king_2 += ALL_SignalFillerConstants.KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            {
                    int b_queens_count = b_queens.getDataSize();
                    if (b_queens_count > 0) {
                            int[] queens_fields = b_queens.getData();
                            for (int i=0; i<b_queens_count; i++) {
                                    
                                    
                                    int fieldID = queens_fields[i];
                                    
                                    b_mobility_queens_all = 0;
                                    
                                    int opking_attacks_counter_1 = 0;
                                    int opking_attacks_counter_2 = 0;
                                    
                                    /**
                                    * Move like a rook
                                    */
                                    long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
                                    int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
                                    //int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
                                    
                                    int size = validDirIDs.length;
                                    for (int dir=0; dir<size; dir++) {
                                            int dirID = validDirIDs[dir];
                                            long[] dirBitboards = dirs[dirID];
                                            
                                            boolean pinned = false;
                                            boolean hidden = false;
                                            for (int seq=0; seq<dirBitboards.length; seq++) {
                                                    long toBitboard = dirs[dirID][seq];
                                                    
                                                    if (pinned) {
                                                            if ((toBitboard & bb_black_all) != 0L) {
                                                                    break;
                                                            }
                                                            if ((toBitboard & bb_white_all) != 0L) {
                                                                    if ((toBitboard & bb_white_king) != 0L) {
                                                                            pin_qk--;
                                                                    } else if ((toBitboard & bb_white_queens) != 0L) {
                                                                            pin_qq--;
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
                                                            
                                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                                    						}
                                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                                                    } else {
                                                                            b_mobility_queens_all++;
                                                                    }
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
                                    //fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
                                    
                                    size = validDirIDs.length;
                                    for (int dir=0; dir<size; dir++) {
                                            int dirID = validDirIDs[dir];
                                            long[] dirBitboards = dirs[dirID];
                                            
                                            boolean pinned = false;
                                            boolean hidden = false;
                                            for (int seq=0; seq<dirBitboards.length; seq++) {
                                                    long toBitboard = dirs[dirID][seq];
                                                    
                                                    if (pinned) {
                                                            if ((toBitboard & bb_black_all) != 0L) {
                                                                    break;
                                                            }
                                                            if ((toBitboard & bb_white_all) != 0L) {
                                                                    if ((toBitboard & bb_white_king) != 0L) {
                                                                            pin_qk--;
                                                                    } else if ((toBitboard & bb_white_queens) != 0L) {
                                                                            pin_qq--;
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
                                                            
                                    						if ((toBitboard & passedPawnsFronts_white) != 0L) {
                                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_white);
                                    						}
                                    						if ((toBitboard & passedPawnsFronts_black) != 0L) {
                                    							passed_pawns_fronts_attacks -= Utils.countBits_less1s(toBitboard & passedPawnsFronts_black);
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
                                                                    } else {
                                                                            b_mobility_queens_all++;
                                                                    }
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
                                    
                                    eval_o -= MOBILITY_QUEEN_O *
                        ALL_SignalFillerConstants.MOBILITY_QUEEN_O[b_mobility_queens_all];
                                    eval_e -= MOBILITY_QUEEN_E *
                        ALL_SignalFillerConstants.MOBILITY_QUEEN_E[b_mobility_queens_all];
                                    
                                    b_queens_attacks_to_white_king_1 += ALL_SignalFillerConstants.KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_1];
                                    b_queens_attacks_to_white_king_2 += ALL_SignalFillerConstants.KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            
            int rooks_paired_h = (w_rooks_paired_h - b_rooks_paired_h);
            eval_o += ROOKS_PAIR_H_O * rooks_paired_h;
            eval_e += ROOKS_PAIR_H_E * rooks_paired_h;
            
            int rooks_paired_v = (w_rooks_paired_v - b_rooks_paired_v);
            eval_o += ROOKS_PAIR_V_O * rooks_paired_v;
            eval_e += ROOKS_PAIR_V_E * rooks_paired_v;
            
            
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
            eval_o += KINGSAFETY_L1_O * kingsafe_l1;
            eval_e += KINGSAFETY_L1_E * kingsafe_l1;
            
            
            int kingsafe_l2 = (w_attacking_pieces_to_black_king_2 * w_attack_to_black_king_2 - b_attacking_pieces_to_white_king_2 * b_attack_to_white_king_2) / (8 * 8);
            eval_o += KINGSAFETY_L2_O * kingsafe_l2;
            eval_e += KINGSAFETY_L2_E * kingsafe_l2;
            
            
            int pin_k = pin_bk + pin_rk + pin_qk;
            
            int pin_big = pin_k + pin_bq + pin_br + pin_rq;
            eval_o += PIN_BIGGER_O * pin_big;
            eval_e += PIN_BIGGER_E * pin_big;
            
            int pin_eq = pin_bn;
            eval_o += PIN_EQ_O * pin_eq;
            eval_e += PIN_EQ_E * pin_eq;
            
            int pin_lower = pin_rb + pin_rn + pin_qn + pin_qr + pin_qb;
            eval_o += PIN_LOWER_O * pin_lower;
            eval_e += PIN_LOWER_O * pin_lower;
            
            
            int attack_bigger = attack_nr + attack_nq + attack_br;
            eval_o += ATTACK_BIGGER_O * attack_bigger;
            eval_e += ATTACK_BIGGER_O * attack_bigger;
            
            int attack_eq = attack_nb + attack_bn;
            eval_o += ATTACK_EQ_O * attack_eq;
            eval_e += ATTACK_EQ_E * attack_eq;
            
            int attack_lower = attack_rb + attack_rn + attack_qn + attack_qb + attack_qr;
            eval_o += ATTACK_LOWER_O * attack_lower;
            eval_e += ATTACK_LOWER_E * attack_lower;
            
            
            eval_o += PAWNS_PSTOPPERS_A_O * passed_pawns_fronts_attacks;
            eval_e += PAWNS_PSTOPPERS_A_E * passed_pawns_fronts_attacks;
            
            
            return interpolator.interpolateByFactor(eval_o, eval_e);

    }
    
    
	private double safeMobilityTrapsHanging() {
		
        double eval_o = 0;
        double eval_e = 0;
        
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
                
        
        
        // Initialize necessary data 
        
        long bb_white_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_WHITE);
        long bb_black_all = bitboard.getFiguresBitboardByColour(Figures.COLOUR_BLACK);
        
        
        
        // Pawns iteration
        
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
        
        
        
        // Knights iteration
        
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
                                
                                w_mobility_knights_safe = 0;
                                
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
                                        
                                        
                                        boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT, toFieldID) >= 0;
                                        if (safe) {
                                                w_mobility_knights_safe++;
                                        }
                                }
                                
                                eval_o += MOBILITY_KNIGHT_S_O * ALL_SignalFillerConstants.MOBILITY_KNIGHT_O[w_mobility_knights_safe];
                                eval_e += MOBILITY_KNIGHT_S_E * ALL_SignalFillerConstants.MOBILITY_KNIGHT_E[w_mobility_knights_safe];
                                                                        
                                if (w_mobility_knights_safe == 2) {
                                        w_trap_knights += 1 * (Fields.getRank_W(fieldID) + 1);
                                } else if (w_mobility_knights_safe == 1) {
                                        w_trap_knights += 2 * (Fields.getRank_W(fieldID) + 1);
                                } else if (w_mobility_knights_safe == 0) {
                                        w_trap_knights += 4 * (Fields.getRank_W(fieldID) + 1);
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
                                
                                if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
                                        int see = bitboard.getSee().seeField(fieldID);
                                        if (see < 0) {
                                                b_hanging_nonpawn++;
                                        }
                                }
                                
                                b_mobility_knights_safe = 0;
                                
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
                                        
                                        
                                        boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT, toFieldID) >= 0;
                                        if (safe) {
                                                b_mobility_knights_safe++;
                                        }
                                }
                                
                                eval_o -= MOBILITY_KNIGHT_S_O * ALL_SignalFillerConstants.MOBILITY_KNIGHT_O[b_mobility_knights_safe];
                                eval_e -= MOBILITY_KNIGHT_S_E * ALL_SignalFillerConstants.MOBILITY_KNIGHT_E[b_mobility_knights_safe];
                                
                                if (b_mobility_knights_safe == 2) {
                                        b_trap_knights += 1 * (Fields.getRank_B(fieldID) + 1);
                                } else if (b_mobility_knights_safe == 1) {
                                        b_trap_knights += 2 * (Fields.getRank_B(fieldID) + 1);
                                } else if (b_mobility_knights_safe == 0) {
                                        b_trap_knights += 4 * (Fields.getRank_B(fieldID) + 1);
                                }
                        }                               
                }
        }
        
        
        
        // Bishops iteration
        
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
                                
                                w_mobility_bishops_safe = 0;
                                
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

                                                boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER, toFieldID) >= 0;
                                                if (safe) {
                                                        w_mobility_bishops_safe++;
                                                }
                                                
                                                if ((toBitboard & bb_black_all) != 0L) {
                                                    break;
                                                }
                                        }
                                }
                                
                                eval_o += MOBILITY_BISHOP_S_O * ALL_SignalFillerConstants.MOBILITY_BISHOP_O[w_mobility_bishops_safe];
                                eval_e += MOBILITY_BISHOP_S_E * ALL_SignalFillerConstants.MOBILITY_BISHOP_E[w_mobility_bishops_safe];
                                
                                if (w_mobility_bishops_safe == 2) {
                                        w_trap_bishops += 1 * (Fields.getRank_W(fieldID) + 1);
                                } else if (w_mobility_bishops_safe == 1) {
                                        w_trap_bishops += 2 * (Fields.getRank_W(fieldID) + 1);
                                } else if (w_mobility_bishops_safe == 0) {
                                        w_trap_bishops += 4 * (Fields.getRank_W(fieldID) + 1);
                                }
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
                                
                                b_mobility_bishops_safe = 0;
                                
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
                                                
                                                boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER, toFieldID) >= 0;
                                                if (safe) {
                                                        b_mobility_bishops_safe++;
                                                }
                                                
                                                if ((toBitboard & bb_white_all) != 0L) {
                                                    break;
                                                }
                                        }
                                }
                                
                                eval_o -= MOBILITY_BISHOP_S_O * ALL_SignalFillerConstants.MOBILITY_BISHOP_O[b_mobility_bishops_safe];
                                eval_e -= MOBILITY_BISHOP_S_E * ALL_SignalFillerConstants.MOBILITY_BISHOP_E[b_mobility_bishops_safe];
                                
                                if (b_mobility_bishops_safe == 2) {
                                        b_trap_bishops += 1 * (Fields.getRank_B(fieldID) + 1);
                                } else if (b_mobility_bishops_safe == 1) {
                                        b_trap_bishops += 2 * (Fields.getRank_B(fieldID) + 1);
                                } else if (b_mobility_bishops_safe == 0) {
                                        b_trap_bishops += 4 * (Fields.getRank_B(fieldID) + 1);
                                }
                        }
                }
        }
        

        
        // Rooks iteration
        
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
                                
                                w_mobility_rooks_safe = 0;
                                
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

                                                boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE, toFieldID) >= 0;
                                                if (safe) {
                                                        w_mobility_rooks_safe++;
                                                }
                                                
                                                if ((toBitboard & bb_black_all) != 0L) {
                                                	break;
                                                }
                                        }
                                }
                                
                                eval_o += MOBILITY_ROOK_S_O * ALL_SignalFillerConstants.MOBILITY_ROOK_O[w_mobility_rooks_safe];
                                eval_e += MOBILITY_ROOK_S_E * ALL_SignalFillerConstants.MOBILITY_ROOK_E[w_mobility_rooks_safe];
                                
                                if (w_mobility_rooks_safe == 2) {
                                        w_trap_rooks += 1 * (Fields.getRank_W(fieldID) + 1);
                                } else if (w_mobility_rooks_safe == 1) {
                                        w_trap_rooks += 2 * (Fields.getRank_W(fieldID) + 1);
                                } else if (w_mobility_rooks_safe == 0) {
                                        w_trap_rooks += 4 * (Fields.getRank_W(fieldID) + 1);
                                }
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
                                
                                b_mobility_rooks_safe = 0;
                                
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

                                                boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE, toFieldID) >= 0;
                                                if (safe) {
                                                        b_mobility_rooks_safe++;
                                                }
                                                
                                                if ((toBitboard & bb_white_all) != 0L) {
                                                	break;
                                                }
                                        }
                                }
                                
                                eval_o -= MOBILITY_ROOK_S_O * ALL_SignalFillerConstants.MOBILITY_ROOK_O[b_mobility_rooks_safe];
                                eval_e -= MOBILITY_ROOK_S_E * ALL_SignalFillerConstants.MOBILITY_ROOK_E[b_mobility_rooks_safe];
                                
                                if (b_mobility_rooks_safe == 2) {
                                        b_trap_rooks += 1 * (Fields.getRank_B(fieldID) + 1);
                                } else if (b_mobility_rooks_safe == 1) {
                                        b_trap_rooks += 2 * (Fields.getRank_B(fieldID) + 1);
                                } else if (b_mobility_rooks_safe == 0) {
                                        b_trap_rooks += 4 * (Fields.getRank_B(fieldID) + 1);
                                }
                        }
                }
        }

        
        
        // Queens iteration
        
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
                                
                                w_mobility_queens_safe = 0;
                                
                                
                                //Move like a rook
                                
                                long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
                                int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
                                int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
                                
                                int size = validDirIDs.length;
                                for (int dir=0; dir<size; dir++) {
                                        int dirID = validDirIDs[dir];
                                        long[] dirBitboards = dirs[dirID];
                                        
                                        for (int seq=0; seq<dirBitboards.length; seq++) {
                                                long toBitboard = dirs[dirID][seq];
                                                int toFieldID = fids[dirID][seq];
                                                
                                                
                                                if ((toBitboard & bb_white_all) != 0L) {
                                                	break;
                                                }

                                                boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN, toFieldID) >= 0;
                                                if (safe) {
                                                        w_mobility_queens_safe++;
                                                }
                                                
                                                if ((toBitboard & bb_black_all) != 0L) {
                                                	break;
                                                }
                                        }
                                }
                                
                                
                                
                                // Move like a bishop
                                
                                dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
                                validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
                                fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
                                
                                size = validDirIDs.length;
                                for (int dir=0; dir<size; dir++) {
                                        int dirID = validDirIDs[dir];
                                        long[] dirBitboards = dirs[dirID];
                                        
                                        for (int seq=0; seq<dirBitboards.length; seq++) {
                                                long toBitboard = dirs[dirID][seq];
                                                int toFieldID = fids[dirID][seq];
                                                
                                                
                                                if ((toBitboard & bb_white_all) != 0L) {
                                                	break;
                                                }

                                                boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN, toFieldID) >= 0;
                                                if (safe) {
                                                        w_mobility_queens_safe++;
                                                }
                                                
                                                if ((toBitboard & bb_black_all) != 0L) {
                                                	break;
                                                }
                                        }
                                }
                                
                                eval_o += MOBILITY_QUEEN_S_O * ALL_SignalFillerConstants.MOBILITY_QUEEN_O[w_mobility_queens_safe];
                                eval_e += MOBILITY_QUEEN_S_E * ALL_SignalFillerConstants.MOBILITY_QUEEN_E[w_mobility_queens_safe];
                                
                                if (w_mobility_queens_safe == 2) {
                                        w_trap_queens += 1 * (Fields.getRank_W(fieldID) + 1);
                                } else if (w_mobility_queens_safe == 1) {
                                        w_trap_queens += 2 * (Fields.getRank_W(fieldID) + 1);
                                } else if (w_mobility_queens_safe == 0) {
                                        w_trap_queens += 4 * (Fields.getRank_W(fieldID) + 1);
                                }
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
                                
                                b_mobility_queens_safe = 0;
                                
                                
                                // Move like a rook
                                
                                long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fieldID];
                                int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
                                int[][] fids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
                                
                                int size = validDirIDs.length;
                                for (int dir=0; dir<size; dir++) {
                                        int dirID = validDirIDs[dir];
                                        long[] dirBitboards = dirs[dirID];
                                        
                                        for (int seq=0; seq<dirBitboards.length; seq++) {
                                                long toBitboard = dirs[dirID][seq];
                                                int toFieldID = fids[dirID][seq];
                                                
                                                
                                                if ((toBitboard & bb_black_all) != 0L) {
                                                	break;
                                                }

                                                boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN, toFieldID) >= 0;
                                                if (safe) {
                                                        b_mobility_queens_safe++;
                                                }
                                                
                                                if ((toBitboard & bb_white_all) != 0L) {
                                                	break;
                                                }
                                        }
                                }
                                
                                
                                
                                // Move like a bishop
                                
                                dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fieldID];
                                validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
                                fids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
                                
                                size = validDirIDs.length;
                                for (int dir=0; dir<size; dir++) {
                                        int dirID = validDirIDs[dir];
                                        long[] dirBitboards = dirs[dirID];
                                        
                                        for (int seq=0; seq<dirBitboards.length; seq++) {
                                                long toBitboard = dirs[dirID][seq];
                                                int toFieldID = fids[dirID][seq];
                                                
                                                
                                                if ((toBitboard & bb_black_all) != 0L) {
                                                	break;
                                                }

                                                boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN, toFieldID) >= 0;
                                                if (safe) {
                                                        b_mobility_queens_safe++;
                                                }
                                                
                                                if ((toBitboard & bb_white_all) != 0L) {
                                                	break;
                                                }
                                        }
                                }
                                
                                eval_o -= MOBILITY_QUEEN_S_O * ALL_SignalFillerConstants.MOBILITY_QUEEN_O[b_mobility_queens_safe];
                                eval_e -= MOBILITY_QUEEN_S_E * ALL_SignalFillerConstants.MOBILITY_QUEEN_E[b_mobility_queens_safe];
                                
                                if (b_mobility_queens_safe == 2) {
                                        b_trap_queens += 1 * (Fields.getRank_B(fieldID) + 1);
                                } else if (b_mobility_queens_safe == 1) {
                                        b_trap_queens += 2 * (Fields.getRank_B(fieldID) + 1);
                                } else if (b_mobility_queens_safe == 0) {
                                        b_trap_queens += 4 * (Fields.getRank_B(fieldID) + 1);
                                }
                        }
                }
        }
        
        
        int trap_knights = w_trap_knights - b_trap_knights;
        eval_o += TRAP_O * trap_knights;
        eval_e += TRAP_E * trap_knights;
        
        int trap_bishop = w_trap_bishops - b_trap_bishops;
        eval_o += TRAP_O * trap_bishop;
        eval_e += TRAP_E * trap_bishop;
        
        int trap_rook = w_trap_rooks - b_trap_rooks;
        eval_o += TRAP_O * trap_rook;
        eval_e += TRAP_E * trap_rook;
        
        int trap_queen = w_trap_queens - b_trap_queens;
        eval_o += TRAP_O * trap_queen;
        eval_e += TRAP_E * trap_queen;
        
        
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
                
                
                if (w_hanging_nonpawn >= ALL_SignalFillerConstants.HUNGED_PIECES_O.length) {
                        w_hanging_nonpawn = ALL_SignalFillerConstants.HUNGED_PIECES_O.length - 1;
                }
                double hunged_pieces = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.HUNGED_PIECES_O[w_hanging_nonpawn], ALL_SignalFillerConstants.HUNGED_PIECES_E[w_hanging_nonpawn]);
                eval_o += HUNGED_PIECE_O * hunged_pieces;
                eval_e += HUNGED_PIECE_E * hunged_pieces;
                
                if (w_hanging_pawn >= ALL_SignalFillerConstants.HUNGED_PAWNS_O.length) {
                        w_hanging_pawn = ALL_SignalFillerConstants.HUNGED_PAWNS_O.length - 1;
                }
                double hunged_pawns = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.HUNGED_PAWNS_O[w_hanging_pawn], ALL_SignalFillerConstants.HUNGED_PAWNS_E[w_hanging_pawn]);
                eval_o += HUNGED_PAWNS_O * hunged_pawns;
                eval_e += HUNGED_PAWNS_E * hunged_pawns;
                
                int w_hanging_all = w_hanging_nonpawn + w_hanging_pawn;
                if (w_hanging_all >= ALL_SignalFillerConstants.HUNGED_ALL_O.length) {
                        w_hanging_all = ALL_SignalFillerConstants.HUNGED_ALL_O.length - 1;
                }
                double hunged_all = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.HUNGED_ALL_O[w_hanging_all], ALL_SignalFillerConstants.HUNGED_ALL_E[w_hanging_all]);
                eval_o += HUNGED_ALL_O * hunged_all;
                eval_e += HUNGED_ALL_E * hunged_all;
                
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
                
                if (b_hanging_nonpawn >= ALL_SignalFillerConstants.HUNGED_PIECES_O.length) {
                        b_hanging_nonpawn = ALL_SignalFillerConstants.HUNGED_PIECES_O.length - 1;
                }
                double hunged_pieces = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.HUNGED_PIECES_O[b_hanging_nonpawn], ALL_SignalFillerConstants.HUNGED_PIECES_E[b_hanging_nonpawn]);
                eval_o -= HUNGED_PIECE_O * hunged_pieces;
                eval_e -= HUNGED_PIECE_E * hunged_pieces;
                
                if (b_hanging_pawn >= ALL_SignalFillerConstants.HUNGED_PAWNS_O.length) {
                        b_hanging_pawn = ALL_SignalFillerConstants.HUNGED_PAWNS_O.length - 1;
                }
                double hunged_pawns = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.HUNGED_PAWNS_O[b_hanging_pawn], ALL_SignalFillerConstants.HUNGED_PAWNS_E[b_hanging_pawn]);
                eval_o -= HUNGED_PAWNS_O * hunged_pawns;
                eval_e -= HUNGED_PAWNS_E * hunged_pawns;
                
                int b_hanging_all = b_hanging_nonpawn + b_hanging_pawn;
                if (b_hanging_all >= ALL_SignalFillerConstants.HUNGED_ALL_O.length) {
                        b_hanging_all = ALL_SignalFillerConstants.HUNGED_ALL_O.length - 1;
                }
                double hunged_all = bitboard.getMaterialFactor().interpolateByFactor(ALL_SignalFillerConstants.HUNGED_ALL_O[b_hanging_all], ALL_SignalFillerConstants.HUNGED_ALL_E[b_hanging_all]);
                eval_o -= HUNGED_ALL_O * hunged_all;
                eval_e -= HUNGED_ALL_E * hunged_all;

        }
        
        
        return interpolator.interpolateByFactor(eval_o, eval_e);

	}
}

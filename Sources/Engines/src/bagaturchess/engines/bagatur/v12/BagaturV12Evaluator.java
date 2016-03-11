package bagaturchess.engines.bagatur.v12;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.CastlingType;
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
import bagaturchess.learning.impl.eval.cfg.IWeightsEvalConfig;
import bagaturchess.learning.impl.filler.SignalFillerConstants;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.BaseEvaluator_cache;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class BagaturV12Evaluator extends BaseEvaluator {
    
    
    long RANK_7TH = Fields.DIGIT_7;
    long RANK_2TH = Fields.DIGIT_2;
    
    
    private IWeightsEvalConfig evalConfig;
    
    
    public BagaturV12Evaluator(IBitBoard _bitboard, IEvalCache _evalCache, IWeightsEvalConfig _evalConfig) {
            super(_bitboard, _evalCache, _evalConfig);
            evalConfig = _evalConfig;
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
            
            //TODO: consider depending on the super class implementation
            //Clear PST scores and calculate them from scratch in eval_pieces in order to apply weights
            eval -= interpolator.interpolateByFactor(baseEval.getPST_o(), baseEval.getPST_e());
            //if (true) throw new IllegalStateException("Who is the super class?");
            
            eval += eval_pieces();
            
            return eval;
    }
    
    
    @Override
    protected double phase4_opening() {
            double eval = 0;
            eval += mobilityKingSafetyPinsAttacks();
            return eval;
    }
    
    
    @Override
    protected double phase5_opening() {
            //double eval = 0;
            //eval += safeMobilityTraps();
            return 0;
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
            eval_o += evalConfig.getKINGSAFE_CASTLING_O() * castling;
            eval_e += evalConfig.getKINGSAFE_CASTLING_E() * castling;
            
            int fianchetto = fianchetto();
            eval_o += evalConfig.getKINGSAFE_FIANCHETTO_O() * fianchetto;
            eval_e += evalConfig.getKINGSAFE_FIANCHETTO_E() * fianchetto;
            
            double movedFGPawns = movedFGPawns();
            
            
            /**
            * Mid-game and End-game features
            */
            int double_bishop = ((w_bishops.getDataSize() >= 2) ? 1 : 0) - ((b_bishops.getDataSize() >= 2) ? 1 : 0);
            eval_o += evalConfig.getBISHOPS_DOUBLE_O() * double_bishop;
            eval_e += evalConfig.getBISHOPS_DOUBLE_E() * double_bishop;

            int double_rooks = ((w_rooks.getDataSize() >= 2) ? 1 : 0) - ((b_rooks.getDataSize() >= 2) ? 1 : 0);
            eval_o += evalConfig.getROOKS_DOUBLE_O() * double_rooks;
            eval_e += evalConfig.getROOKS_DOUBLE_E() * double_rooks;

            int double_knights = ((w_knights.getDataSize() >= 2) ? 1 : 0) - ((b_knights.getDataSize() >= 2) ? 1 : 0);
            eval_o += evalConfig.getKNIGHTS_DOUBLE_O() * double_knights;
            eval_e += evalConfig.getKNIGHTS_DOUBLE_E() * double_knights;
            
            
            //Kings Distance
            int kingFieldID_white = w_king.getData()[0];
            int kingFieldID_black = b_king.getData()[0];
            int kingDistance = Fields.getDistancePoints(kingFieldID_white, kingFieldID_black);
            if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
                    eval_o += SignalFillerConstants.KING_DISTANCE_O[kingDistance] * evalConfig.getKINGS_DISTANCE_O();
                    eval_e += SignalFillerConstants.KING_DISTANCE_E[kingDistance] * evalConfig.getKINGS_DISTANCE_E();
            } else {
                    eval_o -= SignalFillerConstants.KING_DISTANCE_O[kingDistance] * evalConfig.getKINGS_DISTANCE_O();
                    eval_e -= SignalFillerConstants.KING_DISTANCE_E[kingDistance] * evalConfig.getKINGS_DISTANCE_E();
            }
            
            //Refers to http://home.comcast.net/~danheisman/Articles/evaluation_of_material_imbalance.htm
            //
            //A further refinement would be to raise the knight's value by 1/16 and lower the rook's value by 1/8
            //for each pawn above five of the side being valued, with the opposite adjustment for each pawn short of five.
            int w_pawns_above5 = w_pawns.getDataSize() - 5;
            int b_pawns_above5 = b_pawns.getDataSize() - 5;
            
            int pawns5_rooks = w_pawns_above5 * w_rooks.getDataSize() - b_pawns_above5 * b_rooks.getDataSize();
            eval_o += pawns5_rooks * evalConfig.getPAWNS5_ROOKS_O();
            eval_e += pawns5_rooks * evalConfig.getPAWNS5_ROOKS_E();
            
            int pawns5_knights = w_pawns_above5 * w_knights.getDataSize() - b_pawns_above5 * b_knights.getDataSize();
            eval_o += pawns5_knights * evalConfig.getPAWNS5_KNIGHTS_O();
            eval_e += pawns5_knights * evalConfig.getPAWNS5_KNIGHTS_E();
            
            
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
            

            double scores_o = movedFPawn * evalConfig.getKINGSAFE_F_O() +
        missingGPawn * evalConfig.getKINGSAFE_G_O();
            double scores_e = movedFPawn * evalConfig.getKINGSAFE_F_E() +
        missingGPawn * evalConfig.getKINGSAFE_G_E();
            
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
            
            eval_o += bitboard.getBoardConfig().getWeight_PST_KING_O() * pst_king;
            eval_e += bitboard.getBoardConfig().getWeight_PST_KING_E() * pst_king;
            
            
            bitboard.getPawnsCache().lock();
            
            BagaturV12PawnsEval pawnsModelEval = (BagaturV12PawnsEval) bitboard.getPawnsStructure();
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
                                            double pst = interpolator.interpolateByFactor(SignalFillerConstants.PAWN_O[axisSymmetry(fieldID)], SignalFillerConstants.PAWN_E[axisSymmetry(fieldID)]);
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
                                    
                                    double pst = interpolator.interpolateByFactor(SignalFillerConstants.KNIGHT_O[fieldID], SignalFillerConstants.KNIGHT_E[fieldID]);
                                    eval_o += bitboard.getBoardConfig().getWeight_PST_KNIGHT_O() * pst;
                                    eval_e += bitboard.getBoardConfig().getWeight_PST_KNIGHT_E() * pst;
                                    
                                    long fieldBB = Fields.ALL_A1H1[fieldID];
                                                                            
                                // Knight outposts:
                                if ((Fields.SPACE_BLACK & fieldBB) != 0) {
                                        long bb_neighbors = ~PawnStructureConstants.WHITE_FRONT_FULL[fieldID] & PawnStructureConstants.WHITE_PASSED[fieldID];
                                        if ((bb_neighbors & bb_black_pawns) == 0) { // Weak field
                                             
                                             eval_o += evalConfig.getKNIGHT_OUTPOST_O();
                                                    eval_e += evalConfig.getKNIGHT_OUTPOST_E();
                                             
                                             if ((BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_MOVES[fieldID] & bb_white_pawns) != 0) {
                                                     
                                                     eval_o += evalConfig.getKNIGHT_OUTPOST_O();
                                                            eval_e += evalConfig.getKNIGHT_OUTPOST_E();
                                                            
                                                     if (b_knights.getDataSize() == 0) {
                                                             long colouredFields = (fieldBB & Fields.ALL_WHITE_FIELDS) != 0 ?
                                                                             Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
                                                             if ((colouredFields & bb_black_bishops) == 0) {
                                                                     eval_o += evalConfig.getKNIGHT_OUTPOST_O();
                                                                            eval_e += evalConfig.getKNIGHT_OUTPOST_E();
                                                             }
                                                     }
                                             }
                                        }
                                }
                                
                                    int tropism = Fields.getTropismPoint(fieldID, kingFieldID_black);
                                    eval_o += evalConfig.getTROPISM_KNIGHT_O() * tropism;
                                    eval_e += evalConfig.getTROPISM_KNIGHT_E() * tropism;
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
                                    eval_o -= bitboard.getBoardConfig().getWeight_PST_KNIGHT_O() * pst;
                                    eval_e -= bitboard.getBoardConfig().getWeight_PST_KNIGHT_E() * pst;
                                    
                                    long fieldBB = Fields.ALL_A1H1[fieldID];
                                    
                                // Knight outposts:
                                if ((Fields.SPACE_WHITE & fieldBB) != 0) {
                                        long bb_neighbors = ~PawnStructureConstants.BLACK_FRONT_FULL[fieldID] & PawnStructureConstants.BLACK_PASSED[fieldID];
                                        if ((bb_neighbors & bb_white_pawns) == 0) { // Weak field
                                             
                                             eval_o -= evalConfig.getKNIGHT_OUTPOST_O();
                                                    eval_e -= evalConfig.getKNIGHT_OUTPOST_E();
                                                    
                                             if ((WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_MOVES[fieldID] & bb_black_pawns) != 0) {
                                                     
                                                     eval_o -= evalConfig.getKNIGHT_OUTPOST_O();
                                                            eval_e -= evalConfig.getKNIGHT_OUTPOST_E();
                                                            
                                                     if (w_knights.getDataSize() == 0) {
                                                             long colouredFields = (fieldBB & Fields.ALL_WHITE_FIELDS) != 0 ?
                                                                             Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
                                                             if ((colouredFields & bb_white_bishops) == 0) {
                                                                     eval_o -= evalConfig.getKNIGHT_OUTPOST_O();
                                                                            eval_e -= evalConfig.getKNIGHT_OUTPOST_E();
                                                             }
                                                     }
                                             }
                                        }
                                }
                                
                                    int tropism = Fields.getTropismPoint(fieldID, kingFieldID_white);
                                    eval_o -= evalConfig.getTROPISM_KNIGHT_O() * tropism;
                                    eval_e -= evalConfig.getTROPISM_KNIGHT_E() * tropism;
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
                                    eval_o += bitboard.getBoardConfig().getWeight_PST_BISHOP_O() * pst;
                                    eval_e += bitboard.getBoardConfig().getWeight_PST_BISHOP_E() * pst;
                                    
                                    if ((Fields.ALL_WHITE_FIELDS & Fields.ALL_A1H1[fieldID]) != 0L) {
                                            eval_o += w_pawns_on_w_squares * evalConfig.getBISHOPS_BAD_O();
                                            eval_e += w_pawns_on_w_squares * evalConfig.getBISHOPS_BAD_E();
                                    } else {
                                            eval_o += w_pawns_on_b_squares * evalConfig.getBISHOPS_BAD_O();
                                            eval_e += w_pawns_on_b_squares * evalConfig.getBISHOPS_BAD_E();
                                    }
                                    
                                    int tropism = Fields.getTropismPoint(fieldID, kingFieldID_black);
                                    eval_o += evalConfig.getTROPISM_BISHOP_O() * tropism;
                                    eval_e += evalConfig.getTROPISM_BISHOP_E() * tropism;
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
                                    eval_o -= bitboard.getBoardConfig().getWeight_PST_BISHOP_O() * pst;
                                    eval_e -= bitboard.getBoardConfig().getWeight_PST_BISHOP_E() * pst;
                                    
                                    if ((Fields.ALL_WHITE_FIELDS & Fields.ALL_A1H1[fieldID]) != 0L) {
                                            eval_o -= b_pawns_on_w_squares * evalConfig.getBISHOPS_BAD_O();
                                            eval_e -= b_pawns_on_w_squares * evalConfig.getBISHOPS_BAD_E();
                                    } else {
                                            eval_o -= b_pawns_on_b_squares * evalConfig.getBISHOPS_BAD_O();
                                            eval_e -= b_pawns_on_b_squares * evalConfig.getBISHOPS_BAD_E();
                                    }
                                    
                                    int tropism = Fields.getTropismPoint(fieldID, kingFieldID_white);
                                    eval_o -= evalConfig.getTROPISM_BISHOP_O() * tropism;
                                    eval_e -= evalConfig.getTROPISM_BISHOP_E() * tropism;
                            }
                    }
            }
            
            
            /**
            * Rooks iteration
            */
            bitboard.getPawnsCache().lock();
            
            pawnsModelEval = (BagaturV12PawnsEval) bitboard.getPawnsStructure();
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
                            eval_o += bitboard.getBoardConfig().getWeight_PST_ROOK_O() * pst;
                            eval_e += bitboard.getBoardConfig().getWeight_PST_ROOK_E() * pst;
                            
                            long fieldBitboard = Fields.ALL_A1H1[fieldID];
                            if ((fieldBitboard & openedFiles_all) != 0L) {
                                    eval_o += evalConfig.getROOKS_OPENED_O();
                                    eval_e += evalConfig.getROOKS_OPENED_E();
                            } else if ((fieldBitboard & openedFiles_white) != 0L) {
                                    eval_o += evalConfig.getROOKS_SEMIOPENED_O();
                                    eval_e += evalConfig.getROOKS_SEMIOPENED_E();
                            }
                            if ((fieldBitboard & RANK_7TH) != 0L) {
                                    eval_o += evalConfig.getROOKS_7TH_2TH_O();
                                    eval_e += evalConfig.getROOKS_7TH_2TH_E();
                            }
                            
                            int tropism = Fields.getTropismPoint(fieldID, kingFieldID_black);
                            eval_o += evalConfig.getTROPISM_ROOK_O() * tropism;
                            eval_e += evalConfig.getTROPISM_ROOK_E() * tropism;
                    }
            }
            
            int b_rooks_count = b_rooks.getDataSize();
            if (b_rooks_count > 0) {
                    int[] rooks_fields = b_rooks.getData();
                    for (int i=0; i<b_rooks_count; i++) {
                            
                            
                            int fieldID = rooks_fields[i];
                            
                            double pst = interpolator.interpolateByFactor(SignalFillerConstants.ROOK_O[axisSymmetry(fieldID)], SignalFillerConstants.ROOK_E[axisSymmetry(fieldID)]);
                            eval_o -= bitboard.getBoardConfig().getWeight_PST_ROOK_O() * pst;
                            eval_e -= bitboard.getBoardConfig().getWeight_PST_ROOK_E() * pst;
                            
                            long fieldBitboard = Fields.ALL_A1H1[fieldID];
                            if ((fieldBitboard & openedFiles_all) != 0L) {
                                    eval_o -= evalConfig.getROOKS_OPENED_O();
                                    eval_e -= evalConfig.getROOKS_OPENED_E();
                            } else if ((fieldBitboard & openedFiles_black) != 0L) {
                                    eval_o -= evalConfig.getROOKS_SEMIOPENED_O();
                                    eval_e -= evalConfig.getROOKS_SEMIOPENED_E();
                            }
                            if ((fieldBitboard & RANK_2TH) != 0L) {
                                    eval_o -= evalConfig.getROOKS_7TH_2TH_O();
                                    eval_e -= evalConfig.getROOKS_7TH_2TH_E();
                            }
                            
                            int tropism = Fields.getTropismPoint(fieldID, kingFieldID_white);
                            eval_o -= evalConfig.getTROPISM_ROOK_O() * tropism;
                            eval_e -= evalConfig.getTROPISM_ROOK_E() * tropism;
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
                                    eval_o += bitboard.getBoardConfig().getWeight_PST_QUEEN_O() * pst;
                                    eval_e += bitboard.getBoardConfig().getWeight_PST_QUEEN_E() * pst;
                                    
                                    long fieldBitboard = Fields.ALL_A1H1[fieldID];
                                    if ((fieldBitboard & RANK_7TH) != 0L) {
                                            eval_o += evalConfig.getQUEENS_7TH_2TH_O();
                                            eval_e += evalConfig.getQUEENS_7TH_2TH_E();
                                    }
                                    
                                    int tropism = Fields.getTropismPoint(fieldID, kingFieldID_black);
                                    eval_o += evalConfig.getTROPISM_QUEEN_O() * tropism;
                                    eval_e += evalConfig.getTROPISM_QUEEN_E() * tropism;
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
                                    eval_o -= bitboard.getBoardConfig().getWeight_PST_QUEEN_O() * pst;
                                    eval_e -= bitboard.getBoardConfig().getWeight_PST_QUEEN_E() * pst;
                                    
                                    long fieldBitboard = Fields.ALL_A1H1[fieldID];
                                    if ((fieldBitboard & RANK_2TH) != 0L) {
                                            eval_o -= evalConfig.getQUEENS_7TH_2TH_O();
                                            eval_e -= evalConfig.getQUEENS_7TH_2TH_E();
                                    }
                                    
                                    int tropism = Fields.getTropismPoint(fieldID, kingFieldID_white);
                                    eval_o -= evalConfig.getTROPISM_QUEEN_O() * tropism;
                                    eval_e -= evalConfig.getTROPISM_QUEEN_E() * tropism;
                            }
                    }
            }
            
            
            return interpolator.interpolateByFactor(eval_o, eval_e);
    }
    
    
    private int eval_pawns() {
            
            long bb_w_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE);
            long bb_b_rooks = bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE);
            
            bitboard.getPawnsCache().lock();
            
            BagaturV12PawnsEval pawnsModelEval = (BagaturV12PawnsEval) bitboard.getPawnsStructure();
            PawnsModel model = pawnsModelEval.getModel();
            
            double eval_o = pawnsModelEval.getEval_o();
            double eval_e = pawnsModelEval.getEval_e();
            
            //int PAWNS_PASSED_UNSTOPPABLE = 100 + baseEval.getMaterialRook();
            int unstoppablePasser = bitboard.getUnstoppablePasser();
            if (unstoppablePasser > 0) {
                    eval_o += evalConfig.getPAWNS_UNSTOPPABLE_PASSER_O();
                    eval_e += evalConfig.getPAWNS_UNSTOPPABLE_PASSER_E();
            } else if (unstoppablePasser < 0) {
                    eval_o -= evalConfig.getPAWNS_UNSTOPPABLE_PASSER_O();
                    eval_e -= evalConfig.getPAWNS_UNSTOPPABLE_PASSER_E();
            }
            
            int space = space(model);
            eval_o += space * evalConfig.getSPACE_O();
            eval_e += space * evalConfig.getSPACE_E();
            
            
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
                            
                            eval_o += evalConfig.getPAWNS_PASSED_O();
                            eval_e += evalConfig.getPAWNS_PASSED_E();
                            
                            int passer = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.PASSERS_RANK_O[rank], SignalFillerConstants.PASSERS_RANK_E[rank]);
                            eval_o += evalConfig.getPAWNS_PASSED_RNK_O() * passer;
                            eval_e += evalConfig.getPAWNS_PASSED_RNK_E() * passer;
                            
                    int frontFieldID = p.getFieldID() + 8;
                    int frontFrontFieldID = frontFieldID + 8;
                    if (frontFrontFieldID >= 64) {
                     frontFrontFieldID = frontFieldID;
                    }
                    
                    int dist_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD[Fields.getDistancePoints(w_kingID, frontFieldID)];
                    eval_o += evalConfig.getKINGS_PASSERS_F_O() * dist_f;
                    eval_e += evalConfig.getKINGS_PASSERS_F_E() * dist_f;
                    
                    int dist_ff = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[Fields.getDistancePoints(w_kingID, frontFrontFieldID)];
                    eval_o += evalConfig.getKINGS_PASSERS_FF_O() * dist_ff;
                    eval_e += evalConfig.getKINGS_PASSERS_FF_E() * dist_ff;
                    
                    int dist_op_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD_OP[Fields.getDistancePoints(b_kingID, frontFieldID)];
                    eval_o += evalConfig.getKINGS_PASSERS_F_OP_O() * dist_op_f;
                    eval_e += evalConfig.getKINGS_PASSERS_F_OP_E() * dist_op_f;
                            
                            
                            long front = p.getFront();
                            if ((front & bb_w_rooks) != 0L) {
                                    eval_o += 1 * evalConfig.getROOK_INFRONT_PASSER_O();
                                    eval_e += 1 * evalConfig.getROOK_INFRONT_PASSER_E();
                            }
                            
                            long behind = p.getVertical() & ~front;
                            if ((behind & bb_w_rooks) != 0L) {
                                    eval_o += 1 * evalConfig.getROOK_BEHIND_PASSER_O();
                                    eval_e += 1 * evalConfig.getROOK_BEHIND_PASSER_E();
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
                            
                            eval_o -= evalConfig.getPAWNS_PASSED_O();
                            eval_e -= evalConfig.getPAWNS_PASSED_E();
                            
                            int passer = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.PASSERS_RANK_O[rank], SignalFillerConstants.PASSERS_RANK_E[rank]);
                            eval_o -= evalConfig.getPAWNS_PASSED_RNK_O() * passer;
                            eval_e -= evalConfig.getPAWNS_PASSED_RNK_E() * passer;
                            
                    int frontFieldID = p.getFieldID() - 8;
                    int frontFrontFieldID = frontFieldID - 8;
                    if (frontFrontFieldID < 0) {
                     frontFrontFieldID = frontFieldID;
                    }
                    
                    int dist_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD[Fields.getDistancePoints(b_kingID, frontFieldID)];
                    eval_o -= evalConfig.getKINGS_PASSERS_F_O() * dist_f;
                    eval_e -= evalConfig.getKINGS_PASSERS_F_E() * dist_f;
                    
                    int dist_ff = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[Fields.getDistancePoints(b_kingID, frontFrontFieldID)];
                    eval_o -= evalConfig.getKINGS_PASSERS_FF_O() * dist_ff;
                    eval_e -= evalConfig.getKINGS_PASSERS_FF_E() * dist_ff;
                    
                    int dist_op_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD_OP[Fields.getDistancePoints(w_kingID, frontFieldID)];
                    eval_o -= evalConfig.getKINGS_PASSERS_F_OP_O() * dist_op_f;
                    eval_e -= evalConfig.getKINGS_PASSERS_F_OP_E() * dist_op_f; 
                    
                            
                            long front = p.getFront();
                            if ((front & bb_b_rooks) != 0L) {
                                    eval_o -= 1 * evalConfig.getROOK_INFRONT_PASSER_O();
                                    eval_e -= 1 * evalConfig.getROOK_INFRONT_PASSER_E();
                            }
                            
                            long behind = p.getVertical() & ~front;
                            if ((behind & bb_b_rooks) != 0L) {
                                    eval_o -= 1 * evalConfig.getROOK_BEHIND_PASSER_O();
                                    eval_e -= 1 * evalConfig.getROOK_BEHIND_PASSER_E();
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
                                    eval_o += evalConfig.getPAWNS_CANDIDATE_RNK_O() *
                        passerCandidate;
                                    eval_e += evalConfig.getPAWNS_CANDIDATE_RNK_E() *
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
                                    
                                    int passerCandidate = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.PASSERS_CANDIDATE_RANK_O[rank], SignalFillerConstants.PASSERS_CANDIDATE_RANK_E[rank]);
                                    eval_o -= evalConfig.getPAWNS_CANDIDATE_RNK_O() *
                        passerCandidate;
                                    eval_e -= evalConfig.getPAWNS_CANDIDATE_RNK_E() *
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
            
            int w_penetration_op_area = 0;
            int b_penetration_op_area = 0;
            int w_penetration_king_area = 0;
            int b_penetration_king_area = 0;

            int w_mobility_knights_all = 0;
            int b_mobility_knights_all = 0;
            int w_mobility_bishops_all = 0;
            int b_mobility_bishops_all = 0;
            int w_mobility_rooks_all = 0;
            int b_mobility_rooks_all = 0;
            int w_mobility_queens_all = 0;
            int b_mobility_queens_all = 0;
            
            /*int w_mobility_knights_safe = 0;
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
            int b_trap_queens = 0;*/
            
            //int w_hanging_nonpawn = 0;
            //int b_hanging_nonpawn = 0;
            //int w_hanging_pawn = 0;
            //int b_hanging_pawn = 0;
            
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
                    /*int w_pawns_count = w_pawns.getDataSize();
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
                    }*/
            }
            {
                    /*int b_pawns_count = b_pawns.getDataSize();
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
                    }*/
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
                                    
                                    
                                    /*if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
                                            int see = bitboard.getSee().seeField(fieldID);
                                            if (see < 0) {
                                                    w_hanging_nonpawn++;
                                            }
                                    }*/
                                    
                                    
                                    w_mobility_knights_all = 0;
                                    
                                    //w_mobility_knights_safe = 0;
                                    
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
                                            
                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT, toFieldID) >= 0;
                                            if (safe) {
                                                    w_mobility_knights_safe++;
                                            }*/
                                    }
                                    
                                    eval_o += evalConfig.getMOBILITY_KNIGHT_O() *
                        SignalFillerConstants.MOBILITY_KNIGHT_O[w_mobility_knights_all];
                                    eval_e += evalConfig.getMOBILITY_KNIGHT_E() *
                        SignalFillerConstants.MOBILITY_KNIGHT_E[w_mobility_knights_all];
                                    /*eval_o += MOBILITY_KNIGHT_S_O * SignalFillerConstants.MOBILITY_KNIGHT_O[w_mobility_knights_safe];
                                    eval_e += MOBILITY_KNIGHT_S_E * SignalFillerConstants.MOBILITY_KNIGHT_E[w_mobility_knights_safe];
                                                                            
                                    if (w_mobility_knights_safe == 2) {
                                            w_trap_knights += 1 * (Fields.getRank_W(fieldID) + 1);
                                    } else if (w_mobility_knights_safe == 1) {
                                            w_trap_knights += 2 * (Fields.getRank_W(fieldID) + 1);
                                    } else if (w_mobility_knights_safe == 0) {
                                            w_trap_knights += 4 * (Fields.getRank_W(fieldID) + 1);
                                    }*/
                                    
                                    w_knights_attacks_to_black_king_1 += SignalFillerConstants.KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_1];
                                    w_knights_attacks_to_black_king_2 += SignalFillerConstants.KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            {
                    int b_knights_count = b_knights.getDataSize();          
                    if (b_knights_count > 0) {
                            int[] knights_fields = b_knights.getData();
                            for (int i=0; i<b_knights_count; i++) {
                                                                            
                                    
                                    int fieldID = knights_fields[i];
                                    
                                    /*if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
                                            int see = bitboard.getSee().seeField(fieldID);
                                            if (see < 0) {
                                                    b_hanging_nonpawn++;
                                            }
                                    }*/
                                    
                                    
                                    b_mobility_knights_all = 0;
                                    
                                    //b_mobility_knights_safe = 0;
                                    
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
                                            
                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT, toFieldID) >= 0;
                                            if (safe) {
                                                    b_mobility_knights_safe++;
                                            }*/
                                    }
                                    
                                    eval_o -= evalConfig.getMOBILITY_KNIGHT_O() *
                        SignalFillerConstants.MOBILITY_KNIGHT_O[b_mobility_knights_all];
                                    eval_e -= evalConfig.getMOBILITY_KNIGHT_E() *
                        SignalFillerConstants.MOBILITY_KNIGHT_E[b_mobility_knights_all];
                                    /*eval_o -= MOBILITY_KNIGHT_S_O * SignalFillerConstants.MOBILITY_KNIGHT_O[b_mobility_knights_safe];
                                    eval_e -= MOBILITY_KNIGHT_S_E * SignalFillerConstants.MOBILITY_KNIGHT_E[b_mobility_knights_safe];
                                    
                                    if (b_mobility_knights_safe == 2) {
                                            b_trap_knights += 1 * (Fields.getRank_B(fieldID) + 1);
                                    } else if (b_mobility_knights_safe == 1) {
                                            b_trap_knights += 2 * (Fields.getRank_B(fieldID) + 1);
                                    } else if (b_mobility_knights_safe == 0) {
                                            b_trap_knights += 4 * (Fields.getRank_B(fieldID) + 1);
                                    }*/
                                    
                                    b_knights_attacks_to_white_king_1 += SignalFillerConstants.KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_1];
                                    b_knights_attacks_to_white_king_2 += SignalFillerConstants.KING_SAFETY_KNIGHTS_ATTACKS[opking_attacks_counter_2];
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
                                    
                                    /*if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
                                            int see = bitboard.getSee().seeField(fieldID);
                                            if (see < 0) {
                                                    w_hanging_nonpawn++;
                                            }
                                    }*/
                                    
                                    w_mobility_bishops_all = 0;
                                    
                                    //w_mobility_bishops_safe = 0;
                                    
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
                                                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER, toFieldID) >= 0;
                                                                            if (safe) {
                                                                                    w_mobility_bishops_safe++;
                                                                            }*/
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
                                    
                                    eval_o += evalConfig.getMOBILITY_BISHOP_O() *
                        SignalFillerConstants.MOBILITY_BISHOP_O[w_mobility_bishops_all];
                                    eval_e += evalConfig.getMOBILITY_BISHOP_E() *
                        SignalFillerConstants.MOBILITY_BISHOP_E[w_mobility_bishops_all];
                                    /*eval_o += MOBILITY_BISHOP_S_O * SignalFillerConstants.MOBILITY_BISHOP_O[w_mobility_bishops_safe];
                                    eval_e += MOBILITY_BISHOP_S_E * SignalFillerConstants.MOBILITY_BISHOP_E[w_mobility_bishops_safe];
                                    
                                    if (w_mobility_bishops_safe == 2) {
                                            w_trap_bishops += 1 * (Fields.getRank_W(fieldID) + 1);
                                    } else if (w_mobility_bishops_safe == 1) {
                                            w_trap_bishops += 2 * (Fields.getRank_W(fieldID) + 1);
                                    } else if (w_mobility_bishops_safe == 0) {
                                            w_trap_bishops += 4 * (Fields.getRank_W(fieldID) + 1);
                                    }*/
                                    
                                    w_bishops_attacks_to_black_king_1 += SignalFillerConstants.KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_1];
                                    w_bishops_attacks_to_black_king_2 += SignalFillerConstants.KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            {
                    int b_bishops_count = b_bishops.getDataSize();
                    if (b_bishops_count > 0) {
                            int[] bishops_fields = b_bishops.getData();
                            for (int i=0; i<b_bishops_count; i++) {
                                    
                                    
                                    int fieldID = bishops_fields[i];
                                    
                                    /*if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
                                            int see = bitboard.getSee().seeField(fieldID);
                                            if (see < 0) {
                                                    b_hanging_nonpawn++;
                                            }
                                    }*/
                                    
                                    b_mobility_bishops_all = 0;
                                    
                                    //b_mobility_bishops_safe = 0;
                                    
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
                                                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER, toFieldID) >= 0;
                                                                            if (safe) {
                                                                                    b_mobility_bishops_safe++;
                                                                            }*/
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
                                    
                                    eval_o -= evalConfig.getMOBILITY_BISHOP_O() *
                        SignalFillerConstants.MOBILITY_BISHOP_O[b_mobility_bishops_all];
                                    eval_e -= evalConfig.getMOBILITY_BISHOP_E() *
                        SignalFillerConstants.MOBILITY_BISHOP_E[b_mobility_bishops_all];
                                    /*eval_o -= MOBILITY_BISHOP_S_O * SignalFillerConstants.MOBILITY_BISHOP_O[b_mobility_bishops_safe];
                                    eval_e -= MOBILITY_BISHOP_S_E * SignalFillerConstants.MOBILITY_BISHOP_E[b_mobility_bishops_safe];
                                    
                                    if (b_mobility_bishops_safe == 2) {
                                            b_trap_bishops += 1 * (Fields.getRank_B(fieldID) + 1);
                                    } else if (b_mobility_bishops_safe == 1) {
                                            b_trap_bishops += 2 * (Fields.getRank_B(fieldID) + 1);
                                    } else if (b_mobility_bishops_safe == 0) {
                                            b_trap_bishops += 4 * (Fields.getRank_B(fieldID) + 1);
                                    }*/
                                    
                                    b_bishops_attacks_to_white_king_1 += SignalFillerConstants.KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_1];
                                    b_bishops_attacks_to_white_king_2 += SignalFillerConstants.KING_SAFETY_BISHOPS_ATTACKS[opking_attacks_counter_2];
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
                                    
                                    /*if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
                                            int see = bitboard.getSee().seeField(fieldID);
                                            if (see < 0) {
                                                    w_hanging_nonpawn++;
                                            }
                                    }*/
                                    
                                    w_mobility_rooks_all = 0;
                                    
                                    //w_mobility_rooks_safe = 0;
                                    
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
                                                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE, toFieldID) >= 0;
                                                                            if (safe) {
                                                                                    w_mobility_rooks_safe++;
                                                                            }*/
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
                                    
                                    eval_o += evalConfig.getMOBILITY_ROOK_O() *
                        SignalFillerConstants.MOBILITY_ROOK_O[w_mobility_rooks_all];
                                    eval_e += evalConfig.getMOBILITY_ROOK_E() *
                        SignalFillerConstants.MOBILITY_ROOK_E[w_mobility_rooks_all];
                                    /*eval_o += MOBILITY_ROOK_S_O * SignalFillerConstants.MOBILITY_ROOK_O[w_mobility_rooks_safe];
                                    eval_e += MOBILITY_ROOK_S_E * SignalFillerConstants.MOBILITY_ROOK_E[w_mobility_rooks_safe];
                                    
                                    if (w_mobility_rooks_safe == 2) {
                                            w_trap_rooks += 1 * (Fields.getRank_W(fieldID) + 1);
                                    } else if (w_mobility_rooks_safe == 1) {
                                            w_trap_rooks += 2 * (Fields.getRank_W(fieldID) + 1);
                                    } else if (w_mobility_rooks_safe == 0) {
                                            w_trap_rooks += 4 * (Fields.getRank_W(fieldID) + 1);
                                    }*/
                                    
                                    w_rooks_attacks_to_black_king_1 += SignalFillerConstants.KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_1];
                                    w_rooks_attacks_to_black_king_2 += SignalFillerConstants.KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            {
                    int b_rooks_count = b_rooks.getDataSize();
                    if (b_rooks_count > 0) {
                            int[] rooks_fields = b_rooks.getData();
                            for (int i=0; i<b_rooks_count; i++) {
                                    
                                    
                                    int fieldID = rooks_fields[i];

                                    /*if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
                                            int see = bitboard.getSee().seeField(fieldID);
                                            if (see < 0) {
                                                    b_hanging_nonpawn++;
                                            }
                                    }*/
                                    
                                    b_mobility_rooks_all = 0;
                                    
                                    //b_mobility_rooks_safe = 0;
                                    
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
                                                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE, toFieldID) >= 0;
                                                                            if (safe) {
                                                                                    b_mobility_rooks_safe++;
                                                                            }*/
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
                                    
                                    eval_o -= evalConfig.getMOBILITY_ROOK_O() *
                        SignalFillerConstants.MOBILITY_ROOK_O[b_mobility_rooks_all];
                                    eval_e -= evalConfig.getMOBILITY_ROOK_E() *
                        SignalFillerConstants.MOBILITY_ROOK_E[b_mobility_rooks_all];
                                    /*eval_o -= MOBILITY_ROOK_S_O * SignalFillerConstants.MOBILITY_ROOK_O[b_mobility_rooks_safe];
                                    eval_e -= MOBILITY_ROOK_S_E * SignalFillerConstants.MOBILITY_ROOK_E[b_mobility_rooks_safe];
                                    
                                    if (b_mobility_rooks_safe == 2) {
                                            b_trap_rooks += 1 * (Fields.getRank_B(fieldID) + 1);
                                    } else if (b_mobility_rooks_safe == 1) {
                                            b_trap_rooks += 2 * (Fields.getRank_B(fieldID) + 1);
                                    } else if (b_mobility_rooks_safe == 0) {
                                            b_trap_rooks += 4 * (Fields.getRank_B(fieldID) + 1);
                                    }*/
                                    
                                    b_rooks_attacks_to_white_king_1 += SignalFillerConstants.KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_1];
                                    b_rooks_attacks_to_white_king_2 += SignalFillerConstants.KING_SAFETY_ROOKS_ATTACKS[opking_attacks_counter_2];
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
                                    
                                    /*if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
                                            int see = bitboard.getSee().seeField(fieldID);
                                            if (see < 0) {
                                                    w_hanging_nonpawn++;
                                            }
                                    }*/
                                    
                                    w_mobility_queens_all = 0;
                                    
                                    //w_mobility_queens_safe = 0;
                                    
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
                                                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN, toFieldID) >= 0;
                                                                            if (safe) {
                                                                                    w_mobility_queens_safe++;
                                                                            }*/
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
                                                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN, toFieldID) >= 0;
                                                                            if (safe) {
                                                                                    w_mobility_queens_safe++;
                                                                            }*/
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
                                    
                                    eval_o += evalConfig.getMOBILITY_QUEEN_O() *
                        SignalFillerConstants.MOBILITY_QUEEN_O[w_mobility_queens_all];
                                    eval_e += evalConfig.getMOBILITY_QUEEN_E() *
                        SignalFillerConstants.MOBILITY_QUEEN_E[w_mobility_queens_all];
                                    /*eval_o += MOBILITY_QUEEN_S_O * SignalFillerConstants.MOBILITY_QUEEN_O[w_mobility_queens_safe];
                                    eval_e += MOBILITY_QUEEN_S_E * SignalFillerConstants.MOBILITY_QUEEN_E[w_mobility_queens_safe];
                                    
                                    if (w_mobility_queens_safe == 2) {
                                            w_trap_queens += 1 * (Fields.getRank_W(fieldID) + 1);
                                    } else if (w_mobility_queens_safe == 1) {
                                            w_trap_queens += 2 * (Fields.getRank_W(fieldID) + 1);
                                    } else if (w_mobility_queens_safe == 0) {
                                            w_trap_queens += 4 * (Fields.getRank_W(fieldID) + 1);
                                    }*/
                                    
                                    w_queens_attacks_to_black_king_1 += SignalFillerConstants.KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_1];
                                    w_queens_attacks_to_black_king_2 += SignalFillerConstants.KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            {
                    int b_queens_count = b_queens.getDataSize();
                    if (b_queens_count > 0) {
                            int[] queens_fields = b_queens.getData();
                            for (int i=0; i<b_queens_count; i++) {
                                    
                                    
                                    int fieldID = queens_fields[i];
                                    
                                    /*if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
                                            int see = bitboard.getSee().seeField(fieldID);
                                            if (see < 0) {
                                                    b_hanging_nonpawn++;
                                            }
                                    }*/
                                    
                                    b_mobility_queens_all = 0;
                                    
                                    //b_mobility_queens_safe = 0;
                                    
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
                                                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN, toFieldID) >= 0;
                                                                            if (safe) {
                                                                                    b_mobility_queens_safe++;
                                                                            }*/
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
                                                                            /*boolean safe = bitboard.getSee().seeMove(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN, toFieldID) >= 0;
                                                                            if (safe) {
                                                                                    b_mobility_queens_safe++;
                                                                            }*/
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
                                    
                                    eval_o -= evalConfig.getMOBILITY_QUEEN_O() *
                        SignalFillerConstants.MOBILITY_QUEEN_O[b_mobility_queens_all];
                                    eval_e -= evalConfig.getMOBILITY_QUEEN_E() *
                        SignalFillerConstants.MOBILITY_QUEEN_E[b_mobility_queens_all];
                                    /*eval_o -= MOBILITY_QUEEN_S_O * SignalFillerConstants.MOBILITY_QUEEN_O[b_mobility_queens_safe];
                                    eval_e -= MOBILITY_QUEEN_S_E * SignalFillerConstants.MOBILITY_QUEEN_E[b_mobility_queens_safe];
                                    
                                    if (b_mobility_queens_safe == 2) {
                                            b_trap_queens += 1 * (Fields.getRank_B(fieldID) + 1);
                                    } else if (b_mobility_queens_safe == 1) {
                                            b_trap_queens += 2 * (Fields.getRank_B(fieldID) + 1);
                                    } else if (b_mobility_queens_safe == 0) {
                                            b_trap_queens += 4 * (Fields.getRank_B(fieldID) + 1);
                                    }*/     
                                    
                                    b_queens_attacks_to_white_king_1 += SignalFillerConstants.KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_1];
                                    b_queens_attacks_to_white_king_2 += SignalFillerConstants.KING_SAFETY_QUEENS_ATTACKS[opking_attacks_counter_2];
                            }
                    }
            }
            
            
            int rooks_paired_h = (w_rooks_paired_h - b_rooks_paired_h);
            eval_o += evalConfig.getROOKS_PAIR_H_O() * rooks_paired_h;
            eval_e += evalConfig.getROOKS_PAIR_H_E() * rooks_paired_h;
            
            int rooks_paired_v = (w_rooks_paired_v - b_rooks_paired_v);
            eval_o += evalConfig.getROOKS_PAIR_V_O() * rooks_paired_v;
            eval_e += evalConfig.getROOKS_PAIR_V_E() * rooks_paired_v;
            
            
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
            eval_o += evalConfig.getKINGSAFETY_L1_O() * kingsafe_l1;
            eval_e += evalConfig.getKINGSAFETY_L1_E() * kingsafe_l1;
            
            
            int kingsafe_l2 = (w_attacking_pieces_to_black_king_2 * w_attack_to_black_king_2 - b_attacking_pieces_to_white_king_2 * b_attack_to_white_king_2) / (8 * 8);
            eval_o += evalConfig.getKINGSAFETY_L2_O() * kingsafe_l2;
            eval_e += evalConfig.getKINGSAFETY_L2_E() * kingsafe_l2;
            
            
            int pin_k = pin_bk + pin_rk + pin_qk;
            eval_o += evalConfig.getPIN_KING_O() * pin_k;
            eval_e += evalConfig.getPIN_KING_E() * pin_k;
            
            int pin_big = pin_bq + pin_br + pin_rq;
            eval_o += evalConfig.getPIN_BIGGER_O() * pin_big;
            eval_e += evalConfig.getPIN_BIGGER_E() * pin_big;
            
            int pin_eq = pin_bn;
            eval_o += evalConfig.getPIN_EQUAL_O() * pin_eq;
            eval_e += evalConfig.getPIN_EQUAL_E() * pin_eq;
            
            int pin_lower = pin_rb + pin_rn + pin_qn + pin_qr + pin_qb;
            eval_o += evalConfig.getPIN_LOWER_O() * pin_lower;
            eval_e += evalConfig.getPIN_LOWER_O() * pin_lower;
            
            
            int attack_bigger = attack_nr + attack_nq + attack_br;
            eval_o += evalConfig.getATTACK_BIGGER_O() * attack_bigger;
            eval_e += evalConfig.getATTACK_BIGGER_O() * attack_bigger;
            
            int attack_eq = attack_nb + attack_bn;
            eval_o += evalConfig.getATTACK_EQUAL_O() * attack_eq;
            eval_e += evalConfig.getATTACK_EQUAL_E() * attack_eq;
            
            int attack_lower = attack_rb + attack_rn + attack_qn + attack_qb + attack_qr;
            eval_o += evalConfig.getATTACK_LOWER_O() * attack_lower;
            eval_e += evalConfig.getATTACK_LOWER_E() * attack_lower;
            
            
            
            /*int trap_knights = w_trap_knights - b_trap_knights;
            eval_o += TRAP_KNIGHT_O * trap_knights;
            eval_e += TRAP_KNIGHT_E * trap_knights;
            
            int trap_bishop = w_trap_bishops - b_trap_bishops;
            eval_o += TRAP_BISHOP_O * trap_bishop;
            eval_e += TRAP_BISHOP_E * trap_bishop;
            
            int trap_rook = w_trap_rooks - b_trap_rooks;
            eval_o += TRAP_ROOK_O * trap_rook;
            eval_e += TRAP_ROOK_E * trap_rook;
            
            int trap_queen = w_trap_queens - b_trap_queens;
            eval_o += TRAP_QUEEN_O * trap_queen;
            eval_e += TRAP_QUEEN_E * trap_queen;*/
            
            
            /*if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
                    eval_o += HUNGED_PIECE_O * w_hanging_nonpawn;
                    eval_e += HUNGED_PIECE_E * w_hanging_nonpawn;
            } else {
                    eval_o -= HUNGED_PIECE_O * b_hanging_nonpawn;
                    eval_e -= HUNGED_PIECE_E * b_hanging_nonpawn;
            }*/
            
            /*if (bitboard.getColourToMove() == Figures.COLOUR_WHITE) {
                    
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
                    
                    
                    if (w_hanging_nonpawn >= SignalFillerConstants.HUNGED_PIECES_O.length) {
                            w_hanging_nonpawn = SignalFillerConstants.HUNGED_PIECES_O.length - 1;
                    }
                    double hunged_pieces = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.HUNGED_PIECES_O[w_hanging_nonpawn], SignalFillerConstants.HUNGED_PIECES_E[w_hanging_nonpawn]);
                    eval_o += evalConfig.getHUNGED_PIECE_O() * hunged_pieces;
                    eval_e += evalConfig.getHUNGED_PIECE_E() * hunged_pieces;
                    
                    if (w_hanging_pawn >= SignalFillerConstants.HUNGED_PAWNS_O.length) {
                            w_hanging_pawn = SignalFillerConstants.HUNGED_PAWNS_O.length - 1;
                    }
                    double hunged_pawns = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.HUNGED_PAWNS_O[w_hanging_pawn], SignalFillerConstants.HUNGED_PAWNS_E[w_hanging_pawn]);
                    eval_o += evalConfig.getHUNGED_PAWNS_O() * hunged_pawns;
                    eval_e += evalConfig.getHUNGED_PAWNS_E() * hunged_pawns;
                    
                    int w_hanging_all = w_hanging_nonpawn + w_hanging_pawn;
                    if (w_hanging_all >= SignalFillerConstants.HUNGED_ALL_O.length) {
                            w_hanging_all = SignalFillerConstants.HUNGED_ALL_O.length - 1;
                    }
                    double hunged_all = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.HUNGED_ALL_O[w_hanging_all], SignalFillerConstants.HUNGED_ALL_E[w_hanging_all]);
                    eval_o += evalConfig.getHUNGED_ALL_O() * hunged_all;
                    eval_e += evalConfig.getHUNGED_ALL_E() * hunged_all;
                    
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
                    
                    if (b_hanging_nonpawn >= SignalFillerConstants.HUNGED_PIECES_O.length) {
                            b_hanging_nonpawn = SignalFillerConstants.HUNGED_PIECES_O.length - 1;
                    }
                    double hunged_pieces = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.HUNGED_PIECES_O[b_hanging_nonpawn], SignalFillerConstants.HUNGED_PIECES_E[b_hanging_nonpawn]);
                    eval_o -= evalConfig.getHUNGED_PIECE_O() * hunged_pieces;
                    eval_e -= evalConfig.getHUNGED_PIECE_E() * hunged_pieces;
                    
                    if (b_hanging_pawn >= SignalFillerConstants.HUNGED_PAWNS_O.length) {
                            b_hanging_pawn = SignalFillerConstants.HUNGED_PAWNS_O.length - 1;
                    }
                    double hunged_pawns = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.HUNGED_PAWNS_O[b_hanging_pawn], SignalFillerConstants.HUNGED_PAWNS_E[b_hanging_pawn]);
                    eval_o -= evalConfig.getHUNGED_PAWNS_O() * hunged_pawns;
                    eval_e -= evalConfig.getHUNGED_PAWNS_E() * hunged_pawns;
                    
                    int b_hanging_all = b_hanging_nonpawn + b_hanging_pawn;
                    if (b_hanging_all >= SignalFillerConstants.HUNGED_ALL_O.length) {
                            b_hanging_all = SignalFillerConstants.HUNGED_ALL_O.length - 1;
                    }
                    double hunged_all = bitboard.getMaterialFactor().interpolateByFactor(SignalFillerConstants.HUNGED_ALL_O[b_hanging_all], SignalFillerConstants.HUNGED_ALL_E[b_hanging_all]);
                    eval_o -= evalConfig.getHUNGED_ALL_O() * hunged_all;
                    eval_e -= evalConfig.getHUNGED_ALL_E() * hunged_all;

            }*/
            
            
            eval_o += evalConfig.getPENETRATION_OP_O() *
            (w_penetration_op_area - b_penetration_op_area);
            eval_e += evalConfig.getPENETRATION_OP_E() *
            (w_penetration_op_area - b_penetration_op_area);
            eval_o += evalConfig.getPENETRATION_KING_O() *
            (w_penetration_king_area - b_penetration_king_area);
            eval_e += evalConfig.getPENETRATION_KING_E() *
            (w_penetration_king_area - b_penetration_king_area);
            
            
            return interpolator.interpolateByFactor(eval_o, eval_e);

    }
}

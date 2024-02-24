package bagaturchess.learning.goldmiddle.impl8.eval;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.goldmiddle.api.IEvalComponentsProcessor;
import bagaturchess.learning.goldmiddle.impl7.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl7.filler.Bagatur_V41_FeaturesConfigurationImpl;
import bagaturchess.learning.goldmiddle.impl8.filler.Bagatur_PST_FeaturesConfigurationImpl;
import bagaturchess.learning.goldmiddle.impl8.filler.Bagatur_PST_FeaturesConstants;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases_PST_GOLDENMIDDLE extends BaseEvaluator implements Bagatur_PST_FeaturesConstants {
	
	
	private static final int MAX_MATERIAL_FACTOR = 4 * EvalConstants.PHASE[NIGHT] + 4 * EvalConstants.PHASE[BISHOP] + 4 * EvalConstants.PHASE[ROOK] + 2 * EvalConstants.PHASE[QUEEN];
	
	
	private final EvalInfo evalinfo;
	
	private final ChessBoard board;
	
	private Features_Splitter features_splitter;
	

	public BagaturEvaluator_Phases_PST_GOLDENMIDDLE(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		board = ((BoardImpl)bitboard).getChessBoard();
		
		evalinfo = new EvalInfo();
		
		try {
			
			features_splitter = Features_Splitter.load(Features_Splitter.FEATURES_FILE_NAME, Bagatur_PST_FeaturesConfigurationImpl.class.getName());
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	@Override
	public int fullEval(int depth, int alpha, int beta, int rootColour) {
		
		int eval = super.fullEval(depth, alpha, beta, rootColour);
		
		return eval;
	}
	
	
	@Override
	protected void phase0_init() {
		
		evalinfo.clearEvals();
		
		evalinfo.fillBoardInfo(board);
	}
	
	
	@Override
	protected int phase1() {
		
		IFeature[] features_o = features_splitter.getFeatures(1);
		IFeature[] features_e = features_splitter.getFeatures(0);
		
		double eval_o = 0;
		double eval_e = 0;
		
		
		//Pawns
		long piece = evalinfo.getPieces(WHITE, PAWN);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  1 * features_o[FEATURE_ID_PST_PAWN].getWeight(square_index);
			eval_e +=  1 * features_e[FEATURE_ID_PST_PAWN].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		piece = evalinfo.getPieces(BLACK, PAWN);
		
		while (piece != 0) {
			
			final int square_index = EvalConstants.MIRRORED_UP_DOWN[Long.numberOfTrailingZeros(piece)];
			
			eval_o +=  -1 * features_o[FEATURE_ID_PST_PAWN].getWeight(square_index);
			eval_e +=  -1 * features_e[FEATURE_ID_PST_PAWN].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		//Kings
		piece = evalinfo.getPieces(WHITE, KING);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  1 * features_o[FEATURE_ID_PST_KING].getWeight(square_index);
			eval_e +=  1 * features_e[FEATURE_ID_PST_KING].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		piece = evalinfo.getPieces(BLACK, KING);
		
		while (piece != 0) {
			
			final int square_index = EvalConstants.MIRRORED_UP_DOWN[Long.numberOfTrailingZeros(piece)];
			
			eval_o +=  -1 * features_o[FEATURE_ID_PST_KING].getWeight(square_index);
			eval_e +=  -1 * features_e[FEATURE_ID_PST_KING].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		//Queens
		piece = evalinfo.getPieces(WHITE, QUEEN);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  1 * features_o[FEATURE_ID_PST_QUEEN].getWeight(square_index);
			eval_e +=  1 * features_e[FEATURE_ID_PST_QUEEN].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		piece = evalinfo.getPieces(BLACK, QUEEN);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  -1 * features_o[FEATURE_ID_PST_QUEEN].getWeight(square_index);
			eval_e +=  -1 * features_e[FEATURE_ID_PST_QUEEN].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		//Rooks
		piece = evalinfo.getPieces(WHITE, ROOK);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  1 * features_o[FEATURE_ID_PST_ROOK].getWeight(square_index);
			eval_e +=  1 * features_e[FEATURE_ID_PST_ROOK].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		piece = evalinfo.getPieces(BLACK, ROOK);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  -1 * features_o[FEATURE_ID_PST_ROOK].getWeight(square_index);
			eval_e +=  -1 * features_e[FEATURE_ID_PST_ROOK].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		//Bishops
		piece = evalinfo.getPieces(WHITE, BISHOP);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  1 * features_o[FEATURE_ID_PST_BISHOP].getWeight(square_index);
			eval_e +=  1 * features_e[FEATURE_ID_PST_BISHOP].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		piece = evalinfo.getPieces(BLACK, BISHOP);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  -1 * features_o[FEATURE_ID_PST_BISHOP].getWeight(square_index);
			eval_e +=  -1 * features_e[FEATURE_ID_PST_BISHOP].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		//Knights
		piece = evalinfo.getPieces(WHITE, NIGHT);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  1 * features_o[FEATURE_ID_PST_KNIGHT].getWeight(square_index);
			eval_e +=  1 * features_e[FEATURE_ID_PST_KNIGHT].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		piece = evalinfo.getPieces(BLACK, NIGHT);
		
		while (piece != 0) {
			
			final int square_index = Long.numberOfTrailingZeros(piece);
			
			eval_o +=  -1 * features_o[FEATURE_ID_PST_KNIGHT].getWeight(square_index);
			eval_e +=  -1 * features_e[FEATURE_ID_PST_KNIGHT].getWeight(square_index);
			
			piece &= piece - 1;
		}
		
		
		int total_material_factor = Math.min(MAX_MATERIAL_FACTOR, board.material_factor_white + board.material_factor_black);
		
		return (int) (eval_o * total_material_factor + eval_e * (MAX_MATERIAL_FACTOR - total_material_factor)) / MAX_MATERIAL_FACTOR;
	}
	
	
	@Override
	protected int phase2() {
		
		return 0;
	}
	
	
	@Override
	protected int phase3() {
		
		return 0;
	}
	
	
	@Override
	protected int phase4() {
		
		return 0;
	}
	
	
	@Override
	protected int phase5() {
		
		return 0;
	}
}

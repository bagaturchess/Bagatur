package bagaturchess.deeplearning.impl_nnue_v2.jni_eval;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.nnue.NNUEJNIBridge;
import bagaturchess.nnue.NNUEProbeUtils;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
	
	
	private static final int MAX_MATERIAL_FACTOR = 4 * 3 + 4 * 3 + 4 * 5 + 2 * 9;
	
	
	static {
		
		NNUEJNIBridge.loadLib();
		
		NNUEJNIBridge.init();
		
		//IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
		//String fen = bitboard.toEPD();
		//System.out.println("fen=" + fen);
		//int score = NNUEJNIBridge.eval(fen);
		//System.out.println("NNUE score=" + score);
	}
	
	private IBitBoard bitboard;
	
	private final IBoardConfig board_cfg;
	
	private NNUEProbeUtils.Input input;
	
	
	NNUEEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		board_cfg = bitboard.getBoardConfig();
		
		input = new NNUEProbeUtils.Input();
	}
	
	
	@Override
	public boolean useEvalCache_Reads() {
		
		return true;
	}
	
	
	@Override
	protected int phase1() {
		
		/*int count_pawns = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN))
							- Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN));
		int count_knights = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT))
							- Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT));
		int count_bishops = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP))
							- Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP));
		int count_rooks = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_ROOK))
							- Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_ROOK));
		int count_queens = Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_QUEEN))
							- Long.bitCount(bitboard.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_QUEEN));
		
		int score_o = 0;
		int score_e = 0;
		
		score_o += (int) (count_pawns * board_cfg.getMaterial_PAWN_O());
		score_o += (int) (count_knights * board_cfg.getMaterial_KNIGHT_O());
		score_o += (int) (count_bishops * board_cfg.getMaterial_BISHOP_O());
		score_o += (int) (count_rooks * board_cfg.getMaterial_ROOK_O());
		score_o += (int) (count_queens * board_cfg.getMaterial_QUEEN_O());
		
		score_e += (int) (count_pawns * board_cfg.getMaterial_PAWN_E());
		score_e += (int) (count_knights * board_cfg.getMaterial_KNIGHT_E());
		score_e += (int) (count_bishops * board_cfg.getMaterial_BISHOP_E());
		score_e += (int) (count_rooks * board_cfg.getMaterial_ROOK_E());
		score_e += (int) (count_queens * board_cfg.getMaterial_QUEEN_E());
		
		int total_material_factor = Math.min(MAX_MATERIAL_FACTOR, bitboard.getMaterialFactor().getTotalFactor());
		
		return (int) (score_o * total_material_factor + score_e * (MAX_MATERIAL_FACTOR - total_material_factor)) / MAX_MATERIAL_FACTOR;
		*/
		
		return 0;
	}
	
	
	@Override
	protected int phase2() {
		
		NNUEProbeUtils.fillInput(bitboard, input);
		
		int actualWhitePlayerEval_c = NNUEJNIBridge.eval(input.color, input.pieces, input.squares);
		
		if (bitboard.getColourToMove() == BLACK) {
			
			actualWhitePlayerEval_c = -actualWhitePlayerEval_c;
		}
		
		return (int) actualWhitePlayerEval_c;
	}
	
	
	@Override
	protected int phase3() {
		
		int eval = 0;
				
		return eval;
	}
	
	
	@Override
	protected int phase4() {
		
		int eval = 0;
		
		return eval;
	}
	
	
	@Override
	protected int phase5() {
		
		int eval = 0;
		
		return eval;
	}
}

package bagaturchess.deeplearning.impl_nnue_v2.java_eval;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.nnue.NNUE;
import bagaturchess.nnue.NNUEProbeUtils;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class NNUEEvaluator extends BaseEvaluator {
	
	
	private static final int MAX_MATERIAL_FACTOR = 4 * 3 + 4 * 3 + 4 * 5 + 2 * 9;
	
	private IBitBoard bitboard;
	
	private final IBoardConfig board_cfg;
	
	private NNUEProbeUtils.Input input;
	
	private NNUE nnue;
	
	
	NNUEEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		bitboard = _bitboard;
		
		board_cfg = bitboard.getBoardConfig();
		
		input = new NNUEProbeUtils.Input();
		
		nnue = new NNUE(bitboard);
		
		if (NNUE.DO_INCREMENTAL_UPDATES) {
			
			bitboard.addMoveListener(nnue.getIncrementalUpdates());
		}
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
		
		//input = new NNUEProbeUtils.Input();
		
		NNUEProbeUtils.fillInput(bitboard, input);
		
		//int actualWhitePlayerEval_c = NNUEJNIBridge.eval(input.color, input.pieces, input.squares);
		
		int actualWhitePlayerEval_java = nnue.nnue_evaluate_pos(input.color, input.pieces, input.squares, NNUE.DO_INCREMENTAL_UPDATES);
		
		//C and Java evaluations are now with small difference just because of NNUE biases
		/*if (actualWhitePlayerEval_c == actualWhitePlayerEval_java) {
			System.out.println("OK actualWhitePlayerEval_c=" + actualWhitePlayerEval_c);
		} else {
			System.out.println("NOTOK actualWhitePlayerEval_c=" + actualWhitePlayerEval_c + " actualWhitePlayerEval_java=" + actualWhitePlayerEval_java);
		}*/
		
		//actualWhitePlayerEval = (2 * actualWhitePlayerEval) / 3;
		
		//String fen = bitboard.toEPD();
		
		//int actualWhitePlayerEval = NNUEJNIBridge.eval(fen) / 3;
		
		if (bitboard.getColourToMove() == BLACK) {
			
			actualWhitePlayerEval_java = -actualWhitePlayerEval_java;
		}
		
		return (int) actualWhitePlayerEval_java;
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

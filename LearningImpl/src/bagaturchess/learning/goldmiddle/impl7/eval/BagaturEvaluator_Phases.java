package bagaturchess.learning.goldmiddle.impl7.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.learning.goldmiddle.impl7.base.EvalInfo;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	private static final int MAX_MATERIAL_FACTOR = 4 * 3 + 4 * 3 + 4 * 5 + 2 * 9;
	
	
	private final ChessBoard board;
	
	private final IBoardConfig board_cfg;
	
	private final EvalInfo evalInfo;
	
	
	protected BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		board = ((BoardImpl)bitboard).getChessBoard();
		
		board_cfg = bitboard.getBoardConfig();
		
		evalInfo = new EvalInfo();
	}
	
	
	@Override
	public int fullEval(int depth, int alpha, int beta, int rootColour) {
		
		int eval = super.fullEval(depth, alpha, beta, rootColour);
		
		
		return eval;
	}
	
	
	@Override
	protected void phase0_init() {
		
		evalInfo.clearEvals();
		
		evalInfo.fillBoardInfo(board);
	}
	
	
	@Override
	protected int phase1() {
		
		int count_pawns = Long.bitCount(evalInfo.bb_w_pawns) - Long.bitCount(evalInfo.bb_b_pawns);
		int count_knights = Long.bitCount(evalInfo.bb_w_knights) - Long.bitCount(evalInfo.bb_b_knights);
		int count_bishops = Long.bitCount(evalInfo.bb_w_bishops) - Long.bitCount(evalInfo.bb_b_bishops);
		int count_rooks = Long.bitCount(evalInfo.bb_w_rooks) - Long.bitCount(evalInfo.bb_b_rooks);
		int count_queens = Long.bitCount(evalInfo.bb_w_queens) - Long.bitCount(evalInfo.bb_b_queens);
		
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
		
		score_o += board.psqtScore_mg;
		score_e += board.psqtScore_eg;
		
		int total_material_factor = Math.min(MAX_MATERIAL_FACTOR, board.material_factor_white + board.material_factor_black);
		
		return (int) (score_o * total_material_factor + score_e * (MAX_MATERIAL_FACTOR - total_material_factor)) / MAX_MATERIAL_FACTOR;
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

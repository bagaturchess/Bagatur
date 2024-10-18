package bagaturchess.learning.goldmiddle.pesto.eval;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.learning.goldmiddle.api.IEvalComponentsProcessor.EVAL_PHASE_ID_1;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.learning.goldmiddle.api.IEvalComponentsProcessor;
import bagaturchess.learning.goldmiddle.impl7.base.EvalInfo;
import bagaturchess.learning.goldmiddle.impl7.filler.Bagatur_V41_FeaturesConstants;


public class Evaluator implements Bagatur_V41_FeaturesConstants {
	
	
	private static final int MAX_MATERIAL_FACTOR = 4 * EvalConstants.PHASE[NIGHT] + 4 * EvalConstants.PHASE[BISHOP] + 4 * EvalConstants.PHASE[ROOK] + 2 * EvalConstants.PHASE[QUEEN];
	
	
	public static int eval1(IBoardConfig boardConfig, final ChessBoard cb, final EvalInfo evalInfo, final IEvalComponentsProcessor evalComponentsProcessor) {
		
		evalComponentsProcessor.addEvalComponent(EVAL_PHASE_ID_1, 13,
				cb.psqtScore_mg, cb.psqtScore_eg, 1, 1);
		
		calculateMaterialScore(boardConfig, evalInfo, evalComponentsProcessor);
		
		int total_material_factor = Math.min(MAX_MATERIAL_FACTOR, cb.material_factor_white + cb.material_factor_black);
		
		return (int) (evalInfo.eval_o_part1 * total_material_factor + evalInfo.eval_e_part1 * (MAX_MATERIAL_FACTOR - total_material_factor)) / MAX_MATERIAL_FACTOR;
	}
	
	
	private static void calculateMaterialScore(final IBoardConfig boardConfig, final EvalInfo evalInfo, final IEvalComponentsProcessor evalComponentsProcessor) {
		
		int count_pawns = Long.bitCount(evalInfo.bb_w_pawns) - Long.bitCount(evalInfo.bb_b_pawns);
		int count_knights = Long.bitCount(evalInfo.bb_w_knights) - Long.bitCount(evalInfo.bb_b_knights);
		int count_bishops = Long.bitCount(evalInfo.bb_w_bishops) - Long.bitCount(evalInfo.bb_b_bishops);
		int count_rooks = Long.bitCount(evalInfo.bb_w_rooks) - Long.bitCount(evalInfo.bb_b_rooks);
		int count_queens = Long.bitCount(evalInfo.bb_w_queens) - Long.bitCount(evalInfo.bb_b_queens);
		
		evalComponentsProcessor.addEvalComponent(EVAL_PHASE_ID_1, FEATURE_ID_MATERIAL_PAWN,
				(int) (count_pawns * boardConfig.getMaterial_PAWN_O()), (int) (count_pawns * boardConfig.getMaterial_PAWN_E()), 1, 1);

		evalComponentsProcessor.addEvalComponent(EVAL_PHASE_ID_1, FEATURE_ID_MATERIAL_KNIGHT,
				(int) (count_knights * boardConfig.getMaterial_KNIGHT_O()), (int) (count_knights * boardConfig.getMaterial_KNIGHT_E()), 1, 1);

		evalComponentsProcessor.addEvalComponent(EVAL_PHASE_ID_1, FEATURE_ID_MATERIAL_BISHOP,
				(int) (count_bishops * boardConfig.getMaterial_BISHOP_O()), (int) (count_bishops * boardConfig.getMaterial_BISHOP_E()), 1, 1);

		evalComponentsProcessor.addEvalComponent(EVAL_PHASE_ID_1, FEATURE_ID_MATERIAL_ROOK,
				(int) (count_rooks * boardConfig.getMaterial_ROOK_O()), (int) (count_rooks * boardConfig.getMaterial_ROOK_E()), 1, 1);

		evalComponentsProcessor.addEvalComponent(EVAL_PHASE_ID_1, FEATURE_ID_MATERIAL_QUEEN,
				(int) (count_queens * boardConfig.getMaterial_QUEEN_O()), (int) (count_queens * boardConfig.getMaterial_QUEEN_E()), 1, 1);
	}
}

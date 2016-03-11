package bagaturchess.engines.material;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.search.api.internal.EvaluatorAdapter;


public class MaterialEvaluator1 extends EvaluatorAdapter {
	
	
	private IBitBoard bitboard;
	protected IBaseEval baseEval;
	protected IMaterialFactor interpolator;
	
	protected PiecesList w_pawns;
	protected PiecesList b_pawns;
	
	
	public MaterialEvaluator1(IBitBoard _bitboard) {
		bitboard = _bitboard;
		baseEval = bitboard.getBaseEvaluation();
		interpolator = _bitboard.getMaterialFactor();
		
		w_pawns = bitboard.getPiecesLists().getPieces(Constants.PID_W_PAWN);
		b_pawns = bitboard.getPiecesLists().getPieces(Constants.PID_B_PAWN);
	}
	
	
	public int getMaterialQueen() {
		return 50 + baseEval.getMaterialQueen();
	}
	
	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		return returnVal(eval_material_nopawnsdrawrule());
	}
	
	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		return (int) returnVal(eval_material_nopawnsdrawrule());
	}

	
	public int roughEval(int depth, int rootColour) {
		return (int) returnVal(eval_material_nopawnsdrawrule());
	}
	
	
	public int eval_material_nopawnsdrawrule() {
		
		int w_eval_nopawns_o = baseEval.getWhiteMaterialNonPawns_o();
		int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
		int b_eval_nopawns_o = baseEval.getBlackMaterialNonPawns_o();
		int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
		
		int w_eval_pawns_o = baseEval.getWhiteMaterialPawns_o();
		int w_eval_pawns_e = baseEval.getWhiteMaterialPawns_e();
		int b_eval_pawns_o = baseEval.getBlackMaterialPawns_o();
		int b_eval_pawns_e = baseEval.getBlackMaterialPawns_e();
		
		
		if (w_pawns.getDataSize() == 0) {
			if (w_eval_pawns_o != 0 || w_eval_pawns_e != 0) {
				throw new IllegalStateException();
			}
			
			if (w_eval_nopawns_o > b_eval_nopawns_o) {
				if (w_eval_nopawns_o < b_eval_nopawns_o + baseEval.getMaterial_BARIER_NOPAWNS_O()) {
					w_eval_nopawns_o = b_eval_nopawns_o;
				}
			}
			
			if (w_eval_nopawns_e > b_eval_nopawns_e) {
				if (w_eval_nopawns_e < b_eval_nopawns_e + baseEval.getMaterial_BARIER_NOPAWNS_E()) {
					w_eval_nopawns_e = b_eval_nopawns_e;
				}
			}
		}
		
		if (b_pawns.getDataSize() == 0) {
			if (b_eval_pawns_o != 0 || b_eval_pawns_e != 0) {
				throw new IllegalStateException();
			}
			
			if (b_eval_nopawns_o > w_eval_nopawns_o) {
				if (b_eval_nopawns_o < w_eval_nopawns_o + baseEval.getMaterial_BARIER_NOPAWNS_O()) {
					b_eval_nopawns_o = w_eval_nopawns_o;
				}
			}
			
			if (b_eval_nopawns_e > w_eval_nopawns_e) {
				if (b_eval_nopawns_e < w_eval_nopawns_e + baseEval.getMaterial_BARIER_NOPAWNS_E()) {
					b_eval_nopawns_e = w_eval_nopawns_e;
				}
			}
		}
		
		return interpolator.interpolateByFactor(
					(w_eval_nopawns_o - b_eval_nopawns_o) + (w_eval_pawns_o - b_eval_pawns_o),
					(w_eval_nopawns_e - b_eval_nopawns_e) + (w_eval_pawns_e - b_eval_pawns_e)
				);

	}
	
	
	private double returnVal(double eval) {
		
		double result = eval;
		
		result = drawProbability(result);
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			result = -result;
		}
		return result;
	}
	
	
	private double drawProbability(double eval) {
		
		double abs = Math.abs(eval);
		
		/**
		 * Differently coloured bishops
		 */
		/*if (w_bishops.getDataSize() == 1 && b_bishops.getDataSize() == 1
				&& bitboard.getMaterialFactor().getTotalFactor() == 6 //Only the bishops
			) {
			
			long w_colour = (bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER) & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			long b_colour = (bitboard.getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER) & Fields.ALL_WHITE_FIELDS) != 0 ?
					Fields.ALL_WHITE_FIELDS : Fields.ALL_BLACK_FIELDS;
			
			if (w_colour != b_colour) {
				if (eval >= 0) {
					abs -= Math.min(90, abs / 2);	
				} else {
					abs += Math.min(90, abs / 2);
				}
			}
		}*/
		
		/**
		 * 50 moves rule
		 */
		int movesBeforeDraw = 100 - bitboard.getDraw50movesRule();
		double percents = movesBeforeDraw / (double)100;
		abs = ((abs + percents * abs) / (double)2);
		
		/**
		 * Return value
		 */
		return eval >= 0 ? abs : -abs;
	}

}

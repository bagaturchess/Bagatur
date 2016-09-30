package bagaturchess.engines.bagatur.eval_v15;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Figures;


public class EvalInfo_V15 extends Figures {
	
	
	private IBitBoard bitboard;
	
	
	/**
	 * Bitboards
	 */
	long bb_all;
	long bb_all_w_pieces;
	long bb_all_b_pieces;
	long bb_w_pawns;
	long bb_b_pawns;
	long bb_w_bishops;
	long bb_b_bishops;
	long bb_w_knights;
	long bb_b_knights;
	long bb_w_queens;
	long bb_b_queens;
	long bb_w_rooks;
	long bb_b_rooks;
	long bb_w_king;
	long bb_b_king;

	long bb_wpawns_attacks;
	long bb_bpawns_attacks;
	
	long open_files;
	long half_open_files_w;
	long half_open_files_b;
	
	
	/**
	 * Counters
	 */
	int w_kingOpened;
	int b_kingOpened;
	
	
	/**
	 * Evaluation components
	 */
	public int eval_Material_o;
	public int eval_Material_e;

	public int eval_Standard_o;
	public int eval_Standard_e;
	
	public int eval_PST_o;
	public int eval_PST_e;

	public int eval_PawnsStandard_o;
	public int eval_PawnsStandard_e;

	public int eval_PawnsPassed_o;
	public int eval_PawnsPassed_e;

	public int eval_PawnsPassedKing_o;
	public int eval_PawnsPassedKing_e;

	public int eval_PawnsUnstoppable_o;
	public int eval_PawnsUnstoppable_e;
	
	public int eval_PawnsPassedStoppers_o;
	public int eval_PawnsPassedStoppers_e;
	
	public int eval_PawnsRooksQueens_o;
	public int eval_PawnsRooksQueens_e;
	
	
	public EvalInfo_V15(IBitBoard _bitboard) {
		bitboard = _bitboard;
	}
	
	
	@Override
	public String toString() {
		String msg = "";
		
		List<DumpRow> rows = new ArrayList<DumpRow>();
		
		rows.add(new DumpRow("Material     ", eval_Material_o, eval_Material_e));
		rows.add(new DumpRow("Standard     ", eval_Standard_o, eval_Standard_e));
		rows.add(new DumpRow("Positinal    ", eval_PST_o, eval_PST_e));
		rows.add(new DumpRow("Pawns        ", eval_PawnsStandard_o, eval_PawnsStandard_e));
		rows.add(new DumpRow("Passed       ", eval_PawnsPassed_o, eval_PawnsPassed_e));
		rows.add(new DumpRow("PassedKing   ", eval_PawnsPassedKing_o, eval_PawnsPassedKing_e));
		rows.add(new DumpRow("PassedUnstop ", eval_PawnsUnstoppable_o, eval_PawnsUnstoppable_e));
		rows.add(new DumpRow("PawnsRQ      ", eval_PawnsRooksQueens_o, eval_PawnsRooksQueens_e));
		rows.add(new DumpRow("PawnsStop    ", eval_PawnsPassedStoppers_o, eval_PawnsPassedStoppers_e));
		
		for (int i=0; i<rows.size(); i++) {
			msg += rows.get(i) + "\r\n";
		}
		
		return msg;
	}
	
	private final class DumpRow {
		String name;
		int eval_o;
		int eval_e;
		int eval_interpolated;
		
		DumpRow(String _name,
				int _eval_o,
				int _eval_e) {
			name = _name;
			eval_o = _eval_o;
			eval_e = _eval_e;
			eval_interpolated = bitboard.getMaterialFactor().interpolateByFactor(eval_o, eval_e);
		}
		
		@Override
		public String toString() {
			String res = "";
			res += name + ":	" + eval_o + "	" + eval_e + "	" + eval_interpolated;
			return res;
		}
	}

	
	public void clear_short() {
		
		eval_Material_o = 0;
		eval_Material_e = 0;

		eval_Standard_o = 0;
		eval_Standard_e = 0;
		
		eval_PST_o = 0;
		eval_PST_e = 0;
		
		eval_PawnsPassed_o = 0;
		eval_PawnsPassed_e = 0;
		
		eval_PawnsPassedKing_o = 0;
		eval_PawnsPassedKing_e = 0;
		
		eval_PawnsStandard_o = 0;
		eval_PawnsStandard_e = 0;
		
		eval_PawnsUnstoppable_o = 0;
		eval_PawnsUnstoppable_e = 0;
	}
	
	
	public void clear() {

		clear_short();
		
		eval_PawnsPassedStoppers_o = 0;
		eval_PawnsPassedStoppers_e = 0;
		
		eval_PawnsRooksQueens_o = 0;
		eval_PawnsRooksQueens_e = 0;
		
		bb_all = 0;
		bb_all_w_pieces = 0;
		bb_all_b_pieces = 0;
		bb_w_pawns = 0;
		bb_b_pawns = 0;
		bb_w_bishops = 0;
		bb_b_bishops = 0;
		bb_w_knights = 0;
		bb_b_knights = 0;
		bb_w_queens = 0;
		bb_b_queens = 0;
		bb_w_rooks = 0;
		bb_b_rooks = 0;
		bb_w_king = 0;
		bb_b_king = 0;
		
		bb_wpawns_attacks = 0;
		bb_bpawns_attacks = 0;
		
		open_files = 0;
		half_open_files_w = 0;
		half_open_files_b = 0;
		
		w_kingOpened = 0;
		b_kingOpened = 0;
	}
}

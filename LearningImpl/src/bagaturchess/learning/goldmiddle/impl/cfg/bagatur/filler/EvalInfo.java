package bagaturchess.learning.goldmiddle.impl.cfg.bagatur.filler;

import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;

class EvalInfo extends Figures {
	
	//Evals of opening and endgame
	int eval_Material_o;
	int eval_Material_e;

	int eval_Standard_o;
	int eval_Standard_e;

	int eval_PST_o;
	int eval_PST_e;

	int eval_Space_o;
	int eval_Space_e;
	
	int eval_Mobility_o;
	int eval_Mobility_e;
	int eval_Mobility_Safe_o;
	int eval_Mobility_Safe_e;

	int eval_Kingsafety_o;
	int eval_Kingsafety_e;
	
	int eval_PawnsPassed_o;
	int eval_PawnsPassed_e;

	int eval_PawnsPassedKing_o;
	int eval_PawnsPassedKing_e;
	/*int eval_PawnsPassedKing_of;
	int eval_PawnsPassedKing_ef;
	int eval_PawnsPassedKing_off;
	int eval_PawnsPassedKing_eff;
	int eval_PawnsPassedKing_oof;
	int eval_PawnsPassedKing_eof;*/
	
	int eval_PawnsStandard_o;
	int eval_PawnsStandard_e;

	int eval_PawnsUnstoppable_o;
	int eval_PawnsUnstoppable_e;

	int eval_PawnsRooksQueens_o;
	int eval_PawnsRooksQueens_e;

	int eval_PawnsPassedStoppers_o;
	int eval_PawnsPassedStoppers_e;

	int eval_PawnsPassedStoppers_a_o;
	int eval_PawnsPassedStoppers_a_e;

	int eval_Hunged_o;
	int eval_Hunged_e;
	
	int eval_Trapped_o;
	int eval_Trapped_e;

	int eval_NoQueen_o;
	int eval_NoQueen_e;

	/**
	 * 
	 */
	int w_gards;
	int b_gards;
	
	//Bitboards
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
	
	long bb_attackedByWhiteOnly;
	long bb_attackedByBlackOnly;
	long bb_unsafe_for_w_minors;
	long bb_unsafe_for_w_rooks;
	long bb_unsafe_for_w_queens;
	long bb_unsafe_for_b_minors;
	long bb_unsafe_for_b_rooks;
	long bb_unsafe_for_b_queens;
	
	long open_files;
	long half_open_files_w;
	long half_open_files_b;
	
	int w_kingOpened;
	int b_kingOpened;
	
	//King safety related
	long[] attackedBy = new long[Constants.PID_MAX];
	long attackedByWhite;
	long attackedByBlack;
	long[] attackZone = new long[Figures.COLOUR_MAX];
	int[] attackCount  = new int[Figures.COLOUR_MAX];
	int[] attackWeight  = new int[Figures.COLOUR_MAX];
	int[] attacked = new int[Figures.COLOUR_MAX];
	
	long[] attacksByFieldID = new long[Fields.ID_MAX];
	
	
	private IBitBoard bitboard;
	
	
	public EvalInfo(IBitBoard _bitboard) {
		bitboard = _bitboard;
	}
	
	public void clear_short() {
		eval_Material_o = 0;
		eval_Material_e = 0;

		eval_Standard_o = 0;
		eval_Standard_e = 0;
		
		eval_PST_o = 0;
		eval_PST_e = 0;
		
		eval_NoQueen_o = 0;
		eval_NoQueen_e = 0;
		
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
				
		eval_Mobility_o = 0;
		eval_Mobility_e = 0;
		
		eval_Mobility_Safe_o = 0;
		eval_Mobility_Safe_e = 0;
		
		eval_Kingsafety_o = 0;
		eval_Kingsafety_e = 0; 
		
		eval_PawnsRooksQueens_o = 0;
		eval_PawnsRooksQueens_e = 0;

		eval_PawnsPassedStoppers_o = 0;
		eval_PawnsPassedStoppers_e = 0;

		eval_PawnsPassedStoppers_a_o = 0;
		eval_PawnsPassedStoppers_a_e = 0;

		eval_Space_o = 0;
		eval_Space_e = 0;
		
		eval_Hunged_o = 0;
		eval_Hunged_e = 0;
		
		eval_Trapped_o = 0;
		eval_Trapped_e = 0;
		
		w_gards = 0;
		b_gards = 0;
		
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
		
		bb_attackedByWhiteOnly = 0;
		bb_attackedByBlackOnly = 0;
		bb_unsafe_for_w_minors = 0;
		bb_unsafe_for_w_rooks = 0;
		bb_unsafe_for_w_queens = 0;
		bb_unsafe_for_b_minors = 0;
		bb_unsafe_for_b_rooks = 0;
		bb_unsafe_for_b_queens = 0;
		
		open_files = 0;
		half_open_files_w = 0;
		half_open_files_b = 0;
		
		w_kingOpened = 0;
		b_kingOpened = 0;
		
		attackedBy[Constants.PID_W_PAWN] = 0;
		attackedBy[Constants.PID_W_KNIGHT] = 0;
		attackedBy[Constants.PID_W_BISHOP] = 0;
		attackedBy[Constants.PID_W_ROOK] = 0;
		attackedBy[Constants.PID_W_QUEEN] = 0;
		attackedBy[Constants.PID_W_KING] = 0;

		attackedBy[Constants.PID_B_PAWN] = 0;
		attackedBy[Constants.PID_B_KNIGHT] = 0;
		attackedBy[Constants.PID_B_BISHOP] = 0;
		attackedBy[Constants.PID_B_ROOK] = 0;
		attackedBy[Constants.PID_B_QUEEN] = 0;
		attackedBy[Constants.PID_B_KING] = 0;

		attackedByWhite = 0;
		attackedByBlack = 0;
		
		attackZone[Figures.COLOUR_WHITE] = 0;
		attackZone[Figures.COLOUR_BLACK] = 0;
		
		attackCount[Figures.COLOUR_WHITE] = 0;
		attackCount[Figures.COLOUR_BLACK] = 0;
		
		attackWeight[Figures.COLOUR_WHITE] = 0;
		attackWeight[Figures.COLOUR_BLACK] = 0;

		attacked[Figures.COLOUR_WHITE] = 0;
		attacked[Figures.COLOUR_BLACK] = 0;
	}
	
	@Override
	public String toString() {
		String msg = "";
		
		List<DumpRow> rows = new ArrayList<DumpRow>();
		
		rows.add(new DumpRow("Material     ", eval_Material_o, eval_Material_e));
		rows.add(new DumpRow("Standard     ", eval_Standard_o, eval_Standard_e));
		rows.add(new DumpRow("Positinal    ", eval_PST_o, eval_PST_e));
		rows.add(new DumpRow("Mobility     ", eval_Mobility_o, eval_Mobility_e));
		rows.add(new DumpRow("MobilitySafe ", eval_Mobility_Safe_o, eval_Mobility_Safe_e));
		rows.add(new DumpRow("Space        ", eval_Space_o, eval_Space_e));
		rows.add(new DumpRow("Hunged       ", eval_Hunged_o, eval_Hunged_e));
		rows.add(new DumpRow("Trapped      ", eval_Trapped_o, eval_Trapped_e));
		rows.add(new DumpRow("Kingsafety   ", eval_Kingsafety_o, eval_Kingsafety_e));
		rows.add(new DumpRow("Pawns        ", eval_PawnsStandard_o, eval_PawnsStandard_e));
		rows.add(new DumpRow("Passed       ", eval_PawnsPassed_o, eval_PawnsPassed_e));
		rows.add(new DumpRow("PassedKing   ", eval_PawnsPassedKing_o, eval_PawnsPassedKing_e));
		rows.add(new DumpRow("PassedUnstop ", eval_PawnsUnstoppable_o, eval_PawnsUnstoppable_e));
		rows.add(new DumpRow("PawnsRQ      ", eval_PawnsRooksQueens_o, eval_PawnsRooksQueens_e));
		rows.add(new DumpRow("PawnsStop    ", eval_PawnsPassedStoppers_o, eval_PawnsPassedStoppers_e));
		rows.add(new DumpRow("PawnsStopA   ", eval_PawnsPassedStoppers_a_o, eval_PawnsPassedStoppers_a_e));
		rows.add(new DumpRow("NoQueen      ", eval_NoQueen_o, eval_NoQueen_e));
		rows.add(new DumpRow("Gards[wb]    ", w_gards, b_gards));
		rows.add(new DumpRow("KingOpen[wb] ", w_kingOpened, b_kingOpened));
		
			
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
}

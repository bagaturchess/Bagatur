package bagaturchess.engines.bagatur.eval_v15;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Figures;


public class EvalInfo_V15 extends Figures {
	
	
	private IBitBoard bitboard;
	
	
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
		
		//TODO
	}
}

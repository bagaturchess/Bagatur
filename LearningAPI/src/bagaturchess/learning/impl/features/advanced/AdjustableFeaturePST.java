package bagaturchess.learning.impl.features.advanced;


import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.impl.signals.SignalArray;


public class AdjustableFeaturePST extends AdjustableFeatureArray {
	
	
	private static final long serialVersionUID = -1025833618147161451L;
	
	
	public AdjustableFeaturePST(int _id, String _name, int _complexity,
			double[] _omin, double[] _omax, double[] oinitial,
			double[] _emin, double[] _emax, double[] einitial) {
		super(_id, _name, _complexity, _omin, _omax, oinitial, _emin, _emax, einitial);
	}
	
	
	public ISignal createNewSignal() {
		return new SignalArray(64);
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += "FEATURE " + StringUtils.fill(""+getId(),3) + " "
			+ StringUtils.fill(getName(), 20);// + openning + "    " + endgame + "    "
			//+ "    " + getSignalstat();
			
		result += "\r\n";
		
		String matrix = "";
		int linecounter = 0;
		String o_line = "";
		String e_line = "";
		for (int fieldID=0; fieldID<Fields.ID_MAX; fieldID++) {
			
			//int pieceID = board[fieldID];
			
			//if ((square & 0x88) == 0) {
				
				String o_cur = StringUtils.fill("" + (int)weights[fieldID].getWeight() + ", ", 2);
				o_cur += "  ";
				o_line += o_cur;
								
				linecounter++;
				if (linecounter == 8) {
					linecounter = 0;
					matrix = o_line + "		" + e_line + "\r\n" + matrix;
					o_line = "";
					e_line = "";
				}
			//}
		}
		
		result += matrix + "\r\n";
		
		return result;
	}
	
	
	public String toStringArray() {
		
		String result = "";
		
		result += "FEATURE " + StringUtils.fill(""+getId(),3) + " "
			+ StringUtils.fill(getName(), 20);// + openning + "    " + endgame + "    "
			//+ "    " + getSignalstat();
		
		result += "\r\n";
		
		String o_matrix = "";
		int linecounter = 0;
		String o_line = "	";
		//String e_line = "";
		for (int fieldID=0; fieldID<Fields.ID_MAX; fieldID++) {
			
				String o_cur = StringUtils.fill("" + (int)weights[fieldID].getWeight(), 2);
				o_cur += ",	";
				o_line += o_cur;
				
				linecounter++;
				if (linecounter == 8) {
					linecounter = 0;
					o_matrix = o_line + "\r\n" + o_matrix;
					o_line = "	";
				}
		}
		
		result += "{\r\n" + o_matrix + "\r\n}\r\n";
		
		return result;
	}
}

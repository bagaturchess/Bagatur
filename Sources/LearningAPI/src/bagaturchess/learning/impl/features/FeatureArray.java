package bagaturchess.learning.impl.features;


import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.impl.signals.SignalArray;


public class FeatureArray extends Feature {
	
	
	private static final long serialVersionUID = 2173196668581176792L;
	
	
	protected double[] o_weights;
	protected double[] e_weights;
	
	
	public FeatureArray(int _id, String _name, int _complexity, double[] _ovals, double[] _evals) {
		super(_id, _name, _complexity);
		createNewWeights(_ovals, _evals);
	}
	
	
	public FeatureArray(int _id, String _name, int _complexity) {
		super(_id, _name, _complexity);
	}


	@Override
	public String toJavaCode() {
		String o = "public static final double " + getName().replace('.', '_') + "_O	=	";
		String e = "public static final double " + getName().replace('.', '_') + "_E	=	";
		
		return o + "\r\n" + e + "\r\n";
	}
	
	
	@Override
	public ISignal createNewSignal() {
		return new SignalArray(2 * o_weights.length);
	}
	
	
	@Override
	public double eval(ISignal signal, double openningPart) {
		SignalArray signalpst = (SignalArray)signal;
		
		int count = signalpst.getSubsignalsCount();
		int[] ids = signalpst.getSubIDs();
		double[] strengths = signalpst.getSubsignals();
		
		double result = 0;
		for(int i=0; i<count; i++) {
			result += strengths[i] * getWeight(ids[i], openningPart);
		}
		
		return result;
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += "FEATURE " + StringUtils.fill(""+getId(),3) + " "
			+ StringUtils.fill(getName(), 20);// + openning + "    " + endgame + "    "
			//+ "    " + getSignalstat();
		
		result += "\r\n";
		
		String matrix = "";
		//int linecounter = 0;
		String o_line = "";
		String e_line = "";
		for (int fieldID=0; fieldID<o_weights.length; fieldID++) {
			String o_cur = StringUtils.fill("" + (int)o_weights[fieldID] + ", ", 2);
			o_cur += "  ";
			o_line += o_cur;
			
			String e_cur = StringUtils.fill("" + (int)e_weights[fieldID] + ", ", 2);
			e_cur += "  ";
			e_line += e_cur;
		}
	
		matrix = o_line + "		" + e_line + "\r\n" + matrix;
		result += matrix + "\r\n";
		
		return result;
	}
	
	
	private double getWeight(int fieldID, double openningPart) {
		return openningPart * o_weights[fieldID] + (1 - openningPart) * e_weights[fieldID];
	}
	
	
	private void createNewWeights(double[] oinitial, double[] einitial) {
		o_weights = new double[oinitial.length];
		for (int i=0; i<o_weights.length; i++) {
			o_weights[i] = oinitial[i];
		}
		
		e_weights = new double[einitial.length];
		for (int i=0; i<e_weights.length; i++) {
			e_weights[i] = einitial[i];
		}
	}
}

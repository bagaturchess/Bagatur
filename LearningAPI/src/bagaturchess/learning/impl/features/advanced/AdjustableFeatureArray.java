package bagaturchess.learning.impl.features.advanced;


import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.impl.signals.SignalArray;
import bagaturchess.learning.impl.features.baseimpl.Weight;


public class AdjustableFeatureArray extends AdjustableFeature {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2173196658581176792L;
	
	
	protected Weight[] weights;
	//protected Weight[] e_weights;
	
	
	public AdjustableFeatureArray(int _id, String _name, int _complexity,
			double[] _omin, double[] _omax, double[] oinitial,
			double[] _emin, double[] _emax, double[] einitial,
			boolean _norm_adjustment) {
		this(_id, _name, _complexity, _omin, _omax, oinitial, _emin, _emax, einitial);
	}
	
	
	public AdjustableFeatureArray(int _id, String _name, int _complexity,
			double[] _ovals, double[] evals) {
		this(_id, _name, _complexity, _ovals, _ovals, _ovals, evals, evals, evals);
	}
	
	
	public AdjustableFeatureArray(int _id, String _name, int _complexity,
			double[] _omin, double[] _omax, double[] oinitial,
			double[] _emin, double[] _emax, double[] einitial) {
		super(_id, _name, _complexity);
		createNewWeights(_omin, _omax, oinitial, _emin, _emax, einitial);
	}
	
	
	@Override
	public double getLearningSpeed() {

		throw new UnsupportedOperationException();
	}
	
	
	public void clear() {
		for (int i=0; i<weights.length; i++) {
			weights[i].clear();
		}
	}
	
	
	public void createNewWeights(double[] _omin, double[] _omax, double[] oinitial,
			double[] _emin, double[] _emax, double[] einitial) {
		weights = new Weight[_omin.length];
		for (int i=0; i<weights.length; i++) {
			weights[i] = new Weight((int)_omin[i], (int)_omax[i], oinitial[i]);
		}
	}
	
	
	public void adjust(ISignal signal, double amount, double dummy) {
		
		SignalArray signalpst = (SignalArray)signal;
		
		int count = signalpst.getSubsignalsCount();
		int[] ids = signalpst.getSubIDs();
		double[] strengths = signalpst.getSubsignals();
		
		for(int i=0; i<count; i++) {
			
			int id = ids[i];
			
			if (strengths[i] != 0) {
				
				if (strengths[i] < 0) {
					adjust(id, -amount, dummy);
				} else {
					adjust(id, amount, dummy);
				}
			}
		}
	}
	
	protected void adjust(int fieldID, double amount, double dummy) {
		
			weights[fieldID].adjust(amount);
	}
	

	@Override
	public void applyChanges() {
		for (int i=0; i<weights.length; i++) {
			weights[i].multiplyCurrentWeightByAmountAndDirection();
		}
	}
	
	
	public double eval(ISignal signal, double openningPart) {
		SignalArray signalpst = (SignalArray)signal;
		
		int count = signalpst.getSubsignalsCount();
		int[] ids = signalpst.getSubIDs();
		double[] strengths = signalpst.getSubsignals();
		
		double result = 0;
		for(int i=0; i<count; i++) {
			result += strengths[i] * getWeight(ids[i]);
		}
		
		return result;
	}
	
	public ISignal createNewSignal() {
		return new SignalArray(2 * weights.length);
	}
	
	
	@Override
	public double getWeight() {

		throw new UnsupportedOperationException(); 
	}
	
	
	@Override
	public double getWeight(int index) {
		
		return weights[index].getWeight();
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
		for (int fieldID=0; fieldID<weights.length; fieldID++) {
			String o_cur = StringUtils.fill("" + weights[fieldID].getWeight() + ", ", 2);
			o_cur += "  ";
			o_line += o_cur;
		}
	
		matrix = o_line + "		" + e_line + "\r\n" + matrix;
		result += matrix + "\r\n";
		
		return result;
	}


	@Override
	public String toJavaCode(String suffix) {
		String o = "public static final double " + getName().replace('.', '_') + "_O	=	" ;
		String e = "public static final double " + getName().replace('.', '_') + "_E	=	" ;
		
		return o + "\r\n" + e + "\r\n";
	}
}

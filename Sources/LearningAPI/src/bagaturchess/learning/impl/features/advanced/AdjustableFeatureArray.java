package bagaturchess.learning.impl.features.advanced;


import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.impl.signals.SignalArray;


public class AdjustableFeatureArray extends AdjustableFeature {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2173196658581176792L;
	
	
	protected Weight[] o_weights;
	protected Weight[] e_weights;
	
	
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
	
	
	/*@Override
	protected void merge(AdjustableFeature other) {
		super.merge(other);
		if (other instanceof AdjustableFeatureArray) {
			AdjustableFeatureArray other_arr = (AdjustableFeatureArray) other;
			for (int i=0; i<o_weights.length; i++) {
				o_weights[i].merge(other_arr.o_weights[i]);
			}
			
			for (int i=0; i<e_weights.length; i++) {
				e_weights[i].merge(other_arr.e_weights[i]);
			}
		}
	}*/
	
	
	public void clear() {
		for (int i=0; i<o_weights.length; i++) {
			o_weights[i].clear();
		}
		
		for (int i=0; i<e_weights.length; i++) {
			e_weights[i].clear();
		}
	}
	
	
	public void createNewWeights(double[] _omin, double[] _omax, double[] oinitial,
			double[] _emin, double[] _emax, double[] einitial) {
		o_weights = new Weight[_omin.length];
		for (int i=0; i<o_weights.length; i++) {
			o_weights[i] = new Weight((int)_omin[i], (int)_omax[i], oinitial[i]);
		}
		
		e_weights = new Weight[_emin.length];
		for (int i=0; i<e_weights.length; i++) {
			e_weights[i] = new Weight((int)_emin[i], (int)_emax[i], einitial[i]);
		}
	}
	
	
	public void adjust(ISignal signal, double amount, double openningPart) {
		
		SignalArray signalpst = (SignalArray)signal;
		
		int count = signalpst.getSubsignalsCount();
		int[] ids = signalpst.getSubIDs();
		double[] strengths = signalpst.getSubsignals();
		
		for(int i=0; i<count; i++) {
			
			int id = ids[i];
			
			if (strengths[i] != 0) {
				
				if (strengths[i] < 0) {
					adjust(id, -amount, openningPart);
				} else {
					adjust(id, amount, openningPart);
				}
			}
		}
	}
	
	protected void adjust(int fieldID, double amount, double openingPart) {
		
		//if (amount == 0) throw new IllegalStateException("amount=" + amount);
		
		if (openingPart > 1) {
			openingPart = 1;
		}
		
		if (openingPart > 1 || openingPart < 0) {
			throw new IllegalStateException("openingPart=" + openingPart);
		}
		
		if (openingPart >= 0.5 ) {
			o_weights[fieldID].adjust(amount);
		} else {
			e_weights[fieldID].adjust(amount);
		}
	}
	

	@Override
	public void applyChanges() {
		for (int i=0; i<o_weights.length; i++) {
			o_weights[i].multiplyCurrentWeightByAmountAndDirection();
		}
		
		for (int i=0; i<e_weights.length; i++) {
			e_weights[i].multiplyCurrentWeightByAmountAndDirection();
		}
	}
	
	
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
	
	
	private double getWeight(int fieldID, double openningPart) {
		return openningPart * o_weights[fieldID].getWeight()
				+ (1 - openningPart) * e_weights[fieldID].getWeight();
	}
	
	public ISignal createNewSignal() {
		return new SignalArray(2 * o_weights.length);
	}
	
	
	@Override
	public double getWeight() {
		
		throw new UnsupportedOperationException();
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
			String o_cur = StringUtils.fill("" + (int)o_weights[fieldID].getWeight() + ", ", 2);
			o_cur += "  ";
			o_line += o_cur;
			
			String e_cur = StringUtils.fill("" + (int)e_weights[fieldID].getWeight() + ", ", 2);
			e_cur += "  ";
			e_line += e_cur;
		}
	
		matrix = o_line + "		" + e_line + "\r\n" + matrix;
		result += matrix + "\r\n";
		
		return result;
	}


	@Override
	public String toJavaCode() {
		String o = "public static final double " + getName().replace('.', '_') + "_O	=	" ;
		String e = "public static final double " + getName().replace('.', '_') + "_E	=	" ;
		
		return o + "\r\n" + e + "\r\n";
	}
}



package bagaturchess.learning.impl.features.baseimpl;


import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.learning.api.ISignal;


public class FeatureSingle extends Feature {
	
	
	private static final long serialVersionUID = -861041676370138696L;
	
	private Weight openning;
	private Weight endgame;
	
	
	public FeatureSingle(int _id, String _name, int _complexity,
			double oval, double eval) {
		this(_id, _name, _complexity, oval, oval, oval, eval, eval, eval);
	}
	
	
	public FeatureSingle(int _id, String _name, int _complexity,
			double _omin, double _omax, double oinitial,
			double _emin, double _emax, double einitial) {
		super(_id, _name, _complexity);
		createNewWeights(_omin, _omax, oinitial, _emin, _emax, einitial);
	}
	
	
	@Override
	public String toJavaCode() {
		String o = "public static final double " + getName().replace('.', '_') + "_O	=	" + openning + ";";
		String e = "public static final double " + getName().replace('.', '_') + "_E	=	" + endgame + ";";
		
		return o + "\r\n" + e + "\r\n";
	}
	
	@Override
	protected void merge(Feature other) {
		super.merge(other);
		if (other instanceof FeatureSingle) {
			FeatureSingle other_fs = (FeatureSingle) other;
			openning.merge(other_fs.openning);
			endgame.merge(other_fs.endgame);
		}
	}
	
	@Override
	public void clear() {
		openning.clear();
		endgame.clear();
	}
	
	@Override
	public void applyChanges() {
		openning.multiplyCurrentWeightByAmountAndDirection();
		endgame.multiplyCurrentWeightByAmountAndDirection();
	}
	
	
	public ISignal createNewSignal() {
		return new SingleSignal();
	}
	
	
	private void createNewWeights(double omin, double omax, double oinitial,
			double emin, double emax, double einitial) {	
		openning = new Weight(omin, omax, oinitial);
		endgame = new Weight(emin, emax, einitial);
	}
	
	public void adjust(ISignal signal, double amount, double openingPart) {
		
		if (openingPart > 1) {
			openingPart = 1;
		}
		
		if (signal.getStrength() < 0) {
			amount = -amount;
		}
		
		if (amount != 1 && amount != -1) {
			throw new IllegalStateException();
		}
		
		double adjustment = amount;
		//double adjustment = amount * (signal.getStrength() / signal.getRange());
			
		if (signal.getStrength() != 0) {
			adjust(adjustment, openingPart);
		}
	}
	
	private void adjust(double amount, double openningPart) {
		if (openningPart >= 0.5 ) {
			openning.adjust(amount);
		} else {
			endgame.adjust(amount);
		}
	}
	
	public double eval(ISignal signal, double openningPart) {
		return getWeight(openningPart) * signal.getStrength();
	}
	
	private double getWeight(double openningPart) {
		return openningPart * openning.getWeight() + (1 - openningPart) * endgame.getWeight();
	}

	private double getWeight1(double openningPart) {
		
		double w_o = openning.getWeight();
		double w_e = endgame.getWeight();
		
		double delta_y = w_o - w_e;
		
		w_o = w_o + delta_y / 2;
		w_e = w_e - delta_y / 2;
		
		double o = openningPart * w_o;
		double e = (1 - openningPart) * w_e;
		double result = o + e;
		
		return result;
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += "FEATURE " + StringUtils.fill("" + getId(), 3) + " " +
			StringUtils.fill(getName(), 20) + openning + "    " + endgame;
			//+ "    signal_bounds: " + getSignalMin() + " " + getSignalMax();
		
		return result;
	}
}

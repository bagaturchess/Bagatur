

package bagaturchess.learning.impl.features;


import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.impl.signals.SingleSignal;


public class FeatureSingle extends Feature {
	
	
	private static final long serialVersionUID = -861041676370138696L;
	
	private double openning;
	private double endgame;
	
	
	public FeatureSingle(int _id, String _name, int _complexity, double oval, double eval) {
		super(_id, _name, _complexity);
		openning = oval;
		endgame = eval;
	}
	
	
	public FeatureSingle(int _id, String _name, int _complexity) {
		super(_id, _name, _complexity);
	}


	@Override
	public String toJavaCode() {
		String o = "public static final double " + getName().replace('.', '_') + "_O	=	" + openning + ";";
		String e = "public static final double " + getName().replace('.', '_') + "_E	=	" + endgame + ";";
		
		return o + "\r\n" + e + "\r\n";
	}
	
	
	public ISignal createNewSignal() {
		return new SingleSignal();
	}
	
	
	public double eval(ISignal signal, double openningPart) {
		return getWeight(openningPart) * signal.getStrength();
	}
	
	private double getWeight(double openningPart) {
		return openningPart * openning + (1 - openningPart) * endgame;
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "FEATURE " + StringUtils.fill("" + getId(), 3) + " " +
			StringUtils.fill(getName(), 20) + openning + "    " + endgame;
		return result;
	}
}

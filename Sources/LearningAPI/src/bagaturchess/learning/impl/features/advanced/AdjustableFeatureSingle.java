

package bagaturchess.learning.impl.features.advanced;


import bagaturchess.bitboard.impl.utils.StringUtils;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.impl.signals.SingleSignal;


public class AdjustableFeatureSingle extends AdjustableFeature {
	
	
	private static final long serialVersionUID = -861041671370138696L;
	
	
	private Weight weight;
	
	
	public AdjustableFeatureSingle(int _id, String _name, int _complexity,
			double _omin, double _omax, double oinitial,
			double dummy_value1, double dummy_value2, double dummy_value3) {
		
		super(_id, _name, _complexity);
		
		weight = new Weight(_omin, _omax, oinitial);
	}
	
	
	protected void merge(AdjustableFeature other) {
		
		if (other instanceof AdjustableFeatureSingle) {
			
			AdjustableFeatureSingle other_fs = (AdjustableFeatureSingle) other;
			
			weight.merge(other_fs.weight);
			
			if (!other.getName().equals(getName())) {
				
				throw new IllegalStateException("Feature names not equals");
			}
		}
	}
	
	
	@Override
	public void clear() {
		weight.clear();
	}
	
	
	@Override
	public void applyChanges() {
		weight.multiplyCurrentWeightByAmountAndDirection();
	}
	
	
	public ISignal createNewSignal() {
		return new SingleSignal();
	}
	
	
	public void adjust(ISignal signal, double amount, double dummy_value) {
		
		if (signal.getStrength() < 0) {
			
			amount = -amount;
		}
		
		weight.adjust(amount);
	}
	
	
	public double eval(ISignal signal, double dummy_value) {
		
		return weight.getWeight() * signal.getStrength();
	}
	
	
	@Override
	public double getWeight() {
		
		return weight.getWeight();
	}
	
	
	@Override
	public String toString() {
		
		String result = "";
		
		result += "FEATURE " + StringUtils.fill("" + getId(), 3) + " "
					+ StringUtils.fill(getName(), 20)
					+ weight;
		
		return result;
	}


	@Override
	public String toJavaCode() {
		
		return "public static final double " + getName().replace('.', '_') + "	=	" + weight.getWeight() + ";" + "\r\n";
	}
}

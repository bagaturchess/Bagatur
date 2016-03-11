package bagaturchess.uci.impl.commands.options;

public class UCIOptionSpin extends UCIOption<Double> {
	
	
	private double norm;
	
	
	public UCIOptionSpin(String _name, Double _value, String _description, double _norm) {
		super(_name, _value, _description);
		norm = _norm;
	}
	
	
	@Override
	public Double getValue() {
		Double val = super.getValue();
		return val * norm;
	}
}

package bagaturchess.uci.impl.commands.options;


public class UCIOptionSpin_Integer extends UCIOption<Integer> {
	
	
	public UCIOptionSpin_Integer(String _name, Integer _value, String _description) {
		super(_name, _value, _description);
	}
	
	
	@Override
	public Integer getValue() {
		Integer val = super.getValue();
		return val;
	}
}

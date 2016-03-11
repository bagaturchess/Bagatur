package bagaturchess.uci.impl.commands.options;


public class UCIOptionCombo extends UCIOption<String> {
	
	//  "option name Style type combo default Normal var Solid var Normal var Risky\n"
	public UCIOptionCombo(String _name, String _value, String _description) {
		super(_name, _value, _description);
	}
}

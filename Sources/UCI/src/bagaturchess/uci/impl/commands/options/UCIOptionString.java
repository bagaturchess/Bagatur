package bagaturchess.uci.impl.commands.options;


public class UCIOptionString extends UCIOption<String> {
	
	//option name NalimovPath type string default c:\\n"
	public UCIOptionString(String _name, String _value, String _description) {
		super(_name, _value, _description);
	}
}

package bagaturchess.uci.impl.commands.options;

import bagaturchess.uci.api.IChannel;

public class Option {
	
	
	private String name;
	private String description;
	private Object value;
	
	
	public Option(String _name, Object _value, String _description) {
		name = _name;
		value = _value;
		description = _description;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	
	public Object getValue() {
		return value;
	}
	
	
	public void setValue(Object _value) {
		value = _value;
	}
	
	
	public String getDefineCommand() {
		return name + IChannel.WHITE_SPACE + description;
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += name + ">" + value;
		return result;
	}
}

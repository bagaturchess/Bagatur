package bagaturchess.uci.impl.commands.options;

import bagaturchess.uci.api.IChannel;

public class UCIOption<T> {
	
	
	private String name;
	private String description;
	private T value;
	
	
	public UCIOption(String _name, T _value, String _description) {
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
	
	
	public T getValue() {
		return value;
	}
	
	
	public void setValue(T _value) {
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

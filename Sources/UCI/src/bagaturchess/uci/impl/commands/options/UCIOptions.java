package bagaturchess.uci.impl.commands.options;


import java.util.HashMap;
import java.util.Map;


public class UCIOptions {
	
	
	private UCIOption[] options;
	private Map<String, UCIOption> optionsMap;
	
	
	public UCIOptions(UCIOption[] _options) {
		options = _options;
		optionsMap = new HashMap<String, UCIOption>();
		for (int i=0; i<options.length; i++) {
			optionsMap.put(options[i].getName().toLowerCase(), options[i]);
		}
	}
	
	
	public UCIOption[] getAllOptions() {
		return options;
	}
	
	
	public UCIOption getOption(String name) {
		return optionsMap.get(name);
	}
}

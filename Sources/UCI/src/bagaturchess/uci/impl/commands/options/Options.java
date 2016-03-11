package bagaturchess.uci.impl.commands.options;

import java.util.HashMap;
import java.util.Map;

public class Options {
	
	
	//	//setoption name Ponder value false
	private Option[] options = new Option[] {
			new Option("OwnBook", true, "type check default true"),
			new Option("Ponder", true, "type check default true"),
			new Option("UCI_AnalyseMode", false, "type check default false"),
			//new Option("Resign", true, "type check default true"),
			//new Option("ResignRate", 7, "type spin default 7 min 5 max 99"),
	};
	
	private Map<String, Option> optionsMap;
	
	
	public Options() {
		optionsMap = new HashMap<String, Option>();
		for (int i=0; i<options.length; i++) {
			optionsMap.put(options[i].getName().toLowerCase(), options[i]);
		}
	}
	
	
	public Option[] getAllOptions() {
		return options;
	}
	
	
	public Option getOption(String name) {
		return optionsMap.get(name);
	}
}

package bagaturchess.uci.impl.commands.options;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class UCIOptions {
	
	
	public static final String OPTION_NAME_Logging_Policy 				= "Logging Policy";
	public static final String OPTION_NAME_OwnBook 						= "OwnBook";
	public static final String OPTION_NAME_Ponder 						= "Ponder";
	public static final String OPTION_NAME_UCI_AnalyseMode 				= "UCI_AnalyseMode";
	public static final String OPTION_NAME_UCI_Chess960 				= "UCI_Chess960";
	public static final String OPTION_NAME_MemoryUsagePercent 			= "MemoryUsagePercent";
	public static final String OPTION_NAME_TranspositionTable 			= "TranspositionTable";
	public static final String OPTION_NAME_EvalCache 					= "EvalCache";
	public static final String OPTION_NAME_SyzygyPath 					= "SyzygyPath";
	public static final String OPTION_NAME_SyzygyOnline 				= "SyzygyOnline";
	public static final String OPTION_NAME_SyzygyDTZCache 				= "SyzygyDTZCache";
	public static final String OPTION_NAME_MultiPV 						= "MultiPV";
	public static final String OPTION_NAME_Opening_Mode 				= "Opening Mode";
	public static final String OPTION_NAME_SMP_Threads 					= "SMP Threads";
	public static final String OPTION_NAME_CountTranspositionTables 	= "CountTranspositionTables";
	
	
	private static final Set<String> Options_RecreateSearchAdaptor = new HashSet<String>();
	
	
	static {
		Options_RecreateSearchAdaptor.add(UCIOptions.OPTION_NAME_SMP_Threads);
		Options_RecreateSearchAdaptor.add(UCIOptions.OPTION_NAME_SyzygyPath);
		Options_RecreateSearchAdaptor.add(UCIOptions.OPTION_NAME_MemoryUsagePercent);
		Options_RecreateSearchAdaptor.add(UCIOptions.OPTION_NAME_TranspositionTable);
		Options_RecreateSearchAdaptor.add(UCIOptions.OPTION_NAME_EvalCache);
		Options_RecreateSearchAdaptor.add(UCIOptions.OPTION_NAME_SyzygyOnline);
		Options_RecreateSearchAdaptor.add(UCIOptions.OPTION_NAME_SyzygyDTZCache);
		Options_RecreateSearchAdaptor.add(UCIOptions.OPTION_NAME_CountTranspositionTables);
		Options_RecreateSearchAdaptor.add(UCIOptions.OPTION_NAME_UCI_Chess960);
	};
	
	
	public static final boolean needsRestart(String option_name) {
		
		return Options_RecreateSearchAdaptor.contains(option_name);
	}
	
	
	private UCIOption[] options;
	private Map<String, UCIOption> optionsMap;
	
	
	public UCIOptions(UCIOption[] _options) {
		options = _options;
		optionsMap = new HashMap<String, UCIOption>();
		for (int i=0; i<options.length; i++) {
			optionsMap.put(options[i].getName(), options[i]);
		}
	}
	
	
	public UCIOption[] getAllOptions() {
		return options;
	}
	
	
	public UCIOption getOption(String name) {
		return optionsMap.get(name);
	}
}

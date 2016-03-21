package bagaturchess.uci.impl;


import java.util.List;

import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.api.IUCIOptionAction;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptions;
import bagaturchess.uci.impl.commands.options.SetOption;


public class OptionsManager {
	
	
	private UCIOptions options;
	private IUCIOptionsProvider rootProvider;
	private IChannel communicationChanel;
	
	private List<IUCIOptionAction> actions;
	
	
	public OptionsManager(IChannel _communicationChanel, IUCIOptionsProvider _rootProvider, List<IUCIOptionAction> _actions) {
		
		communicationChanel = _communicationChanel;
		rootProvider = _rootProvider;
		actions = _actions;
		
		options = new UCIOptions(rootProvider.getSupportedOptions());
		
		for (int i=0; i<getOptions().getAllOptions().length; i++) {
			UCIOption option = getOptions().getAllOptions()[i];
			setOptionAndLogResult(option);
		}
	}
	
	
	public UCIOptions getOptions() {
		return options;
	}
	
	
	public void set(SetOption setoption) {
		UCIOption option = getOptions().getOption(setoption.getName());
		if (option == null) {
			communicationChanel.dump("UCIOption '" + setoption.getName() + "' not supported.");
		} else {
			
			Object new_value = setoption.getValue();
			
			try {
				
				parseAndSetValue(option, new_value);
				
				setOptionAndLogResult(option);
				
				customActions(option);
				
			} catch (Exception ex) {
				communicationChanel.dump("UCIOption NOT set: " + option + ", because: ");
				communicationChanel.dump(ex);
			}
		}
	}


	private void customActions(UCIOption option) throws Exception {
		for (IUCIOptionAction action: actions) {
			if (option.getName().equals(action.getOptionName())) {
				action.execute();
			}	
		}
	}
	
	
	private void setOptionAndLogResult(UCIOption option) {
		boolean ok = rootProvider.applyOption(option);
		if (ok) {
			communicationChanel.dump("UCIOption set successfully: " + option);
		} else {
			communicationChanel.dump("UCIOption not found: " + option);
		}
	}
	
	
	private void parseAndSetValue(UCIOption option, Object new_value) {
		
		if (new_value == null || "".equals(new_value)) {
			option.setValue(null);
			return;
		}
		if (new_value instanceof String) {
			String str = (String) new_value;
			if ("".equals(str.trim())) {
				option.setValue(null);
				return;
			}
		}
		
		try {
			//try to parse double
			Double val = Double.parseDouble((String) new_value);
			option.setValue(val.doubleValue());
			
		} catch(IllegalArgumentException iae1) {
			
			String new_value_str = (String) new_value;
			//try to parse boolean
			if (new_value_str.toLowerCase().trim().equals("false") || new_value_str.toLowerCase().trim().equals("true")) {
				try {
					boolean flag = Boolean.parseBoolean(new_value_str);
					option.setValue(flag);
				} catch(IllegalArgumentException iae2) {
					throw iae2;
				}	
			} else {
				//throw new IllegalArgumentException("Value is neither number nor boolean: " + new_value_str);
				option.setValue(new_value_str);
			}
		}
	}
}

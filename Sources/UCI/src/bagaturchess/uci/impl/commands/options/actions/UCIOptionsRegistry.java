package bagaturchess.uci.impl.commands.options.actions;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.commands.options.UCIOption;


public class UCIOptionsRegistry implements IUCIOptionsRegistry, IUCIOptionsProvider {
	
	
	private List<UCIOption> options;
	private List<IUCIOptionsProvider> providers;
	
	
	public UCIOptionsRegistry() {
		options = new ArrayList<UCIOption>();
		providers = new ArrayList<IUCIOptionsProvider>();
	}
	
	
	@Override
	public void registerProvider(IUCIOptionsProvider provider) {
		
		providers.add(provider);
		
		UCIOption[] options_arr = provider.getSupportedOptions();
		for (int i=0; i<options_arr.length; i++) {
			options.add(options_arr[i]);
		}
	}
	
	
	@Override
	public UCIOption[] getSupportedOptions() {
		return options.toArray(new UCIOption[0]);
	}
	
		
	@Override
	public boolean applyOption(UCIOption option) {
		for (IUCIOptionsProvider provider: providers) {
			if (provider.applyOption(option)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void registerProviders(IUCIOptionsRegistry registry) {
		//Do Nothing	
	}
}

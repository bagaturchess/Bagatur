package bagaturchess.uci.api;


import bagaturchess.uci.impl.commands.options.UCIOption;


public interface IUCIOptionsProvider {
	
	public UCIOption[] getSupportedOptions();
	
	public boolean applyOption(UCIOption option);
	
	public void registerProviders(IUCIOptionsRegistry registry);
}

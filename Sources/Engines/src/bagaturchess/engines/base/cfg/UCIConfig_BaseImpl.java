package bagaturchess.engines.base.cfg;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.uci.api.IUCIConfig;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionCombo;


public class UCIConfig_BaseImpl implements IUCIConfig {

	
	private String searchAdaptorImpl_ClassName;
	private Object searchAdaptorImpl_ConfigObj;
	
	
	private static final String DEFAULT_loggingPolicy = "single file";
	
	private String loggingPolicy = DEFAULT_loggingPolicy;
	
	//Example  "option name Logging policy type combo default single file var single file var multiple files var none"
	private UCIOption[] options = new UCIOption[] {
			new UCIOptionCombo("Logging Policy",
					DEFAULT_loggingPolicy,
					"type combo default " + DEFAULT_loggingPolicy + " var single file var multiple files var none")
	};
	
	
	public UCIConfig_BaseImpl(String[] args) {
		searchAdaptorImpl_ClassName = args[0];
		searchAdaptorImpl_ConfigObj = ReflectionUtils.createObjectByClassName_StringsConstructor(
											args[1], Utils.copyOfRange(args, 2)
										);
	}
	
	
	@Override
	public String getUCIAdaptor_ClassName() {
		return searchAdaptorImpl_ClassName;
	}
	
	
	@Override
	public Object getUCIAdaptor_ConfigObj() {
		return searchAdaptorImpl_ConfigObj;
	}
	
	
	@Override
	public void registerProviders(IUCIOptionsRegistry registry) {
		
		registry.registerProvider(this);
		
		if (searchAdaptorImpl_ConfigObj instanceof IUCIOptionsProvider) {
			((IUCIOptionsProvider) searchAdaptorImpl_ConfigObj).registerProviders(registry);
		}
	}
	
	
	@Override
	public UCIOption[] getSupportedOptions() {
		return options;
	}
	
	
	@Override
	public boolean applyOption(UCIOption option) {
		if ("Logging Policy".equals(option.getName())) {
			loggingPolicy = (String) option.getValue();
			return true;
		}
		return false;
	}
	
	
	@Override
	public String getUCIAdaptor_LoggingPolicy() {
		return loggingPolicy;
	}
}

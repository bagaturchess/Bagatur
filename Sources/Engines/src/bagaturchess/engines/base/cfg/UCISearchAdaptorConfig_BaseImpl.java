package bagaturchess.engines.base.cfg;


import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.engines.bagatur.cfg.time.TimeConfigImpl;
import bagaturchess.uci.api.ISearchAdaptorConfig;
import bagaturchess.uci.api.ITimeConfig;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.commands.options.UCIOption;


public class UCISearchAdaptorConfig_BaseImpl implements ISearchAdaptorConfig {
	
	
	private UCIOption[] options = new UCIOption[] {
			new UCIOption("OwnBook", true, "type check default true"),
			new UCIOption("Ponder", true, "type check default true"),
			new UCIOption("UCI_AnalyseMode", false, "type check default false"),
	};
	
	private String rootSearchImpl_ClassName;
	private Object rootSearchImpl_ConfigObj;
	
	private final ITimeConfig timeCfg = new TimeConfigImpl();
	
	private boolean isOwnBookEnabled;
	private boolean isPonderingEnabled;
	private boolean isAnalyzeMode;
	
	
	public UCISearchAdaptorConfig_BaseImpl(String[] args) {
		rootSearchImpl_ClassName = args[0];
		rootSearchImpl_ConfigObj = ReflectionUtils.createObjectByClassName_StringsConstructor(
											args[1], Utils.copyOfRange(args, 2)
										);
	}

	
	@Override
	public ITimeConfig getTimeConfig() {
		return timeCfg;
	}
	
	
	@Override
	public String getRootSearchClassName() {
		return rootSearchImpl_ClassName;
	}
	
	
	@Override
	public Object getRootSearchConfig() {
		return rootSearchImpl_ConfigObj;
	}
	
	
	@Override
	public boolean isOwnBookEnabled() {
		return isOwnBookEnabled;
	}
	
	
	@Override
	public boolean isPonderingEnabled() {
		return isPonderingEnabled;
	}
	
	
	@Override
	public boolean isAnalyzeMode() {
		return isAnalyzeMode;
	}
	
	
	@Override
	public void registerProviders(IUCIOptionsRegistry registry) {
		
		registry.registerProvider(this);
		
		if (rootSearchImpl_ConfigObj instanceof IUCIOptionsProvider) {
			((IUCIOptionsProvider) rootSearchImpl_ConfigObj).registerProviders(registry);
		}
	}
	
	
	@Override
	public UCIOption[] getSupportedOptions() {
		return options;
	}
	
	
	@Override
	public boolean applyOption(UCIOption option) {
		
		if ("Ponder".equals(option.getName())) {
			isPonderingEnabled = (Boolean) option.getValue();
			return true;
		} else if ("OwnBook".equals(option.getName())) {
			isOwnBookEnabled = (Boolean) option.getValue();
			return true;
		} else if ("UCI_AnalyseMode".equals(option.getName())) {
			isAnalyzeMode = (Boolean) option.getValue();
			return true;
		}
		
		return false;
	}
}

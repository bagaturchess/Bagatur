package bagaturchess.engines.cfg.base;


import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin_Integer;


public class RootSearchConfig_BaseImpl_SMP_Processes extends RootSearchConfig_BaseImpl_SMP {
	
	
	private int currentThreadMemory = 1024;
	
	//setoption name UCIOptions.OPTION_NAME_SMP_Threads value 16
	private UCIOption[] options = new UCIOption[] {
			new UCIOptionSpin_Integer("Thread Memory (MB)", currentThreadMemory,
					"type spin default " + currentThreadMemory
											+ " min 256"
											+ " max 1024")
	};
	
	
	public RootSearchConfig_BaseImpl_SMP_Processes(String[] args) {
		super(args);
	}
	
	
	@Override
	public UCIOption[] getSupportedOptions() {
		UCIOption[] parentOptions = super.getSupportedOptions();
		
		UCIOption[] result = new UCIOption[parentOptions.length + options.length];
		
		System.arraycopy(options, 0, result, 0, options.length);
		System.arraycopy(parentOptions, 0, result, options.length, parentOptions.length);
		
		return result;
	}
	
	
	@Override
	public boolean applyOption(UCIOption option) {
		if ("Thread Memory (MB)".equals(option.getName())) {
			currentThreadMemory = (Integer) option.getValue();
			return true;
		}
		
		return super.applyOption(option);
	}
	
	
	@Override
	public int getThreadMemory_InMegabytes() {
		return currentThreadMemory;
	}
	
	
	@Override
	public boolean initCaches() {
		return false;
	}
}

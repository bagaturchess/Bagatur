package bagaturchess.uci.impl.commands.options.actions;


import java.io.FileNotFoundException;

import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.api.IUCIConfig;
import bagaturchess.uci.api.IUCIOptionAction;
import bagaturchess.uci.impl.commands.options.UCIOptions;


public class UCIOptionAction_RecreateLogging implements IUCIOptionAction {
	
	
	private IUCIConfig engineBootCfg;
	private IChannel channel;
	
	
	public UCIOptionAction_RecreateLogging(IChannel _channel, IUCIConfig _engineBootCfg) {
		channel = _channel;
		engineBootCfg = _engineBootCfg;
	}
	
	@Override
	public void execute() throws FileNotFoundException {
		channel.initLogging(engineBootCfg);
	}
	
	
	@Override
	public String getOptionName() {
		return UCIOptions.OPTION_NAME_Logging_Policy;
	}
}

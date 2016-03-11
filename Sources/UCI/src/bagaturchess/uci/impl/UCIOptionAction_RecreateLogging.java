package bagaturchess.uci.impl;


import java.io.FileNotFoundException;

import bagaturchess.uci.api.IUCIOptionAction;


public class UCIOptionAction_RecreateLogging implements IUCIOptionAction {
	
	
	private StateManager stateManager;
	
	
	public UCIOptionAction_RecreateLogging(StateManager _stateManager) {
		stateManager = _stateManager;
	}
	
	@Override
	public void execute() throws FileNotFoundException {
		stateManager.initLogging();
	}
	
	
	@Override
	public String getOptionName() {
		return "Logging Policy";
	}
}

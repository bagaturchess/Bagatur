package bagaturchess.uci.impl;


import java.io.FileNotFoundException;

import bagaturchess.uci.api.IUCIOptionAction;


public class UCIOptionAction_RecreateSearchAdaptor implements IUCIOptionAction {
	
	
	private StateManager stateManager;
	
	
	public UCIOptionAction_RecreateSearchAdaptor(StateManager _stateManager) {
		stateManager = _stateManager;
	}
	
	@Override
	public void execute() throws FileNotFoundException {
		stateManager.destroySearchAdaptor();
		stateManager.createSearchAdaptor();
	}
	
	
	@Override
	public String getOptionName() {
		return "Search SMP [Threads count]";
	}
}

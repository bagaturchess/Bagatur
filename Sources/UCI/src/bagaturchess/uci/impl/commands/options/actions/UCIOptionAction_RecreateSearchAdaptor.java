package bagaturchess.uci.impl.commands.options.actions;


import java.io.FileNotFoundException;

import bagaturchess.uci.api.IUCIOptionAction;
import bagaturchess.uci.impl.StateManager;


public class UCIOptionAction_RecreateSearchAdaptor implements IUCIOptionAction {
	
	
	private StateManager stateManager;
	private String optionName;
	
	public UCIOptionAction_RecreateSearchAdaptor(StateManager _stateManager, String _optionName) {
		stateManager = _stateManager;
		optionName = _optionName;
	}
	
	@Override
	public void execute() throws FileNotFoundException {
		if (stateManager.destroySearchAdaptor()) {
			stateManager.createSearchAdaptor();
		}
	}
	
	
	@Override
	public String getOptionName() {
		return optionName;
	}
}

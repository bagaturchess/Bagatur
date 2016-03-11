/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.uci.run;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.uci.api.IUCIConfig;
import bagaturchess.uci.api.IUCIOptionAction;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.Channel;
import bagaturchess.uci.impl.OptionsManager;
import bagaturchess.uci.impl.StateManager;
import bagaturchess.uci.impl.UCIOptionAction_RecreateLogging;
import bagaturchess.uci.impl.UCIOptionAction_RecreateSearchAdaptor;
import bagaturchess.uci.impl.UCIOptionsRegistry;


public class Boot {
	
	
	public static void main(String[] args) {
		
		Channel communicationChanel = new Channel(); //Single file logging by default?
		
		try {
			
			if (args == null || args.length < 1) {
				throw new IllegalStateException("There is no program parameter which points to the engine configuration class");
			}
			
			String engineBootCfg_ClassName = args[0];
			args = Utils.copyOfRange(args, 1, args.length);
			
			
			IUCIConfig engineBootCfg = null;
			try {
				engineBootCfg = (IUCIConfig) ReflectionUtils.createObjectByClassName_StringsConstructor(engineBootCfg_ClassName, args);
			} catch(Exception e) {
				engineBootCfg = (IUCIConfig) ReflectionUtils.createObjectByClassName_NoArgsConstructor(engineBootCfg_ClassName);
			}
			
			//Create state manager and apply initial values of options
			IUCIOptionsRegistry optionsRegistry = new UCIOptionsRegistry();
			engineBootCfg.registerProviders(optionsRegistry);
			
			StateManager manager = new StateManager(engineBootCfg);
			manager.setChannel(communicationChanel);
			
			List<IUCIOptionAction> customActions = new ArrayList<IUCIOptionAction>();
			customActions.add(new UCIOptionAction_RecreateSearchAdaptor(manager));
			customActions.add(new UCIOptionAction_RecreateLogging(manager));
			
			OptionsManager optionsManager = new OptionsManager((IUCIOptionsProvider) optionsRegistry, customActions);
			manager.setOptionsManager(optionsManager);
			
			for (IUCIOptionAction action: customActions) {
				action.execute();
			}
			
			manager.communicate();
			
			
		} catch (Throwable t) {
			if (communicationChanel != null) communicationChanel.dump(t);
		}
	}
}

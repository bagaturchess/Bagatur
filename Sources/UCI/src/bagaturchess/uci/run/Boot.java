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


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.api.IUCIConfig;
import bagaturchess.uci.api.IUCIOptionAction;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.uci.impl.StateManager;
import bagaturchess.uci.impl.commands.options.actions.OptionsManager;
import bagaturchess.uci.impl.commands.options.actions.UCIOptionAction_RecreateLogging;
import bagaturchess.uci.impl.commands.options.actions.UCIOptionsRegistry;


public class Boot {
	
	
	public static void main(String[] args) {
		
		IChannel communicationChanel = new Channel_Console(); //Single file logging by default?
		ChannelManager.setChannel(communicationChanel);
		
		runStateManager(args, communicationChanel);
	}

	public static void runStateManager(String[] args, final IChannel communicationChanel) {
		
		try {
			
			//ChannelManager.setChannel(new Channel_Console(System.in, System.out, System.out));
			
			if (args == null || args.length < 1) {
				throw new IllegalStateException("There is no program parameter which points to the engine configuration class");
			}
			
			String engineBootCfg_ClassName = args[0];
			args = Utils.copyOfRange(args, 1, args.length);
			
			
			IUCIConfig engineBootCfg = null;
			try {
				engineBootCfg = (IUCIConfig) ReflectionUtils.createObjectByClassName_StringsConstructor(engineBootCfg_ClassName, args);
			} catch(Exception e) {
				e.printStackTrace(System.out);
				engineBootCfg = (IUCIConfig) ReflectionUtils.createObjectByClassName_NoArgsConstructor(engineBootCfg_ClassName);
			}
			
			final StateManager manager = new StateManager(engineBootCfg);
			manager.setChannel(communicationChanel);
			
			syncInitStateManager(manager, communicationChanel, engineBootCfg);
			
			manager.communicate();
			
		} catch (Throwable t) {
			if (communicationChanel != null) communicationChanel.dump(t);
			t.printStackTrace(System.out);
		}
	}
	
	
	private static void syncInitStateManager(final StateManager manager, final IChannel communicationChanel,
			final IUCIConfig engineBootCfg) throws Exception {
		
		//(new Thread(
		//		new Runnable() {
					
		//			@Override
		//			public void run() {
						try {
							//Create state manager and apply initial values of options
							IUCIOptionsRegistry optionsRegistry = new UCIOptionsRegistry();
							engineBootCfg.registerProviders(optionsRegistry);
							
							List<IUCIOptionAction> customActions = new ArrayList<IUCIOptionAction>();
							customActions.add(new UCIOptionAction_RecreateLogging(ChannelManager.getChannel(), engineBootCfg));
							
							OptionsManager optionsManager = new OptionsManager(communicationChanel, (IUCIOptionsProvider) optionsRegistry, customActions);
							manager.setOptionsManager(optionsManager);
							
							customActions.get(0).execute(); //Init logging
							
							/*for (IUCIOptionAction action: customActions) {
								action.execute();
							}*/
							
						} catch (Throwable t) {
							communicationChanel.sendLogToGUI("Error while initializing StateManager: " + t.getMessage());
							communicationChanel.dump(t);
							if (t instanceof InvocationTargetException) {
								t = ((InvocationTargetException)t).getCause();
								communicationChanel.sendLogToGUI("Error while initializing StateManager: cause " + t);
								if (t != null) {
									communicationChanel.sendLogToGUI("Error while initializing StateManager: cause message " + t.getMessage());
								}
							}
						}
		//			}
		//		}
		//		)
		//).start();
	}
}

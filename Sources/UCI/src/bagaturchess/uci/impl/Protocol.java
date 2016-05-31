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
package bagaturchess.uci.impl;

import java.util.HashMap;
import java.util.Map;

public class Protocol {
	/**
FROM_GUI>uci
TO_GUI>id name Pinko author Krasimir Topchiyski
TO_GUI>uciok
FROM_GUI>isready
TO_GUI>readyok
FROM_GUI>ucinewgame
FROM_GUI>isready
TO_GUI>readyok
FROM_GUI>position startpos moves d2d4
FROM_GUI>go wtime 300000 btime 300000 winc 0 binc 0
TO_GUI>bestmove g1f3 ponder d8f6
	 */
	public static final String COMMAND_TO_ENGINE_UCI_STR = "uci";
	public static final String COMMAND_TO_ENGINE_ISREADY_STR = "isready";
	public static final String COMMAND_TO_ENGINE_NEWGAME_STR = "ucinewgame";
	
	public static final String COMMAND_TO_ENGINE_POSITION_STR = "position";
	public static final String COMMAND_TO_ENGINE_POSITION_START_POS = "startpos";
	public static final String COMMAND_TO_ENGINE_POSITION_FEN = "fen";
	public static final String COMMAND_TO_ENGINE_POSITION_MOVES = "moves";
	
	public static final String COMMAND_TO_ENGINE_GO_STR = "go";
	public static final String COMMAND_TO_ENGINE_GO_PONDER_STR = "ponder";
	public static final String COMMAND_TO_ENGINE_GO_WTIME_STR = "wtime";
	public static final String COMMAND_TO_ENGINE_GO_BTIME_STR = "btime";
	public static final String COMMAND_TO_ENGINE_GO_WINC_STR = "winc";
	public static final String COMMAND_TO_ENGINE_GO_BINC_STR = "binc";
	public static final String COMMAND_TO_ENGINE_GO_NODES_STR = "nodes";
	public static final String COMMAND_TO_ENGINE_GO_MOVESTOGO_STR = "movestogo";
	public static final String COMMAND_TO_ENGINE_GO_MOVETIME_STR = "movetime";
	public static final String COMMAND_TO_ENGINE_GO_INFINITE_STR = "infinite";
	public static final String COMMAND_TO_ENGINE_GO_DEPTH_STR = "depth";
	
	public static final String COMMAND_TO_ENGINE_PONDERHIT_STR = "ponderhit";
	
	public static final String COMMAND_TO_ENGINE_SETOPTION_STR = "setoption";
	public static final String COMMAND_TO_ENGINE_SETOPTION_NAME_STR = "name";
	public static final String COMMAND_TO_ENGINE_SETOPTION_VALUE_STR = "value";
	
	public static final String COMMAND_TO_ENGINE_STOP_STR = "stop";
	public static final String COMMAND_TO_ENGINE_QUIT_STR = "quit";
	
	public static final String COMMAND_TO_GUI_ID_VERSION_STR = "1.4b";
	public static final String COMMAND_TO_GUI_ID_STR = "id";
	public static final String COMMAND_TO_GUI_ID_NAME_STR = "name";
	public static final String COMMAND_TO_GUI_ID_AUTHOR_STR = "author";
	public static final String COMMAND_TO_GUI_OPTION_STR = "option";
	public static final String COMMAND_TO_GUI_UCIOK_STR = "uciok";
	public static final String COMMAND_TO_GUI_READYOK = "readyok";
	public static final String COMMAND_TO_GUI_POSITION_STR = COMMAND_TO_ENGINE_POSITION_STR;
	public static final String COMMAND_TO_GUI_BESTMOVE_STR = "bestmove";
	public static final String COMMAND_TO_GUI_BESTMOVE_PONDER_STR = "ponder";

	public static final int COMMAND_UNDEFINED = -1;
	public static final int COMMAND_TO_ENGINE_UCI = 0;
	public static final int COMMAND_TO_ENGINE_ISREADY = 1;
	public static final int COMMAND_TO_ENGINE_NEWGAME = 2;
	public static final int COMMAND_TO_ENGINE_POSITION = 3;
	public static final int COMMAND_TO_ENGINE_GO = 4;
	public static final int COMMAND_TO_ENGINE_PONDERHIT = 5;
	public static final int COMMAND_TO_ENGINE_SETOPTION = 6;
	public static final int COMMAND_TO_ENGINE_STOP = 7;
	public static final int COMMAND_TO_ENGINE_QUIT = 8;
	
	public static final int COMMAND_TO_GUI_ID = 0;
	public static final int COMMAND_TO_GUI_OPTION = 1;
	public static final int COMMAND_TO_GUI_UCIOK = 2;
	public static final int COMMAND_TO_GUI_POSITION = 3;
	public static final int COMMAND_TO_GUI_BESTMOVE = 4;
	
	public static Map<String, Integer> toGUI_IDByCommand;
	public static Map<Integer, String> toGUI_CommandByID;
	
	static {
		synchronized (Protocol.class) {
			
			toGUI_IDByCommand = new HashMap<String, Integer>();
			toGUI_CommandByID = new HashMap<Integer, String>();
			
			toGUI_IDByCommand.put(COMMAND_TO_GUI_ID_STR, COMMAND_TO_ENGINE_UCI);
			toGUI_IDByCommand.put(COMMAND_TO_GUI_OPTION_STR, COMMAND_TO_GUI_OPTION);
			toGUI_IDByCommand.put(COMMAND_TO_GUI_UCIOK_STR, COMMAND_TO_GUI_UCIOK);
			toGUI_IDByCommand.put(COMMAND_TO_GUI_POSITION_STR, COMMAND_TO_GUI_POSITION);
			toGUI_IDByCommand.put(COMMAND_TO_GUI_BESTMOVE_STR, COMMAND_TO_GUI_BESTMOVE);
			
			toGUI_CommandByID.put(COMMAND_TO_GUI_ID, COMMAND_TO_ENGINE_UCI_STR);
			toGUI_CommandByID.put(COMMAND_TO_GUI_OPTION, COMMAND_TO_GUI_OPTION_STR);
			toGUI_CommandByID.put(COMMAND_TO_GUI_UCIOK, COMMAND_TO_GUI_UCIOK_STR);
			toGUI_CommandByID.put(COMMAND_TO_GUI_POSITION, COMMAND_TO_GUI_POSITION_STR);
			toGUI_CommandByID.put(COMMAND_TO_GUI_BESTMOVE, COMMAND_TO_GUI_BESTMOVE_STR);
		}
	}
	
	public static Map<String, Integer> toEngine_IDByCommand = new HashMap<String, Integer>();
	public static Map<Integer, String> toEngine_CommandByID = new HashMap<Integer, String>();
	
	static {
		synchronized (Protocol.class) {
			toEngine_IDByCommand.put(COMMAND_TO_ENGINE_UCI_STR, COMMAND_TO_ENGINE_UCI);
			toEngine_IDByCommand.put(COMMAND_TO_ENGINE_ISREADY_STR, COMMAND_TO_ENGINE_ISREADY);
			toEngine_IDByCommand.put(COMMAND_TO_ENGINE_NEWGAME_STR, COMMAND_TO_ENGINE_NEWGAME);
			toEngine_IDByCommand.put(COMMAND_TO_ENGINE_POSITION_STR, COMMAND_TO_ENGINE_POSITION);
			toEngine_IDByCommand.put(COMMAND_TO_ENGINE_GO_STR, COMMAND_TO_ENGINE_GO);
			toEngine_IDByCommand.put(COMMAND_TO_ENGINE_PONDERHIT_STR, COMMAND_TO_ENGINE_PONDERHIT);
			toEngine_IDByCommand.put(COMMAND_TO_ENGINE_SETOPTION_STR, COMMAND_TO_ENGINE_SETOPTION);
			toEngine_IDByCommand.put(COMMAND_TO_ENGINE_STOP_STR, COMMAND_TO_ENGINE_STOP);
			toEngine_IDByCommand.put(COMMAND_TO_ENGINE_QUIT_STR, COMMAND_TO_ENGINE_QUIT);
	
			toEngine_CommandByID.put(COMMAND_TO_ENGINE_UCI, COMMAND_TO_ENGINE_UCI_STR);
			toEngine_CommandByID.put(COMMAND_TO_ENGINE_ISREADY, COMMAND_TO_ENGINE_ISREADY_STR);
			toEngine_CommandByID.put(COMMAND_TO_ENGINE_NEWGAME, COMMAND_TO_ENGINE_NEWGAME_STR);
			toEngine_CommandByID.put(COMMAND_TO_ENGINE_POSITION, COMMAND_TO_ENGINE_POSITION_STR);
			toEngine_CommandByID.put(COMMAND_TO_ENGINE_GO, COMMAND_TO_ENGINE_GO_STR);
			toEngine_CommandByID.put(COMMAND_TO_ENGINE_STOP, COMMAND_TO_ENGINE_STOP_STR);
			toEngine_CommandByID.put(COMMAND_TO_ENGINE_QUIT, COMMAND_TO_ENGINE_QUIT_STR);
		}
	}

	
	public static int getToGUICommandID(String fromGUI) {
		synchronized (Protocol.class) {
			return toGUI_IDByCommand.get(fromGUI);	
		}
	}
	
	public static int getToEngineCommandID(String fromGUI) {
		synchronized (Protocol.class) {
			if (toEngine_IDByCommand == null) {
				throw new IllegalStateException("toEngine_IDByCommand == null");
			}
			if (fromGUI == null) {
				//return COMMAND_UNDEFINED;
				throw new IllegalStateException("fromGUI == null");
			}
			
			Integer commandID = toEngine_IDByCommand.get(fromGUI);
			if (commandID == null) {
				return COMMAND_UNDEFINED;
			}
			
			return commandID;
		}
	}
}

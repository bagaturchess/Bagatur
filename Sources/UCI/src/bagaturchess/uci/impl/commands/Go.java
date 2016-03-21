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
package bagaturchess.uci.impl.commands;


import java.util.StringTokenizer;

import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.uci.impl.Protocol;

/**
 * go
	start calculating on the current position set up with the "position" command.
	There are a number of commands that can follow this command, all will be sent in the same string.
	If one command is not sent its value should be interpreted as it would not influence the search.
	* searchmoves <move1> .... <movei>
		restrict search to this moves only
		Example: After "position startpos" and "go infinite searchmoves e2e4 d2d4"
		the engine should only search the two moves e2e4 and d2d4 in the initial position.
	* ponder
		start searching in pondering mode.
		Do not exit the search in ponder mode, even if it's mate!
		This means that the last move sent in in the position string is the ponder move.
		The engine can do what it wants to do, but after a "ponderhit" command
		it should execute the suggested move to ponder on. This means that the ponder move sent by
		the GUI can be interpreted as a recommendation about which move to ponder. However, if the
		engine decides to ponder on a different move, it should not display any mainlines as they are
		likely to be misinterpreted by the GUI because the GUI expects the engine to ponder
	   on the suggested move.
	* wtime <x>
		white has x msec left on the clock
	* btime <x>
		black has x msec left on the clock
	* winc <x>
		white increment per move in mseconds if x > 0
	* binc <x>
		black increment per move in mseconds if x > 0
	* movestogo <x>
      there are x moves to the next time control,
		this will only be sent if x > 0,
		if you don't get this and get the wtime and btime it's sudden death
	* depth <x>
		search x plies only.
	* nodes <x>
	   search x nodes only,
	* mate <x>
		search for a mate in x moves
	* movetime <x>
		search exactly x mseconds
	* infinite
		search until the "stop" command. Do not exit the search without being told so in this mode!
 */
public class Go extends Protocol {
	

	private static int MAX_DEPTH = Integer.MAX_VALUE;
	private static long MAX_TIME = Long.MAX_VALUE;
	
	//go wtime 3600000 btime 3600000 winc 0 binc 0
	private String commandLine;
	private boolean ponder = false;
	private long wtime = MAX_TIME;
	private long btime = MAX_TIME;
	private long winc = 0;
	private long binc = 0;
	private long nodes = Long.MAX_VALUE;
	private int movestogo = -1; // Should be -1 !!!
	private long movetime = -1; // Should be -1 !!!
	private boolean infinite = false; //go infinite
	private int depth = MAX_DEPTH; // Should be big number !!!
	
	private IChannel channel;
	
	
	public Go(IChannel _channel, String _commandLine) {
		channel = _channel;
		commandLine =_commandLine;
		parse();
	}
	
	private void parse() {
		StringTokenizer st = new StringTokenizer(commandLine, IChannel.WHITE_SPACE);
		
		if (!st.hasMoreTokens()) {
			channel.dump("Incorrect 'go' command: " + commandLine);
		} else {
			String go = st.nextToken();
			if (!go.equals(Protocol.COMMAND_TO_ENGINE_GO_STR)) {
				channel.dump("Incorrect 'go' command: " + commandLine);
			}
		}
		
		/**
		 * Parse ponder
		 */
		int ponderStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_PONDER_STR);
		if (ponderStartIndex != -1) {
			ponder = true;
		}
		
		/**
		 * Parse wtime
		 */
		int wtimeStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_WTIME_STR);
		if (wtimeStartIndex != -1) {
			int wtimeNumberStartIndex = commandLine.indexOf(IChannel.WHITE_SPACE, wtimeStartIndex + 1);
			if (wtimeNumberStartIndex == -1) {
				channel.dump("Incorrect 'go' command (there is no number after wtime): " + commandLine);
			} else {
				int wtimeNumberEndIndex = commandLine.indexOf(IChannel.WHITE_SPACE, wtimeNumberStartIndex + 1);
				if (wtimeNumberEndIndex == -1) {
					wtimeNumberEndIndex = commandLine.length();
				}
				String wtimeStr = commandLine.substring(wtimeNumberStartIndex + 1, wtimeNumberEndIndex);
				wtime = Long.parseLong(wtimeStr);
			}
		}
		
		/**
		 * Parse btime
		 */
		int btimeStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_BTIME_STR);
		if (btimeStartIndex != -1) {
			int btimeNumberStartIndex = commandLine.indexOf(IChannel.WHITE_SPACE, btimeStartIndex + 1);
			if (btimeNumberStartIndex == -1) {
				channel.dump("Incorrect 'go' command (there is no number after btime): " + commandLine);
			} else {
				int btimeNumberEndIndex = commandLine.indexOf(IChannel.WHITE_SPACE, btimeNumberStartIndex + 1);
				if (btimeNumberEndIndex == -1) {
					btimeNumberEndIndex = commandLine.length();
				}
				String btimeStr = commandLine.substring(btimeNumberStartIndex + 1, btimeNumberEndIndex);
				btime = Long.parseLong(btimeStr);
			}
		}
		
		
		/**
		 * Parse winc
		 */
		int wincStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_WINC_STR);
		if (wincStartIndex != -1) {
			int wincNumberStartIndex = commandLine.indexOf(IChannel.WHITE_SPACE, wincStartIndex + 1);
			if (wincNumberStartIndex == -1) {
				channel.dump("Incorrect 'go' command (there is no number after winc): " + commandLine);
			} else {
				int wincNumberEndIndex = commandLine.indexOf(IChannel.WHITE_SPACE, wincNumberStartIndex + 1);
				if (wincNumberEndIndex == -1) {
					wincNumberEndIndex = commandLine.length();
				}
				String wincStr = commandLine.substring(wincNumberStartIndex + 1, wincNumberEndIndex);
				winc = Long.parseLong(wincStr);
			}
		}
		
		/**
		 * Parse binc
		 */
		int bincStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_BINC_STR);
		if (bincStartIndex != -1) {
			int bincNumberStartIndex = commandLine.indexOf(IChannel.WHITE_SPACE, bincStartIndex + 1);
			if (bincNumberStartIndex == -1) {
				channel.dump("Incorrect 'go' command (there is no number after binc): " + commandLine);
			} else {
				int bincNumberEndIndex = commandLine.indexOf(IChannel.WHITE_SPACE, bincNumberStartIndex + 1);
				if (bincNumberEndIndex == -1) {
					bincNumberEndIndex = commandLine.length();
				}
				String bincStr = commandLine.substring(bincNumberStartIndex + 1, bincNumberEndIndex);
				binc = Long.parseLong(bincStr);
			}
		}
		
		/**
		 * Parse nodes
		 */
		int nodesStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_NODES_STR);
		if (nodesStartIndex != -1) {
			int nodesNumberStartIndex = commandLine.indexOf(IChannel.WHITE_SPACE, nodesStartIndex + 1);
			if (nodesStartIndex == -1) {
				channel.dump("Incorrect 'go' command (there is no number after nodes): " + commandLine);
			} else {
				int nodesNumberEndIndex = commandLine.indexOf(IChannel.WHITE_SPACE, nodesNumberStartIndex + 1);
				if (nodesNumberEndIndex == -1) {
					nodesNumberEndIndex = commandLine.length();
				}
				String nodes_str = commandLine.substring(nodesNumberStartIndex + 1, nodesNumberEndIndex);
				nodes = Long.parseLong(nodes_str);
			}
		}
		
		/**
		 * Parse movestogo
		 */
		int movestogoStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_MOVESTOGO_STR);
		if (movestogoStartIndex != -1) {
			int movestogoNumberStartIndex = commandLine.indexOf(IChannel.WHITE_SPACE, movestogoStartIndex + 1);
			if (movestogoNumberStartIndex == -1) {
				channel.dump("Incorrect 'go' command (there is no number after movestogo): " + commandLine);
			} else {
				int movestogoNumberEndIndex = commandLine.indexOf(IChannel.WHITE_SPACE, movestogoNumberStartIndex + 1);
				if (movestogoNumberEndIndex == -1) {
					movestogoNumberEndIndex = commandLine.length();
				}
				String movestogo_str = commandLine.substring(movestogoNumberStartIndex + 1, movestogoNumberEndIndex);
				movestogo = Integer.parseInt(movestogo_str);
			}
		}
		
		/**
		 * Parse movetime
		 */
		int movetimeStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_MOVETIME_STR);
		if (movetimeStartIndex != -1) {
			int movetimeNumberStartIndex = commandLine.indexOf(IChannel.WHITE_SPACE, movetimeStartIndex + 1);
			if (movetimeNumberStartIndex == -1) {
				channel.dump("Incorrect 'go' command (there is no number after movetime): " + commandLine);
			} else {
				int movetimeNumberEndIndex = commandLine.indexOf(IChannel.WHITE_SPACE, movetimeNumberStartIndex + 1);
				if (movetimeNumberEndIndex == -1) {
					movetimeNumberEndIndex = commandLine.length();
				}
				String movetimeStr = commandLine.substring(movetimeNumberStartIndex + 1, movetimeNumberEndIndex);
				movetime = Long.parseLong(movetimeStr);
			}
		}
		
		int infiniteStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_INFINITE_STR);
		if (infiniteStartIndex != -1) {
			infinite = true;
		}
		
		int depthStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_GO_DEPTH_STR);
		if (depthStartIndex != -1) {
			int depthValueStartIndex = commandLine.indexOf(IChannel.WHITE_SPACE, depthStartIndex + 1);
			if (depthValueStartIndex == -1) {
				channel.dump("Incorrect 'go' command (there is no number after depth): " + commandLine);
			} else {
				int depthValueEndIndex = commandLine.indexOf(IChannel.WHITE_SPACE, depthValueStartIndex + 1);
				if (depthValueEndIndex == -1) {
					depthValueEndIndex = commandLine.length();
				}
				String movetimeStr = commandLine.substring(depthValueStartIndex + 1, depthValueEndIndex);
				depth = Integer.parseInt(movetimeStr);
			}
		}
		
		//System.out.println(this);
	}
	
	public boolean isPonder() {
		return ponder;
	}

	public void setPonder(boolean _ponder) {
		ponder = _ponder;
	}
	
	public String toString() {
		String result = "";
		result += "ponder " + ponder;
		result += " wtime " + wtime;
		result += " btime " + btime;
		result += " winc " + winc;
		result += " binc " + binc;
		result += " nodes " + nodes;
		result += " movestogo " + movestogo;
		result += " movetime " + movetime;
		result += " infinite " + infinite;
		result += " depth " + depth;
		return result;
	}
	
	public String getCommandLine() {
		return commandLine;
	}
	
	public long getBtime() {
		return btime;
	}
	
	public long getWtime() {
		return wtime;
	}
	
	public long getWinc() {
		return winc;
	}
	
	public long getBinc() {
		return binc;
	}
	
	public long getNodes() {
		return nodes;
	}
	
	public boolean hasNodes() {
		return nodes != Long.MAX_VALUE; 
	}
	
	public int getMovestogo() {
		return movestogo;
	}
	
	public long getMovetime() {
		return movetime;
	}
	
	public boolean isAnalyzingMode() {
		return infinite;
	}
	
	public boolean hasDepth() {
		return depth != Integer.MAX_VALUE; 
	}
	
	public int getDepth() {
		return depth;
	}
	
	public static void main(String[] args) {
		//Go go = new Go("go ponder wtime 3600001 btime 3600000 winc 123 binc 456 nodes 12345678 movestogo 12 depth 89");
		Go go = new Go(new Channel_Console(), "go nodes 12345678");
		System.out.println(go);
	}
}

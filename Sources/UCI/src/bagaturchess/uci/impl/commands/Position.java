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


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.uci.impl.Protocol;

/**
 *position [fen <fenstring> | startpos ]  moves <move1> .... <movei>
 *set up the position described in fenstring on the internal board and
 *play the moves on the internal chess board.
 *if the game was played  from the start position the string "startpos" will be sent
 *Note: no "new" command is needed. However, if this position is from a different game than
 *the last position sent to the engine, the GUI should have sent a "ucinewgame" inbetween.
 */
public class Position extends Protocol {
	
	private String commandLine;
	private String fen;
	private List<String> moves;
	
	private IChannel channel;
	
	
	public Position(IChannel _channel, String _commandLine) {
		channel = _channel;
		commandLine =_commandLine;
		moves = new ArrayList<String>();
		parse();
	}

	private void parse() {
		StringTokenizer st = new StringTokenizer(commandLine, IChannel.WHITE_SPACE);
		
		if (!st.hasMoreTokens()) {
			channel.dump("Incorrect 'position' command: " + commandLine);
		} else {
			String position = st.nextToken();
			if (!position.equals(Protocol.COMMAND_TO_ENGINE_POSITION_STR)) {
				channel.dump("Incorrect 'position' command: " + commandLine);
			}
		}
		
		String nextword = st.nextToken();
		if (nextword.equals(COMMAND_TO_ENGINE_POSITION_START_POS)) {
			//fen = Constants.INITIAL_BOARD;
		} else if (nextword.equals(COMMAND_TO_ENGINE_POSITION_FEN)) {
			int fenStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_POSITION_FEN) + COMMAND_TO_ENGINE_POSITION_FEN.length();
			fen = commandLine.substring(fenStartIndex, commandLine.length()).trim();
		} else {
			channel.dump("LOG Parsing 'position' command (there is no startpos or fen): " + commandLine);
		}
		
		int movesStartIndex = commandLine.indexOf(COMMAND_TO_ENGINE_POSITION_MOVES);
		if (movesStartIndex >= 0) {
			String movesStr = commandLine.substring(movesStartIndex + COMMAND_TO_ENGINE_POSITION_MOVES.length()).trim();
			StringTokenizer stMoves = new StringTokenizer(movesStr, IChannel.WHITE_SPACE);
			while(stMoves.hasMoreTokens()) {
				String move = stMoves.nextToken();
				moves.add(move);
			}
		}
				
		System.out.println("LOG " + this);
	}
	
	public String toString() {
		String result = "";
		result += fen == null ? "startpos" : "'" + fen + "'";
		result += " -> ";
		result += "moves=" + moves;
		return result;
	}
	
	public static void main(String[] args) {
		try {
			new Position(new Channel_Console(), "position startpos moves e2e4 e7e5");
			new Position(new Channel_Console(), "position fen rn1b2rk/1pp3p1/qp1p2R1/5Q2/3RN2P/1PP5/3PbP2/4K3 w - -");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String getCommandLine() {
		return commandLine;
	}

	public String getFen() {
		return fen;
	}

	public List<String> getMoves() {
		return moves;
	}
}

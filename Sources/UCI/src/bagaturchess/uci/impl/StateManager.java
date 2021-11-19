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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.api.IUCIConfig;
import bagaturchess.uci.api.IUCISearchAdaptor;
import bagaturchess.uci.api.UCISearchAdaptorFactory;
import bagaturchess.uci.impl.commands.Go;
import bagaturchess.uci.impl.commands.Position;
import bagaturchess.uci.impl.commands.options.SetOption;
import bagaturchess.uci.impl.commands.options.actions.OptionsManager;


public class StateManager extends Protocol implements BestMoveSender {
	
	
	//Disabled.
	//ATTENTION: Should be definitely disabled for openning book moves! (otherwise causes perpetual GC calls)
	private boolean GC_AFTER_MOVE = false;
	
	
	protected IUCIConfig engineBootCfg;
	private IChannel channel;
	private IBitBoard board;
	
	private IUCISearchAdaptor searchAdaptor;
	
	private OptionsManager optionsManager;
	
	private String lastFEN;
	
	
	public StateManager(IUCIConfig _engineBootCfg) {
		engineBootCfg = _engineBootCfg;
		board = BoardUtils.createBoard_WithPawnsCache();
	}
	
	
	public void setOptionsManager(OptionsManager om) {
		optionsManager = om;
	}
	
	
	public void setChannel(IChannel _channel) {
		channel = _channel;
	}
	
	
	public void createSearchAdaptor() throws FileNotFoundException {
		
		channel.sendLogToGUI("StateManager: Creating search adaptor ...");
		
		searchAdaptor = UCISearchAdaptorFactory.newUCISearchAdaptor(engineBootCfg, board);
		
		channel.sendLogToGUI("StateManager: Search adaptor Created.");
	}
	
	
	public boolean destroySearchAdaptor() throws FileNotFoundException {
		
		boolean destroyed = false;
		
		channel.sendLogToGUI("StateManager: Destroing search adaptor ...");
		
		IUCISearchAdaptor lastAdaptor = searchAdaptor;
		searchAdaptor = null;
		
		if (lastAdaptor != null) {
			channel.sendLogToGUI("StateManager: Stoping old search adaptor ...");
			lastAdaptor.stopSearch();
			lastAdaptor.shutDown();
			channel.sendLogToGUI("StateManager: Old search adaptor stopped.");
			
			channel.sendLogToGUI("StateManager: Run gc ...");
			System.gc();
			channel.sendLogToGUI("StateManager: GC ok.");
			
			//Wait GC to free up the memory
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			
			destroyed = true;
		}
		
		channel.sendLogToGUI("StateManager: Search adaptor Destroyed.");
		
		return destroyed;
	}
	
	
	public void communicate() throws Exception {
		
		sendHello();
			
		while (true) {
			
			try {
				
				//fromGUILine is null if end of the stream is reached
				String fromGUILine = channel.receiveCommandFromGUI();
				if (fromGUILine == null) {
					channel.sendLogToGUI("StateManager: System.exit(0), because the end of stream is reached");
					Thread.sleep(333);//Wait to write the log
					System.exit(0);
					return;
				}
				
				String fromGUICommand = getFromGUICommand(fromGUILine);
				int fromGUICommandID = getToEngineCommandID(fromGUICommand);
				
				if (fromGUICommandID == Protocol.COMMAND_UNDEFINED) {
					channel.sendLogToGUI("StateManager: Command " + fromGUICommand + " UNSUPPORTED from Bagatur Chess Engine");
					//Thread.sleep(100);
				} else {
					channel.sendLogToGUI("StateManager: exec command " + fromGUICommandID + " > '" + fromGUILine + "'");
					switch (fromGUICommandID) {
						case COMMAND_TO_ENGINE_UCI:
							sendEngineID();
							sendOptions();
							sendUCIOK();
							break;
						case COMMAND_TO_ENGINE_ISREADY:
							//Can be commented as after the later call with ucinewgame command, the search adaptor will be re-created anyway
							if (searchAdaptor == null) {
								createSearchAdaptor();
							}
							sendReadyOK();
							break;
						case COMMAND_TO_ENGINE_NEWGAME:
							//waitAndExecute();
							createNewGame();
							break;
						case COMMAND_TO_ENGINE_POSITION:
							//waitAndExecute();
							setupBoard(fromGUILine);
							break;
						case COMMAND_TO_ENGINE_GO:
							if (searchAdaptor == null) {
								createSearchAdaptor();
							}
							goSearch(fromGUILine);
							break;
						case COMMAND_TO_ENGINE_PONDERHIT:
							if (searchAdaptor != null) {
								ponderHit(fromGUILine);
							} else {
								channel.sendLogToGUI("StateManager: command ponderhit skiped, because searchadpator is null");
							}
							break;
						case COMMAND_TO_ENGINE_SETOPTION:
							//waitAndExecute();
							setOption(fromGUILine);
							break;
						case COMMAND_TO_ENGINE_STOP:
							//waitAndExecute();
							if (searchAdaptor != null) {
								sendBestMove();
							} else {
								channel.sendLogToGUI("StateManager: command stop skiped, because searchadpator is null");
							}
							break;
						case COMMAND_TO_ENGINE_QUIT:
							channel.sendLogToGUI("StateManager: System.exit(0), because of QUIT command");
							Thread.sleep(333);//Wait to write the log
							System.exit(0);
							break;
						default:
							throw new IllegalStateException();
					}
				}
				
				sendNewline();
				
			} catch(Throwable  t) {
				channel.dump(t);
				channel.sendLogToGUI("StateManager: Error: " + t.getMessage());
				Thread.sleep(500);
			}
		}
	}


	private String getFromGUICommand(String fromGUILine) {
		String command = fromGUILine;
		if (command != null) {
			int index = fromGUILine.indexOf(' ');
			if (index != -1) {
				command = fromGUILine.substring(0, index);
			}
		}
		return command;
	}
	
	
	private void sendHello() throws IOException {
		String result = "\r\n\r\n";
		result += "***************************************************************************";
		result += "\r\n";
		result += "* Copyright (C) 2005-2022 Krasimir I. Topchiyski (k_topchiyski@yahoo.com) *";
		result += "\r\n";
		result += "*                                                                         *";
		result += "\r\n";
		result += "* Welcome to Bagatur UCI engine, version " + COMMAND_TO_GUI_ID_VERSION_STR + "                              *";
		result += "\r\n";
		result += "*                                                                         *";
		result += "\r\n";
		result += "* For help, have a look at the UCI protocol definition at:                *";
		result += "\r\n";
		result += "* http://wbec-ridderkerk.nl/html/UCIProtocol.html                         *";
		result += "\r\n";
		result += "***************************************************************************";
		result += "\r\n";
		result += ">";
		channel.sendCommandToGUI_no_newline(result);
	}
	
	
	private void sendNewline() throws IOException {
		//String result = "\r\n";
		//result += ">";
		//channel.sendCommandToGUI_no_newline(result);
	}
	
	
	private void sendEngineID() throws IOException {
		String id_name = COMMAND_TO_GUI_ID_STR + IChannel.WHITE_SPACE;
		id_name += COMMAND_TO_GUI_ID_NAME_STR + IChannel.WHITE_SPACE + "Bagatur " + COMMAND_TO_GUI_ID_VERSION_STR;
		channel.sendCommandToGUI(id_name);
		
		String id_author = COMMAND_TO_GUI_ID_STR + IChannel.WHITE_SPACE;
		id_author += COMMAND_TO_GUI_ID_AUTHOR_STR + IChannel.WHITE_SPACE + "Krasimir Topchiyski" + ", Bulgaria";
		channel.sendCommandToGUI(id_author);
	}
	
	
	private void sendOptions() throws IOException {
		for (int i=0; i<optionsManager.getOptions().getAllOptions().length; i++) {
			String line = COMMAND_TO_GUI_OPTION_STR + IChannel.WHITE_SPACE
							+ COMMAND_TO_ENGINE_SETOPTION_NAME_STR + IChannel.WHITE_SPACE
							+ optionsManager.getOptions().getAllOptions()[i].getDefineCommand();
			channel.sendCommandToGUI(line);
		}
	}
	
	
	private void setOption(String fromGUILine) throws IOException {
		
		channel.sendLogToGUI("StateManager: Set-option called with line: " + fromGUILine);
		SetOption setoption = new SetOption(channel, fromGUILine);
		channel.sendLogToGUI("StateManager: Set-option parsed: " + setoption);
		
		optionsManager.set(setoption);
	}
	
	
	private void sendUCIOK() throws IOException {
		channel.sendLogToGUI("StateManager: sendUCIOK called");
		channel.sendCommandToGUI(COMMAND_TO_GUI_UCIOK_STR);
	}
	
	
	private void sendReadyOK() throws IOException {
		channel.sendLogToGUI("StateManager: sendReadyOK called");
		channel.sendCommandToGUI(COMMAND_TO_GUI_READYOK);
	}
	
	
	private void createNewGame() {
		channel.sendLogToGUI("StateManager: createNewGame called");
		revertGame();
	}
	
	
	private void setupBoard(String fromGUILine) throws FileNotFoundException {
		
		channel.sendLogToGUI("StateManager: setupBoard called with " + fromGUILine);
		
		/*
		 * Setup startup board with FEN or played moves
		 */
		Position position = new Position(channel, fromGUILine);
		
		if (position.getFen() != null) {
			
			if (lastFEN == null || !lastFEN.equals(position.getFen())) {
				
				board = BoardUtils.createBoard_WithPawnsCache(position.getFen(), null);
				destroySearchAdaptor();
				
				lastFEN = position.getFen();
			}
			
		} else {
			
			if (lastFEN != null) {
				board = BoardUtils.createBoard_WithPawnsCache();
				destroySearchAdaptor();
				
				lastFEN = null;	
			}
			
			revertGame();
		}
		
		playMoves(position);
	}


	private void playMoves(Position position) {
		List<String> moves = position.getMoves();
		for (int i = 0; i < moves.size(); i++ ) {
			String moveSign = moves.get(i);
			if (!moveSign.equals("...")) {
				board.makeMoveForward(moveSign);
			}
		}
	}
	
	
	private void goSearch(String fromGUILine) throws IOException {
		channel.sendLogToGUI("StateManager: goSearch called");
		Go go = new Go(channel, fromGUILine);	
		channel.sendLogToGUI(go.toString());
		
		searchAdaptor.goSearch(channel, this, go);
	}
	
	
	private void ponderHit(String fromGUILine) throws IOException {
		channel.sendLogToGUI("StateManager: Ponder hit -> switching search");
		if (searchAdaptor != null) searchAdaptor.ponderHit();
	}
	
	
	@Override
	public void sendBestMove() {
		
		channel.sendLogToGUI("StateManager: sendBestMove called");
		
		if (searchAdaptor == null) {
			channel.sendLogToGUI("StateManager: sendBestMove searchAdaptor is null");
			return;
		}
		
		int[] moveAndPonder = searchAdaptor.stopSearch();
		int move = moveAndPonder[0];
		int ponder = moveAndPonder[1];
		if (move != 0) {
			
			String result = board.getMoveOps().moveToString(move);
			board.makeMoveForward(move);
			
			String bestMoveCommand = COMMAND_TO_GUI_BESTMOVE_STR + IChannel.WHITE_SPACE + result;
			if (ponder != 0) {
				bestMoveCommand += IChannel.WHITE_SPACE + COMMAND_TO_GUI_BESTMOVE_PONDER_STR + IChannel.WHITE_SPACE;
				result = board.getMoveOps().moveToString(ponder);
				bestMoveCommand += result;
			}
			
			channel.sendLogToGUI("StateManager: sendBestMove bestMoveCommand=" + bestMoveCommand);
			
			
			try {
				channel.sendCommandToGUI(bestMoveCommand);
				
				channel.sendLogToGUI("StateManager: bestMoveCommand send");
				
			} catch (IOException e) {
				channel.dump(e);
			}
		} else {
			throw new IllegalStateException("StateManager: ERROR: move returned from UCI Search adaptor is '0' and is not sent to the UCI platform");
			//channel.sendLogToGUI("StateManager: WARNING: StateManager -> move returned from UCI Search adaptor is '0' and is not sent to the UCI platform");
		}
		
		
		if (GC_AFTER_MOVE) {
			System.gc();
		}
	}
	
	
	private void revertGame() {
		int count = board.getPlayedMovesCount();
		int[] moves = board.getPlayedMoves();
		for (int i = count - 1; i >=0; i--) {
			int move = moves[i];
			board.makeMoveBackward(move);
		}
	}
}

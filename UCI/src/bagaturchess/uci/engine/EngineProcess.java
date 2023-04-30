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
package bagaturchess.uci.engine;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class EngineProcess {
	
	
	private String engineName;
	private String startCommand;
	private String[] props;
	private String workDir;
	private Process process;
	private BufferedReader is;
	private BufferedWriter os;
	private BufferedReader err;
	
	private EngineProcessDummperThread dummper;
	
	
	public EngineProcess(String _startCommand, String[] _props, String _workDir) {
		//System.out.println(_startCommand);
		startCommand = _startCommand;
		props = _props;
		workDir = _workDir;
		
		int idx = Math.max(startCommand.lastIndexOf('/'), startCommand.lastIndexOf('\\'));
		if (idx < 0) {
			idx = 0;
		}
		engineName = startCommand.substring(idx + 1);
	}
	
	public EngineProcess(String _engineName, String _startCommand, String[] _props, String _workDir) {
		engineName = _engineName;
		startCommand = _startCommand;
		props = _props;
		workDir = _workDir;
	}
	
	public String getName() {
		return engineName;
	}
	
	
	public void setDummperMode(boolean enabled) {
		if (enabled) {
			dummper.enabled();
		} else {
			dummper.disable();
		}
	}
	
	public void start() throws IOException {
		
		ChannelManager.getChannel().dump("EngineProcess:startCommand=" + startCommand);
		ChannelManager.getChannel().dump("EngineProcess:workDir=" + new File(workDir));
		
		process = Runtime.getRuntime().exec(startCommand, props, new File(workDir));
		
		is = new BufferedReader(new InputStreamReader(process.getInputStream()));
		os = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		
		
		dummper = new EngineProcessDummperThread("OUT", is);
		dummper.start();
		(new EngineProcessDummperThread("ERR", err)).start();
		
		
		Thread closeChildProcess = new Thread() {
		    public void run() {
		    	process.destroy();
		    }
		};
		
		Runtime.getRuntime().addShutdownHook(closeChildProcess);
	}
	
	
	public void destroy() throws IOException {
		
		//ChannelManager.getChannel().sendLogToGUI("EngineProcess: destroy ...");
		
		if (process != null) {
			process.destroy();
		}
		
		//ChannelManager.getChannel().sendLogToGUI("EngineProcess: destroy OK");
		
		dummper.interrupt();
	}
	
	public boolean supportsUCI() throws IOException {
		
		os.write("uci");	
		os.newLine();
		os.flush();
		
		String line;
		while ((line = is.readLine()) != null) {
			//System.out.println(line);
			if (line.contains("uciok")) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isReady() throws IOException {
		
		os.write("isready");	
		os.newLine();
		os.flush();
		
		String line;
		while ((line = is.readLine()) != null) {
			if (line.contains("readyok")) {
				return true;
			}
		}
		
		return false;
	}
	
	public void setOptions(List<String> setoptions) throws IOException {
		
		for(String setoption: setoptions) {
			os.write(setoption);
			os.newLine();
			os.flush();
			
			/*String line;
			while ((line = is.readLine()) != null) {
			}*/
		}
	}
	
	
	public void setupPossition(String position) throws IOException {
		os.write("position " + position);	
		os.newLine();
		os.flush();
	}
	
	public void stop() throws IOException {
		
		//ChannelManager.getChannel().dump("DUMP: EngineProcess.stop() with stack: " + new Exception());
		
		os.write("stop");	
		os.newLine();
		os.flush();
	}
	
	public void go(Go go) throws IOException {
		//System.out.println("go depth " + depth);
		os.write(go.getCommandLine());
		os.newLine();
		os.flush();
	}
	
	public void go_Depth(int depth) throws IOException {
		//System.out.println("go depth " + depth);
		os.write("go depth " + depth);
		os.newLine();
		os.flush();
	}
	
	public void go_FixedNodes(int nodes) throws IOException {
		os.write("go nodes " + nodes);
		os.newLine();
		os.flush();
	}
	
	public void go_FixedDepth(int depth) throws IOException {
		//System.out.println("go depth " + depth);
		os.write("go depth " + depth);
		os.newLine();
		os.flush();
	}
	
	public void go_TimePerMove(int milis) throws IOException {
		//System.out.println("go depth " + depth);
		os.write("go movetime " + milis);
		os.newLine();
		os.flush();
	}
	
	public void go_TimeAndInc(int wtime, int btime, int winc, int binc) throws IOException {
		//go wtime 120000 btime 120000 winc 6000 binc 6000
		os.write("go wtime " + wtime + " btime " + btime + " winc " + winc + " binc " + binc);
		os.newLine();
		os.flush();
	}

	
	public void newGame() throws IOException {
		os.write("ucinewgame");	
		os.newLine();
		os.flush();
	}
	
	
	public String getInfoLine() throws IOException {
		
		
		return getInfoLine(new LineCallBack() {
			
			
			private List<String> lines = new ArrayList<String>();
			private String exitLine = null; 
			
			
			@Override
			public void newLine(String line) {
				
				//System.out.println(engineName + "/EngineProcess: getInfoLine new line is: '" + line + "'");
				
				if (line.contains("LOG")) {
					return;
				}
				
				lines.add(line);
				
				if (line.contains("bestmove")) {
					for (int i=lines.size() - 1; i >=0; i--) {
						//System.out.println(engineName + "/EngineProcess: getInfoLine " + lines.get(i));
						if (lines.get(i).contains("info "/*depth"*/) && lines.get(i).contains(" pv ")) {
							exitLine = lines.get(i);
							break;
						}
					}
					if (exitLine == null) {
						throw new IllegalStateException("No pv: " + lines);
					}
				}
			}
			
			
			@Override
			public String exitLine() {
				return exitLine;
			}
		});
	}
	
	
	public String getInfoLine(LineCallBack lineCallBack) throws IOException {
		
		String line;
		while ((line = is.readLine()) != null) {
			lineCallBack.newLine(line);
			if (lineCallBack.exitLine() != null) {
				return lineCallBack.exitLine();
			}
		}
		
		throw new IllegalStateException("Out of getInfoLine");
	}
	
	
	public static interface LineCallBack {
		public void newLine(String line);
		public String exitLine();//Returns last expected line, if it is still null than continue 
	}
	
	
	public String getInfoLine1() throws IOException {
		
		List<String> lines = new ArrayList<String>();
		
		//System.out.println("in");
		
		String line;
		//while ((line = is.readLine()) != null) {
		
		while (true) {
			
			//System.out.println("in");
			/*long starttime = System.currentTimeMillis();
			while (!is.ready()) {
				long endtime = System.currentTimeMillis();
				if (endtime - starttime > 1000) {
					System.out.println("hangs");
					return null; //engine hangs
				}
				try {
					//System.out.println("retry");
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			
			line = is.readLine();
			//System.out.println(line);
			
			if (line.contains("bestmove")) {
				for (int i=lines.size() - 1; i >=0; i--) {
					if (lines.get(i).startsWith("info")
							&& lines.get(i).contains(" depth ")
							&& (lines.get(i).contains(" pv ") || lines.get(i).contains(" multipv 1 "))) {
						return lines.get(i);
					}
				}
				
				//No info before bestmove
				//System.out.println("No info before bestmove");
				return null;
				//throw new IllegalStateException("No pv: " + lines);
			}
			lines.add(line);
		}
		
		//throw new IllegalStateException("getInfoLine blocked");
	}
}

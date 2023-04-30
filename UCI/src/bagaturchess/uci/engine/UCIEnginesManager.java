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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.EngineProcess.LineCallBack;
import bagaturchess.uci.impl.commands.Go;



public class UCIEnginesManager {

	
	private List<EngineProcess> engines;
	
	
	public UCIEnginesManager() {
		engines = new ArrayList<EngineProcess>();
	}
	
	
	public void addEngine(EngineProcess engine) {
		engines.add(engine);
	}
	
			
	public void startEngines() throws IOException  {
		for (EngineProcess engine: engines) {
			engine.start();
		}
	}

	
	public void destroyEngines() throws IOException  {
		for (EngineProcess engine: engines) {
			engine.destroy();
		}
	}
	
	
	public void stopEngines() throws IOException  {
		for (EngineProcess engine: engines) {
			engine.stop();
		}
	}
	
	
	public void uciOK() throws IOException  {
		
		disable();
		
		int counter = 1;
		for (EngineProcess engine: engines) {			
			if (engine.supportsUCI()) {
				//System.out.println("Engine " + counter + " supports UCI");
			}
			counter++;
		}
		//System.out.println("All Engines started");
		
		enable();
	}
	
	
	public void setOptions(List<String> setoptions) throws IOException {
		
		disable();
		
		for (EngineProcess engine: engines) {			
			engine.setOptions(setoptions);
		}
		
		enable();
	}
	
	
	public void isReady() throws IOException {
		
		disable();
		
		int counter = 1;
		for (EngineProcess engine: engines) {			
			if (engine.isReady()) {
				//System.out.println("Engine " + counter + " is ready");
			}
			counter++;
		}
		//System.out.println("All Engines are ready");
		
		enable();
	}
	
	
	public void newGame() throws IOException {
		
		disable();
		
		for (EngineProcess engine: engines) {			
			engine.newGame();
		}
		//System.out.println("New game started");
		
		enable();
	}
	
	
	public void setupPosition(String epd) throws IOException {
		
		disable();
		
		for (EngineProcess engine: engines) {			
			engine.setupPossition(epd);
		}
		//System.out.println("Position set");
		
		enable();
	}
	
	
	public void go(Go go) throws IOException {
		
		//disable();
		
		for (EngineProcess engine: engines) {			
			engine.go(go);
		}
		//System.out.println("Started");
		
		//enable();
	}
	
	
	public void go_Depth(int depth) throws IOException {
		
		//disable();
		
		for (EngineProcess engine: engines) {			
			engine.go_FixedDepth(depth);
		}
		//System.out.println("Started");
		
		//enable();
	}
	
	
	public void enable() {
		for (EngineProcess engine: engines) {			
			engine.setDummperMode(true);
		}
	}
	
	
	public void disable() {
		for (EngineProcess engine: engines) {			
			engine.setDummperMode(false);
		}
	}
	
	
	public List<String> getInfoLines(LineCallBack lineCallBack) throws IOException {
		List<String> lines = new ArrayList<String>();
		for (EngineProcess engine: engines) {			
			lines.add(engine.getInfoLine(lineCallBack));
		}
		return lines;
	}
	
	
	public List<String> getInfoLines() throws IOException {
		List<String> lines = new ArrayList<String>();
		for (EngineProcess engine: engines) {			
			lines.add(engine.getInfoLine());
		}
		return lines;
	}
}

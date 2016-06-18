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
package bagaturchess.ucitracker.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class UCIEnginesRunner {

	
	private List<Engine> engines;
	
	
	public UCIEnginesRunner() {
		engines = new ArrayList<Engine>();
	}
	
	
	public void addEngine(Engine engine) {
		engines.add(engine);
	}
	
			
	public void startEngines() throws IOException  {
		for (Engine engine: engines) {
			engine.start();
		}
	}

	public void stopEngines() throws IOException  {
		for (Engine engine: engines) {
			engine.stop();
		}
	}
	
	
	public void uciOK() throws IOException  {
		
		disable();
		
		int counter = 1;
		for (Engine engine: engines) {			
			if (engine.supportsUCI()) {
				System.out.println("Engine " + counter + " supports UCI");
			}
			counter++;
		}
		System.out.println("All Engines started");
		
		enable();
	}
	
	
	public void isReady() throws IOException {
		
		disable();
		
		int counter = 1;
		for (Engine engine: engines) {			
			if (engine.isReady()) {
				System.out.println("Engine " + counter + " is ready");
			}
			counter++;
		}
		System.out.println("All Engines are ready");
		
		enable();
	}
	
	
	public void newGame() throws IOException {
		
		disable();
		
		for (Engine engine: engines) {			
			engine.newGame();
		}
		System.out.println("New game started");
		
		enable();
	}
	
	
	public void setupPosition(String epd) throws IOException {
		
		disable();
		
		for (Engine engine: engines) {			
			engine.setupPossition(epd);
		}
		//System.out.println("Position set");
		
		enable();
	}
	
	
	public void go(int depth) throws IOException {
		
		//disable();
		
		for (Engine engine: engines) {			
			engine.go(depth);
		}
		//System.out.println("Started");
		
		//enable();
	}
	
	
	public void enable() {
		for (Engine engine: engines) {			
			engine.setDummperMode(true);
		}
	}
	
	
	public void disable() {
		for (Engine engine: engines) {			
			engine.setDummperMode(false);
		}
	}

	
	public List<String> getInfoLines() throws IOException {
		List<String> lines = new ArrayList<String>();
		for (Engine engine: engines) {			
			lines.add(engine.getInfoLine());
		}
		return lines;
	}
}

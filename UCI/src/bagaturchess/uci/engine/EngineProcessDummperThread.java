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
import java.io.IOException;


public class EngineProcessDummperThread extends Thread {
	
	private volatile boolean enabled = false;
	
	private String header;
	private BufferedReader is;
	
	public EngineProcessDummperThread(String _header, BufferedReader _is) {
		header = _header;
		is = _is;
	}
	
	public void enabled() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
	
	public void run() {
		while (true) {
			String line = null;
			try {
				while (enabled && (line = is.readLine()) != null) {
					//System.out.println(header + ": '" + line + "'");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				//throw new IllegalStateException(e);
				return;
			}
		}
	}
}

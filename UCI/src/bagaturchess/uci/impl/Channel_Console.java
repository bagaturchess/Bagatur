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


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;


import bagaturchess.uci.api.IUCIConfig;


public class Channel_Console extends Channel_Base {
	
	
	public Channel_Console() {
		super();
	}
	
	
	public Channel_Console(InputStream _in, OutputStream _out, PrintStream _dump) {
		super(_in, _out, _dump);
	}
	
	
	//"single file", "multiple files", "none"
	public void initLogging(IUCIConfig engineBootCfg) throws FileNotFoundException {
		if ("single file".equals(engineBootCfg.getUCIAdaptor_LoggingPolicy())) {
			setPrintStream_1File();
		} else if ("multiple files".equals(engineBootCfg.getUCIAdaptor_LoggingPolicy())) {
			setPrintStream_MFiles();
		} else if ("none".equals(engineBootCfg.getUCIAdaptor_LoggingPolicy())) {
			setPrintStream_None();
		} else {
			throw new IllegalStateException("Wrong logging option: '" + engineBootCfg.getUCIAdaptor_LoggingPolicy() + "'");
		}
	}
	
	
	private void setPrintStream_SystemOut() {
		if (dump != null) {
			dumps.add("Switching logging to 'none'");
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			dump.close();
		}
		dump = System.out;
		dumpInitialLines();
	}
	
	
	private void setPrintStream_MFiles() throws FileNotFoundException {
		if (dump != null) {
			dumps.add("Switching logging to multiple files");
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			dump.close();
		}
		createLogDir();
		dump = new PrintStream(new BufferedOutputStream(new FileOutputStream("./log/Bagatur_" + System.currentTimeMillis() + ".log")), true);
		dumpInitialLines();
	}
	
	
	private void setPrintStream_1File() throws FileNotFoundException {
		if (dump != null) {
			dumps.add("Switching logging to single file");
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			dump.close();
		}
		createLogDir();
		dump = new PrintStream(new BufferedOutputStream(new FileOutputStream("./log/Bagatur.log", true)), true);
		dumpInitialLines();
	}
	
	
	private void setPrintStream_None() throws FileNotFoundException {
		if (dump != null) {
			dumps.add("Switching logging to 'none'");
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			dump.close();
		}
		dump = new DummyPrintStream();
		dumpInitialLines();
	}
	
	
	private static void createLogDir() {
		File logDir = new File("./log");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
	}
}

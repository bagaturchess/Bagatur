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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


import bagaturchess.uci.impl.utils.DEBUGUCI;


public class Channel {
	
	
	public static final String NEW_LINE = "\r\n";
	public static final String WHITE_SPACE = " ";
	
	
	static private PrintStream dump;
	static private Queue<Object> dumps;
	static private Thread logThread;
	
	private BufferedReader in;
	private BufferedWriter out;
	
	
	static {
		try {
			
			//setPrintStream_1File();
			
			dumps = new ConcurrentLinkedQueue<Object>();		
			
			logThread = new Thread(new LogRunnable());
			logThread.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public Channel() {
		out = new BufferedWriter(new OutputStreamWriter(System.out));
		in = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static void setPrintStream_SystemOut() {
		if (dump != null) {
			dumps.add("Switching logging to 'none'");
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			dump.close();
		}
		dump = System.out;
		dumpInitialLines();
	}
	
	
	public void setPrintStream_MFiles() throws FileNotFoundException {
		if (dump != null) {
			dumps.add("Switching logging to multiple files");
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			dump.close();
		}
		createLogDir();
		dump = new PrintStream(new BufferedOutputStream(new FileOutputStream("./log/Bagatur_" + System.currentTimeMillis() + ".log")), true);
		dumpInitialLines();
	}
	
	
	public void setPrintStream_1File() throws FileNotFoundException {
		if (dump != null) {
			dumps.add("Switching logging to single file");
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			dump.close();
		}
		createLogDir();
		dump = new PrintStream(new BufferedOutputStream(new FileOutputStream("./log/Bagatur.log", true)), true);
		dumpInitialLines();
	}
	
	
	public void setPrintStream_None() throws FileNotFoundException {
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
	
	
	private static void dumpInitialLines() {
		dump.println(NEW_LINE);
		dump.println(NEW_LINE);
		dump.println("Time: " + new Date());
	}
	
	
	public void close() {
		try {
			in.close();
		} catch (Exception e) {
		}
		try {
			out.close();
		} catch (Exception e) {
		}
	}
	
	
	public void sendLogToGUI(String command) {
		
		if (!DEBUGUCI.DEBUG_MODE) return;
		
		try {
			out.write("LOG " + command + NEW_LINE);
			dump("TO_GUI{" + new Date() + "}>" + " LOG " + command + NEW_LINE);
			out.flush();
		} catch (IOException e) {
		}
	}
	
	
	public void sendCommandToGUI(String command) throws IOException {
		sendCommandToGUI_no_newline(command + NEW_LINE);
	}
	
	
	public void sendCommandToGUI_no_newline(String command) throws IOException {
		out.write(command);
		dump("TO_GUI{" + new Date() + "}>" + command + NEW_LINE);
		out.flush();
	}
	
	
	public String receiveCommandFromGUI() throws IOException {
		String command = in.readLine();
		dump("FROM_GUI{" + new Date() + "}>" + command + NEW_LINE);
		return command;
	}
	
	
	public static void dump(String message) {
		
		if (!DEBUGUCI.DEBUG_MODE) return;
		
		dumps.add(message);
		
		/*try {
			if (message == null) {
				message = "null";
			}
			
			dump.write((message.trim() + NEW_LINE).getBytes());
			dump.flush();
		} catch (IOException e) {
		}*/
	}
	
	
	public static void dump(Throwable t) {
		dumps.add(t);
		
		/*t.printStackTrace(dump);
		dump.flush();*/
	}
	
	
	private static class LogRunnable implements Runnable {
		
		
		private LogRunnable() {
		}
		
		
		@Override
		public void run() {
			while (true) {
				try {
					if (dump == null) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					} else {
						Object cur = dumps.poll();
						if (cur != null) {
							if (cur instanceof Throwable) {
								((Throwable)cur).printStackTrace(dump);
								dump.flush();
							} else {
								if (!(cur instanceof String)) {
									throw new IllegalStateException("!(cur instanceof String): cur=" + cur);
								}
								dump.write((((String)cur).trim() + NEW_LINE).getBytes());
								dump.flush();
							}
						} else {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

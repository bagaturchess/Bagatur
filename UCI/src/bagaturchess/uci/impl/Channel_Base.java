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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;


import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.api.IUCIConfig;
import bagaturchess.uci.impl.utils.DEBUGUCI;


public class Channel_Base implements IChannel {
	
	
	protected PrintStream dump;
	protected Queue<Object> dumps;
	protected Thread logThread;
	
	protected BufferedReader in;
	protected BufferedWriter out;

	private InputStream in_stream;
	private OutputStream out_stream;
	
	
	public Channel_Base() {
		this(System.in, System.out, null);
	}
	
	
	public Channel_Base(InputStream _in, OutputStream _out, PrintStream _dump) {
		
		in_stream = _in;
		out_stream = _out;
		
		in = new BufferedReader(new InputStreamReader(_in));
		out = new BufferedWriter(new OutputStreamWriter(_out));
		dump = _dump;
		
		dumps = new ConcurrentLinkedQueue<Object>();		
		
		logThread = new Thread(new LogRunnable(this));
		logThread.start();
	}
	
	
	public BufferedReader getIn() {
		return in;
	}
	
	
	public BufferedWriter getOut() {
		return out;
	}
	
	
	public InputStream getIn_stream() {
		return in_stream;
	}


	public OutputStream getOut_stream() {
		return out_stream;
	}
	
	
	public void initLogging(IUCIConfig engineBootCfg) throws FileNotFoundException {
		//Do nothing
		//dump = System.
	}
	
	
	protected void dumpInitialLines() {
		dump.println(NEW_LINE);
		dump.println(NEW_LINE);
		dump.println("Time: " + new Date());
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.uci.impl.IChannel#close()
	 */
	@Override
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
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.uci.impl.IChannel#sendLogToGUI(java.lang.String)
	 */
	@Override
	public void sendLogToGUI(String command) {
		
		if (!DEBUGUCI.DEBUG_MODE) return;
		
		try {
			out.write("LOG " + command + NEW_LINE);
			dump("TO_GUI{" + new Date() + "}>" + " LOG " + command + NEW_LINE);
			out.flush();
		} catch (IOException e) {
		}
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.uci.impl.IChannel#sendCommandToGUI(java.lang.String)
	 */
	@Override
	public void sendCommandToGUI(String command) throws IOException {
		sendCommandToGUI_no_newline(command + NEW_LINE);
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.uci.impl.IChannel#sendCommandToGUI_no_newline(java.lang.String)
	 */
	@Override
	public void sendCommandToGUI_no_newline(String command) throws IOException {
		out.write(command);
		dump("TO_GUI{" + new Date() + "}>" + command + NEW_LINE);
		out.flush();
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.uci.impl.IChannel#receiveCommandFromGUI()
	 */
	@Override
	public String receiveCommandFromGUI() throws IOException {
		String command = in.readLine();
		dump("FROM_GUI{" + new Date() + "}>" + command + NEW_LINE);
		return command;
	}
	
	
	public void dump(String message) {
		
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
	
	
	public void dump(Throwable t) {
		dumps.add(t);
		
		/*t.printStackTrace(dump);
		dump.flush();*/
	}
	
	
	private static class LogRunnable implements Runnable {
		
		Channel_Base channel;
		
		private LogRunnable(Channel_Base _channel) {
			channel = _channel;
		}
		
		
		@Override
		public void run() {
			while (true) {
				try {
					if (channel.dump == null) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					} else {
						Object cur = channel.dumps.poll();
						if (cur != null) {
							if (cur instanceof Throwable) {
								
								sendLines(getLines(getStackTrace((Throwable) cur)));
								//((Throwable)cur).printStackTrace(channel.dump);
								channel.dump.flush();
								
							} else {
								
								if (!(cur instanceof String)) {
									throw new IllegalStateException("!(cur instanceof String): cur=" + cur);
								}
								
								sendLines(getLines((String)cur));
								//channel.dump.write((((String)cur).trim() + NEW_LINE).getBytes());
								channel.dump.flush();
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
		
		
		private List<String> getLines(String msg) {
			
			msg = msg.trim();
			
			List<String> lines = new ArrayList<String>();  
			StringTokenizer st = new StringTokenizer(msg, "\r\n");
			while (st.hasMoreTokens()) {
				lines.add(st.nextToken());
			}
			
			return lines;
		}
		
		
		private void sendLines(List<String> lines) throws IOException {
			for (int i=0; i <lines.size(); i++) {
				String line = "info string " + lines.get(i) + "\r\n";
				channel.dump.write(line.getBytes());
			}
		}
		
		
		private String getStackTrace(Throwable throwable) {
		     final StringWriter sw = new StringWriter();
		     final PrintWriter pw = new PrintWriter(sw, true);
		     while (throwable != null) {
		    	 throwable.printStackTrace(pw);
		    	 throwable = throwable.getCause();
		     }
		     return sw.getBuffer().toString();
		}
	}
}

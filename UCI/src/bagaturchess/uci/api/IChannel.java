package bagaturchess.uci.api;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;


public interface IChannel {
	
	
	public static final String NEW_LINE = "\r\n";
	public static final String WHITE_SPACE = " ";
	
	
	public abstract void close();

	public abstract void sendLogToGUI(String command);

	public abstract void sendCommandToGUI(String command) throws IOException;

	public abstract void sendCommandToGUI_no_newline(String command)
			throws IOException;

	public abstract String receiveCommandFromGUI() throws IOException;

	public abstract void dump(String string);

	public abstract void dump(Throwable t);

	public void initLogging(IUCIConfig engineBootCfg) throws FileNotFoundException;
	
	public BufferedReader getIn();

	public BufferedWriter getOut();
}
package bagaturchess.uci.remote;

import java.net.Socket;
import java.io.*;

import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.Channel_Remote;
import bagaturchess.uci.run.Boot;

/**
 *
 */
public class ClientProcessor implements Runnable {
	
	
	private static final int STRING_BUFFER_LENGTH = 255;
	
	
	private Socket mClientSocket;
	private BufferedReader mReader;
	private BufferedWriter mOutput;
	private StringBuffer mBuffer;
	
	private String mCanonicalHostName;
	
	
	public ClientProcessor(Socket aClientSocket, String aCanonicalHostName) {

		mClientSocket = aClientSocket;
		mCanonicalHostName = aCanonicalHostName;

		try {

			IChannel communicationChanel = new Channel_Remote(mClientSocket.getInputStream(), mClientSocket.getOutputStream());
			ChannelManager.setChannel(communicationChanel);

			mReader = communicationChanel.getIn();
			
			mOutput = communicationChanel.getOut();
			
			mBuffer = new StringBuffer(STRING_BUFFER_LENGTH);
			
		} catch (IOException ioe) {
			close(ioe, "Error while initializing client!");
		}
	}
	
	
	@Override
	public void run() {
		
		String[] args = new String[] {
				"bagaturchess.engines.cfg.base.UCIConfig_BaseImpl",
				"bagaturchess.search.impl.uci_adaptor.UCISearchAdaptorImpl_PonderingOpponentMove",
				"bagaturchess.engines.cfg.base.UCISearchAdaptorConfig_BaseImpl",
				"bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD",
				"bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_1Core",
				"bagaturchess.search.impl.alg.impl1.Search_PVS_NWS",
				"bagaturchess.engines.cfg.base.SearchConfigImpl_AB",
				"bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20",
				"bagaturchess.learning.goldmiddle.impl4.cfg.EvaluationConfig_V20", };

		Boot.runStateManager(args, ChannelManager.getChannel());
	}
	
	
	private void close(Exception aException, String aMessage) {
		if (aException != null) {
			aException.printStackTrace();
		}
		System.out.println(aMessage);
		close();
	}
	
	
	public void close() {
		if (mReader != null) {
			try {
				mReader.close();
			} catch (IOException e) {
			}
		}
		if (mOutput != null) {
			try {
				mOutput.close();
			} catch (IOException e) {
			}
		}
		if (mClientSocket != null) {
			try {
				mClientSocket.close();
			} catch (IOException e) {
			}
		}
		mReader = null;
		mOutput = null;
		mClientSocket = null;
	}
}

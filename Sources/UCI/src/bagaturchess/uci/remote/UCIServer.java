package bagaturchess.uci.remote;


import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;


public class UCIServer {
	
	
	private static final int PORT_LISTENING = 333;

	private ServerSocket mServer;
	private ThreadManager mThreadManager;
	
	
	public UCIServer() {
		try {
			System.out.println("");
			mServer = new ServerSocket(PORT_LISTENING);
			mThreadManager = new ThreadManager();
			System.out.println("Bagatur UCI Server listening on port "
					+ PORT_LISTENING);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Cannot instantiate server socket!");
			System.exit(-1);
		}
	}
	
	
	public void start() {
		
		while (true) {
			
			try {
				
				System.out.println("\r\nWaiting client ... ");
				
				Socket clientSocket = mServer.accept();
				
				String canonicalHostName = clientSocket.getInetAddress()
						.getCanonicalHostName();
				
				ClientProcessor client = new ClientProcessor(clientSocket,
						canonicalHostName);
				
				mThreadManager.process(client);
				System.out.println("Client " + canonicalHostName + " accepted!");
				
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed when accepting client!");
			}
		}
	}
}

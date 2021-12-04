package bagaturchess.uci.api;


public class ChannelManager {
	
	private static IChannel channel;
	
	public static IChannel getChannel() {
		/*if (channel == null) {
			System.err.println("Channel not initialized!");
		}*/
		return channel;
	}
	
	public static void setChannel(IChannel _channel) {
		if (channel != null) {
			System.err.println("Channel already initialized!");
		}
		channel = _channel;
	}
}

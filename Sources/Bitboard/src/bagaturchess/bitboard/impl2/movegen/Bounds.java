package bagaturchess.bitboard.impl2.movegen;

import bagaturchess.bitboard.common.Utils;

public class Bounds {
	public static final byte[] LEFT			= Utils.reverseSpecial(new byte[] {
			1,   0,   0,   0,   0,   0,   0,   0,   
			1,   0,   0,   0,   0,   0,   0,   0,   
			1,   0,   0,   0,   0,   0,   0,   0,   
			1,   0,   0,   0,   0,   0,   0,   0,   
			1,   0,   0,   0,   0,   0,   0,   0,   
			1,   0,   0,   0,   0,   0,   0,   0,   
			1,   0,   0,   0,   0,   0,   0,   0,   
			1,   0,   0,   0,   0,   0,   0,   0,   
			});
	
	public static final byte[] RIGHT			= Utils.reverseSpecial(new byte[] {
			0,   0,   0,   0,   0,   0,   0,   1,   
			0,   0,   0,   0,   0,   0,   0,   1,   
			0,   0,   0,   0,   0,   0,   0,   1,   
			0,   0,   0,   0,   0,   0,   0,   1,   
			0,   0,   0,   0,   0,   0,   0,   1,   
			0,   0,   0,   0,   0,   0,   0,   1,   
			0,   0,   0,   0,   0,   0,   0,   1,   
			0,   0,   0,   0,   0,   0,   0,   1,   
			});
	
	public static final byte[] UP			= Utils.reverseSpecial(new byte[] {
			1,   1,   1,   1,   1,   1,   1,   1,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0, 
			});
	
	public static final byte[] DOWN			= Utils.reverseSpecial(new byte[] {
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			0,   0,   0,   0,   0,   0,   0,   0,   
			1,   1,   1,   1,   1,   1,   1,   1,   
			});
}

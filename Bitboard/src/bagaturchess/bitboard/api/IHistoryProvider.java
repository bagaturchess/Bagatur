package bagaturchess.bitboard.api;


public interface IHistoryProvider {

	public int getScores(int color, int move);
	
	public int getCounter1(int color, int parentMove);
	public int getCounter2(int color, int parentMove);
	
	public int getKiller1(int color, int ply);
	public int getKiller2(int color, int ply);
}

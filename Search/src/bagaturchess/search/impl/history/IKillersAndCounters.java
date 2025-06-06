package bagaturchess.search.impl.history;


public interface IKillersAndCounters {

	public void clear();
	
	//Counter moves
	public void addCounterMove(int color, int last_move, int counter_move);
	public int getCounter1(int color, int parentMove);
	public int getCounter2(int color, int parentMove);
	
	
	//Killer moves
	public void addKillerMove(int color, int move, int ply);
	public int getKiller1(int color, int ply);
	public int getKiller2(int color, int ply);
}

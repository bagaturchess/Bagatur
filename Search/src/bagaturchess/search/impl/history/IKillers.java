package bagaturchess.search.impl.history;


public interface IKillers {

	public void clear();	
	
	public void addKillerMove(int color, int move, int ply);
	public int getKiller1(int color, int ply);
	public int getKiller2(int color, int ply);
}

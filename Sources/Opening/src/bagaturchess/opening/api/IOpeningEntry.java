package bagaturchess.opening.api;

public interface IOpeningEntry {
	
	public int getRandomEntry();
	
	public int getMostPlayedEntry();
	
	public long getHashkey();

	public int getWeight();

	public int[] getMoves();
	
	public int[] getCounts();
}

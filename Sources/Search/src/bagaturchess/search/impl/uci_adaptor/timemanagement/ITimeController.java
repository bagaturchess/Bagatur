package bagaturchess.search.impl.uci_adaptor.timemanagement;


public interface ITimeController {
	
	public void newIteration();
	public void newPVLine(int eval, int depth, int bestMove);
	
	public long getStartTime();
	public boolean hasTime();
	public long getRemainningTime();
}

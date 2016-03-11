package bagaturchess.search.impl.uci_adaptor.timemanagement;

public interface ITimeController {
	public boolean hasTime(int futureShift);
	public void newIteration();
	public void newPVLine(int eval, int depth, int bestMove);
	public long getStartTime();
}

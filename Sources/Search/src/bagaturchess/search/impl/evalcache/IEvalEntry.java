package bagaturchess.search.impl.evalcache;


public interface IEvalEntry {
	public boolean isEmpty();
	public int getEval();
	public byte getLevel();
}

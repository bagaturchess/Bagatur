package bagaturchess.search.impl.eval.cache;

public interface IEvalCache {
	public void get(long key, IEvalEntry entry);
	public void put(long hashkey, int level, double eval);
	public int getHitRate();
}

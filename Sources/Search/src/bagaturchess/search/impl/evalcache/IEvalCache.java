package bagaturchess.search.impl.evalcache;

public interface IEvalCache {
	public IEvalEntry get(long key);
	public void put(long hashkey, int level, double eval);
	public int getHitRate();
	public void lock();
	public void unlock();
}

package bagaturchess.search.impl.evalcache;

public interface IEvalCache {
	public IEvalEntry get(long key);
	//public void put(long hashkey, int eval, boolean sketch);
	public void put(long hashkey, double eval, boolean sketch);
	public void put(long hashkey, int level, double eval, int alpha, int beta);
	public void clear();
	public int getHitRate();
	public void lock();
	public void unlock();
}

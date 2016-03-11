package bagaturchess.search.impl.evalcache;

public interface IEvalEntry {

	public int getEval();

	public boolean isSketch();

	public int getLevel();

	public int getLowerBound();
	
	public int getUpperBound();
}

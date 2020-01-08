package bagaturchess.search.impl.rootsearch.sequential.mtd;

import java.util.List;

public interface IBetaGenerator {

	public abstract void decreaseUpper(int val);

	public abstract void increaseLower(int val);

	public abstract List<Integer> genBetas();

	public abstract int getLowerBound();

	public abstract int getUpperBound();
	
	public abstract String toString();

}
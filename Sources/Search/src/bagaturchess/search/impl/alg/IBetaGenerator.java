package bagaturchess.search.impl.alg;

import java.util.List;

public interface IBetaGenerator {

	public abstract void decreaseUpper(int val);

	public abstract void increaseLower(int val);

	public abstract List<Integer> genBetas();

	public abstract int getLowerBound();

	public abstract int getUpperBound();

	public abstract boolean hasLowerBound();

	public abstract boolean hasUpperBound();
	
	public abstract String toString();

}
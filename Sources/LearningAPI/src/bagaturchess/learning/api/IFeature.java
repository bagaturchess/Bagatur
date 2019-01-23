package bagaturchess.learning.api;

import java.io.Serializable;


public interface IFeature extends Comparable<IFeature>, Serializable {
	
	public int getId();
	public int getComplexity();
	public String getName();
	
	public ISignal createNewSignal();
	public void adjust(ISignal signal, double amount, double openningPart);
	public double eval(ISignal signal, double openningPart);
	public void applyChanges();
	public void clear();
	
	public int compareTo(IFeature f);	
	public int hashCode();
	public boolean equals(Object obj);
	public String toJavaCode();

}

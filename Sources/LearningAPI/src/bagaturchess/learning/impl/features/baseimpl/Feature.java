package bagaturchess.learning.impl.features.baseimpl;


import bagaturchess.learning.api.IFeature;


public abstract class Feature implements IFeature {
	
	
	private static final long serialVersionUID = 3377772568019961052L;
	
	
	private int id;
	private String name;
	private int complexity;
	
	
	protected Feature(int _id, String _name, int _complexity) {
		id = _id;
		name = _name;
		complexity = _complexity;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public int getComplexity() {
		return complexity;
	}
	
	public int compareTo(IFeature f) {
		return id - f.getId();
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof IFeature) {
			IFeature f = (IFeature) obj;
			if (f.getId() == id) {
				
				if (!f.getName().equals(getName())) {
					throw new IllegalStateException("f.getName()=" + f.getName() + ", getName()=" + getName());
				}
				if (f.getComplexity() != getComplexity()) {
					throw new IllegalStateException("f.getComplexity()=" + f.getComplexity() + ", getComplexity()=" + getComplexity());
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	
	protected void merge(Feature other) {
		if (other.complexity != complexity) complexity = other.complexity;
		if (!other.name.equals(name)) name = other.name;
	}
}

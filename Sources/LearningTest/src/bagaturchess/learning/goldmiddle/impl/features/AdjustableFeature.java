package bagaturchess.learning.goldmiddle.impl.features;


import bagaturchess.learning.goldmiddle.api.IAdjustableFeature;


abstract class AdjustableFeature extends Feature implements IAdjustableFeature {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8513672919455948831L;
	
	
	protected AdjustableFeature(int _id, String _name, int _complexity) {
		super(_id, _name, _complexity);
	}
	
	
	/*protected void merge(AdjustableFeature other) {
		if (other.complexity != complexity) complexity = other.complexity;
		if (!other.name.equals(name)) name = other.name;
	}*/
}

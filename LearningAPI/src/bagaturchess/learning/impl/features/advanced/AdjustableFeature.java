package bagaturchess.learning.impl.features.advanced;


import bagaturchess.learning.api.IAdjustableFeature;
import bagaturchess.learning.impl.features.baseimpl.Feature;


abstract class AdjustableFeature extends Feature implements IAdjustableFeature {
	
	
	private static final long serialVersionUID = 8513672919455948831L;
	
	
	protected AdjustableFeature(int _id, String _name, int _complexity) {
		
		super(_id, _name, _complexity);
	}
}

package bagaturchess.learning.impl.features.advanced;


import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.IFeaturesMerger;


public class FeaturesMerger implements IFeaturesMerger {

	@Override
	public void merge(IFeature f1, IFeature f2) {
		if (f1.getId() != f2.getId()) {
			throw new IllegalStateException();
		}
		if (f1 instanceof AdjustableFeatureSingle) {
			((AdjustableFeatureSingle)f1).merge((AdjustableFeatureSingle)f2);
		}
	}

}

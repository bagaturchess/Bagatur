package bagaturchess.learning.goldmiddle.run;

import bagaturchess.learning.impl.features.Features;

public class DumpFeatures {

	
	public static void main(String[] args) {
		Features f = Features.load();
		if (f == null) {
			System.out.println("Cannot load features from file");
		} else {
			Features.dump(f.getFeatures());
		}
	}

}

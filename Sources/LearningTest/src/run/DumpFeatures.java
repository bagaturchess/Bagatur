package run;

import bagaturchess.learning.impl.features.impl1.Features;

public class DumpFeatures {

	
	public static void main(String[] args) {
		Features f = Features.load();
		f.dump(f.getFeatures());
	}

}

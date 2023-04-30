package bagaturchess.search.impl.rootsearch.sequential.mtd;

public class BetaGeneratorFactory {
	
	public static IBetaGenerator create(int _initialVal, int min_interval) {
		return new BetaGenerator(_initialVal, 1, min_interval);
		//return new BetaGenerator_new(_initialVal, _betasCount, min_interval);
		//return new BetaGenerator2(_initialVal, _betasCount, min_interval);
	}
}

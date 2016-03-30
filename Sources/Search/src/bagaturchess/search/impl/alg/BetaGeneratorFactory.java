package bagaturchess.search.impl.alg;

public class BetaGeneratorFactory {

	public static IBetaGenerator create(int _initialVal, int _betasCount) {
		return create(_initialVal, _betasCount, 16);
	}
	
	public static IBetaGenerator create(int _initialVal, int _betasCount, int min_interval) {
		//return new BetaGenerator(_initialVal, _betasCount, min_interval);
		return new BetaGenerator2(_initialVal, _betasCount, min_interval);
		//return new BetaGenerator1(_initialVal, _betasCount);
	}
}

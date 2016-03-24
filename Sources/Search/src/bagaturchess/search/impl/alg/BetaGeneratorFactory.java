package bagaturchess.search.impl.alg;

public class BetaGeneratorFactory {

	public static IBetaGenerator create(int _initialVal, int _betasCount) {
		//return new BetaGenerator(_initialVal, _betasCount);
		//return new BetaGenerator1(_initialVal, _betasCount);
		return new BetaGenerator2(_initialVal, _betasCount);
	}
}

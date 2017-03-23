package bagaturchess.engines.bagatur.v15;


import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;


public class PawnsEvalFactory_V15 implements DataObjectFactory<PawnsModelEval> {

	public PawnsModelEval createObject() {
		return new PawnsEval_V15();
	}

}

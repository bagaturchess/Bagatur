package bagaturchess.engines.bagatur.eval;


import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;


public class BagaturPawnsEvalFactory implements DataObjectFactory<PawnsModelEval> {

	public PawnsModelEval createObject() {
		return new BagaturPawnsEval();
	}

}

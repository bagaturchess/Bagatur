package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.eval;


import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;


public class BagaturPawnsEvalFactory implements DataObjectFactory<PawnsModelEval> {

	public PawnsModelEval createObject() {
		return new BagaturPawnsEval();
	}

}

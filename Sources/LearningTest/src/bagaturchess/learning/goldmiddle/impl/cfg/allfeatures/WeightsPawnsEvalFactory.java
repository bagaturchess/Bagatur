package bagaturchess.learning.goldmiddle.impl.cfg.allfeatures;


import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;


public class WeightsPawnsEvalFactory implements DataObjectFactory<PawnsModelEval> {

	public PawnsModelEval createObject() {
		return new WeightsPawnsEval();
	}

}

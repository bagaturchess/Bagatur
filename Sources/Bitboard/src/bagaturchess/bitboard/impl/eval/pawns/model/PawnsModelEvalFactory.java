package bagaturchess.bitboard.impl.eval.pawns.model;


import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;


public class PawnsModelEvalFactory implements DataObjectFactory<PawnsModelEval> {

	public PawnsModelEval createObject() {
		return new PawnsModelEval();
	}

}
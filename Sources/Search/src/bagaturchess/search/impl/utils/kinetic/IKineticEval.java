package bagaturchess.search.impl.utils.kinetic;

import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;

public interface IKineticEval {

	public int getMaterialQueen();
	public int notMovedPenalty(int fieldID);
	public int evalBeforeMove(ISearchMediator mediator, ISearchInfo info, int depth, int maxdepth, int move);
	public int evalAfterMove(ISearchMediator mediator, ISearchInfo info, int depth, int maxdepth, int move);
	public int evalPosition(ISearchMediator mediator, ISearchInfo info, int depth, int maxdepth);
	//public int evalMove(ISearchMediator mediator, ISearchInfo info, int depth, int maxdepth, int move);
	//public boolean moveOK(ISearchMediator mediator, ISearchInfo info, int depth, int maxdepth, int move);
	
}

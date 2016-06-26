package bagaturchess.search.impl.rootsearch.sequential;


import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.impl.tpt.TPTable;
import bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_NormalSearch;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.api.ITimeConfig;
import bagaturchess.uci.impl.commands.Go;


public class DualSearchMediatorImpl extends UCISearchMediatorImpl_NormalSearch {
	
	
	public DualSearchMediatorImpl(IChannel _channel, Go _go,
			ITimeConfig _timeConfig, int _colourToMove,
			BestMoveSender _sender, TPTable _tpt, IRootSearch _rootSearch) {
		super(_channel, _go, _timeConfig, _colourToMove, _sender, _rootSearch, false);
	}
	
	@Override
	public void changedMajor(ISearchInfo info) { 
		//timeController.newPVLine(info.getEval(), info.getDepth(), info.getBestMove());
		super.changedMajor(info);
	}
}

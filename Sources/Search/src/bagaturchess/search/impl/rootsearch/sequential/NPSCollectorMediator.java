package bagaturchess.search.impl.rootsearch.sequential;


import java.util.ArrayList;
import java.util.List;


import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.utils.SearchMediatorProxy;
import bagaturchess.uci.api.ChannelManager;


public class NPSCollectorMediator extends SearchMediatorProxy {
	
	
	private List<ISearchInfo> infos;
	
	private long startTime = System.currentTimeMillis();
	
	
	public NPSCollectorMediator(ISearchMediator _parent) {
		super(_parent);
		
		infos = new ArrayList<ISearchInfo>();
	}


	@Override
	public void registerInfoObject(ISearchInfo info) {
		synchronized (infos) {
			infos.add(info);
		}
	}
	
	
	//Do not change original info object
	public synchronized void changedMajor(ISearchInfo info) {
		
		ISearchInfo result = SearchInfoFactory.getFactory().createSearchInfo();
		ISearchInfo minor = combineMinorInfos(result);
		
		//long secs = Math.max(1, (System.currentTimeMillis() - startTime) / 1000);
		//ChannelManager.getChannel().sendLogToGUI(Thread.currentThread().getName() + " NPS: " + (minor.getSearchedNodes() / secs));
		
		minor.setPV(info.getPV());
		minor.setBestMove(info.getBestMove());
		minor.setEval(info.getEval());
		minor.setLowerBound(info.isLowerBound());
		minor.setUpperBound(info.isUpperBound());
		
		super.changedMajor(minor);
	}
	
	
	//Do not change original info object
	//Collects info objects from all parallel searchers
	public synchronized void changedMinor(ISearchInfo info) {
		
		ISearchInfo result = SearchInfoFactory.getFactory().createSearchInfo();
		ISearchInfo minor = combineMinorInfos(result);
		
		minor.setCurrentMove(info.getCurrentMove());
		minor.setCurrentMoveNumber(info.getCurrentMoveNumber());
		
		super.changedMinor(minor);
	}
	
	
	private ISearchInfo combineMinorInfos(ISearchInfo result) {
		
		result.setSearchedNodes(0);
		result.setDepth(0);
		result.setSelDepth(0);
		
		synchronized (infos) {
			
			for (int i = 0; i < infos.size(); i++) {
				
				ISearchInfo cur = infos.get(i);
				
				result.setSearchedNodes(result.getSearchedNodes() + cur.getSearchedNodes());
				
				result.setTBhits(result.getTBhits() + cur.getTBhits());
				
				if (cur.getDepth() > result.getDepth()) {
					result.setDepth(cur.getDepth());
				}
				if (cur.getSelDepth() > result.getSelDepth()) {
					result.setSelDepth(cur.getSelDepth());
				}
			}
		}
		
		return result;
	}
}

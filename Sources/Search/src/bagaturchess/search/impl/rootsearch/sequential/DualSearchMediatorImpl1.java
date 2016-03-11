package bagaturchess.search.impl.rootsearch.sequential;


import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.utils.SearchMediatorProxy;


public class DualSearchMediatorImpl1 extends SearchMediatorProxy {


	private ISearchInfo lastinfo;
	
	
	public DualSearchMediatorImpl1(ISearchMediator _parent) {
		super(_parent);
	}

	
	@Override
	public ISearchInfo getLastInfo() {
		return lastinfo;
	}

	
	@Override
	public void changedMajor(ISearchInfo info) {
		lastinfo = info;
	}
}

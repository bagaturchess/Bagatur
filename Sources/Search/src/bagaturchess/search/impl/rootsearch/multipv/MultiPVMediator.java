package bagaturchess.search.impl.rootsearch.multipv;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.api.IFinishCallback;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInfoUtils;
import bagaturchess.search.api.internal.SearchInterruptedException;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.utils.SearchMediatorProxy;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.commands.Go;


public class MultiPVMediator extends SearchMediatorProxy implements IFinishCallback {
	
	
	private long startTime = System.currentTimeMillis();
	
	private IRootSearchConfig engineConfiguration;
	private IRootSearch rootSearch;
	private IBitBoard bitboard;
	private int startIteration;
	private int maxIterations;
	private Go go;
	
	private List<MultiPVEntry> multiPVs_current;
	
	private int moves_count;
	private int cur_PVNumber;
	private int cur_depth;
	
	private ISearchInfo lastinfo;
	private ISearchInfo bestinfo;
	
	private Set<ISearchInfo> infos;
	
	private ISearchStopper stopper;
	
	
	public MultiPVMediator(IRootSearchConfig _engineConfiguration, IRootSearch _rootSearch, IBitBoard _bitboard, ISearchMediator _parentMediator, Go _go) {
		
		super(_parentMediator);
		
		stopper = _parentMediator.getStopper();
		
		engineConfiguration = _engineConfiguration;
		rootSearch = _rootSearch;
		bitboard = _bitboard;
		go = _go;

		startIteration = go.getStartDepth();
		maxIterations = go.getDepth();
		
		multiPVs_current = setupMultiPVs();
		moves_count = multiPVs_current.size();
		
		cur_PVNumber = -1;
		cur_depth = startIteration;
		
		infos = new HashSet<ISearchInfo>();
	}
	
	
	int getCurrentDepth() {
		return cur_depth;
	}
	
	
	private List<MultiPVEntry> setupMultiPVs() {
		IMoveList moves = new BaseMoveList();
		
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(moves);
		} else {
			bitboard.genAllMoves(moves);
		}
		
		List<MultiPVEntry> multipvs = new ArrayList<MultiPVEntry>();
		
		int cur_move = 0;
		while ((cur_move = moves.next()) != 0) {
			MultiPVEntry cur_multipv = new MultiPVEntry(cur_move);
			multipvs.add(cur_multipv);
		}
		return multipvs;
	}
	
	
	@Override
	public synchronized void ready() {
		
		//ChannelManager.getChannel().dump("READY CALLED");
		
		if (cur_depth > 1 && stopper.isStopped()) {
			return;
		}
		
		if (cur_PVNumber != -1) { //Not first move of first iteration
			
			MultiPVEntry curEntry = getCurrentPVEntry();
			curEntry.setInfo(lastinfo);
		}
		
		
		if (cur_PVNumber == moves_count - 1) {
			
			//ChannelManager.getChannel().dump("READY CALLED: cur_PVNumber == moves_count - 1");
			
			//Get all PVs of this iteration
			MultiPVEntry[] pvsToSend = orderPVsForSending();
			
			bestinfo = pvsToSend[0].getInfo();
			super.changedMajor(bestinfo);
			
			ISearchInfo nodesInfo = combineMinorInfos();
			
			//Send best lines
			for (int i=0; i<pvsToSend.length; i++) {
				
				pvsToSend[i].getInfo().setSearchedNodes(nodesInfo.getSearchedNodes());
				
				pvsToSend[i].getInfo().setTBhits(nodesInfo.getTBhits());
				
				String message = SearchInfoUtils.buildMajorInfoCommand_multipv(i+1, pvsToSend[i].getInfo(),
						startTime, 0 /*sharedData.getTPT().getUsage()*/, 0 /*sharedData.getTPT().getCount_UniqueInserts()*/, bitboard);
				send(message);
			}
			
			if (cur_depth == maxIterations) {
				cur_depth++;//Increase it as the checks outside of this class could determine that the search has to stop if go command has max depth set
				return;
			} else if (cur_depth < maxIterations) {
				cur_depth++;
				cur_PVNumber = 0;
			} else {
				throw new IllegalStateException("cur_depth=" + cur_depth);
			}
		} else if (cur_PVNumber < moves_count - 1) {
			//ChannelManager.getChannel().dump("READY CALLED: cur_PVNumber < moves_count - 1");
			cur_PVNumber++;
		} else {
			throw new IllegalStateException("cur_PVNumber=" + cur_PVNumber);
		}
		
		startNextSearch();
	}
	
	
	@Override
	public synchronized void changedMinor(ISearchInfo info) {
		
		infos.add(info);
		
		ISearchInfo minor = combineMinorInfos();
		minor.setCurrentMove(getCurrentPVEntry().getMove());
		minor.setCurrentMoveNumber(cur_PVNumber + 1);
		
		minor.setDepth(cur_depth + 1);
		
		super.changedMinor(minor);
	}
	
	
	@Override
	public synchronized void changedMajor(ISearchInfo info) {
		
		infos.add(info);
		
		ISearchInfo minor = combineMinorInfos();
		
		minor.setPV(info.getPV());
		minor.setBestMove(info.getBestMove());
		minor.setEval(info.getEval());
		
		minor.setDepth(cur_depth + 1);
		
		lastinfo = minor;
	}
	
	
	private ISearchInfo combineMinorInfos() {
		
		ISearchInfo result = SearchInfoFactory.getFactory().createSearchInfo();
		
		for (ISearchInfo cur: infos) {
			
			result.setSearchedNodes(result.getSearchedNodes() + cur.getSearchedNodes());
			
			result.setTBhits(result.getTBhits() + cur.getTBhits());
			
			if (cur.getDepth() > result.getDepth()) {
				result.setDepth(cur.getDepth());
			}
			if (cur.getSelDepth() > result.getSelDepth()) {
				result.setSelDepth(cur.getSelDepth());
			}
		}
		return result;
	}
	
	
	@Override
	public ISearchInfo getLastInfo() {
		return bestinfo;
	}
	
	
	private MultiPVEntry getCurrentPVEntry() {
		int counter = 0;
		for (MultiPVEntry cur: multiPVs_current) {
			if  (counter == cur_PVNumber) {
				return cur;
			}
			counter++;
		}
		throw new IllegalStateException("counter=" + counter + ", cur_PVNumber=" + cur_PVNumber);
	}
	
	
	private void startNextSearch() {
		try {
			
			//rootSearch.stopSearchAndWait();
			//ChannelManager.getChannel().dump("before search");
			
			this.setStopper(stopper);//Revert to original stopper
			
			bitboard.makeMoveForward(getCurrentPVEntry().getMove());
			//adjust go: set depth to cur_depth variable
			go.setStartDepth(0);
			go.setDepth(cur_depth);
			rootSearch.negamax(bitboard, this, null, this, go);
			bitboard.makeMoveBackward(getCurrentPVEntry().getMove());
			//ChannelManager.getChannel().dump("after search");
			
		} catch(SearchInterruptedException sie) {
			//Do Nothing
		} catch(Throwable t) {
			ChannelManager.getChannel().dump(t);
		}
	}
	
	
	private MultiPVEntry[] orderPVsForSending() {
		
		Set<MultiPVEntry> multipvs_cur_set = new TreeSet<MultiPVEntry>(); 
		multipvs_cur_set.addAll(multiPVs_current);
				
		MultiPVEntry[] pvsToSend = new MultiPVEntry[Math.min(engineConfiguration.getMultiPVsCount(), multipvs_cur_set.size())];
		int pvnum = 0;
		for (MultiPVEntry cur_multipv: multipvs_cur_set) {
			if (pvnum >= engineConfiguration.getMultiPVsCount()) {
				break;
			}
			pvsToSend[pvnum] = cur_multipv;
			if (cur_multipv == null) {
				throw new IllegalStateException("cur_multipv is null");
			}
			if (cur_multipv.getInfo() == null) {
				throw new IllegalStateException("cur_multipv.getInfo() is null");
			}
			pvnum++;
		}

		//Setup the order for the next iteration
		multiPVs_current = new ArrayList<MultiPVEntry>();
		multiPVs_current.addAll(multipvs_cur_set);
		
		return pvsToSend;
	}
}

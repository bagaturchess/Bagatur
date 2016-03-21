package bagaturchess.search.impl.alg.impl_staticsearch;


import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.IRootWindow;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.alg.SearchImpl.RootWindowImpl;
import bagaturchess.search.impl.utils.kinetic.IKineticEval;
import bagaturchess.uci.api.BestMoveSender;


public class SearchKineticEval implements IKineticEval {

	
	private SearchMTD_Static search;
	
	
	SearchKineticEval(SearchMTD_Static _search) {
		search = _search;
	}
	
	//ISearchInfo info1 = SearchInfoFactory.getFactory().createSearchInfo();
	ISearchMediator mediator1 = new ISearchMediator() {
		
		@Override
		public void startIteration(int iteration) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void send(String msg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void registerInfoObject(ISearchInfo info) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public ISearchStopper getStopper() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public ISearchInfo getLastInfo() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public BestMoveSender getBestMoveSender() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void dump(Throwable t) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void dump(String msg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void changedMinor(ISearchInfo info) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void changedMajor(ISearchInfo info) {
			// TODO Auto-generated method stub
			
		}
	};
	
	IRootWindow rootWin = new RootWindowImpl();
	
	@Override
	public int getMaterialQueen() {
		return search.getEnv().getEval().getMaterialQueen();
	}
	
	@Override
	public int evalPosition(ISearchMediator mediator, ISearchInfo info,
			int depth, int maxdepth) {
		//int eval = nullwin_qsearch(mediator, info, depth, ISearch.MIN, ISearch.MAX, 0);
		//int eval = nullwin_qsearch(mediator, info, 0, depth, ISearch.MIN, ISearch.MAX, 0, 0, false, -19);
		//int eval = pv_qsearch(mediator, info, 0, depth, alpha, ISearch.MAX, 0, 0, false, -19);

		int rest = maxdepth - depth;
		if (rest < 1 && search.getEnv().getBitboard().isInCheck()) {
			rest = 1;
		}
		
		int eval;
		if (rest > 0) {
			/*int lasteval = (int) search.getEnv().getEval().fullEval(0, ISearch.MIN, ISearch.MAX, search.getEnv().getBitboard().getColourToMove());
			TPTEntry tptEntry = search.getEnv().getTPT().get(search.getEnv().getBitboard().getHashKey());
			if (tptEntry != null && tptEntry.getBestMove_lower() != 0) {
				lasteval = tptEntry.getLowerBound();
			}
			eval = searchMTD(mediator1, info1, rest * PLY, lasteval, alpha, MAX, false);
			*/
			eval = search.pv_search(mediator, rootWin, info, rest * ISearch.PLY, rest * ISearch.PLY, 0, ISearch.MIN, ISearch.MAX, 0, 0, null, false, 0, search.getEnv().getBitboard().getColourToMove(), 0, 0, false, 0, true);
		} else if (rest == 0) {
			throw new UnsupportedOperationException("search.pv_qsearch alpha-beta");
			//eval = search.pv_qsearch(mediator, info, 0, depth, ISearch.MIN, ISearch.MAX, 0, 0, false, -19);
		} else {
			eval = (int) search.getEnv().getEval().fullEval(maxdepth, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, -19);
		}
		
		return eval;
	}
	
	
	@Override
	public int evalBeforeMove(ISearchMediator mediator, ISearchInfo info,
			int depth, int maxdepth, int move) {
		return evalPosition(mediator, info, depth, maxdepth);
	}
	
	
	@Override
	public int evalAfterMove(ISearchMediator mediator, ISearchInfo info,
			int depth, int maxdepth, int move) {
		return evalPosition(mediator, info, depth, maxdepth);
	}

	@Override
	public int notMovedPenalty(int fieldID) {
		return 0;
	}
	
	
	/*
	public int searchMTD(ISearchMediator mediator, ISearchInfo info,
			int maxdepth, int initial_eval, int initial_lower, int initial_upper, boolean useMateDistancePrunning) {
		
		//System.out.println("maxdepth=" + maxdepth);
		
		int beta = initial_eval;
		int lasteval = 0;
		
		//int lower = initial_lower;
		//int upper = initial_upper;
		
		int[] prevPV = null;
		PVHistoryEntry entry = search.getEnv().getPVs().getPV(search.getEnv().getBitboard().getHashKey());
		if (entry != null) {
			prevPV = search.getEnv().getPVs().getPV(search.getEnv().getBitboard().getHashKey()).getPv();
			//System.out.println("prevPV=" + prevPV);
		}
		
		IRootWindow rootWin = new RootWindowImpl();
		
		search.betas.clear();
		BetaGenerator beta_gen = new BetaGenerator(initial_eval, 1);
		beta_gen.increaseLower(initial_lower);
		beta_gen.decreaseUpper(initial_upper);
		
		//System.out.println("initial_lower=" + initial_lower + ", initial_upper=" + initial_upper);
		
		//int searchedMoves = 0;
		//boolean first_time = true;
		//int mtdTrustWin = getSearchConfig().getMTDTrustWindow();
		//System.out.println("getSearchConfig().getMTDTrustWindow()=" + getSearchConfig().getMTDTrustWindow());
		while (beta_gen.getLowerBound() + getSearchConfig().getMTDTrustWindow() < beta_gen.getUpperBound()) {
			
			beta = beta_gen.genBetas().get(0);
			
			//System.out.println("beta=" + beta_gen + ", beta=" + beta);
			
			int eval;
			//if (searchedMoves == 0) {
			//	eval = pv_search(mediator, rootWin, info, maxdepth, maxdepth, 0, beta - 1, beta, 0, 0, prevPV, false, 0, search.getEnv().getBitboard().getColourToMove(), 0, 0, false, 0, useMateDistancePrunning);
			//} else {
				eval = nullwin_search(mediator, info, maxdepth, maxdepth, 0, beta, false, 0, 0, prevPV, search.getEnv().getBitboard().getColourToMove(), 0, 0, false, 0, useMateDistancePrunning);
				if (eval >= beta) {
					eval = pv_search(mediator, rootWin, info, maxdepth, maxdepth, 0, beta - 1, beta, 0, 0, prevPV, false, 0, search.getEnv().getBitboard().getColourToMove(), 0, 0, false, 0, useMateDistancePrunning);
				}
			//}
			//int eval = nullwin_search(mediator, stopper, info, maxdepth, 0, beta, false, 0, 0);
			//eval = pv_search(mediator, rootWin, info, maxdepth, maxdepth, 0, beta - 1, beta, 0, 0, prevPV, false, 0, search.getEnv().getBitboard().getColourToMove(), 0, 0, false, 0, useMateDistancePrunning);
			//int eval = root_search(mediator, stopper, info, maxdepth, 0, beta - 1, beta, getPrevPV());
			
			//System.out.println("eval=" + eval + "	" + MoveInt.movesToString(PVNode.convertPV(PVNode.extractPV(pvman.load(0)))));
			
			
			
			if (eval >= beta) {
				//eval is lower bound
				beta_gen.increaseLower(eval);
				
				//info.setPV(PVNode.convertPV(PVNode.extractPV(pvman.load(0))));
				//info.setBestMove(info.getPV()[0]);
				//info.setEval(eval);
				//info.setHashFull(search.getEnv().getTPT().getUsage());
				
				if (eval > initial_eval) {
					storePrevPV(info);
					//info.setEval(eval);
					//if (SEND_PV && mediator != null) {
					//	if (eval > initial_lower && eval < initial_upper) {
							//mediator.changedMajor(info);
					//	}
					//}
				}
				lasteval = eval;
				
				//int window_size = getWindow(lower, upper, first_time, eval);
				//beta = lower + window_size;
			} else {
				//eval is upper bound
				//eval < beta <=> eval <= beta - 1 <=> eval <= alpha
				beta_gen.decreaseUpper(eval);
				
				//int window_size = getWindow(lower, upper, first_time, eval);
				//beta = upper - window_size;
			}
			
			//searchedMoves++;
			//first_time = false;
		}
		
		if (lasteval <= initial_eval) {
			
			storePrevPV(info);
			info.setEval(lasteval);
			if (SEND_PV && mediator != null) {
				if (lasteval > initial_lower && lasteval < initial_upper) {
				}
			}
		}
		
		return lasteval;
	}
	*/

}

package bagaturchess.search.impl.alg.impl_staticsearch;


import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.alg.impl0.SearchMTD0;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.utils.kinetic.KineticData_Position;
import bagaturchess.search.impl.utils.kinetic.KineticEval;
import bagaturchess.search.impl.utils.kinetic.StaticKineticEval;
import bagaturchess.search.impl.utils.kinetic.VectorUtils;


public class SearchMTD_Static extends SearchMTD0 {
	
	
	private KineticEval kinetic;
	
	
	public SearchMTD_Static(Object[] args) {
		super(args);
		init();
	}
	
	
	public SearchMTD_Static(SearchEnv _env) {
		super(_env);
		init();
	}
	
	
	private void init() {
		
		//kinetic = new KineticEval(getEnv().getBitboard(), new SearchKineticEval(this));
		kinetic = new KineticEval(getEnv().getBitboard(), new StaticKineticEval(env.getBitboard(), env.getEval()));
	}
	
	
	protected int roughEval(int depth, int rootColour) {
		
		int roughEval = env.getEval().roughEval(depth, rootColour);
		
		return roughEval;
	}
	
	
	protected int lazyEval(int depth, int alpha, int beta, int rootColour) {

		boolean inCheck = env.getBitboard().isInCheck();
		
		/*if (inCheck) {
			
		} else {
			KineticData_Position pos = kinetic.iterateAllByColour(null, null, depth, depth + 1);
			//System.out.print(pos);
			int scalar = VectorUtils.calcScalar(pos);
			return scalar;
		}*/
		
		int lazy_eval = env.getEval().lazyEval(depth, alpha, beta, rootColour);
		
		return lazy_eval;
		//return env.getEval().roughEval(depth, rootColour);//(depth, alpha, beta, rootColour);
	}
	
	
	protected int fullEval(int depth, int alpha, int beta, int rootColour) {
		
		boolean inCheck = env.getBitboard().isInCheck();
		
		if (inCheck) {
			
		} else {
			KineticData_Position pos = kinetic.iterateAllByColour(null, null, depth, depth + 1);
			//System.out.print(pos);
			int scalar = VectorUtils.calcScalar(pos);
			return scalar;
		}
		
		int full_eval = (int) env.getEval().fullEval(depth, alpha, beta, rootColour);
		
		return full_eval;
		//return env.getEval().lazyEval(depth, alpha, beta, rootColour);
		//return env.getEval().roughEval(depth, rootColour);//(depth, alpha, beta, rootColour);
	}
	
	
	@Override
	public void search(ISearchMediator mediator, int startIteration, int max_iterations, boolean useMateDistancePrunning) {
		
		/*ISearchInfo info1 = SearchInfoFactory.getFactory().createSearchInfo();
		KineticData_Position my1 = iterateAllByColour(mediator, info1, 0);
		System.out.println("INITIAL PD (my): " + my1);
		System.exit(0);*/
		
		super.search(mediator, startIteration, max_iterations, useMateDistancePrunning);
		if (true) return; 
		
		if (env.getBitboard().isInCheck()) {
			
			super.search(mediator, startIteration, max_iterations, useMateDistancePrunning);
			
			if (mediator.getBestMoveSender() != null) {
				mediator.getBestMoveSender().sendBestMove();
			}
			
		} else {
		
			ISearchInfo info = SearchInfoFactory.getFactory().createSearchInfo();
			mediator.registerInfoObject(info);
			
			mediator.startIteration(1);
			
			info.setDepth(1);
			info.setSelDepth(1);
			
			kineticPositionalEval(mediator, info, 5);	
			
			if (mediator.getBestMoveSender() != null) {
				mediator.getBestMoveSender().sendBestMove();
			}
		}
	}
	
	
	private void kineticPositionalEval(ISearchMediator mediator, ISearchInfo info, int maxdepth) {
		
		//TDODO!!!!FIX!!!!!!
		if (env.getBitboard().isInCheck()) {
			throw new IllegalStateException();
		}
		
		KineticData_Position my1 = kinetic.iterateAllByColour(mediator, info, 0, maxdepth);
		System.out.println("INITIAL PD (my): " + my1);
		
		env.getBitboard().makeNullMoveForward();
		KineticData_Position op1 = kinetic.iterateAllByColour(mediator, info, 1, maxdepth);
		env.getBitboard().makeNullMoveBackward();
		
		System.out.println("INITIAL PD (op): " + op1);
		
		System.out.println();
		
		
		IMoveList list = new BaseMoveList();
		list.clear();
		
		//boolean inCheck = env.getBitboard().isInCheck();
		//if (inCheck) {
			//env.getBitboard().genKingEscapes(list);
		//} else {
			env.getBitboard().genAllMoves(list);
		//}
		
		int best_eval = ISearch.MIN;
		int best_move = 0;
		
		int cur_move = 0;
		while ((cur_move = list.next()) != 0) {
			

			env.getBitboard().makeMoveForward(cur_move);
			
			
			boolean inCheck = env.getBitboard().isInCheck();
			
			KineticData_Position op2 = null;
			KineticData_Position my2 = null;
			
			if (inCheck) {
				
				op2 = kinetic.iterateAllByColour_InCheck(mediator, info, 1, maxdepth);
				my2 = VectorUtils.inverse(op2);
				
			} else {
				
				op2 = kinetic.iterateAllByColour(mediator, info, 1, maxdepth);
				
				env.getBitboard().makeNullMoveForward();
				my2 = kinetic.iterateAllByColour(mediator, info, 2, maxdepth);
				env.getBitboard().makeNullMoveBackward();
			}
			
			env.getBitboard().makeMoveBackward(cur_move);
			
			
			int root_eval = -op2.getRootEval();
			
			
			KineticData_Position my_sum = VectorUtils.calcVector(my1, op1, op2, my2);
			int scalar = VectorUtils.calcScalar(my_sum);
			
			int cur_eval = root_eval;
			
			if (isMateVal(cur_eval)) {
				//Do nothing
			} else {
				//cur_eval += scalar;
				//cur_eval += scalar / 10;
				cur_eval += scalar / 20;				
			}
			
			
			
			if (cur_eval > best_eval) {
				best_eval = cur_eval;
				best_move = cur_move;
				
				info.setPV(new int[] {best_move});
				info.setBestMove(info.getPV()[0]);
				info.setEval(best_eval);
				
				mediator.changedMajor(info);
			}
			
			
			//System.out.println(MoveInt.moveToString(cur_move) + "  >  MY2=" + my2 + ", OP2=" + op2);
			System.out.println(MoveInt.moveToString(cur_move) + "	>	" + cur_eval + "	root_eval=" + root_eval + ", scalar=" + scalar + "	" + my_sum + "  >>>  MY2=" + my2 + ", OP2=" + op2);
			//System.out.println();

		}
	}	
}

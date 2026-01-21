/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.search.impl.alg;


import java.util.Stack;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.egtb.syzygy.SyzygyConstants;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.eval.cache.EvalEntry_BaseImpl;
import bagaturchess.search.impl.eval.cache.IEvalEntry;
import bagaturchess.search.impl.tpt.ITTEntry;
import bagaturchess.search.impl.tpt.TTEntry_BaseImpl;


public abstract class SearchImpl implements ISearch {
	
	
	private ISearchConfig_AB searchConfig;
	
	protected SearchEnv env;
	
	protected IMoveList[] lists_root;
	protected IMoveList[] lists_history;
	protected IMoveList[] lists_static_eval;
	protected IMoveList[] lists_attacks;
	
	protected int[] buff_tpt_depthtracking 		= new int[1];
	
	protected ITTEntry[] tt_entries_per_ply 	= new ITTEntry[ISearch.MAX_DEPTH];
	
	protected int[] gtb_probe_result 			= new int[2];
	
	//Used for Lazy SMP
	protected int root_search_first_move_index 	= 0;

	private static final boolean USE_DTZ_CACHE 	= true;
	
	private IEvalEntry temp_cache_entry;
	
	public long[] search_types_stats 			= new long[17];
	
	protected long[] phases_stats 				= new long[8];
	
	public Stack<Integer> validation_stack 		= new Stack<Integer>();
	
	
	public void setup(IBitBoard bitboardForSetup) {
		
		env.getBitboard().revert();
		
		int count = bitboardForSetup.getPlayedMovesCount();
		int[] moves = bitboardForSetup.getPlayedMoves();
		
		for (int i=0; i<count; i++) {
			
			env.getBitboard().makeMoveForward(moves[i]);
		}
	}
	
	
	public void setRootSearchFirstMoveIndex(int _root_search_first_move_index) {
		
		root_search_first_move_index = _root_search_first_move_index;
	}
	
	
	public SearchImpl(SearchEnv _env) {
		
		env = _env;
		
		boolean onTheFlySorting = false;
		
		lists_root = new IMoveList[MAX_DEPTH];
		for (int i=0; i<lists_root.length; i++) {
			lists_root[i] = env.getMoveListFactory().createListAll_Root(env, i, onTheFlySorting);
		}
		
		lists_history = new IMoveList[MAX_DEPTH];
		for (int i=0; i<lists_history.length; i++) {
			lists_history[i] = env.getMoveListFactory().createListHistory(env, i, onTheFlySorting);
		}
		
		lists_static_eval = new IMoveList[MAX_DEPTH];
		for (int i=0; i<lists_static_eval.length; i++) {
			lists_static_eval[i] = env.getMoveListFactory().createListStaticEval(env, i, onTheFlySorting);
		}
		
		lists_attacks = new IMoveList[MAX_DEPTH];
		for (int i=0; i<lists_attacks.length; i++) {
			lists_attacks[i] = env.getMoveListFactory().createListCaptures(env, onTheFlySorting);
		}
		
		for (int i=0; i<tt_entries_per_ply.length; i++) {
			tt_entries_per_ply[i] = new TTEntry_BaseImpl();
		}
		
		initParams(env.getSearchConfig());
		
		if (USE_DTZ_CACHE) {
	    	
	    	temp_cache_entry = new EvalEntry_BaseImpl();
		}
	}
	
	
	private void initParams(ISearchConfig_AB cfg) {
		
		searchConfig = cfg;
	}
	
	
	public ISearchConfig_AB getSearchConfig() {
		
		return searchConfig;
	}
	
	
	protected static SharedData getOrCreateSearchEnv(Object[] args) {
		
		if (args[2] == null) {
			
			throw new IllegalStateException();
			//return new SharedData(ChannelManager.getChannel(), (IEngineConfig) args[1]);
			
		} else {
			
			return (SharedData) args[2];
		}
	}
	
	
	public void newSearch() {
		
		env.getHistory().clear();
		
		env.getCaptureHistory().clear();
		
		env.getContinuationHistory().clear();
		
		env.getKillers().clear();
	
		getEnv().getEval().beforeSearch();
	}
	
	
	public SearchEnv getEnv() {
		return env;
	}
	
	
	public void newGame() {
		env.clear();
	}
	
	
	public boolean isDraw(boolean isPV) {
		
		if (!isPV && env.getBitboard().getStateRepetition() >= 2) {
			
			return true;
		}
		
		if (env.getBitboard().getStateRepetition() >= 3
				|| env.getBitboard().isDraw50movesRule()) {
			
			return true;
		}
		
		if (!env.getBitboard().hasSufficientMatingMaterial()) {
			
			return true;
		}
		
		
		return false;
	}
	
	
	public int getDrawScores(int root_player_colour) {
		
		return SearchUtils.getDrawScores(getEnv().getBitboard().getMaterialFactor(), root_player_colour);
	}
	
	
	public int eval(final int ply, final int alpha, final int beta, final boolean isPv) {
		
		/*int eval = evaluator.roughEval(ply,  -1);
		
		int error_window = (int) (LAZY_EVAL_MARGIN.getEntropy() + 3 * LAZY_EVAL_MARGIN.getDisperse());
		
		if (eval >= alpha - error_window && eval <= beta + error_window) {
			
			int rough_eval = eval;
			
			eval = evaluator.fullEval(ply, alpha, beta, -1);
			
			int diff = Math.abs(eval - rough_eval);
			
			LAZY_EVAL_MARGIN.addValue(diff);
		}*/
		
		
		int eval = env.getEval().fullEval(ply, alpha, beta, -1);
		
		/*if (isPv || (eval >= alpha - 7 && eval <= beta + 7)) {
			
			eval = getEnv().getEval_NNUE().fullEval(ply, alpha, beta, -1);
			
		}*/
		
		//int material = env.getBitboard().getMaterialFactor().getTotalFactor(); //In [0-62]
		
		//eval = eval * (194 + material) / 256;
		
		eval = Math.min(Math.max(IEvaluator.MIN_EVAL, eval), IEvaluator.MAX_EVAL);
		
		if (!env.getBitboard().hasSufficientMatingMaterial(env.getBitboard().getColourToMove())) {
			
			eval = Math.min(getDrawScores(-1), eval);
		}
		
		
		return eval;
	}
	
	
	public boolean isRecaptureSameSquare(int move) {
		
		int lastMove = env.getBitboard().getLastMove();
		
		if (lastMove == 0) return false;
	    
		if (!env.getBitboard().getMoveOps().isCapture(lastMove)) return false;

		if (!env.getBitboard().getMoveOps().isCapture(move)) return false;
	    
		return env.getBitboard().getMoveOps().getToFieldID(move)
	    		== env.getBitboard().getMoveOps().getToFieldID(lastMove);
	}
	
	
	protected int roughEval(int depth, int rootColour) {
		
		throw new UnsupportedOperationException();
	}
	
	
	protected int lazyEval(int depth, int alpha, int beta, int rootColour) {
		
		throw new UnsupportedOperationException();
	}
	
	
	protected int root_search(ISearchMediator mediator, ISearchInfo info,
			int maxdepth, int depth, int alpha_org, int beta, int[] prevPV, int rootColour, boolean useMateDistancePrunning) {
		throw new IllegalStateException();
	}
	
	
	protected long getHashkeyTPT() {
		
		long hashkey;
		
		if (useTPTKeyWithMoveCounter()) {
			
			hashkey = env.getBitboard().getHashKey() ^ ((long) getEnv().getBitboard().getPlayedMovesCount());
			
		} else {
			
			hashkey = env.getBitboard().getHashKey();
		}
		
		return hashkey;
	}
	
	
	protected boolean useTPTKeyWithMoveCounter() {
		
		return false;
	}
	
	
	protected int probeTB(ISearchInfo info, int ply) {
		
		
		int egtb_eval = ISearch.MIN;
		
		
		if (SyzygyTBProbing.getSingleton() != null
    			&& SyzygyTBProbing.getSingleton().isAvailable(env.getBitboard().getMaterialState().getPiecesCount())){
			
			int probe_result = probeWDL_WithCache();
			
			if (probe_result != -1) {
				
				info.setTBhits(info.getTBhits() + 1);
							
				int wdl = (probe_result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				
				//Winner is minimizing DTZ and the loser is maximizing DTZ
		        switch (wdl) {
		        
	            	case SyzygyConstants.TB_WIN:
	            		
	            		int dtz = SyzygyTBProbing.getSingleton().probeDTZ(env.getBitboard());
	            		
	    				if (dtz < 0) {
	    					
	     					/**
	    					 * In this not mate position "8/6P1/8/2kB2K1/8/8/8/4r3 w - - 1 19", DTZ is -1 and WDL is 2 (WIN).
	    					 */
	    					break;
	    				}
	    				
						int distanceToDraw_50MoveRule = 99 - env.getBitboard().getDraw50movesRule();
						//Although we specify the rule50 parameter when calling SyzygyJNIBridge.probeSyzygyDTZ(...)
						//Syzygy TBs report winning score/move
						//but the +mate or +promotion moves line is longer
						//than the moves we have until draw with 50 move rule
						if (distanceToDraw_50MoveRule >= dtz) {
							
							//TODO: the eval is too less in order to be more attractive for search than maybe rook and 1+ passed pawns?
							egtb_eval = MAX_MATERIAL_INTERVAL / (ply + dtz + 1); //+1 in order to be less than a mate in max_depth plies.
							
						}
						
						break;
						
		            case SyzygyConstants.TB_LOSS:
		            	
		            	break;
		            
		            case SyzygyConstants.TB_DRAW:
		            	
	            		dtz = SyzygyTBProbing.getSingleton().probeDTZ(env.getBitboard());
	            		
	    				if (dtz < 0) {

	    					break;
	    				}
	    				
						//egtb_eval = getDrawScores(-1);
	    				egtb_eval = -(ply + dtz); //Force engine to finish the game faster when it is a draw
	    				
						break;
						
		            case SyzygyConstants.TB_BLESSED_LOSS:
		            	
		            	//Too risky
		            	//egtb_eval = getDrawScores(-1);
		                
						break;
						
		            case SyzygyConstants.TB_CURSED_WIN:
		            	
		            	//Too risky
		            	//egtb_eval = getDrawScores(-1);
		                
						break;
						
		            default:
		            	
		            	throw new IllegalStateException("wdl=" + wdl);
		        }
			}	
		}
		
		
		return egtb_eval;
	}
	
	
	private int probeWDL_WithCache() {
	    
	    if (USE_DTZ_CACHE) {
	    	
	    	long hash50movesRule = 128 + env.getBitboard().getDraw50movesRule();
		    hash50movesRule += hash50movesRule << 8;
		    hash50movesRule += hash50movesRule << 16;
		    hash50movesRule += hash50movesRule << 32;
		    
		    long hashkey = hash50movesRule ^ getHashkeyTPT();
		    
	    	env.getSyzygyDTZCache().get(hashkey, temp_cache_entry);
			
			if (!temp_cache_entry.isEmpty()) {
				
				int probe_result = temp_cache_entry.getEval();
		        
				return probe_result;
				
			} else {
		    
		        int probe_result = SyzygyTBProbing.getSingleton().probeWDL(env.getBitboard());
		        
		        env.getSyzygyDTZCache().put(hashkey, 5, probe_result);
		        
		        return probe_result;
			}
			
	    } else {
	    	
	    	int probe_result = SyzygyTBProbing.getSingleton().probeWDL(env.getBitboard());
	        
	        return probe_result;
	    }

	}
}

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
package bagaturchess.search.impl.alg.impl1;


import java.util.EmptyStackException;
import java.util.Stack;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.egtb.syzygy.SyzygyConstants;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;

import bagaturchess.search.impl.alg.SearchStackInfo;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.ITTEntry;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.search.impl.eval.cache.EvalEntry_BaseImpl;
import bagaturchess.search.impl.eval.cache.IEvalEntry;
import bagaturchess.search.impl.history.IHistoryTable;
import bagaturchess.search.impl.movelists.SortedMoveList_Root;


public class Search_PVS_NWS extends SearchImpl {
	
	
	private static final int PHASE_TT 								= 0;
	private static final int PHASE_ATTACKING_GOOD 					= 1;
	private static final int PHASE_KILLER_1 						= 2;
	private static final int PHASE_KILLER_2 						= 3;
	private static final int PHASE_KILLER_3 						= 4;
	private static final int PHASE_KILLER_4 						= 5;
	private static final int PHASE_QUIET 							= 6;
	private static final int PHASE_ATTACKING_BAD 					= 7;
	
	private static final double REDUCTION_AGGRESSIVENESS 			= 1.25;
	private static final double PRUNING_AGGRESSIVENESS 				= 1.25;
	
	private static final int[][] LMR_TABLE 							= new int[64][64];
	
	static {
		
		for (int depth = 1; depth < 64; depth++) {
			
			for (int move_number = 1; move_number < 64; move_number++) {
				
				LMR_TABLE[depth][move_number] = (int) Math.ceil(Math.max(1, Math.log(move_number) * Math.log(depth) / (double) 2));
				LMR_TABLE[depth][move_number] = (int) (LMR_TABLE[depth][move_number] * REDUCTION_AGGRESSIVENESS);
			}
		}
	}
	
	private static final int FUTILITY_MAXDEPTH 						= MAX_DEPTH; //7;
	private static final int FUTILITY_MARGIN 						= 80;
	
	private static final int STATIC_NULL_MOVE_MAXDEPTH 				= MAX_DEPTH; //9;
	private static final int STATIC_NULL_MOVE_MARGIN 				= 60;
	
	private static final int RAZORING_MAXDEPTH 						= MAX_DEPTH; //4;
	private static final int RAZORING_MARGIN 						= 240;
	
	private static final int SEE_MAXDEPTH 							= MAX_DEPTH; //8;
	private static final int SEE_MARGIN 							= 65;
	
	private static final boolean USE_LMR_ON_BAD_CAPTURES 			= false;
	
	private static final boolean USE_DTZ_CACHE 						= true;
	
	private static final boolean VALIDATE_PV 						= false;
	
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	private IEvalEntry temp_cache_entry;
	
	private SearchStackInfo[] ssis 									= new SearchStackInfo[MAX_DEPTH + 1];
	
	private VarStatistic stats 										= new VarStatistic();
	
	private long[] search_types_stats 								= new long[16];
	
	private long[] phases_stats 									= new long[8];
	
	
	public Search_PVS_NWS(Object[] args) {
		
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_PVS_NWS(SearchEnv _env) {
		
		
		super(_env);
		
		
		if (USE_DTZ_CACHE) {
	    	
	    	temp_cache_entry = new EvalEntry_BaseImpl();
		}
		
		for (int i=0; i<ssis.length; i++) {
			
			ssis[i] = new SearchStackInfo(); 
		}
	}
	
	
	@Override
	protected boolean useTPTKeyWithMoveCounter() {
		
		return false;
	}
	
	
	public void newSearch() {
		
		
		super.newSearch();
		
		
		lastSentMinorInfo_nodesCount 	= 0;
		lastSentMinorInfo_timestamp 	= 0;
		
		if (ChannelManager.getChannel() != null) {
			
			if (env.getTPT() != null) ChannelManager.getChannel().dump("Search_PVS_NWS.newSearch: Transposition table hitrate=" + env.getTPT().getHitRate() + ", usage=" + env.getTPT().getUsage());
			
			if (env.getEvalCache() != null) ChannelManager.getChannel().dump("Search_PVS_NWS.newSearch: Evaluation cache hitrate=" + env.getEvalCache().getHitRate() + ", usage=" + env.getEvalCache().getUsage());
			
			if (env.getSyzygyDTZCache() != null) ChannelManager.getChannel().dump("Search_PVS_NWS.newSearch: Syzygy DTZ cache hitrate=" + env.getSyzygyDTZCache().getHitRate() + ", usage=" + env.getSyzygyDTZCache().getUsage());
		}
		
		stats.clear();
	}
	
	
	@Override
	public int pv_search(ISearchMediator mediator, PVManager pvman,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha_org, int beta, int prevbest, int prevprevbest,
			int[] prevPV, boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		
		if (Math.abs(beta) >= 3 * ISearch.MAX_MATERIAL_INTERVAL / 2 && !SearchUtils.isMateVal(beta)) {
			
			beta = (beta / ISearch.MAX_MATERIAL_INTERVAL) * ISearch.MAX_MATERIAL_INTERVAL;
			alpha_org = beta - 1;
		}
		
		return root_search(mediator, info, pvman, env.getEval(),
				0, SearchUtils.normDepth(maxdepth), alpha_org, beta, true, SearchUtils.normDepth(initial_maxdepth));
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, PVManager pvman, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		if (Math.abs(beta) >= 3 * ISearch.MAX_MATERIAL_INTERVAL / 2 && !SearchUtils.isMateVal(beta)) {
			
			beta = (beta / ISearch.MAX_MATERIAL_INTERVAL) * ISearch.MAX_MATERIAL_INTERVAL;
		}
		
		return root_search(mediator, info, pvman, env.getEval(),
				0, SearchUtils.normDepth(maxdepth), beta - 1, beta, false, SearchUtils.normDepth(initial_maxdepth));		
	}
	
	
	private int root_search(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator,
			final int ply, int depth, int alpha, int beta, boolean isPv, int initialMaxDepth) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		final int alphaOrig = alpha;
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		node.type = PVNode.TYPE_NORMAL_SEARCH;
		
		
		SearchStackInfo ssi = ssis[ply];
		ssi.hash_key = getHashkeyTPT();
		ssi.in_check = env.getBitboard().isInCheck();
		ssi.tt_hit = false;
		ssi.static_eval = eval(evaluator, ply, alphaOrig, beta, isPv);
		
		
		int ttMove = 0;
		
		if (env.getTPT() != null) {
			
			env.getTPT().get(ssi.hash_key, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				ssi.tt_hit = true;
				
				ttMove = tt_entries_per_ply[ply].getBestMove();
			}
		}
		
		
		final int parentMove 		= env.getBitboard().getLastMove();
		int bestMove 				= 0;
		int bestScore 				= ISearch.MIN;
		
		int movesPerformed_attacks 	= 0;
		int movesPerformed_quiet 	= 0;
		
		IHistoryTable history 		= env.getHistory();
		IHistoryTable conthist 		= env.getContinuationHistory();
		IHistoryTable caphist 		= env.getCaptureHistory();
		
		SortedMoveList_Root list 	= (SortedMoveList_Root) lists_root[ply];
		
		list.clear();
		list.setTTMove(ttMove);
		env.getBitboard().genAllMoves(list);
			
		
		int move;
		while ((move = list.next()) != 0) {
			
			//Build and sent minor info
			info.setCurrentMove(move);
			info.setCurrentMoveNumber((movesPerformed_attacks + movesPerformed_quiet + 1));
			
			
			boolean isCheckMove = env.getBitboard().isCheckMove(move);
			
			
			env.getBitboard().makeMoveForward(move);
			
			
			if (!env.getBitboard().getMoveOps().isCapture(move)) {
				movesPerformed_quiet++;
			} else {
				movesPerformed_attacks++;
			}
			
			boolean isQuietOrBadCapture = !env.getBitboard().getMoveOps().isCapture(move)
					|| (USE_LMR_ON_BAD_CAPTURES && env.getBitboard().getMoveOps().isCapture(move) && env.getBitboard().getSEEScore(move) < 0);
			
			boolean doLMR = depth >= 2
						&& !ssi.in_check
						&& !isCheckMove
						&& movesPerformed_attacks + movesPerformed_quiet > 2
						&& isQuietOrBadCapture;
			
			int reduction = 1;
			
			if (doLMR) {
				
				reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(movesPerformed_attacks + movesPerformed_quiet, 63)];
				
				reduction = Math.min(depth - 1, Math.max(reduction, 1));
			}
			
			
			int score = ISearch.MIN;
			
			if (reduction > 1) {
				
				score = -search(mediator, info, pvman, evaluator, ply + 1, depth - reduction, -alpha - 1, -alpha, false, initialMaxDepth);
				
				if (score > (VALIDATE_PV ? bestScore : alpha)) {
					
					score = -search(mediator, info, pvman, evaluator, ply + 1, depth - 1, -alpha - 1, -alpha, false, initialMaxDepth);
				}
				
			} else if (!isPv || movesPerformed_attacks + movesPerformed_quiet > 1) {
				
				score = -search(mediator, info, pvman, evaluator, ply + 1, depth - 1, -alpha - 1, -alpha, false, initialMaxDepth);
			}
			
			if (isPv && (score > bestScore || movesPerformed_attacks + movesPerformed_quiet == 1)) {
				
				score = -search(mediator, info, pvman, evaluator, ply + 1, depth - 1, -beta, -alpha, isPv, initialMaxDepth);
			}
			
			
			env.getBitboard().makeMoveBackward(move);
			
			
			if (!env.getBitboard().getMoveOps().isCapture(move)) {
				
				history.registerAll(parentMove, move, depth);				
				conthist.registerAll(parentMove, move, depth);
				
				if (score < beta) {
					
					history.registerBad(parentMove, move, depth);
					conthist.registerBad(parentMove, move, depth);
				}
			} else {
				
				caphist.registerAll(parentMove, move, depth);
				
				if (score < beta) {
					
					caphist.registerBad(parentMove, move, depth);
				}
			}
			
			
			if (score > bestScore) {
				
				bestScore = score;
				bestMove = move;
				
				node.bestmove = bestMove;
				node.eval = bestScore;
				node.leaf = false;
				
				if (ply + 1 < ISearch.MAX_DEPTH) {
					pvman.store(ply + 1, node, pvman.load(ply + 1), true);
				}
			}
			
			alpha = Math.max(alpha, score);
			
			if (alpha >= beta) {
				
				if (!env.getBitboard().getMoveOps().isCapture(move)) {
					
					env.getKillers().addKillerMove(env.getBitboard().getColourToMove(), move, ply);
					
					history.registerGood(parentMove, move, depth);
					conthist.registerGood(parentMove, move, depth);
					
				} else {
					
					caphist.registerGood(parentMove, move, depth);
				}
				
				break;
			}
		}
		
		
		if (movesPerformed_attacks + movesPerformed_quiet == 0) {
			
			if (!ssi.in_check) {
				
				node.bestmove = 0;
				node.eval = getDrawScores(-1);
				node.leaf = true;
				node.type = PVNode.TYPE_DRAW;
				
				bestScore = node.eval;
				bestMove = 0;
				
			} else {
				
				node.bestmove = 0;
				node.eval = -SearchUtils.getMateVal(ply, getEnv().getBitboard());
				node.leaf = true;
				node.type = PVNode.TYPE_MATE;
				
				bestScore = node.eval;
				bestMove = 0;
			}
		}
		
		
		if (bestScore != node.eval) {
			
			throw new IllegalStateException("bestScore != node.eval");
		}
		
		
		if (bestMove != node.bestmove) {
			
			throw new IllegalStateException("bestMove != node.bestmove");
		}
		
		
		if (env.getTPT() != null) {
			
			env.getTPT().put(ssi.hash_key, depth, bestScore, alphaOrig, beta, bestMove);
		}
		
		
		if (VALIDATE_PV) validatePV(node, evaluator, ply, depth, isPv, alphaOrig, beta);
		
		
		return bestScore;
	}
	
	
	private int search(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator,
			final int ply, int depth, int alpha, int beta, boolean isPv, int initialMaxDepth) {
		
		
		if (mediator != null && mediator.getStopper() != null) {
			
			mediator.getStopper().stopIfNecessary(initialMaxDepth, env.getBitboard().getColourToMove(), alpha, beta);
		}
		
		if (ply < 1) {
			
			throw new IllegalStateException("ply < 1 => use root_search");
		}
		
		
		SearchStackInfo ssi = ssis[ply];
		ssi.hash_key = getHashkeyTPT();
		ssi.in_check = env.getBitboard().isInCheck();
		ssi.tt_hit = false;
		ssi.static_eval = eval(evaluator, ply, alpha, beta, isPv);
		
		
		if (ply >= ISearch.MAX_DEPTH) {
			
			return ssi.static_eval;
		}
		
		
		boolean improving = ply - 2 >= 0 ? (ssi.static_eval - ssis[ply - 2].static_eval > 0) : false;
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		node.type = PVNode.TYPE_NORMAL_SEARCH;
		
		
		if (depth <= 0) {
			
			int qeval = qsearch(mediator, pvman, evaluator, info, alpha, beta, ply, isPv);
			
			if (node.eval != qeval) {
				
				throw new IllegalStateException("node.eval != qeval");
			}
			
			return node.eval;
		}
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		if (info.getSelDepth() < ply) {
			
			info.setSelDepth(ply);
		}
		
		
		final int alpha_org = alpha;
		
		
	    if (isDraw(isPv)) {
	    	
	    	node.eval = getDrawScores(-1);
	    	node.type = PVNode.TYPE_DRAW;
	    	
	    	return node.eval;
	    }
		
		
	    //Mate distance pruning
		alpha = Math.max(alpha, -SearchUtils.getMateVal(ply));
		
		beta = Math.min(beta, +SearchUtils.getMateVal(ply + 1));
		
		if (alpha >= beta) {
			
			node.eval = SearchUtils.isMateVal(alpha) ? alpha : beta;
			node.type = PVNode.TYPE_MATE_DISTANCE_PRUNING;
			
			return node.eval;
		}
		
		
		final int parentMove 		= env.getBitboard().getLastMove();
		final int colourToMove 		= env.getBitboard().getColourToMove();
		
		
		int ttMove 									= 0;
		int ttFlag 									= -1;
		int ttValue 								= IEvaluator.MIN_EVAL;
		
		boolean isTTLowerBoundOrExact				= false;
		boolean isTTDepthEnoughForSingularExtension = false;
		
		if (env.getTPT() != null) {
			
			env.getTPT().get(ssi.hash_key, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				ssi.tt_hit = true;
				
				ttMove = tt_entries_per_ply[ply].getBestMove();
				ttFlag = tt_entries_per_ply[ply].getFlag();
				ttValue = tt_entries_per_ply[ply].getEval();
				
				int tpt_depth = tt_entries_per_ply[ply].getDepth();
				
				isTTLowerBoundOrExact = ttFlag == ITTEntry.FLAG_LOWER || ttFlag == ITTEntry.FLAG_EXACT;
				isTTDepthEnoughForSingularExtension = tt_entries_per_ply[ply].getDepth() >= depth - 3;
				
				if (getSearchConfig().isOther_UseTPTScores()) {
					
					if (!isPv && tpt_depth >= depth) {
						
						if (ttFlag == ITTEntry.FLAG_EXACT) {
							
							extractFromTT(ply, node, tt_entries_per_ply[ply], info, isPv);
							
							return node.eval;
							
						} else {
							
							if (ttFlag == ITTEntry.FLAG_LOWER && ttValue >= beta) {
								
								extractFromTT(ply, node, tt_entries_per_ply[ply], info, isPv);
								
								return node.eval;
							}
							
							if (ttFlag == ITTEntry.FLAG_UPPER && ttValue <= alpha) {
								
								extractFromTT(ply, node, tt_entries_per_ply[ply], info, isPv);
								
								return node.eval;
							}
						}
					}
				}
			}
		}
		
		
		//TB probing
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
			
			
			if (egtb_eval != ISearch.MIN) {
				
				if (ssi.in_check) {
					
					if (!env.getBitboard().hasMoveInCheck()) {
						
						egtb_eval = -SearchUtils.getMateVal(ply);
					}
					
				} else {
					
					if (!env.getBitboard().hasMoveInNonCheck()) {
						
						egtb_eval = getDrawScores(-1);
					}
				}
				
				if (ply > 7) {
					
					node.bestmove = 0;
					node.eval = egtb_eval;
					node.leaf = true;
					node.type = PVNode.TYPE_TB;
					
					return node.eval;
				}
			}
        }
		
		
		if (!VALIDATE_PV && ttValue != IEvaluator.MIN_EVAL) {
			
			if (getSearchConfig().isOther_UseTPTScores()) {
				
				if (ttFlag == ITTEntry.FLAG_EXACT
						|| (ttFlag == ITTEntry.FLAG_UPPER && ttValue < ssi.static_eval)
						|| (ttFlag == ITTEntry.FLAG_LOWER && ttValue > ssi.static_eval)
					) {
					
					ssi.static_eval = ttValue;
				}
			}
		}
		
		boolean mateThreat  = false;
		
		if (!isPv
				&& !ssi.in_check
				&& !SearchUtils.isMateVal(alpha)
				&& !SearchUtils.isMateVal(beta)
				&& egtb_eval == ISearch.MIN
			) {
			
			
			if (!VALIDATE_PV && depth >= 2 && ttFlag == -1) {
				
				depth -= 1;
			}
			
			
			if (ssi.static_eval >= beta) {
				
				
				if (depth <= STATIC_NULL_MOVE_MAXDEPTH) {
					
					if (ssi.static_eval - depth * STATIC_NULL_MOVE_MARGIN / PRUNING_AGGRESSIVENESS >= beta) {
						
						node.bestmove = 0;
						node.eval = ssi.static_eval;
						node.leaf = true;
						node.type = PVNode.TYPE_STATIC_NULL_MOVE;
								
						return node.eval;
					}
				}
				
				
				if (depth >= 3) {
					
					boolean hasAtLeastOnePiece = (colourToMove == Constants.COLOUR_WHITE) ?
								env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
								env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
						
					if (hasAtLeastOnePiece) {
						
						env.getBitboard().makeNullMoveForward();
						
						int reduction = depth / 4 + 3 + Math.min((Math.max(0, ssi.static_eval - beta)) / 80, 3);
						reduction = (int) (reduction * REDUCTION_AGGRESSIVENESS);
						
						int score = depth - reduction <= 0 ? -qsearch(mediator, pvman, evaluator, info, -beta, -beta + 1, ply + 1, false)
								: -search(mediator, info, pvman, evaluator, ply + 1, depth - reduction, -beta, -beta + 1, false, initialMaxDepth);
						
						env.getBitboard().makeNullMoveBackward();
						
						if (score >= beta) {
							
							int verify_score = depth - reduction <= 0 ? qsearch(mediator, pvman, evaluator, info, beta - 1, beta, ply, false)
									: search(mediator, info, pvman, evaluator, ply, depth - reduction, beta - 1, beta, false, initialMaxDepth);
							
							if (verify_score >= beta) {
								
								node.bestmove = 0;
								node.eval = verify_score;
								node.leaf = true;
								node.type = PVNode.TYPE_NULL_MOVE;
								
								return node.eval;
							}
							
							//If the verify_score is negative mate score
							if ((verify_score < -ISearch.MAX_MATERIAL_INTERVAL)
									&& depth >= 2
									&& ply < 2 * initialMaxDepth) {
								
								mateThreat = true;
							}
						}
						
						//If the score is negative mate score
						if ((score < -ISearch.MAX_MATERIAL_INTERVAL)
								&& depth >= 2
								&& ply < 2 * initialMaxDepth) {
							
							mateThreat = true;
						}
					}
				}
				
			} else if (ssi.static_eval <= alpha) {
				
				
				if (depth <= RAZORING_MAXDEPTH) {
					
					int razoringMargin = (int) (RAZORING_MARGIN * depth / PRUNING_AGGRESSIVENESS);
					
					if (ssi.static_eval + razoringMargin < alpha) {
						
						int score = qsearch(mediator, pvman, evaluator, info, alpha - razoringMargin - 1, alpha - razoringMargin, ply, false);
						
						if (score < alpha - razoringMargin) {
							
							node.bestmove = 0;
							node.eval = score;
							node.leaf = true;
							node.type = PVNode.TYPE_RAZORING;
							
							return node.eval;
						}
					}
				}
			}
			
			
			//ProbeCut
			int prob_cut_margin = 200;
			int prob_cut_beta = beta + prob_cut_margin;
			
			if (depth >= 3
					&& (ttFlag == -1 || ttValue >= prob_cut_beta)) {
				
				IMoveList list = lists_attacks[ply];
				list.clear();
				env.getBitboard().genCapturePromotionMoves(list);
				
				int prob_cut_depth = Math.max(0, depth - 5);
				
				int move;
				while ((move = list.next()) != 0)  {
					
					env.getBitboard().makeMoveForward(move);
					
					int score = -qsearch(mediator, pvman, evaluator, info, -prob_cut_beta, -prob_cut_beta + 1, ply + 1, isPv);
					
					if (score >= prob_cut_beta && prob_cut_depth > 0) {
						
						score = -search(mediator, info, pvman, evaluator, ply + 1, prob_cut_depth, -prob_cut_beta, -prob_cut_beta + 1, isPv, initialMaxDepth);
					}
					
					env.getBitboard().makeMoveBackward(move);
					
					
					if (score >= prob_cut_beta) {
						
						node.bestmove = 0;
						node.eval = score;
						node.leaf = true;
						node.type = PVNode.TYPE_PROBECUT;
						
						return node.eval;
					}
				}
			}
		}
		
		
		int tt_move_extension = 0;
		
		if (depth >= 4
				&& ply < 2 * initialMaxDepth
				&& isTTLowerBoundOrExact
				&& isTTDepthEnoughForSingularExtension
				&& env.getBitboard().isPossible(ttMove)
			) {
			
			//TODO: Adjust beta margin and depth
			int singular_margin = 2 * depth;
			int singular_beta = ttValue - singular_margin;
			int singular_depth = depth / 2;
			singular_depth = (int) Math.max(1 , singular_depth / REDUCTION_AGGRESSIVENESS);
			
			int singular_value = singular_move_search(mediator, info, pvman, evaluator, ply,
					singular_depth, singular_beta - 1, singular_beta, false, initialMaxDepth, ttMove, ssi.static_eval);
			
			//Singular extension - only ttMove has score above beta
			if (singular_value < singular_beta) {
				
				if (!VALIDATE_PV) {
					
					tt_move_extension = 1;
					
					if (!isPv) {
						
						if (singular_value < singular_beta - singular_margin) {
							
							tt_move_extension++;
							
							if (singular_value < singular_beta - 2 * singular_margin) {
								
								tt_move_extension++;
							}
						}
					}
				}
				
			} else if (!isPv) {
				
				//Multi-cut pruning - 2 moves above beta
				if (singular_value > beta) {
				
					if (!SearchUtils.isMateVal(alpha)
						&& !SearchUtils.isMateVal(beta)) {
						
						node.bestmove = 0;
						node.eval = singular_value;
						node.leaf = true;
						node.type = PVNode.TYPE_MULTICUT;
						
						return node.eval;
					}
				
				} else {
					
					if (!VALIDATE_PV) {
						
						//Expected fail-high
						tt_move_extension = -2;
					}
				}
			}
		}
		
		
		//Still no tt move, so help the search to find the tt move/score faster
		if (!VALIDATE_PV && isPv && ttFlag == -1 && depth >= 3) {
			
			depth -= 2;
		}
		
		
		int bestMove 				= 0;
		int bestScore 				= ISearch.MIN;
		
		int killer1Move 			= 0;
		int killer2Move 			= 0;
		int killer3Move 			= 0;
		int killer4Move 			= 0;
		
		int movesPerformed_attacks 	= 0;
		int movesPerformed_quiet 	= 0;
		
		IHistoryTable history 		= env.getHistory();
		IHistoryTable conthist 		= env.getContinuationHistory();
		IHistoryTable caphist 		= env.getCaptureHistory();
		
		IMoveList list1 			= lists_history[ply];
		IMoveList list2 			= lists_attacks[ply];
		IMoveList list 				= null;
		
		int phase = PHASE_TT;
		while (phase <= PHASE_ATTACKING_BAD) {
			
			switch (phase) {
			
			case PHASE_TT:
				
				if (ttMove != 0
						&& env.getBitboard().isPossible(ttMove)) {
					
					list = list1;
					list.clear();
					list.reserved_add(ttMove);
				}
				
				break;
				
			case PHASE_ATTACKING_GOOD:
				
				list = list2;
				list.clear();
				env.getBitboard().genCapturePromotionMoves(list);
				
				break;
				
			case PHASE_KILLER_1:
				
				killer1Move = env.getKillers().getKiller1(colourToMove, ply);
				
				if (killer1Move != 0
						&& killer1Move != ttMove
						&& env.getBitboard().isPossible(killer1Move)) {
					
					list = list1;
					list.clear();
					list.reserved_add(killer1Move);
				}
				
				break;
				
			case PHASE_KILLER_2:
				
				killer2Move = env.getKillers().getKiller2(colourToMove, ply);
				
				if (killer2Move != 0
						&& killer2Move != ttMove
						&& killer2Move != killer1Move
						&& env.getBitboard().isPossible(killer2Move)) {
					
					list = list1;
					list.clear();
					list.reserved_add(killer2Move);
				}
				
				break;
			
			case PHASE_KILLER_3:
				
				killer3Move = env.getKillers().getKiller3(colourToMove, ply);
				
				if (killer3Move != 0
						&& killer3Move != ttMove
						&& killer3Move != killer1Move
						&& killer3Move != killer2Move
						&& env.getBitboard().isPossible(killer3Move)) {
					
					list = list1;
					list.clear();
					list.reserved_add(killer3Move);
				}
				
				break;
			
			case PHASE_KILLER_4:
				
				killer4Move = env.getKillers().getKiller4(colourToMove, ply);
				
				if (killer4Move != 0
						&& killer4Move != ttMove
						&& killer4Move != killer1Move
						&& killer4Move != killer2Move
						&& killer4Move != killer3Move
						&& env.getBitboard().isPossible(killer4Move)) {
					
					list = list1;
					list.clear();
					list.reserved_add(killer4Move);
				}
				
				break;
				
			case PHASE_ATTACKING_BAD:
				
				list = list2;
				list.clear();
				env.getBitboard().genCapturePromotionMoves(list);
				
				break;
				
			case PHASE_QUIET:
				
				list = list1;
				list.clear();
				env.getBitboard().genNonCaptureNonPromotionMoves(list);
				
				break;
			}
			
			
			int move;
			while (list != null && (move = list.next()) != 0) {
				
				if (phase == PHASE_ATTACKING_GOOD) {
					if (env.getBitboard().getSEEScore(move) < 0) {
						continue;
					}
				}
				
				if (phase == PHASE_ATTACKING_BAD) {
					if (env.getBitboard().getSEEScore(move) >= 0) {
						continue;
					}
				}
				
				if (phase == PHASE_QUIET) {
					if (move == ttMove || move == killer1Move || move == killer2Move) {
						continue;
					}
				} else if (phase == PHASE_ATTACKING_GOOD || phase == PHASE_ATTACKING_BAD) {
					if (move == ttMove) {
						continue;
					}
				}
				
				
				if (info.getSearchedNodes() >= lastSentMinorInfo_nodesCount + 50000 ) { //Check time on each 50 000 nodes
					
					long timestamp = System.currentTimeMillis();
					
					if (timestamp >= lastSentMinorInfo_timestamp + 1000)  {//Send info each second
					
						mediator.changedMinor(info);
						
						lastSentMinorInfo_timestamp = timestamp;
					}
					
					lastSentMinorInfo_nodesCount = info.getSearchedNodes();
				}
				
				
				if (!env.getBitboard().getMoveOps().isCapture(move)) {
					
					stats.addValue(list.getScore());
				}
				
				
				boolean isCheckMove = env.getBitboard().isCheckMove(move);
				
				
				if (!env.getBitboard().getMoveOps().isCapture(move)) {
					movesPerformed_quiet++;
				} else {
					movesPerformed_attacks++;
				}
				
				
				if (!isPv
						&& !ssi.in_check
						&& !isCheckMove
						&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& !SearchUtils.isMateVal(alpha)
						&& !SearchUtils.isMateVal(beta)
						&& egtb_eval == ISearch.MIN
					) {
					
					boolean hasAtLeastOnePiece = (colourToMove == Constants.COLOUR_WHITE) ?
							env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
							env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
							
					if (phase == PHASE_QUIET
							&& list.getScore() <= stats.getEntropy()
							) {
						
						if (movesPerformed_attacks + movesPerformed_quiet >= (3 + depth * depth / (improving ? 1 : 2)) / PRUNING_AGGRESSIVENESS) {
							
							continue;
						}
						
						if (depth <= FUTILITY_MAXDEPTH
								&& hasAtLeastOnePiece
								&& ssi.static_eval + depth * FUTILITY_MARGIN / PRUNING_AGGRESSIVENESS <= alpha) {
							
							continue;
						}
						
						if (movesPerformed_attacks + movesPerformed_quiet > 3
								&& depth <= SEE_MAXDEPTH
								&& hasAtLeastOnePiece
								&& env.getBitboard().getSEEScore(move) < -SEE_MARGIN * depth / PRUNING_AGGRESSIVENESS) {
							
							continue;
						}
						
					} else if (phase == PHASE_ATTACKING_BAD
							&& depth <= SEE_MAXDEPTH
							&& hasAtLeastOnePiece
							&& env.getBitboard().getSEEScore(move) < -SEE_MARGIN * depth / PRUNING_AGGRESSIVENESS) {
						
						continue;
					}
				}
				
				
				int new_depth;
				
				if (move == ttMove && tt_move_extension != 0) {
					
					new_depth = depth - 1 + tt_move_extension;
					
					//Extend TT move to end up with search (not qsearch).
					//new_depth = Math.max(1, new_depth);
					
				} else if (mateThreat) {
					
					new_depth = depth;
					
				} else {
					
					new_depth = depth - 1;
				}
				
				
				boolean isQuietOrBadCapture = !env.getBitboard().getMoveOps().isCapture(move)
						|| (USE_LMR_ON_BAD_CAPTURES && env.getBitboard().getMoveOps().isCapture(move) && env.getBitboard().getSEEScore(move) < 0);
				
				boolean doLMR = new_depth >= 2
						&& !ssi.in_check
						&& !isCheckMove
						&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& list.getScore() <= stats.getEntropy() + stats.getDisperse()
						&& isQuietOrBadCapture;
				
				int reduction = 1;
				
				if (doLMR) {
					
					reduction = LMR_TABLE[Math.min(new_depth, 63)][Math.min(movesPerformed_attacks + movesPerformed_quiet, 63)];
					
					if (!isPv) {
						
						reduction += 1;
					}
					
					//reduction += list.getScore() <= stats.getEntropy() ? 1 : -1;
					
					reduction = Math.min(new_depth - 1, Math.max(reduction, 1));
				}
				
				
				int lmr_depth = new_depth - reduction;
				
				
				env.getBitboard().makeMoveForward(move);
				
				
				int score = ISearch.MIN;
				
				if (reduction > 1) {
											
					score = -search(mediator, info, pvman, evaluator, ply + 1, lmr_depth, -alpha - 1, -alpha, false, initialMaxDepth);
					
					if (score > (VALIDATE_PV ? bestScore : alpha)) {
						
						score = -search(mediator, info, pvman, evaluator, ply + 1, new_depth, -alpha - 1, -alpha, false, initialMaxDepth);
					}
					
				} else if (!isPv || movesPerformed_attacks + movesPerformed_quiet > 1) {
					
					score = -search(mediator, info, pvman, evaluator, ply + 1, new_depth, -alpha - 1, -alpha, false, initialMaxDepth);
				}
				
				if (isPv && (score > bestScore || movesPerformed_attacks + movesPerformed_quiet == 1)) {
					
					score = -search(mediator, info, pvman, evaluator, ply + 1, new_depth, -beta, -alpha, isPv, initialMaxDepth);
				}
				
				env.getBitboard().makeMoveBackward(move);
				
				
				if (!env.getBitboard().getMoveOps().isCapture(move)) {
					
					history.registerAll(parentMove, move, depth);
					conthist.registerAll(parentMove, move, depth);
					
					if (score < beta) {
						
						history.registerBad(parentMove, move, depth);
						conthist.registerBad(parentMove, move, depth);
					}
					
				} else {
					
					caphist.registerAll(parentMove, move, depth);
					
					if (score < beta) {
						
						caphist.registerBad(parentMove, move, depth);
					}
				}
				
				
				if (score > bestScore) {
					
					bestScore = score;
					bestMove = move;
					
					node.bestmove = bestMove;
					node.eval = bestScore;
					node.leaf = false;
					
					if (ply + 1 < ISearch.MAX_DEPTH) {
						pvman.store(ply + 1, node, pvman.load(ply + 1), true);
					}
				}
				
				alpha = Math.max(alpha, score);
				
				if (alpha >= beta) {
					
					if (!env.getBitboard().getMoveOps().isCapture(move)) {
						
						env.getKillers().addKillerMove(colourToMove, move, ply);
						
						history.registerGood(parentMove, move, depth);
						conthist.registerGood(parentMove, move, depth);
						
					} else {
						
						caphist.registerGood(parentMove, move, depth);
					}
					
					phases_stats[phase]++;
					/*if (phases_stats[phase] % 10000 == 0) {
					    for (int i = 0; i < phases_stats.length; i++) {
					        System.out.print(i + "=" + phases_stats[i] + " ");
					    }
					    System.out.println();
					}*/
					
					phase += 379;
					
					break;
				}
			}
			
			phase++;
		}
		
		
		if (movesPerformed_attacks + movesPerformed_quiet == 0) {
			
			if (!ssi.in_check) {
				
				node.bestmove = 0;
				node.eval = getDrawScores(-1);
				node.leaf = true;
				node.type = PVNode.TYPE_DRAW;
				
				bestScore = node.eval;
				bestMove = 0;
				
			} else {
				
				node.bestmove = 0;
				node.eval = -SearchUtils.getMateVal(ply);
				node.leaf = true;
				node.type = PVNode.TYPE_MATE;
				
				bestScore = node.eval;
				bestMove = 0;
			}
		}
		
		
		if (bestScore != node.eval) {
			
			throw new IllegalStateException("bestScore != node.eval");
		}

		
		if (bestMove != node.bestmove) {
			
			throw new IllegalStateException("bestMove != node.bestmove");
		}
		
		
		if (env.getTPT() != null) {
				
			env.getTPT().put(ssi.hash_key, depth, node.eval, alpha_org, beta, node.bestmove);
		}
		
		
		if (VALIDATE_PV) validatePV(node, evaluator, ply, depth, isPv, alpha_org, beta);
		
		
		return node.eval;
	}
	
	
	private int singular_move_search(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator,
			final int ply, int depth, int alpha,
			int beta, boolean isPv, int initialMaxDepth, int ttMove1, int eval) {
		
		
		final long hashkey = getHashkeyTPT() ^ ttMove1;
		
		int ttMove2 = 0; 
				
		if (env.getTPT() != null) {
			
			env.getTPT().get(hashkey, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				ttMove2 = tt_entries_per_ply[ply].getBestMove();
				int ttFlag = tt_entries_per_ply[ply].getFlag();
				int ttValue = tt_entries_per_ply[ply].getEval();
				
				int tpt_depth = tt_entries_per_ply[ply].getDepth();
				
				if (getSearchConfig().isOther_UseTPTScores()) {
					
					if (!isPv && tpt_depth >= depth) {
						
						if (ttFlag == ITTEntry.FLAG_EXACT) {
							
							return ttValue;
							
						} else {
							
							if (ttFlag == ITTEntry.FLAG_LOWER && ttValue >= beta) {
								
								return ttValue;
							}
							
							if (ttFlag == ITTEntry.FLAG_UPPER && ttValue <= alpha) {
								
								return ttValue;
							}
						}
					}
				}
			}
		}
		
		
		final boolean wasInCheck 	= env.getBitboard().isInCheck();
		final int colourToMove 		= env.getBitboard().getColourToMove();
		
		
		final int alpha_org 		= alpha;
		
		int killer1Move 			= 0;
		int killer2Move 			= 0;
		int killer3Move 			= 0;
		int killer4Move 			= 0;
		
		int bestScore 				= ISearch.MIN;
		int bestMove 				= 0;
		
		int all_moves 				= 0;
		
		IMoveList list1 			= lists_history[ply];
		IMoveList list2 			= lists_attacks[ply];
		IMoveList list 				= null;
		
		int phase = PHASE_TT;
		while (phase <= PHASE_ATTACKING_BAD) {
			
			switch (phase) {
			
			case PHASE_TT:
				
				if (ttMove2 != 0
						&& env.getBitboard().isPossible(ttMove2)) {
					
					list = list1;
					list.clear();
					list.reserved_add(ttMove2);
				}
				
				break;
				
			case PHASE_ATTACKING_GOOD:
				
				list = list2;
				list.clear();
				env.getBitboard().genCapturePromotionMoves(list);
				
				break;
				
			case PHASE_KILLER_1:
				
				killer1Move = env.getKillers().getKiller1(colourToMove, ply);
				
				if (killer1Move != 0
						&& killer1Move != ttMove2
						&& env.getBitboard().isPossible(killer1Move)) {
					
					list = list1;
					list.clear();
					list.reserved_add(killer1Move);
				}
				
				break;
				
			case PHASE_KILLER_2:
				
				killer2Move = env.getKillers().getKiller2(colourToMove, ply);
				
				if (killer2Move != 0
						&& killer2Move != ttMove2
						&& killer2Move != killer1Move
						&& env.getBitboard().isPossible(killer2Move)) {
					
					list = list1;
					list.clear();
					list.reserved_add(killer2Move);
				}
				
				break;
			
			case PHASE_KILLER_3:
				
				killer3Move = env.getKillers().getKiller3(colourToMove, ply);
				
				if (killer3Move != 0
						&& killer3Move != ttMove2
						&& killer3Move != killer1Move
						&& killer3Move != killer2Move
						&& env.getBitboard().isPossible(killer3Move)) {
					
					list = list1;
					list.clear();
					list.reserved_add(killer3Move);
				}
				
				break;
			
			case PHASE_KILLER_4:
				
				killer4Move = env.getKillers().getKiller4(colourToMove, ply);
				
				if (killer4Move != 0
						&& killer4Move != ttMove2
						&& killer4Move != killer1Move
						&& killer4Move != killer2Move
						&& killer4Move != killer3Move
						&& env.getBitboard().isPossible(killer4Move)) {
					
					list = list1;
					list.clear();
					list.reserved_add(killer4Move);
				}
				
				break;
				
			case PHASE_ATTACKING_BAD:
				
				list = list2;
				list.clear();
				env.getBitboard().genCapturePromotionMoves(list);
				
				break;
				
			case PHASE_QUIET:
				
				list = list1;
				list.clear();
				env.getBitboard().genNonCaptureNonPromotionMoves(list);
				
				break;
			}
			
			
			int move;
			while (list != null && (move = list.next()) != 0) {
				
				//Skip tt move
				if (move == ttMove1) {
					
					continue;
				}
				
				if (phase == PHASE_ATTACKING_GOOD) {
					if (env.getBitboard().getSEEScore(move) < 0) {
						continue;
					}
				}
				
				if (phase == PHASE_ATTACKING_BAD) {
					if (env.getBitboard().getSEEScore(move) >= 0) {
						continue;
					}
				}
				
				if (phase == PHASE_QUIET) {
					if (move == ttMove2 || move == killer1Move || move == killer2Move) {
						continue;
					}
				} else if (phase == PHASE_ATTACKING_GOOD || phase == PHASE_ATTACKING_BAD) {
					if (move == ttMove2) {
						continue;
					}
				}	
				
				
				boolean isCheckMove = env.getBitboard().isCheckMove(move);
				
				
				all_moves++;
				
				
				if (!isPv
						&& !wasInCheck
						&& !isCheckMove
						&& all_moves > 1
						&& !SearchUtils.isMateVal(alpha)
						&& !SearchUtils.isMateVal(beta)
					) {
					
					boolean hasAtLeastOnePiece = (colourToMove == Constants.COLOUR_WHITE) ?
							env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
							env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
					
					if (phase == PHASE_QUIET
							&& list.getScore() <= stats.getEntropy()
							) {
						
						if (all_moves >= (3 + depth * depth) / PRUNING_AGGRESSIVENESS) {
							
							continue;
						}
						
						if (depth <= FUTILITY_MAXDEPTH
								&& hasAtLeastOnePiece
								&& eval + depth * FUTILITY_MARGIN / PRUNING_AGGRESSIVENESS <= alpha) {
							
							continue;
						}
						
						if (all_moves > 3
								&& depth <= SEE_MAXDEPTH
								&& hasAtLeastOnePiece
								&& env.getBitboard().getSEEScore(move) < -SEE_MARGIN * depth / PRUNING_AGGRESSIVENESS) {
							
							continue;
						}
						
					} else if (phase == PHASE_ATTACKING_BAD
							&& depth <= SEE_MAXDEPTH
							&& hasAtLeastOnePiece
							&& env.getBitboard().getSEEScore(move) < -SEE_MARGIN * depth / PRUNING_AGGRESSIVENESS) {
						
						continue;
					}
				}
				
				
				env.getBitboard().makeMoveForward(move);
				
				
				boolean isQuietOrBadCapture = !env.getBitboard().getMoveOps().isCapture(move)
						|| (USE_LMR_ON_BAD_CAPTURES && env.getBitboard().getMoveOps().isCapture(move) && env.getBitboard().getSEEScore(move) < 0);
				
				boolean doLMR = depth >= 2
						&& !wasInCheck
						&& !isCheckMove
						&& all_moves > 1
						&& list.getScore() <= stats.getEntropy() + stats.getDisperse()
						&& isQuietOrBadCapture;
				
				int reduction = 1;
				
				if (doLMR) {
					
					reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(all_moves, 63)];
					
					if (!isPv) {
						
						reduction += 1;
					}
					
					//reduction += list.getScore() <= stats.getEntropy() ? 1 : -1;
					
					reduction = Math.min(depth - 1, Math.max(reduction, 1));
					
				}
				
				
				int score = ISearch.MIN;
				
				if (reduction > 1) {
					
					score = -search(mediator, info, pvman, evaluator, ply + 1, depth - reduction, -alpha - 1, -alpha, false, initialMaxDepth);
					
					if (score > alpha) {
						
						score = -search(mediator, info, pvman, evaluator, ply + 1, depth - 1, -alpha - 1, -alpha, false, initialMaxDepth);
					}
					
				} else if (!isPv || all_moves > 1) {
					
					score = -search(mediator, info, pvman, evaluator, ply + 1, depth - 1, -alpha - 1, -alpha, false, initialMaxDepth);
				}
				
				if (isPv && (score > alpha || all_moves == 1)) {
					
					score = -search(mediator, info, pvman, evaluator, ply + 1, depth - 1, -beta, -alpha, isPv, initialMaxDepth);
				}
				
				env.getBitboard().makeMoveBackward(move);
				
				
				if (score > bestScore) {
					
					bestScore = score;
					bestMove = move;
				}
				
				alpha = Math.max(alpha, score);
				
				if (alpha >= beta) {
					
					phase += 379;
					
					break;
				}
			}
			
			phase++;
		}
		
		
		if (env.getTPT() != null) {
			
			if (bestMove != 0) {
				
				env.getTPT().put(hashkey, depth, bestScore, alpha_org, beta, bestMove);
			}
		}
		
		
		return bestScore;
	}
	
	
	public int qsearch(ISearchMediator mediator, PVManager pvman, IEvaluator evaluator, ISearchInfo info,
			int alpha, final int beta, final int ply, final boolean isPv) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		if (info.getSelDepth() < ply) {
			
			info.setSelDepth(ply);
		}
		
		
		if (ply >= ISearch.MAX_DEPTH) {
	    	
			return eval(evaluator, ply, alpha, beta, isPv);
		}
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		node.type = PVNode.TYPE_NORMAL_QSEARCH;
		
		
		if (isDraw(isPv)) {
	    	
	    	node.eval = getDrawScores(-1);
	    	node.type = PVNode.TYPE_DRAW;
	    	
	    	return node.eval;
	    }
		
		long hashkey = getHashkeyTPT();
		
	    int ttFlag 		= -1;
	    int ttValue 	= IEvaluator.MIN_EVAL;

		if (env.getTPT() != null) {
			
			env.getTPT().get(hashkey, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				ttValue = tt_entries_per_ply[ply].getEval();
				ttFlag = tt_entries_per_ply[ply].getFlag();
				
				if (!isPv && getSearchConfig().isOther_UseTPTScores()) {
					
					if (ttFlag == ITTEntry.FLAG_EXACT) {
						
				    	extractFromTT(ply, node, tt_entries_per_ply[ply], info, isPv);
				    	
				    	return node.eval;
				    	
					} else {
						
						if (ttFlag == ITTEntry.FLAG_LOWER && ttValue >= beta) {
							
							extractFromTT(ply, node, tt_entries_per_ply[ply], info, isPv);
							
					    	return node.eval;
						}
						
						if (ttFlag == ITTEntry.FLAG_UPPER && ttValue <= alpha) {
							
							extractFromTT(ply, node, tt_entries_per_ply[ply], info, isPv);
							
					    	return node.eval;
						}
					}
				}
			}
		}
	  	
		
		if (env.getBitboard().isInCheck()) {
			//With queens on the board, this extension goes out of control if qsearch plays TT moves which are not attacks only.
			return search(mediator, info, pvman, evaluator, ply, 1, alpha, beta, false, 1);
			//return alpha;
		}
		
		
		int eval = eval(evaluator, ply, alpha, beta, isPv);
		
		
		if (!VALIDATE_PV && ttValue != IEvaluator.MIN_EVAL) {
			
			if (getSearchConfig().isOther_UseTPTScores()) {
				
				if (ttFlag == ITTEntry.FLAG_EXACT
						|| (ttFlag == ITTEntry.FLAG_UPPER && ttValue < eval)
						|| (ttFlag == ITTEntry.FLAG_LOWER && ttValue > eval)
					) {
					
					eval = ttValue;
				}
			}
		}
		
		
		if (eval >= beta) {
			
	    	node.eval = eval;
	    	node.type = PVNode.TYPE_BETA_CUTOFF_QSEARCH;
	    	
	    	return node.eval;
		}
		
		
		/*if (eval + 100 + 2 * 900 < alpha) {
			
	    	node.eval = eval;
			
	    	return node.eval;
		}*/
		
		int alphaOrig 	= alpha;
		
		alpha 			= Math.max(alpha, eval);
		
		int bestMove 	= 0;
		int bestScore 	= ISearch.MIN;
		
		IMoveList list 	= lists_attacks[ply];
		
		int phase 		= PHASE_ATTACKING_GOOD;
		
		while (phase <= PHASE_ATTACKING_GOOD) {
			
			switch (phase) {
			
			case PHASE_ATTACKING_GOOD:
				
				list.clear();
				env.getBitboard().genCapturePromotionMoves(list);
				
				break;
			}
			
			int move;
			while ((move = list.next()) != 0) {
				
				int see = env.getBitboard().getSEEScore(move);
				
				if (see < 0) {
					
					continue;
				}
				
				/*if (eval + 100 + 2 * see < alpha) {
					
					continue;
				}*/
				
				env.getBitboard().makeMoveForward(move);
				
				final int score = -qsearch(mediator, pvman, evaluator, info, -beta, -alpha, ply + 1, isPv);
				
				env.getBitboard().makeMoveBackward(move);
				
				
				if (score > bestScore) {
					
					bestMove = move;
					bestScore = score;
					
					node.bestmove = bestMove;
					node.eval = bestScore;
					node.leaf = false;
					
					if (ply + 1 < ISearch.MAX_DEPTH) {
						pvman.store(ply + 1, node, pvman.load(ply + 1), true);
					}
				}
				
				alpha = Math.max(alpha, bestScore);
				
				if (alpha >= beta) {
					
					phase += 379;
					
					break;
				}
			}
			
			phase++;
		}
		
		
		if (bestScore > eval) {
			
			if (node.eval != bestScore) {
				
				throw new IllegalStateException("node.eval != bestScore"); 
			}
			
			if (node.leaf) {
				
				throw new IllegalStateException("node.leaf"); 
			}
			
		} else {
			
			node.bestmove = 0;
			node.leaf = true;
			node.eval = eval;
			node.type = PVNode.TYPE_NORMAL_QSEARCH;
			
			bestScore = eval;
			bestMove = 0;
		}
		
		
		if (getSearchConfig().isOther_UseAlphaOptimizationInQSearch()) {
			
			if (alphaOrig > node.eval) {
				
				node.bestmove = 0;
				node.leaf = true;
				node.eval = alphaOrig;
				node.type = PVNode.TYPE_ALPHA_RESTORE_QSEARCH;
				
				bestScore = alphaOrig;
				bestMove = 0;
			}
		}
		
		
		if (env.getTPT() != null) {
			
			env.getTPT().put(hashkey, 0, bestScore, alphaOrig, beta, bestMove);
		}
		
		
		if (VALIDATE_PV) validatePV(node, evaluator, ply, 0, isPv, alphaOrig, beta);
		
		
    	return node.eval;
	}
	
	
	private int eval(IEvaluator evaluator, final int ply, final int alpha, final int beta, final boolean isPv) {
		
		/*int eval = evaluator.roughEval(ply,  -1);
		
		int error_window = (int) (LAZY_EVAL_MARGIN.getEntropy() + 3 * LAZY_EVAL_MARGIN.getDisperse());
		
		if (eval >= alpha - error_window && eval <= beta + error_window) {
			
			int rough_eval = eval;
			
			eval = evaluator.fullEval(ply, alpha, beta, -1);
			
			int diff = Math.abs(eval - rough_eval);
			
			LAZY_EVAL_MARGIN.addValue(diff);
		}*/
		
		
		int eval = evaluator.fullEval(ply, alpha, beta, -1);
		
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
	
	
	private boolean extractFromTT(int ply, PVNode result, ITTEntry entry, ISearchInfo info, boolean isPv) {
		
		if (entry.isEmpty()) {
			
			throw new IllegalStateException("entry.isEmpty()");
		}
		
		if (result == null) {
			
			return false;
		}
		
		result.leaf = true;
		result.type = PVNode.TYPE_TT;
		
		if (ply > 0
				&& isDraw(isPv)) {
	    	
			result.eval = getDrawScores(-1);
			
			result.bestmove = 0;
			
			return true;
	    }
		
		
		if (info != null && info.getSelDepth() < ply) {
			
			info.setSelDepth(ply);
		}
		
		result.eval = entry.getEval();
		result.bestmove = entry.getBestMove();
		
		
		boolean draw = false;
		
		if (env.getTPT() != null) {
			
			//if (isPv) {
				
				ply++;
				
				if (ply < ISearch.MAX_DEPTH) {
					
					if (result.bestmove != 0) {
						
						if (!env.getBitboard().isPossible(result.bestmove)) {
							
							throw new IllegalStateException("!env.getBitboard().isPossible(result.bestmove)");
						}
						
						env.getBitboard().makeMoveForward(result.bestmove);
						
						env.getTPT().get(getHashkeyTPT(), tt_entries_per_ply[ply]);
						
						if (!tt_entries_per_ply[ply].isEmpty()) {
							
							result.leaf = false;
							
							draw = extractFromTT(ply, result.child, tt_entries_per_ply[ply], info, isPv);
							
							
							if (draw) {
								
								result.eval = getDrawScores(-1);
							}
						}
						
						env.getBitboard().makeMoveBackward(result.bestmove);
						
					} else {
						
						//It is currently possible in positions with EGTB hit
						
						draw = (result.eval == getDrawScores(-1));
					}
				}
			}
		//}
		
		
		return draw;
	}
	
	
	private Stack<Integer> stack = new Stack<Integer>();
	
	
	private void validatePV(PVNode node, IEvaluator evaluator, int ply, int expectedDepth, boolean isPv, int alpha, int beta) {
		
		
		//First replay the moves
		int eval_sign = 1;
		int alpha_corrected = alpha;
		int beta_corrected = beta;
		
		int actualDepth = 0;
		
		PVNode cur = node;
		
		boolean tt_reached = false;
		
		while(cur != null && !cur.leaf) {
			
			if (cur.bestmove == 0) {
				
				throw new IllegalStateException("cur.bestmove == 0");
			}
			
			if (!tt_reached && node.eval != eval_sign * cur.eval) {
				
				//System.out.println("Not equal score in PV sequence. EVALDIFF=" + (node.eval - eval_sign * cur.eval));
				throw new IllegalStateException("node.eval != eval_sign * cur.eval");
			}
			
			//Only the first TT node has correct value as it is with expected depth
			if (cur.type == PVNode.TYPE_TT) {
				
				tt_reached = true;
			}
			
			actualDepth++;
			
			int colorToMove = env.getBitboard().getColourToMove();
			
			boolean isCheckMove = env.getBitboard().isCheckMove(cur.bestmove);
			
			if (env.getBitboard().isPossible(cur.bestmove)) {
				
				env.getBitboard().makeMoveForward(cur.bestmove);
				
				stack.push(cur.bestmove);
				
			} else {
				
				throw new IllegalStateException("not valid move " + env.getBitboard().getMoveOps().moveToString(cur.bestmove));
			}
			
			eval_sign *= -1;
			if (eval_sign == -1) {
				alpha_corrected = -beta;
				beta_corrected = -alpha;
			} else {
				alpha_corrected = alpha;
				beta_corrected = beta;
			}
					
			
			
			if (env.getBitboard().isInCheck(colorToMove)) {
				
				throw new IllegalStateException("In check after move");
			}
			
			if (isCheckMove) {
				
				if (!env.getBitboard().isInCheck()) {
					
					throw new IllegalStateException("Not in check after check move");
				}
			}
			
			if (cur.leaf) {
				
				break;
			}
			
			cur = cur.child;
		}
		
		
		//Do checks and dump statistics
		search_types_stats[cur.type]++;
		if (search_types_stats[cur.type] % 100000 == 0) {
		    for (int i = 0; i < search_types_stats.length; i++) {
		        System.out.print(i + "=" + search_types_stats[i] + " ");
		    }
		    System.out.println();
		}
		
		int static_eval = eval_sign * eval(evaluator, 0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, isPv);
		
		
		switch(cur.type) {
		
			case PVNode.TYPE_NORMAL_SEARCH:
				
				if (node.eval != static_eval) {
					
					throw new IllegalStateException("eval=" + node.eval + ", static_eval=" + static_eval);
				}
				
				if (actualDepth < expectedDepth) {
					
					throw new IllegalStateException("actualDepth=" + actualDepth + ", expectedDepth=" + expectedDepth);
				}
					
				break;
				
			case PVNode.TYPE_NORMAL_QSEARCH:
				
				if (node.eval != static_eval) {
					
					throw new IllegalStateException("eval=" + node.eval + ", static_eval=" + static_eval);
				}
				
				if (actualDepth < expectedDepth) {
					
					throw new IllegalStateException("actualDepth=" + actualDepth + ", expectedDepth=" + expectedDepth);
				}
				
				break;
				
			case PVNode.TYPE_DRAW:
				
				if (!isDraw(false)) {
					
					if (!env.getBitboard().isInCheck() && !env.getBitboard().hasMoveInNonCheck()) {
						
					} else {
						
						throw new IllegalStateException("!isDraw(isPv)");
					}
				}
				
				break;
				
			case PVNode.TYPE_MATE:
				
				if (env.getBitboard().isInCheck()) {
					
					if (env.getBitboard().hasMoveInCheck()) {
						
						throw new IllegalStateException("env.getBitboard().hasMoveInCheck()");
					}
					
				} else {
					
					throw new IllegalStateException("!env.getBitboard().isInCheck()");
				}
				
				break;
				
			case PVNode.TYPE_MAXDEPTH:
				
				if (node.eval != static_eval) {
					
					throw new IllegalStateException("eval=" + node.eval + ", static_eval=" + static_eval);
				}
				
				if (actualDepth < expectedDepth) {
					
					throw new IllegalStateException("actualDepth=" + actualDepth + ", expectedDepth=" + expectedDepth);
				}
				
				break;
				
			case PVNode.TYPE_MATE_DISTANCE_PRUNING:
				
				if (!SearchUtils.isMateVal(node.eval)) {
					
					throw new IllegalStateException("!SearchUtils.isMateVal(node.eval), node.eval=" + node.eval);
				}
				
				break;
				
			case PVNode.TYPE_TT:
				
				//TODO: Not working because TT score is changed meanwhile
				/*if (env.getTPT() != null) {
					
					env.getTPT().get(getHashkeyTPT(), tt_entries_per_ply[ply]);
					
					if (!tt_entries_per_ply[ply].isEmpty()) {
						
						int tt_eval = tt_entries_per_ply[ply].getEval();
						
						if (eval_sign * tt_eval != node.eval) {
							
							System.out.println("NODETYPE: " + cur.type + ", EVALDIFF=" + (node.eval - eval_sign * tt_eval) + ", score=" + node.eval + ", eval_sign * tt_eval=" + eval_sign * tt_eval);
							//throw new IllegalStateException("tt_eval != node.eval");
							
						} else {
							
							//System.out.println("OK");
						}
						
					} else {
						
						throw new IllegalStateException("tt_entries_per_ply[ply].isEmpty()");
					}
				}*/
				
				break;
				
			case PVNode.TYPE_TB:
				
				break;
				
			case PVNode.TYPE_STATIC_NULL_MOVE:
				
				if (node.eval != static_eval) {
					
					throw new IllegalStateException("eval=" + node.eval + ", static_eval=" + static_eval);
				}
				
				break;
				
			case PVNode.TYPE_NULL_MOVE:
				
				break;
				
			case PVNode.TYPE_RAZORING:
				
				break;
				
			case PVNode.TYPE_PROBECUT:
				
				break;
				
			case PVNode.TYPE_MULTICUT:
				
				break;
				
			case PVNode.TYPE_BETA_CUTOFF_QSEARCH:
				
				if (node.eval != static_eval) {
					
					throw new IllegalStateException("eval=" + node.eval + ", static_eval=" + static_eval);
				}
				
				if (actualDepth < expectedDepth) {
					
					throw new IllegalStateException("actualDepth=" + actualDepth + ", expectedDepth=" + expectedDepth);
				}
				
				break;
				
			case PVNode.TYPE_ALPHA_RESTORE_QSEARCH:
				
				if (cur.eval != alpha_corrected) {
					
					throw new IllegalStateException("DIFF alpha_corrected=" + alpha_corrected + ", beta_corrected=" + beta_corrected + ", cur.eval=" + cur.eval);
				}
				
				if (actualDepth < expectedDepth) {
					
					throw new IllegalStateException("actualDepth=" + actualDepth + ", expectedDepth=" + expectedDepth);
				}
				
				break;
				
			default:
				
				throw new IllegalStateException();
		}
		
		
		//Last, revert the moves
		try {
			
			Integer move;
			
			while ((move = stack.pop()) != null) {
				
				env.getBitboard().makeMoveBackward(move);
			}
			
		} catch (EmptyStackException ese) {
			
			//Do nothing
		}
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

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


import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.MaterialUtil;
import bagaturchess.bitboard.impl1.internal.MoveGenerator;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.SEEUtil;
import bagaturchess.egtb.syzygy.SyzygyConstants;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.SearchInterruptedException;

import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.tpt.ITTEntry;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.search.impl.eval.cache.EvalEntry_BaseImpl;
import bagaturchess.search.impl.eval.cache.IEvalEntry;


public class Search_PVS_NWS extends SearchImpl {
	
	
	private static final int PHASE_TT 							= 0;
	private static final int PHASE_ATTACKING_GOOD 				= 1;
	private static final int PHASE_KILLER_1 					= 2;
	private static final int PHASE_KILLER_2 					= 3;
	private static final int PHASE_COUNTER_1 					= 4;
	private static final int PHASE_COUNTER_2 					= 5;
	private static final int PHASE_QUIET 						= 6;
	private static final int PHASE_ATTACKING_BAD 				= 7;
	
	
	private static final int[] STATIC_NULLMOVE_MARGIN 			= { 0, 60, 130, 210, 300, 400, 510 };
	private static final int[] RAZORING_MARGIN 					= { 0, 240, 280, 300 };
	private static final int[] FUTILITY_MARGIN 					= { 0, 80, 170, 270, 380, 500, 630 };
	private static final int FUTILITY_MARGIN_Q_SEARCH_ATTACKS 	= 100;
	//private static final int FUTILITY_MARGIN_Q_SEARCH_QUIET		= 35;
	
	private static final int[][] LMR_TABLE 						= new int[64][64];
	
	static {
		
		for (int depth = 1; depth < 64; depth++) {
			
			for (int move_number = 1; move_number < 64; move_number++) {
				
				LMR_TABLE[depth][move_number] = (int) Math.ceil(Math.max(1, Math.log(move_number) * Math.log(depth) / (double) 2));
			}
		}
	}
	
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	
	private static final boolean USE_DTZ_CACHE 			= true;
	
	
	private IEvalEntry temp_cache_entry;
	
	
	private VarStatistic move_line_scores_w;
	private VarStatistic move_line_scores_b;
	private VarStatistic move_line_scores_diffs_org;
	private VarStatistic move_line_scores_diffs_scaled;
	private long move_line_distros_counter_w 			= 0;
	private long move_line_distros_counter_b 			= 0;
	
	//TODO: According to the graphics, the negative numbers' distribution looks better, so we may try to use -diff.
	private static final int MOVES_SCALE;
	private static final int MOVES_SCALE_EVAL_DIVIDER;
	static {
		if (EngineConstants.ENABLE_LMR_STATS) {
			
			MOVES_SCALE = 350; //2 * 350 + 1 = 701 units, // 2 * 200 + 1 = 401 units, // 2 * 50 + 1 = 101 units, 2 * 20 + 1 = 41 units, not always well balanced around 0.
			MOVES_SCALE_EVAL_DIVIDER = 7;
			
		}  else {
			
			MOVES_SCALE = 150;
			MOVES_SCALE_EVAL_DIVIDER = 3; //2 * 150 + 1 = 301 units
		}
	}
	
	private static final boolean MOVES_SCALE_DUMP		= false;

	private int[] move_line_distros_w 					= new int[MOVES_SCALE];
	private int[] move_line_distros_b 					= new int[MOVES_SCALE];
	private Map<Integer, Long> move_line_diffs 			= new TreeMap<Integer, Long>();
	
	//private static final double SQRT_2PI 				= Math.sqrt(2 * Math.PI);
	//private static final double RECIPROCAL_4PI			= 1 / (double) (4 * Math.PI);
	
	
	public Search_PVS_NWS(Object[] args) {
		
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_PVS_NWS(SearchEnv _env) {
		
		
		super(_env);
		
		
		if (USE_DTZ_CACHE) {
	    	
	    	temp_cache_entry = new EvalEntry_BaseImpl();
		}
	}
	
	
	public void newSearch() {
		
		
		super.newSearch();
		
		
		((BoardImpl) env.getBitboard()).getMoveGenerator().clearHistoryHeuristics();
		
		
		lastSentMinorInfo_nodesCount 	= 0;
		lastSentMinorInfo_timestamp 	= 0;
		
		
		move_line_scores_w 				= new VarStatistic();
		move_line_scores_b 				= new VarStatistic();
		move_line_scores_diffs_org 		= new VarStatistic();
		move_line_scores_diffs_scaled 	= new VarStatistic();
		move_line_distros_counter_w 	= 0;
		move_line_distros_counter_b 	= 0;
		
		
		if (ChannelManager.getChannel() != null) {
			
			if (env.getTPT() != null) ChannelManager.getChannel().dump("Search_PVS_NWS.newSearch: Transposition table hitrate=" + env.getTPT().getHitRate() + ", usage=" + env.getTPT().getUsage());
			
			if (env.getEvalCache() != null) ChannelManager.getChannel().dump("Search_PVS_NWS.newSearch: Evaluation cache hitrate=" + env.getEvalCache().getHitRate() + ", usage=" + env.getEvalCache().getUsage());
			
			if (env.getSyzygyDTZCache() != null) ChannelManager.getChannel().dump("Search_PVS_NWS.newSearch: Syzygy DTZ cache hitrate=" + env.getSyzygyDTZCache().getHitRate() + ", usage=" + env.getSyzygyDTZCache().getUsage());
		}
	}
	
	
	@Override
	public int pv_search(ISearchMediator mediator, PVManager pvman,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha_org, int beta, int prevbest, int prevprevbest,
			int[] prevPV, boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		
		return search(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, SearchUtils.normDepth(maxdepth), alpha_org, beta, true, 0, 0, 0);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, PVManager pvman, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		return search(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, SearchUtils.normDepth(maxdepth), beta - 1, beta, false, 0, 0, 0);		
	}
	
	
	public int search(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator, ChessBoard cb, MoveGenerator moveGen,
			final int ply, int depth, int alpha, int beta, boolean isPv, int pv_scores_w, int pv_scores_b, int excludedMove) {
		
		
		if (mediator != null && mediator.getStopper() != null) {
			mediator.getStopper().stopIfNecessary(ply + depth, env.getBitboard().getColourToMove(), alpha, beta);
		}
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		if (ply >= ISearch.MAX_DEPTH) {
			
			return eval(evaluator, ply, alpha, beta, isPv, pv_scores_w, pv_scores_b);
		}
		
		
		final int alphaOrig = alpha;
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		
		
	    if (ply > 0
	    		&& isDraw(isPv)) {
	    	
	    	node.eval = getDrawScores(-1);
	    			
	    	return node.eval;
	    }
		
		
		if (EngineConstants.ENABLE_MATE_DISTANCE_PRUNING) {
			if (ply > 0) {
				alpha = Math.max(alpha, -SearchUtils.getMateVal(ply));
				beta = Math.min(beta, +SearchUtils.getMateVal(ply + 1));
				if (alpha >= beta) {
					node.eval = alpha;
					return node.eval;
				}
			}
		}
		
		
		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0);
			Assert.isTrue(alpha >= ISearch.MIN && alpha <= ISearch.MAX);
			Assert.isTrue(beta >= ISearch.MIN && beta <= ISearch.MAX);
		}
		
		
		depth += extensions(cb, moveGen, ply);
		
		
		long hashkey = cb.zobristKey;
		if (excludedMove != 0) {
			hashkey ^= (((long)excludedMove) << 16);
		}
		
		
		int ttMove 									= 0;
		int ttFlag 									= -1;
		int ttValue 								= IEvaluator.MIN_EVAL;
		
		boolean isTTLowerBound 						= false;
		boolean isTTDepthEnoughForSingularExtension = false;
		
		if (env.getTPT() != null) {
			
			env.getTPT().get(hashkey, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				ttMove = tt_entries_per_ply[ply].getBestMove();
				ttFlag = tt_entries_per_ply[ply].getFlag();
				ttValue = tt_entries_per_ply[ply].getEval();
				
				int tpt_depth = tt_entries_per_ply[ply].getDepth();
				
				isTTLowerBound = ttFlag == ITTEntry.FLAG_LOWER;
				isTTDepthEnoughForSingularExtension = tt_entries_per_ply[ply].getDepth() >= depth / 2;
				
				if (getSearchConfig().isOther_UseTPTScores()) {
					
					if (tpt_depth >= depth) {
						
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
		
		
		if (ply >= 7
    			&& SyzygyTBProbing.getSingleton() != null
    			&& SyzygyTBProbing.getSingleton().isAvailable(env.getBitboard().getMaterialState().getPiecesCount())
    			){
			
			
			if (cb.checkingPieces != 0) {
				
				if (!env.getBitboard().hasMoveInCheck()) {
					
					node.bestmove = 0;
					node.eval = -SearchUtils.getMateVal(ply);
					node.leaf = true;
					
					return node.eval;
				}
				
			} else {
				
				if (!env.getBitboard().hasMoveInNonCheck()) {
					
					node.bestmove = 0;
					node.eval = node.eval = getDrawScores(-1);
					node.leaf = true;
					
					return node.eval;
				}
			}
			
			
			/*
			SYZYGY DOWNLOADS:
			http://tablebase.lichess.ovh/tables/standard/7/

			SYZYGY ONLINE:
			https://github.com/niklasf/lila-tablebase#http-api
			https://syzygy-tables.info/
			https://lichess.org/blog/W3WeMyQAACQAdfAL/7-piece-syzygy-tablebases-are-complete


			INFO SOURCE:
			https://www.chessprogramming.org/Syzygy_Bases

			Winner is minimizing DTZ and the loser is maximizing DTZ

			File Types
			Syzygy Bases consist of two sets of files,
			WDL files (extension .rtbw) storing win/draw/loss information considering the fifty-move rule for access during search,
			and DTZ files (extension .rtbz) with distance-to-zero information for access at the root.
			WDL has full data for two sides but DTZ50 omitted data of one side to save space. Each endgame has a pair of those types.

			Search
			During the Search:
			With the WDL tables stored on SSD [12] , it is possible to probe the tables at all depths without much slowdown.
			They have been tested in Ronald de Man's engine Sjaak (playing on FICS as TrojanKnight(C)) a couple of months quite successfully,
			don't probing in quiescence search.
			At the Root:
			Since pure DTZ50-optimal play (i.e. minimaxing the number of moves to the next capture or pawn move by either side) can be very unnatural,
			it might be desirable to let the engine search on the winning moves until it becomes clear that insufficient progress is being made
			and only then switch to DTZ-optimal play (e.g. by detecting repetitions and monitoring the halfmove clock) [13].


			Ronald de Man in a reply to Nguyen Pham, April 15, 2020 [28] :
			Syzygy WDL is double sided, DTZ is single sided.
			WDL:
			So to know whether a 7-piece position is winning, losing or drawn (or cursed),
			the engine needs to do only a single probe of a 7-piece WDL table.
			(It may in addition have to do some probes of 6-piece WDL tables if any direct captures are available.)
			DTZ:
			If the engine needs to know the DTZ value (which is only necessary when a TB root position has been reached),
			the probing code may have to do a 1-ply search to get to the "right" side of the DTZ table.
			For 6-piece TBs, DTZ is 81.9GB when storing only the smaller side of each table. Storing both sides might require perhaps 240GB.
			*/
			
			
			int probe_result = probeWDL_WithCache();
			
			if (probe_result != -1) {
				
				info.setTBhits(info.getTBhits() + 1);
							
				int wdl = (probe_result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				
				//Winner is minimizing DTZ and the loser is maximizing DTZ
		        switch (wdl) {
		        
	            	case SyzygyConstants.TB_WIN:
	            		
	    				//int dtz = (probe_result & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
	            		int dtz = SyzygyTBProbing.getSingleton().probeDTZ(env.getBitboard());
	            		
	    				if (dtz < 0) {
	    					
	    					/**
	    					 * In this not mate position "8/6P1/8/2kB2K1/8/8/8/4r3 w - - 1 19", DTZ is -1 and WDL is 2 (WIN).
	    					 */
	    					break;
	    					//throw new IllegalStateException("dtz=" + dtz + ", env.getBitboard()=" + env.getBitboard());
	    				}
	    				
						int distanceToDraw_50MoveRule = 99 - env.getBitboard().getDraw50movesRule();
						//Although we specify the rule50 parameter when calling SyzygyJNIBridge.probeSyzygyDTZ(...)
						//Syzygy TBs report winning score/move
						//but the +mate or +promotion moves line is longer
						//than the moves we have until draw with 50 move rule
						//! Without this check, the EGTB probing doesn't work correctly and the Bagatur version has smaller ELO rating (-35 ELO)
						if (distanceToDraw_50MoveRule >= dtz) {
							
							int op_factor = cb.colorToMoveInverse == Constants.COLOUR_WHITE ? env.getBitboard().getMaterialFactor().getWhiteFactor() : env.getBitboard().getMaterialFactor().getBlackFactor();
							
							//Opponent has only king so DTZ is DTM
							if (op_factor == 0 && !env.getBitboard().hasSufficientMatingMaterial(cb.colorToMoveInverse)) {
								
								node.bestmove = 0;
			    				//getMateVal with parameter set to 1 achieves max and with ISearch.MAX_DEPTH achieves min
			    				node.eval = SearchUtils.getMateVal(ply + dtz);
								node.leaf = true;
								
								return node.eval;
								
							} else {
								
								node.bestmove = 0;
								node.eval = IEvaluator.MAX_EVAL / (ply + dtz);
								//node.eval = 10 * 9 * (distanceToDraw_50MoveRule - dtz);
								//node.eval = 4 * 9 * ((ISearch.MAX_DEPTH - ply) + (distanceToDraw_50MoveRule - dtz));
								node.leaf = true;
								
								return node.eval;
							}
							
						} else {
							
							node.bestmove = 0;
							node.eval = node.eval = getDrawScores(-1);
							node.leaf = true;
							
							return node.eval;
						}
	            		
		            case SyzygyConstants.TB_LOSS:
		            	
	    				/*
	    				This code doesn't work at the moment
	    				node.bestmove = 0;
	    				//getMateVal with parameter set to 1 achieves max and with ISearch.MAX_DEPTH achieves min
	    				node.eval = -SearchUtils.getMateVal(ply + dtz);
	    				node.leaf = true;
	    				
	    				return node.eval;*/
		            	break;
		            
		            case SyzygyConstants.TB_DRAW:
		            	
						node.bestmove = 0;
						node.eval = node.eval = getDrawScores(-1);
						node.leaf = true;
						
						return node.eval;
		                
		            case SyzygyConstants.TB_BLESSED_LOSS:
		            	
						node.bestmove = 0;
						node.eval = node.eval = getDrawScores(-1);
						node.leaf = true;
						
						return node.eval;
		                //return -27000 + ply;
		                
		            case SyzygyConstants.TB_CURSED_WIN:
		            	
						node.bestmove = 0;
						node.eval = node.eval = getDrawScores(-1);
						node.leaf = true;
						
						return node.eval;
		                //return 27000 - ply;
		                
		            default:
		            	
		            	throw new IllegalStateException("wdl=" + wdl);
		                //return 0;
		        }
			}
        }
		
		
		if (depth == 0) {
			
			int qeval = qsearch(mediator, pvman, evaluator, info, cb, moveGen, alpha, beta, ply, isPv, pv_scores_w, pv_scores_b);
			
			if (node.eval != qeval) {
				
				throw new IllegalStateException();
			}
			
			return node.eval;
		}
		
		
		int eval = ISearch.MIN;
		
		if (!isPv && cb.checkingPieces == 0) {
			
			
			eval = eval(evaluator, ply, alphaOrig, beta, isPv, pv_scores_w, pv_scores_b);
			
			
			if (ttValue != IEvaluator.MIN_EVAL) {
				
				if (EngineConstants.USE_TT_SCORE_AS_EVAL && getSearchConfig().isOther_UseTPTScores()) {
					
					if (ttFlag == ITTEntry.FLAG_EXACT
							|| (ttFlag == ITTEntry.FLAG_UPPER && ttValue < eval)
							|| (ttFlag == ITTEntry.FLAG_LOWER && ttValue > eval)
						) {
						
						eval = ttValue;
					}
				}
			}
			
			
			if (ttFlag == -1 && depth >= 2) {
				
				depth -= 1;
			}
			
			
			if (eval >= beta) {
				
				
				if (EngineConstants.ENABLE_STATIC_NULL_MOVE && depth < STATIC_NULLMOVE_MARGIN.length) {
					if (eval - STATIC_NULLMOVE_MARGIN[depth] >= beta) {
						node.bestmove = 0;
						node.eval = eval;
						node.leaf = true;
						return node.eval;
					}
				}
				
				
				if (EngineConstants.ENABLE_NULL_MOVE && depth >= 3) {
					if (MaterialUtil.hasNonPawnPieces(cb.materialKey, cb.colorToMove)) {
						cb.doNullMove();
						final int reduction = depth / 4 + 3 + Math.min((eval - beta) / 80, 3);
						int score = depth - reduction <= 0 ? -qsearch(mediator, pvman, evaluator, info, cb, moveGen, -beta, -beta + 1, ply + 1, isPv, pv_scores_w, pv_scores_b)
								: -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -beta, -beta + 1, isPv, pv_scores_w, pv_scores_b, 0);
						cb.undoNullMove();
						if (score >= beta) {
							node.bestmove = 0;
							node.eval = score;
							node.leaf = true;
							return node.eval;
						}
					}
				}
				
			} else if (eval <= alpha && !SearchUtils.isMateVal(alpha)) {
				
				
				if (EngineConstants.ENABLE_RAZORING && depth < RAZORING_MARGIN.length) {
					if (eval + RAZORING_MARGIN[depth] < alpha) {
						int score = qsearch(mediator, pvman, evaluator, info, cb, moveGen, alpha - RAZORING_MARGIN[depth], alpha - RAZORING_MARGIN[depth] + 1, ply, isPv, pv_scores_w, pv_scores_b);
						if (score + RAZORING_MARGIN[depth] <= alpha) {
							node.bestmove = 0;
							node.eval = score;
							node.leaf = true;
							return node.eval;
						}
					}
				}
			}
		}
		
		
		//Singular move extension
		int singularMoveExtension = 0;
		int multiCutReduction = 0;
        if (false
        	&& ply > 0
        	&& depth >= 2
        	&& cb.checkingPieces == 0
			&& excludedMove == 0
			&& isTTLowerBound
			&& isTTDepthEnoughForSingularExtension
			) {
			
	        int singularBeta = 0; //ttValue - 50;
	        int reduction = depth / 2;
	        
	        int singularValue = search(mediator, info, pvman, evaluator, cb, moveGen, ply,
	        		depth - reduction, singularBeta - 1, singularBeta, isPv, pv_scores_w, pv_scores_b, ttMove);
	        if (singularValue < singularBeta) {
	        	
	        	singularMoveExtension = 1;
	        	
	        } else if (singularBeta > beta) {
	        	
    	        // Multi-cut pruning
    	        // Our ttMove is assumed to fail high, and now we failed high also on a reduced
    	        // search without the ttMove. So we assume this expected Cut-node is not singular,
    	        // that multiple moves fail high, and we can prune the whole subtree by returning
    	        // a soft bound.
	        	
	            multiCutReduction = 1;
	        }
		}
        
		
		final boolean wasInCheck = cb.checkingPieces != 0;
		
		final int parentMove = moveGen.previous();
		int bestMove = 0;
		int bestScore = ISearch.MIN - 1;
		int killer1Move = 0;
		int killer2Move = 0;
		int counterMove1 = 0;
		int counterMove2 = 0;
		int movesPerformed_attacks = 0;
		int movesPerformed_quiet = 0;
		
		moveGen.startPly();
		int phase = PHASE_TT;
		while (phase <= PHASE_ATTACKING_BAD) {
			
			switch (phase) {
			
			case PHASE_TT:
				
				if (ttMove != 0 && cb.isValidMove(ttMove)) {
					moveGen.addMove(ttMove);
					if (!env.getBitboard().getMoveOps().isCaptureOrPromotion(ttMove)) {
						moveGen.setHHScores(cb.colorToMove, parentMove);
					}
				}
				
				break;
				
			case PHASE_ATTACKING_GOOD:
				
				moveGen.generateAttacks(cb);
				moveGen.setMVVLVAScores(cb);
				moveGen.sort();
				
				break;
				
			case PHASE_KILLER_1:
				
				killer1Move = moveGen.getKiller1(cb.colorToMove, ply);
				if (killer1Move != 0 && killer1Move != ttMove && killer1Move != counterMove1 && killer1Move != counterMove2 && cb.isValidMove(killer1Move)) {
					moveGen.addMove(killer1Move);
					moveGen.setHHScores(cb.colorToMove, parentMove);
				}
				
				break;
				
			case PHASE_KILLER_2:
				
				killer2Move = moveGen.getKiller2(cb.colorToMove, ply);
				if (killer2Move != 0 && killer2Move != killer1Move && killer2Move != ttMove && killer2Move != counterMove1 && killer2Move != counterMove2 && cb.isValidMove(killer2Move)) {
					moveGen.addMove(killer2Move);
					moveGen.setHHScores(cb.colorToMove, parentMove);
				}
				
				break;
			
			case PHASE_COUNTER_1:
				
				counterMove1 = moveGen.getCounter1(cb.colorToMove, parentMove);
				if (counterMove1 != 0 && counterMove1 != ttMove && counterMove1 != killer1Move && counterMove1 != killer2Move && cb.isValidMove(counterMove1)) {
					moveGen.addMove(counterMove1);
					moveGen.setHHScores(cb.colorToMove, parentMove);
				}
				
				break;
				
			/*case PHASE_COUNTER_2:
				
				counterMove2 = moveGen.getCounter2(cb.colorToMove, parentMove);
				if (counterMove2 != 0 && counterMove2 != counterMove1 && counterMove2 != ttMove && counterMove2 != killer1Move && counterMove2 != killer2Move && cb.isValidMove(counterMove2)) {
					moveGen.addMove(counterMove2);
					moveGen.setHHScores(cb.colorToMove, parentMove);
				}
				
				break;
			*/
				
			case PHASE_QUIET:
				
				moveGen.generateMoves(cb);
				moveGen.setHHScores(cb.colorToMove, parentMove);
				moveGen.sort();
				
				break;
			
			case PHASE_ATTACKING_BAD:
				
				moveGen.generateAttacks(cb);
				moveGen.setMVVLVAScores(cb);
				moveGen.sort();
				
				break;
				
			}
			
			
			while (moveGen.hasNext()) {
				
				final int move = moveGen.next();
				
				if (!cb.isLegal(move)) {
					
					continue;
				}
				
				//For now the singular move extension is disabled
				/*if (move == excludedMove) {
					continue;
				}*/
				
				//Build and sent minor info
				if (ply == 0) {
					info.setCurrentMove(move);
					info.setCurrentMoveNumber((movesPerformed_attacks + movesPerformed_quiet + 1));
				}
				
				if (info.getSearchedNodes() >= lastSentMinorInfo_nodesCount + 50000 ) { //Check time on each 50 000 nodes
					
					long timestamp = System.currentTimeMillis();
					
					if (timestamp >= lastSentMinorInfo_timestamp + 1000)  {//Send info each second
					
						mediator.changedMinor(info);
						
						lastSentMinorInfo_timestamp = timestamp;
					}
					
					lastSentMinorInfo_nodesCount = info.getSearchedNodes();
				}
				
				if (phase == PHASE_ATTACKING_GOOD) {
					if (SEEUtil.getSeeCaptureScore(cb, move) < 0) {
						continue;
					}
				}
				
				if (phase == PHASE_ATTACKING_BAD) {
					if (SEEUtil.getSeeCaptureScore(cb, move) >= 0) {
						continue;
					}
				}
				
				if (phase == PHASE_QUIET) {
					if (move == ttMove || move == killer1Move || move == killer2Move || move == counterMove1 || move == counterMove2) {
						continue;
					}
				} else if (phase == PHASE_ATTACKING_GOOD || phase == PHASE_ATTACKING_BAD) {
					if (move == ttMove) {
						continue;
					}
				}
				
				if (!isPv
						&& depth <= 7
						&& !wasInCheck && movesPerformed_attacks + movesPerformed_quiet > 0 && !cb.isDiscoveredMove(MoveUtil.getFromIndex(move))
						&& !SearchUtils.isMateVal(alpha) && !SearchUtils.isMateVal(beta)
					) {
					
					if (phase == PHASE_QUIET
							&& (!EngineConstants.ENABLE_LMP_STATS_DECISION
									|| (EngineConstants.ENABLE_LMP_STATS_DECISION && moveGen.getLMR_Rate(cb.colorToMove, move) <= moveGen.getLMR_ThreasholdPointer_BelowAlpha(cb.colorToMove)))
						) {
						
						if (EngineConstants.ENABLE_LMP
								&& movesPerformed_attacks + movesPerformed_quiet >= depth * 3 + 3
							) {
							
							continue;
						}
						
						if (EngineConstants.ENABLE_FUTILITY_PRUNING && depth < FUTILITY_MARGIN.length) {
							
							if (eval + FUTILITY_MARGIN[depth] <= alpha) {
								
								continue;
							}
						}	
						
						if (eval == ISearch.MIN) {
							
							throw new IllegalStateException("eval == ISearch.MIN");
						}
						
						/*if (eval + getTrustWindow(mediator, depth) <= alpha) {
							
							continue;
						}
						*/
						
					} else if (EngineConstants.ENABLE_SEE_PRUNING
							&& phase == PHASE_ATTACKING_BAD
							&& SEEUtil.getSeeCaptureScore(cb, move) < -20 * depth * depth
						) {
						
						continue;
					}
				}
				
				
				int new_pv_scores_w = (int) (cb.colorToMove == Constants.COLOUR_WHITE ? pv_scores_w + (env.getBitboard().getMoveOps().isCaptureOrPromotion(move) ? 0 : moveGen.getScore()) : pv_scores_w);
				int new_pv_scores_b = (int) (cb.colorToMove == Constants.COLOUR_BLACK ? pv_scores_b + (env.getBitboard().getMoveOps().isCaptureOrPromotion(move) ? 0 : moveGen.getScore()) : pv_scores_b);
				
				env.getBitboard().makeMoveForward(move);
								
				
				if (MoveUtil.isQuiet(move)) {
					movesPerformed_quiet++;
				} else {
					movesPerformed_attacks++;
				}
				
				int score = alpha + 1;
				
				if (EngineConstants.ASSERT) {
					cb.changeSideToMove();
					Assert.isTrue(0 == CheckUtil.getCheckingPieces(cb));
					cb.changeSideToMove();
				}
				
				
				/*System.out.println("COLOR " + cb.colorToMoveInverse
						+ ", moveGen.getLMR_Rate(cb.colorToMoveInverse, move) =" + moveGen.getLMR_Rate(cb.colorToMoveInverse, move)
						+ ", moveGen.getLMR_ThreasholdPointer_AboveAlpha(cb.colorToMoveInverse)=" + moveGen.getLMR_ThreasholdPointer_AboveAlpha(cb.colorToMoveInverse)
						+ ", moveGen.getLMR_ThreasholdPointer_BelowAlpha(cb.colorToMoveInverse)=" + moveGen.getLMR_ThreasholdPointer_BelowAlpha(cb.colorToMoveInverse));
				*/
				
				/*System.out.println("COLOR " + cb.colorToMoveInverse
						+ ", moveGen.getLMR_Stat_Rate() =" + moveGen.getLMR_Stat_Rate()
						);
				*/
				
				boolean doLMR = depth >= 2
							&& movesPerformed_attacks + movesPerformed_quiet > 1
							//&& !env.getBitboard().getMoveOps().isCaptureOrPromotion(move)
							//&& (phase == PHASE_QUIET || phase == PHASE_KILLER_1 || phase == PHASE_KILLER_2)
							&& phase == PHASE_QUIET
							;
				
				int reduction = 1;
				if (doLMR
						&& (!EngineConstants.ENABLE_LMR_STATS_DECISION
								|| EngineConstants.ENABLE_LMR_STATS_DECISION && (Math.random() <= 0.10 || moveGen.getLMR_Rate(cb.colorToMoveInverse, move) <= moveGen.getLMR_ThreasholdPointer_BelowAlpha(cb.colorToMoveInverse)))
						) {
					
					reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(movesPerformed_attacks + movesPerformed_quiet, 63)];
					
					if (!isPv) {
						
						reduction += 1;
					}
					
					reduction += singularMoveExtension;
					reduction += multiCutReduction;
					
					reduction = Math.min(depth - 1, Math.max(reduction, 1));
				}
				
				try {
					
					if (EngineConstants.ENABLE_LMR && reduction != 1) {
						
						moveGen.addLMR_All(cb.colorToMoveInverse, move, depth);
												
						score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -alpha - 1, -alpha, false, new_pv_scores_w, new_pv_scores_b, 0);
						
						if (score > alpha) {
							
							moveGen.addLMR_AboveAlpha(cb.colorToMoveInverse, move, depth);
							
						} else {
							
							moveGen.addLMR_BelowAlpha(cb.colorToMoveInverse, move, depth);
						}
					}
					
					if (EngineConstants.ENABLE_PVS && score > alpha && movesPerformed_attacks + movesPerformed_quiet > 1) {
						
						score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1 - multiCutReduction, -alpha - 1, -alpha, false, new_pv_scores_w, new_pv_scores_b, 0);
					}
					
					if (score > alpha) {
						
						if (move == ttMove) {
							
							score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1 + singularMoveExtension - multiCutReduction, -beta, -alpha, isPv, new_pv_scores_w, new_pv_scores_b, 0);
							
						} else {
							
							score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1 - multiCutReduction, -beta, -alpha, isPv, new_pv_scores_w, new_pv_scores_b, 0);
						}
					}
					
				} catch(SearchInterruptedException sie) {
					
					moveGen.endPly();
					
					throw sie;
				}
				
				
				env.getBitboard().makeMoveBackward(move);
				
				
				if (MoveUtil.isQuiet(move) && cb.checkingPieces == 0) {
					
					moveGen.addBFValue(cb.colorToMove, move, parentMove, depth);
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
					
					alpha = Math.max(alpha, score);
					if (alpha >= beta) {
						
						if (MoveUtil.isQuiet(bestMove) && cb.checkingPieces == 0) {
							moveGen.addCounterMove(cb.colorToMove, parentMove, bestMove);
							moveGen.addKillerMove(cb.colorToMove, bestMove, ply);
							moveGen.addHHValue(cb.colorToMove, bestMove, parentMove, depth);
						}
						
						phase += 379;
						
						break;
					}
				}
			}
			
			phase++;
		}
		
		moveGen.endPly();
		
		
		if (movesPerformed_attacks + movesPerformed_quiet == 0) {
			
			if (cb.checkingPieces == 0) {
				
				node.bestmove = 0;
				node.eval = getDrawScores(-1);
				node.leaf = true;
				
				return node.eval;
				
			} else {
				
				node.bestmove = 0;
				node.eval = -SearchUtils.getMateVal(ply);
				node.leaf = true;
				
				return node.eval;
			}
		}
		
		
		if (EngineConstants.ASSERT) {
			
			Assert.isTrue(bestMove != 0);
		}
		
		
		if (env.getTPT() != null) {
			
			env.getTPT().put(hashkey, depth, bestScore, alphaOrig, beta, bestMove);
		}
			
		
		if (bestScore != node.eval) {
			
			throw new IllegalStateException();
		}
		
		//validatePV(node, evaluator, node.eval, ply, depth, isPv, false);
		
		
		return bestScore;
	}
	
	
	public int qsearch(ISearchMediator mediator, PVManager pvman, IEvaluator evaluator, ISearchInfo info, final ChessBoard cb, final MoveGenerator moveGen,
			int alpha, final int beta, final int ply, final boolean isPv, int pv_scores_w, int pv_scores_b) {
		
		if (cb.checkingPieces != 0) {
			//return search(mediator, info, pvman, evaluator, cb, moveGen, ply, 0, alpha, beta, isPv, pv_scores_w, pv_scores_b, 0);
			return alpha;
		}
		
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		if (ply >= ISearch.MAX_DEPTH) {
			
			return eval(evaluator, ply, alpha, beta, isPv, pv_scores_w, pv_scores_b);
		}
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		
		if (ply > 0
	    		&& isDraw(isPv)) {
	    	
	    	node.eval = getDrawScores(-1);
	    	
	    	return node.eval;
	    }
	    
	    
		int ttMove 		= 0;
	    int ttFlag 		= -1;
	    int ttValue 	= IEvaluator.MIN_EVAL;

		
		if (env.getTPT() != null) {
			
			env.getTPT().get(cb.zobristKey, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				ttValue = tt_entries_per_ply[ply].getEval();
				ttFlag = tt_entries_per_ply[ply].getFlag();
				
				if (getSearchConfig().isOther_UseTPTScores()) {
					
					if (ttFlag == ITTEntry.FLAG_EXACT) {
						
				    	node.eval = ttValue;
						
				    	return node.eval;
				    	
					} else {
						
						if (ttFlag == ITTEntry.FLAG_LOWER && ttValue >= beta) {
							
					    	node.eval = ttValue;
							
					    	return node.eval;
						}
						
						if (ttFlag == ITTEntry.FLAG_UPPER && ttValue <= alpha) {
							
					    	node.eval = ttValue;
							
					    	return node.eval;
						}
					}
				}
				
				ttMove = tt_entries_per_ply[ply].getBestMove();
			}
		}
		
		
		int eval = eval(evaluator, ply, alpha, beta, isPv, pv_scores_w, pv_scores_b);
		
		if (ttValue != IEvaluator.MIN_EVAL) {
			
			if (EngineConstants.USE_TT_SCORE_AS_EVAL && getSearchConfig().isOther_UseTPTScores()) {
				
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
			
	    	return node.eval;
		}
		
		int material_queen = (int) Math.max(getEnv().getBitboard().getBoardConfig().getMaterial_QUEEN_O(), getEnv().getBitboard().getBoardConfig().getMaterial_QUEEN_E());
		
		if (eval + FUTILITY_MARGIN_Q_SEARCH_ATTACKS + material_queen < alpha) {
			
	    	node.eval = eval;
			
	    	return node.eval;
		}
		
		final int alphaOrig = alpha;
		
		alpha = Math.max(alpha, eval);
		
		int bestMove = 0;
		int bestScore = ISearch.MIN;
		
		moveGen.startPly();
		
		int phase = PHASE_TT;
		
		while (phase <= PHASE_ATTACKING_GOOD) {
			
			switch (phase) {
			
				case PHASE_TT:
					
					if (ttMove != 0 && cb.isValidMove(ttMove)) {
						moveGen.addMove(ttMove);
					}
					
					break;
					
				case PHASE_ATTACKING_GOOD:
					
					moveGen.generateAttacks(cb);
					moveGen.setMVVLVAScores(cb);
					moveGen.sort();
					
					break;
			}
			
			
			while (moveGen.hasNext()) {
				
				final int move = moveGen.next();
				
				if (!cb.isLegal(move)) {
					
					continue;
				}
				
				if (phase == PHASE_ATTACKING_GOOD) {
					if (move == ttMove) {
						continue;
					}
				}
				
				int see = SEEUtil.getSeeCaptureScore(cb, move);
				if (see < 0) {
					continue;
				}
				
				if (env.getBitboard().getMoveOps().isCaptureOrPromotion(move)) {
					
					int material_gain = getEnv().getBitboard().getBaseEvaluation().getMaterialGain(move);
					
					if (eval + FUTILITY_MARGIN_Q_SEARCH_ATTACKS + material_gain < alpha) {
						continue;
					}
				} /*else {
					countNotAttacking++;
					if (countNotAttacking >= 3) {
						break;
					}
					if (eval + FUTILITY_MARGIN_Q_SEARCH_QUIET < alpha) {
						continue;
					}
				}*/
				
				env.getBitboard().makeMoveForward(move);
				
				final int score = -qsearch(mediator, pvman, evaluator, info, cb, moveGen, -beta, -alpha, ply + 1, isPv, pv_scores_w, pv_scores_b);
				
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
					
					alpha = Math.max(alpha, score);
					
					if (alpha >= beta) {
						
						phase += 379;
						
						break;
					}
				}
			}
			
			phase++;
		}
		
		moveGen.endPly();
		
		
		if (bestScore > eval) {
			
			if (node.eval != bestScore) {
				
				throw new IllegalStateException(); 
			}
			
			if (node.leaf) {
				
				throw new IllegalStateException(); 
			}
			
			//TODO: Maybe this puts are too often and SMP version could have [problems 10 times lower NPS - to check
			if (env.getTPT() != null) {
					
				env.getTPT().put(cb.zobristKey, 0, bestScore, alphaOrig, beta, bestMove);
			}
			
		} else {
			
			node.bestmove = 0;
			node.leaf = true;
			node.eval = eval;
		}
		
		
		if (getSearchConfig().isOther_UseAlphaOptimizationInQSearch()) {
			
			if (alpha > node.eval) {
				
				node.bestmove = 0;
				node.leaf = true;
				node.eval = alpha;
			}
		}
		
		
		//validatePV(node, evaluator, node.eval, ply, ply, isPv, true);
		
		
    	return node.eval;
	}
	
	
	private int extensions(final ChessBoard cb, final MoveGenerator moveGen, final int ply) {
		
		if (EngineConstants.ENABLE_CHECK_EXTENSION && cb.checkingPieces != 0) {
			
			return 1;
		}
		
		return 0;
	}
	
	
	private int eval(IEvaluator evaluator, final int ply, final int alpha, final int beta, final boolean isPv, int pv_scores_w, int pv_scores_b) {
		
		
		int eval;
		
		/**
		 * TODO: Use material eval + moves_scores to predict eval and then call full eval only if the forecast is out of alpha - TOLERANCE and beta + TOLERANCE bounds.
		 */
		eval = evaluator.fullEval(ply, alpha, beta, 0);
		
		if (EngineConstants.USE_MOVE_SCORE_AS_EVAL) {
			
			eval += getMovesScores(ply, pv_scores_w, pv_scores_b) / MOVES_SCALE_EVAL_DIVIDER;
		}
		
		
		if (!env.getBitboard().hasSufficientMatingMaterial(env.getBitboard().getColourToMove())) {
			
			eval = Math.min(0, eval);
		}
		
		
		return eval;
	}


	private int getTrustWindow(ISearchMediator mediator, int depth) {
		
		int value = 4 * depth * Math.max(1, mediator.getTrustWindow_AlphaAspiration());
		//int value = 1 * depth * Math.max(1, mediator.getTrustWindow_AlphaAspiration());
		
		//System.out.println("mediator.getTrustWindow_AlphaAspiration()=" + mediator.getTrustWindow_AlphaAspiration()
		//						+ ", depth=" + depth + ", value=" + value);
		
		return value;
	}
	
	
	private int getMovesScores(final int ply, int pv_scores_w, int pv_scores_b) {
		
		
		int moves_score = 0;
		
			
		boolean white = env.getBitboard().getColourToMove() == Constants.COLOUR_WHITE;
		
		if (white)  {
			
			/**
			 * WHITE
			 */
			
			pv_scores_w = inverse_linner(pv_scores_w);
			//pv_scores_w = (int) inverse_linner_stdev(pv_scores_w, move_line_scores_w);
			//pv_scores_w = (int) inverse_Gaussian(pv_scores_w, move_line_scores_w);
			
			if (pv_scores_w < 0) {
				
				throw new IllegalStateException("pv_scores_w=" + pv_scores_w);
			}
			
			move_line_scores_w.addValue(pv_scores_w);
			
			pv_scores_w = Math.min(move_line_distros_w.length - 1, Math.max(0, pv_scores_w));
			
			
			if (MOVES_SCALE_DUMP) {
				
				move_line_distros_w[pv_scores_w]++;
				move_line_distros_counter_w++;
			}
		
		} else {
			
			/**
			 * BLACK
			 */
			
			pv_scores_b = inverse_linner(pv_scores_b);
			//pv_scores_b = (int) inverse_linner_stdev(pv_scores_b, move_line_scores_b);
			//pv_scores_b = (int) inverse_Gaussian(pv_scores_b, move_line_scores_b);
			
			if (pv_scores_b < 0) {
				
				throw new IllegalStateException("pv_scores_b=" + pv_scores_b);
			}
			
			move_line_scores_b.addValue(pv_scores_b);
			
			pv_scores_b = Math.min(move_line_distros_b.length - 1, Math.max(0, pv_scores_b));
			
			
			if (MOVES_SCALE_DUMP) {
				
				move_line_distros_b[pv_scores_b]++;
				move_line_distros_counter_b++;
			}
		}
		
		
		/**
		 * Adjust static evaluation
		 * 
		 * TODO: According to the graphics, the negative numbers' distribution looks better, so we may try to use -diff.
		 */
		
		if (white) {
			
			moves_score = (+pv_scores_w -pv_scores_b);

		} else {
			
			moves_score = (+pv_scores_b -pv_scores_w);
		}
		
		moves_score = moves_score / ply;
		
		moves_score = Math.min(move_line_distros_b.length - 1, Math.max(-(move_line_distros_b.length - 1), moves_score));
		
		
		if (MOVES_SCALE_DUMP) move_line_scores_diffs_org.addValue(moves_score);
		
		
		//moves_score = inverse_linner_normalized(moves_score);
		moves_score = inverse_linner(moves_score);
		
		
		if (MOVES_SCALE_DUMP) move_line_scores_diffs_scaled.addValue(moves_score);
		
		
		if (MOVES_SCALE_DUMP) {
			
			//System.out.println("moves_score=" + moves_score);
			
			Long value = move_line_diffs.get(moves_score);
			
			if (value == null) {
				value = new Long(0);
			}
			
			move_line_diffs.put(moves_score, value + 1);
		}
		
		
		/**
		 * DUMP DATA
		 */
		if (MOVES_SCALE_DUMP
				&& move_line_distros_counter_w > 1000000
				&& move_line_distros_counter_b > 1000000
			) {
			
			
			String dump = "**************************************************************";
			
			
			dump += "!!! SCORE DIFFS !!!\r\n";
			
			List<Integer> list_keys = new ArrayList<Integer>();
			List<Long> list_values = new ArrayList<Long>();
			Set<Integer> keys = move_line_diffs.keySet();
			for (Integer key : keys) {
				Long val = move_line_diffs.get(key);
				list_keys.add(key);
				list_values.add(val);
			}
			for (int i = 0; i < list_keys.size(); i++) {
				//dump += list_keys.get(i) + "=" + list_values.get(i) + "\r\n";
				dump += list_values.get(i) + "\r\n";
			}
			
			
			dump += "!!! WHITE !!!\r\n";
			
			for (int i = 0; i < move_line_distros_w.length; i++) {
				dump += move_line_distros_w[i] + "\r\n";
			}
			
			
			dump += "!!! BLACK !!!\r\n";
			for (int i = 0; i < move_line_distros_b.length; i++) {
				dump += move_line_distros_b[i] + "\r\n";
			}
			
			
			System.out.println(dump);
			System.out.println("WHITE_STATS		=	" + move_line_scores_w);
			System.out.println("BLACK_STATS		=	" + move_line_scores_b);
			System.out.println("DIFF_STATS_ORG		=	" + move_line_scores_diffs_org);
			System.out.println("DIFF_STATS_SCALED	=	" + move_line_scores_diffs_scaled);
			
			System.exit(0);
		}
		
		
		return moves_score;
	}


	private int inverse_linner(int pv_scores) {
		
		return pv_scores;
	}
	
	
	private int inverse_linner_normalized(int pv_scores) {
		
		return (int) ((pv_scores - move_line_scores_diffs_scaled.getEntropy()) / Math.max(1, move_line_scores_diffs_scaled.getDisperse()));
		//return (int) (pv_scores - move_line_scores_diffs_scaled.getEntropy());
	}
	
	
	private int inverse_linner_stdev(int pv_scores, VarStatistic move_line_scores) {
		
		return (int) (pv_scores / (double) (0.2 * Math.max(1, move_line_scores.getDisperse())));
	}
	
	
	public double inverse_Gaussian(double pv_scores, VarStatistic move_line_scores) {
		
		
		//https://en.wikipedia.org/wiki/Inverse_Gaussian_distribution
		//!!! http://hyperphysics.phy-astr.gsu.edu/hbase/Math/gaufcn.html
		
		
		double mean 		= move_line_scores.getEntropy();
		double stdev 		= move_line_scores.getDisperse();
		
		double y 			= pv_scores;
		
		double x 			= MOVES_SCALE
							
							* Math.sqrt(
				
									stdev / (2 * Math.PI * y * y * y)
								)
				
							* Math.exp(								
									-(stdev * (y - mean) * (y - mean)) / (2 * mean * mean* y)
								)
							;
		
		pv_scores 		= (int) x;
		
		return pv_scores;
		
	    /*double y = sample * sample;
	    double x = mu + (mu * mu * y) / (2 * lambda) - (mu / (2 * lambda)) * Math.sqrt(4 * mu * lambda * y + mu * mu * y * y);
	    return x;
	    */
	}
	
	
	private boolean extractFromTT(int ply, PVNode result, ITTEntry entry, ISearchInfo info, boolean isPv) {
		
		if (entry.isEmpty()) {
			
			throw new IllegalStateException("entry.isEmpty()");
		}
		
		if (result == null) {
			
			return false;
		}
		
		result.leaf = true;
		
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
			
			if (isPv) {
				
				ply++;
				
				if (ply < ISearch.MAX_DEPTH) {
					
					if (!env.getBitboard().isPossible(result.bestmove)) {
						
						throw new IllegalStateException("!env.getBitboard().isPossible(result.bestmove)");
					}
					
					env.getBitboard().makeMoveForward(result.bestmove);
					
					env.getTPT().get(env.getBitboard().getHashKey(), tt_entries_per_ply[ply]);
					
					if (!tt_entries_per_ply[ply].isEmpty()) {
						
						
						draw = extractFromTT(ply, result.child, tt_entries_per_ply[ply], info, isPv);
						
						
						if (draw) {
							
							result.eval = getDrawScores(-1);
							
						} else {
							
							result.leaf = false;
						}
					}
					
					env.getBitboard().makeMoveBackward(result.bestmove);
				}
			}
		}
		
		
		return draw;
	}
	
	
	private Stack<Integer> stack = new Stack<Integer>();
	
	
	private void validatePV(PVNode node, IEvaluator evaluator, int eval, int ply, int expectedDepth, boolean isPv, boolean qsearch) {
		
		
		if (!qsearch) {
			
			if (node.leaf || node.bestmove == 0) {
				
				throw new IllegalStateException();
			}
		}
		
		
		if (env.getTPT() != null) {
			
			env.getTPT().get(getEnv().getBitboard().getHashKey(), tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				int tt_eval = tt_entries_per_ply[ply].getEval();
				
				if (tt_eval == eval) {
					
					//System.out.println("OK");
					return;
				}
			}
		}
		
		
		int eval_sign = 1;
		
		int actualDepth = 0;
		
		PVNode cur = node;
		
		while(cur != null && cur.bestmove != 0) {
			
			actualDepth++;
			
			int colorToMove = env.getBitboard().getColourToMove();
			
			boolean wasInCheck = env.getBitboard().isInCheck();
			
			boolean isCheckMove = env.getBitboard().isCheckMove(cur.bestmove);
			
			if (env.getBitboard().isPossible(cur.bestmove)) {
				
				env.getBitboard().makeMoveForward(cur.bestmove);
				
				stack.push(cur.bestmove);
				
			} else {
				
				throw new IllegalStateException("not valid move " + env.getBitboard().getMoveOps().moveToString(cur.bestmove));
			}
			
			eval_sign *= -1;
			
			if (wasInCheck) {
				
				if (env.getBitboard().isInCheck(colorToMove)) {
					
					throw new IllegalStateException("In check after move");
				}
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
		
		
		if (isPv) {
			
			int static_eval = eval_sign * eval(evaluator, 0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, isPv, 0, 0);
			
			if (!qsearch || !getSearchConfig().isOther_UseAlphaOptimizationInQSearch()) {
				
				if (eval != static_eval) {
					
					System.out.println("EVALDIFF=" + (eval - static_eval) + ", eval=" + eval + ", static_eval=" + static_eval);
					//throw new IllegalStateException("eval=" + eval + ", static_eval=" + static_eval);
				}
			}
		}


		
		//if (actualDepth < expectedDepth) {
			//if (isPv) {
				/*if (!isDraw()) {
					if (env.getBitboard().isInCheck()) {
						if (env.getBitboard().hasMoveInCheck()) {
							//throw new IllegalStateException("actualDepth=" + actualDepth + ", expectedDepth=" + expectedDepth);
							System.out.println("NOT ok in check");	
						}
					} else {
						if (env.getBitboard().hasMoveInNonCheck()) {
							//throw new IllegalStateException("actualDepth=" + actualDepth + ", expectedDepth=" + expectedDepth);
							System.out.println("NOT ok in noncheck");
						}
					}
				}*/
			//}
		//}
		
		
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
		    
		    long hashkey = hash50movesRule ^ env.getBitboard().getHashKey();
		    
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

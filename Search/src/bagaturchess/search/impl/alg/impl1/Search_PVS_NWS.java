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

import bagaturchess.search.impl.alg.BacktrackingInfo;
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
	
	
	private static final int PHASE_TT 								= 0;
	private static final int PHASE_ATTACKING_GOOD 					= 1;
	private static final int PHASE_KILLER_1 						= 2;
	private static final int PHASE_KILLER_2 						= 3;
	private static final int PHASE_COUNTER_1 						= 4;
	private static final int PHASE_COUNTER_2 						= 5;
	private static final int PHASE_ATTACKING_BAD 					= 6;
	private static final int PHASE_QUIET 							= 7;
	
	private static final int[][] LMR_TABLE 							= new int[64][64];
	
	static {
		
		for (int depth = 1; depth < 64; depth++) {
			
			for (int move_number = 1; move_number < 64; move_number++) {
				
				LMR_TABLE[depth][move_number] = (int) Math.ceil(Math.max(1, Math.log(move_number) * Math.log(depth) / (double) 2));
			}
		}
	}
	
	
	private static final int FUTILITY_MARGIN 						= 80;
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	
	private static final boolean USE_DTZ_CACHE 						= true;
	
	
	private IEvalEntry temp_cache_entry;
	
	private BacktrackingInfo[] search_info 							= new BacktrackingInfo[MAX_DEPTH + 1];
	
	
	public Search_PVS_NWS(Object[] args) {
		
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_PVS_NWS(SearchEnv _env) {
		
		
		super(_env);
		
		
		if (USE_DTZ_CACHE) {
	    	
	    	temp_cache_entry = new EvalEntry_BaseImpl();
		}
		
		for (int i=0; i<search_info.length; i++) {
			
			search_info[i] = new BacktrackingInfo(); 
		}
	}
	
	
	@Override
	protected boolean useTPTKeyWithMoveCounter() {
		
		return false;
	}
	
	
	public void newSearch() {
		
		
		super.newSearch();
		
		
		((BoardImpl) env.getBitboard()).getMoveGenerator().clearHistoryHeuristics();
		
		
		lastSentMinorInfo_nodesCount 	= 0;
		lastSentMinorInfo_timestamp 	= 0;
		
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
		
		MoveGenerator moveGen = ((BoardImpl) env.getBitboard()).getMoveGenerator();
		
		moveGen.setRootSearchFirstMoveIndex(root_search_first_move_index);
		
		return root_search(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				moveGen, 0, SearchUtils.normDepth(maxdepth), alpha_org, beta, true, SearchUtils.normDepth(initial_maxdepth));
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, PVManager pvman, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		MoveGenerator moveGen = ((BoardImpl) env.getBitboard()).getMoveGenerator();
		
		moveGen.setRootSearchFirstMoveIndex(root_search_first_move_index);
		
		return root_search(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				moveGen, 0, SearchUtils.normDepth(maxdepth), beta - 1, beta, false, SearchUtils.normDepth(initial_maxdepth));		
	}
	
	
	private int root_search(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator, ChessBoard cb, MoveGenerator moveGen,
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
		
		
		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0);
			Assert.isTrue(alpha >= ISearch.MIN && alpha <= ISearch.MAX);
			Assert.isTrue(beta >= ISearch.MIN && beta <= ISearch.MAX);
		}
		
		
		long hashkey = getHashkeyTPT(cb);
		
		
		int ttMove = 0;
		
		if (env.getTPT() != null) {
			
			env.getTPT().get(hashkey, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				ttMove = tt_entries_per_ply[ply].getBestMove();
			}
		}
		
		
		final int parentMove = moveGen.previous();
		int bestMove = 0;
		int bestScore = ISearch.MIN;
		
		int movesPerformed_attacks = 0;
		int movesPerformed_quiet = 0;
		
		
		moveGen.startPly();
		
		
		moveGen.generateMoves(cb);
		moveGen.generateAttacks(cb);
		moveGen.setRootScores(cb, parentMove, ttMove, ply);
		moveGen.sort();
			
			
		while (moveGen.hasNext()) {
			
			final int move = moveGen.next();
			
			if (!cb.isLegal(move)) {
				
				continue;
			}
			
			//Build and sent minor info
			info.setCurrentMove(move);
			info.setCurrentMoveNumber((movesPerformed_attacks + movesPerformed_quiet + 1));
			
			
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
			
			
			boolean doLMR = depth >= 2
						&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& MoveUtil.isQuiet(move);
			
			int reduction = 1;
			if (doLMR) {
				
				reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(movesPerformed_attacks + movesPerformed_quiet, 63)];
				
				reduction = Math.min(depth - 1, Math.max(reduction, 1));
			}
			
			
			try {
				
				if (EngineConstants.ENABLE_LMR && reduction != 1) {
										
					score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -alpha - 1, -alpha, false, initialMaxDepth);
				}
				
				if (EngineConstants.ENABLE_PVS && score > alpha && movesPerformed_attacks + movesPerformed_quiet > 1) {
					
					score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -alpha - 1, -alpha, false, initialMaxDepth);
				}
				
				if (score > alpha) {
						
					score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -beta, -alpha, isPv, initialMaxDepth);
				}
				
			} catch(SearchInterruptedException sie) {
				
				moveGen.endPly();
				
				throw sie;
			}
			
			
			env.getBitboard().makeMoveBackward(move);
			
			
			if (MoveUtil.isQuiet(move)) {
				
				moveGen.addBFValue(cb.checkingPieces == 0 ? 0 : 1, cb.colorToMove, move, parentMove, depth);
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
					
					if (MoveUtil.isQuiet(bestMove)) {
						moveGen.addCounterMove(cb.colorToMove, parentMove, bestMove);
						moveGen.addKillerMove(cb.colorToMove, bestMove, ply);
						moveGen.addHHValue(cb.checkingPieces == 0 ? 0 : 1, cb.colorToMove, bestMove, parentMove, depth);
					}
					
					break;
				}
			}
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
				node.eval = -SearchUtils.getMateVal(ply, getEnv().getBitboard());
				node.leaf = true;
				
				return node.eval;
			}
		}
		
		
		if (EngineConstants.ASSERT) {
			
			Assert.isTrue(bestMove != 0);
		}
		
		
		if (env.getTPT() != null) {
			
			if (!SearchUtils.isMateVal(bestScore)) {
				
				env.getTPT().put(hashkey, depth, bestScore, alphaOrig, beta, bestMove);
			}
		}
		
		
		if (bestScore != node.eval) {
			
			throw new IllegalStateException("bestScore != node.eval");
		}
		
		//validatePV(node, evaluator, node.eval, ply, depth, isPv, false);
		
		
		return bestScore;
	}
	
	
	private int search(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator, ChessBoard cb, MoveGenerator moveGen,
			final int ply, int depth, int alpha, int beta, boolean isPv, int initialMaxDepth) {
		
		
		if (mediator != null && mediator.getStopper() != null) {
			
			mediator.getStopper().stopIfNecessary(initialMaxDepth, env.getBitboard().getColourToMove(), alpha, beta);
		}
		
		if (ply < 1) {
			
			throw new IllegalStateException("ply < 1 => use root_search");
		}
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		if (info.getSelDepth() < ply) {
			
			info.setSelDepth(ply);
		}
		
		
		if (ply >= ISearch.MAX_DEPTH) {
			
			return eval(evaluator, ply, alpha, beta, isPv);
		}
		
		
		final int alpha_org = alpha;
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		
		
	    if (isDraw(isPv)) {
	    	
	    	node.eval = getDrawScores(-1);
	    			
	    	return node.eval;
	    }
		
		
		if (EngineConstants.ENABLE_MATE_DISTANCE_PRUNING && !isPv) {
			
			alpha = Math.max(alpha, -SearchUtils.getMateVal(ply));
			
			beta = Math.min(beta, +SearchUtils.getMateVal(ply + 1));
			
			if (alpha >= beta) {
				
				node.eval = alpha;
				
				return node.eval;
			}
		}
		
		
		if (EngineConstants.ASSERT) {
			
			Assert.isTrue(depth >= 0);
			Assert.isTrue(alpha >= ISearch.MIN && alpha <= ISearch.MAX);
			Assert.isTrue(beta >= ISearch.MIN && beta <= ISearch.MAX);
		}
		
		
		//depth += extensions(cb, moveGen, ply);
		
		
		if (depth <= 0) {
			
			int qeval = qsearch(mediator, pvman, evaluator, info, cb, moveGen, alpha, beta, ply, isPv);
			
			if (node.eval != qeval) {
				
				throw new IllegalStateException("node.eval != qeval");
			}
			
			return node.eval;
		}
		
		
		long hashkey = env.getBitboard().getHashKey();
		
		
		int ttMove 									= 0;
		int ttFlag 									= -1;
		int ttValue 								= IEvaluator.MIN_EVAL;
		
		boolean isTTLowerBoundOrExact				= false;
		boolean isTTDepthEnoughForSingularExtension = false;
		
		if (env.getTPT() != null) {
			
			env.getTPT().get(hashkey, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
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
		
		
		int egtb_eval = ISearch.MIN;
		
		if (SyzygyTBProbing.getSingleton() != null
    			&& SyzygyTBProbing.getSingleton().isAvailable(env.getBitboard().getMaterialState().getPiecesCount())
    			){
			
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
							
							/*int op_factor = cb.colorToMoveInverse == Constants.COLOUR_WHITE ? env.getBitboard().getMaterialFactor().getWhiteFactor() : env.getBitboard().getMaterialFactor().getBlackFactor();
							
							//Opponent has only king so DTZ is DTM
							if (op_factor == 0 && !env.getBitboard().hasSufficientMatingMaterial(cb.colorToMoveInverse)) {
								
			    				//getMateVal with parameter set to 1 achieves max and with ISearch.MAX_DEPTH achieves min
			    				egtb_eval = SearchUtils.getMateVal(ply + dtz);
								
							} else {
								
								egtb_eval = IEvaluator.MAX_EVAL / (ply + dtz);
							}*/
							
							//TODO: the eval is too less in order to be more attractive for search than maybe rook and 1+ passed pawns?
							egtb_eval = MAX_MATERIAL_INTERVAL / (ply + dtz + 1); //+1 in order to be less than a mate in max_depth plies.
							
						} /*else {
							
							egtb_eval = getDrawScores(-1);
						}*/
	            		
						break;
						
		            case SyzygyConstants.TB_LOSS:
		            	
	    				/*
	    				This code doesn't work correctly
	    				//getMateVal with parameter set to 1 achieves max and with ISearch.MAX_DEPTH achieves min
	    				egtb_eval = -SearchUtils.getMateVal(ply + dtz);
	    				*/
		            	break;
		            
		            case SyzygyConstants.TB_DRAW:
		            	
						egtb_eval = getDrawScores(-1);
		                
						break;
						
		            case SyzygyConstants.TB_BLESSED_LOSS:
		            	
		            	egtb_eval = getDrawScores(-1);
		                
						break;
						
		            case SyzygyConstants.TB_CURSED_WIN:
		            	
		            	egtb_eval = getDrawScores(-1);
		                
						break;
						
		            default:
		            	
		            	throw new IllegalStateException("wdl=" + wdl);
		        }
			}
        }
		
		
		if (egtb_eval != ISearch.MIN) {
			
			if (cb.checkingPieces != 0) {
				
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
				
				return node.eval;
			}
		}
		
		
		int eval = ISearch.MIN;
		
		if (!isPv
				&& cb.checkingPieces == 0
				&& !SearchUtils.isMateVal(alpha)
				&& !SearchUtils.isMateVal(beta)
				&& egtb_eval == ISearch.MIN
			) {
			
			
			eval = eval(evaluator, ply, alpha, beta, isPv);
			
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
			
			
			if (depth >= 2 && ttFlag == -1) {
				
				depth -= 1;
			}
			
			
			if (eval >= beta) {
				
				
				if (EngineConstants.ENABLE_STATIC_NULL_MOVE && depth < 10) {
					
					if (eval - depth * 60 >= beta) {
						
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
						int score = depth - reduction <= 0 ? -qsearch(mediator, pvman, evaluator, info, cb, moveGen, -beta, -beta + 1, ply + 1, isPv)
								: -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -beta, -beta + 1, isPv, initialMaxDepth);
						
						cb.undoNullMove();
						
						if (score >= beta) {
							
							node.bestmove = 0;
							node.eval = score;
							node.leaf = true;
							
							return node.eval;
						}
					}
				}
				
			} else if (eval <= alpha) {
				
				
				if (EngineConstants.ENABLE_RAZORING && depth < 5) {
					
					int razoringMargin = 240 * depth;
					
					if (eval + razoringMargin < alpha) {
						
						int score = qsearch(mediator, pvman, evaluator, info, cb, moveGen, alpha - razoringMargin - 1, alpha - razoringMargin, ply, isPv);
						
						if (score < alpha - razoringMargin) {
							
							node.bestmove = 0;
							node.eval = score;
							node.leaf = true;
							
							return node.eval;
						}
					}
				}
			}
		}
		
		
		boolean extend_tt_move = false;
		
		if (depth >= 4
				&& isTTLowerBoundOrExact
				&& isTTDepthEnoughForSingularExtension
			) {
			
			//TODO: Adjust beta margin and depth
			int singular_beta = ttValue - 2 * depth;
			int singular_depth = depth / 2;
			
			int singular_value = singular_move_search(mediator, info, pvman, evaluator, cb, moveGen, ply,
					singular_depth, singular_beta - 1, singular_beta, false, initialMaxDepth, ttMove, eval);
			
			if (singular_value < singular_beta) {
				
				//Singular extension - only ttMove has good score
				extend_tt_move = true;
				
			} else if (!isPv && singular_value > beta) {
				
				//Multicut pruning - 2 moves above beta
				node.bestmove = 0;
				node.eval = singular_value;
				node.leaf = true;
				
				return node.eval;
			}
		}
		
		
		//Still no tt move, so help the search to find the tt move/score faster
		if (isPv && ttFlag == -1 && depth >= 3) {
			
			depth -= 2;
		}
		
		
		final boolean wasInCheck = cb.checkingPieces != 0;
		
		final int parentMove = moveGen.previous();
		int bestMove = 0;
		int bestScore = ISearch.MIN;
		
		int killer1Move = 0;
		int killer2Move = 0;
		int counterMove1 = 0;
		int counterMove2 = 0;
		int movesPerformed_attacks = 0;
		int movesPerformed_quiet = 0;
		
		moveGen.startPly();
		int phase = PHASE_TT;
		while (phase <= PHASE_QUIET) {
			
			switch (phase) {
			
			case PHASE_TT:
				
				if (ttMove != 0 && cb.isValidMove(ttMove)) {
					
					moveGen.addMove(ttMove);
					
					if (!env.getBitboard().getMoveOps().isCaptureOrPromotion(ttMove)) {
						
						moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
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
				
				if (killer1Move != 0 && killer1Move != ttMove && cb.isValidMove(killer1Move)) {
					
					moveGen.addMove(killer1Move);
					moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				}
				
				break;
				
			case PHASE_KILLER_2:
				
				killer2Move = moveGen.getKiller2(cb.colorToMove, ply);
				
				if (killer2Move != 0 && killer2Move != ttMove && killer2Move != killer1Move && cb.isValidMove(killer2Move)) {
					
					moveGen.addMove(killer2Move);
					moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				}
				
				break;
			
			case PHASE_COUNTER_1:
				
				counterMove1 = moveGen.getCounter1(cb.colorToMove, parentMove);
				
				if (counterMove1 != 0 && counterMove1 != ttMove && counterMove1 != killer1Move && counterMove1 != killer2Move && cb.isValidMove(counterMove1)) {
					
					moveGen.addMove(counterMove1);
					moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				}
				
				break;
				
			case PHASE_COUNTER_2:
				
				counterMove2 = moveGen.getCounter2(cb.colorToMove, parentMove);
				
				if (counterMove2 != 0 && counterMove2 != counterMove1 && counterMove2 != ttMove && counterMove2 != killer1Move && counterMove2 != killer2Move && cb.isValidMove(counterMove2)) {
					
					moveGen.addMove(counterMove2);
					moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				}
				
				break;
			
			
			case PHASE_ATTACKING_BAD:
				
				moveGen.generateAttacks(cb);
				moveGen.setMVVLVAScores(cb);
				moveGen.sort();
				
				break;
				
			case PHASE_QUIET:
				
				moveGen.generateMoves(cb);
				moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				moveGen.sort();
				
				break;
			}
			
			
			while (moveGen.hasNext()) {
				
				final int move = moveGen.next();
				
				if (!cb.isLegal(move)) {
					
					continue;
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
				
				
				if (info.getSearchedNodes() >= lastSentMinorInfo_nodesCount + 50000 ) { //Check time on each 50 000 nodes
					
					long timestamp = System.currentTimeMillis();
					
					if (timestamp >= lastSentMinorInfo_timestamp + 1000)  {//Send info each second
					
						mediator.changedMinor(info);
						
						lastSentMinorInfo_timestamp = timestamp;
					}
					
					lastSentMinorInfo_nodesCount = info.getSearchedNodes();
				}
				
				
				if (MoveUtil.isQuiet(move)) {
					movesPerformed_quiet++;
				} else {
					movesPerformed_attacks++;
				}
				
				
				if (!isPv
						&& depth <= 7
						&& !wasInCheck
						&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& !SearchUtils.isMateVal(alpha)
						&& !SearchUtils.isMateVal(beta)
						&& egtb_eval == ISearch.MIN
					) {
					
					if (phase == PHASE_QUIET) {
						
						if (EngineConstants.ENABLE_LMP
								&& movesPerformed_attacks + movesPerformed_quiet >= 3 + depth * depth
							) {
							
							continue;
						}
						
						if (eval != ISearch.MIN) { //eval is set
							
							if (EngineConstants.ENABLE_FUTILITY_PRUNING) {
								
								if (eval + depth * FUTILITY_MARGIN <= alpha) {
									
									continue;
								}
							}
						}
						
					} else if (EngineConstants.ENABLE_SEE_PRUNING
							&& phase == PHASE_ATTACKING_BAD
							&& SEEUtil.getSeeCaptureScore(cb, move) < -20 * depth * depth
						) {
						
						continue;
					}
				}
				
				
				int new_depth = (move == ttMove && extend_tt_move) ?(isPv ? depth : depth + 1) : depth - 1;
				
				boolean doLMR = new_depth >= 2
						&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& MoveUtil.isQuiet(move);
				
				int reduction = 1;
				
				if (doLMR) {
					
					reduction = LMR_TABLE[Math.min(new_depth, 63)][Math.min(movesPerformed_attacks + movesPerformed_quiet, 63)];
					
					if (!isPv) {
						
						reduction += 1;
					}
					
					reduction = Math.min(new_depth - 1, Math.max(reduction, 1));
					
				}
				
				
				int lmr_depth = new_depth - reduction;
				
				
				env.getBitboard().makeMoveForward(move);
				
				
				int score = alpha + 1;
				
				if (EngineConstants.ASSERT) {
					cb.changeSideToMove();
					Assert.isTrue(0 == CheckUtil.getCheckingPieces(cb));
					cb.changeSideToMove();
				}
				
				
				try {
					
					if (EngineConstants.ENABLE_LMR && reduction != 1) {
												
						score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, lmr_depth, -alpha - 1, -alpha, false, initialMaxDepth);
					}
					
					if (EngineConstants.ENABLE_PVS && score > alpha && movesPerformed_attacks + movesPerformed_quiet > 1) {
						
						score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, new_depth, -alpha - 1, -alpha, false, initialMaxDepth);
					}
					
					if (score > alpha) {
						
						score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, new_depth, -beta, -alpha, isPv, initialMaxDepth);
					}
					
				} catch(SearchInterruptedException sie) {
					
					moveGen.endPly();
					
					throw sie;
				}
				
				
				env.getBitboard().makeMoveBackward(move);
				
				
				if (MoveUtil.isQuiet(move)) {
					
					moveGen.addBFValue(wasInCheck ? 1 : 0, cb.colorToMove, move, parentMove, depth);
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
						
						if (MoveUtil.isQuiet(bestMove)) {
							moveGen.addCounterMove(cb.colorToMove, parentMove, bestMove);
							moveGen.addKillerMove(cb.colorToMove, bestMove, ply);
							moveGen.addHHValue(wasInCheck ? 1 : 0, cb.colorToMove, bestMove, parentMove, depth);
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
		
		
		if (bestScore != node.eval) {
			
			throw new IllegalStateException("bestScore != node.eval");
		}

		
		if (bestMove != node.bestmove) {
			
			throw new IllegalStateException("bestMove != node.bestmove");
		}
		
		
		if (env.getTPT() != null) {
				
			if (!SearchUtils.isMateVal(node.eval)) {
				
				env.getTPT().put(hashkey, depth, node.eval, alpha_org, beta, node.bestmove);
			}
		}
		
		//validatePV(node, evaluator, node.eval, ply, depth, isPv, false);
		
		return node.eval;
	}
	
	
	private int singular_move_search(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator, ChessBoard cb,
			MoveGenerator moveGen, final int ply, int depth, int alpha,
			int beta, boolean isPv, int initialMaxDepth, int ttMove1, int eval) {
		
		
		long hashkey = env.getBitboard().getHashKey() ^ ttMove1;
		
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
		
		
		final int alpha_org = alpha;
		
		final boolean wasInCheck = cb.checkingPieces != 0;
		
		final int parentMove = moveGen.previous();
		
		int killer1Move = 0;
		int killer2Move = 0;
		int counterMove1 = 0;
		int counterMove2 = 0;
		
		int bestScore = ISearch.MIN;
		int bestMove = 0;
		
		int all_moves = 0;
		
		moveGen.startPly();
		
		int phase = PHASE_TT;
		while (phase <= PHASE_QUIET) {
			
			switch (phase) {
			
			case PHASE_TT:
				
				if (ttMove2 != 0 && cb.isValidMove(ttMove2)) {
					
					moveGen.addMove(ttMove2);
					
					if (!env.getBitboard().getMoveOps().isCaptureOrPromotion(ttMove2)) {
						
						moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
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
				
				if (killer1Move != 0 && killer1Move != ttMove2 && cb.isValidMove(killer1Move)) {
					
					moveGen.addMove(killer1Move);
					moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				}
				
				break;
				
			case PHASE_KILLER_2:
				
				killer2Move = moveGen.getKiller2(cb.colorToMove, ply);
				
				if (killer2Move != 0 && killer2Move != killer1Move && killer2Move != ttMove2 && cb.isValidMove(killer2Move)) {
					
					moveGen.addMove(killer2Move);
					moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				}
				
				break;
			
			case PHASE_COUNTER_1:
				
				counterMove1 = moveGen.getCounter1(cb.colorToMove, parentMove);
				
				if (counterMove1 != 0 && counterMove1 != ttMove2 && counterMove1 != killer1Move && counterMove1 != killer2Move && cb.isValidMove(counterMove1)) {
					
					moveGen.addMove(counterMove1);
					moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				}
				
				break;
				
			case PHASE_COUNTER_2:
				
				counterMove2 = moveGen.getCounter2(cb.colorToMove, parentMove);
				
				if (counterMove2 != 0 && counterMove2 != counterMove1 && counterMove2 != ttMove2 && counterMove2 != killer1Move && counterMove2 != killer2Move && cb.isValidMove(counterMove2)) {
					
					moveGen.addMove(counterMove2);
					moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				}
				
				break;
			
			
			case PHASE_ATTACKING_BAD:
				
				moveGen.generateAttacks(cb);
				moveGen.setMVVLVAScores(cb);
				moveGen.sort();
				
				break;
				
			case PHASE_QUIET:
				
				moveGen.generateMoves(cb);
				moveGen.setHHScores(wasInCheck ? 1 : 0, cb.colorToMove, parentMove);
				moveGen.sort();
				
				break;
			}
			
			
			while (moveGen.hasNext()) {
				
				final int move = moveGen.next();
				
				if (!cb.isLegal(move)) {
					
					continue;
				}
				
				//Skip tt move
				if (move == ttMove1) {
					
					continue;
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
					if (move == ttMove2 || move == killer1Move || move == killer2Move || move == counterMove1 || move == counterMove2) {
						continue;
					}
				} else if (phase == PHASE_ATTACKING_GOOD || phase == PHASE_ATTACKING_BAD) {
					if (move == ttMove2) {
						continue;
					}
				}	
				
				
				all_moves++;
				
				
				if (!isPv
						&& depth <= 7
						&& !wasInCheck
						&& all_moves > 1
						&& !SearchUtils.isMateVal(alpha)
						&& !SearchUtils.isMateVal(beta)
					) {
					
					if (phase == PHASE_QUIET) {
						
						if (EngineConstants.ENABLE_LMP
								&& all_moves >= 3 + depth * depth
							) {
							
							continue;
						}
						
						if (eval != ISearch.MIN) { //eval is set
							
							if (EngineConstants.ENABLE_FUTILITY_PRUNING) {
								
								if (eval + depth * FUTILITY_MARGIN <= alpha) {
									
									continue;
								}
							}
						}
						
					} else if (EngineConstants.ENABLE_SEE_PRUNING
							&& phase == PHASE_ATTACKING_BAD
							&& SEEUtil.getSeeCaptureScore(cb, move) < -20 * depth * depth
						) {
						
						continue;
					}
				}
				
				
				env.getBitboard().makeMoveForward(move);
				
				
				boolean doLMR = depth >= 2
						&& all_moves > 1
						&& MoveUtil.isQuiet(move);
				
				int reduction = 1;
				
				if (doLMR) {
					
					reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(all_moves, 63)];
					
					if (!isPv) {
						
						reduction += 1;
					}
					
					reduction = Math.min(depth - 1, Math.max(reduction, 1));
					
				}
				
				int score = alpha + 1;
				
				if (EngineConstants.ENABLE_LMR && reduction != 1) {
											
					score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -alpha - 1, -alpha, false, initialMaxDepth);
				}
				
				if (EngineConstants.ENABLE_PVS && score > alpha && all_moves > 1) {
					
					score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -alpha - 1, -alpha, false, initialMaxDepth);
				}
				
				if (score > alpha) {
					
					score = -search(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -beta, -alpha, isPv, initialMaxDepth);
				}
				
				env.getBitboard().makeMoveBackward(move);
				
				
				if (score > bestScore) {
					
					bestScore = score;
					bestMove = move;
					
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
		
		if (bestScore == ISearch.MIN) {
			
			//Extend tt move, because it is the only move in this position.
			bestScore = alpha;
		}
		
		
		if (env.getTPT() != null) {
			
			if (!SearchUtils.isMateVal(bestScore) && bestMove != 0) {
				
				env.getTPT().put(hashkey, depth, bestScore, alpha_org, beta, bestMove);
			}
		}
		
		
		return bestScore;
	}
	
	
	public int qsearch(ISearchMediator mediator, PVManager pvman, IEvaluator evaluator, ISearchInfo info, final ChessBoard cb, final MoveGenerator moveGen,
			int alpha, final int beta, final int ply, final boolean isPv) {
		
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
		
		if (isDraw(isPv)) {
	    	
	    	node.eval = getDrawScores(-1);
	    	
	    	return node.eval;
	    }
		
		long hashkey = getHashkeyTPT(cb);
		
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
	  	
		
		if (cb.checkingPieces != 0) {
			//With queens on the board, this extension goes out of control if qsearch plays TT moves which are not attacks only.
			return search(mediator, info, pvman, evaluator, cb, moveGen, ply, 1, alpha, beta, isPv, 1);
			//return alpha;
		}
		
		
		int eval = eval(evaluator, ply, alpha, beta, isPv);
		
		
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
		
		
		/*int material_queen = (int) Math.max(getEnv().getBitboard().getBoardConfig().getMaterial_QUEEN_O(), getEnv().getBitboard().getBoardConfig().getMaterial_QUEEN_E());
		
		if (eval + FUTILITY_MARGIN_Q_SEARCH_ATTACKS + material_queen < alpha) {
			
	    	node.eval = eval;
			
	    	return node.eval;
		}*/
		
		int alphaOrig = alpha;
		
		alpha = Math.max(alpha, eval);
		
		int bestMove = 0;
		int bestScore = ISearch.MIN;
		
		moveGen.startPly();
		
		int phase = PHASE_ATTACKING_GOOD;
		
		while (phase <= PHASE_ATTACKING_GOOD) {
			
			switch (phase) {
			
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
				
				int see = SEEUtil.getSeeCaptureScore(cb, move);
				
				if (see < 0) {
					
					continue;
				}
				
				
				env.getBitboard().makeMoveForward(move);
				
				final int score = -qsearch(mediator, pvman, evaluator, info, cb, moveGen, -beta, -alpha, ply + 1, isPv);
				
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
					
					alpha = Math.max(alpha, bestScore);
					
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
				
				throw new IllegalStateException("node.eval != bestScore"); 
			}
			
			if (node.leaf) {
				
				throw new IllegalStateException("node.leaf"); 
			}
			
		} else {
			
			node.bestmove = 0;
			node.leaf = true;
			node.eval = eval;
			
			bestScore = eval;
			bestMove = 0;
		}
		
		
		if (getSearchConfig().isOther_UseAlphaOptimizationInQSearch()) {
			
			if (alpha > node.eval) {
				
				node.bestmove = 0;
				node.leaf = true;
				node.eval = alpha;
				
				bestScore = alpha;
				bestMove = 0;
			}
		}
		
			
		if (env.getTPT() != null) {
			
			if (!SearchUtils.isMateVal(bestScore) && bestMove != 0) {
				
				env.getTPT().put(hashkey, 0, bestScore, alphaOrig, beta, bestMove);
			}
		}
		
		//validatePV(node, evaluator, node.eval, ply, 0, isPv, true);
		
    	return node.eval;
	}
	
	
	private int extensions(final ChessBoard cb, final MoveGenerator moveGen, final int ply) {
		
		if (EngineConstants.ENABLE_CHECK_EXTENSION && cb.checkingPieces != 0) {
			
			return 1;
		}
		
		return 0;
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
						
						env.getTPT().get(env.getBitboard().getHashKey(), tt_entries_per_ply[ply]);
						
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
	
	
	private void validatePV(PVNode node, IEvaluator evaluator, int eval, int ply, int expectedDepth, boolean isPv, boolean qsearch) {
		
		
		if (!qsearch) {
			
			if (node.leaf || node.bestmove == 0) {
				
				throw new IllegalStateException("node.leaf || node.bestmove == 0");
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
			
			int static_eval = eval_sign * eval(evaluator, 0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, isPv);
			
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

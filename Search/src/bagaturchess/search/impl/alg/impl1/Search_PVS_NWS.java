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


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl.utils.VarStatistic;
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
import bagaturchess.search.impl.pv.PVValidation;
import bagaturchess.search.impl.tpt.ITTEntry;
import bagaturchess.uci.api.ChannelManager;
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
	
	private static final double REDUCTION_AGGRESSIVENESS 			= 1.555;
	private static final double PRUNING_AGGRESSIVENESS 				= 1.333;
	
	private static final int FUTILITY_MAXDEPTH 						= 7; //MAX_DEPTH; //7;
	private static final int FUTILITY_MARGIN 						= 80;
	
	private static final int FUTILITY_LMR_MAXDEPTH 					= 7; //MAX_DEPTH; //7;
	private static final int FUTILITY_LMR_MARGIN 					= 160;
	
	private static final int STATIC_NULL_MOVE_MAXDEPTH 				= 7; //MAX_DEPTH; //9;
	private static final int STATIC_NULL_MOVE_MARGIN 				= 80;
	
	private static final int RAZORING_MAXDEPTH 						= 7; //MAX_DEPTH; //4;
	private static final int RAZORING_MARGIN 						= 260;
	
	private static final int SEE_MAXDEPTH 							= 7; //MAX_DEPTH; //8;
	private static final int SEE_MARGIN 							= 80;
	
	private static final int PROBCUT_MARGIN_BASE 					= 70;
	private static final int PROBCUT_MARGIN_INCREMENT 				= 30;
	
	
	private static final boolean VALIDATE_PV 						= false;
	
	
	private static final double[][] LMR_TABLE 						= new double[64][64];
	
	static {
		
		for (int depth = 1; depth < 64; depth++) {
			
			for (int move_number = 1; move_number < 64; move_number++) {
				
				LMR_TABLE[depth][move_number] = Math.max(1, Math.log(move_number) * Math.log(depth) / (double) 2);
				LMR_TABLE[depth][move_number] = LMR_TABLE[depth][move_number] * REDUCTION_AGGRESSIVENESS;
			}
		}
	}
	
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	
	private SearchStackInfo[] ssis 									= new SearchStackInfo[MAX_DEPTH + 1];
	
	private VarStatistic stats 										= new VarStatistic();
	
	
	public Search_PVS_NWS(Object[] args) {
		
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
	}
	
	
	public Search_PVS_NWS(SearchEnv _env) {
		
		super(_env);
		
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
		
		stats = new VarStatistic();
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
		ssi.static_eval = eval(ply, alphaOrig, beta, isPv);
		
		
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
			//mediator.changedMinor(info);
			
			boolean isCheckMove = env.getBitboard().isCheckMove(move);
			
			
			env.getBitboard().makeMoveForward(move);
			
			
			if (!env.getBitboard().getMoveOps().isCapture(move)) {
				movesPerformed_quiet++;
			} else {
				movesPerformed_attacks++;
			}
			
			boolean isQuiet = !env.getBitboard().getMoveOps().isCaptureOrPromotion(move);
			
			boolean doLMR = depth >= 2
						&& !ssi.in_check
						&& !isCheckMove
						&& list.getScore() <= stats.getEntropy()
						&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& isQuiet;
			
			double reduction = 1;
			
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
		
		
		if (VALIDATE_PV) PVValidation.validatePV(this, env, node, ply, depth, isPv, alphaOrig, beta, validation_stack);
		
		
		return bestScore;
	}
	
	
	private int search(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator,
			final int ply, double depth, int alpha, int beta, boolean isPv, int initialMaxDepth) {
		
		
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
		ssi.static_eval = eval(ply, alpha, beta, isPv);
		
		
		if (ply >= ISearch.MAX_DEPTH) {
			
			return ssi.static_eval;
		}
		
		
		boolean improving = ply - 2 >= 0 ? (ssi.static_eval - ssis[ply - 2].static_eval > 0) : false;
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		node.type = PVNode.TYPE_NORMAL_SEARCH;
		
		
		//Go in qsearch if depth is < 1
		if (depth < 1) {
			
			int qeval = qsearch(mediator, pvman, evaluator, info, alpha, beta, ply, isPv, initialMaxDepth);
			
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
		
		
		//Check for draw
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
		
		
		//TT probing
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
				isTTDepthEnoughForSingularExtension = tpt_depth >= getSMEDepth(depth);
				
				if (getSearchConfig().isOther_UseTPTScores()) {
					
					if (!isPv && tpt_depth >= (int) depth) {
						
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
		int egtb_eval = probeTB(info, ply);
			
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
		
		
		final int parentMove 		= env.getBitboard().getLastMove();
		final int colourToMove 		= env.getBitboard().getColourToMove();
		boolean hasAtLeastOnePiece 	= (colourToMove == Constants.COLOUR_WHITE) ?
										env.getBitboard().getMaterialFactor().getWhiteFactor() >= 3 :
										env.getBitboard().getMaterialFactor().getBlackFactor() >= 3;
				
				
		//Pruning and reductions in non-check and non-PV nodes
		boolean mateThreat  = false;
		
		if (!isPv
				&& !ssi.in_check
				&& hasAtLeastOnePiece
				&& !SearchUtils.isMateVal(alpha)
				&& !SearchUtils.isMateVal(beta)
			) {
			
			
			//Reduce depth if TT value is not presented
			if (!VALIDATE_PV && depth >= 2 && ttFlag == -1) {
				
				depth -= 1;
			}
					
					
			//Fail high pruning
			if (ssi.static_eval >= beta + 35) {
				
				
				//Static null move pruning
				if (depth <= STATIC_NULL_MOVE_MAXDEPTH) {
					
					if (ssi.static_eval - (depth * STATIC_NULL_MOVE_MARGIN - (improving ? 70 : 0)) / PRUNING_AGGRESSIVENESS >= beta) {
						
						node.bestmove = 0;
						node.eval = ssi.static_eval;
						node.leaf = true;
						node.type = PVNode.TYPE_STATIC_NULL_MOVE;
								
						return node.eval;
					}
				}
				
				
				//Verified null move pruning
				if (depth >= 3) {
						
					env.getBitboard().makeNullMoveForward();
					
					double reduction = depth / 3 + 3 + Math.min((Math.max(0, ssi.static_eval - beta)) / 80, 3);
					reduction = reduction * REDUCTION_AGGRESSIVENESS;
					
					int score = depth - reduction <= 0 ? -qsearch(mediator, pvman, evaluator, info, -beta, -beta + 1, ply + 1, false, initialMaxDepth)
							: -search(mediator, info, pvman, evaluator, ply + 1, depth - reduction, -beta, -beta + 1, false, initialMaxDepth);
					
					env.getBitboard().makeNullMoveBackward();
					
					if (score >= beta) {
						
						int verify_score = depth - reduction <= 0 ? qsearch(mediator, pvman, evaluator, info, beta - 1, beta, ply, false, initialMaxDepth)
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
								&& depth >= 2) {
							
							mateThreat = true;
						}
					}
					
					//If the score is negative mate score
					if ((score < -ISearch.MAX_MATERIAL_INTERVAL)
							&& depth >= 2) {
						
						mateThreat = true;
					}
				}
				
			}
			
			
			//Fail low pruning
			if (ssi.static_eval <= alpha) {
				
				
				//Razoring
				if (depth <= RAZORING_MAXDEPTH) {
					
					double razoringMargin = RAZORING_MARGIN * depth / PRUNING_AGGRESSIVENESS;
					
					if (ssi.static_eval + razoringMargin < alpha) {
						
						int score = qsearch(mediator, pvman, evaluator, info, alpha, alpha + 1, ply, false, initialMaxDepth);
						
						if (score <= alpha) {
							
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
			int prob_cut_margin = (int) Math.min(500, PROBCUT_MARGIN_BASE + depth * PROBCUT_MARGIN_INCREMENT);
			int prob_cut_beta = beta + prob_cut_margin;
			
			if (depth >= 3
					&& isTTLowerBoundOrExact
					&& ttValue >= prob_cut_beta) {
				
				IMoveList list = lists_attacks[ply];
				list.clear();
				env.getBitboard().genCapturePromotionMoves(list);
				
				double prob_cut_depth = Math.max(0, depth - 5);
				
				int move;
				while ((move = list.next()) != 0)  {
					
					if (env.getBitboard().getSEEScore(move) < 0) {
						
						continue;
					}
					
					env.getBitboard().makeMoveForward(move);
					
					int score = -qsearch(mediator, pvman, evaluator, info, -prob_cut_beta, -prob_cut_beta + 1, ply + 1, false, initialMaxDepth);
					
					if (score >= prob_cut_beta && prob_cut_depth > 0) {
						
						score = -search(mediator, info, pvman, evaluator, ply + 1, prob_cut_depth, -prob_cut_beta, -prob_cut_beta + 1, false, initialMaxDepth);
					}
					
					env.getBitboard().makeMoveBackward(move);
					
					
					if (score >= prob_cut_beta) {
						
						node.bestmove = move;
						node.eval = score;
						node.leaf = true;
						node.type = PVNode.TYPE_PROBECUT;
						
						return node.eval;
					}
				}
			}
		}
		
		
		//Singular move extension
		int tt_move_extension = 0;
		
		if (depth >= 4
				&& ply < 2 * initialMaxDepth
				&& isTTLowerBoundOrExact
				&& isTTDepthEnoughForSingularExtension
				&& env.getBitboard().isPossible(ttMove)
			) {
			
			int singular_margin = 16 + (int) (2 * depth);
			int singular_beta = ttValue - singular_margin;
			double singular_depth = getSMEDepth(depth);
			singular_depth = Math.max(1 , singular_depth / REDUCTION_AGGRESSIVENESS);
			
			int singular_value = singular_move_search(mediator, info, pvman, evaluator, ply,
					singular_depth, singular_beta - 1, singular_beta, initialMaxDepth, ttMove);
			
			//Singular extension - only ttMove has score above beta
			if (singular_value < singular_beta) {
				
				if (!VALIDATE_PV) {
					
					tt_move_extension = 1;
					
					if (!isPv) {
						
						tt_move_extension += 1;
					}
				}
				
			} else if (!isPv) {
				
				//Multi-cut pruning - at lest 2 moves above beta
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
						
						tt_move_extension = -2;
					}
				}
			}
		}
		
		
		//Still no tt move, so help the search to find the tt move/score faster
		if (!VALIDATE_PV && isPv && ttFlag == -1 && depth >= 3) {
			
			depth -= 2;
		}
		
		
		//Main moves loop
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
		list1.clear();
		list2.clear();
		
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
					if (move == ttMove || move == killer1Move || move == killer2Move || move == killer3Move || move == killer4Move) {
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
				
				
				boolean isCheckMove = env.getBitboard().isCheckMove(move);
				
				
				if (!env.getBitboard().getMoveOps().isCapture(move)) {
					movesPerformed_quiet++;
				} else {
					movesPerformed_attacks++;
				}
				
				
				//Fail-low pruning for non-PV nodes
				if (!isPv
						&& !ssi.in_check
						&& !isCheckMove
						&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& hasAtLeastOnePiece
						&& !SearchUtils.isMateVal(alpha)
						&& !SearchUtils.isMateVal(beta)
					) {
					
					
					if (phase == PHASE_QUIET
							&& list.getScore() <= stats.getEntropy()
							) {
						
						//Late move pruning
						if (movesPerformed_attacks + movesPerformed_quiet >= (3 + depth * depth / (improving ? 1 : 2)) / PRUNING_AGGRESSIVENESS) {
							
							continue;
						}
						
						//Futility pruning
						if (depth <= FUTILITY_MAXDEPTH
								&& ssi.static_eval + depth * FUTILITY_MARGIN / PRUNING_AGGRESSIVENESS <= alpha) {
							
							continue;
						}
						
						//SEE pruning for non-captures
						if (movesPerformed_attacks + movesPerformed_quiet > 3
								&& depth <= SEE_MAXDEPTH
								&& env.getBitboard().getSEEScore(move) < -SEE_MARGIN * depth / PRUNING_AGGRESSIVENESS) {
							
							continue;
						}
						
					} else if (phase == PHASE_ATTACKING_BAD //SEE pruning for captures
							&& depth <= SEE_MAXDEPTH
							&& env.getBitboard().getSEEScore(move) < -SEE_MARGIN * depth / PRUNING_AGGRESSIVENESS) {
						
						continue;
					}
				}
				
				
				boolean isQuiet = !env.getBitboard().getMoveOps().isCaptureOrPromotion(move);
				
				
				//Extensions
				double new_depth;
				
				if (ply < 2 * initialMaxDepth && depth >= 2) {
					
					if (move == ttMove) {
						
						new_depth = depth - 1 + tt_move_extension;
						
						//Extend TT move to end up with search (not qsearch).
						//new_depth = Math.max(1, new_depth);
						
					} /*else if (mateThreat) {
						
						new_depth = depth;
						
					}*/ /*else if (isQuiet) {
						
						new_depth = depth - 1 + Math.max(-1, Math.min(1, list.getScore() / 1000));
						
					}*/ else {
						
						new_depth = depth - 1;
					}
					
				} else {
					
					new_depth = depth - 1;
				}
				
				
				//Late move reduction
				boolean doLMR = new_depth >= 2
						&& !ssi.in_check
						&& !isCheckMove
						&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& list.getScore() <= stats.getEntropy()
						&& isQuiet;
				
				double reduction = 1;
				
				if (doLMR) {
					
					reduction = LMR_TABLE[(int) Math.min(new_depth, 63)][Math.min(movesPerformed_attacks + movesPerformed_quiet, 63)];
					
					if (!isPv) {
						
						reduction += 1;
					}
					
					reduction *= (1 - Math.min(1, list.getScore() / stats.getEntropy()));
					
					reduction = Math.min(new_depth - 1, Math.max(reduction, 1));
				}
				
				
				double lmr_depth = Math.max(0, new_depth - reduction);
				
				
				if (!isPv
						&& !ssi.in_check
						&& !isCheckMove
						&& isQuiet
						&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& lmr_depth <= FUTILITY_LMR_MAXDEPTH
						&& list.getScore() <= stats.getEntropy()
						&& ssi.static_eval + (lmr_depth + 1) * FUTILITY_LMR_MARGIN <= alpha) {
					
					continue;
				}
				
				
				env.getBitboard().makeMoveForward(move);
				
				
				//Under some conditions, reduce the depth with 1 ply
				//if the move made is not improving the static evaluation.
				if (!VALIDATE_PV
						&& !isPv
						&& !ssi.in_check
						&& !isCheckMove
						&& isQuiet
						//&& movesPerformed_attacks + movesPerformed_quiet > 1
						&& list.getScore() <= stats.getEntropy()
						&& new_depth >= 2
						&& ssi.static_eval > -eval(ply, -beta, -alpha, isPv)) {
					
					new_depth--;
				}
				
				
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
				
				
				if (bestScore != ISearch.MIN && score > bestScore && phase == PHASE_QUIET) {
					
					stats.addValue(list.getScore());
				}
				
				
				if (!env.getBitboard().getMoveOps().isCapture(move)) {
					
					history.registerAll(parentMove, move, (int) depth);
					conthist.registerAll(parentMove, move, (int) depth);
					
					if (score < beta) {
						
						history.registerBad(parentMove, move, (int) depth);
						conthist.registerBad(parentMove, move, (int) depth);
					}
					
				} else {
					
					caphist.registerAll(parentMove, move, (int) depth);
					
					if (score < beta) {
						
						caphist.registerBad(parentMove, move, (int) depth);
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
						
						history.registerGood(parentMove, move, (int) depth);
						conthist.registerGood(parentMove, move, (int) depth);
						
					} else {
						
						caphist.registerGood(parentMove, move, (int) depth);
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
				
			env.getTPT().put(ssi.hash_key, (int) depth, node.eval, alpha_org, beta, node.bestmove);
		}
		
		
		if (VALIDATE_PV) PVValidation.validatePV(this, env, node, ply, (int) depth, isPv, alpha_org, beta, validation_stack);
		
		
		return node.eval;
	}
	
	
	private int singular_move_search(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator,
			final int ply, double depth, int alpha,
			int beta, int initialMaxDepth, int ttMove1) {
		
		
		final long hashkey = getHashkeyTPT() ^ Long.rotateLeft(((long) ttMove1) * 0x9E3779B97F4A7C15L, 32);
		
		int ttMove2 = 0; 
				
		if (env.getTPT() != null) {
			
			env.getTPT().get(hashkey, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				ttMove2 = tt_entries_per_ply[ply].getBestMove();
				int ttFlag = tt_entries_per_ply[ply].getFlag();
				int ttValue = tt_entries_per_ply[ply].getEval();
				
				int tpt_depth = tt_entries_per_ply[ply].getDepth();
				
				if (getSearchConfig().isOther_UseTPTScores()) {
					
					if (tpt_depth >= depth) {
						
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
		
		
		final int colourToMove 		= env.getBitboard().getColourToMove();
		
		
		final int alpha_org 		= alpha;
		
		int killer1Move 			= 0;
		int killer2Move 			= 0;
		int killer3Move 			= 0;
		int killer4Move 			= 0;
		
		int bestScore 				= ISearch.MIN;
		int bestMove 				= 0;
		
		int quiet_noncheck_moves 	= 0;
		
		IMoveList list1 			= lists_history[ply];
		IMoveList list2 			= lists_attacks[ply];
		list1.clear();
		list2.clear();
		
		IMoveList list 				= null;
		
		final int quiet_limit 		= 1 + ((int) depth) / 2;
		
		
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
					if (move == ttMove2 || move == killer1Move || move == killer2Move || move == killer3Move || move == killer4Move) {
						continue;
					}
				} else if (phase == PHASE_ATTACKING_GOOD || phase == PHASE_ATTACKING_BAD) {
					if (move == ttMove2) {
						continue;
					}
				}	
				
				
				boolean isCheckMove = env.getBitboard().isCheckMove(move);
				
				
				//Try only the first a few quiet moves in SME verification search
				//Don't skip moves giving check too.				
				if (phase == PHASE_QUIET && !isCheckMove) {
					
					quiet_noncheck_moves++;
				    
				    if (quiet_noncheck_moves > quiet_limit) {
				    	
				        continue;
				    }
				}
				
				
				env.getBitboard().makeMoveForward(move);
				
				
				int score = -search(mediator, info, pvman, evaluator, ply + 1, depth - 1, -alpha - 1, -alpha, false, initialMaxDepth);
				
				
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
				
				env.getTPT().put(hashkey, (int) depth, bestScore, alpha_org, beta, bestMove);
			}
		}
		
		
		return bestScore;
	}
	
	
	public int qsearch(ISearchMediator mediator, PVManager pvman, IEvaluator evaluator, ISearchInfo info,
			int alpha, final int beta, final int ply, final boolean isPv, int initialMaxDepth) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		if (info.getSelDepth() < ply) {
			
			info.setSelDepth(ply);
		}
		
		
		if (ply >= ISearch.MAX_DEPTH) {
	    	
			return eval(ply, alpha, beta, isPv);
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
		
		long hashkey 	= getHashkeyTPT();
		
	    int ttFlag 		= -1;
	    int ttValue 	= IEvaluator.MIN_EVAL;
	    int ttMove 		= 0;
	    
		if (env.getTPT() != null) {
			
			env.getTPT().get(hashkey, tt_entries_per_ply[ply]);
			
			if (!tt_entries_per_ply[ply].isEmpty()) {
				
				ttValue = tt_entries_per_ply[ply].getEval();
				ttFlag = tt_entries_per_ply[ply].getFlag();
				ttMove = tt_entries_per_ply[ply].getBestMove();
				
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
			return search(mediator, info, pvman, evaluator, ply, 1, alpha, beta, false, initialMaxDepth);
			//return alpha;
		}
		
		
		int eval = eval(ply, alpha, beta, isPv);
		
		
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
		
		
		int alphaOrig 	= alpha;
		
		alpha 			= Math.max(alpha, eval);
		
		int bestMove 	= 0;
		int bestScore 	= ISearch.MIN;
		
		IMoveList list 	= lists_attacks_qsearch[ply];
		list.clear();
		
		int phase 		= PHASE_TT;
		
		while (phase <= PHASE_ATTACKING_GOOD) {
			
			switch (phase) {
				
				case PHASE_TT:
					
					if (ttMove != 0
						&& env.getBitboard().isPossible(ttMove)
						&& env.getBitboard().getMoveOps().isCaptureOrPromotion(ttMove)
						) {
						
						list.clear();
						list.reserved_add(ttMove);
					}
					
					break;
					
				case PHASE_ATTACKING_GOOD:
					
					list.clear();
					env.getBitboard().genCapturePromotionMoves(list);
					
					break;
			}
			
			int move;
			while ((move = list.next()) != 0) {
				
				
				if (phase == PHASE_ATTACKING_GOOD
						&& move == ttMove) {
					
					continue;
				}
				
				
				int see = env.getBitboard().getSEEScore(move);
				
				if (see < 0) {
					
					continue;
				}
				
				
				env.getBitboard().makeMoveForward(move);
				
				final int score = -qsearch(mediator, pvman, evaluator, info, -beta, -alpha, ply + 1, isPv, initialMaxDepth);
				
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
		
		
		if (VALIDATE_PV) PVValidation.validatePV(this, env, node, ply, 0, isPv, alphaOrig, beta, validation_stack);
		
		
    	return node.eval;
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
	
	
	private int getSMEDepth(double depth) {
		
		return (int) (depth / 2);
	}
}

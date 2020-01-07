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
package bagaturchess.search.impl.alg.impl2;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.BoardImpl;
import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
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
import bagaturchess.search.impl.env.SearchEnv;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.search.impl.pv.PVNode;
import bagaturchess.search.impl.utils.SearchUtils;


public class CopyOfSearch_PVS_NWS extends SearchImpl {
	
	
	private static final int PHASE_TT = 0;
	private static final int PHASE_ATTACKING = 1;
	private static final int PHASE_KILLER_1 = 2;
	private static final int PHASE_KILLER_2 = 3;
	private static final int PHASE_COUNTER = 4;
	private static final int PHASE_QUIET = 5;
	
	
	private static final int[][] LMR_TABLE = new int[64][64];
	static {
		for (int depth = 1; depth < 64; depth++) {
			for (int moveNumber = 1; moveNumber < 64; moveNumber++) {
				//LMR_TABLE[depth][moveNumber] = (int) (0.5f + Math.log(depth) * Math.log(moveNumber * 1.2f) / 2.5f);
				LMR_TABLE[depth][moveNumber] = 1 + (int) Math.ceil(Math.max(1, Math.log(moveNumber) * Math.log(depth) / (double) 2));
			}
		}
	}
	
	private static final int FUTILITY_MARGIN_Q_SEARCH = 200;
	
	private static final int VALUE_DRAW = 0;
	private static final int VALUE_NONE = 32002;
	  
	
	private long lastSentMinorInfo_timestamp;
	private long lastSentMinorInfo_nodesCount;
	
	
	// Futility and reductions lookup tables, initialized at startup
	int FutilityMoveCounts[][] = new int[2][16]; // [improving][depth]
	int Reductions[][][][] = new int[2][2][64][64];  // [pv][improving][depth][moveNumber]
	// Razor and futility margins
	int RazorMargin = 600;
	int futility_margin(int depth, boolean improving) {
		return (175 - 50 * (improving ? 1 : 0)) * depth;
	}
	
	
	private Stack[] stack = new Stack[MAX_DEPTH];
	private MovePicker[] movePickers = new MovePicker[MAX_DEPTH];
	
	private CounterMoveHistory counterMoves;
	private ButterflyHistory mainHistory;
	private CapturePieceToHistory captureHistory;
	private ContinuationHistory continuationHistory;
	
	
	public CopyOfSearch_PVS_NWS(Object[] args) {
		this(new SearchEnv((IBitBoard) args[0], getOrCreateSearchEnv(args)));
		
		TTUtil.setSizeMB(256);
		EvalUtil.setSizeMB(64);
		
		for (int i=0; i<stack.length; i++) {
			stack[i] = new Stack();
			stack[i].ply = i;
		}
		
		for (int i=0; i<movePickers.length; i++) {
			movePickers[i] = new MovePicker();
		}
		
		counterMoves = new CounterMoveHistory();
		mainHistory = new ButterflyHistory();
		captureHistory = new CapturePieceToHistory();
		continuationHistory = new ContinuationHistory();
	}
	
	
	public CopyOfSearch_PVS_NWS(SearchEnv _env) {
		super(_env);
		
		TTUtil.setSizeMB(256);
		EvalUtil.setSizeMB(64);
		
		for (int i=0; i<stack.length; i++) {
			stack[i] = new Stack();
			stack[i].ply = i;
		}
		
		for (int i=0; i<movePickers.length; i++) {
			movePickers[i] = new MovePicker();
		}
		
		counterMoves = new CounterMoveHistory();
		mainHistory = new ButterflyHistory();
		captureHistory = new CapturePieceToHistory();
		continuationHistory = new ContinuationHistory();
	}
	
	
	@Override
	public int getTPTUsagePercent() {
		return (int) TTUtil.getUsagePercentage() / 10;
	}
	
	
	public void newSearch() {
		
		super.newSearch();
		
		((BoardImpl) env.getBitboard()).getMoveGenerator().clearHistoryHeuristics();
		
		lastSentMinorInfo_nodesCount = 0;
		lastSentMinorInfo_timestamp = 0;
		
		init();
	}
	
	
	@Override
	public int pv_search(ISearchMediator mediator, PVManager pvman,
			ISearchInfo info, int initial_maxdepth, int maxdepth, int depth,
			int alpha_org, int beta, int prevbest, int prevprevbest,
			int[] prevPV, boolean prevNullMove, int evalGain, int rootColour,
			int totalLMReduction, int materialGain, boolean inNullMove,
			int mateMove, boolean useMateDistancePrunning) {
		
		return calculateBestMove(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, normDepth(maxdepth), alpha_org, beta, true, false);
	}
	
	
	@Override
	public int nullwin_search(ISearchMediator mediator, PVManager pvman, ISearchInfo info,
			int initial_maxdepth, int maxdepth, int depth, int beta,
			boolean prevNullMove, int prevbest, int prevprevbest, int[] prevPV,
			int rootColour, int totalLMReduction, int materialGain,
			boolean inNullMove, int mateMove, boolean useMateDistancePrunning) {
		
		return calculateBestMove(mediator, info, pvman, env.getEval(), ((BoardImpl) env.getBitboard()).getChessBoard(),
				((BoardImpl) env.getBitboard()).getMoveGenerator(), 0, normDepth(maxdepth), beta - 1, beta, false, false);		
	}
	
	
	void init() {

		for (int imp = 0; imp <= 1; ++imp)
			for (int d = 1; d < 64; ++d)
				for (int mc = 1; mc < 64; ++mc) {
					
		            double r = Math.log(d) * Math.log(mc) / 1.95;
		
		            Reductions[0][imp][d][mc] = (int) Math.round(r);
		            Reductions[1][imp][d][mc] = Math.max(Reductions[0][imp][d][mc] - 1, 0);
		
		            // Increase reduction for non-PV nodes when eval is not improving
		            if (imp == 0 && r > 1.0) Reductions[0][imp][d][mc]++;
		        }
		
		for (int d = 0; d < 16; ++d) {
		    FutilityMoveCounts[0][d] = (int)(2.4 + 0.74 * Math.pow(d, 1.78));
		    FutilityMoveCounts[1][d] = (int)(5.0 + 1.00 * Math.pow(d, 2.00));
		}
	}
	
	
	public int calculateBestMove(ISearchMediator mediator, ISearchInfo info,
			PVManager pvman, IEvaluator evaluator, ChessBoard cb, MoveGenerator moveGen,
			final int ply, int depth, int alpha, int beta, boolean isPv, boolean cutNode) {

		
		if (mediator != null && mediator.getStopper() != null) {
			mediator.getStopper().stopIfNecessary(ply + depth, env.getBitboard().getColourToMove(), alpha, beta);
		}
		
		
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		if (ply >= ISearch.MAX_DEPTH) {
			return eval(evaluator, ply, alpha, beta);
		}
		
		assert(!(isPv && cutNode));
		
		
		PVNode node = pvman.load(ply);
		node.bestmove = 0;
		node.eval = ISearch.MIN;
		node.leaf = true;
		
	    boolean rootNode = isPv && stack[ply].ply == 0;
		
	    // Check if we have an upcoming move which draws by repetition, or
	    // if the opponent had an alternative move earlier to this position.
	    if (alpha < VALUE_DRAW
	        && !rootNode
	        && isDraw())
	    {
	        alpha = value_draw(depth);
	        if (alpha >= beta) {
				node.eval = alpha;
				return node.eval;
	        }
	    }
	    
		
	    // Dive into quiescence search when the depth reaches zero
	    if (depth <= 0) {
			int qeval = qsearch(evaluator, info, cb, moveGen, alpha, beta, ply);
			node.bestmove = 0;
			node.eval = qeval;
			node.leaf = true;
			return node.eval;
		}
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		
		
		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0);
			Assert.isTrue(alpha >= ISearch.MIN && alpha <= ISearch.MAX);
			Assert.isTrue(beta >= ISearch.MIN && beta <= ISearch.MAX);
			Assert.isTrue(alpha < beta);
		}
		
		
        // Step 3. Mate distance pruning. Even if we mate at the next move our score
        // would be at best mate_in(ss->ply+1), but if alpha is already bigger because
        // a shorter mate was found upward in the tree then there is no need to search
        // because we will never beat the current alpha. Same logic but with reversed
        // signs applies also in the opposite condition of being mated instead of giving
        // mate. In this case return a fail-high score.
		if (EngineConstants.ENABLE_MATE_DISTANCE_PRUNING) {
			if (ply > 0) {
				alpha = Math.max(alpha, -SearchUtils.getMateVal(ply));
				beta = Math.min(beta, +SearchUtils.getMateVal(ply + 1));
				if (alpha >= beta) {
					return alpha;
				}
			}
		}
		
		
		int bestMove = 0;
		stack[ply+1].ply = stack[ply].ply + 1;
	    stack[ply].currentMove = stack[ply+1].excludedMove = bestMove;
	    stack[ply].continuationHistory = continuationHistory.array[0][0];
	    stack[ply + 2].killers[0] = stack[ply + 2].killers[1] = 0;
	    int prevSq = ply <= 0 ? 0 : env.getBitboard().getMoveOps().getToFieldID(stack[ply - 1].currentMove);
	    
	    // Initialize statScore to zero for the grandchildren of the current position.
	    // So statScore is shared between all grandchildren and only the first grandchild
	    // starts with statScore = 0. Later grandchildren start with the last calculated
	    // statScore of the previous grandchild. This influences the reduction rules in
	    // LMR which are based on the statScore of parent position.
	    stack[ply + 2].statScore = 0;

	    // Step 4. Transposition table lookup. We don't want the score of a partial
	    // search to overwrite a previous full search TT value, so we use a different
	    // position key in case of an excluded move.
	    int excludedMove = stack[ply].excludedMove;
	    long hashKey = env.getBitboard().getHashKey() ^ (excludedMove << 16); // Isn't a very good hash
	    
	    boolean ttHit = false;
		int ttScore = 0;
		int ttMove = 0;
		int ttFlag = -1;
		long ttValue = TTUtil.getTTValue(hashKey);
		if (ttValue != 0) {
			ttHit = true;
			ttScore = TTUtil.getScore(ttValue);
			ttMove = TTUtil.getMove(ttValue);
			ttFlag = TTUtil.getFlag(ttValue);
			
			if (!isPv
					&& TTUtil.getDepth(ttValue) >= depth
				) {
				
				switch (ttFlag) {
					case TTUtil.FLAG_EXACT:
						extractFromTT(ply, node, ttValue, info, isPv);
						return node.eval;
					case TTUtil.FLAG_LOWER:
						if (ttScore >= beta) {
							extractFromTT(ply, node, ttValue, info, isPv);
							return node.eval;
						}
						break;
					case TTUtil.FLAG_UPPER:
						if (ttScore <= alpha) {
							extractFromTT(ply, node, ttValue, info, isPv);
							return node.eval;
						}
						break;
				}
			}
		}
		
		
		if (ply > 1
    	    	&& depth >= 7
    			&& SyzygyTBProbing.getSingleton() != null
    			&& SyzygyTBProbing.getSingleton().isAvailable(env.getBitboard().getMaterialState().getPiecesCount())
    			){
			
			if (cb.checkingPieces != 0) {
				if (!env.getBitboard().hasMoveInCheck()) {
					node.bestmove = 0;
					node.eval = -getMateVal(ply);
					node.leaf = true;
					return node.eval;
				}
			} else {
				if (!env.getBitboard().hasMoveInNonCheck()) {
					node.bestmove = 0;
					node.eval = EvalConstants.SCORE_DRAW;
					node.leaf = true;
					return node.eval;
				}
			}
			
			int result = SyzygyTBProbing.getSingleton().probeDTZ(env.getBitboard());
			if (result != -1) {
				int dtz = (result & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
				int wdl = (result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
				int egtbscore =  SyzygyTBProbing.getSingleton().getWDLScore(wdl, ply);
				if (egtbscore > 0) {
					int distanceToDraw = 100 - env.getBitboard().getDraw50movesRule();
					if (distanceToDraw > dtz) {
						node.bestmove = 0;
						node.eval = 9 * (distanceToDraw - dtz);
						node.leaf = true;
						return node.eval;
					} else {
						node.bestmove = 0;
						node.eval = EvalConstants.SCORE_DRAW;
						node.leaf = true;
						return node.eval;
					}
				} else if (egtbscore == 0) {
					node.bestmove = 0;
					node.eval = EvalConstants.SCORE_DRAW;
					node.leaf = true;
					return node.eval;
				}
			}
        }
		
		
		// Step 6. Static evaluation of the position
		boolean improving = false;
		int eval, pureStaticEval;
		
		if (env.getBitboard().isInCheck()) {
	        
			stack[ply].staticEval = eval = pureStaticEval = VALUE_NONE;
	        improving = false;
	        //TODO goto moves_loop;  // Skip early pruning when in check
	        
		} else if (ttHit) {
			
	        // Never assume anything on values stored in TT
			stack[ply].staticEval = eval = pureStaticEval = eval(evaluator, ply, alpha, beta);

	        // Can ttValue be used as a better position evaluation?
	        if (ttFlag == TTUtil.FLAG_EXACT
	        		|| (ttValue > eval && ttFlag == TTUtil.FLAG_LOWER)
	        		|| (ttValue <= eval && ttFlag == TTUtil.FLAG_UPPER)
	        		) {
	        	stack[ply].staticEval = eval = ttScore;
	        }
		} else {
			if (ply == 0) {
				stack[ply].staticEval = eval = pureStaticEval = eval(evaluator, ply, alpha, beta);
			} else if (stack[ply - 1].currentMove != 0) {
	            int p = stack[ply - 1].statScore;
	            int bonus = p > 0 ? (-p - 2500) / 512 :
	                        p < 0 ? (-p + 2500) / 512 : 0;

	            pureStaticEval = eval(evaluator, ply, alpha, beta);
	            stack[ply].staticEval = eval = pureStaticEval + bonus;
	        } else {
	        	stack[ply].staticEval = eval = pureStaticEval = eval(evaluator, ply, alpha, beta);
		        //TODO stacks[ply].staticEval = eval = pureStaticEval = -stacks[ply - 1].staticEval + 2 * 20;	
	        }
	        
			int flag = TTUtil.FLAG_EXACT;
			if (pureStaticEval >= beta) {
				flag = TTUtil.FLAG_LOWER;
			} else if (pureStaticEval <= alpha) {
				flag = TTUtil.FLAG_UPPER;
			}
			
	        //TODO
			//TTUtil.addValue(cb.zobristKey, pureStaticEval, ply, depth, flag, 0);
		}
		
		
		if (!env.getBitboard().isInCheck()) {
			
		    // Step 7. Razoring (~2 Elo)
		    if (depth < 2
		        && eval <= alpha - RazorMargin) {
				int qeval = qsearch(evaluator, info, cb, moveGen, alpha, beta, ply);
				node.bestmove = 0;
				node.eval = qeval;
				node.leaf = true;
				return node.eval;
		    }

		    
		    improving = ply <= 1 ? true : (stack[ply].staticEval >= stack[ply - 2].staticEval || stack[ply - 2].staticEval == VALUE_NONE);
		    
		    
		    // Step 8. Futility pruning: child node (~30 Elo)
		    if (!rootNode
		        &&  depth < 7
		        &&  eval - futility_margin(depth, improving) >= beta
		        &&  eval < 10000) { // Do not return unproven wins
					node.bestmove = 0;
					node.eval = eval;
					node.leaf = true;
					return node.eval;
		    }
		    
		    
		    // Step 9. Null move search with verification search (~40 Elo)
			if (!rootNode
			        //TODO && (ply == 0 || stack[ply - 1].currentMove != 0)
			        && (ply == 0 || stack[ply - 1].statScore < 23200)
			        && eval >= beta
			        && pureStaticEval >= beta - 36 * depth + 225
			        && excludedMove == 0
			        && MaterialUtil.hasNonPawnPieces(cb.materialKey, cb.colorToMove)
				) {
					
					stack[ply].currentMove = 0;
					stack[ply].continuationHistory = continuationHistory.array[0][0];
					
					cb.doNullMove();
					final int reduction = ((823 + 67 * depth) / 256 + Math.min((eval - beta) / 200, 3));
					ttScore = depth - reduction <= 0 ? -qsearch(evaluator, info, cb, moveGen, -beta, -beta + 1, ply)
							: -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -beta, -beta + 1, false, !cutNode);
					cb.undoNullMove();
					
					if (ttScore >= beta) {
						node.bestmove = 0;
						node.eval = ttScore;
						node.leaf = true;
						return node.eval;
				}
			}
			
			
		    // Step 10. ProbCut (~10 Elo)
		    // If we have a good enough capture and a reduced search returns a value
		    // much above beta, we can (almost) safely prune the previous move.
		    /*if (   !isPv
		        &&  depth >= 5
		        &&  Math.abs(beta) < 32000)
		    {
		        int rbeta = Math.min(beta + 216 - 48 * (improving ? 1 : 0), MAX);
		        
		        MovePicker mp = movePickers[ply];
		        mp.init(env.getBitboard(), ttMove, rbeta - stack[ply].staticEval, captureHistory);
		        //MovePicker mp(pos, ttMove, rbeta - ss->staticEval, &thisThread->captureHistory);
		        
		        int probCutCount = 0;

		        int move;
		        while (  (move = mp.next_move()) != 0
		               && probCutCount < 3)
		            if (move != excludedMove && cb.isLegal(move))
		            {
		                probCutCount++;
		                
		                stack[ply].currentMove = move;
		                stack[ply].continuationHistory = continuationHistory.array[env.getBitboard().getMoveOps().getFigurePID(move)][env.getBitboard().getMoveOps().getToFieldID(move)];

		                cb.doMove(move);

		                // Perform a preliminary qsearch to verify that the move holds
		                //-qsearch<NonPV>(pos, ss+1, -rbeta, -rbeta+1);
		                int value = -qsearch(evaluator, info, cb, moveGen, -rbeta, -rbeta+1, ply + 1);

		                // If the qsearch held perform the regular search
		                if (value >= rbeta)
		                	//-search<NonPV>(pos, ss+1, -rbeta, -rbeta+1, depth - 4 * ONE_PLY, !cutNode);
		                    value = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 4, -rbeta, -rbeta+1, false);

		                cb.undoMove(move);

		                if (value >= rbeta)
		                    return value;
		            }
		    }*/
		}
	    
		
		int alphaOrig = alpha;		
		
		
		int figureType = env.getBitboard().getFigureType(prevSq);
		int counterMove = counterMoves.array[figureType][prevSq];
		MovePicker mp = movePickers[ply];
        mp.init(env.getBitboard(), ttMove, mainHistory, captureHistory,
        		ply >= 1 ? stack[ply - 1].continuationHistory : null,
        		ply >= 2 ? stack[ply - 2].continuationHistory : null,
        		ply >= 4 ? stack[ply - 4].continuationHistory : null,
        		counterMove, stack[ply].killers);
        
        
        int quietsSearched[] = new int[64];
        int capturesSearched[] = new int[32];
        int quietCount = 0;
        int captureCount = 0;
        
    	boolean pvExact = isPv && ttHit && ttFlag == TTUtil.FLAG_EXACT;
    	boolean ttCapture = ttMove != 0 && env.getBitboard().getMoveOps().isCaptureOrPromotion(ttMove);
    	
        int move;
        int moveCount = 0;
        int bestValue = MIN;
        while ((move = mp.next_move()) != 0) {
        
        	//System.out.println("move=" + env.getBitboard().getMoveOps().moveToString(move));        	
        	
        	stack[ply].moveCount = ++moveCount;
        	
			
        	int extension = 0;
            // Calculate new depth for this move
            // Step 13. Extensions (~70 Elo)

            // Singular extension search (~60 Elo). If all moves but one fail low on a
            // search of (alpha-s, beta-s), and just one fails high on (alpha, beta),
            // then that move is singular and should be extended. To verify this we do
            // a reduced search on all the other moves but the ttMove and if the
            // result is lower than ttValue minus a margin then we will extend the ttMove.
            /*if (    depth >= 8 * ONE_PLY
                &&  move == ttMove
                && !rootNode
                && !excludedMove // Recursive singular search is not allowed
                &&  ttValue != VALUE_NONE
                && (tte->bound() & BOUND_LOWER)
                &&  tte->depth() >= depth - 3 * ONE_PLY
                &&  pos.legal(move))
            {
                Value rBeta = std::max(ttValue - 2 * depth / ONE_PLY, -VALUE_MATE);
                ss->excludedMove = move;
                value = search<NonPV>(pos, ss, rBeta - 1, rBeta, depth / 2, cutNode);
                ss->excludedMove = MOVE_NONE;

                if (value < rBeta)
                    extension = ONE_PLY;
            }
            else if (    givesCheck // Check extension (~2 Elo)
                     &&  pos.see_ge(move))
                extension = ONE_PLY;

            // Extension if castling
            else if (type_of(move) == CASTLING)
                extension = ONE_PLY;*/
            
            int newDepth = depth - 1 + extension;
            
            
            boolean captureOrPromotion = env.getBitboard().getMoveOps().isCaptureOrPromotion(move);
            boolean moveCountPruning =   depth < 16 && moveCount >= FutilityMoveCounts[improving ? 1 : 0][depth];
            //boolean givesCheck = env.getBitboard().isCheckMove(move); 
            		
            // Step 14. Pruning at shallow depth (~170 Elo)
            /*if (  !rootNode
                //&& pos.non_pawn_material(us)
                //&& bestValue > VALUE_MATED_IN_MAX_PLY
            		) {
                if (   !captureOrPromotion
                    && !givesCheck
                    //&& (!pos.advanced_pawn_push(move) || pos.non_pawn_material() >= Value(5000))
                    ) {
                    // Move count based pruning (~30 Elo)
                    if (moveCountPruning)
                    {
                        //skipQuiets = true;
                        continue;
                    }

                    // Reduced depth of the next LMR search
                	int r = Reductions[isPv ? 1: 0][improving ? 1 : 0][Math.min(depth, 63)][Math.min(moveCount, 63)];
                    int lmrDepth = Math.max(newDepth - r, 0);

                    // Countermoves based pruning (~20 Elo)
                    //if (   lmrDepth < 3 + ((ss-1)->statScore > 0)
                    //    && (*contHist[0])[movedPiece][to_sq(move)] < CounterMovePruneThreshold
                    //    && (*contHist[1])[movedPiece][to_sq(move)] < CounterMovePruneThreshold)
                    //    continue;
                     
                    
                    // Futility pruning: parent node (~2 Elo)
                    if (   lmrDepth < 7
                        && !env.getBitboard().isInCheck()
                        && stack[ply].staticEval + 256 + 200 * lmrDepth <= alpha)
                        continue;

                    // Prune moves with negative SEE (~10 Elo)
                    if (env.getBitboard().getSEEScore(move) < -29 * lmrDepth * lmrDepth)
                        continue;
                }
                else if (   extension == 0 // (~20 Elo)
                         && env.getBitboard().getSEEScore(move) < -100 * depth)
                        continue;
            }*/
            
        	
            // Check for legality just before making the move
            if (!cb.isLegal(move))
            {
            	stack[ply].moveCount = --moveCount;
                continue;
            }
            
            // Update the current move (this must be done after singular extension search)
        	stack[ply].currentMove = move;
        	stack[ply].continuationHistory = continuationHistory.array[env.getBitboard().getMoveOps().getFigurePID(move)][env.getBitboard().getMoveOps().getToFieldID(move)];

            // Step 15. Make the move
        	cb.doMove(move);

            // Step 16. Reduced depth search (LMR). If the move fails high it will be
            // re-searched at full depth.
	        int value = alpha + 1;
			int reduction = 1;
			if (depth > 2 && moveCount > 1 && MoveUtil.isQuiet(move) && !MoveUtil.isPawnPush78(move)) {

				reduction = LMR_TABLE[Math.min(depth, 63)][Math.min(moveCount, 63)];
				/*if (moveGen.getScore() > 40) {
					reduction -= 1;
				}*/
				if (move == 0 || move == counterMove) {
					reduction -= 1;
				}
				if (!isPv) {
					reduction += 1;
				}
				if (cutNode) {
					reduction += 2;
				}
				reduction = Math.min(depth - 1, Math.max(reduction, 1));
			}

			if (EngineConstants.ENABLE_LMR && reduction != 1) {
				value = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - reduction, -alpha - 1, -alpha, false, true);
			}
			
			if (EngineConstants.ENABLE_PVS && value > alpha && moveCount > 1) {
				value = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -alpha - 1, -alpha, false, !cutNode);
			}
			
			if (value > bestValue) {
				value = -calculateBestMove(mediator, info, pvman, evaluator, cb, moveGen, ply + 1, depth - 1, -beta, -alpha, isPv, false);
			}
            
            // Step 18. Undo move
            cb.undoMove(move);
            
            
            if (value > bestValue)
            {
                bestValue = value;
				
                bestMove = move;
				
				node.bestmove = bestMove;
				node.eval = bestValue;
				node.leaf = false;
				
				if (ply + 1 < ISearch.MAX_DEPTH) {
					pvman.store(ply + 1, node, pvman.load(ply + 1), true);
				}
				
                if (value > alpha)
                {
                    /*if (isPv && !rootNode) // Update pv even in fail-high case
                        update_pv(stack[ply].pv, move, stack[ply + 1].pv);
                     */
                	alpha = value;
                	
                    //if (isPv && value < beta) // Update alpha! Always alpha < beta
                    //    alpha = value;
                    //else
                    //{
                	if (value >= beta) {
                        assert(value >= beta); // Fail high
                        //stack[ply].statScore = 0;
                        break;
                	}
                    //}
                }
            }
            
            if (move != bestMove)
            {
                if (captureOrPromotion && captureCount < 32)
                    capturesSearched[captureCount++] = move;

                else if (!captureOrPromotion && quietCount < 64)
                    quietsSearched[quietCount++] = move;
            }
        }
        
        
		if (bestMove != 0)
	    {
	        // Quiet best move: update move sorting heuristics
	        if (!env.getBitboard().getMoveOps().isCaptureOrPromotion(bestMove))
	            update_quiet_stats(ply, stack, bestMove, quietsSearched, quietCount,
	                               stat_bonus(depth + (bestValue > beta + 100 ? 1 : 0)));

	        update_capture_stats(ply, stack, bestMove, capturesSearched, captureCount, stat_bonus(depth + 1));
	    }
		
		
		if (moveCount == 0) {
			if (cb.checkingPieces == 0) {
				node.bestmove = 0;
				node.eval = EvalConstants.SCORE_DRAW;
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
		
		int flag = TTUtil.FLAG_EXACT;
		if (bestValue >= beta) {
			flag = TTUtil.FLAG_LOWER;
		} else if (bestValue <= alphaOrig) {
			flag = TTUtil.FLAG_UPPER;
		}

		if (!SearchUtils.isMateVal(bestValue)) {
			TTUtil.addValue(cb.zobristKey, bestValue, ply, depth, flag, bestMove);
		}
		
		//validatePV(node, depth, isPv);
		
		return bestValue;
	}


	  // update_quiet_stats() updates move sorting heuristics when a new quiet best move is found

	  void update_quiet_stats(int ply, Stack[] stack, int bestMove, int[] quiets, int quietsCnt, int bonus) {

	    if (stack[ply].killers[0] != bestMove)
	    {
	    	stack[ply].killers[1] = stack[ply].killers[0];
	    	stack[ply].killers[0] = bestMove;
	    }

	    int us = env.getBitboard().getColourToMove();
	    int from_to = env.getBitboard().getMoveOps().getFromFieldID(bestMove) * env.getBitboard().getMoveOps().getToFieldID(bestMove);
	    mainHistory.array[us][from_to] += bonus;
	    update_continuation_histories(ply, stack, env.getBitboard().getMoveOps().getFigurePID(bestMove), env.getBitboard().getMoveOps().getToFieldID(bestMove), bonus);

	    if (ply > 0 && stack[ply - 1].currentMove != 0)
	    {
	        int prevSq = env.getBitboard().getMoveOps().getToFieldID(stack[ply - 1].currentMove);
	        counterMoves.array[env.getBitboard().getFigureType(prevSq)][prevSq] = bestMove;
	    }

	    // Decrease all the other played quiet moves
	    for (int i = 0; i < quietsCnt; ++i)
	    {
	    	from_to = env.getBitboard().getMoveOps().getFromFieldID(quiets[i]) * env.getBitboard().getMoveOps().getToFieldID(quiets[i]);
	        mainHistory.array[us][from_to] -= bonus;
	        update_continuation_histories(ply, stack, env.getBitboard().getMoveOps().getFigurePID(quiets[i]), env.getBitboard().getMoveOps().getToFieldID(quiets[i]), -bonus);
	    }
	  }


	  // update_continuation_histories() updates histories of the move pairs formed
	  // by moves at ply -1, -2, and -4 with current move.

	  void update_continuation_histories(int ply, Stack[] stack, int pc, int to, int bonus) {
		  if (ply >= 1 && stack[ply - 1].currentMove != 0)
	            stack[ply - 1].continuationHistory.array[pc][to] = stack[ply - 1].continuationHistory.array[pc][to] + bonus;
		  
		  if (ply >= 2 && stack[ply - 2].currentMove != 0)
	            stack[ply - 2].continuationHistory.array[pc][to] = stack[ply - 2].continuationHistory.array[pc][to] + bonus;

		  if (ply >=4 && stack[ply - 4].currentMove != 0)
	            stack[ply - 4].continuationHistory.array[pc][to] = stack[ply - 4].continuationHistory.array[pc][to] + bonus;
	  }
	  
	  
	  // update_capture_stats() updates move sorting heuristics when a new capture best move is found

	  void update_capture_stats(int ply, Stack[] stack, int bestMove, int[] captures, int captureCnt, int bonus) {

	      //CapturePieceToHistory& captureHistory =  pos.this_thread()->captureHistory;
	      int moved_piece = env.getBitboard().getMoveOps().getFigurePID(bestMove);
	      int capturedType = env.getBitboard().getMoveOps().getCapturedFigureType(bestMove);

          captureHistory.array[moved_piece][env.getBitboard().getMoveOps().getToFieldID(bestMove)][capturedType] += bonus;

	      // Decrease all the other played capture moves
	      for (int i = 0; i < captureCnt; ++i)
	      {
	          moved_piece = env.getBitboard().getMoveOps().getFigurePID(captures[i]);
	          capturedType = env.getBitboard().getMoveOps().getCapturedFigureType(captures[i]);
	          captureHistory.array[moved_piece][env.getBitboard().getMoveOps().getToFieldID(captures[i])][capturedType] -= bonus;
	      }
	  }
	  
	  
	  // History and stats update bonus, based on depth
	  int stat_bonus(int depth) {
	    //return depth > 17 ? 0 : 29 * depth * depth + 138 * depth - 134;
		  return depth * depth;
	  }
	  
	  
	public int qsearch(IEvaluator evaluator, ISearchInfo info, final ChessBoard cb, final MoveGenerator moveGen, int alpha, final int beta, final int ply) {
		
		
		info.setSearchedNodes(info.getSearchedNodes() + 1);
		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		
		long ttValue = TTUtil.getTTValue(cb.zobristKey);
		int ttScore = TTUtil.getScore(ttValue);
		if (ttValue != 0) {
			if (!EngineConstants.TEST_TT_VALUES) {
				if (TTUtil.getDepth(ttValue) >= 0) {
					switch (TTUtil.getFlag(ttValue)) {
					case TTUtil.FLAG_EXACT:
						return ttScore;
					case TTUtil.FLAG_LOWER:
						if (ttScore >= beta) {
							return ttScore;
						}
						break;
					case TTUtil.FLAG_UPPER:
						if (ttScore <= alpha) {
							return ttScore;
						}
					}
				}
			}
		}
		
		final int alphaOrig = alpha;
		
		int eval = ISearch.MIN;
		if (cb.checkingPieces == 0) {
			eval = eval(evaluator, ply, alpha, beta);
			if (eval >= beta) {
				return eval;
			}
			alpha = Math.max(alpha, eval);
		}

		moveGen.startPly();
		
		int phase = PHASE_TT;
		while (phase <= PHASE_ATTACKING) {
			switch (phase) {
				case PHASE_TT:
					if (ttValue != 0) {
						int ttMove = TTUtil.getMove(ttValue);
						if (env.getBitboard().getMoveOps().isCaptureOrPromotion(ttMove)) {
							moveGen.addMove(ttMove);
						}
					}
					break;
				case PHASE_ATTACKING:
					moveGen.generateAttacks(cb);
					moveGen.setMVVLVAScores(cb);
					moveGen.sort();
					break;
			}
			
			while (moveGen.hasNext()) {
				final int move = moveGen.next();
	
				if (cb.checkingPieces == 0) {
					if (MoveUtil.isPromotion(move)) {
						if (MoveUtil.getMoveType(move) != MoveUtil.TYPE_PROMOTION_Q) {
							continue;
						}
					} else if (EngineConstants.ENABLE_Q_FUTILITY_PRUNING
							&& eval + FUTILITY_MARGIN_Q_SEARCH + EvalConstants.MATERIAL[MoveUtil.getAttackedPieceIndex(move)] < alpha) {
						continue;
					}
				}
				
				if (!cb.isLegal(move)) {
					continue;
				}
				
				//if (cb.checkingPieces == 0) {
					if (EngineConstants.ENABLE_Q_PRUNE_BAD_CAPTURES && !cb.isDiscoveredMove(MoveUtil.getFromIndex(move)) && SEEUtil.getSeeCaptureScore(cb, move) <= 0) {
						continue;
					}
				//}
	
				cb.doMove(move);
	
				if (EngineConstants.ASSERT) {
					cb.changeSideToMove();
					Assert.isTrue(0 == CheckUtil.getCheckingPieces(cb));
					cb.changeSideToMove();
				}
				
				final int score = -qsearch(evaluator, info, cb, moveGen, -beta, -alpha, ply + 1);
	
				cb.undoMove(move);
	
				if (score > alpha) {
					int flag = TTUtil.FLAG_EXACT;
					if (score >= beta) {
						flag = TTUtil.FLAG_LOWER;
					} else if (score <= alphaOrig) {
						flag = TTUtil.FLAG_UPPER;
					}
					if (!SearchUtils.isMateVal(score)) {
						TTUtil.addValue(cb.zobristKey, score, ply, 0, flag, move);
					}
				}
				
				if (score >= beta) {
					moveGen.endPly();				
					return score;
				}
				alpha = Math.max(alpha, score);
			}
			
			phase++;
		}
		moveGen.endPly();
		
		return alpha;
	}
	
	
	private int eval(IEvaluator evaluator, final int ply, int alpha, int beta) {
		long value = EvalUtil.getValue(env.getBitboard().getHashKey());
		if (value != 0) {
			return EvalUtil.getScore(value);
		}
		int eval = (int) evaluator.fullEval(ply, alpha, beta, 0);
		EvalUtil.addValue(env.getBitboard().getHashKey(), eval);
		return eval;
	}
	
	
	private boolean extractFromTT(int ply, PVNode result, long currentTTValue, ISearchInfo info, boolean isPv) {
		
		if (currentTTValue == 0) {
			throw new IllegalStateException("currentTTValue == 0");
		}
		
		result.leaf = true;
		
		if (ply > 0 && isDraw()) {
			result.eval = EvalConstants.SCORE_DRAW;
			result.bestmove = 0;
			return true;
		}

		if (info.getSelDepth() < ply) {
			info.setSelDepth(ply);
		}
		
		result.eval = TTUtil.getScore(currentTTValue);
		result.bestmove = TTUtil.getMove(currentTTValue);
		
		boolean draw = false;
		
		if (isPv) {
			
			env.getBitboard().makeMoveForward(result.bestmove);
			
			long hashkey = env.getBitboard().getHashKey();
			long nextTTValue = TTUtil.getTTValue(hashkey);
			
			if (nextTTValue != 0) {
				draw = extractFromTT(ply + 1, result.child, nextTTValue, info, isPv);
				if (draw) {
					result.eval = EvalConstants.SCORE_DRAW;
				} else {
					result.leaf = false;
				}
			}
			
			env.getBitboard().makeMoveBackward(result.bestmove);
		}
		
		return draw;
	}
		
	
	// Add a small random component to draw evaluations to keep search dynamic
	// and to avoid 3fold-blindness.
	int value_draw(int depth) {
	  return depth < 4 ? VALUE_DRAW
	                   : VALUE_DRAW + (int)(Math.random() * 17);
	}
}

package bagaturchess.search.impl.pv;


import java.util.EmptyStackException;
import java.util.Stack;

import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.impl.alg.SearchImpl;
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.env.SearchEnv;


public class PVValidation {
	
	
	public static void validatePV(SearchImpl search, SearchEnv env, PVNode node, int ply, int expectedDepth, boolean isPv, int alpha, int beta, Stack<Integer> stack) {
		
		
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
		search.search_types_stats[cur.type]++;
		if (search.search_types_stats[cur.type] % 100000 == 0) {
		    for (int i = 0; i < search.search_types_stats.length; i++) {
		        System.out.print(i + "=" + search.search_types_stats[i] + " ");
		    }
		    System.out.println();
		}
		
		int static_eval = eval_sign * search.eval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, isPv);
		
		
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
				
				if (!search.isDraw(false)) {
					
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
			
			case PVNode.TYPE_ALPHA_CUTOFF_QSEARCH:
				
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
}

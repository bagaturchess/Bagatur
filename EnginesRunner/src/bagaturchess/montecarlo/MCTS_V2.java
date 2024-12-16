package bagaturchess.montecarlo;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.deeplearning.impl_nnue_v3.NNUEEvaluatorFactory;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.impl.eval.cache.EvalCache_Impl2;


public class MCTS_V2 {

	
    public static void main(String[] args) {
    	
		String fen = Constants.INITIAL_BOARD;
		//String fen = "rnbqk1nr/pppp1pp1/1p2p2p/8/8/1P1P2PP/P1PbPPB1/RNBQK1NR w KQkq - 0 6";
		//String fen = "5r2/1p1RRrk1/4Qq1p/1PP3p1/8/4B3/1b3P1P/6K1 w - - 0 1"; //bm Qxf7+ Rxf7+; id WAC.235
		//String fen = "8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - 0 1"; //bm Rxb2
		//String fen = "2r1n2r/1q4k1/2p1pn2/ppR4p/4PNbP/P1BBQ3/1P4P1/R5K1 b - - 1 32";
		
		
		
		IBoardConfig boardConfig = new bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler.Bagatur_ALL_BoardConfigImpl();
		final IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen, boardConfig);
        
        MCTS mcts = new MCTS();
        
        /*List<Integer> moves = mcts.genAllLegalMoves(bitboard);
        System.out.println(bitboard.isInCheck());
        for (int i=0; i<moves.size(); i++) {
        	System.out.println(bitboard.getMoveOps().moveToString(moves.get(i)));
        }*/
        
        NNUEEvaluatorFactory evaluator_factory = new NNUEEvaluatorFactory();
        IEvaluator evaluator = evaluator_factory.create(bitboard, new EvalCache_Impl2(1000000));
        		
        List<Integer> best_line = mcts.findBestMove(bitboard, evaluator, 1000);
        
        System.out.print("Best Line: ");
        for (int i = 0; i < best_line.size(); i++) {
        	System.out.print(bitboard.getMoveOps().moveToString(best_line.get(i)) + " ");
        }
        
        //System.out.println("Best Move: " + bitboard.getMoveOps().moveToString(bestMove));
    }
    
    
	private static class MCTSNode {
		
	    MCTSNode parent;
	    List<MCTSNode> children;
	    
	    IGameStatus status;
	    List<Integer> legal_moves;
	    int originating_move;
	    int colour_to_move;
	    
	    int visits;
	    double value;

	    
	    //Creates the root node
	    public MCTSNode(IGameStatus status, List<Integer> legal_moves, int colour_to_move) {
	    	
	    	if (status != IGameStatus.NONE) {
	    		
	    		throw new IllegalStateException();
	    	}
	    	
	    	this.children = new ArrayList<MCTSNode>();
	    	
	    	this.status = status;
	    	this.legal_moves = legal_moves;
	    	this.colour_to_move = colour_to_move;
	    }
	    
	    
	    //Creates any node
	    public MCTSNode(MCTSNode parent, int originating_move, IGameStatus status, List<Integer> legal_moves, int colour_to_move) {
	        
	    	this.parent = parent;
	        this.children = new ArrayList<MCTSNode>();
	        
	        this.status = status;
	        this.legal_moves = legal_moves;
	        this.originating_move = originating_move;
	        this.colour_to_move = colour_to_move;
	        
	        this.visits = 0;
	        this.value = 0.0;
	    }

	    public boolean isFullyExpanded() {
	        return children.size() == legal_moves.size();
	    }

	    public boolean isTerminalNode() {
	        return status != IGameStatus.NONE;
	    }
	}
	
	
	private static class MCTS {
		
		
	    private static final double EXPLORATION_FACTOR = Math.sqrt(2);
	    
	    private IMoveList movesBuffer = new BaseMoveList(333);
	    
	    
	    private MCTS() {
	    	
	    }
	    
	    
	    public List<Integer> findBestMove(IBitBoard bitboard, IEvaluator evaluator, int iterations) {
	    
	        MCTSNode rootNode = new MCTSNode(bitboard.getStatus(),
	        		genAllLegalMoves(bitboard), bitboard.getColourToMove());

	        for (int i = 0; i < iterations; i++) {
	        	
	        	System.out.println("iterations=" + i);
	        	
	            MCTSNode selectedNode = select(rootNode);
	            
	            List<Integer> movesToPlay = getPathToNode(selectedNode);
	            
	            for (int j=0; j<movesToPlay.size(); j++) {
	            	bitboard.makeMoveForward(movesToPlay.get(j));
	            }
	            
	            MCTSNode expandedNode = expand(selectedNode, bitboard);
	            
	            if (expandedNode != selectedNode) {
	            	
	            	bitboard.makeMoveForward(expandedNode.originating_move);
	            }
	            
	            double simulationResult = simulate(expandedNode, bitboard, evaluator);
	            
	            if (expandedNode != selectedNode) {
	            	
	            	bitboard.makeMoveBackward(expandedNode.originating_move);
	            }
	            
	            for (int j=movesToPlay.size() - 1; j >= 0; j--) {
	            	bitboard.makeMoveBackward(movesToPlay.get(j));
	            }
	            
	            backpropagate(expandedNode, simulationResult);
	        }

	        
	        for (MCTSNode node: rootNode.children) {
	        	System.out.println(
	        			bitboard.getMoveOps().moveToString(node.originating_move)
	        			+ " " + node.value
	        			+ " " + node.visits
	        			+ " " + node.value / node.visits
	        			);
	        }
	        
	        
	        return getMostVisitedChildrenMoves(rootNode);
	    }
	    
	    
	    private List<Integer> getPathToNode(MCTSNode node) {
	    	
	    	List<Integer> path = new ArrayList<Integer>();
	    	
	    	//node.originating_move is 0 for the root node
	    	while (node != null && node.originating_move != 0) {
	    		
	    		path.add(0, node.originating_move);
	    		
	    		node = node.parent;
	    	}
	    	
	    	return path;
	    }
	    
	    
	    private List<Integer> genAllLegalMoves(IBitBoard bitboard) {

			movesBuffer.clear();
			
			if (bitboard.isInCheck()) {
				
				bitboard.genKingEscapes(movesBuffer);
				
			} else {
				
				bitboard.genAllMoves(movesBuffer);
			}
	    	
			List<Integer> legalMoves = new ArrayList<Integer>(55);
			
			int cur_move = 0;
			while ((cur_move = movesBuffer.next()) != 0) {
				
				legalMoves.add(cur_move);
			}
			
			return legalMoves;
		}


		private MCTSNode select(MCTSNode node) {
	        while (!node.isTerminalNode() && node.isFullyExpanded()) {
	            node = getBestChild(node, EXPLORATION_FACTOR);
	        }
	        return node;
	    }
		
		
	    private MCTSNode expand(MCTSNode node, IBitBoard bitboard) {
	        
	    	if (node.isFullyExpanded()) {
	    		
	    		return node;
	    	}

	        List<Integer> legalMoves = node.legal_moves;
	        
	        for (int move : legalMoves) {
	        	
	            boolean alreadyExpanded = false;
	            
	            for (MCTSNode child : node.children) {
	            	
	                if (child.originating_move == move) {
	                	
	                    alreadyExpanded = true;
	                    break;
	                }
	            }
	            
	            if (!alreadyExpanded) {
	            	
	            	bitboard.makeMoveForward(move);
	                
	                MCTSNode childNode = new MCTSNode(node, move, bitboard.getStatus(),
	                		genAllLegalMoves(bitboard), bitboard.getColourToMove());
	                
	                node.children.add(childNode);
	                
	                bitboard.makeMoveBackward(move);
	                
	                return childNode;
	            }
	        }
	        
	        return null;
	    }
	    
	    
	    private double simulate(MCTSNode node, IBitBoard bitboard, IEvaluator evaluator) {

	    	List<Integer> moves = new ArrayList<Integer>();
	    	
	        while (bitboard.getStatus() == IGameStatus.NONE) {
	        	
	        	if (bitboard.getPlayedMovesCount() >= EngineConstants.MAX_MOVES - 2) {
	        		break;
	        	}
	        	
	            List<Integer> legalMoves = genAllLegalMoves(bitboard);
	            
	            int selected_move = getBestMove(legalMoves, bitboard, evaluator);
	            
	            bitboard.makeMoveForward(selected_move);
	            
	            moves.add(selected_move);
	        }

	        double result = bitboard.getPlayedMovesCount() >= EngineConstants.MAX_MOVES - 2 ?
	        		0 : evaluateResult(bitboard.getStatus());
	        
			//Revert moves
			for (int i = moves.size() - 1; i >=0; i--) {
				
				bitboard.makeMoveBackward(moves.get(i));
			}
			
	        return result;
	    }


		private int getBestMove(List<Integer> legalMoves, IBitBoard bitboard, IEvaluator evaluator) {
			
			if (legalMoves.size() == 0) {
				
				throw new IllegalStateException();
			}
			
			int selected_move = 0;
			int selected_move_eval = Integer.MIN_VALUE;
			for (int i = 0; i < legalMoves.size(); i++) {
				
				int cur_move = legalMoves.get(i);
				
				bitboard.makeMoveForward(cur_move);
				
				int cur_eval = -evaluator.fullEval(-1, -1, -1, -1);
				
				bitboard.makeMoveBackward(cur_move);
				
				if (cur_eval >= selected_move_eval) {
					
					selected_move_eval = cur_eval;
					selected_move = cur_move;
				}
			}
			
			//int selected_move = legal_moves.get(random.nextInt(legal_moves.size()));
			
			return selected_move;
		}
	    
	    
	    private double evaluateResult(IGameStatus status) {
			
			switch (status) {
			
				case NONE:
					throw new IllegalStateException("status=" + status);
					
				case DRAW_3_STATES_REPETITION:
					return 0;
					
				case MATE_WHITE_WIN:
					return 1;
					
				case MATE_BLACK_WIN:
					return -1;
					
				case UNDEFINED:
					throw new IllegalStateException("status=" + status);
					
				case STALEMATE_WHITE_NO_MOVES:
					return 0;
					
				case STALEMATE_BLACK_NO_MOVES:
					return 0;
					
				case DRAW_50_MOVES_RULE:
					return 0;
					
				case NO_SUFFICIENT_MATERIAL:
					return 0;
					
				case PASSER_WHITE:
					throw new IllegalStateException("status=" + status);
					
				case PASSER_BLACK:
					throw new IllegalStateException("status=" + status);
					
				case NO_SUFFICIENT_WHITE_MATERIAL:
					throw new IllegalStateException("status=" + status);
					
				case NO_SUFFICIENT_BLACK_MATERIAL:
					throw new IllegalStateException("status=" + status);
					
				default:
					throw new IllegalStateException("status=" + status);
					
			}
	    }
	    
	    
	    private void backpropagate(MCTSNode node, double result) {
	        MCTSNode currentNode = node;

	        while (currentNode != null) {
	            currentNode.visits++;
	            //currentNode.value += (currentNode.colour_to_move == Constants.COLOUR_BLACK ? result : -result);
	            currentNode.value += result;
	            currentNode = currentNode.parent;
	        }
	    }
	    
	    
	    private MCTSNode getBestChild(MCTSNode node, double explorationFactor) {
	        MCTSNode bestChild = null;
	        double bestValue = Double.NEGATIVE_INFINITY;

	        for (MCTSNode child : node.children) {
	            double uctValue = getUCTValue(child, explorationFactor);
	            if (uctValue > bestValue) {
	                bestValue = uctValue;
	                bestChild = child;
	            }
	        }

	        if (bestChild == null) {
	            throw new IllegalStateException("No children found");
	        }

	        return bestChild;
	    }
	    
	    
	    private double getUCTValue(MCTSNode node, double explorationFactor) {
	        if (node.visits == 0) return Double.MAX_VALUE;
	        double exploitation = node.value / node.visits;
	        //double exploitation = (node.colour_to_move == Constants.COLOUR_WHITE ? node.value : -node.value) / node.visits;
	        double exploration = explorationFactor * Math.sqrt(Math.log(node.parent.visits) / node.visits);
	        return exploitation + exploration;
	    }
	    
	    
	    private MCTSNode getMostVisitedChild(MCTSNode node) {
	        MCTSNode bestChild = null;
	        int maxVisits = -1;

	        for (MCTSNode child : node.children) {
	            if (child.visits > maxVisits) {
	                maxVisits = child.visits;
	                bestChild = child;
	            }
	        }

	        /*if (bestChild == null) {
	            throw new IllegalStateException("No children found");
	        }*/

	        return bestChild;
	    }
	    
	    
	    private MCTSNode getBestValuedChild(MCTSNode node) {
	        MCTSNode bestChild = null;
	        double best_eval = Double.NEGATIVE_INFINITY;

	        for (MCTSNode child : node.children) {
	            double score = child.value / child.visits;
	            if (score > best_eval) {
	                best_eval = score;
	                bestChild = child;
	            }
	        }

	        return bestChild;
	    }
	    
	    
	    private List<Integer> getMostVisitedChildrenMoves(MCTSNode node) {
	    	
	    	List<Integer> moves = new ArrayList<Integer>();
	    	
	    	while (node != null) {
	    		//node = getMostVisitedChild(node);
	    		node = getBestValuedChild(node);
	    		if (node != null) moves.add(node.originating_move);
	    	}
	    	
	    	return moves;
	    }
	}
}

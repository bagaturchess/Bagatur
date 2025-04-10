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
import bagaturchess.bitboard.impl1.internal.SEEUtil;
import bagaturchess.deeplearning.impl_nnue_v3.NNUEEvaluatorFactory;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.impl.eval.cache.EvalCache_Impl2;


public class MCTS_V2 {

	
    public static void main(String[] args) {
    	
		//String fen = Constants.INITIAL_BOARD;
		//String fen = "rnbqk1nr/pppp1pp1/1p2p2p/8/8/1P1P2PP/P1PbPPB1/RNBQK1NR w KQkq - 0 6";
		//String fen = "5r2/1p1RRrk1/4Qq1p/1PP3p1/8/4B3/1b3P1P/6K1 w - - 0 1"; //bm Qxf7+ Rxf7+; id WAC.235
		String fen = "8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - 0 1"; //bm Rxb2
		//String fen = "2r1n2r/1q4k1/2p1pn2/ppR4p/4PNbP/P1BBQ3/1P4P1/R5K1 b - - 1 32";
		
		
		IBoardConfig boardConfig = new bagaturchess.learning.goldmiddle.pesto.cfg.BoardConfigImpl_PeSTO();
		final IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen, boardConfig);
        
        MCTS mcts = new MCTS_V2_Play();
        //MCTS mcts = new MCTS_V2_Eval();
        
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
    
    
	static class MCTSNode {
		
	    MCTSNode parent;
	    List<MCTSNode> children;
	    
	    IGameStatus status;
	    List<Integer> legal_moves;
	    int originating_move;
	    int originating_color;
	    int root_color;
	    
	    int visits;
	    double value;

	    
	    //Creates the root node
	    public MCTSNode(IGameStatus status, List<Integer> legal_moves, int colour_to_move, int root_color) {
	    	
	    	if (status != IGameStatus.NONE) {
	    		
	    		throw new IllegalStateException();
	    	}
	    	
	    	this.children = new ArrayList<MCTSNode>();
	    	
	    	this.status = status;
	    	this.legal_moves = legal_moves;
	    	this.originating_color = colour_to_move;
	    	this.root_color = root_color;
	    }
	    
	    
	    //Creates any node
	    public MCTSNode(MCTSNode parent, int originating_move, IGameStatus status,
	    		List<Integer> legal_moves, int colour_to_move, int root_color) {
	        
	    	this.parent = parent;
	        this.children = new ArrayList<MCTSNode>();
	        
	        this.status = status;
	        this.legal_moves = legal_moves;
	        this.originating_move = originating_move;
	        this.originating_color = colour_to_move;
	        this.root_color = root_color;
	        
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
	
	
	static abstract class MCTS {
		
	    
	    private static final double EXPLORATION_FACTOR 	= Math.sqrt(2);
	    
	    private IMoveList movesBuffer 					= new BaseMoveList(333);
	    
	    
	    MCTS() {
	    	
	    }
	    
	    
	    public List<Integer> findBestMove(IBitBoard bitboard, IEvaluator evaluator, int iterations) {
	    
	        MCTSNode rootNode = new MCTSNode(bitboard.getStatus(),
	        		genAllLegalMoves(bitboard), 1 - bitboard.getColourToMove(), bitboard.getColourToMove());

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
	        System.out.println("ROOT:"
	        		+ " " + rootNode.value
        			+ " " + rootNode.visits
        			+ " " + rootNode.value / rootNode.visits);
	        
	        return getMostVisitedChildrenMoves(rootNode);
	    }
	    
	    
	    protected abstract double simulate(MCTSNode node, IBitBoard bitboard, IEvaluator evaluator);


		private List<Integer> getPathToNode(MCTSNode node) {
	    	
	    	List<Integer> path = new ArrayList<Integer>();
	    	
	    	//node.originating_move is 0 for the root node
	    	while (node != null && node.originating_move != 0) {
	    		
	    		path.add(0, node.originating_move);
	    		
	    		node = node.parent;
	    	}
	    	
	    	return path;
	    }
	    
	    
	    protected List<Integer> genAllLegalMoves(IBitBoard bitboard) {

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
	                		genAllLegalMoves(bitboard), 1 - bitboard.getColourToMove(), node.root_color);
	                
	                node.children.add(childNode);
	                
	                bitboard.makeMoveBackward(move);
	                
	                return childNode;
	            }
	        }
	        
	        return null;
	    }
	    
	    
	    private void backpropagate(MCTSNode node, double result) {
	    	
	    	MCTSNode currentNode = node;

	        while (currentNode != null) {
	            currentNode.visits++;
	            //currentNode.value += result;
	            currentNode.value += (node.originating_color == Constants.COLOUR_WHITE ? result : -result);
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
	        //double exploitation = (node.root_color == Constants.COLOUR_BLACK ? node.value : -node.value) / node.visits;
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
	            //double score = (child.color_to_move == node.root_color ? child.value : -child.value) / child.visits;
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
	    		node = getMostVisitedChild(node);
	    		//node = getBestValuedChild(node);
	    		if (node != null) moves.add(node.originating_move);
	    	}
	    	
	    	return moves;
	    }
	    
	    
		protected int[] getBestMove(List<Integer> legalMoves, IBitBoard bitboard, IEvaluator evaluator) {
			
			if (legalMoves.size() == 0) {
				
				throw new IllegalStateException();
			}
			
			int selected_move = 0;
			int selected_move_eval = Integer.MIN_VALUE;
			
			for (int i = 0; i < legalMoves.size(); i++) {
				
				int cur_move = legalMoves.get(i);
				
				int seeMove = bitboard.getSEEScore(cur_move);
				//int seeField = -bitboard.getSEEFieldScore(bitboard.getMoveOps().getFromFieldID(cur_move));
				
				bitboard.makeMoveForward(cur_move);
				
				int cur_eval = (int) (-evaluator.fullEval(-1, -1, -1, -1) + (Math.random() * 10 - 5));
				if (seeMove < 0) {
					//cur_eval += seeMove;
				}
				//cur_eval += seeField / 10;
				
				bitboard.makeMoveBackward(cur_move);
				
				if (cur_eval >= selected_move_eval) {
					
					selected_move_eval = cur_eval;
					selected_move = cur_move;
				}
			}
			
			//int selected_move = legal_moves.get(random.nextInt(legal_moves.size()));
			
			return new int[] {selected_move, selected_move_eval};
		}
	}
}

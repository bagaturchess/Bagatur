package bagaturchess.test_tactics;


import java.util.ArrayList;
import java.util.List;


// Class to hold FEN, best moves, and ID
public class ChessPuzzle {
	
	
    private String fen;
    private List<String> bestMovesStr;
    private String id;
    private List<Integer> bestMoves = new ArrayList<Integer>();
    
    
    public ChessPuzzle(String fen, List<String> bestMovesStr, String id) {
        this.fen = fen;
        this.bestMovesStr = bestMovesStr;
        this.id = id;
    }

    
    public String getFen() {
        return fen;
    }

    
    public List<String> getBestMovesStr() {
        return bestMovesStr;
    }

    
    public String getId() {
        return id;
    }
    
    
    public List<Integer> getBestMoves() {
    	return bestMoves;
    }
    
    
    void addBestMove(int move) {
    	bestMoves.add(move);
    }
    
    
    @Override
    public String toString() {
        return "FEN: " + fen + ", Best Moves: " + bestMovesStr + ", ID: " + id;
    }
}

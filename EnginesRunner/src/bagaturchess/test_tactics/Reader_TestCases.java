package bagaturchess.test_tactics;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;


public class Reader_TestCases {
	
	
	private static Map<String, Integer> fieldNamesToIds = new HashMap<String,Integer>();
	
	
	static {
		
		fieldNamesToIds.put("h1", 0);
		fieldNamesToIds.put("g1", 1);
		fieldNamesToIds.put("f1", 2);
		fieldNamesToIds.put("e1", 3);
		fieldNamesToIds.put("d1", 4);
		fieldNamesToIds.put("c1", 5);
		fieldNamesToIds.put("b1", 6);
		fieldNamesToIds.put("a1", 7);
		
		fieldNamesToIds.put("h2", 8);
		fieldNamesToIds.put("g2", 9);
		fieldNamesToIds.put("f2", 10);
		fieldNamesToIds.put("e2", 11);
		fieldNamesToIds.put("d2", 12);
		fieldNamesToIds.put("c2", 13);
		fieldNamesToIds.put("b2", 14);
		fieldNamesToIds.put("a2", 15);
		
		fieldNamesToIds.put("h3", 16);
		fieldNamesToIds.put("g3", 17);
		fieldNamesToIds.put("f3", 18);
		fieldNamesToIds.put("e3", 19);
		fieldNamesToIds.put("d3", 20);
		fieldNamesToIds.put("c3", 21);
		fieldNamesToIds.put("b3", 22);
		fieldNamesToIds.put("a3", 23);
		
		fieldNamesToIds.put("h4", 24);
		fieldNamesToIds.put("g4", 25);
		fieldNamesToIds.put("f4", 26);
		fieldNamesToIds.put("e4", 27);
		fieldNamesToIds.put("d4", 28);
		fieldNamesToIds.put("c4", 29);
		fieldNamesToIds.put("b4", 30);
		fieldNamesToIds.put("a4", 31);
		
		fieldNamesToIds.put("h5", 32);
		fieldNamesToIds.put("g5", 33);
		fieldNamesToIds.put("f5", 34);
		fieldNamesToIds.put("e5", 35);
		fieldNamesToIds.put("d5", 36);
		fieldNamesToIds.put("c5", 37);
		fieldNamesToIds.put("b5", 38);
		fieldNamesToIds.put("a5", 39);
		
		fieldNamesToIds.put("h6", 40);
		fieldNamesToIds.put("g6", 41);
		fieldNamesToIds.put("f6", 42);
		fieldNamesToIds.put("e6", 43);
		fieldNamesToIds.put("d6", 44);
		fieldNamesToIds.put("c6", 45);
		fieldNamesToIds.put("b6", 46);
		fieldNamesToIds.put("a6", 47);
		
		fieldNamesToIds.put("h7", 48);
		fieldNamesToIds.put("g7", 49);
		fieldNamesToIds.put("f7", 50);
		fieldNamesToIds.put("e7", 51);
		fieldNamesToIds.put("d7", 52);
		fieldNamesToIds.put("c7", 53);
		fieldNamesToIds.put("b7", 54);
		fieldNamesToIds.put("a7", 55);
		
		fieldNamesToIds.put("h8", 56);
		fieldNamesToIds.put("g8", 57);
		fieldNamesToIds.put("f8", 58);
		fieldNamesToIds.put("e8", 59);
		fieldNamesToIds.put("d8", 60);
		fieldNamesToIds.put("c8", 61);
		fieldNamesToIds.put("b8", 62);
		fieldNamesToIds.put("a8", 63);
	}
	
	
	private static Map<String, Integer> fileNamesToIds = new HashMap<String,Integer>();
	
	
	static {
		
		fileNamesToIds.put("h", 7);
		fileNamesToIds.put("g", 6);
		fileNamesToIds.put("f", 5);
		fileNamesToIds.put("e", 4);
		fileNamesToIds.put("d", 3);
		fileNamesToIds.put("c", 2);
		fileNamesToIds.put("b", 1);
		fileNamesToIds.put("a", 0);
	}
	
	
    // Method to parse a line and extract FEN, best moves (bm), and ID
	private static ChessPuzzle parseLine(String line) {
    	
        int bmIndex = line.indexOf("bm");
        int idIndex = line.indexOf("id");
        
        if (bmIndex > idIndex) {
        	
            String fen = line.substring(0, idIndex).trim();

            // Extract best moves (bm)
            String bmPart = line.substring(bmIndex + 3).trim(); // Skipping "bm " (3 characters)
            List<String> bestMoves = Arrays.asList(bmPart.split("[ ,;]+")); // Split multiple moves by space or comma

            // Extract ID part (everything after "id")

            String idPart = line.substring(idIndex + 3).trim(); // Skipping "id " (3 characters)
            String id = idPart.replace("\"", "").trim(); // Remove surrounding quotes

            return new ChessPuzzle(fen, bestMoves, id);
            
        } else {
        	
            String fen = line.substring(0, bmIndex).trim();

            // Extract best moves (bm)
            String bmPart = line.substring(bmIndex + 3, idIndex).trim(); // Skipping "bm " (3 characters)
            List<String> bestMoves = Arrays.asList(bmPart.split("[ ,;]+")); // Split multiple moves by space or comma

            // Extract ID part (everything after "id")

            String idPart = line.substring(idIndex + 3).trim(); // Skipping "id " (3 characters)
            String id = idPart.replace("\"", "").trim(); // Remove surrounding quotes

            return new ChessPuzzle(fen, bestMoves, id);
        }
    }

	
    // Method to parse the entire text and return a list of ChessPuzzle objects
    private static List<ChessPuzzle> parseText(String text) {
        List<ChessPuzzle> puzzles = new ArrayList<>();
        String[] lines = text.split("\n");

        for (String line : lines) {
            if (!line.trim().isEmpty()) { // Ignore empty lines
            	//System.out.println("Parsing: " + line);
                ChessPuzzle puzzle = parseLine(line);
                puzzles.add(puzzle);
            }
        }

        return puzzles;
    }
    
    
    // Method to read the content of a file and return it as a single string
    private static String readFileAsString(String filePath) throws IOException {
        // Read all lines from the file and join them into a single string
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
    
    
	private static int getBestMove(IBitBoard bitboard, String best_move) {
		
		
		//boolean isCheck = best_move.contains("+");
		//boolean isCapture = best_move.contains("x");
		boolean isCastling = false;
				
    	best_move = best_move.replace("+", "");
    	best_move = best_move.replace("x", "");
    	
    	char pieceStr;
    	String fromStr = null;
    	String toStr;
    	
    	if ("O-O".equals(best_move)) {
			
    		isCastling = true;
    		
    		pieceStr = 'K';
    		
    		if (bitboard.getColourToMove() == Constants.COLOUR_WHITE) {
    			
    			fromStr = "e1";
    			toStr = "g1";
    			
    		} else {
    			
    			fromStr = "e8";
    			toStr = "g8";
    		}
    		
		} else if ("O-O-O".equals(best_move)) {
			
			isCastling = true;
			
			pieceStr = 'K';
			
    		if (bitboard.getColourToMove() == Constants.COLOUR_WHITE) {
    			
    			fromStr = "e1";
    			toStr = "c1";
    			
    		} else {
    			
    			fromStr = "e8";
    			toStr = "c8";
    		}
    		
		} else {
			
	        if (best_move.length() == 4) {
	        	//Rfd1
	        	
	        	if (Character.isUpperCase(best_move.charAt(0))) {
	        		
	        		pieceStr = best_move.charAt(0);
	        		
	        		fromStr = "" + best_move.charAt(1);
	        		
	        		toStr = best_move.substring(2);
	        		
	        	} else {
	        		
	        		throw new IllegalStateException(best_move);
	        	}
	        	
	        } else if (best_move.length() == 3) {
	        	
	        	if (Character.isUpperCase(best_move.charAt(0))) {
	        		//Qg6
	        		
	        		pieceStr = best_move.charAt(0);
	        		
	        		toStr = best_move.substring(1);
	        		
	        	} else {
	        		//de6
	        		
	        		pieceStr = 'P';
	        		
	        		fromStr = "" + best_move.charAt(0);
	        		
	        		toStr = best_move.substring(1);
	        	}
	        	
	        } else if (best_move.length() == 2) {

        		pieceStr = 'P';
        		
        		toStr = best_move.substring(0);
	        	
	        } else {
	        	
	            throw new IllegalStateException("Invalid move format: " + best_move);
	        }
		}
    	
    	
    	int piece;
    	switch(pieceStr) {
    		case 'P' :
    			piece = Constants.TYPE_PAWN;
    			break;
    		case 'N' :
    			piece = Constants.TYPE_KNIGHT;
    			break;
    		case 'B' :
    			piece = Constants.TYPE_BISHOP;
    			break;
    		case 'R' :
    			piece = Constants.TYPE_ROOK;
    			break;
    		case 'Q' :
    			piece = Constants.TYPE_QUEEN;
    			break;
    		case 'K' :
    			piece = Constants.TYPE_KING;
    			break;
    		default:
    			throw new IllegalStateException("" + pieceStr);
    	}
    	
    	
    	int toFieldID = fieldNamesToIds.get(toStr);
    	
    	int move = -1;
    	
    	if (isCastling) {
    	
        	int fromFieldID = fromStr == null ? -1 : fieldNamesToIds.get(fromStr);
        	
        	IMoveList moves = new BaseMoveList();
        	
        	bitboard.genAllMoves(moves);
        	
        	int cur_move;
        	while ((cur_move = moves.next()) != 0) {
        		
        		int test_from = bitboard.getMoveOps().getFromFieldID(cur_move);
        		int test_to = bitboard.getMoveOps().getToFieldID(cur_move);
        		
        		if (fromFieldID == test_from && toFieldID == test_to) {
        	
        			move = cur_move;
        			
        			break;
        		}
        	}
        	
    	} else {
    		
        	int fromFileID = fromStr == null ? -1 : fileNamesToIds.get(fromStr);
        	
        	IMoveList moves = new BaseMoveList();
        	
        	bitboard.genAllMoves(moves);
        	
        	int cur_move;
        	while ((cur_move = moves.next()) != 0) {
        		
        		int test_piece = bitboard.getMoveOps().getFigureType(cur_move);
        		int test_to = bitboard.getMoveOps().getToFieldID(cur_move);
        		
        		if (test_piece == piece && test_to == toFieldID) {
        			
        			if (fromFileID == -1) {
        				
            			move = cur_move;
            			
            			break;
            			
        			} else {
        				
        				if (fromFileID == bitboard.getMoveOps().getFromField_File(cur_move)) {
        					
                  			move = cur_move;
                			
                			break;
        				}
        			}
        		}
        	}
    	}
    	
    	
    	if (move == -1) {
    		
    		throw new IllegalStateException(best_move + " not found");
    	}
    	
        return move;
	}
	

	public static List<ChessPuzzle> getTestCases(String fileName) throws IOException {
		
		String inputText = readFileAsString(fileName);

        // Parsing the input text
        List<ChessPuzzle> puzzles = parseText(inputText);

        // Output each puzzle
        for (ChessPuzzle puzzle : puzzles) {
        	
        	//System.out.println(puzzle);
            
        	IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(puzzle.getFen());
        	
            for (String best_move: puzzle.getBestMovesStr()) {

            	int best_move_int = getBestMove(bitboard, best_move);
            	puzzle.addBestMove(best_move_int);
            }
        }
        
        return puzzles;
	}
	
	
    public static void main(String[] args) throws IOException {
    	
    	List<ChessPuzzle> puzzles = getTestCases("test-cases.epd");
    }
}

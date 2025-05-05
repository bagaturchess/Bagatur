package bagaturchess.datagen;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.nnue_v5.NNUEBridge;
import bagaturchess.nnue_v5.NNUEProbeUtils;
import bagaturchess.nnue_v5.NNUEProbeUtils.Input;


public class Main_Rescore {
	
	
    public static void main(String[] args) {
    	
    	long start_time = System.currentTimeMillis();
    	
        String inputFilePath = "C:/DATA/NNUE/plain/dataset.plain";
        String outputFilePath = "C:/DATA/NNUE/plain/rescored_dataset.plain";
        
        try {
        	
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath), 10000);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath), 10000);
            
            String line;
            int totalLines = 0;
            
            while ((line = reader.readLine()) != null) {
            	
                totalLines++;
                
                if (totalLines % 100000 == 0) {
                	
                	System.out.println("totalLines=" + totalLines + " in " + (System.currentTimeMillis() - start_time) / 1000 + " seconds");
                }
                
                String[] parts = line.split(" \\| ");
                
                if (parts.length != 3) {
                	
                	System.out.println("Error line: " + line);
                	
                    continue;
                }
                
                String fen = parts[0];
                int evaluation;
                float wdl;
                
                try {
                	
                    evaluation = Integer.parseInt(parts[1].trim());
                    
                    wdl = Float.parseFloat(parts[2].trim());
                    
                } catch (NumberFormatException e) {
                	
                	System.out.println("Error numbers: " + line);
                	
                    continue;
                }
                
                IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen);
                
                Input input = new Input();
        		
        		NNUEProbeUtils.fillInput(bitboard, input);
        		
        		int evaluation_rescore = NNUEBridge.fasterEvalArray(input.pieces, input.squares,
        				bitboard.getMaterialState().getPiecesCount(),
        				bitboard.getColourToMove(),
        				bitboard.getDraw50movesRule());
        		//int evaluation_rescore = NNUEBridge.evalFen(fen);
        		
        		//System.out.println("fen=" + fen + ", eval=" + evaluation_rescore);
        		
	    		if (bitboard.getColourToMove() == Constants.COLOUR_BLACK) {
	    			evaluation_rescore = -evaluation_rescore;
	    		}
	    		
	    		//System.out.println("evaluation=" + evaluation + ", evaluation_rescore=" + evaluation_rescore);
        		
	    		//50 moves rule is considered by the re-score logic, but will not be considered in the trained net.
	    		//So skip all draw scores and leave only evaluations close to 0 for the training as an indicators of equal position.
	    		if (evaluation_rescore != 0) {
	    			
					StringBuilder sb = new StringBuilder(100);
					
					sb.append(fen)
					  .append(" | ")
					  .append(evaluation_rescore)
					  .append(" | ")
					  .append(wdl);
					
					String line_rescore = sb.toString();
	                
	                writer.write(line_rescore);
	                writer.newLine();
	    		}
            }
            
            reader.close();
            writer.close();
            
            System.out.println("Total lines processed: " + totalLines);
            System.out.println("Rescored dataset saved to: " + outputFilePath);
            
        } catch (IOException e) {
        	
            e.printStackTrace();
        }
    }
}


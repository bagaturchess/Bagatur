package bagaturchess.ucitracker.impl3;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bagaturchess.bitboard.impl.utils.VarStatistic;


public class PlainConverter_Balanced {
	
	
	private static final long MAX_POSITIONS 					= 300000000;
	
	private static final Map<Integer, Long> scores_distribution = new HashMap<Integer, Long>();
	
	private static VarStatistic scores_count_stat 				= new VarStatistic();
	
	
	public static void main(String[] args) {
		
		String inputFilePath = "C:\\DATA\\NNUE\\test80-2024-04-apr-2tb7p\\all.plain"; // Path to the input file
        String outputFilePath = "dataset.txt"; // Path to the output file

        long lines_counter = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath), 16 * 1024 * 1024);
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath), 16 * 1024 * 1024)) {

            String line;
            String fen = null, score = null, result = null;
            int score_int = 0;
            
            while ((line = br.readLine()) != null) {
                if (line.startsWith("fen")) {
                    fen = line.split(" ", 2)[1];
                } else if (line.startsWith("score")) {
                    score = line.split(" ", 2)[1];
                    score_int = Integer.parseInt(score);
                    if (fen.contains(" b ")) {
                    	score_int = -score_int;
                    }
                    score = "" + score_int;
                } else if (line.startsWith("result")) {
                    result = line.split(" ", 2)[1];
                    float result_float = Float.parseFloat(result);
                    if (result_float == 0) {
                    	result = "0.5";
                    } else if (result_float == -1) {
                    	result= "0";
                    }
                } else if (line.equals("e")) {
                	
                	if (add(score_int)) {
                		
                        bw.write(fen + " | " + score + " | " + result);
                        bw.newLine();
                        lines_counter++;
                        
                        if (lines_counter == MAX_POSITIONS) {
                        	bw.close();
                        	break;
                        }
                        
                        if (lines_counter % 1000000 == 0) {
                        	
                        	System.out.println("Positions Count: " + (lines_counter / 1000000) + "M");
                        }
                        
                        if (lines_counter % 10000 == 0) {
                        	
                        	scores_count_stat = new VarStatistic();
                        	for (Integer key: scores_distribution.keySet()) {
                        		scores_count_stat.addValue(scores_distribution.get(key));
                        	}
                        }
                        
                        Long count = scores_distribution.get(score_int);
                        
                        if (count != null) {
                        	
                        	scores_distribution.put(score_int, count + 1);
                        	scores_count_stat.addValue(count + 1);
                        	
                        } else {
                        	
                        	scores_distribution.put(score_int, 1L);
                        	scores_count_stat.addValue(1);
                        }
                	}
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
	}


	private static boolean add(int score_int) {
		
		Long count = scores_distribution.get(score_int);
		
		if (count == null) {
			
			return true;
		}
		
		return count <= scores_count_stat.getEntropy() + scores_count_stat.getDisperse();
	}
}

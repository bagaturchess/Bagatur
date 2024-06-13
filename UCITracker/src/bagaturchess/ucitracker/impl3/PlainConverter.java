package bagaturchess.ucitracker.impl3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PlainConverter {

	private static final long MAX_POSITIONS = 100000000;
	
	
	public static void main(String[] args) {
		
		String inputFilePath = "C:\\DATA\\NNUE\\test80-2024-04-apr-2tb7p\\all.plain"; // Path to the input file
        String outputFilePath = "dataset.txt"; // Path to the output file

        long lines_counter = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath), 1024 * 1024);
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            String fen = null, score = null, result = null;
            
            while ((line = br.readLine()) != null) {
                if (line.startsWith("fen")) {
                    fen = line.split(" ", 2)[1];
                } else if (line.startsWith("score")) {
                    score = line.split(" ", 2)[1];
                    int score_int = Integer.parseInt(score);
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
                    //if (fen != null && score != null && result != null) {
                        bw.write(fen + " | " + score + " | " + result);
                        bw.newLine();
                        lines_counter++;
                        if (lines_counter == MAX_POSITIONS) {
                        	bw.close();
                        	break;
                        }
                    //}
                    // Reset variables for the next entry
                    fen = null;
                    score = null;
                    result = null;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}

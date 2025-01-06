package bagaturchess.datagen;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Main_Filter {
	
	
    private interface Filter {
    	
    	public boolean skip(String fen, int evaluation, float wdl);
    }
    
    
    //Check if this is a draw game (WDL = 0.5) and the evaluation is close to 0
	private static final Filter filter = new Filter() {
		
		// Define evaluation range for filtering
		int lowerBound = -50;
		int upperBound = 50;
        
		@Override
		public boolean skip(String fen, int evaluation, float wdl) {
			
			return wdl == 0.5 && evaluation >= lowerBound && evaluation <= upperBound;
		}
	};
	
	
    public static void main(String[] args) {
    	
        String inputFilePath = "C:/DATA/NNUE/plain/dataset.plain";
        String outputFilePath = "C:/DATA/NNUE/plain/filtered_dataset.plain";
        
        try {
        	
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath), 10000);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath), 10000);
            
            String line;
            int totalLines = 0;
            int filteredLines = 0;
            
            while ((line = reader.readLine()) != null) {
            	
                totalLines++;
                
                if (totalLines % 10000000 == 0) {
                	
                	System.out.println("totalLines=" + totalLines);
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
                
                if (filter.skip(fen, evaluation, wdl)) {
                	
                    filteredLines++;
                    
                    continue;
                }
                
                writer.write(line);
                writer.newLine();
            }
            
            reader.close();
            writer.close();
            
            System.out.println("Total lines processed: " + totalLines);
            System.out.println("Lines filtered: " + filteredLines);
            System.out.println("Filtered dataset saved to: " + outputFilePath);
            
        } catch (IOException e) {
        	
            e.printStackTrace();
        }
    }
}


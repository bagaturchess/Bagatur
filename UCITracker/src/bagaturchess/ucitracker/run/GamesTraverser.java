package bagaturchess.ucitracker.run;


import bagaturchess.ucitracker.api.PositionsTraverser;
import bagaturchess.ucitracker.impl.travers.PositionsVisitorImpl;


public class GamesTraverser {
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		long startTime = System.currentTimeMillis();
		
		try {
			
			//String filePath = "Arasan13.1.cg";
			//String filePath = "Houdini.15a.cg";
			//String filePath = "stockfish-9.cg";
			//String filePath = "glaurung-2.2.cg";
			//String filePath = "stockfish-10.cg";
			//String filePath = "lc0-v0.25.1.cg";
			//String filePath = "komodo-9.cg";
			//String filePath = "stockfish-14.1.cg";
			//String filePath = "pedone-3.1.cg";
			//String filePath = "fat_titz-1.1.cg";
			//String filePath = "wasp-5-0-0.cg";
			//String filePath = "bagatur-2.3.cg";
			//String filePath = "stockfish-14.1-4N.cg";
			//String filePath = "NNUE.cg";
			String filePath = "stockfish-16.1.cg";
			
			PositionsTraverser.traverseAll(filePath, new PositionsVisitorImpl());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
	
}

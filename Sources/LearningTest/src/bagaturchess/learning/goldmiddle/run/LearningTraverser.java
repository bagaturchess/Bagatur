package bagaturchess.learning.goldmiddle.run;


import cfg.BoardConfigImpl;
import bagaturchess.learning.goldmiddle.impl.visitors.LearningVisitorImpl;
import bagaturchess.ucitracker.api.PositionsTraverser;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class LearningTraverser {
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		long startTime = System.currentTimeMillis();
		try {
			
			//String filePath = "./Houdini.15a.short.cg";
			//String filePath = "./Houdini.15a.cg";
			//String filePath = "./Arasan13.1.cg";
			String filePath = "Stockfish-211-ja.cg";
			
			PositionsVisitor learning = new LearningVisitorImpl();
			
			while (true) {
				PositionsTraverser.traverseAll(filePath, learning, 99999999, new BoardConfigImpl());
				//PositionsTraverser.traverseAll(filePath, learning, 300000, new BoardConfigImpl());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
}

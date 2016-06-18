package bagaturchess.learning.goldmiddle.run;


import cfg.BoardConfigImpl;
import bagaturchess.learning.goldmiddle.impl.visitors.EvaluatorsComparatorVisitorImpl;
import bagaturchess.ucitracker.api.PositionsTraverser;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class EvaluatorsComparatorTraverser {
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		long startTime = System.currentTimeMillis();
		try {
			
			//String filePath = "./Houdini.15a.short.cg";
			//String filePath = "./Houdini.15a.cg";
			//String filePath = "./Arasan13.1.cg";
			String filePath = "Stockfish-211-ja.cg";
			
			PositionsVisitor learning = new EvaluatorsComparatorVisitorImpl();
			
			PositionsTraverser.traverseAll(filePath, learning, 99999999, new BoardConfigImpl(), "bagaturchess.learning.impl.eval.FastPawnsEvalFactory");
			//PositionsTraverser.traverseAll(filePath, learning, 300000, new BoardConfigImpl());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
}

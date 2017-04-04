package bagaturchess.learning.goldmiddle.run;


import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.impl.cfg.allfeatures.ALL_LearningInputImpl;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.Bagatur_LearningInputImpl;
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
			//String filePath = "stockfish-7.cg";
			String filePath = "glaurung-2.2.cg";
			
			PositionsVisitor learning = new LearningVisitorImpl();
			
			ILearningInput input = new ALL_LearningInputImpl();
			
			while (true) {
				PositionsTraverser.traverseAll(filePath, learning, 999999999, input.createBoardConfig(), input.getPawnsEvalFactoryClassName());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
}

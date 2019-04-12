package bagaturchess.learning.goldmiddle.run;


import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.goldmiddle.visitors.LearningVisitorImpl;
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
			//String filePath = "./stockfish-9.cg";
			//String filePath = "./glaurung-2.2.cg";
			//String filePath = "./texel-107.cg";
			String filePath = "./stockfish-10.cg";
			//String filePath = "./komodo-9.cg";
			
			PositionsVisitor learning = new LearningVisitorImpl();
			
			ILearningInput input = LearningInputFactory.createDefaultInput();
			
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

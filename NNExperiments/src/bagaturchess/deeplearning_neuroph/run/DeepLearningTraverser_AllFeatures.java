package bagaturchess.deeplearning_neuroph.run;


import bagaturchess.deeplearning_neuroph.impl.visitors.DeepLearningVisitorImpl_AllFeatures;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.ucitracker.api.PositionsTraverser;


public class DeepLearningTraverser_AllFeatures {
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		long startTime = System.currentTimeMillis();
		try {
			
			//String filePath = "./Houdini.15a.short.cg";
			//String filePath = "./Houdini.15a.cg";
			//String filePath = "./Arasan13.1.cg";
			String filePath = "./stockfish-14.1.cg";
			//String filePath = "./stockfish-14.1-4N.cg";
			//String filePath = "./glaurung-2.2.cg";
			//String filePath = "NNUE.cg";
			
			DeepLearningVisitorImpl_AllFeatures learning = new DeepLearningVisitorImpl_AllFeatures();
			
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

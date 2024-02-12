package bagaturchess.deeplearning_neuroph.run;


import bagaturchess.deeplearning_neuroph.impl.visitors.DeepLearningVisitorImpl_PST;
import bagaturchess.deeplearning_neuroph.impl.visitors.DeepLearningVisitorImpl_PST_And_AllFeatures;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.ucitracker.api.PositionsTraverser;


public class DeepLearningTraverser_PST_And_AllFeatures {
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		long startTime = System.currentTimeMillis();
		try {
			
			//String filePath = "./Houdini.15a.short.cg";
			//String filePath = "./Houdini.15a.cg";
			//String filePath = "./Arasan13.1.cg";
			//String filePath = "./stockfish-9.cg";
			//String filePath = "./glaurung-2.2.cg";
			String filePath = "./texel-107.cg";
			
			DeepLearningVisitorImpl_PST_And_AllFeatures learning = new DeepLearningVisitorImpl_PST_And_AllFeatures();
			
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

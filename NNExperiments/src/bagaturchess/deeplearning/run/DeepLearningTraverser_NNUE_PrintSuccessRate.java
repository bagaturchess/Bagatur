package bagaturchess.deeplearning.run;


import bagaturchess.deeplearning.impl_nnue.visitors.DeepLearningVisitorImpl_NNUE_PrintSuccessRate;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.ucitracker.api.PositionsTraverser;


public class DeepLearningTraverser_NNUE_PrintSuccessRate {
	
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		
		long startTime = System.currentTimeMillis();
		
		try {
			
			//String filePath = "./Houdini.15a.short.cg";
			//String filePath = "./Houdini.15a.cg";
			//String filePath = "./Arasan13.1.cg";
			//String filePath = "./stockfish-14.1.cg";
			//String filePath = "./glaurung-2.2.cg";
			String filePath = "./NNUE_big.cg";
			
			DeepLearningVisitorImpl_NNUE_PrintSuccessRate printer = new DeepLearningVisitorImpl_NNUE_PrintSuccessRate();
			
			ILearningInput input = LearningInputFactory.createDefaultInput();
			
			while (true) {
				
				PositionsTraverser.traverseAll(filePath, printer, 999999999, input.createBoardConfig(), input.getPawnsEvalFactoryClassName());
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
}

package bagaturchess.learning.goldmiddle.run;


import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.goldmiddle.impl4.filler.Bagatur_V20_FeaturesConfigurationImpl;
import bagaturchess.learning.goldmiddle.impl7.filler.Bagatur_V41_FeaturesConfigurationImpl;
import bagaturchess.learning.goldmiddle.visitors.LearningVisitorImpl;
import bagaturchess.learning.impl.features.baseimpl.Features;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.ucitracker.api.PositionsTraverser;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class LearningTraverser {
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		
		long startTime = System.currentTimeMillis();
		
		try {
			
			//String filePath = "Houdini.15a.cg";
			//String filePath = "Arasan13.1.cg";
			//String filePath = "stockfish-9.cg";
			//String filePath = "texel-107.cg";
			//String filePath = "stockfish-10.cg";
			
			//String filePath = "fat_titz-1.1.cg";
			//String filePath = "glaurung-2.2.cg"; //With MutiPV: Reaches around 66% accuracy
			//String filePath = "stockfish-14.1.cg"; //With MutiPV: Reaches around 60% accuracy
			//String filePath = "pedone-3.1.cg"; //With MutiPV: Starts from -177% and reaches around 30% accuracy
			//String filePath = "wasp-5-0-0.cg"; //With MutiPV: Reaches around 58% accuracy
			//String filePath = "bagatur-2.3.cg"; //Without MutiPV: Reaches around 26% accuracy
			
			//String filePath = "stockfish-14.1-4N.cg";
			//String filePath = "stockfish-16.cg";
			String filePath = "NNUE.cg";
			
			String filename_NN = Features_Splitter.FEATURES_FILE_NAME;
			String features_class_name = Bagatur_V41_FeaturesConfigurationImpl.class.getName();
			
			
			if (true) {
				
				Features_Splitter features = Features_Splitter.create(features_class_name);
				Features_Splitter.store(filename_NN, features);
				//Features_Splitter.dump(features);
			
			} else {
				
				Features_Splitter features = Features_Splitter.load(filename_NN, features_class_name);
				Features.toJavaCode(features.getFeatures(1), "_O");
				Features.toJavaCode(features.getFeatures(0), "_E");
				System.exit(0);
			}
					
			
			IEvalConfig cfg = new bagaturchess.learning.goldmiddle.impl7.cfg.EvaluationConfig_V41_GOLDENMIDDLE_Train();
			
			PositionsVisitor learning = new LearningVisitorImpl(cfg);
			
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

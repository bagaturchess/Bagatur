package bagaturchess.learning.goldmiddle.run;


import bagaturchess.learning.goldmiddle.impl.visitors.LearningVisitorImpl;
import bagaturchess.learning.goldmiddle.run.cfg.BoardConfigImpl;
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
			String filePath = "stockfish-7.cg";//690MB -> 11000 games played, 31645781 positions
			
			/*
			Iteration 1. Success percent before this iteration: 70.21475449557569%
			Iteration 2. Success percent before this iteration: 57.45052101823551%
			Iteration 3. Success percent before this iteration: 63.15551859454192%
			Iteration 4. Success percent before this iteration: 75.18386010496731%
			Iteration 5. Success percent before this iteration: 73.09397373741464%
			Iteration 6. Success percent before this iteration: 79.48716696436611%
			Iteration 7. Success percent before this iteration: 78.66814172061612%
			Iteration 8. Success percent before this iteration: 81.08935395041658%
			Iteration 9. Success percent before this iteration: 80.85468246977948%
			Iteration 10. Success percent before this iteration: 81.67357359951933%
			Iteration 11. Success percent before this iteration: 81.69464985497858%
			Iteration 12. Success percent before this iteration: 81.85938685469553%
			Iteration 13. Success percent before this iteration: 81.89669717227757%
			Iteration 14. Success percent before this iteration: 81.96969121937339%
			Iteration 15. Success percent before this iteration: 81.99689027232495%
			Iteration 16. Success percent before this iteration: 82.04958765624669%
			Iteration 17. Success percent before this iteration: 82.07646532983946%
			Iteration 18. Success percent before this iteration: 82.1167889618818%
			Iteration 19. Success percent before this iteration: 82.1423045447354%
			Iteration 20. Success percent before this iteration: 82.17343564522685%
			Iteration 21. Success percent before this iteration: 82.19599532781902%
			Iteration 22. Success percent before this iteration: 82.22164007852571%
			Iteration 23. Success percent before this iteration: 82.24139840680486%
			Iteration 24. Success percent before this iteration: 82.26342362333729%
			Iteration 25. Success percent before this iteration: 82.28121370248292%
			Iteration 26. Success percent before this iteration: 82.30052605480509%
			Iteration 27. Success percent before this iteration: 82.31631767240924%
			Iteration 28. Success percent before this iteration: 82.33296064784052%
			Iteration 29. Success percent before this iteration: 82.347320873144%
			Iteration 30. Success percent before this iteration: 82.36195464337868%
			Iteration 31. Success percent before this iteration: 82.37500856357475%
			Iteration 32. Success percent before this iteration: 82.38808669701623%
			Iteration 33. Success percent before this iteration: 82.39997133573883%
			Iteration 34. Success percent before this iteration: 82.4115233875884%
			Iteration 35. Success percent before this iteration: 82.42207021115657%
			Iteration 36. Success percent before this iteration: 82.43236420280712%
			Iteration 37. Success percent before this iteration: 82.4418480839193%
			Iteration 38. Success percent before this iteration: 82.45106629035473%
			Iteration 39. Success percent before this iteration: 82.4595619437446%
			Iteration 40. Success percent before this iteration: 82.46767862794174%
			Iteration 41. Success percent before this iteration: 82.47514500131848%
			Iteration 42. Success percent before this iteration: 82.48228316512538%
			Iteration 43. Success percent before this iteration: 82.48889196089728%
			*/
			
			
			PositionsVisitor learning = new LearningVisitorImpl();
			
			while (true) {
				PositionsTraverser.traverseAll(filePath, learning, 999999999, new BoardConfigImpl());
				//PositionsTraverser.traverseAll(filePath, learning, 300000, new BoardConfigImpl());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
}

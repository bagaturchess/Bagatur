package bagaturchess.ucitracker.run;


import java.io.IOException;

import bagaturchess.ucitracker.api.PositionsTraverser;
import bagaturchess.ucitracker.impl.travers.PositionsVisitorImpl;


public class GamesTraverser {
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		long startTime = System.currentTimeMillis();
		try {
			
			
			//String filePath = "./TEST/Arasan13.1.cg";
			//String filePath = "./DATA/Houdini.15a.cg";
			//String filePath = "./stockfish-7.cg";
			String filePath = "./glaurung-2.2.cg";
			
			PositionsTraverser.traverseAll(filePath, new PositionsVisitorImpl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
	
}

package bagaturchess.scanner.run;


import bagaturchess.scanner.visitors.ScannerCheckVisitor;
import bagaturchess.ucitracker.api.PositionsTraverser;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class ScannerCheckMain {
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		try {
			
			String filePath = "./stockfish-12.cg";
			
			PositionsVisitor visitor = new ScannerCheckVisitor();
			
			System.out.println("Reading games ... ");
			while (true) {
				PositionsTraverser.traverseAll(filePath, visitor, 999999999, null, null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("OK " + ((endTime - startTime) / 1000) + "sec");		
	}
}

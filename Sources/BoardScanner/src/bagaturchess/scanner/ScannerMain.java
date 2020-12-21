package bagaturchess.scanner;


import bagaturchess.ucitracker.api.PositionsTraverser;


public class ScannerMain {
	
	public static void main(String[] args) {
		
		System.out.println("Reading games ... ");
		long startTime = System.currentTimeMillis();
		try {
			
			String filePath = "./stockfish-12.cg";
			
			ScannerVisitor visitor = new ScannerVisitor();
			
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

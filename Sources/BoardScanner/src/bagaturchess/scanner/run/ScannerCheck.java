package bagaturchess.scanner.run;


import java.awt.image.BufferedImage;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.scanner.impl.BoardScanner;
import bagaturchess.scanner.impl.BoardScanner_RGB;
import bagaturchess.scanner.impl.ImageProperties;
import bagaturchess.scanner.impl.ScannerUtils;
import bagaturchess.scanner.model.NetworkModel;
import bagaturchess.scanner.model.NetworkModel_RGB;
import bagaturchess.ucitracker.api.PositionsTraverser;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class ScannerCheck {
	
	
	private static final String NET_FILE = "scanner.bin";
	
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		try {
			
			String filePath = "./stockfish-12.cg";
			
			ImageProperties imageProperties = new ImageProperties(192, "set1");
			
			NetworkModel netmodel = new NetworkModel_RGB(NET_FILE, imageProperties);
			
			PositionsVisitor visitor = new ScannerCheckVisitor(new BoardScanner_RGB(netmodel), imageProperties);
			
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
	
	
	private static class ScannerCheckVisitor implements PositionsVisitor {
		
		
		private int iteration = 0;
		
		private int counter;
		
		private long startTime;
		
		private double sumDiffs1;
		private double sumDiffs2;
		
		private ImageProperties imageProperties;
		
		private BoardScanner scanner;
		
		
		public ScannerCheckVisitor(BoardScanner _scanner, ImageProperties _imageProperties) throws Exception {
			imageProperties = _imageProperties;
			scanner = _scanner;
		}
		
		
		@Override
		public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
	        
			BufferedImage image = ScannerUtils.createBoardImage(imageProperties, bitboard.toEPD());
			image = ScannerUtils.convertToGrayScale(image);
			
			//ScannerUtils.saveImage(bitboard.toEPD(), image);
			int[][] expected_input = ScannerUtils.convertToGrayMatrix(image);
			String recognized_fen = scanner.scan(expected_input);
			
			String expected_fen_prefix = bitboard.toEPD().split(" ")[0];
			
			sumDiffs1++;
			if (!recognized_fen.equals(expected_fen_prefix)) {
				sumDiffs2++;
			}
			
			counter++;
			//if ((counter % 100) == 0) {
				
				System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
			//}
		}
		
		
		@Override
		public void begin(IBitBoard bitboard) throws Exception {
			
			startTime = System.currentTimeMillis();
			
			counter = 0;
			iteration++;
			
			sumDiffs1 = 0;
			sumDiffs2 = 0;
		}
		
		
		@Override
		public void end() {
			System.out.println("END Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
			//network.save(NET_FILE);
		}
	}
}

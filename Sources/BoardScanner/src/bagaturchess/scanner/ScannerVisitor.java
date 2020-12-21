/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.scanner;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class ScannerVisitor implements PositionsVisitor {
	
	
	private static final int IMAGE_SIZE = 512;
	private static final int SQUARE_SIZE = 64;
	
	private static final Color BLACK_SQUARE = new Color(120, 120, 120);
	private static final Color WHITE_SQUARE = new Color(220, 220, 220);
	
	private int iteration = 0;
	
	private int counter;
	
	private long startTime;
	
	private Image[] piecesImages = new Image[13];
	
	
	public ScannerVisitor() throws Exception {
		loadPiecesImages();
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
        
		BufferedImage image = drawImage(bitboard.toEPD());
		
		saveImage(bitboard.toEPD(), image);
		
		counter++;
		if ((counter % 100000) == 0) {
			
			System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms");

		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		iteration++;
	}
	
	
	public void end() {
		
		//System.out.println("***************************************************************************************************");
		//System.out.println("End iteration " + iteration + ", Total evaluated positions count is " + counter);
		System.out.println("END Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms");
	}
	
	
	private BufferedImage drawImage(String fen) {
		
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = image.createGraphics();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				
				if ((i + j) % 2 == 0) {
					g.setColor(WHITE_SQUARE);
				} else {
					g.setColor(BLACK_SQUARE);
				}
				
				g.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
		}
		
		String[] fenArray = fen.split(" ");
		int positionCount = 63;
		for (int i = 0; i < fenArray[0].length(); i++) {

			int x = (7 - positionCount % 8) * SQUARE_SIZE;
			int y = (7 - positionCount / 8) * SQUARE_SIZE;
			boolean whiteSquare = (7 - positionCount % 8 + 7 - positionCount / 8) % 2 == 0;
					
			final char character = fenArray[0].charAt(i);
			switch (character) {
			case '/':
				continue;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
				positionCount -= Character.digit(character, 10);
				break;
			case 'P':
				g.drawImage(piecesImages[Constants.PID_W_PAWN], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'N':
				g.drawImage(piecesImages[Constants.PID_W_KNIGHT], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'B':
				g.drawImage(piecesImages[Constants.PID_W_BISHOP], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'R':
				g.drawImage(piecesImages[Constants.PID_W_ROOK], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'Q':
				g.drawImage(piecesImages[Constants.PID_W_QUEEN], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'K':
				g.drawImage(piecesImages[Constants.PID_W_KING], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'p':
				g.drawImage(piecesImages[Constants.PID_B_PAWN], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'n':
				g.drawImage(piecesImages[Constants.PID_B_KNIGHT], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'b':
				g.drawImage(piecesImages[Constants.PID_B_BISHOP], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'r':
				g.drawImage(piecesImages[Constants.PID_B_ROOK], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'q':
				g.drawImage(piecesImages[Constants.PID_B_QUEEN], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			case 'k':
				g.drawImage(piecesImages[Constants.PID_B_KING], x, y, SQUARE_SIZE, SQUARE_SIZE, whiteSquare ? WHITE_SQUARE : BLACK_SQUARE, null);
				positionCount--;
				break;
			}
		}
		
		return image;
	}
	
	
	private void loadPiecesImages() throws IOException{
		
		piecesImages[Constants.PID_W_KING] = ImageIO.read(new File("./res/set1_w_k.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_QUEEN] = ImageIO.read(new File("./res/set1_w_q.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_ROOK] = ImageIO.read(new File("./res/set1_w_r.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_BISHOP] = ImageIO.read(new File("./res/set1_w_b.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_KNIGHT] = ImageIO.read(new File("./res/set1_w_n.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_W_PAWN] = ImageIO.read(new File("./res/set1_w_p.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		
		piecesImages[Constants.PID_B_KING] = ImageIO.read(new File("./res/set1_b_k.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_QUEEN] = ImageIO.read(new File("./res/set1_b_q.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_ROOK] = ImageIO.read(new File("./res/set1_b_r.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_BISHOP] = ImageIO.read(new File("./res/set1_b_b.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_KNIGHT] = ImageIO.read(new File("./res/set1_b_n.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
		piecesImages[Constants.PID_B_PAWN] = ImageIO.read(new File("./res/set1_b_p.png")).getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_REPLICATE);
	}
	
	
	private void saveImage(String fen, BufferedImage image) {
		try {
			File file = new File("./data/" + (fen + ".jpg").replace('/', '_'));
			ImageIO.write(image, "jpg", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

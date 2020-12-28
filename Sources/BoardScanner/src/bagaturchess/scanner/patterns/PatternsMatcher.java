/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.scanner.patterns;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.cnn.impl.ImageProperties;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;


public class PatternsMatcher {
	
	
	public static void main(String[] args) {
		
		try {
			
			ImageProperties imageProperties = new ImageProperties(512);
			
			BufferedImage image_board = ImageIO.read(new File("./data/tests/lichess.org/test1.png"));
			image_board = ScannerUtils.resizeImage(image_board, imageProperties.getImageSize());
			//image_board = ScannerUtils.convertToGrayScale(image_board);
			int[][][] board = ScannerUtils.convertToRGBMatrix(image_board);
			
			BufferedImage image_king = createPattern(Constants.PID_W_KING, imageProperties, ScannerUtils.getAVG(image_board));
			int[][][] piece = ScannerUtils.convertToRGBMatrix(image_king);
			
			MatcherData matcherData = matchImages(board, piece);
            
            BufferedImage patternImage = ScannerUtils.createRGBImage(piece);
            ScannerUtils.saveImage("pattern", patternImage, "png");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static BufferedImage createPattern(int pid, ImageProperties imageProperties, Color bgcolor) {
		BufferedImage imagePiece = new BufferedImage(imageProperties.getSquareSize(), imageProperties.getSquareSize(), BufferedImage.TYPE_INT_RGB);
		Graphics g = imagePiece.getGraphics();
		g.setColor(bgcolor);
		g.fillRect(0, 0, imagePiece.getWidth(), imagePiece.getHeight());
		g.drawImage(imageProperties.getPiecesImages()[pid], 0, 0, null);
		return imagePiece;
	}
	
	
	private static final MatcherData matchImages(int[][] graySource, int[][] grayPattern) {
		
		MatcherData result = new MatcherData();
		result.delta = Double.MAX_VALUE;
		
		for (int x = 0; x <= graySource.length - grayPattern.length; x++ ) {
		    for (int y = 0; y <= graySource.length - grayPattern.length; y++ ) {
		        
		    	MatcherData cur = new MatcherData();
		    	cur.x = x;
		    	cur.y = y;
		    	
		        for (int i = 0; i < grayPattern.length; i++ ) {
		            for (int j = 0; j < grayPattern.length; j++ ) {
		            	
		                int pixelSource = graySource[x+i][y+j];
		                int pixelPattern = grayPattern[i][j];
		                
		                cur.delta += Math.abs(pixelSource - pixelPattern);
		                
		                /*if (cur.delta > result.delta) {
		                	i = grayPattern.length;
		                	break;
		                }*/
		            }
		        }
		        
		        if (result.delta > cur.delta) { 
		        	result.delta = cur.delta;
		        	result.x = x;
		        	result.y = y;
		        	
		            int[][] print = new int[grayPattern.length][grayPattern.length];
		            for (int i = 0; i < grayPattern.length; i++) {
		            	for (int j = 0; j < grayPattern.length; j++) {
		            		print[i][j] = graySource[result.x + i][result.y + j];
		            	}
		            }
		            
		            BufferedImage resultImage = ScannerUtils.createGrayImage(print);
		            ScannerUtils.saveImage("result" + result.delta + "_" + x + "_" + y, resultImage, "png");
		        }
		    }
		}
		
		return result;
	}
	
	
	private static final MatcherData matchImages(int[][][] rgbSource, int[][][] rgbPattern) {
		
		MatcherData result = new MatcherData();
		result.delta = Double.MAX_VALUE;
		
		for (int x = 0; x <= rgbSource.length - rgbPattern.length; x++ ) {
		    for (int y = 0; y <= rgbSource.length - rgbPattern.length; y++ ) {
		        
		    	MatcherData cur = new MatcherData();
		    	cur.x = x;
		    	cur.y = y;
		    	
		        for (int i = 0; i < rgbPattern.length; i++ ) {
		            for (int j = 0; j < rgbPattern.length; j++ ) {
		            	
		                int pixelSource_r = rgbSource[x+i][y+j][0];
		                int pixelSource_g = rgbSource[x+i][y+j][1];
		                int pixelSource_b = rgbSource[x+i][y+j][2];
		                int pixelPattern_r = rgbPattern[i][j][0];
		                int pixelPattern_g = rgbPattern[i][j][1];
		                int pixelPattern_b = rgbPattern[i][j][2];
		                
		                cur.delta += Math.abs(pixelSource_r - pixelPattern_r);
		                cur.delta += Math.abs(pixelSource_g - pixelPattern_g);
		                cur.delta += Math.abs(pixelSource_b - pixelPattern_b);
		                
		                /*if (cur.delta > result.delta) {
		                	i = grayPattern.length;
		                	break;
		                }*/
		            }
		        }
		        
		        if (result.delta > cur.delta) { 
		        	result.delta = cur.delta;
		        	result.x = x;
		        	result.y = y;
		        	
		            int[][][] print = new int[rgbPattern.length][rgbPattern.length][3];
		            for (int i = 0; i < rgbPattern.length; i++) {
		            	for (int j = 0; j < rgbPattern.length; j++) {
		            		print[i][j][0] = rgbSource[result.x + i][result.y + j][0];
		            		print[i][j][1] = rgbSource[result.x + i][result.y + j][1];
		            		print[i][j][2] = rgbSource[result.x + i][result.y + j][2];
		            	}
		            }
		            
		            BufferedImage resultImage = ScannerUtils.createRGBImage(print);
		            ScannerUtils.saveImage("result" + result.delta + "_" + x + "_" + y, resultImage, "png");
		        }
		    }
		}
		
		return result;
	}
	
	
	private static final class MatcherData {
		int x;
		int y;
		double delta;
	}
}

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
package bagaturchess.scanner.patterns.api;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.FilterInfo;
import bagaturchess.scanner.common.MatrixUtils.PatternMatchingData;


class ImageHandlerImpl_AWT implements ImageHandler {
	
	
	private static final String[] piecesSets = new String[] {"set1", "set2", "set3"};
	
	private static final Map<String, BufferedImage> piecesImagesFromAllSets = new HashMap<String, BufferedImage>();
	private static final Map<String, BufferedImage> piecesImagesFromAllSetsAndSizes = new HashMap<String, BufferedImage>();
	
	
	public static final Color[] GRAY_COLORS = new Color[256];
	
	
    static {
    	
    	try {
	    	for (int set = 0; set < piecesSets.length; set++) {
		    	for(int pid = 1; pid <= 12; pid++) {
		    		BufferedImage image = loadPieceImageFromFS(pid, piecesSets[set]);
		    		piecesImagesFromAllSets.put(piecesSets[set] + "_" + pid, image);
		    	}
	    	}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    	}
    	
		for (int r = 0; r < 256; r++) {
			for (int g = 0; g < 256; g++) {
				for (int b = 0; b < 256; b++) {
					int gray = (int) (r * 0.2989d + g * 0.5870 + b * 0.1140);
					//if (GRAY_COLORS[gray] == null) {
						GRAY_COLORS[gray] = new Color(r, g, b);
					//}
				}
			}
		}
    }
    
    
	ImageHandlerImpl_AWT() {
		
	}
	
	
	@Override
	public Object loadImageFromFS(Object path) throws IOException {
		return ImageIO.read(new File((String) path));
	}
	
	
	@Override
	public Object resizeImage(Object source, int newsize) {
		return ScannerUtils.resizeImage((BufferedImage) source, newsize);
	}
	
	
	@Override
	public void saveImage(String fileName, String formatName, Object image) throws IOException {
		ScannerUtils.saveImage(fileName, (BufferedImage) image, formatName);
	}
	
	
	@Override
	public int[][] convertToGrayMatrix(Object image) {
		return ScannerUtils.convertToGrayMatrix((BufferedImage) image);
	}


	@Override
	public BufferedImage createGrayImage(int[][] matrix) {
		return ScannerUtils.createGrayImage(matrix);
	}


	@Override
	public BufferedImage loadPieceImageFromMemory(int pid, String piecesSetName, int size) {
		
		String key = piecesSetName + "_" + pid + "_" + size;
		BufferedImage result = piecesImagesFromAllSetsAndSizes.get(key);
		if (result != null) {
			return result;
		}
		
		Image scaledImage = piecesImagesFromAllSets.get(piecesSetName + "_" + pid).getScaledInstance(size, size, Image.SCALE_SMOOTH);
		
		result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		result.getGraphics().drawImage(scaledImage, 0, 0, size, size, null);
		piecesImagesFromAllSetsAndSizes.put(key, result);
		
		return result;
	}
	
	
	private static BufferedImage loadPieceImageFromFS(int pid, String piecesSetName) throws IOException {
		
		String suffix = getSuffix(pid);
		
		String fileName = "./res/" + piecesSetName + suffix;
		
		return ImageIO.read(new File(fileName));
	}


	private static String getSuffix(int pid) {
		
		String suffix = "";
		
		switch (pid) {
		
			case Constants.PID_W_PAWN:
				suffix = "_w_p.png";
				break;
			case Constants.PID_W_KNIGHT:
				suffix = "_w_n.png";
				break;
			case Constants.PID_W_BISHOP:
				suffix = "_w_b.png";
				break;
			case Constants.PID_W_ROOK:
				suffix = "_w_r.png";
				break;
			case Constants.PID_W_QUEEN:
				suffix = "_w_q.png";
				break;
			case Constants.PID_W_KING:
				suffix = "_w_k.png";
				break;
				
			case Constants.PID_B_PAWN:
				suffix = "_b_p.png";
				break;
			case Constants.PID_B_KNIGHT:
				suffix = "_b_n.png";
				break;
			case Constants.PID_B_BISHOP:
				suffix = "_b_b.png";
				break;
			case Constants.PID_B_ROOK:
				suffix = "_b_r.png";
				break;
			case Constants.PID_B_QUEEN:
				suffix = "_b_q.png";
				break;
			case Constants.PID_B_KING:
				suffix = "_b_k.png";
				break;
			default:
				throw new IllegalStateException("pid=" + pid);
		}
		
		return suffix;
	}


	@Override
	public void printInfo(int[][] source, PatternMatchingData matcherData, String fileName) {
		
		int[][] print = new int[matcherData.size][matcherData.size];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				print[i][j] = source[matcherData.x + i][matcherData.y + j];
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createGrayImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}


	@Override
	public void printInfo(PatternMatchingData matcherData, String fileName) {
		
		int[][] print = new int[matcherData.size][matcherData.size];
		for (int i = 0; i < matcherData.size; i++) {
			for (int j = 0; j < matcherData.size; j++) {
				print[i][j] = matcherData.pattern[i][j];
			}
		}
		
		BufferedImage resultImage = ScannerUtils.createGrayImage(print);
		ScannerUtils.saveImage(fileName, resultImage, "png");
	}
	
	
	@Override
	public int[][] createSquareImage(int bgcolor, int size) {
		/*BufferedImage imageSquare = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g = imageSquare.getGraphics();
		g.setColor(GRAY_COLORS[bgcolor]);
		g.fillRect(0, 0, imageSquare.getWidth(), imageSquare.getHeight());
		return ScannerUtils.convertToGrayMatrix(imageSquare);*/
		int[][] result = new int[size][size];
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				result[i][j] = bgcolor;
			}
		}
		return result;
	}
	
	
	@Override
	public int[][] createPieceImage(String pieceSetName, int pid, int bgcolor, int size) {
		Image piece = (Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(pid, pieceSetName, size);
		BufferedImage imagePiece = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g = imagePiece.getGraphics();
		g.setColor(GRAY_COLORS[bgcolor]);
		g.fillRect(0, 0, imagePiece.getWidth(), imagePiece.getHeight());
		Image pieceScaled = piece;
		g.drawImage(pieceScaled, 0, 0, null);
		return ScannerUtils.convertToGrayMatrix(imagePiece);
	}
	
	
	@Override
	public Object createBoardImage(BoardProperties boardProperties, String fen, Object whiteSquareColor, Object blackSquareColor) {
		
		BufferedImage image = new BufferedImage(boardProperties.getImageSize(), boardProperties.getImageSize(), BufferedImage.TYPE_INT_RGB);
		
		Graphics g = image.createGraphics();
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				
				if ((i + j) % 2 == 0) {
					g.setColor((Color) whiteSquareColor);
				} else {
					g.setColor((Color) blackSquareColor);
				}
				
				g.fillRect(i * boardProperties.getSquareSize(), j * boardProperties.getSquareSize(), boardProperties.getSquareSize(), boardProperties.getSquareSize());
			}
		}
		
		String[] fenArray = fen.split(" ");
		int positionCount = 63;
		for (int i = 0; i < fenArray[0].length(); i++) {
			
			int x = (7 - positionCount % 8) * boardProperties.getSquareSize();
			int y = (7 - positionCount / 8) * boardProperties.getSquareSize();
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
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_W_PAWN, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'N':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_W_KNIGHT, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'B':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_W_BISHOP, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'R':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_W_ROOK, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'Q':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_W_QUEEN, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'K':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_W_KING, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'p':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_B_PAWN, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'n':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_B_KNIGHT, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'b':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_B_BISHOP, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'r':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_B_ROOK, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'q':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_B_QUEEN, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			case 'k':
				g.drawImage((Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(Constants.PID_B_KING, boardProperties.getPiecesSetFileNamePrefix(), boardProperties.getSquareSize()), x, y, boardProperties.getSquareSize(), boardProperties.getSquareSize(), whiteSquare ? (Color) whiteSquareColor : (Color) blackSquareColor, null);
				positionCount--;
				break;
			}
		}
		
		return image;
	}
	
	
	@Override
	public Color getColor(int grayColor) {
		return GRAY_COLORS[grayColor];
	}
	
	
	@Override
	public Object enlarge(Object image, double scale, Object bgcolor) {
		int initialSize = ((BufferedImage)image).getWidth();
		BufferedImage result = new BufferedImage((int) (initialSize * scale), (int) (initialSize * scale), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) result.getGraphics();
		g.setColor((Color) bgcolor);
		g.fillRect(0, 0, result.getWidth(), result.getHeight());
		g.drawImage(((BufferedImage)image).getScaledInstance(initialSize, initialSize, Image.SCALE_SMOOTH),
				(int) (initialSize * (scale - 1) / 2f),
				(int) (initialSize * (scale - 1) / 2f),
				initialSize, initialSize, null);
		
		return result;
	}
	
	
	@Override
	public Object getAVG(Object image) {
		
		if (((BufferedImage)image).getHeight() != ((BufferedImage)image).getWidth()) {
			throw new IllegalStateException();
		}
		
		long red = 0;
		long green = 0;
		long blue = 0;
		long count = 0;
        for (int i = 0; i < ((BufferedImage)image).getHeight(); i++) { 
            for (int j = 0; j < ((BufferedImage)image).getWidth(); j++) {
            	int rgb = ((BufferedImage)image).getRGB(i, j);
				red += (rgb & 0xff0000) >> 16;
				green += (rgb & 0xff00) >> 8;
				blue += rgb & 0xff;
				count++;
            }
        }
        
        return new Color((int) (red / count), (int) (green / count), (int) (blue / count));
	}
	
	
	@Override
	public Object extractResult(Object image, PatternMatchingData matcherData, float factorOfExtension) {
		
		while (factorOfExtension > 1 && matcherData.x - (factorOfExtension - 1) * matcherData.size / 2f < 0) {
			factorOfExtension -= 0.0001;
		}
		while (factorOfExtension > 1 && matcherData.x + matcherData.size + (factorOfExtension - 1) * matcherData.size / 2f >= ((BufferedImage)image).getWidth()) {
			factorOfExtension -= 0.0001;
		}
		while (factorOfExtension > 1 && matcherData.y - (factorOfExtension - 1) * matcherData.size / 2f < 0) {
			factorOfExtension -= 0.0001;
		}
		while (factorOfExtension > 1 && matcherData.y + matcherData.size + (factorOfExtension - 1) * matcherData.size / 2f >= ((BufferedImage)image).getHeight()) {
			factorOfExtension -= 0.0001;
		}
		
		if (factorOfExtension < 1) {
			factorOfExtension = 1;
		}
		
		int[][][] print = new int[(int) (matcherData.size * factorOfExtension) + 1][(int) (matcherData.size * factorOfExtension) + 1][3];
		for (int i = 0; i < matcherData.size * factorOfExtension; i++) {
			for (int j = 0; j < matcherData.size * factorOfExtension; j++) {
				
				int indexX = (int) (matcherData.x - (factorOfExtension - 1) * matcherData.size / 2 + i);
				int indexY = (int) (matcherData.y - (factorOfExtension - 1) * matcherData.size / 2 + j);
				
				indexX = Math.max(0, indexX);
				indexX = Math.min(((BufferedImage)image).getWidth() - 1, indexX);
				
				indexY = Math.max(0, indexY);
				indexY = Math.min(((BufferedImage)image).getHeight() - 1, indexY);
				
				int rgb = ((BufferedImage)image).getRGB(indexX, indexY);
				int red = (rgb & 0xff0000) >> 16;
				int green = (rgb & 0xff00) >> 8;
				int blue = rgb & 0xff;
				//int gray = (int) (red * 0.2989d + green * 0.5870 + blue * 0.1140);
				print[i][j][0] = red;
				print[i][j][1] = green;
				print[i][j][2] = blue;
			}
		}
		
		return ScannerUtils.createRGBImage(print);
	}


	@Override
	public Object extractResult(Object image, FilterInfo filterInfo, float factorOfExtension) {
		
		while (factorOfExtension > 1 && filterInfo.minX - (factorOfExtension - 1) * (filterInfo.maxX - filterInfo.minX) / 2f < 0) {
			factorOfExtension -= 0.0001;
		}
		while (factorOfExtension > 1 && filterInfo.minX + (filterInfo.maxX - filterInfo.minX) + (factorOfExtension - 1) * (filterInfo.maxX - filterInfo.minX) / 2f >= ((BufferedImage)image).getWidth()) {
			factorOfExtension -= 0.0001;
		}
		while (factorOfExtension > 1 && filterInfo.minY - (factorOfExtension - 1) * (filterInfo.maxY - filterInfo.minY) / 2f < 0) {
			factorOfExtension -= 0.0001;
		}
		while (factorOfExtension > 1 && filterInfo.minY + (filterInfo.maxY - filterInfo.minY) + (factorOfExtension - 1) * (filterInfo.maxY - filterInfo.minY) / 2f >= ((BufferedImage)image).getHeight()) {
			factorOfExtension -= 0.0001;
		}
		
		if (factorOfExtension < 1) {
			factorOfExtension = 1;
		}
		
		int[][][] print = new int[(int) ((filterInfo.maxX - filterInfo.minX) * factorOfExtension) + 1][(int) ((filterInfo.maxY - filterInfo.minY) * factorOfExtension) + 1][3];
		for (int i = 0; i < (filterInfo.maxX - filterInfo.minX) * factorOfExtension; i++) {
			for (int j = 0; j < (filterInfo.maxY - filterInfo.minY) * factorOfExtension; j++) {
				
				int indexX = (int) (filterInfo.minX - (factorOfExtension - 1) * (filterInfo.maxX - filterInfo.minX) / 2 + i);
				int indexY = (int) (filterInfo.minY - (factorOfExtension - 1) * (filterInfo.maxY - filterInfo.minY) / 2 + j);
				
				indexX = Math.max(0, indexX);
				indexX = Math.min(((BufferedImage)image).getWidth() - 1, indexX);
				
				indexY = Math.max(0, indexY);
				indexY = Math.min(((BufferedImage)image).getHeight() - 1, indexY);
				
				int rgb = ((BufferedImage)image).getRGB(indexX, indexY);
				int red = (rgb & 0xff0000) >> 16;
				int green = (rgb & 0xff00) >> 8;
				int blue = rgb & 0xff;
				//int gray = (int) (red * 0.2989d + green * 0.5870 + blue * 0.1140);
				print[i][j][0] = red;
				print[i][j][1] = green;
				print[i][j][2] = blue;
			}
		}
		
		return ScannerUtils.createRGBImage(print);
	}
	
	
	@Override
	public Object rotateImageByDegrees(Object image, float angle) {
		
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = ((BufferedImage)image).getWidth();
        int h = ((BufferedImage)image).getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);
        
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);
        
        int x = w / 2;
        int y = h / 2;
        
        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(((BufferedImage)image), 0, 0, null);
        g2d.dispose();
        
        return rotated;
    }
}

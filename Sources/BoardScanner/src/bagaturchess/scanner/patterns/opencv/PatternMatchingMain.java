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
package bagaturchess.scanner.patterns.opencv;


import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class PatternMatchingMain {
	
	
	public static void main(String[] args) {
		
		try {
			
			int imageSize = 512;
			
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			
	        String filePath = (new File(".")).getAbsolutePath();
	        String sourceFile = ".\\data\\tests\\preprocess\\test14.png";
	        
	        Object source_obj = ImageHandlerSingleton.getInstance().loadImageFromFS(sourceFile);
	        source_obj = ImageHandlerSingleton.getInstance().resizeImage(source_obj, imageSize);
	        int[][] source_matrix = ImageHandlerSingleton.getInstance().convertToGrayMatrix(source_obj);
	        int bgcolor = MatrixUtils.getAVG(source_matrix);
	        Mat source = ImageHandlerSingleton.getInstance().graphic2Mat(source_obj);
	        Mat source_gray = new Mat(source.height(), source.width(), CvType.CV_8UC4);
			Imgproc.cvtColor(source, source_gray, Imgproc.COLOR_BGR2GRAY);
	        source = source_gray;
	        
	        for (int pid = 1; pid <= 12; pid++) {
	        	
		        MinMaxLocResult bestMatch = null;
		        int bestSize = 0;
		        
		        //For set1
		        //int startSize = (int) (0.68f * (imageSize / 8));
		        //int endSize = (int) (0.83f * (imageSize / 8));
		        
		        int startSize = (int) (0.65f * (imageSize / 8));
		        int endSize = (int) (0.85f * (imageSize / 8));
		        
		        for (int size = startSize; size <= endSize; size++) {
			        
		        	//ImageHandlerSingleton.getInstance().
		        	Object template_obj = ImageHandlerSingleton.getInstance().createPieceImage("set3", pid, bgcolor, size);
			        Mat template = ImageHandlerSingleton.getInstance().graphic2Mat(template_obj);
			        
			        Mat template_gray = new Mat(template.height(),template.width(),CvType.CV_8UC4);
			        Imgproc.cvtColor(template, template_gray, Imgproc.COLOR_BGR2GRAY);
			        template = template_gray;
			        
			        Mat outputImage = new Mat();
			        Imgproc.matchTemplate(source, template, outputImage, Imgproc.TM_CCOEFF_NORMED);
			        
			        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
			        if (bestMatch == null || bestMatch.maxVal < mmr.maxVal) {
			        	bestMatch = mmr;
			        	bestSize = template_gray.width();
			        }
			        //System.out.println(mmr.maxVal);
		        }
		        
		        System.out.println(bestSize);
		        
		        Point matchLoc = bestMatch.maxLoc;
		        //Draw rectangle on result image
		        Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + bestSize,
		                matchLoc.y + bestSize), new Scalar(255, 255, 255));
		        
		        HighGui.imshow("Feature Matching", source);
		        HighGui.waitKey();
	        }
	        
	        //Imgcodecs.imwrite(filePath + "\\data\\opencv" + ".jpg", source);
	        
	        //System.out.println("Completed.");
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

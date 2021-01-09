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
package bagaturchess.scanner.opencv;


import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.KAZE;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class PatternMatchingMain {
	
	
	public static void main(String[] args) {
		
		try {
			
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			
	        String filePath = (new File(".")).getAbsolutePath();
	        String sourceFile = filePath + "\\data\\tests\\lichess.org\\test1.png";
	        String templateFile = filePath + "\\res\\set1_b_k.png";
	        
	        for (int size = 32; size <= 64; size++) {
	        	
		        Mat source = Imgcodecs.imread(sourceFile);
		        Mat resizedSource = new Mat(512, 512, source.type());   
		        Imgproc.resize(source, resizedSource, resizedSource.size(), 0, 0, Imgproc.INTER_CUBIC);
		        source = resizedSource;
		        
		        Mat template = Imgcodecs.imread(templateFile);
		        Mat resizedTemplate = new Mat(size, size, template.type());   
		        Imgproc.resize(template, resizedTemplate, resizedTemplate.size(), 0, 0, Imgproc.INTER_CUBIC);
		        template = resizedTemplate;
		        
		        Mat outputImage = new Mat();    
		        
		        //Template matching method
		        Imgproc.matchTemplate(source, template, outputImage, Imgproc.TM_CCOEFF);
		 
		    
		        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
		        
		        Point matchLoc = mmr.maxLoc;
		        
		        //Draw rectangle on result image
		        Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(),
		                matchLoc.y + template.rows()), new Scalar(255, 255, 255));
		 
		        Imgcodecs.imwrite(filePath + "\\data\\opencv" + size + ".jpg", source);
	        }
	        
	        System.out.println("Completed.");
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

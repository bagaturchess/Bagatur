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
import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

//https://docs.opencv.org/master/d9/dab/tutorial_homography.html
public class ChessBoardDetection4 {

	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		new ChessBoardDetection4();
	}
	
	public ChessBoardDetection4() {
		
        String filePath = (new File(".")).getAbsolutePath();
        String sourceFile_rotated = filePath + "\\data\\tests\\preprocess\\test11.png";
        String sourceFile_target = filePath + "\\data\\tests\\lichess.org\\test5.png";
        
        Mat source_rotated = Imgcodecs.imread(sourceFile_rotated);
        Mat resizedSource = new Mat(512, 512, source_rotated.type());   
        Imgproc.resize(source_rotated, resizedSource, resizedSource.size(), 0, 0, Imgproc.INTER_CUBIC);
        source_rotated = resizedSource;
        
        Mat source_target = Imgcodecs.imread(sourceFile_target);
        Mat resizedTarget = new Mat(512, 512, source_target.type()); 
        Imgproc.resize(source_target, resizedTarget, resizedTarget.size(), 0, 0, Imgproc.INTER_CUBIC);
        source_target = resizedTarget;
        
        MatOfPoint2f corners1 = new MatOfPoint2f(), corners2 = new MatOfPoint2f();
        boolean found1 = Calib3d.findChessboardCorners(source_rotated, new Size(7, 7), corners1, Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK );
        boolean found2 = Calib3d.findChessboardCorners(source_target, new Size(7, 7), corners2, Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
        
        /*if (!found1)
		{
			// optimization
			TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
			Imgproc.cornerSubPix(grayImage, imageCorners, new Size(7, 7), new Size(-1, -1), term);
		}*/
        
        System.out.println(found1 + " " + found2);
        
        if (corners1.empty() || corners2.empty()) {
            System.exit(0);
        }
        
        Mat H = new Mat();
        H = Calib3d.findHomography(corners1, corners2);
        System.out.println(H.dump());
        
        Mat img1_warp = new Mat();
        Imgproc.warpPerspective(source_rotated, img1_warp, H, source_rotated.size());
        
        Mat rotMat = new Mat(2, 3, CvType.CV_32FC1);
        Mat destination = new Mat(img1_warp.rows(), img1_warp.cols(), img1_warp.type());
        Point center = new Point(destination.cols() / 2, destination.rows() / 2);
        rotMat = Imgproc.getRotationMatrix2D(center, 180, 1);
        Imgproc.warpAffine(img1_warp, destination, rotMat, destination.size());
        
        HighGui.imshow("Draw matches", destination);
        HighGui.waitKey(0);
	}
}

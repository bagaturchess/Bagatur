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
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

//https://docs.opencv.org/master/d9/dab/tutorial_homography.html
public class ChessBoardDetection3 {

	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		new ChessBoardDetection3();
	}
	
	public ChessBoardDetection3() {
		
        String filePath = (new File(".")).getAbsolutePath();
        String sourceFile_rotated = filePath + "\\data\\tests\\preprocess\\test11.png";
        String sourceFile_target = filePath + "\\data\\tests\\lichess.org\\test5.png";
        
        Mat source_rotated = Imgcodecs.imread(sourceFile_rotated);
        Mat resizedSource = new Mat(512, 512, source_rotated.type());   
        Imgproc.resize(source_rotated, resizedSource, resizedSource.size(), 0, 0, Imgproc.INTER_CUBIC);
        source_rotated = resizedSource;
        //Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
        
        
        MatOfPoint2f corners1 = new MatOfPoint2f();
        boolean found1 = Calib3d.findChessboardCorners(source_rotated, new Size(7, 7), corners1, Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK );
        
        System.out.println(found1);
        
        if (corners1.empty()) {
            System.exit(0);
        }
        
        float fishVal = 600.0f;
        float cX = 960;
        float cY = 540;
        Mat K = new Mat(3, 3, CvType.CV_32FC1);
        K.put(0, 0, new float[]{fishVal, 0, cX});
        K.put(1, 0, new float[]{0, fishVal, cY});
        K.put(2, 0, new float[]{0, 0, 1});

        Mat D = new Mat(1, 4, CvType.CV_32FC1);
        D.put(0, 0, new float[]{0, 0, 0, 0});

        Mat Knew = K.clone();
        Knew.put(0, 0, new float[]{fishVal * 0.4f, 0.0f, cX});
        Knew.put(1, 0, new float[]{0.0f, fishVal * 0.4f, cY});
        Knew.put(2, 0, new float[]{0.0f, 0.0f, 1.0f});
        
        Mat intrinsic = new Mat(3, 3, CvType.CV_32FC1);
		intrinsic.put(0, 0, 1);
		intrinsic.put(1, 1, 1);
        
		Mat undistored = new Mat(512, 512, source_rotated.type());
		Calib3d.undistort(source_rotated, undistored, intrinsic, Knew);

        HighGui.imshow("Draw matches", source_rotated);
        HighGui.waitKey(0);
	}
}

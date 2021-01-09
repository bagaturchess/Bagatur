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
public class ChessBoardDetection {

	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		new ChessBoardDetection();
	}
	
	public ChessBoardDetection() {
		
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
        
        System.out.println(found1 + " " + found2);
        
        if (corners1.empty() || corners2.empty()) {
            System.exit(0);
        }
        
        Mat H = new Mat();
        H = Calib3d.findHomography(corners1, corners2);
        System.out.println(H.dump());
        
        Mat img1_warp = new Mat();
        Imgproc.warpPerspective(source_rotated, img1_warp, H, source_rotated.size());
        
        Mat img_draw_matches = new Mat();
        
        List<Mat> list2 = new ArrayList<Mat>();
        list2.add(source_rotated);
        list2.add(source_target);
        Core.hconcat(list2, img_draw_matches);
        Point []corners1Arr = corners1.toArray();
        for (int i = 0 ; i < corners1Arr.length; i++) {
            Mat pt1 = new Mat(3, 1, CvType.CV_64FC1), pt2 = new Mat();
            pt1.put(0, 0, corners1Arr[i].x, corners1Arr[i].y, 1 );
            Core.gemm(H, pt1, 1, new Mat(), 0, pt2);
            double[] data = pt2.get(2, 0);
            Core.divide(pt2, new Scalar(data[0]), pt2);
            double[] data1 =pt2.get(0, 0);
            double[] data2 = pt2.get(1, 0);
            Point end = new Point((int)(source_rotated.cols()+ data1[0]), (int)data2[0]);
            Imgproc.line(img_draw_matches, corners1Arr[i], end, new Scalar(0, 255, 0), 2);
        }
        HighGui.imshow("Draw matches", img_draw_matches);
        HighGui.waitKey(0);
	}
}

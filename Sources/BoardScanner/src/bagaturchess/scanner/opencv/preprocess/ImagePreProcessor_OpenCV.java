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
package bagaturchess.scanner.opencv.preprocess;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.opencv.OpenCVUtils;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Base;


public class ImagePreProcessor_OpenCV extends ImagePreProcessor_Base {
	
	
	private MatOfPoint2f targetCorners;
	
	
	public ImagePreProcessor_OpenCV(BoardProperties _boardProperties) throws IOException {
		
		super(_boardProperties);
		
		Object whiteSquareColor = ImageHandlerSingleton.getInstance().getColor(254);
		Object blackSquareColor = ImageHandlerSingleton.getInstance().getColor(0);
		Object targetPerspective_obj = ImageHandlerSingleton.getInstance().createBoardImage(boardProperties,
				"8/8/8/8/8/8/8/8", whiteSquareColor, blackSquareColor);
		
		//ImageHandlerSingleton.getInstance().saveImage("OpenCV_target", "png", targetPerspective_obj);
		
		Mat targetPerspective = ImageHandlerSingleton.getInstance().graphic2Mat(targetPerspective_obj);
		MatOfPoint2f corners = new MatOfPoint2f();
		boolean found = Calib3d.findChessboardCorners(targetPerspective, new Size(7, 7), corners);
		if (!found) {
			throw new IllegalStateException("Chess board not found in the generated image.");
		}
		Point[] targetCorners_array = OpenCVUtils.getOrderedCorners(corners.toArray(), targetPerspective.width(), targetPerspective.height());
		
		targetCorners = new MatOfPoint2f();
		targetCorners.fromArray(targetCorners_array);
	}
	
	
	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_input", "png", image);
		
		Mat source_rgb = ImageHandlerSingleton.getInstance().graphic2Mat(image);
		
		
		Mat result = findChessBoardCornersByBuildInFunction(source_rgb);
		
		if (result == null) {
			
			Mat source_gray = new Mat(source_rgb.height(), source_rgb.width(), CvType.CV_8UC4);
			Imgproc.cvtColor(source_rgb, source_gray, Imgproc.COLOR_BGR2GRAY);
			
	        result = findChessBoardCornersByContour(source_rgb, source_gray);
		}
		
        //HighGui.imshow("Dump", result);
        //HighGui.waitKey(0);
        
		Object resultObj = ImageHandlerSingleton.getInstance().mat2Graphic(result);
		
		ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_result", "png", resultObj);
		
		return resultObj;
	}
	
	
	private Mat findChessBoardCornersByContour(Mat source_rgb, Mat source_gray) {
		
		
		//Imgproc.GaussianBlur(source_gray, source_gray, new Size(3, 3), 1);
		
		/*int kernelSize = 2;
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(2 * kernelSize + 1, 2 * kernelSize + 1),
                new Point(kernelSize, kernelSize));
        Imgproc.erode(source_gray, source_gray, element);*/
        //Imgproc.dilate(source_gray, source_gray, element);
        
		/*HighGui.imshow("cannyOutput", source_gray);
		HighGui.waitKey(0);
        */
        
		Mat cannyOutput = new Mat();
		int threshold = 20;
		Imgproc.Canny(source_gray, cannyOutput, threshold, 4 * threshold);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		//HighGui.imshow("cannyOutput", cannyOutput);
		//HighGui.waitKey(0);
		
		MatOfPoint bigestContour = OpenCVUtils.findBigestContour(contours);
		
		/*MatOfInt hull = new MatOfInt();
		Imgproc.convexHull(bigestContour, hull, true);
		int[] indexes = hull.toArray();
		Point[] values = bigestContour.toArray();
		Point[] hullContour = new Point[indexes.length];
		for (int i = 0; i < indexes.length; i++ ) {
			hullContour[i] = values[indexes[i]];
		}*/
		
		Point[] hullContour = OpenCVUtils.convexHull(bigestContour.toArray());
		/*Mat hullDrawing = source_rgb;//Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
		for (int i = 0; i < hullContour.length; i++ ) {
			Imgproc.drawMarker(hullDrawing, hullContour[i], new Scalar(255, 255, 255));
		}
		HighGui.imshow("hull", hullDrawing);
		HighGui.waitKey(0);
		*/
		
		//MatOfPoint2f curve = new MatOfPoint2f(bigestContour.toArray());
		MatOfPoint2f curve = new MatOfPoint2f(hullContour);
		double epsilon = 0.005 * Imgproc.arcLength(curve, true);
		MatOfPoint2f approxCurve = null;
		//while (approxCurve == null || approxCurve.toArray().length > 6) {
			approxCurve = new MatOfPoint2f();
			Imgproc.approxPolyDP(curve, approxCurve, epsilon, true);
			//epsilon += 0.001;
		//}
		Point[] approxCurve_points = approxCurve.toArray();
		System.out.println("ImagePreProcessor_OpenCV: Chess board found by contours with " + approxCurve_points.length + " points.");
		/*List<MatOfPoint> curve_in_list = new ArrayList<MatOfPoint>();
		curve_in_list.add(new MatOfPoint(approxCurve.toArray()));
		Mat drawing = source_rgb;//Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
		Imgproc.drawContours(drawing, curve_in_list, 0, new Scalar(255, 255, 255));
		HighGui.imshow("curve_in_list", drawing);
		HighGui.waitKey(0);
		*/
		
		if (approxCurve_points.length > 4 ) {
			
	        Rect boundingRec = Imgproc.boundingRect(bigestContour);
	        OpenCVUtils.extendRect(boundingRec, 0.05);
	        /*Mat bounding = new Mat(source_rgb, boundingRec);
			HighGui.imshow("bounding", bounding);
			HighGui.waitKey(0);
			*/
	        
			approxCurve_points = OpenCVUtils.getMinimalQuadrilateral(approxCurve_points, boundingRec);
			/*Mat drawing = source_rgb;
			for (int i = 0; i < approxCurve_points.length; i++ ) {
				//System.out.println("x=" + minimalQuadrilateral[i].x + " y=" + minimalQuadrilateral[i].y);
				Imgproc.drawMarker(drawing, approxCurve_points[i], new Scalar(255, 255, 255));
			}
			HighGui.imshow("getMinimalQuadrilateral", drawing);
			HighGui.waitKey(0);
			*/
		}
		
		Point[] corners_of_contour = OpenCVUtils.getOrderedCorners(approxCurve_points, source_gray.width(), source_gray.height());
		
		MatOfPoint2f src = new MatOfPoint2f(
				corners_of_contour[0],
				corners_of_contour[1],
				corners_of_contour[2],
				corners_of_contour[3]);
		
		MatOfPoint2f dst = new MatOfPoint2f(
		        new Point(0, 0),
		        new Point(0, source_gray.height()),
		        new Point(source_gray.width(), source_gray.height()),
		        new Point(source_gray.width(), 0)      
		        );
		
		Mat result = new Mat();
		Mat warpMat = Imgproc.getPerspectiveTransform(src, dst);
		Imgproc.warpPerspective(source_rgb, result, warpMat, source_rgb.size());
		
        /*HighGui.imshow("Draw matches", result);
        HighGui.waitKey(0);
        
        Rect boundingRec = Imgproc.boundingRect(bigestContour);
        result = new Mat(source, boundingRec);*/
        
        //HighGui.imshow("Draw matches", result);
        //HighGui.waitKey(0);
		
		return result;
	}
	
	
	public Mat findChessBoardCornersByBuildInFunction(Mat source_rgb) {
		
		MatOfPoint2f corners = new MatOfPoint2f();
		boolean found = Calib3d.findChessboardCorners(source_rgb, new Size(7, 7), corners);
		
		if (found && !corners.empty()) {
			
			Mat result = new Mat();
			
			MatOfPoint2f corners_ordered = new MatOfPoint2f();
			corners_ordered.fromArray(OpenCVUtils.getOrderedCorners(corners.toArray(), source_rgb.width(), source_rgb.height()));
			
			Mat H = Calib3d.findHomography(corners_ordered, targetCorners);
			
			Imgproc.warpPerspective(source_rgb, result, H, source_rgb.size());
			
			System.out.println("ImagePreProcessor_OpenCV: Chess board found in a standard way.");
			
			return result;
		}
		
		return null;
	}
	
	
	@Override
	public Point[] getBoardContour(Object image) throws IOException {
		
		Mat source = ImageHandlerSingleton.getInstance().graphic2Mat(image);
		Mat source_gray = new Mat(source.height(), source.width(), CvType.CV_8UC4);
		Imgproc.cvtColor(source, source_gray, Imgproc.COLOR_BGR2GRAY);
		
		MatOfPoint2f corners = new MatOfPoint2f();
		boolean found = Calib3d.findChessboardCorners(source_gray, new Size(7, 7), corners);
		if (!found) {
			return null;
		}
		
		return OpenCVUtils.getOrderedCorners(corners.toArray(), source_gray.width(), source_gray.height());
	}
}

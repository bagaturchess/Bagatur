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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.KMeans;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Base;

public class ImagePreProcessor_OpenCV extends ImagePreProcessor_Base {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private Mat targetPerspective;

	public ImagePreProcessor_OpenCV(BoardProperties _boardProperties) throws IOException {

		super(_boardProperties);

		Object whiteSquareColor = ImageHandlerSingleton.getInstance().getColor(220);
		Object blackSquareColor = ImageHandlerSingleton.getInstance().getColor(120);
		Object targetPerspective_obj = ImageHandlerSingleton.getInstance().createBoardImage(boardProperties,
				"8/8/8/8/8/8/8/8", whiteSquareColor, blackSquareColor);

		ImageHandlerSingleton.getInstance().saveImage("OpenCV_target", "png", targetPerspective_obj);

		targetPerspective = bufferedImage2Mat((BufferedImage) targetPerspective_obj);
	}

	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		
		ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_input", "png", image);
		
		Mat source = bufferedImage2Mat((BufferedImage) image);
		
		MatOfPoint2f corners1 = new MatOfPoint2f();
		MatOfPoint2f corners2 = new MatOfPoint2f();
		boolean found1 = Calib3d.findChessboardCorners(source, new Size(7, 7), corners1,
				Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
		boolean found2 = Calib3d.findChessboardCorners(targetPerspective, new Size(7, 7), corners2,
				Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);
		
		/*if (found1) {
			TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
			Imgproc.cornerSubPix(source, corners1, new Size(7, 7), new Size(-1, -1), term);
		}*/
		
		if (!found1 || !found2) {
			System.out.println("!found1 || !found2 " + found1 + " " + found2);
			return null;
		}
		
		if (corners1.empty() || corners2.empty()) {
			System.out.println("corners1.empty() || corners2.empty()");
			return null;
		}
		System.out.println(corners1.size() + " " + corners2.size());
		
		Point[] wrapPoints_corners1 = new Point[4];
		RotatedRect rotatedRect_corners1 = Imgproc.minAreaRect(corners1);
		rotatedRect_corners1.points(wrapPoints_corners1);
		MatOfPoint2f corners1_unordered = new MatOfPoint2f();
		corners1_unordered.fromArray(wrapPoints_corners1);
		
		Point[] wrapPoints_corners2 = new Point[4];
		RotatedRect rotatedRect_corners2 = Imgproc.minAreaRect(corners2);
		rotatedRect_corners2.points(wrapPoints_corners2);
		MatOfPoint2f corners2_unordered = new MatOfPoint2f();
		corners2_unordered.fromArray(wrapPoints_corners2);
		
		MatOfPoint2f corners1_ordered = new MatOfPoint2f();
		corners1_ordered.fromArray(orderCorners(corners1_unordered.toArray(), source.width(), source.height()));
		
		MatOfPoint2f corners2_ordered = new MatOfPoint2f();
		corners2_ordered.fromArray(orderCorners(corners2_unordered.toArray(), targetPerspective.width(), targetPerspective.height()));
        
		Mat H = new Mat();
		H = Calib3d.findHomography(corners1_ordered, corners2_ordered);
		// System.out.println(H.dump());
		
		//cnts = Imgproc.grabCut(img, mask, rect, bgdModel, fgdModel, iterCount);
		
		Mat img1_warp = new Mat();
		Imgproc.warpPerspective(source, img1_warp, H, source.size());
		
        //HighGui.imshow("Draw matches", img1_warp);
        //HighGui.waitKey(0);
        
		/*Mat rotMat = new Mat(2, 3, CvType.CV_32FC1);
		Mat destination = new Mat(img1_warp.rows(), img1_warp.cols(), img1_warp.type());
		Point center = new Point(destination.cols() / 2, destination.rows() / 2);
		rotMat = Imgproc.getRotationMatrix2D(center, 180, 1);
		Imgproc.warpAffine(img1_warp, destination, rotMat, destination.size());
		*/
		
		BufferedImage result = mat2BufferedImage(img1_warp);
		
		ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_result", "png", result);
		
		return result;
	}
	
	
	public static Mat bufferedImage2Mat(BufferedImage image) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "png", byteArrayOutputStream);
		byteArrayOutputStream.flush();
		return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
	}
	
	
	public static BufferedImage mat2BufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".png", matrix, mob);
		return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
	}
	
	
	private Point[] orderCorners(Point[] cornersUnordered, double maxX, double maxY) {
		
		if (cornersUnordered.length != 4) {
			throw new IllegalStateException();
		}
		
		Point cornerTopLeft = new Point(0, 0);
		Point cornerTopRight = new Point(0, maxY);
		Point cornerBotRight = new Point(maxX, maxY);
		Point cornerBotLeft = new Point(maxX, 0);
		
		Point[] cornerPoints = new Point[4];
		
		cornerPoints[0] = cornersUnordered[0];
		for (int i = 0; i < cornersUnordered.length; i++) {
			if (distance(cornerTopLeft, cornerPoints[0]) > distance(cornerTopLeft, cornersUnordered[i])) {
				cornerPoints[0] = cornersUnordered[i];
			}
		}

		cornerPoints[1] = cornersUnordered[0];
		for (int i = 0; i < cornersUnordered.length; i++) {
			if (distance(cornerTopRight, cornerPoints[1]) > distance(cornerTopRight, cornersUnordered[i])) {
				cornerPoints[1] = cornersUnordered[i];
			}
		}
		
		cornerPoints[2] = cornersUnordered[0];
		for (int i = 0; i < cornersUnordered.length; i++) {
			if (distance(cornerBotRight, cornerPoints[2]) > distance(cornerBotRight, cornersUnordered[i])) {
				cornerPoints[2] = cornersUnordered[i];
			}
		}
		
		cornerPoints[3] = cornersUnordered[0];
		for (int i = 0; i < cornersUnordered.length; i++) {
			if (distance(cornerBotLeft, cornerPoints[3]) > distance(cornerBotLeft, cornersUnordered[i])) {
				cornerPoints[3] = cornersUnordered[i];
			}
		}
		
		return cornerPoints;
	}
	
	
	private Point[] orderCorners1(Point[] cornersUnordered) {
		
		Point[] cornerPoints = new Point[4];
		Point p1, p2, p3, p4;
		Point topLeft = null, topRight = null, botRight = null, botLeft = null;
		
		List<Point> corners = new ArrayList<Point>();
		for (int i = 0; i < cornersUnordered.length; ++i)
			corners.add(cornersUnordered[i]);

		/* Top set of points */
		// find p1
		p1 = corners.get(0);
		for (Point point : corners) {
			if (point.y < p1.y) {
				p1 = point;
			}
		}
		corners.remove(p1);

		// find p2
		p2 = corners.get(0);
		for (Point point : corners) {
			if (distance(p1, point) < distance(p1, p2)) {
				p2 = point;
			}
		}
		corners.remove(p2);

		/* Identify top left and top right */
		/*
		 * Note that the logic is safe if the points have equal x values. Safe in the
		 * sense that different points will get assigned to topLeft and topRight
		 */
		topLeft = p1.x < p2.x ? p1 : p2;
		topRight = p2.x > p1.x ? p2 : p1;

		/* Bottom set of points */
		// corners only contains 2 points, the bottom ones
		p3 = corners.get(0);
		p4 = corners.get(1);
		botRight = p3.x > p4.x ? p3 : p4;
		botLeft = p4.x < p3.x ? p4 : p3;

		cornerPoints[0] = topLeft;
		cornerPoints[1] = topRight;
		cornerPoints[2] = botRight;
		cornerPoints[3] = botLeft;

		return cornerPoints;
	}

	private double distance(Point p1, Point p2) {
		return Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2));
	}
}

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


import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;


public class OpenCVUtils {
	
	
	public static MatOfPoint findBigestContour(List<MatOfPoint> contours) {
		MatOfPoint bigestContour = null;
		int bigestFace = 0;
		for (int i = 0; i < contours.size(); i++) {
			MatOfPoint mop = contours.get(i);
			Rect contourRec = Imgproc.boundingRect(mop);
			int face = contourRec.height * contourRec.width;
			if (face > bigestFace) {
				bigestFace = face;
				bigestContour = mop;
			}
		}
		return bigestContour;
	}
	
	
	public static Point[] getOrderedCorners(Point[] cornersUnordered, double maxX, double maxY) {
		
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
	
	
	public static double distance(Point p1, Point p2) {
		return Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2));
	}
	
	
	// To find orientation of ordered triplet (p, q, r). 
	// The function returns following values 
	// 0 --> p, q and r are colinear 
	// 1 --> Clockwise 
	// 2 --> Counterclockwise 
	public static int orientation(Point p, Point q, Point r) { 
	    double val = (q.y - p.y) * (r.x - q.x) - 
	              (q.x - p.x) * (r.y - q.y); 
	  
	    if (val == 0) return 0;  // colinear 
	    return (val > 0)? 1: 2; // clock or counterclock wise 
	} 
	
	
	// Prints convex hull of a set of n points. 
	public static Point[] convexHull(Point[] points) {
		
		int n = points.length;
		
	    // There must be at least 3 points 
	    if (n < 3) {
	    	throw new IllegalStateException("n < 3");
	    }
	  
	    // Initialize Result 
	    List<Point> hull_list = new ArrayList<Point>(); 
	  
	    // Find the leftmost point 
	    int l = 0; 
	    for (int i = 1; i < n; i++) 
	        if (points[i].x < points[l].x) 
	            l = i; 
	  
	    // Start from leftmost point, keep moving counterclockwise 
	    // until reach the start point again.  This loop runs O(h) 
	    // times where h is number of points in result or output. 
	    int p = l, q; 
	    do
	    { 
	        // Add current point to result 
	    	hull_list.add(points[p]); 
	  
	        // Search for a point 'q' such that orientation(p, x, 
	        // q) is counterclockwise for all points 'x'. The idea 
	        // is to keep track of last visited most counterclock- 
	        // wise point in q. If any point 'i' is more counterclock- 
	        // wise than q, then update q. 
	        q = (p+1)%n; 
	        for (int i = 0; i < n; i++) 
	        { 
	           // If i is more counterclockwise than current q, then 
	           // update q 
	           if (orientation(points[p], points[i], points[q]) == 2) 
	               q = i; 
	        } 
	  
	        // Now q is the most counterclockwise with respect to p 
	        // Set p as q for next iteration, so that q is added to 
	        // result 'hull' 
	        p = q; 
	  
	    } while (p != l);  // While we don't come to first point 
	  
	    Point[] hull = new Point[hull_list.size()];
	    hull_list.toArray(hull);
	    
	    return hull;
	} 
	
    /*contours.clear();
    contours.add(bigestContour);
    
    List<Point> allPoints_array = new ArrayList<Point>();
    for (int i = 0; i < contours.size(); i++) {
    	MatOfPoint mop = contours.get(i);
    	Point[] points = mop.toArray();
    	for (Point point : points) {
    		allPoints_array.add(point);
    	}
    }
    Point[] allPoints = new Point[allPoints_array.size()];
    allPoints_array.toArray(allPoints);
    Point[] corners = getOrderedCorners(allPoints, source_gray.width(), source_gray.height());
	corners1_ordered.fromArray(corners);
	
	
    MatOfPoint2f src = new MatOfPoint2f(
    		corners[0],
    		corners[1],
    		corners[2],
    		corners[3]);

    MatOfPoint2f dst = new MatOfPoint2f(
            new Point(0, 0),
            new Point(0, source_gray.height()),
            new Point(source_gray.width(), source_gray.height()),
            new Point(source_gray.width(), 0)      
            );
    
	Mat warpMat = Imgproc.getPerspectiveTransform(src, dst);
    Imgproc.warpPerspective(source, result, warpMat, source.size());
    */
    
    //HighGui.imshow("Draw matches", result);
    //HighGui.waitKey(0);
    
    
	/*Mat drawing = source_gray;//Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
	for (int i = 0; i < corners.length; i++) {
		Imgproc.drawMarker(drawing, corners[i], new Scalar(255, 255, 255));
	}
    HighGui.imshow("Draw matches", drawing);
    HighGui.waitKey(0);
    */
    
   /* Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
    for (int i = 0; i < contours.size(); i++) {
        Scalar color = new Scalar(255, 255, 155);
        Imgproc.drawContours(drawing, contours, i, color, 2, 0, hierarchy, 0, new Point());
    }
    
    HighGui.imshow("Draw matches", drawing);
    HighGui.waitKey(0);
    */
	
}

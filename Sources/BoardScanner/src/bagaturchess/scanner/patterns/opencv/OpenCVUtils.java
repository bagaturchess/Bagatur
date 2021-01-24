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


import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;


public class OpenCVUtils {
	
	
	public static void extendRect( Rect rect, double percent) {
        double heightExtension = percent * rect.height;
        double widthExtension = percent * rect.width;
        rect.x -= widthExtension / 2;
        rect.width += widthExtension;
        rect.y -= heightExtension / 2;
        rect.height += heightExtension;
	}
	
	
	public static Point[] getMinimalQuadrilateral(Point[] convexPolygon, Rect boundingRec) {
		
		if (convexPolygon.length <= 4) {
			throw new IllegalStateException();
		}
		
		//Create list with all entries
		List<ListItem<Point>> all_init_list = new ArrayList<ListItem<Point>>();
		for (int i = 0; i < convexPolygon.length; i++) {
			ListItem<Point> cur = new ListItem<Point>();
			cur.value = convexPolygon[i];
			all_init_list.add(cur);
		}
		
		//Link the list
		for (int i = 0; i < all_init_list.size() - 1; i++) {
			all_init_list.get(i).next = all_init_list.get(i + 1);
		}
		//Make it cyclic
		all_init_list.get(all_init_list.size() - 1).next = all_init_list.get(0);
		
		
		int countOfPoints = all_init_list.size();
		ListItem<Point> start = all_init_list.get(0);
		
		while (countOfPoints > 4) {
			
			//System.out.println("countOfPoints=" + countOfPoints);
			
			double minTriangleArea = Double.MAX_VALUE;
			ListItem<Point> best = null;
			ListItem<Point> best_intersection = new ListItem<Point>();
			ListItem<Point> cur = start;
			do {
				Point p1 = cur.value;
				Point p2 = cur.next.value;
				Point p3 = cur.next.next.value;
				Point p4 = cur.next.next.next.value;
				
				//Do work
				Point intersection = findIntersection(p1, p2, p4, p3);
				if (intersection != null && boundingRec.contains(intersection)) {
					double cur_area = triangleArea(p2, intersection, p3);
					if (cur_area < minTriangleArea) {
						minTriangleArea = cur_area;
						best = cur;
						best_intersection.value = intersection;
						//System.out.println("minTriangleArea=" + minTriangleArea);
					}
				}
				
				cur = cur.next;
			} while (cur != start);
			
			//If there is best than remove 2 points and put their intersection instead
			if (best == null) {
				break;
			}
			best_intersection.next = best.next.next.next;
			best.next = best_intersection;
			countOfPoints--;
			start = best;
		}
		
		//Compose result
		Point[] result = new Point[countOfPoints];
		while (countOfPoints > 0) {
			result[countOfPoints - 1] = start.value;
			start = start.next;
			countOfPoints--;
		}
		
		return result;
	}
	
	
	public static double triangleArea(Point A, Point B, Point C) {
		double area = (A.x * (B.y - C.y) + B.x * (C.y - A.y) + C.x * (A.y - B.y)) / 2.0;
		return Math.abs(area);
	}
	
	
    public static Point findIntersection(Point l1s, Point l1e, Point l2s, Point l2e) {
        
    	double a1 = l1e.y - l1s.y;
        double b1 = l1s.x - l1e.x;
        double c1 = a1 * l1s.x + b1 * l1s.y;
        
        double a2 = l2e.y - l2s.y;
        double b2 = l2s.x - l2e.x;
        double c2 = a2 * l2s.x + b2 * l2s.y;
        
        double delta = a1 * b2 - a2 * b1;
        if (delta == 0) {
        	return null;
        }
        
        return new Point((b2 * c1 - b1 * c2) / delta, (a1 * c2 - a2 * c1) / delta);
    }
	
	
	public static MatOfPoint findBigestContour(List<MatOfPoint> contours) {
		MatOfPoint bigestContour = null;
		int bigestArea = 0;
		for (int i = 0; i < contours.size(); i++) {
			MatOfPoint mop = contours.get(i);
			double contourArea = Imgproc.contourArea(mop);
			//Rect contourRec = Imgproc.boundingRect(mop);
			//int contourArea = contourRec.height * contourRec.width;
			if (contourArea > bigestArea) {
				bigestArea = (int) contourArea;
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
    
            Mat element = Imgproc.getStructuringElement(elementType, new Size(2 * kernelSize + 1, 2 * kernelSize + 1),
                new Point(kernelSize, kernelSize));
        if (doErosion) {
            Imgproc.erode(matImgSrc, matImgDst, element);
        } else {
            Imgproc.dilate(matImgSrc, matImgDst, element);
        }
        
    */
	
	
	private static final class ListItem<T> {
		public T value;
		public ListItem<T> next;
	}
}

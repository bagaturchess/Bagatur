package bagaturchess.scanner.opencv;

import java.io.File;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class PatternMatchingMain2 {
   public static void main(String args[]) throws Exception {
	   
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
        String filePath = (new File(".")).getAbsolutePath();
        String sourceFile = filePath + "\\data\\tests\\lichess.org\\test1.png";
        String templateFile = filePath + "\\res\\set1_b_k.png";
        
        Mat source = Imgcodecs.imread(sourceFile);
        Mat resizedSource = new Mat(512, 512, source.type());   
        Imgproc.resize(source, resizedSource, resizedSource.size(), 0, 0, Imgproc.INTER_CUBIC);
        source = resizedSource;
        
        Mat template = Imgcodecs.imread(templateFile);
        Mat resizedTemplate = new Mat(53, 53, template.type());   
        Imgproc.resize(template, resizedTemplate, resizedTemplate.size(), 0, 0, Imgproc.INTER_CUBIC);
        template = resizedTemplate;
      
      
      //Creating an empty matrix to store the destination image
      Mat dst = new Mat();
      FastFeatureDetector detector = FastFeatureDetector.create();
      //Detecting the key points in both images
      MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();
      detector.detect(source, keyPoints1);
      MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();
      detector.detect(template, keyPoints2);
      MatOfDMatch matof1to2 = new MatOfDMatch();
      Features2d.drawMatches(source, keyPoints1, template, keyPoints2, matof1to2, dst);
      HighGui.imshow("Feature Matching", dst);
      HighGui.waitKey();
   }
}
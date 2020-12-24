package bagaturchess.scanner.run;


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.scanner.utils.ImageProperties;
import bagaturchess.scanner.utils.ScannerUtils;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class ScannerCheckerMain_SingleSquare {
	
	
	private static final String NET_FILE = "scanner.bin";
	private static ConvolutionalNetwork network;
	
	
	public static void main(String[] args) {
		
		try {
			
			ImageProperties imageProperties = new ImageProperties(512);
			
			List<BufferedImage> grayImages = new ArrayList<BufferedImage>();
			List<Integer> pids = new ArrayList<Integer>();
			for (int pid = 0; pid <= 12; pid++) {
				BufferedImage whiteImage = ScannerUtils.createSquareImage(imageProperties, pid, imageProperties.WHITE_SQUARE);
				BufferedImage blackImage = ScannerUtils.createSquareImage(imageProperties, pid, imageProperties.BLACK_SQUARE);
				whiteImage = ScannerUtils.convertToGrayScale(whiteImage);
				blackImage = ScannerUtils.convertToGrayScale(blackImage);
				grayImages.add(whiteImage);
				grayImages.add(blackImage);
				if (pid == 0) {
					pids.add(0);
					pids.add(13);
				} else {
					pids.add(pid);
					pids.add(pid);
				}
			}
			
				
			System.out.println("Loading network ...");
				
				
			network = (ConvolutionalNetwork) FileIO.createFromFile(new File(NET_FILE));
			
			
			System.out.println("Network loaded.");
			
			int success = 0;
			int failure = 0;
			for (int i = 0; i < grayImages.size(); i++) {
				
				BufferedImage curImage = grayImages.get(i);
				float[] networkInput = ScannerUtils.convertToFlatGrayArray(curImage);
				network.setInput(new Tensor(networkInput));
				network.forward();
				float[] actual_output = network.getOutput();
				
				float maxValue = 0;
				int maxIndex = 0;
				for (int j = 0; j < actual_output.length; j++) {
					if (maxValue < actual_output[j]) {
						maxValue = actual_output[j];
						maxIndex = j;
					}
				}
				
				if (maxIndex == pids.get(i)) {
					success++;
				} else {
					failure++;
				}
			}
			
			System.out.println("Success is " + success / (float)(success + failure));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

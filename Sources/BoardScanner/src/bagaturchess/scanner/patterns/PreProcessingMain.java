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
package bagaturchess.scanner.patterns;


import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Rotate;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Base;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Crop;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Crop_KMeans;


public class PreProcessingMain {
	
	
	public static void main(String[] args) {
		
		try {
			
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/preprocess/test10.png");
			Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/lichess.org/test1.png");
			
			BoardProperties boardProperties = new BoardProperties(256);
			ImagePreProcessor_Base processor = new ImagePreProcessor_Crop_KMeans(boardProperties);
			
			long startTime = System.currentTimeMillis();
			processor.filter(image);
			System.out.println((System.currentTimeMillis() - startTime) + "ms");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

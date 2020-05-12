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
package bagaturchess.learning.goldmiddle.api;


public class LearningInputFactory {
	
	
	public static ILearningInput createDefaultInput() {
		
		return new bagaturchess.learning.goldmiddle.impl4.Bagatur_V20_LearningInputImpl();
		//return new bagaturchess.learning.goldmiddle.impl3.Bagatur_V18_LearningInputImpl();//82.42
		//return new bagaturchess.learning.goldmiddle.impl1.Bagatur_V17_LearningInputImpl();//81
		//return new bagaturchess.learning.goldmiddle.impl2.Bagatur_V17_LearningInputImpl();
		//return new bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.Bagatur_ALL_LearningInputImpl();
		//return new bagaturchess.learning.goldmiddle.impl.cfg.base_allfeatures.ALL_LearningInputImpl();
		//return new bagaturchess.learning.goldmiddle.impl.cfg.bagatur.Bagatur_LearningInputImpl();
	}
}

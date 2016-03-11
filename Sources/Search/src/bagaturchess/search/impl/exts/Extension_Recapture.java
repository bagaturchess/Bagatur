/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
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
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */


package bagaturchess.search.impl.exts;

import bagaturchess.search.api.IExtensionMode;


public class Extension_Recapture extends ExtensionInfo {
	
	
	/**
	Recapture > RecaptureExtension -> , calc=15, rate: 0.4031114025153484, count_total: 11643706, count_ration: 0.1733678362501526, STATS: 
	0.0      0.000   0.000   0.000   0.000   1.000
	1170801.0   0.024   0.018  27,600.83  27,600.83   1.000
	214655.0   0.038   0.008  8,071.498  8,071.498   1.000
	107872.0   0.049   0.009  5,247.198  5,247.198   1.000
	64934.0   0.062   0.011  4,043.345  4,043.345   1.000
	54181.0   0.075   0.011  4,084.537  4,084.537   1.000
	50425.0   0.094   0.013  4,739.35  4,739.35   1.000
	58066.0   0.112   0.013  6,522.041  6,522.041   1.000
	80173.0   0.128   0.010  10,265.812  10,265.812   1.000
	90245.0   0.140   0.011  12,610.913  12,610.913   1.000
	91987.0   0.162   0.016  14,887.081  14,887.081   1.000
	140275.0   0.195   0.023  27,300.585  27,300.585   1.000
	330846.0   0.243   0.032  80,446.548  80,446.548   1.000
	690606.0   0.289   0.036  199,444.842  199,444.842   1.000
	1490536.0   0.342   0.035  510,063.114  510,063.114   1.000
	2839251.0   0.397   0.039  1,127,974.786  1,127,974.786   1.000
	4168853.0   0.454   0.043  1,892,867.819  1,892,867.819   1.000
	 */
	
	private double[] bounds = new double[] {
			0,		//0
			0.024, //1
			0.038,  //2
			0.049,  //3
			0.062,  //4
			0.075,  //5
			0.094,  //6
			0.112,  //7
			0.128,  //8
			0.140,  //9
			0.162,  //10
			0.195,  //11
			0.243,  //12
			0.289,  //13
			0.342,  //14
			0.397,  //15
			0.454,  //16
			0.470,	//17
			0.470,  //18
			0.470,  //19
			0.470,  //20
			0.470,  //21
			0.470,  //22
			0.470,  //23
			0.470,  //24
			0.470,  //25
			0.470,  //26
			0.470,  //27
			0.470,  //28
			0.470,  //29
			0.470,  //30
			0.470,  //31
			0.470,  //32
			0.470	//33
			};
	
	public Extension_Recapture(IExtensionMode extensionMode, int updateInterval, int _accepted_score_diff) {
		super("RecaptureExtension", extensionMode, updateInterval, _accepted_score_diff);
	}
	
	@Override
	protected double getStaticRating(int level) {
		return bounds[level];
	}
	
	@Override
	protected int getObservationFrequency() {
		return 173;
	}
}

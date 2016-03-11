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


public class Extension_MoveEval extends ExtensionInfo {
	
	
	/**
	MoveEval > MoveEvalExtension -> , calc=14, rate: 0.6923728813559322, count_total: 23364442, count_ration: 0.2793448623678094, STATS: 
	0.0      0.000   0.000   0.000   0.000   1.000
	694015.0   0.040   0.038  27,921.622  27,921.622   1.000
	37979.0   0.048   0.031  1,806.756  1,806.756   1.000
	19116.0   0.070   0.043  1,337.192  1,337.192   1.000
	17175.0   0.087   0.042  1,494.207  1,494.207   1.000
	14394.0   0.102   0.046  1,466.878  1,466.878   1.000
	14428.0   0.121   0.046  1,743.726  1,743.726   1.000
	14640.0   0.138   0.047  2,015.195  2,015.195   1.000
	11890.0   0.159   0.054  1,890.726  1,890.726   1.000
	62870.0   0.290   0.056  18,232.586  18,232.586   1.000
	292031.0   0.364   0.047  106,354.975  106,354.975   1.000
	612958.0   0.412   0.042  252,246.086  252,246.086   1.000
	1409891.0   0.481   0.043  677,762.956  677,762.956   1.000
	2781771.0   0.549   0.049  1,526,027.472  1,526,027.472   1.000
	4227937.0   0.625   0.051  2,643,167.694  2,643,167.694   1.000
	5226081.0   0.700   0.053  3,658,544.73  3,658,544.73   1.000
	7927266.0   0.788   0.053  6,248,814.129  6,248,814.129   1.000
	 */
	
	private double[] bounds = new double[] {
			0,		//0
			0.040, //1
			0.048,  //2
			0.070,  //3
			0.087,  //4
			0.102,  //5
			0.121,  //6
			0.138,  //7
			0.159,  //8
			0.290,  //9
			0.364,  //10
			0.412,  //11
			0.481,  //12
			0.549,  //13
			0.625,  //14
			0.700,  //15
			0.788,  //16
			0.800,	//17
			0.800,  //18
			0.800,  //19
			0.800,  //20
			0.800,  //21
			0.800,  //22
			0.800,  //23
			0.800,  //24
			0.800,  //25
			0.800,  //26
			0.800,  //27
			0.800,  //28
			0.800,  //29
			0.800,  //30
			0.800,  //31
			0.800,  //32
			0.800	//33
			};
	
	public Extension_MoveEval(IExtensionMode extensionMode, int updateInterval, int _accepted_score_diff) {
		super("MoveEvalExtension", extensionMode, updateInterval, _accepted_score_diff);
	}
	
	@Override
	protected double getStaticRating(int level) {
		return bounds[level];
	}
	
	@Override
	protected int getObservationFrequency() {
		return 279;
	}
}

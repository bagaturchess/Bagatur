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


public class Extension_WinCapPawn extends ExtensionInfo {
	
	
	/**
	WinCapPawn > WinCapPawnExtension -> , calc=15, rate: 0.39506726457399105, count_total: 12153033, count_ration: 0.1809514114395108, STATS: 
	0.0      0.000   0.000   0.000   0.000   1.000
	1483224.0   0.017   0.027  24,730.487  24,730.487   1.000
	551060.0   0.038   0.013  21,077.774  21,077.774   1.000
	553410.0   0.055   0.012  30,357.278  30,357.278   1.000
	494229.0   0.077   0.015  37,892.367  37,892.367   1.000
	398316.0   0.097   0.013  38,633.899  38,633.899   1.000
	337631.0   0.121   0.016  40,869.421  40,869.421   1.000
	295270.0   0.141   0.014  41,593.42  41,593.42   1.000
	214385.0   0.162   0.015  34,824.718  34,824.718   1.000
	212814.0   0.187   0.015  39,774.552  39,774.552   1.000
	340082.0   0.209   0.013  71,201.352  71,201.352   1.000
	478608.0   0.230   0.017  109,975.768  109,975.768   1.000
	589402.0   0.260   0.021  153,480.371  153,480.371   1.000
	728936.0   0.298   0.028  217,057.609  217,057.609   1.000
	1103067.0   0.349   0.031  385,510.01  385,510.01   1.000
	1624699.0   0.405   0.041  658,247.925  658,247.925   1.000
	2747900.0   0.490   0.050  1,347,322.548  1,347,322.548   1.000
	 */
	
	private double[] bounds = new double[] {
			0,		//0
			0.017, //1
			0.038,  //2
			0.055,  //3
			0.077,  //4
			0.097,  //5
			0.121,  //6
			0.141,  //7
			0.162,  //8
			0.187,  //9
			0.209,  //10
			0.230,  //11
			0.260,  //12
			0.298,  //13
			0.349,  //14
			0.405,  //15
			0.490,  //16
			0.500,	//17
			0.500,  //18
			0.500,  //19
			0.500,  //20
			0.500,  //21
			0.500,  //22
			0.500,  //23
			0.500,  //24
			0.500,  //25
			0.500,  //26
			0.500,  //27
			0.500,  //28
			0.500,  //29
			0.500,  //30
			0.500,  //31
			0.500,  //32
			0.500	//33
			};
	
	public Extension_WinCapPawn(IExtensionMode extensionMode, int updateInterval, int _accepted_score_diff) {
		super("WinCapPawnExtension", extensionMode, updateInterval, _accepted_score_diff);
	}
	
	@Override
	protected double getStaticRating(int level) {
		return bounds[level];
	}
	
	@Override
	protected int getObservationFrequency() {
		return 181;
	}
}

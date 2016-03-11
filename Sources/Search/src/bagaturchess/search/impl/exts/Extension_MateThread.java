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



public class Extension_MateThread extends ExtensionInfo {
	
	
	/**
	MateThread > MateThreadExtension -> , calc=10, rate: 0.13569131832797426, count_total: 527146, count_ration: 0.007848889469377097, STATS: 
	0.0      0.000   0.000   0.000   0.000   1.000
	19716.0   0.003   0.002  52.742  52.742   1.000
	6017.0   0.006   0.005  36.121  36.121   1.000
	9838.0   0.010   0.007  96.356  96.356   1.000
	19359.0   0.020   0.007  394.482  394.482   1.000
	26012.0   0.029   0.008  752.967  752.967   1.000
	26506.0   0.044   0.009  1,158.895  1,158.895   1.000
	26895.0   0.059   0.011  1,580.39  1,580.39   1.000
	28529.0   0.084   0.016  2,395.401  2,395.401   1.000
	31989.0   0.115   0.022  3,678.514  3,678.514   1.000
	40352.0   0.148   0.014  5,971.29  5,971.29   1.000
	49790.0   0.165   0.010  8,195.438  8,195.438   1.000
	52435.0   0.182   0.012  9,537.589  9,537.589   1.000
	58390.0   0.208   0.019  12,166.99  12,166.99   1.000
	52278.0   0.239   0.024  12,502.413  12,502.413   1.000
	41686.0   0.290   0.032  12,088.991  12,088.991   1.000
	37354.0   0.340   0.030  12,705.142  12,705.142   1.000
	 */
	
	private double[] bounds = new double[] {
			0,		//0
			0.003,  //1
			0.006,  //2
			0.010,  //3
			0.020,  //4
			0.029,  //5
			0.044,  //6
			0.059,  //7
			0.084,  //8
			0.115,  //9
			0.148,  //10
			0.165,  //11
			0.182,  //12
			0.208,  //13
			0.239,  //14
			0.290,  //15
			0.340,  //16
			0.350,	//17
			0.350,  //18
			0.350,  //19
			0.350,  //20
			0.350,  //21
			0.350,  //22
			0.350,  //23
			0.350,  //24
			0.350,  //25
			0.350,  //26
			0.350,  //27
			0.350,  //28
			0.350,  //29
			0.350,  //30
			0.350,  //31
			0.350,  //32
			0.350	//33
			};
	
	public Extension_MateThread(IExtensionMode extensionMode, int updateInterval, int _accepted_score_diff) {
		super("MateThreadExtension", extensionMode, updateInterval, _accepted_score_diff, true);
	}
	
	@Override
	protected double getStaticRating(int level) {
		return bounds[level];
	}
	
	@Override
	protected int getObservationFrequency() {
		return 9;
	}
}

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


public class Extension_WinCapNonPawn extends ExtensionInfo {
	
	
	/**
	WinCapNonPawn > WinCapNonPawnExtension -> , calc=16, rate: 0.4072484661548262, count_total: 17186733, count_ration: 0.255900201569766, STATS: 
	0.0      0.000   0.000   0.000   0.000   1.000
	1296068.0   0.044   0.015  56,569.628  56,569.628   1.000
	313923.0   0.053   0.006  16,554.283  16,554.283   1.000
	200168.0   0.061   0.007  12,135.321  12,135.321   1.000
	175317.0   0.068   0.008  11,980.576  11,980.576   1.000
	162175.0   0.076   0.007  12,337.593  12,337.593   1.000
	166615.0   0.084   0.008  14,000.564  14,000.564   1.000
	135723.0   0.091   0.007  12,353.235  12,353.235   1.000
	68353.0   0.098   0.010  6,685.787  6,685.787   1.000
	46414.0   0.112   0.014  5,187.828  5,187.828   1.000
	77475.0   0.131   0.017  10,153.7  10,153.7   1.000
	152049.0   0.157   0.021  23,829.079  23,829.079   1.000
	308778.0   0.196   0.033  60,624.788  60,624.788   1.000
	758962.0   0.250   0.041  190,114.639  190,114.639   1.000
	1986411.0   0.318   0.049  631,796.943  631,796.943   1.000
	4487662.0   0.389   0.047  1,747,152.164  1,747,152.164   1.000
	6850640.0   0.455   0.052  3,119,405.235  3,119,405.235   1.000
	 */
	
	private double[] bounds = new double[] {
			0,		//0
			0.044, //1
			0.053,  //2
			0.061,  //3
			0.068,  //4
			0.076,  //5
			0.084,  //6
			0.091,  //7
			0.098,  //8
			0.112,  //9
			0.131,  //10
			0.157,  //11
			0.196,  //12
			0.250,  //13
			0.318,  //14
			0.389,  //15
			0.455,  //16
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
	
	public Extension_WinCapNonPawn(IExtensionMode extensionMode, int updateInterval, int _accepted_score_diff) {
		super("WinCapNonPawnExtension", extensionMode, updateInterval, _accepted_score_diff);
	}
	
	@Override
	protected double getStaticRating(int level) {
		return bounds[level];
	}
	
	@Override
	protected int getObservationFrequency() {
		return 256;
	}
}

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



public class Extension_PasserPush extends ExtensionInfo {
	
	
	/**
	PasserPush > PasserPushExtension -> , calc=15, rate: 0.48514806169618746, count_total: 24999353, count_ration: 0.37222545272645674, STATS: 
	0.0      0.000   0.000   0.000   0.000   1.000
	558857.0   0.001   0.002  668.028  668.028   1.000
	115919.0   0.004   0.003  481.876  481.876   1.000
	60731.0   0.008   0.004  465.094  465.094   1.000
	40663.0   0.011   0.006  454.858  454.858   1.000
	45878.0   0.017   0.006  773.085  773.085   1.000
	55159.0   0.023   0.008  1,263.879  1,263.879   1.000
	70099.0   0.039   0.014  2,759.015  2,759.015   1.000
	146847.0   0.062   0.018  9,162.858  9,162.858   1.000
	314677.0   0.092   0.029  28,892.599  28,892.599   1.000
	536065.0   0.144   0.049  77,244.546  77,244.546   1.000
	949058.0   0.218   0.049  206,994.98  206,994.98   1.000
	1532000.0   0.282   0.046  432,743.747  432,743.747   1.000
	2332690.0   0.353   0.048  822,316.528  822,316.528   1.000
	4056065.0   0.432   0.054  1,751,265.967  1,751,265.967   1.000
	5997077.0   0.513   0.064  3,075,072.457  3,075,072.457   1.000
	8187568.0   0.613   0.074  5,020,182.567  5,020,182.567   1.000
	 */
	
	private double[] bounds = new double[] {
			0,		//0
			0.001, //1
			0.004,  //2
			0.008,  //3
			0.011,  //4
			0.017,  //5
			0.023,  //6
			0.039,  //7
			0.062,  //8
			0.092,  //9
			0.144,  //10
			0.218,  //11
			0.282,  //12
			0.353,  //13
			0.432,  //14
			0.513,  //15
			0.613,  //16
			0.650,	//17
			0.650,  //18
			0.650,  //19
			0.650,  //20
			0.650,  //21
			0.650,  //22
			0.650,  //23
			0.650,  //24
			0.650,  //25
			0.650,  //26
			0.650,  //27
			0.650,  //28
			0.650,  //29
			0.650,  //30
			0.650,  //31
			0.650,  //32
			0.650	//33
			};
	
	public Extension_PasserPush(IExtensionMode extensionMode, int updateInterval, int _accepted_score_diff) {
		super("PasserPushExtension", extensionMode, updateInterval, _accepted_score_diff);
	}
	
	@Override
	protected double getStaticRating(int level) {
		return bounds[level];
	}
	
	@Override
	protected int getObservationFrequency() {
		return 372;
	}
}

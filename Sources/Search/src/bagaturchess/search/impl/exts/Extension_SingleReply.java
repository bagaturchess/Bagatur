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


public class Extension_SingleReply extends ExtensionInfo {
	
	/**
	SingleReply > SingleReplyExtension -> , calc=16, rate: 0.6842105263157895, count_total: 11483, count_ration: 0.007489012354294264, STATS: 
	0.0      0.000   0.000   0.000   0.000   1.000
	3.0      0.377   0.078   1.130   1.130   1.000
	39.0     0.331   0.008  12.904  12.904   1.000
	91.0     0.345   0.019  31.390  31.390   1.000
	102.0    0.359   0.022  36.621  36.621   1.000
	94.0     0.369   0.015  34.685  34.685   1.000
	75.0     0.377   0.020  28.261  28.261   1.000
	60.0     0.384   0.014  23.031  23.031   1.000
	79.0     0.389   0.008  30.732  30.732   1.000
	83.0     0.394   0.012  32.711  32.711   1.000
	95.0     0.401   0.007  38.049  38.049   1.000
	138.0    0.408   0.009  56.326  56.326   1.000
	183.0    0.420   0.010  76.947  76.947   1.000
	235.0    0.437   0.012  102.754  102.754   1.000
	348.0    0.459   0.014  159.84  159.84   1.000
	1155.0   0.527   0.048  608.911  608.911   1.000
	8703.0   0.734   0.154  6,387.595  6,387.595   1.000
	 */
	
	private double[] bounds = new double[] {
			0.000,
			0.310,
			0.331,
			0.345,
			0.359,
			0.369,
			0.377,
			0.384,
			0.389,
			0.394,
			0.401,
			0.408,
			0.420,
			0.437,
			0.459,
			0.527,
			0.734
			};
	
	public Extension_SingleReply(IExtensionMode extensionMode, int updateInterval, int _accepted_score_diff) {
		super("SingleReplyExtension", extensionMode, updateInterval, _accepted_score_diff, true);
	}
	
	
	@Override
	protected double getStaticRating(int level) {
		return bounds[level];
	}
	
	@Override
	protected int getObservationFrequency() {
		return 10;
	}
}

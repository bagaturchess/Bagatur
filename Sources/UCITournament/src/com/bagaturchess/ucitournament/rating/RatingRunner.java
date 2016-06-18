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
package com.bagaturchess.ucitournament.rating;


import java.util.List;

import com.bagaturchess.ucitournament.framework.match.MatchRunner;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_FixedDepth;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_FixedNodes;


public class RatingRunner {
	
	public static void main(String[] args) {
		try {
			
			RatingWorkspace workspace = new RatingWorkspace(".");
			
			//MatchRunner match = new MatchRunner_FixedDepth(1);
			//MatchRunner match = new MatchRunner_FixedNodes(11111);
			MatchRunner match = new MatchRunner_FixedNodes(55555);
			//MatchRunner match = new MatchRunner_FixedNodes(33333);
			//MatchRunner match = new MatchRunner_TimePerMove(3000);
			//MatchRunner match = new MatchRunner_TimeAndInc(1 * 60 * 1000, 1 * 60 * 1000, 1 * 1000, 1 * 1000);
			//MatchRunner match = new MatchRunner_TimeAndInc(1 * 60 * 500, 1 * 60 * 500, 1 * 500, 1 * 500);
			//MatchRunner match = new MatchRunner_TimeAndInc(5000, 5000, 500, 500);
			
			IPlayingStrategy[] strategies = new IPlayingStrategy[] {new PlayingStrategy_MinPlayedGamesCount(workspace, match),
																	new PlayingStrategy_MaxELOMovingDir(workspace, match),
																	new PlayingStrategy_Random(workspace, match)
			};
			
			
			int counter = 1;
			while (true) {
				for (int i=0; i<strategies.length; i++) {
					
					for (int j=0; j<strategies[i].getWeight(); j++) {
						workspace.getLog().log("Games " + counter + ", " + (counter + 1) + " follows ... ");
						counter += 2;
						
						List<EngineMetaInf> enginesMetaInfs = StorageManager.loadEnginesMetaInf(workspace);
						strategies[i].selectEnginesAndPlay(enginesMetaInfs);
						StorageManager.storeEnginesMetaInf(workspace, enginesMetaInfs);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

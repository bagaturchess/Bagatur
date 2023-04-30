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


import java.io.IOException;
import java.util.List;

import com.bagaturchess.ucitournament.framework.match.MatchRunner;


public class PlayingStrategy_MaxELOMovingDir extends PlayingStrategy_BaseImpl {

	
	protected PlayingStrategy_MaxELOMovingDir(RatingWorkspace workspace, MatchRunner match) {
		super(workspace, match);
	}

	
	public void selectEnginesAndPlay(List<EngineMetaInf> enginesMetaInfs) throws IOException, InterruptedException {
		
		List<EngineMetaInf> selection = OperationsManager.get2EngineByMovingDir_max(enginesMetaInfs);
		
		EngineMetaInf e1 = null;
		EngineMetaInf e2 = null;
		while (e1 == null || e1.getName().equals(e2.getName())) {
			e1 = selection.get(rand(0, selection.size()));
			e2 = enginesMetaInfs.get(rand(0, enginesMetaInfs.size()));
		}
		
		EngineMetaInf[] pair = new EngineMetaInf[] {e1, e2};
		
		workspace.getLog().log("PlayingStrategy.MaxELOMovingDir->	" + Math.max(pair[0].getELOMovingDirection(), pair[1].getELOMovingDirection()));
		
		playPair(pair);
	}


	@Override
	protected String getName() {
		return "PlayingStrategy.MaxELOMovingDir";
	}
	
	public int getWeight() {
		return 1;
	}
}

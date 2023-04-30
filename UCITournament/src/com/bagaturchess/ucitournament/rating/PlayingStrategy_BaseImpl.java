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
import java.util.Random;

import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.EngineProcess_BagaturImpl_WorkspaceImpl;

import com.bagaturchess.ucitournament.framework.match.MatchRunner;


public abstract class PlayingStrategy_BaseImpl implements IPlayingStrategy {

	
	protected MatchRunner match;
	protected RatingWorkspace workspace;
	
	private Random random = new Random();
	
	protected PlayingStrategy_BaseImpl(RatingWorkspace _workspace, MatchRunner _match) {
		match = _match;
		workspace = _workspace;
	}
	
	
	protected abstract String getName();
	
	
	void playPair(EngineMetaInf[] pair) throws IOException, InterruptedException {

		EngineProcess white = new EngineProcess_BagaturImpl_WorkspaceImpl(pair[0].getName(), pair[0].getProgramArgs());
		EngineProcess black = new EngineProcess_BagaturImpl_WorkspaceImpl(pair[1].getName(), pair[1].getProgramArgs());
		
		white.start();
		Thread.sleep(5);
		black.start();
		
		/**
		 * Play first game
		 */
		workspace.getLog().log(getName() + "->	Pairing1:	" + white.getName() + "	vs.	" + black.getName());
		match.newGame();
		int result1 = match.execute(white, black);
		workspace.getLog().log(getName() + "->	Result1:	" + result1);
		
		
		/**
		 * Switch sides
		 */
		workspace.getLog().log(getName() + "->	Pairing2:	" + black.getName() + "	vs.	" + white.getName());
		match.newGame();
		int result2 = match.execute(black, white);
		workspace.getLog().log(getName() + "->	Result2:	" + result2);
		
		white.destroy();
		black.destroy();
		
		double overall_result = (result1 - result2) / (double)2;
		
		OperationsManager.adjustProperties(pair, overall_result, workspace, 2);
	}
	
	protected int rand(int from_idx_inclusive, int to_idx_exclusive) {
		if (to_idx_exclusive <= from_idx_inclusive) {
			throw new IllegalStateException();
		}
		
		int result = (int)((to_idx_exclusive - from_idx_inclusive) * random.nextDouble());
		return result;
	}
}

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
package bagaturchess.search.api.internal;

import bagaturchess.search.api.IEvaluator;


public class EvaluatorAdapter implements IEvaluator {

	public int getMaterialQueen() {
		return 900;
	}
	
	public void beforeSearch() {
		//throw new UnsupportedOperationException("Not implemented");
	}

	public int eval(int depth, int alpha, int beta, boolean pvNode, int rootColour) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public double fullEval(int depth, int alpha, int beta, int rootColour) {
		return eval(depth, alpha, beta, true, rootColour);
	}

	public int lazyEval(int depth, int alpha, int beta, int rootColour) {
		return eval(depth, alpha, beta, false, rootColour);
	}

	public int roughEval(int depth, int rootColour) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public int fastEval(int depth, int rootColour) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public int getMaterial(int pieceType) {
		throw new UnsupportedOperationException("Not implemented");
	}
}

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
package bagaturchess.bitboard.tests.pawnstructure;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import bagaturchess.bitboard.tests.pawnstructure.passers.Passers1;

public class RunAll {
	
	private static Collection<String> testcases = new ArrayList<String>();
	
	static {
		
		testcases.add(Passers1.class.getName());
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.passers.Passers2");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.passers.Passers3");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.passers.Passers4");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.passers.Passers5");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.passers.Passers51");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.passers.Passers6");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.passers.Passers7");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.passers.Passers8");
		
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.guards.Guards1");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.guards.Guards2");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.guards.Guards3");
		
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.storms.Storms1");
		
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.doubled.Doubled1");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.doubled.Doubled2");
		
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.backward.Backward1");
		
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.isolated.Isolated1");
		
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.supported.Supported1");
		
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.supported_cannotbe.CannotBeSupported1");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.supported_cannotbe.CannotBeSupported2");
		
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.islands.Islands1");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.islands.Islands2");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.islands.Islands3");
		
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.weakfields.Weak1");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.weakfields.Weak2");
		testcases.add("game.chess.engine.bitboard.eval.pawns.model.tests.weakfields.Weak3");
	}
	
	public static void main(String[] args) {
		//Passers1 p1 = new Passers1();
		for (String clazzName: testcases) {
			try {
				Class clazz = RunAll.class.getClassLoader().loadClass(clazzName);
				Object obj = clazz.newInstance();
				System.out.print("Executing " + clazz.getName() + " ... ");
				Method m = clazz.getMethod("validate", (Class[])null);
				m.invoke(obj, (Object[])null);
				System.out.println("OK");
			} catch (Exception e) {
				System.out.println("FAILED");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}

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
package bagaturchess.ucitracker.impl.gamemodel.serialization;


import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import bagaturchess.ucitracker.impl.gamemodel.EvaluatedGame;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedMove;
import bagaturchess.ucitracker.impl.gamemodel.EvaluatedPosition;


public class GameModelWriter {
	
	
	/*public static void writeListOfgames(List<EvaluatedGame> games, DataOutputStream dos) throws IOException {
		for (EvaluatedGame game: games) {
			writeEvaluatedGame(game, dos);
		}
	}*/
	
	
	public static void writeEvaluatedGame(EvaluatedGame game, DataOutputStream dos) throws IOException {
		
		int[] opening = game.getOpening();
		dos.writeInt(opening.length);
		
		for (int i=0; i<opening.length; i++) {
			dos.writeInt(opening[i]);
		}
		
		List<EvaluatedPosition> epositions = game.getBoardStates();
		dos.writeInt(epositions.size());
		
		for (EvaluatedPosition eposition: epositions) {
			writeEvaluatedPosition(eposition, dos);
		}
	}
	
	
	private static void writeEvaluatedPosition(EvaluatedPosition eposition, DataOutputStream dos) throws IOException {
		dos.writeInt(eposition.getOriginateMove());
		
		Set<EvaluatedMove> emoves = eposition.getChildren();
		dos.writeInt(emoves.size());
		
		for (EvaluatedMove emove: emoves) {
			writeEvaluatedMove(emove, dos);
		}
	}
	
	
	private static void writeEvaluatedMove(EvaluatedMove emove, DataOutputStream dos) throws IOException {
		dos.writeInt(emove.eval_ofOriginatePlayer());
		dos.writeInt(emove.getStatus().ordinal());
		
		int[] moves = emove.getMoves();
		
		dos.writeInt(moves.length);
		
		if (moves.length == 0) {
			throw new IllegalStateException();
		}
		
		for (int i=0; i<moves.length; i++) {
			dos.writeInt(moves[i]);
		}
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		//File root = new File("E:\\own\\Projects\\WS.Chess.New\\EvalCapture\\games\\rybka\\D8_all");
		//File root = new File("E:\\own\\Projects\\WS.Chess.New\\EvalCapture\\games\\glaurung\\");
		
		//File root = new File("./DATA/");
		
		/*DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("./DATA/Houdini.15a.captured"), 10 * 1024 * 1024));
		
		
		String[] names = collect(root);
		for (int i=0; i<names.length; i++) {
			long startTime = System.currentTimeMillis();
			System.out.print("Adding [" + i + "] " + names[i]);
			List<EvaluatedGame> games = GamesPersistency.load(names[i]);
			try {
				writeListOfgames(games, dos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long endTime = System.currentTimeMillis();
			System.out.println(" ... OK " + ((endTime - startTime) / 1000) + "sec");
		}
		
		try {
			dos.flush();
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}

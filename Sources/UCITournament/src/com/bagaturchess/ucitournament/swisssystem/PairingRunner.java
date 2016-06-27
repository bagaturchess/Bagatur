package com.bagaturchess.ucitournament.swisssystem;


import java.io.File;
import java.io.IOException;
import java.util.List;

import bagaturchess.uci.engine.EngineProcess;
import bagaturchess.uci.engine.EngineProcess_BagaturImpl_WorkspaceImpl;

import com.bagaturchess.ucitournament.framework.match.MatchRunner;
import com.bagaturchess.ucitournament.framework.match.MatchRunner_FixedNodes;


public class PairingRunner {

	
	public static void main(String[] args) {
		try {
			
			int GAMES = 18;
			
			SwissSystemWorkspace workspace = new SwissSystemWorkspace(".");
			
			File[] pairings = workspace.getPairingFiles();
			if (pairings.length == 0) {
				List<EngineMetaInf> engines = StorageManager.loadEnginesMetaInf(workspace);
				System.out.println("GEN PAIRING OF ROUND: " + (pairings.length + 1) + ", GAMES: " + (engines.size() / 2));
				Pairing pairing = PairingGenerator.gen(engines, true);
				StorageManager.storePairing(workspace, pairing, 1);
				pairings = workspace.getPairingFiles();
			}
			
			int counter = GAMES - pairings.length + 1;
			while (counter > 0) {
				
				pairings = workspace.getPairingFiles();
				List<EngineMetaInf> engines = StorageManager.loadEnginesMetaInf(workspace);
				
				System.out.println("PLAYING ROUND: " + pairings.length + ", GAMES: " + (engines.size() / 2));
				
				if (pairings.length == 0) {
					throw new IllegalStateException("No pairings");
				}
				
				Pairing last_pairing = StorageManager.loadPairing(pairings[pairings.length - 1]);
				//System.out.println(last_pairing);
				
				List<PairingEntry> entries = last_pairing.getEntries();
				for (int i=0; i<entries.size(); i++) {
					PairingEntry pairingEntry = entries.get(i);
					if (!pairingEntry.getResult().equals(PairingEntry.RESULT_NOT_AVAILABLE)) {
						continue;
					}
					
					
					//Play the game
					String engine_white = pairingEntry.getWhiteEngineName();
					String engine_black = pairingEntry.getBlackEngineName();
					PairMetaInf pair = getPair(engine_white, engine_black, engines);
					MatchRunner match = new MatchRunner_FixedNodes(100000);
					int result = playPair(pair, workspace, match);
					
					
					//Update model: games count, result, pairing
					pair.getWhite().setCount_white(pair.getWhite().getCount_white() + 1);
					pair.getBlack().setCount_black(pair.getBlack().getCount_black() + 1);
					switch (result) {
						case 1:
							pair.getWhite().setScores(pair.getWhite().getScores() + 2);
							pairingEntry.setResult("1-0");
							break;
						case 0:
							pair.getWhite().setScores(pair.getWhite().getScores() + 1);
							pair.getBlack().setScores(pair.getBlack().getScores() + 1);
							pairingEntry.setResult("1/2-1/2");
							break;
						case -1:
							pair.getBlack().setScores(pair.getBlack().getScores() + 2);
							pairingEntry.setResult("0-1");
							break;
						default:
							throw new IllegalStateException("result=" + result);
					}
					
					
					//Store result in pairing files and in engines meta-information file
					StorageManager.storeEnginesMetaInf(workspace, engines);
					StorageManager.storePairing(workspace, last_pairing, pairings.length);
					
					
					//throw new IllegalStateException("Not implemented");
				}
				
				counter--;
				
				if (counter == 0) {
					break;
				}
				
				//Generate the next pairing and start from the beginning
				System.out.println("GEN PAIRING OF ROUND: " + (pairings.length + 1) + ", GAMES: " + (engines.size() / 2));
				Pairing pairing = PairingGenerator.gen(engines, (pairings.length + 1) % 2 == 1);
				StorageManager.storePairing(workspace, pairing, pairings.length + 1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static PairMetaInf getPair(String white_name, String black_name, List<EngineMetaInf> engines) {
		
		EngineMetaInf white = null;
		EngineMetaInf black = null;
		for (EngineMetaInf engine: engines) {
			if (engine.getName().equals(white_name)) {
				white = engine;
			}
			if (engine.getName().equals(black_name)) {
				black = engine;
			}
		}
		
		if (white == null) {
			throw new IllegalStateException("Engine not found: " + white_name);
		}
		if (black == null) {
			throw new IllegalStateException("Engine not found: " + black_name);
		}
		
		return new PairMetaInf(white, black);
	}
	
	
	//returns 1,0 or -1
	private static int playPair(PairMetaInf pair, SwissSystemWorkspace workspace, MatchRunner match) throws IOException, InterruptedException {
		
		/*if (true) {
			int result = (int) Math.round(Math.random() * 3d);
			if (result <= 1) {
				return -1;
			} else if (result <= 2) {
				return 0;
			} else {
				return 1;
			}
		}*/
		
		EngineProcess white = new EngineProcess_BagaturImpl_WorkspaceImpl(pair.getWhite().getName(), pair.getWhite().getProgramArgs());
		EngineProcess black = new EngineProcess_BagaturImpl_WorkspaceImpl(pair.getBlack().getName(), pair.getBlack().getProgramArgs());
		
		white.start();
		Thread.sleep(5);
		black.start();
		
		/**
		 * Play game
		 */
		workspace.getLog().log("GAME: " + white.getName() + "	vs.	" + black.getName());
		match.newGame();
		int result = match.execute(white, black);
		workspace.getLog().log("RESULT: " + result);
		
		white.destroy();
		black.destroy();
		
		return result;
	}

}

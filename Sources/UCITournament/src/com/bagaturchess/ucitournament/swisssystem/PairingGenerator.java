package com.bagaturchess.ucitournament.swisssystem;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PairingGenerator {
	
	
	public static Pairing gen(List<EngineMetaInf> enginesMetaInfs, boolean odd_round) {
		
		if (enginesMetaInfs.size() < 2) {
			throw new IllegalStateException("Engines count is less than 2: " + enginesMetaInfs.size());
		}
		if (enginesMetaInfs.size() % 2 != 0) {
			throw new IllegalStateException("Engines count is not even: " + enginesMetaInfs.size());
		}
		
		List<PairMetaInf> pairing = tryCombinations(enginesMetaInfs);
		
		List<PairingEntry> pairingList = new ArrayList<PairingEntry>();
		for (PairMetaInf pair: pairing) {
			pairingList.add(new PairingEntry(pair.getWhite().getName(), pair.getBlack().getName()));
		}
		Pairing result = new Pairing(pairingList);
		
		return result;
	}
	
	
	private static List<PairMetaInf> tryCombinations(List<EngineMetaInf> enginesMetaInfs) {
		
		int best_eval = Integer.MIN_VALUE;
		List<PairMetaInf> best = null;
		
		for (int i=0; i<10000; i++) {
			List<EngineMetaInf> engines_copy = new ArrayList<EngineMetaInf>();
			engines_copy.addAll(enginesMetaInfs);
			Collections.shuffle(engines_copy);
			
			List<PairMetaInf> pairing = select(engines_copy);
			if (pairing != null) {
				int eval = evalList(pairing);
				if (eval > best_eval) {
					best_eval = eval;
					best = pairing;
				}
			}
		}
		
		evalError(best);
		
		return best;
	}


	private static List<PairMetaInf> select(List<EngineMetaInf> enginesMetaInfs) {
		
		List<PairMetaInf> pairs = genAllPairs(enginesMetaInfs);
		
		Map<EngineMetaInf, Set<PairMetaInf>> pairsByEngine = getPairsByEngineMap(pairs);
		
		List<PairMetaInf> result = new ArrayList<PairMetaInf>();
		Set<String> names = new HashSet<String>();
		for (EngineMetaInf engine: enginesMetaInfs) {
			
			if (names.contains(engine.getName())) {
				continue;
			}
			
			Set<PairMetaInf> engine_pairs = pairsByEngine.get(engine);
			PairMetaInf best = getBest(engine_pairs, names);
			if (best == null) {
				return null; //There is no solution found
				//best = getBest(engine_pairs, names);
				//throw new IllegalStateException("best is null");
			}
			
			names.add(best.getWhite().getName());
			names.add(best.getBlack().getName());
			result.add(best);
			
			if (names.size() == enginesMetaInfs.size()) {
				break;
			}
		}
		
		return result;
	}


	private static PairMetaInf getBest(Set<PairMetaInf> engine_pairs, Set<String> excluded) {
		
		PairMetaInf best = null;
		int best_eval = Integer.MIN_VALUE;
		
		for (PairMetaInf pair: engine_pairs) {
			
			if (!excluded.contains(pair.getWhite().getName()) && !excluded.contains(pair.getBlack().getName())) {
				
				int cur_eval = evalPair(pair);
				if (cur_eval > best_eval) {
					best_eval = cur_eval;
					best = pair;
				}
			}
		}
		
		return best;
	}


	private static Map<EngineMetaInf, Set<PairMetaInf>> getPairsByEngineMap(List<PairMetaInf> pairs) {
		Map<EngineMetaInf, Set<PairMetaInf>> pairsByEngine = new HashMap<EngineMetaInf, Set<PairMetaInf>>();
		for (PairMetaInf pair: pairs) {
			Set<PairMetaInf> pairSet_white = pairsByEngine.get(pair.getWhite());
			Set<PairMetaInf> pairSet_black = pairsByEngine.get(pair.getBlack());
			if (pairSet_white == null) {
				pairSet_white = new HashSet<PairMetaInf>();
				pairsByEngine.put(pair.getWhite(), pairSet_white);
			}
			if (pairSet_black == null) {
				pairSet_black = new HashSet<PairMetaInf>();
				pairsByEngine.put(pair.getBlack(), pairSet_black);
			}
			pairSet_white.add(pair);
			pairSet_black.add(pair);
		}
		return pairsByEngine;
	}
	
	
	
	private static List<PairMetaInf> genAllPairs(List<EngineMetaInf> enginesMetaInfs) {
		
		List<PairMetaInf> result = new ArrayList<PairMetaInf>();
		
		for (EngineMetaInf engine_white: enginesMetaInfs) {
			List<PairMetaInf> current = genAllPossiblePairs(engine_white, enginesMetaInfs);
			result.addAll(current);
		}
		
		return result;
	}
	
	
	private static List<PairMetaInf> genAllPossiblePairs(EngineMetaInf engine_white, List<EngineMetaInf> enginesMetaInfs) {
		
		List<PairMetaInf> result = new ArrayList<PairMetaInf>();
		
		for (EngineMetaInf engine_black: enginesMetaInfs) {
			
			if (!engine_white.getName().equals(engine_black.getName())) {
				
				if (!engine_white.getPaired().contains(engine_black.getName())) {
					
					if (engine_black.getPaired().contains(engine_white.getName())) {
						throw new IllegalStateException("Inconsistent pairing: " + engine_white.getName() + ", " + engine_black.getName());
					}
					
					PairMetaInf pair = new PairMetaInf(engine_white, engine_black);
					result.add(pair);	
				}
			}
		}
		
		return result;
	}
	
	
	private static int getProvokedDeltaColour(EngineMetaInf engine_white, EngineMetaInf engine_black) {
		int result = 0;
		
		result += Math.abs(engine_white.getCount_white() + 1 - engine_white.getCount_black());
		result += Math.abs(engine_black.getCount_black() + 1 - engine_black.getCount_white());
		
		return result;
	}
	
	
	private static int evalPair(PairMetaInf pair) {
		EngineMetaInf engine_white = pair.getWhite();
		EngineMetaInf engine_black = pair.getBlack();
		
		int cur_delta_scores = Math.abs(engine_white.getScores() - engine_black.getScores());
		
		int cur_delta_colours = getProvokedDeltaColour(engine_white, engine_black);
		
		int eval = -(cur_delta_scores + cur_delta_colours);
		
		return eval;
	}
	
	
	private static int evalList(List<PairMetaInf> pairs) {
		
		int eval = 0;
		
		for (PairMetaInf pair: pairs) { 
			eval += pair.getEval();
		}
		
		return eval;
	}
	
	
	private static void evalError(List<PairMetaInf> pairing) {
		int total_eval_colour = 0;
		int total_eval_score = 0;
		int score_violations = 0;
		for (PairMetaInf pair: pairing) {
			EngineMetaInf engine_white = pair.getWhite();
			EngineMetaInf engine_black = pair.getBlack();
			
			int cur_delta_scores = Math.abs(engine_white.getScores() - engine_black.getScores());
			if (cur_delta_scores > 0) {
				score_violations++;
			}
			int cur_delta_colours = getProvokedDeltaColour(engine_white, engine_black);
			
			total_eval_colour += -cur_delta_colours;
			total_eval_score += -cur_delta_scores;
		}
		System.out.println("total_eval_colour: " + total_eval_colour + ", total_eval_score: " + total_eval_score + ", score_violations: " + score_violations);
	}
}

package bagaturchess.engines.run.api;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl_SMP;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch;


public class RunAPI {
	
	
	public static IRootSearch createEngine() {
		IRootSearchConfig cfg = new RootSearchConfig_BaseImpl_SMP(
				new String[] {
								"bagaturchess.search.impl.alg.impl0.SearchMTD0",
								"bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD",								
								"bagaturchess.engines.bagatur.cfg.board.BoardConfigImpl",
								"bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2"
								}
				);
		
		IRootSearch search = new MTDSequentialSearch(new Object[] {cfg, null});
		
		return search;
	}
	

	private static String[] searchMove(IRootSearch engine, IRunAPIStatus status, String boardPositionFEN, int millis, int max_depth) {
		
		IBitBoard bitboard = new Board(boardPositionFEN, null, engine.getSharedData().getEngineConfiguration().getBoardConfig());		
		//System.out.println(bitboard);
		
		engine.newGame(bitboard);
		
		RunAPIMediator mediator = new RunAPIMediator(status, millis);
		engine.negamax(bitboard, mediator, 1, max_depth, true, null);
		
		while (!mediator.getStopper().isStopped() && !((RunAPIBestMoveSender) mediator.getBestMoveSender()).isInterrupted()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
		}
		
		int move = mediator.getLastInfo().getBestMove();
		
		String[] result = new String[2];
		result[0] = MoveInt.moveToString(move); 
		//result[0] = MoveInt.moveToStringUCI(move); 
		//result[0] = MoveInt.moveToStringOwn(move);
		
		bitboard.makeMoveForward(move);
		result[1] = bitboard.toEPD();
		
		return result;
	}
	
	
	public static String[] searchMove_byTime(IRootSearch engine, IRunAPIStatus status, String boardPositionFEN, int millis) {
		return searchMove(engine, status, boardPositionFEN, millis, 100);
	}
	
	//strength is between 1 and 10 -> 10 is the strongest
	//The search will be interrupted after 10 minutes
	public static String[] searchMove_byStrength(IRootSearch engine, IRunAPIStatus status, String boardPositionFEN, int strength) {
		if (strength < 1 || strength > 10) {
			throw new IllegalStateException("Depth must be a number between 1 and 10");
		}
		
		int searchTimeInSeconds = (int) Math.pow(2, strength);
		
		return searchMove(engine, status, boardPositionFEN, searchTimeInSeconds * 1000, 100);
	}
}

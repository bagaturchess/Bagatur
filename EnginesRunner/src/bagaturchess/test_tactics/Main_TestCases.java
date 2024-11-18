package bagaturchess.test_tactics;


import java.io.IOException;
import java.util.List;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_1Core;
import bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_SMP_Threads;
import bagaturchess.engines.cfg.base.TimeConfigImpl;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD;
import bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_NormalSearch;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.uci_adaptor.timemanagement.TimeControllerFactory;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.uci.impl.commands.Go;


public class Main_TestCases {
	
	
    public static void main(String[] args) throws IOException {
    	
		IRootSearchConfig cfg = new RootSearchConfig_BaseImpl_1Core(
			//new RootSearchConfig_BaseImpl_SMP_Threads(
				new String[] {
								bagaturchess.search.impl.alg.impl1.Search_PVS_NWS.class.getName(),
								
								bagaturchess.engines.cfg.base.SearchConfigImpl_AB.class.getName(),
								bagaturchess.learning.goldmiddle.pesto.cfg.BoardConfigImpl_PeSTO.class.getName(),
								bagaturchess.deeplearning.impl_nnue_v3.EvaluationConfig.class.getName(),
								//bagaturchess.learning.goldmiddle.pesto.cfg.BoardConfigImpl_PeSTO.class.getName(),
								//bagaturchess.learning.goldmiddle.pesto.cfg.EvaluationConfig_PeSTO.class.getName()
					}
				);
		
		ChannelManager.setChannel(new Channel_Console(System.in, System.out, System.out));		
		
    	List<ChessPuzzle> puzzles = Reader_TestCases.getTestCases("test-cases.epd");
    	System.out.println(puzzles.size() + " loaded test cases.");
    	
    	
    	int all_tests_count = 0;
    	int passed_tests_count = 0;
    	
   		Go go = new Go(ChannelManager.getChannel(), "go nodes 10000");
		
    	for (ChessPuzzle puzzle: puzzles) {
    		
    		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(puzzle.getFen());
    		
    		SharedData sharedData = new SharedData(ChannelManager.getChannel(), cfg);
    		
    		final IRootSearch search = new SequentialSearch_MTD(new Object[] {cfg, sharedData});
    		
    		search.createBoard(bitboard);
    		
    		final Object sync = new Object();
    		
    		ITimeController timeController = TimeControllerFactory.createTimeController(new TimeConfigImpl(), bitboard.getColourToMove(), go);
    		
    		final SearchMediator mediator = new SearchMediator(ChannelManager.getChannel(),
    				    				go,
    				    				timeController,
    				    				bitboard.getColourToMove(),
    				    				new BestMoveSender() {
    										@Override
    										public void sendBestMove() {
    											
    											System.out.println("MTDSchedulerMain: Best move send");
    								    		
    											search.stopSearchAndWait();
    								    		
    											search.shutDown();
    											
    								    		synchronized (sync) {
    								    			
    								    			sync.notifyAll();
    								    		}
    										}
    									},
    									
    									search, false);
    		
    		
    		search.negamax(bitboard, mediator, timeController, go);
    		
    		synchronized (sync) {
				
	    		try {
					
	    			sync.wait();
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
    		}
    		
    		
    		all_tests_count++;
    		
    		
    		int found_best_move = mediator.getBestMove();
    		
    		for (int expected_best_move: puzzle.getBestMoves()) {
    			
    			if (found_best_move == expected_best_move) {
    				
    				passed_tests_count++;
    				
    				break;
    			}
    		}
    	}
    	
    	try {
    		
			Thread.sleep(500);
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
    	
    	System.out.println("all_tests_count=" + all_tests_count + ", passed_tests_count=" + passed_tests_count);
    	
    	System.exit(0);
    }
    
    
    private static class SearchMediator extends UCISearchMediatorImpl_NormalSearch {

    	
    	private int best_move;
    	
		public SearchMediator(IChannel _channel, Go _go,
				ITimeController _timeController, int _colourToMove,
				BestMoveSender _sender, IRootSearch _rootSearch,
				boolean isEndlessSearch) {
			super(_channel, _go, _timeController, _colourToMove, _sender, _rootSearch,
					isEndlessSearch);
		}
		
		
		public void startIteration(int iteration) {
			
			super.startIteration(iteration);
		}
		
		
		@Override
		public void changedMajor(ISearchInfo info) {
			
			super.changedMajor(info);
			
			best_move = info.getBestMove();
		}
		
		
		public int getBestMove() {
			
			return best_move;
		}
    }
}

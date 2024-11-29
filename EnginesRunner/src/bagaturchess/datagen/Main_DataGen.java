package bagaturchess.datagen;


import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_1Core;
import bagaturchess.engines.cfg.base.TimeConfigImpl;
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.api.OpeningBookFactory;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.impl.env.MemoryConsumers;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.rootsearch.sequential.SequentialSearch_MTD;
import bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_NormalSearch;
import bagaturchess.search.impl.uci_adaptor.timemanagement.ITimeController;
import bagaturchess.search.impl.uci_adaptor.timemanagement.TimeControllerFactory;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.uci.impl.DummyPrintStream;
import bagaturchess.uci.impl.commands.Go;


public class Main_DataGen implements Runnable {
	
	
	private static final int THREADS_COUNT 			= 10;
	
	
	static {
		
    	MemoryConsumers.set_MEMORY_USAGE_PERCENT(1 / (double) (2 * THREADS_COUNT));
    	MemoryConsumers.set_STATIC_JVM_MEMORY(0);
		
		ChannelManager.setChannel(
				new Channel_Console(
						new ByteArrayInputStream(new byte[64]),
						new DummyPrintStream.DummyOS(),
						new DummyPrintStream()
					)
				);	
	}
	
	private static final int START_FILE_INDEX 		= 0;
	private static final int POSITIONS_PER_FILE 	= 1000000;
	private static final Object WRITE_SYNC 			= new Object();
	private static final int MAX_EVAL 				= 32000;
	
	private static final String OUTPUT_FILE_PREFIX 	= "C:/DATA/NNUE/plain/dataset";
	
	private static final int POSITIONS_PER_MOVE 	= 5000;
	
	private static final Go GO_COMMAND 				= new Go(ChannelManager.getChannel(), "go nodes " + POSITIONS_PER_MOVE);
	
	
	private static volatile int games 				= 0;
	private static volatile int positions 			= 0;
	
	
	private static OpeningBook ob;
	
	static {
		
		try {
			
			ob = OpeningBookFactory.load("./../Resources/bin/engine/ob/w.ob", "./../Resources/bin/engine/ob/b.ob");
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	private static final IRootSearchConfig cfg = new RootSearchConfig_BaseImpl_1Core(
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
	
	
    public static void main(String[] args) throws IOException {	

    	
		ExecutorService executor = Executors.newFixedThreadPool(THREADS_COUNT);
		
		
		List<Main_DataGen> generators = new ArrayList<Main_DataGen>();
		
		for (int i = 0; i < THREADS_COUNT; i++) {
			
			generators.add(new Main_DataGen());
		}
		
		for (int i = 0; i < THREADS_COUNT; i++) {
			
			executor.execute(generators.get(i));
		}	
    }
    
    
	@Override
	public void run() {
		
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
		
		while (true) {
			
			
			List<Integer> opening_moves = playRandomOpening(bitboard);
			
			
			final SharedData sharedData = new SharedData(ChannelManager.getChannel(), cfg);
			
			final IRootSearch search = new SequentialSearch_MTD(new Object[] {cfg, sharedData});
			
			search.createBoard(bitboard);
			
			
			List<Integer> moves = new ArrayList<Integer>();
			List<Integer> evals = new ArrayList<Integer>();
			
			boolean error_in_the_game = false;
			while (bitboard.getStatus().equals(IGameStatus.NONE)) {
				
				final Object sync = new Object();
	    		
	    		ITimeController timeController = TimeControllerFactory.createTimeController(new TimeConfigImpl(), bitboard.getColourToMove(), GO_COMMAND);
	    		
	    		final SearchMediator mediator = new SearchMediator(ChannelManager.getChannel(),
	    									GO_COMMAND,
	    				    				timeController,
	    				    				bitboard.getColourToMove(),
	    				    				new BestMoveSender() {
	    										@Override
	    										public void sendBestMove() {
	    											
	    											//System.out.println("MTDSchedulerMain: Best move send");
	    								    		
	    											search.stopSearchAndWait();
	    											
	    								    		synchronized (sync) {
	    								    			
	    								    			sync.notifyAll();
	    								    		}
	    										}
	    									},
	    									
	    									search, false);
	    		
	    		
	    		search.negamax(bitboard, mediator, timeController, GO_COMMAND);
	    		
	    		synchronized (sync) {
					
		    		try {
						
		    			sync.wait();
						
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
	    		}

	    		int found_best_move = mediator.getBestMove();
	    		if (found_best_move == 0) {
	    			error_in_the_game = true;
	    			break;
	    		}
	    		int found_best_eval = mediator.getBestEval();
	    		if (bitboard.getColourToMove() == Constants.COLOUR_BLACK) {
	    			found_best_eval = -found_best_eval;
	    		}
	    			
	    		bitboard.makeMoveForward(found_best_move);
	    		
	    		moves.add(found_best_move);
	    		evals.add(found_best_eval);
	    		//System.out.println(found_best_eval);
			}
			
			
			if (!error_in_the_game) {
				
				try {
					
					writeGame(bitboard, moves, evals);
				
				} catch(Exception e) {
					
					throw new RuntimeException(e);
				}
				
			} else {
				
				//System.out.println("error game");
			}
			
			
			//Revert moves
			for (int i = moves.size() - 1; i >=0; i--) {
				
				bitboard.makeMoveBackward(moves.get(i));
			}
			
			//Revert opening moves
			for (int i = opening_moves.size() - 1; i >=0; i--) {
				
				bitboard.makeMoveBackward(opening_moves.get(i));
			}
			
			
			search.shutDown();
		}
	}


	private void writeGame(IBitBoard bitboard, List<Integer> moves, List<Integer> evals) throws IOException {
		
		float result = getGameTerminationScore(bitboard.getStatus());
					
		//Revert moves
		for (int i = moves.size() - 1; i >=0; i--) {
			
			bitboard.makeMoveBackward(moves.get(i));
		}
		
		synchronized (WRITE_SYNC) {
			
			games++;
			
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(
							OUTPUT_FILE_PREFIX + "_"
							+ (START_FILE_INDEX + (positions / POSITIONS_PER_FILE)) + ".plain",
						true),
					16 * 1024);
			
			for (int i = 0; i < moves.size(); i++) {
				
				int move = moves.get(i);
				int eval = evals.get(i);
				
				if (/*!bitboard.getMoveOps().isCaptureOrPromotion(move)
						&&*/ Math.abs(eval) < MAX_EVAL) {
					
					positions++;
					
					String line = bitboard.toEPD() + " | " + eval + " | " + result;
					
					bw.write(line);
					bw.newLine();
					
					bw.flush();
				}
				
				bitboard.makeMoveForward(move);
			}
			
			bw.close();
		}
		
		
		System.out.println("games = " + games + ", status = " + bitboard.getStatus() + ", result = " + result + ", positions=" + positions);
	}


	private static List<Integer> playRandomOpening(IBitBoard bitboard) {
		
		List<Integer> moves = new ArrayList<Integer>();
		
		while (true) {
			
			IOpeningEntry entry = ob.getEntry(bitboard.getHashKey(), bitboard.getColourToMove());
			
			//System.out.println("entry=" + entry);
					
			if (entry == null) {
				
				break;
			}
			
			/*if (entry.getWeight() < OpeningBook.OPENING_BOOK_MIN_MOVES) {
				
				break;
			}*/
			
			//OPENING_BOOK_MODE_POWER2=most played first, OPENING_BOOK_MODE_POWER1=random intermediate, OPENING_BOOK_MODE_POWER0=random full
			int mode = OpeningBook.OPENING_BOOK_MODE_POWER0;
	
			int move = entry.getRandomEntry(mode);
			
			bitboard.makeMoveForward(move);
			
			moves.add(move);
		}
		
		return moves;
	}


	private static float getGameTerminationScore(IGameStatus status) {
		
		
		switch (status) {
		
			case NONE:
				throw new IllegalStateException("status=" + status);
				
			case DRAW_3_STATES_REPETITION:
				return 0.5f;
				
			case MATE_WHITE_WIN:
				return 1;
				
			case MATE_BLACK_WIN:
				return 0;
				
			case UNDEFINED:
				throw new IllegalStateException("status=" + status);
				
			case STALEMATE_WHITE_NO_MOVES:
				return 0.5f;
				
			case STALEMATE_BLACK_NO_MOVES:
				return 0.5f;
				
			case DRAW_50_MOVES_RULE:
				return 0.5f;
				
			case NO_SUFFICIENT_MATERIAL:
				return 0.5f;
				
			case PASSER_WHITE:
				throw new IllegalStateException("status=" + status);
				
			case PASSER_BLACK:
				throw new IllegalStateException("status=" + status);
				
			case NO_SUFFICIENT_WHITE_MATERIAL:
				throw new IllegalStateException("status=" + status);
				
			case NO_SUFFICIENT_BLACK_MATERIAL:
				throw new IllegalStateException("status=" + status);
				
			default:
				throw new IllegalStateException("status=" + status);
				
		}
	}
	
	
    private static class SearchMediator extends UCISearchMediatorImpl_NormalSearch {

    	
    	private int best_move;
    	private int best_eval;
    	
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
			
			if (!info.isUpperBound()) {
				
				best_move = info.getBestMove();
				best_eval = info.getEval();
			}
		}
		
		
		public int getBestMove() {
			
			return best_move;
		}
		
		
		public int getBestEval() {
			
			return best_eval;
		}
    }
}

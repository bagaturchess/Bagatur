package bagaturchess.search.impl.uci_adaptor;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.egtb.syzygy.OnlineSyzygy;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.utils.SearchUtils;


public class TimeSaver {
	
	
	private OpeningBook ob;
	
	
	private boolean ENABLE_TB_OFFLINE_PROBING_IN_ROOT_POSITIONS = false;
	
	
	public TimeSaver(OpeningBook _ob) {
		
		ob = _ob;
	}
	
	
	public boolean beforeMove(IBitBoard bitboardForSetup, int openningBook_Mode, final ISearchMediator mediator, boolean useOpening, boolean useOnlineSyzygy, long timeToThinkInMiliseconds) {
		
		mediator.dump("TimeSaver: useOpening = " + useOpening + ", ob=" + ob);
		
		//Search in the book
		if (useOpening && ob != null) {
			
			IOpeningEntry entry = ob.getEntry(bitboardForSetup.getHashKey(), bitboardForSetup.getColourToMove());
			
			if (entry != null && entry.getWeight() >= OpeningBook.OPENING_BOOK_MIN_MOVES) {
				
				int move = 0;
				switch (openningBook_Mode) {
				
					case OpeningBook.OPENING_BOOK_MODE_POWER0:
						move = entry.getRandomEntry(0);
						break;
						
					case OpeningBook.OPENING_BOOK_MODE_POWER1:
						move = entry.getRandomEntry(1);
						break;
						
					case OpeningBook.OPENING_BOOK_MODE_POWER2:
						move = entry.getRandomEntry(2);
						break;
						
					default:
						throw new IllegalStateException("openningBook_Mode=" + openningBook_Mode);
				}
				
				mediator.dump("TimeSaver: Opening move " + bitboardForSetup.getMoveOps().moveToString(move));
				
				ISearchInfo info = createInfo(move, 0);
				
				mediator.changedMajor(info);
				
				if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
				
				return true;
			}
		}
		
		
		mediator.dump("TimeSaver: bitboard.hasSingleMove() = " + bitboardForSetup.hasSingleMove());
		
		//Check if there is only one legal move
		if (bitboardForSetup.hasSingleMove()) {
			
			IMoveList list = new BaseMoveList();
			
			if (bitboardForSetup.isInCheck()) {
				int count = bitboardForSetup.genKingEscapes(list);
				if (count != 1) {
					throw new IllegalStateException();
				}
			} else {
				int count = bitboardForSetup.genAllMoves(list);
				if (count != 1) {
					throw new IllegalStateException();
				}
			}
			
			int move = list.reserved_getMovesBuffer()[0];
			
			mediator.dump("TimeSaver: Single reply move " + bitboardForSetup.getMoveOps().moveToString(move));
			
			ISearchInfo info = createInfo(move, 1);
			
			mediator.changedMajor(info);
			
			if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
			
			return true;
		}
		
		
		//Doesn't work well at the moment: plays correct most moves but does't do promotion move and make draw from winning games.
		if (bitboardForSetup.getMaterialState().getPiecesCount() <= 7) {
			
			
			//Try offline probing
			if (ENABLE_TB_OFFLINE_PROBING_IN_ROOT_POSITIONS) {
				
				mediator.dump("TimeSaver.OfflineSyzygy: offline probing with TBs on file system...");
				
				long[] result_long_pair = new long[2];
				
				SyzygyTBProbing.getSingleton().probeMove(bitboardForSetup, result_long_pair);
				
				long dtz = result_long_pair[0];
				
				mediator.dump("TimeSaver.OfflineSyzygy: dtz = " + dtz);
	    		
				if (dtz != -1) {
					
					int best_move = (int) result_long_pair[1];
					
					System.out.println("TimeSaver.OfflineSyzygy: Syzygy bestmove is " + bitboardForSetup.getMoveOps().moveToString(best_move));
					
					ISearchInfo info = createInfo(best_move, ISearch.MAX_DEPTH);
					
					info.setSelDepth(ISearch.MAX_DEPTH);
					
					int eval = SearchUtils.getMateVal(ISearch.MAX_DEPTH); //9 * ((100 - bitboardForSetup.getDraw50movesRule()) - dtz)
					
					info.setEval(eval);
					
					info.setBestMove(best_move);
						
					mediator.changedMajor(info);
					
					if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
					
					mediator.dump("TimeSaver.OfflineSyzygy: Syzygy move send.");
					
					return true;
					
				}
			}
			
			
			if (useOnlineSyzygy && bitboardForSetup.getEnpassantSquareID() == 0) {
				
				Runnable server_request_response_handler = new OnlineSyzygyServerHandler(bitboardForSetup, mediator);
				
				//Execute the Online Syzygy server request in parallel to the standard search
				new Thread(server_request_response_handler).start();
			}
		}
		
		
		return false;
	}
	
	
	private static ISearchInfo createInfo(int move, int depth) {
		ISearchInfo info = SearchInfoFactory.getFactory().createSearchInfo();
		info.setDepth(depth);
		info.setSelDepth(depth);
		info.setBestMove(move);
		info.setPV(new int[] {move});
		return info;
	}
	
	
	private class OnlineSyzygyServerHandler implements Runnable {
		
		
		private IBitBoard bitboardForSetup;
		
		private ISearchMediator mediator;
		
		
		OnlineSyzygyServerHandler(IBitBoard _bitboardForSetup, final ISearchMediator _mediator) {
			
			bitboardForSetup = _bitboardForSetup;
			
			mediator = _mediator;
		}
		
		
		@Override
		public void run() {
			
			mediator.dump("TimeSaver.OnlineSyzygy: EGTB Probing ...");
			
			long hashkey_before_request = bitboardForSetup.getHashKey();
			
			int[] result = new int[3];
			
			long start_time = System.currentTimeMillis();
			
			//OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection(bitboardForSetup, result);
			//int dtz = result[0];//Depth to zeroing-move. A zeroing-move is a move which resets the move count to zero under the fifty-move rule, i.e. mate, a capture, or a pawn move.
			//int dtm = result[1];//Depth to mate
			
			String server_response_json_text = OnlineSyzygy.getWDL_BlockingOnSocketConnection(bitboardForSetup, result, new OnlineSyzygy.Logger() {
				
				@Override
				public void addText(String message) {
					mediator.dump(message);
				}
				
				@Override
				public void addException(Exception exception) {
					mediator.dump(exception);
				}
			});
			
			long end_time = System.currentTimeMillis();
			
			mediator.dump("TimeSaver.OnlineSyzygy: url connection terminated in " + (end_time - start_time) + " ms");
			mediator.dump("TimeSaver.OnlineSyzygy: response from server:" + server_response_json_text);
			
			if (server_response_json_text != null) {
				
				if (result[0] != -1 && result[1] != -1 && result[2] != -1) {
					
					mediator.dump("TimeSaver.OnlineSyzygy: EGTB probing was sucessfull");
					
					int dtz = result[0];//Depth to zeroing-move. A zeroing-move is a move which resets the move count to zero under the fifty-move rule, i.e. mate, a capture, or a pawn move.
					int winner = result[1];//Winner's color or -1 if the position has not found
					int best_move = result[2];//Winner's color or -1 if the position has not found
						
					mediator.dump("TimeSaver.OnlineSyzygy: winner=" + winner + ", dtz=" + dtz + ", best_move=" + bitboardForSetup.getMoveOps().moveToString(best_move));
					
					long hashkey_after_response = bitboardForSetup.getHashKey();
					
					//Has the search already made a move on the board or not?
					if (hashkey_after_response == hashkey_before_request
								&& !mediator.getStopper().isStopped()
							) {
						
						ISearchInfo info = createInfo(best_move, 1);
						
						int eval = SearchUtils.getMateVal(ISearch.MAX_DEPTH);
						
						info.setEval(eval);
						
						info.setBestMove(best_move);
						
						mediator.changedMajor(info);
						
						if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
						
						mediator.dump("TimeSaver.OfflineSyzygy: EGTB probing ok - syzygy move send to UCI: " + bitboardForSetup.getMoveOps().moveToString(best_move));
						
					} else {
						
						mediator.dump("TimeSaver.OfflineSyzygy: Syzygy move NOT send to UCI, because hashkey_after_response != hashkey_before_request or mediator.getStopper().isStopped() is true,"
										+ " which means the bestmove is already made by the search");
					}
					
				} else {
				
					mediator.dump("TimeSaver.OnlineSyzygy: EGTB probing ok - winner is still unknown.");
				}
				
			} else {
				
				mediator.dump("TimeSaver.OnlineSyzygy: EGTB probing failed - unable to get meaningful json response from the server.");
			}
		}
	}
}

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
import bagaturchess.search.impl.alg.SearchUtils;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.tpt.ITTEntry;
import bagaturchess.search.impl.tpt.ITTable;
import bagaturchess.search.impl.tpt.TTEntry_BaseImpl;


public class TimeSaver {
	
	
	//Doesn't work well at the moment: plays correct most moves but does't do promotion move and make draw out of winning games.
	private static final boolean ENABLE_TB_OFFLINE_PROBING_IN_ROOT_POSITIONS 	= false;
	
	private static final boolean ENABLE_TB_ONLINE_PROBING_IN_ROOT_POSITIONS 	= true;
	
	private static int stats_online_syzygy_calls 								= 0;
	
	private static int stats_online_syzygy_wins 								= 0;
	
	private static int stats_online_syzygy_draws 								= 0;
	
	
	private OpeningBook ob;
	
	private IUCISearchAdaptor_Extension search_adapter;
	
	
	public TimeSaver(IUCISearchAdaptor_Extension _search_stopper, OpeningBook _ob) {
		
		search_adapter = _search_stopper;
		
		ob = _ob;
	}
	
	
	public boolean beforeMove(IBitBoard bitboardForSetup, int openningBook_Mode, final ISearchMediator mediator, boolean useOpening, boolean uci_option_UseOnlineSyzygy, long timeToThinkInMiliseconds) {
		
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
		
		
		if (bitboardForSetup.getMaterialState().getPiecesCount() <= 7) {
			
			//Try offline probing for the current position
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
			
			
			//Try online probing for the current position
			if (ENABLE_TB_ONLINE_PROBING_IN_ROOT_POSITIONS && uci_option_UseOnlineSyzygy) {
				
				//server currently dosesn't support enpassant move in FEN signature and returns error
				if (bitboardForSetup.getEnpassantSquareID() == 0) {
					
					//Runnable server_request_response_handler = new OnlineSyzygyServerHandler_WDL(bitboardForSetup, mediator);
					Runnable server_request_response_handler = new OnlineSyzygyServerHandler_DTM_DTZ(bitboardForSetup, mediator, timeToThinkInMiliseconds);
					
					//Execute the Online Syzygy server request in parallel to the standard search
					new Thread(server_request_response_handler).start();
				}
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
	
	
	private class OnlineSyzygyServerHandler_DTM_DTZ implements Runnable {
		
		
		private IBitBoard bitboardForSetup;
		
		private ISearchMediator mediator;
		
		private long timeToThinkInMiliseconds;
		
		
		OnlineSyzygyServerHandler_DTM_DTZ(IBitBoard _bitboardForSetup, final ISearchMediator _mediator, long _timeToThinkInMiliseconds) {
			
			bitboardForSetup = _bitboardForSetup;
			
			mediator = _mediator;
			
			timeToThinkInMiliseconds = _timeToThinkInMiliseconds;
			
			mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): timeToThinkInMiliseconds = " + timeToThinkInMiliseconds);
		}
		
		
		@Override
		public void run() {
			
			mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): EGTB Probing ...");
			
			mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): PROBING STATISTICS [server_calls=" + stats_online_syzygy_calls
							+ ", wins=" + stats_online_syzygy_wins
							+ ", draws=" + stats_online_syzygy_draws
							+ "]");
			
			//We have to keep hashkey and color to move, because board object may change (if move is made).
			long hashkey_before_server_request = bitboardForSetup.getHashKey();
			int colour_to_move = bitboardForSetup.getColourToMove();
			int distanceToDraw_50MoveRule = 100 - bitboardForSetup.getDraw50movesRule();
			
			long start_time = System.currentTimeMillis();
			
			
			//Make server request
			String fen = bitboardForSetup.toEPD().replace(' ', '_');
			
			int[] dtz_and_dtm = new int[2];
			
			String bestmove_string = OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection(fen, colour_to_move, timeToThinkInMiliseconds, dtz_and_dtm, new OnlineSyzygy.Logger() {
				
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
			
			mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): url connection terminated in " + (end_time - start_time) + " ms");
			
			stats_online_syzygy_calls++;
			
			//Make server move (if possible)
			if (bestmove_string != null) {
				
				try {
					
					if (bitboardForSetup.getHashKey() == hashkey_before_server_request) {
					
						int best_move = bitboardForSetup.getMoveOps().stringToMove(bestmove_string);
					
						int dtz = dtz_and_dtm[0]; //Distance/Depth to zeroing-move. A zeroing-move is a move which resets the move count to zero under the fifty-move rule, i.e. mate, a capture, or a pawn move.
						int dtm = dtz_and_dtm[1]; //Distance/Depth to mate.
						
						mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): dtm=" + dtm
								+ ", best_move=" + bestmove_string + ", dtz=" + dtz);
						
						if (!mediator.getStopper().isStopped()) {
							
							ISearchInfo info = createInfo(best_move, ISearch.MAX_DEPTH);
							
							info.setSelDepth(ISearch.MAX_DEPTH);
							
							info.setBestMove(best_move);
							
							boolean send_move = false;
							
							if (dtm > 0) {
								
								if (distanceToDraw_50MoveRule >= dtm) {
									
									//We have a winner
									info.setEval(SearchUtils.getMateVal(dtm));
									
									send_move = true;
									
									mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): EGTB probing ok - score is mate in " + dtm + ", eval= " + info.getEval());
									
									stats_online_syzygy_wins++;
									
								} else {
									
									final int DRAW_SCORE = SearchUtils.getDrawScores(bitboardForSetup.getMaterialFactor(), -1);
									
									//The game is draw in the best case and we go for it
									info.setEval(DRAW_SCORE);
									
									send_move = true;
									
									mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): EGTB probing ok - score is mate in " + dtm + ", but is draw because of 50 moves rule, eval= " + info.getEval());
									
									stats_online_syzygy_draws++;
								}

								
							} else {
								
								ITTable transposition_table = ((UCISearchAdaptorImpl_Base) search_adapter).getSharedData().getTranspositionTableProvider().getTPT();
								
								ITTEntry tt_entry = new TTEntry_BaseImpl();
								
								transposition_table.get(hashkey_before_server_request, tt_entry);
								
								if (!tt_entry.isEmpty() && tt_entry.getDepth() >= 7) {
									
									/*if (tt_entry.getFlag() == ITTEntry.FLAG_EXACT
											|| tt_entry.getFlag() == ITTEntry.FLAG_UPPER
											) {*/

										final int DRAW_SCORE = SearchUtils.getDrawScores(bitboardForSetup.getMaterialFactor(), -1);
										
										//Use online Syzygy if the position evaluation is below this number:
										final int ONLINE_PROBING_EVAL_THRESHOLD = DRAW_SCORE; //-50; //-1; //May be make it a UCI option?
										
										if (tt_entry.getEval() <= ONLINE_PROBING_EVAL_THRESHOLD) {
											
											//The game is draw in the best case and we go for it
											info.setEval(DRAW_SCORE);
											
											send_move = true;
											
											mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): EGTB probing ok - score is " + DRAW_SCORE + " (draw)");
											
											stats_online_syzygy_draws++;
										}
									//}
								}
							}
							
							if (send_move) {
								
								mediator.getStopper().markStopped();
								
								mediator.changedMajor(info);
								
								mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): EGTB probing ok - syzygy move "
												+ bitboardForSetup.getMoveOps().moveToString(best_move)
												+ " set and search is marked for stopping.");
							}
							
						} else {
							
							mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): Syzygy move NOT send to UCI, because mediator.getStopper().isStopped() is true, which means the search has over.");
						}
						
					} else {
						
						mediator.dump("OnlineSyzygy.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): Syzygy move NOT send to UCI, because bitboardForSetup.getHashKey() == hashkey_before_server_request, which means the best move is already made by the search.");
					}
					
				} catch (NumberFormatException nfe) {
					
					mediator.dump(nfe);
				}
				
			} else {
				
				mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_DTM_DTZ): EGTB probing failed - unable to get meaningful json response from the server.");
			}
		}
	}
	
	
	private class OnlineSyzygyServerHandler_WDL implements Runnable {
		
		
		private IBitBoard bitboardForSetup;
		
		private ISearchMediator mediator;
		
		private long timeToThinkInMiliseconds;
		
		
		OnlineSyzygyServerHandler_WDL(IBitBoard _bitboardForSetup, final ISearchMediator _mediator, long _timeToThinkInMiliseconds) {
			
			bitboardForSetup = _bitboardForSetup;
			
			mediator = _mediator;
			
			timeToThinkInMiliseconds = _timeToThinkInMiliseconds;
		}
		
		
		@Override
		public void run() {
			
			mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_WDL): EGTB Probing ...");
			
			//We have to keep hashkey and color to move, because board object may change (if move is made).
			long hashkey_before_server_request = bitboardForSetup.getHashKey();
			int colour_to_move = bitboardForSetup.getColourToMove();
			
			long start_time = System.currentTimeMillis();
			
			
			//Make server request
			String fen = bitboardForSetup.toEPD().replace(' ', '_');
			
			int[] winner_and_dtz = new int[2];
			
			String bestmove_string = OnlineSyzygy.getWDL_BlockingOnSocketConnection(fen, colour_to_move, timeToThinkInMiliseconds, winner_and_dtz, new OnlineSyzygy.Logger() {
				
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
			
			mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_WDL): url connection terminated in " + (end_time - start_time) + " ms");
			
			
			//Make server move (if possible)
			if (bestmove_string != null) {
				
				try {
					
					if (bitboardForSetup.getHashKey() == hashkey_before_server_request) {
					
						int best_move = bitboardForSetup.getMoveOps().stringToMove(bestmove_string);
					
						int winner = winner_and_dtz[0]; //Winner's color or -1 if the position has not found
						int dtz = winner_and_dtz[1]; //Distance/Depth to zeroing-move. A zeroing-move is a move which resets the move count to zero under the fifty-move rule, i.e. mate, a capture, or a pawn move.
						
						mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_WDL): winner=" + winner + ", best_move=" + bitboardForSetup.getMoveOps().moveToString(best_move) + ", dtz=" + dtz);
						
						if (!mediator.getStopper().isStopped()) {
							
							ISearchInfo info = createInfo(best_move, ISearch.MAX_DEPTH);
							
							info.setSelDepth(ISearch.MAX_DEPTH);
							
							int eval = SearchUtils.getMateVal(ISearch.MAX_DEPTH);
							
							info.setEval(eval);
							
							info.setBestMove(best_move);
							
							mediator.getStopper().markStopped();
							
							mediator.changedMajor(info);
							
							mediator.dump("TimeSaver.OfflineSyzygy (OnlineSyzygyServerHandler_WDL): EGTB probing ok - syzygy move "
											+ bitboardForSetup.getMoveOps().moveToString(best_move)
											+ " set and search is marked for stopping.");
							
						} else {
							
							mediator.dump("TimeSaver.OfflineSyzygy (OnlineSyzygyServerHandler_WDL): Syzygy move NOT send to UCI, because mediator.getStopper().isStopped() is true, which means the search has over.");
						}
						
					} else {
						
						mediator.dump("OnlineSyzygy.OfflineSyzygy (OnlineSyzygyServerHandler_WDL): Syzygy move NOT send to UCI, because bitboardForSetup.getHashKey() == hashkey_before_server_request, which means the best move is already made by the search.");
					}
					
				} catch (NumberFormatException nfe) {
					
					mediator.dump(nfe);
				}
				
			} else {
				
				mediator.dump("TimeSaver.OnlineSyzygy (OnlineSyzygyServerHandler_WDL): EGTB probing failed - unable to get meaningful json response from the server.");
			}
		}
	}
}

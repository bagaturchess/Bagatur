package bagaturchess.search.impl.uci_adaptor;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.egtb.syzygy.OnlineSyzygy;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.info.SearchInfoFactory;
import bagaturchess.search.impl.utils.SearchUtils;


public class TimeSaver {
	
	
	private OpeningBook ob;
	
	
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
		
		
		if (bitboardForSetup.getMaterialState().getPiecesCount() <= 7) {
			
			//Try offline probing
			
			mediator.dump("TimeSaver.OfflineSyzygy: offline probing with TBs on file system...");
			
			long[] result_long_pair = new long[2];
			
			SyzygyTBProbing.getSingleton().probeMove(bitboardForSetup, result_long_pair);
			
			long dtz = result_long_pair[0];
			
			mediator.dump("TimeSaver.OfflineSyzygy: dtz = " + dtz);
			
			if (dtz != -1) {
				
				int best_move = (int) result_long_pair[1];
				
				System.out.println("TimeSaver.OfflineSyzygy: Syzygy bestmove is " + bitboardForSetup.getMoveOps().moveToString(best_move));
				
				ISearchInfo info = createInfo(best_move, (int) dtz);
				
				int eval = SearchUtils.getMateVal((int) Math.abs(dtz));
				
				info.setEval(eval);
				
				info.setBestMove(best_move);
				
				mediator.changedMajor(info);
				
				if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
				
				mediator.dump("TimeSaver.OfflineSyzygy: Syzygy move send.");
				
				return true;
				
			} else if (useOnlineSyzygy) {
				
				//Try online probing
				//We must have enough time to get response from the server
				if (timeToThinkInMiliseconds >= 500
						&& bitboardForSetup.getMaterialState().getPiecesCount() <= 7
						) {
					
					mediator.dump("TimeSaver.OnlineSyzygy: EGTB Probing ...");
					
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
						
						if (result[2] != -1) {
							
							mediator.dump("TimeSaver.OnlineSyzygy: EGTB probing was sucessfull");
							
							dtz = result[0];//Depth to zeroing-move. A zeroing-move is a move which resets the move count to zero under the fifty-move rule, i.e. mate, a capture, or a pawn move.
							int winner = result[1];//Winner's color or -1 if the position has not found
							int best_move = result[2];//Winner's color or -1 if the position has not found
								
							mediator.dump("TimeSaver.OnlineSyzygy: winner=" + winner + ", dtz=" + dtz + ", best_move=" + bitboardForSetup.getMoveOps().moveToString(best_move));
							
							ISearchInfo info = createInfo(best_move, (int) dtz);
							
							int eval = SearchUtils.getMateVal((int) Math.abs(dtz));
							
							info.setEval(eval);
							
							info.setBestMove(best_move);
							
							mediator.changedMajor(info);
							
							if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
							
							mediator.dump("TimeSaver.OfflineSyzygy: Syzygy move send.");
							
							return true;
							
						} else {
						
							mediator.dump("TimeSaver.OnlineSyzygy: EGTB probing ok - winner is still unknown.");
						}
						
					} else {
						
						mediator.dump("TimeSaver.OnlineSyzygy: EGTB probing failed - unable to get meaningful json response from the server.");
					}
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
	
	
	/*protected void setupBoard(IBitBoard _bitboardForSetup) {
		bitboard.revert();
		
		int movesCount = _bitboardForSetup.getPlayedMovesCount();
		int[] moves = _bitboardForSetup.getPlayedMoves();
		for (int i=0; i<movesCount; i++) {
			bitboard.makeMoveForward(moves[i]);
		}
	}
	
	
	private int getOpeningMove_Evaluation(IOpeningEntry entry, SharedData sharedData, IBitBoard bitboardForSetup, ISearchMediator mediator) {
		
		setupBoard(bitboardForSetup);
		
		IEvaluator eval = sharedData.getEvaluatorFactory().create(bitboard, sharedData.getAndRemoveEvalCache(), sharedData.getEngineConfiguration().getEvalConfig());
		
		double best_val = Integer.MIN_VALUE;
		int best_move = 0;
		
		int[] moves = entry.getMoves();
		for (int i=0; i<moves.length; i++) {
			
			int cur_move = moves[i];
			
			bitboard.makeMoveForward(cur_move);
			
			IOpeningEntry cur_entry = ob.getEntry(bitboard.getHashKey(), bitboard.getColourToMove());
			double cur_val = -minmaxOpening(cur_entry, bitboard, eval, 0);
			
			if (cur_val > best_val) {
				best_val = cur_val;
				best_move = cur_move;
			}
			
			bitboard.makeMoveBackward(cur_move);
			
			mediator.dump("Opening move candidate: " + MoveInt.moveToString(cur_move) + ", eval is " + cur_val);
		}
		
		return best_move;
	}
	
	
	private double minmaxOpening(IOpeningEntry entry, IBitBoard bitboard, IEvaluator eval, int depth) {
		
		if (entry == null) {
			return eval.fullEval(0, Integer.MIN_VALUE, Integer.MAX_VALUE, bitboard.getColourToMove());
		}
		
		if (bitboard.getStateRepetition() >= 2) {
			return 0;
		}
		
		if (MoveInt.isCapture(bitboard.getLastMove())) {
			//Do nothing
		} else {
			if (depth > 5) {
				return eval.fullEval(0, Integer.MIN_VALUE, Integer.MAX_VALUE, bitboard.getColourToMove());
			}
		}
		
		int[] moves = entry.getMoves();
		
		double best_val = Integer.MIN_VALUE;
		for (int i=0; i<moves.length; i++) {
			
			int cur_move = moves[i];
			
			bitboard.makeMoveForward(cur_move);
			
			IOpeningEntry cur_entry = ob.getEntry(bitboard.getHashKey(), bitboard.getColourToMove());
			
			double cur_val = -minmaxOpening(cur_entry, bitboard, eval, depth + 1);
			
			if (cur_val > best_val) {
				best_val = cur_val;
			}
			
			bitboard.makeMoveBackward(cur_move);
		}
		
		return best_val;
	}
	
	*/
}

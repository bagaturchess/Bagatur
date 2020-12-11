/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.search.impl.alg.impl2;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.IMoveList;


/*
  enum Stages {
    MAIN_TT, CAPTURE_INIT, GOOD_CAPTURE, REFUTATION, QUIET_INIT, QUIET, BAD_CAPTURE,
    EVASION_TT, EVASION_INIT, EVASION,
    PROBCUT_TT, PROBCUT_INIT, PROBCUT,
    QSEARCH_TT, QCAPTURE_INIT, QCAPTURE, QCHECK_INIT, QCHECK
  };
 */
public class MovePicker implements IMoveList {
	
	
	private static final int STAGE_MAIN_TT = 1;
	private static final int STAGE_CAPTURE_INIT = 2;
	private static final int STAGE_GOOD_CAPTURE = 3;
	private static final int STAGE_REFUTATION = 4;
	private static final int STAGE_QUIET_INIT = 5;
	private static final int STAGE_QUIET = 6;
	private static final int STAGE_BAD_CAPTURE = 7;
	private static final int STAGE_EVASION_TT = 8;
	private static final int STAGE_EVASION_INIT = 9;
	private static final int STAGE_EVASION = 10;
	private static final int STAGE_PROBCUT_TT = 11;
	private static final int STAGE_PROBCUT_INIT = 12;
	private static final int STAGE_PROBCUT = 13;
	private static final int STAGE_QSEARCH_TT = 14;
	private static final int STAGE_QCAPTURE_INIT = 15;
	private static final int STAGE_QCAPTURE = 16;
	private static final int STAGE_QCHECK_INIT = 17;
	private static final int STAGE_QCHECK = 18;
	
	
	private int[][] PieceValue = {
			  { 0, 136, 782, 830, 1289, 2529 },
			  { 0, 208, 865, 918, 1378, 2687 }
			};
	
	IBitBoard board;
	ButterflyHistory mainHistory;
	CapturePieceToHistory captureHistory;
	PieceToHistory continuationHistory0;
	PieceToHistory continuationHistory1;
	PieceToHistory continuationHistory2;
	
	long refutations[] = new long[3];
	int ttMove;
	int stage;
	int recaptureSquare;
	int threshold;
	long[] moves_quiet = new long[256];
	long[] moves_capture_promotion_good = new long[256];
	long[] moves_capture_promotion_bad = new long[256];
	int cur_quiet, cur_capture_promotion_good, cur_capture_promotion_bad;
	int max_quiet, max_capture_promotion_good, max_capture_promotion_bad;
	
	
	// MovePicker constructor for ProbCut: we generate captures with SEE greater than or equal to the given threshold.
	public void init(IBitBoard _board, int _ttMove, int beta,
			CapturePieceToHistory _captureHistory) {
		
		board = _board;
		
		mainHistory = null;
		captureHistory = _captureHistory;
		continuationHistory0 = null;
		continuationHistory1 = null;
		continuationHistory2 = null;
		
		threshold = beta;
		
		assert(!board.isInCheck());
		
		stage = STAGE_PROBCUT_TT;
		ttMove = _ttMove != 0
		          && board.isPossible(_ttMove)
		          && board.getMoveOps().isCapture(_ttMove)
		          && board.getSEEScore(_ttMove) >= 0 ? _ttMove : 0;
		stage += (ttMove == 0) ? 1 : 0;
	}
	
	             
	//MovePicker constructor for the main search
	public void init(IBitBoard _board, int _ttMove,
			ButterflyHistory _mainHistory, CapturePieceToHistory _captureHistory,
			PieceToHistory _continuationHistory0, PieceToHistory _continuationHistory1, PieceToHistory _continuationHistory2,
			int counterMove, int[] killers) {
		
		board = _board;
		
		mainHistory = _mainHistory;
		captureHistory = _captureHistory;
		continuationHistory0 = _continuationHistory0;
		continuationHistory1 = _continuationHistory1;
		continuationHistory2 = _continuationHistory2;
		
		threshold = 0;
		
		assert(!board.isInCheck());
		
		stage = STAGE_MAIN_TT;
		ttMove = _ttMove != 0 && board.isPossible(_ttMove) ? _ttMove : 0;
		stage += (ttMove == 0 ? 1 : 0);
	}
	
	
	public int next_move() {
		
		  switch (stage) {
		  	  
			  case STAGE_MAIN_TT:
			  case STAGE_EVASION_TT:
			  case STAGE_QSEARCH_TT:
			  case STAGE_PROBCUT_TT:
			      ++stage;
			      if (ttMove != 0) {
			    	  return ttMove;
			      }
	
			  case STAGE_CAPTURE_INIT:
			  case STAGE_PROBCUT_INIT:
			  case STAGE_QCAPTURE_INIT:
			      
				  cur_capture_promotion_good = 0;
			      max_capture_promotion_good = 0;
			      
				  cur_capture_promotion_bad = 0;
			      max_capture_promotion_bad = 0;
			      
				  cur_quiet = 0;
			      max_quiet = 0;
			      
			      if (board.isInCheck()) {
			    	  board.genKingEscapes(this);
			      } else {
			    	  board.genCapturePromotionMoves(this);
			      }
			      
			      Utils.randomize(moves_capture_promotion_good, cur_capture_promotion_good, max_capture_promotion_good);
				  Utils.bubbleSort(cur_capture_promotion_good, max_capture_promotion_good, moves_capture_promotion_good);
				  
			      ++stage;
			      //break top;
	
			  case STAGE_GOOD_CAPTURE:
				  
				  for (int i=cur_capture_promotion_good; i < max_capture_promotion_good; i++) {
					  if (board.getSEEScore((int) moves_capture_promotion_good[i]) >= threshold) {
						  cur_capture_promotion_good++;
						  return (int) moves_capture_promotion_good[i];
					  }
					  cur_capture_promotion_good++;
				  }
				  
				  ++stage;
				  
				  /*
			      // Prepare the pointers to loop over the refutations array
			      cur = std::begin(refutations);
			      endMoves = std::end(refutations);
	
			      // If the countermove is the same as a killer, skip it
			      if (   refutations[0].move == refutations[2].move
			          || refutations[1].move == refutations[2].move)
			          --endMoves;
				   */
	 				
			  
			  case STAGE_REFUTATION:
			      /*if (select<Next>([&](){ return    move != MOVE_NONE
			                                    && !pos.capture(move)
			                                    &&  pos.pseudo_legal(move); }))
			          return move;*/
			      ++stage;
	
			  case STAGE_QUIET_INIT:
				  
			      if (board.isInCheck()) {
			    	  //Do nothing
			      } else {
			    	  board.genAllMoves(this);
			      }
			      
			      Utils.randomize(moves_quiet, cur_quiet, max_quiet);
			      Utils.bubbleSort(cur_quiet, max_quiet, moves_quiet);
				  
			      ++stage;
				
			      
			  case STAGE_QUIET:
				  
				  for (int i = cur_quiet; i < max_quiet; i++) {
					  cur_quiet++;
					  return (int) moves_quiet[i];
				  }
				  
			      Utils.randomize(moves_capture_promotion_bad, cur_capture_promotion_bad, max_capture_promotion_bad);
			      Utils.bubbleSort(cur_capture_promotion_bad, max_capture_promotion_bad, moves_capture_promotion_bad);
				  
			      ++stage;
	
			  case STAGE_BAD_CAPTURE:
				  
				  for (int i = cur_capture_promotion_bad; i < cur_capture_promotion_bad; i++) {
					  if (board.getSEEScore((int) moves_capture_promotion_bad[i]) < threshold) {
						  cur_capture_promotion_bad++;
						  return (int) moves_capture_promotion_bad[i];
					  }
					  cur_capture_promotion_bad++;
				  }
				  
				  ++stage;
	
			  /*case EVASION_INIT:
			      cur = moves;
			      endMoves = generate<EVASIONS>(pos, cur);
	
			      score<EVASIONS>();
			      ++stage;
	
			  case EVASION:
			      return select<Best>(Any);
	
			  case PROBCUT:
			      return select<Best>([&](){ return pos.see_ge(move, threshold); });
	
			  case QCAPTURE:
			      if (select<Best>([&](){ return   depth > DEPTH_QS_RECAPTURES
			                                    || to_sq(move) == recaptureSquare; }))
			          return move;
	
			      // If we did not find any move and we do not try checks, we have finished
			      if (depth != DEPTH_QS_CHECKS)
			          return MOVE_NONE;
	
			      ++stage;
	
			  case QCHECK_INIT:
			      cur = moves;
			      endMoves = generate<QUIET_CHECKS>(pos, cur);
	
			      ++stage;
	
			  case QCHECK:
			      return select<Next>(Any);
			      */
		  }

		  assert(false);
		      
		  return 0; // Silence warning
	}
	
	
	@Override
	public void reserved_add(int move_org) {
		
		int ordval = genOrderingValue(move_org);
		
		long move = MoveInt.addOrderingValue(move_org, ordval);
		
		if (board.getMoveOps().isCaptureOrPromotion(move_org)) {
			
			if (board.getSEEScore(move_org) >= 0) {
				if (max_capture_promotion_good == 0) {
					moves_capture_promotion_good[0] = move;
				} else {
					if (move > moves_capture_promotion_good[0]) {
						moves_capture_promotion_good[max_capture_promotion_good] = moves_capture_promotion_good[0];
						moves_capture_promotion_good[0] = move;
					} else {
						moves_capture_promotion_good[max_capture_promotion_good] = move;
					}
				}
				max_capture_promotion_good++;	
			} else {
				if (max_capture_promotion_bad == 0) {
					moves_capture_promotion_bad[0] = move;
				} else {
					if (move > moves_capture_promotion_bad[0]) {
						moves_capture_promotion_bad[max_capture_promotion_bad] = moves_capture_promotion_bad[0];
						moves_capture_promotion_bad[0] = move;
					} else {
						moves_capture_promotion_bad[max_capture_promotion_bad] = move;
					}
				}
				max_capture_promotion_bad++;
			}

		} else {
			if (max_quiet == 0) {
				moves_quiet[0] = move;
			} else {
				if (move > moves_quiet[0]) {
					moves_quiet[max_quiet] = moves_quiet[0];
					moves_quiet[0] = move;
				} else {
					moves_quiet[max_quiet] = move;
				}
			}
			max_quiet++;
		}
	}

	
	private int genOrderingValue(int move) {
		
		if (board.getMoveOps().isCaptureOrPromotion(move)) {
	        return PieceValue[1][board.getMoveOps().getCapturedFigureType(move)]
	                 + captureHistory.array[board.getMoveOps().getFigurePID(move)][board.getMoveOps().getToFieldID(move)][board.getMoveOps().getCapturedFigureType(move)] / 8;
		} else {
			
			if (mainHistory == null || continuationHistory0 == null || continuationHistory1 == null || continuationHistory2 == null) {
				return 0;
			}
			
			return mainHistory.array[board.getColourToMove()][board.getMoveOps().getFromFieldID(move) * board.getMoveOps().getToFieldID(move)]
	                 + continuationHistory0.array[board.getMoveOps().getFigurePID(move)][board.getMoveOps().getToFieldID(move)]
	                 + continuationHistory1.array[board.getMoveOps().getFigurePID(move)][board.getMoveOps().getToFieldID(move)]
	                 + continuationHistory2.array[board.getMoveOps().getFigurePID(move)][board.getMoveOps().getToFieldID(move)];
		}
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IInternalMoveList#reserved_clear()
	 */
	@Override
	public void reserved_clear() {
		throw new IllegalStateException();
	}


	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IInternalMoveList#reserved_removeLast()
	 */
	@Override
	public void reserved_removeLast() {
		throw new IllegalStateException();
	}


	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IInternalMoveList#reserved_getCurrentSize()
	 */
	@Override
	public int reserved_getCurrentSize() {
		throw new IllegalStateException();
	}


	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.api.IInternalMoveList#reserved_getMovesBuffer()
	 */
	@Override
	public int[] reserved_getMovesBuffer() {
		throw new IllegalStateException();
	}


	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.impl.movelist.IMoveList#clear()
	 */
	@Override
	public void clear() {
		throw new IllegalStateException();
	}


	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.impl.movelist.IMoveList#size()
	 */
	@Override
	public int size() {
		throw new IllegalStateException();
	}


	/* (non-Javadoc)
	 * @see bagaturchess.bitboard.impl.movelist.IMoveList#next()
	 */
	@Override
	public int next() {
		throw new IllegalStateException();
	}
}

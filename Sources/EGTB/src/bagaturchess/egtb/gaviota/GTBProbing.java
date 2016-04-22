package bagaturchess.egtb.gaviota;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.datastructs.list.ListNodeObject;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.egtb.gaviota.cache.GTBCache_IN;
import bagaturchess.egtb.gaviota.cache.GTBCache_OUT;


public class GTBProbing {
	
	
	public static int MAX_PIECES_COUNT = 5;//Including both kings
	
	
	private GTBCache_OUT cache_out;
	private GTBCache_IN cache_in;
	private Filler filler;
	
	private GTBProbeOutput no_result;
	
	private IMoveList temp_list = new BaseMoveList();
	private int[] temp_out = new int[2];
	
	
	public GTBProbing(GTBCache_OUT _cache_out, GTBCache_IN _cache_in) {
		
		cache_out = _cache_out;
		cache_in = _cache_in;
		no_result = new GTBProbeOutput();
		
		createFiller();
	}
	
	
	private void createFiller() {
		filler  = new Filler(cache_out, cache_in);
	}
	
	
	public void probeMove(IBitBoard board, int[] out) {//out = new int[] {move, moves_to_mate_if_any_or_zero_for_draw}
		
		//Check pieces count
		if (board.getMaterialState().getPiecesCount() > MAX_PIECES_COUNT) {
			out[0] = 0;
			out[1] = 0;
			return;
		}
		
		
		//Check castling rights
        if (board.hasRightsToKingCastle(Constants.COLOUR_WHITE) || board.hasRightsToQueenCastle(Constants.COLOUR_WHITE)
        		|| board.hasRightsToKingCastle(Constants.COLOUR_BLACK) || board.hasRightsToQueenCastle(Constants.COLOUR_BLACK)) {
        	out[0] = 0;
			out[1] = 0;
			return;
        }
		
        
		temp_list.clear();
		board.genAllMoves(temp_list);
		
		boolean allMovesHit = true;
		List<GTBProbeOutput> moves = new ArrayList<GTBProbeOutput>();
		int cur_move;
		while ((cur_move = temp_list.next()) != 0) {
			
			board.makeMoveForward(cur_move);
			GTBProbeInput input = new GTBProbeInput();
			GTBProbing_NativeWrapper.getInstance().fill(board, input);
			GTBProbing_NativeWrapper.getInstance().probeHard(input, temp_out);
			//probe(board, temp_out);
			board.makeMoveBackward(cur_move);
			
			
			if (temp_out[0] == GTBProbeOutput.DRAW) {
				moves.add(new GTBProbeOutput(cur_move, temp_out[0], temp_out[1]));
			} else if (temp_out[0] == GTBProbeOutput.WMATE) {
				moves.add(new GTBProbeOutput(cur_move, temp_out[0], temp_out[1]));
			} else if (temp_out[0] == GTBProbeOutput.BMATE) {
				moves.add(new GTBProbeOutput(cur_move, temp_out[0], temp_out[1]));
			} else {
				allMovesHit = false;
				break;
			}
		}
		
		if (allMovesHit) {
			if (moves.size() > 0) {
				Collections.sort(moves);
				
				if (board.getColourToMove() == Constants.COLOUR_WHITE) {
					Collections.reverse(moves);
				}
				
				for (int i=0; i<moves.size(); i++) {
					System.out.println(moves.get(i));
				}
				
				GTBProbeOutput best = moves.get(0);
				
				out[0] = best.move;
				out[1] = best.movesToMate;
				if (out[1] != 0) {
					if (board.getColourToMove() == Constants.COLOUR_WHITE && best.result == GTBProbeOutput.BMATE) {
						out[1] = -out[1];
					}
					if (board.getColourToMove() == Constants.COLOUR_BLACK && best.result == GTBProbeOutput.WMATE) {
						out[1] = -out[1];
					}
				}
			}
		}
	}
	
	
	public void probe(IBitBoard board, int[] out) {
		
		
		//Check pieces count
		if (board.getMaterialState().getPiecesCount() > MAX_PIECES_COUNT) {
			out[0] = no_result.result;
			out[1] = no_result.movesToMate;
			return;
		}
		
		
		//Check castling rights
        if (board.hasRightsToKingCastle(Constants.COLOUR_WHITE) || board.hasRightsToQueenCastle(Constants.COLOUR_WHITE)
        		|| board.hasRightsToKingCastle(Constants.COLOUR_BLACK) || board.hasRightsToQueenCastle(Constants.COLOUR_BLACK)) {
        	out[0] = no_result.result;
			out[1] = no_result.movesToMate;
			return;
        }
        
        
		long hashkey = board.getHashKey();
		cache_out.lock();
		GTBProbeOutput result = cache_out.get(hashkey);
		if (result != null) {
			out[0] = result.result;
			out[1] = result.movesToMate;
			cache_out.unlock();
			return;
		} else {
			out[0] = no_result.result;
			out[1] = no_result.movesToMate;
		}
		cache_out.unlock();
		
		/**
		 * !!! REMOVE
		 */
		/*if (true) {
			return;
		}*/
		
		
		if (result == null) {
	        
			result = no_result;
			
			cache_in.lock();
			GTBProbeInput input = cache_in.getEntryForFilling(hashkey);
			//cache_in.unlock();
			
			if (input == null) { //Entry is not scheduled for loading
		
				/*Lazy initialization
				if (filler == null) {
					createFiller();
				}*/
				
				ListNodeObject<GTBProbeInput> node = filler.getFreeEntry();
				if (node == null) {
					
					//cache_in.lock();
					input = cache_in.reuseEntryForFilling(hashkey);
					if (input == null) {
						//System.out.println("cache_in.size: " + cache_in.getCurrentSize());
					} else {
						GTBProbing_NativeWrapper.getInstance().fill(board, input);
					}
					//cache_in.unlock();
					
				} else {
					GTBProbing_NativeWrapper.getInstance().fill(board, node.getValue());
					
					//cache_in.lock();
					filler.returnFreeEntry(node);
					//cache_in.unlock();
				}
			}
			
			cache_in.unlock();
		}
	}


	public void clear() {
		if (filler != null) filler.stop();
	}
}

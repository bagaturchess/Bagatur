package bagaturchess.egtb.cache;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;


public abstract class EGTBProbing {
	
	
	public static int MAX_PIECES_COUNT = 7;//Including both kings
	
	
	private EGTBProbeOutput no_result;
	
	private IMoveList temp_moves_list;
	
	//new int[] {move, moves_to_mate_if_any_or_zero_for_draw}
	private int[] temp_out;
	
	
	public EGTBProbing() {
		
		no_result = new EGTBProbeOutput();
		
		temp_moves_list = new BaseMoveList();
		
		temp_out = new int[2];
	}
	
	
	public abstract void setPath_Sync(String tbPath, int memInMegabytes);
	
	public abstract void fill(IBitBoard board, EGTBProbeInput input);
	
	public abstract void probeHard(EGTBProbeInput input, int[] temp_out);
	
	
	public void probeMove(IBitBoard board, int[] out) {
		
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
		
        
        temp_moves_list.clear();
		board.genAllMoves(temp_moves_list);
		
		boolean allMovesHit = true;
		List<EGTBProbeOutput> moves = new ArrayList<EGTBProbeOutput>();
		int cur_move;
		while ((cur_move = temp_moves_list.next()) != 0) {
			
			board.makeMoveForward(cur_move);
			
			
			EGTBProbeInput input = new EGTBProbeInput();
			
			fill(board, input);
			
			probeHard(input, temp_out);
			
			
			board.makeMoveBackward(cur_move);
			
			
			if (temp_out[0] == EGTBProbeOutput.DRAW) {
				moves.add(new EGTBProbeOutput(cur_move, temp_out[0], temp_out[1]));
			} else if (temp_out[0] == EGTBProbeOutput.WMATE) {
				moves.add(new EGTBProbeOutput(cur_move, temp_out[0], temp_out[1]));
			} else if (temp_out[0] == EGTBProbeOutput.BMATE) {
				moves.add(new EGTBProbeOutput(cur_move, temp_out[0], temp_out[1]));
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
				
				EGTBProbeOutput best = moves.get(0);
				
				out[0] = best.move;
				out[1] = best.movesToMate;
				if (out[1] != 0) {
					if (board.getColourToMove() == Constants.COLOUR_WHITE && best.result == EGTBProbeOutput.BMATE) {
						out[1] = -out[1];
					}
					if (board.getColourToMove() == Constants.COLOUR_BLACK && best.result == EGTBProbeOutput.WMATE) {
						out[1] = -out[1];
					}
				}
			}
		}
	}
	
	
	public void probe(IBitBoard board, int[] out, EGTBProbeInput temp_input, EGTBCache cache_out) {
		
		
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
		
		EGTBProbeOutput result = cache_out.get(hashkey);
		
		if (result != null) {
			
			out[0] = result.result;
			out[1] = result.movesToMate;
			cache_out.unlock();
			
			return;
			
		} else {
			
			out[0] = no_result.result;
			out[1] = no_result.movesToMate;
			
			
			fill(board, temp_input);
			
			probeHard(temp_input, temp_out);
			
			
			cache_out.put(hashkey, out[0], out[1]);

		}
		
		cache_out.unlock();
	}
}

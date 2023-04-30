package bagaturchess.bitboard.impl_kingcaptureallowed.attacks;


import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;


public class FieldAttack {
	
	public static final boolean isFieldAttacked(final int fieldID, final int attackingColour, final int[] board, final IPiecesLists plist) {
		
		
		boolean hasRooksOrQueens =
			(attackingColour == Constants.COLOUR_WHITE) ?
					(plist.getPieces(Constants.PID_W_ROOK).getDataSize() + plist.getPieces(Constants.PID_W_QUEEN).getDataSize() > 0) :
					(plist.getPieces(Constants.PID_B_ROOK).getDataSize() + plist.getPieces(Constants.PID_B_QUEEN).getDataSize() > 0);
		
		//Check for rook and queen attacks
		if (hasRooksOrQueens) {
			int [] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
			int[][] dirs_ids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
			int size = validDirIDs.length;
			
			for (int i=0; i<size; i++) {
				
				final int dirID = validDirIDs[i];
				final int[] dirIDs = dirs_ids[dirID];
				
				for (int seq=0; seq<dirIDs.length; seq++) {
					
					final int toFieldID = dirIDs[seq];
					final int targetPID = board[toFieldID];
					if (targetPID != 0) {
						
						int expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_QUEEN : Constants.PID_B_QUEEN;
						if (targetPID == expectedPID) {
							return true;
						}
						
						expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_ROOK : Constants.PID_B_ROOK;
						if (targetPID == expectedPID) {
							return true;
						}
						
						break; //Stop search in this direction
					}
				}
			}
		}
		
		
		boolean hasBishopsOrQueens =
			(attackingColour == Constants.COLOUR_WHITE) ?
					(plist.getPieces(Constants.PID_W_BISHOP).getDataSize() + plist.getPieces(Constants.PID_W_QUEEN).getDataSize() > 0) :
					(plist.getPieces(Constants.PID_B_BISHOP).getDataSize() + plist.getPieces(Constants.PID_B_QUEEN).getDataSize() > 0);
		
		//Check for officer and queen attacks
		if (hasBishopsOrQueens) {
			int [] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
			int[][] dirs_ids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
			int size = validDirIDs.length;
			
			for (int i=0; i<size; i++) {
				
				final int dirID = validDirIDs[i];
				final int[] dirIDs = dirs_ids[dirID];
				
				for (int seq=0; seq<dirIDs.length; seq++) {
					
					final int toFieldID = dirIDs[seq];
					final int targetPID = board[toFieldID];
					if (targetPID != 0) {
						
						int expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_QUEEN : Constants.PID_B_QUEEN;
						if (targetPID == expectedPID) {
							return true;
						}
						
						expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_BISHOP : Constants.PID_B_BISHOP;
						if (targetPID == expectedPID) {
							return true;
						}
						
						break; //Stop search in this direction
					}
				}
			}
		}
		
		
		boolean hasKnights =
			(attackingColour == Constants.COLOUR_WHITE) ?
					(plist.getPieces(Constants.PID_W_KNIGHT).getDataSize() > 0) :
						(plist.getPieces(Constants.PID_B_KNIGHT).getDataSize() > 0);
					
		//Check for knight attacks
		if (hasKnights) {
			int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
			int[][] dirs_ids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
			int size = validDirIDs.length;
			
			for (int i=0; i<size; i++) {
				
				final int dirID = validDirIDs[i];
				final int toFieldID = dirs_ids[dirID][0];
				final int targetPID = board[toFieldID];
				
				if (targetPID != 0) {
					final int expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_KNIGHT : Constants.PID_B_KNIGHT;
					if (targetPID == expectedPID) {
						return true;
					}
				}
			}
		}
		
		
		//Check for king attacks
		int [] validDirIDs = KingPlies.ALL_KING_VALID_DIRS[fieldID];
		int[][] dirs_ids = KingPlies.ALL_KING_DIRS_WITH_FIELD_IDS[fieldID];
		int size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			final int dirID = validDirIDs[i];
			final int toFieldID = dirs_ids[dirID][0];
			final int targetPID = board[toFieldID];
			
			if (targetPID != 0) {
				final int expectedPID = (attackingColour == Constants.COLOUR_WHITE) ? Constants.PID_W_KING : Constants.PID_B_KING;
				if (targetPID == expectedPID) {
					return true;
				}
			}
		}
		
		
		//Check for pawn attacks
		switch (attackingColour) {
		
			case Constants.COLOUR_WHITE:
			{
				int letter = Fields.LETTERS[fieldID];
				if (letter != Fields.LETTER_A_ID) {
					final int targetFieldID = fieldID - 9;
					if (targetFieldID >= 0) {
						final int targetPID = board[targetFieldID];
						if (targetPID == Constants.PID_W_PAWN) {
							return true;
						}
					}
				}
				
				if (letter != Fields.LETTER_H_ID) {
					final int targetFieldID = fieldID - 7;
					if (targetFieldID >= 0) {
						final int targetPID = board[targetFieldID];
						if (targetPID == Constants.PID_W_PAWN) {
							return true;
						}
					}
				}
				
				break;
			}
			case Constants.COLOUR_BLACK:
			{
				int letter = Fields.LETTERS[fieldID];
				if (letter != Fields.LETTER_A_ID) {
					final int targetFieldID = fieldID + 7;
					if (targetFieldID <= 63) {
						final int targetPID = board[targetFieldID];
						if (targetPID == Constants.PID_B_PAWN) {
							return true;
						}
					}
				}
				
				if (letter != Fields.LETTER_H_ID) {
					final int targetFieldID = fieldID + 9;
					if (targetFieldID <= 63) {
						final int targetPID = board[targetFieldID];
						if (targetPID == Constants.PID_B_PAWN) {
							return true;
						}
					}
				}
				
				break;
			}
		}
		
		
		return false;
	}
	
}

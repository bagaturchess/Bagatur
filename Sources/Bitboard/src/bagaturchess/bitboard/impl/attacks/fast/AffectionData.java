/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
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
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.bitboard.impl.attacks.fast;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.datastructs.numbers.IndexNumberSet;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.state.PiecesList;

public class AffectionData {
	
	int[] removed;
	int[] introduced;	
	int[] affected;
	int[][] affected_dirs;
	int[][] affected_dirs_types;
	
	int[] removed_pids;
	int[] introduced_pids;
	int[] affected_pids;
	
	private IndexNumberSet affectedQueens;
	boolean queenSetCleared = false;
	
	private IBitBoard bitboard;
	private IPiecesLists piecesLists;
	private IPlayerAttacks whiteAttacks;
	private IPlayerAttacks blackAttacks;
	
	private IndexNumberSet removedSet;
	private IndexNumberSet introducedSet;
	private IndexNumberSet affectedSet;
	private IndexNumberSet affectedDirsSet;
	
	public AffectionData(IBitBoard _bitboard, IPlayerAttacks _whiteAttacks, IPlayerAttacks _blackAttacks) {
		bitboard = _bitboard;
		piecesLists = bitboard.getPiecesLists();
		
		whiteAttacks = _whiteAttacks;
		blackAttacks = _blackAttacks;
		
		removed = new int[4];
		introduced = new int[4];
		affected = new int[16];
		
		removed_pids = new int[4];
		introduced_pids = new int[4];
		affected_pids = new int[16];
		
		affected_dirs = new int[Figures.ID_MAX][8];
		affected_dirs_types = new int[Figures.ID_MAX][8];
		affectedQueens = new IndexNumberSet(Figures.ID_MAX); 
		
		if (IPlayerAttacks.DEBUG_ATTACKS) {
			removedSet = new IndexNumberSet(Figures.ID_MAX);
			introducedSet = new IndexNumberSet(Figures.ID_MAX);
			affectedSet = new IndexNumberSet(Figures.ID_MAX);
			affectedDirsSet = new IndexNumberSet(12);
		}
	}
	
	public void clear() {
		removed[0] = 0;
		introduced[0] = 0;
		affected[0] = 0;
		queenSetCleared = false;
		//affectedQueens.clear();
	}
	
	private void addRemoved(int pid, int fieldID) {
		if (IPlayerAttacks.DEBUG_ATTACKS) {
			if (removed[0] >= removed.length - 1) {
				throw new IllegalStateException();
			}
		}
		int size = ++removed[0];
		removed[size] = fieldID;
		removed_pids[size] = pid;
	}

	private void addIntroduced(int pid, int fieldID) {
		int size = ++introduced[0];
		introduced[size] = fieldID;
		introduced_pids[size] = pid;
	}

	public void addPromotionFigureID_OnForwardMove(int pid, int fieldID) {
		
		//if (true) return;
		
		addIntroduced(pid, fieldID);
	}
	
	public void fillRemovedIntroduced_OnForwardMove(int move) {
		
		//if (true) return; 
		
		long allAffectedFields = 0L;
		long shortAffectedFields = 0L;
		
		
		int pid = MoveInt.getFigurePID(move);
		int fromFieldID = MoveInt.getFromFieldID(move);
		int toFieldID = MoveInt.getToFieldID(move);
		
		boolean capture = MoveInt.isCapture(move);
		boolean enpass = MoveInt.isEnpassant(move);
		long toFieldBitboard = MoveInt.getToFieldBitboard(move);
		
		allAffectedFields |= MoveInt.getFromFieldBitboard(move);
		allAffectedFields |= toFieldBitboard;
		
		addRemoved(pid, fromFieldID);
		
		if (MoveInt.isCastling(move)) {
			int castleID = MoveInt.getCastlingRookPID(move);
			int castleFrom = MoveInt.getCastlingRookFromID(move);
			int castleTo = MoveInt.getCastlingRookToID(move);
			
			addRemoved(castleID, castleFrom);
			addIntroduced(castleID, castleTo);
			addIntroduced(pid, toFieldID); //add king
			
			long fromCastleBoard = Fields.ALL_ORDERED_A1H1[castleFrom];
			long toCastleBoard = Fields.ALL_ORDERED_A1H1[castleTo];
			allAffectedFields |= fromCastleBoard;
			allAffectedFields |= toCastleBoard;
			
		} else if (enpass) {
			int enpassFieldID = MoveInt.getEnpassantCapturedFieldID(move);
			long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[enpassFieldID];
			addRemoved(MoveInt.getCapturedFigurePID(move), enpassFieldID);
			addIntroduced(pid, toFieldID);
			
			allAffectedFields |= opponentPawnBitboard;
		} else {
			if (!MoveInt.isPromotion(move)) {
				addIntroduced(pid, toFieldID);
			}
			
			if (capture) {
				addRemoved(MoveInt.getCapturedFigurePID(move), toFieldID);
			}
		}
		
		shortAffectedFields = allAffectedFields;
		
		//TODO: Make that hard optimization, it is hard because of hidden attacks of sliding figures
		if (IPlayerAttacks.SUPPORT_HIDDEN_ATTACKS) {
			//Leave shortAffectedFields as allAffectedFields
		} else {
			if (capture && !enpass) {
				shortAffectedFields &= ~toFieldBitboard;
			}
		}
		
		if ((whiteAttacks.attacksByType(Figures.TYPE_OFFICER) & shortAffectedFields) != 0L) {
			fillOfficersAffections(shortAffectedFields, allAffectedFields, whiteAttacks, Figures.COLOUR_WHITE, Figures.TYPE_OFFICER);
		}
		
		if ((blackAttacks.attacksByType(Figures.TYPE_OFFICER) & shortAffectedFields) != 0L) {
			fillOfficersAffections(shortAffectedFields, allAffectedFields, blackAttacks, Figures.COLOUR_BLACK, Figures.TYPE_OFFICER);
		}
	
		if ((whiteAttacks.attacksByType(Figures.TYPE_QUEEN) & shortAffectedFields) != 0L) {
			//Queen's officers attacks should be before queen' castles affection because of duplicated affected ids
			fillOfficersAffections(shortAffectedFields, allAffectedFields, whiteAttacks, Figures.COLOUR_WHITE, Figures.TYPE_QUEEN);
			fillCastlesAffections(shortAffectedFields, allAffectedFields, whiteAttacks, Figures.COLOUR_WHITE, Figures.TYPE_QUEEN);
		}
		if ((blackAttacks.attacksByType(Figures.TYPE_QUEEN) & shortAffectedFields) != 0L) {
			//Queen's officers attacks should be before queen' castles affection because of duplicated affected ids
			fillOfficersAffections(shortAffectedFields, allAffectedFields, blackAttacks, Figures.COLOUR_BLACK, Figures.TYPE_QUEEN);
			fillCastlesAffections(shortAffectedFields, allAffectedFields, blackAttacks, Figures.COLOUR_BLACK, Figures.TYPE_QUEEN);
		}
		
		if ((whiteAttacks.attacksByType(Figures.TYPE_CASTLE) & shortAffectedFields) != 0L) {
			fillCastlesAffections(shortAffectedFields, allAffectedFields, whiteAttacks, Figures.COLOUR_WHITE, Figures.TYPE_CASTLE);
		}
		if ((blackAttacks.attacksByType(Figures.TYPE_CASTLE) & shortAffectedFields) != 0L) {
			fillCastlesAffections(shortAffectedFields, allAffectedFields, blackAttacks, Figures.COLOUR_BLACK, Figures.TYPE_CASTLE);
		}
	}
	
	/**
	 * First remove all in removed and introduced arrays
	 */
	private void fillOfficersAffections(long affectedFields, long allAffectedFields, IPlayerAttacks attacks, int colour, int figtype) {
		
		if (IPlayerAttacks.DEBUG_ATTACKS) {
			if (figtype != Figures.TYPE_QUEEN && figtype != Figures.TYPE_OFFICER) {
				throw new IllegalStateException();
			}
		}
		
		//long typeAttacks = attacks.attacksByType(figtype);
		
		//if ((typeAttacks & affectedFields) != 0L) { //Should update at least one of the officers direction
			
			int pid = Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][figtype];
		
			PiecesList figs = piecesLists.getPieces(pid);
			int size = figs.getDataSize();
			int[] ids = figs.getData();
			
			for (int i=0; i<size; i++) {
				
				int fieldID = ids[i];
				long pos = Fields.ALL_ORDERED_A1H1[fieldID];
				
				if ((allAffectedFields & pos) != 0L) {
					continue; // The figure actually moved, it will be handled by addRemoved or addIntroduced
				}

				long curAttacks = attacks.attacksByFieldID(figtype, fieldID);
				
				/**
				 * Optimization for queens
				 */
				if (figtype != Figures.TYPE_OFFICER) {
					//int fieldID = bitboard.getFieldID(figureID);
					curAttacks = curAttacks & OfficerPlies.ALL_OFFICER_MOVES[fieldID];
				}
				
				if ((curAttacks & affectedFields) != 0L) {
					//This figure is affected
					//Find the affected directions
										
					int[] dirs = affected_dirs[fieldID];
					int[] types = affected_dirs_types[fieldID];
					dirs[0] = 0;
					types[0] = 0;
					
					if (figtype == Figures.TYPE_QUEEN) {
						if (!queenSetCleared) {
							affectedQueens.clear();
							queenSetCleared = true;
						}
						affectedQueens.add(fieldID);
					}

					int cur_affected = ++affected[0];
					affected[cur_affected] = fieldID;
					affected_pids[cur_affected] = pid;
					
					long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[fieldID];
					if ((dir0 & affectedFields) != 0L) {
						//Add figureID, dirtype and dir=0;
						int count = ++dirs[0];
						dirs[count] = 0;
						types[0]++;
						types[count] = Figures.TYPE_OFFICER;
					}
					long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[fieldID];
					if ((dir1 & affectedFields) != 0L) {
						//Add figureID, dirtype and dir=1;
						int count = ++dirs[0];
						dirs[count] = 1;
						types[0]++;
						types[count] = Figures.TYPE_OFFICER;
					}
					long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[fieldID];
					if ((dir2 & affectedFields) != 0L) {
						//Add figureID, dirtype and dir=2;
						int count = ++dirs[0];
						dirs[count] = 2;
						types[0]++;
						types[count] = Figures.TYPE_OFFICER;
					}
					long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[fieldID];
					if ((dir3 & affectedFields) != 0L) {
						//Add figureID, dirtype and dir=3;
						int count = ++dirs[0];
						dirs[count] = 3;
						types[0]++;
						types[count] = Figures.TYPE_OFFICER;
					}
				}
			}
		//}
	}
	
	/**
	 * First remove all in removed and introduced arrays
	 */
	private void fillCastlesAffections(long affectedFields, long allAffectedFields, IPlayerAttacks attacks, int colour, int figtype) {
		
		if (IPlayerAttacks.DEBUG_ATTACKS) {
			if (figtype != Figures.TYPE_QUEEN && figtype != Figures.TYPE_CASTLE) {
				throw new IllegalStateException();
			}
		}
		
		//long typeAttacks = attacks.attacksByType(figtype);
		
		//if ((typeAttacks & affectedFields) != 0L) { //Should update at least one of the officers direction
		
		int pid = Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][figtype];
		
			PiecesList figs = piecesLists.getPieces(pid);
			int size = figs.getDataSize();
			int[] ids = figs.getData();
			
			for (int i=0; i<size; i++) {
				
				int fieldID = ids[i];
				long pos = Fields.ALL_ORDERED_A1H1[fieldID];
				
				if ((allAffectedFields & pos) != 0L) {
					continue; // The figure actually moved, it will be handled by addRemoved or addIntroduced
				}
				
				long curAttacks = attacks.attacksByFieldID(figtype, fieldID);
				
				/**
				 * Optimization for queens
				 */
				if (figtype != Figures.TYPE_CASTLE) {
					curAttacks = curAttacks & CastlePlies.ALL_CASTLE_MOVES[fieldID];
				}
				
				if ((curAttacks & affectedFields) != 0L) {
					//This figure is affected
					//Find the affected directions
					int[] dirs = affected_dirs[fieldID];
					int[] types = affected_dirs_types[fieldID];
					
					if (!queenSetCleared || !affectedQueens.contains(fieldID)) {
						types[0] = 0;
						dirs[0] = 0;
						int cur_affected = ++affected[0];
						if (cur_affected > 7) {
							int g=0;
						}
						affected[cur_affected] = fieldID;
						affected_pids[cur_affected] = pid;
					}
					
					long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[fieldID];
					if ((dir0 & affectedFields) != 0L) {
						//Add figureID, dirtype and dir=0;
						int count = ++dirs[0];
						dirs[count] = 0;
						types[0]++;
						types[count] = Figures.TYPE_CASTLE;
					}
					long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[fieldID];
					if ((dir1 & affectedFields) != 0L) {
						//Add figureID, dirtype and dir=1;
						int count = ++dirs[0];
						dirs[count] = 1;
						types[0]++;
						types[count] = Figures.TYPE_CASTLE;
					}
					long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[fieldID];
					if ((dir2 & affectedFields) != 0L) {
						//Add figureID, dirtype and dir=2;
						int count = ++dirs[0];
						dirs[count] = 2;
						types[0]++;
						types[count] = Figures.TYPE_CASTLE;
					}
					long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[fieldID];
					if ((dir3 & affectedFields) != 0L) {
						//Add figureID, dirtype and dir=3;
						int count = ++dirs[0];
						dirs[count] = 3;
						types[0]++;
						types[count] = Figures.TYPE_CASTLE;
					}
				}
			}
		//}
	}

	public void checkConsistency() { 
		removedSet.clear();
		introducedSet.clear();
		affectedSet.clear();
		
		checkUnique(this.removed, removedSet);
		checkUnique(this.introduced, introducedSet);
		checkUnique(this.affected, affectedSet);
	}

	private void checkUnique(int[] ids, IndexNumberSet set) {
		int size = ids[0];
		for (int i=0; i<size; i++) {
			set.add(ids[i + 1]);
		}
	}
}

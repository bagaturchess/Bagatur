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

import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.common.GlobalConstants;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;

public class FastPlayersAttacks implements MoveListener {
	
	private Board bitboard;
	private FastPlayerAttacks white;
	private FastPlayerAttacks black;
	
	private int playedMoveIndex = 0;
	private AffectionData[] leveldata;
	private FieldsStateMachine fieldAttacksCollector;
	
	public FastPlayersAttacks(Board _bitboard, FieldsStateMachine _fieldAttacksCollector) {
		bitboard = _bitboard;
		fieldAttacksCollector = _fieldAttacksCollector;
		
		white = new FastPlayerAttacks(Figures.COLOUR_WHITE, bitboard, fieldAttacksCollector);
		black = new FastPlayerAttacks(Figures.COLOUR_BLACK, bitboard, fieldAttacksCollector);
		
		leveldata = new AffectionData[GlobalConstants.MAX_MOVES_IN_GAME];
		for (int i=0; i<GlobalConstants.MAX_MOVES_IN_GAME; i++) {
			leveldata[i] = new AffectionData(bitboard, white, black);
		}
	}
	
	public IPlayerAttacks getWhiteAttacks() {
		return white;
	}

	public IPlayerAttacks getBlackAttacks() {
		return black;
	}

	public void preForwardMove(int color, int move) {
		
		playedMoveIndex++;
		
		//"a2-a3(0), a7-a6(0), a3-a4(0), b7-b6(0), Nb1-a3(0), "
		AffectionData data = leveldata[playedMoveIndex - 1];
		data.clear();
		
		/**
		 * Fill data (without promoted figure id)
		 */
		data.fillRemovedIntroduced_OnForwardMove(move);
		
		if (IPlayerAttacks.DEBUG_ATTACKS) {
			data.checkConsistency();
		}
		
		remove(data.removed, data.removed_pids);
		removeAffected(data);
	}
	
	public void addPiece_Special(int promotedPID, int fieldID) {
		AffectionData data = leveldata[playedMoveIndex - 1];
		data.addPromotionFigureID_OnForwardMove(promotedPID, fieldID);
	}
	
	public void initially_addPiece(int promotedPID, int fieldID, long bb_pieces) {
		AffectionData data = leveldata[playedMoveIndex - 1];
		data.addPromotionFigureID_OnForwardMove(promotedPID, fieldID);
	}
	
	public void postForwardMove(int color, int move) {
		
		AffectionData data = leveldata[playedMoveIndex - 1];
		introduce(data.introduced, data.introduced_pids);
		introduceAffected(data);
		
		if (IPlayerAttacks.DEBUG_ATTACKS) {
			white.checkConsistency();
			black.checkConsistency();
			fieldAttacksCollector.checkConsistency();
		}
	}


	
	public void preBackwardMove(int color, int move) {
		
		playedMoveIndex--;
		
		AffectionData data = leveldata[playedMoveIndex];
		
		remove(data.introduced, data.introduced_pids);
		removeAffected(data);
	}

	public void postBackwardMove(int color, int move) {
		AffectionData data = leveldata[playedMoveIndex];
		introduce(data.removed, data.removed_pids);
		introduceAffected(data);
		
		if (IPlayerAttacks.DEBUG_ATTACKS) {
			white.checkConsistency();
			black.checkConsistency();
			fieldAttacksCollector.checkConsistency();
		}
	}
	
	
	//Common methods
	
	private void remove(int[] removed, int[] removed_pids) {
		int size = removed[0];
		for (int i=0; i<size; i++) {
			int fieldID = removed[i + 1];
			int pid = removed_pids[i + 1];
			
			int colour = Figures.getFigureColour(pid);
			if (colour == Figures.COLOUR_WHITE) {
				white.removeFigure(pid, fieldID, -1, -1);
			} else {
				black.removeFigure(pid, fieldID, -1, -1);
			}
		}
	}
	
	private void removeAffected(AffectionData data) {
		int[] affected = data.affected;
		int[] affected_pids = data.affected_pids;
		int size = affected[0];
		if (size > 0) {
			for (int i=0; i<size; i++) {
				int fieldID = affected[i + 1];
				int pid = affected_pids[i + 1];
				
				int[] dirs = data.affected_dirs[fieldID];
				int[] types = data.affected_dirs_types[fieldID];
				
				if (IPlayerAttacks.DEBUG_ATTACKS) {
					if (dirs[0] != types[0] || types[0] == 0) {
						throw new IllegalStateException();
					}
				}

				int colour = Figures.getFigureColour(pid);
				if (colour == Figures.COLOUR_WHITE) {
					for (int j=0; j<dirs[0]; j++) {
						white.removeFigure(pid, fieldID, dirs[j + 1], types[j + 1]);
					}
				} else {
					for (int j=0; j<dirs[0]; j++) {
						black.removeFigure(pid, fieldID, dirs[j + 1], types[j + 1]);
					}
				}
			}
		}
	}
	
	private void introduce(int[] introduced, int[] introduced_pids) {
		int size = introduced[0];
		for (int i=0; i<size; i++) {
			int fieldID = introduced[i + 1];
			int pid = introduced_pids[i + 1];
			
			int colour = Figures.getFigureColour(pid);
			if (colour == Figures.COLOUR_WHITE) {
				white.introduceFigure(pid, fieldID, -1, -1);
			} else {
				black.introduceFigure(pid, fieldID, -1, -1);
			}
		}
	}
	
	private void introduceAffected(AffectionData data) {
		int[] affected = data.affected;
		int[] affected_pids = data.affected_pids;
		int size = affected[0];
		if (size > 0) {
			for (int i=0; i<size; i++) {
				int fieldID = affected[i + 1];
				int pid = affected_pids[i + 1];
				
				int[] dirs = data.affected_dirs[fieldID];
				int[] types = data.affected_dirs_types[fieldID];
				
				if (IPlayerAttacks.DEBUG_ATTACKS) {
					if (dirs[0] != types[0] || types[0] == 0) {
						throw new IllegalStateException();
					}
				}

				int colour = Figures.getFigureColour(pid);
				if (colour == Figures.COLOUR_WHITE) {
					for (int j=0; j<dirs[0]; j++) {
						white.introduceFigure(pid, fieldID, dirs[j + 1], types[j + 1]);
					}
				} else {
					for (int j=0; j<dirs[0]; j++) {
						black.introduceFigure(pid, fieldID, dirs[j + 1], types[j + 1]);
					}
				}
			}
		}
	}

	public FieldsStateMachine getFieldAttacksCollector() {
		return fieldAttacksCollector;
	}

	public void checkConsistency() {
		white.checkConsistency();
		black.checkConsistency();
	}
}

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
package bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour;

import java.io.Serializable;

import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.impl.Figures;


public class FieldAttacksFactory implements Serializable {
	
	public static FieldAttacks create(int pa, int kna, int oa, int ma, int ra, int qa, int ka, int xa) {
		return new FieldAttacks(pa, kna, oa, ma, ra, qa, ka, xa);
	}
	
	public static FieldAttacks modify(int operation,
			int figureType, FieldAttacks source) {
		
		FieldAttacks result = source.clone();
		
		if (!result.isConsistent()) {
			throw new IllegalStateException();
		}
		
		if (operation == IFieldsAttacks.OP_ADD_ATTACK) {
			switch(figureType) {
				case Figures.TYPE_PAWN:
					if (result.pa_count < IFieldsAttacks.MAX_PAWN_STATES - 1) {
						result.pa_count++;
					} else {
						throw new IllegalStateException("More than two pawn attacks");
						//result.xa_count++;
					}
					break;
				case Figures.TYPE_KNIGHT:
					if (IFieldsAttacks.MINOR_UNION) {
						if (result.ma_count < IFieldsAttacks.MAX_MINOR_STATES - 1) {
							result.ma_count++;
						} else {
							result.xa_count++;
						}
					} else {
						if (result.kna_count < IFieldsAttacks.MAX_KNIGHT_STATES - 1) {
							result.kna_count++;
						} else {
							result.xa_count++;
						}
					}
					break;
				case Figures.TYPE_OFFICER:
					if (IFieldsAttacks.MINOR_UNION) {
						if (result.ma_count < IFieldsAttacks.MAX_MINOR_STATES - 1) {
							result.ma_count++;
						} else {
							result.xa_count++;
						}
					} else {
						if (result.oa_count < IFieldsAttacks.MAX_OFFICER_STATES - 1) {
							result.oa_count++;
						} else {
							result.xa_count++;
						}
					}
					break;
				case Figures.TYPE_CASTLE:
					if (result.ra_count < IFieldsAttacks.MAX_ROOK_STATES - 1) {
						result.ra_count++;
					} else {
						result.xa_count++;
					}
					break;
				case Figures.TYPE_QUEEN:
					if (result.qa_count < IFieldsAttacks.MAX_QUEEN_STATES - 1) {
						result.qa_count++;
					} else {
						result.xa_count++;
					}
					break;
				case Figures.TYPE_KING:
					if (result.ka_count < IFieldsAttacks.MAX_KING_STATES - 1) {
						result.ka_count++;
					} else {
						throw new IllegalStateException("More than one king attack");
						//result.xa_count++;
					}
					break;
				default:
					throw new IllegalStateException();
			}
		} else if (operation == IFieldsAttacks.OP_REM_ATTACK) {
			switch(figureType) {
				case Figures.TYPE_PAWN:
					if (result.pa_count > 0) { 
						result.pa_count--;
					} else {
						throw new IllegalStateException();
					}
					/*if (result.xa_count > 0 && result.pa_count == IFieldsAttacks.MAX_PAWN_STATES - 1) {
						result.xa_count--;
					} else {
						if (result.pa_count > 0) { 
							result.pa_count--;
						} else {
							throw new IllegalStateException();
						}
					}*/
					break;
				case Figures.TYPE_KNIGHT:
					if (IFieldsAttacks.MINOR_UNION) {
						if (result.xa_count > 0 && result.ma_count == IFieldsAttacks.MAX_MINOR_STATES - 1) {
							result.xa_count--;
						} else {
							if (result.ma_count > 0) { 
								result.ma_count--;
							} else {
								throw new IllegalStateException();
							}
						}
					} else {
						if (result.xa_count > 0 && result.kna_count == IFieldsAttacks.MAX_KNIGHT_STATES - 1) {
							result.xa_count--;
						} else {
							if (result.kna_count > 0) { 
								result.kna_count--;
							} else {
								throw new IllegalStateException();
							}
						}
					}
					break;
				case Figures.TYPE_OFFICER:
					if (IFieldsAttacks.MINOR_UNION) {
						if (result.xa_count > 0 && result.ma_count == IFieldsAttacks.MAX_MINOR_STATES - 1) {
							result.xa_count--;
						} else {
							if (result.ma_count > 0) { 
								result.ma_count--;
							} else {
								throw new IllegalStateException();
							}
						}
					} else {
						if (result.xa_count > 0 && result.oa_count == IFieldsAttacks.MAX_OFFICER_STATES - 1) {
							result.xa_count--;
						} else {
							if (result.oa_count > 0) { 
								result.oa_count--;
							} else {
								throw new IllegalStateException();
							}
						}
					}
					break;
				case Figures.TYPE_CASTLE:
					if (result.xa_count > 0 && result.ra_count == IFieldsAttacks.MAX_ROOK_STATES - 1) {
						result.xa_count--;
					} else {
						if (result.ra_count > 0) { 
							result.ra_count--;
						} else {
							throw new IllegalStateException();
						}
					}
					break;
				case Figures.TYPE_QUEEN:
					if (result.xa_count > 0 && result.qa_count == IFieldsAttacks.MAX_QUEEN_STATES - 1) {
						result.xa_count--;
					} else {
						if (result.qa_count > 0) { 
							result.qa_count--;
						} else {
							throw new IllegalStateException();
						}
					}
					break;
				case Figures.TYPE_KING:
					if (result.ka_count > 0) { 
						result.ka_count--;
					} else {
						throw new IllegalStateException();
					}
					/*if (result.xa_count > 0) {
						throw new IllegalStateException("More than one king attack");
						//result.xa_count--;
					} else {
						if (result.ka_count > 0) { 
							result.ka_count--;
						} else {
							throw new IllegalStateException();
						}
					}*/
					break;
				default:
					throw new IllegalStateException();
			}
		} else {
			throw new IllegalStateException();
		}
		
		if (result.isConsistent()) {
			return result;
		} else {
			return null;
		}
	}
}

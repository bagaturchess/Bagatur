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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.impl.Figures;


class FieldAttacksSMGenerator {
	
	//public static int MIN_BIT_COUNT;
  //public static int MIN_BINARY_SIZE;
	
	/*static {
			 
				MIN_BIT_COUNT = 1;
				while (Math.pow(2, MIN_BIT_COUNT) <= allStatesList.length) {
					MIN_BIT_COUNT++;
				}
				
				MIN_BINARY_SIZE = (int) Math.pow(2, MIN_BIT_COUNT);
				
				System.out.println("MIN_BIT_COUNT=" + MIN_BIT_COUNT);
				System.out.println("MIN_BINARY_SIZE=" + MIN_BINARY_SIZE);
		}	
	}*/
	
	/*public static final int minBinarySize() {
		int pow = 1;
		while (Math.pow(2, pow) <= allStatesList.length) {
			pow++;
		}
		
		return (int) Math.pow(2, pow);
	}*/
	
	static void createStateMachine(FieldAttacksStateMachine result) {
		
		FieldAttacks[] allStatesList = result.allStatesList;
		int[][][] machine = result.machine = new int[IFieldsAttacks.OP_MAX][Figures.TYPE_MAX][allStatesList.length];
		
		//System.out.println("Field states count: " + allStatesList.length);
		
		for (int i = allStatesList.length - 1; i >= 0; i--) {
			
			FieldAttacks cur = allStatesList[i];
			int curID = cur.id;
			
			/*if (curID == 0 ) {
				boolean stop = true;
			}*/
			
			if (cur.pa_count > 2 || cur.pa_count < 0) {
				throw new IllegalStateException();
			}
			
			if (cur.pa_count == 0 || cur.pa_count == 1) {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_PAWN][curID] = getID(result, IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_PAWN, cur);
			} else {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_PAWN][curID] = -1;
			}
			
			if (cur.pa_count == 2 || cur.pa_count == 1) {
				machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_PAWN][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_PAWN, cur);
			} else {
				machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_PAWN][curID] = -1;
			}
			
			/*if (cur.pa_count < IFieldsAttacks.MAX_PAWN_STATES - 1
					|| (onlyOneMaxAttack_exceptKingAndPawns(cur) && cur.xa_count < IFieldsAttacks.MAX_OTHER_STATES - 1)) {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_PAWN][curID] = getID(IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_PAWN, cur);
			} else {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_PAWN][curID] = -1;
			}
			
			if (cur.pa_count == IFieldsAttacks.MAX_PAWN_STATES - 1 && cur.xa_count > 0) {
				machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_PAWN][curID] = getID(IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_PAWN, cur);
			} else {
				if (cur.pa_count > 0) {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_PAWN][curID] = getID(IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_PAWN, cur);;
				} else {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_PAWN][curID] = -1;
				}
			}*/

			if (IFieldsAttacks.MINOR_UNION) {
				
				if (cur.ma_count < IFieldsAttacks.MAX_MINOR_STATES - 1
						|| (onlyOneMaxAttack_exceptKingAndPawns(cur) && cur.xa_count < IFieldsAttacks.MAX_OTHER_STATES - 1)) {
					machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_KNIGHT][curID] = getID(result, IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_KNIGHT, cur);
					machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_OFFICER][curID] = getID(result, IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_OFFICER, cur);
				} else {
					machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_KNIGHT][curID] = -1;
					machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_OFFICER][curID] = -1;
				}
				
				if (cur.ma_count == IFieldsAttacks.MAX_MINOR_STATES - 1 && cur.xa_count > 0) {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KNIGHT][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_KNIGHT, cur);
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_OFFICER][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_OFFICER, cur);
				} else {
					if (cur.ma_count > 0) {
						machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KNIGHT][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_KNIGHT, cur);;
						machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_OFFICER][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_OFFICER, cur);
					} else {
						machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KNIGHT][curID] = -1;
						machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_OFFICER][curID] = -1;
					}
				}
			} else {
				if (cur.kna_count < IFieldsAttacks.MAX_KNIGHT_STATES - 1
						|| (onlyOneMaxAttack_exceptKingAndPawns(cur) && cur.xa_count < IFieldsAttacks.MAX_OTHER_STATES - 1)) {
					machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_KNIGHT][curID] = getID(result, IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_KNIGHT, cur);
				} else {
					machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_KNIGHT][curID] = -1;
				}
				
				if (cur.kna_count == IFieldsAttacks.MAX_KNIGHT_STATES - 1 && cur.xa_count > 0) {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KNIGHT][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_KNIGHT, cur);
				} else {
					if (cur.kna_count > 0) {
						machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KNIGHT][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_KNIGHT, cur);;
					} else {
						machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KNIGHT][curID] = -1;
					}
				}
				
				
				if (cur.oa_count < IFieldsAttacks.MAX_OFFICER_STATES - 1
						|| (onlyOneMaxAttack_exceptKingAndPawns(cur) && cur.xa_count < IFieldsAttacks.MAX_OTHER_STATES - 1)) {
					machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_OFFICER][curID] = getID(result, IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_OFFICER, cur);
				} else {
					machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_OFFICER][curID] = -1;
				}
				
				if (cur.oa_count == IFieldsAttacks.MAX_OFFICER_STATES - 1 && cur.xa_count > 0) {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_OFFICER][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_OFFICER, cur);
				} else {
					if (cur.oa_count > 0) {
						machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_OFFICER][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_OFFICER, cur);;
					} else {
						machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_OFFICER][curID] = -1;
					}
				}
				
			}
			
			
			if (cur.ra_count < IFieldsAttacks.MAX_ROOK_STATES - 1
					|| (onlyOneMaxAttack_exceptKingAndPawns(cur) && cur.xa_count < IFieldsAttacks.MAX_OTHER_STATES - 1)) {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_CASTLE][curID] = getID(result, IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_CASTLE, cur);
			} else {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_CASTLE][curID] = -1;
			}
			
			if (cur.ra_count == IFieldsAttacks.MAX_ROOK_STATES - 1 && cur.xa_count > 0) {
				machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_CASTLE][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_CASTLE, cur);
			} else {
				if (cur.ra_count > 0) {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_CASTLE][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_CASTLE, cur);;
				} else {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_CASTLE][curID] = -1;
				}
			}
			
			
			if (cur.qa_count < IFieldsAttacks.MAX_QUEEN_STATES - 1
					|| (onlyOneMaxAttack_exceptKingAndPawns(cur) && cur.xa_count < IFieldsAttacks.MAX_OTHER_STATES - 1)) {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_QUEEN][curID] = getID(result, IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_QUEEN, cur);
			} else {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_QUEEN][curID] = -1;
			}
			
			if (cur.qa_count == IFieldsAttacks.MAX_QUEEN_STATES - 1 && cur.xa_count > 0) {
				machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_QUEEN][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_QUEEN, cur);
			} else {
				if (cur.qa_count > 0) {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_QUEEN][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_QUEEN, cur);;
				} else {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_QUEEN][curID] = -1;
				}
			}
			
			
			if (cur.ka_count > 1 || cur.ka_count < 0) {
				throw new IllegalStateException();
			}
			
			if (cur.ka_count == 0) {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_KING][curID] = getID(result, IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_KING, cur);
			} else {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_KING][curID] = -1;
			}
			
			if (cur.ka_count == 1) {
				machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KING][curID] = getID(result, IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_KING, cur);
			} else {
				machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KING][curID] = -1;
			}
			
			/*if (cur.ka_count < IFieldsAttacks.MAX_KING_STATES - 1
					|| (onlyOneMaxAttack(cur) && cur.xa_count < IFieldsAttacks.MAX_OTHER_STATES - 1)) {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_KING][curID] = getID(IFieldsAttacks.OP_ADD_ATTACK, Figures.TYPE_KING, cur);
			} else {
				machine[IFieldsAttacks.OP_ADD_ATTACK][Figures.TYPE_KING][curID] = -1;
			}
			
			if (cur.ka_count == IFieldsAttacks.MAX_KING_STATES - 1 && cur.xa_count > 0) {
				machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KING][curID] = getID(IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_KING, cur);
			} else {
				if (cur.ka_count > 0) {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KING][curID] = getID(IFieldsAttacks.OP_REM_ATTACK, Figures.TYPE_KING, cur);;
				} else {
					machine[IFieldsAttacks.OP_REM_ATTACK][Figures.TYPE_KING][curID] = -1;
				}
			}*/
		}
	}
	
	private static int getID(FieldAttacksStateMachine result, int operation, int type, FieldAttacks current) {
		FieldAttacks modified = FieldAttacksFactory.modify(operation, type, current);
		return getID(result, modified);
	}
	
	private static int getID(FieldAttacksStateMachine result, FieldAttacks toSearch) {
		
		FieldAttacks found = result.allStatesMap.get(toSearch);
		
		if (found != null) {
			return found.id;
		}
		
		/*for (int i = allStatesList.length - 1; i >= 0; i--) {
			FieldAttacks_SingleColour cur = allStatesList[i];
			if (cur.equals(toSearch)) {
				return cur.id;
			}
		}*/
		
		return -1;
	}
	
	static void getAllFieldsAttacks(FieldAttacksStateMachine result) {
		
		int size = 1000;
		
		List<FieldAttacks> all = new ArrayList<FieldAttacks>(size);
		Map<FieldAttacks, FieldAttacks> allStatesMap = result.allStatesMap = new HashMap<FieldAttacks, FieldAttacks>(2 * size);
		
		int max_pa = IFieldsAttacks.MAX_PAWN_STATES;
		int max_kna = IFieldsAttacks.MAX_KNIGHT_STATES;
		int max_oa = IFieldsAttacks.MAX_OFFICER_STATES;
		int max_ma = IFieldsAttacks.MAX_MINOR_STATES;
		int max_ra = IFieldsAttacks.MAX_ROOK_STATES;
		int max_qa = IFieldsAttacks.MAX_QUEEN_STATES;
		int max_ka = IFieldsAttacks.MAX_KING_STATES;
		int max_xa = IFieldsAttacks.MAX_OTHER_STATES;
		
		for (int pa = 0; pa < max_pa; pa++) {
			for (int ra = 0; ra < max_ra; ra++) {
				for (int qa = 0; qa < max_qa; qa++) {
					for (int ka = 0; ka < max_ka; ka++) {
						
						if (IFieldsAttacks.MINOR_UNION) {
							
							for (int ma = 0; ma < max_ma; ma++) {
											
								if (onlyOneMaxAttack_exceptKingAndPawns(pa, -1, -1, ma, ra, qa, ka)) {
									for (int xa = 0; xa < max_xa; xa++) {
										FieldAttacks cur = FieldAttacksFactory.create(pa, -1, -1, ma, ra, qa, ka, xa);
										if (!cur.isConsistent()) {
											throw new IllegalStateException();
										}
										
										all.add(cur);
										allStatesMap.put(cur, cur);
									}
								} else {
									FieldAttacks cur = FieldAttacksFactory.create(pa, -1, -1, ma, ra, qa, ka, 0);
									if (!cur.isConsistent()) {
										throw new IllegalStateException();
									}

									all.add(cur);
									allStatesMap.put(cur, cur);
								}
							}
							
						} else {
						
							for (int kna = 0; kna < max_kna; kna++) {
								for (int oa = 0; oa < max_oa; oa++) {
											
									if (onlyOneMaxAttack_exceptKingAndPawns(pa, kna, oa, -1, ra, qa, ka)) {
										for (int xa = 0; xa < max_xa; xa++) {
											FieldAttacks cur = FieldAttacksFactory.create(pa, kna, oa, -1, ra, qa, ka, xa);
											if (!cur.isConsistent()) {
												throw new IllegalStateException();
											}
											
											all.add(cur);
											allStatesMap.put(cur, cur);
										}
									} else {
										FieldAttacks cur = FieldAttacksFactory.create(pa, kna, oa, -1, ra, qa, ka, 0);
										if (!cur.isConsistent()) {
											throw new IllegalStateException();
										}
	
										all.add(cur);
										allStatesMap.put(cur, cur);
									}
								}
							}
							
						}
					}
				}
			}
		}
		
		result.allStatesList = (FieldAttacks[]) all.toArray(new FieldAttacks[0]);
		Arrays.sort(result.allStatesList);
		
		for (int i=result.allStatesList.length-1; i>=0; i--) {
			result.allStatesList[i].id = i;		
			//System.out.println(allStatesList[i]);
		}
	}
	
	private static boolean onlyOneMaxAttack_exceptKingAndPawns(FieldAttacks state) {
		return onlyOneMaxAttack_exceptKingAndPawns(state.pa_count, state.kna_count, state.oa_count, state.ma_count, state.ra_count , state.qa_count , state.ka_count);
	}
	
	private static boolean onlyOneMaxAttack_exceptKingAndPawns(int pa, int kna, int oa, int ma, int ra, int qa, int ka) {
		if (pa >= IFieldsAttacks.MAX_PAWN_STATES) {
			throw new IllegalStateException();
		}
		
		if (IFieldsAttacks.MINOR_UNION) {
			if (ma >= IFieldsAttacks.MAX_MINOR_STATES) {
				throw new IllegalStateException();
			}
		} else {
			if (kna >= IFieldsAttacks.MAX_KNIGHT_STATES) {
				throw new IllegalStateException();
			}
			if (oa >= IFieldsAttacks.MAX_OFFICER_STATES) {
				throw new IllegalStateException();
			}
		}
		
		if (ra >= IFieldsAttacks.MAX_ROOK_STATES) {
			throw new IllegalStateException();
		}
		if (qa >= IFieldsAttacks.MAX_QUEEN_STATES) {
			throw new IllegalStateException();
		}
		if (ka >= IFieldsAttacks.MAX_KING_STATES) {
			throw new IllegalStateException();
		}
		
		if (IFieldsAttacks.MINOR_UNION) {
			
			if (//pa < IFieldsAttacks.MAX_PAWN_STATES - 1
					ma == IFieldsAttacks.MAX_MINOR_STATES - 1
					&& ra < IFieldsAttacks.MAX_ROOK_STATES - 1
					&& qa < IFieldsAttacks.MAX_QUEEN_STATES - 1
					//&& ka < IFieldsAttacks.MAX_KING_STATES - 1
					) {
				return true;
			}
			
			if (//pa < IFieldsAttacks.MAX_PAWN_STATES - 1
					ma < IFieldsAttacks.MAX_MINOR_STATES - 1
					&& ra == IFieldsAttacks.MAX_ROOK_STATES - 1
					&& qa < IFieldsAttacks.MAX_QUEEN_STATES - 1
					//&& ka < IFieldsAttacks.MAX_KING_STATES - 1
					) {
				return true;
			}
	
			if (//pa < IFieldsAttacks.MAX_PAWN_STATES - 1
					ma < IFieldsAttacks.MAX_MINOR_STATES - 1
					&& ra < IFieldsAttacks.MAX_ROOK_STATES - 1
					&& qa == IFieldsAttacks.MAX_QUEEN_STATES - 1
					//&& ka < IFieldsAttacks.MAX_KING_STATES - 1
					) {
				return true;
			}

		} else {
			
			if (//pa < IFieldsAttacks.MAX_PAWN_STATES - 1
					kna == IFieldsAttacks.MAX_KNIGHT_STATES - 1
					&& oa < IFieldsAttacks.MAX_OFFICER_STATES - 1
					&& ra < IFieldsAttacks.MAX_ROOK_STATES - 1
					&& qa < IFieldsAttacks.MAX_QUEEN_STATES - 1
					//&& ka < IFieldsAttacks.MAX_KING_STATES - 1
					) {
				return true;
			}
			
			if (//pa < IFieldsAttacks.MAX_PAWN_STATES - 1
					kna < IFieldsAttacks.MAX_KNIGHT_STATES - 1
					&& oa == IFieldsAttacks.MAX_OFFICER_STATES - 1
					&& ra < IFieldsAttacks.MAX_ROOK_STATES - 1
					&& qa < IFieldsAttacks.MAX_QUEEN_STATES - 1
					//&& ka < IFieldsAttacks.MAX_KING_STATES - 1
					) {
				return true;
			}
			
			if (//pa < IFieldsAttacks.MAX_PAWN_STATES - 1
					kna < IFieldsAttacks.MAX_KNIGHT_STATES - 1
					&& oa < IFieldsAttacks.MAX_OFFICER_STATES - 1
					&& ra == IFieldsAttacks.MAX_ROOK_STATES - 1
					&& qa < IFieldsAttacks.MAX_QUEEN_STATES - 1
					//&& ka < IFieldsAttacks.MAX_KING_STATES - 1
					) {
				return true;
			}
	
			if (//pa < IFieldsAttacks.MAX_PAWN_STATES - 1
					kna < IFieldsAttacks.MAX_KNIGHT_STATES - 1
					&& oa < IFieldsAttacks.MAX_OFFICER_STATES - 1
					&& ra < IFieldsAttacks.MAX_ROOK_STATES - 1
					&& qa == IFieldsAttacks.MAX_QUEEN_STATES - 1
					//&& ka < IFieldsAttacks.MAX_KING_STATES - 1
					) {
				return true;
			}
		}

		return false;
	}
	
	public static void main(String[] args) {
		
		//int[][][] machine = StateMachinesGenerator.createStateMachine();
		
	}
}

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
package bagaturchess.search.impl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import bagaturchess.bitboard.impl.Figures;


public class MoveEvalStat {
	
	private static final int SHIFT = 50;
	
	private Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
	
	private SliderVariable evals_w_top1_noncap_pv    = new SliderVariable("evals_w_top1_noncap_pv    ", 1, 512, 500);
	//private SliderVariable evals_w_top1_cap_pv       = new SliderVariable("evals_w_top1_cap_pv       ", 2, 99, 2000);
	private SliderVariable evals_w_top1_noncap_nonpv = new SliderVariable("evals_w_top1_noncap_nonpv ", 1, 1024, 500);
	//private SliderVariable evals_w_top1_cap_nonpv    = new SliderVariable("evals_w_top1_cap_nonpv    ", 1, 99, 2000);
	
	private SliderVariable evals_w_top2_noncap_pv    = new SliderVariable("evals_w_top2_noncap_pv    ", 1, 35, 500);
	//private SliderVariable evals_w_top2_cap_pv       = new SliderVariable("evals_w_top2_cap_pv       ", 20, 80, 2000);
	private SliderVariable evals_w_top2_noncap_nonpv = new SliderVariable("evals_w_top2_noncap_nonpv ", 1, 70, 500);
	//private SliderVariable evals_w_top2_cap_nonpv    = new SliderVariable("evals_w_top2_cap_nonpv    ", 10, 80, 2000);
	
	private SliderVariable evals_b_top1_noncap_pv    = new SliderVariable("evals_b_top1_noncap_pv    ", 1, 512, 500);
	//private SliderVariable evals_b_top1_cap_pv       = new SliderVariable("evals_b_top1_cap_pv       ", 1, 99, 2000);
	private SliderVariable evals_b_top1_noncap_nonpv = new SliderVariable("evals_b_top1_noncap_nonpv ", 1, 1024, 500);
	//private SliderVariable evals_b_top1_cap_nonpv    = new SliderVariable("evals_b_top1_cap_nonpv    ", 1, 99, 2000);
	
	private SliderVariable evals_b_top2_noncap_pv    = new SliderVariable("evals_b_top2_noncap_pv    ", 1, 35, 500);
	//private SliderVariable evals_b_top2_cap_pv       = new SliderVariable("evals_b_top2_cap_pv       ", 20, 80, 2000);
	private SliderVariable evals_b_top2_noncap_nonpv = new SliderVariable("evals_b_top2_noncap_nonpv ", 1, 70, 500);
	//private SliderVariable evals_b_top2_cap_nonpv    = new SliderVariable("evals_b_top2_cap_nonpv    ", 10, 80, 2000);
	
	private SliderVariable evals_b_researched = new SliderVariable("evals_b_researched    ", 19, 1, 500);
	private SliderVariable evals_w_researched = new SliderVariable("evals_w_researched    ", 19, 1, 500);
	
	private int max_w_noncap_pv = 0;
	private int max_b_noncap_pv = 0;
	
	@Override
	public String toString() {
		String msg = "";
		
		msg += evals_w_top1_noncap_pv + "\r\n";
		//msg += evals_w_top1_cap_pv + "\r\n";
		//msg += evals_w_top1_noncap_nonpv + "\r\n";
		//msg += evals_w_top1_cap_nonpv + "\r\n";
		msg += evals_w_top2_noncap_pv + "\r\n";
		//msg += evals_w_top2_cap_pv + "\r\n";
		//msg += evals_w_top2_noncap_nonpv + "\r\n";
		//msg += evals_w_top2_cap_nonpv + "\r\n";
		msg += evals_b_top1_noncap_pv + "\r\n";
		//msg += evals_b_top1_cap_pv + "\r\n";
		//msg += evals_b_top1_noncap_nonpv + "\r\n";
		//msg += evals_b_top1_cap_nonpv + "\r\n";
		msg += evals_b_top2_noncap_pv + "\r\n";
		//msg += evals_b_top2_cap_pv + "\r\n";
		//msg += evals_b_top2_noncap_nonpv + "\r\n";
		//msg += evals_b_top2_cap_nonpv + "\r\n";
		
		msg += evals_b_researched + "\r\n";
		msg += evals_w_researched + "\r\n";
		
		/*for(Integer eval: map.keySet()) {
			Integer count = map.get(eval);
			msg += eval + "	" + count + "\r\n";
		}
		msg += "END\r\n";
		*/
		
		return msg;
	}
	
	public int getResearched(int colour) {
		if (colour == Figures.COLOUR_WHITE) {
			return evals_w_researched.getPointer();
		} else {
			return evals_b_researched.getPointer();
		}
	}
	
	public void addResearched(int colour, int eval) {
		
		if (eval <= 0) {
			eval = 1;
		}
		
		//if (!isCapOrProm) {
			if (eval >= 500) {
				eval = 499;
			}
		//} else {
		//	if (eval >= 2000) {
		//		eval = 1999;
		//	}
		//}
		
		if (colour == Figures.COLOUR_WHITE) {
			evals_w_researched.updatePositive(eval);
		} else {
			evals_b_researched.updatePositive(eval);
		}
	}
	
	public int getMaxNonCapPv(int colour) {
		if (colour == Figures.COLOUR_WHITE) {
			return (max_w_noncap_pv - SHIFT) / 2;
		} else {
			return (max_b_noncap_pv - SHIFT) / 2;
		}
	}
	
	public void addMoveEval(int eval, int colour, boolean isCapOrProm, boolean pv) {
		
		eval += SHIFT;
		
		if (eval <= 0) {
			eval = 1;
		}
		
		if (!isCapOrProm) {
			if (eval >= 500) {
				eval = 499;
			}
		} else {
			if (eval >= 2000) {
				eval = 1999;
			}
		}
		
		if (colour == Figures.COLOUR_WHITE) {
			
			if (eval > max_w_noncap_pv) {
				max_w_noncap_pv = eval;
			}
			
			if (isCapOrProm) {
				throw new IllegalStateException();
				/*if (pv) {
					evals_w_top1_cap_pv.updatePositive(eval);
					evals_w_top2_cap_pv.updatePositive(eval);
				} else {
					evals_w_top1_cap_nonpv.updatePositive(eval);
					evals_w_top2_cap_nonpv.updatePositive(eval);
				}*/
			} else {
				if (pv) {
					evals_w_top1_noncap_pv.updatePositive(eval);
					evals_w_top2_noncap_pv.updatePositive(eval);
				} else {
					//throw new IllegalStateException();
					evals_w_top1_noncap_nonpv.updatePositive(eval);
					evals_w_top2_noncap_nonpv.updatePositive(eval);
				}
			}	
		} else {
			
			if (eval > max_b_noncap_pv) {
				max_b_noncap_pv = eval;
			}
			
			if (isCapOrProm) {
				throw new IllegalStateException();
				/*if (pv) {
					evals_b_top1_cap_pv.updatePositive(eval);
					evals_b_top2_cap_pv.updatePositive(eval);
				} else {
					evals_b_top1_cap_nonpv.updatePositive(eval);
					evals_b_top2_cap_nonpv.updatePositive(eval);
				}
				*/
			} else {
				if (pv) {
					evals_b_top1_noncap_pv.updatePositive(eval);
					evals_b_top2_noncap_pv.updatePositive(eval);
				} else {
					//throw new IllegalStateException();
					evals_b_top1_noncap_nonpv.updatePositive(eval);
					evals_b_top2_noncap_nonpv.updatePositive(eval);					
				}
			}
		}
	}
	
	public int getTop1Eval(int colour, boolean isCapOrProm, boolean pv) {
		
		int result = 0;
		
		if (colour == Figures.COLOUR_WHITE) {
			if (isCapOrProm) {
				if (pv) {
					throw new IllegalStateException();
					//result = evals_w_top1_cap_pv.getPointer();
				} else {
					throw new IllegalStateException();
					//result = evals_w_top1_cap_nonpv.getPointer();
				}
			} else {
				if (pv) {
					result = evals_w_top1_noncap_pv.getPointer();
				} else {
					//throw new IllegalStateException();
					result = evals_w_top1_noncap_nonpv.getPointer();				
				}
			}	
		} else {
			if (isCapOrProm) {
				if (pv) {
					throw new IllegalStateException();
					//result = evals_b_top1_cap_pv.getPointer();
				} else {
					throw new IllegalStateException();
					//result = evals_b_top1_cap_nonpv.getPointer();
				}
			} else {
				if (pv) {
					result = evals_b_top1_noncap_pv.getPointer();
				} else {
					//throw new IllegalStateException();
					result = evals_b_top1_noncap_nonpv.getPointer();			
				}
			}
		}
		
		return result - SHIFT;
	}
	
	public int getTop2Eval(int colour, boolean isCapOrProm, boolean pv) {
		
		int result = 0;
		
		if (colour == Figures.COLOUR_WHITE) {
			if (isCapOrProm) {
				if (pv) {
					throw new IllegalStateException();
					//result = evals_w_top2_cap_pv.getPointer();
				} else {
					throw new IllegalStateException();
					//result = evals_w_top2_cap_nonpv.getPointer();
				}
			} else {
				if (pv) {
					result = evals_w_top2_noncap_pv.getPointer();
				} else {
					//throw new IllegalStateException();
					result = evals_w_top2_noncap_nonpv.getPointer();				
				}
			}	
		} else {
			if (isCapOrProm) {
				if (pv) {
					throw new IllegalStateException();
					//result = evals_b_top2_cap_pv.getPointer();
				} else {
					throw new IllegalStateException();
					//result = evals_b_top2_cap_nonpv.getPointer();
				}
			} else {
				if (pv) {
					result = evals_b_top2_noncap_pv.getPointer();
				} else {
					//throw new IllegalStateException();
					result = evals_b_top2_noncap_nonpv.getPointer();			
				}
			}
		}
		
		return result - SHIFT;
	}
	
	public void addMoveStat(int eval) {
		if (map.containsKey(eval)) {
			map.put(eval, map.get(eval) + 1);
		} else {
			map.put(eval, 1);
		}
	}
}

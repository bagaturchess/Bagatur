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
package bagaturchess.search.impl.movelists;


public class OrderingStatistics {
	
	public static final int MAX_VAL = 10000;
	private static final int NORM = 2;
	
	public long tpt_count;
	public long tpt_best;
	
	public long wincap_count;
	public long wincap_best;
	
	public long eqcap_count;
	public long eqcap_best;
	
	public long counter_count;
	public long counter_best;
	
	public long matekiller_count;
	public long matekiller_best;
	
	public long prevbest_count;
	public long prevbest_best;
	
	public long matemove_count;
	public long matemove_best;
	
	public long killer_count;
	public long killer_best;
	
	public long passer_count;
	public long passer_best;
	
	public long prevpv_count;
	public long prevpv_best;

	public long castling_count;
	public long castling_best;
	
	public long losecap_count;
	public long losecap_best;
	
	public double history_count;
	public double history_best;
	
	public double pst_count;
	public double pst_best;
	
	
	public OrderingStatistics() {
		clear();
	}
	
	private void clear() {
		tpt_count = 1;
		tpt_best = 0;
		
		wincap_count = 1;
		wincap_best = 0;
		
		eqcap_count = 1;
		eqcap_best = 0;
		
		counter_count = 1;
		counter_best = 0;
		
		matekiller_count = 1;
		matekiller_best = 0;
		
		prevbest_count = 1;
		prevbest_best = 0;
		
		matemove_count = 1;
		matemove_best = 0;
		
		killer_count = 1;
		killer_best = 0;
		
		passer_count = 1;
		passer_best = 0;
		
		prevpv_count = 1;
		prevpv_best = 0;

		castling_count = 1;
		castling_best = 0;
		
		losecap_count = 1;
		losecap_best = 0;
		
		history_count = 1;
		history_best = 0;
		
		pst_count = 1;
		pst_best = 0;
	}
	
	
	public int getOrdVal_TPT() {
		return (int) (MAX_VAL * (tpt_best / (double) tpt_count));
	}
	
	public int getOrdVal_WINCAP() {
		return (int) (MAX_VAL * (wincap_best / (double) wincap_count));
	}
	
	public int getOrdVal_EQCAP() {
		return (int) (MAX_VAL * (eqcap_best / (double) eqcap_count));
	}
	
	public int getOrdVal_COUNTER() {
		return (int) (MAX_VAL * (counter_best / (double) counter_count));
	}
	
	public int getOrdVal_MATEKILLER() {
		return (int) (MAX_VAL * (matekiller_best / (double) matekiller_count));
	}
	
	public int getOrdVal_PREVBEST() {
		return (int) (MAX_VAL * (prevbest_best / (double) prevbest_count));
	}

	public int getOrdVal_MATEMOVE() {
		return (int) (MAX_VAL * (matemove_best / (double) matemove_count));
	}
	
	public int getOrdVal_KILLER() {
		return (int) (MAX_VAL * (killer_best / (double) killer_count));
	}
	
	public int getOrdVal_PASSER() {
		return (int) (MAX_VAL * (passer_best / (double) passer_count));
	}
	
	public int getOrdVal_PREVPV() {
		return (int) (MAX_VAL * (prevpv_best / (double) prevpv_count));
	}
	
	public int getOrdVal_CASTLING() {
		return (int) (MAX_VAL * (castling_best / (double) castling_count));
	}
	
	public int getOrdVal_LOSECAP() {
		return - (int) (MAX_VAL * (losecap_best / (double) losecap_count));
	}
	
	public int getOrdVal_HISTORY() {
		return (int) (MAX_VAL * (history_best / (double) history_count));
	}

	public int getOrdVal_PST() {
		return (int) (MAX_VAL * (pst_best / (double) pst_count));
	}

	
	@Override
	public String toString() {
		String msg = "";
		
		msg += "TPT        :	" + tpt_count + "	" + tpt_best + "	" + (tpt_best / (double) tpt_count) + "\r\n";
		msg += "WINCAP     :	" + wincap_count + "	" + wincap_best + "	" + (wincap_best / (double) wincap_count) + "\r\n";
		msg += "EQCAP      :	" + eqcap_count + "	" + eqcap_best + "	" + (eqcap_best / (double) eqcap_count) + "\r\n";
		msg += "COUNTER    :	" + counter_count + "	" + counter_best + "	" + (counter_best / (double) counter_count) + "\r\n";
		msg += "MATEKILLER :	" + matekiller_count + "	" + matekiller_best + "	" + (matekiller_best / (double) matekiller_count) + "\r\n";
		msg += "PREVBEST   :	" + prevbest_count + "	" + prevbest_best + "	" + (prevbest_best / (double) prevbest_count) + "\r\n";
		msg += "MATEMOVE   :	" + matemove_count + "	" + matemove_best + "	" + (matemove_best / (double) matemove_count) + "\r\n";
		msg += "KILLER     :	" + killer_count + "	" + killer_best + "	" + (killer_best / (double) killer_count) + "\r\n";
		msg += "PASSER     :	" + passer_count + "	" + passer_best + "	" + (passer_best / (double) passer_count) + "\r\n";
		msg += "PREVPV     :	" + prevpv_count + "	" + prevpv_best + "	" + (prevpv_best / (double) prevpv_count) + "\r\n";
		msg += "CASTLING   :	" + castling_count + "	" + castling_best + "	" + (castling_best / (double) castling_count) + "\r\n";
		msg += "LOSECAP    :	" + losecap_count + "	" + losecap_best + "	" + ((/*losecap_count-*/losecap_best) / (double) losecap_count) + "\r\n";
		msg += "HISTORY    :	" + (int)history_count + "	" + (int)history_best + "	" + (history_best / (double) history_count) + "\r\n";
		msg += "PST        :	" + (int)pst_count + "	" + (int)pst_best + "	" + (pst_best / (double) pst_count) + "\r\n";
		
		return msg;
	}
	
	public void normalize() {
		tpt_count = Math.max(1, tpt_count / NORM);
		tpt_best /= NORM;
		
		wincap_count = Math.max(1, wincap_count / NORM);
		wincap_best /= NORM;
		
		eqcap_count = Math.max(1, eqcap_count / NORM);
		eqcap_best /= NORM;
		
		counter_count = Math.max(1, counter_count / NORM);
		counter_best /= NORM;
		
		matekiller_count = Math.max(1, matekiller_count / NORM);
		matekiller_best /= NORM;
		
		prevbest_count = Math.max(1, prevbest_count / NORM);
		prevbest_best /= NORM;
		
		matemove_count = Math.max(1, matemove_count / NORM);
		matemove_best /= NORM;
		
		killer_count = Math.max(1, killer_count / NORM);
		killer_best /= NORM;
		
		passer_count = Math.max(1, passer_count / NORM);
		passer_best /= NORM;
		
		prevpv_count = Math.max(1, prevpv_count / NORM);
		prevpv_best /= NORM;
		
		castling_count = Math.max(1, castling_count / NORM);
		castling_best /= NORM;
		
		losecap_count = Math.max(1, losecap_count / NORM);
		losecap_best /= NORM;
		
		history_count = Math.max(1, history_count / NORM);
		history_best /= NORM;
		
		pst_count = Math.max(1, pst_count / NORM);
		pst_best /= NORM;
	}
}

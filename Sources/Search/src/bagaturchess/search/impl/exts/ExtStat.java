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
package bagaturchess.search.impl.exts;

public class ExtStat {
	
	public int pv_InCheck;
	public int pv_Prom;
	public int pv_PasserPush;
	public int pv_CapNonPawn;
	public int pv_CapPawn;
	public int pv_SingleMove;
	public int pv_GoodMove;
	
	public int nonpv_InCheck;
	public int nonpv_Prom;
	public int nonpv_PasserPush;
	public int nonpv_CapNonPawn;
	public int nonpv_CapPawn;
	public int nonpv_SingleMove;
	public int nonpv_Mate;
	public int nonpv_GoodMove;
	
	@Override
	public String toString() {
		String res = "";
		
		res += "pv_InCheck       " + pv_InCheck + "\r\n";
		res += "pv_Prom          " + pv_Prom + "\r\n";
		res += "pv_PasserPush    " + pv_PasserPush + "\r\n";
		res += "pv_CapNonPawn    " + pv_CapNonPawn + "\r\n";
		res += "pv_CapPawn       " + pv_CapPawn + "\r\n";
		res += "pv_SingleMove    " + pv_SingleMove + "\r\n";
		res += "pv_GoodMove      " + pv_GoodMove + "\r\n";
		
		res += "nonpv_InCheck    " + nonpv_InCheck + "\r\n";
		res += "nonpv_Prom       " + nonpv_Prom + "\r\n";
		res += "nonpv_PasserPush " + nonpv_PasserPush + "\r\n";
		res += "nonpv_CapNonPawn " + nonpv_CapNonPawn + "\r\n";
		res += "nonpv_CapPawn    " + nonpv_CapPawn + "\r\n";
		res += "nonpv_SingleMove " + nonpv_SingleMove + "\r\n";
		res += "nonpv_Mate       " + nonpv_Mate + "\r\n";
		res += "nonpv_GoodMove   " + nonpv_GoodMove + "\r\n";
		
		return res;
	}
}

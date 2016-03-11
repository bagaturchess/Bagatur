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
package bagaturchess.uci.impl.commands.info;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class Info {
	
	
	private String[] pv;
	private int eval;
	private boolean mate;
	
	
	public Info(String infoLine) {
		
		//System.out.println(infoLine);
		
		/**
		 * Extract pricipal variation
		 */
		int pvStart = infoLine.indexOf(" pv ");
		if (pvStart <= 0) {
			throw new IllegalStateException();
		}
		
		String pvLine = infoLine.substring(pvStart + 4, infoLine.length());
		List<String> movesList = new ArrayList<String>();
		StringTokenizer movesString = new StringTokenizer(pvLine, " ");
		while (movesString.hasMoreElements()) {
			String moveStr = movesString.nextToken();
			movesList.add(moveStr);
		}
		pv = movesList.toArray(new String[0]);
		
		
		/**
		 * Extract scores
		 */
		//Example for mate: info depth 1 seldepth 7 score mate 1 time 0 nodes 22 pv f6e4
		//int scoreStart = infoLine.indexOf("score cp ");
		int scoreStart = infoLine.indexOf(" score ");
		if (scoreStart <= 0) {
			throw new IllegalStateException();
		}
		int cpOrMateStart = infoLine.indexOf(" ", scoreStart + 6);
		
		if (infoLine.indexOf(" mate ", cpOrMateStart) > 0) {
			mate = true;
			int scoreEnd = infoLine.indexOf(" ", cpOrMateStart + 6);
			String number = infoLine.substring(cpOrMateStart + 6, scoreEnd);
			eval = Integer.parseInt(number);			
		} else if (infoLine.indexOf(" cp ", cpOrMateStart) > 0) {
			mate = false;
			int scoreEnd = infoLine.indexOf(" ", cpOrMateStart + 4);
			String number = infoLine.substring(cpOrMateStart + 4, scoreEnd);
			eval = Integer.parseInt(number);
		} else {
			throw new IllegalStateException(infoLine);
		}
	}
	
	public int getEval() {
		return eval;
	}

	public boolean isMate() {
		return mate;
	}

	public String[] getPv() {
		return pv;
	}
	
	@Override
	public String toString() {
		
		String result = "";
		
		result += "INFO: [";
		
		for (int i=0; i<pv.length; i++) {
			result += pv[i] + " "; 
		}
		
		if (mate) {
			result += " " + "mate";
		}
		
		result += "] EVAL: " + eval;
		
		return result;
	}
	
	public static void main(String[] args) {
		//Info info = new Info("info depth 1 seldepth 7 score mate 12 time 0 nodes 22 pv f6e4 h2h4");
		Info info = new Info("info depth 1 seldepth 7 score cp 12345 time 0 nodes 22 pv f6e4 h2h4");
		System.out.println(info);
	}
}

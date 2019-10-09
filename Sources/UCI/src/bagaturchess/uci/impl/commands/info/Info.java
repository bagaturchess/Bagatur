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
	private int depth;
	private int seldepth;
	private long nodes;
	private String currmove;
	private int currmovenumber;
	private int hashfull;
	
	
	public Info(String infoLine) {
		
		//System.out.println(infoLine);
		
		/**
		 * Extract pricipal variation
		 */
		int pvStart = infoLine.indexOf(" pv ");
		/*if (pvStart <= 0) {
			throw new IllegalStateException();
		}*/
		
		if (pvStart >= 0) {
			String pvLine = infoLine.substring(pvStart + 4, infoLine.length());
			List<String> movesList = new ArrayList<String>();
			StringTokenizer movesString = new StringTokenizer(pvLine, " ");
			while (movesString.hasMoreElements()) {
				String moveStr = movesString.nextToken();
				movesList.add(moveStr);
			}
			pv = movesList.toArray(new String[0]);
		}
		
		/**
		 * Extract scores
		 */
		//Example for mate: info depth 1 seldepth 7 score mate 1 time 0 nodes 22 pv f6e4
		//int scoreStart = infoLine.indexOf("score cp ");
		int scoreStart = infoLine.indexOf(" score ");
		/*if (scoreStart <= 0) {
			throw new IllegalStateException();
		}*/
		
		if (scoreStart >= 0) {
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
		
		/**
		 * Extract depth
		 */
		int depthStart = infoLine.indexOf(" depth ");
		if (depthStart >= 0) {
			int depthNumberStart = infoLine.indexOf(" ", depthStart + 6);
			if (depthNumberStart < 0) {
				throw new IllegalStateException("depthNumberStart=" + depthNumberStart);
			}
			int depthNumberEnd = getEndIndex(infoLine, depthNumberStart);
			String number = infoLine.substring(depthNumberStart + 1, depthNumberEnd).trim();
			depth = Integer.parseInt(number);
		}
		
		
		/**
		 * Extract selective depth
		 */
		int seldepthStart = infoLine.indexOf(" seldepth ");
		if (seldepthStart >= 0) {
			int seldepthNumberStart = infoLine.indexOf(" ", seldepthStart + 9);
			if (seldepthNumberStart < 0) {
				throw new IllegalStateException("seldepthNumberStart=" + seldepthNumberStart);
			}
			int seldepthNumberEnd = getEndIndex(infoLine, seldepthNumberStart);
			String number = infoLine.substring(seldepthNumberStart + 1, seldepthNumberEnd).trim();
			seldepth = Integer.parseInt(number);
		}
		
		
		/**
		 * Extract nodes
		 */
		int nodesStart = infoLine.indexOf(" nodes ");
		if (nodesStart >= 0) {
			int nodesNumberStart = infoLine.indexOf(" ", nodesStart + 6);
			if (nodesNumberStart < 0) {
				throw new IllegalStateException("nodesNumberStart=" + nodesNumberStart);
			}
			int nodesNumberEnd = getEndIndex(infoLine, nodesNumberStart);
			String number = infoLine.substring(nodesNumberStart + 1, nodesNumberEnd).trim();
			nodes = Long.parseLong(number);
		}
		
		
		/**
		 * Extract currmove
		 */
		int currmoveStart = infoLine.indexOf(" currmove ");
		if (currmoveStart >= 0) {
			int currmoveNumberStart = infoLine.indexOf(" ", currmoveStart + 8);
			if (currmoveNumberStart < 0) {
				throw new IllegalStateException("currmoveNumberStart=" + currmoveNumberStart);
			}
			int currmoveNumberEnd = getEndIndex(infoLine, currmoveNumberStart);
			currmove = infoLine.substring(currmoveNumberStart + 1, currmoveNumberEnd).trim();
		}
		
		
		/**
		 * Extract currmovenumber
		 */
		int currmovenumberStart = infoLine.indexOf(" currmovenumber ");
		if (currmovenumberStart >= 0) {
			int currmoveNumberNumberStart = infoLine.indexOf(" ", currmovenumberStart + 8);
			if (currmoveNumberNumberStart < 0) {
				throw new IllegalStateException("currmoveNumberNumberStart=" + currmoveNumberNumberStart);
			}
			int currmoveNumberNumberEnd = getEndIndex(infoLine, currmoveNumberNumberStart);
			String number = infoLine.substring(currmoveNumberNumberStart + 1, currmoveNumberNumberEnd).trim();
			currmovenumber = Integer.parseInt(number);
		}
		
		
		/**
		 * Extract hashfull
		 */
		int hashfullStart = infoLine.indexOf(" hashfull ");
		if (hashfullStart >= 0) {
			int hashfullNumberStart = infoLine.indexOf(" ", hashfullStart + 9);
			if (hashfullNumberStart < 0) {
				throw new IllegalStateException("hashfullNumberStart=" + hashfullNumberStart);
			}
			int hashfullNumberEnd = getEndIndex(infoLine, hashfullNumberStart);
			String number = infoLine.substring(hashfullNumberStart + 1, hashfullNumberEnd).trim();
			hashfull = Integer.parseInt(number);
		}
	}

	private int getEndIndex(String infoLine, int start) {
		
		int index = infoLine.indexOf(" ", start + 1);
		if (index < 0) {
			index = infoLine.length();
		}
		
		return index;
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

	public int getDepth() {
		return depth;
	}

	public int getSelDepth() {
		return seldepth;
	}

	public long getNodes() {
		return nodes;
	}

	public String getCurrmove() {
		return currmove;
	}
	
	public int getCurrmoveNumber() {
		return currmovenumber;
	}
	
	public int getHashfull() {
		return hashfull;
	}
	
	
	@Override
	public String toString() {
		
		String result = "";
		
		result += "INFO: [";
		
		result += "depth " + depth + " ";
		
		result += "pv ";
				
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

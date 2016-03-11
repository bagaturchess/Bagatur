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

import bagaturchess.bitboard.impl.Figures;
import bagaturchess.search.api.IExtensionMode;



public class Extensions {
	
	private int _accepted_score_diff = 25;
	
	private ExtensionInfo w_SingleReply;
	private ExtensionInfo w_MateThread;
	private ExtensionInfo w_PasserPush;
	private ExtensionInfo w_Recapture;
	private ExtensionInfo w_WinCapNonPawn;
	private ExtensionInfo w_WinCapPawn;
	private ExtensionInfo w_MoveEval;
	
	private ExtensionInfo b_SingleReply;
	private ExtensionInfo b_MateThread;
	private ExtensionInfo b_PasserPush;
	private ExtensionInfo b_Recapture;
	private ExtensionInfo b_WinCapNonPawn;
	private ExtensionInfo b_WinCapPawn;
	private ExtensionInfo b_MoveEval;
	
	public Extensions(IExtensionMode extensionMode, int updateInterval) {
		w_SingleReply = new Extension_SingleReply(extensionMode, updateInterval, _accepted_score_diff);
		w_MateThread = new Extension_MateThread(extensionMode, updateInterval, _accepted_score_diff);
		w_PasserPush = new Extension_PasserPush(extensionMode, updateInterval, _accepted_score_diff);
		w_Recapture = new Extension_Recapture(extensionMode, updateInterval, _accepted_score_diff);
		w_WinCapNonPawn = new Extension_WinCapNonPawn(extensionMode, updateInterval, _accepted_score_diff);
		w_WinCapPawn = new Extension_WinCapPawn(extensionMode, updateInterval, _accepted_score_diff);
		w_MoveEval = new Extension_MoveEval(extensionMode, updateInterval, _accepted_score_diff);
		
		b_SingleReply = new Extension_SingleReply(extensionMode, updateInterval, _accepted_score_diff);
		b_MateThread = new Extension_MateThread(extensionMode, updateInterval, _accepted_score_diff);
		b_PasserPush = new Extension_PasserPush(extensionMode, updateInterval, _accepted_score_diff);
		b_Recapture = new Extension_Recapture(extensionMode, updateInterval, _accepted_score_diff);
		b_WinCapNonPawn = new Extension_WinCapNonPawn(extensionMode, updateInterval, _accepted_score_diff);
		b_WinCapPawn = new Extension_WinCapPawn(extensionMode, updateInterval, _accepted_score_diff);
		b_MoveEval = new Extension_MoveEval(extensionMode, updateInterval, _accepted_score_diff);
	}
	
	
	public ExtensionInfo getMateThreadPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_MateThread : b_MateThread;
	}
	
	public ExtensionInfo getPasserPushPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_PasserPush : b_PasserPush;
	}
	
	public ExtensionInfo getRecapturePV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_Recapture : b_Recapture;
	}
	
	public ExtensionInfo getSingleReplyPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_SingleReply : b_SingleReply;
	}
	
	public ExtensionInfo getWinCapNonPawnPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_WinCapNonPawn : b_WinCapNonPawn;
	}
	
	public ExtensionInfo getWinCapPawnPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_WinCapPawn : b_WinCapPawn;
	}
	
	
	public ExtensionInfo getMateThreadNonPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_MateThread : b_MateThread;
	}

	public ExtensionInfo getPasserPushNonPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_PasserPush : b_PasserPush;
	}
	
	
	public ExtensionInfo getRecaptureNonPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_Recapture : b_Recapture;
	}
	
	public ExtensionInfo getSingleReplyNonPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_SingleReply : b_SingleReply;
	}
	
	public ExtensionInfo getWinCapNonPawnNonPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_WinCapNonPawn : b_WinCapNonPawn;
	}
	
	public ExtensionInfo getWinCapPawnNonPV(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_WinCapPawn : b_WinCapPawn;
	}
	
	public ExtensionInfo getMoveEval(int colour) {
		return colour == Figures.COLOUR_WHITE ? w_MoveEval : b_MoveEval;
	}
	
	public void normalize() {
		w_SingleReply.normalize();
		w_MateThread.normalize();
		w_PasserPush.normalize();
		w_Recapture.normalize();
		w_WinCapNonPawn.normalize();
		w_WinCapPawn.normalize();
		w_MoveEval.normalize();
		
		b_SingleReply.normalize();
		b_MateThread.normalize();
		b_PasserPush.normalize();
		b_Recapture.normalize();
		b_WinCapNonPawn.normalize();
		b_WinCapPawn.normalize();
		b_MoveEval.normalize();
	}
	
	
	@Override
	public String toString() {
		String result = "";
		
		result += "w_SingleReply > " + w_SingleReply + "\r\n";
		result += "w_MateThread > " + w_MateThread + "\r\n";
		result += "w_PasserPush > " + w_PasserPush + "\r\n";
		result += "w_Recapture > " + w_Recapture + "\r\n";
		result += "w_WinCapNonPawn > " + w_WinCapNonPawn + "\r\n";
		result += "w_WinCapPawn > " + w_WinCapPawn + "\r\n";
		result += "w_MoveEval > " + w_MoveEval + "\r\n";
		
		result += "b_SingleReply > " + b_SingleReply + "\r\n";
		result += "b_MateThread > " + b_MateThread + "\r\n";
		result += "b_PasserPush > " + b_PasserPush + "\r\n";
		result += "b_Recapture > " + b_Recapture + "\r\n";
		result += "b_WinCapNonPawn > " + b_WinCapNonPawn + "\r\n";
		result += "b_WinCapPawn > " + b_WinCapPawn + "\r\n";
		result += "b_MoveEval > " + b_MoveEval + "\r\n";
		
		return result;
	}
}

/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.scanner.common;


public class MatchingInfo_BaseImpl implements IMatchingInfo {
	
	
	private static final String ALL_FIELD_NAMES[] = new String[] {	"H1", "G1", "F1", "E1", "D1", "C1", "B1", "A1",
			"H2", "G2", "F2", "E2", "D2", "C2", "B2", "A2",
			"H3", "G3", "F3", "E3", "D3", "C3", "B3", "A3",
			"H4", "G4", "F4", "E4", "D4", "C4", "B4", "A4",
			"H5", "G5", "F5", "E5", "D5", "C5", "B5", "A5",
			"H6", "G6", "F6", "E6", "D6", "C6", "B6", "A6",
			"H7", "G7", "F7", "E7", "D7", "C7", "B7", "A7",
			"H8", "G8", "F8", "E8", "D8", "C8", "B8", "A8",
	};
	
	
	private int phasesCount;
	private int currentPhase;
	private String phaseName;
	private double currentPhaseProgress;
	private int currentSquareID;
	
	private String latestMessage1;
	private String latestMessage2;
	
	
	public MatchingInfo_BaseImpl() {
	}
	
	
	@Override
	public void setPhasesCount(int _phasesCount) {
		phasesCount = _phasesCount;
	}
	
	
	@Override
	public void setCurrentPhase(int orderNumber) {
		currentPhase = orderNumber;
		currentPhaseProgress = 0;
	}
	
	
	@Override
	public void setCurrentPhaseProgress(double progress) {
		currentPhaseProgress = progress;
	}
	
	
	@Override
	public void setPhaseName(String _phaseName) {
		phaseName = _phaseName;
	}
	
	
	@Override
	public void setSquare(int squareID) {
		currentSquareID = squareID;
		latestMessage1 = "Phase [" + currentPhase + "/" + phasesCount + "] " + (int) (100 * currentPhaseProgress) + "%";
		latestMessage2 = phaseName + " working on " + ALL_FIELD_NAMES[squareID];
		//System.out.println(latestMessage1 + " " + latestMessage2);
	}


	@Override
	public String getLatestMessage1() {
		return latestMessage1;
	}


	@Override
	public String getLatestMessage2() {
		return latestMessage2;
	}


	@Override
	public int getCurrentSquareID() {
		return currentSquareID;
	}
}

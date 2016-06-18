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
package com.bagaturchess.ucitournament.swisssystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class EngineMetaInf {
	
	private String name;
	private String programArgs;
	
	private int scores;
	private int count_black;
	private int count_white;
	private Set<String> paired;
	
	
	public EngineMetaInf(String _name, String _programArgs) {
		
		if (_name == null) {
			throw new IllegalStateException();
		}
		if (_programArgs == null) {
			throw new IllegalStateException();
		}
		
		name = _name;
		//cfgClass = _cfgClass;
		programArgs = _programArgs;
		
		paired = new HashSet<String>();
		//System.out.println(_programArgs);
	}
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getProgramArgs() {
		return programArgs;
	}


	public void setProgramArgs(String programArgs) {
		this.programArgs = programArgs;
	}


	public int getScores() {
		return scores;
	}


	public void setScores(int scores) {
		this.scores = scores;
	}


	public int getCount_all() {
		return count_white + count_black;
	}


	public int getCount_white() {
		return count_white;
	}

	
	public void setCount_white(int count_white) {
		this.count_white = count_white;
	}
	
	
	public int getCount_black() {
		return count_black;
	}
	
	
	public void setCount_black(int count_black) {
		this.count_black = count_black;
	}


	public Set<String> getPaired() {
		return paired;
	}

	
	public void addPaired(String name) {
		paired.add(name);
	}
	

	public void setPaired(Set<String> paired) {
		this.paired = paired;
	}


	public String asString() {
		String msg = "";
		msg += getName() + "	=	";
		msg += getScores() + ",	";
		msg += getCount_white() + ",	";
		msg += getCount_black();//+ ",	";
		return msg;
	}
	
	
	@Override
	public String toString() {
		String msg = "";
		msg += " name='" + getName() + "'";	
		msg += ", scores='" + getScores() + "'";
		msg += ", args='" + getProgramArgs() + "'";
		
		return msg;
	}
}

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
package com.bagaturchess.ucitournament.rating;


public class EngineMetaInf {
	
	private int place;
	
	private String name;
	//private String cfgClass;
	private String programArgs;
	
	private int ELO = 2300;
	private int playedGamesCount;
	private int ELOAdjustments_sum;
	private int ELOAdjustments_total;
	
	
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
		
		//System.out.println(_programArgs);
	}
	
	
	/*public String getCfgClass() {
		return cfgClass;
	}*/
	
	
	public int getELO() {
		return ELO;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public int getPlace() {
		return place;
	}
	
	
	public int getPlayedGamesCount() {
		return playedGamesCount;
	}
	
	
	public String getProgramArgs() {
		return programArgs;
	}
	
	
	public void setELO(int elo) {
		ELO = elo;
	}
	
	
	public void setPlace(int place) {
		this.place = place;
	}
	
	
	public void setPlayedGamesCount(int playedGamesCount) {
		this.playedGamesCount = playedGamesCount;
	}
	
	
	public int getELOAdjustments_sum() {
		return ELOAdjustments_sum;
	}
	
	
	public void addELOAdjustments_sum(int eLOAdjustmentsSum) {
		ELOAdjustments_sum += eLOAdjustmentsSum;
	}
	
	
	public int getELOAdjustments_total() {
		return ELOAdjustments_total;
	}
	
	
	public void addELOAdjustments_total(int eLOAdjustmentsTotal) {
		ELOAdjustments_total += eLOAdjustmentsTotal;
	}
	
	public double getELOMovingDirection() {
		if (ELOAdjustments_total == 0) {
			return 0;
		}
		return ELOAdjustments_sum / (double) ELOAdjustments_total;
	}
	
	public String asString() {
		String msg = "";
		msg += getName() + "	=	";	
		msg += getPlace() + ",	";
		msg += getELO() + ",	";
		msg += getPlayedGamesCount() + ",	";
		msg += getELOAdjustments_sum() + ",	";
		msg += getELOAdjustments_total() + ",	";
		msg += getELOMovingDirection();
		return msg;
	}
	
	@Override
	public String toString() {
		String msg = "";
		msg += " name='" + getName() + "'";	
		msg += ", place='" + getPlace() + "'";
		msg += ", ELO='" + getELO() + "'";
		msg += ", games='" + getPlayedGamesCount() + "'";
		msg += ", ELOMovingDir='" + getELOMovingDirection() + "'";
		//msg += ", cfgclass='" + getCfgClass() + "'";
		msg += ", args='" + getProgramArgs() + "'";
		
		return msg;
	}
}

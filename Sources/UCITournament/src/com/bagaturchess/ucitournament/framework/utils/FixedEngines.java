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
package com.bagaturchess.ucitournament.framework.utils;


import bagaturchess.ucitracker.impl.Engine;


public class FixedEngines {
	
	public static Engine glaurung = new Engine("E:\\own\\Projects\\ARENA\\Arena\\Engines\\glaurung22\\Windows\\glaurung-w32.exe",
			new String [0],
			"E:\\own\\Projects\\ARENA\\Arena\\Engines\\glaurung22\\Windows\\");
	
	public static Engine rybka = new Engine("E:/own/Projects/ARENA/rybka/Rybka.exe",
			new String [0],
			"E:/own/Projects/ARENA/rybka/");
	
	public static Engine aice = new Engine("E:\\own\\Projects\\ARENA\\Engines\\aice0992\\aice.exe",
			new String [0],
			"E:\\own\\Projects\\ARENA\\Engines\\aice0992\\");
	
	public static Engine mediocre = new Engine("java -Xmx256M -classpath E:\\own\\Projects\\ARENA\\Engines\\MEDIOCRE\\bin Mediocre",
			new String [0], //new String [] {"JAVA_HOME=C:\\jdk1.5.0_12", "Path=C:\\jdk1.5.0_12\\bin"}, 
			"E:\\own\\Projects\\ARENA\\Engines\\MEDIOCRE");
	
	public static Engine fruit21 = new Engine("E:\\own\\Projects\\ARENA\\Engines\\fruit_21\\fruit_21",
			new String [0],
			"E:\\own\\Projects\\ARENA\\Engines\\fruit_21\\");
	
	public static Engine Houdini_15a_w32 = new Engine("C:\\own\\chess\\ENGINES\\Houdini_15a\\Houdini_15a_w32.exe",
			new String [0],
			"C:\\own\\chess\\ENGINES\\Houdini_15a\\");
	
	public static Engine Stockfish_211_32_ja = new Engine("C:\\own\\chess\\ENGINES\\stockfish-211-ja\\Windows\\stockfish-211-32-ja.exe",
			new String [0],
			"C:\\own\\chess\\ENGINES\\stockfish-211-ja\\Windows\\");
	
}

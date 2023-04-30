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

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

public class SwissSystemWorkspace {
	
	
	private File home;
	private SwissSystemLog log;
	
	
	public SwissSystemWorkspace(String _home) {
		home = new File(_home);
		if (!home.exists()) {
			throw new IllegalStateException("Directory does not exists: " + home.getAbsolutePath());
		}
		
		log = new SwissSystemLog();
	}
	
	
	public SwissSystemLog getLog() {
		return log;
	}
	
	
	public File getEnginesFile() {
		return new File(home, "engines.txt");
	}
	
	
	public File getEnginesDataFile() {
		return new File(home, "engines.dat");
	}
	
	
	public String getPairingFileName(int pairing_number) {
		return "pairing." + pairing_number + ".txt";
	}
	
	
	private String getPairingNumberByFileName(String fileName) {
		StringTokenizer tokens = new StringTokenizer(fileName, ".");
		String result = tokens.nextToken();
		result = tokens.nextToken();
		return result;
	}
	
	
	public File[] getPairingFiles() {
		String[] pairingFilesNames = home.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("pairing.") && name.endsWith(".txt");
			}
		});
		
		File[] pairingFiles = new File[pairingFilesNames.length];
		for (int i=0; i<pairingFilesNames.length; i++) {
			pairingFiles[i] = new File(home, pairingFilesNames[i]);
		}
		
		Arrays.sort(pairingFiles, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				
				String f1_idx_str = getPairingNumberByFileName(f1.getName());
				String f2_idx_str = getPairingNumberByFileName(f2.getName());
				
				int f1_idx= Integer.parseInt(f1_idx_str);
				int f2_idx= Integer.parseInt(f2_idx_str);
				
				int delta = f1_idx - f2_idx;
				if (delta == 1) {
					return 1;
				}
				
				return delta;
			}
		});
		
		return pairingFiles;
	}
}

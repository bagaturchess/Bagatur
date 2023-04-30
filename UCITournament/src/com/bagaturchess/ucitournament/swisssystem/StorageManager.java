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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;


public class StorageManager {
		
	
	public static final List<EngineMetaInf> loadEnginesMetaInf(SwissSystemWorkspace workspace) throws IOException {
		
		InputStream descriptionFile_is = null;
		InputStream systemFile_is = null;
		InputStream[] pairingFile_is = null;
		try {
			descriptionFile_is = new FileInputStream(workspace.getEnginesFile()); 
			systemFile_is = new FileInputStream(workspace.getEnginesDataFile());
			
			/*File[] pairingFiles = workspace.getPairingFiles();
			pairingFile_is = new InputStream[pairingFiles.length];
			for (int i=0; i<pairingFiles.length; i++) {
				pairingFile_is[i] = new FileInputStream(pairingFiles[i]);
			}*/
			
			Map<String, EngineMetaInf> enginesByName = loadEnginesMetaInf(descriptionFile_is);
			addSystemInf(enginesByName, systemFile_is);
			addPairingInf(enginesByName, workspace.getPairingFiles());
			
			List<EngineMetaInf> result = new ArrayList<EngineMetaInf>();
			for (String name: enginesByName.keySet()){
				result.add(enginesByName.get(name));
			}
			
			return result;
			
		} finally {
			if (descriptionFile_is != null) {
				try {
					descriptionFile_is.close();
				} catch(Exception ioe) {}
			}
			if (systemFile_is != null) {
				try {
					systemFile_is.close();
				} catch(Exception ioe) {}
			}
			if (pairingFile_is != null) {
				for (int i=0; i<pairingFile_is.length; i++) {
					try {
						pairingFile_is[i].close();
					} catch(Exception ioe) {}
				}
			}
		}
	}
	
	
	public static final void storeEnginesMetaInf(SwissSystemWorkspace workspace, List<EngineMetaInf> enginesMetaInfs) throws IOException {
		OutputStream systemFile_os = null;
		
		try {
			
			//OperationsManager.adjustPlaces(enginesMetaInfs);
			
			systemFile_os = new FileOutputStream(workspace.getEnginesDataFile());
			storeEnginesMetaInf(enginesMetaInfs, systemFile_os);
		} finally {
			if (systemFile_os != null) {
				try {
					systemFile_os.close();
				} catch(Exception ioe) {}
			}
		}
	}
	
	
	public static final void storePairing(SwissSystemWorkspace workspace, Pairing pairing, int pairing_number) throws IOException {
		OutputStream pairingFile_os = null;
		
		try {
			
			pairingFile_os = new FileOutputStream(workspace.getPairingFileName(pairing_number));
			
			BufferedWriter systemFile = new BufferedWriter(new OutputStreamWriter(pairingFile_os));
			for (PairingEntry cur: pairing.getEntries()) {
				String str = cur.asString();
				systemFile.write(str);
				systemFile.write("\r\n");
			}
			systemFile.flush();
			
		} finally {
			if (pairingFile_os != null) {
				try {
					pairingFile_os.close();
				} catch(Exception ioe) {}
			}
		}
	}
	
	
	public static final Pairing loadPairing(File pairingFileObj) throws IOException {
		InputStream pairingFile_is = null;
		
		try {
			
			pairingFile_is = new FileInputStream(pairingFileObj);
			BufferedReader pairingFile = new BufferedReader(new InputStreamReader(pairingFile_is));
			
			List<PairingEntry> entries = new ArrayList<PairingEntry>();
			String line;
			while ((line = pairingFile.readLine()) != null) {
				line = line.trim();
				if (line.equals("")) {
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}
				
				List<String> values = new ArrayList<String>();
				StringTokenizer line_sep = new StringTokenizer(line, ",");
				while (line_sep.hasMoreTokens()) {
					String next = line_sep.nextToken();
					values.add(next.trim());
				}
				
				if (values.size() != 3) {
					throw new IllegalStateException("size=" + values.size());
				}
				
				PairingEntry entry = new PairingEntry(values.get(0), values.get(1), values.get(2));
				entries.add(entry);
			}
			
			Pairing result = new Pairing(entries);
			
			return result;
			
		} finally {
			if (pairingFile_is != null) {
				try {
					pairingFile_is.close();
				} catch(Exception ioe) {}
			}
		}
	}
	
	
	private static final void storeEnginesMetaInf(List<EngineMetaInf> enginesMetaInfs, OutputStream systemFile_os) throws IOException {
		
		Set<EngineMetaInf> sorted = new TreeSet<EngineMetaInf>(new Comparator_Scores());
		for (EngineMetaInf cur_engine: enginesMetaInfs) {
			sorted.add(cur_engine);
		}
		
		BufferedWriter systemFile = new BufferedWriter(new OutputStreamWriter(systemFile_os));
		for (EngineMetaInf cur: sorted) {
			String str = cur.asString();
			systemFile.write(str);
			systemFile.write("\r\n");
		}
		systemFile.flush();
	}
	
	
	private static final Map<String, EngineMetaInf> loadEnginesMetaInf(InputStream descriptionFile_is) throws IOException {
		
		Map<String, EngineMetaInf> enginesByNames = new HashMap<String, EngineMetaInf>();
		
		BufferedReader descriptionFile = new BufferedReader(new InputStreamReader(descriptionFile_is));
		
		String line;
		while ((line = descriptionFile.readLine()) != null) {
			line = line.trim();
			if (line.equals("")) {
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			
			int index_key_val_sep = line.indexOf("=");
			if (index_key_val_sep == -1) {
				throw new IllegalStateException("Line does not contan '=' simbol -> " + line);
			}
			
			String line_key = line.substring(0, index_key_val_sep).trim();
			String line_val = line.substring(index_key_val_sep + 1, line.length()).trim();
			
			if (line_key.equals("")) {
				throw new IllegalStateException("Line has no key -> " + line);
			}
			
			if (line_val.equals("")) {
				throw new IllegalStateException("Line has no value -> " + line);
			}
			
			if (enginesByNames.containsKey(line_key)) {
				throw new IllegalStateException("Duplicate engine's names -> " + line_key);
			}
			
			
			/**
			 * Get class and args
			 */
			String line_args = line_val;
			/*int index_class_args_sep = line_val.indexOf(",");
			if (index_class_args_sep == -1) {
				throw new IllegalStateException("Line does not contan ',' simbol -> " + line);
			}
			
			String line_class = line_val.substring(0, index_class_args_sep).trim();
			String line_args = line_val.substring(index_class_args_sep + 1, line_val.length()).trim();
			
			
			if (line_class.equals("")) {
				throw new IllegalStateException("Line has no class -> " + line);
			}*/
			
			if (line_args.equals("")) {
				//throw new IllegalStateException("Line has no args -> " + line);
			}
			
			EngineMetaInf engineMetaInf = new EngineMetaInf(line_key, line_args);
			enginesByNames.put(engineMetaInf.getName(), engineMetaInf);
		}
		
		return enginesByNames;
	}
	
	
	private static final void addSystemInf(Map<String, EngineMetaInf> enginesByNames, InputStream systemFile_is) throws IOException {
		
		BufferedReader systemFile = new BufferedReader(new InputStreamReader(systemFile_is));
		
		String line;
		while ((line = systemFile.readLine()) != null) {
			line = line.trim();
			if (line.equals("")) {
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			
			int index_key_val_sep = line.indexOf("=");
			if (index_key_val_sep == -1) {
				throw new IllegalStateException("Line does not contan '=' simbol -> " + line);
			}
			
			String line_key = line.substring(0, index_key_val_sep).trim();
			String line_val = line.substring(index_key_val_sep + 1, line.length()).trim();
			
			if (line_key.equals("")) {
				throw new IllegalStateException("Line has no key -> " + line);
			}
			
			if (line_val.equals("")) {
				throw new IllegalStateException("Line has no value -> " + line);
			}
			
			EngineMetaInf engineMetaInf = enginesByNames.get(line_key);
			if (engineMetaInf != null) {
				addMetaInfLine(engineMetaInf, line_val);
			}
		}
	}
	
	
	private static final void addMetaInfLine(EngineMetaInf engineMetaInf, String line) {
		
		List<String> values = new ArrayList<String>();
		
		StringTokenizer line_sep = new StringTokenizer(line, ",");
		while (line_sep.hasMoreTokens()) {
			String next = line_sep.nextToken();
			values.add(next.trim());
		}
		
		if (values.size() != 3) {
			throw new IllegalStateException("size=" + values.size());
		}
		
		int scores = Integer.parseInt(values.get(0));
		int games_with_white = Integer.parseInt(values.get(1));
		int games_with_black = Integer.parseInt(values.get(2));
		
		engineMetaInf.setScores(scores);
		engineMetaInf.setCount_white(games_with_white);
		engineMetaInf.setCount_black(games_with_black);
	}
	
	
	private static final void addPairingInf(Map<String, EngineMetaInf> enginesByNames, File[] pairingFiles) throws IOException {
		for (int i=0; i<pairingFiles.length; i++) {
			Pairing pairing = loadPairing(pairingFiles[i]);
			List<PairingEntry> entries = pairing.getEntries();
			for (PairingEntry entry: entries) {
				if (!entry.getResult().equals(PairingEntry.RESULT_NOT_AVAILABLE)) {
					EngineMetaInf white = enginesByNames.get(entry.getWhiteEngineName());
					EngineMetaInf black = enginesByNames.get(entry.getBlackEngineName());
					white.addPaired(black.getName());
					black.addPaired(white.getName());
				}
			}
		}
	}
}

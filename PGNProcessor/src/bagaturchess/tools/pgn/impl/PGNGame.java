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
package bagaturchess.tools.pgn.impl;


//import java.util.Date;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PGNGame {
	
	//private int id;
	private List<PGNTurn> mTurns;
	private PGNGameProperties mProperties = new PGNGameProperties();
	
	private String archiveFileName = null;
	private int game_status = PGNConstants.PGN_GAME_STATUS_CREATING_INSTANCE;
	private String gameID;
	private String gameSource;
	
	public PGNGame() {
		mTurns = new ArrayList<PGNTurn>();
	}
	
	
	public void load(StringBuffer aPGNGameAsStringBuffer) throws IOException {
		int lastValueEndIndex = PGNGameParseHelper.initGameProperties(this,aPGNGameAsStringBuffer);
		boolean mIsReducedFormat = PGNGameParseHelper.initReducedFormatFlags(this,aPGNGameAsStringBuffer, lastValueEndIndex);
		
		if (mIsReducedFormat) {
			PGNGameParseHelper.extractGameTurns(this, aPGNGameAsStringBuffer, lastValueEndIndex);
			setGameStatus(PGNConstants.PGN_GAME_STATUS_PARSED_SUCCESSFULLY);
		} else {
			boolean mIsReducedFormat_ignoreCommentsAndVariants = PGNGameParseHelper.initReducedFormatFlags_ignoreCommentsAndVariants(this,aPGNGameAsStringBuffer, lastValueEndIndex);
			if (mIsReducedFormat_ignoreCommentsAndVariants) {
				PGNGameParseHelper.extractGameTurns_ignoreCommentsAndVariants(this, aPGNGameAsStringBuffer, lastValueEndIndex);
				setGameStatus(PGNConstants.PGN_GAME_STATUS_PARSED_SUCCESSFULLY);
			} else {
				setGameStatus(PGNConstants.PGN_GAME_STATUS_PARSED_NOT_IN_REDUCED_FORMAT);
			}
		}
		
		gameID = createID();
		gameSource = aPGNGameAsStringBuffer.toString();
	}
	
	private String createID() {
		String id = getProperties().get_str_date() + "$"
							+ getWhitePlayerName() + "$"
							+ getBlackPlayerName() + "$"
							+ mTurns.size() + "$"
							+ ((mTurns.size() == 0) ? "NoMoves" : mTurns.get(0))
							+ ((mTurns.size() == 0) ? "NoMoves" : mTurns.get(mTurns.size() - 1));
		return id;
	}

	public PGNGameProperties getProperties() {
		return mProperties;
	}

	public List<PGNTurn> getTurns() {
		return mTurns;
	}

	public void setTurns(List<PGNTurn> turns) {
		mTurns = turns;
	}
	
	
	/**
	 * Helper method
	 * @return
	 */
	public String getTagValue(String pTagName) {
		String result = getProperty(pTagName);
		if (result == null) {
			throw new RuntimeException(
				"PGN tag '" + pTagName + "' has null vaue!");
		}
		return result;
	}
	
	String getProperty(String name) {
		return (String)mProperties.get(name);
	}
	
	Integer getIntProperty(String name) {
		return (Integer)mProperties.get(name);
	}
	
	public String toString() {
		String result = "************* PGNGame start *************\r\n";

		
		for (String key:new String[]{ PGNConstants.PROPERTY_TAG_ASP_FEN,PGNConstants.PROPERTY_TAG_ASP_SETUP,PGNConstants.PROPERTY_TAG_ERI_BOARD,PGNConstants.PROPERTY_TAG_ERI_EVENT_DATE,PGNConstants.PROPERTY_TAG_ERI_EVENT_SPONSOR,PGNConstants.PROPERTY_TAG_ERI_SECTION,PGNConstants.PROPERTY_TAG_ERI_STAGE,PGNConstants.PROPERTY_TAG_GC_TERMINATION,PGNConstants.PROPERTY_TAG_M_ANNOTATOR,PGNConstants.PROPERTY_TAG_M_MODE,PGNConstants.PROPERTY_TAG_M_PLY_COUNT,PGNConstants.PROPERTY_TAG_OI_ECO,PGNConstants.PROPERTY_TAG_OI_NIC,PGNConstants.PROPERTY_TAG_OI_OPENING,PGNConstants.PROPERTY_TAG_OI_SUB_VARIATION,PGNConstants.PROPERTY_TAG_OI_VARIATION,PGNConstants.PROPERTY_TAG_PRI_WHITE_ELO,PGNConstants.PROPERTY_TAG_PRI_WHITE_NA,PGNConstants.PROPERTY_TAG_PRI_WHITE_TITLE,PGNConstants.PROPERTY_TAG_PRI_WHITE_TYPE,PGNConstants.PROPERTY_TAG_PRI_WHITE_USCF,PGNConstants.PROPERTY_TAG_STR_BLACK,PGNConstants.PROPERTY_TAG_STR_DATE,PGNConstants.PROPERTY_TAG_STR_EVENT,PGNConstants.PROPERTY_TAG_STR_RESULT,PGNConstants.PROPERTY_TAG_STR_ROUND,PGNConstants.PROPERTY_TAG_STR_SITE,PGNConstants.PROPERTY_TAG_STR_WHITE,PGNConstants.PROPERTY_TAG_TC_TIME_CONTROL,PGNConstants.PROPERTY_TAG_TDRI_TIME,PGNConstants.PROPERTY_TAG_TDRI_UTC_DATE,PGNConstants.PROPERTY_TAG_TDRI_UTC_TIME,PGNConstants.PROPERTY_TAG_OTHERS_BLACK_ELO,PGNConstants.PROPERTY_TAG_OTHERS_WHITE_ELO }) {
			Object value = mProperties.get(key);
			if (value != null) {
				result += "[" + key + " \"" + value + "\"]\r\n";
			}
		}

		result += "\r\n";

		for (int i = 0; i < mTurns.size(); i++) {
			PGNTurn pgnTurn = (PGNTurn) mTurns.get(i);
			result += pgnTurn + "\r\n";
		}

		result += "\r\n*****************************************";

		return result;
	}
	
	/**
	 * Helper method
	 * @return
	 */
	public String getStringIdentification() {
		String result = "";
		for (int i = 0; i < mTurns.size(); i++) {
			PGNTurn pgnTurn = (PGNTurn) mTurns.get(i);
			result += pgnTurn + " ";
		}
		return result;
	}

	/*
	public void setFileName(String _fileName) {
		fileName = _fileName;
	}
	
	public String getFileName() {
		return fileName;
	} */

	public String getWhitePlayerName() {
		return getProperty(PGNConstants.PROPERTY_TAG_STR_WHITE);
	}

	public String getBlackPlayerName() {
		return getProperty(PGNConstants.PROPERTY_TAG_STR_BLACK);
	}
	
	public Integer getWhiteELO() {
		return getIntProperty(PGNConstants.PROPERTY_TAG_OTHERS_WHITE_ELO);
	}

	public Integer getBlackELO() {
		return getIntProperty(PGNConstants.PROPERTY_TAG_OTHERS_BLACK_ELO);
	}
	
	public String getResult() {
		return getProperty(PGNConstants.PROPERTY_TAG_STR_RESULT);
	}

	public void addProperty(String key,String value) {
		if ( mProperties == null) {
			mProperties = new PGNGameProperties();
		}
		mProperties.set(key,value);
	}

	public void add(PGNTurn curTurn) {
		mTurns.add(curTurn);
	}

	public String getArchiveFileName() {
		return archiveFileName;
	}

	public void setArchiveFileName(String archiveFileName) {
		this.archiveFileName = archiveFileName;
	}

	public int getGameStatus() {
		return game_status;
	}

	public void setGameStatus(int _game_status) {
		game_status = _game_status;
	}

	public String getID() {
		return gameID;
	}

	public String getGameSource() {
		return gameSource;
	}
}

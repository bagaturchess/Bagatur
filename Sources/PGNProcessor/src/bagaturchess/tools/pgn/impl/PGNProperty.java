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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PGNProperty {
	
	private int id;
	private String key;
	private String value;
	
	public PGNProperty() {
		
	}
	
	public PGNProperty(String name,String value) {
		this.key = name;
		this.value = value;
	}

	
	public void setKey(String key) {
		this.key = key;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
	public static Map<String,String> asMap(Collection<PGNProperty> props) {
		HashMap<String,String> mProperties = new HashMap<String,String>();
		mProperties.put(PGNConstants.PROPERTY_TAG_ASP_FEN, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_ASP_SETUP, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_ERI_BOARD, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_ERI_EVENT_DATE, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_ERI_EVENT_SPONSOR, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_ERI_SECTION, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_ERI_STAGE, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_GC_TERMINATION, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_M_ANNOTATOR, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_M_MODE, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_M_PLY_COUNT, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_OI_ECO, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_OI_NIC, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_OI_OPENING, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_OI_SUB_VARIATION, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_OI_VARIATION, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_PRI_WHITE_ELO, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_PRI_WHITE_NA, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_PRI_WHITE_TITLE, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_PRI_WHITE_TYPE, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_PRI_WHITE_USCF, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_STR_BLACK, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_STR_DATE, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_STR_EVENT, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_STR_RESULT, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_STR_ROUND, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_STR_SITE, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_STR_WHITE, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_TC_TIME_CONTROL, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_TDRI_TIME, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_TDRI_UTC_DATE, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_TDRI_UTC_TIME, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_OTHERS_BLACK_ELO, null);
		mProperties.put(PGNConstants.PROPERTY_TAG_OTHERS_WHITE_ELO, null);
		
		for (PGNProperty prop:props) {
			mProperties.put(prop.getKey(), prop.getValue());
		}
		return mProperties;
	}
}

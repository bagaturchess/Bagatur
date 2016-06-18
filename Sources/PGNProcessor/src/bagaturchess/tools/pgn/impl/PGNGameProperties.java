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

public class PGNGameProperties {

	
	String  _asp_fen;
	String	_asp_setup;
	String	_eri_board;
	String	_eri_event_date;
	String	_eri_event_sponsor;
	String	_eri_section;
	String	_eri_stage;
	String	_gc_termination;
	String	_m_annotator;
	String	_m_mode;
	String	_m_ply_count;
	String	_oi_eco;
	String	_oi_nic;
	String	_oi_opening;
	String	_oi_sub_variation;
	String	_oi_variation;
	Integer	_pri_white_elo;
	String	_pri_white_na;
	String	_pri_white_title;
	String	_pri_white_type;
	String	_pri_white_uscf;
	String	_str_black;
	String	_str_date;
	String	_str_event;
	String	_str_event_type;
	String	_str_event_rounds;
	String	_str_event_country;
	String	_str_result;
	String	_str_round;
	String	_str_site;
	String	_str_white;
	String	_tc_time_control;
	String	_tdri_time;
	String	_tdri_utc_date;
	String	_tdri_utc_time;
	Integer	_others_black_elo;
	Integer	_others_white_elo;
	String _source;
	String _source_date;
	String _white_team;
	String _white_team_country;
	String _black_team;
	String _black_team_country;
	
	public PGNGameProperties() {
		
	}
	
	public void set(String propName,String value) {
		if (propName.equals(PGNConstants.PROPERTY_TAG_ASP_FEN)) { _asp_fen = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_ASP_SETUP)) { _asp_setup = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_BOARD)) { _eri_board = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_EVENT_DATE)) { _eri_event_date = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_EVENT_SPONSOR)) { _eri_event_sponsor = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_SECTION)) { _eri_section = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_STAGE)) { _eri_stage = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_GC_TERMINATION)) { _gc_termination = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_M_ANNOTATOR)) { _m_annotator = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_M_MODE)) { _m_mode = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_M_PLY_COUNT)) { _m_ply_count = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_ECO)) { _oi_eco = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_NIC)) { _oi_nic = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_OPENING)) { _oi_opening = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_SUB_VARIATION)) { _oi_sub_variation = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_VARIATION)) { _oi_variation = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_ELO)) { _pri_white_elo = new Integer(value); return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_NA)) { _pri_white_na = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_TITLE)) { _pri_white_title = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_TYPE)) { _pri_white_type = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_USCF)) { _pri_white_uscf = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_BLACK)) { _str_black = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_DATE)) { _str_date = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_EVENT)) { _str_event = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_RESULT)) { _str_result = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_ROUND)) { _str_round = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_SITE)) { _str_site = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_WHITE)) { _str_white = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_TC_TIME_CONTROL)) { _tc_time_control = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_TDRI_TIME)) { _tdri_time = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_TDRI_UTC_DATE)) { _tdri_utc_date = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_TDRI_UTC_TIME)) { _tdri_utc_time = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_OTHERS_BLACK_ELO)) { _others_black_elo = new Integer(value); return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_OTHERS_WHITE_ELO)) { _others_white_elo = new Integer(value); return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_EVENT_TYPE)) { _str_event_type = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_EVENT_ROUNDS)) { _str_event_rounds = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_EVENT_COUNTRY)) { _str_event_country = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_SOURCE)) { _source = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_SOURCE_DATE)) { _source_date = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_WHITE_TEAM)) { _white_team = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_WHITE_TEAM_COUNTRY)) { _white_team_country = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_BLACK_TEAM)) { _black_team = value; return; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_BLACK_TEAM_COUNTRY)) { _black_team_country = value; return; }
		
		//throw new IllegalArgumentException("Unknown tag " + propName);
		System.out.println("INFO: Unknown tag " + propName);
	}

	public Object get(String propName) {
		
		if (propName.equals(PGNConstants.PROPERTY_TAG_ASP_FEN)) return _asp_fen ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_ASP_SETUP)) return _asp_setup ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_BOARD)) return _eri_board ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_EVENT_DATE)) return _eri_event_date ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_EVENT_SPONSOR)) return _eri_event_sponsor ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_SECTION)) return _eri_section ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_ERI_STAGE)) return _eri_stage ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_GC_TERMINATION)) return _gc_termination ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_M_ANNOTATOR)) return _m_annotator ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_M_MODE)) return _m_mode ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_M_PLY_COUNT)) return _m_ply_count ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_ECO)) return _oi_eco ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_NIC)) return _oi_nic ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_OPENING)) return _oi_opening ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_SUB_VARIATION)) return _oi_sub_variation ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_OI_VARIATION)) return _oi_variation ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_ELO)) return _pri_white_elo ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_NA)) return _pri_white_na ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_TITLE)) return _pri_white_title ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_TYPE)) return _pri_white_type ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_PRI_WHITE_USCF)) return _pri_white_uscf ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_BLACK)) return _str_black ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_DATE)) return _str_date ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_EVENT)) return _str_event ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_RESULT)) return _str_result ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_ROUND)) return _str_round ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_SITE)) return _str_site ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_STR_WHITE)) return _str_white ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_TC_TIME_CONTROL)) return _tc_time_control ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_TDRI_TIME)) return _tdri_time ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_TDRI_UTC_DATE)) return _tdri_utc_date ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_TDRI_UTC_TIME)) return _tdri_utc_time ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_OTHERS_BLACK_ELO)) return _others_black_elo ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_OTHERS_WHITE_ELO)) return _others_white_elo ;
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_EVENT_TYPE)) { return _str_event_type; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_EVENT_ROUNDS)) { return _str_event_rounds; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_EVENT_COUNTRY)) { return _str_event_country; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_SOURCE)) { return _source; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_SOURCE_DATE)) { return _source_date; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_WHITE_TEAM)) { return _white_team; }
		if (propName.equals(PGNConstants.PROPERTY_TAG_UNKNOWN_WHITE_TEAM_COUNTRY)) { return _white_team_country; }

		throw new IllegalArgumentException("Unknown tag " + propName);
	}
	
	
	public String get_asp_fen() {
		return _asp_fen;
	}

	public void set_asp_fen(String _asp_fen) {
		this._asp_fen = _asp_fen;
	}

	public String get_asp_setup() {
		return _asp_setup;
	}

	public void set_asp_setup(String _asp_setup) {
		this._asp_setup = _asp_setup;
	}

	public String get_eri_board() {
		return _eri_board;
	}

	public void set_eri_board(String _eri_board) {
		this._eri_board = _eri_board;
	}

	public String get_eri_event_date() {
		return _eri_event_date;
	}

	public void set_eri_event_date(String _eri_event_date) {
		this._eri_event_date = _eri_event_date;
	}

	public String get_eri_event_sponsor() {
		return _eri_event_sponsor;
	}

	public void set_eri_event_sponsor(String _eri_event_sponsor) {
		this._eri_event_sponsor = _eri_event_sponsor;
	}

	public String get_eri_section() {
		return _eri_section;
	}

	public void set_eri_section(String _eri_section) {
		this._eri_section = _eri_section;
	}

	public String get_eri_stage() {
		return _eri_stage;
	}

	public void set_eri_stage(String _eri_stage) {
		this._eri_stage = _eri_stage;
	}

	public String get_gc_termination() {
		return _gc_termination;
	}

	public void set_gc_termination(String _gc_termination) {
		this._gc_termination = _gc_termination;
	}

	public String get_m_annotator() {
		return _m_annotator;
	}

	public void set_m_annotator(String _m_annotator) {
		this._m_annotator = _m_annotator;
	}

	public String get_m_mode() {
		return _m_mode;
	}

	public void set_m_mode(String _m_mode) {
		this._m_mode = _m_mode;
	}

	public String get_m_ply_count() {
		return _m_ply_count;
	}

	public void set_m_ply_count(String _m_ply_count) {
		this._m_ply_count = _m_ply_count;
	}

	public String get_oi_eco() {
		return _oi_eco;
	}

	public void set_oi_eco(String _oi_eco) {
		this._oi_eco = _oi_eco;
	}

	public String get_oi_nic() {
		return _oi_nic;
	}

	public void set_oi_nic(String _oi_nic) {
		this._oi_nic = _oi_nic;
	}

	public String get_oi_opening() {
		return _oi_opening;
	}

	public void set_oi_opening(String _oi_opening) {
		this._oi_opening = _oi_opening;
	}

	public String get_oi_sub_variation() {
		return _oi_sub_variation;
	}

	public void set_oi_sub_variation(String _oi_sub_variation) {
		this._oi_sub_variation = _oi_sub_variation;
	}

	public String get_oi_variation() {
		return _oi_variation;
	}

	public void set_oi_variation(String _oi_variation) {
		this._oi_variation = _oi_variation;
	}

	public Integer get_others_black_elo() {
		return _others_black_elo;
	}

	public void set_others_black_elo(Integer _others_black_elo) {
		this._others_black_elo = _others_black_elo;
	}

	public Integer get_others_white_elo() {
		return _others_white_elo;
	}

	public void set_others_white_elo(Integer _others_white_elo) {
		this._others_white_elo = _others_white_elo;
	}

	public Integer get_pri_white_elo() {
		return _pri_white_elo;
	}

	public void set_pri_white_elo(Integer _pri_white_elo) {
		this._pri_white_elo = _pri_white_elo;
	}

	public String get_pri_white_na() {
		return _pri_white_na;
	}

	public void set_pri_white_na(String _pri_white_na) {
		this._pri_white_na = _pri_white_na;
	}

	public String get_pri_white_title() {
		return _pri_white_title;
	}

	public void set_pri_white_title(String _pri_white_title) {
		this._pri_white_title = _pri_white_title;
	}

	public String get_pri_white_type() {
		return _pri_white_type;
	}

	public void set_pri_white_type(String _pri_white_type) {
		this._pri_white_type = _pri_white_type;
	}

	public String get_pri_white_uscf() {
		return _pri_white_uscf;
	}

	public void set_pri_white_uscf(String _pri_white_uscf) {
		this._pri_white_uscf = _pri_white_uscf;
	}

	public String get_str_black() {
		return _str_black;
	}

	public void set_str_black(String _str_black) {
		this._str_black = _str_black;
	}

	public String get_str_date() {
		return _str_date;
	}

	public void set_str_date(String _str_date) {
		this._str_date = _str_date;
	}

	public String get_str_event() {
		return _str_event;
	}

	public void set_str_event(String _str_event) {
		this._str_event = _str_event;
	}

	public String get_str_result() {
		return _str_result;
	}

	public void set_str_result(String _str_result) {
		this._str_result = _str_result;
	}

	public String get_str_round() {
		return _str_round;
	}

	public void set_str_round(String _str_round) {
		this._str_round = _str_round;
	}

	public String get_str_site() {
		return _str_site;
	}

	public void set_str_site(String _str_site) {
		this._str_site = _str_site;
	}

	public String get_str_white() {
		return _str_white;
	}

	public void set_str_white(String _str_white) {
		this._str_white = _str_white;
	}

	public String get_tc_time_control() {
		return _tc_time_control;
	}

	public void set_tc_time_control(String _tc_time_control) {
		this._tc_time_control = _tc_time_control;
	}

	public String get_tdri_time() {
		return _tdri_time;
	}

	public void set_tdri_time(String _tdri_time) {
		this._tdri_time = _tdri_time;
	}

	public String get_tdri_utc_date() {
		return _tdri_utc_date;
	}

	public void set_tdri_utc_date(String _tdri_utc_date) {
		this._tdri_utc_date = _tdri_utc_date;
	}

	public String get_tdri_utc_time() {
		return _tdri_utc_time;
	}

	public void set_tdri_utc_time(String _tdri_utc_time) {
		this._tdri_utc_time = _tdri_utc_time;
	}

	public String get_str_event_type() {
		return _str_event_type;
	}

	public void set_str_event_type(String _str_event_type) {
		this._str_event_type = _str_event_type;
	}

	public String get_str_event_country() {
		return _str_event_country;
	}

	public void set_str_event_country(String _str_event_country) {
		this._str_event_country = _str_event_country;
	}

	public String get_str_event_rounds() {
		return _str_event_rounds;
	}

	public void set_str_event_rounds(String _str_event_rounds) {
		this._str_event_rounds = _str_event_rounds;
	}

	public String get_source() {
		return _source;
	}

	public void set_source(String _source) {
		this._source = _source;
	}

	public String get_source_date() {
		return _source_date;
	}

	public void set_source_date(String _source_date) {
		this._source_date = _source_date;
	}

	public String get_white_team() {
		return _white_team;
	}

	public void set_white_team(String _white_team) {
		this._white_team = _white_team;
	}

	public String get_white_team_country() {
		return _white_team_country;
	}

	public void set_white_team_country(String _white_team_country) {
		this._white_team_country = _white_team_country;
	}
	
}

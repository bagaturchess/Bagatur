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


public interface PGNConstants {
  //Game separators arrays (13-'\r',10-'\n',91-'[',93-']')
  public static final int[] PGN_GAME_PREFIX = {13, 10, 13, 10, 91};
  public static final int[] PGN_GAME_SUFFIX = {13, 10, 13, 10};
  public static final int[] PGN_GAME_DELIM = {93, 13, 10, 13, 10};

  public static final String STR_GAME_TERMINATION_MARKER_WHITE_WINS = "1-0";
  public static final String STR_GAME_TERMINATION_MARKER_BLACK_WINS = "0-1";
  public static final String STR_GAME_TERMINATION_MARKER_DRAWN = "1/2-1/2";
  public static final String STR_GAME_TERMINATION_MARKER_UNKNOWN = "*";

  public static final int PGN_GAME_STATUS_CREATING_INSTANCE = 1;
  public static final int PGN_GAME_STATUS_PARSING = 2;
  public static final int PGN_GAME_STATUS_PARSED_NOT_IN_REDUCED_FORMAT = 3;
  public static final int PGN_GAME_STATUS_PARSED_WITH_ERRORS = 4;
  public static final int PGN_GAME_STATUS_PARSED_INVALID = 5;
  public static final int PGN_GAME_STATUS_PARSED_SUCCESSFULLY = 6;
  public static final int PGN_GAME_STATUS_WAITING_FOR_PROCESS = 7;
  public static final int PGN_GAME_STATUS_IN_PROCESS = 8;
  public static final int PGN_GAME_STATUS_PROCESS_FINISHD_WITH_ERRORS = 9;
  public static final int PGN_GAME_STATUS_PROCESS_FINISHED_SUCCESSFULLY = 10;

  public static final String FILE_PGN_SUFFIX = ".pgn";
  public static final String FILE_JAR_SUFFIX = ".jar";
  public static final String FILE_ZIP_SUFFIX = ".zip";

  //STR - SEVEN_TAG_ROSTER
  public static final String PROPERTY_TAG_STR_EVENT = "Event";
  public static final String PROPERTY_TAG_STR_SITE = "Site";
  public static final String PROPERTY_TAG_STR_DATE = "Date";
  public static final String PROPERTY_TAG_STR_ROUND = "Round";
  public static final String PROPERTY_TAG_STR_WHITE = "White";
  public static final String PROPERTY_TAG_STR_BLACK = "Black";
  public static final String PROPERTY_TAG_STR_RESULT = "Result";
  //PRI - PLAYER_RELATED_INFORMATIN
  public static final String PROPERTY_TAG_PRI_WHITE_TITLE = "WhiteTitle";
  public static final String PROPERTY_TAG_PRI_WHITE_ELO = "WhiteElo";
  public static final String PROPERTY_TAG_PRI_WHITE_USCF = "WhiteUSCF";
  public static final String PROPERTY_TAG_PRI_WHITE_NA = "WhiteNA";
  public static final String PROPERTY_TAG_PRI_WHITE_TYPE = "WhiteType";
  //ERI - EVENT_RELATED_INFORMATION
  public static final String PROPERTY_TAG_ERI_EVENT_DATE = "EventDate";
  public static final String PROPERTY_TAG_ERI_EVENT_SPONSOR = "EventSponsor";
  public static final String PROPERTY_TAG_ERI_SECTION = "Section";
  public static final String PROPERTY_TAG_ERI_STAGE = "Stage";
  public static final String PROPERTY_TAG_ERI_BOARD = "Board";
  //OI - OPENING_INFORMATION
  public static final String PROPERTY_TAG_OI_OPENING = "Opening";
  public static final String PROPERTY_TAG_OI_VARIATION = "Variation";
  public static final String PROPERTY_TAG_OI_SUB_VARIATION = "SubVariation";
  public static final String PROPERTY_TAG_OI_ECO = "ECO";
  public static final String PROPERTY_TAG_OI_NIC = "NIC";
  //TDRI - TIME_DATE_RELATED_INFORMATION
  public static final String PROPERTY_TAG_TDRI_TIME = "Time";
  public static final String PROPERTY_TAG_TDRI_UTC_TIME = "UTCTime";
  public static final String PROPERTY_TAG_TDRI_UTC_DATE = "UTCDate";
  //TC - TIME_CONTROL
  public static final String PROPERTY_TAG_TC_TIME_CONTROL = "TimeControl";
  //ASP - ALTERNATIVE_STARTING_POSITIONS
  public static final String PROPERTY_TAG_ASP_SETUP = "SetUp";
  public static final String PROPERTY_TAG_ASP_FEN = "FEN";
  //GC - GAME_CONCLUSION
  public static final String PROPERTY_TAG_GC_TERMINATION = "Termination";
  //M - MISCELLANEOUS
  public static final String PROPERTY_TAG_M_ANNOTATOR = "Annotator";
  public static final String PROPERTY_TAG_M_MODE = "Mode";
  public static final String PROPERTY_TAG_M_PLY_COUNT = "PlyCount";
  //OTHERS - OTHERS
  public static final String PROPERTY_TAG_OTHERS_WHITE_ELO = "WhiteElo";
  public static final String PROPERTY_TAG_OTHERS_BLACK_ELO = "BlackElo";

  public static final String PROPERTY_TAG_UNKNOWN_EVENT_TYPE = "EventType";
  public static final String PROPERTY_TAG_UNKNOWN_EVENT_ROUNDS = "EventRounds";
  public static final String PROPERTY_TAG_UNKNOWN_EVENT_COUNTRY = "EventCountry";
  public static final String PROPERTY_TAG_UNKNOWN_SOURCE = "Source";
  public static final String PROPERTY_TAG_UNKNOWN_SOURCE_DATE = "SourceDate";
  public static final String PROPERTY_TAG_UNKNOWN_WHITE_TEAM = "WhiteTeam";
  public static final String PROPERTY_TAG_UNKNOWN_WHITE_TEAM_COUNTRY = "WhiteTeamCountry";  
  public static final String PROPERTY_TAG_UNKNOWN_BLACK_TEAM = "BlackTeam";
  public static final String PROPERTY_TAG_UNKNOWN_BLACK_TEAM_COUNTRY = "BlackTeamCountry";
  
  public static final String STR_ELLIPSIS = "..";

  public static final char CHAR_VALUE_PREFIX = '"';
  public static final char CHAR_VALUE_SUFFIX = '"';
  public static final char CHAR_PROPERTY_PREFIX = '[';
  public static final char CHAR_PROPERTY_SUFFIX = ']';
  public static final char CHAR_COMMENT_PREFIX = '{';
  public static final char CHAR_COMMENT_SUFFIX = '}';
  public static final char CHAR_VARIANT_PREFIX = '(';
  public static final char CHAR_VARIANT_SUFFIX = ')';
  //Numeric Annotation Glyph (NAG)
  public static final char CHAR_NAG = '$';
  public static final char CHAR_DOT = '.';

  public static final int ASCII_SPECIAL_CHAR_MAX = 32;

  public static final String DATE_FORMAT = "yyyy.MM.dd";
  public static final String DATE_UNKNOWN_YEAR = "????";
  public static final String DATE_UNKNOWN_MONTH = "??";
  public static final String DATE_UNKNOWN_DAY = "??";
  public static final String DATE_BLANK_YEAR = "0001";
  public static final String DATE_BLANK_MONTH = "01";
  public static final String DATE_BLANK_DAY = "01";
  public static final String DATE_FIELD_SEPARATOR = ".";

  //SAN (Standard Algebraic Notation) properties
  public static final String SAN_CASTLE_KING_SIDE_STR = "O-O";
  public static final String SAN_CASTLE_QUEEN_SIDE_STR = "O-O-O";
  public static final String SAN_CHECK_SUFFIX_STR = "+";
  public static final String SAN_MATE_SUFFIX_STR = "#";
  public static final String SAN_PROMOTIONS_STR = "=";
  public static final String SAN_CAPTURE_STR = "x";
  public static final char SAN_CHECK_SUFFIX_CHAR = '+';
  public static final char SAN_MATE_SUFFIX_CHAR = '#';
  public static final char SAN_PROMOTIONS_CHAR = '=';
  public static final char SAN_CAPTURE_CHAR = 'x';
  public static final String SAN_IDENTIFIER_PAWN_STR = "P";
  public static final String SAN_IDENTIFIER_KNIGHT_STR = "N";
  public static final String SAN_IDENTIFIER_BISHOP_STR = "B";
  public static final String SAN_IDENTIFIER_ROOK_STR = "R";
  public static final String SAN_IDENTIFIER_QUEEN_STR = "Q";
  public static final String SAN_IDENTIFIER_KING_STR = "K";
  public static final char SAN_IDENTIFIER_PAWN_CHAR = 'P';
  public static final char SAN_IDENTIFIER_KNIGHT_CHAR = 'N';
  public static final char SAN_IDENTIFIER_BISHOP_CHAR = 'B';
  public static final char SAN_IDENTIFIER_ROOK_CHAR = 'R';
  public static final char SAN_IDENTIFIER_QUEEN_CHAR = 'Q';
  public static final char SAN_IDENTIFIER_KING_CHAR = 'K';
  public static final String SAN_FILE_A_STR = "a";
  public static final String SAN_FILE_B_STR = "b";
  public static final String SAN_FILE_C_STR = "c";
  public static final String SAN_FILE_D_STR = "d";
  public static final String SAN_FILE_E_STR = "e";
  public static final String SAN_FILE_F_STR = "f";
  public static final String SAN_FILE_G_STR = "g";
  public static final String SAN_FILE_H_STR = "h";
  public static final String SAN_RANK_1_STR = "1";
  public static final String SAN_RANK_2_STR = "2";
  public static final String SAN_RANK_3_STR = "3";
  public static final String SAN_RANK_4_STR = "4";
  public static final String SAN_RANK_5_STR = "5";
  public static final String SAN_RANK_6_STR = "6";
  public static final String SAN_RANK_7_STR = "7";
  public static final String SAN_RANK_8_STR = "8";
  public static final char SAN_FILE_A_CHAR = 'a';
  public static final char SAN_FILE_B_CHAR = 'b';
  public static final char SAN_FILE_C_CHAR = 'c';
  public static final char SAN_FILE_D_CHAR = 'd';
  public static final char SAN_FILE_E_CHAR = 'e';
  public static final char SAN_FILE_F_CHAR = 'f';
  public static final char SAN_FILE_G_CHAR = 'g';
  public static final char SAN_FILE_H_CHAR = 'h';
  public static final char SAN_RANK_1_CHAR = '1';
  public static final char SAN_RANK_2_CHAR = '2';
  public static final char SAN_RANK_3_CHAR = '3';
  public static final char SAN_RANK_4_CHAR = '4';
  public static final char SAN_RANK_5_CHAR = '5';
  public static final char SAN_RANK_6_CHAR = '6';
  public static final char SAN_RANK_7_CHAR = '7';
  public static final char SAN_RANK_8_CHAR = '8';
  public static final int SAN_FILE_UNDEFINED = -1;
  public static final int SAN_RANK_UNDEFINED = -1;

  public static final int PGN_PLAYER_THREADS_POOL_SIZE = 8;
  public static final int PGN_REPLAYER_THREADS_POOL_SIZE = 8;
	public static final int PGN_THREATENS_EXTRACTOR_THREADS_POOL_SIZE = 8;

  public static final int PGN_CMPLETED_GAMES_INITIAL_SIZE = 64;
	public static final int PGN_REPLAYED_GAMES_INITIAL_SIZE = 64;

  public static final int PGN_REPLAY_DISTRIBUTOR_THREAD_SLEEP_TIME = 1;
  public static final int PGN_REPLAY_COUNT = 3;

	public static final int PGN_THREATENS_EXTRACTOR_DISTRIBUTOR_THREAD_SLEEP_TIME = 10;
}

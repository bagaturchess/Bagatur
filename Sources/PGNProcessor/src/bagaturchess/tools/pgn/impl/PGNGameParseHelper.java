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

public class PGNGameParseHelper {

	
	static int initGameProperties(PGNGame game,StringBuffer aPGNGameAsStringBuffer) {
		//Get game properties
		int curKeyStartIndex = -1;
		int curKeyEndIndex = -1;
		int curValueStartIndex = -1;
		int curValueEndIndex = 0;
		char curChar;
		for (int i = 0; i < aPGNGameAsStringBuffer.length(); i++) {
			//If end of property block is reached then break for-cycle
			if (isIntBufferEqualsToStringBuffer(PGNConstants.PGN_GAME_DELIM,
				aPGNGameAsStringBuffer,
				i,
				i + PGNConstants.PGN_GAME_DELIM.length)) {
				//If we have new lines between game properties don't break cycle.
				int skipFrom = i + 1;
				while (aPGNGameAsStringBuffer.charAt(skipFrom)
					<= PGNConstants.ASCII_SPECIAL_CHAR_MAX)
					skipFrom++;
				if (aPGNGameAsStringBuffer.charAt(skipFrom)
					!= PGNConstants.CHAR_PROPERTY_PREFIX)
					break;
				i = skipFrom;
			}

			curChar = aPGNGameAsStringBuffer.charAt(i);
			if (curChar == PGNConstants.CHAR_PROPERTY_PREFIX) {
				//Get key start and end index
				curKeyStartIndex = i + 1;
				curKeyEndIndex = curKeyStartIndex;
				while (aPGNGameAsStringBuffer.charAt(curKeyEndIndex)
					> PGNConstants.ASCII_SPECIAL_CHAR_MAX)
					curKeyEndIndex++;

				//Get value start and end index
				curValueStartIndex = curKeyEndIndex;
				while (aPGNGameAsStringBuffer.charAt(curValueStartIndex)
					<= PGNConstants.ASCII_SPECIAL_CHAR_MAX
					|| aPGNGameAsStringBuffer.charAt(curValueStartIndex)
						== PGNConstants.CHAR_VALUE_PREFIX)
					curValueStartIndex++;

				curValueEndIndex = curValueStartIndex;
				while (aPGNGameAsStringBuffer.charAt(curValueEndIndex)
					!= PGNConstants.CHAR_VALUE_SUFFIX)
					curValueEndIndex++;

				//Add current key and value to game property members
//				game.addProperty( 
					createProperty(game,
						aPGNGameAsStringBuffer,
						curKeyStartIndex,
						curKeyEndIndex,
						curValueStartIndex,
						curValueEndIndex);
			}
		}

		return curValueEndIndex;
	}

	private static void createProperty(PGNGame game, StringBuffer aPGNGameAsStringBuffer, int aKeyStartIndex, int aKeyEndIndex, int aValueStartIndex, int aValueEndIndex) {
		String tagName = aPGNGameAsStringBuffer.substring(aKeyStartIndex, aKeyEndIndex);
		String tagValue = aPGNGameAsStringBuffer.substring(aValueStartIndex, aValueEndIndex);
		game.addProperty(tagName, tagValue);
	}
	
	
	private static boolean isIntBufferEqualsToStringBuffer(
			int[] aAscii,
			StringBuffer aStrBuf,
			int aFromIndex,
			int aEndIndex) {
			boolean result = false;

			if (!(aAscii.length < aEndIndex - aFromIndex)) {
				for (int i = aFromIndex; i < aEndIndex; i++) {
					if (aAscii[i - aFromIndex] != aStrBuf.charAt(i))
						break;

					if (i == aEndIndex - 1)
						result = true;
				}
			}

			return result;
		}

		private static boolean isStringBufferEndsWithString(
			StringBuffer aStrBuf,
			int pBufEndIndex,
			String aStr) {
			boolean result = false;
			int strLen = aStr.length();
			if (strLen <= pBufEndIndex && pBufEndIndex <= aStrBuf.length()) {
				for (int i = 0; i < strLen; i++) {
					if (aStr.charAt(i) != aStrBuf.charAt(pBufEndIndex - strLen + i + 1))
						break;
					if (i == strLen - 1)
						result = true;
				}
			}
			return result;
		}

		private static boolean isStringBufferEndsWithGameTerminationMarker(
			StringBuffer pStrBuf,
			int pBufEndIndex) {
			return isStringBufferEndsWithString(
				pStrBuf,
				pBufEndIndex,
				PGNConstants.STR_GAME_TERMINATION_MARKER_WHITE_WINS)
				|| isStringBufferEndsWithString(
					pStrBuf,
					pBufEndIndex,
					PGNConstants.STR_GAME_TERMINATION_MARKER_BLACK_WINS)
				|| isStringBufferEndsWithString(
					pStrBuf,
					pBufEndIndex,
					PGNConstants.STR_GAME_TERMINATION_MARKER_DRAWN)
				|| isStringBufferEndsWithString(
					pStrBuf,
					pBufEndIndex,
					PGNConstants.STR_GAME_TERMINATION_MARKER_UNKNOWN);
		}

		private static void getSingleTurn(PGNGame game,
			int aNumber,
			StringBuffer aTurnAsStrBuf,
			int aStartIndex,
			int aEndIndex) {
			//System.out.println( "'"+aTurnAsStrBuf.substring( aStartIndex, aEndIndex ) +"'");
			PGNTurn curTurn = getMove(aNumber, aTurnAsStrBuf, aStartIndex, aEndIndex);
			game.add(curTurn);
		}
	  
		private static PGNTurn getMove( int aNumber, StringBuffer aTurnAsStrBuf, int aStartIndex, int aEndIndex ) {
			 	int moveNumber = aNumber;
			
		    int curEndIndex = aStartIndex;
		    
		    while ( aTurnAsStrBuf.charAt( curEndIndex ) > PGNConstants.ASCII_SPECIAL_CHAR_MAX
		        && curEndIndex < aEndIndex )
		      curEndIndex++;
		
		    String whitePly = aTurnAsStrBuf.substring( aStartIndex, curEndIndex );
		    String blackPly = null;
		    
		    if ( curEndIndex != aEndIndex ) {
		    	while ( aTurnAsStrBuf.charAt( curEndIndex ) <= PGNConstants.ASCII_SPECIAL_CHAR_MAX )
		    		curEndIndex++;
		    	blackPly = aTurnAsStrBuf.substring( curEndIndex, aEndIndex );
		    }
			
			return new PGNTurn(moveNumber, whitePly, blackPly);
		}
		
		static boolean initReducedFormatFlags(PGNGame game,
				StringBuffer aPGNGameAsStringBuffer,
				int aStartIndex) {
				boolean mHasCommentary = false;
				boolean mHasRAV = false;
				boolean mHasNAG = false;
				boolean mHasEllipsis =
					aPGNGameAsStringBuffer.indexOf(PGNConstants.STR_ELLIPSIS) != -1;

				for (int i = aStartIndex; i < aPGNGameAsStringBuffer.length(); i++) {
					if (aPGNGameAsStringBuffer.charAt(i) == PGNConstants.CHAR_COMMENT_PREFIX)
						mHasCommentary = true;

					if (aPGNGameAsStringBuffer.charAt(i) == PGNConstants.CHAR_VARIANT_PREFIX)
						mHasRAV = true;

					if (aPGNGameAsStringBuffer.charAt(i) == PGNConstants.CHAR_NAG)
						mHasNAG = true;
				}

				boolean mHasStandardSevenTag =
					(game.getProperty(PGNConstants.PROPERTY_TAG_STR_EVENT) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_SITE) != null) 
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_DATE) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_ROUND) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_WHITE) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_BLACK) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_RESULT) != null);

				boolean mIsReducedFormat =
					!mHasEllipsis
						&& !mHasCommentary
						&& mHasStandardSevenTag
						&& !mHasRAV
						&& !mHasNAG;
				return mIsReducedFormat;
			}
		
		static boolean initReducedFormatFlags_ignoreCommentsAndVariants(PGNGame game,
				StringBuffer aPGNGameAsStringBuffer,
				int aStartIndex) {
				boolean mHasCommentary = false;
				boolean mHasRAV = false;
				boolean mHasNAG = false;
				boolean mHasEllipsis =
					aPGNGameAsStringBuffer.indexOf(PGNConstants.STR_ELLIPSIS) != -1;

				for (int i = aStartIndex; i < aPGNGameAsStringBuffer.length(); i++) {
					if (aPGNGameAsStringBuffer.charAt(i) == PGNConstants.CHAR_COMMENT_PREFIX)
						mHasCommentary = true;

					if (aPGNGameAsStringBuffer.charAt(i) == PGNConstants.CHAR_VARIANT_PREFIX)
						mHasRAV = true;

					if (aPGNGameAsStringBuffer.charAt(i) == PGNConstants.CHAR_NAG)
						mHasNAG = true;
				}

				boolean mHasStandardSevenTag =
					(game.getProperty(PGNConstants.PROPERTY_TAG_STR_EVENT) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_SITE) != null) 
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_DATE) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_ROUND) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_WHITE) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_BLACK) != null)
						&& (game.getProperty(PGNConstants.PROPERTY_TAG_STR_RESULT) != null);

				boolean mIsReducedFormat =
					!mHasEllipsis
						//&& !mHasCommentary
						&& mHasStandardSevenTag
						//&& !mHasRAV
						&& !mHasNAG;
				return mIsReducedFormat;
			}
		
		static void extractGameTurns(PGNGame game,
				StringBuffer aPGNGameAsStringBuffer,
				int aStartIndex) {
				int curCountOfDots = 0;
				int curTurnStartIndex = -1;
				int curTurnEndIndex = -1;
				int bufEndIndex = aPGNGameAsStringBuffer.length() - 1;
				char curChar;

				//Shift left bufEndIndex to end of game termination marker
				while (aPGNGameAsStringBuffer.charAt(bufEndIndex)
					<= PGNConstants.ASCII_SPECIAL_CHAR_MAX)
					bufEndIndex--;
				//Here must be ends with game termination marker "0-1", "1-0", "1/2-1/2" or "*"
				if (isStringBufferEndsWithGameTerminationMarker(aPGNGameAsStringBuffer,
					bufEndIndex)) {
					while (aPGNGameAsStringBuffer.charAt(bufEndIndex)
						> PGNConstants.ASCII_SPECIAL_CHAR_MAX)
						bufEndIndex--;
					while (aPGNGameAsStringBuffer.charAt(bufEndIndex)
						<= PGNConstants.ASCII_SPECIAL_CHAR_MAX)
						bufEndIndex--;
				}

				//Here we are to the end of last turn
				for (int i = aStartIndex; i <= bufEndIndex; i++) {
					curChar = aPGNGameAsStringBuffer.charAt(i);
					if (curChar == PGNConstants.CHAR_DOT) {
						curCountOfDots++;

						curTurnStartIndex = i + 1;
						while (aPGNGameAsStringBuffer.charAt(curTurnStartIndex)
							<= PGNConstants.ASCII_SPECIAL_CHAR_MAX
							&& curTurnEndIndex < bufEndIndex)
							curTurnStartIndex++;

						curTurnEndIndex = curTurnStartIndex;

						while (aPGNGameAsStringBuffer.charAt(curTurnEndIndex)
							> PGNConstants.ASCII_SPECIAL_CHAR_MAX
							&& curTurnEndIndex < bufEndIndex)
							curTurnEndIndex++;

						while (aPGNGameAsStringBuffer.charAt(curTurnEndIndex)
							<= PGNConstants.ASCII_SPECIAL_CHAR_MAX
							&& curTurnEndIndex < bufEndIndex)
							curTurnEndIndex++;

						while (aPGNGameAsStringBuffer.charAt(curTurnEndIndex)
							> PGNConstants.ASCII_SPECIAL_CHAR_MAX
							&& curTurnEndIndex < bufEndIndex)
							curTurnEndIndex++;

						if (curTurnEndIndex == bufEndIndex) {
							getSingleTurn(game,
								curCountOfDots,
								aPGNGameAsStringBuffer,
								curTurnStartIndex,
								curTurnEndIndex + 1);
						} else {
							getSingleTurn(game,
								curCountOfDots,
								aPGNGameAsStringBuffer,
								curTurnStartIndex,
								curTurnEndIndex);
						}

						i = curTurnEndIndex;
					}
				}
			}
		
		static void extractGameTurns_ignoreCommentsAndVariants(PGNGame game,
				StringBuffer aPGNGameAsStringBuffer_withCommentsAndVariants,
				int aStartIndex) {
				
				StringBuffer aPGNGameAsStringBuffer = new StringBuffer(aPGNGameAsStringBuffer_withCommentsAndVariants.length());
				//boolean[] ignored = new boolean[aPGNGameAsStringBuffer.length()];
				int curIndex = 0;
				int openedBrackets = 0;
				while (curIndex < aPGNGameAsStringBuffer_withCommentsAndVariants.length()) {
					char curChar = aPGNGameAsStringBuffer_withCommentsAndVariants.charAt(curIndex);
					
					if (curChar == PGNConstants.CHAR_COMMENT_PREFIX) openedBrackets++;
					if (curChar == PGNConstants.CHAR_VARIANT_PREFIX) openedBrackets++;
					
					if (openedBrackets < 0) {
						throw new IllegalStateException("openedBrackets=" + openedBrackets);
					}
					
					if (openedBrackets == 0) {
						//ignored[curIndex] = true;
						aPGNGameAsStringBuffer.append(curChar);
					}
					
					if (curChar == PGNConstants.CHAR_COMMENT_SUFFIX) openedBrackets--;
					if (curChar == PGNConstants.CHAR_VARIANT_SUFFIX) openedBrackets--;
					
					curIndex++;
				}
				
				int curCountOfDots = 0;
				int curTurnStartIndex = -1;
				int curTurnEndIndex = -1;
				int bufEndIndex = aPGNGameAsStringBuffer.length() - 1;
				char curChar;
				
				//Shift left bufEndIndex to end of game termination marker
				while (aPGNGameAsStringBuffer.charAt(bufEndIndex)
					<= PGNConstants.ASCII_SPECIAL_CHAR_MAX)
					bufEndIndex--;
				
				//Here must be ends with game termination marker "0-1", "1-0", "1/2-1/2" or "*"
				if (isStringBufferEndsWithGameTerminationMarker(aPGNGameAsStringBuffer,
					bufEndIndex)) {
					while (aPGNGameAsStringBuffer.charAt(bufEndIndex)
						> PGNConstants.ASCII_SPECIAL_CHAR_MAX)
						bufEndIndex--;
					while (aPGNGameAsStringBuffer.charAt(bufEndIndex)
						<= PGNConstants.ASCII_SPECIAL_CHAR_MAX)
						bufEndIndex--;
				}

				//Here we are to the end of last turn
				for (int i = aStartIndex; i <= bufEndIndex; i++) {
					curChar = aPGNGameAsStringBuffer.charAt(i);
					if (curChar == PGNConstants.CHAR_DOT) {
						curCountOfDots++;

						curTurnStartIndex = i + 1;
						while (aPGNGameAsStringBuffer.charAt(curTurnStartIndex)
							<= PGNConstants.ASCII_SPECIAL_CHAR_MAX
							&& curTurnEndIndex < bufEndIndex)
							curTurnStartIndex++;

						curTurnEndIndex = curTurnStartIndex;

						while (aPGNGameAsStringBuffer.charAt(curTurnEndIndex)
							> PGNConstants.ASCII_SPECIAL_CHAR_MAX
							&& curTurnEndIndex < bufEndIndex)
							curTurnEndIndex++;

						while (aPGNGameAsStringBuffer.charAt(curTurnEndIndex)
							<= PGNConstants.ASCII_SPECIAL_CHAR_MAX
							&& curTurnEndIndex < bufEndIndex)
							curTurnEndIndex++;

						while (aPGNGameAsStringBuffer.charAt(curTurnEndIndex)
							> PGNConstants.ASCII_SPECIAL_CHAR_MAX
							&& curTurnEndIndex < bufEndIndex)
							curTurnEndIndex++;

						if (curTurnEndIndex == bufEndIndex) {
							getSingleTurn(game,
								curCountOfDots,
								aPGNGameAsStringBuffer,
								curTurnStartIndex,
								curTurnEndIndex + 1);
						} else {
							getSingleTurn(game,
								curCountOfDots,
								aPGNGameAsStringBuffer,
								curTurnStartIndex,
								curTurnEndIndex);
						}

						i = curTurnEndIndex;
					}
				}
			}
}

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


/**
 * Created by Krasimir Topchiyski
 * Date: 2003-11-4
 * Time: 10:53:32
 *
 */
public class PGNGameCounter {
  private int mGamesParsing = 0;
  private int mGamesParsedNotInReducedFormat = 0;
  private int mGamesParsedWithErrors = 0;
  private int mGamesParsedInvalid = 0;
  private int mGamesParsedSuccessfully = 0;
  private int mPGNGamesTotal = 0;


  public synchronized void increaseCounter( int pPGNGameStatus ) {
    if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSING ) {
      mGamesParsing++;
      mPGNGamesTotal++;
    } else if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSED_NOT_IN_REDUCED_FORMAT ) {
      mGamesParsedNotInReducedFormat++;
    } else if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSED_WITH_ERRORS ) {
      mGamesParsedWithErrors++;
    } else if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSED_INVALID ) {
      mGamesParsedInvalid++;
    } else if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSED_SUCCESSFULLY ) {
      mGamesParsedSuccessfully++;
    } else {
      throw new IllegalArgumentException( "Invalid PGN game status='" + pPGNGameStatus + "'!" );
    }
  }


  public synchronized void decreaseCounter( int pPGNGameStatus ) {
    if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSING ) {
      mGamesParsing--;
    } else if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSED_NOT_IN_REDUCED_FORMAT ) {
      mGamesParsedNotInReducedFormat--;
    } else if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSED_WITH_ERRORS ) {
      mGamesParsedWithErrors--;
    } else if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSED_INVALID ) {
      mGamesParsedInvalid--;
    } else if ( pPGNGameStatus == PGNConstants.PGN_GAME_STATUS_PARSED_SUCCESSFULLY ) {
      mGamesParsedSuccessfully--;
    } else {
      throw new IllegalArgumentException( "Invalid PGN game status='" + pPGNGameStatus + "'!" );
    }
  }


  public int getGamesParsing() {
    return mGamesParsing;
  }


  public void setGamesParsing( int pGamesParsing ) {
    mGamesParsing = pGamesParsing;
  }


  public int getGamesParsedNotInReducedFormat() {
    return mGamesParsedNotInReducedFormat;
  }


  public void setGamesParsedNotInReducedFormat( int pGamesParsedNotInReducedFormat ) {
    mGamesParsedNotInReducedFormat = pGamesParsedNotInReducedFormat;
  }


  public int getGamesParsedWithErrors() {
    return mGamesParsedWithErrors;
  }


  public void setGamesParsedWithErrors( int pGamesParsedWithErrors ) {
    mGamesParsedWithErrors = pGamesParsedWithErrors;
  }


  public int getGamesParsedInvalid() {
    return mGamesParsedInvalid;
  }


  public void setGamesParsedInvalid( int pGamesParsedInvalid ) {
    mGamesParsedInvalid = pGamesParsedInvalid;
  }


  public int getGamesParsedSuccessfully() {
    return mGamesParsedSuccessfully;
  }


  public void setGamesParsedSuccessfully( int pGamesParsedSuccessfully ) {
    mGamesParsedSuccessfully = pGamesParsedSuccessfully;
  }


  public int getPGNGamesTotal() {
    return mPGNGamesTotal;
  }


  public void setPGNGamesTotal( int pPGNGamesTotal ) {
    mPGNGamesTotal = pPGNGamesTotal;
  }


  public synchronized String toString() {
    String result = "[Parsing: " + mGamesParsing +
        " ParsedNotRF: " + mGamesParsedNotInReducedFormat +
        " ParsedWithErrors: " + mGamesParsedWithErrors +
        " ParsedInvalid: " + mGamesParsedInvalid +
        " ParsedOK: " + mGamesParsedSuccessfully +
        " Total: " + mPGNGamesTotal + "]";
    return result;
  }
}

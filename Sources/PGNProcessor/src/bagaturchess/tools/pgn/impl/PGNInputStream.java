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
 *
 * PGN grammar can be found here
 * http://www.very-best.de/pgn-spec.htm.
 */


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class PGNInputStream {
  //FileInputStream to PGN file
  InputStream mNativeIS = null;

  //Internal string buffer for current game
  StringBuffer mPGNGameBuf = new StringBuffer();

  private int mBufferSize = Math.max( PGNConstants.PGN_GAME_PREFIX.length,
                                      PGNConstants.PGN_GAME_SUFFIX.length );

  private byte[] mTempByteBuffer = new byte[ mBufferSize ];

  private PGNGameCounter mPGNGameCounter;


  private static void shiftLeftByteArray( byte[] aCurBuf, int aNewByte ) {
    for ( int i = 0; i < aCurBuf.length - 1; i++ ) {
      aCurBuf[ i ] = aCurBuf[ i + 1 ];
    }

    aCurBuf[ aCurBuf.length - 1 ] = ( byte ) aNewByte;
  }


  //Tests if aCurBuf ands with aTemplateArr
  //Size of aTemplateArr is less then aCurBuf
  private static boolean endsWithArray( byte[] aCurBuf, int[] aTemplateArr ) {
    boolean result = false;

    int sizeDif = aCurBuf.length - aTemplateArr.length;

    for ( int i = aTemplateArr.length - 1; i >= 0; i-- ) {
      if ( ( byte ) aTemplateArr[ i ] != aCurBuf[ i + sizeDif ] )
        break;

      if ( i == 0 )
      //after 'result = true' possibly break for-cycle
        result = true;
    }

    return result;
  }


  public PGNInputStream( InputStream pPGNFileInputStream) {
    mNativeIS = pPGNFileInputStream;
  }


  public PGNInputStream( String pAbsoluteFilePathToPGN)
      throws FileNotFoundException {
    mNativeIS = new FileInputStream( pAbsoluteFilePathToPGN );
  }


  public PGNGame readGame()
      throws IOException {
    PGNGame tempPGNGame = null;

    int tempInt;
    boolean isInGame = false;

    mPGNGameBuf.delete( 0, mPGNGameBuf.length() );
    while ( ( tempInt = mNativeIS.read() ) != -1 ) {
      shiftLeftByteArray( mTempByteBuffer, tempInt );

      if ( endsWithArray( mTempByteBuffer, PGNConstants.PGN_GAME_PREFIX ) ) {
        mPGNGameBuf.append( ( char ) PGNConstants.PGN_GAME_PREFIX[ PGNConstants.PGN_GAME_PREFIX.length - 1 ] );
        isInGame = true;
      } else if ( endsWithArray( mTempByteBuffer, PGNConstants.PGN_GAME_SUFFIX )
          && !endsWithArray( mTempByteBuffer, PGNConstants.PGN_GAME_DELIM ) ) {
        if ( isInGame ) {
          mPGNGameBuf.delete( mPGNGameBuf.length() - PGNConstants.PGN_GAME_SUFFIX.length + 1, mPGNGameBuf.length() );
          break;
        }
      } else {
        if ( isInGame ) {
          mPGNGameBuf.append( ( char ) tempInt );
        }
      }
    }

    if ( mPGNGameBuf.length() > 0 ) {
    	tempPGNGame = new PGNGame();
    	tempPGNGame.load(mPGNGameBuf);
    }

    return tempPGNGame;
  }


  public void close()
      throws IOException {
    mNativeIS.close();
  }
}

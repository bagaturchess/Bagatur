/**
 * FrankWalter - a java chess engine
 * Copyright 2019 Laurens Winkelhagen (ljgw@users.noreply.github.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package bagaturchess.egtb.syzygy;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;

import com.winkelhagen.chess.syzygy.SyzygyBridge;


/**
 * converter class to fit the FrankWalter board representation on the SyzygyBridge
 */
public class SyzygyTBProbing {
	
	
	private static boolean loadingInitiated;
	private static SyzygyTBProbing instance;
	
	
    private SyzygyTBProbing() {
    	loadingInitiated = false;
    }
    
    
	public boolean loadNativeLibrary() {
		return SyzygyBridge.loadNativeLibrary();
	}
	
	
    public static final SyzygyTBProbing getSingleton() {
    	if (instance == null && !loadingInitiated) {
    		instance = new SyzygyTBProbing();
    		if (!instance.loadNativeLibrary()) {
    			instance = null;
    		}
    		loadingInitiated = true;
    	}
    	return instance;
    }
    
    public final void load(String path) {
    	SyzygyBridge.load(path);
    }
    
    /**
     * wrapper for {@link com.winkelhagen.chess.syzygy.SyzygyBridge#isAvailable(int)}
     * @param piecesLeft the number of pieces left on the board
     * @return true iff there is a Syzygy result to be expected, given the number of pieces currently on the board
     */
    public boolean isAvailable(int piecesLeft){
        return SyzygyBridge.isAvailable(piecesLeft);
    }
    
    
    /**
     * probes the Syzygy TableBases for a WinDrawLoss result
     * @param board the FrankWalter board representation
     * @return a WDL result (see {@link #getWDLScore(int, int)})
     */
    public int probeWDL(IBitBoard board){
        if (board.hasRightsToKingCastle(Constants.COLOUR_WHITE) || board.hasRightsToQueenCastle(Constants.COLOUR_WHITE)
        		|| board.hasRightsToKingCastle(Constants.COLOUR_BLACK) || board.hasRightsToQueenCastle(Constants.COLOUR_BLACK)){
            return -1;
        }
        return SyzygyBridge.probeSyzygyWDL(
        		convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_WHITE)),
        		convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_BLACK)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KING)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KING)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_QUEEN)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_QUEEN)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_ROOK)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_ROOK)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN)),
                0,//board.getEpSquare()==-1?0:board.getEpSquare(),
                board.getColourToMove() == Constants.COLOUR_WHITE
        );
    }
    

	/**
     * probes the Syzygy TableBases for a DistanceToZero result.
     * If castling is still allowed, no accurate DTZ can be given
     * @param board the FrankWalter board representation
     * @return a WDL result (see {@link #toXBoardScore(int)} and {@link #toMove(int)})
     */
    public synchronized int probeDTZ(IBitBoard board){
        if (board.hasRightsToKingCastle(Constants.COLOUR_WHITE) || board.hasRightsToQueenCastle(Constants.COLOUR_WHITE)
        		|| board.hasRightsToKingCastle(Constants.COLOUR_BLACK) || board.hasRightsToQueenCastle(Constants.COLOUR_BLACK)){
            return -1;
        }
        return SyzygyBridge.probeSyzygyDTZ(
        		convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_WHITE)),
        		convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_BLACK)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KING)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KING)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_QUEEN)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_QUEEN)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_ROOK)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_ROOK)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_BISHOP)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_BISHOP)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT)),
        		convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_PAWN)) | convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_PAWN)),
                board.getDraw50movesRule(),
                0,//board.getEpSquare()==-1?0:board.getEpSquare(),
                board.getColourToMove() == Constants.COLOUR_WHITE
        );
    }


    public int toMove(int result){
        int from = (result & SyzygyConstants.TB_RESULT_FROM_MASK) >> SyzygyConstants.TB_RESULT_FROM_SHIFT;
        int to = (result & SyzygyConstants.TB_RESULT_TO_MASK) >> SyzygyConstants.TB_RESULT_TO_SHIFT;
        int promotes = (result & SyzygyConstants.TB_RESULT_PROMOTES_MASK) >> SyzygyConstants.TB_RESULT_PROMOTES_SHIFT;
        return getMove(from, to, promotes);
    }

    public int getMove(int fromSquare, int toSquare, int promotes) {
        return fromSquare | (toSquare <<6) | (promotes << 12);
    }
    
    /**
     * returns the score associated to the move in the result (xboard compatible, i.e. (+/-) 28000-full moves to win/lose or 0 for draw.
     *
     * @param result of the DTZ tablebase operation
     * @return the score to be displayed by xboard
     * todo: fix: this returns DTZero, not DTMate.
     */
    public int toXBoardScore(int result){
        int dtz = (result & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
        int dtzFull = (dtz+1)/2;
        int wdl = (result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
        switch (wdl){
            case SyzygyConstants.TB_LOSS:
                return -28000 + dtzFull; //LW DTM: -100000 - dtzFull
            case SyzygyConstants.TB_BLESSED_LOSS:
                return 0;
            case SyzygyConstants.TB_DRAW:
                return 0;
            case SyzygyConstants.TB_CURSED_WIN:
                return +28000 - dtzFull; //LW DTM: +100000 + dtzFull
            case SyzygyConstants.TB_WIN:
                return +28000 - dtzFull; //LW DTM: +100000 + dtzFull
            default:
                return 0;
        }
    }
    
    /**
     * returns the score to use inside the main search, based on the WDL result of a TableBase query and the search depth
     * @param wdl the WinDrawLoss result from the probe
     * @param depth the depth of the current search
     * @return the score associated with this position
     */
    public synchronized int getWDLScore(int wdl, int depth) {
        switch (wdl){
            case SyzygyConstants.TB_LOSS:
                return -28000 + depth;
            case SyzygyConstants.TB_BLESSED_LOSS:
                return 0;//-27000 + depth;
            case SyzygyConstants.TB_DRAW:
                return 0;
            case SyzygyConstants.TB_CURSED_WIN:
                return 0;//27000 - depth;
            case SyzygyConstants.TB_WIN:
                return 28000 - depth;
            default:
                throw new IllegalStateException("wdl=" + wdl);
        }
    }
    
	private static long convertBB(long figures) {
		return figures;
	}
}
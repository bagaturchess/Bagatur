package bagaturchess.egtb.syzygy;

/**
 * Class containing SyzygyConstants and some relevant helper methods.
 * The public API is best described in the tbprobe.c file.
 */
public class SyzygyConstants {

    /*
     * Win-Draw-Loss results.
     * Blessed loss means that the position should lose, if it were not for the 50-move rule.
     * Cursed win conversely means that the position would be won, if it were not for the 50-move rule.
     */
    public static final int  TB_LOSS                     = 0;       /* LOSS */
    public static final int  TB_BLESSED_LOSS             = 1;       /* LOSS but 50-move draw */
    public static final int  TB_DRAW                     = 2;       /* DRAW */
    public static final int  TB_CURSED_WIN               = 3;       /* WIN but 50-move draw  */
    public static final int  TB_WIN                      = 4;       /* WIN  */

    /*
     * Promote values corresponding to No-piece, Queen, Rook, Bishop and Knight.
     */
    public static final int  TB_PROMOTES_NONE            = 0;
    public static final int  TB_PROMOTES_QUEEN           = 1;
    public static final int  TB_PROMOTES_ROOK            = 2;
    public static final int  TB_PROMOTES_BISHOP          = 3;
    public static final int  TB_PROMOTES_KNIGHT          = 4;

    /*
     * Masks and Shifts that allow us to store more information in the DTZ result
     */
    public static final int  TB_RESULT_WDL_MASK          = 0x0000000F;
    public static final int  TB_RESULT_TO_MASK           = 0x000003F0;
    public static final int  TB_RESULT_FROM_MASK         = 0x0000FC00;
    public static final int  TB_RESULT_PROMOTES_MASK     = 0x00070000;
    public static final int  TB_RESULT_DTZ_MASK          = 0xFFF00000;
    public static final int  TB_RESULT_WDL_SHIFT         = 0;
    public static final int  TB_RESULT_TO_SHIFT          = 4;
    public static final int  TB_RESULT_FROM_SHIFT        = 10;
    public static final int  TB_RESULT_PROMOTES_SHIFT    = 16;
    public static final int  TB_RESULT_DTZ_SHIFT         = 20;

    /**
     * return the 'from square' part of the move.
     * @param result the result of the probeDTZ operation.
     * @return square 1 .. 64. square x corresponds to a bitboard with value 2^(x-1),
     */
    public static int fromSquare(int result) {
        return (result & SyzygyConstants.TB_RESULT_FROM_MASK) >> SyzygyConstants.TB_RESULT_FROM_SHIFT;
    }

    /**
     * return the 'to square' part of the move.
     * @param result the result of the probeDTZ operation.
     * @return square 1 .. 64. square x corresponds to a bitboard with value 2^(x-1),
     */
    public static int toSquare(int result) {
        return (result & SyzygyConstants.TB_RESULT_TO_MASK) >> SyzygyConstants.TB_RESULT_TO_SHIFT;
    }

    /**
     * return the optional promote part of the move.
     * @param result the result of the probeDTZ operation.
     * @return square 1 .. 4 for queen, rook, bishop and knight, or 0 for no-piece.
     */
    public static int promoteInto(int result) {
        return (result & SyzygyConstants.TB_RESULT_PROMOTES_MASK) >> SyzygyConstants.TB_RESULT_PROMOTES_SHIFT;
    }

    /**
     * return the distance to zero of the position, the number of moves needed to reach the optimal transition (where the 50 move rule would be reset).
     * @param result the result of the probeDTZ operation.
     * @return the number of moves needed to reach the optimal transition (where the 50 move rule would be reset).
     */
    public static int distanceToZero(int result) {
        return (result & SyzygyConstants.TB_RESULT_DTZ_MASK) >> SyzygyConstants.TB_RESULT_DTZ_SHIFT;
    }

    /**
     * return the win-draw-loss status of the position.
     * @param result the result of the probeDTZ operation.
     * @return 1 .. 5 for loss, blessed loss, draw, cursed win and win.
     */
    public static int winDrawLoss(int result) {
        return (result & SyzygyConstants.TB_RESULT_WDL_MASK) >> SyzygyConstants.TB_RESULT_WDL_SHIFT;
    }
}

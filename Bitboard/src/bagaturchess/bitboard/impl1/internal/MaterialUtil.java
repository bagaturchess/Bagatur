package bagaturchess.bitboard.impl1.internal;


public class MaterialUtil {

	public static final int[][] VALUES = {
			// WHITE QQQRRRBBBNNNPPPP
			{ 0, 1 << 0, 1 << 4, 1 << 7, 1 << 10, 1 << 13 },
			// BLACK QQQRRRBBBNNNPPPP
			{ 0, 1 << 16, 1 << 20, 1 << 23, 1 << 26, 1 << 29 } };

	public static final int[] SHIFT = { 0, 16 };

	private static final int MASK_MINOR_MAJOR_ALL = 0xfff0fff0;
	private static final int MASK_MINOR_MAJOR_WHITE = 0xfff0;
	private static final int MASK_MINOR_MAJOR_BLACK = 0xfff00000;
	private static final int[] MASK_MINOR_MAJOR = { MASK_MINOR_MAJOR_WHITE, MASK_MINOR_MAJOR_BLACK };
	private static final int[] MASK_NON_NIGHTS = { 0xff8f, 0xff8f0000 };
	private static final int MASK_SINGLE_BISHOPS = 0x800080;
	private static final int MASK_SINGLE_BISHOP_NIGHT_WHITE = 0x90;
	private static final int MASK_SINGLE_BISHOP_NIGHT_BLACK = 0x900000;
	private static final int[] MASK_PAWNS_QUEENS = { 0xe00f, 0xe00f0000 };
	private static final int[] MASK_SLIDING_PIECES = { 0xff80, 0xff800000 };
	private static final int[] MASK_MATING_MATERIAL = { 0xff6f, 0xff6f0000 };

	public static boolean containsMajorPieces(final int material) {
		return (material & MASK_MINOR_MAJOR_ALL) != 0;
	}

	public static boolean hasNonPawnPieces(final int material, final int color) {
		return (material & MASK_MINOR_MAJOR[color]) != 0;
	}

	public static boolean hasWhiteNonPawnPieces(final int material) {
		return (material & MASK_MINOR_MAJOR_WHITE) != 0;
	}

	public static boolean hasBlackNonPawnPieces(final int material) {
		return (material & MASK_MINOR_MAJOR_BLACK) != 0;
	}

	public static boolean oppositeBishops(final int material) {
		return Long.bitCount(material & MASK_MINOR_MAJOR_ALL) == 2 && Long.bitCount(material & MASK_SINGLE_BISHOPS) == 2;
	}

	public static boolean onlyWhitePawnsOrOneNightOrBishop(final int material) {
		switch (Long.bitCount(material & MASK_MINOR_MAJOR_WHITE)) {
		case 0:
			return true;
		case 1:
			return Long.bitCount(material & MASK_SINGLE_BISHOP_NIGHT_WHITE) == 1;
		default:
			return false;
		}
	}

	public static boolean onlyBlackPawnsOrOneNightOrBishop(final int material) {
		switch (Long.bitCount(material & MASK_MINOR_MAJOR_BLACK)) {
		case 0:
			return true;
		case 1:
			return Long.bitCount(material & MASK_SINGLE_BISHOP_NIGHT_BLACK) == 1;
		default:
			return false;
		}
	}

	public static boolean hasPawnsOrQueens(final int material, final int color) {
		return (material & MASK_PAWNS_QUEENS[color]) != 0;
	}

	public static boolean hasOnlyNights(final int material, final int color) {
		return (material & MASK_NON_NIGHTS[color]) == 0;
	}

	public static int getMajorPieces(final int material, final int color) {
		return (material & MASK_MINOR_MAJOR[color]) >>> SHIFT[color];
	}

	public static boolean hasSlidingPieces(final int material, final int color) {
		return (material & MASK_SLIDING_PIECES[color]) != 0;
	}

	public static boolean isKBNK(final int material) {
		return material == 0x90 || material == 0x900000;
	}

	public static boolean isKRKP(final int material) {
		return material == 0x10400 || material == 0x4000001;
	}

	public static boolean isDrawByMaterial(final ChessBoard cb) {
		if (Long.bitCount(cb.allPieces) > 4) {
			return false;
		}
		switch (cb.materialKey) {
		case 0x0: // KK
		case 0x10: // KNK
		case 0x20: // KNNK
		case 0x80: // KBK
		case 0x100000: // KKN
		case 0x100010: // KNKN
		case 0x100080: // KNKB
		case 0x200000: // KKNN
		case 0x800000: // KKB
		case 0x800010: // KBKN
		case 0x800080: // KBKB
			return true;
		case 0x1: // KPK
		case 0x10000: // KPK
			//return KPKBitbase.isDraw(cb);
		case 0x81: // KBPK
		case 0x810000: // KBPK
			//return EndGameEvaluator.isKBPKDraw(cb.pieces);
		case 0x12000: // KQKP
		case 0x20000001: // KQKP
			//return EndGameEvaluator.isKQKPDraw(cb);
		}
		return false;

	}

	public static boolean hasMatingMaterial(final ChessBoard cb, final int color) {
		if (Long.bitCount(cb.friendlyPieces[color]) > 3) {
			return true;
		}
		if (Long.bitCount(cb.friendlyPieces[color]) > 2) {
			return !hasOnlyNights(cb.materialKey, color);
		}
		return (cb.materialKey & MASK_MATING_MATERIAL[color]) != 0;
	}

}

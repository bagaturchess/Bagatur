package bagaturchess.engines.evaladapters.carbalo;


/**
 * Discover attacks to squares
 */
public class BitboardAttacks {
	public long[] rook;
	public long[] bishop;
	public long[] knight;
	public long[] king;
	public long[][] pawn;

	/**
	 * If disabled, does not use Magic Bitboards, improves loading speed in GWT
	 * and avoids memory crashes in mobile browsers
	 */
	public static boolean USE_MAGIC = true;
	static BitboardAttacks instance;

	public static BitboardAttacks getInstance() {
		if (instance == null) {
			if (USE_MAGIC) {
				instance = new BitboardAttacksMagic();
			} else {
				//instance = new BitboardAttacks();
			}
		}
		return instance;
	}

	long squareAttackedAux(long square, int shift, long border) {
		if ((square & border) == 0) {
			if (shift > 0) {
				square <<= shift;
			} else {
				square >>>= -shift;
			}
			return square;
		}
		return 0;
	}

	long squareAttackedAuxSlider(long square, int shift, long border) {
		long ret = 0;
		while ((square & border) == 0) {
			if (shift > 0) {
				square <<= shift;
			} else {
				square >>>= -shift;
			}
			ret |= square;
		}
		return ret;
	}

	BitboardAttacks() {
		long time1 = System.currentTimeMillis();
		rook = new long[64];
		bishop = new long[64];
		knight = new long[64];
		king = new long[64];
		pawn = new long[2][64];

		long square = 1;
		byte i = 0;
		while (square != 0) {
			rook[i] = squareAttackedAuxSlider(square, +8, BitboardUtils.b_u) //
					| squareAttackedAuxSlider(square, -8, BitboardUtils.b_d) //
					| squareAttackedAuxSlider(square, -1, BitboardUtils.b_r) //
					| squareAttackedAuxSlider(square, +1, BitboardUtils.b_l);

			bishop[i] = squareAttackedAuxSlider(square, +9, BitboardUtils.b_u | BitboardUtils.b_l) //
					| squareAttackedAuxSlider(square, +7, BitboardUtils.b_u | BitboardUtils.b_r) //
					| squareAttackedAuxSlider(square, -7, BitboardUtils.b_d | BitboardUtils.b_l) //
					| squareAttackedAuxSlider(square, -9, BitboardUtils.b_d | BitboardUtils.b_r);

			knight[i] = squareAttackedAux(square, +17, BitboardUtils.b2_u | BitboardUtils.b_l) //
					| squareAttackedAux(square, +15, BitboardUtils.b2_u | BitboardUtils.b_r) //
					| squareAttackedAux(square, -15, BitboardUtils.b2_d | BitboardUtils.b_l) //
					| squareAttackedAux(square, -17, BitboardUtils.b2_d | BitboardUtils.b_r) //
					| squareAttackedAux(square, +10, BitboardUtils.b_u | BitboardUtils.b2_l) //
					| squareAttackedAux(square, +6, BitboardUtils.b_u | BitboardUtils.b2_r) //
					| squareAttackedAux(square, -6, BitboardUtils.b_d | BitboardUtils.b2_l) //
					| squareAttackedAux(square, -10, BitboardUtils.b_d | BitboardUtils.b2_r);

			pawn[Color.W][i] = squareAttackedAux(square, 7, BitboardUtils.b_u | BitboardUtils.b_r) //
					| squareAttackedAux(square, 9, BitboardUtils.b_u | BitboardUtils.b_l);

			pawn[Color.B][i] = squareAttackedAux(square, -7, BitboardUtils.b_d | BitboardUtils.b_l) //
					| squareAttackedAux(square, -9, BitboardUtils.b_d | BitboardUtils.b_r);

			king[i] = squareAttackedAux(square, +8, BitboardUtils.b_u) //
					| squareAttackedAux(square, -8, BitboardUtils.b_d) //
					| squareAttackedAux(square, -1, BitboardUtils.b_r) //
					| squareAttackedAux(square, +1, BitboardUtils.b_l) //
					| squareAttackedAux(square, +9, BitboardUtils.b_u | BitboardUtils.b_l) //
					| squareAttackedAux(square, +7, BitboardUtils.b_u | BitboardUtils.b_r) //
					| squareAttackedAux(square, -7, BitboardUtils.b_d | BitboardUtils.b_l) //
					| squareAttackedAux(square, -9, BitboardUtils.b_d | BitboardUtils.b_r);

			square <<= 1;
			i++;
		}
		long time2 = System.currentTimeMillis();
	}


	/**
	 * without magic bitboards, too expensive, but uses less memory
	 */
	public long getRookAttacks(int index, long all) {
		return getRookShiftAttacks(BitboardUtils.index2Square(index), all);
	}

	public long getBishopAttacks(int index, long all) {
		return getBishopShiftAttacks(BitboardUtils.index2Square(index), all);
	}

	public long getRookShiftAttacks(long square, long all) {
		return checkSquareAttackedAux(square, all, +8, BitboardUtils.b_u) | checkSquareAttackedAux(square, all, -8, BitboardUtils.b_d)
				| checkSquareAttackedAux(square, all, -1, BitboardUtils.b_r) | checkSquareAttackedAux(square, all, +1, BitboardUtils.b_l);
	}

	public long getBishopShiftAttacks(long square, long all) {
		return checkSquareAttackedAux(square, all, +9, BitboardUtils.b_u | BitboardUtils.b_l)
				| checkSquareAttackedAux(square, all, +7, BitboardUtils.b_u | BitboardUtils.b_r)
				| checkSquareAttackedAux(square, all, -7, BitboardUtils.b_d | BitboardUtils.b_l)
				| checkSquareAttackedAux(square, all, -9, BitboardUtils.b_d | BitboardUtils.b_r);
	}

	/**
	 * Attacks for sliding pieces
	 */
	private long checkSquareAttackedAux(long square, long all, int shift, long border) {
		long ret = 0;
		while ((square & border) == 0) {
			if (shift > 0) {
				square <<= shift;
			} else {
				square >>>= -shift;
			}
			ret |= square;
			// If we collide with other piece
			if ((square & all) != 0) {
				break;
			}
		}
		return ret;
	}
}
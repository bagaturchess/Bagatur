package bagaturchess.engines.evaladapters.carbalo;


public class BitboardAttacksMagic extends BitboardAttacks {

	public static final byte[] rookShiftBits = { //
			12, 11, 11, 11, 11, 11, 11, 12, //
			11, 10, 10, 10, 10, 10, 10, 11, //
			11, 10, 10, 10, 10, 10, 10, 11, //
			11, 10, 10, 10, 10, 10, 10, 11, //
			11, 10, 10, 10, 10, 10, 10, 11, //
			11, 10, 10, 10, 10, 10, 10, 11, //
			11, 10, 10, 10, 10, 10, 10, 11, //
			12, 11, 11, 11, 11, 11, 11, 12 //
	};

	public static final byte[] bishopShiftBits = { //
			6, 5, 5, 5, 5, 5, 5, 6, //
			5, 5, 5, 5, 5, 5, 5, 5, //
			5, 5, 7, 7, 7, 7, 5, 5, //
			5, 5, 7, 9, 9, 7, 5, 5, //
			5, 5, 7, 9, 9, 7, 5, 5, //
			5, 5, 7, 7, 7, 7, 5, 5, //
			5, 5, 5, 5, 5, 5, 5, 5, //
			6, 5, 5, 5, 5, 5, 5, 6 //
	};

	// Magic numbers generated with MagicNumbersGen
	public static final long[] rookMagicNumber = {0x1080108000400020L, 0x40200010004000L, 0x100082000441100L, 0x480041000080080L, 0x100080005000210L,
			0x100020801000400L, 0x280010000800200L, 0x100008020420100L, 0x400800080400020L, 0x401000402000L, 0x100801000200080L, 0x801000800800L,
			0x800400080080L, 0x800200800400L, 0x1000200040100L, 0x4840800041000080L, 0x20008080004000L, 0x404010002000L, 0x808010002000L, 0x828010000800L,
			0x808004000800L, 0x14008002000480L, 0x40002100801L, 0x20001004084L, 0x802080004000L, 0x200080400080L, 0x810001080200080L, 0x10008080080010L,
			0x4000080080040080L, 0x40080020080L, 0x1000100040200L, 0x80008200004124L, 0x804000800020L, 0x804000802000L, 0x801000802000L, 0x2000801000800804L,
			0x80080800400L, 0x80040080800200L, 0x800100800200L, 0x8042000104L, 0x208040008008L, 0x10500020004000L, 0x100020008080L, 0x2000100008008080L,
			0x200040008008080L, 0x8020004008080L, 0x1000200010004L, 0x100040080420001L, 0x80004000200040L, 0x200040100140L, 0x20004800100040L, 0x100080080280L,
			0x8100800400080080L, 0x8004020080040080L, 0x9001000402000100L, 0x40080410200L, 0x208040110202L, 0x800810022004012L, 0x1000820004011L,
			0x1002004100009L, 0x41001002480005L, 0x81000208040001L, 0x4000008201100804L, 0x2841008402L};
	public static final long[] bishopMagicNumber = {0x1020041000484080L, 0x20204010a0000L, 0x8020420240000L, 0x404040085006400L, 0x804242000000108L,
			0x8901008800000L, 0x1010110400080L, 0x402401084004L, 0x1000200810208082L, 0x20802208200L, 0x4200100102082000L, 0x1024081040020L, 0x20210000000L,
			0x8210400100L, 0x10110022000L, 0x80090088010820L, 0x8001002480800L, 0x8102082008200L, 0x41001000408100L, 0x88000082004000L, 0x204000200940000L,
			0x410201100100L, 0x2000101012000L, 0x40201008200c200L, 0x10100004204200L, 0x2080020010440L, 0x480004002400L, 0x2008008008202L, 0x1010080104000L,
			0x1020001004106L, 0x1040200520800L, 0x8410000840101L, 0x1201000200400L, 0x2029000021000L, 0x4002400080840L, 0x5000020080080080L, 0x1080200002200L,
			0x4008202028800L, 0x2080210010080L, 0x800809200008200L, 0x1082004001000L, 0x1080202411080L, 0x840048010101L, 0x40004010400200L, 0x500811020800400L,
			0x20200040800040L, 0x1008012800830a00L, 0x1041102001040L, 0x11010120200000L, 0x2020222020c00L, 0x400002402080800L, 0x20880000L, 0x1122020400L,
			0x11100248084000L, 0x210111000908000L, 0x2048102020080L, 0x1000108208024000L, 0x1004100882000L, 0x41044100L, 0x840400L, 0x4208204L,
			0x80000200282020cL, 0x8a001240100L, 0x2040104040080L};

	// Mask = Attacks without border for magic bitboards
	public long[] rookMask;
	public long[][] rookMagic;
	public long[] bishopMask;
	public long[][] bishopMagic;

	public BitboardAttacksMagic() {
		super();
		long time1 = System.currentTimeMillis();
		rookMask = new long[64];
		rookMagic = new long[64][];
		bishopMask = new long[64];
		bishopMagic = new long[64][];

		long square = 1;
		byte i = 0;
		while (square != 0) {

			rookMask[i] = squareAttackedAuxSliderMask(square, +8, BitboardUtils.b_u) //
					| squareAttackedAuxSliderMask(square, -8, BitboardUtils.b_d) //
					| squareAttackedAuxSliderMask(square, -1, BitboardUtils.b_r) //
					| squareAttackedAuxSliderMask(square, +1, BitboardUtils.b_l);

			bishopMask[i] = squareAttackedAuxSliderMask(square, +9, BitboardUtils.b_u | BitboardUtils.b_l) //
					| squareAttackedAuxSliderMask(square, +7, BitboardUtils.b_u | BitboardUtils.b_r) //
					| squareAttackedAuxSliderMask(square, -7, BitboardUtils.b_d | BitboardUtils.b_l) //
					| squareAttackedAuxSliderMask(square, -9, BitboardUtils.b_d | BitboardUtils.b_r);

			// And now generate magics
			int rookPositions = (1 << rookShiftBits[i]);
			rookMagic[i] = new long[rookPositions];
			for (int j = 0; j < rookPositions; j++) {
				long pieces = generatePieces(j, rookShiftBits[i], rookMask[i]);
				int magicIndex = magicTransform(pieces, rookMagicNumber[i], rookShiftBits[i]);
				rookMagic[i][magicIndex] = getRookShiftAttacks(square, pieces);
			}

			int bishopPositions = (1 << bishopShiftBits[i]);
			bishopMagic[i] = new long[bishopPositions];
			for (int j = 0; j < bishopPositions; j++) {
				long pieces = generatePieces(j, bishopShiftBits[i], bishopMask[i]);
				int magicIndex = magicTransform(pieces, bishopMagicNumber[i], bishopShiftBits[i]);
				bishopMagic[i][magicIndex] = getBishopShiftAttacks(square, pieces);
			}
			square <<= 1;
			i++;
		}
		long time2 = System.currentTimeMillis();
	}

	/**
	 * Fills pieces from a mask. Necessary for magic generation variable bits is
	 * the mask bytes number index goes from 0 to 2^bits
	 */
	private long generatePieces(int index, int bits, long mask) {
		int i;
		long lsb;
		long result = 0L;
		for (i = 0; i < bits; i++) {
			lsb = mask & (-mask);
			mask ^= lsb; // Deactivates lsb bit of the mask to get next bit next time
			if ((index & (1 << i)) != 0)
				result |= lsb; // if bit is set to 1
		}
		return result;
	}

	private long squareAttackedAuxSliderMask(long square, int shift, long border) {
		long ret = 0;
		while ((square & border) == 0) {
			if (shift > 0) {
				square <<= shift;
			} else {
				square >>>= -shift;
			}
			if ((square & border) == 0) {
				ret |= square;
			}
		}
		return ret;
	}

	public static int magicTransform(long b, long magic, byte bits) {
		return (int) ((b * magic) >>> (64 - bits));
	}

	/**
	 * Magic! attacks, very fast method
	 */
	@Override
	public long getRookAttacks(int index, long all) {
		int i = magicTransform(all & rookMask[index], rookMagicNumber[index], rookShiftBits[index]);
		return rookMagic[index][i];
	}

	@Override
	public long getBishopAttacks(int index, long all) {
		int i = magicTransform(all & bishopMask[index], bishopMagicNumber[index], bishopShiftBits[index]);
		return bishopMagic[index][i];
	}
}

/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *
 *  Impl3 compatibility wrapper.
 */
package bagaturchess.bitboard.impl3.internal;

/**
 * Impl3 keeps its own package namespace while remaining assignment-compatible
 * with IBoard, whose getCastlingConfig() return type is the original
 * bagaturchess.bitboard.impl1.internal.CastlingConfig.
 */
public class CastlingConfig extends bagaturchess.bitboard.impl1.internal.CastlingConfig {

	public static final int A1 = 7;
	public static final int C1 = 5;
	public static final int D1 = 4;
	public static final int E1 = 3;
	public static final int F1 = 2;
	public static final int G1 = 1;
	public static final int H1 = 0;

	public static final int A8 = 63;
	public static final int C8 = 61;
	public static final int D8 = 60;
	public static final int E8 = 59;
	public static final int F8 = 58;
	public static final int G8 = 57;
	public static final int H8 = 56;

	public static final CastlingConfig CLASSIC_CHESS = new CastlingConfig(E1, H1, A1, E8, H8, A8);

	public CastlingConfig(int from_SquareID_king_w,
			int from_SquareID_rook_kingside_w,
			int from_SquareID_rook_queenside_w,
			int from_SquareID_king_b,
			int from_SquareID_rook_kingside_b,
			int from_SquareID_rook_queenside_b) {

		super(from_SquareID_king_w,
				from_SquareID_rook_kingside_w,
				from_SquareID_rook_queenside_w,
				from_SquareID_king_b,
				from_SquareID_rook_kingside_b,
				from_SquareID_rook_queenside_b);
	}
}

package bagaturchess.engines.carbaloadapter;


/**
 * Holds all the possible attacks for a board.
 * It is used by the evaluators and the move iterator, and also to speed the SEE calculations detecting not attacked squares.
 * Calculates the checking pieces and the interpose squares to avoid checks.
 */
public class AttacksInfo {
	public static final int W = 0;
	public static final int B = 1;

	BitboardAttacks bbAttacks;
	
	// Includes attacks by pinned pieces that cannot move to the square, but limit king mobility
	public long attackedSquaresAlsoPinned[] = {0, 0};
	// The other attacks do not include those from pinned pieces
	public long attackedSquares[] = {0, 0};
	public long attacksFromSquare[] = new long[64];
	public long pawnAttacks[] = {0, 0};
	public long knightAttacks[] = {0, 0};
	public long bishopAttacks[] = {0, 0};
	public long rookAttacks[] = {0, 0};
	public long queenAttacks[] = {0, 0};
	public long kingAttacks[] = {0, 0};
	public int kingIndex[] = {0, 0};
	public long pinnedMobility[] = new long[64];
	//
	// Squares with possible ray attacks to the kings: used to detect check and move legality
	//
	public long bishopAttacksKing[] = {0, 0};
	public long rookAttacksKing[] = {0, 0};

	public long mayPin[] = {0, 0}; // both my pieces than can discover an attack and the opponent pieces pinned, that is any piece attacked by a slider
	public long piecesGivingCheck;
	public long interposeCheckSquares;
	public long pinnedPieces;

	public AttacksInfo() {
		this.bbAttacks = BitboardAttacks.getInstance();
	}

	/**
	 * Checks for a pinned piece in each ray
	 */
	private void checkPinnerRay(long ray, long mines, long attackerSlider) {
		long pinner = ray & attackerSlider;
		if (pinner != 0) {
			long pinned = ray & mines;
			pinnedPieces |= pinned;
			pinnedMobility[BitboardUtils.square2Index(pinned)] = ray;
		}
	}

	private void checkPinnerBishop(int kingIndex, long bishopSliderAttacks, long all, long mines, long otherBishopsOrQueens) {
		if ((bishopSliderAttacks & mines) == 0 || (bbAttacks.bishop[kingIndex] & otherBishopsOrQueens) == 0) {
			return;
		}
		long xray = bbAttacks.getBishopAttacks(kingIndex, all & ~(mines & bishopSliderAttacks));
		if ((xray & ~bishopSliderAttacks & otherBishopsOrQueens) != 0) {
			int rank = kingIndex >> 3;
			int file = 7 - kingIndex & 7;

			checkPinnerRay(xray & BitboardUtils.RANKS_UPWARDS[rank] & BitboardUtils.FILES_LEFT[file], mines, otherBishopsOrQueens);
			checkPinnerRay(xray & BitboardUtils.RANKS_UPWARDS[rank] & BitboardUtils.FILES_RIGHT[file], mines, otherBishopsOrQueens);
			checkPinnerRay(xray & BitboardUtils.RANKS_DOWNWARDS[rank] & BitboardUtils.FILES_LEFT[file], mines, otherBishopsOrQueens);
			checkPinnerRay(xray & BitboardUtils.RANKS_DOWNWARDS[rank] & BitboardUtils.FILES_RIGHT[file], mines, otherBishopsOrQueens);
		}
	}

	private void checkPinnerRook(int kingIndex, long rookSliderAttacks, long all, long mines, long otherRooksOrQueens) {
		if ((rookSliderAttacks & mines) == 0 || (bbAttacks.rook[kingIndex] & otherRooksOrQueens) == 0) {
			return;
		}
		long xray = bbAttacks.getRookAttacks(kingIndex, all & ~(mines & rookSliderAttacks));
		if ((xray & ~rookSliderAttacks & otherRooksOrQueens) != 0) {
			int rank = kingIndex >> 3;
			int file = 7 - kingIndex & 7;

			checkPinnerRay(xray & BitboardUtils.RANKS_UPWARDS[rank], mines, otherRooksOrQueens);
			checkPinnerRay(xray & BitboardUtils.FILES_LEFT[file], mines, otherRooksOrQueens);
			checkPinnerRay(xray & BitboardUtils.RANKS_DOWNWARDS[rank], mines, otherRooksOrQueens);
			checkPinnerRay(xray & BitboardUtils.FILES_RIGHT[file], mines, otherRooksOrQueens);
		}
	}
	
	public void build(IBoard board) {
		long all = board.getAll();
		long mines = board.getColourToMove() == 0 ? board.getWhites() : board.getBlacks();
		long myKing = board.getKings() & mines;
		int us = board.getColourToMove() == 0 ? 0 : 1;
		
		attackedSquaresAlsoPinned[W] = 0;
		attackedSquaresAlsoPinned[B] = 0;
		pawnAttacks[W] = 0;
		pawnAttacks[B] = 0;
		knightAttacks[W] = 0;
		knightAttacks[B] = 0;
		bishopAttacks[W] = 0;
		bishopAttacks[B] = 0;
		rookAttacks[W] = 0;
		rookAttacks[B] = 0;
		queenAttacks[W] = 0;
		queenAttacks[B] = 0;
		kingAttacks[W] = 0;
		kingAttacks[B] = 0;
		mayPin[W] = 0;
		mayPin[B] = 0;
		pinnedPieces = 0;
		piecesGivingCheck = 0;
		interposeCheckSquares = 0;

		kingIndex[W] = BitboardUtils.square2Index(board.getKings() & board.getWhites());
		kingIndex[B] = BitboardUtils.square2Index(board.getKings() & board.getBlacks());

		bishopAttacksKing[W] = bbAttacks.getBishopAttacks(kingIndex[W], all);
		checkPinnerBishop(kingIndex[W], bishopAttacksKing[W], all, board.getWhites(), (board.getBishops() | board.getQueens()) & board.getBlacks());
		bishopAttacksKing[B] = bbAttacks.getBishopAttacks(kingIndex[B], all);
		checkPinnerBishop(kingIndex[B], bishopAttacksKing[B], all, board.getBlacks(), (board.getBishops() | board.getQueens()) & board.getWhites());

		rookAttacksKing[W] = bbAttacks.getRookAttacks(kingIndex[W], all);
		checkPinnerRook(kingIndex[W], rookAttacksKing[W], all, board.getWhites(), (board.getRooks() | board.getQueens()) & board.getBlacks());
		rookAttacksKing[B] = bbAttacks.getRookAttacks(kingIndex[B], all);
		checkPinnerRook(kingIndex[B], rookAttacksKing[B], all, board.getBlacks(), (board.getRooks() | board.getQueens()) & board.getWhites());

		long pieceAttacks;
		int index;
		long square = 1;
		for (index = 0; index < 64; index++) {
			if ((square & all) != 0) {
				int color = (board.getWhites() & square) != 0 ? W : B;
				long pinnedSquares = (square & pinnedPieces) != 0 ? pinnedMobility[index] : Square.ALL;

				pieceAttacks = 0;
				if ((square & board.getPawns()) != 0) {
					pieceAttacks = bbAttacks.pawn[color][index];
					if ((square & mines) == 0 && (pieceAttacks & myKing) != 0) {
						piecesGivingCheck |= square;
					}
					pawnAttacks[color] |= pieceAttacks & pinnedSquares;

				} else if ((square & board.getKnights()) != 0) {
					pieceAttacks = bbAttacks.knight[index];
					if ((square & mines) == 0 && (pieceAttacks & myKing) != 0) {
						piecesGivingCheck |= square;
					}
					knightAttacks[color] |= pieceAttacks & pinnedSquares;

				} else if ((square & board.getBishops()) != 0) {
					pieceAttacks = bbAttacks.getBishopAttacks(index, all);
					if ((square & mines) == 0 && (pieceAttacks & myKing) != 0) {
						piecesGivingCheck |= square;
						interposeCheckSquares |= pieceAttacks & bishopAttacksKing[us]; // And with only the diagonal attacks to the king
					}
					bishopAttacks[color] |= pieceAttacks & pinnedSquares;
					mayPin[color] |= all & pieceAttacks;

				} else if ((square & board.getRooks()) != 0) {
					pieceAttacks = bbAttacks.getRookAttacks(index, all);
					if ((square & mines) == 0 && (pieceAttacks & myKing) != 0) {
						piecesGivingCheck |= square;
						interposeCheckSquares |= pieceAttacks & rookAttacksKing[us]; // And with only the rook attacks to the king
					}
					rookAttacks[color] |= pieceAttacks & pinnedSquares;
					mayPin[color] |= all & pieceAttacks;

				} else if ((square & board.getQueens()) != 0) {
					long bishopSliderAttacks = bbAttacks.getBishopAttacks(index, all);
					if ((square & mines) == 0 && (bishopSliderAttacks & myKing) != 0) {
						piecesGivingCheck |= square;
						interposeCheckSquares |= bishopSliderAttacks & bishopAttacksKing[us]; // And with only the diagonal attacks to the king
					}
					long rookSliderAttacks = bbAttacks.getRookAttacks(index, all);
					if ((square & mines) == 0 && (rookSliderAttacks & myKing) != 0) {
						piecesGivingCheck |= square;
						interposeCheckSquares |= rookSliderAttacks & rookAttacksKing[us]; // And with only the rook attacks to the king
					}
					pieceAttacks = rookSliderAttacks | bishopSliderAttacks;
					queenAttacks[color] |= pieceAttacks & pinnedSquares;
					mayPin[color] |= all & pieceAttacks;

				} else if ((square & board.getKings()) != 0) {
					pieceAttacks = bbAttacks.king[index];
					kingAttacks[color] |= pieceAttacks;
				}

				attackedSquaresAlsoPinned[color] |= pieceAttacks;
				attacksFromSquare[index] = pieceAttacks & pinnedSquares;
			} else {
				attacksFromSquare[index] = 0;
			}
			square <<= 1;
		}
		attackedSquares[W] = pawnAttacks[W] | knightAttacks[W] | bishopAttacks[W] | rookAttacks[W] | queenAttacks[W] | kingAttacks[W];
		attackedSquares[B] = pawnAttacks[B] | knightAttacks[B] | bishopAttacks[B] | rookAttacks[B] | queenAttacks[B] | kingAttacks[B];
	}
}
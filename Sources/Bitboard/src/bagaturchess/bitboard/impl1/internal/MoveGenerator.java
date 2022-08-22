package bagaturchess.bitboard.impl1.internal;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;

import java.util.Arrays;
import java.util.Random;

import bagaturchess.bitboard.common.Properties;
import bagaturchess.bitboard.impl.utils.VarStatistic;


public final class MoveGenerator {
	
	
	public static boolean USE_ContinuationHistory 	= true;
	
	
	private final int[] moves = new int[3000];
	private final int[] moveScores = new int[3000];
	private final int[] nextToGenerate = new int[EngineConstants.MAX_PLIES * 2];
	private final int[] nextToMove = new int[EngineConstants.MAX_PLIES * 2];
	private int currentPly;

	private final int[][][] COUNTER_MOVES = new int[2][7][64];

	private final int[] KILLER_MOVE_1 = new int[EngineConstants.MAX_PLIES * 2];
	private final int[] KILLER_MOVE_2 = new int[EngineConstants.MAX_PLIES * 2];

	private final int[][] HH_MOVES = new int[2][64 * 64];
	private final int[][] BF_MOVES = new int[2][64 * 64];
	
	private final int[][][] HH_MOVES1 = new int[2][7][64];
	private final int[][][] BF_MOVES1 = new int[2][7][64];
	
	private final ContinuationHistory[] HH_ContinuationHistory = new ContinuationHistory[2];
	private final ContinuationHistory[] BF_ContinuationHistory = new ContinuationHistory[2];
	
	
	private static final int LMR_STAT_MULTIPLIER 				= 1000;
	private static final double LMR_DEVIATION_MULTIPLIER 		= 1.5; //1.75 //1.5 //0 //1.25 //2.5 //1
	private final long[][] LMR_ALL 								= new long[2][64 * 64];
	private final long[][] LMR_BELOW_ALPHA 						= new long[2][64 * 64];
	private final long[][] LMR_ABOVE_ALPHA 						= new long[2][64 * 64];
	private VarStatistic[] lmrBelowAlphaAVGScores 				= new VarStatistic[2];
	private VarStatistic[] lmrAboveAlphaAVGScores 				= new VarStatistic[2];
	
	private static final boolean BUILD_EXACT_STATS 				= false;
	private static final double LMR_RATE_THREASHOLD_ABOVE_ALPHA = 0.95; //Top X%
	private static final double LMR_RATE_THREASHOLD_BELOW_ALPHA = 0.0001; //Top Y%
	private static final long[][] LMR_STATS_COUNTER_ABOVE_ALPHA = new long[2][LMR_STAT_MULTIPLIER + 1];
	private static final long[][] LMR_STATS_COUNTER_BELOW_ALPHA = new long[2][LMR_STAT_MULTIPLIER + 1];
	private static int[] lmr_rate_all_above_alpha 				= new int[2];
	private static int[] lmr_rate_pointer_above_alpha 			= new int[2];
	private static int[] lmr_rate_all_below_alpha 				= new int[2];
	private static int[] lmr_rate_pointer_below_alpha 			= new int[2];
	
	
	private Random randomizer = new Random();
	private long randomizer_counter;
	

	public MoveGenerator() {
		
		lmrBelowAlphaAVGScores[0] = new VarStatistic();
		lmrBelowAlphaAVGScores[1] = new VarStatistic();
		
		lmrAboveAlphaAVGScores[0] = new VarStatistic();
		lmrAboveAlphaAVGScores[1] = new VarStatistic();
		
		clearHistoryHeuristics();
		
		if (USE_ContinuationHistory) {
			
			HH_ContinuationHistory[WHITE] = new ContinuationHistory();
			HH_ContinuationHistory[BLACK] = new ContinuationHistory();
			
			BF_ContinuationHistory[WHITE] = new ContinuationHistory();
			BF_ContinuationHistory[BLACK] = new ContinuationHistory();
		}
	}

	public void clearHistoryHeuristics() {
		
		Arrays.fill(HH_MOVES[WHITE], 0);
		Arrays.fill(HH_MOVES[BLACK], 0);
		Arrays.fill(BF_MOVES[WHITE], 1);
		Arrays.fill(BF_MOVES[BLACK], 1);
		Arrays.fill(LMR_ALL[WHITE], 0);
		Arrays.fill(LMR_ALL[BLACK], 0);
		Arrays.fill(LMR_BELOW_ALPHA[WHITE], 0);
		Arrays.fill(LMR_BELOW_ALPHA[BLACK], 0);
		Arrays.fill(LMR_ABOVE_ALPHA[WHITE], 0);
		Arrays.fill(LMR_ABOVE_ALPHA[BLACK], 0);
		Arrays.fill(LMR_STATS_COUNTER_ABOVE_ALPHA[WHITE], 0);
		Arrays.fill(LMR_STATS_COUNTER_ABOVE_ALPHA[BLACK], 0);
		Arrays.fill(LMR_STATS_COUNTER_BELOW_ALPHA[WHITE], 0);
		Arrays.fill(LMR_STATS_COUNTER_BELOW_ALPHA[BLACK], 0);
		Arrays.fill(lmr_rate_all_above_alpha, 0);
		Arrays.fill(lmr_rate_pointer_above_alpha, 0);
		Arrays.fill(lmr_rate_all_below_alpha, 0);
		Arrays.fill(lmr_rate_pointer_below_alpha, 0);
		lmrBelowAlphaAVGScores[0].clear();
		lmrBelowAlphaAVGScores[1].clear();
		lmrAboveAlphaAVGScores[0].clear();
		lmrAboveAlphaAVGScores[1].clear();
		
		Arrays.fill(HH_MOVES1[WHITE][0], 0);
		Arrays.fill(HH_MOVES1[WHITE][PAWN], 0);
		Arrays.fill(HH_MOVES1[WHITE][NIGHT], 0);
		Arrays.fill(HH_MOVES1[WHITE][BISHOP], 0);
		Arrays.fill(HH_MOVES1[WHITE][ROOK], 0);
		Arrays.fill(HH_MOVES1[WHITE][QUEEN], 0);
		Arrays.fill(HH_MOVES1[WHITE][KING], 0);
		
		Arrays.fill(HH_MOVES1[BLACK][0], 0);
		Arrays.fill(HH_MOVES1[BLACK][PAWN], 0);
		Arrays.fill(HH_MOVES1[BLACK][NIGHT], 0);
		Arrays.fill(HH_MOVES1[BLACK][BISHOP], 0);
		Arrays.fill(HH_MOVES1[BLACK][ROOK], 0);
		Arrays.fill(HH_MOVES1[BLACK][QUEEN], 0);
		Arrays.fill(HH_MOVES1[BLACK][KING], 0);
		
		Arrays.fill(BF_MOVES1[WHITE][0], 1);
		Arrays.fill(BF_MOVES1[WHITE][PAWN], 1);
		Arrays.fill(BF_MOVES1[WHITE][NIGHT], 1);
		Arrays.fill(BF_MOVES1[WHITE][BISHOP], 1);
		Arrays.fill(BF_MOVES1[WHITE][ROOK], 1);
		Arrays.fill(BF_MOVES1[WHITE][QUEEN], 1);
		Arrays.fill(BF_MOVES1[WHITE][KING], 1);
		
		Arrays.fill(BF_MOVES1[BLACK][0], 1);
		Arrays.fill(BF_MOVES1[BLACK][PAWN], 1);
		Arrays.fill(BF_MOVES1[BLACK][NIGHT], 1);
		Arrays.fill(BF_MOVES1[BLACK][BISHOP], 1);
		Arrays.fill(BF_MOVES1[BLACK][ROOK], 1);
		Arrays.fill(BF_MOVES1[BLACK][QUEEN], 1);
		Arrays.fill(BF_MOVES1[BLACK][KING], 1);	
		
		currentPly = 0;
	}
	
	
	public void addHHValue(final int color, final int move, final int parentMove, final int depth) {
		HH_MOVES[color][MoveUtil.getFromToIndex(move)] += depth * depth;
		HH_MOVES1[color][MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
		if (USE_ContinuationHistory) HH_ContinuationHistory[color == WHITE ? BLACK : WHITE].array[MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].array[MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
	}
	
	
	public void addBFValue(final int color, final int move, final int parentMove, final int depth) {
		BF_MOVES[color][MoveUtil.getFromToIndex(move)] += depth * depth;
		BF_MOVES1[color][MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
		if (USE_ContinuationHistory) BF_ContinuationHistory[color == WHITE ? BLACK : WHITE].array[MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].array[MoveUtil.getSourcePieceIndex(move)][MoveUtil.getToIndex(move)] += depth * depth;
	}
	
	
	public int getHHScore(final int color, final int fromToIndex, final int pieceType, final int toIndex, final int parentMove) {
		int value1 = 100 * HH_MOVES[color][fromToIndex] / BF_MOVES[color][fromToIndex];
		int value2 = 100 * HH_MOVES1[color][pieceType][toIndex] / BF_MOVES1[color][pieceType][toIndex];
		int value3 = USE_ContinuationHistory ? getContinuationHistoryScore(color, pieceType, toIndex, parentMove) : 0;
		
		if (USE_ContinuationHistory) {
			
			return value3;
			
		} else {
			
			return Math.max(value1, Math.max(value2, value3));
		}
		
		//return (value1 + value2 + value3) / 3;
	}
	
	
	private int getContinuationHistoryScore(final int color, final int pieceType, final int toIndex, final int parentMove) {
		return 100 * HH_ContinuationHistory[color == WHITE ? BLACK : WHITE].array[MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].array[pieceType][toIndex] / 
				BF_ContinuationHistory[color == WHITE ? BLACK : WHITE].array[MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)].array[pieceType][toIndex];
	}
	
	
	public void addLMR_All(final int color, final int move, final int depth) {
		
		LMR_ALL[color][MoveUtil.getFromToIndex(move)] += depth * depth;
	}
	
	
	public void addLMR_AboveAlpha(final int color, final int move, final int depth) {
		
		int fromToIndex = MoveUtil.getFromToIndex(move);
		
		LMR_ABOVE_ALPHA[color][fromToIndex] += depth * depth;
		
		int rate = getLMR_Rate_internal(color, fromToIndex);
		
		if (BUILD_EXACT_STATS) {
			
			//System.out.println("addLMR_AboveAlpha: COLOR " + color + ", LMR_RATE_THREASHOLD_ABOVE_ALPHA=" + LMR_RATE_THREASHOLD_ABOVE_ALPHA + " POINTER=" + getLMR_ThreasholdPointer_AboveAlpha(color));
			
			LMR_STATS_COUNTER_ABOVE_ALPHA[color][rate]++;
			lmr_rate_all_above_alpha[color]++;
			
			int sum = 0;
			int pointer = LMR_STAT_MULTIPLIER - 1;
			while (pointer > 0 && sum / (double) lmr_rate_all_above_alpha[color] < LMR_RATE_THREASHOLD_ABOVE_ALPHA) {
				sum += LMR_STATS_COUNTER_ABOVE_ALPHA[color][pointer];
				pointer--;
			}
			
			lmr_rate_pointer_above_alpha[color] = pointer;
		}
		
		lmrAboveAlphaAVGScores[color].addValue(rate);
	}
	
	
	public void addLMR_BelowAlpha(final int color, final int move, final int depth) {
		
		int fromToIndex = MoveUtil.getFromToIndex(move);
		
		LMR_BELOW_ALPHA[color][fromToIndex] += depth * depth;

		int rate = getLMR_Rate_internal(color, fromToIndex);
		
		if (BUILD_EXACT_STATS) {
			
			//System.out.println("addLMR_BelowAlpha: COLOR " + color + ", LMR_RATE_THREASHOLD_BELOW_ALPHA=" + LMR_RATE_THREASHOLD_BELOW_ALPHA + " POINTER=" + getLMR_ThreasholdPointer_BelowAlpha(color));
			
			LMR_STATS_COUNTER_BELOW_ALPHA[color][rate]++;
			lmr_rate_all_below_alpha[color]++;
			
			int sum = 0;
			int pointer = LMR_STAT_MULTIPLIER - 1;
			while (pointer > 0 && sum / (double) lmr_rate_all_below_alpha[color] < LMR_RATE_THREASHOLD_BELOW_ALPHA) {
				sum += LMR_STATS_COUNTER_BELOW_ALPHA[color][pointer];
				pointer--;
			}
			
			lmr_rate_pointer_below_alpha[color] = pointer;
		}
		
		
		lmrBelowAlphaAVGScores[color].addValue(rate);
	}
	
	
	public int getLMR_Rate(final int color, final int move) {
		
		int fromToIndex = MoveUtil.getFromToIndex(move);
		
		if (LMR_ALL[color][fromToIndex] == 0) {
			
			return 0;
		}
		
		return getLMR_Rate_internal(color, fromToIndex);
	}
	
	
	private int getLMR_Rate_internal(final int color, final int fromToIndex) {
		
		//return (int) (LMR_STAT_MULTIPLIER * (LMR_ABOVE_ALPHA[color][fromToIndex]) / LMR_ALL[color][fromToIndex]);
		
		return (int) (LMR_STAT_MULTIPLIER * (LMR_ALL[color][fromToIndex] - LMR_BELOW_ALPHA[color][fromToIndex]) / LMR_ALL[color][fromToIndex]);
	}
	
	
	public int getLMR_ThreasholdPointer_AboveAlpha(int color) {
		
		if (BUILD_EXACT_STATS) {
			
			return lmr_rate_pointer_above_alpha[color];
			
		} else {
		
			int pointer_above_alpha = (int) (lmrAboveAlphaAVGScores[color].getEntropy() + LMR_DEVIATION_MULTIPLIER * lmrAboveAlphaAVGScores[color].getDisperse());
	
			//System.out.println("AboveAlpha: color=" + color + ", Entropy=" + lmrAboveAlphaAVGScores[color].getEntropy() + ", Disperse=" + lmrAboveAlphaAVGScores[color].getDisperse());
			
			return pointer_above_alpha;
		}
	}
	
	
	public int getLMR_ThreasholdPointer_BelowAlpha(int color) {
		
		if (BUILD_EXACT_STATS) {
			
			return lmr_rate_pointer_below_alpha[color];
			
		} else {
		
			int pointer_below_alpha = (int) (lmrBelowAlphaAVGScores[color].getEntropy() + LMR_DEVIATION_MULTIPLIER * lmrBelowAlphaAVGScores[color].getDisperse());
			
			//System.out.println("BelowAlpha: color=" + color + ", Entropy=" + lmrBelowAlphaAVGScores[color].getEntropy() + ", Disperse=" + lmrBelowAlphaAVGScores[color].getDisperse());
			
			return pointer_below_alpha;
		}
	}
	
	
	public void addKillerMove(final int move, final int ply) {
		if (EngineConstants.ENABLE_KILLER_MOVES) {
			if (KILLER_MOVE_1[ply] != move) {
				KILLER_MOVE_2[ply] = KILLER_MOVE_1[ply];
				KILLER_MOVE_1[ply] = move;
			}
		}
	}
	
	
	public void addCounterMove(final int color, final int parentMove, final int counterMove) {
		if (EngineConstants.ENABLE_COUNTER_MOVES) {
			COUNTER_MOVES[color][MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)] = counterMove;
		}
	}

	public int getCounter(final int color, final int parentMove) {
		return COUNTER_MOVES[color][MoveUtil.getSourcePieceIndex(parentMove)][MoveUtil.getToIndex(parentMove)];
	}

	public int getKiller1(final int ply) {
		return KILLER_MOVE_1[ply];
	}

	public int getKiller2(final int ply) {
		return KILLER_MOVE_2[ply];
	}

	public void startPly() {
		nextToGenerate[currentPly + 1] = nextToGenerate[currentPly];
		nextToMove[currentPly + 1] = nextToGenerate[currentPly];
		currentPly++;
	}

	public void endPly() {
		currentPly--;
	}

	public int next() {
		return moves[nextToMove[currentPly]++];
	}

	public int getScore() {
		return moveScores[nextToMove[currentPly] - 1];
	}

	public int previous() {
		if (nextToMove[currentPly] - 1 < 0) {
			return 0;
		}
		
		return moves[nextToMove[currentPly] - 1];
	}

	public boolean hasNext() {
		return nextToGenerate[currentPly] != nextToMove[currentPly];
	}

	public void addMove(final int move) {
		moves[nextToGenerate[currentPly]++] = move;
	}

	public void setMVVLVAScores(final ChessBoard cb) {
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			moveScores[j] = MoveUtil.getAttackedPieceIndex(moves[j]) * 6 - MoveUtil.getSourcePieceIndex(moves[j]);
		}
	}
	
	public void setSEEScores(final ChessBoard cb) {
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			moveScores[j] = SEEUtil.getSeeCaptureScore(cb, moves[j]);
		}
	}
	
	public int getCountGoodAttacks(final ChessBoard cb) {
		int count = 0;
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (SEEUtil.getSeeCaptureScore(cb, moves[j]) > 0) count++;
		}
		return count;
	}
	
	
	public int getCountEqualAttacks(final ChessBoard cb) {
		int count = 0;
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (SEEUtil.getSeeCaptureScore(cb, moves[j]) == 0) count++;
		}
		return count;
	}
	
	
	public int getCountBadAttacks(final ChessBoard cb) {
		int count = 0;
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (SEEUtil.getSeeCaptureScore(cb, moves[j]) < 0) count++;
		}
		return count;
	}
	
	
	public int getCountGoodAndEqualAttacks(final ChessBoard cb) {
		int count = 0;
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (SEEUtil.getSeeCaptureScore(cb, moves[j]) >= 0) count++;
		}
		return count;
	}
	
	
	public int getCountMoves() {
		return nextToGenerate[currentPly] - nextToMove[currentPly];
	}
	
	
	public void setHHScores(final int colorToMove, final int parentMove) {
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			moveScores[j] = getHHScore(colorToMove, MoveUtil.getFromToIndex(moves[j]), MoveUtil.getSourcePieceIndex(moves[j]), MoveUtil.getToIndex(moves[j]), parentMove);
		}
	}
	
	
	public void setRootScores(final ChessBoard cb, final int parentMove, final int ttMove) {
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (ttMove == moves[j]) {
				moveScores[j] = 10000;
			} else if (!MoveUtil.isQuiet(moves[j])) {
				moveScores[j] = 1000 + SEEUtil.getSeeCaptureScore(cb, moves[j]);
			} else {
				moveScores[j] = getHHScore(cb.colorToMove, MoveUtil.getFromToIndex(moves[j]), MoveUtil.getSourcePieceIndex(moves[j]), MoveUtil.getToIndex(moves[j]), parentMove);
			}
			//System.out.println("moveScores[j]=" + moveScores[j]);
		}
	}
	
	
	public void setAllScores(final ChessBoard cb, final int parentMove, final int ttMove, int counterMove, int killer1Move, int killer2Move) {
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			if (ttMove == moves[j]) {
				moveScores[j] = 10000;
			} else if (counterMove == moves[j]) {
				moveScores[j] = 300;
			} else if (killer1Move == moves[j]) {
				moveScores[j] = 500;
			} else if (killer2Move == moves[j]) {
				moveScores[j] = 400;
			} else if (!MoveUtil.isQuiet(moves[j])) {
				moveScores[j] = 1000 + SEEUtil.getSeeCaptureScore(cb, moves[j]);
			} else {
				moveScores[j] = getHHScore(cb.colorToMove, MoveUtil.getFromToIndex(moves[j]), MoveUtil.getSourcePieceIndex(moves[j]), MoveUtil.getToIndex(moves[j]), parentMove);
			}
			//System.out.println("moveScores[j]=" + moveScores[j]);
		}
	}
	
	
	public void sort() {
		
		final int left = nextToMove[currentPly];
		
		randomizer_counter++;
		if (randomizer_counter % 100 == 0) {
			randomize(moveScores, moves, left, nextToGenerate[currentPly] - 1);
		}
		
		for (int i = left, j = i; i < nextToGenerate[currentPly] - 1; j = ++i) {
			final int score = moveScores[i + 1];
			final int move = moves[i + 1];
			while (score > moveScores[j]) {
				moveScores[j + 1] = moveScores[j];
				moves[j + 1] = moves[j];
				if (j-- == left) {
					break;
				}
			}
			moveScores[j + 1] = score;
			moves[j + 1] = move;
		}
	}
	
	
	private void randomize(int[] arr1, int[] arr2, int start, int end) {
		
	    for (int i = end; i > start + 1; i--) {
	    	
	    	int rnd_index = start + randomizer.nextInt(i - start);	    
	    	
	    	int tmp = arr1[i-1];
	    	arr1[i-1] = arr1[rnd_index];
	    	arr1[rnd_index] = tmp;
	    	
	    	tmp = arr2[i-1];
	    	arr2[i-1] = arr2[rnd_index];
	    	arr2[rnd_index] = tmp;
	    }
	}
	

	/*public String getMovesAsString() {
		StringBuilder sb = new StringBuilder();
		for (int j = nextToMove[currentPly]; j < nextToGenerate[currentPly]; j++) {
			sb.append(new MoveWrapper(moves[j]) + ", ");
		}
		return sb.toString();
	}
	 */
	
	
	public void generateMoves(final ChessBoard cb) {

		switch (Long.bitCount(cb.checkingPieces)) {
		case 0:
			// not in-check
			generateNotInCheckMoves(cb);
			break;
		case 1:
			// in-check
			switch (cb.pieceIndexes[Long.numberOfTrailingZeros(cb.checkingPieces)]) {
			case PAWN:
				// fall-through
			case NIGHT:
				// move king
				addKingMoves(cb);
				break;
			default:
				generateOutOfSlidingCheckMoves(cb);
			}
			break;
		default:
			// double check, only the king can move
			addKingMoves(cb);
		}
	}

	public void generateAttacks(final ChessBoard cb) {

		switch (Long.bitCount(cb.checkingPieces)) {
		case 0:
			// not in-check
			generateNotInCheckAttacks(cb);
			break;
		case 1:
			generateOutOfCheckAttacks(cb);
			break;
		default:
			// double check, only the king can attack
			addKingAttacks(cb);
		}
	}

	private void generateNotInCheckMoves(final ChessBoard cb) {

		// non pinned pieces
		addKingMoves(cb);
		addQueenMoves(cb.pieces[cb.colorToMove][QUEEN] & ~cb.pinnedPieces, cb.allPieces, cb.emptySpaces);
		addRookMoves(cb.pieces[cb.colorToMove][ROOK] & ~cb.pinnedPieces, cb.allPieces, cb.emptySpaces);
		addBishopMoves(cb.pieces[cb.colorToMove][BISHOP] & ~cb.pinnedPieces, cb.allPieces, cb.emptySpaces);
		addNightMoves(cb.pieces[cb.colorToMove][NIGHT] & ~cb.pinnedPieces, cb.emptySpaces);
		addPawnMoves(cb.pieces[cb.colorToMove][PAWN] & ~cb.pinnedPieces, cb, cb.emptySpaces);

		// pinned pieces
		long piece = cb.friendlyPieces[cb.colorToMove] & cb.pinnedPieces;
		while (piece != 0) {
			switch (cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]) {
			case PAWN:
				addPawnMoves(Long.lowestOneBit(piece), cb,
						cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case BISHOP:
				addBishopMoves(Long.lowestOneBit(piece), cb.allPieces,
						cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case ROOK:
				addRookMoves(Long.lowestOneBit(piece), cb.allPieces,
						cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case QUEEN:
				addQueenMoves(Long.lowestOneBit(piece), cb.allPieces,
						cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
			}
			piece &= piece - 1;
		}

	}

	private void generateOutOfSlidingCheckMoves(final ChessBoard cb) {

		// TODO when check is blocked -> pinned piece

		// move king or block sliding piece
		final long inBetween = ChessConstants.IN_BETWEEN[cb.kingIndex[cb.colorToMove]][Long.numberOfTrailingZeros(cb.checkingPieces)];
		if (inBetween != 0) {
			addNightMoves(cb.pieces[cb.colorToMove][NIGHT] & ~cb.pinnedPieces, inBetween);
			addBishopMoves(cb.pieces[cb.colorToMove][BISHOP] & ~cb.pinnedPieces, cb.allPieces, inBetween);
			addRookMoves(cb.pieces[cb.colorToMove][ROOK] & ~cb.pinnedPieces, cb.allPieces, inBetween);
			addQueenMoves(cb.pieces[cb.colorToMove][QUEEN] & ~cb.pinnedPieces, cb.allPieces, inBetween);
			addPawnMoves(cb.pieces[cb.colorToMove][PAWN] & ~cb.pinnedPieces, cb, inBetween);
		}

		addKingMoves(cb);
	}

	private void generateNotInCheckAttacks(final ChessBoard cb) {

		final long enemies = cb.friendlyPieces[cb.colorToMoveInverse];

		// non pinned pieces
		addEpAttacks(cb);
		addPawnAttacksAndPromotions(cb.pieces[cb.colorToMove][PAWN] & ~cb.pinnedPieces, cb, enemies, cb.emptySpaces);
		addNightAttacks(cb.pieces[cb.colorToMove][NIGHT] & ~cb.pinnedPieces, cb.pieceIndexes, enemies);
		addRookAttacks(cb.pieces[cb.colorToMove][ROOK] & ~cb.pinnedPieces, cb, enemies);
		addBishopAttacks(cb.pieces[cb.colorToMove][BISHOP] & ~cb.pinnedPieces, cb, enemies);
		addQueenAttacks(cb.pieces[cb.colorToMove][QUEEN] & ~cb.pinnedPieces, cb, enemies);
		addKingAttacks(cb);

		// pinned pieces
		long piece = cb.friendlyPieces[cb.colorToMove] & cb.pinnedPieces;
		while (piece != 0) {
			switch (cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]) {
			case PAWN:
				addPawnAttacksAndPromotions(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]], 0);
				break;
			case BISHOP:
				addBishopAttacks(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case ROOK:
				addRookAttacks(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
				break;
			case QUEEN:
				addQueenAttacks(Long.lowestOneBit(piece), cb,
						enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
			}
			piece &= piece - 1;
		}

	}

	private void generateOutOfCheckAttacks(final ChessBoard cb) {
		// attack attacker
		addEpAttacks(cb);
		addPawnAttacksAndPromotions(cb.pieces[cb.colorToMove][PAWN] & ~cb.pinnedPieces, cb, cb.checkingPieces, cb.emptySpaces);
		addNightAttacks(cb.pieces[cb.colorToMove][NIGHT] & ~cb.pinnedPieces, cb.pieceIndexes, cb.checkingPieces);
		addBishopAttacks(cb.pieces[cb.colorToMove][BISHOP] & ~cb.pinnedPieces, cb, cb.checkingPieces);
		addRookAttacks(cb.pieces[cb.colorToMove][ROOK] & ~cb.pinnedPieces, cb, cb.checkingPieces);
		addQueenAttacks(cb.pieces[cb.colorToMove][QUEEN] & ~cb.pinnedPieces, cb, cb.checkingPieces);
		addKingAttacks(cb);
	}

	private void addPawnAttacksAndPromotions(final long pawns, final ChessBoard cb, final long enemies, final long emptySpaces) {

		if (pawns == 0) {
			return;
		}

		if (cb.colorToMove == WHITE) {

			// non-promoting
			long piece = pawns & Bitboard.RANK_NON_PROMOTION[WHITE] & Bitboard.getBlackPawnAttacks(enemies);
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);
				long moves = StaticMoves.PAWN_ATTACKS[WHITE][fromIndex] & enemies;
				while (moves != 0) {
					final int toIndex = Long.numberOfTrailingZeros(moves);
					addMove(MoveUtil.createAttackMove(fromIndex, toIndex, PAWN, cb.pieceIndexes[toIndex]));
					moves &= moves - 1;
				}
				piece &= piece - 1;
			}

			// promoting
			piece = pawns & Bitboard.RANK_7;
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);

				// promotion move
				if ((Long.lowestOneBit(piece) << 8 & emptySpaces) != 0) {
					addPromotionMove(fromIndex, fromIndex + 8);
				}

				// promotion attacks
				addPromotionAttacks(StaticMoves.PAWN_ATTACKS[WHITE][fromIndex] & enemies, fromIndex, cb.pieceIndexes);

				piece &= piece - 1;
			}
		} else {
			// non-promoting
			long piece = pawns & Bitboard.RANK_NON_PROMOTION[BLACK] & Bitboard.getWhitePawnAttacks(enemies);
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);
				long moves = StaticMoves.PAWN_ATTACKS[BLACK][fromIndex] & enemies;
				while (moves != 0) {
					final int toIndex = Long.numberOfTrailingZeros(moves);
					addMove(MoveUtil.createAttackMove(fromIndex, toIndex, PAWN, cb.pieceIndexes[toIndex]));
					moves &= moves - 1;
				}
				piece &= piece - 1;
			}

			// promoting
			piece = pawns & Bitboard.RANK_2;
			while (piece != 0) {
				final int fromIndex = Long.numberOfTrailingZeros(piece);

				// promotion move
				if ((Long.lowestOneBit(piece) >>> 8 & emptySpaces) != 0) {
					addPromotionMove(fromIndex, fromIndex - 8);
				}

				// promotion attacks
				addPromotionAttacks(StaticMoves.PAWN_ATTACKS[BLACK][fromIndex] & enemies, fromIndex, cb.pieceIndexes);

				piece &= piece - 1;
			}
		}
	}

	private void addBishopAttacks(long piece, final ChessBoard cb, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getBishopMoves(fromIndex, cb.allPieces) & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, BISHOP, cb.pieceIndexes[toIndex]));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addRookAttacks(long piece, final ChessBoard cb, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getRookMoves(fromIndex, cb.allPieces) & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, ROOK, cb.pieceIndexes[toIndex]));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addQueenAttacks(long piece, final ChessBoard cb, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getQueenMoves(fromIndex, cb.allPieces) & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, QUEEN, cb.pieceIndexes[toIndex]));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addBishopMoves(long piece, final long allPieces, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getBishopMoves(fromIndex, allPieces) & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), BISHOP));
				moves &= moves - 1;
			}

			piece &= piece - 1;
		}
	}

	private void addQueenMoves(long piece, final long allPieces, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getQueenMoves(fromIndex, allPieces) & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), QUEEN));
				moves &= moves - 1;
			}

			piece &= piece - 1;
		}
	}

	private void addRookMoves(long piece, final long allPieces, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = MagicUtil.getRookMoves(fromIndex, allPieces) & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), ROOK));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addNightMoves(long piece, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = StaticMoves.KNIGHT_MOVES[fromIndex] & possiblePositions;
			while (moves != 0) {
				addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), NIGHT));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addPawnMoves(final long pawns, final ChessBoard cb, final long possiblePositions) {

		if (pawns == 0) {
			return;
		}

		if (cb.colorToMove == WHITE) {
			// 1-move
			long piece = pawns & (possiblePositions >>> 8) & Bitboard.RANK_23456;
			while (piece != 0) {
				addMove(MoveUtil.createWhitePawnMove(Long.numberOfTrailingZeros(piece)));
				piece &= piece - 1;
			}
			// 2-move
			piece = pawns & (possiblePositions >>> 16) & Bitboard.RANK_2;
			while (piece != 0) {
				if ((cb.emptySpaces & (Long.lowestOneBit(piece) << 8)) != 0) {
					addMove(MoveUtil.createWhitePawn2Move(Long.numberOfTrailingZeros(piece)));
				}
				piece &= piece - 1;
			}
		} else {
			// 1-move
			long piece = pawns & (possiblePositions << 8) & Bitboard.RANK_34567;
			while (piece != 0) {
				addMove(MoveUtil.createBlackPawnMove(Long.numberOfTrailingZeros(piece)));
				piece &= piece - 1;
			}
			// 2-move
			piece = pawns & (possiblePositions << 16) & Bitboard.RANK_7;
			while (piece != 0) {
				if ((cb.emptySpaces & (Long.lowestOneBit(piece) >>> 8)) != 0) {
					addMove(MoveUtil.createBlackPawn2Move(Long.numberOfTrailingZeros(piece)));
				}
				piece &= piece - 1;
			}
		}
	}

	private void addKingMoves(final ChessBoard cb) {
		
		if (Properties.DUMP_CASTLING) System.out.println("MoveGenerator.addKingMoves");
		
		final int fromIndex = cb.kingIndex[cb.colorToMove];
		
		long moves = StaticMoves.KING_MOVES[fromIndex] & cb.emptySpaces;
		
		while (moves != 0) {
			
			addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), KING));
			
			moves &= moves - 1;
		}

		
		// castling
		if (cb.checkingPieces == 0) {
			
			if (Properties.DUMP_CASTLING) System.out.println("MoveGenerator.addKingMoves: cb.colorToMove=" + cb.colorToMove + ", cb.castlingRights=" + cb.castlingRights + ", cb.castlingConfig=" + cb.castlingConfig);
			
			long castlingIndexes = CastlingUtil.getCastlingIndexes(cb.colorToMove, cb.castlingRights, cb.castlingConfig);
			
			if (Properties.DUMP_CASTLING) System.out.println("MoveGenerator.addKingMoves: castlingIndexes=" + castlingIndexes);
			
			while (castlingIndexes != 0) {
				
				final int toIndex_king = Long.numberOfTrailingZeros(castlingIndexes);
				
				if (Properties.DUMP_CASTLING) System.out.println("MoveGenerator.addKingMoves: toIndex_king=" + toIndex_king);
				
				// no piece in between?
				if (CastlingUtil.isValidCastlingMove(cb, fromIndex, toIndex_king)) {
					
					addMove(MoveUtil.createCastlingMove(fromIndex, toIndex_king));
				}
				
				castlingIndexes &= castlingIndexes - 1;
			}
		}
	}

	private void addKingAttacks(final ChessBoard cb) {
		final int fromIndex = cb.kingIndex[cb.colorToMove];
		long moves = StaticMoves.KING_MOVES[fromIndex] & cb.friendlyPieces[cb.colorToMoveInverse];
		while (moves != 0) {
			final int toIndex = Long.numberOfTrailingZeros(moves);
			addMove(MoveUtil.createAttackMove(fromIndex, toIndex, KING, cb.pieceIndexes[toIndex]));
			moves &= moves - 1;
		}
	}

	private void addNightAttacks(long piece, final int[] pieceIndexes, final long possiblePositions) {
		while (piece != 0) {
			final int fromIndex = Long.numberOfTrailingZeros(piece);
			long moves = StaticMoves.KNIGHT_MOVES[fromIndex] & possiblePositions;
			while (moves != 0) {
				final int toIndex = Long.numberOfTrailingZeros(moves);
				addMove(MoveUtil.createAttackMove(fromIndex, toIndex, NIGHT, pieceIndexes[toIndex]));
				moves &= moves - 1;
			}
			piece &= piece - 1;
		}
	}

	private void addEpAttacks(final ChessBoard cb) {
		if (cb.epIndex == 0) {
			return;
		}
		long piece = cb.pieces[cb.colorToMove][PAWN] & StaticMoves.PAWN_ATTACKS[cb.colorToMoveInverse][cb.epIndex];
		while (piece != 0) {
			addMove(MoveUtil.createEPMove(Long.numberOfTrailingZeros(piece), cb.epIndex));
			piece &= piece - 1;
		}
	}

	private void addPromotionMove(final int fromIndex, final int toIndex) {
		addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex));
		addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_N, fromIndex, toIndex));
		if (EngineConstants.GENERATE_BR_PROMOTIONS) {
			addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_B, fromIndex, toIndex));
			addMove(MoveUtil.createPromotionMove(MoveUtil.TYPE_PROMOTION_R, fromIndex, toIndex));
		}
	}

	private void addPromotionAttacks(long moves, final int fromIndex, final int[] pieceIndexes) {
		while (moves != 0) {
			final int toIndex = Long.numberOfTrailingZeros(moves);
			addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_Q, fromIndex, toIndex, pieceIndexes[toIndex]));
			addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_N, fromIndex, toIndex, pieceIndexes[toIndex]));
			if (EngineConstants.GENERATE_BR_PROMOTIONS) {
				addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_B, fromIndex, toIndex, pieceIndexes[toIndex]));
				addMove(MoveUtil.createPromotionAttack(MoveUtil.TYPE_PROMOTION_R, fromIndex, toIndex, pieceIndexes[toIndex]));
			}
			moves &= moves - 1;
		}
	}
}

package bagaturchess.learning.goldmiddle.impl4;

import java.util.Arrays;


public class Statistics {

	public static final boolean ENABLED = true;

	public static long evalNodes, abNodes, seeNodes, pvNodes, cutNodes, allNodes, qNodes;
	public static long ttHits, ttMisses;
	public static int staleMateCount, mateCount;
	public static int depth;
	public static int seldepth;
	public static int epCount, castleCount, promotionCount;
	public static long pawnEvalCacheHits, pawnEvalCacheMisses;
	public static long materialCacheMisses, materialCacheHits;
	public static int bestMoveTT, bestMoveTTLower, bestMoveTTUpper, bestMoveCounter, bestMoveKiller1, bestMoveKiller2, bestMoveKillerEvasive1,
			bestMoveKillerEvasive2, bestMoveOther, bestMovePromotion, bestMoveWinningCapture, bestMoveLosingCapture;
	public static int repetitions, repetitionTests;
	public static int checkExtensions, endGameExtensions;
	public static int nullMoveHit, nullMoveMiss;
	public static long evalCacheHits, evalCacheMisses;
	public static int iidCount;
	public static final int[] razored = new int[10];
	public static final int[] futile = new int[10];
	public static final int[] staticNullMoved = new int[10];
	public static final int[] lmped = new int[10];
	public static final int[] failHigh = new int[64];
	public static int drawishByMaterialCount;

	public static void reset() {

		if (!ENABLED) {
			return;
		}
		Arrays.fill(razored, 0);
		Arrays.fill(futile, 0);
		Arrays.fill(staticNullMoved, 0);
		Arrays.fill(lmped, 0);
		Arrays.fill(failHigh, 0);

		bestMoveCounter = 0;
		qNodes = 0;
		pvNodes = 1; // so we never divide by zero
		cutNodes = 0;
		allNodes = 0;
		drawishByMaterialCount = 0;
		pawnEvalCacheMisses = 0;
		pawnEvalCacheHits = 0;
		castleCount = 0;
		epCount = 0;
		evalNodes = 0;
		ttHits = 0;
		ttMisses = 0;
		staleMateCount = 0;
		mateCount = 0;
		depth = 0;
		seldepth = 0;
		abNodes = 0;
		promotionCount = 0;
		seeNodes = 0;
		repetitions = 0;
		nullMoveHit = 0;
		nullMoveMiss = 0;
		bestMoveTT = 0;
		bestMoveTTLower = 0;
		bestMoveTTUpper = 0;
		bestMoveKiller1 = 0;
		bestMoveKiller2 = 0;
		bestMoveKillerEvasive1 = 0;
		bestMoveKillerEvasive2 = 0;
		bestMoveOther = 0;
		bestMovePromotion = 0;
		bestMoveWinningCapture = 0;
		bestMoveLosingCapture = 0;
		checkExtensions = 0;
		endGameExtensions = 0;
		repetitionTests = 0;
		evalCacheHits = 0;
		evalCacheMisses = 0;
		iidCount = 0;
	}
}

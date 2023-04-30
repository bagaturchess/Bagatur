package bagaturchess.search.impl.rootsearch.sequential.mtd;


public class BetaGeneratorHelper {
	
	
	public static final int[] getOneStepBetas_FirstTime(int betasCount) {
		
		if (betasCount == 1) {
			return new int[] {0};
			
		} else if (betasCount == 2) {
			return new int[] {0, 64};
			
		} else if (betasCount == 4) {
			return new int[] {-32, 0, 32, 64};
			
		} else if (betasCount == 8) {
			return new int[] {-64, -32, -16, 0, 8, 16, 32, 64};
			
		} else if (betasCount == 16) {
			return new int[] {-64, -32, -16, -8, -4, -2, -1, 0,
								1, 2, 4, 8, 16, 24, 32, 64};
			
		} else if (betasCount == 32) {
			return new int[] {-64, -56 -48, -40, -36, -32, -28, -24,
								-20, -16, -12, -8, -4, -2, -1, 0,
								1, 2, 4, 8, 12, 16, 20, 24,
								28, 32, 36, 40, 44, 48, 56, 64};
			
		} else if (betasCount == 64) {
			return new int[] {-64, -60, -52, -56 -48, -46, -44, -42,
								-40, -38, -36, -34, -32, -30, -28, -26,
								-24, -22, -20, -18, -16, -14, -12, -10,
								-8, -6, -5, -4, -3, -2,	-1,	0,
								1, 2, 3, 4, 5, 6, 7, 8,
								10, 12, 14, 16, 18, 20, 22, 24,
								26, 28, 30, 32, 34, 36, 38, 40,
								42, 44, 46, 48, 52, 56, 60, 64};
		} else {
			throw new IllegalStateException("Parallel betas count is not supported = " + betasCount);
		}
	}
	
	
	public static final int[] getOneStepBetas_OneDirection(int betasCount) {
		
		if (betasCount == 1) {
			return new int[] {64};
			
		} else if (betasCount == 2) {
			return new int[] {32, 64};
			
		} else if (betasCount == 4) {
			return new int[] {8, 16, 32, 64};
			
		} else if (betasCount == 8) {
			return new int[] {1, 2, 4, 8, 16, 24, 32, 64};
			
		} else if (betasCount == 16) {
			return new int[] {1, 2, 4, 8, 12, 16, 20, 24,
								28, 32, 40, 44, 48, 52, 56, 64};
			
		} else if (betasCount == 32) {
			return new int[] {1, 2, 4, 6, 8, 10, 12, 14,
								16, 18, 20, 22, 24, 26, 28, 30,
								32, 34, 36, 38, 40, 42, 44, 46,
								48, 50, 52, 54, 56, 58, 60, 64};
			
		} else if (betasCount == 64) {
			return new int[] {1, 2, 3, 4, 5, 6, 7, 8,
								9, 10, 11, 12, 13, 14, 15, 16,
								17, 18, 19, 20, 21, 22, 23, 24,
								25, 26, 27, 28, 29, 30, 31, 32,
								33, 34, 35, 36, 37, 38, 39, 40,
								41, 42, 43, 44, 45, 46, 47, 48,
								49, 50, 51, 52, 53, 54, 55, 56,
								57, 58, 59, 60, 61, 62, 63, 64};
			
		} else {
			throw new IllegalStateException("Parallel betas count is not supported = " + betasCount);
		}
	}
}

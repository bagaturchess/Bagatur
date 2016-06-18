package bagaturchess.learning.goldmiddle.run;

public class PSTMain {
	
	
	private static int[] SYM_HOR = new int[] {
			 7,    6,    5,    4,    3,    2,    1,    0,   
			15,   14,   13,   12,   11,   10,    9,    8,   
			23,   22,   21,   20,   19,   18,   17,   16,   
			31,   30,   29,   28,   27,   26,   25,   24,   
			39,   38,   37,   36,   35,   34,   33,   32,   
			47,   46,   45,   44,   43,   42,   41,   40,   
			55,   54,   53,   52,   51,   50,   49,   48,   
			63,   62,   61,   60,   59,   58,   57,   56,   
	};
	
	
	private static int[] SYM_VER = new int[] {
		56,   57,   58,   59,   60,   61,   62,   63,   
		48,   49,   50,   51,   52,   53,   54,   55,   
		40,   41,   42,   43,   44,   45,   46,   47,   
		32,   33,   34,   35,   36,   37,   38,   39,   
		24,   25,   26,   27,   28,   29,   30,   31,   
		16,   17,   18,   19,   20,   21,   22,   23,   
		 8,    9,   10,   11,   12,   13,   14,   15,   
		 0,    1,    2,    3,    4,    5,    6,    7,   
	};
	
	
	public static void main(String[] args) {
		int[] arr = new int[] {
				-23,   -19,   -14,   -8,   -3,   0,   1,   2,   3,   4,   5,   6,   7,   9,
		};
		
		int min = 10000;
		int max = -10000;
		int sum = 0;
		for (int i=0; i<arr.length; i++) {
			sum += arr[i];
			if (arr[i] < min) {
				min = arr[i];
			}
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		int avg = sum / arr.length;
		System.out.println("AVG: " + avg + ", MIN: " + min + ", MAX: " + max);
		
		/*for (int i=0; i<arr.length; i++) {
			int j = SYM_HOR[i];
			//int j = SYM_VER[i];
			
			if (i > 0 && i % 8 ==0) {
				System.out.println("");
			}
			System.out.print(((arr[i] + arr[j]) / 2 - avg) + ", ");
		}*/
		
		for (int i=0; i<arr.length; i++) {
			int j = arr[i];
			int value = (int) (j - ((max + min) / 2.0));
			System.out.print(value + ", ");
		}
	}
}

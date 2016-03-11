/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.bitboard.common;

import java.util.Arrays;
import java.util.Random;

import bagaturchess.bitboard.impl.Bits;

public class Utils {
	
	
	public static String[] copyOfRange(String[] args, int start) {
		return copyOfRange(args, start, args.length);
	}
	
	public static String[] copyOfRange(String[] args, int start, int end) {
		String[] subarray = new String[args.length - 1];
		int counter = 0;
		for (int i=start; i<end; i++) {
			subarray[counter++] = args[i];
		}
		return subarray;
	}
	
	public static String[] concat(String[] a1, String[] a2) {
		String[] result = new String[a1.length + a2.length];
		
		System.arraycopy(a1, 0, result, 0, a1.length);
		System.arraycopy(a2, 0, result, a1.length, a2.length);
		
		return result;
	}
	
	
	public static void dumpMemory(String message) {
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		System.out.println("Memory: [" + message + "]" + ((total - free) / (1024 * 1024)) + " MB");
	}
	
	public static final long[] copy(long[] source) {
		long[] result = new long[source.length];
		for (int i=0; i<source.length; i++) {
			result[i] = source[i];
		}
		return result;
	}
	
	public static final long[] copy(long[] source, long[] result) {
		for (int i=0; i<source.length; i++) {
			result[i] = source[i];
		}
		return result;
	}
	
	public static final int[] copy(int[] source) {
		int[] result = new int[source.length];
		for (int i=0; i<source.length; i++) {
			result[i] = source[i];
		}
		return result;
	}
	
	public static final boolean[] copy(boolean[] source) {
		boolean[] result = new boolean[source.length];
		for (int i=0; i<source.length; i++) {
			result[i] = source[i];
		}
		return result;
	}
	
	public static final long[][] copy(long[][] source) {
		long[][] result = new long[source.length][];
		for (int i=0; i<source.length; i++) {
			long[] el = source[i];
			if (el != null) {
				result[i] = copy(source[i]);
			}
		}
		return result;
	}
	
	public static final boolean equals(long[][] arr1, long[][] arr2) {
		boolean result = false;
		if (arr1.length == arr2.length) {
			for (int i=0; i<arr1.length; i++) {
				long[] el1 = arr1[i];
				long[] el2 = arr2[i];
				if (!Arrays.equals(el1, el2)) {
					result = false;
					break;
				} else if(i == arr1.length - 1) {
					result = true;
					break;
				}
			}		
		}
		return result;
	}
	
	  public static final int countBits_less1s(long number) {
		  	int result = 0;
		  	while (number != Bits.NUMBER_0) {
		  		number = (number - 1) & number;
		  		result++;
		  	}
		  	if (result < 0 || result >= 64) {
		  		throw new IllegalStateException();
		  	}
		  	return result;
		 }
	  
  public static final int countBits(long val) {
      val -= (val & 0xaaaaaaaaaaaaaaaaL) >>> 1;
      val =  (val & 0x3333333333333333L) + ((val >>> 2) & 0x3333333333333333L);
      val =  (val + (val >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
      val += val >>> 8;     
      val += val >>> 16;    
      return ((int)(val) + (int)(val >>> 32)) & 0xff;
  }
  
  public static final int firstSetBit(long number) {
  	int result = 0;
  	for (int i=0; i<Bits.ALL_BITS.length; i++) {
  		if ((Bits.ALL_BITS[i] & number) != 0L) {
  			result = 63 - i;
  			break;
  		}
  	}
  	if (result < 0 || result >= 64) {
  		throw new IllegalStateException();
  	}
  	return result;
  }
  
  public static final int lastSetBit(long number) {
  	int result = 0;
  	for (int i=Bits.ALL_BITS.length - 1; i>=0; i--) {
  		if ((Bits.ALL_BITS[i] & number) != 0L) {
  			result = 63 - i;
  			break;
  		}
  	}
  	if (result < 0 || result >= 64) {
  		throw new IllegalStateException();
  	}
  	return result;
  }
  
  public static final boolean has1BitSet(long number) {
  	final long number1 = (number - 1) & number;
	return number1 == Bits.NUMBER_0; 
  }
  
  public static final boolean has2BitsSet(long number) {
  	final long number1 = (number - 1) & number;
  	final long number2 = (number1 - 1) & number1;
  	return number2 == Bits.NUMBER_0;
  }
  
  public static final boolean has3BitsSet(long number) {
  	final long number1 = (number - 1) & number;
  	final long number2 = (number1 - 1) & number1;
  	final long number3 = (number2 - 1) & number2;
  	return number3 == Bits.NUMBER_0;
  }
  
  public static double[] reverseSpecial(double[] arr) {
	  	if (arr.length != 64) {
	  		throw new IllegalStateException();
	  	}
	  	reverse(arr, 0, arr.length);
	  	
	  	reverse(arr, 0, 8);
	  	reverse(arr, 8, 16);
	  	reverse(arr, 16, 24);
	  	reverse(arr, 24, 32);
	  	reverse(arr, 32, 40);
	  	reverse(arr, 40, 48);
	  	reverse(arr, 48, 56);
	  	reverse(arr, 56, 64);
	  	
	  	return arr;
	  }
  
  public static byte[] reverseSpecial(byte[] arr) {
	  	if (arr.length != 64) {
	  		throw new IllegalStateException();
	  	}
	  	reverse(arr, 0, arr.length);
	  	
	  	reverse(arr, 0, 8);
	  	reverse(arr, 8, 16);
	  	reverse(arr, 16, 24);
	  	reverse(arr, 24, 32);
	  	reverse(arr, 32, 40);
	  	reverse(arr, 40, 48);
	  	reverse(arr, 48, 56);
	  	reverse(arr, 56, 64);
	  	
	  	return arr;
	  }
  
  public static int[] reverseSpecial(int[] arr) {
  	if (arr.length != 64) {
  		throw new IllegalStateException();
  	}
  	reverse(arr, 0, arr.length);
  	
  	reverse(arr, 0, 8);
  	reverse(arr, 8, 16);
  	reverse(arr, 16, 24);
  	reverse(arr, 24, 32);
  	reverse(arr, 32, 40);
  	reverse(arr, 40, 48);
  	reverse(arr, 48, 56);
  	reverse(arr, 56, 64);
  	
  	return arr;
  }
  
  public static double[] reverse(double[] arr) {
	  	
	  	int size = arr.length;
	  	
	  	for (int i = 0; i < size / 2; i++) {
	  		double tmp = arr[i];
	  		arr[i] = arr[size - i - 1];
	  		arr[size - i - 1] = tmp;
	  	}
	  	
	  	return arr;
	  }
  
  public static int[] reverse(int[] arr) {
  	
  	int size = arr.length;
  	
  	for (int i = 0; i < size / 2; i++) {
  		int tmp = arr[i];
  		arr[i] = arr[size - i - 1];
  		arr[size - i - 1] = tmp;
  	}
  	
  	return arr;
  }
  
  public static byte[] reverse(byte[] arr) {
	  	
	  	int size = arr.length;
	  	
	  	for (int i = 0; i < size / 2; i++) {
	  		byte tmp = arr[i];
	  		arr[i] = arr[size - i - 1];
	  		arr[size - i - 1] = tmp;
	  	}
	  	
	  	return arr;
	  }
  
  public static double[] reverse(double[] arr, int from, int to) {
	  	to--;
	  	while (from < to) {
	  		double f = arr[from];
	  		double t = arr[to];
	  		arr[from] = t;
	  		arr[to] = f;
	  		from++;
	  		to--;
	  	}
	  	return arr;
	  }
  
  public static int[] reverse(int[] arr, int from, int to) {
  	to--;
  	while (from < to) {
  		int f = arr[from];
  		int t = arr[to];
  		arr[from] = t;
  		arr[to] = f;
  		from++;
  		to--;
  	}
  	return arr;
  }
  
  public static byte[] reverse(byte[] arr, int from, int to) {
	  	to--;
	  	while (from < to) {
	  		byte f = arr[from];
	  		byte t = arr[to];
	  		arr[from] = t;
	  		arr[to] = f;
	  		from++;
	  		to--;
	  	}
	  	return arr;
	  }
  
	public static void bubbleSort(int[] arr1_sortby, int[] arr2, int size) {
		
		//int size = arr1_sortby.length;
		
		for (int i = 0; i < size; i++) {
			boolean change = false;
			for (int j= i + 1; j < size; j++) {
				int i_el = arr1_sortby[i];
				int j_el = arr1_sortby[j];
				if (j_el > i_el) {
					arr1_sortby[i] = i_el;
					arr1_sortby[j] = j_el;
					
					int i_el1 = arr2[i];
					int j_el1 = arr2[j];
					arr2[i] = i_el1;
					arr2[j] = j_el1;
					change = true;
				}
			}
			if (!change) {
				return;
			}
		}
		
		//check(from, to, moves);
	}
	
	private static Random rnd = new Random();
	
	public static void randomize(long[] arr, int start, int end) {
	    for (int i=end; i>1+start; i--) {
	    	
	    	int rnd_index = start + rnd.nextInt(i - start);
	    	
	    	long tmp = arr[i-1];
	      arr[i-1] = arr[rnd_index];
	      arr[rnd_index] = tmp;
	    }
	}
	
	public static void randomize(int[] arr, int start, int end) {
	    for (int i=end; i>1+start; i--) {
	    	
	    	int rnd_index = start + rnd.nextInt(i - start);
	    	
	    	int tmp = arr[i-1];
	      arr[i-1] = arr[rnd_index];
	      arr[rnd_index] = tmp;
	    }
	}
	
  public static void main(String args[]) {
	  
	  
	long max = Runtime.getRuntime().maxMemory();
	long total = Runtime.getRuntime().totalMemory();
	long free = Runtime.getRuntime().freeMemory();
	long usedMemory = max - (free + (max - total));
	System.out.println("max memory " + (max/ (1024 * 1024)));
	System.out.println("total memory " + (total/ (1024 * 1024)));
	System.out.println("free memory " + (free/ (1024 * 1024)));
	
	System.out.println("Used memory " + (usedMemory / (1024 * 1024)) + "MB");
  	//int[] arr = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
  	//reverse(arr);
  	//reverse(arr, 3, 5);
    //System.out.println(Arrays.asList());
  }
}

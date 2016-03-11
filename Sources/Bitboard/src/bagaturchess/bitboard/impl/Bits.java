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
package bagaturchess.bitboard.impl;

public class Bits {
	public static final long NUMBER_0 = 0L;
	public static final long NUMBER_1 = 1L;
	public static final long NUMBER_MINUS_1 = -1L;

	public static final int NUMBER_64 = 64;
	public static final int NUMBER_69 = 69;
	
	public static final int PRIME_67 = 64;
	
	private static final int MAX_INT = 2147483647;
	
	public static final long BIT_0 = 1L << 63;
	public static final long BIT_1 = 1L << 62;
	public static final long BIT_2 = 1L << 61;
	public static final long BIT_3 = 1L << 60;
	public static final long BIT_4 = 1L << 59;
	public static final long BIT_5 = 1L << 58;
	public static final long BIT_6 = 1L << 57;
	public static final long BIT_7 = 1L << 56;
	public static final long BIT_8 = 1L << 55;
	public static final long BIT_9 = 1L << 54;
	public static final long BIT_10 = 1L << 53;
	public static final long BIT_11 = 1L << 52;
	public static final long BIT_12 = 1L << 51;
	public static final long BIT_13 = 1L << 50;
	public static final long BIT_14 = 1L << 49;
	public static final long BIT_15 = 1L << 48;
	public static final long BIT_16 = 1L << 47;
	public static final long BIT_17 = 1L << 46;
	public static final long BIT_18 = 1L << 45;
	public static final long BIT_19 = 1L << 44;
	public static final long BIT_20 = 1L << 43;
	public static final long BIT_21 = 1L << 42;
	public static final long BIT_22 = 1L << 41;
	public static final long BIT_23 = 1L << 40;
	public static final long BIT_24 = 1L << 39;
	public static final long BIT_25 = 1L << 38;
	public static final long BIT_26 = 1L << 37;
	public static final long BIT_27 = 1L << 36;
	public static final long BIT_28 = 1L << 35;
	public static final long BIT_29 = 1L << 34;
	public static final long BIT_30 = 1L << 33;
	public static final long BIT_31 = 1L << 32;
	public static final long BIT_32 = 1L << 31;
	public static final long BIT_33 = 1L << 30;
	public static final long BIT_34 = 1L << 29;
	public static final long BIT_35 = 1L << 28;
	public static final long BIT_36 = 1L << 27;
	public static final long BIT_37 = 1L << 26;
	public static final long BIT_38 = 1L << 25;
	public static final long BIT_39 = 1L << 24;
	public static final long BIT_40 = 1L << 23;
	public static final long BIT_41 = 1L << 22;
	public static final long BIT_42 = 1L << 21;
	public static final long BIT_43 = 1L << 20;
	public static final long BIT_44 = 1L << 19;
	public static final long BIT_45 = 1L << 18;
	public static final long BIT_46 = 1L << 17;
	public static final long BIT_47 = 1L << 16;
	public static final long BIT_48 = 1L << 15;
	public static final long BIT_49 = 1L << 14;
	public static final long BIT_50 = 1L << 13;
	public static final long BIT_51 = 1L << 12;
	public static final long BIT_52 = 1L << 11;
	public static final long BIT_53 = 1L << 10;
	public static final long BIT_54 = 1L << 9;
	public static final long BIT_55 = 1L << 8;
	public static final long BIT_56 = 1L << 7;
	public static final long BIT_57 = 1L << 6;
	public static final long BIT_58 = 1L << 5;
	public static final long BIT_59 = 1L << 4;
	public static final long BIT_60 = 1L << 3;
	public static final long BIT_61 = 1L << 2;
	public static final long BIT_62 = 1L << 1;
	public static final long BIT_63 = 1L << 0;
	
	public static final long[] ALL_BITS = {BIT_0, BIT_1, BIT_2, BIT_3, BIT_4, BIT_5, BIT_6, BIT_7, //67
		BIT_8, BIT_9, BIT_10, BIT_11, BIT_12, BIT_13, BIT_14, BIT_15, //59
		BIT_16, BIT_17, BIT_18, BIT_19, BIT_20, BIT_21, BIT_22, BIT_23, //53
		BIT_24, BIT_25, BIT_26, BIT_27, BIT_28, BIT_29, BIT_30, BIT_31, //53
		BIT_32, BIT_33, BIT_34, BIT_35, BIT_36, BIT_37, BIT_38, BIT_39, //37
		BIT_40, BIT_41, BIT_42, BIT_43, BIT_44, BIT_45, BIT_46, BIT_47, //29
		BIT_48, BIT_49, BIT_50, BIT_51, BIT_52, BIT_53, BIT_54, BIT_55, //19
		BIT_56, BIT_57, BIT_58, BIT_59, BIT_60, BIT_61, BIT_62, BIT_63}; //11
	
	/*public static final int getIDByBitboard(long bitBoard, int normalizer) {
		int id = (int) (bitBoard % normalizer);
		if (id < 0) {
			id = -id;
		}
		return id;
	}*/
	
    public static final int bitCount(long val) {
        val -= (val & 0xaaaaaaaaaaaaaaaaL) >>> 1;
        val =  (val & 0x3333333333333333L) + ((val >>> 2) & 0x3333333333333333L);
        val =  (val + (val >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
        val += val >>> 8;     
        val += val >>> 16;    
        return ((int)(val) + (int)(val >>> 32)) & 0xff;
    }
    
    public static long reverse(long i) {
		i = (i & 0x5555555555555555L) << 1 | (i >>> 1) & 0x5555555555555555L;
		i = (i & 0x3333333333333333L) << 2 | (i >>> 2) & 0x3333333333333333L;
		i = (i & 0x0f0f0f0f0f0f0f0fL) << 4 | (i >>> 4) & 0x0f0f0f0f0f0f0f0fL;
		i = (i & 0x00ff00ff00ff00ffL) << 8 | (i >>> 8) & 0x00ff00ff00ff00ffL;
		i = (i << 48) | ((i & 0xffff0000L) << 16) |
		    ((i >>> 16) & 0xffff0000L) | (i >>> 48);
		return i;
    }
    
    public static final String toBinaryString(long number) {
    	String result = Long.toBinaryString(number);
    	int len = result.length();
    	for (int i=len; i<64; i++ ) {
    		result = "0" + result;
    	}
    	return result;
    }
    
    public static final String toBinaryStringMatrix(long number) {
    	String result = "\r\n";
    	String line = toBinaryString(number);
    	result += line.substring(56, 64) + "\r\n";
    	result += line.substring(48, 56) + "\r\n";
    	result += line.substring(40, 48) + "\r\n";
    	result += line.substring(32, 40) + "\r\n";
    	result += line.substring(24, 32) + "\r\n";
    	result += line.substring(16, 24) + "\r\n";
    	result += line.substring(8, 16) + "\r\n";
    	result += line.substring(0, 8) + "\r\n";
    	return result;
    }
    
    public static long findNormalizerSimpleNumber(long[] numbers) {
    	long result = -1;
    	
    	int numbersCount = numbers.length;
    	for (int i=numbersCount; i<=MAX_INT; i++) {
    		
    		//if (isSimple(i)) {
    			//System.out.println("try with: " + i);
    			long[] remainders = new long[i];
    			fillWithNegativeOnes(remainders);
    			for (int j=0; j<numbersCount; j++) {
    				long number = numbers[j];
    				long remainder = number % i;
    				if (remainder <= 0) {
    					remainder = -remainder;
    				}
    				if (Integer.MAX_VALUE <= remainder) {
    					throw new IllegalStateException("remainder=" + remainder
    							+ ", Integer.MAX_VALUE=" + Integer.MAX_VALUE);
    				}
    				
    				if (remainders[(int)remainder] != -1) {
    					break;
    				}
    				if (j == numbersCount - 1) {
    					result = i;
    					break;
    				}
    				remainders[(int)remainder] = 1;
    			}
    		//}
    		
			//Finded
			if (result != -1) {
				break;
			}
    	}
    	return result;
    }
    
    private static final void fillWithNegativeOnes(long[] arr) {
    	int length = arr.length;
    	for (int i=0; i<length; i++) {
    		arr[i] = -1;
    	}
    }
    
    public static final boolean isSimple(long number) {
    	if (number <= 0) {
    		throw new IllegalStateException();
    	}
    	boolean result = true;
    	long square = (long) Math.ceil(Math.sqrt(number));
    	for (long i=2; i<=square; i++) {
    		if (number % i == 0) {
    			result = false;
    			break;
    		}
    	}
    	return result;
    }
    
    public static final int nextSetBit_R2L(int from, long bits)
    {
    	long mask = 1L << from;
    	
    	do
        {
          if ((bits & mask) != 0)
            return from;
          mask <<= 1;
          from++;
        }
        while (mask != 0);

        return -1;
    }

    public static final int nextSetBit_R2L(int from, int to, long bits)
    {
    	long mask = 1L << from;
    	
    	do
        {
          if ((bits & mask) != 0)
            return from;
          mask <<= 1;
          from++;
        }
        while (from <= to);

        return -1;
    }
    
    public static final int nextSetBit_L2R(int from, long bits)
    {
    	long mask = 1L << 63 - from;
    	
    	do
        {
          if ((bits & mask) != 0)
            return from;
          mask >>= 1;
          from++;
        }
        while (mask != 0);

        return -1;
    }

    
    public static void main(String[] args) {
    	/*String result = "";
    	for (int i=0; i<64; i++) {
    		result += "BIT_" + i + ", ";
    	}
    	System.out.print(result);
    	*/
    	
    	for (int i=4769291; i<9999999; i++) {
    		if (isSimple(i) && i > 999999) {
    			System.out.println(i);
    		}
    	}
    	
    	//System.out.println(findNormalizerSimpleNumber(new long[] {1, 3, 7}));
    	//System.out.println(findNormalizerSimpleNumber(ALL_BITS));
    }
}

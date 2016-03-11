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
package bagaturchess.bitboard.impl.datastructs;

public class PrimeGenerator {

  /**
   * Prime numbers.<p>
   */
  static final protected int[] primes = {13, 17, 19, 23, 29, 31, 37, 43, 53, 61, 73, 89, 107, 127, 149, 179, 223, 257, 307, 367, 439, 523, 631, 757, 907, 1087, 1301, 1559, 1871, 2243, 2689, 3229, 3877, 4649, 5581, 6689, 8039, 9631, 11579, 13873, 16649, 19973, 23971, 28753, 34511, 41411, 49697, 59621, 71549, 85853, 103043, 123631, 148361, 178021, 213623, 256349, 307627, 369137, 442961, 531569, 637873, 765437, 918529, 1102237, 1322669, 1587221, 1904647, 2285581, 2742689, 3291221, 3949469, 4739363, 5687237, 6824669, 8189603, 9827537, 11793031, 14151629, 16981957, 20378357, 24454013, 29344823, 35213777, 42256531, 50707837, 60849407, 73019327, 87623147, 105147773, 126177323, 151412791, 181695341, 218034407, 261641287, 313969543, 376763459, 452116163, 542539391, 651047261, 781256711, 937508041, 1125009637, 1350011569, 1620013909, 1944016661, 2147483647};

  /**
   * Gets closest prime number bigger than key.
   * Performs binary search in db.<p>
   *
   * Usage:
   * <p><blockquote><pre>
   *    long l = PrimeGenerator.getClosestPrime(20);
   *    int index = (int)(l >> 32); // index = 3;
   *    int prime = (int)l;        // prime = 23
   * </pre></blockquote><p>
   *
   * @param   key the base number.
   * @return  two ints packed in long:
   *          high int is position i db array,
   *          low int is the closest prime number bigger than key.
   */
  public static final long getClosestPrime(int key) {
    int low = 0;
    int high = primes.length - 1;

    while (low <= high) {
      int mid = (low + high) >>> 1;
      int midVal = primes[mid];

      if (midVal < key) {
        low = mid + 1;
      } else if (midVal > key) {
        high = mid - 1;
      } else {
        return ((long) mid << 32) | primes[mid];
      }
    }

    return ((long) low << 32) | primes[low];
  }

  /**
   * Gets closest prime number bigger than key.
   * Performs sequential search in db starting from specified possition.<p>
   *
   * Usage:
   * <p><blockquote><pre>
   *    long l = PrimeGenerator.getClosestPrime(20, 4);
   *    int index = (int)(l >> 32); // index = 4;
   *    int prime = (int)l;        // prime = 29
   * </pre></blockquote><p>
   *
   * @param   key the base number.
   * @param   startPos the start position in db.
   * @return  two ints packed in long:
   *          high int is position i db array,
   *          low int is the closest prime number bigger than key.
   */
  public static final long getClosestPrime(int key, int startPos) {
    int i = (startPos < primes.length) ? startPos : primes.length;
    for (; primes[i] < key; i++) {
      ; 
    }
    return ((long) i << 32) | primes[i];
  }

}



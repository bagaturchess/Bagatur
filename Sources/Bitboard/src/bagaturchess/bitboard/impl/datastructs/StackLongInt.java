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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import bagaturchess.bitboard.common.Properties;

public class StackLongInt {
  /**
   * Last element in the list.<p>
   */
  protected static final int LAST = -1;
  
  /**
   * Static exception for better performance.<p>
   */
  public static final int NO_VALUE = LAST;
  
  //public static long collisions = 0; 
  
  //public static long puts = 0;
  
  /**
   * Capacity of the hash table.<p>
   */
  protected int capacity;
  /**
   * Pointer to list of free slots.<p>
   */
  transient protected int nextFree;
  /**
   * Keys.<p>
   */
  transient protected long keys[];
  /**
   * Values.<p>
   */
  transient protected int elements[];
  /**
   * Pointer to next slot in case of collision.<p>
   */
  transient protected int nextPtr[];

  protected int count;


  /**
   * Constructs a new, empty hashtable with specified initial
   * capacity, grow step and load factor.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   * @param   growStep  grow step of the hashtable.
   * @param   loadFactor  load factor of hashtable.
   * @param   hasher a hash function.
   */
  public StackLongInt(int initialCapacity) {
    init(initialCapacity);
  }

  /**
   * Retrieves the count of the elements in the structure.<p>
   *
   * @return   the count of the elements in the structure.
   */
  public int size() {
    return count;
  }

  public boolean isEmpty() {
    return count == 0;
  }
  
  public int getArraySize() {
  	return elements.length;
  }

  /**
   * Returns an array of the keys in this hashtable.<p>
   *
   * @return  array of the keys in this hashtable.
   */
  public long[] getAllKeys() {
    int index = 0;
    long[] result = new long[count];

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        result[index++] = keys[pos - capacity];
      } 
    } 

    return result;
  }

  /**
   * Returns an array of the values in this hashtable.<p>
   *
   * @return  array of the values in this hashtable.
   */
  public int[] getAllValues() {
    int index = 0;
    int[] result = new int[count];

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        result[index++] = elements[pos - capacity];
      } 
    } 

    return result;
  }

  /**
   * Tests if some key is mapped to the specified value in this hashtable.
   * This operation is more expensive than the containsKey method.
   *
   * Note that this method is identical in functionality to containsValue.<p>
   *
   * @param     value a value to search for.
   * @return    true if and only if some key maps to the
   *            value argument in this hashtable,
   *            false otherwise.
   */
  public boolean contains(int value) {
    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        if (elements[pos - capacity] == value) {
          return true;
        }
      } 
    } 

    return false;
  }

  /**
   * Tests if some key is mapped to the specified value in this hashtable.
   * This operation is more expensive than the containsKey method.
   *
   * Note that this method is identical in functionality to contains() method.<p>
   *
   * @param      value a value to search for.
   * @return     true if and only if some key maps to the
   *             value argument in this hashtable,
   *             false otherwise.
   */
  public boolean containsValue(int value) {
    return contains(value);
  }

  /**
   * Tests if the specified key is a key in this hashtable.<p>
   *
   * @param   key a possible element.
   * @return  true if and only if the specified element is in this
   *          hashtable, false otherwise.
   */
  public boolean containsKey(long key) {
    int pos = nextPtr[hash(key) % capacity];

    while (pos != LAST) {
      if (keys[pos - capacity] == key) {
        return true;
      }

      pos = nextPtr[pos];
    }

    return false;
  }

	/**
   * Clears this hashtable so that it contains no keys.<p>
   */
  public void clear() {
    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    nextFree = capacity;
    count = 0;
  }

  /**
   * Creates a shallow copy of this hashtable. The whole structure of the
   * hashtable is copied, but values are not cloned.
   * This is a relatively expensive operation.<p>
   *
   * @return  a clone of the hashtable.
   */
  public Object clone() {
    StackLongInt result = null;
		try {
			result = (StackLongInt) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    result.keys = new long[keys.length];
    result.elements = new int[elements.length];
    result.nextPtr = new int[nextPtr.length];
    System.arraycopy(nextPtr, 0, result.nextPtr, 0, nextPtr.length);
    System.arraycopy(keys, 0, result.keys, 0, keys.length);
    System.arraycopy(elements, 0, result.elements, 0, elements.length);
    return result;
  }

  /**
   * Compares the specified object with this set for equality.<p>
   *
   * @param  object a specified object.
   * @return true if the specified object is equal to this hashtable, false otherwise.
   */
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }

    if (!(object instanceof StackLongInt)) {
      return false;
    }

    StackLongInt t = (StackLongInt) object;

    if (t.count != count) {
      return false;
    }

    int index;
    int temp;

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        index = pos - capacity;
        temp = t.get(keys[index]);

        if (temp == NO_VALUE) {
        	return false;
        }
        
        if (elements[index] != temp) {
          return false;
        }
      } 
    } 

    return true;
  }

  
  public int hashCode() {
	  int result = 17;
	  result = 37 * result + count;
	  result = 37 * result + capacity;
	  result = 37 * result + arrayHashCode(nextPtr);
	  result = 37 * result + arrayHashCode(keys);
	  result = 37 * result + arrayHashCode(elements);
	  return result;
	}
	
	private int arrayHashCode(int[] array) {
	  if (array == null) {
	    return 0;
	  }
	  int result = 17;
	  for (int i = 0; i < array.length; i++) {
	    result = 37 * result + array[i];
	  }  
	  return result;
	}
	
	private int arrayHashCode(long[] array) {
	  if (array == null) {
	    return 0;
	  }
	  int result = 17;
	  for (int i = 0; i < array.length; i++) {
	    result = 37 * result + (int)(array[i] ^ (array[i] >>> 32));
	  }
	  return result;
	}
	
  public int inc(long key) {
	  	//puts++;
	  
	    if (count == elements.length) {
	        throw new IllegalStateException("Not enough initial space.");
	    }

	    int pos = hash(key) % capacity;

	    int index;

	    while (nextPtr[pos] != LAST) {
	    	//collisions++;
	      pos = nextPtr[pos];
	      index = pos - capacity;

	      if (keys[index] == key) {
	        return ++elements[index];
	      }
	    }

	    index = nextFree - capacity;
	    nextPtr[pos] = nextFree;
	    keys[index] = key;
	    elements[index] = 1;
	    nextFree = nextPtr[nextFree];
	    nextPtr[nextPtr[pos]] = LAST;
	    count++;
	    return 1;
  }

  public int get(long key) {
    int index;
    int pos = nextPtr[hash(key) % capacity];
    
    if (Properties.DEBUG_MODE) {
  	  if (capacity != keys.length) {
  		  throw new IllegalStateException("capacity=" + capacity + ", keys.length=" + keys.length);
  	  }
    }
    
    while (pos != LAST) {
      index = pos - capacity;
      
      if (!Properties.DEBUG_MODE) {
	      if (index >= capacity) {//Bugfixing for rare cases: In some cases in Chess Art For Kids game, the index is out of range: index==capacity.
	    	  return NO_VALUE;
	      }
      }
      
      if (keys[index] == key) {
        return elements[index];
      }

      pos = nextPtr[pos];
    }

    return NO_VALUE;
  }
  
  public int dec(long key) {
	  
	    int prevPos = hash(key) % capacity;
	    int pos = nextPtr[prevPos];
	    
	    int index;
	    
	    while (pos != LAST) {
	      index = pos - capacity;

	      if (keys[index] == key) {
	    	elements[index]--;
	    	if (elements[index] == 0) { //Remove
	          nextPtr[prevPos] = nextPtr[pos];
	          nextPtr[pos] = nextFree;
	          nextFree = pos;
	          count--;
	    	}
	        return elements[index];
	      }
	      
	      prevPos = pos;
	      pos = nextPtr[pos];
	    }
	    
	    if (Properties.DEBUG_MODE) {
		    throw new IllegalStateException("Key " + key + " not found.");
	    }
	    
	    return NO_VALUE;
  }

  /**
   * Initial data structure for use.<p>
   *
   * @param   initialCapacity specified capacity.
   */
  protected void init(int initialCapacity) {
    capacity = initialCapacity;

    nextPtr = new int[2 * capacity];

    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    keys = new long[capacity];
    elements = new int[capacity];
    nextFree = capacity;
    count = 0;
  }
  
  public static int hash(long key) {
	  	//int hash = (int) (key & 0x7fffffff);
	  	//System.out.println(hash);
	    //return hash;
	  	return (int) (key & 0x7fffffff);
  }

  /**
   * Returns a string representation of this hashtable object
   * in the form of a set of entries, enclosed in braces and separated
   * by the ASCII characters ,  (comma and space). Each
   * entry is rendered as the key, an equality sign =, and the
   * associated element, where the toString method is used to
   * convert the key and element to strings. Overrides the
   * toString method of java.lang.Object.<p>
   *
   * @return  a string representation of this hashtable.
   */
  public String toString() {
    int c = 0;
    int index;
    StringBuffer buf = new StringBuffer();
    buf.append("{");

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        index = pos - capacity;
        buf.append(keys[index] + "=" + elements[index]);

        if (++c < count) {
          buf.append(", ");
        }
      } 
    } 

    buf.append("}");
    return buf.toString();
  }

  // -----------------------------------------------------------
  // -------------- Some serialization magic -------------------
  // -----------------------------------------------------------
  /**
   * This method is used by Java serializer.<p>
   *
   * @param   stream an output stream.
   * @exception   IOException if an IO exception occur.
   */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    int index;

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        index = pos - capacity;
        stream.writeLong(keys[index]);
        stream.writeInt(elements[index]);
      } 
    } 
  }

  /**
   * This method is used by Java serializer.<p>
   *
   * @param   stream an input stream.
   * @exception   IOException if an IO exception occur.
   * @exception   ClassNotFoundException if the class not found.
   */
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    nextPtr = new int[capacity];

    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    keys = new long[capacity];
    elements = new int[capacity];
    nextFree = capacity;
    int size = count;
    count = 0;

    for (int i = 0; i < size; i++) {
      putQuick(stream.readLong(), stream.readInt());
    } 
  }
  
  /**
   * Put method for internal use. Not  and does not perform check for
   * overflow.<p>
   *
   * @param   key hashtable key.
   * @param   value value that key is to be mapped to.
   */
  protected void putQuick(long key, int value) {
    int pos = hash(key) % capacity;
    int index;

    while (nextPtr[pos] != LAST) {
      pos = nextPtr[pos];
    }

    index = nextFree - capacity;
    nextPtr[pos] = nextFree;
    keys[index] = key;
    elements[index] = value;
    nextFree = nextPtr[nextFree];
    nextPtr[nextPtr[pos]] = LAST;
    count++;
  }
}


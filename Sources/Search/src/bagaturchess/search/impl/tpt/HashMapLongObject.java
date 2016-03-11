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
package bagaturchess.search.impl.tpt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class implements Hashtable, which maps keys to values.<p>
 *
 *
 * Initial capacity and load factor are parameteres of the
 * hashtable instance that affect its performance.
 * Capacity parameter specifies the number of buckets in hash table.
 * Initial capacity is the capacity at the time the hash table
 * is created.  Hash table is open, i.e. in case of "hash
 * collision", a single bucket stores multiple entries, which must be searched
 * sequentially.  Load factor indicates number of entries that are
 * allowed in hash table before its capacity is automatically increased.
 * When the number of entries in the hashtable exceeds the product of the load
 * factor and the current capacity, the capacity is increased by calling the
 * rehash method.<p>
 *
 * Generally, the default load factor (.75) offers a good tradeoff between
 * time and space costs.  Higher values decrease the space overhead but
 * increase the time cost to look up an entry (which is reflected in most
 * Hashtable operations, including get and put).<p>
 *
 * The initial capacity controls a tradeoff between wasted space and the
 * need for rehash operations, which are time-consuming.
 * Rehash operations will not occur if the initial
 * capacity is greater than the maximum number of entries the
 * Hashtable will contain divided by its load factor. However,
 * setting the initial capacity too high can waste space.<p>
 *
 * Creating Hashtable with large capacity may be appropriate
 * if many entries are to be inserted into it.
 * Thus entries may be inserted more efficiently than if
 * automatic rehashing is performed when table resizing is necessary.<p>
 *
 */

public class HashMapLongObject<V> {

  static final long serialVersionUID = 1660087326306111301L;
  /**
   * Default load factor for the hashtable.<p>
   */
  public static final float LOAD_FACTOR = 0.75f;
  /**
   * Default initial capacity for the hashtable.<p>
   */
  public static final int INITIAL_CAPACITY = 13;
  /**
   * Default grow step (newSize = oldSize * growStep) for the hashtable.<p>
   */
  public static final int GROW_STEP = 2;
  /**
   * Last element in the list.<p>
   */
  protected static final int LAST = -1;
  /**
   * Static exception for better performance.<p>
   */
  
  public static long collisions = 0; 
  
  public static long puts = 0;
  
  /**
   * Grow step (newSize = oldSize * growStep).<p>
   */
  protected int growStep;
  /**
   * Grow step in the simple number database.<p>
   */
  protected int growSimpl;
  /**
   * Current loaf factor (0.0, 1.0].<p>
   */
  protected float loadFactor;

  /**
   * Index in the simple number database.<p>
   */
  protected int simplIndex;
  /**
   * Limit for the hash table (limit = capacity * loadFactor).<p>
   */
  protected int limit;
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
  transient protected V[] elements;
  /**
   * Pointer to next slot in case of collision.<p>
   */
  transient protected int nextPtr[];

  protected int count;

  /**
   * Constructs a new, empty hashtable with a default capacity grow step and load
   * factor. which are 13, 2, .75.<p>
   */
  public HashMapLongObject() {
    this(INITIAL_CAPACITY, GROW_STEP, LOAD_FACTOR);
  }

  /**
   * Constructs a new, empty hashtable with a specific capacity and default capacity grow step and load
   * factor. which are 2, .75.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   */
  public HashMapLongObject(int initialCapacity) {
    this(initialCapacity, GROW_STEP, LOAD_FACTOR);
  }

  /**
   * Constructs a new, empty hashtable with specified initial
   * capacity, grow step and load factor.<p>
   *
   * @param   initialCapacity the specified initial capacity of the hashtable.
   * @param   growStep  grow step of the hashtable.
   * @param   loadFactor  load factor of hashtable.
   * @param   hasher a hash function.
   */
  public HashMapLongObject(int initialCapacity, int growStep, float loadFactor) {
    if ((loadFactor > 1.0) || (loadFactor <= 0)) {
      throw new IllegalArgumentException("Load Factor = " + loadFactor);
    }

    if (growStep <= 1) {
      throw new IllegalArgumentException("Grow step = " + growStep);
    }

    this.growStep = growStep;

    if (growStep == 2) {
      growSimpl = 4;
    } else if (growStep < 10) {
      growSimpl = growStep + 4;
    } else {
      growSimpl = 13;
    }

    this.loadFactor = loadFactor;
    simplIndex = 0;
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
  public Object[] getAllValues() {
    int index = 0;
    Object[] result = new Object[count];

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        result[index++] = elements[pos - capacity];
      } 
    } 

    return result;
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
    HashMapLongObject result = null;
		try {
			result = (HashMapLongObject) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    result.keys = new long[keys.length];
    result.elements = new Object[elements.length];
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

    if (!(object instanceof HashMapLongObject)) {
      return false;
    }

    HashMapLongObject t = (HashMapLongObject) object;

    if (t.count != count) {
      return false;
    }

    int index;
    Object temp;

    for (int i = 0; i < capacity; i++) {
      for (int pos = nextPtr[i]; pos != LAST; pos = nextPtr[pos]) {
        index = pos - capacity;
        temp = t.get(keys[index]);

        if (temp == null) {
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
	
	private int arrayHashCode(Object[] array) {
		  if (array == null) {
		    return 0;
		  }
		  int result = 17;
		  for (int i = 0; i < array.length; i++) {
		    result = 37 * result + array[i].hashCode();
		  }  
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
	
	
  /**
   * Reduces the size of hash table.
   * Shrink factor shows how much elements can be added before rehash.<p>
   *
   * For example:<p>
   *  If shrink factor is 1.0 then even one element can cause rehash.
   *  If shrink factor is 0.5 and set has 100 elements,
   *  100 more elements can be put before rehash.<p>
   *
   *  default shrinkFactor = 0.75.<p>
   */
  public void shrink() {
    shrink(LOAD_FACTOR);
  }

  /**
   * Reduces the size of hashtable.
   * Shrink factor shows how much elements can be added before rehash.<p>
   *
   * For example:<p>
   *  If shrink factor is 1.0 then even one element can cause rehash.
   *  If shrink factor is 0.5 and set has 100 elements,
   *  100 more elements can be put before rehash.<p>
   *
   * @param   shrinkFactor the shrink factor.
   */
  public void shrink(float shrinkFactor) {
  	throw new IllegalStateException();
  }

  /**
   * Maps the specified key to the specified value in this hashtable.
   *
   * The value can be retrieved by calling the get() method with a key that
   * is equal to the original key.<p>
   *
   * @param   key hashtable key.
   * @param   value value that key is to be mapped to.
   * @return  true if key has been mapped in this hashtable (old value is replaced),
   *          false otherwise (new entry has created).
   */
  
  public boolean put(long key, V value) {
  	
  	puts++;
  	
  	if (value == null) {
  		throw new IllegalStateException("value=" + value);
  	}
  	
    if (count == limit) {
      rehash();
    }

    int pos = hash(key) % capacity;

    int index;

    while (nextPtr[pos] != LAST) {
    	collisions++;
      pos = nextPtr[pos];
      index = pos - capacity;

      if (keys[index] == key) {
        elements[index] = value;
        return true;
      }
    }

    index = nextFree - capacity;
    nextPtr[pos] = nextFree;
    keys[index] = key;
    elements[index] = value;
    nextFree = nextPtr[nextFree];
    nextPtr[nextPtr[pos]] = LAST;
    count++;
    return false;
  }

  /**
   * Put method for internal use. Not  and does not perform check for
   * overflow.<p>
   *
   * @param   key hashtable key.
   * @param   value value that key is to be mapped to.
   */
  protected void putQuick(long key, V value) {
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

  public V get(long key) {
    int index;
    int pos = nextPtr[hash(key) % capacity];
    
    while (pos != LAST) {
      index = pos - capacity;

      if (keys[index] == key) {
        return elements[index];
      }

      pos = nextPtr[pos];
    }

    return null;
  }
  
  /**
   * Removes the key (and its corresponding value) from this
   * hashtable. This method does nothing if the key is not in the hashtable.<p>
   *
   * @param   key the key that needs to be removed.
   * @return  true if key has been mapped in this hashtable,
   *          false otherwise.
   */
  public boolean remove(long key) {
    int prevPos = hash(key) % capacity;
    int pos = nextPtr[prevPos];

    while (pos != LAST) {
      if (keys[pos - capacity] == key) {
        nextPtr[prevPos] = nextPtr[pos];
        nextPtr[pos] = nextFree;
        nextFree = pos;
        count--;
        return true;
      }

      prevPos = pos;
      pos = nextPtr[pos];
    }

    return false;
  }

  /**
   * Initial data structure for use.<p>
   *
   * @param   initialCapacity specified capacity.
   */
  protected void init(int initialCapacity) {
    if (growStep > 17) {
      this.capacity = (int) PrimeGenerator.getClosestPrime(initialCapacity);
    } else {
      long l = PrimeGenerator.getClosestPrime(initialCapacity, simplIndex);
      simplIndex = (int) (l >> 32) + growSimpl;
      this.capacity = (int) l;
    }

    limit = (int) (capacity * loadFactor);
    nextPtr = new int[capacity + limit];

    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    keys = new long[limit];
    elements = (V[]) new Object[limit];
    
    nextFree = capacity;
    count = 0;
  }

  /**
   * Increases the capacity of this hashtable and internally reorganizes
   * it to accommodate and access its entries more efficiently.
   * This method is called automatically when the
   * number of keys in the hashtable exceeds this hashtable capacity
   * and load factor.<p>
   */
  protected void rehash() {
    long[] oldKeys = keys;
    V[] oldElements = elements;
    init(capacity * growStep);

    for (int i = 0; i < oldKeys.length; i++) {
      putQuick(oldKeys[i], oldElements[i]);
    } 
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
        stream.writeObject(elements[index]);
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
    nextPtr = new int[capacity + limit];

    for (int i = 0; i < capacity; i++) {
      nextPtr[i] = LAST;
    } 

    for (int i = capacity; i < nextPtr.length;) {
      nextPtr[i] = ++i;
    } 

    keys = new long[limit];
    elements = (V[]) new Object[limit];
    nextFree = capacity;
    int size = count;
    count = 0;

    for (int i = 0; i < size; i++) {
      putQuick(stream.readLong(), (V) stream.readObject());
    } 
  }

  /*private static int hash(long key) {
		return (int) key;
	}*/
  
  public static int hash(long key) {
  	//int hash = (int) (key & 0x7fffffff);
  	//System.out.println(hash);
    //return hash;
  	return (int) (key & 0x7fffffff);
  }
  
}


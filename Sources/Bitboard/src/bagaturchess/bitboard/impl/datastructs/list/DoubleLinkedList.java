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
package bagaturchess.bitboard.impl.datastructs.list;

import bagaturchess.bitboard.common.Properties;

public class DoubleLinkedList<V> {
	
	ListNodeObject<V> first;
	ListNodeObject<V> last;
	
	int maxSize;
	int curSize;
	
	/**
	 * Statistics
	 */
	long garbage;
	
	public DoubleLinkedList(int _maxSize) {
		maxSize = _maxSize;
		curSize = 0;
	}
	
	public void moveToHead(ListNodeObject<V> node) {
		if (node != first) {
			if (node == last) {
				last = node.prev;
			}
			node.bypassNeighbours();
			addHead(node);
		}
	}
	
	public ListNodeObject<V> addHead(long key, ListNodeObject<V> node) {
		curSize++;
		if (curSize > maxSize) {
			throw new IllegalStateException("curSize " + curSize + ", maxSize " + maxSize);
		}
		return addHead(node);
	}
	
	public ListNodeObject<V> addHead(long key, Object value) {
		curSize++;
		if (curSize > maxSize) {
			throw new IllegalStateException("curSize " + curSize + ", maxSize " + maxSize);
		}
		return addHead(new ListNodeObject(key, value));
	}
	
	private ListNodeObject<V> addHead(ListNodeObject<V> node) {
		ListNodeObject<V> firstNode = first;
		first = node;
		if (firstNode != null) {
			first.next = firstNode;
			firstNode.prev = first;
		} else {
			if (last != null) {
				throw new IllegalStateException("last != null");
			}
			last = first;
		}
		return first;
	}
	
	public ListNodeObject<V> removeLast() {
		ListNodeObject<V> removed = null;
		if (last != null) {
			curSize--;
			garbage++;
			removed = last;
			last = removed.prev;
			if (last != null) {
				last.next = null;
			} else {
				first = null;
			}
			removed.prev = null;
		} else {
			if (curSize != 0) {
				throw new IllegalStateException("curSize != 0");
			}
			if (first != null) {
				throw new IllegalStateException("first != null");
			}
		}
		
		return removed;
	}
	

	public ListNodeObject<V> removeHead() {
		
		ListNodeObject<V> removed = null;
		if (first != null) {
			curSize--;
			garbage++;
			removed = first;
			first = removed.next;
			if (first != null) {
				first.prev = null;
			} else {
				last = null;
			}
			removed.next = null;
		} else {
			if (curSize != 0) {
				throw new IllegalStateException("curSize != 0 " + curSize);
			}
			if (first != null) {
				throw new IllegalStateException("first != null");
			}
		}
		
		return removed;
	}
	
	public ListNodeObject<V> lastToHead() {
		ListNodeObject<V> newFirst = last;
		
		if (Properties.DEBUG_MODE) {
			if (last == null && first != null)
				throw new IllegalStateException();
			if (last != null && first == null)
				throw new IllegalStateException();
			if (last.next != null) {
				throw new IllegalStateException();
			}
			if (first.prev != null) {
				throw new IllegalStateException();
			}
			
			ListNodeObject<V> cur1 = first;
			ListNodeObject<V> cur_back1 = null;
			while (cur1 != null) {
				cur_back1 = cur1;
				cur1 = cur1.next;
			}			
			if (cur_back1 != last) {
				throw new IllegalStateException("last is not cur_backup");
			} else {
				//System.out.println("pinok3");
			}

			ListNodeObject<V> cur2 = last;
			ListNodeObject<V> cur_back2 = null;
			while (cur2 != null) {
				cur_back2 = cur2;
				cur2 = cur2.prev;
			}			
			if (cur_back2 != first) {
				throw new IllegalStateException("first is not cur_backup");
			} else {
				//System.out.println("pinok3");
			}

		}
		
		if (last != null && last != first) {
			ListNodeObject<V> prevLast = last.prev;
			prevLast.next = null;//npe
			last.prev = null;
			if (prevLast == first) { //Special case with list of 2 nodes
				first.next = null;
				first.prev = newFirst;
				newFirst.next = first;
				last = first;
				first = newFirst;
			} else { //Standard case - more than 2 nodes in the list
				if (Properties.DEBUG_MODE) {
					if (prevLast == null)
						throw new IllegalStateException();
				}
				
				last = prevLast;
				newFirst.next = first;
				first.prev = newFirst;
				first = newFirst;
			}
		}
		
		return newFirst;
	}
	
	public int size() {
		return curSize;
	}
}

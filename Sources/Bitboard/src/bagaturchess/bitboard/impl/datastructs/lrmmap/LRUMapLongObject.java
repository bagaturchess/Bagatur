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
package bagaturchess.bitboard.impl.datastructs.lrmmap;


import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.common.Properties;
import bagaturchess.bitboard.impl.datastructs.HashMapLongObject;
import bagaturchess.bitboard.impl.datastructs.IValuesVisitor_HashMapLongObject;
import bagaturchess.bitboard.impl.datastructs.list.DoubleLinkedList;
import bagaturchess.bitboard.impl.datastructs.list.ListNodeObject;


public class LRUMapLongObject<T> {
	
	
	private int FACTOR = 1;//2;
	
	
	private IBinarySemaphore semaphore;
	
	private DataObjectFactory<T> factory;
	protected int maxSize;
	protected int curSize;
	protected DoubleLinkedList<T> list;
	protected HashMapLongObject<ListNodeObject<T>> map;
	
	private boolean full = false;
	private boolean disabeCreation;
	
	private long gets = 0;
	private long gets_ok = 0;
	private double hitrate = 0;
	
	
	protected LRUMapLongObject(DataObjectFactory<T> _factory, int _maxSize, boolean fillWithDummyEntries,
			IBinarySemaphore _semaphore) {
		this(_factory, _maxSize, fillWithDummyEntries, _semaphore, false);
	}
	
	protected LRUMapLongObject(DataObjectFactory<T> _factory, int _maxSize, boolean fillWithDummyEntries,
			IBinarySemaphore _semaphore, boolean _disabeCreation) {
		initBySize(_factory, _maxSize);
		if (fillWithDummyEntries) fillWithDummyEntries(_maxSize);
		
		semaphore = _semaphore;
		disabeCreation = _disabeCreation;
	}
	
	public void visitValues(final IValuesVisitor_HashMapLongObject<T> visitor) {
		
		IValuesVisitor_HashMapLongObject<ListNodeObject<T>> nodesVisitor = new IValuesVisitor_HashMapLongObject<ListNodeObject<T>>() {
			@Override
			public void visit(ListNodeObject<T> value) {
				visitor.visit(value.getValue());
			}
		};
		
		map.visitValues(nodesVisitor);
	}
	
	private void initBySize(DataObjectFactory<T> _factory, int _maxSize) {
		
		if (_maxSize <= 0) {
			throw new IllegalStateException("Non-positive maxSize=" + maxSize);
		}
		
		factory = _factory;
		maxSize = _maxSize;
		curSize = 0;
		
		list = new DoubleLinkedList<T>(maxSize);
		map = new HashMapLongObject<ListNodeObject<T>>((int) FACTOR * maxSize);
	}
	
	
	private void fillWithDummyEntries(int count) {
		for (int i=0; i<count; i++) {
			associateEntry((long)(Long.MAX_VALUE * Math.random()));
		}
	}
	
	
	public void lock() {
		semaphore.lock();
	}
	
	
	public void unlock() {
		semaphore.unlock();
	}
	
	
	/*public void lock() {
		lock.writeLock().lock();
	}
	
	
	public void unlock() {
		lock.writeLock().unlock();
	}*/
	
	
	/*protected boolean remove(long key) {
		ListNodeObject<T> node = map.get(key);
		if (node != null) {
			map.remove(key);
			list.remove(node);
			
			curSize--;
			
			return true;
		}
		return false;
	}*/
	
	
	protected void addHeadEntry(ListNodeObject<T> node) {
		
		curSize++;
		if (curSize >= maxSize) {
			full = true;
		}
		
		
		map.put(node.getKey(), node);
		list.addHead(node.getKey(), node);
	}
	
	
	protected ListNodeObject<T> removeHeadEntry() {
		
		if (list.size() != curSize) {
			throw new IllegalStateException("list.size()=" + list.size() + ", curSize=" + curSize);
		}
		
		ListNodeObject<T> node = list.removeHead();
		if (node != null) {
			map.remove(node.getKey());
			
			curSize--;
			if (curSize < maxSize) {
				full = false;
			}
			
			node.clearNeighbours();
			
			return node;	
		}
		
		return null;
	}
	
	
	protected T get(long key) {
		ListNodeObject<T> node = map.get(key);
		if (node != null) {
			return node.getValue();	
		}
		return null;
	}
	
	
	protected T getAndUpdateLRU(long key) {
		ListNodeObject<T> node = (ListNodeObject<T>) map.get(key);
		
		gets++;
		if (node != null) {
			gets_ok++;
			hitrate = gets_ok / (double) gets;
			
			list.moveToHead(node);
			return node.getValue();
		}
		return null;
	}
	
	public int getHitRate() {
		return (int) (100 * hitrate);
	}
	
	public int getUsage() {
		return (int) (100 * (curSize / (double) maxSize));
	}
	
	
	protected T associateEntry(long key) {
		
		if (Properties.DEBUG_MODE) {
			ListNodeObject<T> node = (ListNodeObject<T>) map.get(key);
			if (node != null) {
				throw new IllegalStateException("Entry with key " + key + " already exists");
			}
			
			if (curSize > maxSize) {
				throw new IllegalStateException("curSize=" + curSize + ", maxSize=" + maxSize);
			}
			
			if (map.size() != curSize) {
				throw new IllegalStateException("curSize=" + curSize + ", map.size()=" + map.size());
			}
			if (list.size() != curSize) {
				throw new IllegalStateException("curSize=" + curSize + ", list.size()=" + list.size());
			}
		}
		
		if (!full) {
			
			if (!disabeCreation) {
				curSize++;
				if (curSize >= maxSize) {
					full = true;
				}
				
				T data = factory.createObject();
				ListNodeObject<T> newNode = list.addHead(key, data);
				map.put(key, newNode);
				
				return data;
			} else {
				return null;
			}
			
		} else {
			
			ListNodeObject<T> entry = list.lastToHead();
			long hashkey = entry.getKey();
			map.remove(hashkey);
			entry.setKey(key);
			map.put(key, entry);
			
			return entry.getValue();
		}
	}
	
	public void clear() {
		curSize = 0;
		list = new DoubleLinkedList<T>(maxSize);
		map = new HashMapLongObject<ListNodeObject<T>>((int) FACTOR * maxSize);
		full = false;
	}
	
	public int getCurrentSize() {
		return curSize;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
}

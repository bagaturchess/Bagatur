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
package bagaturchess.search.impl.pv;


public class PVManager {
	
	
	private static int SHIFT = 0;
	
	private final int maxdepth;
	private final PVNode[] pvs;
	
	
	public PVManager(int _maxdepth) {
		maxdepth = _maxdepth;
		
		pvs = new PVNode[maxdepth];
		
		for (int i=0; i<pvs.length - SHIFT; i++) {
			
			pvs[i] = new PVNode();
			
			PVNode cur = pvs[i];
			for (int count=0; count < maxdepth - i - 1 - SHIFT; count++) {
				PVNode newNode = new PVNode();
				newNode.parent = cur;
				cur.child = newNode;
				
				cur = newNode;
			}
		}
	}
	
	
	public boolean isConnectedToTheRoot(int depth) {
		PVNode node = pvs[depth];
		while (node != null) {
			if (node == pvs[0]) {
				return true;
			}
			node = node.parent;
		}
		return false;
	}
	
	
	public PVNode load(int depth) {
		return pvs[depth];
	}
	
	
	public void store(int depth, PVNode parent, PVNode newChild, boolean checkRef) {
				
		if (checkRef && newChild != pvs[depth]) {
			throw new IllegalStateException();
		}
		
		PVNode oldChild = parent.child;
		parent.child = newChild;
		newChild.parent = parent;
		
		oldChild.parent = null;
		pvs[depth] = oldChild;
		
		//validate();
	}
	
	
	private void validate() {
		for (int i=0; i<pvs.length - SHIFT; i++) {
			
			PVNode test = pvs[i];
			if (test.parent != null) {
				throw new IllegalStateException();
			}
			for (int count=0; count < maxdepth - i - 1 - SHIFT; count++) {
				
				PVNode child = test.child;
				if (child.parent != test) {
					throw new IllegalStateException();
				}

				test = test.child;
			}
		}
	}
	
	
	public static void main(String[] args) {
		PVManager pvm = new PVManager(10);
		pvm.validate();
		System.out.println(pvm);
	}
}

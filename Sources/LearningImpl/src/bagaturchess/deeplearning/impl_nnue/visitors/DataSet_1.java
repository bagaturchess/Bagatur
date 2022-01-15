/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.deeplearning.impl_nnue.visitors;


import java.util.ArrayList;
import java.util.List;

import javax.visrec.ml.data.Column;
import javax.visrec.ml.data.DataSet;

import deepnetts.data.MLDataItem;
import deepnetts.data.TabularDataSet;
import deepnetts.util.Tensor;


public class DataSet_1 implements DataSet<MLDataItem> {
	
	
	private List<MLDataItem> items;
	private String[] targetNames;
	
	
	public DataSet_1() {
		
		items = new ArrayList<MLDataItem>();
		
		targetNames = new String[13];
		for (int i = 0; i < targetNames.length; i++) {
			targetNames[i] = "LABEL" + i;
		}
	}
	
	
	public void addItem(Object inputs, float[] outputs) {
		if (inputs.getClass().equals(float[][][].class)) {
			items.add(new TabularDataSet.Item(new Tensor((float[][][])inputs), new Tensor(outputs)));
		} else if (inputs.getClass().equals(float[][].class)) {
			items.add(new TabularDataSet.Item(new Tensor((float[][])inputs), new Tensor(outputs)));
		} else {
			throw new IllegalStateException();
		}
	}
	
	
	@Override
	public List<MLDataItem> getItems() {
		return items;
	}
	
	
	@Override
	public DataSet<MLDataItem>[] split(double... parts) {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public List<Column> getColumns() {
		throw new UnsupportedOperationException();
	}


	@Override
	public String[] getTargetColumnsNames() {
		return targetNames;
	}


	@Override
	public void setColumns(List<Column> arg0) {
		throw new UnsupportedOperationException();
	}


	/*@Override
	public String[] getColumnNames() {
		throw new UnsupportedOperationException();
	}


	@Override
	public String[] getTargetNames() {
		return targetNames;
	}


	@Override
	public void setColumnNames(String[] arg0) {
		throw new UnsupportedOperationException();
	}*/
}

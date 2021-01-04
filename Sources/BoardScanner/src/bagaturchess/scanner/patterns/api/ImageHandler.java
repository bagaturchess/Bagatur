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
package bagaturchess.scanner.patterns.api;


import java.io.IOException;


/**
 * Handles porting to awt and to android with different implementations
 */
public interface ImageHandler<T1, T2> {
	public T1 loadImageFromFS(T2 path) throws IOException;
	public T1 resizeImage(T1 source, int newsize);
	public void saveImage(String fileName, String formatName, T1 image) throws IOException;
	public int[][] convertToGrayMatrix(T1 image);
	public T1 createGrayImage(int[][] matrix);
	public T1 loadPieceImageFromMemory(int pid, String piecesSetName, int size);
}

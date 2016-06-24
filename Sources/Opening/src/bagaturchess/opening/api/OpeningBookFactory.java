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
package bagaturchess.opening.api;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import bagaturchess.bitboard.impl.Figures;


public class OpeningBookFactory {
	
	
	private static final int BUFFER_SIZE = 1024 * 1024;
	
	
	private static OpeningBook ob;
	
	
	public static OpeningBook getBook() {
		return ob;
	}
	
	
	public static OpeningBook initBook(InputStream is_w, InputStream is_b) {
		if (ob == null) {
			synchronized (OpeningBookFactory.class) {
				if (ob == null) {
					try {
						ob = OpeningBookFactory.load(is_w, is_b);
					} catch (Throwable t) {
						throw new IllegalStateException("No book");
					}
				}
			}
		}
		return ob;
	}
	
	public static OpeningBook load(String inFilePathName) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(inFilePathName), BUFFER_SIZE));
		OpeningBook result = (OpeningBook) is.readObject();
		return result;
	}
	
	
	@SuppressWarnings("unused")
	private static OpeningBook load_ResourceAsStream(String inFileName) throws FileNotFoundException, IOException, ClassNotFoundException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream is = cl.getResourceAsStream(inFileName);
		if (is != null) {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is, BUFFER_SIZE));
			OpeningBook result = (OpeningBook) ois.readObject();
			return result;
		}
		return null;
	}
	
	private static OpeningBook load_FileSystem(InputStream is) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is, BUFFER_SIZE));
		OpeningBook result = (OpeningBook) ois.readObject();
		return result;
	}
	
	public static OpeningBook load(InputStream is_w, InputStream is_b) throws FileNotFoundException, IOException, ClassNotFoundException {
		OpeningBook white = load_FileSystem(is_w);
		OpeningBook black = load_FileSystem(is_b);
		if (white != null && black != null) {
			return new OpeningBookWithBothPlayersImpl(white, black);
		} else {
			return null;
		}
	}
	
	public static OpeningBook load(String whiteFileName, String blackFileName) throws FileNotFoundException, IOException, ClassNotFoundException {
		OpeningBook white = load(whiteFileName);
		OpeningBook black = load(blackFileName);
		if (white != null && black != null) {
			return new OpeningBookWithBothPlayersImpl(white, black);
		} else {
			return null;
		}
	}
	
	private static class OpeningBookWithBothPlayersImpl implements OpeningBook {

		
		private static final long serialVersionUID = 8664931488023105907L;
		
		private OpeningBook whiteOpening;
		private OpeningBook blackOpening;
		
		
		public OpeningBookWithBothPlayersImpl(OpeningBook _whiteOpening, OpeningBook _blackOpening) {
			whiteOpening = _whiteOpening;
			blackOpening = _blackOpening;
		}
		
		public int get(long hashkey, int colour) {
			if (colour == Figures.COLOUR_WHITE) {
				return whiteOpening.get(hashkey, Figures.COLOUR_WHITE);
			} else if (colour == Figures.COLOUR_BLACK) {
				return blackOpening.get(hashkey, Figures.COLOUR_BLACK);
			} else {
				throw new IllegalStateException();
			}
		}

		//public int size() {
		//	return whiteOpening.size() + blackOpening.size();
		//}

		/*public void unload() {
			whiteOpening.unload();
			blackOpening.unload();
		}*/

		/*public OpeningEntry getEntry(long hashkey, int colour) {
			if (colour == Figures.COLOUR_WHITE) {
				return whiteOpening.getEntry(hashkey, Figures.COLOUR_WHITE);
			} else if (colour == Figures.COLOUR_BLACK) {
				return blackOpening.getEntry(hashkey, Figures.COLOUR_BLACK);
			} else {
				throw new IllegalStateException();
			}
		}*/

		public void store(String outFileName) {
			throw new UnsupportedOperationException();
		}

		public void add(long hashkey, int move) {
			throw new UnsupportedOperationException();
		}

		public IOpeningEntry getEntry(long hashkey, int colour) {
			if (colour == Figures.COLOUR_WHITE) {
				return whiteOpening.getEntry(hashkey, Figures.COLOUR_WHITE);
			} else if (colour == Figures.COLOUR_BLACK) {
				return blackOpening.getEntry(hashkey, Figures.COLOUR_BLACK);
			} else {
				throw new IllegalStateException();
			}
		}

		public int[][] getAllMovesAndCounts(long hashkey, int colour) {
			if (colour == Figures.COLOUR_WHITE) {
				return whiteOpening.getAllMovesAndCounts(hashkey, Figures.COLOUR_WHITE);
			} else if (colour == Figures.COLOUR_BLACK) {
				return blackOpening.getAllMovesAndCounts(hashkey, Figures.COLOUR_BLACK);
			} else {
				throw new IllegalStateException();
			}
		}

		@Override
		public void add(long hashkey, int move, int result) {
			throw new UnsupportedOperationException();
		}
	}
}

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
package bagaturchess.bitboard.common;

import bagaturchess.bitboard.impl.Constants;


public class Fen extends Constants {
	
	
	//Active colour
	private int colourToMove;
	
	//Castling availability
	private boolean whiteKingSide = false;
	private boolean whiteQueenSide = false;
	private boolean blackKingSide = false;
	private boolean blackQueenSide = false;
	
	private String enpassantTargetSquare;
	private String halfmoveClock;
	private String fullmoveNumber;
	
	private int[] board = new int[64];
	
	public static final Fen parse(String fenStr) {
		return new Fen(fenStr);
	}
	
	public Fen(String fen) {
		
		StringBuilder buffer = new StringBuilder(fen);
		
		parse(buffer);
	}
	
	private void parse(StringBuilder buffer) {
		int endIndex = parsePiecePlacement(buffer);
		
		//Active colour
		endIndex++;
		if (endIndex >= buffer.length()) {
			throw new IllegalStateException("Invalid fen: no active colour field");
		}
		
		char cur = buffer.charAt(endIndex);
		if (cur == 'w') {
			colourToMove = Constants.COLOUR_WHITE;
		} else if (cur == 'b') {
			colourToMove = Constants.COLOUR_BLACK;
		} else {
			throw new IllegalStateException("Invalid fen: char='" + cur + "' in active colour field");
		}
		
		//Castling availability
		endIndex++;
		endIndex++;
		if (endIndex < buffer.length()) {
			cur = buffer.charAt(endIndex);
			if (cur != '-') {
				while(endIndex < buffer.length() && (cur = buffer.charAt(endIndex)) > 32) {
					switch (cur) {
						case 'K':
							whiteKingSide = true;
							break;
						case 'Q':
							whiteQueenSide = true;
							break;
						case 'k':
							blackKingSide = true;
							break;
						case 'q':
							blackQueenSide = true;
							break;
						default:
							throw new IllegalStateException("Invalid fen: char='" + cur + "' in castling availability field");
					}
					endIndex++;
				}
			} else {
				endIndex++;
			}
			
			//En passant target square
			endIndex++;
			if (endIndex < buffer.length()) {
				int startIndex = endIndex;
				cur = buffer.charAt(endIndex);
				
				if (cur != '-') {
					while(endIndex < buffer.length() && (cur = buffer.charAt(endIndex)) > 32) {
						endIndex++;
					}
					
					enpassantTargetSquare = buffer.substring(startIndex, endIndex);
				} else {
					endIndex++;
				}
				
				//Halfmove clock
				endIndex++;
				if (endIndex < buffer.length()) {
					startIndex = endIndex;
					cur = buffer.charAt(endIndex);
					
					if (cur != '-') {
						while(endIndex < buffer.length() && (cur = buffer.charAt(endIndex)) > 32) {
							endIndex++;
						}
						
						halfmoveClock = buffer.substring(startIndex, endIndex);
					}
					
					//Fullmove number
					endIndex++;
					if (endIndex < buffer.length()) {
						startIndex = endIndex;
						cur = buffer.charAt(endIndex);
						
						if (cur != '-') {
							while(endIndex < buffer.length() && (cur = buffer.charAt(endIndex)) > 32) {
								endIndex++;
							}
							
							fullmoveNumber = buffer.substring(startIndex, buffer.length()).trim();
						}
					}
				}
			}			
		}
		//System.out.println(cur);
	}

	private int parsePiecePlacement(StringBuilder buffer) {
		//piece placement
		int letter = 0;
		int digit = 7;
		int endIndex = 0;
		char cur;
		while((cur = buffer.charAt(endIndex)) > 32) {
			if (cur == '/') {
				digit--;
				letter = 0;
				endIndex++;
				if (endIndex >= buffer.length()) {
					throw new IllegalStateException("Invalid fen: no space char after data field");
				}
				continue;
			}
			
			if (Character.isDigit(cur)) {
				switch (cur) {
					case '0':
						throw new IllegalStateException("Invalid fen: char='" + cur + "' in data field");
					case '1':
						letter += 1;
						break;
					case '2':
						letter += 2;
						break;
					case '3':
						letter += 3;
						break;
					case '4':
						letter += 4;
						break;
					case '5':
						letter += 5;
						break;
					case '6':
						letter += 6;
						break;
					case '7':
						letter += 7;
						break;
					case '8':
						letter += 8;
						break;
					default:
						throw new IllegalStateException("Invalid fen: char='" + cur + "' in data field");
				}
			} else if (Character.isLetter(cur)) {
				
				int l = letter;
				int d = digit;
				
				//int square = 8 * (7 - d) + l;
				int square = 8 * d + l;
				
					switch (cur) {
						case 'p':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_BLACK][Constants.TYPE_PAWN];
							letter++;
							break;
						case 'n':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_BLACK][Constants.TYPE_KNIGHT];
							letter++;
							break;
						case 'b':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_BLACK][Constants.TYPE_BISHOP];
							letter++;
							break;
						case 'r':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_BLACK][Constants.TYPE_ROOK];
							letter++;
							break;
						case 'q':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_BLACK][Constants.TYPE_QUEEN];
							letter++;
							break;
						case 'k':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_BLACK][Constants.TYPE_KING];
							letter++;
							break;
						case 'P':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_WHITE][Constants.TYPE_PAWN];
							letter++;
							break;
						case 'N':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_WHITE][Constants.TYPE_KNIGHT];
							letter++;
							break;
						case 'B':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_WHITE][Constants.TYPE_BISHOP];
							letter++;
							break;
						case 'R':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_WHITE][Constants.TYPE_ROOK];
							letter++;
							break;
						case 'Q':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_WHITE][Constants.TYPE_QUEEN];
							letter++;
							break;
						case 'K':
							board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[Constants.COLOUR_WHITE][Constants.TYPE_KING];
							letter++;
							break;
						default:
							throw new IllegalStateException("Invalid fen: char='" + cur + "' in data field");
					}
			} else {
				throw new IllegalStateException("Invalid fen: char='" + cur + "' in data field");
			}
			
			endIndex++;
			
			if (endIndex >= buffer.length()) {
				throw new IllegalStateException("Invalid fen: no space char after data field");
			}
		}
		return endIndex;
	}
	
	public boolean hasBlackKingSide() {
		return blackKingSide;
	}

	public boolean hasBlackQueenSide() {
		return blackQueenSide;
	}

	public int[] getBoardArray() {
		return board;
	}

	public int getColourToMove() {
		return colourToMove;
	}

	public boolean hasWhiteKingSide() {
		return whiteKingSide;
	}

	public boolean hasWhiteQueenSide() {
		return whiteQueenSide;
	}
	
	public String getEnpassantTargetSquare() {
		return enpassantTargetSquare;
	}
	
	public String getFullmoveNumber() {
		return fullmoveNumber;
	}

	public String getHalfmoveClock() {
		return halfmoveClock;
	}
	
	public String toString() {
		String result = "";
		result += "active colour   = " + getColourToMove() + "\r\n";
		result += "castling        = " + hasWhiteKingSide() + " " + hasWhiteQueenSide()
							+ " " + hasBlackKingSide() + " " + hasBlackQueenSide() + "\r\n";
		result += "enpassantSquare = '" + getEnpassantTargetSquare() + "'\r\n";
		result += "halfmoveClock   = '" + getHalfmoveClock() + "'\r\n";
		result += "fullmoveNumber  = '" + getFullmoveNumber() + "'\r\n";
		
		return result;
	}
}

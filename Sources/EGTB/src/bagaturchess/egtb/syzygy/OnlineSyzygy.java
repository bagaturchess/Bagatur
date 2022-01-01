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
package bagaturchess.egtb.syzygy;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.utils.VarStatistic;


public class OnlineSyzygy {
	
	
	private static final String CHARSET_ENCODING 			= "UTF-8";
	
	private static long last_server_response_timestamp 		= 0;
	
	private static int MAX_powerof2_for_waiting_time 		= 7;
	
	private static int current_powerof2_for_waiting_time 	= 0;
	
	private final static VarStatistic stat_response_times 	= new VarStatistic();
	
	private final static VarStatistic stat_waiting_times 	= new VarStatistic();
	
	static {
		
		for (int i = 0; i < 10; i++) {
			
			stat_response_times.addValue(111); //9.009009009... requests per second
		}
	}
	
	
	private static final int getWaitingTimeBetweenRequests() {
		
		//Wait between 2 server requests for 2 reasons:
		//1) Response could be "Server returned HTTP response code: 429" and there is no sense to try again.
		//2) There is no sense to send server request if the time per move (which engine/search has) is less than the server response time.
		//2.1) The minimum waiting time (returned by minimalPossibleTime method) is set to the average server response time + its standard deviation. This has to cover more than 75% of the cases successfully.
		//2.2) Increase the waiting time with factor of 2 (multiply it by 2) if there are request limits reached (e.g. 429 errors).
		//2.3) Decrease the waiting time with factor of 2 (divide it by 2) after each successful request/response sequence.
		return (int) (Math.pow(2, current_powerof2_for_waiting_time) * minimalPossibleWaitingTime());
	}


	private static double minimalPossibleWaitingTime() {
		
		return Math.max(15, stat_response_times.getEntropy() + stat_response_times.getDisperse());
	}
	
	
	public static final String getDTZandDTM_BlockingOnSocketConnection(String fen, int colour_to_move, long timeToThinkInMiliseconds, int[] dtz_and_dtm, Logger logger) {
		
		//If we have pending server request than exit
		/*if (current_request_url == null) {
			
			return null;
		}*/
		
		if (timeToThinkInMiliseconds < minimalPossibleWaitingTime() ) {
			
			return null;
		}
		
		if (System.currentTimeMillis() <= last_server_response_timestamp + getWaitingTimeBetweenRequests()) {
			
			return null;
		}
		
		dtz_and_dtm[0] = -1;
		dtz_and_dtm[1] = -1;
		
		last_server_response_timestamp = System.currentTimeMillis();
		
		String bestmove_string = null;
		
		/*Piece placement (from White's perspective). Each rank is described, starting with rank 8 and ending with rank 1; within each rank, the contents of each square are described from file "a" through file "h". Following the Standard Algebraic Notation (SAN), each piece is identified by a single letter taken from the standard English names (pawn = "P", knight = "N", bishop = "B", rook = "R", queen = "Q" and king = "K"). White pieces are designated using upper-case letters ("PNBRQK") while black pieces use lowercase ("pnbrqk"). Empty squares are noted using digits 1 through 8 (the number of empty squares), and "/" separates ranks.
		Active color. "w" means White moves next, "b" means Black moves next.
		Castling availability. If neither side can castle, this is "-". Otherwise, this has one or more letters: "K" (White can castle kingside), "Q" (White can castle queenside), "k" (Black can castle kingside), and/or "q" (Black can castle queenside). A move that temporarily prevents castling does not negate this notation.
		En passant target square in algebraic notation. If there's no en passant target square, this is "-". If a pawn has just made a two-square move, this is the position "behind" the pawn. This is recorded regardless of whether there is a pawn in position to make an en passant capture.[6]
		Halfmove clock: The number of halfmoves since the last capture or pawn advance, used for the fifty-move rule.[7]
		Fullmove number: The number of the full move. It starts at 1, and is incremented after Black's move.*/
		String url_for_the_request = "http://tablebase.lichess.ovh/standard?fen=" + fen;
		
		try {
			
			String server_response_json_text = getHTMLFromURL(url_for_the_request, logger);
			
			logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: json_response_text=" + server_response_json_text);
			
			current_powerof2_for_waiting_time--;
			
			if (current_powerof2_for_waiting_time < 0) {
				
				current_powerof2_for_waiting_time = 0;
			}
			
			stat_waiting_times.addValue(getWaitingTimeBetweenRequests());
			
			logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: current_powerof2_for_waiting_time set to " + current_powerof2_for_waiting_time);
			
			stat_response_times.addValue(System.currentTimeMillis() - last_server_response_timestamp);
			
			logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: stat_waiting_times: AVG=" + stat_waiting_times.getEntropy()
					+ " ms, STDEV=" + stat_waiting_times.getDisperse()
					+ " ms, MAX=" + stat_waiting_times.getMaxVal() + " ms"
				);
			
			logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: stat_response_time: AVG=" + stat_response_times.getEntropy()
					+ " ms, STDEV=" + stat_response_times.getDisperse()
					+ " ms, MAX=" + stat_response_times.getMaxVal() + " ms"
				);
			
			/* Example of server response taken in December 2021
			{
				"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"dtz":9,"precise_dtz":null,"dtm":43,"category":"win",
			
				"moves":
				[
					{"uci":"d1c2","san":"Kc2","zeroing":false,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"dtz":-8,"precise_dtz":null,"dtm":-42,"category":"loss"},
					{"uci":"d1e2","san":"Ke2","zeroing":false,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"dtz":-8,"precise_dtz":null,"dtm":-42,"category":"loss"},
					{"uci":"d2d3","san":"d3","zeroing":true,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"dtz":0,"precise_dtz":0,"dtm":0,"category":"draw"},
					{"uci":"d2d4","san":"d4","zeroing":true,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"dtz":0,"precise_dtz":0,"dtm":0,"category":"draw"},
					{"uci":"d1c1","san":"Kc1","zeroing":false,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"dtz":0,"precise_dtz":0,"dtm":0,"category":"draw"},
					{"uci":"d1e1","san":"Ke1","zeroing":false,"checkmate":false,"stalemate":false,"variant_win":false,"variant_loss":false,"insufficient_material":false,"dtz":0,"precise_dtz":0,"dtm":0,"category":"draw"}
				]
			}*/
			
			
			//Extract DTZ
			String dtz_string = JSONUtils.extractJSONAttribute(logger, server_response_json_text, "\"dtz\":");
			
			if (dtz_string != null && !dtz_string.equals("null")) {
				
				try {
					
					dtz_and_dtm[0] = Integer.parseInt(dtz_string);
					
				} catch (NumberFormatException nfe) {
					
					logger.addException(nfe);
				}
			}
			
			
			//Extract DTM
			String dtm_string = JSONUtils.extractJSONAttribute(logger, server_response_json_text, "\"dtm\":");
			
			if (dtm_string != null && !dtm_string.equals("null")) {
				
				try {
					
					dtz_and_dtm[1] = Integer.parseInt(dtm_string);
					
				} catch (NumberFormatException nfe) {
					
					logger.addException(nfe);
				}
			}
			
			//Possible outcomes are "win", "draw", "loss", "blessed-loss", "cursed-win"
			//Possible outcomes are "win", "unknown", "maybe-win", "cursed-win", "draw", "blessed-loss", "maybe-loss", "loss"
			String game_category_string = JSONUtils.extractJSONAttribute(logger, server_response_json_text, "\"category\":");
			
			if (game_category_string != null && !game_category_string.equals("\"unknown\"")) {
				
				if (game_category_string.equals("\"win\"")
						|| game_category_string.equals("\"blessed-loss\"")
						|| game_category_string.equals("\"draw\"")
						//|| game_category_string.equals("\"cursed-win\"")
						) {
					
					String first_array_string = JSONUtils.extractFirstJSONArray(logger, server_response_json_text);
					
					//System.out.println("first_array_string=" + first_array_string);
					
					String[] array_elements = JSONUtils.extractJSONArrayElements(logger, first_array_string);
					
					if (array_elements.length > 0) {						
						
						String array_element = array_elements[0];
						
						//System.out.println("first_array_element_string=" + array_element);
					
						//The uci moves list is ordered - the first line of the response contains the best move
						bestmove_string = JSONUtils.extractJSONAttribute(logger, array_element, "\"uci\":"); //"uci":"d1c2",
						
						if (bestmove_string != null) {
							
							bestmove_string = bestmove_string.replace("\"", ""); //The value is quoted
							
							logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: bestmove_string=" + bestmove_string);
						}
					}
					
				} else {
					
					logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: skiped category: " + game_category_string);
				}
			}
			
		} catch (Exception e) {
			
			//e is FileNotFoundException if the requested position is not presented on the server
			//e.printStackTrace();
			
			logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: " + e.getMessage());
			
			current_powerof2_for_waiting_time++;
			
			if (current_powerof2_for_waiting_time > MAX_powerof2_for_waiting_time) {
				
				current_powerof2_for_waiting_time = MAX_powerof2_for_waiting_time;
			}
			
			stat_waiting_times.addValue(getWaitingTimeBetweenRequests());
			
			logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: current_powerof2_for_waiting_time set to " + current_powerof2_for_waiting_time);
		}
		
		
		return bestmove_string;
	}
	
	
	public static final String getWDL_BlockingOnSocketConnection(String fen, int colour_to_move, long timeToThinkInMiliseconds, int[] result, Logger logger) {
		
		//If we have pending server request than exit
		/*if (current_request_url == null) {
			
			return null;
		}*/
		
		if (timeToThinkInMiliseconds < minimalPossibleWaitingTime() ) {
			
			return null;
		}
		
		if (System.currentTimeMillis() <= last_server_response_timestamp + getWaitingTimeBetweenRequests()) {
			
			return null;
		}
		
		result[0] = -1;
		result[1] = -1;
		
		last_server_response_timestamp = System.currentTimeMillis();
		
		String bestmove_string = null;
		
		String url_for_the_request_mainline = "http://tablebase.lichess.ovh/standard/mainline?fen=" + fen;
		
		try {
			
			String server_response_json_text = getHTMLFromURL(url_for_the_request_mainline, logger);
			
			logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: server_response_json_text=" + server_response_json_text);
			
			current_powerof2_for_waiting_time--;
			
			if (current_powerof2_for_waiting_time < 0) {
				
				current_powerof2_for_waiting_time = 0;
			}
			
			stat_waiting_times.addValue(getWaitingTimeBetweenRequests());
			
			logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: current_powerof2_for_waiting_time set to " + current_powerof2_for_waiting_time);
			
			stat_response_times.addValue(System.currentTimeMillis() - last_server_response_timestamp);
			
			logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: stat_waiting_times: AVG=" + stat_waiting_times.getEntropy()
					+ " ms, STDEV=" + stat_waiting_times.getDisperse()
					+ " ms, MAX=" + stat_waiting_times.getMaxVal() + " ms"
				);
			
			logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: stat_response_times: AVG=" + stat_response_times.getEntropy()
					+ " ms, STDEV=" + stat_response_times.getDisperse()
					+ " ms, MAX=" + stat_response_times.getMaxVal() + " ms"
				);
			
			/* Example of server response taken in November 2021
			
			{"mainline":
				[
					{"uci":"d1c2","san":"Kc2","dtz":-8},
					{"uci":"d8c7","san":"Kc7","dtz":7},
					{"uci":"c2c3","san":"Kc3","dtz":-6},
					{"uci":"c7b6","san":"Kb6","dtz":5},
					{"uci":"c3c4","san":"Kc4","dtz":-4},
					{"uci":"b6b7","san":"Kb7","dtz":3},
					{"uci":"c4c5","san":"Kc5","dtz":-2},
					{"uci":"b7a6","san":"Ka6","dtz":1},
					{"uci":"d2d3","san":"d3","dtz":-4},
					{"uci":"a6b7","san":"Kb7","dtz":3},
					{"uci":"c5d6","san":"Kd6","dtz":-2},
					{"uci":"b7a6","san":"Ka6","dtz":1},
					{"uci":"d3d4","san":"d4","dtz":-2},
					{"uci":"a6a5","san":"Ka5","dtz":1},
					{"uci":"d4d5","san":"d5","dtz":-4},
					{"uci":"a5a4","san":"Ka4","dtz":3},
					{"uci":"d6c5","san":"Kc5","dtz":-2},
					{"uci":"a4a3","san":"Ka3","dtz":1},
					{"uci":"d5d6","san":"d6","dtz":-2},
					{"uci":"a3a2","san":"Ka2","dtz":1},
					{"uci":"d6d7","san":"d7","dtz":-2},
					{"uci":"a2a1","san":"Ka1","dtz":1},
					{"uci":"d7d8q","san":"d8=Q","dtz":-8},
					{"uci":"a1b1","san":"Kb1","dtz":7},
					{"uci":"d8d2","san":"Qd2","dtz":-6},
					{"uci":"b1a1","san":"Ka1","dtz":5},
					{"uci":"c5b4","san":"Kb4","dtz":-4},
					{"uci":"a1b1","san":"Kb1","dtz":3},
					{"uci":"b4a3","san":"Ka3","dtz":-2},
					{"uci":"b1a1","san":"Ka1","dtz":1},
					{"uci":"d2c1","san":"Qc1#","dtz":-1}
				],
				"winner":"w",
				"dtz":9
			}*/
			
			
			String winner_string = JSONUtils.extractJSONAttribute(logger, server_response_json_text, "\"winner\":");
			
			if (winner_string != null) {
				
				int winner_color = -1;
				
				if (winner_string.equals("\"w\"")) {
					
					winner_color = Constants.COLOUR_WHITE;
					
				} else if (winner_string.equals("\"b\"")) {
					
					winner_color = Constants.COLOUR_BLACK;					
				}
				
				result[0] = winner_color;
				
				if (result[0] == colour_to_move) {
					
					//Here the player is winning the game
					
					String first_array_string = JSONUtils.extractFirstJSONArray(logger, server_response_json_text);
					
					//System.out.println("first_array_string=" + first_array_string);
					
					String[] array_elements = JSONUtils.extractJSONArrayElements(logger, first_array_string);
					
					if (array_elements.length > 0) {						
						
						String array_element = array_elements[0];
						
						//System.out.println("first_array_element_string=" + array_element);
						
						String dtz_string = JSONUtils.extractJSONAttribute(logger, array_element, "\"dtz\":");
						
						if (dtz_string != null) {
							
							try {
								
								int dtz = Integer.parseInt(dtz_string);
								
								//this is the first occurance of dtz string in json response.
								//We have to add one move and switch the sign by multiplying to -1.
								dtz += dtz > 0 ? 1 : -1;
								dtz = -dtz;
								
								result[1] = dtz;
										
							} catch (NumberFormatException nfe) {
								
								logger.addException(nfe);
							}
						}
						
						
						//The uci moves list is ordered - the first line of the response contains the best move
						bestmove_string = JSONUtils.extractJSONAttribute(logger, array_element, "\"uci\":"); //"uci":"d1c2",
						
						if (bestmove_string != null) {
							
							bestmove_string = bestmove_string.replace("\"", ""); //The value is quoted
							
							logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: bestmove_string=" + bestmove_string);
						}
					}
				}
			}
			
		} catch (Exception e) {
			
			//e is FileNotFoundException if the requested position is not presented on the server
			//e.printStackTrace();
			
			logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: " + e.getMessage());
			
			current_powerof2_for_waiting_time++;
			
			if (current_powerof2_for_waiting_time > MAX_powerof2_for_waiting_time) {
				
				current_powerof2_for_waiting_time = MAX_powerof2_for_waiting_time;
			}
			
			stat_waiting_times.addValue(getWaitingTimeBetweenRequests());
			
			logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: current_powerof2_for_waiting_time set to " + current_powerof2_for_waiting_time);
		}
		
		
		return bestmove_string;
	}

	

	
	
	//private static URL current_request_url 				= null;
	
	private static String getHTMLFromURL(String urlToRead, Logger logger) throws Exception {
		
		logger.addText("OnlineSyzygy.getHTMLFromURL: open and read stream of connection " + urlToRead);
		
		URL current_request_url = new URL(urlToRead);
		
		HttpURLConnection conn = (HttpURLConnection) current_request_url.openConnection();
		conn.setConnectTimeout(5 * 60 * 1000); // 0 = Infinite
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Content-Type", "application/json; utf-8");
		conn.setRequestMethod("GET");
		
		byte[] bytes = readAllBytes(conn);
		
		current_request_url = null;
		
		String html = new String(bytes, Charset.forName(CHARSET_ENCODING));

		return html;
	}
	
	
	private static byte[] readAllBytes(HttpURLConnection conn) throws IOException {

		InputStream inputStream = conn.getInputStream();
		
		final int bufLen = 4096;
		
		byte[] buf = new byte[bufLen];
		
		int readLen;

		try {
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
				outputStream.write(buf, 0, readLen);

			return outputStream.toByteArray();

		} catch (IOException e) {
			
			throw e;
			
		} finally {
			
			try {
				
				inputStream.close();
				
			} catch (IOException ioe) {
				
				ioe.printStackTrace();
			}
			
			conn.disconnect();
		}
	}
	
	
	public interface Logger {
		
		public void addText(String message);
		
		public void addException(Exception exception);
	}
	
	
	public static void main(String[] args) {
		
		IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("3k4/8/8/8/8/8/3P4/3K4 w - -");
		//IBitBoard board = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
		
		for (int counter = 0; counter < 100; counter++) {
		
			System.out.println("Try " + (counter + 1));
			
			String fen = board.toEPD().replace(' ', '_');
			
			
			/*int[] winner_and_dtz = new int[2];
			
			String best_move = getWDL_BlockingOnSocketConnection(fen, board.getColourToMove(), 500, winner_and_dtz, new Logger() {
				
				@Override
				public void addText(String message) {
					System.out.println(message);
				}
				
				@Override
				public void addException(Exception exception) {
					exception.printStackTrace();
				}
			});
			
			System.out.println("winner=" + winner_and_dtz[0] + ", best_move=" + best_move + ", dtz=" + winner_and_dtz[1]);*/
			
			
			int[] dtz_and_dtm = new int[2];
			
			String best_move = getDTZandDTM_BlockingOnSocketConnection(fen, board.getColourToMove(), 500, dtz_and_dtm, new Logger() {
				
				@Override
				public void addText(String message) {
					System.out.println(message);
				}
				
				@Override
				public void addException(Exception exception) {
					exception.printStackTrace();
				}
			});
			
			System.out.println("dtz=" + dtz_and_dtm[0] + ", best_move=" + best_move + ", dtm=" + dtz_and_dtm[1]);
			
			
			try {
				
				System.out.println("Waiting " + getWaitingTimeBetweenRequests() + " ms");
				
				Thread.sleep(getWaitingTimeBetweenRequests());
				
			} catch (InterruptedException e) {}
		}
	}
}

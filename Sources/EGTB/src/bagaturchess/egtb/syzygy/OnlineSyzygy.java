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
	
	//private static URL current_request_url 				= null;
	
	
	private static final int getWaitingTimeBetweenRequests() {
		
		//Wait between 2 server requests for 2 reasons:
		//1) Response could be "Server returned HTTP response code: 429" and there is no sense to try again.
		//2) There is no sense to send server request if the time per move (which engine/search has) is less than the server response time.
		//2.1) The minimum waiting time (returned by minimalPossibleTime method) is set to the average server response time + its standard deviation. This has to cover more than 75% of the cases successfully.
		//2.2) Increase the waiting time with factor of 2 (multiply it by 2) if there are request limits reached (e.g. 429 errors).
		//2.3) Decrease the waiting time with factor of 2 (divide it by 2) after each successful request/response sequence.
		return (int) (Math.pow(2, current_powerof2_for_waiting_time) * minimalPossibleTime());
	}


	private static double minimalPossibleTime() {
		
		return Math.max(15, stat_response_times.getEntropy() + stat_response_times.getDisperse());
	}
	
	
	public static final String getDTZandDTM_BlockingOnSocketConnection(String fen, int colour_to_move, long timeToThinkInMiliseconds, int[] result, Logger logger) {
		
		//If we have pending server request than exit
		/*if (current_request_url == null) {
			
			return null;
		}*/
		
		if (timeToThinkInMiliseconds < minimalPossibleTime() ) {
			
			return null;
		}
		
		if (System.currentTimeMillis() <= getWaitingTimeBetweenRequests() + last_server_response_timestamp) {
			
			return null;
		}
		
		result[0] = -1;
		result[1] = -1;
		
		last_server_response_timestamp = System.currentTimeMillis();
		
		String bestmove_string = null;
		
		//String url_for_the_request_mainline = "http://tablebase.lichess.ovh/standard/mainline?fen=" + fen;//2 times slower
		String url_for_the_request = "http://tablebase.lichess.ovh/standard?fen=" + fen;
		
		try {
			
			String server_response_json_text = getHTMLFromURL(url_for_the_request);
			
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
			
			
			//Possible outcomes are "win", "draw", "loss", "blessed-loss", "cursed-win"
			String game_category_string = extractJSONAttribute(logger, server_response_json_text, "\"category\":");
			
			if (game_category_string != null) {
				
				if (game_category_string.equals("\"win\"")
						|| game_category_string.equals("\"blessed-loss\"")
						|| game_category_string.equals("\"draw\"")
						) {
					
					int[] dtz_and_dtm = extractDTZandDTM(logger, server_response_json_text);
					
					result[0] = dtz_and_dtm[0];
					result[1] = dtz_and_dtm[1];
					
					String first_array_string = extractFirstJSONArray(logger, server_response_json_text);
					
					//System.out.println("first_array_string=" + first_array_string);
					
					String[] array_elements = extractJSONArrayElements(logger, first_array_string);
					
					if (array_elements.length > 0) {						
						
						String array_element = array_elements[0];
						
						//System.out.println("first_array_element_string=" + array_element);
					
						//The uci moves list is ordered - the first line of the response contains the best move
						bestmove_string = extractJSONAttribute(logger, array_element, "\"uci\":"); //"uci":"d1c2",
						
						if (bestmove_string != null) {
							
							bestmove_string = bestmove_string.replace("\"", ""); //The value is quoted
							
							logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: bestmove_string=" + bestmove_string);
						}
					}
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
	
	
	private static int[] extractDTZandDTM(Logger logger, String json_containing_dtz_dtm) {
		
		
		int[] result = new int[2];
		
		result[0] = -1;
		result[1] = -1;
		
		
		String dtz_string = extractJSONAttribute(logger, json_containing_dtz_dtm, "\"dtz\":");
		
		if (dtz_string != null) {
			
			try {
				
				result[0] = Integer.parseInt(dtz_string);
				
				String dtm_string = extractJSONAttribute(logger, json_containing_dtz_dtm, "\"dtm\":");
				
				if (dtm_string != null) {
					
					try {
						
						result[1] = Integer.parseInt(dtm_string);
						
					} catch (NumberFormatException nfe) {
						
						//logger.addException(nfe);
					}
				}
				
			} catch (NumberFormatException nfe) {
				
				//logger.addException(nfe);
			}
		}
		
		
		return result;
	}
	
	
	public static final String getWDL_BlockingOnSocketConnection(String fen, int colour_to_move, long timeToThinkInMiliseconds, int[] result, Logger logger) {
		
		//If we have pending server request than exit
		/*if (current_request_url == null) {
			
			return null;
		}*/
		
		if (timeToThinkInMiliseconds < minimalPossibleTime() ) {
			
			return null;
		}
		
		if (System.currentTimeMillis() <= getWaitingTimeBetweenRequests() + last_server_response_timestamp) {
			
			return null;
		}
		
		result[0] = -1;
		result[1] = -1;
		
		last_server_response_timestamp = System.currentTimeMillis();
		
		String bestmove_string = null;
		
		String url_for_the_request_mainline = "http://tablebase.lichess.ovh/standard/mainline?fen=" + fen;
		
		try {
			
			String server_response_json_text = getHTMLFromURL(url_for_the_request_mainline);
			
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
			
			
			String winner_string = extractJSONAttribute(logger, server_response_json_text, "\"winner\":");
			
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
					
					String first_array_string = extractFirstJSONArray(logger, server_response_json_text);
					
					//System.out.println("first_array_string=" + first_array_string);
					
					String[] array_elements = extractJSONArrayElements(logger, first_array_string);
					
					if (array_elements.length > 0) {						
						
						String array_element = array_elements[0];
						
						//System.out.println("first_array_element_string=" + array_element);
						
						String dtz_string = extractJSONAttribute(logger, array_element, "\"dtz\":");
						
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
						bestmove_string = extractJSONAttribute(logger, array_element, "\"uci\":"); //"uci":"d1c2",
						
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

	
	private static String extractFirstJSONArray(Logger logger, String json_text) {
		
		int start_index = json_text.indexOf("[");
		
		if (start_index == -1) {
			
			return null;
		}
		
		int end_index = json_text.indexOf("]", start_index);
		
		if (end_index == -1) {
			
			return null;
		}
		
		String attribute_value = json_text.substring(start_index, end_index + 1);
		
		return attribute_value;
	}
	
	
	private static String[] extractJSONArrayElements(Logger logger, String json_array) {
		
		List<String> array_elements_list = new ArrayList<String>();
		
		char[] chars = json_array.toCharArray();
		
		for (int i = 0; i < chars.length; i++) {
			
			char cur_char1 = chars[i];
			
			if (cur_char1 == '{') {
				
				for (int j = i; j < chars.length; j++) {
					
					char cur_char2 = chars[j];
					
					if (cur_char2 == '}') {
						
						array_elements_list.add(json_array.substring(i, j + 1));
						
						i = j;
						
						break;
					}
				}
			}
		}
		
		return array_elements_list.toArray(new String[0]);
	}
	
	
	private static String extractJSONAttribute(Logger logger, String json_object, String attribute_name) {
		
		int start_index = json_object.indexOf(attribute_name);
		
		if (start_index == -1) {
			
			return null;
		}
		
		logger.addText("OnlineSyzygy.extractJSONAttribute: attribute_name=" + attribute_name + " found");
		
		int possible_end_index1 = json_object.indexOf(",", start_index);
		
		int possible_end_index2 = json_object.indexOf("}", start_index);
		
		int dtm_end_index = 0;
		
		if (possible_end_index1 != -1 && possible_end_index2 != -1) {
			
			dtm_end_index = Math.min(possible_end_index1, possible_end_index2);
			
		} else if (possible_end_index1 != -1) {
			
			dtm_end_index = possible_end_index1;
			
		} else if (possible_end_index2 != -1) {
			
			dtm_end_index = possible_end_index2;
			
		} else {
			
			return null;
		}		
		
		String attribute_value = json_object.substring(start_index + attribute_name.length(), dtm_end_index);
		
		logger.addText("OnlineSyzygy.extractJSONAttribute: attribute_value=" + attribute_value);
		
		return attribute_value;
	}
	
	
	private static String getHTMLFromURL(String urlToRead) throws Exception {

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
	
	
	public interface Logger {
		
		public void addText(String message);
		
		public void addException(Exception exception);
	}
}

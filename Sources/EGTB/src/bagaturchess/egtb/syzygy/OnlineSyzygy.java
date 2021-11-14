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

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;


public class OnlineSyzygy {
	
	
	private static final String CHARSET_ENCODING = "UTF-8";
	
	
	//We have to wait between each server request,
	//because otherwise we got "Server returned HTTP response code: 429"
	private static int[] WAITING_TIME_BETWEEN_REQESTS_IN_MILISECONDS = new int[] {
			500,
			1000,
			2 * 1000,
			4 * 1000,
			8 * 1000,
			16 * 1000,
			32 * 1000,
			64 * 1000
		};
	
	private static int current_index_for_waiting_time = 0;
	
	
	private static long last_server_response_timestamp = 0;
	
	
	public static final String getDTZandDTM_BlockingOnSocketConnection(IBitBoard board, int[] result) {
	
		if (System.currentTimeMillis() <= WAITING_TIME_BETWEEN_REQESTS_IN_MILISECONDS[current_index_for_waiting_time] + last_server_response_timestamp) {
			
			return null;
		}
		
		last_server_response_timestamp = System.currentTimeMillis();
		
		String response = null;
		
		int dtz = -1;
		int dtm = -1;
		
		String fen = board.toEPD().replace(' ', '_');
		
		//String url_for_the_request_mainline = "http://tablebase.lichess.ovh/standard/mainline?fen=" + fen;//2 times slower
		String url_for_the_request = "http://tablebase.lichess.ovh/standard?fen=" + fen;
		
		try {
			
			String json_response_text = getHTMLFromURL(url_for_the_request);
			
			current_index_for_waiting_time--;
			
			if (current_index_for_waiting_time < 0) {
				
				current_index_for_waiting_time = 0;
			}
			
			response = json_response_text;
			
			int dtz_start_index = json_response_text.indexOf("\"dtz\":");
			
			if (dtz_start_index != -1) {
				
				int possible_dtz_end_index1 = json_response_text.indexOf(",", dtz_start_index);
				int possible_dtz_end_index2 = json_response_text.indexOf("}", dtz_start_index);
				
				int dtz_end_index = 0;
				
				if (possible_dtz_end_index1 != -1 && possible_dtz_end_index2 != -1) {
					
					dtz_end_index = Math.min(possible_dtz_end_index1, possible_dtz_end_index2);
					
				} else if (possible_dtz_end_index1 != -1) {
					
					dtz_end_index = possible_dtz_end_index1;
					
				} else if (possible_dtz_end_index2 != -1) {
					
					dtz_end_index = possible_dtz_end_index2;
					
				} else {
					
					return null;
				}
				
				
				String dtz_string = json_response_text.substring(dtz_start_index + 6, dtz_end_index);
				
				//System.out.println("dtz_string=" + dtz_string);
				
				try {
					
					dtz = Integer.parseInt(dtz_string);
					
					int dtm_start_index = json_response_text.indexOf("\"dtm\":");
					
					if (dtm_start_index != -1) {
						
						int possible_dtm_end_index1 = json_response_text.indexOf(",", dtm_start_index);
						int possible_dtm_end_index2 = json_response_text.indexOf("}", dtm_start_index);
						
						int dtm_end_index = 0;
						
						if (possible_dtm_end_index1 != -1 && possible_dtm_end_index2 != -1) {
							
							dtm_end_index = Math.min(possible_dtm_end_index1, possible_dtm_end_index2);
							
						} else if (possible_dtm_end_index1 != -1) {
							
							dtm_end_index = possible_dtm_end_index1;
							
						} else if (possible_dtm_end_index2 != -1) {
							
							dtm_end_index = possible_dtm_end_index2;
							
						} else {
							
							return null;
						}
						
						String dtm_string = json_response_text.substring(dtm_start_index + 6, dtm_end_index);
						
						try {
							
							dtm = Integer.parseInt(dtm_string);
							
						} catch (NumberFormatException nfe) {
							
							//Do nothing
						}
					}
					
				} catch (NumberFormatException nfe) {
					
					//Do nothing
				}
			}
			
		} catch (Exception e) {
			
			//e.printStackTrace();
			
			current_index_for_waiting_time++;
			
			if (current_index_for_waiting_time > WAITING_TIME_BETWEEN_REQESTS_IN_MILISECONDS.length - 1) {
				
				current_index_for_waiting_time = WAITING_TIME_BETWEEN_REQESTS_IN_MILISECONDS.length - 1;
			}
		}
		
		result[0] = dtz;
		result[1] = dtm;
		
		return response;
	}
	
	
	public static final String getWDL_BlockingOnSocketConnection(IBitBoard board, int[] result) {
		
		if (System.currentTimeMillis() <= WAITING_TIME_BETWEEN_REQESTS_IN_MILISECONDS[current_index_for_waiting_time] + last_server_response_timestamp) {
			
			return null;
		}
		
		last_server_response_timestamp = System.currentTimeMillis();
		
		String response = null;
		
		int dtz = -1;
		int winner_color = -1;
		
		String fen = board.toEPD().replace(' ', '_');
		
		String url_for_the_request_mainline = "http://tablebase.lichess.ovh/standard/mainline?fen=" + fen;//2 times slower
		//String url_for_the_request = "http://tablebase.lichess.ovh/standard?fen=" + fen;
		
		try {
			
			String json_response_text = getHTMLFromURL(url_for_the_request_mainline);
			
			current_index_for_waiting_time--;
			
			if (current_index_for_waiting_time < 0) {
				
				current_index_for_waiting_time = 0;
			}
			
			response = json_response_text;
			
			/*
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
					{"uci":"c5d6","san":"Kd6","dtz":-2},{"uci":"b7a6","san":"Ka6","dtz":1},{"uci":"d3d4","san":"d4","dtz":-2},{"uci":"a6a5","san":"Ka5","dtz":1},{"uci":"d4d5","san":"d5","dtz":-4},{"uci":"a5a4","san":"Ka4","dtz":3},{"uci":"d6c5","san":"Kc5","dtz":-2},{"uci":"a4a3","san":"Ka3","dtz":1},{"uci":"d5d6","san":"d6","dtz":-2},{"uci":"a3a2","san":"Ka2","dtz":1},{"uci":"d6d7","san":"d7","dtz":-2},{"uci":"a2a1","san":"Ka1","dtz":1},{"uci":"d7d8q","san":"d8=Q","dtz":-8},{"uci":"a1b1","san":"Kb1","dtz":7},{"uci":"d8d2","san":"Qd2","dtz":-6},{"uci":"b1a1","san":"Ka1","dtz":5},{"uci":"c5b4","san":"Kb4","dtz":-4},{"uci":"a1b1","san":"Kb1","dtz":3},{"uci":"b4a3","san":"Ka3","dtz":-2},{"uci":"b1a1","san":"Ka1","dtz":1},{"uci":"d2c1","san":"Qc1#","dtz":-1}
				],
				"winner":"w",
				"dtz":9
			}*/
			
			int winner_start_index = json_response_text.indexOf("\"winner\":");
			
			if (winner_start_index != -1) {
				
				//int winner_end_index = json_response_text.indexOf(",", winner_start_index);
				
				int possible_winner_end_index1 = json_response_text.indexOf(",", winner_start_index);
				int possible_winner_end_index2 = json_response_text.indexOf("}", winner_start_index);
				
				int winner_end_index = 0;
				
				if (possible_winner_end_index1 != -1 && possible_winner_end_index2 != -1) {
					
					winner_end_index = Math.min(possible_winner_end_index1, possible_winner_end_index2);
					
				} else if (possible_winner_end_index1 != -1) {
					
					winner_end_index = possible_winner_end_index1;
					
				} else if (possible_winner_end_index2 != -1) {
					
					winner_end_index = possible_winner_end_index2;
					
				} else {
					
					return null;
				}
				
				String winner_string = json_response_text.substring(winner_start_index + 9, winner_end_index);
				
				if (winner_string.equals("\"w\"")) {
					
					winner_color = Constants.COLOUR_WHITE;
					
				} else if (winner_string.equals("\"b\"")) {
					
					winner_color = Constants.COLOUR_BLACK;					
				}
			}
			
			
			int dtz_start_index = json_response_text.indexOf("\"dtz\":");
			
			if (dtz_start_index != -1) {
				
				int possible_dtz_end_index1 = json_response_text.indexOf(",", dtz_start_index);
				int possible_dtz_end_index2 = json_response_text.indexOf("}", dtz_start_index);
				
				int dtz_end_index = 0;
				
				if (possible_dtz_end_index1 != -1 && possible_dtz_end_index2 != -1) {
					
					dtz_end_index = Math.min(possible_dtz_end_index1, possible_dtz_end_index2);
					
				} else if (possible_dtz_end_index1 != -1) {
					
					dtz_end_index = possible_dtz_end_index1;
					
				} else if (possible_dtz_end_index2 != -1) {
					
					dtz_end_index = possible_dtz_end_index2;
					
				} else {
					
					return null;
				}
				
				String dtz_string = json_response_text.substring(dtz_start_index + 6, dtz_end_index);
				
				//System.out.println("dtz_string=" + dtz_string);
				
				try {
					
					dtz = Integer.parseInt(dtz_string);
					
					//this is the first occurance of dtz string in json response.
					//We have to add one move and switch the sign by multiplying to -1.
					dtz += dtz > 0 ? 1 : -1;
					dtz = -dtz;
					
					//System.out.println("dtz=" + dtz);
					
				} catch (NumberFormatException nfe) {
					
					//Do nothing
				}
			}
			
		} catch (Exception e) {
			
			//e is FileNotFoundException if the requested position is not presented on the server
			//e.printStackTrace();
			
			current_index_for_waiting_time++;
			
			if (current_index_for_waiting_time > WAITING_TIME_BETWEEN_REQESTS_IN_MILISECONDS.length - 1) {
				
				current_index_for_waiting_time = WAITING_TIME_BETWEEN_REQESTS_IN_MILISECONDS.length - 1;
			}
		}
		
		result[0] = dtz;
		result[1] = winner_color;
		
		return response;
	}
	
	
	private static String getHTMLFromURL(String urlToRead) throws Exception {

		URL url = new URL(urlToRead);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Content-Type", "application/json; utf-8");
		conn.setRequestMethod("GET");
		
		byte[] bytes = readAllBytes(conn.getInputStream());

		String html = new String(bytes, Charset.forName(CHARSET_ENCODING));

		return html;
	}
	
	
	private static byte[] readAllBytes(InputStream inputStream) throws IOException {

		final int bufLen = 4096;
		byte[] buf = new byte[bufLen];
		int readLen;
		IOException exception = null;

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
				outputStream.write(buf, 0, readLen);

			return outputStream.toByteArray();

		} catch (IOException e) {
			
			exception = e;
			
			throw e;
			
		} finally {
			
			if (exception == null)
				
				inputStream.close();
			
			else
				
				try {
					inputStream.close();
				} catch (IOException e) {
					exception.addSuppressed(e);
				}
		}
	}
	
	
	public static void main(String[] args) {
		
		IBitBoard board  = BoardUtils.createBoard_WithPawnsCache("3k4/8/8/8/8/8/3P4/3K4 w - -");
		//IBitBoard board = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
		
		int[] result = new int[2];
		
		for (int counter = 0; counter < 100; counter++) {
		
			//String response = getDTZandDTM_BlockingOnSocketConnection(board, result);
			//System.out.println("dtz=" + result[0]);
			//System.out.println("wdl=" + result[1]);
			
			String response = getWDL_BlockingOnSocketConnection(board, result);
			//System.out.println("dtz=" + result[0]);
			//System.out.println("winner=" + result[1]);
			
			if (response != null) {
				
				System.out.println("Try " + (counter + 1) + ": OK");
				
			} else {
				
				System.out.println("Try " + (counter + 1) + ": FAILED");
			}
			
			try {
				
				System.out.println("Waiting " + WAITING_TIME_BETWEEN_REQESTS_IN_MILISECONDS[current_index_for_waiting_time] + " ms");
				Thread.sleep(WAITING_TIME_BETWEEN_REQESTS_IN_MILISECONDS[current_index_for_waiting_time]);
				
			} catch (InterruptedException e) {}
		}
	}
}

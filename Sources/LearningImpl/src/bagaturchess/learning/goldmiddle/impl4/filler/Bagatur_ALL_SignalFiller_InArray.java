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
package bagaturchess.learning.goldmiddle.impl4.filler;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;


public class Bagatur_ALL_SignalFiller_InArray implements Bagatur_V20_FeaturesConstants {
	
	
	private ISignalFiller filler;
	private ISignals signals;
	
	
	public Bagatur_ALL_SignalFiller_InArray(IBitBoard board) {
		
		filler = new Bagatur_V20_SignalFiller(board);
		
		signals = new Signals();
	}
	
	
	public void fillSignals(double[] signals_arr, int startIndex) {
		
		
		signals.clear();
		
		filler.fill(signals);
		
		
		int addIndex = startIndex;
		
		
		//OPENNING
		//Material
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_PAWN)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_KNIGHT)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_BISHOP)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_ROOK)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_QUEEN)).getStrength_o();
		
		
		//Imbalance
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_KNIGHT_PAWNS)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR)).getStrength_o();
		
		
		//PST
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PIECE_SQUARE_TABLE)).getStrength_o();
		
		
		//Pawns
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_DOUBLE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_CONNECTED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_NEIGHBOUR)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_ISOLATED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_BACKWARD)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_INVERSE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_PASSED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_PASSED_CANDIDATE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_PASSED_UNSTOPPABLE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWN_SHIELD)).getStrength_o();
		
		
		//Mobility
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_BISHOP)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_ROOK)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_QUEEN)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_KING)).getStrength_o();
		
		
		//Threats
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_DOUBLE_ATTACKED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_UNUSED_OUTPOST)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_PAWN_PUSH)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_PAWN_ATTACKS)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_MULTIPLE_PAWN_ATTACKS)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_MAJOR_ATTACKED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_PAWN_ATTACKED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_QUEEN_ATTACKED_ROOK)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_QUEEN_ATTACKED_MINOR)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_THREAT_ROOK_ATTACKED)).getStrength_o();
		
		
		//Others
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_SIDE_TO_MOVE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_ONLY_MAJOR_DEFENDERS)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_ROOK_BATTERY)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_ROOK_7TH_RANK)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_ROOK_TRAPPED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_ROOK_FILE_OPEN)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_BISHOP_OUTPOST)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_BISHOP_PRISON)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_BISHOP_PAWNS)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_PAWN_BLOCKAGE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_KNIGHT_OUTPOST)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_CASTLING)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_PINNED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_OTHERS_DISCOVERED)).getStrength_o();
		
		
		
		//King safety and space
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_KING_SAFETY)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_SPACE)).getStrength_o();
	}
	
	
	private static class Signals implements ISignals {
		
		
		private ISignal[] signals_array = new ISignal[FEATURE_ID_SPACE + 1];
		private List<ISignal> signals_list = new ArrayList<ISignal>();
		
		
		private Signals() {
			
			//Material
			signals_array[FEATURE_ID_MATERIAL_PAWN] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_KNIGHT] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_BISHOP] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_ROOK] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_QUEEN] = new Signal();
			
			
			//Imbalance
			signals_array[FEATURE_ID_MATERIAL_IMBALANCE_KNIGHT_PAWNS] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAWNS] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_IMBALANCE_BISHOP_DOUBLE] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_IMBALANCE_QUEEN_KNIGHTS] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_IMBALANCE_ROOK_PAIR] = new Signal();
			
			
			//PST
			signals_array[FEATURE_ID_PIECE_SQUARE_TABLE] = new Signal();
			
			
			//Pawns
			signals_array[FEATURE_ID_PAWN_DOUBLE] = new Signal();
			signals_array[FEATURE_ID_PAWN_CONNECTED] = new Signal();
			signals_array[FEATURE_ID_PAWN_NEIGHBOUR] = new Signal();
			signals_array[FEATURE_ID_PAWN_ISOLATED] = new Signal();
			signals_array[FEATURE_ID_PAWN_BACKWARD] = new Signal();
			signals_array[FEATURE_ID_PAWN_INVERSE] = new Signal();
			signals_array[FEATURE_ID_PAWN_PASSED] = new Signal();
			signals_array[FEATURE_ID_PAWN_PASSED_CANDIDATE] = new Signal();
			signals_array[FEATURE_ID_PAWN_PASSED_UNSTOPPABLE] = new Signal();
			signals_array[FEATURE_ID_PAWN_SHIELD] = new Signal();
			
			
			//Mobility
			signals_array[FEATURE_ID_MOBILITY_KNIGHT] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_BISHOP] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_ROOK] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_QUEEN] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_KING] = new Signal();
			
			
			//Threats
			signals_array[FEATURE_ID_THREAT_DOUBLE_ATTACKED] = new Signal();
			signals_array[FEATURE_ID_THREAT_UNUSED_OUTPOST] = new Signal();
			signals_array[FEATURE_ID_THREAT_PAWN_PUSH] = new Signal();
			signals_array[FEATURE_ID_THREAT_PAWN_ATTACKS] = new Signal();
			signals_array[FEATURE_ID_THREAT_MULTIPLE_PAWN_ATTACKS] = new Signal();
			signals_array[FEATURE_ID_THREAT_MAJOR_ATTACKED] = new Signal();
			signals_array[FEATURE_ID_THREAT_PAWN_ATTACKED] = new Signal();
			signals_array[FEATURE_ID_THREAT_QUEEN_ATTACKED_ROOK] = new Signal();
			signals_array[FEATURE_ID_THREAT_QUEEN_ATTACKED_MINOR] = new Signal();
			signals_array[FEATURE_ID_THREAT_ROOK_ATTACKED] = new Signal();
			
			
			//Others
			signals_array[FEATURE_ID_OTHERS_SIDE_TO_MOVE] = new Signal();
			signals_array[FEATURE_ID_OTHERS_ONLY_MAJOR_DEFENDERS] = new Signal();
			signals_array[FEATURE_ID_OTHERS_ROOK_BATTERY] = new Signal();
			signals_array[FEATURE_ID_OTHERS_ROOK_7TH_RANK] = new Signal();
			signals_array[FEATURE_ID_OTHERS_ROOK_TRAPPED] = new Signal();
			signals_array[FEATURE_ID_OTHERS_ROOK_FILE_OPEN] = new Signal();
			signals_array[FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN_ISOLATED] = new Signal();
			signals_array[FEATURE_ID_OTHERS_ROOK_FILE_SEMI_OPEN] = new Signal();
			signals_array[FEATURE_ID_OTHERS_BISHOP_OUTPOST] = new Signal();
			signals_array[FEATURE_ID_OTHERS_BISHOP_PRISON] = new Signal();
			signals_array[FEATURE_ID_OTHERS_BISHOP_PAWNS] = new Signal();
			signals_array[FEATURE_ID_OTHERS_BISHOP_CENTER_ATTACK] = new Signal();
			signals_array[FEATURE_ID_OTHERS_PAWN_BLOCKAGE] = new Signal();
			signals_array[FEATURE_ID_OTHERS_KNIGHT_OUTPOST] = new Signal();
			signals_array[FEATURE_ID_OTHERS_CASTLING] = new Signal();
			signals_array[FEATURE_ID_OTHERS_PINNED] = new Signal();
			signals_array[FEATURE_ID_OTHERS_DISCOVERED] = new Signal();
			
			//King safety and space
			signals_array[FEATURE_ID_KING_SAFETY] = new Signal();
			signals_array[FEATURE_ID_SPACE] = new Signal();
			
			
			for (int i=0; i<signals_array.length; i++) {
				if (signals_array[i] != null) {
					signals_list.add(signals_array[i]);
				}
			}
		}
		
		
		@Override
		public ISignal getSignal(int id) {
			return signals_array[id];
		}
		
		@Override
		public void clear() {
			for (int i=0; i<signals_list.size(); i++) {
				signals_list.get(i).clear();
			}
		}
	}
	
	
	private static class Signal implements ISignal {
		
		
		private double strength_o;
		private double strength_e;
		
		private Signal() {
			//Do nothing
		}
		
		
		@Override
		public double getStrength() {
			throw new UnsupportedOperationException();
		}
		
		
		public double getStrength_o() {	
			return strength_o;
		}
		

		public double getStrength_e() {	
			return strength_e;
		}
		
		
		/* (non-Javadoc)
		 * @see bagaturchess.learning.api.ISignal#clear()
		 */
		@Override
		public void clear() {
			strength_o = 0;
			strength_e = 0;
		}

		
		/* (non-Javadoc)
		 * @see bagaturchess.learning.api.ISignal#addStrength(double, double)
		 */
		@Override
		public void addStrength(double signalStrength, double dummy_param) {
			strength_o += signalStrength;
			strength_e += signalStrength;
		}

		/* (non-Javadoc)
		 * @see bagaturchess.learning.api.ISignal#getStrength(int)
		 */
		@Override
		public double getStrength(int subid) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see bagaturchess.learning.api.ISignal#addStrength(int, double, double)
		 */
		@Override
		public void addStrength(int subid, double signalStrength,
				double opening_part) {
			throw new UnsupportedOperationException();
		}			
	}
}

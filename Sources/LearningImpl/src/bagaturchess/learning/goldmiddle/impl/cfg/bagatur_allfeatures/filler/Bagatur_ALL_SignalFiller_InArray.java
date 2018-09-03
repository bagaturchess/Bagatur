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
package bagaturchess.learning.goldmiddle.impl.cfg.bagatur_allfeatures.filler;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;


public class Bagatur_ALL_SignalFiller_InArray implements Bagatur_ALL_FeaturesConstants {
	
	
	private ISignalFiller filler;
	private ISignals signals;
	
	
	public Bagatur_ALL_SignalFiller_InArray(IBitBoard board) {
		
		filler = new Bagatur_ALL_SignalFiller(board);
		
		signals = new Signals();
	}
	
	
	public void fillSignals(double[] signals_arr, int startIndex) {
		
		
		signals.clear();
		
		filler.fill(signals);
		
		
		int addIndex = startIndex;
		
		
		//OPENNING
		//Standard
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_PAWN)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_KNIGHT)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_BISHOP)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_ROOK)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_QUEEN)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_DOUBLE_BISHOP)).getStrength_o();
		
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PST)).getStrength_o();
		
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_TEMPO)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_CASTLING)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_FIANCHETTO)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_TRAP_BISHOP)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_BLOCKED_PAWN)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_KINGS_OPPOSITION)).getStrength_o();
		
		//Pawns
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_GUARDS)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_DOUBLED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_ISOLATED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_BACKWARD)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_SUPPORTED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_CANDIDATE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_PASSED_SUPPORTED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_PASSED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_F)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_FF)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_OP_F)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PASSED_UNSTOPPABLE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_PASSED_STOPPERS)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_ROOK_OPENED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_ROOK_SEMIOPENED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_ROOK_7TH2TH)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_QUEEN_7TH2TH)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_OPENED)).getStrength_o();
		
		//Moves iteration
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_BISHOP)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_ROOK)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_QUEEN)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_KNIGHT_OUTPOST)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_BISHOP_OUTPOST)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_BISHOP_BAD)).getStrength_o();
		
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_KING_SAFETY)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_SPACE)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_HUNGED)).getStrength_o();
		
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT_S)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_BISHOP_S)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_ROOK_S)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_QUEEN_S)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_TRAPED)).getStrength_o();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PASSERS_FRONT_ATTACKS)).getStrength_o();
		
		
		//ENDGAME
		//Standard
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_PAWN)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_KNIGHT)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_BISHOP)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_ROOK)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_QUEEN)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MATERIAL_DOUBLE_BISHOP)).getStrength_e();
		
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PST)).getStrength_e();
		
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_TEMPO)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_CASTLING)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_FIANCHETTO)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_TRAP_BISHOP)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_BLOCKED_PAWN)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_STANDARD_KINGS_OPPOSITION)).getStrength_e();
		
		//Pawns
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_GUARDS)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_DOUBLED)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_ISOLATED)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_BACKWARD)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_SUPPORTED)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_CANDIDATE)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_PASSED_SUPPORTED)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_PASSED)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_F)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_FF)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_OP_F)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PASSED_UNSTOPPABLE)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_PASSED_STOPPERS)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_ROOK_OPENED)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_ROOK_SEMIOPENED)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_ROOK_7TH2TH)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_QUEEN_7TH2TH)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PAWNS_KING_OPENED)).getStrength_e();
		
		//Moves iteration
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_BISHOP)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_ROOK)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_QUEEN)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_KNIGHT_OUTPOST)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_BISHOP_OUTPOST)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_BISHOP_BAD)).getStrength_e();
		
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_KING_SAFETY)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_SPACE)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_HUNGED)).getStrength_e();
		
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_KNIGHT_S)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_BISHOP_S)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_ROOK_S)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_MOBILITY_QUEEN_S)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_TRAPED)).getStrength_e();
		signals_arr[addIndex++] = ((Signal)signals.getSignal(FEATURE_ID_PASSERS_FRONT_ATTACKS)).getStrength_e();
	}
	
	
	private static class Signals implements ISignals {
		
		
		private ISignal[] signals_array = new ISignal[FEATURE_ID_PASSERS_FRONT_ATTACKS + 1];
		private List<ISignal> signals_list = new ArrayList<ISignal>();
		
		
		private Signals() {
			
			signals_array[FEATURE_ID_MATERIAL_PAWN] = new Signal();
			
			//Standard
			signals_array[FEATURE_ID_MATERIAL_PAWN] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_KNIGHT] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_BISHOP] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_ROOK] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_QUEEN] = new Signal();
			signals_array[FEATURE_ID_MATERIAL_DOUBLE_BISHOP] = new Signal();
			
			signals_array[FEATURE_ID_PST] = new Signal();
			
			signals_array[FEATURE_ID_STANDARD_TEMPO] = new Signal();
			signals_array[FEATURE_ID_STANDARD_CASTLING] = new Signal();
			signals_array[FEATURE_ID_STANDARD_FIANCHETTO] = new Signal();
			signals_array[FEATURE_ID_STANDARD_TRAP_BISHOP] = new Signal();
			signals_array[FEATURE_ID_STANDARD_BLOCKED_PAWN] = new Signal();
			signals_array[FEATURE_ID_STANDARD_KINGS_OPPOSITION] = new Signal();
			
			//Pawns
			signals_array[FEATURE_ID_PAWNS_KING_GUARDS] = new Signal();
			signals_array[FEATURE_ID_PAWNS_DOUBLED] = new Signal();
			signals_array[FEATURE_ID_PAWNS_ISOLATED] = new Signal();
			signals_array[FEATURE_ID_PAWNS_BACKWARD] = new Signal();
			signals_array[FEATURE_ID_PAWNS_SUPPORTED] = new Signal();
			signals_array[FEATURE_ID_PAWNS_CANDIDATE] = new Signal();
			signals_array[FEATURE_ID_PAWNS_PASSED_SUPPORTED] = new Signal();
			signals_array[FEATURE_ID_PAWNS_PASSED] = new Signal();
			signals_array[FEATURE_ID_PAWNS_KING_F] = new Signal();
			signals_array[FEATURE_ID_PAWNS_KING_FF] = new Signal();
			signals_array[FEATURE_ID_PAWNS_KING_OP_F] = new Signal();
			signals_array[FEATURE_ID_PASSED_UNSTOPPABLE] = new Signal();
			signals_array[FEATURE_ID_PAWNS_PASSED_STOPPERS] = new Signal();
			signals_array[FEATURE_ID_PAWNS_ROOK_OPENED] = new Signal();
			signals_array[FEATURE_ID_PAWNS_ROOK_SEMIOPENED] = new Signal();
			signals_array[FEATURE_ID_PAWNS_ROOK_7TH2TH] = new Signal();
			signals_array[FEATURE_ID_PAWNS_QUEEN_7TH2TH] = new Signal();
			signals_array[FEATURE_ID_PAWNS_KING_OPENED] = new Signal();
			
			//Moves iteration
			signals_array[FEATURE_ID_MOBILITY_KNIGHT] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_BISHOP] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_ROOK] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_QUEEN] = new Signal();
			signals_array[FEATURE_ID_KNIGHT_OUTPOST] = new Signal();
			signals_array[FEATURE_ID_BISHOP_OUTPOST] = new Signal();
			signals_array[FEATURE_ID_BISHOP_BAD] = new Signal();
			
			signals_array[FEATURE_ID_KING_SAFETY] = new Signal();
			signals_array[FEATURE_ID_SPACE] = new Signal();
			signals_array[FEATURE_ID_HUNGED] = new Signal();
			
			signals_array[FEATURE_ID_MOBILITY_KNIGHT_S] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_BISHOP_S] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_ROOK_S] = new Signal();
			signals_array[FEATURE_ID_MOBILITY_QUEEN_S] = new Signal();
			signals_array[FEATURE_ID_TRAPED] = new Signal();
			signals_array[FEATURE_ID_PASSERS_FRONT_ATTACKS] = new Signal();
			
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
		
		
		/* (non-Javadoc)
		 * @see bagaturchess.learning.api.ISignal#getStrength()
		 */
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
		public void addStrength(double signalStrength, double opening_part) {
			strength_o += signalStrength * opening_part;
			strength_e += signalStrength * (1 - opening_part);
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

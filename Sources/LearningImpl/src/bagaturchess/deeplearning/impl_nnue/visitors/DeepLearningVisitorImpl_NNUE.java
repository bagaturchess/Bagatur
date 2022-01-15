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
package bagaturchess.deeplearning.impl_nnue.visitors;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.visrec.ml.data.DataSet;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.bitboard.impl1.NNUE_Input;
import bagaturchess.deeplearning.api.NeuralNetworkUtils;
import bagaturchess.deeplearning.impl_nnue.NeuralNetworkUtils_NNUE_PSQT;
import bagaturchess.ucitracker.api.PositionsVisitor;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.FileIO;
import deepnetts.util.Tensor;


public class DeepLearningVisitorImpl_NNUE implements PositionsVisitor {
	
	
	public static final String NET_FILE = "nnue.dn.bin";
	
	
	private int iteration = 0;
	
	private int counter;
	
	private NeuralNetwork network;
	
	
	private double sumDiffs1;
	private double sumDiffs2;
	
	private long startTime;
	
	double[] inputs = new double[NeuralNetworkUtils_NNUE_PSQT.getInputsSize()];
	
	float[][][] inputs_3d = new float[8][8][12];
	
	
	public DeepLearningVisitorImpl_NNUE() throws Exception {
		
		if ((new File(NET_FILE)).exists()) {
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(NET_FILE));
			network = (ConvolutionalNetwork) ois.readObject();
			ois.close();
			
			//printWeights(network.getWeights());
			
		} else {
			
			network = NeuralNetworkUtils_NNUE_PSQT.buildNetwork();
		}
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		
		//double expectedWhitePlayerEval_func = 1 / (double) (1 + 1 / Math.pow(Math.E, expectedWhitePlayerEval));
		//double expectedWhitePlayerEval_func = Math.tanh(expectedWhitePlayerEval);
		//double expectedWhitePlayerEval_func = Math.log(expectedWhitePlayerEval);
		float expectedWhitePlayerEval_func = expectedWhitePlayerEval;
		
		
		if (status != IGameStatus.NONE) {
			
			throw new IllegalStateException("status=" + status);
		}
		
		
		/*for (int index = 0; index < bitboard.getNNUEInputs().length; index++) {
			
			int piece_type = index / 64;
			
			if (piece_type < 0 || piece_type > 11) {
				
				throw new IllegalStateException("piece_type=" + piece_type);
			}
			
			int sqare_id = index % 64;
			int file = sqare_id & 7;
			int rank = sqare_id >>> 3;
			
			inputs_3d[file][rank][piece_type] = (float) bitboard.getNNUEInputs()[index];
		}
		
		Tensor tensor = new Tensor(inputs_3d);
		*/
		
		float[] inputs_1d = (float[]) bitboard.getNNUEInputs();
		
		Tensor tensor = new Tensor(inputs_1d.length, 1, inputs_1d);
		
		network.setInput(tensor);
		
		//forward method is already called in setInput(tensor)
		//network.forward();
		
		double actualWhitePlayerEval = network.getOutput()[0];
		
		
		sumDiffs1 += Math.abs(0 - expectedWhitePlayerEval_func);
		sumDiffs2 += Math.abs(expectedWhitePlayerEval_func - actualWhitePlayerEval);
		
		
        BackpropagationTrainer trainer = (BackpropagationTrainer) network.getTrainer();
        
        trainer//.setLearningRate(1f)
                //.setMaxError(0.01f)
                .setMaxEpochs(1);
        
        DataSet_1 set = new DataSet_1();
        
        float[] outputs = new float[1];
        outputs[0] = expectedWhitePlayerEval_func;
        
        set.addItem(inputs_1d, outputs);
        
        trainer.train(set);
        
		/*DataSet trainingSet = new DataSet(NeuralNetworkUtils_NNUE_PSQT.getInputsSize(), 1);
        trainingSet.addRow(new DataSetRow(bitboard.getNNUEInputs(), new double[]{expectedWhitePlayerEval_func}));
        network.getLearningRule().doLearningEpoch(trainingSet);
        */
        
        
		counter++;
		if ((counter % 1000000) == 0) {
			
			System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%, positions: " + counter);
			
			/*try {
				
				FileIO.writeToFile(network, NET_FILE);
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}*/
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		iteration++;
		sumDiffs1 = 0;
		sumDiffs2 = 0;
	}
	
	
	public void end() {
		
		//System.out.println("***************************************************************************************************");
		//System.out.println("End iteration " + iteration + ", Total evaluated positions count is " + counter);
		System.out.println("END Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%, positions: " + counter);
		
		try {
			
			FileIO.writeToFile(network, NET_FILE);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	
	private double[] copy(double[] src) {
	
		double[] dst = new double[src.length];
		
		for (int i = 0; i < src.length; i++) {
			
			dst[i] = src[i];
		}
		
		return dst;
	}
	
	
	public static final void printWeights(Double[] nnue_weights) {
		
		System.out.println("nnue_weights=" + nnue_weights.length);
		
		for (int color = 0; color < 2; color++) {
			
			for (int piece_type = PAWN; piece_type <= KING; piece_type++) {
				
				System.out.println("******************************************************************************************************************************************");
				System.out.println("COLOR: " + color + ", TYPE: " + piece_type);
				
				VarStatistic stats = new VarStatistic();
				
				for (int rank = 7; rank >= 0; rank--) {
					
					String board_line = "";
					
					for (int file = 0; file < 8; file++) {
						
						int square_id = 8 * rank + file;
						
						int nnue_index = NNUE_Input.getInputIndex(color, piece_type, square_id);
						
						double nnue_weight = nnue_weights[nnue_index];
						
						stats.addValue(nnue_weight);
								
						board_line += nnue_weight + ", ";
					}
					
					System.out.println(board_line);
				}
				
				System.out.println("STATS: " + stats);
				System.out.println("******************************************************************************************************************************************");
			}
		}
		
		System.exit(0);
	}
}

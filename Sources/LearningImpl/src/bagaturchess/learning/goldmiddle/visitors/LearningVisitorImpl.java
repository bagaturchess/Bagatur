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
package bagaturchess.learning.goldmiddle.visitors;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.learning.api.IAdjustableFeature;
import bagaturchess.learning.api.IFeature;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.api.ILearningInput;
import bagaturchess.learning.goldmiddle.api.LearningInputFactory;
import bagaturchess.learning.goldmiddle.impl.eval.FeaturesEvaluator;
import bagaturchess.learning.impl.features.baseimpl.Features_Splitter;
import bagaturchess.learning.impl.signals.Signals;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.impl.pv.PVManager;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.ucitracker.api.PositionsVisitor;


public class LearningVisitorImpl implements PositionsVisitor {
	
	
	boolean PERSISTENT = true;
	
	
	private int iteration = 0;
	
	private int counter;
	
	private ISignalFiller filler;
	
	private ISignals signals;
	
	private Features_Splitter features_splitter;
	
	private IEvaluator evaluator;
	
	private ISearch searcher;
	
	private PVManager pvman = new PVManager(ISearch.MAX_DEPTH);
	
	private double sumDiffs1;
	
	private double sumDiffs2;
	
	private long startTime;
	
	private FeaturesFilter filter;
	
	private IBitBoard bitboard;
	
	
	public LearningVisitorImpl() throws Exception {
		
		this(new FeaturesFilter() {
			
			@Override
			public boolean isAdjustable(int featureID) {
				
				return true;
			}
		});
		
		//ChannelManager.setChannel(new Channel_Console(System.in, System.out, System.out));
	}
	
	
	public LearningVisitorImpl(FeaturesFilter _filter) throws Exception {
		
		filter = _filter;
	}
	
	
	@Override
	public void visitPosition(IBitBoard bitboard, IGameStatus status, int expectedWhitePlayerEval) {
		
		if (status != IGameStatus.NONE) {
			
			throw new IllegalStateException("status=" + status);
		}
		
		if (bitboard.getStatus() != IGameStatus.NONE) {
			
			//throw new IllegalStateException("bitboard.getStatus()=" + bitboard.getStatus());
			return;
		}
		
		double actualWhitePlayerEval = evaluator.fullEval(0, IEvaluator.MIN_EVAL, IEvaluator.MAX_EVAL, bitboard.getColourToMove());
		
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			
			actualWhitePlayerEval = -actualWhitePlayerEval;
		}
		
		/*searcher.newSearch();
		
		int depth = 1;
		
		ISearchInfo info = SearchInfoFactory.getFactory().createSearchInfo();
		info.setDepth(depth);
		
		int actual_white_search_score = searcher.pv_search(dummy_mediator, pvman, info, ISearch.PLY * depth, ISearch.PLY * depth,
				0, ISearch.MIN, ISearch.MAX, 0, 0, null, false, 0, bitboard.getColourToMove(), 0, 0, false, 0, false);
		
		//System.out.println("info=" + info.getDepth());
		
		if (bitboard.getColourToMove() == Figures.COLOUR_BLACK) {
			
			actual_white_search_score = -actual_white_search_score;
		}

		int delta = (int) (actualWhitePlayerEval - actual_white_search_score);
		
		//System.out.println("delta=" + delta);
		*/
		
		double openingPart = bitboard.getMaterialFactor().getOpenningPart();
		
		newAdjustment(actualWhitePlayerEval, expectedWhitePlayerEval, openingPart);
		
		
		counter++;
		
		if ((counter % 100000) == 0) {
			//System.out.println(counter);
		}
	}
	
	
	public void newAdjustment(double actualWhitePlayerEval, double expectedWhitePlayerEval, double openingPart) {
		
		sumDiffs1 += Math.abs(0 - expectedWhitePlayerEval);
		sumDiffs2 += Math.abs(expectedWhitePlayerEval - actualWhitePlayerEval);
		
		double deltaP = expectedWhitePlayerEval - actualWhitePlayerEval;
		//double deltaP = actualWhitePlayerEval - expectedWhitePlayerEval;
		
		IFeature[] features = features_splitter.getFeatures(bitboard);
		
		if (deltaP != 0) {
			
			for (int i = 0; i < features.length; i++) {
				
				IFeature feature = features[i];
				
				if (feature != null) {
					
					int featureID = feature.getId();
					
					if (filter.isAdjustable(featureID)) {
						
						ISignal cur_signal = signals.getSignal(featureID);
						
						if (cur_signal.getStrength() != 0) {
							
							double adjustment = deltaP > 0 ? 1 : -1;
							
							((IAdjustableFeature) features[i]).adjust(cur_signal, adjustment, -1);
						}
					}
				}
			}
		}
	}
	
	
	public void begin(IBitBoard bitboard) throws Exception {
		
		startTime = System.currentTimeMillis();
		
		counter = 0;
		
		iteration++;
		
		sumDiffs1 = 0;
		
		sumDiffs2 = 0;
		
		ILearningInput input = LearningInputFactory.createDefaultInput();
		
		this.bitboard = bitboard;
		
		filler = input.createFiller(bitboard);
		
		features_splitter = Features_Splitter.load(Features_Splitter.FEATURES_FILE_NAME, input.getFeaturesConfigurationClassName());
		
		signals = new Signals(features_splitter.getFeatures(bitboard));
		
		evaluator = new FeaturesEvaluator(bitboard, null, filler, features_splitter, signals);
		
		/*IRootSearchConfig cfg = new RootSearchConfig_BaseImpl_1Core(
					new String[] {
						bagaturchess.search.impl.alg.impl1.Search_PVS_NWS.class.getName(),
						SearchConfigImpl_AB.class.getName(),
						bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20.class.getName(),
						bagaturchess.learning.goldmiddle.impl.eval.FeaturesEvaluationConfig.class.getName(),
					}
				);


		searcher = null;
		
		SharedData sharedData = new SharedData(ChannelManager.getChannel(), cfg);
		String searchClassName =  cfg.getSearchClassName();
		searcher = (ISearch) ReflectionUtils.createObjectByClassName_ObjectsConstructor(
						searchClassName,
						new Object[] {bitboard,  cfg, sharedData}
					);
		
		searcher.getEnv().setEval(evaluator);
		*/
	}
	
	
	public void end() {
		
		System.out.println("Iteration " + iteration + ": Time " + (System.currentTimeMillis() - startTime) + "ms, " + "Success percent before this iteration: " + (100 * (1 - (sumDiffs2 / sumDiffs1))) + "%");
		
		Features_Splitter.updateWeights(features_splitter);
		
		Features_Splitter.store(Features_Splitter.FEATURES_FILE_NAME, features_splitter);
	}
	
	
	public static interface FeaturesFilter {
		public boolean isAdjustable(int featureID);
	}
	
	
	private final ISearchMediator dummy_mediator = new ISearchMediator() {
		
		
		@Override
		public void startIteration(int iteration) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setStopper(ISearchStopper stopper) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void send(String msg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void registerInfoObject(ISearchInfo info) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public int getTrustWindow_MTD_Step() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public int getTrustWindow_BestMove() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public int getTrustWindow_AlphaAspiration() {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public ISearchStopper getStopper() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public ISearchInfo getLastInfo() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public BestMoveSender getBestMoveSender() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void dump(Throwable t) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void dump(String msg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void changedMinor(ISearchInfo info) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void changedMajor(ISearchInfo info) {
			// TODO Auto-generated method stub
			
		}
	};
}

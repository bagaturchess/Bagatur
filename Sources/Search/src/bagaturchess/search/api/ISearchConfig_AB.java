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


package bagaturchess.search.api;


public interface ISearchConfig_AB {
	
	
	public static final int PLY = 16;

	public static final int PLY_01_16 = 1;
	public static final int PLY_02_16 = 2;
	public static final int PLY_03_16 = 3;
	public static final int PLY_04_16 = 4;
	public static final int PLY_05_16 = 5;
	public static final int PLY_06_16 = 6;
	public static final int PLY_07_16 = 7;
	public static final int PLY_08_16 = 8;
	public static final int PLY_09_16 = 9;
	public static final int PLY_10_16 = 10;
	public static final int PLY_11_16 = 11;
	public static final int PLY_12_16 = 12;
	public static final int PLY_13_16 = 13;
	public static final int PLY_14_16 = 14;
	public static final int PLY_15_16 = 15;
	
	
	public int getOpenningBook_Mode();
	
	
	/**
	 * Extensions for PV and NonPV Nodes
	 */
	public IExtensionMode getExtensionMode();
	public int getDynamicExtUpdateInterval();
	
	public int getExtension_CheckInPV();
	public int getExtension_SingleReplyInPV();
	public int getExtension_WinCapNonPawnInPV();
	public int getExtension_WinCapPawnInPV();
	public int getExtension_RecapturePV();
	public int getExtension_MateThreatPV();
	public int getExtension_PasserPushPV();
	public int getExtension_PromotionPV();
	public int getExtension_MoveEvalPV();
	public boolean isExtension_MateLeafPV();
	
	public int getExtension_CheckInNonPV();
	public int getExtension_SingleReplyInNonPV();
	public int getExtension_WinCapNonPawnInNonPV();
	public int getExtension_WinCapPawnInNonPV();
	public int getExtension_RecaptureNonPV();
	public int getExtension_MateThreatNonPV();
	public int getExtension_PasserPushNonPV();
	public int getExtension_PromotionNonPV();
	public int getExtension_MoveEvalNonPV();
	public boolean isExtension_MateLeafNonPV();
	
	
	/**
	 * Reductions
	 */
	public int getReduction_LMRRootIndex1();
	public int getReduction_LMRRootIndex2();
	public int getReduction_LMRPVIndex1();
	public int getReduction_LMRPVIndex2();
	public int getReduction_LMRNonPVIndex1();
	public int getReduction_LMRNonPVIndex2();
	
	public boolean isReduction_ReduceCapturesInLMR();
	public boolean isReduction_ReduceHistoryMovesInLMR();
	public boolean isReduction_ReduceHighEvalMovesInLMR();
	
	
	/**
	 * Static Pruning
	 */
	public int getPruning_StaticPVIndex();
	public int getPruning_StaticNonPVIndex();
	public boolean isPruning_Razoring();
	public boolean isPruning_NullMove();
	public boolean isPrunning_MateDistance();
	
	
	/**
	 * Internal Iterative Deeping
	 */
	public boolean isIID_PV();
	public boolean isIID_NonPV();
	
	
	/**
	 * Others
	 */
	public boolean isOther_SingleBestmove();
	public boolean isOther_StoreTPTInQsearch();
	public boolean isOther_UseTPTInRoot();
	public boolean isOther_UseTPTScoresQsearchPV();
	public boolean isOther_UseTPTScoresNonPV();
	public boolean isOther_UseTPTScoresPV();
	public boolean isOther_UseCheckInQSearch();
	public boolean isOther_UseSeeInQSearch();
	public boolean isOther_UsePVHistory();
	public int getTPTUsageDepthCut();
	
	/**
	 * Move ordering
	 */
	public boolean randomizeMoveLists();
	public boolean sortMoveLists();
	public int getOrderingWeight_CASTLING();
	public int getOrderingWeight_COUNTER();
	public int getOrderingWeight_EQ_CAP();
	public int getOrderingWeight_KILLER();
	public int getOrderingWeight_LOSE_CAP();
	public int getOrderingWeight_MATE_KILLER();
	public int getOrderingWeight_MATE_MOVE();
	public int getOrderingWeight_PASSER_PUSH();
	public int getOrderingWeight_PREV_BEST_MOVE();
	public int getOrderingWeight_PREVPV_MOVE();
	public int getOrderingWeight_TPT_MOVE();
	public int getOrderingWeight_WIN_CAP();
	
	
}

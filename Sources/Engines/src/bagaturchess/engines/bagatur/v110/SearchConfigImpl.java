

package bagaturchess.engines.bagatur.v110;


import bagaturchess.search.api.IExtensionMode;
import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.search.api.ISearchConfig_MTD;
import bagaturchess.search.impl.alg.impl1.ISearchConfig1_MTD;


public class SearchConfigImpl implements ISearchConfig_MTD {
	
	/*
	pv_InCheck       1136305
	pv_Prom          74206
	pv_PasserPush    557758
	pv_CapNonPawn    1448956
	pv_CapPawn       1020597
	pv_SingleMove    118789
	nonpv_InCheck    17754238
	nonpv_Prom       609617
	nonpv_PasserPush 4900682
	nonpv_CapNonPawn 20175120
	nonpv_CapPawn    8424226
	nonpv_SingleMove 2058868
	nonpv_Mate       420409
	*/
	
	
	private static final int MAX_INDEX 				= 200;
	
	private IExtensionMode mode 					= IExtensionMode.DYNAMIC;
	private int dynamicExt_UpdateInterval			= 1000;
	
	public int extension_CheckInPV 					= PLY;
	public int extension_SingleReplyInPV 			= PLY;
	public int extension_WinCapNonPawnInPV 			= 0;//PLY;
	public int extension_WinCapPawnInPV 			= 0;//PLY / 2;
	public int extension_RecapturePV 				= PLY;
	public int extension_PasserPushPV 				= PLY - 1;
	public int extension_PromotionPV 				= PLY - 1;
	public int extension_MateThreatPV 				= PLY - 1;
	public int extension_MoveEvalPV 				= PLY - 1;
	public boolean extension_MateLeafPV				= false;
	
	public int extension_CheckInNonPV 				= PLY;
	public int extension_SingleReplyInNonPV 		= PLY;
	public int extension_WinCapNonPawnInNonPV 		= 0;//PLY;
	public int extension_WinCapPawnInNonPV 			= 0;//PLY / 2;
	public int extension_RecaptureNonPV 			= PLY;
	public int extension_PasserPushNonPV 			= PLY - 1;
	public int extension_PromotionNonPV 			= PLY - 1;
	public int extension_MateThreatNonPV			= PLY - 1;
	public int extension_MoveEvalNonPV 				= PLY - 1;
	public boolean extension_MateLeafNonPV			= false;
	
	
	/**
	 * Reductions
	 */
	public int reduction_LMRRootIndex1   			= 1;
	public int reduction_LMRRootIndex2   			= 1;
	
	public int reduction_LMRPVIndex1     			= 1;
	public int reduction_LMRPVIndex2     			= 1;
	
	public int reduction_LMRNonPVIndex1 			= 0;
	public int reduction_LMRNonPVIndex2 			= 1;
	
	public boolean reduction_ReduceCapturesInLMR 	 = false;
	public boolean reduction_ReduceHistoryMovesInLMR = true;
	public boolean reduction_ReduceHighEvalMovesInLMR= true;
	
	
	/**
	 * Static Pruning
	 */
	public int pruning_StaticPVIndex 				= 1;
	public int pruning_StaticNonPVIndex 			= 1;
	public boolean pruning_NullMove 				= true;
	public boolean pruning_Razoring 				= false;
	public boolean prunning_MateDistance 			= true;
	
	
	/**
	 * Internal Iterative Deeping
	 */
	public boolean IID_PV 							= true;
	public boolean IID_NonPV 						= true;
	
	
	/**
	 * Others
	 */
	public boolean other_SingleBestmove 			= false;
	public boolean other_StoreTPTInQsearch 			= true;
	public boolean other_UseCheckInQSearch 			= true;
	public boolean other_UsePVHistory 				= true;
	public boolean other_UseSeeInQSearch 			= true;
	public boolean other_UseTPTInRoot 				= true;
	public boolean other_UseTPTScoresNonPV 			= true;
	public boolean other_UseTPTScoresQsearchPV 		= true;
	
	
	public int orderingWeight_TPT_MOVE 				= 1;
	public int orderingWeight_MATE_MOVE       		= 1;
	public int orderingWeight_COUNTER         		= 1;
	public int orderingWeight_WIN_CAP         		= 1;
	public int orderingWeight_PREV_BEST_MOVE  		= 1;
	public int orderingWeight_EQ_CAP          		= 1;
	public int orderingWeight_MATE_KILLER     		= 1;
	public int orderingWeight_PREVPV_MOVE     		= 1;
	public int orderingWeight_CASTLING 	 	 		= 1;
	public int orderingWeight_PASSER_PUSH 	 		= 1;
	public int orderingWeight_KILLER          		= 1;
	public int orderingWeight_LOSE_CAP        		= -1;
	
	private int mtdTrustWindow = 0;
	
	
	@Override
	public int getExtension_MoveEvalPV() {
		return extension_MoveEvalPV;
	}
	
	@Override
	public int getExtension_MoveEvalNonPV() {
		return extension_MoveEvalNonPV;
	}
	
	public static int getMAX_INDEX() {
		return MAX_INDEX;
	}
	
	
	public static int getPLY() {
		return PLY;
	}
	
	public int getPly() {
		return PLY;
	}
	
	public IExtensionMode getExtensionMode() {
		return mode;
	}
	
	public int getDynamicExtUpdateInterval() {
		return dynamicExt_UpdateInterval;
	}
	
	public int getExtension_CheckInNonPV() {
		return extension_CheckInNonPV;
	}
	
	
	public int getExtension_CheckInPV() {
		return extension_CheckInPV;
	}
	
	
	public int getExtension_MateThreatPV() {
		return extension_MateThreatPV;
	}
	
	
	public int getExtension_MateThreatNonPV() {
		return extension_MateThreatNonPV;
	}
	
	public int getExtension_SingleReplyInNonPV() {
		return extension_SingleReplyInNonPV;
	}
	
	
	public int getExtension_SingleReplyInPV() {
		return extension_SingleReplyInPV;
	}
	
	public int getExtension_WinCapNonPawnInNonPV() {
		return extension_WinCapNonPawnInNonPV;
	}
	
	public int getExtension_WinCapNonPawnInPV() {
		return extension_WinCapNonPawnInPV;
	}
	
	public int getExtension_WinCapPawnInNonPV() {
		return extension_WinCapPawnInNonPV;
	}
	
	public int getExtension_WinCapPawnInPV() {
		return extension_WinCapPawnInPV;
	}
	
	public boolean isIID_NonPV() {
		return IID_NonPV;
	}
	
	public boolean isIID_PV() {
		return IID_PV;
	}
	
	public boolean isOther_SingleBestmove() {
		return other_SingleBestmove;
	}
	
	
	public boolean isOther_StoreTPTInQsearch() {
		return other_StoreTPTInQsearch;
	}
	
	public boolean isPruning_NullMove() {
		return pruning_NullMove;
	}
	
	public boolean isPruning_Razoring() {
		return pruning_Razoring;
	}
	
	
	public int getPruning_StaticNonPVIndex() {
		return pruning_StaticNonPVIndex;
	}
	
	public int getPruning_StaticPVIndex() {
		return pruning_StaticPVIndex;
	}
	
	public int getReduction_LMRNonPVIndex1() {
		return reduction_LMRNonPVIndex1;
	}
	
	public int getReduction_LMRNonPVIndex2() {
		return reduction_LMRNonPVIndex2;
	}
	
	public int getReduction_LMRPVIndex1() {
		return reduction_LMRPVIndex1;
	}
	
	public int getReduction_LMRPVIndex2() {
		return reduction_LMRPVIndex2;
	}
	
	public int getReduction_LMRRootIndex1() {
		return reduction_LMRRootIndex1;
	}
	
	public int getReduction_LMRRootIndex2() {
		return reduction_LMRRootIndex2;
	}
	
	public int getExtension_PasserPushPV() {
		return extension_PasserPushPV;
	}
	
	
	public int getExtension_PromotionPV() {
		return extension_PromotionPV;
	}
	
	
	public int getExtension_PasserPushNonPV() {
		return extension_PasserPushNonPV;
	}
	
	
	public int getExtension_PromotionNonPV() {
		return extension_PromotionNonPV;
	}
	
	
	public int getExtension_RecaptureNonPV() {
		return extension_RecaptureNonPV;
	}
	
	
	public int getExtension_RecapturePV() {
		return extension_RecapturePV;
	}


	public boolean isOther_UseCheckInQSearch() {
		return other_UseCheckInQSearch;
	}


	public boolean isOther_UsePVHistory() {
		return other_UsePVHistory;
	}


	public boolean isOther_UseSeeInQSearch() {
		return other_UseSeeInQSearch;
	}


	public boolean isOther_UseTPTInRoot() {
		return other_UseTPTInRoot;
	}


	public boolean isOther_UseTPTScoresNonPV() {
		return other_UseTPTScoresNonPV;
	}


	public boolean isOther_UseTPTScoresQsearchPV() {
		return other_UseTPTScoresQsearchPV;
	}


	public boolean isPrunning_MateDistance() {
		return prunning_MateDistance;
	}


	public boolean isReduction_ReduceCapturesInLMR() {
		return reduction_ReduceCapturesInLMR;
	}


	public boolean isReduction_ReduceHistoryMovesInLMR() {
		return reduction_ReduceHistoryMovesInLMR;
	}


	public boolean isExtension_MateLeafNonPV() {
		return extension_MateLeafNonPV;
	}


	public boolean isExtension_MateLeafPV() {
		return extension_MateLeafPV;
	}


	public int getOrderingWeight_CASTLING() {
		return orderingWeight_CASTLING;
	}


	public int getOrderingWeight_COUNTER() {
		return orderingWeight_COUNTER;
	}


	public int getOrderingWeight_EQ_CAP() {
		return orderingWeight_EQ_CAP;
	}


	public int getOrderingWeight_KILLER() {
		return orderingWeight_KILLER;
	}


	public int getOrderingWeight_LOSE_CAP() {
		return orderingWeight_LOSE_CAP;
	}


	public int getOrderingWeight_MATE_KILLER() {
		return orderingWeight_MATE_KILLER;
	}


	public int getOrderingWeight_MATE_MOVE() {
		return orderingWeight_MATE_MOVE;
	}


	public int getOrderingWeight_PASSER_PUSH() {
		return orderingWeight_PASSER_PUSH;
	}


	public int getOrderingWeight_PREV_BEST_MOVE() {
		return orderingWeight_PREV_BEST_MOVE;
	}


	public int getOrderingWeight_PREVPV_MOVE() {
		return orderingWeight_PREVPV_MOVE;
	}


	public int getOrderingWeight_TPT_MOVE() {
		return orderingWeight_TPT_MOVE;
	}


	public int getOrderingWeight_WIN_CAP() {
		return orderingWeight_WIN_CAP;
	}

	public boolean isReduction_ReduceHighEvalMovesInLMR() {
		return reduction_ReduceHighEvalMovesInLMR;
	}


	public boolean randomizeMoveLists() {
		return true;
	}


	public boolean sortMoveLists() {
		return true;
	}

	@Override
	public boolean isOther_UseTPTScoresPV() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getTPTUsageDepthCut() {
		return 0;
	}

	@Override
	public boolean isOpenningModeRandom() {
		// TODO Auto-generated method stub
		return true;
	}
}

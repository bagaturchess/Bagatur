package bagaturchess.engines.searchtune;


import bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD;
import bagaturchess.search.impl.alg.impl1.ISearchConfig1_MTD;


public class SearchConfig1_MTD_Impl_DISABLED extends SearchConfigImpl_MTD implements ISearchConfig1_MTD {
	
	
	/**
	 * Search parameters for PV nodes 
	 */
	public boolean pv_pruning_mate_distance = false;
	public boolean pv_pruning_null_move = false;
	public int pv_pruning_null_move_margin = -300;
	
	public boolean pv_optimization_tpt_scores = false;
	
	public int pv_reduction_lmr1 = 100;
	public int pv_reduction_lmr2 = 100;
	
	public boolean pv_qsearch_pruning_mate_distance = false;
	public boolean pv_qsearch_optimization_tpt_scores = false;
	public boolean pv_qsearch_use_see = false;
	public boolean pv_qsearch_move_checks = false;
	public boolean pv_qsearch_use_queen_material_margin = false;
	public boolean pv_qsearch_store_tpt_scores = false;
	
	
	/**
	 * Search parameters for NON-PV nodes 
	 */
	public boolean nonpv_pruning_mate_distance = false;
	public boolean nonpv_pruning_null_move = false;
	public int nonpv_pruning_null_move_margin = -300;
	public boolean nonpv_pruning_futility = false;
	
	public boolean nonpv_optimization_tpt_scores = false;
	
	public int nonpv_reduction_lmr1 = 100;
	public int nonpv_reduction_lmr2 = 100;
	public boolean nonpv_reduction_too_good_scores = false;
	
	public boolean nonpv_qsearch_pruning_mate_distance = false;
	public boolean nonpv_qsearch_optimization_tpt_scores = false;
	public boolean nonpv_qsearch_use_see = false;
	public boolean nonpv_qsearch_move_checks = false;
	public boolean nonpv_qsearch_use_queen_material_margin = false;
	public boolean nonpv_qsearch_store_tpt_scores = false;
	
	
	public SearchConfig1_MTD_Impl_DISABLED() {
		
	}
	
	
	public SearchConfig1_MTD_Impl_DISABLED(String[] args) {
		
	}
	
	
	@Override
	public boolean getPV_Pruning_MateDistance() {
		return pv_pruning_mate_distance;
	}

	@Override
	public boolean getNONPV_Pruning_MateDistance() {
		return nonpv_pruning_mate_distance;
	}
	
	@Override
	public boolean getPV_Pruning_NullMove() {
		return pv_pruning_null_move;
	}

	@Override
	public boolean getNONPV_Pruning_NullMove() {
		return nonpv_pruning_null_move;
	}
	
	
	@Override
	public int getPV_Pruning_NullMove_Margin() {
		return pv_pruning_null_move_margin;
	}
	
	
	@Override
	public int getNONPV_Pruning_NullMove_Margin() {
		return nonpv_pruning_null_move_margin;
	}
	
	
	@Override
	public boolean getPV_Optimization_TPTScores() {
		return pv_optimization_tpt_scores;
	}
	
	
	@Override
	public boolean getNONPV_Optimization_TPTScores() {
		return nonpv_optimization_tpt_scores;
	}
	
	
	@Override
	public int getPV_reduction_lmr1() {
		return pv_reduction_lmr1;
	}
	
	
	@Override
	public int getNONPV_reduction_lmr1() {
		return nonpv_reduction_lmr1;
	}
	
	
	@Override
	public int getPV_reduction_lmr2() {
		return pv_reduction_lmr2;
	}
	
	
	@Override
	public int getNONPV_reduction_lmr2() {
		return nonpv_reduction_lmr2;
	}


	@Override
	public boolean getPV_QSearch_Pruning_MateDistance() {
		return pv_qsearch_pruning_mate_distance;
	}


	@Override
	public boolean getNONPV_QSearch_Pruning_MateDistance() {
		return nonpv_qsearch_pruning_mate_distance;
	}


	@Override
	public boolean getPV_QSearch_Optimization_TPTScores() {
		return pv_qsearch_optimization_tpt_scores;
	}


	@Override
	public boolean getNONPV_QSearch_Optimization_TPTScores() {
		return nonpv_qsearch_optimization_tpt_scores;
	}


	@Override
	public boolean getPV_QSearch_Use_SEE() {
		return pv_qsearch_use_see;
	}


	@Override
	public boolean getNONPV_QSearch_Use_SEE() {
		return nonpv_qsearch_use_see;
	}


	@Override
	public boolean getPV_QSearch_Move_Checks() {
		return pv_qsearch_move_checks;
	}


	@Override
	public boolean getNONPV_QSearch_Move_Checks() {
		return nonpv_qsearch_move_checks;
	}


	@Override
	public boolean getPV_QSearch_Use_Queen_Material_Margin() {
		return pv_qsearch_use_queen_material_margin;
	}


	@Override
	public boolean getNONPV_QSearch_Use_Queen_Material_Margin() {
		return nonpv_qsearch_use_queen_material_margin;
	}


	@Override
	public boolean getPV_QSearch_Store_TPT_Scores() {
		return pv_qsearch_store_tpt_scores;
	}


	@Override
	public boolean getNONPV_QSearch_Store_TPT_Scores() {
		return nonpv_qsearch_store_tpt_scores;
	}


	@Override
	public boolean getNONPV_reduction_too_good_scores() {
		return nonpv_reduction_too_good_scores;
	}


	@Override
	public boolean getNONPV_Pruning_Futiliy() {
		return nonpv_pruning_futility;
	}
	
	
}

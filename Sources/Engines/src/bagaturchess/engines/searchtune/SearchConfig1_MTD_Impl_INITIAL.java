package bagaturchess.engines.searchtune;


import bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD;
import bagaturchess.search.impl.alg.impl1.ISearchConfig1_MTD;


public class SearchConfig1_MTD_Impl_INITIAL extends SearchConfigImpl_MTD implements ISearchConfig1_MTD {
	
	
	/**
	 * Search parameters for PV nodes 
	 */
	public boolean pv_pruning_mate_distance = true;
	public boolean pv_pruning_null_move = true;
	public int pv_pruning_null_move_margin = -300;
	
	public boolean pv_optimization_tpt_scores = true;
	
	public int pv_reduction_lmr1 = 1;
	public int pv_reduction_lmr2 = 3;
	
	public boolean pv_qsearch_pruning_mate_distance = true;
	public boolean pv_qsearch_optimization_tpt_scores = true;
	public boolean pv_qsearch_use_see = true;
	public boolean pv_qsearch_move_checks = true;
	public boolean pv_qsearch_use_queen_material_margin = true;
	public boolean pv_qsearch_store_tpt_scores = true;
	
	
	/**
	 * Search parameters for NON-PV nodes 
	 */
	public boolean nonpv_pruning_mate_distance = true;
	public boolean nonpv_pruning_null_move = true;
	public int nonpv_pruning_null_move_margin = -300;
	public boolean nonpv_pruning_futility = true;
	
	public boolean nonpv_optimization_tpt_scores = true;
	
	public int nonpv_reduction_lmr1 = 1;
	public int nonpv_reduction_lmr2 = 3;
	public boolean nonpv_reduction_too_good_scores = true;
	
	public boolean nonpv_qsearch_pruning_mate_distance = true;
	public boolean nonpv_qsearch_optimization_tpt_scores = true;
	public boolean nonpv_qsearch_use_see = true;
	public boolean nonpv_qsearch_move_checks = true;
	public boolean nonpv_qsearch_use_queen_material_margin = true;
	public boolean nonpv_qsearch_store_tpt_scores = true;
	
	
	public SearchConfig1_MTD_Impl_INITIAL() {
		
	}
	
	
	public SearchConfig1_MTD_Impl_INITIAL(String[] args) {
		
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

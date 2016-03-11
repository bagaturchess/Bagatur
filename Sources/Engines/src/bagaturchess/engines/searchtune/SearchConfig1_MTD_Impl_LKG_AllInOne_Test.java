package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG_AllInOne_Test extends SearchConfig1_MTD_Impl_LKG_AllInOne {
	
	
	public SearchConfig1_MTD_Impl_LKG_AllInOne_Test() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG_AllInOne_Test(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		
		pv_pruning_mate_distance 				= true;
		pv_pruning_null_move 					= true;
		pv_pruning_null_move_margin 			= -300;
		
		pv_optimization_tpt_scores 				= false;
		
		pv_reduction_lmr1 						= 1;
		pv_reduction_lmr2 						= 1;
		
		pv_qsearch_pruning_mate_distance 		= true;
		pv_qsearch_optimization_tpt_scores 		= true;
		pv_qsearch_use_see 						= true;
		pv_qsearch_move_checks 					= true;
		pv_qsearch_use_queen_material_margin 	= true;
		pv_qsearch_store_tpt_scores 			= true;
		
		
		/**
		 * Search parameters for NON-PV nodes 
		 */
		nonpv_pruning_mate_distance 			= true;
		nonpv_pruning_null_move 				= true;
		nonpv_pruning_null_move_margin 			= -300;
		nonpv_pruning_futility 					= true;
		
		nonpv_optimization_tpt_scores 			= true;
		
		nonpv_reduction_lmr1 					= 0;
		nonpv_reduction_lmr2 					= 1;
		nonpv_reduction_too_good_scores 		= true;
		
		nonpv_qsearch_pruning_mate_distance		= true;
		nonpv_qsearch_optimization_tpt_scores 	= true;
		nonpv_qsearch_use_see 					= true;
		nonpv_qsearch_move_checks 				= true;
		nonpv_qsearch_use_queen_material_margin = true;
		nonpv_qsearch_store_tpt_scores 			= true;
	}
}

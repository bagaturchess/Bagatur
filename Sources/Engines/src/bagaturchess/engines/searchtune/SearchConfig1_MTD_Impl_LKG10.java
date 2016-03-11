package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG10 extends SearchConfig1_MTD_Impl_LKG9 {
	
	
	public SearchConfig1_MTD_Impl_LKG10() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG10(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		
		nonpv_reduction_lmr2 					= 4;
		pv_reduction_lmr1						= 3;
		
		pv_pruning_null_move					= true;
		nonpv_qsearch_use_queen_material_margin	= true;
		pv_qsearch_store_tpt_scores				= true;
		nonpv_optimization_tpt_scores			= false;
		pv_qsearch_pruning_mate_distance		= false;
		nonpv_qsearch_move_checks				= false;
		pv_qsearch_move_checks					= true;
		pv_pruning_mate_distance				= false;
		pv_optimization_tpt_scores				= true;
		nonpv_reduction_too_good_scores			= true;
		nonpv_pruning_null_move					= true;
	}
}

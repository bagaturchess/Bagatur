package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG_AllInOne extends SearchConfig1_MTD_Impl_LKG0 {
	
	
	public SearchConfig1_MTD_Impl_LKG_AllInOne() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG_AllInOne(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		
		//lkg1
		pv_reduction_lmr1 						= 2;
		
		//lkg2
		nonpv_reduction_lmr1 					= 1;
		
		//lkg3
		pv_qsearch_use_see 						= true;
		
		//lkg4
		nonpv_qsearch_optimization_tpt_scores 	= true;
		
		//lkg5
		nonpv_reduction_lmr1 					= 6;
		
		//lkg6
		pv_qsearch_pruning_mate_distance		= true;
		nonpv_qsearch_optimization_tpt_scores	= false;
		nonpv_optimization_tpt_scores			= true;
		
		//lkg7
		pv_reduction_lmr1						= 1;
		nonpv_reduction_lmr1					= 1;
		pv_qsearch_use_queen_material_margin	= true;
		nonpv_qsearch_use_queen_material_margin	= true;
		pv_qsearch_pruning_mate_distance		= false;
		pv_qsearch_optimization_tpt_scores		= true;
		nonpv_qsearch_optimization_tpt_scores	= true;
		pv_pruning_mate_distance				= true;
		
		//lkg8
		pv_reduction_lmr2						= 7;
		nonpv_reduction_lmr2					= 2;
		nonpv_qsearch_use_queen_material_margin	= false;
		nonpv_qsearch_store_tpt_scores			= true;
		nonpv_pruning_futility					= true;
		pv_qsearch_use_queen_material_margin	= false;
		
		//lkg9
		pv_reduction_lmr2 						= 4;
		nonpv_reduction_lmr2 					= 1;
		pv_qsearch_pruning_mate_distance		= true;
		nonpv_qsearch_move_checks				= true;
		nonpv_qsearch_pruning_mate_distance		= true;
		
		//lkg10
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
		
		//lkg11
		pv_reduction_lmr1						= 1;
		
		//lkg12
		pv_reduction_lmr2 						= 6;
		
		//lkg13
		pv_reduction_lmr2 						= 8;
		nonpv_reduction_lmr2 					= 6;
		
		//lkg
		nonpv_reduction_lmr2 					= 5;
	}
}

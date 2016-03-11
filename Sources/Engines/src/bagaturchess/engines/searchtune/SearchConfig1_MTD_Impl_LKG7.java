package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG7 extends SearchConfig1_MTD_Impl_LKG6 {
	
	
	public SearchConfig1_MTD_Impl_LKG7() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG7(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		
		pv_reduction_lmr1						= 1;
		nonpv_reduction_lmr1					= 1;
		
		pv_qsearch_use_queen_material_margin	= true;
		nonpv_qsearch_use_queen_material_margin	= true;
		pv_qsearch_pruning_mate_distance		= false;
		pv_qsearch_optimization_tpt_scores		= true;
		nonpv_qsearch_optimization_tpt_scores	= true;
		pv_pruning_mate_distance				= true;
	}
}

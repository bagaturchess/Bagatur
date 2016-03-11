package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG8 extends SearchConfig1_MTD_Impl_LKG7 {
	
	
	public SearchConfig1_MTD_Impl_LKG8() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG8(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		
		pv_reduction_lmr2						=	7;
		nonpv_reduction_lmr2					=	2;
		
		nonpv_qsearch_use_queen_material_margin	=	false;
		nonpv_qsearch_store_tpt_scores			=	true;
		nonpv_pruning_futility					=	true;
		pv_qsearch_use_queen_material_margin	=	false;
	}
}

package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG9 extends SearchConfig1_MTD_Impl_LKG8 {
	
	
	public SearchConfig1_MTD_Impl_LKG9() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG9(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		
		pv_reduction_lmr2 					=	4;
		nonpv_reduction_lmr2 				=	1;
		
		pv_qsearch_pruning_mate_distance	=	true;
		nonpv_qsearch_move_checks			=	true;
		nonpv_qsearch_pruning_mate_distance	=	true;
	}
}

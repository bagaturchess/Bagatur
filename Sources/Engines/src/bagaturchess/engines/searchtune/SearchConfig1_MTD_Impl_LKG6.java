package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG6 extends SearchConfig1_MTD_Impl_LKG5 {
	
	
	public SearchConfig1_MTD_Impl_LKG6() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG6(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		pv_qsearch_pruning_mate_distance		=	true;
		nonpv_qsearch_optimization_tpt_scores	=	false;
		nonpv_optimization_tpt_scores			=	true;
	}
}

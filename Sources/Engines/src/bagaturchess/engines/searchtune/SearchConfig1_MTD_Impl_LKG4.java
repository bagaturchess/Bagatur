package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG4 extends SearchConfig1_MTD_Impl_LKG3 {
	
	
	public SearchConfig1_MTD_Impl_LKG4() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG4(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		nonpv_qsearch_optimization_tpt_scores = true;
	}
}

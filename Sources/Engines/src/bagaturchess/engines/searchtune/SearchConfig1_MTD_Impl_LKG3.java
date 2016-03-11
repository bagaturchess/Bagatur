package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG3 extends SearchConfig1_MTD_Impl_LKG2 {
	
	
	public SearchConfig1_MTD_Impl_LKG3() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG3(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		pv_qsearch_use_see = true;
	}
}

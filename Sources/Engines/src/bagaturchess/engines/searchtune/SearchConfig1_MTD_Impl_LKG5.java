package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG5 extends SearchConfig1_MTD_Impl_LKG4 {
	
	
	public SearchConfig1_MTD_Impl_LKG5() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG5(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		nonpv_reduction_lmr1 = 6;
	}
}

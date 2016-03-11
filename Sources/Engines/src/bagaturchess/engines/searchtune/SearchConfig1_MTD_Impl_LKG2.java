package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG2 extends SearchConfig1_MTD_Impl_LKG1 {
	
	
	public SearchConfig1_MTD_Impl_LKG2() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG2(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		nonpv_reduction_lmr1 = 1;
	}
}

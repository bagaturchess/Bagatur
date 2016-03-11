package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG1 extends SearchConfig1_MTD_Impl_LKG0 {
	
	
	public SearchConfig1_MTD_Impl_LKG1() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG1(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		pv_reduction_lmr1 = 2;
	}
}

package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG12 extends SearchConfig1_MTD_Impl_LKG11 {
	
	
	public SearchConfig1_MTD_Impl_LKG12() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG12(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		pv_reduction_lmr2 = 6;
	}
}

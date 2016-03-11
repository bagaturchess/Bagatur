package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG11 extends SearchConfig1_MTD_Impl_LKG10 {
	
	
	public SearchConfig1_MTD_Impl_LKG11() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG11(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		pv_reduction_lmr1						= 1;
	}
}

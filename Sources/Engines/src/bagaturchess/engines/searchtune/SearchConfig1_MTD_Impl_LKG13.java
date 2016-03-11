package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG13 extends SearchConfig1_MTD_Impl_LKG12 {
	
	
	public SearchConfig1_MTD_Impl_LKG13() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG13(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		pv_reduction_lmr2 						= 8;
		nonpv_reduction_lmr2 					= 6;
	}
}

package bagaturchess.engines.searchtune;


public class SearchConfig1_MTD_Impl_LKG extends SearchConfig1_MTD_Impl_LKG12 {
	
	
	public SearchConfig1_MTD_Impl_LKG() {
		super();
		init();
	}
	
	public SearchConfig1_MTD_Impl_LKG(String[] args) {
		super(args);
		init();
	}
	
	private void init() {
		nonpv_reduction_lmr2 					= 5;
	}
}

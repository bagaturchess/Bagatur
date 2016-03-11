


package bagaturchess.engines.bagatur.cfg.search;


public class SearchConfigImpl_MTD_SMP extends SearchConfigImpl_MTD {
	
	
	public SearchConfigImpl_MTD_SMP() {
		
	}
	
	
	public SearchConfigImpl_MTD_SMP(String[] args) {
		
	}
	
	
	@Override
	public int getTPTUsageDepthCut() {
		
		if (true) return 0;
		
		int cpus_count = Runtime.getRuntime().availableProcessors();
		double result = Math.round(log2(cpus_count));
		
		result = result - 1;
		
		//System.out.println(result);
		return (int) result;
	}
	
	private double log2(double number) {
		return Math.log(number) / Math.log(2);
	} 
	
	public static void main(String args[]) {
		SearchConfigImpl_MTD_SMP tmp = new SearchConfigImpl_MTD_SMP();
		int f = tmp.getTPTUsageDepthCut();
	}
}

package bagaturchess.search.impl.alg.impl0;


public class TPTAccess {
	
	
	private int count_CPUs = Runtime.getRuntime().availableProcessors();
	private int counter = 0;
	
	
	TPTAccess() {
		//System.out.println("TPT access: CPUs count " + count_CPUs);
	}
	
	
	public boolean access() {
		
		//if (true) return true; 
		
		counter--;
		
		if (counter <= 0) {
			counter = count_CPUs;
			//System.out.println("TPT access: yes");
			return true;
		}
		
		//System.out.println("TPT access: no");
		
		return false;
	}
}

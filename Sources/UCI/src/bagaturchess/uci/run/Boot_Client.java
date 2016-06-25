package bagaturchess.uci.run;


import bagaturchess.uci.engine.EngineProcess_BagaturImpl;


public class Boot_Client {
	
	public static void main(String[] args) {
		
		EngineProcess_BagaturImpl bagatur = new EngineProcess_BagaturImpl("BagaturClientProcess", "");
		
		try {
			
			bagatur.start();
			
			//bagatur.go(15);
			
			Thread.sleep(30000);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			
			try {
				
				bagatur.stop();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

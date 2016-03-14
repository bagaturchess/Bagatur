package bagaturchess.engines.run;

import bagaturchess.engines.run.api.IRunAPIStatus;
import bagaturchess.engines.run.api.RunAPI;
import bagaturchess.engines.run.api.RunAPIStatusImpl1;
import bagaturchess.search.api.IRootSearch;

public class RunAPIMain {
	
	private static IRootSearch engine = RunAPI.createEngine();
	
	public static void main(String[] args) {
		//IRootSearch engine = RunAPI.createEngine();
		IRunAPIStatus status = new RunAPIStatusImpl1();
		//String result = RunAPI.searchMove_byTime(engine, status, "rn1b2rk/1pp3p1/qp1p2R1/5Q2/3RN2P/1PP5/3PbP2/4K3 w - -", 10000);
		String[] result = RunAPI.searchMove_byStrength(engine, status, "rn1b2rk/1pp3p1/qp1p2R1/5Q2/3RN2P/1PP5/3PbP2/4K3 w - -", 10);
		System.out.println("DONE!");
		System.out.println("Move: " + result[0]);
		System.out.println("New FEN: " + result[1]);
	}
}

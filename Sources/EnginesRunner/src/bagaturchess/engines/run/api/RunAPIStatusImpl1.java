package bagaturchess.engines.run.api;


public class RunAPIStatusImpl1 implements IRunAPIStatus {

	@Override
	public void sendInfoLine(String info) {
		System.out.println(info);
	}

}

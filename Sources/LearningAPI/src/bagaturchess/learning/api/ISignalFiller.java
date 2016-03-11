package bagaturchess.learning.api;


public interface ISignalFiller {
	public void fill(ISignals signals);
	public void fillByComplexity(int complexity, ISignals signals);
}

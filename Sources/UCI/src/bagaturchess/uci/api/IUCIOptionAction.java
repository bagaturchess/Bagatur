package bagaturchess.uci.api;

public interface IUCIOptionAction {
	public String getOptionName();
	public void execute() throws Exception;
}

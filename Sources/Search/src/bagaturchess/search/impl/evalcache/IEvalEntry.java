package bagaturchess.search.impl.evalcache;


public interface IEvalEntry {
	public boolean isEmpty();
	public int getEval();
	public byte getLevel();
	public void setIsEmpty(boolean empty);
	public void setEval(int eval);
	public void setLevel(byte level);
}

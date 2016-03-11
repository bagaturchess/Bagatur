package bagaturchess.search.impl.uci_adaptor.timemanagement;


//Enum class
public class ITimeControllerType {
	
		
	public static final int INFINITE = 1;
	public static final int FIXED_DEPTH = 2;
	public static final int FIXED_NODES = 3;
	public static final int TIME_PER_MOVE = 4;
	public static final int INCREMENT_PER_MOVE = 5;
	public static final int TOURNAMENT = 6;
	public static final int SUDDEN_DEATH = 7;
	
	public static final int COUNT = 7;
	
	
	public static String asString(int type) {
		switch (type) {
			case INFINITE:
				return "INFINITE";
			case FIXED_DEPTH:
				return "FIXED_DEPTH";
			case FIXED_NODES:
				return "FIXED_NODES";
			case TIME_PER_MOVE:
				return "TIME_PER_MOVE";
			case INCREMENT_PER_MOVE:
				return "INCREMENT_PER_MOVE";
			case TOURNAMENT:
				return "TOURNAMENT";
			case SUDDEN_DEATH:
				return "SUDDEN_DEATH";
			default:
				throw new IllegalStateException();
		}
	}
}

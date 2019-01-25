package bagaturchess.engines.evaladapters.chess22k;

public class Assert {

	public static void isTrue(boolean condition) {
		if (!condition) {
			throw new AssertionError();
		}
	}

	public static void isTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}

}

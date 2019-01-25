package bagaturchess.engines.evaladapters.carbalo;

public class StringUtils {
	private static String SPACES = "                     ";

	public static String padRight(String str, int totalChars) {
		return str + SPACES.substring(0, totalChars - str.length());
	}

	public static String padLeft(String str, int totalChars) {
		return SPACES.substring(0, totalChars - str.length()) + str;
	}
}

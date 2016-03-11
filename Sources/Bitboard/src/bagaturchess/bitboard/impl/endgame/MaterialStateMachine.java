package bagaturchess.bitboard.impl.endgame;


import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


//Data files: http://sites.google.com/site/gaviotachessengine/Home/endgame-tablebases-1
//tbprobe.cpp: http://sites.google.com/site/gaviotachessengine/download
//on this next link, scroll to the bottom for the TB probing code
public class MaterialStateMachine {
	
	
	private static final char[] SIMBOLS = new char[] {'q', 'r', 'b', 'n', 'p'};

	
	public MaterialStateMachine() {
		
	}
	
	
	//Length includes king
	private static Set<String> generatorOfOneSide(int length) {
		Set<String> result = new HashSet<String>();
		generator_helper("k", 0, result, length);
		//for (String cur: result) {
		//	System.out.print("\"" + cur + "\", ");
		//}
		return result;
	}
	
	
	public static void generator_helper(String currentSignature, int simbolsStartIndex, Set<String> result, int length) {
		
		if (currentSignature.length() >= length) {
			result.add(currentSignature);
			return;
		}
		if (simbolsStartIndex == SIMBOLS.length) {
			result.add(currentSignature);
			return;
		}
		
		//Put and continue putting
		generator_helper(currentSignature + SIMBOLS[simbolsStartIndex], simbolsStartIndex, result, length);
		
		//Put and stop putting
		generator_helper(currentSignature + SIMBOLS[simbolsStartIndex], simbolsStartIndex + 1, result, length);
		
		//Not put and stop putting
		generator_helper(currentSignature, simbolsStartIndex + 1, result, length);
	}
	
	
	private static Set<Pair> generatePairs(int maxlength) {
		
		Set<String> oneSide = generatorOfOneSide(maxlength - 1);
		String[] oneSideStrs = new String[oneSide.size()];
		int k=0;
		for (String cur: oneSide) {
			oneSideStrs[k++] = cur;
		}
		
		Set<Pair> result = new HashSet<Pair>();
		for (int i=0; i<oneSideStrs.length; i++) {
			for (int j=0; j<oneSideStrs.length; j++) {
				String first = oneSideStrs[i];
				String second = oneSideStrs[j];
				long firstScores = getScores(first);
				long secondScores = getScores(second);
				if (secondScores > firstScores) {
					String tmp = second;
					second = first;
					first = tmp;
				}
				String all = first + second;
				if (all.length() > 2 && all.length() <= maxlength) {
					result.add(new Pair(first, second));
					System.out.println("first=" + first + ", second=" + second);
				}
			}
		}
		return result;
	}
	
	
	private static long getScores(String source) {
		long result = (long) Math.pow(10, source.length());
		for (int i=0; i<SIMBOLS.length; i++) {
			char cur = SIMBOLS[i];
			int count = count(source, cur);
			result += Math.pow(2 * count, SIMBOLS.length - i);
		}
		return result;
	}

	
	private static int count(String source, char what) {
		int count = 0;
		for (int i=0; i<source.length(); i++) {
			if (source.charAt(i) == what) {
				count++;
			}
		}
		return count;
	}
	

	private static class Pair {
		
		
		private String first;
		private String second;
		
		
		public Pair(String _first, String _second) {
			first = _first;
			second = _second;
		}
		
		
		@Override
		public int hashCode() {
			return first.hashCode() + second.hashCode();
		}
		
		
		@Override
		public boolean equals(Object obj) {
			Pair other = (Pair) obj;
			return (first.equals(other.first) && second.equals(other.second));
		}
		
		
		@Override
		public String toString() {
			return first + second;
		}
	}
	
	
	public static void main(String[] args) {
		
		Set<Pair> result = generatePairs(5);
		Set<String> result_str = new HashSet<String>();
		for (Pair cur: result) {
			result_str.add(cur.toString());
		}
		
		/*for (Pair cur: result) {
			//System.out.print("\"" + cur + "\", ");
			System.out.println(cur);
			
			if (!Gaviota.names_man345_set.contains(cur.toString())) {
				System.out.println("NOTFOUND> " + cur);
			}
		}*/
		
		for (String cur: Gaviota.names_man345_set) {
			if (!result_str.contains(cur)) {
				System.out.println("NOTFOUND> " + cur);
			}
		}
		System.out.println(result_str.size() + " " + Gaviota.names_man345_set.size());
		
	}
}

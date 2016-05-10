package bagaturchess.search.api.internal;

import bagaturchess.bitboard.impl.movegen.MoveInt;

public class SearchInfoUtils {
	
	//info multipv 1 depth 13 score cp -25 time 10344 nodes 770950 nps 74531 pv c7c6 e3e4 b8d7 e1g1 c6d5 c4d5 a8c8 c1e3 f8e8 d5e6 f7e6 a1c1 d7e5 d1a4 f6g4 a4a7 g4e3
	
	public static String buildMajorInfoCommand_multipv(int pvnum, ISearchInfo info, long startTime, int tptusage, long nodes) {
		return buildMajorInfoCommand(info, startTime, tptusage, "info multipv " + pvnum, nodes);
	}
	
	
	public static String buildMajorInfoCommand(ISearchInfo info, long startTime, int tptusage, long nodes) {
		return buildMajorInfoCommand(info, startTime, tptusage, "info", nodes);
	}
	
	private static String buildMajorInfoCommand(ISearchInfo info, long startTime, int tptusage, String prefix, long nodes) {
		
		//info depth 1 seldepth 9 time 31 nodes 0 score cp 99 nps 0 currmove Nf5-h6 currmovenumber 25 hashfull 0 pv Nf5-h6, Rg8-g7, Nh6-f5, Nb6-a4,
		//info depth 4 seldepth 10 score cp 31999 time 63 nodes 2733 pv d1d5 b7b5 a3a4 a5b6 a4a6
		//info depth 8 seldepth 13 score cp 30000 time 172 nodes 56129 nps 19991 pv Rd1-d5, b7-b5, Qa3xa4, Ka5xa4, Ra8xa6,  hashfull 0
		
		long time = (System.currentTimeMillis() - startTime);
		
		nodes = info.getSearchedNodes();
		
		long timeInSecs = (time / 1000);
		if (timeInSecs == 0) {
			timeInSecs = 1;
		}
		
		String message = "";
		message += prefix;
		message += " depth " + info.getDepth();
		message += " seldepth " + info.getSelDepth();
		message += " time " + time;
		message += " nodes " + nodes;
		long nps = nodes / timeInSecs;
		if (nps > 1) {
			message += " nps " + nps;
		}
		
		long eval = (int)info.getEval();
		if (info.isMateScore()) {
			message += " score mate " + info.getMateScore();
		} else {
			message += " score cp " + eval;
		}
		
		if (info.isLowerBound()) {
			message += " lowerbound";
		} else if (info.isUpperBound()) {
			message += " upperbound";
		}
		
		if (tptusage != -1) message += " hashfull " + (10 * tptusage);
		
		//if (!info.isUpperBound()) {
			
			String pv = "";
			if (info.getPV() != null) {
				for (int j=0; j<info.getPV().length; j++) {
					pv += MoveInt.moveToStringUCI(info.getPV()[j]);
					if (j != info.getPV().length - 1) {
						pv += " ";//", ";
					}
				}
			}
			
			message += " pv " + pv;
		//}
		
		return message;
	}
	
	
	public static String buildMinorInfoCommand(ISearchInfo info, long startTime, int tptusage, long nodes) {
		long time = (System.currentTimeMillis() - startTime);
		long timeInSecs = (time / 1000);
		if (timeInSecs == 0) {
			timeInSecs = 1;
		}
		
		nodes = info.getSearchedNodes();
		
		String message = "";
		message += "info";
		message += " depth " + info.getDepth();
		message += " seldepth " + info.getSelDepth();
		message += " nodes " + info.getSearchedNodes();
		long nps = nodes / timeInSecs;
		if (nps > 1) {
			message += " nps " + nps;
		}
		if (info.getCurrentMove() != 0) {
			message += " currmove " + MoveInt.moveToStringUCI(info.getCurrentMove());
			message += " currmovenumber " + info.getCurrentMoveNumber();
		}
		if (tptusage != -1) message += " hashfull " + (10 * tptusage);
		
		return message;
	}
}

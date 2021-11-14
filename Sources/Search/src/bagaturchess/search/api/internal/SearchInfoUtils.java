package bagaturchess.search.api.internal;


import bagaturchess.bitboard.api.IBitBoard;


public class SearchInfoUtils {
	
	//info multipv 1 depth 13 score cp -25 time 10344 nodes 770950 nps 74531 pv c7c6 e3e4 b8d7 e1g1 c6d5 c4d5 a8c8 c1e3 f8e8 d5e6 f7e6 a1c1 d7e5 d1a4 f6g4 a4a7 g4e3
	
	public static String buildMajorInfoCommand_multipv(int pvnum, ISearchInfo info, long startTime, int tptusage, long nodes, IBitBoard board) {
		return buildMajorInfoCommand(info, startTime, tptusage, "info multipv " + pvnum, nodes, board);
	}
	
	
	public static String buildMajorInfoCommand(ISearchInfo info, long startTime, int tptusage, long nodes, IBitBoard board) {
		return buildMajorInfoCommand(info, startTime, tptusage, "info", nodes, board);
	}
	
	private static String buildMajorInfoCommand(ISearchInfo info, long startTime, int tptusage, String prefix, long nodes, IBitBoard board) {
		
		//info depth 1 seldepth 9 time 31 nodes 0 score cp 99 nps 0 currmove Nf5-h6 currmovenumber 25 hashfull 0 pv Nf5-h6, Rg8-g7, Nh6-f5, Nb6-a4,
		//info depth 4 seldepth 10 score cp 31999 time 63 nodes 2733 pv d1d5 b7b5 a3a4 a5b6 a4a6
		//info depth 8 seldepth 13 score cp 30000 time 172 nodes 56129 nps 19991 pv Rd1-d5, b7-b5, Qa3xa4, Ka5xa4, Ra8xa6,  hashfull 0
		
		long time = (System.currentTimeMillis() - startTime);
		
		nodes = info.getSearchedNodes();
		
		if (time == 0) {
			time = 1;
		}
		
		StringBuilder message = new StringBuilder(128);
		
		message.append(prefix);
		message.append(" depth " + info.getDepth());
		message.append(" seldepth " + info.getSelDepth());
		message.append(" time " + time);
		message.append(" nodes " + nodes);
		long nps = 1000 * nodes / time;
		if (nps > 1) {
			message.append(" nps " + nps);
		}
		if (info.getTBhits() > 0) {
			message.append(" tbhits " + info.getTBhits());
		}
		long eval = (int)info.getEval();
		if (info.isMateScore()) {
			message.append(" score mate " + info.getMateScore());
		} else {
			message.append(" score cp " + eval);
		}
		
		if (info.isLowerBound()) {
			message.append(" lowerbound");
		} else if (info.isUpperBound()) {
			message.append(" upperbound");
		}
		
		if (tptusage != -1) message.append(" hashfull " + (10 * tptusage));
		
		if (!info.isUpperBound()) {
			
			message.append(" pv ");
			
			if (info.getPV() != null) {
				for (int j=0; j<info.getPV().length; j++) {
					message.append(board.getMoveOps().moveToString(info.getPV()[j]));
					if (j != info.getPV().length - 1) {
						message.append(" ");//", ";
					}
				}
			}
		}
		
		return message.toString();
	}
	
	
	public static String buildMinorInfoCommand(ISearchInfo info, long startTime, int tptusage, long nodes, IBitBoard board) {
		long time = (System.currentTimeMillis() - startTime);
		long timeInSecs = (time / 1000);
		if (timeInSecs == 0) {
			timeInSecs = 1;
		}
		
		nodes = info.getSearchedNodes();
		
		StringBuilder message = new StringBuilder(128);

		
		message.append("info");
		message.append(" depth " + info.getDepth());
		message.append(" seldepth " + info.getSelDepth());
		message.append(" nodes " + info.getSearchedNodes());
		long nps = nodes / timeInSecs;
		if (nps > 1) {
			message.append(" nps " + nps);
		}
		if (info.getTBhits() > 0) {
			message.append(" tbhits " + info.getTBhits());
		}
		if (info.getCurrentMove() != 0) {
			message.append(" currmove ");
			message.append(board.getMoveOps().moveToString(info.getCurrentMove()));
			message.append(" currmovenumber " + info.getCurrentMoveNumber());
		}
		if (tptusage != -1) message.append(" hashfull " + (10 * tptusage));
		
		return message.toString();
	}
}

package bagaturchess.search.impl.uci_adaptor;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.egtb.gaviota.GTBProbing;
import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.info.SearchInfoFactory;


public class TimeSaver {
	
	
	private GTBProbing egtbProbing;
	private OpeningBook ob;
	//private IBitBoard bitboard;
	
	
	public TimeSaver(OpeningBook _ob /*, IRootSearchConfig rootSearchCfg, IBitBoard boardForSetup*/) {
		ob = _ob;
		//bitboard = new Board(boardForSetup.toEPD(), null, rootSearchCfg.getBoardConfig());
		//egtbProbing = sharedData.getGTBProbing();
	}
	
	
	public boolean beforeMove(IBitBoard bitboardForSetup, int openningBook_Mode, ISearchMediator mediator, boolean useOpening) {
		
		mediator.dump("TimeSaver: useOpening = " + useOpening + ", ob=" + ob);
		//Search in the book
		if (useOpening && ob != null) {
			IOpeningEntry entry = ob.getEntry(bitboardForSetup.getHashKey(), bitboardForSetup.getColourToMove());
			if (entry != null && entry.getWeight() >= OpeningBook.OPENNING_BOOK_MIN_MOVES) {
				
				int move = 0;
				switch (openningBook_Mode) {
				
					case OpeningBook.OPENNING_BOOK_MODE_POWER0:
						move = entry.getRandomEntry(0);
						break;
						
					case OpeningBook.OPENNING_BOOK_MODE_POWER1:
						move = entry.getRandomEntry(1);
						break;
						
					case OpeningBook.OPENNING_BOOK_MODE_POWER2:
						move = entry.getRandomEntry(2);
						break;
						
					default:
						throw new IllegalStateException("openningBook_Mode=" + openningBook_Mode);
				}
				
				mediator.dump("TimeSaver: Opening move " + bitboardForSetup.getMoveOps().moveToString(move));
				ISearchInfo info = createInfo(move, 0);
				mediator.changedMajor(info);
				if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
				return true;
			}
		}
		
		
		mediator.dump("TimeSaver: bitboard.hasSingleMove() = " + bitboardForSetup.hasSingleMove());
		//Check if there is only one legal move
		if (bitboardForSetup.hasSingleMove()) {
			IMoveList list = new BaseMoveList();
			if (bitboardForSetup.isInCheck()) {
				int count = bitboardForSetup.genKingEscapes(list);
				if (count != 1) {
					throw new IllegalStateException();
				}
			} else {
				int count = bitboardForSetup.genAllMoves(list);
				if (count != 1) {
					throw new IllegalStateException();
				}
			}
			int move = list.reserved_getMovesBuffer()[0];
			mediator.dump("TimeSaver: Single reply move " + bitboardForSetup.getMoveOps().moveToString(move));
			ISearchInfo info = createInfo(move, 1);
			mediator.changedMajor(info);
			if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
			return true;
		}
		
		/*mediator.dump("TimeSaver: EGTB Probing = " + egtbProbing);
		int[] out = new int[2];
		egtbProbing.probeMove(bitboardForSetup, out);
		if (out[0] != 0) { //Has move
			
			mediator.dump("TimeSaver: EGTB -> HIT " + MoveInt.moveToString(out[0]) + "	" + out[1]);
			
			ISearchInfo info = createInfo(out[0]);
			*/
		
			/*if (out[1] > 0) {
				int eval = SearchUtils.getMateVal(Math.abs(out[1]));
				info.setEval(eval);
				info.setMateScore(true);
				
				mediator.changedMajor(info);
				if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
				return true;
			}*/
			
			/*
			if (out[1] == 0) {
				info.setEval(0);
			} else {
				int eval = SearchUtils.getMateVal(Math.abs(out[1]));
				if (out[1] < 0) {
					eval = -eval;
					eval = -700;
				} else {
					//info.setMateScore(true);
				}
				info.setEval(eval);
			}
			
			mediator.changedMajor(info);
			if (mediator.getBestMoveSender() != null) mediator.getBestMoveSender().sendBestMove();
			return true;*/
		
		/*
		} else {
			mediator.dump("TimeSaver: EGTB -> No HIT");
		}
		*/
		
		return false;
	}
	
	
	private static ISearchInfo createInfo(int move, int depth) {
		ISearchInfo info = SearchInfoFactory.getFactory().createSearchInfo();
		info.setDepth(depth);
		info.setSelDepth(depth);
		info.setBestMove(move);
		info.setPV(new int[] {move});
		return info;
	}
	
	
	/*protected void setupBoard(IBitBoard _bitboardForSetup) {
		bitboard.revert();
		
		int movesCount = _bitboardForSetup.getPlayedMovesCount();
		int[] moves = _bitboardForSetup.getPlayedMoves();
		for (int i=0; i<movesCount; i++) {
			bitboard.makeMoveForward(moves[i]);
		}
	}
	
	
	private int getOpeningMove_Evaluation(IOpeningEntry entry, SharedData sharedData, IBitBoard bitboardForSetup, ISearchMediator mediator) {
		
		setupBoard(bitboardForSetup);
		
		IEvaluator eval = sharedData.getEvaluatorFactory().create(bitboard, sharedData.getAndRemoveEvalCache(), sharedData.getEngineConfiguration().getEvalConfig());
		
		double best_val = Integer.MIN_VALUE;
		int best_move = 0;
		
		int[] moves = entry.getMoves();
		for (int i=0; i<moves.length; i++) {
			
			int cur_move = moves[i];
			
			bitboard.makeMoveForward(cur_move);
			
			IOpeningEntry cur_entry = ob.getEntry(bitboard.getHashKey(), bitboard.getColourToMove());
			double cur_val = -minmaxOpening(cur_entry, bitboard, eval, 0);
			
			if (cur_val > best_val) {
				best_val = cur_val;
				best_move = cur_move;
			}
			
			bitboard.makeMoveBackward(cur_move);
			
			mediator.dump("Opening move candidate: " + MoveInt.moveToString(cur_move) + ", eval is " + cur_val);
		}
		
		return best_move;
	}
	
	
	private double minmaxOpening(IOpeningEntry entry, IBitBoard bitboard, IEvaluator eval, int depth) {
		
		if (entry == null) {
			return eval.fullEval(0, Integer.MIN_VALUE, Integer.MAX_VALUE, bitboard.getColourToMove());
		}
		
		if (bitboard.getStateRepetition() >= 2) {
			return 0;
		}
		
		if (MoveInt.isCapture(bitboard.getLastMove())) {
			//Do nothing
		} else {
			if (depth > 5) {
				return eval.fullEval(0, Integer.MIN_VALUE, Integer.MAX_VALUE, bitboard.getColourToMove());
			}
		}
		
		int[] moves = entry.getMoves();
		
		double best_val = Integer.MIN_VALUE;
		for (int i=0; i<moves.length; i++) {
			
			int cur_move = moves[i];
			
			bitboard.makeMoveForward(cur_move);
			
			IOpeningEntry cur_entry = ob.getEntry(bitboard.getHashKey(), bitboard.getColourToMove());
			
			double cur_val = -minmaxOpening(cur_entry, bitboard, eval, depth + 1);
			
			if (cur_val > best_val) {
				best_val = cur_val;
			}
			
			bitboard.makeMoveBackward(cur_move);
		}
		
		return best_val;
	}
	
	*/
}

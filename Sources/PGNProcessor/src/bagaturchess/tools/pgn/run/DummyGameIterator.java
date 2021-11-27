package bagaturchess.tools.pgn.run;


import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.tools.pgn.api.IGameIterator;
import bagaturchess.tools.pgn.api.IPlyIterator;
import bagaturchess.tools.pgn.impl.PGNGame;


public class DummyGameIterator implements IGameIterator, IPlyIterator {

	
	private int counter = 0;
	
	private IBoard bitboard;
	private IBitBoard testboard;
	
	
	DummyGameIterator() {
		testboard = BoardUtils.createBoard_WithPawnsCache();
	}
	
	@Override
	public void preIteration(IBoard _bitboard) {
		bitboard = _bitboard;
	}

	@Override
	public void postIteration() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preGame(int gameCount, PGNGame pgnGame, String pgnGameID,
			IBoard bitboard) {
		counter++;
		
		if (counter % 10000 == 0) {
			System.out.println("GAME: " + gameCount);
		}
		
		testboard.mark();
	}

	@Override
	public void postGame() {
		testboard.reset();
	}

	@Override
	public void preMove(int colour, int move, IBoard bitboard, int moveNumber) {
		testboard.makeMoveForward(move);
		
		if (bitboard.getMoveOps().isCaptureOrPromotion(move)
				&& !bitboard.getMoveOps().isEnpassant(move)
				) {
			
			IMoveList tmp_list = new BaseMoveList();
			bitboard.genCapturePromotionMoves(tmp_list);
			
			boolean found = false;
			int cur_move = 0;
			while((cur_move = tmp_list.next()) != 0) {
				if (cur_move == move) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				throw new IllegalStateException();
			}
		}
	}

	@Override
	public void postMove() {
		//System.out.println(bitboard);
		
		
		boolean check1 = bitboard.isInCheck();
		boolean check2 = testboard.isInCheck();
		if (check1 != check2) {
			bitboard.isInCheck();
			throw new IllegalStateException();
		}
		
		long pawnhashkey1 = bitboard.getPawnsHashKey();
		long pawnhashkey2 = testboard.getPawnsHashKey();
		if (pawnhashkey1 != pawnhashkey2) {
			throw new IllegalStateException();
		}
		
		long hashkey1 = bitboard.getHashKey();
		long hashkey2 = testboard.getHashKey();
		if (hashkey1 != hashkey2) {
			throw new IllegalStateException();
		}
		
		int staterep1 = bitboard.getStateRepetition();
		int staterep2 = testboard.getStateRepetition();
		if (staterep1 != staterep2) {
			throw new IllegalStateException();
		}
		
		int cast1 = bitboard.getCastlingType(bitboard.getColourToMove());
		int cast2 = testboard.getCastlingType(testboard.getColourToMove());
		if (cast1 != cast2) {
			throw new IllegalStateException();
		}
		
		int draw50_1 = bitboard.getDraw50movesRule();
		int draw50_2 = testboard.getDraw50movesRule();
		if (draw50_1 != draw50_2) {
			throw new IllegalStateException();
		}
		
		boolean draw50b_1 = bitboard.isDraw50movesRule();
		boolean draw50b_2 = testboard.isDraw50movesRule();
		if (draw50b_1 != draw50b_2) {
			throw new IllegalStateException();
		}
		
		boolean suffMat1 = bitboard.hasSufficientMatingMaterial();
		boolean suffMat2 = testboard.hasSufficientMatingMaterial();
		if (suffMat1 != suffMat2) {
			throw new IllegalStateException();
		}
		
	}

}

package bagaturchess.bitboard.impl3;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.eval.BaseEvaluation;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl1.movegen.MoveInt;
import bagaturchess.bitboard.impl3.internal.ChessBoard;
import bagaturchess.bitboard.impl3.internal.ChessBoardUtil;
import bagaturchess.bitboard.impl3.internal.MoveGenerator;
import bagaturchess.bitboard.impl3.internal.MoveUtil;

import static bagaturchess.bitboard.impl3.internal.ChessConstants.WHITE;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl3.internal.ChessConstants.KING;


public class BoardImpl implements IBitBoard {

	
	private ChessBoard board;
	private MoveGenerator moveGen;
	
	private PiecesListsImpl dummyPiecesLists = new PiecesListsImpl(this);
	
	public BoardImpl() {
		this(Constants.INITIAL_BOARD);
	}
	
	
	public BoardImpl(String fen) {
		
		ChessBoard.initInstances(1);
		
		board = ChessBoard.getInstance();
		ChessBoardUtil.setFenValues(fen, board);
		ChessBoardUtil.init(board);
		
		moveGen = new MoveGenerator();
	}
	
	
	@Override
	public boolean isInCheck() {
		return board.checkingPieces != 0;
	}
	
	
	@Override
	public int getColourToMove() {
		return board.colorToMove == WHITE ? Constants.COLOUR_WHITE : Constants.COLOUR_BLACK;
	}
	
	
	@Override
	public int genAllMoves(IInternalMoveList list) {
		int count = 0;
		moveGen.startPly();
		moveGen.generateMoves(board);
		while (moveGen.hasNext()) {
			final int move = moveGen.next();
			list.reserved_add(convertMove_toBagatur(move));
			count++;
		}
		moveGen.endPly();
		
		//System.out.println(count);
		
		return count;
	}
	
	
	@Override
	public void makeMoveForward(int move) {
		board.doMove(convertMove_fromBagatur(move));
	}

	
	@Override
	public void makeMoveBackward(int move) {
		board.undoMove(convertMove_fromBagatur(move));
	}
	
	
	private int convertMove_toBagatur(int move) {
		int fromIndex = MoveUtil.getFromIndex(move);
		int toIndex = MoveUtil.getFromIndex(move);
		
		//MoveInt.createNonCapture(pid, fromIndex, toIndex);
		
		return move;
	}
	
	
	private int convertMove_fromBagatur(int move) {
		int fromIndex = MoveUtil.getFromIndex(move);
		int toIndex = MoveUtil.getFromIndex(move);
		
		//MoveInt.createNonCapture(pid, fromIndex, toIndex);
		
		return move;
	}
	
	
	@Override
	public void revert() {
		//Do nothing
	}
	
	
	@Override
	public long getHashKey() {
		return board.zobristKey;
	}

	
	@Override
	public long getPawnsHashKey() {
		return board.pawnZobristKey;
	}
	
	
	@Override
	public IPiecesLists getPiecesLists() {
		return dummyPiecesLists;
	}
	
	
	@Override
	public IMaterialFactor getMaterialFactor() {
		return new IMaterialFactor() {
			
			@Override
			public int interpolateByFactorAndColour(int colour, int val_o, int val_e) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int interpolateByFactor(double val_o, double val_e) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int interpolateByFactor(int val_o, int val_e) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getWhiteFactor() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getTotalFactor() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public double getOpenningPart() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getBlackFactor() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}
	
	
	@Override
	public IBaseEval getBaseEvaluation() {
		return new BaseEvaluation(new BoardConfigImpl_V17(), getMaterialFactor());
	}
	
	
	@Override
	public long getFiguresBitboardByColourAndType(int colour, int type) {
		return board.pieces[colour == Constants.COLOUR_WHITE ? WHITE : BLACK][type];
	}
	
	
	@Override
	public long getFiguresBitboardByColour(int colour) {
		return getFiguresBitboardByColourAndType(colour, PAWN)
				| getFiguresBitboardByColourAndType(colour, NIGHT)
				| getFiguresBitboardByColourAndType(colour, BISHOP)
				| getFiguresBitboardByColourAndType(colour, ROOK)
				| getFiguresBitboardByColourAndType(colour, QUEEN)
				| getFiguresBitboardByColourAndType(colour, KING);
	}
	
	
	@Override
	public long getFreeBitboard() {
		return ~board.allPieces;
	}
	
	
	@Override
	public int getDraw50movesRule() {
		return 100;//TODO
	}
	
	
	@Override
	public boolean isDraw50movesRule() {
		return false;
	}
	
	
	@Override
	public PawnsEvalCache getPawnsCache() {
		return null;
	}
	
	
	@Override
	public void setPawnsCache(PawnsEvalCache pawnsCache) {
		//Do nothing
	}
	
	
	@Override
	public int getStateRepetition() {
		return 1;//TODO
	}
	
	
	@Override
	public boolean hasSufficientMaterial() {
		return true;
	}
	
	
	@Override
	public boolean isPasserPush(int move) {
		return false;
	}
	
	
	@Override
	public int getLastMove() {
		return 0;
	}
	
	
	/**
	 * NOT IMPLEMENTED METHODS
	 */
	
	
	@Override
	public int[] getMatrix() {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public PawnsModelEval getPawnsStructure() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IBoardConfig getBoardConfig() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genKingEscapes(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genCapturePromotionMoves(IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields,
			IInternalMoveList list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void makeNullMoveForward() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void makeNullMoveBackward() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFigureID(int fieldID) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISEE getSee() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void mark() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toEPD() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IMaterialState getMaterialState() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getUnstoppablePasser() {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public boolean isInCheck(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasMoveInCheck() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasMoveInNonCheck() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCheckMove(int move) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPossible(int move) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasSingleMove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getCastlingType(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasRightsToKingCastle(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasRightsToQueenCastle(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPlayedMovesCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[] getPlayedMoves() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IGameStatus getStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getFiguresBitboardByPID(int pid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getAttacksSupport() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getFieldsStateSupport() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttacksSupport(boolean attacksSupport,
			boolean fieldsStateSupport) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPlayerAttacks getPlayerAttacks(int colour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IFieldsAttacks getFieldsAttacks() {
		throw new UnsupportedOperationException();
	}
 
	
	public IBoard clone() {
		return null;
	}
	
	
	private static final class PiecesListsImpl implements IPiecesLists {
		
		
		private PiecesList dummy;
		
		
		PiecesListsImpl(IBitBoard board) {
			dummy = new PiecesList(board, 8);
			dummy.add(1);
			dummy.add(2);
			dummy.add(3);
			dummy.add(4);
			dummy.add(5);
			dummy.add(6);
			dummy.add(7);
			dummy.add(8);
		}
		
		@Override
		public void rem(int pid, int fieldID) {
			// TODO Auto-generated method stub
			
		}
		
		
		@Override
		public void add(int pid, int fieldID) {
			// TODO Auto-generated method stub
			
		}
		
		
		@Override
		public void move(int pid, int fromFieldID, int toFieldID) {
			// TODO Auto-generated method stub
			
		}
		
		
		@Override
		public PiecesList getPieces(int pid) {
			return dummy;
		}
	}
}

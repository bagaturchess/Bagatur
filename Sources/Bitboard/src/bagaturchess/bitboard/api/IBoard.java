package bagaturchess.bitboard.api;

import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;


public interface IBoard {
	
	
	public int[] getMatrix();
	
	public IBoardConfig getBoardConfig();
	public IPiecesLists getPiecesLists();
	public int getColourToMove();
	
	public int genAllMoves(final IInternalMoveList list);
	public int genCapturePromotionMoves(final IInternalMoveList list);
	
	public void makeMoveForward(final int move);
	public void makeMoveBackward(final int move);
	
	public void makeNullMoveForward();
	public void makeNullMoveBackward();
	
	public long getHashKey();
	public long getPawnsHashKey();
	public int getStateRepetition();
	
	public int getFigureID(int fieldID);
	
	public ISEE getSee();
	
	public void mark();
	
	public void reset();
	
	public String toEPD();
	
	public IMaterialState getMaterialState();
	public IMaterialFactor getMaterialFactor();
	public IBaseEval getBaseEvaluation();
	public PawnsEvalCache getPawnsCache();
	public PawnsModelEval getPawnsStructure();
	public boolean isPasserPush(int move);
	public int getCastlingType(int colour);
	public boolean hasUnstoppablePasser();
	public int getUnstoppablePasser();
	
	public boolean isDraw50movesRule();
	public int getDraw50movesRule();
	public boolean hasSufficientMaterial();
	
	public IBoard clone();
	
	
	//TODO: remove
	public long getFreeBitboard();
	public long getFiguresBitboardByPID(int pid);
	public long getFiguresBitboardByColourAndType(int colour, int type);
	public long getFiguresBitboardByColour(int colour);
	//public void setPawnsCache(PawnsEvalCache pawnsCache);
	public boolean isInCheck();
	public boolean isInCheck(int colour);
	public boolean hasMoveInCheck();
	public boolean hasMoveInNonCheck();

	public boolean hasRightsToKingCastle(int colourToMove);

	public boolean hasRightsToQueenCastle(int colourToMove);

}

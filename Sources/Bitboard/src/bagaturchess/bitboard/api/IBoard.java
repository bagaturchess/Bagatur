package bagaturchess.bitboard.api;

import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;


public interface IBoard {
	
	public int[] getMatrix();
	
	public PawnsEvalCache getPawnsCache();
	public void setPawnsCache(PawnsEvalCache pawnsCache);
	
	public PawnsModelEval getPawnsStructure();
	
	public IBoardConfig getBoardConfig();
	public IPiecesLists getPiecesLists();
	public int getColourToMove();
	
	public int genAllMoves(final IInternalMoveList list);
	public int genKingEscapes(final IInternalMoveList list);
	public int genCapturePromotionMoves(final IInternalMoveList list);
	public int genNonCaptureNonPromotionMoves(final IInternalMoveList list);
	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields, final IInternalMoveList list);
	
	public int getEnpassantSquareID();
	
	public void makeMoveForward(final int move);
	public void makeMoveForward(final String ucimove);
	public void makeMoveBackward(final int move);
	
	public void makeNullMoveForward();
	public void makeNullMoveBackward();
	
	public long getHashKey();
	public long getHashKeyAfterMove(final int move);
	public long getPawnsHashKey();
	public int getStateRepetition();
	
	public int getFigureID(int fieldID);
	public int getFigureType(int fieldID);
	public int getFigureColour(int fieldID);
	
	public ISEE getSee();
	public int getSEEScore(int move);
	public int getSEEFieldScore(int squareID);
	
	public IMoveOps getMoveOps();
	
	public void mark();
	public void reset();
	public void revert();
	
	public String toEPD();
	
	public IMaterialState getMaterialState();
	public IMaterialFactor getMaterialFactor();
	public IBaseEval getBaseEvaluation();
	
	public boolean isPasserPush(int move);
	public int getUnstoppablePasser();
	
	public boolean isDraw50movesRule();
	public int getDraw50movesRule();
	public boolean hasSufficientMatingMaterial();
	public boolean hasSufficientMatingMaterial(int color);
	
	//public IBoard clone();
	
	public boolean isInCheck();
	public boolean isInCheck(int colour);
	public boolean hasMoveInCheck();
	public boolean hasMoveInNonCheck();
	public boolean isCheckMove(int move);
	
	public boolean isPossible(int move);
	
	public boolean hasSingleMove();
	
	public int getCastlingType(int colour);
	public boolean hasRightsToKingCastle(int colour);
	public boolean hasRightsToQueenCastle(int colour);
	
	/**
	 * Game related methods
	 */
	public int getPlayedMovesCount();
	public int[] getPlayedMoves();
	public int getLastMove();
	public IGameStatus getStatus();
}

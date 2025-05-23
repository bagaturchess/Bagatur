package bagaturchess.bitboard.impl2;


import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;


public class BoardImpl implements IBitBoard{

	@Override
	public CastlingConfig getCastlingConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PawnsEvalCache getPawnsCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPawnsCache(PawnsEvalCache pawnsCache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PawnsModelEval getPawnsStructure() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBoardConfig getBoardConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPiecesLists getPiecesLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColourToMove() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int genAllMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int genKingEscapes(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int genCapturePromotionMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int genAllMoves_ByFigureID(int fieldID, long excludedToFields,
			IInternalMoveList list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEnpassantSquareID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void makeMoveForward(int move) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void makeMoveForward(String ucimove) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void makeMoveBackward(int move) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void makeNullMoveForward() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void makeNullMoveBackward() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getHashKey() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getHashKeyAfterMove(int move) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPawnsHashKey() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStateRepetition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFigureID(int fieldID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFigureType(int fieldID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFigureColour(int fieldID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ISEE getSee() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSEEScore(int move) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSEEFieldScore(int squareID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IMoveOps getMoveOps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mark() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void revert() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toEPD() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMaterialState getMaterialState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMaterialFactor getMaterialFactor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBaseEval getBaseEvaluation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPasserPush(int move) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getUnstoppablePasser() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDraw50movesRule() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDraw50movesRule() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasSufficientMatingMaterial() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSufficientMatingMaterial(int color) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInCheck(int colour) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMoveInCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMoveInNonCheck() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCheckMove(int move) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPossible(int move) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSingleMove() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CastlingType getCastlingType(int colour) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CastlingPair getCastlingPair() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRightsToKingCastle(int colour) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRightsToQueenCastle(int colour) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPlayedMovesCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getPlayedMoves() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLastMove() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IGameStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getNNUEInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addMoveListener(MoveListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getFreeBitboard() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFiguresBitboardByPID(int pid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFiguresBitboardByColourAndType(int colour, int type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getFiguresBitboardByColour(int colour) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getAttacksSupport() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getFieldsStateSupport() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAttacksSupport(boolean attacksSupport,
			boolean fieldsStateSupport) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IPlayerAttacks getPlayerAttacks(int colour) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFieldsAttacks getFieldsAttacks() {
		// TODO Auto-generated method stub
		return null;
	}

}

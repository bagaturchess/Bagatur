package bagaturchess.engines.bagatur.v14;

public interface IPosition {

	public abstract long getPieceTypeBB(int pid);

	public abstract long getWhiteBB();

	public abstract long getBlackBB();

	public abstract int getPIDBySquare(int squareID);

	public abstract int getWKingSQ();

	public abstract int getBKingSQ();

	public abstract int getPST1(int pid);

	public abstract int getPST2(int pid);

	public abstract int getPST1();

	public abstract int getPST2();
	
	public abstract long pawnZobristHash();

	public abstract long kingZobristHash();

	public abstract int getKingSq(boolean whiteMove);

	public abstract int getwMtrl();

	public abstract int getbMtrl();

	public abstract int getwMtrlPawns();

	public abstract int getbMtrlPawns();

	public abstract boolean isWhiteMove();

	//Castling
	public abstract int getCastleMask();
	public abstract boolean a1Castle();
	public abstract boolean h1Castle();
	public abstract boolean a8Castle();
	public abstract boolean h8Castle();

	public abstract int getY(int sq);

	public abstract int getX(int sq);

}
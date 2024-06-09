package bagaturchess.nnue_v2;


public class Accumulators
{
	private final NNUE.NNUEAccumulator whiteAccumulator;
	private final NNUE.NNUEAccumulator blackAccumulator;
	
	public Accumulators(NNUE network)
	{
		whiteAccumulator = new NNUE.NNUEAccumulator(network, -1);
		blackAccumulator = new NNUE.NNUEAccumulator(network, -1);
	}
	
	public void fullAccumulatorUpdate(int white_king_sq, int black_king_sq, int[] white_pieces, int[] white_squares, int[] black_pieces, int[] black_squares)
	{
		whiteAccumulator.reset();
		blackAccumulator.reset();
		
		whiteAccumulator.setBucketIndex(NNUE.chooseInputBucket(white_king_sq, NNUE.WHITE));
		blackAccumulator.setBucketIndex(NNUE.chooseInputBucket(black_king_sq, NNUE.BLACK));
		
		for (int i = 0; i < white_pieces.length; i++) {
			if (white_pieces[i] == -1) {
				break;
			}
			whiteAccumulator.add(NNUE.getIndex(white_squares[i], NNUE.WHITE, white_pieces[i], NNUE.WHITE));
			blackAccumulator.add(NNUE.getIndex(white_squares[i], NNUE.WHITE, white_pieces[i], NNUE.BLACK));
		}
		
		for (int i = 0; i < black_pieces.length; i++) {
			if (black_pieces[i] == -1) {
				break;
			}
			whiteAccumulator.add(NNUE.getIndex(black_squares[i], NNUE.BLACK, black_pieces[i], NNUE.WHITE));
			blackAccumulator.add(NNUE.getIndex(black_squares[i], NNUE.BLACK, black_pieces[i], NNUE.BLACK));
		}
	}
	
	public NNUE.NNUEAccumulator getWhiteAccumulator()
	{
		return whiteAccumulator;
	}
	
	public NNUE.NNUEAccumulator getBlackAccumulator()
	{
		return blackAccumulator;
	}
}

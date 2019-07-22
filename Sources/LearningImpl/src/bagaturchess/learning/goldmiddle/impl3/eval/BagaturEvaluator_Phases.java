package bagaturchess.learning.goldmiddle.impl3.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.evalcache.IEvalCache;


public class BagaturEvaluator_Phases extends BaseEvaluator {
	
	
	public static final int Direction_NORTH 		= 8;
	public static final int Direction_EAST 			= 1;
	public static final int Direction_SOUTH 		= -Direction_NORTH;//-8
	public static final int Direction_WEST 			= -Direction_EAST;//-1
	public static final int Direction_NORTH_EAST 	= Direction_NORTH + Direction_EAST;//9
	public static final int Direction_SOUTH_EAST 	= Direction_SOUTH + Direction_EAST;//-7
	public static final int Direction_SOUTH_WEST 	= Direction_SOUTH + Direction_WEST;//-9
	public static final int Direction_NORTH_WEST 	= Direction_NORTH + Direction_WEST;//7
	
	public static final long AllSquares 			= ~0;
	public static final long DarkSquares 			= 0xAA55AA55AA55AA55L;
	public static final long LightSquares 			= ~DarkSquares;

	public static final long FileABB = 0x0101010101010101L;
	public static final long FileBBB = FileABB << 1;
	public static final long FileCBB = FileABB << 2;
	public static final long FileDBB = FileABB << 3;
	public static final long FileEBB = FileABB << 4;
	public static final long FileFBB = FileABB << 5;
	public static final long FileGBB = FileABB << 6;
	public static final long FileHBB = FileABB << 7;
	
	public static final long Rank1BB = 0xFF;
	public static final long Rank2BB = Rank1BB << (8 * 1);
	public static final long Rank3BB = Rank1BB << (8 * 2);
	public static final long Rank4BB = Rank1BB << (8 * 3);
	public static final long Rank5BB = Rank1BB << (8 * 4);
	public static final long Rank6BB = Rank1BB << (8 * 5);
	public static final long Rank7BB = Rank1BB << (8 * 6);
	public static final long Rank8BB = Rank1BB << (8 * 7);
	
	
	protected final EvalInfo evalinfo;
	
	
	public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
		
		super(_bitboard, _evalCache, _evalConfig);
		
		evalinfo = new EvalInfo();
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase1()
	 */
	@Override
	protected double phase1() {
		int eval = 0;
		
		evalinfo.clearEvals1();
		calculateMaterialScore();
		
		eval = bitboard.getMaterialFactor().interpolateByFactor(
					baseEval.getPST_o() + evalinfo.eval_o_part1,
					baseEval.getPST_e() + evalinfo.eval_e_part1
				);
		
		return eval;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase2()
	 */
	@Override
	protected double phase2() {
		int eval = 0;
		
		if ((FileABB & Fields.A1) == 0) {
			//throw new IllegalStateException();
		}
		
		return eval;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase3()
	 */
	@Override
	protected double phase3() {
		int eval = 0;
		
		return eval;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase4()
	 */
	@Override
	protected double phase4() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	/* (non-Javadoc)
	 * @see bagaturchess.search.impl.eval.BaseEvaluator#phase5()
	 */
	@Override
	protected double phase5() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	public void calculateMaterialScore() {
		
		
		int w_eval_nopawns_o = baseEval.getWhiteMaterialNonPawns_o();
		int w_eval_nopawns_e = baseEval.getWhiteMaterialNonPawns_e();
		int b_eval_nopawns_o = baseEval.getBlackMaterialNonPawns_o();
		int b_eval_nopawns_e = baseEval.getBlackMaterialNonPawns_e();
		
		int w_eval_pawns_o = baseEval.getWhiteMaterialPawns_o();
		int w_eval_pawns_e = baseEval.getWhiteMaterialPawns_e();
		int b_eval_pawns_o = baseEval.getBlackMaterialPawns_o();
		int b_eval_pawns_e = baseEval.getBlackMaterialPawns_e();

		evalinfo.eval_o_part1 += (w_eval_nopawns_o - b_eval_nopawns_o) + (w_eval_pawns_o - b_eval_pawns_o);
		evalinfo.eval_e_part1 += (w_eval_nopawns_e - b_eval_nopawns_e) + (w_eval_pawns_e - b_eval_pawns_e);
	}
	
	
	public static long shiftBB(final long b, final int direction)
	{
		switch (direction) {
			case Direction_NORTH:
				return b << 8;
			case Direction_SOUTH:
				return b >>> 8;
			case Direction_EAST:
				return (b & ~FileHBB) << 1;
			case Direction_WEST:
				return (b & ~FileABB) >>> 1;
			case Direction_NORTH_EAST:
				return (b & ~FileHBB) << 9;
			case Direction_NORTH_WEST:
				return (b & ~FileABB) << 7;
			case Direction_SOUTH_EAST:
				return (b & ~FileHBB) >>> 7;
			case Direction_SOUTH_WEST:
				return (b & ~FileABB) >>> 9;
			default:
				throw new IllegalStateException("must be return 0");
		}
	}
	
	
	public static int file_of(int squareID)
	{
	  return squareID & 7;
	}
	
	
	public static int rank_of(int squareID)
	{
	  return squareID >>> 3;
	}
	
	
	protected static class EvalInfo {
		
		
		public int eval_o_part1;
		public int eval_e_part1;
		public int eval_o_part2;
		public int eval_e_part2;
		
		
		public void clearEvals1() {
			eval_o_part1 = 0;
			eval_e_part1 = 0;
		}
		
		
		public void clearEvals2() {
			eval_o_part2 = 0;
			eval_e_part2 = 0;
		}
	}
	
	
	protected static class Pawns {
		
		
		public int[] scores = new int[Constants.COLOUR_BLACK + 1];
		public long[] passedPawns = new long[Constants.COLOUR_BLACK + 1];
		public long[] pawnAttacks = new long[Constants.COLOUR_BLACK + 1];
		public long[] pawnAttacksSpan = new long[Constants.COLOUR_BLACK + 1];
		public int[] kingSquares = new int[Constants.COLOUR_BLACK + 1];
		public int[] kingSafety = new int[Constants.COLOUR_BLACK + 1];
		public int[] weakUnopposed = new int[Constants.COLOUR_BLACK + 1];
		public int[] castlingRights = new int[Constants.COLOUR_BLACK + 1];
		public int[] semiopenFiles = new int[Constants.COLOUR_BLACK + 1];
		public int[][] pawnsOnSquares = new int[Constants.COLOUR_BLACK + 1][Constants.COLOUR_BLACK + 1]; // [color][light/dark squares]
		public int asymmetry;
		public int openFiles;
		  
		  
		public int evaluate(IBitBoard bitboard, int Us, PiecesList Us_pawns_list) {

			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			final int Up_direction = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);

			long b;
			long neighbours;
			long stoppers;
			long doubled;
			long supported;
			long phalanx;
			long lever;
			long leverPush;
			//int s;//square
			boolean opposed;
			boolean backward;
			int score = 0;
			
			long ourPawns = bitboard.getFiguresBitboardByColourAndType(Us, Constants.TYPE_PAWN);
			long theirPawns = bitboard.getFiguresBitboardByColourAndType(Them, Constants.TYPE_PAWN);

			passedPawns[Us] = pawnAttacksSpan[Us] = weakUnopposed[Us] = 0;
			semiopenFiles[Us] = 0xFF;
			kingSquares[Us] = -1;
			pawnAttacks[Us] = pawn_attacks_bb(ourPawns, Us);
			pawnsOnSquares[Us][Constants.COLOUR_BLACK] = Long.bitCount(ourPawns & DarkSquares);
			pawnsOnSquares[Us][Constants.COLOUR_WHITE] = Long.bitCount(ourPawns & LightSquares);
			
			//C++ TO JAVA CONVERTER TODO TASK: Pointer arithmetic is detected on this variable, so pointers on this variable are left unchanged:
			//Square * pl = pos.<PieceType.PAWN.getValue()>squares(Us);
            int pawns_count = Us_pawns_list.getDataSize();
            if (pawns_count > 0) {
	            int[] pawns_fields = Us_pawns_list.getData();
	            for (int i=0; i<pawns_count; i++) {
	            	int squareID = pawns_fields[i];
	            	int fileID = file_of(squareID);
	            	
	            	/*
					semiopenFiles[Us] &= ~(1 << fileID);
					pawnAttacksSpan[Us] |= pawn_attack_span(Us, s);
					
					// Flag the pawn
					opposed = theirPawns & forward_file_bb(Us, s) != null;
		//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
		//ORIGINAL LINE: stoppers = theirPawns & passed_pawn_mask(Us, s);
					stoppers.copyFrom(theirPawns & passed_pawn_mask(Us, s));
		//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
		//ORIGINAL LINE: lever = theirPawns & PawnAttacks[Us][s];
					lever.copyFrom(theirPawns & PawnAttacks[Us][s.getValue()]);
		//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
		//ORIGINAL LINE: leverPush = theirPawns & PawnAttacks[Us][s + Up];
					leverPush.copyFrom(theirPawns & PawnAttacks[Us][s.getValue() + Up_direction]);
		//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
		//ORIGINAL LINE: doubled = ourPawns & (s - Up);
					doubled.copyFrom(ourPawns & (s - Up_direction));
		//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
		//ORIGINAL LINE: neighbours = ourPawns & adjacent_files_bb(f);
					neighbours.copyFrom(ourPawns & adjacent_files_bb(f));
		//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
		//ORIGINAL LINE: phalanx = neighbours & rank_bb(s);
					phalanx.copyFrom(neighbours & rank_bb(s));
		//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
		//ORIGINAL LINE: supported = neighbours & rank_bb(s - Up);
					supported.copyFrom(neighbours & rank_bb(s - Up_direction));
	
					// A pawn is backward when it is behind all pawns of the same color
					// on the adjacent files and cannot be safely advanced.
					backward = (ourPawns & pawn_attack_span(Them, s + Up_direction)) == null && (stoppers & (leverPush | (s + Up_direction))) != null;
	
					// Passed pawns will be properly scored in evaluation because we need
					// full attack info to evaluate them. Include also not passed pawns
					// which could become passed after one or two pawn pushes when are
					// not attacked more times than defended.
					if ((stoppers ^ lever ^ leverPush) == null && popcount(new uint64_t(supported)) >= popcount(new uint64_t(lever)) - 1 && popcount(new uint64_t(phalanx)) >= popcount(new uint64_t(leverPush)))
					{
						passedPawns[Us] |= s;
					}
	
					else if (stoppers == SquareBB[s.getValue() + Up_direction] && relative_rank(Us, s) >= Rank.RANK_5.getValue())
					{
		//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
		//ORIGINAL LINE: b = shift<Up>(supported) & ~theirPawns;
						b.copyFrom(GlobalMembers.<Up>shift(new uint64_t(supported)) & ~theirPawns);
						while (b != null)
						{
							if (!more_than_one(theirPawns & PawnAttacks[Us][pop_lsb(b).getValue()]))
							{
								e.passedPawns[Us] |= s;
							}
						}
					}
	
					// Score this pawn
					if (supported | phalanx != null)
					{
						score += Connected[opposed][(boolean)phalanx][popcount(new uint64_t(supported))][relative_rank(Us, s).getValue()];
					}
	
					else if (!neighbours)
					{
						score -= Isolated, e.weakUnopposed[Us] += !opposed;
					}
	
					else if (backward)
					{
						score -= Backward, e.weakUnopposed[Us] += !opposed;
					}
	
					if (doubled != null && supported == null)
					{
						score -= Doubled;
					}
					*/
	            }
			}

			return score;
		}
		
		
		public static long pawn_attacks_bb(final long b, final int colour)
		{
		  return colour == Constants.COLOUR_WHITE ?
				  shiftBB(b, Direction_NORTH_WEST) | shiftBB(b, Direction_NORTH_EAST)
				  : shiftBB(b, Direction_SOUTH_WEST) | shiftBB(b, Direction_SOUTH_EAST);
		}
	}
}

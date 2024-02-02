/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.learning.goldmiddle.impl3.filler;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.BoardProxy_ReversedBBs;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.learning.api.IFeatureComplexity;
import bagaturchess.learning.api.ISignal;
import bagaturchess.learning.api.ISignalFiller;
import bagaturchess.learning.api.ISignals;
import bagaturchess.learning.goldmiddle.impl3.eval.Evaluator;


public class Bagatur_V18_SignalFiller extends Evaluator implements ISignalFiller, Bagatur_V18_FeaturesConstants {
	
	
	private PiecesList w_knights;
	private PiecesList b_knights;
	private PiecesList w_bishops;
	private PiecesList b_bishops;
	private PiecesList w_rooks;
	private PiecesList b_rooks;
	private PiecesList w_queens;
	private PiecesList b_queens;
	private PiecesList w_pawns;
	private PiecesList b_pawns;
	
	
	private PawnsFiller pawnsFiller;
	
	
	public Bagatur_V18_SignalFiller(IBitBoard bitboard) {
		
		super(bitboard);
		
		w_knights = bitboard.getPiecesLists().getPieces(Constants.PID_W_KNIGHT);
		b_knights = bitboard.getPiecesLists().getPieces(Constants.PID_B_KNIGHT);
		w_bishops = bitboard.getPiecesLists().getPieces(Constants.PID_W_BISHOP);
		b_bishops = bitboard.getPiecesLists().getPieces(Constants.PID_B_BISHOP);
		w_rooks = bitboard.getPiecesLists().getPieces(Constants.PID_W_ROOK);
		b_rooks = bitboard.getPiecesLists().getPieces(Constants.PID_B_ROOK);
		w_queens = bitboard.getPiecesLists().getPieces(Constants.PID_W_QUEEN);
		b_queens = bitboard.getPiecesLists().getPieces(Constants.PID_B_QUEEN);
		w_pawns = bitboard.getPiecesLists().getPieces(Constants.PID_W_PAWN);
		b_pawns = bitboard.getPiecesLists().getPieces(Constants.PID_B_PAWN);
		
		pawnsFiller = new PawnsFiller();
	}
	
	
	@Override
	public void fill(ISignals signals) {
		
		double openningPart = bitboard.getMaterialFactor().getOpenningPart();
		
		evalinfo.clearEvals1();
		
		fillMaterialScore(signals);
		signals.getSignal(FEATURE_ID_PIECE_SQUARE_TABLE).addStrength(interpolateInternal(bitboard.getBaseEvaluation().getPST_o(), bitboard.getBaseEvaluation().getPST_e(), openningPart), openningPart);
		//Iteration 23: Time 1651ms, Success percent before this iteration: 75.27585420536631%
		
		fillImbalances(signals);
		//Iteration 36: Time 1537ms, Success percent before this iteration: 75.47931942765015%
		
		evalinfo.clearEvals2();
		evalinfo.fillBB(bitboard);
		
		pawnsFiller.fillPawns(signals, bitboard, evalinfo, Constants.COLOUR_WHITE, bitboard.getPiecesLists().getPieces(Constants.PID_W_PAWN));
		pawnsFiller.fillPawns(signals, bitboard, evalinfo, Constants.COLOUR_BLACK, bitboard.getPiecesLists().getPieces(Constants.PID_B_PAWN));
		
		initialize(Constants.COLOUR_WHITE);
		initialize(Constants.COLOUR_BLACK);
		
		fillPieces(signals, Constants.COLOUR_WHITE, Constants.TYPE_KNIGHT);
		fillPieces(signals, Constants.COLOUR_BLACK, Constants.TYPE_KNIGHT);
		fillPieces(signals, Constants.COLOUR_WHITE, Constants.TYPE_BISHOP);
		fillPieces(signals, Constants.COLOUR_BLACK, Constants.TYPE_BISHOP);
		fillPieces(signals, Constants.COLOUR_WHITE, Constants.TYPE_ROOK);
		fillPieces(signals, Constants.COLOUR_BLACK, Constants.TYPE_ROOK);
		fillPieces(signals, Constants.COLOUR_WHITE, Constants.TYPE_QUEEN);
		fillPieces(signals, Constants.COLOUR_BLACK, Constants.TYPE_QUEEN);
		
		pawnsFiller.fillKingSafety(signals, bitboard, evalinfo, Constants.COLOUR_WHITE);
		pawnsFiller.fillKingSafety(signals, bitboard, evalinfo, Constants.COLOUR_BLACK);
		
		fillKing(signals, Constants.COLOUR_WHITE);
		fillKing(signals, Constants.COLOUR_BLACK);
		
		
		fillThreats(signals, Constants.COLOUR_WHITE);
		fillThreats(signals, Constants.COLOUR_BLACK);
		
		pawnsFiller.fillPassedPawns(signals, bitboard, evalinfo, Constants.COLOUR_WHITE);
		pawnsFiller.fillPassedPawns(signals, bitboard, evalinfo, Constants.COLOUR_BLACK);
		
		fillSpace(signals, Constants.COLOUR_WHITE);
		fillSpace(signals, Constants.COLOUR_BLACK);
	}


	@Override
	public void fillByComplexity(int complexity, ISignals signals) {
		switch(complexity) {
			case IFeatureComplexity.GROUP1:
				fill(signals);
				return;
			case IFeatureComplexity.GROUP2:
				return;
			case IFeatureComplexity.GROUP3:
				return;
			case IFeatureComplexity.GROUP4:
				return;
			case IFeatureComplexity.GROUP5:
				return;
			default:
				throw new IllegalStateException("complexity=" + complexity);
		}
	}
	
	
	public void fillMaterialScore(ISignals signals) {
		
		
		double openningPart = bitboard.getMaterialFactor().getOpenningPart();
		
		
		signals.getSignal(FEATURE_ID_MATERIAL_PAWN).addStrength(w_pawns.getDataSize() - b_pawns.getDataSize(), openningPart);
		signals.getSignal(FEATURE_ID_MATERIAL_KNIGHT).addStrength(w_knights.getDataSize() - b_knights.getDataSize(), openningPart);
		signals.getSignal(FEATURE_ID_MATERIAL_BISHOP).addStrength(w_bishops.getDataSize() - b_bishops.getDataSize(), openningPart);
		signals.getSignal(FEATURE_ID_MATERIAL_ROOK).addStrength(w_rooks.getDataSize() - b_rooks.getDataSize(), openningPart);
		signals.getSignal(FEATURE_ID_MATERIAL_QUEEN).addStrength(w_queens.getDataSize() - b_queens.getDataSize(), openningPart);
	}
	
	
	private void fillImbalances(ISignals signals) {
		
		double openningPart = bitboard.getMaterialFactor().getOpenningPart();
		
		material.initialize(evalinfo);
		int imbalance_o = (material.imbalance(Constants.COLOUR_WHITE) - material.imbalance(Constants.COLOUR_BLACK)) / 16;
		int imbalance_e = imbalance_o;
		
		signals.getSignal(FEATURE_ID_MATERIAL_IMBALANCE).addStrength(interpolateInternal(imbalance_o, imbalance_e, openningPart), openningPart);
	}
	
	
	private void fillPieces(ISignals signals, int Us, int pieceType) {
		
		double openningPart = bitboard.getMaterialFactor().getOpenningPart();
		
		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		final int Direction_Down = (Us == Constants.COLOUR_WHITE ? Direction_SOUTH : Direction_NORTH);
		final long OutpostRanks = (Us == Constants.COLOUR_WHITE ? Rank4BB | Rank5BB | Rank6BB : Rank5BB | Rank4BB | Rank3BB);

		long b;
		long bb;
		
		evalinfo.attackedBy[Us][pieceType] = 0;
		
		PiecesList pieces_list = bitboard.getPiecesLists().getPieces(Figures.getPidByColourAndType(Us, pieceType));
		
        int pieces_count = pieces_list.getDataSize();
        if (pieces_count > 0) {
            int[] pieces_fields = pieces_list.getData();
            for (int i=0; i<pieces_count; i++) {
            	
            	int squareID = pieces_fields[i];
            	long squareBB = SquareBB[squareID];
            	
	      		// Find attacked squares, including x-ray attacks for bishops and rooks
	      		b = pieceType == Constants.TYPE_BISHOP ? attacks_bb(Constants.TYPE_BISHOP, squareID, evalinfo.bb_all ^ (evalinfo.bb_queens[Constants.COLOUR_WHITE] | evalinfo.bb_queens[Constants.COLOUR_BLACK]))
	      						: pieceType == Constants.TYPE_ROOK ? attacks_bb(Constants.TYPE_ROOK, squareID,
	      									evalinfo.bb_all
		      								^ (evalinfo.bb_queens[Constants.COLOUR_WHITE] | evalinfo.bb_queens[Constants.COLOUR_BLACK])
		      								^ (evalinfo.bb_rooks[Constants.COLOUR_WHITE] | evalinfo.bb_rooks[Constants.COLOUR_BLACK])
	      								)
	      						: attacks_from(bitboard, squareID, pieceType);
	      		
	      		/*
	      		//TODO implement blockers_for_king
	      		if ((pos.blockers_for_king(Us) & squareBB) != 0) {
	      			b &= LineBB[pos.<PieceType.KING.getValue().getValue()>square(Us)][squareID];
	      		}*/
	      		
	      		evalinfo.attackedBy2[Us] |= evalinfo.attackedBy[Us][Constants.TYPE_ALL] & b;
	      		evalinfo.attackedBy[Us][pieceType] |= b;
	      		evalinfo.attackedBy[Us][Constants.TYPE_ALL] |= b;
	      		
	      		if ((b & evalinfo.kingRing[Them]) != 0) {
	      			evalinfo.kingAttackersCount[Us]++;
	      			evalinfo.kingAttackersWeight[Us] += KingAttackWeights[pieceType];
	      			evalinfo.kingAttacksCount[Us] += Long.bitCount(b & evalinfo.attackedBy[Them][Constants.TYPE_KING]);
	      		}
	      		
	      		int mob = Long.bitCount(b & evalinfo.mobilityArea[Us]);
	      		
	      		addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(MobilityBonus_O[pieceType - 2][mob], MobilityBonus_E[pieceType - 2][mob], openningPart), openningPart);
	      		
	      		if (pieceType == Constants.TYPE_BISHOP || pieceType == Constants.TYPE_KNIGHT) {
	      			
	      			// Bonus if piece is on an outpost square or can reach one
	      			bb = (OutpostRanks & ~pawnsFiller.pawnAttacksSpan[Them]);
	      			if ((bb & squareBB) != 0) {
	      				int o = Outpost_O[(pieceType == Constants.TYPE_BISHOP) ? 1 : 0][(evalinfo.attackedBy[Us][Constants.TYPE_PAWN] & squareBB) == 0 ? 0 : 1] * 2;
	      				int e = Outpost_E[(pieceType == Constants.TYPE_BISHOP) ? 1 : 0][(evalinfo.attackedBy[Us][Constants.TYPE_PAWN] & squareBB) == 0 ? 0 : 1] * 2;
	      				addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(o, e, openningPart), openningPart);
	      				
	      			} else if ((bb &= (b & ~evalinfo.bb_all_pieces[Us])) != 0) {
	      				int o = Outpost_O[(pieceType == Constants.TYPE_BISHOP) ? 1 : 0][(evalinfo.attackedBy[Us][Constants.TYPE_PAWN] & bb) == 0 ? 0 : 1 ];
	      				int e = Outpost_E[(pieceType == Constants.TYPE_BISHOP) ? 1 : 0][(evalinfo.attackedBy[Us][Constants.TYPE_PAWN] & bb) == 0 ? 0 : 1 ];
	      				addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(o, e, openningPart), openningPart);
	      			}
	      			  
	      			// Knight and Bishop bonus for being right behind a pawn
	      			if ((shiftBB(evalinfo.bb_pawns[Constants.COLOUR_WHITE] | evalinfo.bb_pawns[Constants.COLOUR_BLACK], Direction_Down) & squareBB) != 0) {
	      				addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(MinorBehindPawn_O, MinorBehindPawn_E, openningPart), openningPart);
	      			}
	
	      			// Penalty if the piece is far from the king
	      			int multiplier = distance(squareID, getKingSquareID(evalinfo, Us));
	      			addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(-KingProtector_O * multiplier, -KingProtector_E * multiplier, openningPart), openningPart);
	      			
	      			if (pieceType == Constants.TYPE_BISHOP) {
	      				// Penalty according to number of pawns on the same color square as the
	      				// bishop, bigger when the center files are blocked with pawns.
	      				long blocked = evalinfo.bb_pawns[Us] & shiftBB(evalinfo.bb_all, Direction_Down);
	      				
	      				multiplier = pawnsFiller.pawns_on_same_color_squares(Us, squareID) * (1 + Long.bitCount(blocked & CenterFiles));
	      				addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(-BishopPawns_O * multiplier, -BishopPawns_E * multiplier, openningPart), openningPart);
	      				
	      				// Bonus for bishop on a long diagonal which can "see" both center squares
	      				if (more_than_one(attacks_bb(Constants.TYPE_BISHOP, squareID, evalinfo.bb_pawns[Constants.COLOUR_WHITE] | evalinfo.bb_pawns[Constants.COLOUR_BLACK]) & Center)) {
	      					addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(LongDiagonalBishop_O, LongDiagonalBishop_E, openningPart), openningPart);
	      				}
	      			}
	      		}
	
	      		if (pieceType == Constants.TYPE_ROOK)
	      		{
	      			// Bonus for aligning rook with enemy pawns on the same rank/file
	      			if (relative_rank_bySquare(Us, squareID) >= Rank5) {
	      				int multiplier = Long.bitCount(evalinfo.bb_pawns[Them] & PseudoAttacks[Constants.TYPE_ROOK][squareID]);
	      				addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(RookOnPawn_O * multiplier, RookOnPawn_E * multiplier, openningPart), openningPart);
	      			}
	
	      			// Bonus for rook on an open or semi-open file
	      			if (pawnsFiller.semiopen_file(Us, file_of(squareID)) != 0) {
	      				int index = pawnsFiller.semiopen_file(Them, file_of(squareID)) == 0 ? 0 : 1;
	      				addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(RookOnFile_O[index], RookOnFile_E[index], openningPart), openningPart);
	      			}
	
	      			// Penalty when trapped by the king, even more if the king cannot castle
	      			else if (mob <= 3) {
	      				int kf = file_of(getKingSquareID(evalinfo, Us));
	      				if ((kf < FileE) == (file_of(squareID) < kf)) {
	      					int o = (TrappedRook_O - mob * 22) * (1 + ((bitboard.hasRightsToKingCastle(Us) || bitboard.hasRightsToQueenCastle(Us)) ? 0 : 1));
	      					int e = (TrappedRook_E - 0) * (1 + ((bitboard.hasRightsToKingCastle(Us) || bitboard.hasRightsToQueenCastle(Us)) ? 0 : 1));
	      					addToSignal(Us, signals.getSignal(FEATURE_ID_MOBILITY_OUTPOST_OTHERS), interpolateInternal(-o, -e, openningPart), openningPart);
	      				}
	      			}
	      		}
	
	      		if (pieceType == Constants.TYPE_QUEEN)
	      		{
	      			// Penalty if any relative pin or discovered attack against the queen
	      			//TODO implement slider_blockers
	      			/*long queenPinners;
	      			if (pos.slider_blockers(pos.pieces(Them, PieceType.ROOK, PieceType.BISHOP), squareID, queenPinners) != null)
	      			{
	      				score -= WeakQueen;
	      			}*/
	      		}
            }
        }
	}
	
	
	private void fillKing(ISignals signals, int Us) {
		
		double openningPart = bitboard.getMaterialFactor().getOpenningPart();
		
		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		final long Camp = (Us == Constants.COLOUR_WHITE ? AllSquares ^ Rank6BB ^ Rank7BB ^ Rank8BB : AllSquares ^ Rank1BB ^ Rank2BB ^ Rank3BB);

		final int squareID_ksq = getKingSquareID(evalinfo, Us);
		long kingFlank;
		long weak;
		long b;
		long b1;
		long b2;
		long safe;
		long unsafeChecks;

		// Find the squares that opponent attacks in our king flank, and the squares
		// which are attacked twice in that flank.
		kingFlank = KingFlank[file_of(squareID_ksq)];
		
		b1 = evalinfo.attackedBy[Them][Constants.TYPE_ALL] & kingFlank & Camp;
		b2 = b1 & evalinfo.attackedBy2[Them];
		
		int tropism = Long.bitCount(b1) + Long.bitCount(b2);

		// Main king safety evaluation
		if (evalinfo.kingAttackersCount[Them] > 1 - Long.bitCount(evalinfo.bb_queens[Them])) {
			int kingDanger = 0;
			unsafeChecks = 0;
			
			// Attacked squares defended at most once by our queen or king
			weak = evalinfo.attackedBy[Them][Constants.TYPE_ALL] & ~evalinfo.attackedBy2[Us]
					& (~evalinfo.attackedBy[Us][Constants.TYPE_ALL] | evalinfo.attackedBy[Us][Constants.TYPE_KING] | evalinfo.attackedBy[Us][Constants.TYPE_QUEEN]);
			
			// Analyse the safe enemy's checks which are possible on next move
			safe = ~evalinfo.bb_all_pieces[Them];
			safe &= ~evalinfo.attackedBy[Us][Constants.TYPE_ALL] | (weak & evalinfo.attackedBy2[Them]);
			
			b1 = attacks_bb(Constants.TYPE_ROOK, squareID_ksq, evalinfo.bb_all ^ evalinfo.bb_queens[Us]);
			b2 = attacks_bb(Constants.TYPE_BISHOP, squareID_ksq, evalinfo.bb_all ^ evalinfo.bb_queens[Us]);
			
			// Enemy queen safe checks
			if (((b1 | b2) & evalinfo.attackedBy[Them][Constants.TYPE_QUEEN] & safe & ~evalinfo.attackedBy[Us][Constants.TYPE_QUEEN]) != 0) {
				kingDanger += QueenSafeCheck;
			}
			
			b1 &= evalinfo.attackedBy[Them][Constants.TYPE_ROOK];
			b2 &= evalinfo.attackedBy[Them][Constants.TYPE_BISHOP];
			
			// Enemy rooks checks
			if ((b1 & safe) != 0) {
				kingDanger += RookSafeCheck;
			} else {
				unsafeChecks |= b1;
			}
			
			// Enemy bishops checks
			if ((b2 & safe) != 0) {
				kingDanger += BishopSafeCheck;
			} else {
				unsafeChecks |= b2;
			}

			// Enemy knights checks
			//TODO check
			b = attacks_from(bitboard, squareID_ksq, Constants.TYPE_KNIGHT) & evalinfo.attackedBy[Them][Constants.TYPE_KNIGHT];
			if ((b & safe) != 0) {
				kingDanger += KnightSafeCheck;
			} else {
				unsafeChecks |= b;
			}

			// Unsafe or occupied checking squares will also be considered, as long as
			// the square is in the attacker's mobility area.
			unsafeChecks &= evalinfo.mobilityArea[Them];
			
			kingDanger += evalinfo.kingAttackersCount[Them] * evalinfo.kingAttackersWeight[Them]
					  + 69 * evalinfo.kingAttacksCount[Them]
					  + 185 * Long.bitCount(evalinfo.kingRing[Us] & weak)
					  + 150 * Long.bitCount(/*TODO pos.blockers_for_king(Us) |*/ unsafeChecks)
					  + tropism * tropism / 4
					  - 873 * (bitboard.getPiecesLists().getPieces(Figures.getPidByColourAndType(Them, Constants.TYPE_QUEEN)).getDataSize() == 0 ? 1 : 0) //TODO check !pos.<PieceType.QUEEN.getValue()>count(Them)
					  - 30;
			
			// Transform the kingDanger units into a Score, and subtract it from the evaluation
			if (kingDanger > 0) {
				addToSignal(Us, signals.getSignal(FEATURE_ID_KING_SAFETY), interpolateInternal(-kingDanger * kingDanger / 4096, -kingDanger / 16, openningPart), openningPart);
			}
		}
		
		// Penalty when our king is on a pawnless flank
		if (((evalinfo.bb_pawns[Us] | evalinfo.bb_pawns[Them]) & kingFlank) == 0) {
			addToSignal(Us, signals.getSignal(FEATURE_ID_KING_SAFETY), interpolateInternal(-PawnlessFlank_O, -PawnlessFlank_E, openningPart), openningPart);
		}

		// King tropism bonus, to anticipate slow motion attacks on our king
		addToSignal(Us, signals.getSignal(FEATURE_ID_KING_SAFETY), interpolateInternal(-CloseEnemies_O * tropism, -CloseEnemies_E * tropism, openningPart), openningPart);
	}
	
	
	private void fillThreats(ISignals signals, int Us) {

		double openningPart = bitboard.getMaterialFactor().getOpenningPart();
		
		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
		final long TRank3BB = (Us == Constants.COLOUR_WHITE ? Rank3BB : Rank6BB);

		long b;
		long weak;
		long defended;
		long nonPawnEnemies;
		long stronglyProtected;
		long safe;
		long restricted;

		// Non-pawn enemies
		nonPawnEnemies = evalinfo.bb_all_pieces[Them] ^ evalinfo.bb_pawns[Them];

		// Squares strongly protected by the enemy, either because they defend the
		// square with a pawn, or because they defend the square twice and we don't.
		stronglyProtected = evalinfo.attackedBy[Them][Constants.TYPE_PAWN] | (evalinfo.attackedBy2[Them] & ~evalinfo.attackedBy2[Us]);

		// Non-pawn enemies, strongly protected
		defended = nonPawnEnemies & stronglyProtected;

		// Enemies not strongly protected and under our attack
		weak = evalinfo.bb_all_pieces[Them] & ~stronglyProtected & evalinfo.attackedBy[Us][Constants.TYPE_ALL];
	  
		// Safe or protected squares
		safe = ~evalinfo.attackedBy[Them][Constants.TYPE_ALL] | evalinfo.attackedBy[Us][Constants.TYPE_ALL];

		// Bonus according to the kind of attacking pieces
		if ((defended | weak) != 0) {
			b = (defended | weak) & (evalinfo.attackedBy[Us][Constants.TYPE_KNIGHT] | evalinfo.attackedBy[Us][Constants.TYPE_BISHOP]);
			while (b != 0) {
				
				int squareID = Long.numberOfTrailingZeros(b);
				int pid = bitboard.getFigureID(squareID);
				int pieceType = Figures.getFigureType(pid);
				
				addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(ThreatByMinor_O[pieceType], ThreatByMinor_E[pieceType], openningPart), openningPart);
				
				if (pieceType != Constants.TYPE_PAWN) {
					int multiplier = relative_rank_bySquare(Them, squareID);
					addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(ThreatByRank_O * multiplier, ThreatByRank_E * multiplier, openningPart), openningPart);
				}
				
				b &= b - 1;
			}

			b = weak & evalinfo.attackedBy[Us][Constants.TYPE_ROOK];
			while (b != 0) {
				
				int squareID = Long.numberOfTrailingZeros(b);
				int pid = bitboard.getFigureID(squareID);
				int pieceType = Figures.getFigureType(pid);
				
				addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(ThreatByRook_O[pieceType], ThreatByRook_E[pieceType], openningPart), openningPart);
				
				if (pieceType != Constants.TYPE_PAWN) {
					int multiplier = relative_rank_bySquare(Them, squareID);
					addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(ThreatByRank_O * multiplier, ThreatByRank_E * multiplier, openningPart), openningPart);
				}
				
				b &= b - 1;
			}

			if ((weak & evalinfo.attackedBy[Us][Constants.TYPE_KING]) != 0) {
				addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(ThreatByKing_O, ThreatByKing_E, openningPart), openningPart);
			}

			int multiplier = Long.bitCount(weak & ~evalinfo.attackedBy[Them][Constants.TYPE_ALL]);
			addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(Hanging_O * multiplier, Hanging_E * multiplier, openningPart), openningPart);
			
			b = weak & nonPawnEnemies & evalinfo.attackedBy[Them][Constants.TYPE_ALL];
			multiplier = Long.bitCount(b);
			addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(Overload_O * multiplier, Overload_E * multiplier, openningPart), openningPart);
		}

		// Bonus for restricting their piece moves
		restricted = evalinfo.attackedBy[Them][Constants.TYPE_ALL] & ~evalinfo.attackedBy[Them][Constants.TYPE_PAWN] & ~evalinfo.attackedBy2[Them] & evalinfo.attackedBy[Us][Constants.TYPE_ALL];
		int multiplier = Long.bitCount(restricted);
		addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(RestrictedPiece_O * multiplier, RestrictedPiece_E * multiplier, openningPart), openningPart);
		
		// Bonus for enemy unopposed weak pawns
		if ((evalinfo.bb_rooks[Us] | evalinfo.bb_queens[Us]) != 0) {
			addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(WeakUnopposedPawn_O * pawnsFiller.weakUnopposed[Them], WeakUnopposedPawn_E * pawnsFiller.weakUnopposed[Them], openningPart), openningPart);
		}

		// Find squares where our pawns can push on the next move
		b = shiftBB(evalinfo.bb_pawns[Us], Direction_Up) & evalinfo.bb_free;
		b |= shiftBB(b & TRank3BB, Direction_Up) & evalinfo.bb_free;

		// Keep only the squares which are relatively safe
		b &= ~evalinfo.attackedBy[Them][Constants.TYPE_PAWN] & safe;

		// Bonus for safe pawn threats on the next move
		b = pawn_attacks_bb(b, Us) & evalinfo.bb_all_pieces[Them];
		multiplier = Long.bitCount(b);
		addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(ThreatByPawnPush_O * multiplier, ThreatByPawnPush_E * multiplier, openningPart), openningPart);
		
		// Our safe or protected pawns
		b = evalinfo.bb_pawns[Us] & safe;

		b = pawn_attacks_bb(b, Us) & nonPawnEnemies;
		multiplier = Long.bitCount(b);
		addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(ThreatBySafePawn_O * multiplier, ThreatBySafePawn_E * multiplier, openningPart), openningPart);
		
		// Bonus for threats on the next moves against enemy queen
		if (Long.bitCount(evalinfo.bb_queens[Them]) == 1) {
			
			int squareID = Long.numberOfTrailingZeros(evalinfo.bb_queens[Them]);
			safe = evalinfo.mobilityArea[Us] & ~stronglyProtected;

			b = evalinfo.attackedBy[Us][Constants.TYPE_KNIGHT] & attacks_from(bitboard, squareID, Constants.TYPE_KNIGHT);
			multiplier = Long.bitCount(b & safe);
			addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(KnightOnQueen_O * multiplier, KnightOnQueen_E * multiplier, openningPart), openningPart);
			
			b = (evalinfo.attackedBy[Us][Constants.TYPE_BISHOP] & attacks_from(bitboard, squareID, Constants.TYPE_BISHOP) | (evalinfo.attackedBy[Us][Constants.TYPE_ROOK] & attacks_from(bitboard, squareID, Constants.TYPE_ROOK)));
			multiplier = Long.bitCount(b & safe & evalinfo.attackedBy2[Us]);
			addToSignal(Us, signals.getSignal(FEATURE_ID_THREATS), interpolateInternal(SliderOnQueen_O * multiplier, SliderOnQueen_E * multiplier, openningPart), openningPart);
		}
	}
	
	
	private void fillSpace(ISignals signals, int Us) {
		
		double openningPart = bitboard.getMaterialFactor().getOpenningPart();
		
		final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
		final long SpaceMask = Us == Constants.COLOUR_WHITE ? CenterFiles & (Rank2BB | Rank3BB | Rank4BB) : CenterFiles & (Rank7BB | Rank6BB | Rank5BB);
		
		// Find the available squares for our pieces inside the area defined by SpaceMask
		long safe = SpaceMask & ~evalinfo.bb_pawns[Us] & ~evalinfo.attackedBy[Them][Constants.TYPE_PAWN];
		
		// Find all squares which are at most three squares behind some friendly pawn
		long behind = evalinfo.bb_pawns[Us];
		behind |= (Us == Constants.COLOUR_WHITE ? behind >>> 8 : behind << 8);
		behind |= (Us == Constants.COLOUR_WHITE ? behind >>> 16 : behind << 16);
		
		int bonus = Long.bitCount(safe) + Long.bitCount(behind & safe);
		int piecesCount = Long.bitCount(evalinfo.bb_all_pieces[Us]);
		int weight = piecesCount - 2 * pawnsFiller.openFiles;
		
		addToSignal(Us, signals.getSignal(FEATURE_ID_SPACE), interpolateInternal( bonus * weight * weight / 16, 0, openningPart), openningPart);
	}
	
	
	private static final void addToSignal(int Us, ISignal signal, double strength, double openningPart) {
		if (Us == Constants.COLOUR_WHITE) {
			signal.addStrength(strength, openningPart);
		} else {
			signal.addStrength(-strength, openningPart);
		}
	}
	
	
	private static final double interpolateInternal(double o, double e, double openningPart) {
		return (o * openningPart + e * (1 - openningPart));
	}
	
	
	private static final class PawnsFiller extends Evaluator.Pawns {
		
		
		private void fillPawns(ISignals signals, IBitBoard bitboard, EvalInfo evalinfo, int Us, PiecesList Us_pawns_list) {
			
			double openningPart = bitboard.getMaterialFactor().getOpenningPart();
			
			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
			
			long neighbours;
			long stoppers;
			long doubled;
			long supported;
			long phalanx;
			long lever;
			long leverPush;
			boolean opposed;
			boolean backward;
			
			long ourPawns = evalinfo.bb_pawns[Us];
			long theirPawns = evalinfo.bb_pawns[Them];

			passedPawns[Us] = pawnAttacksSpan[Us] = weakUnopposed[Us] = 0;
			semiopenFiles[Us] = 0xFF;
			kingSquares[Us] = -1;
			pawnAttacks[Us] = pawn_attacks_bb(ourPawns, Us);
			pawnsOnSquares[Us][Constants.COLOUR_BLACK] = Long.bitCount(ourPawns & DarkSquares);
			pawnsOnSquares[Us][Constants.COLOUR_WHITE] = Long.bitCount(ourPawns & LightSquares);
			
            int pawns_count = Us_pawns_list.getDataSize();
            if (pawns_count > 0) {
	            int[] pawns_fields = Us_pawns_list.getData();
	            for (int i=0; i<pawns_count; i++) {
	            	
	            	int squareID = pawns_fields[i];
	            	
	            	int fileID = file_of(squareID);
	            	int rankID = rank_of(squareID);
	            	if (rankID == 0 || rankID == 7) {
	            		throw new IllegalStateException();
	            	}
	            	
					semiopenFiles[Us] &= ~(1 << fileID);
					pawnAttacksSpan[Us] |= pawn_attack_span(Us, squareID);
					
					// Flag the pawn
					opposed = (theirPawns & forward_file_bb(Us, squareID)) != 0;
					stoppers = theirPawns & passed_pawn_mask(Us, squareID);
					lever = (theirPawns & PawnAttacks[Us][squareID]);
					leverPush = theirPawns & PawnAttacks[Us][squareID + Direction_Up];
					doubled = ourPawns & SquareBB[squareID - Direction_Up];
					neighbours = ourPawns & adjacent_files_bb(fileID);
					phalanx = neighbours & rank_bb(squareID);
					supported = neighbours & rank_bb(squareID - Direction_Up);
					
					// A pawn is backward when it is behind all pawns of the same color
					// on the adjacent files and cannot be safely advanced.
					backward = (ourPawns & pawn_attack_span(Them, squareID + Direction_Up)) == 0 && (stoppers & (leverPush | (SquareBB[squareID + Direction_Up]))) != 0;
					
					// Passed pawns will be properly scored in evaluation because we need
					// full attack info to evaluate them. Include also not passed pawns
					// which could become passed after one or two pawn pushes when are
					// not attacked more times than defended.
					if ((stoppers ^ lever ^ leverPush) == 0 && (Long.bitCount(supported) >= Long.bitCount(lever) - 1) && Long.bitCount(phalanx) >= Long.bitCount(leverPush)) {
						
						passedPawns[Us] |= SquareBB[squareID];
						
					} else if (stoppers == SquareBB[squareID + Direction_Up] && relative_rank_bySquare(Us, squareID) >= Rank5) {
						
						long b = shiftBB(supported, Direction_Up) & ~theirPawns;
						while (b != 0) {
							if (!more_than_one(theirPawns & PawnAttacks[Us][Long.numberOfTrailingZeros(b)])) {
								passedPawns[Us] |= SquareBB[squareID];
							}
							b &= b - 1;
						}
					}
					
					// Score this pawn
					if ((supported | phalanx) != 0) {
						
						int supportedCount = Long.bitCount(supported);
						int o = Connected_O[opposed ? 1 : 0][phalanx == 0 ? 0 : 1][supportedCount][relative_rank_bySquare(Us, squareID)];
						int e = Connected_E[opposed ? 1 : 0][phalanx == 0 ? 0 : 1][supportedCount][relative_rank_bySquare(Us, squareID)];
						addToSignal(Us, signals.getSignal(FEATURE_ID_PAWNS), interpolateInternal(o, e, openningPart), openningPart);
						
					} else if (neighbours == 0) {
						
						addToSignal(Us, signals.getSignal(FEATURE_ID_PAWNS), interpolateInternal(-Isolated_O, -Isolated_E, openningPart), openningPart);
						weakUnopposed[Us] += (!opposed ? 1 : 0);
						
					} else if (backward) {
						
						addToSignal(Us, signals.getSignal(FEATURE_ID_PAWNS), interpolateInternal(-Backward_O, -Backward_E, openningPart), openningPart);
						weakUnopposed[Us] += (!opposed ? 1 : 0);
					}
	
					if (doubled != 0 && supported == 0) {
						addToSignal(Us, signals.getSignal(FEATURE_ID_PAWNS), interpolateInternal(-Doubled_O, -Doubled_E, openningPart), openningPart);
					}
	            }
			}
		}
		
		
		private final void fillKingSafety(ISignals signals, IBitBoard bitboard, EvalInfo evalinfo, int Us) {
			
			double openningPart = bitboard.getMaterialFactor().getOpenningPart();
			
			int squareID_ksq = getKingSquareID(evalinfo, Us);
			kingSquares[Us] = squareID_ksq;
			//castlingRights[Us] = bitboard.hasRightsToKingCastle(Us) || bitboard.hasRightsToQueenCastle(Us)//pos.can_castle(Us);
			int minKingPawnDistance = 0;
			
			long pawns = evalinfo.bb_pawns[Us];
			if (pawns != 0) {
				while ((DistanceRingBB[squareID_ksq][minKingPawnDistance] & pawns) == 0) {
					minKingPawnDistance++;	
				}
			}
			
			int bonus = evaluate_shelter(evalinfo, Us, squareID_ksq);
			
			// If we can castle use the bonus after the castling if it is bigger
			if (bitboard.hasRightsToKingCastle(Us)) {
				bonus = Math.max(bonus, evaluate_shelter(evalinfo, Us, relative_square(Us, Fields.G1_ID)));
			}
			
			if (bitboard.hasRightsToQueenCastle(Us)) {
				bonus = Math.max(bonus, evaluate_shelter(evalinfo, Us, relative_square(Us, Fields.C1_ID)));
			}
			
			addToSignal(Us, signals.getSignal(FEATURE_ID_KING_SAFETY), interpolateInternal(bonus, -16 * minKingPawnDistance, openningPart), openningPart);
		}
		
		
		private void fillPassedPawns(ISignals signals, IBitBoard bitboard, EvalInfo evalinfo, int Us) {
			
			double openningPart = bitboard.getMaterialFactor().getOpenningPart();
			
			
			final int Them = (Us == Constants.COLOUR_WHITE ? Constants.COLOUR_BLACK : Constants.COLOUR_WHITE);
			final int Direction_Up = (Us == Constants.COLOUR_WHITE ? Direction_NORTH : Direction_SOUTH);
			int squareID_ksq = getKingSquareID(evalinfo, Us);
			
			long b;
			long bb;
			long squaresToQueen;
			long defendedSquares;
			long unsafeSquares;
			
			b = passedPawns[Us];

			while (b != 0) {
				
				int squareID = Long.numberOfTrailingZeros(b);
            	
				int rankID = relative_rank_bySquare(Us, squareID);
				
				int bonus_o = PassedRank_O[rankID];
				int bonus_e = PassedRank_E[rankID];
				
				if (rankID > Rank3)
				{
					int w = (rankID - 2) * (rankID - 2) + 2;
					int blockSq = squareID + Direction_Up;
					long blockBB = SquareBB[blockSq];
					
					// Adjust bonus based on the king's proximity
					bonus_o += 0;
					bonus_e += (king_proximity(Them, blockSq, squareID_ksq) * 5 - king_proximity(Us, blockSq, squareID_ksq) * 2) * w;

					// If blockSq is not the queening square then consider also a second push
					if (rankID != Rank7) {
						bonus_o -= 0;
						bonus_e -= king_proximity(Us, blockSq + Direction_Up, squareID_ksq) * w;
					}
					
					// If the pawn is free to advance, then increase the bonus
					if (bitboard.getFigureID(blockSq) == Constants.PID_NONE) {//pos.empty(blockSq))
						
						// If there is a rook or queen attacking/defending the pawn from behind,
						// consider all the squaresToQueen. Otherwise consider only the squares
						// in the pawn's path attacked or occupied by the enemy.
						defendedSquares = unsafeSquares = squaresToQueen = forward_file_bb(Us, squareID);
						
						bb = forward_file_bb(Them, squareID)
								& (evalinfo.bb_queens[Constants.COLOUR_WHITE] | evalinfo.bb_queens[Constants.COLOUR_BLACK] | evalinfo.bb_rooks[Constants.COLOUR_WHITE] | evalinfo.bb_rooks[Constants.COLOUR_BLACK])
								& attacks_from(bitboard, squareID, Constants.TYPE_ROOK);

						if ((evalinfo.bb_all_pieces[Us] & bb) == 0) {
							defendedSquares &= evalinfo.attackedBy[Us][Constants.TYPE_ALL];
						}

						if ((evalinfo.bb_all_pieces[Them] & bb) == 0) {
							unsafeSquares &= evalinfo.attackedBy[Them][Constants.TYPE_ALL] | evalinfo.bb_all_pieces[Them];
						}
						
						// If there aren't any enemy attacks, assign a big bonus. Otherwise
						// assign a smaller bonus if the block square isn't attacked.
						int k = unsafeSquares == 0 ? 20 : (unsafeSquares & blockBB) == 0 ? 9 : 0;
						
						// If the path to the queen is fully defended, assign a big bonus.
						// Otherwise assign a smaller bonus if the block square is defended.
						if (defendedSquares == squaresToQueen) {
							k += 6;
						} else if ((defendedSquares & blockBB) != 0) {
							k += 4;
						}
						
						bonus_o += k * w;
						bonus_e += k * w;
					}
				} // rank > RANK_3

				// Scale down bonus for candidate passers which need more than one
				// pawn push to become passed, or have a pawn in front of them.
				if (!pawn_passed(evalinfo, Us, squareID + Direction_Up)
						|| ((evalinfo.bb_pawns[Constants.COLOUR_WHITE] | evalinfo.bb_pawns[Constants.COLOUR_BLACK]) & forward_file_bb(Us, squareID)) != 0) {
					bonus_o = bonus_o / 2;
					bonus_e = bonus_e / 2;
				}
				
				addToSignal(Us, signals.getSignal(FEATURE_ID_PASSED_PAWNS), interpolateInternal(bonus_o + PassedFile_O[file_of(squareID)], bonus_e + PassedFile_E[file_of(squareID)], openningPart), openningPart);
				
				b &= b - 1;
			}
		}
	}
}

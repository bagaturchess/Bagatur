/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
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
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.bitboard.impl_kingcaptureallowed;


import java.util.Arrays;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.BackupInfo;
import bagaturchess.bitboard.common.Fen;
import bagaturchess.bitboard.common.GlobalConstants;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Properties;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl_kingcaptureallowed.attacks.FieldAttack;
import bagaturchess.bitboard.impl_kingcaptureallowed.attacks.SEE;
import bagaturchess.bitboard.impl.eval.BaseEvaluation;
import bagaturchess.bitboard.impl.eval.MaterialFactor;
import bagaturchess.bitboard.impl.datastructs.StackLongInt;
import bagaturchess.bitboard.impl.endgame.MaterialState;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnStructureConstants;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.BlackPawnMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.CastleMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.KingMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.KnightMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.OfficerMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.QueenMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.WhitePawnMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.plies.Castling;
import bagaturchess.bitboard.impl_kingcaptureallowed.plies.Enpassanting;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl.state.PiecesLists;
import bagaturchess.bitboard.impl.zobrist.ConstantStructure;


abstract class Board3 extends Fields implements IBitBoard, Cloneable {
	
	
	private static final boolean DEBUG = false;

	
	private IBoardConfig boardConfig;
	
	
	public PiecesLists pieces;
	public int[] board;
	
	protected StackLongInt playedBoardStates;
	protected long hashkey = Bits.NUMBER_0;
	protected long pawnskey = Bits.NUMBER_0;
	
	protected BackupInfo[] backupInfo;
	
	protected int lastMoveColour = Figures.COLOUR_BLACK;
	
	protected int[] playedMoves;
	protected int playedMovesCount = 0;
	protected int marked_playedMovesCount = 0;
	
	protected int lastCaptureOrPawnMoveBefore = 0;
	
	private SEE see;
	
	private MoveListener[] moveListeners;
	private MaterialState materialState;
	private MaterialFactor materialFactor;
	private BaseEvaluation eval;
	protected PawnsEvalCache pawnsCache;
	
	protected IBoard.CastlingType[] castledByColour;
	//protected int lastCastledColour = Figures.COLOUR_UNSPECIFIED;
	
	protected boolean[] inCheckCache;
	protected boolean[] inCheckCacheInitialized; 
	
	
	public Board3() {
		this((IBoardConfig) null);
	}
	
	
	public Board3(IBoardConfig boardConfig) {
		this(Constants.INITIAL_BOARD, boardConfig);
	}
	
	
	public Board3(String fenStr) {
		this(fenStr, (IBoardConfig)null);
	}
	
	
	public Board3(String fenStr, IBoardConfig boardConfig) {
		this(fenStr, null, boardConfig);
	}
	
	
	public Board3(String fenStr, PawnsEvalCache _pawnsCache, IBoardConfig _boardConfig) {
		
		pawnsCache = _pawnsCache;
		boardConfig = _boardConfig;
		
		Fen fen = Fen.parse(fenStr);
		
		
		board = new int[Fields.ID_MAX];
		castledByColour = new IBoard.CastlingType[Figures.COLOUR_MAX];
		
		lastMoveColour = fen.getColourToMove() == Figures.COLOUR_WHITE ? Figures.COLOUR_BLACK : Figures.COLOUR_WHITE;
		if (fen.getColourToMove() == Figures.COLOUR_WHITE) {
			hashkey ^= ConstantStructure.WHITE_TO_MOVE;
			pawnskey ^= ConstantStructure.WHITE_TO_MOVE;
		} else {
			
		}
		
		pieces = new PiecesLists(this);
		
		
		//Init move listeners
		moveListeners = new MoveListener[0];
		materialFactor = new MaterialFactor();
		materialState = new MaterialState();
		addMoveListener(materialFactor);
		addMoveListener(materialState);
		if (boardConfig != null) {
			eval = new BaseEvaluation(boardConfig, materialFactor);
			addMoveListener(eval);
		}
		
		
		init(fen.getBoardArray());
		
		
		playedMoves = new int[GlobalConstants.MAX_MOVES_IN_GAME];
		playedMovesCount = 0;
		
		backupInfo = new BackupInfo[GlobalConstants.MAX_MOVES_IN_GAME];
		for (int i=0; i<backupInfo.length; i++) {
			backupInfo[i] = new BackupInfo();
		}
		
		int enpassantTargetFieldID = -1;
		if (fen.getEnpassantTargetSquare() != null) {
			enpassantTargetFieldID = Enpassanting.getEnemyFieldID(fen.getEnpassantTargetSquare());
		}
		
		backupInfo[playedMovesCount].enpassantPawnFieldID = enpassantTargetFieldID;
		backupInfo[playedMovesCount].w_kingSideAvailable = fen.hasWhiteKingSide();
		backupInfo[playedMovesCount].w_queenSideAvailable = fen.hasWhiteQueenSide();
		backupInfo[playedMovesCount].b_kingSideAvailable = fen.hasBlackKingSide();
		backupInfo[playedMovesCount].b_queenSideAvailable = fen.hasBlackQueenSide();
		
		if (backupInfo[playedMovesCount].enpassantPawnFieldID != -1) {
			hashkey ^= ConstantStructure.HAS_ENPASSANT;
		}
		
		if (backupInfo[playedMovesCount].w_kingSideAvailable) {
			hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE];
		}
		if (backupInfo[playedMovesCount].b_kingSideAvailable) {
			hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK];
		}
		if (backupInfo[playedMovesCount].w_queenSideAvailable) {
			hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE];
		}
		if (backupInfo[playedMovesCount].b_queenSideAvailable) {
			hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK];
		}
		
		playedBoardStates = new StackLongInt(9631);//9631, 3229
		playedBoardStates.inc(hashkey);
		
		
		see = new SEE(this);
		
		inCheckCache = new boolean[] {false, false, false};
		inCheckCacheInitialized = new boolean[] {false, false, false};
		
		checkConsistency();
	} 
	
	
	public void addMoveListener(MoveListener listener) {
		MoveListener[] oldMoveListeners = moveListeners;
		MoveListener[] newMoveListeners = new MoveListener[moveListeners.length + 1];
		if (oldMoveListeners.length > 0) {
			for (int i=0; i<oldMoveListeners.length; i++) {
				newMoveListeners[i] = oldMoveListeners[i];
			}
		}
		
		newMoveListeners[oldMoveListeners.length] = listener;
		
		moveListeners = newMoveListeners;
	}
	
	
	public String toEPD() {
		
		String result = "";
		
		int empty = 0;
		
		for (int digit=7; digit>=0; digit--) {
			String line = "";
			
			for (int letter=0; letter<8; letter++) {
				
				int fieldID = 8 * digit + letter;
				int pid = board[fieldID];
				
				if (pid == Constants.PID_NONE) {
					empty++;
				} else {
					if (empty != 0) {
						line += empty;
					}
					String sign = Constants.PIECE_IDENTITY_2_SIGN[pid];
					line += sign;
					empty = 0;
				}
				
				if (letter == 7 && empty != 0) {
					line += empty;
					empty = 0;
				}
			}
			
			if (digit != 0) {
				line += "/";
			}
			
			result = result + line;
		}
		
		result += " ";
		result += getColourToMove() == Figures.COLOUR_WHITE ? "w" : "b";
		
		result += " ";
		if (backupInfo[playedMovesCount].w_kingSideAvailable) {
			result += "K";
		}
		if (backupInfo[playedMovesCount].w_queenSideAvailable) {
			result += "Q";
		}
		if (backupInfo[playedMovesCount].b_kingSideAvailable) {
			result += "k";
		}
		if (backupInfo[playedMovesCount].b_queenSideAvailable) {
			result += "q";
		}
		
		if (result.endsWith(" ")) {
			result += "-";
		}
		
		result += " ";
		int enpassTargetFieldID = backupInfo[playedMovesCount].enpassantPawnFieldID;
		if (enpassTargetFieldID != -1) {
			result += Fields.getFieldSign(enpassTargetFieldID);
		} else {
			result += "-";
		}
		
		result += " ";
		//result += getPlayedMovesCount_Total();
		
		result += " ";
		//result += ((getPlayedMovesCount_Total() + 1) / 2 + 1);
		
		return result;
	}
	
	
	@Override
	public PawnsEvalCache getPawnsCache() {
		return pawnsCache;
	}
	
	
	@Override
	public PawnsModelEval getPawnsStructure() {
		long pawnskey = getPawnsHashKey();
		
		PawnsModelEval result = pawnsCache.get(pawnskey);
		if (result == null) {
			result = pawnsCache.put(pawnskey);
			result.rebuild(this);
		}
		
		return result;
	}
	
	
	@Override
	public boolean isPasserPush(int move) {
		
		int colour = MoveInt.getColour(move);
		
		if (getColourToMove() != colour) {
			throw new IllegalStateException();
		}
		
		if (!MoveInt.isPawn(move)) {
			return false;
		}
		
		int fromFieldID = MoveInt.getFromFieldID(move);
		boolean sameVertical = Fields.LETTERS[fromFieldID] == Fields.LETTERS[MoveInt.getToFieldID(move)];
		
		if (!sameVertical) {
			return false;
		}
		
		if (getPawnsCache() == null) {
			return false;
		}
		
		getPawnsCache().lock();
		
		PawnsModelEval modelEval = getPawnsStructure();
		PawnsModel model = modelEval.getModel();
		
		if (colour == Figures.COLOUR_WHITE) {
			int w_passed_count = model.getWPassedCount();
			if (w_passed_count <= 0) {
				getPawnsCache().unlock();
				return false;
			}
			if (w_passed_count > 0) {
				Pawn[] w_passed = model.getWPassed();
				for (int i=0; i<w_passed_count; i++) {
					if (w_passed[i].getFieldID() == fromFieldID) {
						getPawnsCache().unlock();
						return true;
					}
				}
			}
		} else {
			int b_passed_count = model.getBPassedCount();
			if (b_passed_count <= 0) {
				getPawnsCache().unlock();
				return false;
			}
			if (b_passed_count > 0) {
				Pawn[] b_passed = model.getBPassed();
				for (int i=0; i<b_passed_count; i++) {
					if (b_passed[i].getFieldID() == fromFieldID) {
						getPawnsCache().unlock();
						return true;
					}
				}
			}
		}
		
		getPawnsCache().unlock();
		
		return false;
	}
	
	
	private boolean hasUnstoppablePasser(int colourToMove) {
		
		if (colourToMove != getColourToMove()) {
			//throw new IllegalStateException("colourToMove != getColourToMove()");
		}
		
		
		if (colourToMove == Figures.COLOUR_WHITE) {
			if (materialFactor.getBlackFactor() > 0) {
				return false;
			}
		} else {
			if (materialFactor.getWhiteFactor() > 0) {
				return false;
			}
		}
		
		boolean result = false;
		
		PawnsModelEval modelEval = getPawnsStructure();
		PawnsModel model = modelEval.getModel();
		
		
		getPawnsCache().lock();
		
		
		int w_passed_count = model.getWPassedCount();
		int b_passed_count = model.getBPassedCount();
		if (colourToMove == Figures.COLOUR_WHITE) {
			if (w_passed_count > 0) {
				int w_rank = model.getWUnstoppablePasserRank();
				int b_rank = model.getBMaxPassedRank();
				if (w_rank != 0) {
					if (w_rank > b_rank + 1) {
						result = true;
					}
				}
			}
		} else {
			if (b_passed_count > 0) {
				int w_rank = model.getWMaxPassedRank();
				int b_rank = model.getBUnstoppablePasserRank();
				if (b_rank != 0) {
					if (b_rank > w_rank + 1) {
						result = true;
					}
				}
			}
		}
		
		getPawnsCache().unlock();
		
		return result;
	}
	
	
	@Override
	public int getUnstoppablePasser() {
		
		int result = 0;
		
		getPawnsCache().lock();
		
		PawnsModelEval modelEval = getPawnsStructure();
		PawnsModel model = modelEval.getModel();

		
		int w_passed_count = model.getWPassedCount();
		int b_passed_count = model.getBPassedCount();
		
		if (w_passed_count > 0 && materialFactor.getBlackFactor() == 0) {
			int w_rank = model.getWUnstoppablePasserRank();
			int b_rank = model.getBMaxPassedRank();
			if (w_rank != 0) {
				if (w_rank > b_rank + 1) {
					result += 1;
				}
			} else if (w_rank == 0) {
				long w_passers_keysquares = 0;
				Pawn[] w_passed = model.getWPassed();
				for (int i=0; i<w_passed_count; i++) {
					if (w_passed[i].getRank() > b_rank + 2) {
						w_passers_keysquares |= PawnStructureConstants.WHITE_KEY_SQUARES[w_passed[i].getFieldID()];
					}
				}
				PiecesList w_king = getPiecesLists().getPieces(Constants.PID_W_KING);
				int fieldID = w_king.getData()[0];
				long bb_wking = Fields.ALL_A1H1[fieldID];
				if ((w_passers_keysquares & bb_wking) != 0L) {
					result += 1;
				}
			}
		}
		
		if (b_passed_count > 0 && materialFactor.getWhiteFactor() == 0) {
			int w_rank = model.getWMaxPassedRank();
			int b_rank = model.getBUnstoppablePasserRank();
			if (b_rank != 0) {
				if (b_rank > w_rank + 1) {
					result -= 1;
				}
			} else if (b_rank == 0) {
				long b_passers_keysquares = 0;
				Pawn[] b_passed = model.getBPassed();
				for (int i=0; i<b_passed_count; i++) {
					if (b_passed[i].getRank() > w_rank + 2) {
						b_passers_keysquares |= PawnStructureConstants.BLACK_KEY_SQUARES[b_passed[i].getFieldID()];
					}
				}
				PiecesList b_king = getPiecesLists().getPieces(Constants.PID_B_KING);
				int fieldID = b_king.getData()[0];
				long bb_bking = Fields.ALL_A1H1[fieldID];
				if ((b_passers_keysquares & bb_bking) != 0L) {
					result -= 1;
				}
			}
		}
		
		
		getPawnsCache().unlock();
		
		return result;
	}
	
	
	@Override
	public IBoard.CastlingType getCastlingType(int colour) {
		return castledByColour[colour];
	}
	
	
	public void mark() {
		marked_playedMovesCount = playedMovesCount;
	}


	public void reset() {
		for (int i=playedMovesCount - 1; i>=marked_playedMovesCount;i--) {
			int move = playedMoves[i];
			if (move == 0) {
				makeNullMoveBackward();
			} else {
				makeMoveBackward(move);
			}
		}
	}
	
		
	public final void makeMoveForward(final int move) {
		makeMoveForward(move, true);
	}
	
	
	public final void makeMoveForward(final int move, boolean invalidateCheckKeepers) {
		
		
		if (DEBUG) checkConsistency();
		
		
		if (eval != null) eval.move(move);
		if (moveListeners.length > 0) {
			for (int i=0; i<moveListeners.length; i++) {
				moveListeners[i].preForwardMove(MoveInt.getColour(move), move);
			}
		}
		
		
		if (Properties.DEBUG_MODE) {
			int figureID = MoveInt.getFigurePID(move);
			
			
			final int figureColour = MoveInt.getColour(move);
			if (figureColour != getColourToMove()) {
				throw new IllegalStateException("Move the same colour " + figureColour + " more then once.");
			}

		}
		
		//long expected_key = 0L;
		//long expected_pawnkey = 0L;

		
		//MAIN LOGIC
		
		int pid = MoveInt.getFigurePID(move);
		int figureColour = MoveInt.getColour(move);
		int figureType = MoveInt.getFigureType(move);
		//int dirID = MoveInt.getDir(move);
		int fromFieldID = MoveInt.getFromFieldID(move);
		int toFieldID = MoveInt.getToFieldID(move);
		
		
		//Keep enpassant flag and colour
		BackupInfo curInfo = backupInfo[playedMovesCount];
		curInfo.hashkey = hashkey;
		curInfo.pawnshash = pawnskey;
		curInfo.lastCaptureOrPawnMoveBefore = lastCaptureOrPawnMoveBefore;
		if (MoveInt.isCapture(move) || MoveInt.isPawn(move)) {
			lastCaptureOrPawnMoveBefore = 0;
		} else {
			lastCaptureOrPawnMoveBefore++;
		}
		
		
		BackupInfo nextInfo = backupInfo[playedMovesCount + 1];
		
		
		nextInfo.enpassantPawnFieldID = -1;
		
		if (figureType == Figures.TYPE_PAWN //Pawn move
				&& !MoveInt.isCapture(move) //Non capture
				) {

			//if (dirID == Enpassanting.ENPASSANT_DIR_ID) { //To fields forward
			if (Math.abs(fromFieldID - toFieldID) == 16) { //To fields forward
				
				//Check whether the adjoining files contains opponent pawn
				int[] adjoiningFiles = Enpassanting.ADJOINING_FILES_FIELD_IDS[figureColour][toFieldID];
				for (int i=0; i<adjoiningFiles.length; i++) {
					int adjoining_field_id = adjoiningFiles[i];
					int adjoining_field_pid = board[adjoining_field_id];
					if (Constants.PIECE_IDENTITY_2_TYPE[adjoining_field_pid] == Figures.TYPE_PAWN
							&& Constants.hasDiffColour(pid, adjoining_field_pid)) {
						nextInfo.enpassantPawnFieldID = toFieldID;
						break;
					}
				}
			}
		}
		
		if (curInfo.enpassantPawnFieldID != nextInfo.enpassantPawnFieldID) {
			hashkey ^= ConstantStructure.HAS_ENPASSANT;
		}
		
		if (figureColour == Figures.COLOUR_WHITE) {
			
			nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
			nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
			
			if (curInfo.w_kingSideAvailable) {
				switch(pid) {
					case Constants.PID_W_KING:
						hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
						pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
						nextInfo.w_kingSideAvailable = false;
						break;
					case Constants.PID_W_ROOK:
						if (fromFieldID == H1_ID) {
							hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
							pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
							nextInfo.w_kingSideAvailable = false;
						} else {
							nextInfo.w_kingSideAvailable = true;
						}
						break;
					default:
						nextInfo.w_kingSideAvailable = true;
				}
			} else {
				nextInfo.w_kingSideAvailable = false;
			}
			
			if (curInfo.w_queenSideAvailable) {
				switch(pid) {
					case Constants.PID_W_KING:
						hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
						pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
						nextInfo.w_queenSideAvailable = false;
						break;
					case Constants.PID_W_ROOK:
						if (fromFieldID == A1_ID) {
							hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
							pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
							nextInfo.w_queenSideAvailable = false;
						} else {
							nextInfo.w_queenSideAvailable = true;
						}
						break;
					default:
						nextInfo.w_queenSideAvailable = true;
				}
			} else {
				nextInfo.w_queenSideAvailable = false;
			}
		} else {
			nextInfo.w_kingSideAvailable = curInfo.w_kingSideAvailable;
			nextInfo.w_queenSideAvailable = curInfo.w_queenSideAvailable;
			
			if (curInfo.b_kingSideAvailable) {
				switch(pid) {
					case Constants.PID_B_KING:
						hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
						pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
						nextInfo.b_kingSideAvailable = false;
						break;
					case Constants.PID_B_ROOK:
						if (fromFieldID == H8_ID) {
							hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
							pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
							nextInfo.b_kingSideAvailable = false;
						} else {
							nextInfo.b_kingSideAvailable = true;
						}
						break;
					default:
						nextInfo.b_kingSideAvailable = true;
				}
			} else {
				nextInfo.b_kingSideAvailable = false;
			}
			
			if (curInfo.b_queenSideAvailable) {
				switch(pid) {
					case Constants.PID_B_KING:
						hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
						pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
						nextInfo.b_queenSideAvailable = false;
						break;
					case Constants.PID_B_ROOK:
						if (fromFieldID == A8_ID) {
							hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
							pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
							nextInfo.b_queenSideAvailable = false;
						} else {
							nextInfo.b_queenSideAvailable = true;
						}
						break;
					default:
						nextInfo.b_queenSideAvailable = true;
				}
			} else {
				nextInfo.b_queenSideAvailable = false;
			}
		}
		
		
		if (DEBUG) {
			if (pid != Figures.getPidByColourAndType(figureColour, figureType)) {
				throw new IllegalStateException();
			}
		}
		
		pieces.move(pid, fromFieldID, toFieldID);
		
		board[fromFieldID] = Constants.PID_NONE;
		board[toFieldID] = pid;
		
		
		hashkey ^= ConstantStructure.getMoveHash(pid, fromFieldID, toFieldID);
		
		if (figureType == Figures.TYPE_PAWN || figureType == Figures.TYPE_KING) {
			pawnskey ^= ConstantStructure.getMoveHash(pid, fromFieldID, toFieldID);
		}
		
		if (MoveInt.isCapture(move)) { //Capture
			
			//if (true) throw new IllegalStateException(); 
			
			int capturedPID = MoveInt.getCapturedFigurePID(move);
			final int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];
			
			
			if (MoveInt.isEnpassant(move)) {
				
				//if (true) throw new IllegalStateException(); 
				
				int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
				
				board[capturedFieldID] = Constants.PID_NONE;
				
				pieces.rem(capturedPID, capturedFieldID);
				
				hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
				
				if (capturedFigureType == Figures.TYPE_PAWN) {
					pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
				}
			} else {
				
				pieces.rem(capturedPID, toFieldID);
				
				hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
				
				if (capturedFigureType == Figures.TYPE_PAWN) {
					pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
				}
			}
			
		} else if (MoveInt.isCastling(move)) {

			
			int castlePID = MoveInt.getCastlingRookPID(move);
			
			int fromCastleFieldID = MoveInt.getCastlingRookFromID(move);
			int toCastleFieldID = MoveInt.getCastlingRookToID(move);
			
			pieces.move(castlePID, fromCastleFieldID, toCastleFieldID);
			
			board[fromCastleFieldID] = Constants.PID_NONE;
			board[toCastleFieldID] = castlePID;
			
			
			hashkey ^= ConstantStructure.MOVES_KEYS[castlePID][fromCastleFieldID];
			hashkey ^= ConstantStructure.MOVES_KEYS[castlePID][toCastleFieldID];
			
			castledByColour[figureColour] = MoveInt.isCastleKingSide(move) ? CastlingType.KINGSIDE : CastlingType.QUEENSIDE;
		}
		
		if (MoveInt.isPromotion(move)) {
			
			
			//if (true) throw new IllegalStateException();
			
			pieces.rem(pid, toFieldID);
			hashkey ^= ConstantStructure.MOVES_KEYS[pid][toFieldID];
			pawnskey ^= ConstantStructure.MOVES_KEYS[pid][toFieldID];
			
			int promotedFigurePID = MoveInt.getPromotionFigurePID(move);
			pieces.add(promotedFigurePID, toFieldID);
			hashkey ^= ConstantStructure.MOVES_KEYS[promotedFigurePID][toFieldID];
			
			board[toFieldID] = promotedFigurePID;
			
		}
		
		
		switchLastMoveColour();
		
		
		playedBoardStates.inc(hashkey);
		
		playedMoves[playedMovesCount++] = move;
		
		
		if (Properties.DEBUG_MODE) {
			if (Properties.DEBUG_LEVEL == Properties.DEBUG_LEVEL3) {
				checkConsistency();
			}
		}
		
		
		if (moveListeners.length > 0) {
			for (int i=0; i<moveListeners.length; i++) {
				moveListeners[i].postForwardMove(MoveInt.getColour(move), move);
			}
		}
		
		invalidatedInChecksCache();
		
		if (DEBUG) checkConsistency();
	}
	
	
	public final void makeMoveBackward(final int move) {

		if (DEBUG) checkConsistency();

		makeMoveBackward(move, true);
	}
	
	
	public final void makeMoveBackward(final int move, boolean invalidateCheckKeepers) {
		
		
		if (eval != null) eval.unmove(move);
		if (moveListeners.length > 0) {
			for (int i=0; i<moveListeners.length; i++) {
				moveListeners[i].preBackwardMove(MoveInt.getColour(move), move);
			}
		}
		
		
		final int pid = MoveInt.getFigurePID(move);
		final int fromFieldID = MoveInt.getFromFieldID(move);
		final int toFieldID = MoveInt.getToFieldID(move);
		
		
		//Update enpassant flag and colour
		BackupInfo curInfo = backupInfo[playedMovesCount];
		BackupInfo prevInfo = playedMovesCount > 0 ? backupInfo[playedMovesCount - 1] : null;
		if (prevInfo != null && curInfo.enpassantPawnFieldID != prevInfo.enpassantPawnFieldID) {
			//hashkey ^= ConstantStructure.HAS_ENPASSANT;
		}
		curInfo.enpassantPawnFieldID = -1;
		
		board[fromFieldID] = pid;
		board[toFieldID] = Constants.PID_NONE;
		
		
		if (!MoveInt.isPromotion(move)) pieces.move(pid, toFieldID, fromFieldID);
		
		/**
		 * Must be before capture block in order to revive captured figure at promotion move in figureIDPerFieldID array.
		 */
		if (MoveInt.isPromotion(move)) {
			int promotedFigurePID = MoveInt.getPromotionFigurePID(move);
			
			pieces.rem(promotedFigurePID, toFieldID);
			//hashkey ^= MOVES_KEYS[promotedFigurePID][toFieldID];
			
			pieces.add(pid, fromFieldID);
			//hashkey ^= MOVES_KEYS[pid][toFieldID]; // First remove already added figure above
			//pawnskey ^= MOVES_KEYS[pid][toFieldID]; // Remove already added pawn above
			
		}
		
		if (MoveInt.isCapture(move)) { //Capture
			
			//if (true) throw new IllegalStateException();
			
			final int capturedPID = MoveInt.getCapturedFigurePID(move);
			//final int capturedFigureColour = Figures.OPPONENT_COLOUR[figureColour];
			//final int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];

			if (MoveInt.isEnpassant(move)) {
				
				//if (true) throw new IllegalStateException(); 
				
				int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
				
				board[capturedFieldID] = capturedPID;
				
				pieces.add(capturedPID, capturedFieldID);
				
			} else {
				board[toFieldID] = capturedPID;
					
				pieces.add(capturedPID, toFieldID);
			}
			
			/*if (capturedFigureType == Figures.TYPE_PAWN) {
				//pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
			}*/
			
		} else if (MoveInt.isCastling(move)) {

			int castlePID = MoveInt.getCastlingRookPID(move);
			
			int fromCastleFieldID = MoveInt.getCastlingRookFromID(move);
			int toCastleFieldID = MoveInt.getCastlingRookToID(move);
			
			pieces.move(castlePID, toCastleFieldID, fromCastleFieldID);
			
			board[fromCastleFieldID] = castlePID;
			board[toCastleFieldID] = Constants.PID_NONE;
			
			castledByColour[MoveInt.getColour(move)] = CastlingType.NONE;
		}
		
		
		switchLastMoveColour();
		
		
		playedMoves[--playedMovesCount] = 0;
		
		
		if (moveListeners.length > 0) {
			for (int i=0; i<moveListeners.length; i++) {
				moveListeners[i].postBackwardMove(MoveInt.getColour(move), move);
			}
		}
		
		
		if (DEBUG)  checkConsistency();

		hashkey = prevInfo.hashkey;
		pawnskey = prevInfo.pawnshash;
		lastCaptureOrPawnMoveBefore = prevInfo.lastCaptureOrPawnMoveBefore;
		
		invalidatedInChecksCache();
	}

	public void makeNullMoveForward() {
		BackupInfo curInfo = backupInfo[playedMovesCount];		
		BackupInfo nextInfo = backupInfo[playedMovesCount + 1];
		
		nextInfo.w_kingSideAvailable = curInfo.w_kingSideAvailable;
		nextInfo.w_queenSideAvailable = curInfo.w_queenSideAvailable;
		nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
		nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
		nextInfo.enpassantPawnFieldID = curInfo.enpassantPawnFieldID;
		
		playedMoves[playedMovesCount++] = 0;
		
		switchLastMoveColour();
	}
	
	public final void makeNullMoveBackward() {

		playedMoves[--playedMovesCount] = 0;
		
		switchLastMoveColour();
	}
	
	
	public final void switchLastMoveColour() {
		lastMoveColour = Figures.OPPONENT_COLOUR[lastMoveColour];
		hashkey ^= ConstantStructure.WHITE_TO_MOVE;
		pawnskey ^= ConstantStructure.WHITE_TO_MOVE;
	}
	

	public SEE getSee() {
		return see;
		//throw new UnsupportedOperationException();
	}
	
	
	/**
	 * MOVEGEN
	 */
	
	protected final boolean kingSidePossible(int colour, int opponentColour) {
		
		int[] fieldIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[colour];
		boolean notOccupied = true;
		for (int i=0; i<fieldIDs.length; i++) {
			if (board[fieldIDs[i]] != Constants.PID_NONE) {
				notOccupied = false;
				break;
			}
		}
		
		return (colour == Constants.COLOUR_WHITE ? backupInfo[playedMovesCount].w_kingSideAvailable : backupInfo[playedMovesCount].b_kingSideAvailable)
		       && notOccupied
		       && board[(colour == Constants.COLOUR_WHITE ? Fields.H1_ID : Fields.H8_ID)] == (colour == Constants.COLOUR_WHITE ? Constants.PID_W_ROOK : Constants.PID_B_ROOK)
		       && checkCheckingAtKingSideFields(colour, opponentColour);
	}

	protected final boolean queenSidePossible(int colour, int opponentColour) {

		int[] fieldIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[colour];
		boolean notOccupied = true;
		for (int i=0; i<fieldIDs.length; i++) {
			if (board[fieldIDs[i]] != Constants.PID_NONE) {
				notOccupied = false;
				break;
			}
		}
		
		if (notOccupied) {
			int BfieldID = (colour == Constants.COLOUR_WHITE) ? Fields.B1_ID : Fields.B8_ID;
			if (board[BfieldID] != Constants.PID_NONE) {
				notOccupied = false;
			}
		}
		
		return (colour == Constants.COLOUR_WHITE ? backupInfo[playedMovesCount].w_queenSideAvailable : backupInfo[playedMovesCount].b_queenSideAvailable)
		       && notOccupied
		       && board[(colour == Constants.COLOUR_WHITE ? Fields.A1_ID : Fields.A8_ID)] == (colour == Constants.COLOUR_WHITE ? Constants.PID_W_ROOK : Constants.PID_B_ROOK)
		       && checkCheckingAtQueenSideFields(colour, opponentColour);
	}
	
	private final boolean checkCheckingAtKingSideFields(int colour, int opponentColour) {
		
		int kingFieldID = getKingFieldID(colour);
		if (FieldAttack.isFieldAttacked(kingFieldID, opponentColour, board, pieces)) {
			return false;
		}
		
		int[] fieldsIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[colour];
		for (int i=0; i<fieldsIDs.length; i++) {
			int fieldID = fieldsIDs[i];
			if (FieldAttack.isFieldAttacked(fieldID, opponentColour, board, pieces)) {
				return false;
			}
		}
		
		return true;
	}
	
	private final boolean checkCheckingAtQueenSideFields(int colour, int opponentColour) {
		
		int kingFieldID = getKingFieldID(colour);
		if (FieldAttack.isFieldAttacked(kingFieldID, opponentColour, board, pieces)) {
			return false;
		}
		
		int[] fieldsIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[colour];
		for (int i=0; i<fieldsIDs.length; i++) {
			int fieldID = fieldsIDs[i];
			if (FieldAttack.isFieldAttacked(fieldID, opponentColour, board, pieces)) {
				return false;
			}
		}
		
		return true;
	}
	

	protected final int getKingFieldID(int colour) {
		int kingFieldID = pieces.getPieces(colour == Figures.COLOUR_WHITE ? Constants.PID_W_KING : Constants.PID_B_KING).getData()[0];
		return kingFieldID;
	}

	protected final PiecesList getKingIndexSet(int colour) {
		return pieces.getPieces(colour == Figures.COLOUR_WHITE ? Constants.PID_W_KING : Constants.PID_B_KING);
	}

	
	public final int genAllMoves(final IInternalMoveList list, boolean checkKeepersAware) {
		return genAllMoves(0L, false, getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	public final int genAllMoves(final IInternalMoveList list) {
		return genAllMoves(0L, true, getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	public final int genAllMoves(final IInternalMoveList list, long excludedToFieldsBoard) {
		return genAllMoves(excludedToFieldsBoard, true, getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	
	protected final int genAllMoves(long excludedToFieldsBoard, boolean checkKeepersAware, int colour, final IInternalMoveList list, final int maxCount) {
		
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		genAllMoves_FiguresWithSameType(colour, opponentColour, Figures.TYPE_PAWN, list);
		genAllMoves_FiguresWithSameType(colour, opponentColour, Figures.TYPE_KNIGHT, list);
		genAllMoves_FiguresWithSameType(colour, opponentColour, Figures.TYPE_OFFICER, list);
		genAllMoves_FiguresWithSameType(colour, opponentColour, Figures.TYPE_CASTLE, list);
		genAllMoves_FiguresWithSameType(colour, opponentColour, Figures.TYPE_QUEEN, list);
		genAllMoves_FiguresWithSameType(colour, opponentColour, Figures.TYPE_KING, list);
		
		return 0;
	}
	
	
	private final void genAllMoves_FiguresWithSameType(
			final int colour, final int opponentColour, final int type,
			final IInternalMoveList list) {
		
		final int pid = Figures.getPidByColourAndType(colour, type);
		final PiecesList fields = pieces.getPieces(pid);
		
		final int size = fields.getDataSize();
		final int[] data = fields.getData();
		
		for (int i=0; i<size; i++) {
			
			final int fieldID = data[i];
			
			genAllMoves_ByFigureID(fieldID, pid, colour, opponentColour, type, list);
		}
	}
	
	
	private final void genAllMoves_ByFigureID(int fieldID, int pid,
			final int colour, final int opponentColour, final int type,
			final IInternalMoveList list) {
		
			
		switch(type) {
			case Figures.TYPE_KNIGHT:
				KnightMovesGen.genAllMoves(pid, fieldID, board, list);
		
				break;
			case Figures.TYPE_KING:
				
		
					int opKingID = getKingFieldID(opponentColour);
					KingMovesGen.genAllMoves(pid,
							fieldID,
							board,
							opKingID,
							kingSidePossible(colour, opponentColour),
							queenSidePossible(colour, opponentColour),
							list);
				
				break;
			case Figures.TYPE_PAWN:
				if (colour == Figures.COLOUR_WHITE) {
						WhitePawnMovesGen.genAllMoves(
								fieldID,
								board,
								backupInfo[playedMovesCount].enpassantPawnFieldID,
								list);
				} else {
						BlackPawnMovesGen.genAllMoves(
								fieldID,
								board,
								backupInfo[playedMovesCount].enpassantPawnFieldID,
								list);
				}
				break;
			case Figures.TYPE_OFFICER:
					OfficerMovesGen.genAllMoves(pid,
							fieldID,
							board,
							list);
				
				break;
			case Figures.TYPE_CASTLE:
					CastleMovesGen.genAllMoves(pid,
							fieldID,
							board,
							list);
				break;
			case Figures.TYPE_QUEEN:
					QueenMovesGen.genAllMoves(pid,
							fieldID,
							board,
							list);
				break;
		}
	}
	
	
	public final int genCapturePromotionMoves(final IInternalMoveList list) {
		return genCapturePromotionMoves(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	
	private final int genCapturePromotionMoves(int colour, final IInternalMoveList list, final int maxCount) {

		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		genCaptureMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_PAWN, list, maxCount);
		
		genCaptureMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_KNIGHT, list, maxCount);
		
		genCaptureMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_OFFICER, list, maxCount);
		
		genCaptureMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_CASTLE, list, maxCount);

		genCaptureMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_QUEEN, list, maxCount);
		
		genCaptureMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_KING, list, maxCount);

		
		return 0;
	}
	
	
	public final void genCaptureMoves_FiguresWithSameType(final int colour, final int opponentColour, final int type,
			final IInternalMoveList list, final int maxCount) {
		
		final int pid = Figures.getPidByColourAndType(colour, type);
		final PiecesList fields = pieces.getPieces(pid);
		
		final int size = fields.getDataSize();
		final int[] data = fields.getData();
		
		for (int i=0; i<size; i++) {
			
			final int fieldID = data[i];
			
			genCaptureMoves_ByFigureID(fieldID, pid, colour, opponentColour, type, list);
		}
	}

	
	private final void genCaptureMoves_ByFigureID(int fieldID, int pid,
			final int colour, final int opponentColour, final int type,
			final IInternalMoveList list) {
		
			
		switch(type) {
			case Figures.TYPE_KNIGHT:
				KnightMovesGen.genCaptureMoves(pid, fieldID, board, list);
		
				break;
			case Figures.TYPE_KING:
				
		
					int opKingID = getKingFieldID(opponentColour);
					KingMovesGen.genCaptureMoves(pid,
							fieldID,
							board,
							opKingID,
							list);
				
				break;
			case Figures.TYPE_PAWN:
				if (colour == Figures.COLOUR_WHITE) {
						WhitePawnMovesGen.genCapturePromotionMoves(
								fieldID,
								board,
								list);
				} else {
						BlackPawnMovesGen.genCapturePromotionMoves(
								fieldID,
								board,
								list);
				}
				break;
			case Figures.TYPE_OFFICER:
					OfficerMovesGen.genCaptureMoves(pid,
							fieldID,
							board,
							list);
				
				break;
			case Figures.TYPE_CASTLE:
					CastleMovesGen.genCaptureMoves(pid,
							fieldID,
							board,
							list);
				break;
			case Figures.TYPE_QUEEN:
					QueenMovesGen.genCaptureMoves(pid,
							fieldID,
							board,
							list);
				break;
		}
	}
	
	
	public final int genNonCaptureNonPromotionMoves(final IInternalMoveList list) {
		return genNonCaptureNonPromotionMoves(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
			
	private final int genNonCaptureNonPromotionMoves(int colour, final IInternalMoveList list, final int maxCount) {
		throw new UnsupportedOperationException();
	}
	
	
	/**
	 * Board Struct
	 */
	
	public void revert() {
		int count = getPlayedMovesCount();
		int[] moves = getPlayedMoves();
		for (int i=count - 1; i>=0; i--) {
			if (moves[i] == 0) {
				makeNullMoveBackward();
			} else {
				makeMoveBackward(moves[i]);	
			}
		}
	}
	
	
	private void init(int[] boardArr) {
		
		for (int fieldID=0; fieldID<Fields.ID_MAX; fieldID++) {
			
			int pid = boardArr[fieldID];
			
			board[fieldID] = pid;
			
			if (pid != Constants.PID_NONE) {
				pieces.add(pid, fieldID);
				
				materialFactor.initially_addPiece(pid, fieldID, 0);
				materialState.initially_addPiece(pid, fieldID, 0);
				if (eval != null) eval.initially_addPiece(pid, fieldID, 0);
				
				hashkey ^= ConstantStructure.MOVES_KEYS[pid][fieldID];
				if (pid == Constants.PID_W_PAWN ||
						pid == Constants.PID_B_PAWN ||
						pid == Constants.PID_W_KING ||
						pid == Constants.PID_B_KING 
					) {
					pawnskey ^= ConstantStructure.MOVES_KEYS[pid][fieldID];
				}
			}
		}
	}
	
	
	public final IPiecesLists getPiecesLists() {
		return pieces;
	}
	
	
	public final long getPawnsHashKey() {
		return pawnskey;
	}
	
	
	@Override
	public IBaseEval getBaseEvaluation() {
		return eval;
	}
	
	
	@Override
	public IMaterialFactor getMaterialFactor() {
		return materialFactor;
	}

	@Override
	public IMaterialState getMaterialState() {
		return materialState;
	}	
	
	
	public int getFigureID(int fieldID) {
		return board[fieldID];
	}
	

	public boolean hasRightsToKingCastle(int colour) {
		return colour == Figures.COLOUR_WHITE ? backupInfo[playedMovesCount].w_kingSideAvailable : backupInfo[playedMovesCount].b_kingSideAvailable;
	}
	
	public boolean hasRightsToQueenCastle(int colour) {
		return colour == Figures.COLOUR_WHITE ? backupInfo[playedMovesCount].w_queenSideAvailable : backupInfo[playedMovesCount].b_queenSideAvailable;
	}
	
	public int getPlayedMovesCount() {
		return playedMovesCount;
	}
	
	
	public int[] getPlayedMoves() {
		return playedMoves;
	}
	
	public int getLastMove() {
		if (playedMovesCount > 0) {
			return playedMoves[playedMovesCount - 1];
		} else return 0;
	}
	
	public int getLastLastMove() {
		if (playedMovesCount > 1) {
			return playedMoves[playedMovesCount - 2];
		} else return 0;
	}
	
	public final int getColourToMove() {
		return Figures.OPPONENT_COLOUR[lastMoveColour];
	}
	
	public final int getStateRepetition() {
		return getStateRepetition(hashkey);
	}
	
	public final int getStateRepetition(long hashkey) {
		int count = playedBoardStates.get(hashkey);
		if (count == StackLongInt.NO_VALUE) {
			return 0;
		} else return count;
	}
	
	public final long getHashKey() {
		return hashkey;
	}
	
	public final StackLongInt getPlayedBoardStates() {
		return playedBoardStates;
	}
	
	
	public IBoard clone() {
		Board3 clone = null;
		try {
			clone = (Board3) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cloneInternal(clone);
		return clone;
	}
	
	public void cloneInternal(Board3 clone) {
		//clone.free = free;
		//clone.allByColour = Utils.copy(allByColour);
		//clone.allByColourAndType = Utils.copy(allByColourAndType);
		clone.board = Utils.copy(board);
		//clone.stateManager = stateManager.clone();
		//TODO: clone.hasEnpassant = hasEnpassant;
		//clone.possibleQueenCastleSideByColour = Utils.copy(possibleQueenCastleSideByColour);
		//clone.possibleKingCastleSideByColour = Utils.copy(possibleKingCastleSideByColour);
		//clone.castledByColour = Utils.copy(castledByColour);
		clone.lastMoveColour = lastMoveColour;
		clone.hashkey = hashkey;
		clone.pawnskey = pawnskey;
		//clone.material = material.clone();
	}
	
	public int hashCode() {
		return (int) hashkey; 
	}
	
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof Board3) {
			Board3 other = (Board3) obj;
			//result = other.free == free;
			//result = result && Arrays.equals(other.allByColour, allByColour);
			//result = result && Utils.equals(other.allByColourAndType, allByColourAndType);
			result = Arrays.equals(other.board, board);
			result = result && checkAliveFiguresData(other);
			//result = result && other.stateManager.equals(stateManager);
			//TODO: result = result && other.hasEnpassant == hasEnpassant;
			//result = result && Arrays.equals(other.possibleQueenCastleSideByColour, possibleQueenCastleSideByColour);
			//result = result && Arrays.equals(other.possibleKingCastleSideByColour, possibleKingCastleSideByColour);
			result = result && Arrays.equals(other.castledByColour, castledByColour);
			result = result && other.lastMoveColour == lastMoveColour;
			result = result && other.hashkey == hashkey;
			result = result && other.pawnskey == pawnskey;
			//result = result && other.material.equals(material);
		}
		return result;
	}
	
	private boolean checkAliveFiguresData(Board3 other) {
		//FiguresStateManager otherStateManager = other.stateManager;
		//boolean result = checkAliveFiguresDataForColour(other, otherStateManager.getAliveFigures(Figures.COLOUR_WHITE), stateManager.getAliveFigures(Figures.COLOUR_WHITE));
		//result = result && checkAliveFiguresDataForColour(other, otherStateManager.getAliveFigures(Figures.COLOUR_BLACK), stateManager.getAliveFigures(Figures.COLOUR_BLACK));
		return true;
	}
	
	
	public String toString() {
		String result = "\r\nWhite: " + /*Bits.toBinaryString(allByColour[Figures.COLOUR_WHITE])+ */ "\r\n";
		result += "Black: " + /*Bits.toBinaryString(allByColour[Figures.COLOUR_BLACK]) +*/ "\r\n";
		result += matrixToString();
		result += "Moves: " + movesToString() + "\r\n";
		result += "EPD: " + toEPD() + "\r\n";
		//TODO:Fix result += "Game status: " + getStatus() + "\r\n";
		return result;
	}

	private String matrixToString() {
		//char[] columnsLetters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
		//StringBuffer loStringBuffer = new StringBuffer(400);
		
		
		String result = "";
		
		int counter = 0;
		String line = "";
		for (int square=0; square<board.length; square++) {
			
			int pieceID = board[square];
			
			//if ((square & 0x88) == 0) {
				
				String squareStr = Constants.getPieceIDString(pieceID);
				squareStr += "  ";
				line += squareStr;
				
				counter++;
				if (counter == 8) {
					counter = 0;
					result = line + "\r\n" + result;
					line = "";
				}
			//}
		}
		
		result += "\r\n";
		result += "\r\n";
		result += "Hashkey : " + hashkey;
		result += "\r\n";
		result += "Pawnkey : " + pawnskey;
		result += "\r\n";
		
		return result;

	}
	
	private String movesToString() {
		String result = "";
		for (int i=0; i<playedMovesCount; i++) {
			int move = playedMoves[i];
			result += getMoveOps().moveToString(move) + ", ";
		}
		return result;
	}
	
	
	protected void checkConsistency() {
		long allWhiteBitboard = NUMBER_0;
		
		//IndexNumberSet[] allWhite = stateManager.getAliveFigures(Figures.COLOUR_WHITE);
		
		allWhiteBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_WHITE,
				Figures.TYPE_PAWN, pieces.getPieces(Constants.PID_W_PAWN));
		allWhiteBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_WHITE,
				Figures.TYPE_KNIGHT, pieces.getPieces(Constants.PID_W_KNIGHT));
		allWhiteBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_WHITE,
				Figures.TYPE_KING, pieces.getPieces(Constants.PID_W_KING));
		allWhiteBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_WHITE,
				Figures.TYPE_OFFICER, pieces.getPieces(Constants.PID_W_BISHOP));
		allWhiteBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_WHITE,
				Figures.TYPE_CASTLE, pieces.getPieces(Constants.PID_W_ROOK));
		allWhiteBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_WHITE,
				Figures.TYPE_QUEEN, pieces.getPieces(Constants.PID_W_QUEEN));
		
		long allBlackBitboard = NUMBER_0;
		//IndexNumberSet[] allBlack = stateManager.getAliveFigures(Figures.COLOUR_BLACK);
		allBlackBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_BLACK,
				Figures.TYPE_PAWN, pieces.getPieces(Constants.PID_B_PAWN));
		allBlackBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_BLACK,
				Figures.TYPE_KNIGHT, pieces.getPieces(Constants.PID_B_KNIGHT));
		allBlackBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_BLACK,
				Figures.TYPE_KING, pieces.getPieces(Constants.PID_B_KING));
		allBlackBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_BLACK,
				Figures.TYPE_OFFICER, pieces.getPieces(Constants.PID_B_BISHOP));
		allBlackBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_BLACK,
				Figures.TYPE_CASTLE, pieces.getPieces(Constants.PID_B_ROOK));
		allBlackBitboard |= checkConsistency_AliveFiguresByTypeAndColour(Figures.COLOUR_BLACK,
				Figures.TYPE_QUEEN, pieces.getPieces(Constants.PID_B_QUEEN));
	}

	protected long checkConsistency_AliveFiguresByTypeAndColour(int colour, int type, PiecesList figIDs) {
		
		long typeBitboard = NUMBER_0;
		
		for (int i=0; i<64; i++) {
			int pid = board[i];
			if (pid == Figures.getPidByColourAndType(colour, type)) {
				if (!figIDs.contains(i)) {
					throw new IllegalStateException();
				}
			}
		}
		
		int size = figIDs.getDataSize();
		int[] data = figIDs.getData();
		for (int i=0; i<size; i++) {
			
			int fieldID = data[i];
			if (fieldID < 0 || fieldID >= 64) {
				throw new IllegalStateException("figureID == " + fieldID);
			}
			
			if (board[fieldID] != Figures.getPidByColourAndType(colour, type))  {
				throw new IllegalStateException("board[fieldID]=" + board[fieldID]
				                                + ", Figures.getPidByColourAndType(colour, type)=" + Figures.getPidByColourAndType(colour, type));
			}
			
			long figBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
			if (fieldID != get67IDByBitboard(figBitboard)) {
				throw new IllegalStateException("fieldID=" + fieldID + " figBitboard=" + figBitboard);
			}
			
			typeBitboard |= figBitboard;
		}
		
		return typeBitboard;
	}


	public BackupInfo[] getBackups() {
		return backupInfo;
	}
	

	public int[] getOpeningMoves() {
		int movesCount = getPlayedMovesCount();
		int[] moves = getPlayedMoves();
		int[] o_moves = new int[movesCount];
		for (int i=0; i<movesCount; i++) {
			o_moves[i] = moves[i];
		}
		return o_moves;
	}
	

	@Override
	public IBoardConfig getBoardConfig() {
		return boardConfig;
	}


	@Override
	public boolean isDraw50movesRule() {
		return lastCaptureOrPawnMoveBefore >= 100;
	}
	
	
	@Override
	public int getDraw50movesRule() {
		return lastCaptureOrPawnMoveBefore;
	}
	
	
	@Override
	public boolean hasSufficientMatingMaterial() {
		
		return hasSufficientMatingMaterial(Figures.COLOUR_WHITE) || hasSufficientMatingMaterial(Figures.COLOUR_BLACK);
	}
	
	
	@Override
	public boolean hasSufficientMatingMaterial(int color) {
		
		
		/**
		 * If has pawn - true
		 */
		long pawns = getFiguresBitboardByColourAndType(color, Figures.TYPE_PAWN);
		if (pawns != 0L) {
			return true;
		}
		
		
		/**
		 * If has queen - true
		 */
		long queens = getFiguresBitboardByColourAndType(color, Figures.TYPE_QUEEN);
		if (queens != 0L) {
			return true;
		}
		
		
		/**
		 * If has rook - true
		 */
		long rooks = getFiguresBitboardByColourAndType(color, Figures.TYPE_CASTLE);
		if (rooks != 0L) {
			return true;
		}
		
		
		long bishops = getFiguresBitboardByColourAndType(color, Figures.TYPE_OFFICER);
		long knights = getFiguresBitboardByColourAndType(color, Figures.TYPE_KNIGHT);
		
		
		/**
		 * If has 3 or more bishops and knights = true
		 */
		if (Utils.countBits(bishops) + Utils.countBits(knights) >= 3) {
			
			return true;
		}
		
		
		/**
		 * If has 2 different colors bishop - true
		 */
		if (bishops != 0L) {
			
			if ((bishops & Fields.ALL_WHITE_FIELDS) != 0 && (bishops & Fields.ALL_BLACK_FIELDS) != 0) {
				
				return true;
			}
		}
		
		
		/**
		 * If has 1 bishop and 1 knight - true
		 */
		if (Utils.countBits(bishops) == 1 && Utils.countBits(knights) == 1) {
			
			if ((bishops & Fields.ALL_WHITE_FIELDS) != 0 && (bishops & Fields.ALL_BLACK_FIELDS) != 0) {
				
				return true;
			}
		}
		
		
		/**
		 * If all other cases - false
		 */
		return false;
	}
	
	
	@Override
	public boolean isInCheck() {
		
		int colourToMove = getColourToMove();
		
		boolean result = isInCheck(colourToMove);
		
		return result;
	}
	
	
	@Override
	public boolean isInCheck(int colour) {
		
		if (inCheckCacheInitialized[colour]) {
			return inCheckCache[colour];
		}
		
		if (pieces.getPieces(Constants.PID_W_KING).getDataSize() == 0) {
			//throw new IllegalStateException("Captured white king");
			return false;//Fix for Chess Art for Kids - Chess Pieces Aware mode
		}
		
		if (pieces.getPieces(Constants.PID_B_KING).getDataSize() == 0) {
			//throw new IllegalStateException("Captured black king");
			return false;//Fix for Chess Art for Kids - Chess Pieces Aware mode
		}
		
		int kingFieldID = (colour == Constants.COLOUR_WHITE)
			? pieces.getPieces(Constants.PID_W_KING).getData()[0] : pieces.getPieces(Constants.PID_B_KING).getData()[0];
		
		boolean result = FieldAttack.isFieldAttacked(kingFieldID, Constants.COLOUR_OP[colour], board, pieces);
		
		inCheckCacheInitialized[colour] = true;
		inCheckCache[colour] = result;
		
		return result;
	}
	
	
	protected final void invalidatedInChecksCache() {
		inCheckCacheInitialized[Figures.COLOUR_WHITE] = false;
		inCheckCacheInitialized[Figures.COLOUR_BLACK] = false;
	}
	
	
	@Override
	public boolean hasMoveInCheck() {
		return true;
	}


	@Override
	public boolean hasMoveInNonCheck() {
		return true;
	}

	
	public int[] getMatrix() {
		return board;
	}
}

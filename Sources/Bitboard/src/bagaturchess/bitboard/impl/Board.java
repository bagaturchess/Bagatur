/**  BagaturChess (UCI chess engine and tools)
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
package bagaturchess.bitboard.impl;

import java.util.Arrays;

import bagaturchess.bitboard.api.IAttackListener;
import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMobility;
import bagaturchess.bitboard.api.IMoveIterator;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.BackupInfo;
import bagaturchess.bitboard.common.BoardStat;
import bagaturchess.bitboard.common.CastlingType;
import bagaturchess.bitboard.common.Fen;
import bagaturchess.bitboard.common.GamePhase;
import bagaturchess.bitboard.common.GlobalConstants;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Properties;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.attacks.SEE;
import bagaturchess.bitboard.impl.attacks.control.AttackListener_Mobility;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.attacks.fast.FastPlayersAttacks;
import bagaturchess.bitboard.impl.cfg.BoardConfigImpl;
import bagaturchess.bitboard.impl.datastructs.StackLongInt;
import bagaturchess.bitboard.impl.datastructs.numbers.IndexNumberMap;
import bagaturchess.bitboard.impl.datastructs.numbers.IndexNumberSet;
import bagaturchess.bitboard.impl.endgame.MaterialState;
import bagaturchess.bitboard.impl.eval.BaseEvaluation;
import bagaturchess.bitboard.impl.eval.MaterialFactor;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnStructureConstants;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movegen.BlackPawnMovesGen;
import bagaturchess.bitboard.impl.movegen.CastleMovesGen;
import bagaturchess.bitboard.impl.movegen.KingMovesGen;
import bagaturchess.bitboard.impl.movegen.KnightMovesGen;
import bagaturchess.bitboard.impl.movegen.Move;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movegen.OfficerMovesGen;
import bagaturchess.bitboard.impl.movegen.QueenMovesGen;
import bagaturchess.bitboard.impl.movegen.WhitePawnMovesGen;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;
import bagaturchess.bitboard.impl.plies.checking.BlackPawnsChecks;
import bagaturchess.bitboard.impl.plies.checking.Checker;
import bagaturchess.bitboard.impl.plies.checking.Checking;
import bagaturchess.bitboard.impl.plies.checking.CheckingCount;
import bagaturchess.bitboard.impl.plies.checking.KnightChecks;
import bagaturchess.bitboard.impl.plies.checking.WhitePawnsChecks;
import bagaturchess.bitboard.impl.plies.specials.Castling;
import bagaturchess.bitboard.impl.plies.specials.Enpassanting;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl.state.PiecesLists;
import bagaturchess.bitboard.impl.zobrist.ConstantStructure;
//import bagaturchess.bitboard.datastructs.lrmmap.DataObjectFactory;


public class Board extends Fields implements IBitBoard, Cloneable {
	
	private boolean DEBUG = false;


	public BoardStat statistics;
	
	public long free;
	
	public long[] allByColour;
	
	public long[][] allByColourAndType;
	
	public int[] board;
	
	//protected boolean[] possibleQueenCastleSideByColour_initial = new boolean[Figures.COLOUR_MAX];
	//protected boolean[] possibleKingCastleSideByColour_initial = new boolean[Figures.COLOUR_MAX];
	
	//protected boolean[] possibleQueenCastleSideByColour = new boolean[Figures.COLOUR_MAX];
	//protected boolean[] possibleKingCastleSideByColour = new boolean[Figures.COLOUR_MAX];
	
	protected BackupInfo[] backupInfo;
	//protected boolean hasEnpassant;
	//protected int enpassantColour; //The colour of the player which can make enpassant capture
	//protected long enpassantPawnBitboard; //The pawn which provokes the enpassant capture (this pawn which will be captured via the move)
	
	protected int lastMoveColour = Figures.COLOUR_BLACK;
	
	public PiecesLists pieces;
	
	//public long[][] initialMatrix; //It would not be updated at making moves forward/backward
	
	protected long hashkey = Bits.NUMBER_0;
	protected long pawnskey = Bits.NUMBER_0;
	
	protected int[] castledByColour;
	protected int lastCastledColour = Figures.COLOUR_UNSPECIFIED;
	
	protected int lastCaptureOrPawnMoveBefore = 0;
	protected int lastCaptureFieldID = -1;
	
	protected int[] playedMoves;
	protected int playedMovesCount = 0;
	protected int playedMovesCount_initial = 0;
	protected int marked_playedMovesCount = 0;
	
	//protected HashMap<Long, Integer> playedBoardStates;
	protected StackLongInt playedBoardStates;
	//protected StackLongInt playedPawnStates;
	
	protected IndexNumberMap[] checkKeepersBuffer;
	protected boolean[] checkKeepersInitialized; 
	protected boolean[] inCheckCache;
	protected boolean[] inCheckCacheInitialized; 
	protected Checker checkerBuffer;
	
	private IndexNumberSet duplicatesRemoverBuffer = new IndexNumberSet(ConstantStructure.MOVES_INDEXES_MAX);
	
	//protected MoveIteratorFactory[] moveIteratorFactory;
	//protected MoveIterator[][][] iterators;
	
	protected IInternalMoveList movesBuffer;
	//private long[][] movesIsPossibleInCheckBuffer = new long[30][Move.MOVE_LONGS_COUNT];
	
	//private boolean attacksSupport_old = false;
	
	private boolean attacksSupport = false;
	private boolean fieldsStateSupport = false;
	private MoveListener fastPlayerAttacks;
	private MoveListener[] moveListeners;
	private FieldsStateMachine fieldAttacksCollector;
	
	/*private int whiteInCheck;
	private int blackInCheck;
	
	private int whiteKingMoves;
	private int blackKingMoves;
	private int whiteQueensMoves;
	private int blackQueensMoves;*/
	
	private BaseEvaluation eval;
	private MaterialFactor materialFactor;
	private MaterialState materialState;
	private PawnsEvalCache pawnsCache;
	
	private SEE see;
	
	private IBoardConfig boardConfig;
	private IMobility attackListener;
	
	
	public Board() {
		this((IBoardConfig) null);
	}
	
	public Board(IBoardConfig boardConfig) {
		this(Constants.INITIAL_BOARD, boardConfig);
	}
	
	public Board(String fenStr) {
		this(fenStr, (IBoardConfig)null);
	}
	
	public Board(String fenStr, IBoardConfig boardConfig) {
		this(fenStr, null, boardConfig);
	}
	
	public Board(String fenStr, PawnsEvalCache _pawnsCache, IBoardConfig _boardConfig) {
		
		pawnsCache = _pawnsCache;
		boardConfig = _boardConfig;
		/*if (pawnsCache == null) {
			
			//if (true) throw new IllegalStateException(); 
			
			DataObjectFactory<PawnsModelEval> pawnsCacheFactory = null;
			try {
				pawnsCacheFactory = (DataObjectFactory<PawnsModelEval>) Board.class.getClassLoader().loadClass(EngineConfigFactory.getDefaultEngineConfiguration().getEvalConfig().getPawnsCacheFactoryClassName()).newInstance();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			
			pawnsCache = new PawnsEvalCache(pawnsCacheFactory, EngineConfigFactory.getDefaultEngineConfiguration().getPawnsCacheSize());
			
			//pawnsCache = new PawnsEvalCache(pawnsCacheFactory, 10000);
		}*/
		
		Fen fen = Fen.parse(fenStr);
		
		statistics = new BoardStat();
		
		allByColour = new long[Figures.COLOUR_MAX];
		allByColourAndType = new long[Figures.COLOUR_MAX][Figures.TYPE_MAX];
		board = new int[Fields.ID_MAX];
		castledByColour = new int[Figures.COLOUR_MAX];
		
		//hasEnpassant = false;
		//hashkey ^= ConstantStructure.HAS_ENPASSANT; there is no enpasant at initial position
		
		lastMoveColour = fen.getColourToMove() == Figures.COLOUR_WHITE ? Figures.COLOUR_BLACK : Figures.COLOUR_WHITE;
		if (fen.getColourToMove() == Figures.COLOUR_WHITE) {
			hashkey ^= ConstantStructure.WHITE_TO_MOVE;
			pawnskey ^= ConstantStructure.WHITE_TO_MOVE;
		} else {
			
		}
		
		pieces = new PiecesLists(this);
		
		if (fen.getHalfmoveClock() != null) {
			try {
				playedMovesCount_initial = Integer.parseInt(fen.getHalfmoveClock());
			} catch(Exception e) {
				
			}
		}
		
		moveListeners = new MoveListener[0];
		
		materialFactor = new MaterialFactor();
		addMoveListener(materialFactor);
		materialState = new MaterialState();
		addMoveListener(materialState);
		
		if (boardConfig != null) {
			eval = new BaseEvaluation(boardConfig, materialFactor);
			addMoveListener(eval);
		}
		
		init(fen.getBoardArray());
		
		/*if (stateManager.isAlive(Castling.CASTLE_ID_FOR_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE])) {
			possibleKingCastleSideByColour[Figures.COLOUR_WHITE] = true;
			hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE];
		}
		if (stateManager.isAlive(Castling.CASTLE_ID_FOR_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK])) {
			possibleKingCastleSideByColour[Figures.COLOUR_BLACK] = true;
			hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK];
		}
		if (stateManager.isAlive(Castling.CASTLE_ID_FOR_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE])) {
			possibleQueenCastleSideByColour[Figures.COLOUR_WHITE] = true;
			hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE];
		}
		if (stateManager.isAlive(Castling.CASTLE_ID_FOR_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK])) {
			possibleQueenCastleSideByColour[Figures.COLOUR_BLACK] = true;
			hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK];
		}*/
		
		playedMoves = new int[GlobalConstants.MAX_MOVES_IN_GAME];
		playedMovesCount = 0;
		
		backupInfo = new BackupInfo[GlobalConstants.MAX_MOVES_IN_GAME];
		for (int i=0; i<backupInfo.length; i++) {
			backupInfo[i] = new BackupInfo();
		}
		
		long enpassantTargetPawn = 0L;
		if (fen.getEnpassantTargetSquare() != null) {
			enpassantTargetPawn = Enpassanting.getEnemyBitboard(fen.getEnpassantTargetSquare());
		}
		
		backupInfo[playedMovesCount].enpassantPawnBitboard = enpassantTargetPawn;
		backupInfo[playedMovesCount].w_kingSideAvailable = fen.hasWhiteKingSide();
		backupInfo[playedMovesCount].w_queenSideAvailable = fen.hasWhiteQueenSide();
		backupInfo[playedMovesCount].b_kingSideAvailable = fen.hasBlackKingSide();
		backupInfo[playedMovesCount].b_queenSideAvailable = fen.hasBlackQueenSide();
		
		if (backupInfo[playedMovesCount].enpassantPawnBitboard != 0L) {
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
		
		//playedPawnStates = new StackLongInt(9631);
		//playedPawnStates.inc(pawnskey);
		
		checkKeepersBuffer = new IndexNumberMap[Figures.COLOUR_MAX];
		checkKeepersBuffer[Figures.COLOUR_WHITE] = new IndexNumberMap(Fields.PRIME_67);
		checkKeepersBuffer[Figures.COLOUR_BLACK] = new IndexNumberMap(Fields.PRIME_67);
		
		checkKeepersInitialized = new boolean[] {false, false, false};
		inCheckCache = new boolean[] {false, false, false};
		inCheckCacheInitialized = new boolean[] {false, false, false};
		
		checkerBuffer = new Checker();
		
		movesBuffer = new BaseMoveList(64);
		
		
		if (boardConfig != null) {
			attacksSupport = boardConfig.getFieldsStatesSupport();
			fieldsStateSupport = boardConfig.getFieldsStatesSupport();
			initAttacksSupport();
		}
				
		//init(fen.getBoardArray());
		
		//int colourToMove = getColourToMove();
		/*if (isInCheck(colourToMove)) {
			if (colourToMove == Figures.COLOUR_WHITE) {
				whiteInCheck++;
			} else {
				blackInCheck++;
			}
		}*/
		
		see = new SEE(this);
		
		checkConsistency();
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
		long enpassTarget = backupInfo[playedMovesCount].enpassantPawnBitboard;
		if (enpassTarget != 0L) {
			int targetSquareID = Enpassanting.converteEnpassantTargetToFENFormat(enpassTarget);
			result += Fields.getFieldSign(targetSquareID);
		} else {
			result += "-";
		}
		
		result += " ";
		result += getPlayedMovesCount_Total();
		
		result += " ";
		result += ((getPlayedMovesCount_Total() + 1) / 2 + 1);
		
		return result;
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
	
	public IBaseEval getBaseEvaluation() {
		return eval;
	}
	
	public PawnsModelEval getPawnsStructure() {
		long pawnskey = getPawnsHashKey();
		
		PawnsModelEval result = pawnsCache.get(pawnskey);
		if (result == null) {
			result = pawnsCache.put(pawnskey);
			result.rebuild(this);
		}
		
		return result;
	}
	
	public void setPawnsCache(PawnsEvalCache pawnsCache) {
		this.pawnsCache = pawnsCache;
	}
	
	public PawnsEvalCache getPawnsCache() {
		return pawnsCache;
	}
	
	public boolean hasUnstoppablePasser() {
		return hasUnstoppablePasser(getColourToMove());
	}
	
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
	
	public int getUnstoppablePasser() {
		
		int result = 0;
		
		//getPawnsCache().lock();
		
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
		
		
		//getPawnsCache().unlock();
		
		return result;
	}
	
	private boolean hasUnstoppablePasser(int colourToMove) {
		
		//if (true) throw new IllegalStateException();
		
		/*if (true) {
			return false;
		}*/
		
		/*if (getBaseEvaluation().getTotalFactor() != 0) {
			return false;
		}*/
		
		/*if (getPawnsCache() == null) {
			return false;
		}*/
		
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
		
		
		/*int w_passed_count = model.getWPassedCount();
		int b_passed_count = model.getBPassedCount();
		if (colourToMove == Figures.COLOUR_WHITE) {
			if (w_passed_count > 0) {
				int w_rank = model.getWUnstoppablePasserRank();
				int b_rank = model.getBMaxPassedRank();
				if (w_rank != 0) {
					if (w_rank > b_rank + 1) {
						result = true;
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
						result = true;
					}
				}
			}
		}*/
		
		getPawnsCache().unlock();
		
		return result;
	}
	
	public final boolean hasMinorOrMajorPieces(int colour) {
		return allByColour[colour] != (allByColourAndType[colour][Figures.TYPE_KING] | allByColourAndType[colour][Figures.TYPE_PAWN]);
	}
	
	/*public void clearKingMovesCounters() {
		whiteKingMoves = 0;
		blackKingMoves = 0;
	}
	
	public int getBlackKingMovesCounts() {
		return blackKingMoves;
	}
	
	public int getWhiteKingMovesCounts() {
		return whiteKingMoves;
	}
	
	public void clearQueenMovesCounters() {
		whiteQueensMoves = 0;
		blackQueensMoves = 0;
	}
	
	public int getBlackQueensMovesCounts() {
		return blackQueensMoves;
	}
	
	public int getWhiteQueensMovesCounts() {
		return whiteQueensMoves;
	}
	
	public final void clearInCheckCounters() {
		whiteInCheck = 0;
		blackInCheck = 0;
	}
	
	public final int getBlackInCheckCounts() {
		return blackInCheck;
	}
	
	public final int getWhiteInCheckCounts() {
		return whiteInCheck;
	}*/
	
	private final void initAttacksSupport() {
		if (attacksSupport) {
			if (!fieldsStateSupport) {
				throw new IllegalStateException();
			}
			//if (fieldsStateSupport) {
				attackListener = boardConfig == null ? null : new AttackListener_Mobility(boardConfig);
				fieldAttacksCollector = new FieldsStateMachine(this, (IAttackListener) attackListener);
			//}
			
			fastPlayerAttacks = new FastPlayersAttacks(this, fieldAttacksCollector);
			((FastPlayersAttacks)fastPlayerAttacks).checkConsistency();
			addMoveListener(fastPlayerAttacks);
		}
	}
	
	private void addMoveListener(MoveListener listener) {
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
	
	public final boolean isPossible(int move) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.isPossible.start();
		}
		
		if (move == 0) {
			throw new IllegalStateException("isPossible invoked with move = 0");
			//return false;
		}
		
		int colour = MoveInt.getColour(move);
		
		if (colour != getColourToMove()) {
			throw new IllegalStateException("isPossible invoked with move which color is not the current colour");
		}
		
		/*if (MoveInt.isCastling(move) || MoveInt.isEnpassant(move)) {
			return false;
		}*/
		
		int type = MoveInt.getFigureType(move);
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		boolean isInCheck = isInCheck(colour);
		
		//if (Properties.DEBUG_MODE) {
			/*if (isInCheck) {
				throw new IllegalStateException("Call to isPossible during check");
			}*/
		//}
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no possible moves.
		 */

		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.isPossible.stop(1);
			}
			return false;
		}
		
		//int pid = (int) move[1];
		/*if (!stateManager.isAlive(figureID)) {
			if (Properties.STATISTICS_MODE) {
				statistics.isPossible.stop(1);
			}
			return false;
		}*/
		
		boolean enpassant = false;
		boolean isPossible = false;
		switch (type) {
			case Figures.TYPE_KNIGHT:
				isPossible = KnightMovesGen.isPossible(move, board);
				break;
			case Figures.TYPE_KING:
				int opKingID = getKingFieldID(opponentColour);
				isPossible = KingMovesGen.isPossible(this, move, board,
						kingSidePossible(colour, opponentColour), queenSidePossible(colour, opponentColour),
						Fields.ALL_ORDERED_A1H1[opKingID],
						opKingID,
						free);
				break;
			case Figures.TYPE_PAWN:
				
				BackupInfo curEnpassInfo = backupInfo[playedMovesCount];
				if (colour == Figures.COLOUR_WHITE) {
					isPossible = WhitePawnMovesGen.isPossible(move, board, free,
							curEnpassInfo.enpassantPawnBitboard != 0,
							curEnpassInfo.enpassantPawnBitboard);
				} else {
					isPossible = BlackPawnMovesGen.isPossible(move, board, free,
							curEnpassInfo.enpassantPawnBitboard != 0,
							curEnpassInfo.enpassantPawnBitboard);
				}
				
				if (isPossible) {
					if (MoveInt.isEnpassant(move)) {
						enpassant = true;
					}
				}
				
				break;
			case Figures.TYPE_OFFICER:
				isPossible = OfficerMovesGen.isPossible(move, board, free);
				break;
			case Figures.TYPE_CASTLE:
				isPossible = CastleMovesGen.isPossible(move, board, free);
				break;
			case Figures.TYPE_QUEEN:
				isPossible = QueenMovesGen.isPossible(move, board, free);
				break;
			default:
					throw new IllegalStateException();
		}
		
		if (!isPossible) {
			if (Properties.STATISTICS_MODE) {
				statistics.isPossible.stop(1);
			}
			return false;
		}
		
		/**
		 * Detect if opens check
		 */
		fillCheckKeepers(colour);
		long excludedToFieldsBoard = 0L;
		if (checkKeepersBuffer[colour].contains(MoveInt.getFromFieldID(move))) {
			excludedToFieldsBoard |= ~checkKeepersBuffer[colour].getValue(MoveInt.getFromFieldID(move));
		}
		long toBitboard = Fields.ALL_ORDERED_A1H1[MoveInt.getToFieldID(move)];
		if ((toBitboard & excludedToFieldsBoard) != 0L) {
			if (Properties.STATISTICS_MODE) {
				statistics.isPossible.stop(0);
			}
			return false;
		}
		
		if (isInCheck || enpassant) {
			
			makeMoveForward(move);
			isPossible = !isInCheck(colour);
			makeMoveBackward(move);
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.isPossible.stop(isPossible ? 1 : 0);
		}
		
		return isPossible;
		
		/*} else {
			throw new IllegalStateException();
			int count = genKingEscapes(figureID, colour, movesIsPossibleInCheckBuffer);
			if (count == 0) {
				if (Properties.STATISTICS_MODE) {
					statistics.isPossible.stop(1);
				}
				return false;
			}
			
			for (int i=0; i<count; i++) {
				if (Move.isEquals(move, movesIsPossibleInCheckBuffer[i])) {
					if (Properties.STATISTICS_MODE) {
						statistics.isPossible.stop(1);
					}
					return true;
				}
			}
			if (Properties.STATISTICS_MODE) {
				statistics.isPossible.stop(1);
			}
			return false;
		}*/
	}
	
	/*public boolean opensCheck(long[] move) {
		
		int colour = getColourToMove();
		if (Properties.DEBUG_MODE) {
			if (colour != Move.getColour(move)) {
				throw new IllegalStateException();
			}
		}
		
		if (isInCheck(colour)) {
			makeMoveForward(move);
			boolean result = isInCheck(colour);
			makeMoveBackward(move);
			return result;			
		} else {
			return false;
		}
		
	}*/
	
	public final void makeMoveForward(final int move) {
		if (Properties.STATISTICS_MODE) {
			statistics.forwardMove.start();
		}
		
		makeMoveForward(move, true);
		
		/*int moveColour = Move.getColour(move);
		int colourToMove = getColourToMove();
		if (isInCheck(colourToMove)) {
			if (colourToMove == Figures.COLOUR_WHITE) {
				whiteInCheck++;
			} else {
				blackInCheck++;
			}
		}*/
		
		/*if (Move.isKing(move) && !Move.isCastleSide(move) && !Move.isCapture(move)) {
			if (moveColour == Figures.COLOUR_WHITE) {
				whiteKingMoves++;
			} else {
				blackKingMoves++;
			}
		} else if (Move.isQueen(move) && !Move.isCapture(move)) {	
			if (moveColour == Figures.COLOUR_WHITE) {
				whiteQueensMoves++;
			} else {
				blackQueensMoves++;
			}
		}*/
		
		if (Properties.STATISTICS_MODE) {
			statistics.forwardMove.stop(1);
		}
	}
	
	public final void makeMoveForward(final int move, boolean invalidateCheckKeepers) {
		
		if (playedMovesCount + 1 >= GlobalConstants.MAX_MOVES_IN_GAME) {
			throw new IllegalStateException("MORE THAN " + GlobalConstants.MAX_MOVES_IN_GAME + " MOVES: " + this);
		}
		
		if (eval != null) eval.move(move);
		
		//System.out.println("FW " + playedMovesCount + "-" + (playedMovesCount + 1) + " Before " + Move.moveToString(move) + " = " + hashkey);
		
		//System.out.println("Play forward " + Move.moveToString(move) + " hash " + hashkey);
		
		/*if (hashkey == -6661964169401238187L) {
			int g = 0;
		}*/
		
		if (DEBUG) checkConsistency();
		
		boolean inCheck = false;
		
		if (Properties.DEBUG_MODE) {
			int figureID = MoveInt.getFigurePID(move);
			/*if (!stateManager.isAlive(figureID)) {
				throw new IllegalStateException("Move death figure");
			}*/
			
			final int figureColour = MoveInt.getColour(move);
			if (figureColour != getColourToMove()) {
				throw new IllegalStateException("Move the same colour " + figureColour + " more then once.");
			}
			
			inCheck = isInCheck(figureColour);
		}
		
		long expected_key = 0L;
		long expected_pawnkey = 0L;
		if (DEBUG) {
			//if (Properties.DEBUG_LEVEL <= Properties.DEBUG_LEVEL3) {
				expected_key = getHashKeyAfterMove(move);
				expected_pawnkey = getPawnHashKeyAfterMove(move);
			//}
		}
		
		if (moveListeners.length > 0) {
			for (int i=0; i<moveListeners.length; i++) {
				moveListeners[i].preForwardMove(move);
			}
		}
		
		//MAIN LOGIC
		
		int pid = MoveInt.getFigurePID(move);
		int figureColour = MoveInt.getColour(move);
		int figureType = MoveInt.getFigureType(move);
		int dirID = MoveInt.getDir(move);
		int fromFieldID = MoveInt.getFromFieldID(move);
		int toFieldID = MoveInt.getToFieldID(move);
		
		final long fromBoard = Fields.ALL_ORDERED_A1H1[fromFieldID];
		final long toBoard = Fields.ALL_ORDERED_A1H1[toFieldID];
		
		//stateManager.increaseCounter(figureID);
		
		/*if (Castling.castleSideInvolvedFiguresIDsByColour[figureColour][pid]) {
			updateCastlingForward(pid, figureColour, possibleKingCastleSideByColour, possibleQueenCastleSideByColour);
		}*/
		
		//Keep enpassant flag and colour
		BackupInfo curInfo = backupInfo[playedMovesCount];
		curInfo.hashkey = hashkey;
		curInfo.pawnshash = pawnskey;
		curInfo.lastCaptureOrPawnMoveBefore = lastCaptureOrPawnMoveBefore;
		curInfo.lastCaptureFieldID = lastCaptureFieldID;
		
		if (MoveInt.isCapture(move) || MoveInt.isPawn(move)) {
			lastCaptureOrPawnMoveBefore = 0;
		} else {
			lastCaptureOrPawnMoveBefore++;
		}
		
		if (MoveInt.isCapture(move)) {
			lastCaptureFieldID = toFieldID;
		}
			
		
		BackupInfo nextInfo = backupInfo[playedMovesCount + 1];
		
		if (figureType == Figures.TYPE_PAWN //Pawn move
				&& !MoveInt.isCapture(move) //Non capture
				&& Math.abs(fromFieldID - toFieldID) == 16) { //To fields forward
			//Check whether the adjoining files contains opponent pawn
			long adjoiningFiles = Enpassanting.ADJOINING_FILES[figureColour][toFieldID];
			
			int opponentColour = Figures.OPPONENT_COLOUR[figureColour];
			if ((adjoiningFiles & allByColourAndType[opponentColour][Figures.TYPE_PAWN]) != Bits.NUMBER_0) {
				//There is an enpassant capture for the opponent player
				nextInfo.enpassantPawnBitboard = toBoard;
			} else {
				nextInfo.enpassantPawnBitboard = 0L;
			}
		} else {
			nextInfo.enpassantPawnBitboard = 0L;
		}
		
		if (curInfo.enpassantPawnBitboard != nextInfo.enpassantPawnBitboard) {
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
		
		//bitboardsByFigureID[figureID] = toBoard;
		//fieldIDPerFigureID[figureID] = toFieldID;
		
		if (DEBUG) {
			if (pid != Figures.getPidByColourAndType(figureColour, figureType)) {
				throw new IllegalStateException();
			}
		}
		
		pieces.move(pid, fromFieldID, toFieldID);
		
		//int oldFigureID = figureIDPerFieldID[toFieldID];
		board[fromFieldID] = Constants.PID_NONE;
		board[toFieldID] = pid;
		
		/*long typeBitboard = allByColourAndType[figureColour][figureType];
		typeBitboard &= ~fromBoard;
		typeBitboard |= toBoard;
		allByColourAndType[figureColour][figureType] = typeBitboard;*/
		allByColourAndType[figureColour][figureType] &= ~fromBoard;
		allByColourAndType[figureColour][figureType] |= toBoard;
		
		allByColour[figureColour] &= ~fromBoard;
		allByColour[figureColour] |= toBoard;
		
		//hashkey ^= ConstantStructure.MOVES_KEYS[pid][fromFieldID];
		//hashkey ^= ConstantStructure.MOVES_KEYS[pid][toFieldID];
		hashkey ^= ConstantStructure.getMoveHash(pid, fromFieldID, toFieldID);
		
		if (figureType == Figures.TYPE_PAWN || figureType == Figures.TYPE_KING) {
			//pawnskey ^= ConstantStructure.MOVES_KEYS[pid][fromFieldID];
			//pawnskey ^= ConstantStructure.MOVES_KEYS[pid][toFieldID];
			pawnskey ^= ConstantStructure.getMoveHash(pid, fromFieldID, toFieldID);
		}
		
		if (MoveInt.isCapture(move)) { //Capture
			
			//if (true) throw new IllegalStateException(); 
			
			int capturedPID = MoveInt.getCapturedFigurePID(move);
			final int capturedFigureColour = Figures.OPPONENT_COLOUR[figureColour];
			final int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];
			
			if (Properties.DEBUG_MODE) {
				/**
				 * Promoted figure with changed ID because of backward/forward moves play and this 'move' is cached!
				 */
				//!!! This should be fixed now because the indexed number set
				//!!! with promoted figure ids is not queue but stack.
				/*if ((masks & Move.MASK_ENPASSANT) == Bits.NUMBER_0) {
					if (oldFigureID != capturedFigureID) {
						throw new IllegalStateException();
					}
				}*/
			}
			
			//stateManager.kill(capturedFigureID);
			
			long capturedTypeBitboard = allByColourAndType[capturedFigureColour][capturedFigureType];
			if (MoveInt.isEnpassant(move)) {
				
				//if (true) throw new IllegalStateException(); 
				
				int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
				long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[capturedFieldID];
				
				capturedTypeBitboard &= ~opponentPawnBitboard;
				allByColour[capturedFigureColour] &= ~opponentPawnBitboard;
				board[capturedFieldID] = Constants.PID_NONE;
				
				pieces.rem(capturedPID, capturedFieldID);
				
				hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
				
				if (capturedFigureType == Figures.TYPE_PAWN) {
					pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
				}
			} else {
				capturedTypeBitboard &= ~toBoard;
				allByColour[capturedFigureColour] &= ~toBoard;
				
				pieces.rem(capturedPID, toFieldID);
				
				hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
				
				if (capturedFigureType == Figures.TYPE_PAWN) {
					pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
				}
			}
			allByColourAndType[capturedFigureColour][capturedFigureType] = capturedTypeBitboard;
			
			//int capturedFigureFieldID = board[capturedPID];
			
		} else if (MoveInt.isCastling(move)) {
			//System.out.println("CASTLE");
			
			//if (true) throw new IllegalStateException(); 
			
			int castlePID = MoveInt.getCastlingRookPID(move);
			
			int fromCastleFieldID = MoveInt.getCastlingRookFromID(move);
			int toCastleFieldID = MoveInt.getCastlingRookToID(move);
			long fromCastleBoard = Fields.ALL_ORDERED_A1H1[fromCastleFieldID];
			long toCastleBoard = Fields.ALL_ORDERED_A1H1[toCastleFieldID];
			
			//stateManager.increaseCounter(castleID);
			
			//bitboardsByFigureID[castleID] = toCastleBoard;
			//fieldIDPerFigureID[castleID] = toCastleFieldID;
			
			pieces.move(castlePID, fromCastleFieldID, toCastleFieldID);
			
			board[fromCastleFieldID] = Constants.PID_NONE;
			//if (true) throw new IllegalStateException();
			board[toCastleFieldID] = castlePID;
			
			/*typeBitboard = allByColourAndType[figureColour][Figures.TYPE_CASTLE];
			typeBitboard &= ~fromCastleBoard;
			typeBitboard |= toCastleBoard;
			allByColourAndType[figureColour][Figures.TYPE_CASTLE] = typeBitboard;*/
			allByColourAndType[figureColour][Figures.TYPE_CASTLE] &= ~fromCastleBoard;
			allByColourAndType[figureColour][Figures.TYPE_CASTLE] |= toCastleBoard;
			                                                                  
			allByColour[figureColour] &= ~fromCastleBoard;
			allByColour[figureColour] |= toCastleBoard;
			
			hashkey ^= ConstantStructure.MOVES_KEYS[castlePID][fromCastleFieldID];
			hashkey ^= ConstantStructure.MOVES_KEYS[castlePID][toCastleFieldID];
			
			castledByColour[figureColour] = MoveInt.isCastleKingSide(move) ? CastlingType.KING_SIDE : CastlingType.QUEEN_SIDE;
		}
		
		if (MoveInt.isPromotion(move)) {
			
			
			//if (true) throw new IllegalStateException();
			
			pieces.rem(pid, toFieldID);
			hashkey ^= ConstantStructure.MOVES_KEYS[pid][toFieldID];
			pawnskey ^= ConstantStructure.MOVES_KEYS[pid][toFieldID];
			
			int promotedFigurePID = MoveInt.getPromotionFigurePID(move);
			pieces.add(promotedFigurePID, toFieldID);
			hashkey ^= ConstantStructure.MOVES_KEYS[promotedFigurePID][toFieldID];
			
			if (attacksSupport) fastPlayerAttacks.addPiece_Special(promotedFigurePID, toFieldID);
			
			//bitboardsByFigureID[promotedFigureID] = toBoard;
			//fieldIDPerFigureID[promotedFigureID] = toFieldID;
			//fieldIDPerFigureID[pid] = Fields.DUMMY_FIELD_ID;
			
			//board[fromFieldID] = Constants.PID_NONE;
			board[toFieldID] = promotedFigurePID;
			
			allByColourAndType[figureColour][Constants.PIECE_IDENTITY_2_TYPE[promotedFigurePID]] |= toBoard;
			allByColour[figureColour] |= toBoard;
			
			/**
			 * Remove pawn
			 */
			//long typeBitboard = allByColourAndType[figureColour][figureType];
			allByColourAndType[figureColour][figureType] &= ~toBoard;
			//allByColourAndType[figureColour][figureType] = typeBitboard;
		}
		
		/*free |= fromBoard;
		free &= ~toBoard;*/
		free = ~(allByColour[Figures.COLOUR_WHITE] | allByColour[Figures.COLOUR_BLACK]);
		
		//movesCount++;
		
		/*if (Properties.STATISTICS_MODE) {
			statistics.testing.start();
		}*/
		
		//stateManager.postMoveForward(move);
		
		/*if (Properties.STATISTICS_MODE) {
			statistics.testing.stop(1);
		}*/
		
		switchLastMoveColour();
		
		if (DEBUG) {
			//if (Properties.DEBUG_LEVEL <= Properties.DEBUG_LEVEL3) {
				if (hashkey != expected_key) {
					throw new IllegalStateException("Wrong hash key");
				}
				if (pawnskey != expected_pawnkey) {
					throw new IllegalStateException("Wrong pawn hash key");
				}
			//}
		}
		
		playedBoardStates.inc(hashkey);
		//playedPawnStates.inc(pawnskey);
		
		playedMoves[playedMovesCount++] = move;
		
		if (invalidateCheckKeepers) {
			invalidatedCheckKeepers();
		}
		invalidatedInChecks();
		
		if (Properties.DEBUG_MODE) {
			if (Properties.DEBUG_LEVEL == Properties.DEBUG_LEVEL3) {
				//checkConsistency();
			}
		}
		
		if (moveListeners.length > 0) {
			for (int i=0; i<moveListeners.length; i++) {
				moveListeners[i].postForwardMove(move);
			}
		}
		
		if (Properties.DEBUG_MODE) {
			if (Properties.DEBUG_MODE_IN_CHECK_AFTER_KING_ESCAPE && inCheck) {
				if (isInCheck(figureColour)) {
					throw new IllegalStateException("Wrong king escape");
				}
			}
		}
		
		//System.out.println("Play forward " + Move.moveToString(move) + " hash " + hashkey);
		
		//final int capturedFigureType = (int) move[13];
		
		if (DEBUG) checkConsistency();
		
		/*if (Move.isPromotion(move)) {
			System.out.println(this);
		}*/
		//System.out.println("FW " + (playedMovesCount - 1) + "-" + (playedMovesCount) + " After " + Move.moveToString(move) + " = " + hashkey);
		
		//System.out.println("pawnshash=" + pawnskey);
		/*if (getPawnsHashKey() == -7213553216120748179L) {
			int h=0;
		}*/
	}
	
	public final void makeMoveBackward(final int move) {
		if (Properties.STATISTICS_MODE) {
			statistics.backwardMove.start();
		}
		
		if (DEBUG) checkConsistency();
		
		/*int moveColour = Move.getColour(move);
		int oppColour = Figures.OPPONENT_COLOUR[moveColour];
		if (isInCheck(oppColour)) {
			if (oppColour == Figures.COLOUR_WHITE) {
				if (whiteInCheck > 0) {
					whiteInCheck--;
				}
			} else {
				if (blackInCheck > 0) {
					blackInCheck--;
				}
			}
		}
		
		if (Move.isKing(move) && !Move.isCastleSide(move) && !Move.isCapture(move)) {
			if (moveColour == Figures.COLOUR_WHITE) {
				if (whiteKingMoves > 0) {
					whiteKingMoves--;
				}
			} else {
				if (blackKingMoves > 0) {
					blackKingMoves--;
				}
			}
		} else if (Move.isQueen(move) && !Move.isCapture(move)) {
			if (moveColour == Figures.COLOUR_WHITE) {
				if (whiteQueensMoves > 0) {
					whiteQueensMoves--;
				}
			} else {
				if (blackQueensMoves > 0) {
					blackQueensMoves--;
				}
			}
		}*/
		
		makeMoveBackward(move, true);
		
		if (Properties.STATISTICS_MODE) {
			statistics.backwardMove.stop(1);
		}
	}
	
	public final void makeMoveBackward(final int move, boolean invalidateCheckKeepers) {

		
		//System.out.println("BW " + playedMovesCount + "-" + (playedMovesCount - 1) + " Before " + Move.moveToString(move) + " = " + hashkey);
		
		if (eval != null) eval.unmove(move);
		
		if (moveListeners.length > 0) {
			for (int i=0; i<moveListeners.length; i++) {
				moveListeners[i].preBackwardMove(move);
			}
		}
		
		
		final int pid = MoveInt.getFigurePID(move);
		final int figureColour = MoveInt.getColour(move);
		final int figureType = MoveInt.getFigureType(move);
		//final int dirID = (int) move[5];
		final int fromFieldID = MoveInt.getFromFieldID(move);
		final int toFieldID = MoveInt.getToFieldID(move);
		final long fromBoard = Fields.ALL_ORDERED_A1H1[fromFieldID];
		final long toBoard = Fields.ALL_ORDERED_A1H1[toFieldID];

		
		long affectedFields = 0L;
		affectedFields |= fromBoard;
		affectedFields |= toBoard;
		
		
		//stateManager.decreaseCounter(figureID);
		
		if (playedBoardStates.dec(hashkey) <= -1) {
			throw new IllegalStateException("hashkey " + hashkey + " not found");
		}

		//if (playedPawnStates.dec(pawnskey) <= -1) {
		//	throw new IllegalStateException("pawnskey " + pawnskey + " not found");
		//}
		
		
		//Update enpassant flag and colour
		BackupInfo curInfo = backupInfo[playedMovesCount];
		BackupInfo prevInfo = playedMovesCount > 0 ? backupInfo[playedMovesCount - 1] : null;
		if (prevInfo != null && curInfo.enpassantPawnBitboard != prevInfo.enpassantPawnBitboard) {
			//hashkey ^= ConstantStructure.HAS_ENPASSANT;
		}
		curInfo.enpassantPawnBitboard = 0L;
		
		/*if (curInfo.w_kingSideAvailable != prevInfo.w_kingSideAvailable) {
			//hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE];
			//pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE];
		}

		if (curInfo.w_queenSideAvailable != prevInfo.w_queenSideAvailable) {
			//hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE];
			//pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE];
		}

		if (curInfo.b_kingSideAvailable != prevInfo.b_kingSideAvailable) {
			//hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK];
			//pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK];
		}

		if (curInfo.b_queenSideAvailable != prevInfo.b_queenSideAvailable) {
			//hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK];
			//pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK];
		}*/
		
		//bitboardsByFigureID[figureID] = fromBoard;
		//fieldIDPerFigureID[figureID] = fromFieldID;
		
		board[fromFieldID] = pid;
		board[toFieldID] = Constants.PID_NONE;
		
		long typeBitboard = allByColourAndType[figureColour][figureType];
		typeBitboard |= fromBoard;
		typeBitboard &= ~toBoard;
		allByColourAndType[figureColour][figureType] = typeBitboard;
		
		allByColour[figureColour] |= fromBoard;
		allByColour[figureColour] &= ~toBoard;
		
		//long[][] MOVES_KEYS = ConstantStructure.MOVES_KEYS;
		
		//hashkey ^= ConstantStructure.getMoveHash(pid, fromFieldID, toFieldID);
		//hashkey ^= MOVES_KEYS[pid][fromFieldID];
		
		/*if (figureType == Figures.TYPE_PAWN || figureType == Figures.TYPE_KING) {
			//pawnskey ^= MOVES_KEYS[pid][fromFieldID];
			//pawnskey ^= MOVES_KEYS[pid][toFieldID];
		}*/
		
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
			
			allByColourAndType[figureColour][Constants.PIECE_IDENTITY_2_TYPE[promotedFigurePID]] &= ~toBoard;
			allByColour[figureColour] &= ~toBoard;
		}
		
		if (MoveInt.isCapture(move)) { //Capture
			
			//if (true) throw new IllegalStateException();
			
			final int capturedPID = MoveInt.getCapturedFigurePID(move);
			final int capturedFigureColour = Figures.OPPONENT_COLOUR[figureColour];
			final int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];

			long capturedTypeBitboard = allByColourAndType[capturedFigureColour][capturedFigureType];
			if (MoveInt.isEnpassant(move)) {
				
				//if (true) throw new IllegalStateException(); 
				
				int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
				long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[capturedFieldID];
				
				affectedFields |= opponentPawnBitboard;
				
				board[capturedFieldID] = capturedPID;
				
				capturedTypeBitboard |= opponentPawnBitboard;
				allByColour[capturedFigureColour] |= opponentPawnBitboard;
				
				pieces.add(capturedPID, capturedFieldID);
				
			} else {
				board[toFieldID] = capturedPID;
					
				capturedTypeBitboard |= toBoard;
				allByColour[capturedFigureColour] |= toBoard;
				
				pieces.add(capturedPID, toFieldID);
			}
			
			allByColourAndType[capturedFigureColour][capturedFigureType] = capturedTypeBitboard;
			
			//hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
			
			/*if (capturedFigureType == Figures.TYPE_PAWN) {
				//pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
			}*/
			
		} else if (MoveInt.isCastling(move)) {

			int castlePID = MoveInt.getCastlingRookPID(move);
			
			int fromCastleFieldID = MoveInt.getCastlingRookFromID(move);
			int toCastleFieldID = MoveInt.getCastlingRookToID(move);
			long fromCastleBoard = Fields.ALL_ORDERED_A1H1[fromCastleFieldID];
			long toCastleBoard = Fields.ALL_ORDERED_A1H1[toCastleFieldID];
			
			affectedFields |= fromCastleBoard;
			affectedFields |= toCastleBoard;
			
			//stateManager.decreaseCounter(castleID);
			
			//bitboardsByFigureID[castleID] = fromCastleBoard;
			//fieldIDPerFigureID[castleID] = fromCastleFieldID;
			
			pieces.move(castlePID, toCastleFieldID, fromCastleFieldID);
			
			board[fromCastleFieldID] = castlePID;
			board[toCastleFieldID] = Constants.PID_NONE;
			
			typeBitboard = allByColourAndType[figureColour][Figures.TYPE_CASTLE];
			typeBitboard |= fromCastleBoard;
			typeBitboard &= ~toCastleBoard;
			allByColourAndType[figureColour][Figures.TYPE_CASTLE] = typeBitboard;
			
			allByColour[figureColour] |= fromCastleBoard;
			allByColour[figureColour] &= ~toCastleBoard;
			
			//hashkey ^= MOVES_KEYS[castlePID][fromCastleFieldID];
			//hashkey ^= MOVES_KEYS[castlePID][toCastleFieldID];
			
			castledByColour[figureColour] = CastlingType.NONE;
		}
		
		/*if (Castling.castleSideInvolvedFiguresIDsByColour[figureColour][pid]) {
			updateCastlingBackward(pid, figureColour);
		}*/
		
		//free &= ~fromBoard;
		//free |= toBoard;
		free = ~(allByColour[Figures.COLOUR_WHITE] | allByColour[Figures.COLOUR_BLACK]);		
		
		//movesCount--;
		//stateManager.postMoveBackward(move);
		
		switchLastMoveColour();
		
		playedMoves[--playedMovesCount] = 0;
		
		if (invalidateCheckKeepers) {
			invalidatedCheckKeepers();
		}
		invalidatedInChecks();
		
		if (moveListeners.length > 0) {
			for (int i=0; i<moveListeners.length; i++) {
				moveListeners[i].postBackwardMove(move);
			}
		}
		
		if (DEBUG)  checkConsistency();
		
		/*if (prevInfo.hashkey != hashkey) {
			throw new IllegalStateException();
		}
		if (prevInfo.pawnshash != pawnskey) {
			throw new IllegalStateException();
		}*/

		hashkey = prevInfo.hashkey;
		pawnskey = prevInfo.pawnshash;
		lastCaptureOrPawnMoveBefore = prevInfo.lastCaptureOrPawnMoveBefore;
		lastCaptureFieldID = prevInfo.lastCaptureFieldID;
		
		//final int capturedFigureType = (int) move[13];
		//System.out.println("BW " + (playedMovesCount + 1) + "-" + (playedMovesCount) + " After " + Move.moveToString(move) + " = " + hashkey);
	}

	public void makeNullMoveForward() {
		BackupInfo curInfo = backupInfo[playedMovesCount];		
		BackupInfo nextInfo = backupInfo[playedMovesCount + 1];
		
		nextInfo.w_kingSideAvailable = curInfo.w_kingSideAvailable;
		nextInfo.w_queenSideAvailable = curInfo.w_queenSideAvailable;
		nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
		nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
		nextInfo.enpassantPawnBitboard = curInfo.enpassantPawnBitboard;
		
		playedMoves[playedMovesCount++] = 0;
		
		switchLastMoveColour();
	}
	
	public final void makeNullMoveBackward() {

		//BackupInfo curInfo = backupInfo[playedMovesCount];
		//BackupInfo prevInfo = playedMovesCount > 0 ? backupInfo[playedMovesCount - 1] : null;

		playedMoves[--playedMovesCount] = 0;
		
		switchLastMoveColour();
	}
	
	public final long getPawnHashKeyAfterMove(final int move) {
		
		if (true) throw new IllegalStateException("Check whether the pawnskay should be updated with the colour"); 
		
		//System.out.println("FW " + playedMovesCount + "-" + (playedMovesCount + 1) + " Before " + Move.moveToString(move) + " = " + hashkey);
		
		long pawnskey = this.pawnskey;
		
		//long masks = move[0];
		int pid = MoveInt.getFigurePID(move);
		int figureColour = MoveInt.getColour(move);
		int figureType = MoveInt.getFigureType(move);
		//int dirID = (int) move[5];
		int fromFieldID = MoveInt.getFromFieldID(move);
		int toFieldID = MoveInt.getToFieldID(move);
		
		//final long fromBoard = Fields.ALL_ORDERED_A1H1[fromFieldID];
		//final long toBoard = Fields.ALL_ORDERED_A1H1[toFieldID];
		
		//Keep enpassant flag and colour
		BackupInfo curInfo = backupInfo[playedMovesCount];
		BackupInfo nextInfo = backupInfo[playedMovesCount + 1];
		
		/*if (figureType == Figures.TYPE_PAWN //Pawn move
				&& (masks & Move.MASK_CAPTURE) == Bits.NUMBER_0 //Non capture
				&& dirID == Enpassanting.ENPASSANT_DIR_ID) { //To fields forward
			//Check whether the adjoining files contains opponent pawn
			long adjoiningFiles = Enpassanting.ADJOINING_FILES[figureColour][toFieldID];
			
			int opponentColour = Figures.OPPONENT_COLOUR[figureColour];
			if ((adjoiningFiles & allByColourAndType[opponentColour][Figures.TYPE_PAWN]) != Bits.NUMBER_0) {
				//There is an enpassant capture for the opponent player
				nextInfo.enpassantPawnBitboard = toBoard;
			} else {
				nextInfo.enpassantPawnBitboard = 0L;
			}
		} else {
			nextInfo.enpassantPawnBitboard = 0L;
		}
		
		if (curInfo.enpassantPawnBitboard != nextInfo.enpassantPawnBitboard) {
			hashkey ^= ConstantStructure.HAS_ENPASSANT;
		}*/
		
		if (figureColour == Figures.COLOUR_WHITE) {
			
			nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
			nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
			
			if (curInfo.w_kingSideAvailable) {
				switch(pid) {
					case Constants.PID_W_KING:
						pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
						nextInfo.w_kingSideAvailable = false;
						break;
					case Constants.PID_W_ROOK:
						if (fromFieldID == H1_ID) {
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
						pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
						nextInfo.w_queenSideAvailable = false;
						break;
					case Constants.PID_W_ROOK:
						if (fromFieldID == A1_ID) {
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
						pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
						nextInfo.b_kingSideAvailable = false;
						break;
					case Constants.PID_B_ROOK:
						if (fromFieldID == H8_ID) {
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
						pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
						nextInfo.b_queenSideAvailable = false;
						break;
					case Constants.PID_B_ROOK:
						if (fromFieldID == A8_ID) {
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
		
		long[][] MOVES_KEYS = ConstantStructure.MOVES_KEYS;

		if (figureType == Figures.TYPE_PAWN || figureType == Figures.TYPE_KING) {
			pawnskey ^= MOVES_KEYS[pid][fromFieldID];
			pawnskey ^= MOVES_KEYS[pid][toFieldID];
		}
		
		if (MoveInt.isCapture(move)) { //Capture
			if (true) throw new IllegalStateException("Fix when is enpass, see the makeForwardMove method where it is fixed.");
			int capturedPID = MoveInt.getCapturedFigurePID(move);
			final int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];
			if (capturedFigureType == Figures.TYPE_PAWN) {
				pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
			}
		} 
		
		if (MoveInt.isPromotion(move)) {
			pawnskey ^= MOVES_KEYS[pid][toFieldID];
			
		}
		
		pawnskey ^= ConstantStructure.WHITE_TO_MOVE;

		return pawnskey;
	}
	
	public final long getHashKeyAfterMove(final int move) {
		
		if (true) throw new IllegalStateException("Check whether the pawnskay should be updated with the colour");
		
		long hashkey = this.hashkey;
		
		int pid = MoveInt.getFigurePID(move);
		int figureColour = MoveInt.getColour(move);
		int figureType = MoveInt.getFigureType(move);
		int dirID = MoveInt.getDir(move);
		int fromFieldID = MoveInt.getFromFieldID(move);
		int toFieldID = MoveInt.getToFieldID(move);
		
		//final long fromBoard = Fields.ALL_ORDERED_A1H1[fromFieldID];
		final long toBoard = Fields.ALL_ORDERED_A1H1[toFieldID];
		
		//stateManager.increaseCounter(figureID);
		
		/*if (Castling.castleSideInvolvedFiguresIDsByColour[figureColour][pid]) {
			updateCastlingForward(pid, figureColour, possibleKingCastleSideByColour, possibleQueenCastleSideByColour);
		}*/
		
		//Keep enpassant flag and colour
		BackupInfo curInfo = backupInfo[playedMovesCount];
		BackupInfo nextInfo = backupInfo[playedMovesCount + 1];
		
		if (figureType == Figures.TYPE_PAWN //Pawn move
				&& !MoveInt.isCapture(move) //Non capture
				&& dirID == Enpassanting.ENPASSANT_DIR_ID) { //To fields forward
			//Check whether the adjoining files contains opponent pawn
			long adjoiningFiles = Enpassanting.ADJOINING_FILES[figureColour][toFieldID];
			
			int opponentColour = Figures.OPPONENT_COLOUR[figureColour];
			if ((adjoiningFiles & allByColourAndType[opponentColour][Figures.TYPE_PAWN]) != Bits.NUMBER_0) {
				//There is an enpassant capture for the opponent player
				nextInfo.enpassantPawnBitboard = toBoard;
			} else {
				nextInfo.enpassantPawnBitboard = 0L;
			}
		} else {
			nextInfo.enpassantPawnBitboard = 0L;
		}
		
		if (curInfo.enpassantPawnBitboard != nextInfo.enpassantPawnBitboard) {
			hashkey ^= ConstantStructure.HAS_ENPASSANT;
		}
		
		if (figureColour == Figures.COLOUR_WHITE) {
			
			nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
			nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
			
			if (curInfo.w_kingSideAvailable) {
				switch(pid) {
					case Constants.PID_W_KING:
						hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
						nextInfo.w_kingSideAvailable = false;
						break;
					case Constants.PID_W_ROOK:
						if (fromFieldID == H1_ID) {
							hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
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
						nextInfo.w_queenSideAvailable = false;
						break;
					case Constants.PID_W_ROOK:
						if (fromFieldID == A1_ID) {
							hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
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
						nextInfo.b_kingSideAvailable = false;
						break;
					case Constants.PID_B_ROOK:
						if (fromFieldID == H8_ID) {
							hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
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
						nextInfo.b_queenSideAvailable = false;
						break;
					case Constants.PID_B_ROOK:
						if (fromFieldID == A8_ID) {
							hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
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
		
		long[][] MOVES_KEYS = ConstantStructure.MOVES_KEYS;
		
		
		hashkey ^= MOVES_KEYS[pid][fromFieldID];
		hashkey ^= MOVES_KEYS[pid][toFieldID];

		if (MoveInt.isCapture(move)) { //Capture
			int capturedPID = MoveInt.getCapturedFigurePID(move);
			hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
		} else if (MoveInt.isCastling(move)) {

			int castlePID = figureColour == Figures.COLOUR_WHITE ? Constants.PID_W_ROOK : Constants.PID_B_ROOK;
			
			int fromCastleFieldID = MoveInt.isCastleKingSide(move) ? Castling.getRookFromFieldID_king(figureColour) : Castling.getRookFromFieldID_queen(figureColour);
			int toCastleFieldID = MoveInt.isCastleKingSide(move) ? Castling.getRookToFieldID_king(figureColour) : Castling.getRookToFieldID_queen(figureColour);
			
			hashkey ^= MOVES_KEYS[castlePID][fromCastleFieldID];
			hashkey ^= MOVES_KEYS[castlePID][toCastleFieldID];
		}
		
		if (MoveInt.isPromotion(move)) {
			hashkey ^= MOVES_KEYS[pid][toFieldID];
			int promotedFigurePID = MoveInt.getPromotionFigurePID(move);
			hashkey ^= MOVES_KEYS[promotedFigurePID][toFieldID];
		}
		
		
		hashkey ^= ConstantStructure.WHITE_TO_MOVE;

		
		return hashkey;
	}
	
	
	public final void switchLastMoveColour() {
		lastMoveColour = Figures.OPPONENT_COLOUR[lastMoveColour];
		hashkey ^= ConstantStructure.WHITE_TO_MOVE;
		pawnskey ^= ConstantStructure.WHITE_TO_MOVE;
	}
	
	/*private static long[] pointsByFigureType = new long[10];
	static {
		pointsByFigureType[Figures.TYPE_KING] = 1;
		pointsByFigureType[Figures.TYPE_QUEEN] = 4;
		pointsByFigureType[Figures.TYPE_CASTLE] = 8;
		pointsByFigureType[Figures.TYPE_OFFICER] = 16;
		pointsByFigureType[Figures.TYPE_KNIGHT] = 16;
		pointsByFigureType[Figures.TYPE_PAWN] = 100;
	}
	
	public long getControlPoints(int colour, long[] pointsByFigureType, int fieldID, long fieldBitboard) {		
		return Controlling.getFieldPoints(this, colour,
					Figures.OPPONENT_COLOUR[colour],
					fieldBitboard, fieldID, free, pointsByFigureType);
	}*/

	public IPlayerAttacks getPlayerAttacks(int colour) {
		
		if (Properties.DEBUG_MODE) {
			if (!attacksSupport) {
				throw new IllegalStateException();
			}
		}
		
		/*if (colour == Figures.COLOUR_WHITE) {
			return whiteAttacks;
		} else {
			return blackAttacks;
		}*/
		
		//return getPlayerAttacks_Internal(colour);
		return getPlayerAttacks_fast(colour);
	}

	public IPlayerAttacks getPlayerAttacks_fast(int colour) {
		
		if (Properties.DEBUG_MODE) {
			if (!attacksSupport) {
				throw new IllegalStateException();
			}
		}
		
		if (colour == Figures.COLOUR_WHITE) {
			return ((FastPlayersAttacks)fastPlayerAttacks).getWhiteAttacks();
		} else {
			return ((FastPlayersAttacks)fastPlayerAttacks).getBlackAttacks();
		}
	}

	public boolean getAttacksSupport() {
		return attacksSupport;
	}

	public boolean getFieldsStateSupport() {
		return fieldsStateSupport;
	}
	
	public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport) {
		
		if (this.attacksSupport && !attacksSupport) {
			throw new IllegalStateException();
		}
		
		if (fieldsStateSupport && !attacksSupport) { 
			throw new IllegalStateException();
		}
			
		this.attacksSupport = attacksSupport;
		this.fieldsStateSupport = fieldsStateSupport;
		
		if (attacksSupport && fastPlayerAttacks == null) {
			initAttacksSupport();
		}
	}

	public IFieldsAttacks getFieldsAttacks() {
		return fieldAttacksCollector;
	}

	public int getGamePhase() {
		
		//if (true) throw new IllegalStateException(); 
		
		int matFactor = materialFactor.getWhiteFactor() + materialFactor.getBlackFactor();
					//+ pieces.getFiguresStateListener().getBlackFactor();
			
		if (matFactor == 0) {
			return GamePhase.ENDGAME_PAWNS;
		} else if (matFactor <= 18) {
			return GamePhase.ENDGAME_LATE;
		} else if (matFactor <= 32) {
			return GamePhase.ENDGAME;
		} else if (matFactor <= 53) {
			return GamePhase.MIDGAME;
		} else {
			return GamePhase.OPENNING;
		}
	}

	public SEE getSee() {
		return see;
	}
	
	
	/**
	 * MOVEGEN
	 */
	
	protected final boolean kingSidePossible(int colour, int opponentColour) {
		/*if (true){//colour == Figures.COLOUR_BLACK) {
			return false;
		}*/
		long kingSideMask = Castling.MASK_KING_CASTLE_SIDE_BY_COLOUR[colour];
		return (colour == Figures.COLOUR_WHITE ? backupInfo[playedMovesCount].w_kingSideAvailable : backupInfo[playedMovesCount].b_kingSideAvailable)
		       && (kingSideMask & ~free) == Bits.NUMBER_0
		       && (allByColourAndType[colour][Figures.TYPE_CASTLE] & (colour == Figures.COLOUR_WHITE ? Fields.H1 : Fields.H8)) != 0
		       && checkCheckingAtKingSideFields(colour, opponentColour);
	}

	protected final boolean queenSidePossible(int colour, int opponentColour) {
		/*if (true){//colour == Figures.COLOUR_BLACK) {
			return false;
		}*/
		
		long queenSideMask = Castling.MASK_QUEEN_CASTLE_SIDE_BY_COLOUR[colour];
		return (colour == Figures.COLOUR_WHITE ? backupInfo[playedMovesCount].w_queenSideAvailable : backupInfo[playedMovesCount].b_queenSideAvailable)
		       && (queenSideMask & ~free) == Bits.NUMBER_0
		       && (allByColourAndType[colour][Figures.TYPE_CASTLE] & (colour == Figures.COLOUR_WHITE ? Fields.A1 : Fields.A8)) != 0
		       && checkCheckingAtQueenSideFields(colour, opponentColour);
	}
	
	private final boolean checkCheckingAtKingSideFields(int colour, int opponentColour) {
		boolean result = true;
		int[] fieldsIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[colour];
		long[] fieldsBitboards = Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[colour];
		for (int i=0; i<fieldsIDs.length; i++) {
			int fieldID = fieldsIDs[i];
			long fieldBitboard = fieldsBitboards[i];
			if (Checking.isFieldAttacked(this, opponentColour, colour, fieldBitboard, fieldID, free, true)) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	private final boolean checkCheckingAtQueenSideFields(int colour, int opponentColour) {
		boolean result = true;
		int[] fieldsIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[colour];
		long[] fieldsBitboards = Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[colour];
		for (int i=0; i<fieldsIDs.length; i++) {
			int fieldID = fieldsIDs[i];
			long fieldBitboard = fieldsBitboards[i];
			if (Checking.isFieldAttacked(this, opponentColour, colour, fieldBitboard, fieldID, free, true)) {
				result = false;
				break;
			}
		}
		return result;
	}

	public final boolean isInCheck() {
		return isInCheck(getColourToMove());
	}
	
	public final boolean isInCheck(int colour) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.isInCheck.start();
		}
		
		if (inCheckCacheInitialized[colour]) {
			if (Properties.STATISTICS_MODE) {
				statistics.isInCheck.stop(inCheckCache[colour] ? 1 : 0);
			}
			return inCheckCache[colour];
		}
		
		boolean inCheck = false;
		if (attacksSupport) {
			inCheck = isInCheckByAttacks(colour);
		} else {
			inCheck = isInCheckInternal(colour, free);
		}
		
		inCheckCache[colour] = inCheck;
		inCheckCacheInitialized[colour] = true;
		
		if (Properties.STATISTICS_MODE) {
			statistics.isInCheck.stop(inCheck ? 1 : 0);
		}
		return inCheck;
	}

	private final boolean isInCheckByAttacks(int colour) {
		int kingFieldID = getKingFieldID(colour);
		long kingBitboard = Fields.ALL_ORDERED_A1H1[kingFieldID];

		
		IPlayerAttacks attacks = getPlayerAttacks(Figures.OPPONENT_COLOUR[colour]);
		long all = attacks.allAttacks();
		if ((all & kingBitboard) != 0L) {
			return true;
		}
		
		return false;
	}
	
	protected final boolean isInCheckInternal(int colour, long _free) {
		int kingFieldID = getKingFieldID(colour);
		long kingBitboard = Fields.ALL_ORDERED_A1H1[kingFieldID];
		
		int lastMove = getLastMove();
		boolean kingAttackPossible = lastMove == 0 ?
				false : MoveInt.isCastling(lastMove);
		
		boolean inCheck = Checking.isInCheck(this, colour,
				Figures.OPPONENT_COLOUR[colour],
				kingBitboard, kingFieldID, _free, kingAttackPossible);
		return inCheck;
	}
	
	public final boolean isCheckMove(int move) {
		if (Properties.STATISTICS_MODE) {
			statistics.isCheckMove.start();
		}
		/*if (move[30] == hashkey //board is equal
			&& move[31] == move[29] //Move is with the same figure, dir and seq
			) {
			//return (move[0] & Move.MASK_CHECK) != NUMBER_0;
		} else {
			move[30] = hashkey;
			move[31] = move[29];
		}*/
		
		int colour = MoveInt.getColour(move);
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		int opponentKingFieldID = getKingFieldID(opponentColour);
		long opponentKingBitboard = Fields.ALL_ORDERED_A1H1[opponentKingFieldID];
		
		boolean isCheck = Checking.isCheckMove(this, move, colour, opponentColour, free, opponentKingBitboard, opponentKingFieldID);
		
		if (Properties.STATISTICS_MODE) {
			statistics.isCheckMove.stop(isCheck ? 1 : 0);
		}
		
		return isCheck;
		/*if (Checking.isDirectCheckMove(move, colour, opponentColour, free, opponentKingBitboard, opponentKingFieldID)) {
			move[0] |= Move.MASK_CHECK;
			move[0] |= Move.MASK_CHECK_DIRECT;
			move[0] &= ~Move.MASK_CHECK_HIDDEN;
			return true;
		} else if (Checking.isHiddenCheckMove(this, move, colour, opponentColour, free, opponentKingBitboard, opponentKingFieldID)) {
			move[0] |= Move.MASK_CHECK;
			move[0] &= ~Move.MASK_CHECK_DIRECT;
			move[0] |= Move.MASK_CHECK_HIDDEN;
			return true;
		} else {
			move[0] &= ~Move.MASK_CHECK;
			move[0] &= ~Move.MASK_CHECK_DIRECT;
			move[0] &= ~Move.MASK_CHECK_HIDDEN;
			return false;
		}*/
		/*if (Checking.isDirectCheckMove(move, colour, opponentColour, free, opponentKingBitboard, opponentKingFieldID)) {
			if (Checking.isHiddenCheckMove(this, move, colour, opponentColour, free, opponentKingBitboard, opponentKingFieldID)) {
				move[0] |= Move.MASK_CHECK;
				move[0] |= Move.MASK_CHECK_DIRECT;
				move[0] |= Move.MASK_CHECK_HIDDEN;
			} else {
				move[0] |= Move.MASK_CHECK;
				move[0] |= Move.MASK_CHECK_DIRECT;
				move[0] &= ~Move.MASK_CHECK_HIDDEN;
			}
			return true;
		} else {
			if (Checking.isHiddenCheckMove(this, move, colour, opponentColour, free, opponentKingBitboard, opponentKingFieldID)) {
				move[0] |= Move.MASK_CHECK;
				move[0] &= ~Move.MASK_CHECK_DIRECT;
				move[0] |= Move.MASK_CHECK_HIDDEN;
				return true;
			} else {
				move[0] &= ~Move.MASK_CHECK;
				move[0] &= ~Move.MASK_CHECK_DIRECT;
				move[0] &= ~Move.MASK_CHECK_HIDDEN;
				return false;
			}
		}*/
	}

	protected final int getKingFieldID(int colour) {
		int kingFieldID = pieces.getPieces(colour == Figures.COLOUR_WHITE ? Constants.PID_W_KING : Constants.PID_B_KING).getData()[0];
		return kingFieldID;
	}

	protected final PiecesList getKingIndexSet(int colour) {
		return pieces.getPieces(colour == Figures.COLOUR_WHITE ? Constants.PID_W_KING : Constants.PID_B_KING);
	}

	
	public final boolean isDirectCheckMove(int move) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.isDirectCheckMove.start();
		}
		
		/*if (move[30] == hashkey //board is equal
			&& move[31] == move[29] //Move is with the same figure, dir and seq
			) {
			//return (move[0] & Move.MASK_CHECK_DIRECT) != NUMBER_0;
		}*/
		
		int colour = MoveInt.getColour(move);
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		int opponentKingFieldID = getKingFieldID(opponentColour);
		long opponentKingBitboard = Fields.ALL_ORDERED_A1H1[opponentKingFieldID];

		boolean isCheck = Checking.isDirectCheckMove(move, colour, free, opponentKingBitboard, opponentKingFieldID);
		
		/*if (isCheck) {
			move[0] |= Move.MASK_CHECK;
			move[0] |= Move.MASK_CHECK_DIRECT;
			move[0] &= ~Move.MASK_CHECK_HIDDEN;
		} else {
			//move[0] &= ~Move.MASK_CHECK;
			move[0] &= ~Move.MASK_CHECK_DIRECT;
			//move[0] &= ~Move.MASK_CHECK_HIDDEN;
		}*/
		
		if (Properties.STATISTICS_MODE) {
			statistics.isDirectCheckMove.stop(isCheck ? 1 : 0);
		}

		
		return isCheck;
	}
	
	public final int getChecksCount(int colour) {
		
		throw new IllegalStateException();
	}
	
	private final void fillCheckKeepers_FromOfficerOrQueen(int colour,// int opponentColour,
			int myKingFieldID,
			long myPieces, long opponentPieces,
			final long myOfficersAttacksFromMyKing, final long opponentOfficersBoard,
			//final int checkFigureType //officer or queen
			final int checkingPID
			) {
		
		if ((myOfficersAttacksFromMyKing & opponentOfficersBoard) != NUMBER_0) {
			long dir = 0;
			long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[myKingFieldID];
			long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[myKingFieldID];
			long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[myKingFieldID];
			long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[myKingFieldID];
			
			boolean hit = false;
			
			if ((opponentOfficersBoard & dir0) != NUMBER_0) {
				hit = true;
				dir |= dir0;
			}
			
			if ((opponentOfficersBoard & dir1) != NUMBER_0) {
				hit = true;
				dir |= dir1;
			}
			
			if ((opponentOfficersBoard & dir2) != NUMBER_0) {
				hit = true;
				dir |= dir2;
			} 
			
			if ((opponentOfficersBoard & dir3) != NUMBER_0) {
				hit = true;
				dir |= dir3;
			}
			
			if (!hit) {
				throw new IllegalStateException();
			}
			
			boolean hit1 = false;
			IndexNumberMap buffer = checkKeepersBuffer[colour];
			PiecesList opponentOfficersIDs = pieces.getPieces(checkingPID);
			int size = opponentOfficersIDs.getDataSize();
			int[] ids = opponentOfficersIDs.getData();
			for (int i=0; i<size; i++) {
				int opponentOfficerFieldID = ids[i];
				long opponentOfficerBitboard = Fields.ALL_ORDERED_A1H1[opponentOfficerFieldID];
				if ((opponentOfficerBitboard & dir) != NUMBER_0) {
					hit1 = true;
					//int officerFieldID = fieldIDPerFigureID[opponentOfficerID];
					long path = OfficerPlies.PATHS[opponentOfficerFieldID][myKingFieldID];
					if ((path & opponentPieces) == NUMBER_0) {
						long myAndPath = path & myPieces;
					
						if (myAndPath != NUMBER_0 && Utils.has1BitSet(myAndPath)) {
							/**
							 * Get the field ID of this one bit and after that the figure ID on that field ID.
							 */
							int fieldID = get67IDByBitboard(myAndPath);
							//int figureID = figureIDPerFieldID[fieldID];
							//HACK: was figureID
							buffer.add(fieldID, path | opponentOfficerBitboard);
						}
					}
				}
			}
			
			if (!hit1) {
				throw new IllegalStateException();
			}
		}
	}
	
	private final void fillCheckKeepers_FromCastleOrQueen(int colour,// int opponentColour,
			int myKingFieldID,
			long myPieces, long opponentPieces,
			final long myCastlesAttacksFromMyKing, final long opponentCastlesBoard,
			//final int checkFigureType //officer or queen
			final int checkingPID
			) {
		
		if ((myCastlesAttacksFromMyKing & opponentCastlesBoard) != NUMBER_0) {
			long dir = 0;
			long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[myKingFieldID];
			long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[myKingFieldID];
			long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[myKingFieldID];
			long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[myKingFieldID];
			
			boolean hit = false;
			boolean enpassOpenDir = false;
			
			if ((opponentCastlesBoard & dir0) != NUMBER_0) {
				hit = true;
				dir |= dir0;
			}
			
			if ((opponentCastlesBoard & dir1) != NUMBER_0) {
				hit = true;
				enpassOpenDir = true;
				dir |= dir1;
			}
			
			if ((opponentCastlesBoard & dir2) != NUMBER_0) {
				hit = true;
				dir |= dir2;
			} 
			
			if ((opponentCastlesBoard & dir3) != NUMBER_0) {
				hit = true;
				enpassOpenDir = true;
				dir |= dir3;
			}
			
			if (!hit) {
				throw new IllegalStateException();
			}
			
			boolean hit1 = false;
			IndexNumberMap buffer = checkKeepersBuffer[colour];
			PiecesList opponentCastlesIDs = pieces.getPieces(checkingPID);
			int size = opponentCastlesIDs.getDataSize();
			int[] ids = opponentCastlesIDs.getData();
			for (int i=0; i<size; i++) {
				int castleFieldID = ids[i];
				long opponentCastleBitboard = Fields.ALL_ORDERED_A1H1[castleFieldID];
				if ((opponentCastleBitboard & dir) != NUMBER_0) {
					hit1 = true;
					//int castleFieldID = fieldIDPerFigureID[opponentCastleID];
					long path = CastlePlies.PATHS[castleFieldID][myKingFieldID];
					if ((path & opponentPieces) == NUMBER_0) {
						long myAndPath = path & myPieces;
					
						if (myAndPath != NUMBER_0 && Utils.has1BitSet(myAndPath)) {
							/**
							 * Get the field ID of this one bit and after that the figure ID on that field ID.
							 */
							int fieldID = get67IDByBitboard(myAndPath);
							//int figureID = figureIDPerFieldID[fieldID];
							buffer.add(fieldID, path | opponentCastleBitboard);
						}
					} /*TODO: should check this only if the pawn figureID is the enpass capturing pawn
					else if (enpassOpenDir && hasEnpassant && colour == enpassantColour) {
						if ((path & (opponentPieces & ~enpassantPawnBitboard)) == NUMBER_0) {
							long myAndPath = path & myPieces;
						
							if (myAndPath != NUMBER_0 && Utils.has1BitSet(myAndPath)) {
								int fieldID = get67IDByBitboard(myAndPath);
								int figureID = figureIDPerFieldID[fieldID];
								buffer.add(figureID, path | opponentCastleBitboard);
							}
						}
					}*/
				}
			}
			
			if (!hit1) {
				throw new IllegalStateException("enpassOpenDir=" + enpassOpenDir
						+ ", enpasInfo[playedMovesCount]" + Move.toBinaryString(backupInfo[playedMovesCount].enpassantPawnBitboard)
						
						+ ", bitboard=" + this);
			}
		}
	}
	
	protected final void invalidatedCheckKeepers() {
		checkKeepersInitialized[Figures.COLOUR_WHITE] = false;
		checkKeepersInitialized[Figures.COLOUR_BLACK] = false;
		checkKeepersBuffer[Figures.COLOUR_WHITE].clear();
		checkKeepersBuffer[Figures.COLOUR_BLACK].clear();
		
		inCheckCacheInitialized[Figures.COLOUR_WHITE] = false;
		inCheckCacheInitialized[Figures.COLOUR_BLACK] = false;
	}
	
	protected final void invalidatedInChecks() {
		inCheckCacheInitialized[Figures.COLOUR_WHITE] = false;
		inCheckCacheInitialized[Figures.COLOUR_BLACK] = false;
	}
	
	protected final void fillCheckKeepers(int colour) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.fillCheckKeepers.start();
		}
		
		if (checkKeepersInitialized[colour]) {
			if (Properties.STATISTICS_MODE) {
				statistics.fillCheckKeepers.stop(0);
			}

			return;
		}
		
		int myKingFieldID = getKingFieldID(colour);
		//long kingBitboard = Fields.ALL_ORDERED_A1H1[myKingFieldID];
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		final long myOfficersAttacksFromMyKing = OfficerPlies.ALL_OFFICER_MOVES[myKingFieldID];
		fillCheckKeepers_FromOfficerOrQueen(colour,
				myKingFieldID, allByColour[colour], allByColour[opponentColour],
				myOfficersAttacksFromMyKing, allByColourAndType[opponentColour][Figures.TYPE_OFFICER],
				Figures.getPidByColourAndType(opponentColour, Figures.TYPE_OFFICER));
		fillCheckKeepers_FromOfficerOrQueen(colour,
				myKingFieldID, allByColour[colour], allByColour[opponentColour],
				myOfficersAttacksFromMyKing, allByColourAndType[opponentColour][Figures.TYPE_QUEEN],
				Figures.getPidByColourAndType(opponentColour, Figures.TYPE_QUEEN));
		
		final long myCastlesAttacksFromMyKing = CastlePlies.ALL_CASTLE_MOVES[myKingFieldID];
		fillCheckKeepers_FromCastleOrQueen(colour,
				myKingFieldID, allByColour[colour], allByColour[opponentColour],
				myCastlesAttacksFromMyKing, allByColourAndType[opponentColour][Figures.TYPE_CASTLE],
				Figures.getPidByColourAndType(opponentColour, Figures.TYPE_CASTLE));
		fillCheckKeepers_FromCastleOrQueen(colour,
				myKingFieldID, allByColour[colour], allByColour[opponentColour],
				myCastlesAttacksFromMyKing, allByColourAndType[opponentColour][Figures.TYPE_QUEEN],
				Figures.getPidByColourAndType(opponentColour, Figures.TYPE_QUEEN));

		checkKeepersInitialized[colour] = true;
		
		if (Properties.STATISTICS_MODE) {
			statistics.fillCheckKeepers.stop(1);
		}
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
		if (Properties.STATISTICS_MODE) {
			statistics.allMoves.start();
		}
		
		int count = 0;
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no need to generate moves.
		 */
		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(0);
			}
			return 0;
		}
		
		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		count += genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour,
				Figures.TYPE_PAWN, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(count);
			}
			return count;
		}
		
		count += genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour,
				Figures.TYPE_KNIGHT, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(count);
			}
			return count;
		}
		
		count += genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour,
				Figures.TYPE_OFFICER, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(count);
			}
			return count;
		}
		
		count += genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour,
				Figures.TYPE_CASTLE, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(count);
			}
			return count;
		}
		
		count += genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour,
				Figures.TYPE_QUEEN, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(count);
			}
			return count;
		}
		
		count += genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour,
				Figures.TYPE_KING, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(count);
			}
			return count;
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.allMoves.stop(count);
		}
		
		return count;
	}

	public final int genMinorMoves(final IInternalMoveList list) {
		return genMinorMoves(0L, true, getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	protected final int genMinorMoves(long excludedToFieldsBoard, boolean checkKeepersAware, int colour, final IInternalMoveList list, final int maxCount) {
		if (Properties.STATISTICS_MODE) {
			statistics.allMoves.start();
		}
		
		int count = 0;
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no need to generate moves.
		 */
		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(0);
			}
			return 0;
		}
		
		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		count += genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour,
				Figures.TYPE_KNIGHT, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(count);
			}
			return count;
		}
		
		count += genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour,
				Figures.TYPE_OFFICER, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.allMoves.stop(count);
			}
			return count;
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.allMoves.stop(count);
		}
		
		return count;
	}
	
	private final int genAllMoves_FiguresWithSameType(long excludedToFieldsBoard_init,
			final boolean interuptAtFirstExclusionHit,
			boolean checkKeepersAware,
			final int colour, final int opponentColour, final int type,
			final IInternalMoveList list, final int maxCount) {
		
		int count = 0;
		
		int pid = Figures.getPidByColourAndType(colour, type);
		final PiecesList fields = pieces.getPieces(pid);
		
		//final PiecesList all_from_type = all[type];
		final int size = fields.getDataSize();
		final int[] data = fields.getData();
		
		for (int i=0; i<size; i++) {
			
			int fieldID = data[i];
			
			count += genAllMoves_ByFigureID(fieldID, pid, excludedToFieldsBoard_init,
					interuptAtFirstExclusionHit, checkKeepersAware,
					colour, opponentColour, type, list, maxCount);
			if (count >= maxCount) {
				return count;
			}
		}
		
		return count;
	}
	
	public final int genAllMoves_ByFigureID(int fieldID, long excludedToFields,
			final IInternalMoveList list) {
	
		int pid = board[fieldID];
		
		if (pid == Constants.PID_NONE) {
			throw new IllegalStateException();
		}
		
		int colour = Constants.getColourByPieceIdentity(pid);
		int type = Constants.PIECE_IDENTITY_2_TYPE[pid];
		
		fillCheckKeepers(colour);
	
		return genAllMoves_ByFigureID(fieldID, pid, excludedToFields,
				true, true, 
				colour, Figures.OPPONENT_COLOUR[colour], type,
				list, 100);
				
	}
	
	private final int genAllMoves_ByFigureID(int fieldID, int pid, long excludedToFieldsBoard_init,
			boolean interuptAtFirstExclusionHit,
			boolean checkKeepersAware,
			final int colour, final int opponentColour, final int type,
			final IInternalMoveList list, final int maxCount) {
		
		/**
		 * Leads to illegal moves at least in check - king escapes.
		 * Try to hardcode it to true and see what are the problems
		 */
		//interuptAtFirstExclusionHit = true;
		
		int count = 0;
			
		long excludedToFieldsBoard = excludedToFieldsBoard_init;
		if (checkKeepersAware) {
			if (checkKeepersBuffer[colour].contains(fieldID)) {
				excludedToFieldsBoard |= ~checkKeepersBuffer[colour].getValue(fieldID);
			}
		}
		
		long fieldBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
		//int fieldID = fieldIDPerFigureID[figureID];
		
		switch(type) {
			case Figures.TYPE_KNIGHT:
				long allMoves = KnightPlies.ALL_KNIGHT_MOVES[fieldID];
				if ((allMoves & ~excludedToFieldsBoard) != NUMBER_0) {
					count += KnightMovesGen.genAllMoves(excludedToFieldsBoard, pid, 
							
							fieldID,
							allByColour[colour],
							allByColour[opponentColour],
							board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
				}
				break;
			case Figures.TYPE_KING:
				allMoves = KingPlies.ALL_KING_MOVES[fieldID];
				if ((allMoves & ~excludedToFieldsBoard) != NUMBER_0) {
					int opKingID = getKingFieldID(opponentColour);
					count += KingMovesGen.genAllMoves(checkKeepersAware, this, excludedToFieldsBoard, pid, colour, opponentColour,
							fieldBitboard,
							fieldID,
							free,
							allByColour[colour],
							allByColour[opponentColour],
							board,
							kingSidePossible(colour, opponentColour),
							queenSidePossible(colour, opponentColour),
							Fields.ALL_ORDERED_A1H1[opKingID],
							opKingID, list, maxCount);
					if (count >= maxCount) {
						return count;
					}
				}
				break;
			case Figures.TYPE_PAWN:
				BackupInfo curEnpassInfo = backupInfo[playedMovesCount];
				if (colour == Figures.COLOUR_WHITE) {
					allMoves = WhitePawnPlies.ALL_WHITE_PAWN_MOVES[fieldID];
					if ((allMoves & ~excludedToFieldsBoard) != NUMBER_0
							|| curEnpassInfo.enpassantPawnBitboard != 0L) { //HACK && colour == curEnpassInfo.enpassantColour)) {
						count += WhitePawnMovesGen.genAllMoves(this, excludedToFieldsBoard, interuptAtFirstExclusionHit, pid, 
								fieldID,
								free,
								allByColour[opponentColour],
								board,
								curEnpassInfo.enpassantPawnBitboard != 0L,
								curEnpassInfo.enpassantPawnBitboard,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
				} else {
					allMoves = BlackPawnPlies.ALL_BLACK_PAWN_MOVES[fieldID];
					if ((allMoves & ~excludedToFieldsBoard) != NUMBER_0
							|| curEnpassInfo.enpassantPawnBitboard != 0 ) { //&& colour == curEnpassInfo.enpassantColour)) {
						count += BlackPawnMovesGen.genAllMoves(this, excludedToFieldsBoard, interuptAtFirstExclusionHit, pid,
								fieldID,
								free,
								//allByColour[opponentColour],
								board,
								curEnpassInfo.enpassantPawnBitboard != 0,
								curEnpassInfo.enpassantPawnBitboard,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
				}
				break;
			case Figures.TYPE_OFFICER:
				allMoves = OfficerPlies.ALL_OFFICER_MOVES[fieldID];
				if ((allMoves & ~excludedToFieldsBoard) != NUMBER_0) {
					count += OfficerMovesGen.genAllMoves(excludedToFieldsBoard, interuptAtFirstExclusionHit, pid,
							fieldID,
							free,
							allByColour[opponentColour],
							board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
				}
				break;
			case Figures.TYPE_CASTLE:
				allMoves = CastlePlies.ALL_CASTLE_MOVES[fieldID];
				if ((allMoves & ~excludedToFieldsBoard) != NUMBER_0) {
					count += CastleMovesGen.genAllMoves(excludedToFieldsBoard, interuptAtFirstExclusionHit, pid,
							fieldID,
							free,
							allByColour[opponentColour],
							board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
				}
				break;
			case Figures.TYPE_QUEEN:
				if ((CastlePlies.ALL_CASTLE_MOVES[fieldID] & ~excludedToFieldsBoard) != NUMBER_0
					|| (OfficerPlies.ALL_OFFICER_MOVES[fieldID] & ~excludedToFieldsBoard) != NUMBER_0) {
					count += QueenMovesGen.genAllMoves(excludedToFieldsBoard, interuptAtFirstExclusionHit, pid,
							fieldID,
							free,
							allByColour[opponentColour],
							board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
				}
				break;
		}
		
		return count;
	}
	
	public final int genCapturePromotionMoves(final IInternalMoveList list) {
		return genCapturePromotionMoves(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	private final int genCapturePromotionMoves(int colour, final IInternalMoveList list, final int maxCount) {

		if (Properties.STATISTICS_MODE) {
			statistics.capturePromotionMoves.start();
		}

		int count = 0;
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no need to generate moves.
		 */
		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.capturePromotionMoves.stop(0);
			}

			return 0;
		}
		
		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN) != 0L) {
			count += genCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_PAWN, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.capturePromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_KNIGHT) != 0L) {
			count += genCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_KNIGHT, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.capturePromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_OFFICER) != 0L) {
			count += genCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_OFFICER, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.capturePromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_CASTLE) != 0L) {
			count += genCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_CASTLE, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.capturePromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_QUEEN) != 0L) {
			count += genCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_QUEEN, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.capturePromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		count += genCaptureMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_KING, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.capturePromotionMoves.stop(count);
			}
			return count;
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.capturePromotionMoves.stop(count);
		}

		
		return count;
	}

	public final int genCaptureMoves_FiguresWithSameType(final int colour, final int opponentColour, final int type,
			final IInternalMoveList list, final int maxCount) {
		
		/**
		 * Pawns optimization
		 */
		if (type == Figures.TYPE_PAWN) {
			BackupInfo curEnpassInfo = backupInfo[playedMovesCount];
			if (curEnpassInfo.enpassantPawnBitboard == 0
					&& !hasPawnsCapturePromotion(colour, allByColourAndType[colour][Figures.TYPE_PAWN], allByColour[opponentColour])) {
				return 0;
			}
		}

		int count = 0;
		
		int pid = Figures.getPidByColourAndType(colour, type);
		final PiecesList fields = pieces.getPieces(pid);
		
		//final PiecesList all_from_type = all[type];
		final int size = fields.getDataSize();
		final int[] data = fields.getData();
		
		for (int i=0; i<size; i++) {
			
			int fieldID = data[i];
			
			long excludedToFieldsBoard = 0L;
			if (checkKeepersBuffer[colour].contains(fieldID)) {
				excludedToFieldsBoard = ~checkKeepersBuffer[colour].getValue(fieldID);
			}
			
			long bitboard = Fields.ALL_ORDERED_A1H1[fieldID];
			
			switch(type) {
				case Figures.TYPE_KNIGHT:
					long opponentPieces = allByColour[opponentColour];
					long attacks = KnightPlies.ALL_KNIGHT_MOVES[fieldID];
					if ((opponentPieces & attacks) != 0L) {
						count += KnightMovesGen.genCaptureMoves(excludedToFieldsBoard, pid,
								fieldID,
								allByColour[colour],
								allByColour[opponentColour],
								board,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
					break;
				case Figures.TYPE_KING:
					opponentPieces = allByColour[opponentColour];
					attacks = KingPlies.ALL_KING_MOVES[fieldID];
					if ((opponentPieces & attacks) != 0L) {
						int opKingID = getKingFieldID(opponentColour);
						count += KingMovesGen.genCaptureMoves(this, excludedToFieldsBoard, pid, colour, opponentColour,
								bitboard,
								fieldID,
								free,
								allByColour[colour],
								allByColour[opponentColour],
								board,
								Fields.ALL_ORDERED_A1H1[opKingID],
								opKingID,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
					break;
				case Figures.TYPE_PAWN:
					BackupInfo curEnpassInfo = backupInfo[playedMovesCount];
					if (colour == Figures.COLOUR_WHITE) {
						
						/**
						 * Pawns optimization
						 */
						if (curEnpassInfo.enpassantPawnBitboard == 0
								&& !hasPawnsCapturePromotion(colour, bitboard, allByColour[opponentColour])) {
							break;
						}
						
						count += WhitePawnMovesGen.genCapturePromotionEnpassantMoves(this, excludedToFieldsBoard, true, pid,
								fieldID,
								free,
								allByColour[opponentColour],
								board,
								curEnpassInfo.enpassantPawnBitboard != 0,
								curEnpassInfo.enpassantPawnBitboard,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					} else {
						
						/**
						 * Pawns optimization
						 */
						if (curEnpassInfo.enpassantPawnBitboard == 0
								&& !hasPawnsCapturePromotion(colour, bitboard, allByColour[opponentColour])) {
							break;
						}
						
						count += BlackPawnMovesGen.genCapturePromotionEnpassantMoves(this, excludedToFieldsBoard, true, pid,
								fieldID,
								free,
								allByColour[opponentColour],
								board,
								curEnpassInfo.enpassantPawnBitboard != 0,
								curEnpassInfo.enpassantPawnBitboard,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
					break;
				case Figures.TYPE_OFFICER:
					opponentPieces = allByColour[opponentColour];
					attacks = OfficerPlies.ALL_OFFICER_MOVES[fieldID];
					if ((opponentPieces & attacks) != 0L) {
						count += OfficerMovesGen.genCaptureMoves(excludedToFieldsBoard, true, pid,
								fieldID,
								free,
								opponentPieces,
								board,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
					break;
				case Figures.TYPE_CASTLE:
					opponentPieces = allByColour[opponentColour];
					attacks = CastlePlies.ALL_CASTLE_MOVES[fieldID];
					if ((opponentPieces & attacks) != 0L) {
						count += CastleMovesGen.genCaptureMoves(this, excludedToFieldsBoard, true, pid,
								fieldID,
								free,
								opponentPieces,
								board,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
					break;
				case Figures.TYPE_QUEEN:
					opponentPieces = allByColour[opponentColour];
					attacks = CastlePlies.ALL_CASTLE_MOVES[fieldID] | OfficerPlies.ALL_OFFICER_MOVES[fieldID];
					if ((opponentPieces & attacks) != 0L) {
						count += QueenMovesGen.genCaptureMoves(this, excludedToFieldsBoard, pid,
								fieldID,
								free,
								opponentPieces,
								board,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
					break;
			}
		}
		
		return count;
	}

	private final boolean hasPawnsCapturePromotion(int colour, long pawnsBoard, long opponentBoard) {
		if (colour == Figures.COLOUR_WHITE) {
			
			if ((pawnsBoard & Fields.DIGIT_7) != 0) {
				return true;// promotion
			}
			
			long nonactivePawns = pawnsBoard & Fields.LETTER_A;
			long activePawns = pawnsBoard & ~nonactivePawns;
			long attacks1 = activePawns >> 7;
			nonactivePawns = pawnsBoard & Fields.LETTER_H;
			activePawns = pawnsBoard & ~nonactivePawns;
			long attacks2 = activePawns >> 9;
			
			long or = attacks1 | attacks2;
			
			if ((or & opponentBoard) != 0) { //Has no capture
				return true;
			}
		} else {
			
			if ((pawnsBoard & Fields.DIGIT_2) != 0) {
				return true;// promotion
			}
			
			long nonactivePawns = pawnsBoard & Fields.LETTER_A;
			long activePawns = pawnsBoard & ~nonactivePawns;
			long attacks1 = activePawns << 9;
			nonactivePawns = pawnsBoard & Fields.LETTER_H;
			activePawns = pawnsBoard & ~nonactivePawns;
			long attacks2 = activePawns << 7;
			
			long or = attacks1 | attacks2;
			
			if ((or & opponentBoard) != 0) { //Has no capture
				return true;
			}
		}
		
		return false;
	}
	
	public final int genNonCaptureNonPromotionMoves(final IInternalMoveList list) {
		return genNonCaptureNonPromotionMoves(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
			
	private final int genNonCaptureNonPromotionMoves(int colour, final IInternalMoveList list, final int maxCount) {
		int count = 0;
		
		if (Properties.STATISTICS_MODE) {
			statistics.nonCaptureNonPromotionMoves.start();
		}
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no need to generate moves.
		 */
		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.nonCaptureNonPromotionMoves.stop(0);
			}
			
			return 0;
		}
		
		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_PAWN) != 0L) {
			count += genNonCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_PAWN, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.nonCaptureNonPromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_KNIGHT) != 0L) {
			count += genNonCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_KNIGHT, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.nonCaptureNonPromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_OFFICER) != 0L) {
			count += genNonCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_OFFICER, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.nonCaptureNonPromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_CASTLE) != 0L) {
			count += genNonCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_CASTLE, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.nonCaptureNonPromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		if (getFiguresBitboardByColourAndType(colour, Figures.TYPE_QUEEN) != 0L) {
			count += genNonCaptureMoves_FiguresWithSameType(colour, opponentColour,
					Figures.TYPE_QUEEN, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.nonCaptureNonPromotionMoves.stop(count);
				}
				return count;
			}
		}
		
		count += genNonCaptureMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_KING, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.nonCaptureNonPromotionMoves.stop(count);
			}
			return count;
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.nonCaptureNonPromotionMoves.stop(count);
		}
		
		return count;
	}

	private final int genNonCaptureMoves_FiguresWithSameType(final int colour, final int opponentColour, final int type,
			final IInternalMoveList list, final int maxCount) {
		
		int count = 0;
		
		int pid = Figures.getPidByColourAndType(colour, type);
		final PiecesList fields = pieces.getPieces(pid);
		
		//final PiecesList all_from_type = all[type];
		final int size = fields.getDataSize();
		final int[] data = fields.getData();
		
		for (int i=0; i<size; i++) {
			
			int fieldID = data[i];
			
			long excludedToFieldsBoard = 0L;
			if (checkKeepersBuffer[colour].contains(fieldID)) {
				excludedToFieldsBoard = ~checkKeepersBuffer[colour].getValue(fieldID);
			}
			
			long bitboard = Fields.ALL_ORDERED_A1H1[fieldID];
			
			switch(type) {
				case Figures.TYPE_KNIGHT:
					count += KnightMovesGen.genNonCaptureMoves(excludedToFieldsBoard, pid,
							fieldID,
							allByColour[colour],
							allByColour[opponentColour],
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					break;
				case Figures.TYPE_KING:
					int opKingID = getKingFieldID(opponentColour);
					count += KingMovesGen.genNonCaptureMoves(this, excludedToFieldsBoard, pid, colour, opponentColour,
							bitboard,
							fieldID,
							free,
							allByColour[colour],
							allByColour[opponentColour],
							kingSidePossible(colour, opponentColour),
							queenSidePossible(colour, opponentColour),
							Fields.ALL_ORDERED_A1H1[opKingID],
							opKingID,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					break;
				case Figures.TYPE_PAWN:
					BackupInfo curEnpassInfo = backupInfo[playedMovesCount];
					if (colour == Figures.COLOUR_WHITE) {
						count += WhitePawnMovesGen.genNonCaptureMoves(excludedToFieldsBoard,  true, pid,
								fieldID,
								free,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					} else {
						count += BlackPawnMovesGen.genNonCaptureMoves(excludedToFieldsBoard, true, pid,
								fieldID,
								free,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
					break;
				case Figures.TYPE_OFFICER:
					count += OfficerMovesGen.genNonCaptureMoves(excludedToFieldsBoard, true, pid,
							fieldID,
							free,
							allByColour[opponentColour],
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					break;
				case Figures.TYPE_CASTLE:
					count += CastleMovesGen.genNonCaptureMoves(excludedToFieldsBoard, true, pid,
							fieldID,
							free,
							allByColour[opponentColour],
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					break;
				case Figures.TYPE_QUEEN:
					count += QueenMovesGen.genNonCaptureMoves(excludedToFieldsBoard, pid,
							fieldID,
							free,
							allByColour[opponentColour],
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					break;
			}
		}
		
		return count;
	}
	
	public final int genPromotions(final IInternalMoveList list) {
		return genPromotions(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	private final int genPromotions(int colour, final IInternalMoveList list, final int maxCount) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.promotions.start();
		}
		
		int count = 0;
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no need to generate moves.
		 */
		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.promotions.stop(0);
			}
			
			return 0;
		}
		
		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		if (colour == Figures.COLOUR_WHITE) {
			if ((allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_PAWN] & Fields.DIGIT_7) == Fields.NUMBER_0) {
				if (Properties.STATISTICS_MODE) {
					statistics.promotions.stop(0);
				}
				
				return 0;
			}
		} else {
			if ((allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_PAWN] & Fields.DIGIT_2) == Fields.NUMBER_0) {
				if (Properties.STATISTICS_MODE) {
					statistics.promotions.stop(0);
				}
				
				return 0;
			}
		}
		
		int pid = Figures.getPidByColourAndType(colour, Figures.TYPE_PAWN);
		final PiecesList fields = pieces.getPieces(pid);

		
		//final PiecesList all_from_type = all[Figures.TYPE_PAWN];
		final int size = fields.getDataSize();
		final int[] data = fields.getData();
		
		for (int i=0; i<size; i++) {
			
			int fieldID = data[i];
			long figureBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
			
			if (colour == Figures.COLOUR_WHITE) {
				if ((figureBitboard & Fields.DIGIT_7) != Fields.NUMBER_0) {
					long excludedToFieldsBoard = 0L;
					if (checkKeepersBuffer[colour].contains(fieldID)) {
						excludedToFieldsBoard = ~checkKeepersBuffer[colour].getValue(fieldID);
					}
					count += WhitePawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true,
							figureBitboard, fieldID,
							free, allByColour[opponentColour], board,
							list, maxCount);
					if (count >= maxCount) {
						if (Properties.STATISTICS_MODE) {
							statistics.promotions.stop(count);
						}
						return count;
					}
				}
			} else {
				if ((figureBitboard & Fields.DIGIT_2) != Fields.NUMBER_0) {
					long excludedToFieldsBoard = 0L;
					if (checkKeepersBuffer[colour].contains(fieldID)) {
						excludedToFieldsBoard = ~checkKeepersBuffer[colour].getValue(fieldID);
					}
					count += BlackPawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true,
							figureBitboard, fieldID,
							free, allByColour[opponentColour], board,
							list, maxCount);
					if (count >= maxCount) {
						if (Properties.STATISTICS_MODE) {
							statistics.promotions.stop(count);
						}
						return count;
					}
				}
			}
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.promotions.stop(count);
		}
		
		return count;
	}
	
	public final int gen2MovesPromotions(final IInternalMoveList list) {
		return gen2MovesPromotions(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	private final int gen2MovesPromotions(int colour, final IInternalMoveList list, final int maxCount) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.promotions2.start();
		}
		
		int count = 0;
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no need to generate moves.
		 */
		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.promotions2.stop(0);
			}
			
			return 0;
		}
		
		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		if (colour == Figures.COLOUR_WHITE) {
			if ((allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_PAWN] & Fields.DIGIT_6) == Fields.NUMBER_0) {
				if (Properties.STATISTICS_MODE) {
					statistics.promotions2.stop(0);
				}
				
				return 0;
			}
		} else {
			if ((allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_PAWN] & Fields.DIGIT_3) == Fields.NUMBER_0) {
				if (Properties.STATISTICS_MODE) {
					statistics.promotions2.stop(0);
				}
				
				return 0;
			}
		}
		
		int pid = Figures.getPidByColourAndType(colour, Figures.TYPE_PAWN);
		final PiecesList fields = pieces.getPieces(pid);
		
		final int size = fields.getDataSize();
		final int[] data = fields.getData();
		
		for (int i=0; i<size; i++) {
			
			int fieldID = data[i];
			long figureBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
			
			BackupInfo curEnpassInfo = backupInfo[playedMovesCount];
			
			if (colour == Figures.COLOUR_WHITE) {
				if ((figureBitboard & Fields.DIGIT_6) != Fields.NUMBER_0) {
					long excludedToFieldsBoard = 0L;
					if (checkKeepersBuffer[colour].contains(fieldID)) {
						excludedToFieldsBoard = ~checkKeepersBuffer[colour].getValue(fieldID);
					}
					/*count += WhitePawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true,
							figureID, colour, opponentColour,
							figureBitboard, fieldID,
							free, allByColour[opponentColour], figureIDPerFieldID,
							count, moves);*/
					
					count += WhitePawnMovesGen.genAllMoves(this, excludedToFieldsBoard, true, pid,
							fieldID,
							free,
							allByColour[opponentColour],
							board,
							curEnpassInfo.enpassantPawnBitboard != 0,
							curEnpassInfo.enpassantPawnBitboard,
							list, maxCount);
					if (count >= maxCount) {
						if (Properties.STATISTICS_MODE) {
							statistics.promotions2.stop(count);
						}
						return count;
					}
				}
			} else {
				if ((figureBitboard & Fields.DIGIT_3) != Fields.NUMBER_0) {
					long excludedToFieldsBoard = 0L;
					if (checkKeepersBuffer[colour].contains(fieldID)) {
						excludedToFieldsBoard = ~checkKeepersBuffer[colour].getValue(fieldID);
					}
					/*count += BlackPawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true,
							figureID, colour, opponentColour,
							figureBitboard, fieldID,
							free, allByColour[opponentColour], figureIDPerFieldID,
							count, moves);*/
					count += BlackPawnMovesGen.genAllMoves(this, excludedToFieldsBoard, false, pid,
							fieldID,
							free,
							//allByColour[opponentColour],
							board,
							curEnpassInfo.enpassantPawnBitboard != 0,
							curEnpassInfo.enpassantPawnBitboard,
							list, maxCount);
					if (count >= maxCount) {
						if (Properties.STATISTICS_MODE) {
							statistics.promotions2.stop(count);
						}
						return count;
					}
				}
			}
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.promotions2.stop(count);
		}
		
		return count;
	}
	
	public final int genDirectCheckMoves(final IInternalMoveList list) {
		return genDirectCheckMoves(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	private final int genDirectCheckMoves(int colour, final IInternalMoveList list, final int maxCount) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.directCheckMoves.start();
		}
		
		int count = 0;
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no need to generate moves.
		 */
		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.directCheckMoves.stop(0);
			}

			return 0;
		}
		
		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		//int opponentKingID = Castling.KINGS_IDS_BY_COLOUR[opponentColour];
		
		int opponentKingFieldID = getKingFieldID(opponentColour);
		long opponentKingBitboard = Fields.ALL_ORDERED_A1H1[opponentKingFieldID];
		
		count += genCheckMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_PAWN, opponentKingFieldID, opponentKingBitboard, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.directCheckMoves.stop(count);
			}
			return count;
		}
		
		count += genCheckMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_KNIGHT, opponentKingFieldID, opponentKingBitboard, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.directCheckMoves.stop(count);
			}
			return count;
		}
		
		count += genCheckMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_OFFICER, opponentKingFieldID, opponentKingBitboard, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.directCheckMoves.stop(count);
			}
			return count;
		}
		
		count += genCheckMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_CASTLE, opponentKingFieldID, opponentKingBitboard, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.directCheckMoves.stop(count);
			}
			return count;
		}
		
		count += genCheckMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_QUEEN, opponentKingFieldID, opponentKingBitboard, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.directCheckMoves.stop(count);
			}
			return count;
		}
		
		count += genCheckMoves_FiguresWithSameType(colour, opponentColour,
				Figures.TYPE_KING, opponentKingFieldID, opponentKingBitboard, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.directCheckMoves.stop(count);
			}
			return count;
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.directCheckMoves.stop(count);
		}

		
		return count;
	}

	private final int genCheckMoves_FiguresWithSameType(final int colour, final int opponentColour, final int type,
			int opponentKingFieldID, long opponentKingBitboard,
			IInternalMoveList list, final int maxCount) {
		
		int count = 0;
		
		boolean promotionsPossible = false;
		boolean enpassantPossible = false;
		if (type == Figures.TYPE_PAWN) {
			BackupInfo curEnpassInfo = backupInfo[playedMovesCount];
			enpassantPossible = curEnpassInfo.enpassantPawnBitboard != 0; // && curEnpassInfo.enpassantColour == colour;
			if (colour == Figures.COLOUR_WHITE) {
				promotionsPossible = (allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_PAWN] & Fields.DIGIT_7) != Fields.NUMBER_0;
			} else {
				promotionsPossible = (allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_PAWN] & Fields.DIGIT_2) != Fields.NUMBER_0;
			}
		}
		

		int pid = Figures.getPidByColourAndType(colour, type);
		final PiecesList fields = pieces.getPieces(pid);
		
		final int size = fields.getDataSize();
		final int[] data = fields.getData();
		
		int enpassCount = 0;
		
		for (int i=0; i<size; i++) {
			
			int fieldID = data[i];
			
			long excludedToFieldsBoard = 0L;
			if (checkKeepersBuffer[colour].contains(fieldID)) {
				excludedToFieldsBoard = ~checkKeepersBuffer[colour].getValue(fieldID);
			}
			
			long figureBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
			
			switch(type) {
				case Figures.TYPE_KNIGHT:
					if ((KnightChecks.FIELDS_ATTACK_2[opponentKingFieldID] & figureBitboard) != 0) {
						count += KnightMovesGen.genCheckMoves(excludedToFieldsBoard,
								pid,
								fieldID,
								opponentKingFieldID,
								allByColour[colour],
								allByColour[opponentColour],
								board,
								list, maxCount);
						if (count >= maxCount) {
							return count;
						}
					}
					break;
				case Figures.TYPE_KING:
					/**
					 * Only castle side triggered direct checks
					 */
					
					IInternalMoveList toUse = list;
					if (toUse == null) {
						movesBuffer.reserved_clear();
						toUse = movesBuffer;
					}
					
					int sizeBefore = toUse.reserved_getCurrentSize();
					
					int fromIndex_before = count;
					count += KingMovesGen.genCastleSides(colour,
							kingSidePossible(colour, opponentColour),
							queenSidePossible(colour, opponentColour),
							toUse, maxCount);
					int castleSideCount = count - fromIndex_before;
					if (castleSideCount > 2) {
						throw new IllegalStateException();
					}
					if (castleSideCount > 0) {
						if (castleSideCount == 1) {
							if (!isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore])) {
								count--;
								toUse.reserved_removeLast();
							}
						} else if (castleSideCount == 2) {
							boolean first = isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
							boolean second = isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore + 1]);
							if (first) {
								if (second) {
									/**
									 * This is impossible, I want to see it throwing this ISE!
									 */
									throw new IllegalStateException("Both castle sides are direct check moves");
								} else {
									count--;
									toUse.reserved_removeLast();
								}
							} else {
								if (second) {
									//int firstMove = list.reserved_getMovesBuffer()[fromIndex_before];
									int secondMove = toUse.reserved_getMovesBuffer()[sizeBefore + 1];
									toUse.reserved_getMovesBuffer()[sizeBefore] = secondMove;
									//list.reserved_getMovesBuffer()[fromIndex_before + 1] = firstMove;
									count--;
									toUse.reserved_removeLast();
								} else {
									count -= 2;
									toUse.reserved_removeLast();
									toUse.reserved_removeLast();
								}
							}
						}
					}
					if (count >= maxCount) {
						return count;
					}
					break;
				case Figures.TYPE_PAWN:
					
					BackupInfo curEnpassInfo = backupInfo[playedMovesCount];
					if (colour == Figures.COLOUR_WHITE) {
						long possibleAttacks = WhitePawnsChecks.FIELDS_ATTACKERS_2[opponentKingFieldID];
						long pawns = allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_PAWN];
						if ((pawns & possibleAttacks) != 0L) {
							count += WhitePawnMovesGen.genCheckMoves(excludedToFieldsBoard,
									pid,
									fieldID,
									opponentKingFieldID,
									free,
									allByColour[opponentColour],
									board,
									list, maxCount);
							if (count >= maxCount) {
								return count;
							}
							if (enpassantPossible) {
								
								toUse = list;
								if (toUse == null) {
									movesBuffer.reserved_clear();
									toUse = movesBuffer;
								}
								sizeBefore = toUse.reserved_getCurrentSize();
								
								fromIndex_before = count;
								count += WhitePawnMovesGen.genEnpassantMove(this, excludedToFieldsBoard,
										pid,
										fieldID, allByColour[opponentColour],
										board, curEnpassInfo.enpassantPawnBitboard != 0,
										curEnpassInfo.enpassantPawnBitboard, toUse, maxCount);
								int curEnpassCount = count - fromIndex_before;
								if (curEnpassCount > 1) {
									throw new IllegalStateException();
								} 
								if (curEnpassCount == 1) {
									enpassCount++;
									if (!isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore])) {
										count--;
										toUse.reserved_removeLast();
									}
								}
								if (enpassCount == 2) {
									enpassantPossible = false;
								}
								if (count >= maxCount) {
									return count;
								}
							}
						}
						
						if (promotionsPossible) {
							
							toUse = list;
							if (toUse == null) {
								movesBuffer.reserved_clear();
								toUse = movesBuffer;
							}
							
							sizeBefore = toUse.reserved_getCurrentSize();
							
							if ((figureBitboard & Fields.DIGIT_7) != Fields.NUMBER_0) {
								int promCount = WhitePawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true,
										figureBitboard,
										fieldID,
										free, allByColour[opponentColour], board,
										toUse, maxCount);
								
								count += promCount;
								/*if (sizeBefore + promCount != toUse.reserved_getCurrentSize()) {
									throw new IllegalStateException();
								}*/
								
								int[] moves = toUse.reserved_getMovesBuffer();
								for (int cur = sizeBefore; cur < sizeBefore + promCount; cur++) {
									int prom = moves[cur];
									if (!isDirectCheckMove(prom)) {
										moves[cur] = moves[sizeBefore + promCount - 1];
										cur--;
										promCount--;
										count--;
										toUse.reserved_removeLast();
									}
								}
								
								if (count >= maxCount) {
									return count;
								}
							}
						}
					} else {
						long possibleAttacks = BlackPawnsChecks.FIELDS_ATTACKERS_2[opponentKingFieldID];
						long pawns = allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_PAWN];
						if ((pawns & possibleAttacks) != 0L) {
							count += BlackPawnMovesGen.genCheckMoves(excludedToFieldsBoard,
									pid, 
									fieldID,
									opponentKingFieldID,
									free,
									allByColour[opponentColour],
									board,
									list, maxCount);
							if (count >= maxCount) {
								return count;
							}
							if (enpassantPossible) {
								
								toUse = list;
								if (toUse == null) {
									movesBuffer.reserved_clear();
									toUse = movesBuffer;
								}
								
								sizeBefore = toUse.reserved_getCurrentSize();
								
								fromIndex_before = count;
								count += BlackPawnMovesGen.genEnpassantMove(this, excludedToFieldsBoard,
										pid,
										fieldID,
										allByColour[opponentColour],
										board, curEnpassInfo.enpassantPawnBitboard != 0,
										curEnpassInfo.enpassantPawnBitboard, toUse, maxCount);
								int curEnpassCount = count - fromIndex_before;
								if (curEnpassCount > 1) {
									throw new IllegalStateException();
								} 
								if (curEnpassCount == 1) {
									enpassCount++;
									if (!isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore])) {
										count--;
										toUse.reserved_removeLast();
									}
								}
								if (enpassCount == 2) {
									enpassantPossible = false;
								}
								if (count >= maxCount) {
									return count;
								}
							}
						}
						
						if (promotionsPossible) {
							
							toUse = list;
							if (toUse == null) {
								movesBuffer.reserved_clear();
								toUse = movesBuffer;
							}
							
							sizeBefore = toUse.reserved_getCurrentSize();
							
							//if (true) throw new IllegalStateException(); 
							
							if ((figureBitboard & Fields.DIGIT_2) != Fields.NUMBER_0) {
								//final int fromIndex_before = fromIndex;
								int promCount = BlackPawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true,
										figureBitboard,
										fieldID,
										free, allByColour[opponentColour], board,
										toUse, maxCount);
								
								count += promCount;
								
								//final int promCount = fromIndex - fromIndex_before;
								//int usedIndex = fromIndex;
								
								/*if (sizeBefore + promCount != toUse.reserved_getCurrentSize()) {
									throw new IllegalStateException();
								}*/
								
								int[] moves = toUse.reserved_getMovesBuffer();
								for (int cur = sizeBefore; cur < sizeBefore + promCount; cur++) {
									int prom = moves[cur];
									if (!isDirectCheckMove(prom)) {
										moves[cur] = moves[sizeBefore + promCount - 1];
										cur--;
										promCount--;
										count--;
										toUse.reserved_removeLast();
									}
								}
								
								if (count >= maxCount) {
									return count;
								}
							}
						}
					}
					break;
				case Figures.TYPE_OFFICER:
					count += OfficerMovesGen.genCheckMoves(excludedToFieldsBoard,
							pid,
							fieldID,
							opponentKingBitboard,
							opponentKingFieldID,
							free,
							allByColour[colour],
							allByColour[opponentColour],
							board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					break;
				case Figures.TYPE_CASTLE:
					count += CastleMovesGen.genCheckMoves(excludedToFieldsBoard,
							pid,
							fieldID,
							opponentKingFieldID,
							free,
							allByColour[colour],
							allByColour[opponentColour],
							board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					break;
				case Figures.TYPE_QUEEN:
					count += QueenMovesGen.genCheckMoves(excludedToFieldsBoard, pid,
							fieldID,
							opponentKingBitboard,
							opponentKingFieldID,
							free,
							allByColour[colour],
							allByColour[opponentColour],
							board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					break;
			}
		}
		
		return count;
	}
	
	public final int genHiddenCheckMoves(final IInternalMoveList list) {
		return genHiddenCheckMoves(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	private final int genHiddenCheckMoves(int colour, IInternalMoveList list, final int maxCount) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.hiddenCheckMoves.start();
		}
		
		int count = 0;
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no need to generate moves.
		 */
		
		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.hiddenCheckMoves.stop(0);
			}

			return 0;
		}
		
		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		int opponentKingFieldID = getKingFieldID(opponentColour);
		
		long myPieces = getFiguresBitboardByColour(colour);
		long opponentPieces = getFiguresBitboardByColour(opponentColour);
		
		/**
		 * officers and queens
		 */
		final long officersAttacks = OfficerPlies.ALL_OFFICER_MOVES[opponentKingFieldID];
		count += genHiddenChecksFromOfficers(colour, opponentColour, opponentKingFieldID,
				myPieces, opponentPieces,
				officersAttacks,
				allByColourAndType[colour][Figures.TYPE_OFFICER],
				Figures.TYPE_OFFICER,
				list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.hiddenCheckMoves.stop(count);
			}
			return count;
		}
		
		count += genHiddenChecksFromOfficers(colour, opponentColour, opponentKingFieldID,
				myPieces, opponentPieces,
				officersAttacks,
				allByColourAndType[colour][Figures.TYPE_QUEEN],
				Figures.TYPE_QUEEN,
				list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.hiddenCheckMoves.stop(count);
			}
			return count;
		}
		
		/**
		 * castles and queens
		 */
		final long castlesAttacks = CastlePlies.ALL_CASTLE_MOVES[opponentKingFieldID];
		count += genHiddenChecksFromCastles(colour, opponentColour, opponentKingFieldID,
				myPieces, opponentPieces,
				castlesAttacks,
				allByColourAndType[colour][Figures.TYPE_CASTLE],
				Figures.TYPE_CASTLE,
				list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.hiddenCheckMoves.stop(count);
			}
			return count;
		}
		
		count += genHiddenChecksFromCastles(colour, opponentColour, opponentKingFieldID,
				myPieces, opponentPieces,
				castlesAttacks,
				allByColourAndType[colour][Figures.TYPE_QUEEN],
				Figures.TYPE_QUEEN,
				list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.hiddenCheckMoves.stop(count);
			}
			return count;
		}
		
		/**
		 * Check whether the enpassant move (if possible) makes hidden check
		 */
		int enpassCount = 0;
		BackupInfo curEnpassInfo = backupInfo[playedMovesCount];
		boolean enpassantPossible = curEnpassInfo.enpassantPawnBitboard != 0;// && curEnpassInfo.enpassantColour == colour;
		if (enpassantPossible) {
			
			int pid = Figures.getPidByColourAndType(colour, Figures.TYPE_PAWN);
			final PiecesList fields = pieces.getPieces(pid);
			
			final int size = fields.getDataSize();
			final int[] data = fields.getData();
			
			for (int i=0; i<size; i++) {
				
				int fieldID = data[i];
				long figureBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
				
				if (colour == Figures.COLOUR_WHITE) {
					long excludedToFieldsIDs = 0L;
					if (checkKeepersBuffer[colour].contains(fieldID)) {
						excludedToFieldsIDs |= ~checkKeepersBuffer[colour].getValue(fieldID);
					}
					
					IInternalMoveList toUse = list;
					if (toUse == null) {
						movesBuffer.reserved_clear();
						toUse = movesBuffer;
					}
					
					int sizeBefore = toUse.reserved_getCurrentSize();
					
					int fromIndex_before = count;
					count += WhitePawnMovesGen.genEnpassantMove(this, excludedToFieldsIDs,
							pid,
							fieldID,
							allByColour[opponentColour],
							board, curEnpassInfo.enpassantPawnBitboard != 0,
							curEnpassInfo.enpassantPawnBitboard, toUse, maxCount);
					int curEnpassCount = count - fromIndex_before;
					if (curEnpassCount > 1) {
						throw new IllegalStateException();
					} 
					if (curEnpassCount == 1) {
						enpassCount++;
						boolean checkMove = isCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
						boolean directCheckMove = isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
						boolean hiddenCheckMove = checkMove && !directCheckMove;
						if (!hiddenCheckMove) {
							count--;
							toUse.reserved_removeLast();
						}
					}
					if (enpassCount == 2) {
						enpassantPossible = false;
					}
					
					if (count >= maxCount) {
						if (Properties.STATISTICS_MODE) {
							statistics.hiddenCheckMoves.stop(count);
						}
						return count;
					}
					
				} else {
					long excludedToFieldsIDs = 0L;
					if (checkKeepersBuffer[colour].contains(fieldID)) {
						excludedToFieldsIDs |= ~checkKeepersBuffer[colour].getValue(fieldID);
					}
					
					IInternalMoveList toUse = list;
					if (toUse == null) {
						movesBuffer.reserved_clear();
						toUse = movesBuffer;
					}
					
					int sizeBefore = toUse.reserved_getCurrentSize();
					
					int fromIndex_before = count;
					count += BlackPawnMovesGen.genEnpassantMove(this, excludedToFieldsIDs,
							pid,
							fieldID,
							allByColour[opponentColour],
							board, curEnpassInfo.enpassantPawnBitboard != 0,
							curEnpassInfo.enpassantPawnBitboard, toUse, maxCount);
					int curEnpassCount = count - fromIndex_before;
					if (curEnpassCount > 1) {
						throw new IllegalStateException();
					} 
					if (curEnpassCount == 1) {
						enpassCount++;
						boolean checkMove = isCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
						boolean directCheckMove = isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
						boolean hiddenCheckMove = checkMove && !directCheckMove;
						if (!hiddenCheckMove) {
							count--;
							toUse.reserved_removeLast();
						}
					}
					if (enpassCount == 2) {
						enpassantPossible = false;
					}
					
					if (count >= maxCount) {
						if (Properties.STATISTICS_MODE) {
							statistics.hiddenCheckMoves.stop(count);
						}
						return count;
					}
					
				}
				
				if (!enpassantPossible) {
					break;
				}
			}
			
			if (enpassCount < 1) {
				/**
				 * Enpassant opens check here.
				 */
				//throw new IllegalStateException();
			}
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.hiddenCheckMoves.stop(count);
		}

		return count;
	}

	private final int genHiddenChecksFromOfficers(int colour, int opponentColour,
			int opponentKingFieldID,
			long myPieces, long opponentPieces,
			final long officersAttacks, final long myOfficersBoard,
			final int checkFigureType, //officer or queen
			final IInternalMoveList list, final int maxCount) {
		
		int count = 0;
		
		if ((officersAttacks & myOfficersBoard) != NUMBER_0) {
			long dir = 0;
			long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[opponentKingFieldID];
			long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[opponentKingFieldID];
			long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[opponentKingFieldID];
			long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[opponentKingFieldID];
			
			boolean hit = false;
			
			if ((myOfficersBoard & dir0) != NUMBER_0) {
				hit = true;
				dir |= dir0;
			}
			
			if ((myOfficersBoard & dir1) != NUMBER_0) {
				hit = true;
				dir |= dir1;
			}
			
			if ((myOfficersBoard & dir2) != NUMBER_0) {
				hit = true;
				dir |= dir2;
			} 
			
			if ((myOfficersBoard & dir3) != NUMBER_0) {
				hit = true;
				dir |= dir3;
			}
			
			if (!hit) {
				throw new IllegalStateException();
			}
			
			boolean hit1 = false;
			//PiecesList myOfficersIDs = stateManager.getAliveFigures(colour)[checkFigureType];
			int pid = Figures.getPidByColourAndType(colour, checkFigureType);
			final PiecesList fields = pieces.getPieces(pid);
			int size = fields.getDataSize();
			int[] ids = fields.getData();
			for (int i=0; i<size; i++) {
				int officerFieldID = ids[i];
				long officerBitboard = Fields.ALL_ORDERED_A1H1[officerFieldID];
				if ((officerBitboard & dir) != NUMBER_0) {
					hit1 = true;
					long path = OfficerPlies.PATHS[officerFieldID][opponentKingFieldID];
					if ((path & opponentPieces) == NUMBER_0) {
						long myAndPath = path & myPieces;
					
						if (myAndPath != NUMBER_0 && Utils.has1BitSet(myAndPath)) {
							/**
							 * Get the field ID of this one bit and after that the figure ID on that field ID.
							 */
							int fieldID = get67IDByBitboard(myAndPath);
							int f_pid = board[fieldID];
							int figureType = Figures.getTypeByPid(f_pid);
							switch(figureType) {
								case Figures.TYPE_PAWN:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_OFFICER:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_CASTLE:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_QUEEN:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_KNIGHT:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_KING:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
							}
						}
					}
				}
			}
			
			
			if (!hit1) {
				throw new IllegalStateException();
			}
		}
		return count;
	}
	
	private final int genHiddenChecksFromCastles(int colour, int opponentColour,
			int opponentKingFieldID,
			long myPieces, long opponentPieces,
			final long castlesAttacks, final long myCastlesBoard,
			final int checkFigureType, //officer or queen
			final IInternalMoveList list, final int maxCount) {
		
		int count = 0;
		
		if ((castlesAttacks & myCastlesBoard) != NUMBER_0) {
			long dir = 0;
			long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[opponentKingFieldID];
			long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[opponentKingFieldID];
			long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[opponentKingFieldID];
			long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[opponentKingFieldID];
			
			boolean hit = false;
			
			if ((myCastlesBoard & dir0) != NUMBER_0) {
				hit = true;
				dir |= dir0;
			}
			
			if ((myCastlesBoard & dir1) != NUMBER_0) {
				hit = true;
				dir |= dir1;
			}
			
			if ((myCastlesBoard & dir2) != NUMBER_0) {
				hit = true;
				dir |= dir2;
			}
			
			if ((myCastlesBoard & dir3) != NUMBER_0) {
				hit = true;
				dir |= dir3;
			}
			
			if (!hit) {
				throw new IllegalStateException();
			}
			
			boolean hit1 = false;

			int pid = Figures.getPidByColourAndType(colour, checkFigureType);
			final PiecesList fields = pieces.getPieces(pid);
			int size = fields.getDataSize();
			int[] ids = fields.getData();
			for (int i=0; i<size; i++) {
				int castleFieldID = ids[i];
				long castleBitboard = Fields.ALL_ORDERED_A1H1[castleFieldID];
				if ((castleBitboard & dir) != NUMBER_0) {
					hit1 = true;
					long path = CastlePlies.PATHS[castleFieldID][opponentKingFieldID];
					if ((path & opponentPieces) == NUMBER_0) {
						long myAndPath = path & myPieces;
					
						if (myAndPath != NUMBER_0 && Utils.has1BitSet(myAndPath)) {
							/**
							 * Get the field ID of this one bit and after that the figure ID on that field ID.
							 */
							int fieldID = get67IDByBitboard(myAndPath);
							int f_pid = board[fieldID];
							int figureType = Figures.getTypeByPid(f_pid);
							switch(figureType) {
								case Figures.TYPE_PAWN:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_OFFICER:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_CASTLE:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_QUEEN:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_KNIGHT:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
								case Figures.TYPE_KING:
									count += getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount);
									if (count >= maxCount) {
										return count;
									}
									break;
							}
						}
					}
				}
			}
			
			
			if (!hit1) {
				throw new IllegalStateException();
			}
		}
		return count;
	}
	
	public final int getAllSingleFigureMoves(long excludedToFieldsIDs,
			final int colour, final int opponentColour,
			final int pid, final int figureType,
			final long fieldBitboard, final int fieldID,
			final IInternalMoveList list, final int maxCount) {
		
		int count = 0;
		
		if (checkKeepersBuffer[colour].contains(fieldID)) {
			excludedToFieldsIDs |= ~checkKeepersBuffer[colour].getValue(fieldID);
		}
		
		switch(figureType) {
			case Figures.TYPE_KNIGHT:
				count += KnightMovesGen.genAllMoves(excludedToFieldsIDs, pid,
						fieldID,
						allByColour[colour],
						allByColour[opponentColour],
						board,
						list, maxCount);
				if (count >= maxCount) {
					return count;
				}
				break;
			case Figures.TYPE_KING:
				int opKingID = getKingFieldID(opponentColour);
				
				count += KingMovesGen.genAllMoves(true, this, excludedToFieldsIDs, pid, colour, opponentColour,
						fieldBitboard,
						fieldID,
						free,
						allByColour[colour],
						allByColour[opponentColour],
						board,
						kingSidePossible(colour, opponentColour),
						queenSidePossible(colour, opponentColour),
						Fields.ALL_ORDERED_A1H1[opKingID],
						opKingID,
						list, maxCount);
				if (count >= maxCount) {
					return count;
				}
				break;
			case Figures.TYPE_PAWN:
				/**
				 * Do not generate captures and promotions because of duplicates - use genAllNonSpecialMoves
				 */
				if (colour == Figures.COLOUR_WHITE) {
					/*fromIndex += WhitePawnMovesGen.genAllMoves(excludedToFieldsIDs, figureID, colour, opponentColour,
							fieldBitboard,
							fieldID,
							free,
							allByColour[colour],
							allByColour[opponentColour],
							figureIDPerFieldID,
							hasEnpassant,
							enpassantColour,
							enpassantPawnBitboard,
							fromIndex, moves);*/
					/**
					 * All without enpassant
					 */
					count += WhitePawnMovesGen.genPromotionMoves(excludedToFieldsIDs, true,
							fieldBitboard, fieldID,
							free, allByColour[opponentColour], board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					count += WhitePawnMovesGen.genAllNonSpecialMoves(excludedToFieldsIDs, true, pid,
							fieldID,
							free,
							allByColour[opponentColour],
							board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
				} else {
					/*fromIndex += BlackPawnMovesGen.genAllMoves(excludedToFieldsIDs, figureID, colour, opponentColour,
							fieldBitboard,
							fieldID,
							free,
							allByColour[colour],
							allByColour[opponentColour],
							figureIDPerFieldID,
							hasEnpassant,
							enpassantColour,
							enpassantPawnBitboard,
							fromIndex, moves);*/
					/**
					 * All without enpassant
					 */
					count += BlackPawnMovesGen.genPromotionMoves(excludedToFieldsIDs, true,
							fieldBitboard, fieldID,
							free, allByColour[opponentColour], board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
					count += BlackPawnMovesGen.genAllNonSpecialMoves(excludedToFieldsIDs, true, pid,
							fieldID,
							free,
							allByColour[opponentColour],
							board,
							list, maxCount);
					if (count >= maxCount) {
						return count;
					}
				}
				break;
			case Figures.TYPE_OFFICER:
				count += OfficerMovesGen.genAllMoves(excludedToFieldsIDs, true, pid,
						fieldID,
						free,
						allByColour[opponentColour],
						board,
						list, maxCount);
				if (count >= maxCount) {
					return count;
				}
				break;
			case Figures.TYPE_CASTLE:
				count += CastleMovesGen.genAllMoves(excludedToFieldsIDs, true, pid,
						fieldID,
						free,
						allByColour[opponentColour],
						board,
						list, maxCount);
				if (count >= maxCount) {
					return count;
				}
				break;
			case Figures.TYPE_QUEEN:
				count += QueenMovesGen.genAllMoves(excludedToFieldsIDs, true, pid,
						fieldID,
						free,
						allByColour[opponentColour],
						board,
						list, maxCount);
				if (count >= maxCount) {
					return count;
				}
				break;
		}

		/*if (fromIndex - fromIndex_backup != 0) {
			System.out.println(Move.moveToString(moves[fromIndex - 1]));
		}*/
		
		return count;
	}
	
	public final int genAllCheckMoves(final IInternalMoveList list) {
		return genAllCheckMoves(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	private final int genAllCheckMoves(int colour, final IInternalMoveList list, final int maxCount) {
		if (Properties.STATISTICS_MODE) {
			statistics.allCheckMoves.start();
		}
		
		int count = 0;
		
		count += genHiddenCheckMoves(colour, list, maxCount);
		count += genDirectCheckMoves(colour, list, maxCount);
		count -= removeDuplicates(list, count);
		
		if (Properties.STATISTICS_MODE) {
			statistics.allCheckMoves.stop(count);
		}

		return count;
	}
	
	public final int genCapturePromotionCheckMoves(final IInternalMoveList list) {
		return genCapturePromotionCheckMoves(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	private final int genCapturePromotionCheckMoves(int colour, final IInternalMoveList list, final int maxCount) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.capturePromotionCheckMoves.start();
		}
		
		int count = 0;
		
		count += genCapturePromotionMoves(colour, list, maxCount);
		count += genHiddenCheckMoves(colour, list, maxCount);
		count += genDirectCheckMoves(colour, list, maxCount);
		count -= removeDuplicates(list, count);

		if (Properties.STATISTICS_MODE) {
			statistics.capturePromotionCheckMoves.stop(count);
		}

		return count;
	}
	
	private final int removeDuplicates(IInternalMoveList list, int count) {
		int removed = 0;
		
		if (count != list.reserved_getCurrentSize()) {
			throw new IllegalStateException();
		}
		
		//if (true) throw new IllegalStateException();
		duplicatesRemoverBuffer.clear();
		
		int[] moves = list.reserved_getMovesBuffer();
		for (int i=0; i<count; i++) {
			int cur = moves[i];
			int moveIDX = MoveInt.getMoveIndex(cur);
			if (duplicatesRemoverBuffer.contains(moveIDX)) {
				if (i < count - 1) {
					moves[i] = moves[count - 1];
					moves[count - 1] = cur;
					count--;
					list.reserved_removeLast();
					i--;
				}
				removed++;
			} else {
				duplicatesRemoverBuffer.add(moveIDX);
			}
		}
		
		return removed;
	}

	public final int genKingEscapes(final IInternalMoveList list) {
		return genKingEscapes(getColourToMove(), list, GlobalConstants.MAX_MOVES_ON_LEVEL);
	}
	
	public final int genKingEscapes(int colour, final IInternalMoveList list, final int maxCount) {
		
		if (Properties.STATISTICS_MODE) {
			statistics.kingEscapes.start();
		}
		
		int count = 0;
		
		if (Properties.DEBUG_MODE) {
			if (!isInCheck(colour)) {
				throw new IllegalStateException("Call to genKingEscapes during non-check");
			}
		}
		
		/**
		 * Check whether the King of colour is alive.
		 * If the King is death then there is no need to generate moves.
		 */
		if (getKingIndexSet(colour).getDataSize() == 0) {
			if (Properties.STATISTICS_MODE) {
				statistics.kingEscapes.stop(0);
			}
			return 0;
		}

		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		int myKingID = getKingFieldID(colour);
		
		int checksCount = CheckingCount.getChecksCount(checkerBuffer, this, colour, opponentColour,
				Fields.ALL_ORDERED_A1H1[myKingID],
				myKingID, free);
		
		if (checksCount <= 0) {
			throw new IllegalStateException("checksCount <= 0");
		}
		
		count += genAllMoves_FiguresWithSameType(0L, false, true, colour, opponentColour,
				Figures.TYPE_KING, list, maxCount);
		if (count >= maxCount) {
			if (Properties.STATISTICS_MODE) {
				statistics.kingEscapes.stop(count);
			}
			return count;
		}
			
		if (checksCount == 1) {
			long includedToFieldsBoard = checkerBuffer.fieldBitboard;
			if (checkerBuffer.slider) {
				includedToFieldsBoard |= checkerBuffer.sliderAttackRayBitboard;
			}
			count += genAllMoves_FiguresWithSameType(~includedToFieldsBoard, false, true, colour, opponentColour,
					Figures.TYPE_PAWN, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.kingEscapes.stop(count);
				}
				return count;
			}
			
			count += genAllMoves_FiguresWithSameType(~includedToFieldsBoard, false, true, colour, opponentColour,
					Figures.TYPE_KNIGHT, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.kingEscapes.stop(count);
				}
				return count;
			}
			
			count += genAllMoves_FiguresWithSameType(~includedToFieldsBoard, false, true, colour, opponentColour,
					Figures.TYPE_OFFICER, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.kingEscapes.stop(count);
				}
				return count;
			}
			
			count += genAllMoves_FiguresWithSameType(~includedToFieldsBoard, false, true, colour, opponentColour,
					Figures.TYPE_CASTLE, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.kingEscapes.stop(count);
				}
				return count;
			}
			
			count += genAllMoves_FiguresWithSameType(~includedToFieldsBoard, false, true, colour, opponentColour,
					Figures.TYPE_QUEEN, list, maxCount);
			if (count >= maxCount) {
				if (Properties.STATISTICS_MODE) {
					statistics.kingEscapes.stop(count);
				}
				return count;
			}

		} else if (checksCount > 2) {
			throw new IllegalStateException("Triple check!");
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.kingEscapes.stop(count);
		}

		return count;
	}
	
	/*protected int genKingEscapes(int figureID, int colour, final long[][] moves) {
		
		if (true) throw new IllegalStateException();
		
		int count = 0;
		
		if (Properties.DEBUG_MODE) {
			if (!isInCheck(colour)) {
				throw new IllegalStateException("Call to genKingEscapes during non-check");
			}
		}
		

		if (getKingIndexSet(colour).getDataSize() != 0) {
			return 0;
		}

		fillCheckKeepers(colour);
		
		int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
		int myKingID = getKingFieldID(colour);
		int checksCount = CheckingCount.getChecksCount(checkerBuffer, this, colour, opponentColour,
				Fields.ALL_ORDERED_A1H1[myKingID],
				myKingID, free);
		
		if (checksCount <= 0) {
			throw new IllegalStateException();
		}
		
		if (figureID == myKingID) {
			count += genAllMoves_FiguresWithSameType(0L, false, true, colour, opponentColour,
					Figures.TYPE_KING, moves, count, GlobalConstants.MAX_MOVES_ON_LEVEL);
		} else {
			if (checksCount == 1) {
				long includedToFieldsBoard = checkerBuffer.fieldBitboard;
				if (checkerBuffer.slider) {
					includedToFieldsBoard |= checkerBuffer.sliderAttackRayBitboard;
				}
				
				count += genAllMoves_ByFigureID(figureID, ~includedToFieldsBoard,
						false,
						true,
						colour, opponentColour,
						Figures.FIGURES_TYPES[figureID],
						moves, count, GlobalConstants.MAX_MOVES_ON_LEVEL);
			} else if (checksCount > 2) {
				throw new IllegalStateException("Triple check!");
			}
		}
		
		return count;
	}*/
	
	public final boolean hasMoveInCheck() {
		if (Properties.STATISTICS_MODE) {
			statistics.hasMoveInCheck.start();
		}
		
		boolean result = genKingEscapes(getColourToMove(), null, 1) > 0;
		
		if (Properties.STATISTICS_MODE) {
			statistics.hasMoveInCheck.stop(result ? 1 : 0);
		}

		return result;
	}
	
	public final boolean hasMoveInNonCheck() {
		
		if (Properties.STATISTICS_MODE) {
			statistics.hasMoveInNonCheck.start();
		}
		
		boolean result = genAllMoves(0L, true, getColourToMove(), null, 1) > 0;
		
		if (Properties.STATISTICS_MODE) {
			statistics.hasMoveInNonCheck.stop(result ? 1 : 0);
		}
		
		return result;
	}
	
	public final boolean hasMove() {
		if (Properties.STATISTICS_MODE) {
			statistics.hasMove.start();
		}
		
		boolean inCheck = isInCheck();
		boolean result;
		if (inCheck) {
			 result = hasMoveInCheck();
		} else {
			result = hasMoveInNonCheck();
		}
		
		if (Properties.STATISTICS_MODE) {
			statistics.hasMove.stop(result ? 1 : 0);
		}
		
		return result;
	}
	
	public final boolean hasCapturePromotionCheck() {
		
		int colour = getColourToMove();
		
		if (genCapturePromotionMoves(colour, null, 1) > 0) {
			return true;
		}
		if (genDirectCheckMoves(colour, null, 1) > 0) {
			return true;
		}
		if (genHiddenCheckMoves(colour, null, 1) > 0) {
			return true;
		}
		
		return false;
	}
	
	public final boolean hasChecks(int colour) {
		if (genDirectCheckMoves(colour, null, 1) > 0) {
			return true;
		}
		if (genHiddenCheckMoves(colour, null, 1) > 0) {
			return true;
		}
		
		return false;
	}
	
	public final boolean hasSingleMove() {
		int colour = getColourToMove();
		
		boolean inCheck = isInCheck(colour);
		boolean result;
		
		if (inCheck) {
			 result = genKingEscapes(colour, null, 2) == 1;
		} else {
			result = genAllMoves(0L, true, colour, null, 2) == 1;
		}
		
		return result;
	}
	
	public final boolean has2MovePromotions() {
		return gen2MovesPromotions(getColourToMove(), null, 1) >= 1;
	}

	public final boolean hasPromotions() {
		return genPromotions(getColourToMove(), null, 1) >= 1;
	}
	
	public final IMoveIterator iterator(int factoryHandle) {
		
		throw new IllegalStateException();
		
		/*int colour = getColourToMove();
		boolean inCheck = isInCheck(colour);
		int checkIndex = inCheck ? 0 : 1;
		
		MoveIterator iter = iterators[checkIndex][factoryHandle][playedMovesCount];
		
		if (iter == null) {
			iter = iterators[checkIndex][factoryHandle][playedMovesCount]
			         = moveIteratorFactory[factoryHandle].create(this, inCheck);
		} else {
			iter.reset();
		}
		
		return iter;*/
	}
	
	public final void test(int colour) {
		//testMovesCounting(colour);
	}
	
	public final void testMovesCounting(int colour) {
		int maxCount = 1;
		if (genAllMoves(0L, true, colour, null, maxCount) > maxCount) {
			throw new IllegalStateException();
		}
		if (genCapturePromotionMoves(colour, null, maxCount) > maxCount) {
			throw new IllegalStateException();
		}
		if (genNonCaptureNonPromotionMoves(colour, null, maxCount) > maxCount) {
			throw new IllegalStateException();
		}
		if (genPromotions(colour, null, maxCount) > maxCount) {
			throw new IllegalStateException();
		}
		if (genDirectCheckMoves(colour, null, maxCount) > maxCount) {
			throw new IllegalStateException();
		}
		if (genHiddenCheckMoves(colour, null, maxCount) > maxCount) {
			throw new IllegalStateException();
		}
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
				
				materialFactor.initially_addPiece(pid, fieldID);
				materialState.initially_addPiece(pid, fieldID);
				if (eval != null) eval.initially_addPiece(pid, fieldID);
				/*if (moveListeners.length > 0) {
					for (int i=0; i<moveListeners.length; i++) {
						moveListeners[i].initially_addPiece(pid, fieldID);
					}
				}*/
				
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
		

		int size;
		int[] data;
		
		//Init white bitboards
		
		PiecesList king = pieces.getPieces(Constants.PID_W_KING);
		size = king.getDataSize();
		data = king.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_KING] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		PiecesList knights = pieces.getPieces(Constants.PID_W_KNIGHT);
		size = knights.getDataSize();
		data = knights.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_KNIGHT] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		PiecesList pawns = pieces.getPieces(Constants.PID_W_PAWN);
		size = pawns.getDataSize();
		data = pawns.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_PAWN] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		PiecesList officers = pieces.getPieces(Constants.PID_W_BISHOP);
		size = officers.getDataSize();
		data = officers.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_OFFICER] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		PiecesList castles = pieces.getPieces(Constants.PID_W_ROOK);
		size = castles.getDataSize();
		data = castles.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_CASTLE] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		PiecesList queens = pieces.getPieces(Constants.PID_W_QUEEN);
		size = queens.getDataSize();
		data = queens.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_QUEEN] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		//Init black bitboards
		king = pieces.getPieces(Constants.PID_B_KING);
		size = king.getDataSize();
		data = king.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_KING] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		knights = pieces.getPieces(Constants.PID_B_KNIGHT);
		size = knights.getDataSize();
		data = knights.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_KNIGHT] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		pawns = pieces.getPieces(Constants.PID_B_PAWN);
		size = pawns.getDataSize();
		data = pawns.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_PAWN] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		officers = pieces.getPieces(Constants.PID_B_BISHOP);
		size = officers.getDataSize();
		data = officers.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_OFFICER] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		castles = pieces.getPieces(Constants.PID_B_ROOK);
		size = castles.getDataSize();
		data = castles.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_CASTLE] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		queens = pieces.getPieces(Constants.PID_B_QUEEN);
		size = queens.getDataSize();
		data = queens.getData();
		for (int i=0; i<size; i++) {
			int fieldID = data[i];
			allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_QUEEN] |= Fields.ALL_ORDERED_A1H1[fieldID];
		}
		
		allByColour[Figures.COLOUR_WHITE] = allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_KING] | allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_PAWN] |
								allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_KNIGHT] |	allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_OFFICER] |
								allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_CASTLE] | allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_QUEEN];
		
		allByColour[Figures.COLOUR_BLACK] = allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_KING] | allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_PAWN] |
								allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_KNIGHT] | allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_OFFICER] |
								allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_CASTLE] | allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_QUEEN];
		
		free = ~(allByColour[Figures.COLOUR_WHITE] | allByColour[Figures.COLOUR_BLACK]);
	}
	
	public final IPiecesLists getPiecesLists() {
		return pieces;
	}
	
	public long getFigureBitboardByID(int figureID) {
		throw new UnsupportedOperationException();
	}

	public final long getFiguresBitboardByColour(int colour) {
		return allByColour[colour];
	}

	public final long getFiguresBitboardByColourAndType(int colour, int type) {
		return allByColourAndType[colour][type];
	}

	public final long getFiguresBitboardByPID(int pid) {
		return allByColourAndType[Constants.getColourByPieceIdentity(pid)][Constants.PIECE_IDENTITY_2_TYPE[pid]];
	}
	
	public final long getFreeBitboard() {
		return free;
	}

	public final long getPawnsHashKey() {
		return pawnskey;
	}
	
	public int getFieldID(int figureID) {
		throw new UnsupportedOperationException();
	}
	
	public int getFigureID(int fieldID) {
		return board[fieldID];
	}
	
	
	public int getCastlingType(int colour) {
		return castledByColour[colour];
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
	
	public int getPlayedMovesCount_Total() {
		
		//if (true) throw new IllegalStateException(); 
		
		return playedMovesCount_initial + getPlayedMovesCount();
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
	

	public BoardStat getStatistics() {
		return statistics;
	}
	
	public Board clone() {
		Board clone = null;
		try {
			clone = (Board) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cloneInternal(clone);
		return clone;
	}
	
	public void cloneInternal(Board clone) {
		clone.free = free;
		clone.allByColour = Utils.copy(allByColour);
		clone.allByColourAndType = Utils.copy(allByColourAndType);
		clone.board = Utils.copy(board);
		//clone.stateManager = stateManager.clone();
		//TODO: clone.hasEnpassant = hasEnpassant;
		//clone.possibleQueenCastleSideByColour = Utils.copy(possibleQueenCastleSideByColour);
		//clone.possibleKingCastleSideByColour = Utils.copy(possibleKingCastleSideByColour);
		clone.lastMoveColour = lastMoveColour;
		clone.castledByColour = Utils.copy(castledByColour);
		clone.hashkey = hashkey;
		clone.pawnskey = pawnskey;
		//clone.material = material.clone();
	}
	
	public int hashCode() {
		return (int) hashkey; 
	}
	
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof Board) {
			Board other = (Board) obj;
			result = other.free == free;
			result = result && Arrays.equals(other.allByColour, allByColour);
			result = result && Utils.equals(other.allByColourAndType, allByColourAndType);
			result = result && Arrays.equals(other.board, board);
			result = result && checkAliveFiguresData(other);
			//result = result && other.stateManager.equals(stateManager);
			//TODO: result = result && other.hasEnpassant == hasEnpassant;
			//result = result && Arrays.equals(other.possibleQueenCastleSideByColour, possibleQueenCastleSideByColour);
			//result = result && Arrays.equals(other.possibleKingCastleSideByColour, possibleKingCastleSideByColour);
			result = result && other.lastMoveColour == lastMoveColour;
			result = result && Arrays.equals(other.castledByColour, castledByColour);
			result = result && other.hashkey == hashkey;
			result = result && other.pawnskey == pawnskey;
			//result = result && other.material.equals(material);
		}
		return result;
	}
	
	private boolean checkAliveFiguresData(Board other) {
		//FiguresStateManager otherStateManager = other.stateManager;
		//boolean result = checkAliveFiguresDataForColour(other, otherStateManager.getAliveFigures(Figures.COLOUR_WHITE), stateManager.getAliveFigures(Figures.COLOUR_WHITE));
		//result = result && checkAliveFiguresDataForColour(other, otherStateManager.getAliveFigures(Figures.COLOUR_BLACK), stateManager.getAliveFigures(Figures.COLOUR_BLACK));
		return true;
	}

	private boolean checkAliveFiguresDataForColour(Board other, PiecesList[] other_alive, PiecesList[] alive) {
		boolean result = checkAliveFiguresDataForColourAndType(other, other_alive[Figures.TYPE_PAWN], alive[Figures.TYPE_PAWN]);
		result = result && checkAliveFiguresDataForColourAndType(other, other_alive[Figures.TYPE_KING], alive[Figures.TYPE_KING]);
		result = result && checkAliveFiguresDataForColourAndType(other, other_alive[Figures.TYPE_KNIGHT], alive[Figures.TYPE_KNIGHT]);
		result = result && checkAliveFiguresDataForColourAndType(other, other_alive[Figures.TYPE_OFFICER], alive[Figures.TYPE_OFFICER]);
		result = result && checkAliveFiguresDataForColourAndType(other, other_alive[Figures.TYPE_CASTLE], alive[Figures.TYPE_CASTLE]);
		result = result && checkAliveFiguresDataForColourAndType(other, other_alive[Figures.TYPE_QUEEN], alive[Figures.TYPE_QUEEN]);
		return result;
	}
	
	private boolean checkAliveFiguresDataForColourAndType(Board other, PiecesList other_figures, PiecesList figures) {
		boolean result = true;
		
		int size = figures.getDataSize();
		int[] data = figures.getData();
		
		int other_size = other_figures.getDataSize();
		//int[] other_data = other_figures.getData();
		
		if (other_size == size) {
			for (int i=0; i<size; i++) {
				//int other_figure = other_data[i];
				int figure = data[i];
				if (other.board[figure] != board[figure]) {
					result = false;
					break;
				}
			}
		} else {
			result = false;
		}

		return result;
	}
	
	public String toString() {
		String result = "\r\nWhite: " + Bits.toBinaryString(allByColour[Figures.COLOUR_WHITE]) + "\r\n";
		result += "Black: " + Bits.toBinaryString(allByColour[Figures.COLOUR_BLACK]) + "\r\n";
		result += "Free : " + Bits.toBinaryString(free) + "\r\n";
		result += "WKing: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_KING]) + "\r\n";
		result += "BKing: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_KING]) + "\r\n";
		result += "WPawn: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_PAWN]) + "\r\n";
		result += "BPawn: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_PAWN]) + "\r\n";
		result += "WKngh: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_KNIGHT]) + "\r\n";
		result += "BKngh: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_KNIGHT]) + "\r\n";
		result += "WOffi: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_OFFICER]) + "\r\n";
		result += "BOffi: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_OFFICER]) + "\r\n";
		result += "WCast: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_CASTLE]) + "\r\n";
		result += "BCast: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_CASTLE]) + "\r\n";
		result += "WQeen: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_WHITE][Figures.TYPE_QUEEN]) + "\r\n";
		result += "BQeen: " + Bits.toBinaryString(allByColourAndType[Figures.COLOUR_BLACK][Figures.TYPE_QUEEN]) + "\r\n";
		result += matrixToString();
		result += "Moves: " + movesToString() + "\r\n";
		result += "EPD: " + toEPD() + "\r\n";
		//result += "Game status: " + getStatus() + "\r\n";
		return result;
	}

	private String matrixToString() {
		char[] columnsLetters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
		StringBuffer loStringBuffer = new StringBuffer(400);
		
		
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
			result += MoveInt.moveToString(move) + ", ";
		}
		return result;
	}
	
	/*private int[][] createMatrix() {
		int[][] result = new int[8][8];
		
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				result[i][j] = Figures.DUMMY_FIGURE_ID; 
			}
		}
		
		IndexNumberSet[] allWhite = stateManager.getAliveFigures(Figures.COLOUR_WHITE);
		fillMatrixByFiguresTypeSet(result, allWhite[Figures.TYPE_PAWN]);
		fillMatrixByFiguresTypeSet(result, allWhite[Figures.TYPE_KNIGHT]);
		fillMatrixByFiguresTypeSet(result, allWhite[Figures.TYPE_KING]);
		fillMatrixByFiguresTypeSet(result, allWhite[Figures.TYPE_OFFICER]);
		fillMatrixByFiguresTypeSet(result, allWhite[Figures.TYPE_CASTLE]);
		fillMatrixByFiguresTypeSet(result, allWhite[Figures.TYPE_QUEEN]);
		
		
		IndexNumberSet[] allBlack = stateManager.getAliveFigures(Figures.COLOUR_BLACK);
		fillMatrixByFiguresTypeSet(result, allBlack[Figures.TYPE_PAWN]);
		fillMatrixByFiguresTypeSet(result, allBlack[Figures.TYPE_KNIGHT]);
		fillMatrixByFiguresTypeSet(result, allBlack[Figures.TYPE_KING]);
		fillMatrixByFiguresTypeSet(result, allBlack[Figures.TYPE_OFFICER]);
		fillMatrixByFiguresTypeSet(result, allBlack[Figures.TYPE_CASTLE]);
		fillMatrixByFiguresTypeSet(result, allBlack[Figures.TYPE_QUEEN]);
		
		return result;
	}

	private void fillMatrixByFiguresTypeSet(int[][] result, IndexNumberSet figureIDs) {
		int size = figureIDs.getDataSize();
		int[] data = figureIDs.getData();
		for (int i=0; i<size; i++) {
			int figureID = data[i];
			if (figureID == Figures.DUMMY_FIGURE_ID) {
				throw new IllegalStateException();
			}
			int fieldID = fieldIDPerFigureID[figureID];
			int letter = Fields.LETTERS[fieldID];
			int digit = Fields.DIGITS[fieldID];
			result[letter][digit] = figureID;
		}
	}*/
	
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
		if (allWhiteBitboard != allByColour[Figures.COLOUR_WHITE]) {
			throw new IllegalStateException("allWhiteBitboard=" + allWhiteBitboard + ", allByColour[Figures.COLOUR_WHITE]=" + allByColour[Figures.COLOUR_WHITE]);
		}
		
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
		if (allBlackBitboard != allByColour[Figures.COLOUR_BLACK]) {
			throw new IllegalStateException("allBlackBitboard=" + allBlackBitboard + ", allByColour[Figures.COLOUR_BLACK]=" + allByColour[Figures.COLOUR_BLACK]);
		}
		
		//checkConsistency_DeathFigures();
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
		
		if (typeBitboard != allByColourAndType[colour][type]) {
			throw new IllegalStateException("typeBitboard=" + typeBitboard + " allByColourAndType[colour][type]=" + allByColourAndType[colour][type]);
		}
		
		return typeBitboard;
	}


	public BackupInfo[] getBackups() {
		return backupInfo;
	}
	
	public boolean hasSufficientMaterial() {

		if (materialFactor.getTotalFactor() > 12) { // 2w knights + 2b knights
			return true;
		}
		
		
		/**
		 * If has pawn - true
		 */
		long w_pawns = getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_PAWN);
		if (w_pawns != 0L) {
			return true;
		}
		long b_pawns = getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_PAWN);
		if (b_pawns != 0L) {
			return true;
		}
		
		/**
		 * If has queen - true
		 */
		long w_queens = getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_QUEEN);
		if (w_queens != 0L) {
			return true;
		}
		long b_queens = getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_QUEEN);
		if (b_queens != 0L) {
			return true;
		}

		/**
		 * If has rook - true
		 */
		long w_rooks = getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_CASTLE);
		if (w_rooks != 0L) {
			return true;
		}
		long b_rooks = getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_CASTLE);
		if (b_rooks != 0L) {
			return true;
		}
		
		int o1 = Utils.countBits(getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_OFFICER));
		int k1 = Utils.countBits(getFiguresBitboardByColourAndType(Figures.COLOUR_WHITE, Figures.TYPE_KNIGHT));
		
		int mi1 = o1 + k1;
		
		int o2 = Utils.countBits(getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_OFFICER));
		int k2 = Utils.countBits(getFiguresBitboardByColourAndType(Figures.COLOUR_BLACK, Figures.TYPE_KNIGHT));
		
		int mi2 = o2 + k2;

		if (mi1 <= 1 && mi2 <= 1) {
			return false;
		}
		
		if (o1 == 0 && o2 == 0) {
			return false;
		}
		
		return true;
	}
	
	public boolean isDraw50movesRule() {
		return lastCaptureOrPawnMoveBefore >= 100;
	}
	
	public int getDraw50movesRule() {
		return lastCaptureOrPawnMoveBefore;
	}
	
	public int getLastCaptrueFieldID() {
		return lastCaptureFieldID;
	}
	
	public final IGameStatus getStatus() {
		
		if (getStateRepetition() > 3) {
			throw new IllegalStateException();
		}
		
		int colourToMove = getColourToMove();
		//int kingID = Castling.KINGS_IDS_BY_COLOUR[colourToMove];
		
		if (getKingIndexSet(colourToMove).getDataSize() == 0) {
			throw new IllegalStateException();
		}
		
		if (getStateRepetition() >= 2) {
			//3 states repetition draw
			return IGameStatus.DRAW_3_STATES_REPETITION;
		}
		
		if (isInCheck()) {
			if (!hasMoveInCheck()) {
				//Mate
				if (colourToMove == Figures.COLOUR_WHITE) {
					return IGameStatus.MATE_BLACK_WIN;
				} else {
					return IGameStatus.MATE_WHITE_WIN;
				}
			}
		} else {
			if (!hasMoveInNonCheck()) {
				//Stale Mate
				if (colourToMove == Figures.COLOUR_WHITE) {
					return IGameStatus.STALEMATE_WHITE_NO_MOVES;
				} else {
					return IGameStatus.STALEMATE_BLACK_NO_MOVES;
				}
			}
		}
		
		if (!hasSufficientMaterial()) {
			return IGameStatus.NO_SUFFICIENT_MATERIAL;
		}
		
		if (isDraw50movesRule()) {
			return IGameStatus.DRAW_50_MOVES_RULE;
		}
		
		if (hasUnstoppablePasser(Figures.COLOUR_WHITE)) {
			return IGameStatus.PASSER_WHITE;
		} else if (hasUnstoppablePasser(Figures.COLOUR_BLACK)) {
			return IGameStatus.PASSER_BLACK;
		}
		
		if (pieces.getPieces(Constants.PID_W_PAWN).getDataSize() == 0) {//No pawns
			if (pieces.getPieces(Constants.PID_W_QUEEN).getDataSize() == 0 && pieces.getPieces(Constants.PID_W_ROOK).getDataSize() == 0) {
				if (pieces.getPieces(Constants.PID_W_BISHOP).getDataSize() == 0) {
					return IGameStatus.NO_SUFFICIENT_WHITE_MATERIAL;
				}
				if (pieces.getPieces(Constants.PID_W_KNIGHT).getDataSize() == 0 && pieces.getPieces(Constants.PID_W_BISHOP).getDataSize() == 1) {
					return IGameStatus.NO_SUFFICIENT_WHITE_MATERIAL;
				}
			}
		}
		
		if (pieces.getPieces(Constants.PID_B_PAWN).getDataSize() == 0) {//No pawns
			if (pieces.getPieces(Constants.PID_B_QUEEN).getDataSize() == 0 && pieces.getPieces(Constants.PID_B_ROOK).getDataSize() == 0) {
				if (pieces.getPieces(Constants.PID_B_BISHOP).getDataSize() == 0) {
					return IGameStatus.NO_SUFFICIENT_BLACK_MATERIAL;
				}
				if (pieces.getPieces(Constants.PID_B_KNIGHT).getDataSize() == 0 && pieces.getPieces(Constants.PID_B_BISHOP).getDataSize() == 1) {
					return IGameStatus.NO_SUFFICIENT_BLACK_MATERIAL;
				}
			}
		}
		
		return IGameStatus.NONE;
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
	public IMaterialFactor getMaterialFactor() {
		return materialFactor;
	}

	@Override
	public IMaterialState getMaterialState() {
		return materialState;
	}	
	
	//@Override
	public IMobility getMobility() {
		//return attackListener; it doesn't work correctly
		return null;
	}

	@Override
	public IBoardConfig getBoardConfig() {
		return boardConfig;
	}

	@Override
	public int[] getMatrix() {
		//return board;
		throw new UnsupportedOperationException();
	}
}

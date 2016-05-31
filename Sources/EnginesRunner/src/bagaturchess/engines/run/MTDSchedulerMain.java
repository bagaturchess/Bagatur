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


package bagaturchess.engines.run;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.BoardUtils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl1.Board3_Adapter;
import bagaturchess.engines.bagatur.eval.BagaturEvaluator;
import bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl;
import bagaturchess.engines.base.cfg.RootSearchConfig_BaseImpl_SMP;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.impl.env.SharedData;
import bagaturchess.search.impl.rootsearch.multipv.MultiPVMediator;
import bagaturchess.search.impl.rootsearch.multipv.MultiPVRootSearch;
import bagaturchess.search.impl.rootsearch.parallel.MTDParallelSearch;
import bagaturchess.search.impl.rootsearch.sequential.MTDSequentialSearch;
import bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_Base;
import bagaturchess.search.impl.uci_adaptor.UCISearchMediatorImpl_NormalSearch;
import bagaturchess.search.impl.uci_adaptor.timemanagement.controllers.TimeController_FixedDepth;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.uci.impl.commands.Go;


public class MTDSchedulerMain {
	
	
	public static void main(String[] args) {
		
		//IRootSearchConfig cfg = new RootSearchConfig_SMP();
		//IRootSearch search = new MTDParallelSearch(new Object[] {cfg, null});
		
		//IRootSearchConfig cfg = new RootSearchConfig_SingleCore_MTD();
		/*IRootSearchConfig cfg = new RootSearchConfig_BaseImpl(
				new String[] {"bagaturchess.search.impl.alg.SearchMTD",
								"bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD",
								"bagaturchess.learning.impl.eval.cfg.WeightsBoardConfigImpl",
								"bagaturchess.engines.evalmoves.EvaluationConfg"
								}
				);*/
		/*IRootSearchConfig cfg = new RootSearchConfig_BaseImpl(
				new String[] {"bagaturchess.search.impl.alg.SearchAB1",
								"bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_AB",
								"bagaturchess.learning.impl.eval.cfg.WeightsBoardConfigImpl",
								"bagaturchess.engines.learning.cfg.weights.EvaluationConfg"
								}
				);*/
		/*IRootSearchConfig cfg = new RootSearchConfig_BaseImpl(
				new String[] {"bagaturchess.search.impl.alg.impl1.SearchMTD1",
								"bagaturchess.engines.searchtune.SearchConfig1_MTD_Impl",
								"bagaturchess.learning.impl.eval.cfg.WeightsBoardConfigImpl",
								"bagaturchess.engines.evalmoves.EvaluationConfg"
								}
				);*/
				
		IRootSearchConfig cfg = new RootSearchConfig_BaseImpl_SMP(
				new String[] {
								"bagaturchess.search.impl.alg.impl0.SearchMTD0",
								//"bagaturchess.search.impl.alg.impl5_scratch.SearchMTD_AlphaBeta_pv_nonpv",
								//"bagaturchess.search.impl.alg.impl5_scratch.SearchMTD_MinMax",
								//"bagaturchess.search.impl.alg.impl5_scratch.SearchMTD_AlphaBeta",
								//"bagaturchess.search.impl.alg.impl_staticsearch.SearchMTD_Static",
								
								//"bagaturchess.engines.searchtune.SearchConfig1_MTD_Impl_LKG",
								//"bagaturchess.engines.bagatur.v110.SearchConfigImpl",
								"bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD",
								//"bagaturchess.engines.searchtune.SearchConfig1_MTD_Impl_LKG_AllInOne_Test",
								//"bagaturchess.engines.searchtune.SearchConfig1_MTD_Impl_INITIAL",
								
								//"bagaturchess.engines.learning.cfg.weights.boardtune.WeightsBoardConfig_LKG",
								//"bagaturchess.engines.learning.cfg.weights.evaltune.WeightsEvaluationConfig_LKG"
								
								//"bagaturchess.engines.bagatur.progressive.BagaturV12BoardConfig",
								//"bagaturchess.engines.bagatur.progressive.BagaturV12EvaluationConfig"
								
								//"bagaturchess.engines.material.MaterialBoardConfigImpl",
								//"bagaturchess.engines.material.MaterialEvalConfigImpl"
								
								"bagaturchess.engines.bagatur.cfg.board.BoardConfigImpl",
								"bagaturchess.engines.bagatur.cfg.eval.BagaturEvalConfigImpl_v2"
								
								//"bagaturchess.engines.bagatur.v14.EvaluationConfg13"
								
								//"bagaturchess.engines.learning.cfg.weights.WeightsBoardConfig",
								//"bagaturchess.engines.learning.cfg.weights.WeightsEvaluationConfig"
								
								
								//"bagaturchess.engines.bagatur.v13.DualEvaluationConfg"
								//"bagaturchess.engines.bagatur.v13_a.EvaluationConfg13"
								
								//"bagaturchess.engines.bagatur.v14.BoardConfigImpl",
								//"bagaturchess.engines.bagatur.v14.EvaluationConfg13"	
								
								}
				);
		
		ChannelManager.setChannel(new Channel_Console(System.in, System.out, System.out));
		
		SharedData arg1 = new SharedData(ChannelManager.getChannel(), cfg);
		
		IRootSearch search = new MTDParallelSearch(new Object[] {cfg, arg1});
		//IRootSearch search = new MTDSequentialSearch(new Object[] {cfg, arg1});
		IRootSearch searchMultiPV = new MultiPVRootSearch(cfg, search);
		
		SharedData sharedData = search.getSharedData();
		
		/*double c = 1.0643;
		//System.out.println("=" + Math.pow(c, 100));
		
		for (int i=0; i<100; i++) {
			
			System.out.println(i + "=" + Math.pow(c, i));
		}
		System.exit(0);*/
		
		//IBitBoard bitboard  = new Board("2r1r2k/1q3ppp/p2Rp3/2p1P3/6QB/p3P3/bP3PPP/3R2K1 w - - bm Bf6; id WAC.222");
		//IBitBoard bitboard  = new Board("2rr3k/pp3pp1/1nnqbN1p/3pN3/2pP4/2P3Q1/PPB4P/R4RK1 w - - bm Qg6");//Fast mate
		//IBitBoard bitboard  = new Board("7k/p6r/1p4Q1/7R/3p4/7P/P5P1/2q2K2 w - -");
		
		//IBitBoard bitboard  = new Board("8/k7/3p/p2P1p/P2P1P/8/8/K7 w - - 0 1");
		
		//IBitBoard bitboard  = new Board("rn1qkb1r/1p2pppp/p6/1NpnB3/Q1Pp2b1/3P1N2/PP2PPPP/R3KB1R b KQkq - 3 9", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("rn1qkb1r/1p2pppp/pn5/1Np1B3/Q1Pp2b1/3P1N2/PP2PPPP/R3KB1R w KQkq - 3 9", null, cfg.getBoardConfig());
		
		//1r5k/p1p4p/4p3/1rppb1Qq/3P2P1/1P3P2/P1P5/2KR4 w - - 0 25 MATE IN 7, extracted from a game with cuckoo chess
		//3r2k1/1p3p1p/pqb2n2/4rN2/2Pp2p1/3B4/PP1Q1PPP/2RR2K1 b - - 3 24 MATE IN 7 - white wins
		//r1b2rk1/4qppp/p7/2p1PpB1/P3p3/6Q1/1PP3PP/2KR3R b - - 1 21 - f7-f6 is the move, Qe7-e6 is bad move - forced material loss
		//r1b2rk1/5ppp/p3q3/2p1PpB1/P3p3/6Q1/1PP3PP/2KR3R w - - 2 22 - Bg5-f6 is the move, material loss for black
		//r4rk1/pp3ppp/2pq4/3p4/4nPb1/2BBP3/PPP3PP/R3QRK1 w - - 2 15 Bd3xe4 is the correct move +3.00
		//2r1n2r/1q4k1/2p1pn2/ppR4p/4PNbP/P1BBQ3/1P4P1/R5K1 b - - 1 32 correct move is Kg7f7, the position is used for testing captures extensions
		//5k2/4r1p1/4r3/8/1Q6/P7/2PP4/1K6 w - - 0 1  the position is used for testing passer push extensions
		//1r6/4k3/1pbpPb1p/p1p2P2/2Pp3p/3P3P/2P2RP1/R6K b - - 3 39, evaluation of Stockfish is +4, but Bagatur doesn't find why
		//8/3k4/8/8/7n/7P/6K1/8 w - - 3 39; pawn vs knight is not with eval 0
		
		//IBitBoard bitboard  = new Board("1r6/4k3/1pbpPb1p/p1p2P2/2Pp3p/3P3P/2P2RP1/R6K b - - 3 39", null/*sharedData.getAndRemovePawnsCache()*/, cfg.getBoardConfig());
		
		//IBitBoard bitboard = new Board(Constants.INITIAL_BOARD, null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("rn1b2rk/1pp3p1/qp1p2R1/5Q2/3RN2P/1PP5/3PbP2/4K3 w - -", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - bm Rxb2", null, cfg.getBoardConfig());
		IBitBoard bitboard  = new Board("5r2/1p1RRrk1/4Qq1p/1PP3p1/8/4B3/1b3P1P/6K1 w - - bm Qxf7+ Rxf7+; id WAC.235", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("7k/6r1/8/8/8/8/1Q6/K7 w - -", null, cfg.getBoardConfig()); //Queen and King vs. Rook and King
		//IBitBoard bitboard  = new Board("k7/1q6/8/8/8/7R/8/6K1 b - - ", null, cfg.getBoardConfig());
				
		//IBitBoard bitboard  = new Board("8/7k/7P/6K1/2B5/8/8/8 b - - 19 82", null, cfg.getBoardConfig());//Too big eval of bishop and pawn
		//IBitBoard bitboard  = new Board3_Adapter("1r2r1k1/5pp1/p2p4/2q1pNP1/b3P3/nP4Q1/PKP1R1B1/3R4 b - - 1 29", sharedData.getPawnsCache(), cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board3_Adapter("3r1rk1/1p2npp1/1qpbpn1p/p2p4/1P1P2P1/P1N1PQ2/1BP2P1P/1B2RRK1 w - - 0 21", sharedData.getPawnsCache(), cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board3_Adapter("4r2k/pp2r1pp/1qp1N3/3P1Q2/8/7P/PPP3P1/5R1K b - - 3 32", sharedData.getPawnsCache(), cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board3_Adapter("r4rk1/ppp1q2p/3bp1p1/5p2/2BPb3/2P5/PP1NQPPP/4RRK1 b - - 6 19", sharedData.getPawnsCache(), cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board3_Adapter("r1bq1rk1/p1p2ppp/2p5/8/1b2Q3/5N2/PPPP1PPP/R1B1K2R b KQ - 3 10", sharedData.getPawnsCache(), cfg.getBoardConfig());
		
		//IBitBoard bitboard  = new Board3_Adapter("r7/Nbp2kp1/1p1bpp2/1P1p2q1/Q2P4/4RBPp/1PP1PP1P/6K1 b - - 5 26 ", sharedData.getPawnsCache(), cfg.getBoardConfig()); //e6-e5 is the best move
		
		//IBitBoard bitboard  = new Board("3k4/8/8/8/8/8/3P4/3K4 w - -", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("3k4/3p4/8/8/8/8/8/3K4 w - -", null, cfg.getBoardConfig());
		
		//IBitBoard bitboard  = new Board("4k3/8/8/8/8/8/3R4/3K4 w - -", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("4k3/8/8/8/8/8/3R4/3K4 b - -", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("4k3/4r3/8/8/8/8/8/3K4 w - -", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("4k3/4r3/8/8/8/8/8/3K4 b - -", null, cfg.getBoardConfig());
		
		//IBitBoard bitboard  = new Board("4k3/8/8/8/8/8/3Q4/3K4 w - -", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("4k3/8/8/8/8/8/3Q4/3K4 b - -", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("4k3/4q3/8/8/8/8/8/3K4 w - -", null, cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("4k3/4q3/8/8/8/8/8/3K4 b - -", null, cfg.getBoardConfig());
		
		//IBitBoard bitboard  = new Board("rn2kb1r/p3pppp/1q2b3/1p2P1N1/2pP2n1/1PN5/5PPP/R1BQKB1R b KQkq - 2 10 ", sharedData.getPawnsCache(), cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("B7/8/4n3/p1p1kp2/P2p3p/1PP2P2/3K3P/8 w - - 0 49 ", sharedData.getPawnsCache(), cfg.getBoardConfig());
		
		/*
		BoardUtils.playGame(bitboard, "");
		IEvaluator evaluator = sharedData.getEvaluatorFactory().create(bitboard, search.getSharedData().getAndRemoveEvalCache(), cfg.getEvalConfig());
		evaluator.fullEval(-1, -1000000, 1000000, Constants.COLOUR_WHITE);
		System.out.println(bitboard);
		System.out.println(((BagaturEvaluator)evaluator).dump(bitboard.getColourToMove()));
		System.exit(0);
		*/
		
		//IBitBoard bitboard  = new Board("1k1rr3/ppp2ppp/4b3/3P4/4P2q/B1P5/P1PQ2PP/1R2R1K1 w - - 3 18"); //kingsafety - black is bad.
		//IBitBoard bitboard  = new Board("rnb1r3/2k2Npp/p4n2/8/1b6/1Bp1P3/PP3PPP/R1B2RK1 w - - 6 16"); 
		//IBitBoard bitboard  = new Board("7k/bP5P/P3p1B1/4p1p1/8/5P1K/8/8 w - - 31 86", sharedData.getPawnsCache(), cfg.getBoardConfig()); // White king should go to passers 
		//IBitBoard bitboard  = new Board("8/4k3/8/4b3/8/8/7p/7K b - - 35 170"); 
		//IBitBoard bitboard  = new Board("r4rk1/ppp1qppp/2n1p3/2b1pb2/2P5/NQ3N2/PP1B1PPP/2KR1B1R w - - 6 13", sharedData.getPawnsCache(), cfg.getBoardConfig());
		//IBitBoard bitboard  = new Board("6k1/2p4p/3bpppB/prp5/P6P/2PP1P2/2PK1P2/8 w - - 4 24");
		//IBitBoard bitboard  = new Board("6k1/prb2p1p/R4Qp1/3p4/3pr3/1P4Pq/2P1NP2/4R1K1 w - - 9 28");
		//IBitBoard bitboard  = new Board("6k1/1pp3p1/p1p1p1r1/3n1r2/3R1P2/P2P3K/1PP2P2/R1B5 w - - 12 30", sharedData.getPawnsCache(), cfg.getBoardConfig());// -M4!!!!!!!!!!!!!!!
		//IBitBoard bitboard  = new Board("8/1kp5/4n1R1/6P1/2b4p/1p3P2/5K1P/8 w - - 0 49"); 
		
		//IBitBoard bitboard  = new Board("3R4/4pk2/1p3b2/1r1p3p/p7/2P5/rP3PPP/2B1R1K1 w - - 4 36");
		
		//IBitBoard bitboard  = new Board("4r1k1/1p2P1p1/2pq3p/1p2R2P/1P1p2P1/P7/2PKQ3/8 b - - 14 47");
		
		//IBitBoard bitboard  = new Board("4rbk1/3n1ppp/2N5/NP1p4/5B2/3b4/P4PPP/2R3K1 w - - 3 33"); 
		
		//IBitBoard bitboard  = new Board("2rk4/5R2/p3p3/1P2b3/8/BP4P1/P2pp1KP/8 w - - 4 44"); 
		
		//IBitBoard bitboard  = new Board("r1b3k1/pppr3p/2n2Qp1/5p2/2P1N3/2B1P3/q4PPP/1R3RK1 b - - 9 23");//BAD for black
		
		//depth 10 seldepth 36 score cp -646 time 1593 nodes 1421708 pv g5h6 e7f6 b5c6 h3h4 f1f2 f6f2 h6g7 f2d4 g7g8 h2g3 g8f7 d4h8 h7h5 h8h7 f7f6 g4h5
		//IBitBoard bitboard  = new Board("8/1p2Q2p/p5p1/1b1p2k1/6P1/1P5P/2P2P1K/5q2 b - - 10 46");//Bad for black -700
		
		//IBitBoard bitboard  = new Board("8/pkp5/1p1n2K1/1P1P4/PR1NpN1P/4n1r1/4P3/8 w - - 10 59"); 
		//IBitBoard bitboard  = new Board("7k/Q1pn2pp/2p5/P3pr2/6r1/3PBqP1/1PP2P1K/1R2R3 w - - 5 31");//Prunnings make shits
		//IBitBoard bitboard  = new Board("3k4/1pprp2p/p3bp2/1N6/8/P2B2P1///1PPb1P1P/1K5R w - - 0 24"); 
		
		//IBitBoard bitboard  = new Board("5rk1/pp4p1/2n1p2p/2Npq3/2p5/6P1/P3P1BP/R4Q1K w - - bm Qxf8+; id ");
		//IBitBoard bitboard  = new Board("6k1/5p2/p1p5/1p6/P1p1PKp1/2P2n2/B7/1RB3r1 b - - 1 39");
		//IBitBoard bitboard  = new Board("2r5/1p5k/1q2N1Np/2p5/4Q1P1/p2P3P/1b3P2/2R3K1 b - - 5 35");
		
		//IBitBoard bitboard  = new Board("2r1k1r1/1p3p1p/p2bp1pQ/7q/2Pp4/3P4/PP3PPP/R1B1R1K1 w - - 6 26"); 
		//IBitBoard bitboard  = new Board("2r1k1r1/1p3p1p/p2bp1pB/8/2Pp4/3P4/PP3PPP/R3R1K1 b - - 9 27");
		//IBitBoard bitboard  = new Board("r5k1/p1p2ppp/1rn1p3/4pb2/Q1Pq4/N7/PP1N1PPP/2KR1B1R w - - 18 19");
		//IBitBoard bitboard  = new Board("1r1r2k1/2p2ppp/p3p3/2P1pb2/1n5P/Q1N5/PP1N1qP1/2KR1B1R w - - 1 24"); 
		//IBitBoard bitboard  = new Board("6k1/1pp3p1/p1p1p1r1/3n1r2/3R1P2/P2P3K/1PP2P2/R1B5 w - - 12 30");// -M4!!!!!!!!!!!!!!!
		
		//IBitBoard bitboard  = new Board("r4q1k/p2bR1rp/2p2Q1N/5p2/5p2/2P5/PP3PPP/R5K1 w - - bm Rf7;");
		//IBitBoard bitboard  = new Board("7k/4b1p1/R7/1P3p2/5Q1p/2PqBP1K/1P3P1P/6r1 w - - 6 35"); 
		//IBitBoard bitboard  = new Board("7k/4b1p1/p7/1P1q1p2/5Q1p/2P2P1K/1P3P1P/R1B1r3 w - - 2 33"); 
		//IBitBoard bitboard  = new Board("7k/4b1p1/R7/1P1q1p2/5Q1p/2P2P1K/1P3P1P/2B3r1 w - - 4 34");
		
		//IBitBoard bitboard  = new Board("4r2k/5qp1/p2b4/1p3pQp/P7/2P2P2/1P3P1P/R1B3K1 w - - 3 29");//King safety test, the right move is Bc1-e3
		//IBitBoard bitboard  = new Board("7k/5qp1/p2b4/1P3pQp/8/2P2P2/1P3PKP/R1B1r3 b - - 2 30"); 
		
		//IBitBoard bitboard  = new Board("6k1/3R2p1/5pKp/1pB5/1P4P1/7r/8/8 b - - 2 57 "); //-M2
		//IBitBoard bitboard  = new Board("8/8/p7/P7/4P3/1kp5/4K3/8 w - - 1 82"); //Unstoppable passers problem?
		//IBitBoard bitboard  = new Board("1k4r1/pp3p1p/8/2pp3q/2n1r3/PQB1P1PP/1PP2P2/3R1RK1 w - - 1 24"); 
		
		//IBitBoard bitboard  = new Board("r3k2r/p1n3p1/4p3/R4p2/2N4p/1nP2N1P/1P3KP1/3R4 w kq - 10 30"); 
		//IBitBoard bitboard  = new Board("r6r/p1n5/3Npkp1/5p2/7p/1nP2N1P/1P3KP1/3RR3 w - - 2 33");
		
		
		
		//IBitBoard bitboard  = new Board("8/1p6/p7/1P6/P2K4/2P5/1k6/8 b - - 0 55"); 
		//IBitBoard bitboard  = new Board("8/4k3/8/6p1/6B1/4K3/8/8 w - - 4 67");
		//IBitBoard bitboard  = new Board(Constants.INITIAL_BOARD, sharedData.getPawnsCache(), cfg.getBoardConfig());
		
		//IBitBoard bitboard  = new Board("2rq1rk1/pp3ppp/2n2b2/4NR2/3P4/PB5Q/1P4PP/3R2K1 w"); 
		//IBitBoard bitboard  = new Board("4r1k1/4bpp1/8/p2pP2p/r1pBb3/2P2N1P/1P3PP1/2KRR3 w - - 1 30 ");
		//IBitBoard bitboard  = new Board("r3kb1r/p1p3pp/5p2/2p1pb2/2N5/2P1BP2/PPP3PP/R1K3R1 b kq - 2 15");
		//IBitBoard bitboard  = new Board("r3kb1r/p1p3pp/5p2/2p1pb2/2N5/2P1BP2/PPP1K1PP/R5R1 b kq - 2 15");
		//IBitBoard bitboard  = new Board("r3kb1r/p1p3pp/5p2/2p1pb2/2N5/2P1BP2/PPP3PP/R2K2R1 w kq - 1 15"); 
		//IBitBoard bitboard  = new Board("r3kb1r/pp1b1ppp/nq2pn2/1N1p4/Q4B2/4P3/PPP2PPP/2KR1BNR w kq - 4 9");
		
		/*
		2kr3r/pp1q1ppp/5n2/1Nb5/2Pp1B2/7Q/P4PPP/1R3RK1 w - - bm Nxa7+; id "WAC.071";
		r4rk1/p1B1bpp1/1p2pn1p/8/2PP4/3B1P2/qP2QP1P/3R1RK1 w - - bm Ra1; id "WAC.080";
		2qr2k1/4b1p1/2p2p1p/1pP1p3/p2nP3/PbQNB1PP/1P3PK1/4RB2 b - - bm Be6; id "WAC.091";
		r4rk1/1p2ppbp/p2pbnp1/q7/3BPPP1/2N2B2/PPP4P/R2Q1RK1 b - - bm Bxg4; id "WAC.092";
		8/k1b5/P4p2/1Pp2p1p/K1P2P1P/8/3B4/8 w - - bm b6+; id "WAC.100";
		r1bq4/1p4kp/3p1n2/p4pB1/2pQ4/8/1P4PP/4RRK1 w - - bm Re8; id "WAC.145";
		1br2rk1/1pqb1ppp/p3pn2/8/1P6/P1N1PN1P/1B3PP1/1QRR2K1 w - - bm Ne4; id "WAC.152";
		5rk1/2p4p/2p4r/3P4/4p1b1/1Q2NqPp/PP3P1K/R4R2 b - - bm Qg2+; id "WAC.163";
		r1q2rk1/p3bppb/3p1n1p/2nPp3/1p2P1P1/6NP/PP2QPB1/R1BNK2R b KQ - bm Nxd5; id "WAC.180";
		1r2k1r1/5p2/b3p3/1p2b1B1/3p3P/3B4/PP2KP2/2R3R1 w - - bm Bf6; id "WAC.183";
		rr4k1/p1pq2pp/Q1n1pn2/2bpp3/4P3/2PP1NN1/PP3PPP/R1B1K2R b KQ - bm Nb4; id "WAC.196";
		2r1r2k/1q3ppp/p2Rp3/2p1P3/6QB/p3P3/bP3PPP/3R2K1 w - - bm Bf6; id "WAC.222";
		8/8/8/1p5r/p1p1k1pN/P2pBpP1/1P1K1P2/8 b - - bm Rxh4 b4; id "WAC.229";
		2b5/1r6/2kBp1p1/p2pP1P1/2pP4/1pP3K1/1R3P2/8 b - - bm Rb4; id "WAC.230";
		5r2/1p1RRrk1/4Qq1p/1PP3p1/8/4B3/1b3P1P/6K1 w - - bm Qxf7+ Rxf7+; id "WAC.235";
		8/6k1/5pp1/Q6p/5P2/6PK/P4q1P/8 b - - bm Qf1+; id "WAC.239";
		2rq1rk1/pp3ppp/2n2b2/4NR2/3P4/PB5Q/1P4PP/3R2K1 w - - bm Qxh7+; id "WAC.241";
		1r3r1k/3p4/1p1Nn1R1/4Pp1q/pP3P1p/P7/5Q1P/6RK w - - bm Qe2; id "WAC.243";
		2k1r3/1p2Bq2/p2Qp3/Pb1p1p1P/2pP1P2/2P5/2P2KP1/1R6 w - - bm Rxb5; id "WAC.247";
		1rb1r1k1/p1p2ppp/5n2/2pP4/5P2/2QB4/qNP3PP/2KRB2R b - - bm Re2; id "WAC.252";
		3r1rk1/1pb1qp1p/2p3p1/p7/P2Np2R/1P5P/1BP2PP1/3Q1BK1 w - - bm Nf5; id "WAC.256";
		r2r2k1/1R2qp2/p5pp/2P5/b1PN1b2/P7/1Q3PPP/1B1R2K1 b - - bm Rab8; id "WAC.264";
		2r1r1k1/pp1q1ppp/3p1b2/3P4/3Q4/5N2/PP2RPPP/4R1K1 w - - bm Qg4; id "WAC.270";
		1nbq1r1k/3rbp1p/p1p1pp1Q/1p6/P1pPN3/5NP1/1P2PPBP/R4RK1 w - - bm Nfg5; id "WAC.293";
		3r1rk1/p3qp1p/2bb2p1/2p5/3P4/1P6/PBQN1PPP/2R2RK1 b - - bm Bxg2 Bxh2+; id "WAC.297";
		 */
		
		//bitboard.makeNullMoveForward();
		//bitboard  = new Board(bitboard.toEPD());
		
		//IBitBoard bitboard  = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - bm Rxb2");
		//IBitBoard bitboard  = new Board("r7/2p1Qpk1/2p1p1p1/4P2p/rpq5/3R3P/2P2PP1/3R2K1 w - - 6 48"); 
		//IBitBoard bitboard  = new Board("r2r2k1/2p1qp2/p1b1p1pp/npN5/3P2BP/2P3Q1/P1P2PP1/1R2R1K1 w - - 1 25");
		//IBitBoard bitboard  = new Board("r2r1bk1/p4ppp/1p6/n2NP3/P1P2B2/3B1N2/1R3PPP/n5K1 w - - 2 22");
		//IBitBoard bitboard  = new Board("rnbqkb1r/ppp1pppp/3p1n2/8/3P4/2N1P3/PPP2PPP/R1BQKBNR b KQkq d3 0 3");
		
		//IBitBoard bitboard  = new Board("5rk1/1ppb3p/p1pb4/6q1/3P1p1r/2P1R2P/PP1BQ1P1/5RKN w - - bm Rg3");
		
		//IBitBoard bitboard  = new Board("r4rk1/ppp1qppp/2n1p3/2b1pb2/2P5/NQ3N2/PP1B1PPP/2KR1B1R w - - 6 13");
		//IBitBoard bitboard  = new Board("r3r1k1/pQp2ppp/2nb4/3p4/3Pn2q/P1NBPP2/1P3P1P/R1B2R1K w - - 3 14");
		
		//IBitBoard bitboard  = new Board("r2qr1k1/ppp2ppp/2nb4/3p4/3Pn1b1/PQNBPN2/1P3PPP/R1B2RK1 w - - 5 11");
		//IBitBoard bitboard  = new Board("r4rk1/pQp1bppp/5n2/1N1p4/2Pp4/3P2Pq/PP1BPP2/R4RK1 w - - 0 15");
		
		//IBitBoard bitboard  = new Board("5b2/5P2/2k1p3/p7/1PK3p1/8/8/4B3 b - - 0 56"); 
		
		//IBitBoard bitboard  = new Board("2rr3k/pp3pp1/1nnqbN1p/3pN3/2pP4/2P3Q1/PPB4P/R4RK1 w - - bm Qg6");
		//IBitBoard bitboard  = new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
		//IBitBoard bitboard  = new Board("5rk1/1pN5/1P1p2p1/3P3p/4p2P/4QpPq/3b4/6K1 w - - 98 49");
		//IBitBoard bitboard  = new Board("2K5/2Q5/8/8/8/kq6/8/8 b - - 18 64 ");
		//IBitBoard bitboard  = new Board("2r1r1k1/pp1bbp1p/3q2p1/3Pp3/8/1P1BB3/P2QRPPP/5RK1 w - - 1 19 "); // bishop on a7
		//IBitBoard bitboard  = new Board("3r1rk1/p4p2/1p3ppQ/4pB2/1P1pP3/P4qP1/5P2/R1R3K1 w - - 0 30");//big material lost
		//IBitBoard bitboard  = new Board("3r1rk1/p3qp1p/2bb2p1/2p5/3P4/1P6/PBQN1PPP/2R2RK1 b");
		//IBitBoard bitboard  = new Board("rnbqkb1r/pppppppp/8/4P3/1nP5/8/PP1P1PPP/RNBQKBNR w KQkq - 1 4 ");
		//IBitBoard bitboard  = new Board("2k5/nppb3/8/8/8/8/3NPPB1/3K4 b - - ");
		//IBitBoard bitboard  = new Board("r1b3k1/pp1rbpp1/q3p2p/P1p5/2PP1Bn1/3Q1NP1/1P3PBP/R3R1K1 b - - 5 19  c5xd4 instead of e7f6");
		//1r1q1rk1/1b1pbppp/1p2p3/p3P3/1nP5/1PN1B3/1P1QBPPP/R2R2K1 w - a6 0 17 should move Nc3b5
		
		//IBitBoard bitboard  = new Board("r1bqkbnr/pp2pppp/2np4/3P4/3P4/8/PP3PPP/RNBQKBNR w KQkq - 10 5"); //test enpas bug
		//IBitBoard bitboard  = new Board("4r1k1/p1qr1p2/2pb1Bp1/1p5p/3P1n1R/1B3P2/PP3PK1/2Q4R w - - bm Qxf4");
		//IBitBoard bitboard  = new Board("8/8/3k4/2pp4/8/3K1N2/8/8 w - - 12 57 ");
		//IBitBoard bitboard  = new Board("1r2n1k1/5pp1/ppQ5/7p/3PP3/7P/PP4P1/7K w - - 2 31");
		//IBitBoard bitboard  = new Board("3q3r/3pbQ2/kpb1p3/p1p1P3/2P3Np/PP2B2P/1P5K/6R1 w - - 20 45");
		//2kr1b1r/ppp1q1p1/2b1pp2/P3P3/1P1PpB2/2P4p/4NPPP/R2Q1RK1 w - - 0 15  king safety
		//IBitBoard bitboard  = new Board("3q3r/3pbQ2/kpb1p3/p1p1P3/2P3Np/PP2B2P/1P5K/6R1 w - - 20 45");//  officer on h6 instead of knight to h6
		//IBitBoard bitboard  = new Board("1k5r/pp2Np1p/4p1pb/2p1Pb2/2P5/P4N2/KPn1BPPP/7R b - - 1 19");//Bf5e4/g4? is about to +200
		//IBitBoard bitboard  = new Board("r2q1rk1/1pp2ppp/p1n1pn2/3p4/1b1P4/2N1PN2/PPPB1PPP/R2Q1RK1 w - - 6 10 ");
		//IBitBoard bitboard  = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - bm Rxb2; id");
		//IBitBoard bitboard  = new Board("8/7p/8/1pk3p1/p5P1/P1K4P/8/8 w - - 3 52 ");//Should be +10 in 18 moves
		//IBitBoard bitboard  = new Board("2r5/2n5/P6p/1P6/3B2k1/3K1R2/8/8 w - - 10 67"); //Safe the rook instead of pushing passers
		//IBitBoard bitboard  = new Board("8/5p2/5k1p/2Q2q1P/6p1/6P1/7K/8 w - - 8 57");//Qc5xf5 lose the game 
		//IBitBoard bitboard  = new Board("8/5p1p/5P1k/p4b2/8/2q3P1/K2R1R1P/8 w - a6 0 48");
		//IBitBoard bitboard  = new Board("r2qk1nr/ppp2ppp/2nbp3/3p4/3P4/1P3P2/PBPN1PPP/R2QKB1R w KQkq - 1 7");
		
		//IBitBoard bitboard  = new Board("2kr3r/pp1q1ppp/5n2/1Nb5/2Pp1B2/7Q/P4PPP/1R3RK1 w - - bm Nxa7+; id WAC.071");
		//IBitBoard bitboard  = new Board("5r2/1p1RRrk1/4Qq1p/1PP3p1/8/4B3/1b3P1P/6K1 w - - bm Qxf7+ Rxf7+; id WAC.235");
		//IBitBoard bitboard  = new Board("5r1k/p1R3pp/1p6/3P1b2/2P1n2n/P7/4N1KP/R7 w - - 17 38");
		//IBitBoard bitboard  = new Board("8/2R5/8/8/4p2p/3b1pnk/5B2/4K3 w - - 12 72");
		//IBitBoard bitboard  = new Board("rnbqkbnr/pppp1ppp/8/8/3pP3/8/PPP2PPP/RNBQKBNR w KQkq - 0 3 "); 
		//IBitBoard bitboard  = new Board("rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 1");
		//IBitBoard bitboard  = new Board("r2q2k1/p1p2rpp/1p1b4/P2Pnp2/1PP1p2n/1Q5P/2BBNPP1/R3R1K1 w - - 3 29");
		//IBitBoard bitboard  = new Board("2kr4/1pp2pp1/pn4p1/6P1/1n2N3/4PP2/1B2B2P/2R3K1 b - - bm Nd3");
		//IBitBoard bitboard  = new Board("1r4k1/3r1pbp/6p1/4p1Bn/2B1P3/5P2/1PP2QPP/qNK4R w - - 4 27");//Rook is with less eval than bishop and 2 pawns which is bed
		//IBitBoard bitboard  = new Board("2b5/1r6/2kBp1p1/p2pP1P1/2pP4/1pP3K1/1R3P2/8 b - - bm Rb4; id WAC.230");
		//IBitBoard bitboard  = new Board("8/8/8/1p5r/p1p1k1pN/P2pBpP1/1P1K1P2/8 b - - bm Rxh4 b4; id WAC.229");
		
		//IBitBoard bitboard  = new Board("r3k2r/p2p1ppp/bqn1p3/1N1n4/PbQP4/1P3N2/3B1PPP/R3KB1R w KQkq - bm Bd2xb4");
		//IBitBoard bitboard  = new Board("r2qkbnr/1pp1pppp/p1n1b3/1B1p4/3P1B2/4P3/PPP2PPP/RN1QK1NR w KQkq - bm Bd3");
		
		//IBitBoard bitboard  = new Board("r3k2r/p2p1ppp/bqn1p3/1N1n4/PbQP4/1P3N2/3B1PPP/R3KB1R w KQkq - bm Bd4");// White should not make Castle Queen Side
		//IBitBoard bitboard  = new Board("r1b2rk1/pp1p4/nn2p2p/4Ppp1/1R6/2P1BB2/P1P1NPPP/5RK1 w - - 6 20");
		//IBitBoard bitboard  = new Board("2Q3bk/8/P1R3pp/r3P3/3P4/1R5P/q7/1K6 w - - 13 65 ");
		
		//IBitBoard bitboard  = new Board("6k1/p1r1qp2/1p1p2pp/2n1p3/r3P1B1/P1P1R2P/1QP2PP1/4R1K1 w - - 0 30"); 
		//IBitBoard bitboard  = new Board("r1b1k2r/pp1p1ppp/2n5/q3p3/1b2n3/P1N1PNB1/1PPQ1PPP/R3KB1R w KQkq - 1 10"); 
		//IBitBoard bitboard  = new Board("r1b1k2r/pp1p1ppp/2n5/q3p3/1b2n3/P1N1PNB1/1PPQ1PPP/R3KB1R w KQkq - bm a3xb4");
		//IBitBoard bitboard  = new Board("r2qkbnr/1pp1pppp/p1n1b3/1B1p4/3P1B2/4P3/PPP2PPP/RN1QK1NR w KQkq - bm Bd3"); //Rc1xc6 lose the game
		//IBitBoard bitboard  = new Board("r1r3k1/p2p1ppp/1p2pb2/3q4/2bP1B2/1PP1Q3/P4PPP/R3R1K1 w - - 14 21"); 
		
		//IBitBoard bitboard  = new Board("r1r3k1/p2p1ppp/bp2pb2/3q4/3P1B2/1PP1Q3/P4PPP/1R2R1K1 w - - 16 22");
		//BoardUtils.playGame(bitboard, "e3-d2, f6-h4, e1-e5, d5-d6, d2-c2");//, Bh4xf2, Kg1xf2");
		
		//IBitBoard bitboard  = new Board("6r1/1p2pk1p/3p1p1b/1N2n3/5P1r/P3B1Nb/1PP2R1P/1K1R4 w - - 9 25 //f4xe5 instead of Rd4"); 
		
		//IBitBoard bitboard  = new Board("r1b1kb1r/pp1p1pp1/1qn1p3/3n3p/3P3P/2PB2B1/PP3PP1/RN1QK1NR w KQkq - 1 9");
		//IBitBoard bitboard  = new Board("r1b1kb1r/pp1p1pp1/1qn1p3/3n3p/3P3P/2PB2B1/PPQ2PP1/RN2K1NR b KQkq - 2 9"); 
		//IBitBoard bitboard  = new Board("r1b1kb1r/pp1p1pp1/1qn1p3/7p/1n1P3P/2PB2B1/PPQ2PP1/RN2K1NR w KQkq - 3 10");
		//IBitBoard bitboard  = new Board("r3r1k1/1Qpn3p/p5p1/2b1qp2/4B3/P4P2/1PPB1P1P/R3K2R w KQ f6 0 20"); 
		
		//IBitBoard bitboard  = new Board("r4rk1/p1Q2ppp/bbp1pn2/q2p4/5B2/4PN2/PPPN1PPP/R3K2R w KQ - 10 12");//Queen sacrifice for shits
		//IBitBoard bitboard  = new Board("r4rk1/p4ppp/bQp1pn2/q2p4/5B2/4PN2/PPPN1PPP/R3K2R b KQ - 11 12");
		//BoardUtils.playGame(bitboard, "a7xb6, f3-d4");
		//BoardUtils.playGame(bitboard, "a7xb6, f3-d4, a5xd2, e1xd2, f6-g4, f4-g3, c6-c5, d4-c6, a6-b5, c6-e7, g8-h8");
		
		//IBitBoard bitboard  = new Board("r4rk1/pb1pbppp/4p3/1p2P3/3nQ3/5N2/PPPR1qPP/2K2B1R w - - 8 15"); 
		//IBitBoard bitboard  = new Board("2kr1b1r/pp4pp/4pn2/q7/n1p2P2/2N2B2/PPPBQ1PP/R4R1K b - - 5 17");
		
		//IBitBoard bitboard  = new Board("8/3p3p/6p1/2kp1p2/8/P2R3P/r4PP1/4K3 w - - 4 35"); 
		//IBitBoard bitboard  = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - bm Rxb2; id ");
		//IBitBoard bitboard  = new Board("rnbqkb1r/ppp2ppp/4p3/3pP3/4n3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 1 5"); 
		//IBitBoard bitboard  = new Board("8/4q1kp/2Q3p1/8/3br3/1p3PP1/7P/3R2NK b - - 1 42 "); 
		
		//IBitBoard bitboard  = new Board("5rk1/8/2Q3PR/1p1p1q2/2p2P2/pnP3P1/5B1K/8 b - - 1 50");
		
		//IBitBoard bitboard  = new Board("8/5pkp/4r1p1/1q6/2pP2P1/4PQ2/1P5P/R2K4 w - - 2 36"); 
		//IBitBoard bitboard  = new Board("8/3q1pkp/4r1p1/8/2pP2P1/4PQ2/1P2K2P/R7 w - - 0 35");
		
		//IBitBoard bitboard  = new Board("r5k1/3q1ppp/2p2n2/3pb3/7Q/2N1P3/2PB1PPP/1R4K1 w - - 3 23"); 
		
		//IBitBoard bitboard  = new Board("4r3/pppR1pk1/4q3/6p1/8/2P1P1P1/PP2Q2P/6K1 w - - 5 32"); //Best move is Qe2-d3
		//IBitBoard bitboard  = new Board("8/8/3r2k1/p7/P2r3p/7P/3pR1PK/3R4 w - - 11 48 "); // //best moveis g4 or g3
		//IBitBoard bitboard  = new Board("8/8/3r2k1/p7/P6p/7P/3R2PK/8 b - - 14 7");
		
		//IBitBoard bitboard  = new Board("2rr3k/pp3pp1/1nnqbN1p/3pN3/2pP4/2P3Q1/PPB4P/R4RK1 w - - bm Qg6");
		//IBitBoard bitboard  = new Board("5b2/8/p1p1p1p1/4pk1p/PPK4P/2P2PP1/7B/8 w - - 1 56"); 
		//IBitBoard bitboard  = new Board("k7/p4p2/P1q1b1p1/3p3p/3Q4/7P/5PP1/1R4K1 w - - bm Qe5 Qf4; id ");
		//IBitBoard bitboard  = new Board("1r2kbR1/3p1p2/4p3/p3P2r/2p1PB2/2P5/P1PR4/5K2 w - - 4 33");
		
		//IBitBoard bitboard  = new Board("2rr3k/pp3pp1/1nnqbN1p/3pN3/2pP4/2P3Q1/PPB4P/R4RK1 w - - bm Qg6; id ");
		
		//IBitBoard bitboard  = new Board("2b3k1/4rrpp/p2p4/2pP2RQ/1pP1Pp1N/1P3P1P/1q6/6RK w - - bm Qxh7+; id ");
		
		//IBitBoard bitboard  = new Board("3r2k1/1p3ppp/2pq4/p1n5/P6P/1P6/1PB2QP1/1K2R3 w - - am Rd1; id ");
		
		//IBitBoard bitboard  = new Board("6k1/6P1/8/8/3B3/8/1K6/8 w - - am ");
		//IBitBoard bitboard  = new Board("2rqkb1Q/p3pp1p/6p1/8/8/4B3/PPP2PbP/R3K2R w KQ - 2 15");
		
		//IBitBoard bitboard  = new Board("r2q1rk1/1pp2pp1/p1n2n1p/bN1pp3/PP1P4/2P1Pb2/4BPPP/R1BQ1RK1 w - - 1 13"); 
		
		//IBitBoard bitboard  = new Board();
		//IBitBoard bitboard  = new Board("r3kb1r/1pp2pp1/p1nqpn2/3p3p/3P2b1/2N1PN1P/PPP1BPP1/R1BQ1RK1 w kq h6 0 9 "); //King Safety
		//IBitBoard bitboard  = new Board("1r1r1k2/1Bpq1p2/1b1p1p2/4p3/1PP1Q3/p2PP1P1/P4P1P/1R3RK1 w - - 5 23");
		//IBitBoard bitboard  = new Board("1r2rk2/1Bpq1p2/1b1p1p2/4p3/1PP1Q3/p2PP1P1/P4P1P/1R3RK1 b - - 4 22"); 
		
		//IBitBoard bitboard  = new Board("r3r1k1/ppp1qpp1/2nb1n1p/3p1B2/3P4/PQN1P2P/1P3PP1/R1B2RK1 b - - 5 17");//Pawn shield
		//IBitBoard bitboard  = new Board("8/8/p7/P2q4/1k6/2p2P2/2Q1K3/8 w - - 10 80");
		//IBitBoard bitboard  = new Board("8/8/p7/P7/4P3/1kp5/4K3/8 w - - 1 82"); //Ustoppable passers problem?
		
		//IBitBoard bitboard  = new Board("1r3r2/p3p2k/1p3b2/2pP4/2B1R3/PR6/5PPP/6K1 b - - 7 35");
		//IBitBoard bitboard  = new Board("r2k1b1r/pp1np2p/3p4/2pq1Pp1/2NP4/2P1BP2/PP3P1P/R2QR1K1 w - - 1 17");
		//IBitBoard bitboard  = new Board("3rr1k1/p2q1ppp/1ppb1n2/8/3P3N/P1NQPPPb/1P1B3P/R1R4K b - - 1 22"); 
		//IBitBoard bitboard  = new Board("2k3r1/ppp2pr1/3p1p2/5n2/3P1P1p/2P2RP1/PP3N1P/R5K1 w - - 2 26 ");
		//IBitBoard bitboard  = new Board("2kr3r/ppp1np1p/3p1p2/5b2/8/3P1N2/PPP2PPP/R3KB1R w KQ - 6 13"); 
		//IBitBoard bitboard  = new Board("4r1k1/pp3p1p/2q3p1/1R1p1b2/3Pn3/PP1BB3/5PPP/1Q4K1 w - - 5 25"); 
		//IBitBoard bitboard  = new Board("r1bq1rk1/ppp2pbp/n5p1/4p1B1/2P1P1n1/2N2N2/PP2BPPP/R2Q1RK1 b - - 3 10");
		
		//D: 8	SD: 18 Time: 4.427 s	Eval: -545	NPS: 85689	PV: Bf1-d3, Ne4xd2, Ke1xd2, Nc6-d4, e3xd4, Bb4xc3, b2xc3, e5xd4, Nf3xd4
		//D: 8	SD: 18 Time: 4.827 s	Eval: -319	NPS: 89718	PV: a3xb4, Qa5xa1, Qd2-d1, Qa1xd1, Ke1xd1, f7-f5, Nc3xe4, f5xe4, Nf3-g5, Nc6xb4, Bg3xe5
		
		

		//IRootSearch search = new MTDSequentialSearch(new Object[] {cfg, null});
		search.newGame(bitboard);
		IEvaluator eval = sharedData.getEvaluatorFactory().create(bitboard, null, sharedData.getEngineConfiguration().getEvalConfig());
		
		
		//BoardUtils.playGame(bitboard, "d2-d4, g8-f6, c2-c3, g7-g6, c1-f4, f8-g7, b1-d2, d7-d5, d1-a4, b8-c6, g1-f3, e8-g8, e2-e3, f6-h5, f4-e5, c6-e5, d4-e5, d8-d7, a4-b3");
		//BoardUtils.playGame(bitboard, "a3xb4, a5xa1, d2-d1, a1xd1, e1xd1, f7-f5, c3xe4, f5xe4, f3-g5, c6xb4, g3xe5");
		
		//BoardUtils.playGame(bitboard, "b3-b2, a5-b5, c8xg8, h8xg8, b2xb5, c6xb5, a6-a7, f2-f8, a7-a8=Q, f8xa8, e5-e6");
		//BoardUtils.playGame(bitboard, "c1xc6, f2-a2, b1-c1, a2xb3, c1-d2, b3-b2, d2-d3, a5-a3, c6-c3, a3xc3, c8xc3, b2-b5, d3-e3, b5xa6");
		
		//BoardUtils.playGame(bitboard, "a5-a4, e8-g8, f3xh4, b3-c5, a4-a1, c5-e4, f2-f1, c7-d5, a1-a4, e4-g3, f1-f2, f5-f4, h4-g6, g3-e4, f2-f3, e4-g5, f3-e2");
		//BoardUtils.playGame(bitboard, "c4-d6, e8-e7, a5-e5, c7-d5, c3-c4, e7xd6, c4xd5, e6xd5, d1xd5, d6-c7, e5xf5, a8-e8, f5-e5, e8-f8");
		//bitboard = new Board(bitboard.toEPD());
		
		//IBitBoard bitboard  = new Board("r4rk1/ppp3pp/2N1p3/8/2P1pp1q/1Q2P3/PP2BKPP/3R3R w - - 2 21");
		
		//IBitBoard bitboard  = new Board("2r2rk1/pp1b2pp/8/P2pp3/1q2n2P/3Q2B1/1P1N1PP1/R3KB1R b KQ - 3 21 "); 
		
		//BoardUtils.playGame(bitboard, "b3xb7, c5xa3, b7xc6, f7-f6, d2-c3, f8-b8, d1-d7, b8-b3, b2xa3, b3xc3, c1-d2");
		//BoardUtils.playGame(bitboard, "a5-a4, e1-f2, a4-a3, c4-b3, a3-a2, b3xa2, f8xb4, a2-b3, b4-d6, b3-a4, e6-e5, a4-a5, e5-e4, f7-f8=b");
		//BoardUtils.playGame(bitboard, "a5-a4, b4-b5, c6-d7, c4-d3, a4-a3, b5-b6, a3-a2, e1-c3");
		//BoardUtils.playGame(bitboard, "b3xb7, g4xf3, g2xf3, d8-g5, g1-h1, g5-h5, f3-f4, h5-f3, h1-g1, f3-g4, g1-h1, g4-f3, h1-g1, f3-g4, g1-h1, g4-f3");
		//BoardUtils.playGame(bitboard, "f1xa6, f2xc5, d2-f3, c5-e3, d1-d2, b4xa6, h1-e1, e3-f4, a3xa6, d8xd2, f3xd2, b8xb2");
		//BoardUtils.playGame(bitboard, "d4xd5, c6xd5, h3-h4, g8-h7, b2-b3, h7-h6, c2-c4, f5-h5");
		
		//bitboard.setPawnsCache(createPawnsCache());
		
		System.out.println(bitboard);
		//System.exit(0);
		
		//System.out.println(System.getenv());
		
		/*MoveList list = new BaseMoveList();
		int count = bitboard.genAllMoves(list);
		for (int i=0; i<count; i++) {
			int move = list.reserved_getMovesBuffer()[i];
			System.out.println("" + MoveInt.moveToString(move));
		}*/
		
		
		/*
		IEvaluatorFactory evaluatorFactory = null;
		try {
			evaluatorFactory = (IEvaluatorFactory) MTDSchedulerMain.class.getClassLoader().loadClass(EngineConfigFactory.getDefaultEngineConfiguration().getEvaluatorFactoryClassName()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		
		IEvaluator eval = evaluatorFactory.create(bitboard, null);
		int evalint = eval.fullEval(0, 0, 0, bitboard.getColourToMove());
		System.out.println(((BagaturEvaluator)eval).dump(bitboard.getColourToMove()));
		System.out.println("STATIC EVAL: " + evalint);
		*/
		
		//System.exit(0);
		
		/*SearchEnv env = new SearchEnv(bitboard, new SharedData(evaluatorFactory));
		ISearch search = new SearchAB_PV(env);
		search.search(new MediatorDummper(5000000, true), 50);*/
		
		//bagaturchess.configs.tune.search.exts.extmode_mixed.MixedExts_All16_UpdateIntervalX cfg =
		//	new bagaturchess.configs.tune.search.exts.extmode_mixed.MixedExts_All16_UpdateIntervalX(new String[]{"10"});
		//-Dengine.boot.cfg=bagaturchess.properties.EngineConfigBaseImpl
		
		//ISearchMediator mediator1 = new MediatorDummper(bitboard, eval, 5000000, true);
		IChannel channel = new Channel_Console();
		final ISearchMediator mediator1 = new UCISearchMediatorImpl_NormalSearch(channel,
				new Go(channel, "go infinite"),
				new TimeController_FixedDepth(),
				bitboard.getColourToMove(),
				new BestMoveSender() {
					@Override
					public void sendBestMove() {
						System.out.println("Best move send");
					}
				},
				search, true);
		
		//ISearchMediator mediator2 = new MediatorDummper(bitboard, eval, 5000000, true);
		
		//searchMultiPV.newGame(bitboard);
		//searchMultiPV.negamax(bitboard, mediator1, 1, 100, true, null);
		search.negamax(bitboard, mediator1, 1, ISearch.MAX_DEPTH, true, null);
		
		//search.negamax(bitboard, mediator1, 2, 2, true);
		
		//ISearchMediator mediator1 = new MediatorDummper(bitboard, eval, 5000000, true);
		//MultiPVMediator multipvMediator = new MultiPVMediator(sharedData, cfg, search, bitboard, mediator1, 1, 5, true, null);
		//multipvMediator.ready();
		
		//CallbackTest search_withcallback = new CallbackTest(search, eval, bitboard);
		//search_withcallback.ready();
		
		/*ISearch search = new SearchLazyNew(bitboard);
		search.search(new MediatorDummper(5000000, true), 50);*/
		
		
		//search.search(new MediatorDummper(5000000, true), 50);
		
		//search.search( new ExplainerMediator(5000000, true, bitboard), 50);
		
		//IBitBoard new_bitboard = new Board(bitboard.toEPD());
		//new_bitboard.setAttacksSupport(EngineConfig.getSingleton().getFieldsStatesSupport(), EngineConfig.getSingleton().getFieldsStatesSupport());
		//new_bitboard.setAttacksSupport(false, false);
		
		//search.negamax(bitboard, new MediatorDummper(5000000, true), 9);		
		//test();
		
		//IBitBoard new_bitboard1 = new Board(bitboard.toEPD());
		//new_bitboard.setAttacksSupport(EngineConfig.getSingleton().getFieldsStatesSupport(), EngineConfig.getSingleton().getFieldsStatesSupport());
		
		//search.negamax(new_bitboard, new MediatorDummper(5000000, true), 12);
		
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*System.out.println("SECOND");
		
		MoveList list = new BaseMoveList();
		bitboard.genAllMoves(list);
		int move = list.next();
		bitboard.makeMoveForward(move);
		
		search.negamax(new Board(bitboard.toEPD()), new MediatorDummper(5000));*/
	}
	
	private static PawnsEvalCache createPawnsCache() {
		/*DataObjectFactory<PawnsModelEval> pawnsCacheFactory = null;
		try {
			pawnsCacheFactory = (DataObjectFactory<PawnsModelEval>) SharedData.class.getClassLoader().loadClass(EngineConfigFactory.getSingleton().getPawnsCacheFactoryClassName()).newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	
		return new PawnsEvalCache(pawnsCacheFactory, EngineConfigFactory.getSingleton().getPawnsCacheSize());*/
		return null;
	}
	
	/*public static void test() {
		IBitBoard bitboard  = new Board("rn1b2rk/1pp3p1/qp1p2R1/5Q2/3RN2P/1PP5/3PbP2/4K3 w - -");
		//IBitBoard bitboard  = new Board();
		//IBitBoard bitboard  = new Board("8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - - bm Rxb2");
		System.out.println(bitboard);
		//MTDSequentialSearch search = new MTDSequentialSearch(new EvaluatorLearningFactory());
		

		//IBitBoard new_bitboard = new Board(bitboard.toEPD());
		//bitboard.setAttacksSupport(EngineConfig.getSingleton().getFieldsStatesSupport(), EngineConfig.getSingleton().getFieldsStatesSupport());
		
		Search search = new Search(new SearchEnv(bitboard, new SharedData(new EvaluatorLearningFactory())));

		int rootColour = bitboard.getColourToMove();
		bitboard.mark();
		
		int MAXDEPTH = 15;
		for (int maxdepth = 4; maxdepth<MAXDEPTH; maxdepth+=2) {
			
			System.out.println("\r\nMAXDEPTH: " + maxdepth);
			
			MoveList list = new BaseMoveList();
			bitboard.genAllMoves(list);
	
			int lower = Search.MIN;
			int cur_move = 0;
			while ((cur_move = list.next()) != 0) {
				bitboard.makeMoveForward(cur_move);
				int result = singleSequence(bitboard, search, 2 * maxdepth, maxdepth / 2, rootColour, lower);
				if (result > lower) {
					lower = result;
				}
				//bitboard.makeMoveBackward(cur_move);
				bitboard.reset();
				System.out.println("" + MoveInt.moveToString(cur_move) + "	" + result);
			}
		}
	}

	private static int singleSequence(IBitBoard bitboard, Search search, int iteration, int maxdepth, int rootColour, int lower) {
		int result = 0;
		
		int counter = iteration;
		while (counter > 0) {
			
			SearchMediator mediator = new MediatorDummper(5000000, false);
			search.search(mediator, maxdepth, lower);
			ISearchInfo info = mediator.getLastInfo();
			
			result = info.getEval(); 
			if (rootColour != bitboard.getColourToMove()) {
				result *= -1;
			}
			//System.out.println("result=" + result);
			
			int[] pv = info.getPV();
			bitboard.makeMoveForward(pv[0]);
			//if (pv.length >= 2) bitboard.makeMoveForward(pv[1]);
			if (bitboard.isInCheck()) {
				if (!bitboard.hasMoveInCheck()) {
					//result = 
					//result = -1234567;
					break;
				}
			} else {
				if (!bitboard.hasMoveInNonCheck()) {
					//result = -1234567;
					break;
				}
			}
			
			counter--;
		}
		
		return result;
	}*/
}

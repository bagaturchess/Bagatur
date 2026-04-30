package bagaturchess.nnue_v7;

import java.io.File;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;

public final class BigNnueDemo {
    public static void main(String[] args) throws Exception {

		String fen0 = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		String fen1 = "4kq2/8/8/8/8/8/8/2QK4 w - - 0 1";
		String fen2 = "4k3/8/8/8/8/8/8/2QK4 w - - 0 1";
		String fen3 = "4kq2/8/8/8/8/8/8/3K4 w - - 0 1";
		String fen4 = "4k3/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		String fen5 = "rnbqkbnr/pppppppp/8/8/8/8/8/4K3 w KQkq - 0 1";
		String fen6 = "4kr2/8/8/8/8/8/8/2RK4 w - - 0 1";
		String fen7 = "4k3/8/8/8/8/8/8/2RK4 w - - 0 1";
		String fen8 = "4kr2/8/8/8/8/8/8/3K4 w - - 0 1";
		String fen9 = "4k3/8/8/8/8/8/3Q4/4K3 w - - 0 1";
		
        BigNnueNetwork net = BigNnueNetwork.load(new File("./nn-b1a57edbea57.nnue"));
        
		IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(fen0);
		
		NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
		
		NNUEProbeUtils.fillInput(bitboard, input);
		
        int eval = net.evaluateAdjusted(input);

        System.out.println("description=" + net.description());
        System.out.println("fen=" + fen2);
        System.out.println(eval);
    }
}

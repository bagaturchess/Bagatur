


package bagaturchess.egtb.gaviota;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.egtb.gaviota.EGTBProbing;


public class GTBProbing_NativeWrapper {
	
	
	private String tb_path = "";
	
    private final EGTBProbing gtb;
    
    
    private static GTBProbing_NativeWrapper INSTANCE;
    
    private static String error = "No errors";
    
    private static boolean hasError = false;
    
    
    private GTBProbing_NativeWrapper() {
    	gtb = EGTBProbing.getSingleton();
    }
    
    
    public static GTBProbing_NativeWrapper getInstance() {
    	
    	if (hasError) {
    		return null;
    	}
    	
		if (INSTANCE == null) {
			synchronized(GTBProbing_NativeWrapper.class) {
				if (INSTANCE == null) {
					
			    	try {
			    		INSTANCE = new GTBProbing_NativeWrapper();
			    	} catch(Throwable t) {
			    		hasError = true;
			    		error = "egtbprobe dynamic library could not be loaded (or not found). Error message is :" + t.getMessage();
			    		//java.lang.UnsatisfiedLinkError
			    		//Can't load IA 32-bit .dll on a AMD 64-bit platform
			    		System.out.println(error);
			    		//t.printStackTrace();
			    	}
			    	
				}
			}
		}
		
        return INSTANCE;
    }
    
    
    public static String getErrorMessage() {
    	return error;
    }
    
    
    /*
    public void setPath_Sync(String tbPath, int memInMegabytes) {
        if (!tb_path.equals(tbPath)) {
            tb_path = tbPath;
           	System.out.print("Loading: " + tb_path + " ... ");
           	EGTBProbing.getSingleton().init(tb_path, memInMegabytes);
           	System.out.println("OK");
        }
    }
    */
    
    
    public void setPath_Async(final String tbPath, final int memInMegabytes) {
        
    	if (!tb_path.equals(tbPath)) {
  
            Thread t = new Thread(new Runnable() {
                public void run() {
                    tb_path = tbPath;
                	System.out.print("Loading: " + tb_path + " ... ");
                	EGTBProbing.getSingleton().init(tb_path, memInMegabytes);
                	System.out.println("OK");
                }
            });
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
            
            try {Thread.sleep(300);} catch (InterruptedException e) {}
        }
    }
    
    
    public final void probeHard(GTBProbeInput input, int[] out) {
    	
    	
    	//For debug purpose
    	//if (true) throw new IllegalStateException();
    	
    	
    	//If the entries are empty.
    	//Currently, this happens only when the input cache is filled in advance with dummy entries.
    	if (input.blackPieces[0] == EGTBProbing.NATIVE_PID_NONE
    		|| input.whitePieces[0] == EGTBProbing.NATIVE_PID_NONE
    			) {
        	out[0] = GTBProbeOutput.UNKNOWN;
        	out[1] = 0;
    		return;
    	}
    	
    	
        boolean res = gtb.probeHard(input.colourTomove, input.enpassSquare,
            						input.whiteSquares, input.blackSquares, input.whitePieces, input.blackPieces,
            						out);
        
        if (res) {
            switch (out[0]) {
	            case EGTBProbing.NATIVE_INFO_DRAW:
	            	out[0] = GTBProbeOutput.DRAW;
	            	out[1] = 0;
	                break;
	            case EGTBProbing.NATIVE_INFO_WMATE:
	            	out[0] = GTBProbeOutput.WMATE;
	            	//out[1] = out[1];
	                break;
	            case EGTBProbing.NATIVE_INFO_BMATE:
	            	out[0] = GTBProbeOutput.BMATE;
	            	//out[1] = out[1];
	                break;
	            default:
	            	out[0] = GTBProbeOutput.UNKNOWN;
	            	out[1] = 0;
	                break;
            }
        } else {
        	out[0] = GTBProbeOutput.UNKNOWN;
        	out[1] = 0;
        }
    }
    
    
	public void fill(IBoard board, GTBProbeInput input) {
		
		input.hashkey = board.getHashKey();
		
		input.colourTomove = board.getColourToMove() == Constants.COLOUR_WHITE ? EGTBProbing.NATIVE_WHITE_TO_MOVE : EGTBProbing.NATIVE_BLACK_TO_MOVE;
		
		
		input.enpassSquare = -1;//TODO board.getEpSquare();
        if (input.enpassSquare == -1)
        	input.enpassSquare = EGTBProbing.NATIVE_SQUARE_NONE;
        
        
		int nWhite = 0;
        int nBlack = 0;
        
        int[] data;
        int size;
        
        IPiecesLists plists = board.getPiecesLists();
        
        //White pieces
        PiecesList w_king = plists.getPieces(Constants.PID_W_KING);
        data = w_king.getData();
        size = w_king.getDataSize();
        for (int i=0;i<size;i++) {
        	input.whiteSquares[nWhite] = data[i];
        	input.whitePieces[nWhite++] = EGTBProbing.NATIVE_PID_KING;
        }
        
        PiecesList w_pawns = plists.getPieces(Constants.PID_W_PAWN);
        data = w_pawns.getData();
        size = w_pawns.getDataSize();
        for (int i=0;i<size;i++) {
        	input.whiteSquares[nWhite] = data[i];
        	input.whitePieces[nWhite++] = EGTBProbing.NATIVE_PID_PAWN;
        }
        
        PiecesList w_knights = plists.getPieces(Constants.PID_W_KNIGHT);
        data = w_knights.getData();
        size = w_knights.getDataSize();
        for (int i=0;i<size;i++) {
        	input.whiteSquares[nWhite] = data[i];
        	input.whitePieces[nWhite++] = EGTBProbing.NATIVE_PID_KNIGHT;
        }
        
        PiecesList w_bishops = plists.getPieces(Constants.PID_W_BISHOP);
        data = w_bishops.getData();
        size = w_bishops.getDataSize();
        for (int i=0;i<size;i++) {
        	input.whiteSquares[nWhite] = data[i];
        	input.whitePieces[nWhite++] = EGTBProbing.NATIVE_PID_BISHOP;
        }
        
        PiecesList w_rooks = plists.getPieces(Constants.PID_W_ROOK);
        data = w_rooks.getData();
        size = w_rooks.getDataSize();
        for (int i=0;i<size;i++) {
        	input.whiteSquares[nWhite] = data[i];
        	input.whitePieces[nWhite++] = EGTBProbing.NATIVE_PID_ROOK;
        }
        
        PiecesList w_queens = plists.getPieces(Constants.PID_W_QUEEN);
        data = w_queens.getData();
        size = w_queens.getDataSize();
        for (int i=0;i<size;i++) {
        	input.whiteSquares[nWhite] = data[i];
        	input.whitePieces[nWhite++] = EGTBProbing.NATIVE_PID_QUEEN;
        }
        
        
        //Black pieces
        PiecesList b_king = plists.getPieces(Constants.PID_B_KING);
        data = b_king.getData();
        size = b_king.getDataSize();
        for (int i=0;i<size;i++) {
        	input.blackSquares[nBlack] = data[i];
        	input.blackPieces[nBlack++] = EGTBProbing.NATIVE_PID_KING;
        }
        
        PiecesList b_pawns = plists.getPieces(Constants.PID_B_PAWN);
        data = b_pawns.getData();
        size = b_pawns.getDataSize();
        for (int i=0;i<size;i++) {
        	input.blackSquares[nBlack] = data[i];
        	input.blackPieces[nBlack++] = EGTBProbing.NATIVE_PID_PAWN;
        }
        
        PiecesList b_knights = plists.getPieces(Constants.PID_B_KNIGHT);
        data = b_knights.getData();
        size = b_knights.getDataSize();
        for (int i=0;i<size;i++) {
        	input.blackSquares[nBlack] = data[i];
        	input.blackPieces[nBlack++] = EGTBProbing.NATIVE_PID_KNIGHT;
        }
        
        PiecesList b_bishops = plists.getPieces(Constants.PID_B_BISHOP);
        data = b_bishops.getData();
        size = b_bishops.getDataSize();
        for (int i=0;i<size;i++) {
        	input.blackSquares[nBlack] = data[i];
        	input.blackPieces[nBlack++] = EGTBProbing.NATIVE_PID_BISHOP;
        }
        
        PiecesList b_rooks = plists.getPieces(Constants.PID_B_ROOK);
        data = b_rooks.getData();
        size = b_rooks.getDataSize();
        for (int i=0;i<size;i++) {
        	input.blackSquares[nBlack] = data[i];
        	input.blackPieces[nBlack++] = EGTBProbing.NATIVE_PID_ROOK;
        }
        
        PiecesList b_queens = plists.getPieces(Constants.PID_B_QUEEN);
        data = b_queens.getData();
        size = b_queens.getDataSize();
        for (int i=0;i<size;i++) {
        	input.blackSquares[nBlack] = data[i];
        	input.blackPieces[nBlack++] = EGTBProbing.NATIVE_PID_QUEEN;
        }
        
        
        input.whiteSquares[nWhite] = EGTBProbing.NATIVE_SQUARE_NONE;
        input.blackSquares[nBlack] = EGTBProbing.NATIVE_SQUARE_NONE;
        input.whitePieces[nWhite] = EGTBProbing.NATIVE_PID_NONE;
        input.blackPieces[nBlack] = EGTBProbing.NATIVE_PID_NONE;
	}
}

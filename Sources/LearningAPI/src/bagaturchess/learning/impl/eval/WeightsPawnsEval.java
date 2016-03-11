package bagaturchess.learning.impl.eval;


import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.learning.impl.eval.cfg.Weights;
import bagaturchess.learning.impl.filler.SignalFillerConstants;


public class WeightsPawnsEval extends PawnsModelEval implements Weights {
	
	
	private double eval_o;
	private double eval_e;
	
	
	public int getEval_o() {
		return (int) eval_o;
	}
	
	
	public int getEval_e() {
		return (int) eval_e;
	}
	
	
	@Override
	protected void eval() {
		
		eval_o = 0;
		eval_e = 0;
		
		PawnsModel pmodel = getModel();
		Pawn[] w_pawns_m = pmodel.getWPawns();
		int w_count = pmodel.getWCount();
		Pawn[] b_pawns_m = pmodel.getBPawns();
		int b_count = pmodel.getBCount();
		
		//int w_kingID = pmodel.getWKingFieldID();
		//int b_kingID = pmodel.getBKingFieldID();
		
		if (w_count > 0) {
			
			for (int i=0; i<w_count; i++) {
				
				Pawn p = w_pawns_m[i];
				
				/*if (p.isPassed()) {
					eval_o += PAWNS_PASSED_O;
					eval_e += PAWNS_PASSED_E;
					
					int rank = p.getRank();
					
					eval_o += PAWNS_PASSED_RNK_O * SignalFillerConstants.PASSERS_RANK_O[rank];
					eval_e += PAWNS_PASSED_RNK_E * SignalFillerConstants.PASSERS_RANK_E[rank];
					
			        int frontFieldID = p.getFieldID() + 8;
			        int frontFrontFieldID = frontFieldID + 8;
			        if (frontFrontFieldID >= 64) {
			        	frontFrontFieldID = frontFieldID;
			        }
			        
			        int dist_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD[Fields.getDistancePoints(w_kingID, frontFieldID)];
			        eval_o += KINGS_PASSERS_F_O * dist_f;
			        eval_e += KINGS_PASSERS_F_E * dist_f;
			        
			        int dist_ff = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[Fields.getDistancePoints(w_kingID, frontFrontFieldID)];
			        eval_o += KINGS_PASSERS_FF_O * dist_ff;
			        eval_e += KINGS_PASSERS_FF_E * dist_ff;
			        
			        int dist_op_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD_OP[Fields.getDistancePoints(b_kingID, frontFieldID)];
			        eval_o += KINGS_PASSERS_F_OP_O * dist_op_f;
			        eval_e += KINGS_PASSERS_F_OP_E * dist_op_f;
				}
				
				if (p.isCandidate()) {
					int rank = p.getRank();
					eval_o += PAWNS_CANDIDATE_RNK_O * SignalFillerConstants.PASSERS_CANDIDATE_RANK_O[rank];
					eval_e += PAWNS_CANDIDATE_RNK_E * SignalFillerConstants.PASSERS_CANDIDATE_RANK_E[rank];
				}*/
				
				if (p.isDoubled()) {
					eval_o += PAWNS_DOUBLED_O;
			        eval_e += PAWNS_DOUBLED_E;
				}
				
				if (p.isIsolated()) {
					eval_o += PAWNS_ISOLATED_O;
			        eval_e += PAWNS_ISOLATED_E;
				}
				
				if (p.isBackward()) {
					eval_o += PAWNS_BACKWARD_O;
			        eval_e += PAWNS_BACKWARD_E;
				}
				
				if (p.isSupported()) {
					eval_o += PAWNS_SUPPORTED_O;
			        eval_e += PAWNS_SUPPORTED_E;
				} else if (p.cannotBeSupported()) {
					eval_o += PAWNS_CANNOTBS_O;
			        eval_e += PAWNS_CANNOTBS_E;
				}
				
				if (p.isGuard()) {
					eval_o += PAWNS_GARDS_O;
			        eval_e += PAWNS_GARDS_E;
			        
					eval_o += PAWNS_GARDS_REM_O * p.getGuardRemoteness();
			        eval_e += PAWNS_GARDS_REM_E * p.getGuardRemoteness();
				}
				
				if (p.isStorm()) {
					eval_o += PAWNS_STORMS_O;
			        eval_e += PAWNS_STORMS_E;
			        
					eval_o += PAWNS_STORMS_CLS_O * (8 - p.getStormCloseness());
			        eval_e += PAWNS_STORMS_CLS_E * (8 - p.getStormCloseness());
				}
			}
		}
		
		
		if (b_count > 0) {
			
			for (int i=0; i<b_count; i++) {
				
				Pawn p = b_pawns_m[i];
				
				/*if (p.isPassed()) {
					
					eval_o -= PAWNS_PASSED_O;
					eval_e -= PAWNS_PASSED_E;
					
					int rank = p.getRank();
					
					eval_o -= PAWNS_PASSED_RNK_O * SignalFillerConstants.PASSERS_RANK_O[rank];
					eval_e -= PAWNS_PASSED_RNK_E * SignalFillerConstants.PASSERS_RANK_E[rank];
					
			        int frontFieldID = p.getFieldID() - 8;
			        int frontFrontFieldID = frontFieldID - 8;
			        if (frontFrontFieldID < 0) {
			        	frontFrontFieldID = frontFieldID;
			        }
			        
			        int dist_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD[Fields.getDistancePoints(b_kingID, frontFieldID)];
			        eval_o -= KINGS_PASSERS_F_O * dist_f;
			        eval_e -= KINGS_PASSERS_F_E * dist_f;
			        
			        int dist_ff = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFRONTFIELD[Fields.getDistancePoints(b_kingID, frontFrontFieldID)];
			        eval_o -= KINGS_PASSERS_FF_O * dist_ff;
			        eval_e -= KINGS_PASSERS_FF_E * dist_ff;
			        
			        int dist_op_f = rank * SignalFillerConstants.PASSERS_KING_CLOSENESS_FRONTFIELD_OP[Fields.getDistancePoints(w_kingID, frontFieldID)];
			        eval_o -= KINGS_PASSERS_F_OP_O * dist_op_f;
			        eval_e -= KINGS_PASSERS_F_OP_E * dist_op_f;			        
				}
				
				if (p.isCandidate()) {
					int rank = p.getRank();
					eval_o -= PAWNS_CANDIDATE_RNK_O * SignalFillerConstants.PASSERS_CANDIDATE_RANK_O[rank];
					eval_e -= PAWNS_CANDIDATE_RNK_E * SignalFillerConstants.PASSERS_CANDIDATE_RANK_E[rank];
				}*/
				
				if (p.isDoubled()) {
					eval_o -= PAWNS_DOUBLED_O;
			        eval_e -= PAWNS_DOUBLED_E;
				}
				
				if (p.isIsolated()) {
					eval_o -= PAWNS_ISOLATED_O;
			        eval_e -= PAWNS_ISOLATED_E;
				}
				
				if (p.isBackward()) {
					eval_o -= PAWNS_BACKWARD_O;
			        eval_e -= PAWNS_BACKWARD_E;
				}
				
				if (p.isSupported()) {
					eval_o -= PAWNS_SUPPORTED_O;
			        eval_e -= PAWNS_SUPPORTED_E;
				} else if (p.cannotBeSupported()) {
					eval_o -= PAWNS_CANNOTBS_O;
			        eval_e -= PAWNS_CANNOTBS_E;
				}
				
				if (p.isGuard()) {
					eval_o -= PAWNS_GARDS_O;
			        eval_e -= PAWNS_GARDS_E;
			        
					eval_o -= PAWNS_GARDS_REM_O * p.getGuardRemoteness();
			        eval_e -= PAWNS_GARDS_REM_E * p.getGuardRemoteness();
				}
				
				if (p.isStorm()) {
					eval_o -= PAWNS_STORMS_O;
			        eval_e -= PAWNS_STORMS_E;
			        
					eval_o -= PAWNS_STORMS_CLS_O * (8 - p.getStormCloseness());
			        eval_e -= PAWNS_STORMS_CLS_E * (8 - p.getStormCloseness());
				}
			}
		}
		
		int opened = pmodel.getWKingOpenedFiles() - pmodel.getBKingOpenedFiles();
		eval_o += PAWNS_OPENNED_O * opened;
        eval_e += PAWNS_OPENNED_E * opened;
        
		int semiownopened = pmodel.getWKingSemiOwnOpenedFiles() - pmodel.getBKingSemiOwnOpenedFiles();
		eval_o += PAWNS_SEMIOP_OWN_O * semiownopened;
        eval_e += PAWNS_SEMIOP_OWN_E * semiownopened;

		int semiopopened = pmodel.getWKingSemiOpOpenedFiles() - pmodel.getBKingSemiOpOpenedFiles();
		eval_o += PAWNS_SEMIOP_OP_O * semiopopened;
        eval_e += PAWNS_SEMIOP_OP_E * semiopopened;

		int weak = pmodel.getWWeakFields() - pmodel.getBWeakFields();
		eval_o += PAWNS_WEAK_O * weak;
        eval_e += PAWNS_WEAK_E * weak;

		int islands = pmodel.getWIslandsCount() - pmodel.getBIslandsCount();
		eval_o += PAWNS_ISLANDS_O * islands;
        eval_e += PAWNS_ISLANDS_E * islands;
        
        
        eval_o = Math.round(eval_o);
        eval_e = Math.round(eval_e);
	}
}

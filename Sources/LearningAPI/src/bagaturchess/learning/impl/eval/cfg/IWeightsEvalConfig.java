package bagaturchess.learning.impl.eval.cfg;


import bagaturchess.search.api.IEvalConfig;


public interface IWeightsEvalConfig extends IEvalConfig {

	public abstract double getKINGSAFE_CASTLING_O();

	public abstract double getKINGSAFE_CASTLING_E();

	public abstract double getKINGSAFE_FIANCHETTO_O();

	public abstract double getKINGSAFE_FIANCHETTO_E();

	public abstract double getBISHOPS_DOUBLE_O();

	public abstract double getBISHOPS_DOUBLE_E();

	public abstract double getKNIGHTS_DOUBLE_O();

	public abstract double getKNIGHTS_DOUBLE_E();

	public abstract double getROOKS_DOUBLE_O();

	public abstract double getROOKS_DOUBLE_E();

	public abstract double getPAWNS5_ROOKS_O();

	public abstract double getPAWNS5_ROOKS_E();

	public abstract double getPAWNS5_KNIGHTS_O();

	public abstract double getPAWNS5_KNIGHTS_E();

	public abstract double getKINGSAFE_F_O();

	public abstract double getKINGSAFE_F_E();

	public abstract double getKINGSAFE_G_O();

	public abstract double getKINGSAFE_G_E();

	public abstract double getKINGS_DISTANCE_O();

	public abstract double getKINGS_DISTANCE_E();

	public abstract double getPAWNS_DOUBLED_O();

	public abstract double getPAWNS_DOUBLED_E();

	public abstract double getPAWNS_ISOLATED_O();

	public abstract double getPAWNS_ISOLATED_E();

	public abstract double getPAWNS_BACKWARD_O();

	public abstract double getPAWNS_BACKWARD_E();

	public abstract double getPAWNS_SUPPORTED_O();

	public abstract double getPAWNS_SUPPORTED_E();

	public abstract double getPAWNS_CANNOTBS_O();

	public abstract double getPAWNS_CANNOTBS_E();

	public abstract double getPAWNS_PASSED_O();

	public abstract double getPAWNS_PASSED_E();

	public abstract double getPAWNS_PASSED_RNK_O();

	public abstract double getPAWNS_PASSED_RNK_E();

	public abstract double getPAWNS_UNSTOPPABLE_PASSER_O();

	public abstract double getPAWNS_UNSTOPPABLE_PASSER_E();

	public abstract double getPAWNS_CANDIDATE_RNK_O();

	public abstract double getPAWNS_CANDIDATE_RNK_E();

	public abstract double getKINGS_PASSERS_F_O();

	public abstract double getKINGS_PASSERS_F_E();

	public abstract double getKINGS_PASSERS_FF_O();

	public abstract double getKINGS_PASSERS_FF_E();

	public abstract double getKINGS_PASSERS_F_OP_O();

	public abstract double getKINGS_PASSERS_F_OP_E();

	public abstract double getPAWNS_ISLANDS_O();

	public abstract double getPAWNS_ISLANDS_E();

	public abstract double getPAWNS_GARDS_O();

	public abstract double getPAWNS_GARDS_E();

	public abstract double getPAWNS_GARDS_REM_O();

	public abstract double getPAWNS_GARDS_REM_E();

	public abstract double getPAWNS_STORMS_O();

	public abstract double getPAWNS_STORMS_E();

	public abstract double getPAWNS_STORMS_CLS_O();

	public abstract double getPAWNS_STORMS_CLS_E();

	public abstract double getPAWNS_OPENNED_O();

	public abstract double getPAWNS_OPENNED_E();

	public abstract double getPAWNS_SEMIOP_OWN_O();

	public abstract double getPAWNS_SEMIOP_OWN_E();

	public abstract double getPAWNS_SEMIOP_OP_O();

	public abstract double getPAWNS_SEMIOP_OP_E();

	public abstract double getPAWNS_WEAK_O();

	public abstract double getPAWNS_WEAK_E();

	public abstract double getSPACE_O();

	public abstract double getSPACE_E();

	public abstract double getROOK_INFRONT_PASSER_O();

	public abstract double getROOK_INFRONT_PASSER_E();

	public abstract double getROOK_BEHIND_PASSER_O();

	public abstract double getROOK_BEHIND_PASSER_E();

	public abstract double getBISHOPS_BAD_O();

	public abstract double getBISHOPS_BAD_E();

	public abstract double getKNIGHT_OUTPOST_O();

	public abstract double getKNIGHT_OUTPOST_E();

	public abstract double getROOKS_OPENED_O();

	public abstract double getROOKS_OPENED_E();

	public abstract double getROOKS_SEMIOPENED_O();

	public abstract double getROOKS_SEMIOPENED_E();

	public abstract double getTROPISM_KNIGHT_O();

	public abstract double getTROPISM_KNIGHT_E();

	public abstract double getTROPISM_BISHOP_O();

	public abstract double getTROPISM_BISHOP_E();

	public abstract double getTROPISM_ROOK_O();

	public abstract double getTROPISM_ROOK_E();

	public abstract double getTROPISM_QUEEN_O();

	public abstract double getTROPISM_QUEEN_E();

	public abstract double getROOKS_7TH_2TH_O();

	public abstract double getROOKS_7TH_2TH_E();

	public abstract double getQUEENS_7TH_2TH_O();

	public abstract double getQUEENS_7TH_2TH_E();

	public abstract double getKINGSAFETY_L1_O();

	public abstract double getKINGSAFETY_L1_E();

	public abstract double getKINGSAFETY_L2_O();

	public abstract double getKINGSAFETY_L2_E();

	public abstract double getMOBILITY_KNIGHT_O();

	public abstract double getMOBILITY_KNIGHT_E();

	public abstract double getMOBILITY_BISHOP_O();

	public abstract double getMOBILITY_BISHOP_E();

	public abstract double getMOBILITY_ROOK_O();

	public abstract double getMOBILITY_ROOK_E();

	public abstract double getMOBILITY_QUEEN_O();

	public abstract double getMOBILITY_QUEEN_E();

	public abstract double getMOBILITY_KNIGHT_S_O();

	public abstract double getMOBILITY_KNIGHT_S_E();

	public abstract double getMOBILITY_BISHOP_S_O();

	public abstract double getMOBILITY_BISHOP_S_E();

	public abstract double getMOBILITY_ROOK_S_O();

	public abstract double getMOBILITY_ROOK_S_E();

	public abstract double getMOBILITY_QUEEN_S_O();

	public abstract double getMOBILITY_QUEEN_S_E();

	public abstract double getPENETRATION_OP_O();

	public abstract double getPENETRATION_OP_E();

	public abstract double getPENETRATION_OP_S_O();

	public abstract double getPENETRATION_OP_S_E();

	public abstract double getPENETRATION_KING_O();

	public abstract double getPENETRATION_KING_E();

	public abstract double getPENETRATION_KING_S_O();

	public abstract double getPENETRATION_KING_S_E();

	public abstract double getROOKS_PAIR_H_O();

	public abstract double getROOKS_PAIR_H_E();

	public abstract double getROOKS_PAIR_V_O();

	public abstract double getROOKS_PAIR_V_E();

	public abstract double getTRAP_O();

	public abstract double getTRAP_E();

	public abstract double getPIN_KING_O();

	public abstract double getPIN_KING_E();

	public abstract double getPIN_BIGGER_O();

	public abstract double getPIN_BIGGER_E();

	public abstract double getPIN_EQUAL_O();

	public abstract double getPIN_EQUAL_E();

	public abstract double getPIN_LOWER_O();

	public abstract double getPIN_LOWER_E();

	public abstract double getATTACK_BIGGER_O();

	public abstract double getATTACK_BIGGER_E();

	public abstract double getATTACK_EQUAL_O();

	public abstract double getATTACK_EQUAL_E();

	public abstract double getATTACK_LOWER_O();

	public abstract double getATTACK_LOWER_E();

	public abstract double getHUNGED_PIECE_O();

	public abstract double getHUNGED_PIECE_E();

	public abstract double getHUNGED_PAWNS_O();

	public abstract double getHUNGED_PAWNS_E();

	public abstract double getHUNGED_ALL_O();

	public abstract double getHUNGED_ALL_E();

}
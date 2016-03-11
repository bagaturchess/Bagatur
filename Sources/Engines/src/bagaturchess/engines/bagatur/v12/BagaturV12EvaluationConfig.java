package bagaturchess.engines.bagatur.v12;


import bagaturchess.learning.impl.eval.cfg.IWeightsEvalConfig;


public class BagaturV12EvaluationConfig implements IWeightsEvalConfig {	
	
    
    public double KINGSAFE_CASTLING_O = 8.695473272912482;
    public double KINGSAFE_CASTLING_E = 0.0;
    public double KINGSAFE_FIANCHETTO_O = 30;
    public double KINGSAFE_FIANCHETTO_E = 0.0;
    public double BISHOPS_DOUBLE_O = 38.55304785134276;
    public double BISHOPS_DOUBLE_E = 58.2527533291309;
    public double KNIGHTS_DOUBLE_O = -15;
    public double KNIGHTS_DOUBLE_E = -30;
    public double ROOKS_DOUBLE_O = -15;
    public double ROOKS_DOUBLE_E = -30;
    public double PAWNS5_ROOKS_O = -4;
    public double PAWNS5_ROOKS_E = -7;
    public double PAWNS5_KNIGHTS_O = 3.699203461185335;
    public double PAWNS5_KNIGHTS_E = 7.4901092619848955;
    public double KINGSAFE_F_O = -20;
    public double KINGSAFE_F_E = 0.0;
    public double KINGSAFE_G_O = -20;
    public double KINGSAFE_G_E = 0.0;
    public double KINGS_DISTANCE_O = 0;
    public double KINGS_DISTANCE_E = 3;
    public double PAWNS_DOUBLED_O = -10;
    public double PAWNS_DOUBLED_E = -14.29780586088556;
    public double PAWNS_ISOLATED_O = -14.081104147241549;
    public double PAWNS_ISOLATED_E = -11.146861253903177;
    public double PAWNS_BACKWARD_O = -7.375578895582516;
    public double PAWNS_BACKWARD_E = -7.2179199051395857;
    public double PAWNS_SUPPORTED_O = 5.609065058083699;
    public double PAWNS_SUPPORTED_E = 5.3465159357916567;
    public double PAWNS_CANNOTBS_O = -10.6863498502616525;
    public double PAWNS_CANNOTBS_E = -10.5308967547913821;
    public double PAWNS_PASSED_O = 4.680477377823983;
    public double PAWNS_PASSED_E = 3.4342682149594577;
    public double PAWNS_PASSED_RNK_O = 1.1424944013732647;
    public double PAWNS_PASSED_RNK_E = 1.4706529633174854;
    public double PAWNS_UNSTOPPABLE_PASSER_O = 0.0;
    public double PAWNS_UNSTOPPABLE_PASSER_E = 550.0;
    public double PAWNS_CANDIDATE_RNK_O = 1.9256140108109243;
    public double PAWNS_CANDIDATE_RNK_E = 1.3898951461659623;
    public double KINGS_PASSERS_F_O = 0.0;
    public double KINGS_PASSERS_F_E = 1.771641359692092;
    public double KINGS_PASSERS_FF_O = 0.0;
    public double KINGS_PASSERS_FF_E = 1.0573809123088491;
    public double KINGS_PASSERS_F_OP_O = 0.0;
    public double KINGS_PASSERS_F_OP_E = 1.664989299655242;
    public double PAWNS_ISLANDS_O = -20;
    public double PAWNS_ISLANDS_E = -20;
    public double PAWNS_GARDS_O = 15.095304223518145;
    public double PAWNS_GARDS_E = 0.0;
    public double PAWNS_GARDS_REM_O = -6.817067617714686;
    public double PAWNS_GARDS_REM_E = 0.0;
    public double PAWNS_STORMS_O = 5.6562511086470018;
    public double PAWNS_STORMS_E = 0.0;
    public double PAWNS_STORMS_CLS_O = 2.7528654000457136;
    public double PAWNS_STORMS_CLS_E = 0.0;
    public double PAWNS_OPENNED_O = -36.51630475211046;
    public double PAWNS_OPENNED_E = 0.0;
    public double PAWNS_SEMIOP_OWN_O = -27.61263467545501;
    public double PAWNS_SEMIOP_OWN_E = 0.0;
    public double PAWNS_SEMIOP_OP_O = -11.243766767056155;
    public double PAWNS_SEMIOP_OP_E = 0.0;
    public double PAWNS_WEAK_O = -10.56049594965303;
    public double PAWNS_WEAK_E = -5.11469942441058452;
    public double SPACE_O = 0.4842269012003156;
    public double SPACE_E = 1.06495242638572;
    public double ROOK_INFRONT_PASSER_O = -26.281968971663698;
    public double ROOK_INFRONT_PASSER_E = -25.7874043073770787;
    public double ROOK_BEHIND_PASSER_O = 20.16400430403572036;
    public double ROOK_BEHIND_PASSER_E = 20.370505576804776;
    public double BISHOPS_BAD_O = -5.47203138436696224;
    public double BISHOPS_BAD_E = -5.6447870737777084;
    public double KNIGHT_OUTPOST_O = 16.189159441831243;
    public double KNIGHT_OUTPOST_E = 21.4890542689350073;
    public double ROOKS_OPENED_O = 24.64660545935084;
    public double ROOKS_OPENED_E = 10.9523862741844824;
    public double ROOKS_SEMIOPENED_O = 16.68542672912037;
    public double ROOKS_SEMIOPENED_E = 10.0138229289325038;
    public double TROPISM_KNIGHT_O = 0.59900906859684694;
    public double TROPISM_KNIGHT_E = 0.0;
    public double TROPISM_BISHOP_O = 0.4844399390677248;
    public double TROPISM_BISHOP_E = 0.0;
    public double TROPISM_ROOK_O = 0.6719263432348239;
    public double TROPISM_ROOK_E = 0.0;
    public double TROPISM_QUEEN_O = 0.52101756508375879;
    public double TROPISM_QUEEN_E = 0.0;
    public double ROOKS_7TH_2TH_O = 14.098363515907408;
    public double ROOKS_7TH_2TH_E = 25.959258182976324;
    public double QUEENS_7TH_2TH_O = 10.3427421276523095;
    public double QUEENS_7TH_2TH_E = 10.95734336974601;
    public double KINGSAFETY_L1_O = 50.79324915525265;
    public double KINGSAFETY_L1_E = 0.0;
    public double KINGSAFETY_L2_O = 7.139658663877436;
    public double KINGSAFETY_L2_E = 0.0;
    public double MOBILITY_KNIGHT_O = 0.799920552085603;
    public double MOBILITY_KNIGHT_E = 1.4023260453044764;
    public double MOBILITY_BISHOP_O = 0.7140264083199767;
    public double MOBILITY_BISHOP_E = 1.1880883496711263;
    public double MOBILITY_ROOK_O = 0.8519327926189777;
    public double MOBILITY_ROOK_E = 1.3656293704586049;
    public double MOBILITY_QUEEN_O = 0.28489031564274016;
    public double MOBILITY_QUEEN_E = 0.7969717663469346;
    public double MOBILITY_KNIGHT_S_O = 0.5701417732919651;
    public double MOBILITY_KNIGHT_S_E = 1.5943119808282296;
    public double MOBILITY_BISHOP_S_O = 0.5236869290177378;
    public double MOBILITY_BISHOP_S_E = 0.702074640076866;
    public double MOBILITY_ROOK_S_O = 0.4436233529865903;
    public double MOBILITY_ROOK_S_E = 1.6241039419399184;
    public double MOBILITY_QUEEN_S_O = 0.28976717176007516;
    public double MOBILITY_QUEEN_S_E = 0.7694076178835493;
    public double PENETRATION_OP_O = 0.27824204653201886;
    public double PENETRATION_OP_E = 0.0;
    public double PENETRATION_OP_S_O = 0.22735909034148527;
    public double PENETRATION_OP_S_E = 0.0;
    public double PENETRATION_KING_O = 0.2379869316404612;
    public double PENETRATION_KING_E = 0.0;
    public double PENETRATION_KING_S_O = 0.23623177607415432;
    public double PENETRATION_KING_S_E = 0.0;
    public double ROOKS_PAIR_H_O = 10.141756627196248;
    public double ROOKS_PAIR_H_E = 10.7208904465692263;
    public double ROOKS_PAIR_V_O = 15.20986129053952293;
    public double ROOKS_PAIR_V_E = 15.49629572742464223;
    
    public double TRAP_O 			= 3.3667991106010713;
    public double TRAP_E 			= 2.4438235169149752;
    public double PIN_KING_O 		= 40.400084487677882;
    public double PIN_KING_E 		= 40.763515095856501;
    public double PIN_BIGGER_O 		= 30.8010112003917484;
    public double PIN_BIGGER_E	 	= 30.4066615810464205;
    public double PIN_EQUAL_O 		= 15.94761907883406;
    public double PIN_EQUAL_E 		= 15.559151201492769;
    public double PIN_LOWER_O 		= 10.7369572454951826;
    public double PIN_LOWER_E 		= 10.0522002261614265;
    public double ATTACK_BIGGER_O	= 40.232728105857094;
    public double ATTACK_BIGGER_E 	= 40.731304482994883;
    public double ATTACK_EQUAL_O 	= 30.797031610592222;
    public double ATTACK_EQUAL_E 	= 30.082858644052445;
    public double ATTACK_LOWER_O 	= 20.2924847990005928;
    public double ATTACK_LOWER_E 	= 20.533588111226228;
    public double HUNGED_PIECE_O 	= 15.035823470670232;
    public double HUNGED_PIECE_E 	= 15.387603531653903;
    public double HUNGED_PAWNS_O 	= 7.8950906090115673;
    public double HUNGED_PAWNS_E	= 7.317972174452671;
    public double HUNGED_ALL_O 		= 9.336728836626604;
    public double HUNGED_ALL_E 		= 9.995315715674308;
    
    
	public BagaturV12EvaluationConfig() {
		
	}
	
	
	public BagaturV12EvaluationConfig(String[] args) {
		
	}


	@Override
	public boolean useLazyEval() {
		return true;
	}
	
	
	@Override
	public boolean useEvalCache() {
		return true;
	}
	
	
	@Override
	public String getEvaluatorFactoryClassName() {
		return BagaturV12EvaluatorFactory.class.getName();
	}
	
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		return BagaturV12PawnsEvalFactory.class.getName();
	}

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFE_CASTLING_O()
	 */
    @Override
	public double getKINGSAFE_CASTLING_O() {
        return KINGSAFE_CASTLING_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFE_CASTLING_E()
	 */
    @Override
	public double getKINGSAFE_CASTLING_E() {
        return KINGSAFE_CASTLING_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFE_FIANCHETTO_O()
	 */
    @Override
	public double getKINGSAFE_FIANCHETTO_O() {
        return KINGSAFE_FIANCHETTO_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFE_FIANCHETTO_E()
	 */
    @Override
	public double getKINGSAFE_FIANCHETTO_E() {
        return KINGSAFE_FIANCHETTO_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getBISHOPS_DOUBLE_O()
	 */
    @Override
	public double getBISHOPS_DOUBLE_O() {
        return BISHOPS_DOUBLE_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getBISHOPS_DOUBLE_E()
	 */
    @Override
	public double getBISHOPS_DOUBLE_E() {
        return BISHOPS_DOUBLE_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKNIGHTS_DOUBLE_O()
	 */
    @Override
	public double getKNIGHTS_DOUBLE_O() {
        return KNIGHTS_DOUBLE_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKNIGHTS_DOUBLE_E()
	 */
    @Override
	public double getKNIGHTS_DOUBLE_E() {
        return KNIGHTS_DOUBLE_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_DOUBLE_O()
	 */
    @Override
	public double getROOKS_DOUBLE_O() {
        return ROOKS_DOUBLE_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_DOUBLE_E()
	 */
    @Override
	public double getROOKS_DOUBLE_E() {
        return ROOKS_DOUBLE_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS5_ROOKS_O()
	 */
    @Override
	public double getPAWNS5_ROOKS_O() {
        return PAWNS5_ROOKS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS5_ROOKS_E()
	 */
    @Override
	public double getPAWNS5_ROOKS_E() {
        return PAWNS5_ROOKS_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS5_KNIGHTS_O()
	 */
    @Override
	public double getPAWNS5_KNIGHTS_O() {
        return PAWNS5_KNIGHTS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS5_KNIGHTS_E()
	 */
    @Override
	public double getPAWNS5_KNIGHTS_E() {
        return PAWNS5_KNIGHTS_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFE_F_O()
	 */
    @Override
	public double getKINGSAFE_F_O() {
        return KINGSAFE_F_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFE_F_E()
	 */
    @Override
	public double getKINGSAFE_F_E() {
        return KINGSAFE_F_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFE_G_O()
	 */
    @Override
	public double getKINGSAFE_G_O() {
        return KINGSAFE_G_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFE_G_E()
	 */
    @Override
	public double getKINGSAFE_G_E() {
        return KINGSAFE_G_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGS_DISTANCE_O()
	 */
    @Override
	public double getKINGS_DISTANCE_O() {
        return KINGS_DISTANCE_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGS_DISTANCE_E()
	 */
    @Override
	public double getKINGS_DISTANCE_E() {
        return KINGS_DISTANCE_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_DOUBLED_O()
	 */
    @Override
	public double getPAWNS_DOUBLED_O() {
        return PAWNS_DOUBLED_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_DOUBLED_E()
	 */
    @Override
	public double getPAWNS_DOUBLED_E() {
        return PAWNS_DOUBLED_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_ISOLATED_O()
	 */
    @Override
	public double getPAWNS_ISOLATED_O() {
        return PAWNS_ISOLATED_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_ISOLATED_E()
	 */
    @Override
	public double getPAWNS_ISOLATED_E() {
        return PAWNS_ISOLATED_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_BACKWARD_O()
	 */
    @Override
	public double getPAWNS_BACKWARD_O() {
        return PAWNS_BACKWARD_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_BACKWARD_E()
	 */
    @Override
	public double getPAWNS_BACKWARD_E() {
        return PAWNS_BACKWARD_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_SUPPORTED_O()
	 */
    @Override
	public double getPAWNS_SUPPORTED_O() {
        return PAWNS_SUPPORTED_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_SUPPORTED_E()
	 */
    @Override
	public double getPAWNS_SUPPORTED_E() {
        return PAWNS_SUPPORTED_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_CANNOTBS_O()
	 */
    @Override
	public double getPAWNS_CANNOTBS_O() {
        return PAWNS_CANNOTBS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_CANNOTBS_E()
	 */
    @Override
	public double getPAWNS_CANNOTBS_E() {
        return PAWNS_CANNOTBS_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_PASSED_O()
	 */
    @Override
	public double getPAWNS_PASSED_O() {
        return PAWNS_PASSED_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_PASSED_E()
	 */
    @Override
	public double getPAWNS_PASSED_E() {
        return PAWNS_PASSED_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_PASSED_RNK_O()
	 */
    @Override
	public double getPAWNS_PASSED_RNK_O() {
        return PAWNS_PASSED_RNK_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_PASSED_RNK_E()
	 */
    @Override
	public double getPAWNS_PASSED_RNK_E() {
        return PAWNS_PASSED_RNK_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_UNSTOPPABLE_PASSER_O()
	 */
    @Override
	public double getPAWNS_UNSTOPPABLE_PASSER_O() {
        return PAWNS_UNSTOPPABLE_PASSER_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_UNSTOPPABLE_PASSER_E()
	 */
    @Override
	public double getPAWNS_UNSTOPPABLE_PASSER_E() {
        return PAWNS_UNSTOPPABLE_PASSER_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_CANDIDATE_RNK_O()
	 */
    @Override
	public double getPAWNS_CANDIDATE_RNK_O() {
        return PAWNS_CANDIDATE_RNK_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_CANDIDATE_RNK_E()
	 */
    @Override
	public double getPAWNS_CANDIDATE_RNK_E() {
        return PAWNS_CANDIDATE_RNK_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGS_PASSERS_F_O()
	 */
    @Override
	public double getKINGS_PASSERS_F_O() {
        return KINGS_PASSERS_F_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGS_PASSERS_F_E()
	 */
    @Override
	public double getKINGS_PASSERS_F_E() {
        return KINGS_PASSERS_F_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGS_PASSERS_FF_O()
	 */
    @Override
	public double getKINGS_PASSERS_FF_O() {
        return KINGS_PASSERS_FF_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGS_PASSERS_FF_E()
	 */
    @Override
	public double getKINGS_PASSERS_FF_E() {
        return KINGS_PASSERS_FF_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGS_PASSERS_F_OP_O()
	 */
    @Override
	public double getKINGS_PASSERS_F_OP_O() {
        return KINGS_PASSERS_F_OP_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGS_PASSERS_F_OP_E()
	 */
    @Override
	public double getKINGS_PASSERS_F_OP_E() {
        return KINGS_PASSERS_F_OP_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_ISLANDS_O()
	 */
    @Override
	public double getPAWNS_ISLANDS_O() {
        return PAWNS_ISLANDS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_ISLANDS_E()
	 */
    @Override
	public double getPAWNS_ISLANDS_E() {
        return PAWNS_ISLANDS_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_GARDS_O()
	 */
    @Override
	public double getPAWNS_GARDS_O() {
        return PAWNS_GARDS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_GARDS_E()
	 */
    @Override
	public double getPAWNS_GARDS_E() {
        return PAWNS_GARDS_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_GARDS_REM_O()
	 */
    @Override
	public double getPAWNS_GARDS_REM_O() {
        return PAWNS_GARDS_REM_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_GARDS_REM_E()
	 */
    @Override
	public double getPAWNS_GARDS_REM_E() {
        return PAWNS_GARDS_REM_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_STORMS_O()
	 */
    @Override
	public double getPAWNS_STORMS_O() {
        return PAWNS_STORMS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_STORMS_E()
	 */
    @Override
	public double getPAWNS_STORMS_E() {
        return PAWNS_STORMS_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_STORMS_CLS_O()
	 */
    @Override
	public double getPAWNS_STORMS_CLS_O() {
        return PAWNS_STORMS_CLS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_STORMS_CLS_E()
	 */
    @Override
	public double getPAWNS_STORMS_CLS_E() {
        return PAWNS_STORMS_CLS_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_OPENNED_O()
	 */
    @Override
	public double getPAWNS_OPENNED_O() {
        return PAWNS_OPENNED_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_OPENNED_E()
	 */
    @Override
	public double getPAWNS_OPENNED_E() {
        return PAWNS_OPENNED_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_SEMIOP_OWN_O()
	 */
    @Override
	public double getPAWNS_SEMIOP_OWN_O() {
        return PAWNS_SEMIOP_OWN_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_SEMIOP_OWN_E()
	 */
    @Override
	public double getPAWNS_SEMIOP_OWN_E() {
        return PAWNS_SEMIOP_OWN_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_SEMIOP_OP_O()
	 */
    @Override
	public double getPAWNS_SEMIOP_OP_O() {
        return PAWNS_SEMIOP_OP_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_SEMIOP_OP_E()
	 */
    @Override
	public double getPAWNS_SEMIOP_OP_E() {
        return PAWNS_SEMIOP_OP_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_WEAK_O()
	 */
    @Override
	public double getPAWNS_WEAK_O() {
        return PAWNS_WEAK_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS_WEAK_E()
	 */
    @Override
	public double getPAWNS_WEAK_E() {
        return PAWNS_WEAK_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getSPACE_O()
	 */
    @Override
	public double getSPACE_O() {
        return SPACE_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getSPACE_E()
	 */
    @Override
	public double getSPACE_E() {
        return SPACE_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOK_INFRONT_PASSER_O()
	 */
    @Override
	public double getROOK_INFRONT_PASSER_O() {
        return ROOK_INFRONT_PASSER_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOK_INFRONT_PASSER_E()
	 */
    @Override
	public double getROOK_INFRONT_PASSER_E() {
        return ROOK_INFRONT_PASSER_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOK_BEHIND_PASSER_O()
	 */
    @Override
	public double getROOK_BEHIND_PASSER_O() {
        return ROOK_BEHIND_PASSER_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOK_BEHIND_PASSER_E()
	 */
    @Override
	public double getROOK_BEHIND_PASSER_E() {
        return ROOK_BEHIND_PASSER_E;
    }

    
    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getBISHOPS_BAD_O()
	 */
    @Override
	public double getBISHOPS_BAD_O() {
        return BISHOPS_BAD_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getBISHOPS_BAD_E()
	 */
    @Override
	public double getBISHOPS_BAD_E() {
        return BISHOPS_BAD_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKNIGHT_OUTPOST_O()
	 */
    @Override
	public double getKNIGHT_OUTPOST_O() {
        return KNIGHT_OUTPOST_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKNIGHT_OUTPOST_E()
	 */
    @Override
	public double getKNIGHT_OUTPOST_E() {
        return KNIGHT_OUTPOST_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_OPENED_O()
	 */
    @Override
	public double getROOKS_OPENED_O() {
        return ROOKS_OPENED_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_OPENED_E()
	 */
    @Override
	public double getROOKS_OPENED_E() {
        return ROOKS_OPENED_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_SEMIOPENED_O()
	 */
    @Override
	public double getROOKS_SEMIOPENED_O() {
        return ROOKS_SEMIOPENED_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_SEMIOPENED_E()
	 */
    @Override
	public double getROOKS_SEMIOPENED_E() {
        return ROOKS_SEMIOPENED_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTROPISM_KNIGHT_O()
	 */
    @Override
	public double getTROPISM_KNIGHT_O() {
        return TROPISM_KNIGHT_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTROPISM_KNIGHT_E()
	 */
    @Override
	public double getTROPISM_KNIGHT_E() {
        return TROPISM_KNIGHT_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTROPISM_BISHOP_O()
	 */
    @Override
	public double getTROPISM_BISHOP_O() {
        return TROPISM_BISHOP_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTROPISM_BISHOP_E()
	 */
    @Override
	public double getTROPISM_BISHOP_E() {
        return TROPISM_BISHOP_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTROPISM_ROOK_O()
	 */
    @Override
	public double getTROPISM_ROOK_O() {
        return TROPISM_ROOK_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTROPISM_ROOK_E()
	 */
    @Override
	public double getTROPISM_ROOK_E() {
        return TROPISM_ROOK_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTROPISM_QUEEN_O()
	 */
    @Override
	public double getTROPISM_QUEEN_O() {
        return TROPISM_QUEEN_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTROPISM_QUEEN_E()
	 */
    @Override
	public double getTROPISM_QUEEN_E() {
        return TROPISM_QUEEN_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_7TH_2TH_O()
	 */
    @Override
	public double getROOKS_7TH_2TH_O() {
        return ROOKS_7TH_2TH_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_7TH_2TH_E()
	 */
    @Override
	public double getROOKS_7TH_2TH_E() {
        return ROOKS_7TH_2TH_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getQUEENS_7TH_2TH_O()
	 */
    @Override
	public double getQUEENS_7TH_2TH_O() {
        return QUEENS_7TH_2TH_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getQUEENS_7TH_2TH_E()
	 */
    @Override
	public double getQUEENS_7TH_2TH_E() {
        return QUEENS_7TH_2TH_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFETY_L1_O()
	 */
    @Override
	public double getKINGSAFETY_L1_O() {
        return KINGSAFETY_L1_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFETY_L1_E()
	 */
    @Override
	public double getKINGSAFETY_L1_E() {
        return KINGSAFETY_L1_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFETY_L2_O()
	 */
    @Override
	public double getKINGSAFETY_L2_O() {
        return KINGSAFETY_L2_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getKINGSAFETY_L2_E()
	 */
    @Override
	public double getKINGSAFETY_L2_E() {
        return KINGSAFETY_L2_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_KNIGHT_O()
	 */
    @Override
	public double getMOBILITY_KNIGHT_O() {
        return MOBILITY_KNIGHT_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_KNIGHT_E()
	 */
    @Override
	public double getMOBILITY_KNIGHT_E() {
        return MOBILITY_KNIGHT_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_BISHOP_O()
	 */
    @Override
	public double getMOBILITY_BISHOP_O() {
        return MOBILITY_BISHOP_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_BISHOP_E()
	 */
    @Override
	public double getMOBILITY_BISHOP_E() {
        return MOBILITY_BISHOP_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_ROOK_O()
	 */
    @Override
	public double getMOBILITY_ROOK_O() {
        return MOBILITY_ROOK_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_ROOK_E()
	 */
    @Override
	public double getMOBILITY_ROOK_E() {
        return MOBILITY_ROOK_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_QUEEN_O()
	 */
    @Override
	public double getMOBILITY_QUEEN_O() {
        return MOBILITY_QUEEN_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_QUEEN_E()
	 */
    @Override
	public double getMOBILITY_QUEEN_E() {
        return MOBILITY_QUEEN_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_KNIGHT_S_O()
	 */
    @Override
	public double getMOBILITY_KNIGHT_S_O() {
        return MOBILITY_KNIGHT_S_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_KNIGHT_S_E()
	 */
    @Override
	public double getMOBILITY_KNIGHT_S_E() {
        return MOBILITY_KNIGHT_S_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_BISHOP_S_O()
	 */
    @Override
	public double getMOBILITY_BISHOP_S_O() {
        return MOBILITY_BISHOP_S_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_BISHOP_S_E()
	 */
    @Override
	public double getMOBILITY_BISHOP_S_E() {
        return MOBILITY_BISHOP_S_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_ROOK_S_O()
	 */
    @Override
	public double getMOBILITY_ROOK_S_O() {
        return MOBILITY_ROOK_S_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_ROOK_S_E()
	 */
    @Override
	public double getMOBILITY_ROOK_S_E() {
        return MOBILITY_ROOK_S_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_QUEEN_S_O()
	 */
    @Override
	public double getMOBILITY_QUEEN_S_O() {
        return MOBILITY_QUEEN_S_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getMOBILITY_QUEEN_S_E()
	 */
    @Override
	public double getMOBILITY_QUEEN_S_E() {
        return MOBILITY_QUEEN_S_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_OP_O()
	 */
    @Override
	public double getPENETRATION_OP_O() {
        return PENETRATION_OP_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_OP_E()
	 */
    @Override
	public double getPENETRATION_OP_E() {
        return PENETRATION_OP_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_OP_S_O()
	 */
    @Override
	public double getPENETRATION_OP_S_O() {
        return PENETRATION_OP_S_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_OP_S_E()
	 */
    @Override
	public double getPENETRATION_OP_S_E() {
        return PENETRATION_OP_S_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_KING_O()
	 */
    @Override
	public double getPENETRATION_KING_O() {
        return PENETRATION_KING_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_KING_E()
	 */
    @Override
	public double getPENETRATION_KING_E() {
        return PENETRATION_KING_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_KING_S_O()
	 */
    @Override
	public double getPENETRATION_KING_S_O() {
        return PENETRATION_KING_S_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_KING_S_E()
	 */
    @Override
	public double getPENETRATION_KING_S_E() {
        return PENETRATION_KING_S_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_PAIR_H_O()
	 */
    @Override
	public double getROOKS_PAIR_H_O() {
        return ROOKS_PAIR_H_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_PAIR_H_E()
	 */
    @Override
	public double getROOKS_PAIR_H_E() {
        return ROOKS_PAIR_H_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_PAIR_V_O()
	 */
    @Override
	public double getROOKS_PAIR_V_O() {
        return ROOKS_PAIR_V_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getROOKS_PAIR_V_E()
	 */
    @Override
	public double getROOKS_PAIR_V_E() {
        return ROOKS_PAIR_V_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTRAP_O()
	 */
    @Override
	public double getTRAP_O() {
        return TRAP_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getTRAP_E()
	 */
    @Override
	public double getTRAP_E() {
        return TRAP_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_KING_O()
	 */
    @Override
	public double getPIN_KING_O() {
        return PIN_KING_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_KING_E()
	 */
    @Override
	public double getPIN_KING_E() {
        return PIN_KING_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_BIGGER_O()
	 */
    @Override
	public double getPIN_BIGGER_O() {
        return PIN_BIGGER_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_BIGGER_E()
	 */
    @Override
	public double getPIN_BIGGER_E() {
        return PIN_BIGGER_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_EQUAL_O()
	 */
    @Override
	public double getPIN_EQUAL_O() {
        return PIN_EQUAL_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_EQUAL_E()
	 */
    @Override
	public double getPIN_EQUAL_E() {
        return PIN_EQUAL_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_LOWER_O()
	 */
    @Override
	public double getPIN_LOWER_O() {
        return PIN_LOWER_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_LOWER_E()
	 */
    @Override
	public double getPIN_LOWER_E() {
        return PIN_LOWER_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getATTACK_BIGGER_O()
	 */
    @Override
	public double getATTACK_BIGGER_O() {
        return ATTACK_BIGGER_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getATTACK_BIGGER_E()
	 */
    @Override
	public double getATTACK_BIGGER_E() {
        return ATTACK_BIGGER_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getATTACK_EQUAL_O()
	 */
    @Override
	public double getATTACK_EQUAL_O() {
        return ATTACK_EQUAL_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getATTACK_EQUAL_E()
	 */
    @Override
	public double getATTACK_EQUAL_E() {
        return ATTACK_EQUAL_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getATTACK_LOWER_O()
	 */
    @Override
	public double getATTACK_LOWER_O() {
        return ATTACK_LOWER_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getATTACK_LOWER_E()
	 */
    @Override
	public double getATTACK_LOWER_E() {
        return ATTACK_LOWER_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getHUNGED_PIECE_O()
	 */
    @Override
	public double getHUNGED_PIECE_O() {
        return HUNGED_PIECE_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getHUNGED_PIECE_E()
	 */
    @Override
	public double getHUNGED_PIECE_E() {
        return HUNGED_PIECE_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getHUNGED_PAWNS_O()
	 */
    @Override
	public double getHUNGED_PAWNS_O() {
        return HUNGED_PAWNS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getHUNGED_PAWNS_E()
	 */
    @Override
	public double getHUNGED_PAWNS_E() {
        return HUNGED_PAWNS_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getHUNGED_ALL_O()
	 */
    @Override
	public double getHUNGED_ALL_O() {
        return HUNGED_ALL_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getHUNGED_ALL_E()
	 */
    @Override
	public double getHUNGED_ALL_E() {
        return HUNGED_ALL_E;
    }
}

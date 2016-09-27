package bagaturchess.engines.learning.cfg.weights;


import bagaturchess.learning.impl.eval.WeightsEvaluatorFactory;
import bagaturchess.learning.impl.eval.WeightsPawnsEvalFactory;
import bagaturchess.learning.impl.eval.cfg.IWeightsEvalConfig;


public class WeightsEvaluationConfig implements IWeightsEvalConfig {	
	
	public static final double KINGSAFE_CASTLING_O	=	7.018527092420808;
	public static final double KINGSAFE_CASTLING_E	=	0.0;

	public static final double KINGSAFE_FIANCHETTO_O	=	3.814482829896902;
	public static final double KINGSAFE_FIANCHETTO_E	=	0.0;

	public static final double BISHOPS_DOUBLE_O	=	26.410516559087743;
	public static final double BISHOPS_DOUBLE_E	=	50.36404037381933;

	public static final double KNIGHTS_DOUBLE_O	=	6.141925503528418;
	public static final double KNIGHTS_DOUBLE_E	=	0.8180726150679087;

	public static final double ROOKS_DOUBLE_O	=	34.239944052586836;
	public static final double ROOKS_DOUBLE_E	=	12.266938207024184;

	public static final double ROOKS_5PAWNS_O	=	1.1449463441379821;
	public static final double ROOKS_5PAWNS_E	=	0.7907087802572541;

	public static final double KNIGHTS_5PAWNS_O	=	1.4478340854891338;
	public static final double KNIGHTS_5PAWNS_E	=	13.649052417250422;

	public static final double KINGSAFE_F_O	=	-4.061304867448334;
	public static final double KINGSAFE_F_E	=	0.0;

	public static final double KINGSAFE_G_O	=	-8.93723287547486;
	public static final double KINGSAFE_G_E	=	0.0;

	public static final double KINGS_DISTANCE_O	=	-0.3608269754035846;
	public static final double KINGS_DISTANCE_E	=	-0.01;

	public static final double PAWNS_DOUBLED_O	=	-0.34616079998012406;
	public static final double PAWNS_DOUBLED_E	=	-0.7540698924520604;

	public static final double PAWNS_ISOLATED_O	=	-12.410797943382056;
	public static final double PAWNS_ISOLATED_E	=	-11.808026187675356;

	public static final double PAWNS_BACKWARD_O	=	-4.900460289317495;
	public static final double PAWNS_BACKWARD_E	=	-2.6560359481999165;

	public static final double PAWNS_SUPPORTED_O	=	3.8831239224473566;
	public static final double PAWNS_SUPPORTED_E	=	3.955544515227791;

	public static final double PAWNS_CANNOTBS_O	=	-1.615334828235159;
	public static final double PAWNS_CANNOTBS_E	=	-3.1436933681341754;

	public static final double PAWNS_PASSED_O	=	5.469733813150776;
	public static final double PAWNS_PASSED_E	=	2.151374727571095;

	public static final double PAWNS_PASSED_RNK_O	=	0.8820059889947125;
	public static final double PAWNS_PASSED_RNK_E	=	1.0070980141506483;

	public static final double PAWNS_UNSTOPPABLE_PASSER_O	=	0.0;
	public static final double PAWNS_UNSTOPPABLE_PASSER_E	=	550.0;

	public static final double PAWNS_CANDIDATE_RNK_O	=	0.504583178470483;
	public static final double PAWNS_CANDIDATE_RNK_E	=	0.9265848751343567;

	public static final double KINGS_PASSERS_F_O	=	0.0;
	public static final double KINGS_PASSERS_F_E	=	0.6207612376868458;

	public static final double KINGS_PASSERS_FF_O	=	0.0;
	public static final double KINGS_PASSERS_FF_E	=	0.6138705327132712;

	public static final double KINGS_PASSERS_F_OP_O	=	0.0;
	public static final double KINGS_PASSERS_F_OP_E	=	1.6111206753705305;

	public static final double PAWNS_ISLANDS_O	=	-1.4429702845313008;
	public static final double PAWNS_ISLANDS_E	=	-0.8019170966106466;

	public static final double PAWNS_GARDS_O	=	4.8241932201171185;
	public static final double PAWNS_GARDS_E	=	0.0;

	public static final double PAWNS_GARDS_REM_O	=	-5.112755567981873;
	public static final double PAWNS_GARDS_REM_E	=	0.0;

	public static final double PAWNS_STORMS_O	=	1.08775419681368;
	public static final double PAWNS_STORMS_E	=	0.0;

	public static final double PAWNS_STORMS_CLS_O	=	3.2110414498044624;
	public static final double PAWNS_STORMS_CLS_E	=	0.0;

	public static final double PAWNS_OPENNED_O	=	-41.87840279215143;
	public static final double PAWNS_OPENNED_E	=	0.0;

	public static final double PAWNS_SEMIOP_OWN_O	=	-25.690451910967116;
	public static final double PAWNS_SEMIOP_OWN_E	=	0.0;

	public static final double PAWNS_SEMIOP_OP_O	=	-12.068004273774859;
	public static final double PAWNS_SEMIOP_OP_E	=	0.0;

	public static final double PAWNS_WEAK_O	=	-1.710255536391454;
	public static final double PAWNS_WEAK_E	=	-0.3283715879321947;

	public static final double SPACE_O	=	0.9952928776229448;
	public static final double SPACE_E	=	0.6737790769917585;

	public static final double ROOK_INFRONT_PASSER_O	=	-0.08379657618696915;
	public static final double ROOK_INFRONT_PASSER_E	=	-0.0182706663912822;

	public static final double ROOK_BEHIND_PASSER_O	=	0.12207705647899722;
	public static final double ROOK_BEHIND_PASSER_E	=	13.138761519858772;
	
	
	public static final double BISHOPS_BAD_O	=	-1.188167722601592;
	public static final double BISHOPS_BAD_E	=	-1.1720921063387297;

	public static final double KNIGHT_OUTPOST_O	=	9.92070018698378;
	public static final double KNIGHT_OUTPOST_E	=	0.08281209480610474;

	public static final double ROOKS_OPENED_O	=	24.44190263549624;
	public static final double ROOKS_OPENED_E	=	0.4332001016930819;

	public static final double ROOKS_SEMIOPENED_O	=	9.0291425869115;
	public static final double ROOKS_SEMIOPENED_E	=	10.031969611296532;

	public static final double TROPISM_KNIGHT_O	=	0.07612308123602757;
	public static final double TROPISM_KNIGHT_E	=	0.0;

	public static final double TROPISM_BISHOP_O	=	0.3288706023701906;
	public static final double TROPISM_BISHOP_E	=	0.0;

	public static final double TROPISM_ROOK_O	=	0.39592106708864805;
	public static final double TROPISM_ROOK_E	=	0.0;

	public static final double TROPISM_QUEEN_O	=	0.21395911314372187;
	public static final double TROPISM_QUEEN_E	=	0.0;

	public static final double ROOKS_7TH_2TH_O	=	18.641856548809184;
	public static final double ROOKS_7TH_2TH_E	=	5.977158997305306;

	public static final double QUEENS_7TH_2TH_O	=	0.9282637364662676;
	public static final double QUEENS_7TH_2TH_E	=	10.384645339522729;

	public static final double KINGSAFETY_L1_O	=	36.51855013087701;
	public static final double KINGSAFETY_L1_E	=	0.0;

	public static final double KINGSAFETY_L2_O	=	18.24405623944656;
	public static final double KINGSAFETY_L2_E	=	0.0;

	public static final double MOBILITY_KNIGHT_O	=	0.23381577951816987;
	public static final double MOBILITY_KNIGHT_E	=	1.1304451836686797;

	public static final double MOBILITY_BISHOP_O	=	1.23451676645372;
	public static final double MOBILITY_BISHOP_E	=	0.9416816779081824;

	public static final double MOBILITY_ROOK_O	=	0.7931931668303283;
	public static final double MOBILITY_ROOK_E	=	1.2452038526965021;

	public static final double MOBILITY_QUEEN_O	=	0.18774489185452473;
	public static final double MOBILITY_QUEEN_E	=	1.0477815074718118;

	public static final double MOBILITY_KNIGHT_S_O	=	0.40258925894803105;
	public static final double MOBILITY_KNIGHT_S_E	=	0.7671547885808264;

	public static final double MOBILITY_BISHOP_S_O	=	0.4661369417429389;
	public static final double MOBILITY_BISHOP_S_E	=	0.2815246069695339;

	public static final double MOBILITY_ROOK_S_O	=	0.37714363269976464;
	public static final double MOBILITY_ROOK_S_E	=	0.7615013606710039;

	public static final double MOBILITY_QUEEN_S_O	=	0.7092750757091117;
	public static final double MOBILITY_QUEEN_S_E	=	1.2430047712338932;

	public static final double ROOKS_PAIR_H_O	=	2.9452786833387363;
	public static final double ROOKS_PAIR_H_E	=	0.39461229046915725;

	public static final double ROOKS_PAIR_V_O	=	2.113548534017563;
	public static final double ROOKS_PAIR_V_E	=	0.764932699776456;

	public static final double TRAP_O	=	-0.5228546950014155;
	public static final double TRAP_E	=	-1.1624992926923456;

	public static final double PIN_BK_O	=	10.0;
	public static final double PIN_BK_E	=	10.0;

	public static final double PIN_BQ_O	=	10.0;
	public static final double PIN_BQ_E	=	10.0;

	public static final double PIN_BR_O	=	10.0;
	public static final double PIN_BR_E	=	10.0;

	public static final double PIN_BN_O	=	10.0;
	public static final double PIN_BN_E	=	10.0;

	public static final double PIN_RK_O	=	36.944311374342135;
	public static final double PIN_RK_E	=	47.19369328902263;

	public static final double PIN_RQ_O	=	1.3171218132220082;
	public static final double PIN_RQ_E	=	0.9514498462414637;

	public static final double PIN_RB_O	=	17.417382186356928;
	public static final double PIN_RB_E	=	0.0;

	public static final double PIN_RN_O	=	0.4539812490720678;
	public static final double PIN_RN_E	=	2.354795185952967;

	public static final double PIN_QK_O	=	10.0;
	public static final double PIN_QK_E	=	10.0;

	public static final double PIN_QQ_O	=	10.0;
	public static final double PIN_QQ_E	=	10.0;

	public static final double PIN_QN_O	=	10.0;
	public static final double PIN_QN_E	=	10.0;

	public static final double PIN_QR_O	=	10.0;
	public static final double PIN_QR_E	=	10.0;

	public static final double PIN_QB_O	=	10.0;
	public static final double PIN_QB_E	=	10.0;

	public static final double ATTACK_BIGGER_O	=	31.957245932505337;
	public static final double ATTACK_BIGGER_E	=	50.0;

	public static final double ATTACK_EQ_O	=	21.811291758457845;
	public static final double ATTACK_EQ_E	=	17.335787749036037;

	public static final double ATTACK_LOWER_O	=	0.40746519933435665;
	public static final double ATTACK_LOWER_E	=	25.14628665177842;

	public static final double HUNGED_PIECE_O	=	0.0;
	public static final double HUNGED_PIECE_E	=	0.0;

	public static final double HUNGED_PAWS_O	=	0.0;
	public static final double HUNGED_PAWS_E	=	0.0;

	public static final double HUNGED_ALL_O	=	0.0;
	public static final double HUNGED_ALL_E	=	0.0;
    
    
	public WeightsEvaluationConfig() {
		
	}
	
	
	public WeightsEvaluationConfig(String[] args) {
		
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
		return WeightsEvaluatorFactory.class.getName();
	}
	
	
	@Override
	public String getPawnsCacheFactoryClassName() {
		return WeightsPawnsEvalFactory.class.getName();
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
        return ROOKS_5PAWNS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS5_ROOKS_E()
	 */
    @Override
	public double getPAWNS5_ROOKS_E() {
        return ROOKS_5PAWNS_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS5_KNIGHTS_O()
	 */
    @Override
	public double getPAWNS5_KNIGHTS_O() {
        return KNIGHTS_5PAWNS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPAWNS5_KNIGHTS_E()
	 */
    @Override
	public double getPAWNS5_KNIGHTS_E() {
        return KNIGHTS_5PAWNS_E;
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
        return 0;//PENETRATION_OP_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_OP_E()
	 */
    @Override
	public double getPENETRATION_OP_E() {
    	return 0;//return PENETRATION_OP_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_OP_S_O()
	 */
    @Override
	public double getPENETRATION_OP_S_O() {
    	return 0;//return PENETRATION_OP_S_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_OP_S_E()
	 */
    @Override
	public double getPENETRATION_OP_S_E() {
    	return 0;//return PENETRATION_OP_S_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_KING_O()
	 */
    @Override
	public double getPENETRATION_KING_O() {
    	return 0;//return PENETRATION_KING_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_KING_E()
	 */
    @Override
	public double getPENETRATION_KING_E() {
    	return 0;//return PENETRATION_KING_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_KING_S_O()
	 */
    @Override
	public double getPENETRATION_KING_S_O() {
    	return 0;//return PENETRATION_KING_S_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPENETRATION_KING_S_E()
	 */
    @Override
	public double getPENETRATION_KING_S_E() {
    	return 0;//return PENETRATION_KING_S_E;
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
    	return 0;//return PIN_KING_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_KING_E()
	 */
    @Override
	public double getPIN_KING_E() {
    	return 0;//return PIN_KING_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_BIGGER_O()
	 */
    @Override
	public double getPIN_BIGGER_O() {
    	return 0;//return PIN_BIGGER_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_BIGGER_E()
	 */
    @Override
	public double getPIN_BIGGER_E() {
    	return 0;//return PIN_BIGGER_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_EQUAL_O()
	 */
    @Override
	public double getPIN_EQUAL_O() {
    	return 0;//return PIN_EQUAL_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_EQUAL_E()
	 */
    @Override
	public double getPIN_EQUAL_E() {
    	return 0;//return PIN_EQUAL_E;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_LOWER_O()
	 */
    @Override
	public double getPIN_LOWER_O() {
        return 0;//return PIN_LOWER_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getPIN_LOWER_E()
	 */
    @Override
	public double getPIN_LOWER_E() {
    	return 0;//return PIN_LOWER_E;
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
        return ATTACK_EQ_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getATTACK_EQUAL_E()
	 */
    @Override
	public double getATTACK_EQUAL_E() {
        return ATTACK_EQ_E;
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
        return HUNGED_PAWS_O;
    }

    /* (non-Javadoc)
	 * @see bagaturchess.learning.impl.eval.cfg.IWeights#getHUNGED_PAWNS_E()
	 */
    @Override
	public double getHUNGED_PAWNS_E() {
        return HUNGED_PAWS_E;
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

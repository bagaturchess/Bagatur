package bagaturchess.engines.learning.cfg.weights.evaltune;

import java.util.StringTokenizer;

public class NamesGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String command = "=bagaturchess.search.impl.alg.impl0.SearchMTD0 bagaturchess.engines.bagatur.cfg.search.SearchConfigImpl_MTD bagaturchess.engines.learning.cfg.weights.boardtune.WeightsBoardConfig_LKG0_1 bagaturchess.engines.learning.cfg.weights.evaltune.WeightsEvaluationConfig_LKG -e ";
		String percent = "1.00";
		
		StringTokenizer tokanizer = new StringTokenizer(attributes, ";");
		while (tokanizer.hasMoreTokens()) {
			String line = tokanizer.nextToken();
			line = line.substring(16);
			int index = line.indexOf("=");
			line = line.substring(0, index);
			line = line.trim();
			
			String c1 = line + "+" + percent + command + line + "=" + percent;
			String c2 = line + "-" + percent + command + line + "=-" + percent;
			
			System.out.println(c1);
			System.out.println(c2);
			
		}
	}

	private static final String attributes = "" + 
	"public double KINGSAFE_CASTLING_O = 8.695473272912482;\r\n" + 
    "public double KINGSAFE_CASTLING_E = 0.0;\r\n" + 
    "public double KINGSAFE_FIANCHETTO_O = 30;\r\n" + 
    "public double KINGSAFE_FIANCHETTO_E = 0.0;\r\n" + 
    "public double BISHOPS_DOUBLE_O = 38.55304785134276;\r\n" + 
    "public double BISHOPS_DOUBLE_E = 58.2527533291309;\r\n" + 
    "public double KNIGHTS_DOUBLE_O = -0.8418948768316152;\r\n" + 
    "public double KNIGHTS_DOUBLE_E = -0.3541363041112693;\r\n" + 
    "public double ROOKS_DOUBLE_O = -0.6394803203763532;\r\n" + 
    "public double ROOKS_DOUBLE_E = -0.9391348699444786;\r\n" + 
    "public double PAWNS5_ROOKS_O = -0.5390982511872409;\r\n" + 
    "public double PAWNS5_ROOKS_E = -1.3387240561386002;\r\n" + 
    "public double PAWNS5_KNIGHTS_O = 3.699203461185335;\r\n" + 
    "public double PAWNS5_KNIGHTS_E = 7.4901092619848955;\r\n" + 
    "public double KINGSAFE_F_O = -6.135834830407696;\r\n" + 
    "public double KINGSAFE_F_E = 0.0;\r\n" + 
    "public double KINGSAFE_G_O = -0.031287654466305076;\r\n" + 
    "public double KINGSAFE_G_E = 0.0;\r\n" + 
    "public double KINGS_DISTANCE_O = 0;\r\n" + 
    "public double KINGS_DISTANCE_E = 0.3593091534579083;\r\n" + 
    "public double PAWNS_DOUBLED_O = -0.06344121207625283;\r\n" + 
    "public double PAWNS_DOUBLED_E = -14.29780586088556;\r\n" + 
    "public double PAWNS_ISOLATED_O = -14.081104147241549;\r\n" + 
    "public double PAWNS_ISOLATED_E = -11.146861253903177;\r\n" + 
    "public double PAWNS_BACKWARD_O = -6.375578895582516;\r\n" + 
    "public double PAWNS_BACKWARD_E = -2.2179199051395857;\r\n" + 
    "public double PAWNS_SUPPORTED_O = 3.609065058083699;\r\n" + 
    "public double PAWNS_SUPPORTED_E = 3.3465159357916567;\r\n" + 
    "public double PAWNS_CANNOTBS_O = -1.6863498502616525;\r\n" + 
    "public double PAWNS_CANNOTBS_E = -0.5308967547913821;\r\n" + 
    "public double PAWNS_PASSED_O = 4.680477377823983;\r\n" + 
    "public double PAWNS_PASSED_E = 3.4342682149594577;\r\n" + 
    "public double PAWNS_PASSED_RNK_O = 1.1424944013732647;\r\n" + 
    "public double PAWNS_PASSED_RNK_E = 1.4706529633174854;\r\n" + 
    "public double PAWNS_UNSTOPPABLE_PASSER_O = 0.0;\r\n" + 
    "public double PAWNS_UNSTOPPABLE_PASSER_E = 550.0;\r\n" + 
    "public double PAWNS_CANDIDATE_RNK_O = 1.9256140108109243;\r\n" + 
    "public double PAWNS_CANDIDATE_RNK_E = 1.3898951461659623;\r\n" + 
    "public double KINGS_PASSERS_F_O = 0.0;\r\n" + 
    "public double KINGS_PASSERS_F_E = 1.771641359692092;\r\n" + 
    "public double KINGS_PASSERS_FF_O = 0.0;\r\n" + 
    "public double KINGS_PASSERS_FF_E = 1.0573809123088491;\r\n" + 
    "public double KINGS_PASSERS_F_OP_O = 0.0;\r\n" + 
    "public double KINGS_PASSERS_F_OP_E = 1.664989299655242;\r\n" + 
    "public double PAWNS_ISLANDS_O = -1.1426560677936877;\r\n" + 
    "public double PAWNS_ISLANDS_E = -0.6574727778250118;\r\n" + 
    "public double PAWNS_GARDS_O = 15.095304223518145;\r\n" + 
    "public double PAWNS_GARDS_E = 0.0;\r\n" + 
    "public double PAWNS_GARDS_REM_O = -6.817067617714686;\r\n" + 
    "public double PAWNS_GARDS_REM_E = 0.0;\r\n" + 
    "public double PAWNS_STORMS_O = 3.6562511086470018;\r\n" + 
    "public double PAWNS_STORMS_E = 0.0;\r\n" + 
    "public double PAWNS_STORMS_CLS_O = 1.7528654000457136;\r\n" + 
    "public double PAWNS_STORMS_CLS_E = 0.0;\r\n" + 
    "public double PAWNS_OPENNED_O = -36.51630475211046;\r\n" + 
    "public double PAWNS_OPENNED_E = 0.0;\r\n" + 
    "public double PAWNS_SEMIOP_OWN_O = -27.61263467545501;\r\n" + 
    "public double PAWNS_SEMIOP_OWN_E = 0.0;\r\n" + 
    "public double PAWNS_SEMIOP_OP_O = -11.243766767056155;\r\n" + 
    "public double PAWNS_SEMIOP_OP_E = 0.0;\r\n" + 
    "public double PAWNS_WEAK_O = -1.56049594965303;\r\n" + 
    "public double PAWNS_WEAK_E = -0.11469942441058452;\r\n" + 
    "public double SPACE_O = 0.4842269012003156;\r\n" + 
    "public double SPACE_E = 1.06495242638572;\r\n" + 
    "public double ROOK_INFRONT_PASSER_O = -26.281968971663698;\r\n" + 
    "public double ROOK_INFRONT_PASSER_E = -0.7874043073770787;\r\n" + 
    "public double ROOK_BEHIND_PASSER_O = 0.16400430403572036;\r\n" + 
    "public double ROOK_BEHIND_PASSER_E = 0.370505576804776;\r\n" + 
    "public double PST_PAWN_O = 0.5710827986385989;\r\n" + 
    "public double PST_PAWN_E = 0.9927040298136856;\r\n" + 
    "public double PST_KING_O = 1.4152868740093765;\r\n" + 
    "public double PST_KING_E = 1.6886842944327374;\r\n" + 
    "public double PST_KNIGHTS_O = 1.020352936358533;\r\n" + 
    "public double PST_KNIGHTS_E = 1.2065438212857906;\r\n" + 
    "public double PST_BISHOPS_O = 1.1449938687083554;\r\n" + 
    "public double PST_BISHOPS_E = 1.587546500773107;\r\n" + 
    "public double PST_ROOKS_O = 1.862971446158503;\r\n" + 
    "public double PST_ROOKS_E = 0.7278444261035139;\r\n" + 
    "public double PST_QUEENS_O = 0.10831914054754384;\r\n" + 
    "public double PST_QUEENS_E = 0.8606995592185696;\r\n" + 
    "public double BISHOPS_BAD_O = -0.47203138436696224;\r\n" + 
    "public double BISHOPS_BAD_E = -0.6447870737777084;\r\n" + 
    "public double KNIGHT_OUTPOST_O = 6.189159441831243;\r\n" + 
    "public double KNIGHT_OUTPOST_E = 1.4890542689350073;\r\n" + 
    "public double ROOKS_OPENED_O = 24.64660545935084;\r\n" + 
    "public double ROOKS_OPENED_E = 2.9523862741844824;\r\n" + 
    "public double ROOKS_SEMIOPENED_O = 16.68542672912037;\r\n" + 
    "public double ROOKS_SEMIOPENED_E = 1.0138229289325038;\r\n" + 
    "public double TROPISM_KNIGHT_O = 0.09900906859684694;\r\n" + 
    "public double TROPISM_KNIGHT_E = 0.0;\r\n" + 
    "public double TROPISM_BISHOP_O = 0.4844399390677248;\r\n" + 
    "public double TROPISM_BISHOP_E = 0.0;\r\n" + 
    "public double TROPISM_ROOK_O = 0.6719263432348239;\r\n" + 
    "public double TROPISM_ROOK_E = 0.0;\r\n" + 
    "public double TROPISM_QUEEN_O = 0.12101756508375879;\r\n" + 
    "public double TROPISM_QUEEN_E = 0.0;\r\n" + 
    "public double ROOKS_7TH_2TH_O = 14.098363515907408;\r\n" + 
    "public double ROOKS_7TH_2TH_E = 25.959258182976324;\r\n" + 
    "public double QUEENS_7TH_2TH_O = 6.3427421276523095;\r\n" + 
    "public double QUEENS_7TH_2TH_E = 9.95734336974601;\r\n" + 
    "public double KINGSAFETY_L1_O = 50.79324915525265;\r\n" + 
    "public double KINGSAFETY_L1_E = 0.0;\r\n" + 
    "public double KINGSAFETY_L2_O = 7.139658663877436;\r\n" + 
    "public double KINGSAFETY_L2_E = 0.0;\r\n" + 
    "public double MOBILITY_KNIGHT_O = 0.799920552085603;\r\n" + 
    "public double MOBILITY_KNIGHT_E = 1.4023260453044764;\r\n" + 
    "public double MOBILITY_BISHOP_O = 0.7140264083199767;\r\n" + 
    "public double MOBILITY_BISHOP_E = 1.1880883496711263;\r\n" + 
    "public double MOBILITY_ROOK_O = 0.8519327926189777;\r\n" + 
    "public double MOBILITY_ROOK_E = 1.3656293704586049;\r\n" + 
    "public double MOBILITY_QUEEN_O = 0.038489031564274016;\r\n" + 
    "public double MOBILITY_QUEEN_E = 0.7969717663469346;\r\n" + 
    "public double MOBILITY_KNIGHT_S_O = 0.5701417732919651;\r\n" + 
    "public double MOBILITY_KNIGHT_S_E = 1.5943119808282296;\r\n" + 
    "public double MOBILITY_BISHOP_S_O = 0.5236869290177378;\r\n" + 
    "public double MOBILITY_BISHOP_S_E = 0.702074640076866;\r\n" + 
    "public double MOBILITY_ROOK_S_O = 0.4436233529865903;\r\n" + 
    "public double MOBILITY_ROOK_S_E = 1.6241039419399184;\r\n" + 
    "public double MOBILITY_QUEEN_S_O = 0.08976717176007516;\r\n" + 
    "public double MOBILITY_QUEEN_S_E = 0.7694076178835493;\r\n" + 
    "public double PENETRATION_OP_O = 0.17824204653201886;\r\n" + 
    "public double PENETRATION_OP_E = 0.0;\r\n" + 
    "public double PENETRATION_OP_S_O = 0.22735909034148527;\r\n" + 
    "public double PENETRATION_OP_S_E = 0.0;\r\n" + 
    "public double PENETRATION_KING_O = 0.1379869316404612;\r\n" + 
    "public double PENETRATION_KING_E = 0.0;\r\n" + 
    "public double PENETRATION_KING_S_O = 0.13623177607415432;\r\n" + 
    "public double PENETRATION_KING_S_E = 0.0;\r\n" + 
    "public double ROOKS_PAIR_H_O = 9.141756627196248;\r\n" + 
    "public double ROOKS_PAIR_H_E = 1.7208904465692263;\r\n" + 
    "public double ROOKS_PAIR_V_O = 0.20986129053952293;\r\n" + 
    "public double ROOKS_PAIR_V_E = 0.49629572742464223;\r\n" + 
    
    "public double TRAP_O 			= 2.3667991106010713;\r\n" + 
    "public double TRAP_E 			= 0.4438235169149752;\r\n" + 
    "public double PIN_KING_O 		= 4.400084487677882;\r\n" + 
    "public double PIN_KING_E 		= 4.763515095856501;\r\n" + 
    "public double PIN_BIGGER_O 	= 4.8010112003917484;\r\n" + 
    "public double PIN_BIGGER_E	 	= 6.4066615810464205;\r\n" + 
    "public double PIN_EQUAL_O 		= 4.94761907883406;\r\n" + 
    "public double PIN_EQUAL_E 		= 9.559151201492769;\r\n" + 
    "public double PIN_LOWER_O 		= 0.7369572454951826;\r\n" + 
    "public double PIN_LOWER_E 		= 3.0522002261614265;\r\n" + 
    "public double ATTACK_BIGGER_O	= 4.232728105857094;\r\n" + 
    "public double ATTACK_BIGGER_E 	= 22.731304482994883;\r\n" + 
    "public double ATTACK_EQUAL_O 	= 5.797031610592222;\r\n" + 
    "public double ATTACK_EQUAL_E 	= 12.082858644052445;\r\n" + 
    "public double ATTACK_LOWER_O 	= 2.2924847990005928;\r\n" + 
    "public double ATTACK_LOWER_E 	= 6.533588111226228;\r\n" + 
    "public double HUNGED_PIECE_O 	= 5.035823470670232;\r\n" + 
    "public double HUNGED_PIECE_E 	= 10.387603531653903;\r\n" + 
    "public double HUNGED_PAWNS_O 	= 0.8950906090115673;\r\n" + 
    "public double HUNGED_PAWNS_E	= 5.317972174452671;\r\n" + 
    "public double HUNGED_ALL_O 	= 2.336728836626604;\r\n" + 
    "public double HUNGED_ALL_E 	= 7.995315715674308;";
}

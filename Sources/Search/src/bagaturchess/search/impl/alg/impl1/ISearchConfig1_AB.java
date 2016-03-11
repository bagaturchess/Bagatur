package bagaturchess.search.impl.alg.impl1;


import bagaturchess.search.api.ISearchConfig_AB;


public interface ISearchConfig1_AB extends ISearchConfig_AB {
	
	public boolean getPV_Pruning_MateDistance();
	public boolean getNONPV_Pruning_MateDistance();
	
	public boolean getPV_Pruning_NullMove();
	public boolean getNONPV_Pruning_NullMove();
	
	public int getPV_Pruning_NullMove_Margin();
	public int getNONPV_Pruning_NullMove_Margin();
	
	public boolean getPV_Optimization_TPTScores();
	public boolean getNONPV_Optimization_TPTScores();
	
	public int getPV_reduction_lmr1();
	public int getNONPV_reduction_lmr1();
	
	public int getPV_reduction_lmr2();
	public int getNONPV_reduction_lmr2();
	
	public boolean getNONPV_reduction_too_good_scores();
	
	public boolean getPV_QSearch_Pruning_MateDistance();
	public boolean getNONPV_QSearch_Pruning_MateDistance();
	
	public boolean getPV_QSearch_Optimization_TPTScores();
	public boolean getNONPV_QSearch_Optimization_TPTScores();

	public boolean getPV_QSearch_Use_SEE();
	public boolean getNONPV_QSearch_Use_SEE();
	
	public boolean getPV_QSearch_Move_Checks();
	public boolean getNONPV_QSearch_Move_Checks();
	
	public boolean getPV_QSearch_Use_Queen_Material_Margin();
	public boolean getNONPV_QSearch_Use_Queen_Material_Margin();
	
	public boolean getPV_QSearch_Store_TPT_Scores();
	public boolean getNONPV_QSearch_Store_TPT_Scores();
	
	//public boolean getPV_Pruning_Futiliy();
	public boolean getNONPV_Pruning_Futiliy();
}

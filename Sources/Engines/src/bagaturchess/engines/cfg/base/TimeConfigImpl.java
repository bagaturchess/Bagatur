package bagaturchess.engines.cfg.base;


import bagaturchess.uci.api.ITimeConfig;


public class TimeConfigImpl implements ITimeConfig {
	
	
	private double timeoptimization_ConsumedTimeVSRemainingTimePercent 	= 0.50;
	private int moveEvallDiff_MaxScoreDiff 								= 150;
	private double moveEvallDiff_MaxTotalTimeUsagePercent				= 0.25;
	
	
	public TimeConfigImpl() {
		
	}
	
	
	@Override
	public int getMoveEvallDiff_MaxScoreDiff() {
		return moveEvallDiff_MaxScoreDiff;
	}


	@Override
	public double getMoveEvallDiff_MaxTotalTimeUsagePercent() {
		return moveEvallDiff_MaxTotalTimeUsagePercent;
	}
	
	
	@Override
	public double getTimeoptimization_ConsumedTimeVSRemainingTimePercent() {
		return timeoptimization_ConsumedTimeVSRemainingTimePercent;
	}
}

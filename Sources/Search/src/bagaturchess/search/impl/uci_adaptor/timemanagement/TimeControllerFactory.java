package bagaturchess.search.impl.uci_adaptor.timemanagement;


import bagaturchess.bitboard.impl.Figures;
import bagaturchess.search.impl.uci_adaptor.timemanagement.controllers.TimeController_FixedNodes;
import bagaturchess.search.impl.uci_adaptor.timemanagement.controllers.TimeController_IncrementPerMove;
import bagaturchess.search.impl.uci_adaptor.timemanagement.controllers.TimeController_FixedDepth;
import bagaturchess.search.impl.uci_adaptor.timemanagement.controllers.TimeController_Infinite;
import bagaturchess.search.impl.uci_adaptor.timemanagement.controllers.TimeController_SuddenDeath;
import bagaturchess.search.impl.uci_adaptor.timemanagement.controllers.TimeController_TimePerMove;
import bagaturchess.search.impl.uci_adaptor.timemanagement.controllers.TimeController_Tournament;
import bagaturchess.uci.api.ITimeConfig;
import bagaturchess.uci.impl.commands.Go;


public class TimeControllerFactory extends ITimeControllerType {
	
	
	public static final ITimeController createTimeController(ITimeConfig timeconfig, int colourToMove, Go goCommand) {
		
		int type = getControllerType(colourToMove, goCommand);
		
		ITimeController result = null;
		switch (type) {
			case INFINITE:
				result = new TimeController_Infinite();
				break;
			case FIXED_DEPTH:
				result =  new TimeController_FixedDepth();
				break;
			case FIXED_NODES:
				result =  new TimeController_FixedNodes();
				break;
			case TIME_PER_MOVE:
				result =  new TimeController_TimePerMove(goCommand.getMovetime());
				break;
			case INCREMENT_PER_MOVE:
				result =  new TimeController_IncrementPerMove(timeconfig);
				((TimeController_IncrementPerMove)result).setupMinMoveTimeAndTotalClockTime(goCommand, colourToMove);
				break;
			case TOURNAMENT:
				result =  new TimeController_Tournament(timeconfig, goCommand.getMovestogo());
				((TimeController_Tournament)result).setupMinMoveTimeAndTotalClockTime(goCommand, colourToMove);
				break;
			case SUDDEN_DEATH:
				result =  new TimeController_SuddenDeath();
				((TimeController_SuddenDeath)result).setupMinMoveTimeAndTotalClockTime(goCommand, colourToMove);
				break;
			default:
				throw new IllegalStateException("Cannot determine the TimeController_IncrementPerMove type for go command: " + goCommand);
		}
		
		return result;
	}
	
	
	private static int getControllerType(int colourToMove, Go goCommand) {
		int type = -1;
		if (goCommand.isAnalyzingMode()) {
			type = INFINITE;
		} else if (goCommand.hasDepth()) {
			type = FIXED_DEPTH;
		} else if (goCommand.hasNodes()) {
			type = FIXED_NODES;
		} else if (goCommand.getMovetime() != -1) {
			type = TIME_PER_MOVE;
		} else if (((colourToMove == Figures.COLOUR_WHITE) && goCommand.getWinc() > 0) || ((colourToMove == Figures.COLOUR_BLACK) && goCommand.getBinc() > 0)) {
			type = INCREMENT_PER_MOVE;
		} else if (goCommand.getMovestogo() != -1) {
			type = TOURNAMENT;
		} else {
			type = SUDDEN_DEATH;
		}
		return type;
	}
}

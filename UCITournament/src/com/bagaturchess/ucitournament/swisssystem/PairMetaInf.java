package com.bagaturchess.ucitournament.swisssystem;


public class PairMetaInf {
	
	
	private EngineMetaInf white;
	private EngineMetaInf black;
	private int eval;


	public PairMetaInf(EngineMetaInf _white, EngineMetaInf _black) {
		white = _white;
		black = _black;
	}


	public EngineMetaInf getWhite() {
		return white;
	}


	public EngineMetaInf getBlack() {
		return black;
	}
	
	
	public int getEval() {
		return eval;
	}


	public void setEval(int eval) {
		this.eval = eval;
	}
}


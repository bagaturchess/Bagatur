package com.bagaturchess.ucitournament.swisssystem;


public class PairingEntry {
	
	
	public static final String RESULT_NOT_AVAILABLE = "N/A";
	
	
	private String white;
	private String black;
	private String result;
	
	
	public PairingEntry(String _white, String _black) {
		this(_white, _black, RESULT_NOT_AVAILABLE);
	}
	
	
	public PairingEntry(String _white, String _black, String _result) {
		white = _white;
		black = _black;
		result = _result;
	}
	
	
	public String getWhiteEngineName() {
		return white;
	}
	
	
	public String getBlackEngineName() {
		return black;
	}
	
	
	public String getResult() {
		return result;
	}
	
	
	public void setResult(String _result) {
		if (!_result.equals("1-0") && !_result.equals("0-1") && !_result.equals("1/2-1/2")) {
			throw new IllegalStateException("_result=" + _result);
		}
		result = _result;
	}
	
	
	public String asString() {
		String msg = "";
		msg += getWhiteEngineName() + ",	" + getBlackEngineName() + ",	" + getResult();
		return msg;
	}
	
	
	public String toString() {
		String msg = "";
		msg += getWhiteEngineName() + "	" + getBlackEngineName() + "	" + getResult();
		return msg;
	}
}

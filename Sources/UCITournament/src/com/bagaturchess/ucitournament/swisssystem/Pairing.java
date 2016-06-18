package com.bagaturchess.ucitournament.swisssystem;


import java.util.List;


public class Pairing {
	
	
	private List<PairingEntry> pairing;
	
	
	public Pairing(List<PairingEntry> _pairing) {
		pairing = _pairing;
	}
	
	
	public List<PairingEntry> getEntries() {
		return pairing;
	}
	
	
	public String toString() {
		String msg = "";
		for (PairingEntry pair: pairing) {
			msg += pair.toString();
			msg += "\r\n";
		}
		return msg;
	}
}

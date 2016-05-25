/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */
package bagaturchess.search.impl.uci_adaptor;


import bagaturchess.search.api.IRootSearch;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.commands.Go;


public class UCISearchMediatorImpl_OpponentPondering extends UCISearchMediatorImpl_Base {
	
	
	public UCISearchMediatorImpl_OpponentPondering(IChannel _channel, Go _go,
			int _colourToMove, BestMoveSender _sender, IRootSearch _rootSearch, boolean isEndlessSearch) {
		super(_channel, _go, _colourToMove, _sender, _rootSearch, isEndlessSearch);
		setStopper(new PonderingStopper());
	}
	
	
	@Override
	public void send(String messageToGUI) {
		//Send just log information
		getChannel().sendLogToGUI(messageToGUI);
	}
}

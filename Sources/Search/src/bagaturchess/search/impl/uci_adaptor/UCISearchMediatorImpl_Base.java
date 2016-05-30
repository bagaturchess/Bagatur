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


import java.io.IOException;

import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInfoUtils;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.commands.Go;


public abstract class UCISearchMediatorImpl_Base implements ISearchMediator {
	
	
	private IChannel channel;
	private Go goCommand;
	private int colourToMove;
	private ISearchStopper stopper;
	private BestMoveSender sender;
	protected IRootSearch rootSearch;
	
	private ISearchInfo lastinfo;
	private ISearchInfo[] last3infos;
	private long startTime;
	
	private String nextMinorLine;
	private long lastSentMinorInfo_timestamp;
	
	private boolean isEndlessSearch;
	
	
	public UCISearchMediatorImpl_Base(IChannel _channel, Go _go, int _colourToMove, BestMoveSender _sender,
			IRootSearch _rootSearch, boolean _isEndlessSearch) {
		
		channel = _channel;
		goCommand = _go;
		colourToMove = _colourToMove;
		sender = _sender;
		rootSearch = _rootSearch;
		isEndlessSearch = _isEndlessSearch;
		
		last3infos = new ISearchInfo[3];
		
		startTime = System.currentTimeMillis();
	}
	
	
	@Override
	public void registerInfoObject(ISearchInfo info) {
		//throw new IllegalStateException();
	}
	
	
	protected long getStartTime() {
		return startTime;
	}
	
	
	protected IChannel getChannel() {
		return channel;
	}
	
	public void startIteration(int iteration) {
	}
	
	public int getColourToMove() {
		return colourToMove;
	}
	
	public Go getGoCommand() {
		return goCommand;
	}
	
	public ISearchInfo getLastInfo() {
		return lastinfo;
	}
	
	public BestMoveSender getBestMoveSender() {
		return sender;
	}
	
	
	public void setLastInfo(ISearchInfo info) {
		throw new IllegalStateException();
	}
	
	
	public void changedMajor(ISearchInfo info) {
		
		if (!info.isUpperBound()) {
			lastinfo = info;
		}
		
		String message = SearchInfoUtils.buildMajorInfoCommand(info, getStartTime(), rootSearch.getTPTUsagePercent(), 0);
		send(message);
		
		stopIfMateIsFound();
	}
	
	
	public void changedMinor(ISearchInfo info) {
		nextMinorLine = SearchInfoUtils.buildMinorInfoCommand(info, getStartTime(), rootSearch.getTPTUsagePercent(), 0);
		if (nextMinorLine != null) {
			long timestamp = System.currentTimeMillis();
			if (timestamp > lastSentMinorInfo_timestamp + 1000 /*Update UI, once per second*/) {
				lastSentMinorInfo_timestamp = timestamp;
				send(nextMinorLine);
			}
		}
	}
	
	
	@Override
	public ISearchStopper getStopper() {
		return stopper;
	}
	
	@Override
	public void setStopper(ISearchStopper _stopper) {
		stopper = _stopper;
	}
	
	public void dump(String msg) {
		//channel.sendLogToGUI(msg);
		channel.dump(msg);
	}
	
	public void dump(Throwable t) {
		channel.dump(t);
	}
	
	/**
	 * PRIVATE METHODS 
	 * 
	 */	
	@Override
	public void send(String messageToGUI) {
		try {
			channel.sendCommandToGUI(messageToGUI);
		} catch (IOException e) {
			channel.dump(e);
		}
	}

	
	private void stopIfMateIsFound() {
		
		if (isEndlessSearch) {
			return;
		}
		
		if (stopper != null && stopper instanceof GlobalStopperImpl) {
			synchronized (stopper) {
				if (!stopper.isStopped()) {
					last3infos[0] = last3infos[1]; 
					last3infos[1] = last3infos[2];
					last3infos[2] = lastinfo;
					
					if (last3infos[0] != null && last3infos[1] != null && last3infos[2] != null) {
						if (last3infos[0].isMateScore() && last3infos[1].isMateScore() && last3infos[2].isMateScore()) {
							if (last3infos[0].getMateScore() == last3infos[1].getMateScore() && last3infos[1].getMateScore() == last3infos[2].getMateScore()) {
								//if (last3infos[0].getMateScore() > 0) {
									if (last3infos[0].getDepth() != last3infos[1].getDepth() && last3infos[1].getDepth() != last3infos[2].getDepth()) {
										getStopper().markStopped();
										
										rootSearch.stopSearchAndWait();
										
										sender.sendBestMove();
									}
								//}
							}
						}
					}
				}
			}
		}
	}
	
	
	@Override
	public int getTrustWindow_BestMove() {
		throw new IllegalStateException();
	}
	
	
	@Override
	public int getTrustWindow_AlphaAspiration() {
		throw new IllegalStateException();
	}
	
	
	@Override
	public int getTrustWindow_MTD_Step() {
		throw new IllegalStateException();
	}
}

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

import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.search.api.ISearchConfig_MTD;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInfoUtils;
import bagaturchess.search.impl.tpt.TPTable;
import bagaturchess.uci.api.BestMoveSender;
import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.commands.Go;


public abstract class UCISearchMediatorImpl_Base implements ISearchMediator {
	
	
	private IChannel channel;
	private Go goCommand;
	private int colourToMove;
	private ISearchStopper stopper;
	private BestMoveSender sender;
	private TPTable tpt;
	
	private ISearchInfo lastinfo;
	private ISearchInfo[] last3infos;
	private long startTime;
	
	private String nextMinorLine;
	private long lastSentMinorInfo_timestamp;
	private ISearchConfig_MTD searchConfig;
	
	
	private int MTD_TRUST_WINDOW_MIN = 4;
	private int MTD_TRUST_WINDOW_MAX = 128;
	private int MTD_TRUST_WINDOW_MULTIPLIER = 2;
	
	
	public UCISearchMediatorImpl_Base(IChannel _channel, Go _go, int _colourToMove, BestMoveSender _sender,
			TPTable _tpt, ISearchConfig_AB _searchConfig) {
		
		channel = _channel;
		goCommand = _go;
		colourToMove = _colourToMove;
		sender = _sender;
		tpt = _tpt;
		
		if (_searchConfig instanceof ISearchConfig_MTD) {
			searchConfig = (ISearchConfig_MTD) _searchConfig;
			searchConfig.setMTDTrustWindow(0);
		}
		
		last3infos = new ISearchInfo[3];
		
		startTime = System.currentTimeMillis();
	}
	
	
	@Override
	public void registerInfoObject(ISearchInfo info) {
		//throw new IllegalStateException();
	}
	
	
	protected boolean enabledOptimization_ForMateScores() {
		return false;
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
	
	void setLastInfo(ISearchInfo info) {
		lastinfo = info;
	}
	
	
	public void changedMajor(ISearchInfo info) {
		
		if (searchConfig != null) {
			if (lastinfo != null) {
				
				int cur_mtdTrustWindow = searchConfig.getMTDTrustWindow();
				
				if (cur_mtdTrustWindow < MTD_TRUST_WINDOW_MIN) {
					cur_mtdTrustWindow = MTD_TRUST_WINDOW_MIN;
				} else {
				
					if (lastinfo.getBestMove() == info.getBestMove()) {
						if (cur_mtdTrustWindow == 0) {
							//cur_mtdTrustWindow = MTD_TRUST_WINDOW_MIN;
							throw new IllegalStateException("cur_mtdTrustWindow == 0");
						} else {
							cur_mtdTrustWindow *= MTD_TRUST_WINDOW_MULTIPLIER;
						}
					} else {
						cur_mtdTrustWindow /= MTD_TRUST_WINDOW_MULTIPLIER;
					}
					
					if (cur_mtdTrustWindow < 0) {
						throw new IllegalStateException("cur_mtdTrustWindow=" + cur_mtdTrustWindow);
					}
				}
				
				if (cur_mtdTrustWindow > MTD_TRUST_WINDOW_MAX) {
					cur_mtdTrustWindow = MTD_TRUST_WINDOW_MAX;
				}
				
				searchConfig.setMTDTrustWindow(cur_mtdTrustWindow);
				
				channel.dump("MTD Trust window changed to " + searchConfig.getMTDTrustWindow());
				
			}
		}
		
		lastinfo = info;
		
		String message = SearchInfoUtils.buildMajorInfoCommand(info, getStartTime(), (tpt != null) ? tpt.getUsage() : -1);
		send(message);
		
		stopIfMateIsFound();
	}
	
	
	public void changedMinor(ISearchInfo info) {
		nextMinorLine = SearchInfoUtils.buildMinorInfoCommand(info, getStartTime(), (tpt != null) ? tpt.getUsage() : -1);
		if (nextMinorLine != null) {
			long timestamp = System.currentTimeMillis();
			if (timestamp > lastSentMinorInfo_timestamp + 1000 /*Update UI, once per second*/) {
				lastSentMinorInfo_timestamp = timestamp;
				send(nextMinorLine);
			}
		}
	}
	
	
	public ISearchStopper getStopper() {
		return stopper;
	}
	
	void setStoper(ISearchStopper _stopper) {
		stopper = _stopper;
	}
	
	public void dump(String msg) {
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
		
		if (!enabledOptimization_ForMateScores()) {
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
}

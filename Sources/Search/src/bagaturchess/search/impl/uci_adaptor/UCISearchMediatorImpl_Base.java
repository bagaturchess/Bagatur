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

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.search.api.IRootSearch;
import bagaturchess.search.api.internal.ISearchInfo;
import bagaturchess.search.api.internal.ISearchMediator;
import bagaturchess.search.api.internal.ISearchStopper;
import bagaturchess.search.api.internal.SearchInfoUtils;
import bagaturchess.search.impl.utils.SearchUtils;
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
	
	
	private static int TRUST_WINDOW_BEST_MOVE_MULTIPLIER = 2;
	private static int TRUST_WINDOW_BEST_MOVE_MIN = 8;
	private static int TRUST_WINDOW_BEST_MOVE_MAX = 64;
	private int trustWindow_BestMove;
	
	private static int TRUST_WINDOW_ALPHA_ASPIRATION_MIN = 1;
	private static int TRUST_WINDOW_ALPHA_ASPIRATION_MAX = SearchUtils.getMateVal(1);
	private int trustWindow_AlphaAspiration;
	
	private VarStatistic best_moves_diffs_per_depth;
	private static int TRUST_WINDOW_MTD_STEP_MIN = 4;
	
	private boolean isEndlessSearch;
	
	
	public UCISearchMediatorImpl_Base(IChannel _channel, Go _go, int _colourToMove, BestMoveSender _sender,
			IRootSearch _rootSearch, boolean _isEndlessSearch) {
		
		channel = _channel;
		goCommand = _go;
		colourToMove = _colourToMove;
		sender = _sender;
		rootSearch = _rootSearch;
		isEndlessSearch = _isEndlessSearch;
		
		trustWindow_BestMove 		= TRUST_WINDOW_BEST_MOVE_MIN;
		trustWindow_AlphaAspiration = TRUST_WINDOW_ALPHA_ASPIRATION_MIN;
		
		last3infos = new ISearchInfo[3];
		
		best_moves_diffs_per_depth = new VarStatistic(false);
		best_moves_diffs_per_depth.addValue(1, 1);
		
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
	
	void setLastInfo(ISearchInfo info) {
		lastinfo = info;
	}
	
	
	public void changedMajor(ISearchInfo info) {
		
		if (lastinfo != null) {
			
			adjustTrustWindow_BestMove(info);
			
			adjustTrustWindow_AlphaAspiration(info);
			
			if (!info.isMateScore() && !lastinfo.isMateScore()) {
				int eval_diff = Math.abs(info.getEval() - lastinfo.getEval());
				best_moves_diffs_per_depth.addValue(eval_diff, eval_diff);
			}
			dump("UCISearchMediatorImpl_Base Trust Window MTD Step set to " + getTrustWindow_MTD_Step());
		}
		
		lastinfo = info;
		
		String message = SearchInfoUtils.buildMajorInfoCommand(info, getStartTime(), rootSearch.getTPTUsagePercent(), 0);
		send(message);
		
		stopIfMateIsFound();
	}


	private void adjustTrustWindow_BestMove(ISearchInfo info) {
		
		int cur_mtdTrustWindow = trustWindow_BestMove;
		
		if (cur_mtdTrustWindow < TRUST_WINDOW_BEST_MOVE_MIN) {
			cur_mtdTrustWindow = TRUST_WINDOW_BEST_MOVE_MIN;
		} else {
			
			if (lastinfo.getBestMove() == info.getBestMove()) {
				if (cur_mtdTrustWindow == 0) {
					//cur_mtdTrustWindow = MTD_TRUST_WINDOW_MIN;
					throw new IllegalStateException("cur_mtdTrustWindow == 0");
				} else {
					cur_mtdTrustWindow *= TRUST_WINDOW_BEST_MOVE_MULTIPLIER;
				}
			} else {
				cur_mtdTrustWindow /= TRUST_WINDOW_BEST_MOVE_MULTIPLIER;
			}
			
			if (cur_mtdTrustWindow < 0) {
				throw new IllegalStateException("cur_mtdTrustWindow=" + cur_mtdTrustWindow);
			}
		}
		
		/*if (cur_mtdTrustWindow > trustWindow_AlphaAspiration) {
			cur_mtdTrustWindow = trustWindow_AlphaAspiration;
		}*/
		
		if (cur_mtdTrustWindow > TRUST_WINDOW_BEST_MOVE_MAX) {
			cur_mtdTrustWindow = TRUST_WINDOW_BEST_MOVE_MAX;
		}
		
		trustWindow_BestMove = cur_mtdTrustWindow;
		
		dump("UCISearchMediatorImpl_Base Trust Window Best Move set to " + trustWindow_BestMove);
	}
	
	
	private void adjustTrustWindow_AlphaAspiration(ISearchInfo info) {
		
		if (!info.isMateScore() && trustWindow_AlphaAspiration != TRUST_WINDOW_ALPHA_ASPIRATION_MAX) {
			
			trustWindow_AlphaAspiration += 2 * Math.max(1, Math.abs(info.getEval() - lastinfo.getEval()));
			
		} else {
			trustWindow_AlphaAspiration = TRUST_WINDOW_ALPHA_ASPIRATION_MAX;
		}
		
		if (trustWindow_AlphaAspiration < 0) {
			throw new IllegalStateException("cur_mtdTrustWindow alpha =" + trustWindow_AlphaAspiration);
		}
		
		if (trustWindow_AlphaAspiration < TRUST_WINDOW_ALPHA_ASPIRATION_MIN) {
			trustWindow_AlphaAspiration = TRUST_WINDOW_ALPHA_ASPIRATION_MIN;
		}
		
		if (trustWindow_AlphaAspiration > TRUST_WINDOW_ALPHA_ASPIRATION_MAX) {
			trustWindow_AlphaAspiration = TRUST_WINDOW_ALPHA_ASPIRATION_MAX;
		}
		
		dump("UCISearchMediatorImpl_Base Trust Window Alpha Aspiration set to " + trustWindow_AlphaAspiration);
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
	
	
	public ISearchStopper getStopper() {
		return stopper;
	}
	
	void setStoper(ISearchStopper _stopper) {
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
		return trustWindow_BestMove;
	}
	
	
	@Override
	public int getTrustWindow_AlphaAspiration() {
		return trustWindow_AlphaAspiration;
	}
	
	
	@Override
	public int getTrustWindow_MTD_Step() {
		return 4;//(int) Math.max(TRUST_WINDOW_MTD_STEP_MIN, best_moves_diffs_per_depth.getEntropy());
	}
}

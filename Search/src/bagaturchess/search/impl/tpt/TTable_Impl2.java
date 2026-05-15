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
package bagaturchess.search.impl.tpt;


import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.uci.api.ChannelManager;


public class TTable_Impl2 implements ITTable {

	
	private static final int CLUSTER_SIZE = 4;
	private static final int ENTRY_LONGS = 2;
	private static final long VALID_MASK = 1L << 8; // bit 8 is unused: depth uses 0..7, flag starts at 9
	
	private static final int FLAG_SHIFT = 9;
	private static final int MOVE_SHIFT = 11; //Move is 22 bits
	private static final int SCORE_SHIFT = 34;

	
	private final int maxEntries;
	
	// Interleaved layout: [mixedKey0, value0, mixedKey1, value1, ...]
	// mixedKey is hashKey ^ value, so a torn/inconsistent read is very unlikely to pass the key check.
	private final long[] table;

	private long counter_usage;
	private long counter_tries;
	private long counter_hits;
	
	
	public TTable_Impl2(long sizeInBytes) {
	    long maxEntriesLong = Long.highestOneBit(sizeInBytes / (ENTRY_LONGS * Long.BYTES));
	    if (maxEntriesLong < CLUSTER_SIZE) {
	        throw new IllegalArgumentException("Insufficient memory allocated.");
	    }

	    final long maxArrayEntries = Long.highestOneBit(Integer.MAX_VALUE / ENTRY_LONGS);
	    if (maxEntriesLong > maxArrayEntries) {
	        maxEntriesLong = maxArrayEntries;
	    }

	    this.maxEntries = (int) maxEntriesLong;
	    this.table = new long[maxEntries * ENTRY_LONGS];

	    if (ChannelManager.getChannel() != null) {
	        ChannelManager.getChannel().dump("TTable_Impl2 initialized with " + maxEntries + " entries.");
	    }
	}

	@Override
	public int getUsage() {
		return (int) (counter_usage * 100 / maxEntries);
	}

	@Override
	public int getHitRate() {
		return counter_tries == 0 ? 0 : (int) (counter_hits * 100 / counter_tries);
	}

	@Override
	public void correctAllDepths(int reduction) {
		// No implementation needed
	}

	@Override
	public void get(long key, ITTEntry entry) {
		counter_tries++;
		entry.setIsEmpty(true);

		final long[] localTable = table;
		int pos = getIndex(key) * ENTRY_LONGS;
		for (int i = 0; i < CLUSTER_SIZE; i++, pos += ENTRY_LONGS) {
			long storedValue = localTable[pos + 1];
			if (storedValue == 0) {
				continue;
			}
			long storedKey = localTable[pos];
			if ((storedKey ^ storedValue) == key) {
				counter_hits++;
				entry.setIsEmpty(false);
				entry.setDepth(getDepth(storedValue));
				entry.setFlag(getFlag(storedValue));
				entry.setEval(getScore(storedValue));
				entry.setBestMove(getMove(storedValue));
				return;
			}
		}
	}

	@Override
	public final void put(long hashkey, int depth, int eval, int alpha, int beta, int bestmove) {
		
		if (eval > 536870911 || eval < -536870912) {
			
			return;
			//throw new IllegalStateException("TT score overflow: eval=" + eval);
		}
		
		int flag = ITTEntry.FLAG_EXACT;
		
		if (eval >= beta) {
			
			flag = ITTEntry.FLAG_LOWER;
			
		} else if (eval <= alpha) {
			
			flag = ITTEntry.FLAG_UPPER;
		}
		
		addValue(hashkey, eval, depth, flag, bestmove);
	}
	
	private final void addValue(final long new_key, int new_score, final int new_depth, final int new_flag, final int new_move) {

	    final long new_value = createValue(new_score, new_move, new_flag, new_depth);
	    final long[] localTable = table;
	    final int start_pos = getIndex(new_key) * ENTRY_LONGS;

	    int replace_pos = -1;
	    int replace_depth = Integer.MAX_VALUE;
	    int replace_flag = Integer.MAX_VALUE;

	    for (int i = 0, pos = start_pos; i < CLUSTER_SIZE; i++, pos += ENTRY_LONGS) {

	        long stored_value = localTable[pos + 1];

	        // Empty slot found
	        if (stored_value == 0) {
	            replace_pos = pos;
	            counter_usage++;
	            break;
	        }
	        
	        long stored_key = localTable[pos];
	        int stored_depth = getDepth(stored_value);
	        
	        if ((stored_key ^ stored_value) == new_key) {
	            
		        	// No need to update identical entry
		        	if (new_value == stored_value) {
		        		
		                return;
		        	}
	            
		        	if (true) { //Always replace strategy
		        		
		        		replace_pos = pos;
		        		break;
		        	}
		        	
	            if (new_depth > stored_depth) {
	            	
	                replace_pos = pos;
	                break;

	            } else if (new_depth == stored_depth) {
	                
	                replace_pos = pos;
	                break;
	                
	            		/*int stored_flag = getFlag(stored_value);
	            	
	                if (isStrongerFlag(new_flag, stored_flag)) {
	                
	                    replace_pos = pos;
	                    break;

	                } else if (new_flag == stored_flag) {
	                	
	                		int stored_score = getScore(stored_value);
	                	
	                    if (isBetterEval(new_score, stored_score, new_flag)
	                    		|| (new_move != 0 && getMove(stored_value) == 0)
	                    		|| new_move == getMove(stored_value)) {
	                    	
		                    	replace_pos = pos;
		                    	break;
	                    }
	                    
	                    return;
	                    
	                } else {
	                
	                    return; // Same depth, weaker flag
	                }*/
	          	
	            } else {
	                
	                return; // New entry is shallower, skip
	            }
	        }
	        
	        // Replacement candidate for a different key: prefer the shallowest entry,
	        // and for equal depth prefer the weakest bound information.
	        int stored_flag = getFlag(stored_value);
	        if (stored_depth < replace_depth
	        		|| (stored_depth == replace_depth && stored_flag > replace_flag)) {
	        	
	            replace_depth = stored_depth;
	            replace_flag = stored_flag;
	            replace_pos = pos;
	        }
	    }

	    if (replace_pos == -1) {
	    	
	        throw new IllegalStateException("No available entry to replace.");
	    }

	    localTable[replace_pos] = new_key ^ new_value;
	    localTable[replace_pos + 1] = new_value;
	}

	private static boolean isStrongerFlag(int newFlag, int oldFlag) {
		
	    return newFlag < oldFlag;
	}

	private static boolean isBetterEval(int newEval, int oldEval, int flag) {
		
	    switch (flag) {
	        case ITTEntry.FLAG_EXACT:
	        case ITTEntry.FLAG_LOWER:
	            return newEval > oldEval;
	        case ITTEntry.FLAG_UPPER:
	            return newEval < oldEval;
	        default:
	           throw new IllegalStateException();
	    }
	}

	private int getIndex(long key) {
		return ((int) (key ^ (key >>> 32)) & (maxEntries - CLUSTER_SIZE));
	}

	private static int getScore(long value) {
		return (int) (value >> SCORE_SHIFT);
	}

	private static int getDepth(long value) {
		return (int) (value & 0xFF);
	}

	private static int getFlag(long value) {
		return (int) ((value >>> FLAG_SHIFT) & 0x3);
	}

	private static int getMove(long value) {
		return (int) ((value >>> MOVE_SHIFT) & 0x3FFFFF);
	}

	private static long createValue(int score, int move, int flag, int depth) {
		if (EngineConstants.ASSERT) {
			Assert.isTrue(depth >= 0 && depth <= 255);
		}
		return VALID_MASK | ((long) score << SCORE_SHIFT) | ((long) move << MOVE_SHIFT) | ((long) flag << FLAG_SHIFT) | depth;
	}
}

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


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.EngineConstants;
import bagaturchess.search.api.internal.ISearch;
import bagaturchess.uci.api.ChannelManager;


public class TTable_Impl2 implements ITTable {

	
	private static final int ALWAYS_REPLACE_DEPTH = ISearch.MAX_DEPTH;
	
	
	private static final int FLAG_SHIFT = 9;
	private static final int MOVE_SHIFT = 11; //Move is 22 bits
	private static final int SCORE_SHIFT = 34;

	
	private final int maxEntries;
	
	private final ByteBuffer keys;
	private final ByteBuffer values;

	private long counter_usage;
	private long counter_tries;
	private long counter_hits;
	
	
	public TTable_Impl2(long sizeInBytes) {
	    long maxEntriesLong = Long.highestOneBit(sizeInBytes / 16);
	    if (maxEntriesLong < 4) {
	        throw new IllegalArgumentException("Insufficient memory allocated.");
	    }

	    long byteSize = maxEntriesLong * 8;
	    if (byteSize > Integer.MAX_VALUE) {
	        maxEntriesLong = Integer.MAX_VALUE / 8;
	        byteSize = maxEntriesLong * 8L;
	    }

	    this.maxEntries = (int) maxEntriesLong;

	    keys = ByteBuffer.allocateDirect((int) byteSize).order(ByteOrder.nativeOrder());
	    values = ByteBuffer.allocateDirect((int) byteSize).order(ByteOrder.nativeOrder());

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

		int index = getIndex(key);
		for (int i = 0; i < 4; i++) {
			long storedKey = keys.getLong((index + i) * 8);
			long storedValue = values.getLong((index + i) * 8);
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
	    final int start_index_entry = getIndex(new_key);

	    int replaced_min_depth = Integer.MAX_VALUE;
	    int replaced_index = -1;

	    for (int i = start_index_entry; i < start_index_entry + 4; i++) {

	        long stored_key = keys.getLong(i * 8);

	        // Empty slot found
	        if (stored_key == 0) {
	            
	            replaced_index = i;
	            replaced_min_depth = 0;
	            counter_usage++;
	            break;
	        }
	        
	        long stored_value = values.getLong(i * 8);
	        int stored_depth = getDepth(stored_value);
	        
	        // Same key
	        if ((stored_key ^ stored_value) == new_key) {
	            
	        	// No need to update identical entry
	        	if (new_value == stored_value) {
	        		
	                return;
	        	}
	            
	        	//Always replace for depth lower than ALWAYS_REPLACE_DEPTH
	        	if (stored_depth <= ALWAYS_REPLACE_DEPTH) {
	        		
	                replaced_index = i;
	                break;
	        	}
	        	
	            if (new_depth > stored_depth) {
	            	
	                replaced_index = i;
	                break;

	            } else if (new_depth == stored_depth) {
	                
		            int stored_flag = getFlag(stored_value);
		            
	                if (isStrongerFlag(new_flag, stored_flag)) {
	                
	                    replaced_index = i;
	                    break;

	                } else if (new_flag == stored_flag) {
	                	
	                    replaced_index = i;
	                    break;
	                    
	                	/*int stored_score = getScore(stored_value);
	                	
	                    if (isBetterEval(new_score, stored_score, new_flag)) {
	                    
	                        replaced_index = i;
	                        break;
	                        
	                    } else {
	                    
	                        return; // Same depth, same flag, worse eval
	                    }*/
	                    
	                } else {
	                
	                    return; // Same depth, weaker flag
	                }
	          	
	            } else {
	                
	                return; // New entry is shallower, skip
	            }
	        }
	        
	        if (stored_depth < replaced_min_depth) {
	        	
	            replaced_min_depth = stored_depth;
	            replaced_index = i;
	        }
	    }

	    if (replaced_index == -1) {
	    	
	        throw new IllegalStateException("No available entry to replace.");
	    }

	    keys.putLong(replaced_index * 8, new_key ^ new_value);
	    values.putLong(replaced_index * 8, new_value);
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
		return ((int) (key ^ (key >>> 32)) & (maxEntries - 4));
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
		return ((long) score << SCORE_SHIFT) | ((long) move << MOVE_SHIFT) | ((long) flag << FLAG_SHIFT) | depth;
	}
}

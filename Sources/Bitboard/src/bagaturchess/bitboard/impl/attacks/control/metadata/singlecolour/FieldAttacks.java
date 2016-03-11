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
package bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour;

import java.io.Serializable;

import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.impl.Figures;


public class FieldAttacks
	implements Comparable<FieldAttacks>, Cloneable, Serializable {
	
	private static final long serialVersionUID = -1691481162448968174L;

	int id;
	
	int pa_count;
	int kna_count;
	int oa_count;
	int ma_count;
	int ra_count;
	int qa_count;
	int ka_count;
	int xa_count;
	
	public static final int PA_MASK = 1;
	public static final int MA_MASK = 2;
	public static final int RA_MASK = 4;
	public static final int QA_MASK = 8;
	public static final int KA_MASK = 16;
	
	int pattern;
	
	public FieldAttacks(int pa_count,
			int kna_count, int oa_count, int ma_count,
			int ra_count, int qa_count,
			int ka_count, int xa_count) {
		
		this.pa_count = pa_count;
		this.kna_count = kna_count;
		this.oa_count = oa_count;
		this.ma_count = ma_count;
		this.ra_count = ra_count;
		this.qa_count = qa_count;
		this.ka_count = ka_count;
		this.xa_count = xa_count;
		
		pattern = 0;
		if (pa_count > 0) {
			pattern |= PA_MASK;
		}
		if (kna_count > 0 || oa_count > 0 || ma_count > 0) {
			pattern |= MA_MASK;
		}
		if (ra_count > 0) {
			pattern |= RA_MASK;
		}
		if (qa_count > 0) {
			pattern |= QA_MASK;
		}
		if (ka_count > 0) {
			pattern |= KA_MASK;
		}
		if (pattern < 0 || pattern > 31) {
			throw new IllegalStateException();
		}
	}
	
	public int getPattern() {
		return pattern;
	}
	
	public boolean hasAttacksFrom(int figureType) {
		boolean result = false;
		
		switch (figureType) {
			case Figures.TYPE_PAWN:
				result = pa_count > 0;
				break;
			case Figures.TYPE_OFFICER:
				if (IFieldsAttacks.MINOR_UNION) {
					result = ma_count > 0;
				} else {
					result = oa_count > 0;
				}
				break;
			case Figures.TYPE_KNIGHT:
				if (IFieldsAttacks.MINOR_UNION) {
					result = ma_count > 0;
				} else {
					result = kna_count > 0;
				}
				break;
			case Figures.TYPE_CASTLE:
				result = ra_count > 0;
				break;
			case Figures.TYPE_QUEEN:
				result = qa_count > 0;
				break;
			case Figures.TYPE_KING:
				result = ka_count > 0;
				break;
			default:
				throw new IllegalStateException();
		}
		
		return result;
		
	}
	
	public int paCount() {
		int pa = pa_count; 
		/*if (pa_count == IFieldsAttacks.MAX_PAWN_STATES - 1) {
			pa += xa_count;
		}*/
		return pa;
	}
	
	public int maCount() {
		if (!IFieldsAttacks.MINOR_UNION) {
			throw new IllegalStateException();
		}
		
		int ma = ma_count; 
		if (ma_count == IFieldsAttacks.MAX_MINOR_STATES - 1) {
			ma += xa_count;
		}
		return ma;
	}
	
	public int knaCount() {
		
		if (IFieldsAttacks.MINOR_UNION) {
			throw new IllegalStateException();
		}
		
		int kna = kna_count; 
		if (kna_count == IFieldsAttacks.MAX_KNIGHT_STATES - 1) {
			kna += xa_count;
		}
		return kna;
	}
	
	public int oaCount() {
		
		if (IFieldsAttacks.MINOR_UNION) {
			throw new IllegalStateException();
		}
		
		int oa = oa_count; 
		if (oa_count == IFieldsAttacks.MAX_OFFICER_STATES - 1) {
			oa += xa_count;
		}
		return oa;
	}
	
	public int raCount() {
		int ra = ra_count; 
		if (ra_count == IFieldsAttacks.MAX_ROOK_STATES - 1) {
			ra += xa_count;
		}
		return ra;
	}
	
	public int qaCount() {
		int qa = qa_count; 
		if (qa_count == IFieldsAttacks.MAX_QUEEN_STATES - 1) {
			qa += xa_count;
		}
		return qa;
	}
	
	public int kaCount() {
		int ka = ka_count; 
		/*if (ka_count == IFieldsAttacks.MAX_KING_STATES - 1) {
			ka += xa_count;
		}*/
		return ka;
	}
	
	public boolean hasNonKingAttack() {
		
		if (qa_count > 0) {
			return true;
		}
		if (ra_count > 0) {
			return true;
		}
		if (IFieldsAttacks.MINOR_UNION) {
			if (ma_count > 0) {
				return true;
			}
		} else {
			if (oa_count > 0) {
				return true;
			}
			if (kna_count > 0) {
				return true;
			}
		}
		if (pa_count > 0) {
			return true;
		}
		
		return false;
	}
	
	public int getMaxType() {
		
		if (qa_count > 0) {
			return Figures.TYPE_QUEEN;
		} else if (ra_count > 0) {
			return Figures.TYPE_CASTLE;
		} else {
			if (IFieldsAttacks.MINOR_UNION) {
				if (ma_count > 0) {
					return Figures.TYPE_OFFICER;
				}
			} else {
				if (oa_count > 0) {
					return Figures.TYPE_OFFICER;
				} else if (kna_count > 0) {
					return Figures.TYPE_KNIGHT;
				}
			}
		}
		
		return Figures.TYPE_UNDEFINED;
	}
	
	public int compareTo(FieldAttacks other) {
		
		int p_delta = paCount() - other.paCount();
		if (p_delta != 0) {
			return p_delta;
		}

		int m_delta = -1;
		if (IFieldsAttacks.MINOR_UNION) {
			m_delta = maCount() - other.maCount();
		} else {
			m_delta = (oaCount() + knaCount()) - (other.oaCount() + other.knaCount());
		}
		
		if (m_delta != 0) {
			return m_delta;
		}

		int r_delta = raCount() - other.raCount();
		if (r_delta != 0) {
			return r_delta;
		}

		int q_delta = qaCount() - other.qaCount();
		if (q_delta != 0) {
			return q_delta;
		}
		
		int k_delta = kaCount() - other.kaCount();
		if (k_delta != 0) {
			return k_delta;
		}
		
		return -1;
	}
	
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		FieldAttacks other = (FieldAttacks) o;
		
		if (IFieldsAttacks.MINOR_UNION) {
			return pa_count == other.pa_count
						&& ma_count == other.ma_count
						&& ra_count == other.ra_count
						&& qa_count == other.qa_count
						&& ka_count == other.ka_count
						&& xa_count == other.xa_count;
		} else {
			return pa_count == other.pa_count
						&& kna_count == other.kna_count
						&& oa_count == other.oa_count
						&& ra_count == other.ra_count
						&& qa_count == other.qa_count
						&& ka_count == other.ka_count
						&& xa_count == other.xa_count;
		}
	}
	
	public int hashCode() {
		int hash = -1;
		if (IFieldsAttacks.MINOR_UNION) {
			hash = pa_count + (ma_count << 4) + (ra_count << 8) + (qa_count << 12) + (ka_count << 16) + (xa_count << 20);
		} else {
			hash = pa_count + (kna_count << 4) + (oa_count << 8) + (ra_count << 12) + (qa_count << 16) + (ka_count << 20) + (xa_count << 24);
		}
		//System.out.println(hash);
		return hash;
	}
	
	public String toString() {
		String result = "id:" + id + "->";
		
		result += makeString(pa_count, "P");
		if (IFieldsAttacks.MINOR_UNION) {
			result += makeString(ma_count, "M");
		} else {
			result += makeString(kna_count, "N");
			result += makeString(oa_count, "B");
		}
		result += makeString(ra_count, "R");
		result += makeString(qa_count, "Q");
		result += makeString(ka_count, "K");
		result += makeString(xa_count, "X");
		
		return result;
	}
	
	@Override
	public FieldAttacks clone() {
		FieldAttacks result = null;
		try {
			result = (FieldAttacks) super.clone();
			
			result.id = id;
			result.pa_count = pa_count;
			result.kna_count = kna_count;
			result.oa_count = oa_count;
			result.ma_count = ma_count;
			result.ra_count = ra_count;
			result.qa_count = qa_count;
			result.ka_count = ka_count;
			result.xa_count = xa_count;
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	private String makeString(int number, String symbol) {
		String result = "";
		
		for (int i=0; i<number; i++) {
			result += symbol;
		}
		
		return result;
	}
	
	public boolean isConsistent() {
		if (pa_count >= IFieldsAttacks.MAX_PAWN_STATES) {
			throw new IllegalStateException();
		}
		
		if (IFieldsAttacks.MINOR_UNION) {
			if (ma_count >= IFieldsAttacks.MAX_MINOR_STATES) {
				throw new IllegalStateException();
			}
		} else {
			if (kna_count >= IFieldsAttacks.MAX_KNIGHT_STATES) {
				throw new IllegalStateException();
			}
			if (oa_count >= IFieldsAttacks.MAX_OFFICER_STATES) {
				throw new IllegalStateException();
			}
		}
		
		if (ra_count >= IFieldsAttacks.MAX_ROOK_STATES) {
			throw new IllegalStateException();
		}
		if (qa_count >= IFieldsAttacks.MAX_QUEEN_STATES) {
			throw new IllegalStateException();
		}
		if (ka_count >= IFieldsAttacks.MAX_KING_STATES) {
			throw new IllegalStateException();
		}
		if (xa_count >= IFieldsAttacks.MAX_OTHER_STATES) {
			throw new IllegalStateException();
		}
		
		if (pa_count < 0) {
			throw new IllegalStateException();
		}
		
		if (IFieldsAttacks.MINOR_UNION) {
			if (ma_count < 0) {
				throw new IllegalStateException();
			}
		} else {
			if (kna_count < 0) {
				throw new IllegalStateException();
			}
			if (oa_count < 0) {
				throw new IllegalStateException();
			}
		}
		
		if (ra_count < 0) {
			throw new IllegalStateException();
		}
		if (qa_count < 0) {
			throw new IllegalStateException();
		}
		if (ka_count < 0) {
			throw new IllegalStateException();
		}
		if (xa_count < 0) {
			throw new IllegalStateException();
		}
		
		int countMaxAttacks = 0;
		
		/*if (pa_count == IFieldsAttacks.MAX_PAWN_STATES - 1) {
			countMaxAttacks++;
		}*/
		
		if (IFieldsAttacks.MINOR_UNION) {
			if (ma_count == IFieldsAttacks.MAX_MINOR_STATES - 1) {
				countMaxAttacks++;
			}
		} else {
			if (kna_count == IFieldsAttacks.MAX_KNIGHT_STATES - 1) {
				countMaxAttacks++;
			}
			if (oa_count == IFieldsAttacks.MAX_OFFICER_STATES - 1) {
				countMaxAttacks++;
			}
		}
		
		if (ra_count == IFieldsAttacks.MAX_ROOK_STATES - 1) {
			countMaxAttacks++;
		}
		if (qa_count == IFieldsAttacks.MAX_QUEEN_STATES - 1) {
			countMaxAttacks++;
		}
		/*if (ka_count == IFieldsAttacks.MAX_KING_STATES - 1) {
			countMaxAttacks++;
		}*/
		
		if (countMaxAttacks == 0 && xa_count > 0) {
			throw new IllegalStateException();
		}
		
		if (countMaxAttacks > 1 && xa_count != 0) {
			return false;
		} else {
			return true;
		}
	}

	public int getId() {
		return id;
	}
}

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
package bagaturchess.bitboard.impl.utils;

import java.text.NumberFormat;

public class StringUtils {
	
	public static String align(double number) {
		String result = NumberFormat.getInstance().format(number);
		if (number < 10) {
			result = " " + result;
		}
		
		int dotIndex = result.indexOf('.');
		if (dotIndex < 0) {
			result += '.';
		}
		result = fill(result, 6, '0');
		
		return result;
	}
	
	public static String fill(String str, int max) {
		return fill(str, max, ' ');
	}
	
	private static String fill(String str, int max, char c) {
		int len = str.length();
		for (int i=max; i>len; i--) {
			str += c;
		}
		return str;
	}
	
	/*public static String cut(double number) {
		return NumberFormat.getInstance().format(number);
	}*/
}

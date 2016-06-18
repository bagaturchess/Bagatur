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
package bagaturchess.tools.pgn.impl;

import java.util.HashSet;
import java.util.Set;

public class ExcludedGames {
	
	private static final Set<String> excluded = new HashSet<String>();
	
	static {
		excluded.add("1. e4 c5 2. Nf3 Nc6 3. Bb5 Qc7 4. O-O d6 5. d4 cxd4 6. c3 dxc3 7. Nxc3 e6 8. Bd2 Ke7 9. Be3 Ke8 10. Bf4 Nf6 11. e5 dxe5 12. Bxe5 Qb6 13. Bd4 Qc7 14. Bxf6 gxf6 15. Re1 Be7 16. Qd2 Bd7 17. Rac1 h5 18. Qe3 a6 19. Ba4 O-O-O 20. a3 Kb8 21. b4 b5 22. Bb3 Bd6 23. Qe2 Qb7 24. Ne4 Be7 25. Qe3 Ne5 26. Nxe5 fxe5 27. Nc5 Bxc5 28. Qxe5+ Ka8 29. Rxc5 Rhg8 30. g3 Bc6 31. Bc2 Rd5 32. Rxd5 Bxd5 33. Be4 Bxe4 34. Rxe4 Rd8 35. Re1 Qf3 36. h4 Rd3 37. Rc1 Rd1+ 38. Rxd1 Qxd1+ 39. Kg2 Kb7 40. Qc5 Qd7 41. Qxh5 f5 42. Qf3+ Kc7 43. h5 e5 44. Qc3+");
		excluded.add("1. e4 g6 2. d4 Bg7 3. Nc3 d6 4. Nf3 a6 5. Be3 b5 6. Bd3 Nd7 7. Qd2 Bb7 8. a4 b4 9. Ne2 c5 10. c3 Ngf6 11. Bh6 bxc3 12. bxc3 Bxh6 13. Qxh6 cxd4 14. cxd4 Bxe4 15. Bxe4 Nxe4 16. O-O e6 17. Ng3 Nef6 18. Rfe1 Qe7 19. Rac1 Qf8 20. Qg5 Qe7 21. Qh6 O-O 22. Qxf8+");
	}
	
	public static final boolean isExcluded(String gameID) { 
		return excluded.contains(gameID);
	}
}

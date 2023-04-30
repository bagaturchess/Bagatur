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
		excluded.add("1. d4 Nf6 2. c4 c5 3. d5 b5 4. Nf3 bxc4 5. Nc3 d6 6. e4 Nbd7 7. Bxc4 g6 8. O-O Bg7 9. h3 Nb6 10. Bb5+ Bd7 11. Be2 O-O 12. Bf4 Nh5 13. Bh2 Bxc3 14. bxc3 Na4 15. Qd2 f6 16. Rab1 Ng7 17. Bf4 Nb6 18. Ne1 Qe8 19. h4 Qf7 20. c4 e5 21. dxe6 Bxe6 22. Qxd6 Bxc4 23. Bxc4 Qxc4 24. Be3 Qxe4 25. Nf3 Rac8 26. Nc4 Nf5");
		excluded.add("1. e4 c6 2. d4 d5 3. e5 Bf5 4. Bd3 Bg6 5. Nf3 e6 6. Be3 Nd7 7. Bxg6 hxg6 8. c3 c5 9. Nbd2 Ne7 10. Rc1 Nc6 11. h3 Rc8 12. O-O Be7 13. Nb3 c4 14. Nbd2 b5 15. b4 Nb6 16. Qc2 Na4 17. a3 Rb8 18. Rb1 Rc8 19. Rbc1 Rb8 20. Rb1 Ra8 21. Rfe1 Rb8 22. Nf1 Rc8 23. Ng3 Rb8 24. Ne2 Kd7 25. Rbd1 Kc7 26. Rd2 Kb7 27. Rdd1 Ka8 28. Qd2 Rb7 29. Rc1 Rf8 30. Rc2 Kb8 31. Bg5 Ka8 32. Bf4 Kb8 33. Be3 Ka8 34. Bf4 Kb8 35. Be3 Ka8 36. Ng3 Kb8 37. Ng5 Ka8 38. Nf3 Kb8 39. Bg5 Ka8 40. Bxe7 Nxe7 41. Ng5 Nf5 42. Nxf5 gxf5 43. Nf4 Qe7 44. Kxg1 Rh8 45. Rcc1 g6 46. Qe3 Rc7 47. Qg3 Rh5 48. Re3 Rc8 49. Kf1");
		excluded.add("1. e4 c6 2. d4 d5 3. exd5 cxd5 4. Bd3 Nc6 5. c3 Nf6 6. Bf4 Bg4 7. Qb3 Qd7 8. Nd2 g6 9. h3 Bf5 10. Be2 h5 11. Ngf3 Bg7 12. Ne5 Qc8 13. Rc1 Nxd4 14. Qa4+ Nc6 15. Bb5 Bd7 16. c4 O-O 17. cxd5 Nxd5 18. Nxc6 Bxc6 19. O-O Bxb5 20. Qxb5 Qd8 21. Bg3 Ne3 22. fxe3 Qxd2 23. Qxb7 Qxe3+ 24. Bf2 Qg5 25. b3 Rfb8 26. Qe4 Bf8 27. Bh4 Qa5 28. Kxg2");
		excluded.add("1. Nc3 c5 2. e4 e6 3. f4 Nc6 4. Bb5 Qc7 5. d3 a6 6. Bxc6 Qxc6 7. Nf3 Nf6 8. O-O Be7 9. Qe1 O-O 10. Bd2 b5 11. Ne5 Qc7 12. Qg3 d6 13. Ng4 b4 14. Nd1 Nxg4 15. Qxg4 f5 16. exf5 exf5 17. Qh3 Bf6 18. c3 a5 19. Ne3 Be6 20. Qf3 Qf7 21. a3 Rab8 22. axb4 axb4 23. Ra6 d5 24. Raa1 Rfd8 25. Nd1 bxc3 26. bxc3 d4 27. c4 Bc8 28. Ra7 Qe6 29. Ba5 Re8 30. Bc7 Qe1 31. Bxg2");
		excluded.add("1. c4 g6 2. Nc3 Bg7 3. d4 d6 4. g3 Nd7 5. Bg2 e5 6. Nf3 Ne7 7. h4 h6 8. dxe5 dxe5 9. e4 O-O 10. Be3 Nc6 11. Qd2 Nb6 12. Bxh6 Nxc4 13. Qxd8 Rxd8 14. Bxg7 Kxg7 15. Nd5 Nxb2 16. Qd2 Nd1 17. Rfxd1 Bg4 18. Rxc6 Bxf3 19. Bxf3 Bxe2 20. Bg4 Rb8 21. Ne3 b5 22. h5 a5 23. f4 gxh5 24. Bxh5 Rh8 25. Rxc8+ Rxc8 26. fxe5 Nd8 27. Nf5+ Nc6 28. Rb1 Rb8 29. c2+ Bf8 30. b5 Nxe5 31. Qf4 Kf6 32. dxc5 f6 33. e6 Rc8 34. c6 a4 35. g5 Kg7 36. Qd4 Nc4 37. Rf3 Rxf8 38. gxf6+ Rxf6");
		excluded.add("1. c4 Nf6 2. Nc3 g6 3. g3 Bg7 4. Bg2 O-O 5. d4 d6 6. Nf3 Nc6 7. O-O Bf5 8. d5 Na5 9. Nd2 Ne8 10. e4 Bd7 11. Qc2 c6 12. dxc6 Nxc6 13. Nf3 Rc8 14. Be3 Na5 15. Nd2 b6 16. Qd3 Nf6 17. Rfe1 Ng4 18. b3 Nc6 19. a3 f5 20. h3 Nxe3 21. Qxe3 f4 22. gxf4 Bh6 23. Ne2 e5 24. Qd3 Qe7 25. Rad1 Bxf4 26. Nf3 Rf6 27. Nxf4 exf4 28. Ng5 Be8 29. Kh2 Bf7 30. Qe2 Re8 31. Qd3 h6 32. b4 Ne5 33. xxe5 dxe5 34. Bf1 Be6");
		excluded.add("1. c4 e5 2. g3 Nf6 3. Bg2 g6 4. Nc3 Bg7 5. e4 Nc6 6. Nge2 d6 7. O-O O-O 8. f4 exf4 9. gxf4 Bg4 10. d4 Nd7 11. Be3 Na5 12. b3 c5 13. Rc1 Nc6 14. Qd2 Qa5 15. Bf3 Bh3 16. Bg2 Bxg2 17. Kxg2 Nf6 18. d5 Ne7 19. f5 Kh8 20. Bh6 Neg8 21. Bxg7+ Kxg7 22. Ng3 Rfe8 23. fxg6 hxg6 24. Rxf6 Nxf6 25. Qg5 Re5 26. Nf5+ Rxf5 27. exf5 Re8 28. fxg6 Re5 29. Qg3 fxg6 30. Rf1 Qd8 31. Qh4 Qe7 32. Kh1 a6 33. Rg1 Qf7 34. Rf1 g5 35. Qh3 g4 36. Qh4 Qg6 37. Qf2 Qh5 38. Qg3 Rf5 39. Bc1");
		excluded.add("1. g3 c5 2. Bg2 Nc6 3. c4 g6 4. Nc3 Bg7 5. d3 e6 6. e4 Nge7 7. Nge2 a6 8. O-O O-O 9. a3 b5 10. Be3 bxc4 11. dxc4 d6 12. Rb1 Rb8 13. Qd2 Ne5 14. b3 Ng4 15. Bg5 f6 16. h3 fxg5 17. hxg4 Nc6 18. e5 Nxe5 19. Ne4 Bb7 20. Qxd6 Nf3+ 21. Kh1 Bxe4 22. Qxe6+ Kh8 23. Qxe4 Nd2 24. Qc6 Nxb1 25. Qa8 Rxa8 26. Qa4");
		excluded.add("1. e4 e5 2. Nf3 Nc6 3. Nc3 Nf6 4. Bc4 Bc5 5. O-O O-O 6. d3 h6 7. Re1 d6 8. h3 Be6 9. Be3 Bb6 10. Nd5 Bxd5 11. exd5 Na5 12. b3 Qd7 13. Qd2 Bxe3 14. Rxe3 Nxc4 15. dxc4 Rfe8 16. Rae1 Nh5 17. Kh2 Nf4 18. Ng1 c6 19. Ne2 Ng6 20. Rg3 Qd8 21. Rf1 Qf6 22. f4 Nh4 23. Rg4 Nf5 24. Ng3 Nxg3 25. Kxg3 exf4+ 26. Rfxf4 Qe5 27. Kf2 cxd5 28. cxd5 Rac8 29. c4 Re7 30. Qd4 Qxd4+ 31. Rxd4 Rce8 32. Kf1 Re2 33. a4 Rb2 34. Rd3 Rbe2 35. Re1+");
		excluded.add("1. e4 e6 2. b3 d5 3. Bb2 dxe4 4. Nc3 Nf6 5. g4 h6 6. Qe2 Nc6 7. Nxe4 e5 8. O-O-O Be6 9. Bg2 Nd5 10. Qd3 Ndb4 11. Ne2 Nd4 12. Nxd4 exd4 13. c3 Nc6 14. Ng3 Qg5 15. Nf5 dxc3 16. Bxc3 h5 17. h3 f6 18. Qe3 Ba3+ 19. Bb2 Nxc3 20. Qxc3 Qxc3 21. Raxc1 Bxb2+ 22. Rxc7 Rd7 23. Rfc1 Rxc7 24. Rxc7 Qb1+ 25. f4 Qxf1+ 26. Kxf1 Rb8 27. f3 b6 28. h4 Ba6+ 29. c3 g6+ 30. e1 Bc4 31. Rb7 Bd5 32. Bxd5 exd5 33. h3 Rxc3+ 34. Kxd2 Rc4 35. d3 Rxa4 36. e4 Ra3+ 37. Kxf4 Kf7 38. Rxd5 Ke6 39. Rb5 g5+ 40. d4");
		excluded.add("1. e4 e6 2. Nf3 d5 3. Nc3 Nf6 4. e5 Nbd7 5. d4 c5 6. d4");
		excluded.add("1. e4 e6 2. Nf3 c5 3. d4 cxd4 4. Nxd4 a6 5. Nc3 Qc7 6. Bd3 Bc5 7. Nde2 b5 8. Ng3 Nf6 9. O-O d6 10. Kh1 h5 11. Qe1 Bb7 12. Be3 h4 13. Nge2 Bxe3 14. fxe3 h3 15. Qg3 Nbd7 16. Nd4 hxg2+ 17. Kxg2 Qb6 18. a3 Nc5 19. Qf3 O-O-O 20. b4 Nfd7 21. a4 Ne5 22. Qe2 Qc7 23. Ra3 Ng4 24. Nf3 d5 25. e5 Nxe5 26. Nxe5 Qxe5 27. Kg1 d4 28. Qf2 Qxe3 29. axb5 axb5 30. Qg3 Qf4");
		excluded.add("1. e4 e6 2. d4 d5 3. Nc3 Nf6 4. e5 Nfd7 5. f4 c5 6. Nf3 Nc6 7. Qd3 a6 8. Bd2 b5 9. a3 Nb6 10. Ne2 b4 11. axb4 cxb4 12. b3 Ne7 13. g3 a5 14. Bg2 Nf5 15. Bxc5");
		excluded.add("1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5 4. c3 Nf6 5. d4 exd4 6. cxd4 Bb4+ 7. Nc3 d6 8. O-O Ng4 9. d5 Nce5 10. Qa4+ Qd7 11. Bb5 c6 12. dxc6 bxc6 13. Nxe5 cxb5 14. Qxb5 Qxb5 15. Nxb5 Kf8 16. Nxg4 Bxg4 17. Be2");
		excluded.add("1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5 4. c3 Nf6 5. d3 a6 6. Bb3 d6 7. Nbd2 Ba7 8. h3 h6 9. Nf1 Be6 10. Ng3 Qd7 11. O-O O-O 12. Be3 Bxb3 13. Qxb3 Ne7 14. d4 Ng6 15. b3");
		excluded.add("1. e4 g6 2. d4 Bg7 3. Nc3 d6 4. Nge2 c6 5. g3 Nf6 6. Bg2 Nbd7 7. O-O O-O 8. a4 e5 9. h3 Re8 10. Be3 Qa5 11. Qd2 exd4 12. Nxd4 Nc5 13. f3 d5 14. exd5 Nxd5 15. Bf2 Be6 16. Rfe1 Nxc3 17. bxc3 Rad8 18. Qe3 Bd7 19. Qd2 Ne6 20. Bc4+");
		excluded.add("1. d4 g6 2. e4 Bg7 3. Nf3 d6 4. h3 a6 5. c3 Nd7 6. Bd3 e6 7. O-O Ne7 8. a4 O-O 9. Na3 b6 10. Bd2 h6 11. Qe2 Bb7 12. Rfe1 Kh7 13. Nc4 f6 14. h4 e5 15. h5 gxh5 16. Nh4 Qe8 17. Ne3 Rg8 18. Nd5 Bxd5 19. exd5+ Kh8 20. Qe4 f5 21. Nxf5 Nf6 22. Qh4 Ng6 23. Qh2 Qf8 24. Nh4 Ne7 25. f3 Nd7 26. Qh3 Bf6 27. Nf5 Bg5 28. f4 exf4 29. Nxe7 f3 30. Bxg5 f2+ 31. Kh1 fxe1=R+ 32. Rxe1 Rxg5 33. Qxd7 Qf2 34. Rg1 Qg3 35. Qe6 Qh4+ 36. Qh3 Qxh3+ 37. gxh3 Rxg1+ 38. Kxg1 Kg7 39. Nc6 a5 40. Ne7 Kf6 41. Nf5 Kg5 42. Ng7 Kh4 43. Ne6 Kxh3 44. Nxc7 Rg8+ 45. Kh1 Kg3 46. Bb5 h4 47. Ne8 Kf4 48. Nxd6 Rd8 49. Nc4 Rxd5 50. Nxb6 Rg5 51. Nc4 Kg3 52. Nd2 Rf5 53. Bc6 h3 54. Ne4+ Kh4 55. Kg1 Rf4 56. b4 Rg4+ 57. Kh1 axb4 58. Nf2 Rg2 59. d6 Rxb4");
		excluded.add("1. d4 e6 2. c4 Nf6 3. Nf3 c5 4. d5 exd5 5. cxd5 d6 6. Nc3 g6 7. h3 Bg7 8. e4 O-O 9. Bd3 b5 10. Nxb5 Nxe4 11. Bxe4 Re8 12. Ng5 Qa5+ 13. Nc3 Ba6 14. Qc2 f5 15. Ne6 fxe4 16. Bd2 Nc6 17. dxc6 Rxe6 18. Qb3 c4 19. Qd1 d5 20. Qg4 Rae8 21. O-O Qc5 22. Qg5 Re5 23. Qg3 d4 24. Ne2 R5e7 25. Bg5 Rf7 26. Rfe1 Qxc6 27. Rad1 Qb6 28. Bc1 Bb7 29. Qh4 Qc5 30. Ng3 Qc6 31. Re2 e3 32. fxe3 d3 33. Red2 Rxe3 34. Nf1 Re4 35. Qg3 Be5 36. Qg5 Qb6+ 37. Kh1 Bf4 38. Qxb5 Re1 39. d8 Qxd8");
		excluded.add("1. b3 e5 2. Bb2 d6 3. e4 Nf6 4. Nc3 g6 5. Nf3 c5 6. Bc4 Bg7 7. Nd5 O-O 8. Nxf6+ Qxf6 9. Qe2 Nc6 10. O-O-O a6 11. c3 b5 12. Bd5 Bd7 13. Bxc6 Bxc6 14. d4 Qf4+ 15. Qd2 Bxe4 16. Qxf4 exf4 17. dxc5 dxc5 18. Rhe1 Rfe8 19. Rd7 Rac8 20. Ng5 Bf5 21. Red1 h6 22. Nf3 Be4 23. R1d2 b4 24. c4 Bxb2+ 25. Kxb2 Bxf3 26. Bxf3");
		excluded.add("1. d4 e6 2. c4 Nf6 3. Nf3 c5 4. d5 exd5 5. cxd5 d6 6. Nc3 g6 7. h3 Bg7 8. e4 O-O 9. Bd3 b5 10. Nxb5 Nxe4 11. Bxe4 Re8 12. Ng5 Qa5+ 13. Nc3 Ba6 14. Qc2 f5 15. Ne6 fxe4 16. Bd2 Nc6 17. dxc6 Rxe6 18. Qb3 c4 19. Qd1 d5 20. Qg4 Rae8 21. O-O Qc5 22. Qg5 Re5 23. Qg3 d4 24. Ne2 R5e7 25. Bg5 Rf7 26. Rfe1 Qxc6 27. Rad1 Qb6 28. Bc1 Bb7 29. Qh4 Qc5 30. Ng3 Qc6 31. Re2 e3 32. fxe3 d3 33. Red2 Rxe3 34. Nf1 Re4 35. Qg3 Be5 36. Qg5 Qb6+ 37. Kh1 Bf4 38. Bf3 Re1 39. Qd8+ Qxd8");
		excluded.add("1. e4 e5 2. Nf3 d6 3. d4 Be7 4. Nc3 Nd7 5. Bc4 h6 6. dxe5 Nxe5 7. Nxe5 dxe5 8. O-O Nf6 9. Nd5 Be6 10. f3 c6 11. Nxf6+ gxf6 12. Bxe6 fxe6 13. f4 Qb6+ 14. Kh1 exf4 15. Bxf4 Qxf6 16. Qg4 Nd7 17. Rad1 Qf5 18. Raxd1 Kd8 19. c4 Ke8 20. Bd6 Bxd6 21. Rxd6 Kf7 22. Qb4 Kg6 23. Bxd5 Kg7 24. Qxb7 Kg8 25. xxe6+ Kg7 26. Rxd7+ Kf8 27. Rd3");
		excluded.add("1. e4 d6 2. d4 Nf6 3. Nc3 c6 4. f3 Nbd7 5. Be3 b5 6. Qd2 b4 7. Nce2 a5 8. g4 Nb6 9. Ng3 Ba6 10. Bxa6 Rxa6 11. Qd3 Ra8 12. O-O-O e6 13. g5 Nfd7 14. f4 Be7 15. Nf3 Qc8 16. f5 Qa6 17. Kb1 Qxd3 18. cxd3 axb6 19. Rhf1 Kxg7 20. Nd2 e5 21. h4 f6 22. Nf3 c5 23. d5 Nxa8 24. Nh5+ hxg4 25. b3 a4 26. Rg1 axb3 27. axb3 Nxg4 28. g6 hxg6 29. fxg6 Ne6 30. Kb2 f5 31. exf5 Ra4 32. Bg5 Bxg5 33. Nxg5 Nxg5 34. xxg7 Bxg5 35. Bf4 Qe7 36. h5 Bxf4 37. Qxf4 Rh8 38. e4 Qh4 39. Kf1 Qh3 40. Ke3 Rg3+ 41. Qf3 Rxf3+ 42. d2 fxe6 43. Rd7 c4 44. f2+ Qxb3+ 45. Ke2 Qc2+");
		excluded.add("1. e4 d6 2. d4 g6 3. Nc3 Nf6 4. Be3 c6 5. f3 b5 6. Bd3 Nbd7 7. Qd2 Bb7 8. g4 Qa5 9. a3 a6 10. O-O-O O-O-O 11. Qe1 Kb8 12. e5 dxe5 13. dxe5 Nd5 14. Be2 Qc7 15. Nxd5 cxd5 16. Bg5 Nb6 17. Qf2 Ka7 18. Qb3");
		excluded.add("1. d4 Nf6 2. c4 e6 3. Nf3 d5 4. Nc3 Bb4 5. Qa4+ Nc6 6. e3 O-O 7. Bd2 Re8 8. Rd1 a6 9. a3 Bf8 10. Qc2 h6 11. b4 Bd7 12. h3 dxc4 13. Bxc4 b5 14. Bd3 a5 15. Nxb5 axb4 16. a4 e5 17. dxe5 Nxe5 18. Nxe5 Rxe5 19. Bc4 c6 20. Nd4 Qa5 21. Ra1 c5 22. Nb3 Qa7 23. a5 Qb7 24. f3 Rg5 25. e4 Rxg2 26. Rc1 Bb5 27. g1 Rxg1+ 28. Rxg1 Bxc4 29. Qxc4 Qa6");
		excluded.add("1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. d3 b5 6. Bb3 Be7 7. O-O O-O 8. Nc3 d6 9. a3 Bg4 10. Be3 Qd7 11. h3 Bh5 12. Nd5 Nxd5 13. Bxd5 Bxf3 14. Qxf3 Rab8 15. c3 Bf6 16. Bc2 Ne7 17. a2 Ng6 18. d4 Qe7 19. Qg4 Rfe8 20. Rxe8+ Qxe8 21. dxe5 Nxe5 22. Qe2");
		excluded.add("1. e4 c5 2. Nf3 Nc6 3. d4 cxd4 4. Nxd4 Qb6 5. c3 Nf6 6. Bd3 Nxd4 7. cxd4 Qxd4 8. Nc3 e6 9. O-O Bb4 10. Be3 Qe5 11. Rc1 Bxc3 12. f4 Qh5 13. Qxh5 Nxh5 14. Rxc3 b6 15. e5 f5 16. exf6 Nxf6 17. Rfc1 O-O 18. Bd4 Bb7 19. Bb5 a6 20. Ba4 b5 21. Bb3 Nd5 22. Rg3 g6 23. Bd1 Nxf4 24. Rcc3 Rac8 25. Rxc8 Rxc8 26. Be3 Nd5 27. Bh6 Ne7 28. Rd3 Bd5 29. Bf3 Bxf3 30. Rxf3 Nf5 31. Bf4 Nd4 32. Rf2 Rc2 33. Rxc2 Nxc2 34. Bd6 Kf7 35. Kf2 Kf6 36. Kf3 Ne1+ 37. Kf2 Nd3+ 38. Ke3 Nxb2 39. Kd4 Nc4 40. Bg3 d5 41. Qh4");
		excluded.add("1. e4 c5 2. Nf3 Nc6 3. d4 cxd4 4. Nxd4 Qb6 5. Nb3 Nf6 6. Nc3 e6 7. Bd3 a6 8. Qe2 d6 9. g4 h6 10. f4 Qc7 11. Rg1 b5 12. a4 b4 13. Nd1 Bb7 14. Ne3 Nd7 15. a5 Nc5 16. Nxc5 dxc5 17. Nc4 Nd4 18. Qe3 g5 19. Rf1 gxf4 20. Qxf4 Qxf4 21. Bxf4 Rd8 22. O-O-O Be7 23. Kb1 Nb5 24. Rd2 Bg5 25. Rdf2 Bxf4 26. Rxf4 Rh7 27. e5 Rg7 28. Rf6 Ke7 29. Ne3 Nd4 30. Rxh6 Nf3 31. Bxa6 Nd2+ 32. Kc1 Bxa6 33. Rd1 Nf3 34. Rd6 Rxd6 35. exd6+ Kd7 36. b3 Ne5 37. h3 Rg8 38. Rh7 Kxd6 39. h4 Nxg4 40. Nxg4 Rxg4 41. Rxf7 Rxh4 42. Ra7 Bb5 43. Rb7 Bc6 44. a6 Rd4 45. Re7 Kd5 46. Ng8+");
		excluded.add("1. Nc3 c5 2. e4 e6 3. f4 Nc6 4. Bb5 Qc7 5. d3 a6 6. Bxc6 Qxc6 7. Nf3 Nf6 8. O-O Be7 9. Qe1 O-O 10. Bd2 b5 11. Ne5 Qc7 12. Qg3 d6 13. Ng4 b4 14. Nd1 Nxg4 15. Qxg4 f5 16. exf5 exf5 17. Qh3 Bf6 18. c3 a5 19. Ne3 Be6 20. Qf3 Qf7 21. a3 Rab8 22. axb4 axb4 23. Ra6 d5 24. Raa1 Rfd8 25. Nd1 bxc3 26. bxc3 d4 27. c4 Bc8 28. Ra7 Qe6 29. Ba5 Re8 30. Bc7 Qe1 31. Rh3");
		excluded.add("1. e4 e6 2. Nf3 c5 3. d4 cxd4 4. Nxd4 a6 5. Nc3 Qc7 6. Bd3 Bc5 7. Nde2 b5 8. Ng3 Nf6 9. O-O d6 10. Kh1 h5 11. Qe1 Bb7 12. Be3 h4 13. Nge2 Bxe3 14. fxe3 h3 15. Qg3 Nbd7 16. Nd4 hxg2+ 17. Kxg2 Qb6 18. a3 Nc5 19. Qf3 O-O-O 20. b4 Nfd7 21. a4 Ne5 22. Qe2 Qc7 23. Ra3 Ng4 24. Nf3 d5 25. e5 Nxe5 26. Nxe5 Qxe5 27. Kg1 d4 28. Qf2 Qxe3 29. axb5 axb5 30. Rxf2 Qf4");
		excluded.add("1. d4 Nf6 2. Bg5 Ne4 3. Bf4 d5 4. f3 Nd6 5. Nc3 c6 6. e4 e6 7. a3 Be7 8. Bd3 O-O 9. Nge2 Nd7 10. O-O b6 11. Kh1 Bb7 12. Qe1 c5 13. dxc5 Nxc5 14. Rd1 Nxd3 15. Rxd3 Nc4 16. Bc1 Rc8 17. exd5 exd5 18. Nxd5 Bxd5 19. Nc3 Bh4 20. g3 Bxf3+ 21. Rfxf3 Qe8 22. Rf1 Qc6+ 23. Nd5 Bd8 24. Qe4 b5 25. Rfd1 Re8 26. Qg2 Qe6 27. h3 h6 28. Nf4 Qc6 29. Rb3 Bg5 30. Nxb5");
		excluded.add("1. e4 e5 2. Nc3 Nc6 3. Bc4 Nf6 4. d3 Bc5 5. Bg5 h6 6. Bh4 d6 7. Na4 Bb6 8. Nxb6 axb6 9. c3 Be6 10. Bxe6 fxe6 11. Bxf6 Qxf6 12. Ne2 Ne7 13. Qd2 Ng6 14. O-O Nf4 15. Nxf4 exf4 16. e5 dxe5 17. Rfe1 O-O-O 18. Re4 Rd5 19. Qe2 Rhd8 20. Rd1 Qg5 21. c4 f3 22. Qf1 Rxd3 23. Rxd3 Rxd3 24. h4 Qg6 25. Re1 Rd4 26. g3 Rxh4 27. Rxe5 Re4 28. Rb5 Re2 29. Rb3 Qe4 30. Kh2 Re1 31. Qd3 Qxd3 32. Rxd3 Re5 33. g4 Re4 34. Kg3 Rxc4 35. Rxf3 Rc2 36. Rf7 e5 37. Re7 Rxb2 38. Re6 Rxa2 39. Kxg6");
		
	}
	
	public static final boolean isExcluded(String gameID) { 
		return excluded.contains(gameID);
	}
}

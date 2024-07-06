/*
  Stockfish, a UCI chess playing engine derived from Glaurung 2.1
  Copyright (C) 2004-2024 The Stockfish developers (see AUTHORS file)

  Stockfish is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  Stockfish is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#include <iostream>

#include "probe.h"

using namespace Stockfish;

int main() {
    Probe::init("nn-b1a57edbea57.nnue", "nn-baff1ede1f90.nnue");

    std::cout<<"Fen Eval: "<<Probe::eval("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")<<std::endl;

    int pieceBoard[] = {4, 2, 3, 5, 6, 3, 2, 4,
                        1, 1, 1, 1, 1, 1, 1, 1,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0,
                        9, 9, 9, 9, 9, 9, 9, 9,
                        12, 10, 11, 13, 14, 11, 10, 12};
    std::cout<<"Pos Eval: "<<Probe::eval(pieceBoard, true, 0)<<std::endl;

    int pieces[] = {4, 2, 3, 5, 6, 3, 2, 4,
                    1, 1, 1, 1, 1, 1, 1, 1,
                    9, 9, 9, 9, 9, 9, 9, 9,
                    12, 10, 11, 13, 14, 11, 10, 12};

    int squares[] = {
            0, 1, 2,3, 4, 5, 6, 7,
            8, 9, 10, 11, 12, 13, 14, 15,
            48, 49, 50, 51, 52, 53, 54, 55,
            56, 57, 58, 59, 60, 61, 62, 63
    };

    std::cout<<"Faster Pos Eval: "<<Probe::eval(pieces, squares, 32, true, 0)<<std::endl;

    std::cin.get();
    return 0;
}

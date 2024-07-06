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

#include "evaluate.h"

#include <algorithm>
#include <cstdlib>
#include <fstream>
#include <iostream>
#include <sstream>
#include <unordered_map>
#include <vector>

#include "incbin/incbin.h"
#include "nnue/evaluate_nnue.h"
#include "position.h"
#include "types.h"

// Macro to embed the default efficiently updatable neural network (NNUE) file
// data in the engine binary (using incbin.h, by Dale Weiler).
// This macro invocation will declare the following three variables
//     const unsigned char        gEmbeddedNNUEData[];  // a pointer to the embedded data
//     const unsigned char *const gEmbeddedNNUEEnd;     // a marker to the end
//     const unsigned int         gEmbeddedNNUESize;    // the size of the embedded file
// Note that this does not work in Microsoft Visual Studio.
#if !defined(_MSC_VER) && !defined(NNUE_EMBEDDING_OFF)
INCBIN(EmbeddedNNUEBig, EvalFileDefaultNameBig);
INCBIN(EmbeddedNNUESmall, EvalFileDefaultNameSmall);
#else
const unsigned char        gEmbeddedNNUEBigData[1]   = {0x0};
const unsigned char* const gEmbeddedNNUEBigEnd       = &gEmbeddedNNUEBigData[1];
const unsigned int         gEmbeddedNNUEBigSize      = 1;
const unsigned char        gEmbeddedNNUESmallData[1] = {0x0};
const unsigned char* const gEmbeddedNNUESmallEnd     = &gEmbeddedNNUESmallData[1];
const unsigned int         gEmbeddedNNUESmallSize    = 1;
#endif


namespace Stockfish {

namespace Eval {

    NNUE::EvalFiles NNUE::load_networks(const std::string& rootDirectory,
                                        NNUE::EvalFiles    evalFiles) {

        for (auto& [netSize, evalFile] : evalFiles)
        {
            std::string user_eval_file = evalFile.defaultName;

#if defined(DEFAULT_NNUE_DIRECTORY)
            std::vector<std::string> dirs = {"<internal>", "", rootDirectory,
                                         stringify(DEFAULT_NNUE_DIRECTORY)};
#else
            std::vector<std::string> dirs = {"<internal>", "", rootDirectory};
#endif

            for (const std::string& directory : dirs)
            {
                if (evalFile.current != user_eval_file)
                {
                    if (directory != "<internal>")
                    {
                        std::ifstream stream(directory + user_eval_file, std::ios::binary);
                        auto          description = NNUE::load_eval(stream, netSize);

                        if (description.has_value())
                        {
                            evalFile.current        = user_eval_file;
                            evalFile.netDescription = description.value();
                        }
                    }

                    if (directory == "<internal>" && user_eval_file == evalFile.defaultName)
                    {
                        // C++ way to prepare a buffer for a memory stream
                        class MemoryBuffer: public std::basic_streambuf<char> {
                        public:
                            MemoryBuffer(char* p, size_t n) {
                                setg(p, p, p + n);
                                setp(p, p + n);
                            }
                        };

                        MemoryBuffer buffer(
                                const_cast<char*>(reinterpret_cast<const char*>(
                                        netSize == Small ? gEmbeddedNNUESmallData : gEmbeddedNNUEBigData)),
                                size_t(netSize == Small ? gEmbeddedNNUESmallSize : gEmbeddedNNUEBigSize));
                        (void) gEmbeddedNNUEBigEnd;  // Silence warning on unused variable
                        (void) gEmbeddedNNUESmallEnd;

                        std::istream stream(&buffer);
                        auto         description = NNUE::load_eval(stream, netSize);


                        if (description.has_value())
                        {
                            evalFile.current        = user_eval_file;
                            evalFile.netDescription = description.value();
                        }
                    }
                }
            }
        }

        return evalFiles;
    }
}

// Returns a static, purely materialistic evaluation of the position from
// the point of view of the given color. It can be divided by PawnValue to get
// an approximation of the material advantage on the board in terms of pawns.
int Eval::simple_eval(const Position& pos, Color c) {
    return PawnValue * (pos.count<PAWN>(c) - pos.count<PAWN>(~c))
         + (pos.non_pawn_material(c) - pos.non_pawn_material(~c));
}


    Value Eval::evaluate(const Position& pos) {

        int  simpleEval = simple_eval(pos, pos.side_to_move());
        bool smallNet   = std::abs(simpleEval) > 1050;

        int nnueComplexity;

        Value nnue = smallNet ? NNUE::evaluate<NNUE::Small>(pos, true, &nnueComplexity)
                              : NNUE::evaluate<NNUE::Big>(pos, true, &nnueComplexity);

        nnue -= nnue * (nnueComplexity + std::abs(simpleEval - nnue)) / 32768;

        int npm = pos.non_pawn_material() / 64;
        int v   = (nnue * (915 + npm + 9 * pos.count<PAWN>())) / 1024;

        // Damp down the evaluation linearly when shuffling
        int shuffling = pos.rule50_count();
        v             = v * (200 - shuffling) / 214;

        // Guarantee evaluation does not hit the tablebase range
        v = std::clamp(v, VALUE_TB_LOSS_IN_MAX_PLY + 1, VALUE_TB_WIN_IN_MAX_PLY - 1);

        return v;
    }
}  // namespace Stockfish

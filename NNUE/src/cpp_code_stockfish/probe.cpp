#include "probe.h"
#include "bitboard.h"
#include "position.h"
#include "evaluate.h"
#include "nnue/nnue_architecture.h"

namespace Stockfish {

    namespace Probe {

        void init( const char *bigNetFile, const char *smallNetFile) {
            Bitboards::init();

            std::unordered_map<Eval::NNUE::NetSize, Eval::EvalFile> evalFiles = {
                    {Eval::NNUE::Big,   {"EvalFile",      bigNetFile,   "None", ""}},
                    {Eval::NNUE::Small, {"EvalFileSmall", smallNetFile, "None", ""}}
            };

            evalFiles = Eval::NNUE::load_networks("", evalFiles);

            for (auto &[netSize, evalFile]: evalFiles) {
                std::cout << "Option: " << evalFile.optionName << std::endl; // Print other members similarly
                std::cout << "Name: " << evalFile.defaultName << std::endl;
                std::cout << std::endl;
            }
        }

        int eval(const char *fen) {
            Position pos;
            StateListPtr states(new std::deque<StateInfo>(1));

            pos.set(fen, &states->back());
            int eval = Eval::evaluate(pos);

            return eval;
        }

        int eval(const int pieceBoard[], bool side, int rule50) {
            Position pos;
            StateListPtr states(new std::deque<StateInfo>(1));

            pos.set(pieceBoard, side, rule50, &states->back());
            int eval = Eval::evaluate(pos);

            return eval;
        }

        int eval(const int pieces[], const int squares[], int pieceAmount, bool side, int rule50) {
            Position pos;
            StateListPtr states(new std::deque<StateInfo>(1));

            pos.set(pieces, squares, pieceAmount, side, rule50, &states->back());
            int eval = Eval::evaluate(pos);

            return eval;
        }
    }
}
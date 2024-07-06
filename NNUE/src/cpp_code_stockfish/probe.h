#ifndef STOCKFISH_PROBE_H
#define STOCKFISH_PROBE_H

namespace Stockfish {
    namespace Probe {
        void init(const char*, const char*);

        int eval(const char *fen);
        int eval(const int pieceBoard[], bool side, int rule50);
        int eval(const int pieces[], const int squares[], int pieceAmount, bool side, int rule50);
    }
}

#endif //STOCKFISH_PROBE_H

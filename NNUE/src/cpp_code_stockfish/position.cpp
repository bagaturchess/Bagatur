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

#include "position.h"

#include <array>
#include <cctype>
#include <cstring>
#include <iostream>
#include <sstream>
#include <string_view>

#include "bitboard.h"
#include "misc.h"

using std::string;

namespace Stockfish {

namespace {

constexpr std::string_view PieceToChar(" PNBRQK  pnbrqk");

constexpr Piece Pieces[] = {W_PAWN, W_KNIGHT, W_BISHOP, W_ROOK, W_QUEEN, W_KING,
                            B_PAWN, B_KNIGHT, B_BISHOP, B_ROOK, B_QUEEN, B_KING};
}  // namespace


// Returns an ASCII representation of the position
void Position::print(const Position& pos) {

    std::cout << "\n +---+---+---+---+---+---+---+---+\n";

    for (Rank r = RANK_8; r >= RANK_1; --r)
    {
        for (File f = FILE_A; f <= FILE_H; ++f)
            std::cout << " | " << PieceToChar[pos.piece_on(make_square(f, r))];

        std::cout << " | " << (1 + r) << "\n +---+---+---+---+---+---+---+---+\n";
    }

        std::cout << "   a   b   c   d   e   f   g   h\n";
}

Position& Position::set(const int pieceBoard[], bool side, int rule50, StateInfo* si) {

    std::memset(this, 0, sizeof(Position));
    std::memset(si, 0, sizeof(StateInfo));
    st = si;

    st->nonPawnMaterial[WHITE] = st->nonPawnMaterial[BLACK] = VALUE_ZERO;
    st->rule50 = rule50;

    for (int i = 0; i < 64; i++) {
        if (pieceBoard[i] != 0) {
            Piece pc = Piece(pieceBoard[i]);
            put_piece(pc, Square(i));
            if (type_of(pc) != PAWN && type_of(pc) != KING) {
                st->nonPawnMaterial[color_of(pc)] += PieceValue[pc];
            }
        }
    }

    sideToMove = side ? WHITE : BLACK;

    return *this;
}

Position& Position::set(const int pieces[], const int squares[], int pieceAmount, bool side, int rule50, StateInfo* si) {

    std::memset(this, 0, sizeof(Position));
    std::memset(si, 0, sizeof(StateInfo));
    st = si;

    st->nonPawnMaterial[WHITE] = st->nonPawnMaterial[BLACK] = VALUE_ZERO;
    st->rule50 = rule50;

    for (int i = 0; i < pieceAmount; i++) {
        Piece pc = Piece(pieces[i]);
        put_piece(pc, Square(squares[i]));
        if (type_of(pc) != PAWN && type_of(pc) != KING) {
            st->nonPawnMaterial[color_of(pc)] += PieceValue[pc];
        }
    }

    sideToMove = side ? WHITE : BLACK;

    return *this;
}

// Initializes the position object with the given FEN string.
// This function is not very robust - make sure that input FENs are correct,
// this is assumed to be the responsibility of the GUI.
Position& Position::set(const string& fenStr, StateInfo* si) {
    /*
   A FEN string defines a particular position using only the ASCII character set.

   A FEN string contains six fields separated by a space. The fields are:

   1) Piece placement (from white's perspective). Each rank is described, starting
      with rank 8 and ending with rank 1. Within each rank, the contents of each
      square are described from file A through file H. Following the Standard
      Algebraic Notation (SAN), each piece is identified by a single letter taken
      from the standard English names. White pieces are designated using upper-case
      letters ("PNBRQK") whilst Black uses lowercase ("pnbrqk"). Blank squares are
      noted using digits 1 through 8 (the number of blank squares), and "/"
      separates ranks.

   2) Active color. "w" means white moves next, "b" means black.

   3) Castling availability. If neither side can castle, this is "-". Otherwise,
      this has one or more letters: "K" (White can castle kingside), "Q" (White
      can castle queenside), "k" (Black can castle kingside), and/or "q" (Black
      can castle queenside).

   4) En passant target square (in algebraic notation). If there's no en passant
      target square, this is "-". If a pawn has just made a 2-square move, this
      is the position "behind" the pawn. Following X-FEN standard, this is recorded
      only if there is a pawn in position to make an en passant capture, and if
      there really is a pawn that might have advanced two squares.

   5) Halfmove clock. This is the number of halfmoves since the last pawn advance
      or capture. This is used to determine if a draw can be claimed under the
      fifty-move rule.

   6) Fullmove number. The number of the full move. It starts at 1, and is
      incremented after Black's move.
*/

    unsigned char      col, row, token;
    size_t             idx;
    Square             sq = SQ_A8;
    std::istringstream ss(fenStr);

    std::memset(this, 0, sizeof(Position));
    std::memset(si, 0, sizeof(StateInfo));
    st = si;

    ss >> std::noskipws;
    st->nonPawnMaterial[WHITE] = st->nonPawnMaterial[BLACK] = VALUE_ZERO;

    // 1. Piece placement
    while ((ss >> token) && !isspace(token))
    {
        if (isdigit(token))
            sq += (token - '0') * EAST;  // Advance the given number of files

        else if (token == '/')
            sq += 2 * SOUTH;

        else if ((idx = PieceToChar.find(token)) != string::npos)
        {
            put_piece(Piece(idx), sq);
            ++sq;
            if (type_of(Piece(idx)) != PAWN && type_of(Piece(idx)) != KING) {
                st->nonPawnMaterial[color_of(Piece(idx))] += PieceValue[Piece(idx)];
            }
        }
    }

    // 2. Active color
    ss >> token;
    sideToMove = (token == 'w' ? WHITE : BLACK);
    ss >> token;

    while ((ss >> token) && !isspace(token))
    {
    }
    if (((ss >> col) && (col >= 'a' && col <= 'h'))
        && ((ss >> row) && (row == (sideToMove == WHITE ? '6' : '3'))))
    {

    }

    ss >> std::skipws >> st->rule50 >> gamePly;

    // Convert from fullmove starting from 1 to gamePly starting from 0,
    // handle also common incorrect FEN with fullmove = 0.
    gamePly = std::max(2 * (gamePly - 1), 0) + (sideToMove == BLACK);
    return *this;
}
}  // namespace Stockfish

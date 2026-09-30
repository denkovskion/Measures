/*
 * MIT License
 *
 * Copyright (c) 2026 Ivan Denkovski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package blog.art.chess.measures.piece

import blog.art.chess.measures.move.Move
import groovy.transform.CompileStatic

@CompileStatic
abstract class Piece {
    static boolean generateMoves(List<Piece> board, boolean blackToMove, Set<Integer> castlingOrigins, Integer enPassantTarget, List<Move> moves) {
        for (int origin in 0..<64) {
            Piece piece = board.get(origin)
            if (piece != null && piece.black == blackToMove) {
                if (!piece.generateMoves(board, origin, castlingOrigins, enPassantTarget, moves)) {
                    return false
                }
            }
        }
        return true
    }

    protected abstract boolean isBlack()

    protected abstract boolean generateMoves(List<Piece> board, int origin, Set<Integer> castlingOrigins, Integer enPassantTarget, List<Move> moves)

    abstract String getUciCode()

    static String toUciCode(int square) {
        return new String([('a' as char) + square.intdiv(8), ('1' as char) + square % 8] as char[])
    }

    static void validate(List<Piece> board, boolean blackToMove, Set<Integer> castlingOrigins, Integer enPassantTarget) {
        for (boolean value : [false, true]) {
            int frequency = 0
            for (Piece piece : board) {
                if (piece instanceof King && piece.black == value) {
                    frequency++
                }
            }
            if (!(frequency == 1)) {
                throw new IllegalArgumentException('Not accepted number of kings')
            }
        }
        for (int castlingOrigin : castlingOrigins) {
            Piece piece = board.get(castlingOrigin)
            int file = castlingOrigin.intdiv(8) + 1
            int rank = castlingOrigin % 8 + 1
            if (!((file == 5 && piece instanceof King || (file == 1 || file == 8) && piece instanceof Rook) && (rank == 1 && !piece.black || rank == 8 && piece.black))) {
                throw new IllegalArgumentException('Not accepted castling rights')
            }
        }
        if (enPassantTarget != null) {
            Piece captured = board.get(enPassantTarget + (blackToMove ? 1 : -1))
            if (!(enPassantTarget % 8 + 1 == (blackToMove ? 3 : 6) && board.get(enPassantTarget + (blackToMove ? -1 : 1)) == null && board.get(enPassantTarget) == null && captured instanceof Pawn && captured.black != blackToMove)) {
                throw new IllegalArgumentException('Not accepted en passant square')
            }
        }
    }
}

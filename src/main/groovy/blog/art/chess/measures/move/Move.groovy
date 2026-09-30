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

package blog.art.chess.measures.move

import blog.art.chess.measures.game.Position
import blog.art.chess.measures.piece.Piece
import groovy.transform.CompileStatic

@CompileStatic
abstract class Move {
    static boolean isPositionLegal(Position position, List<Move> pseudoLegalMoves) {
        return Piece.generateMoves(position.board, position.blackToMove, position.castlingOrigins, position.enPassantTarget, pseudoLegalMoves)
    }

    Position make(Position position, List<Move> pseudoLegalMoves) {
        if (preMake(position)) {
            List<Piece> board = new ArrayList<>(position.board)
            updateBoard(board)
            boolean blackToMove = !position.blackToMove
            Set<Integer> castlingOrigins = new HashSet<>(position.castlingOrigins)
            updateCastlingOrigins(castlingOrigins)
            Integer enPassantTarget = getEnPassantTarget()
            Position result = new Position(board, blackToMove, castlingOrigins, enPassantTarget)
            if (isPositionLegal(result, pseudoLegalMoves)) {
                return result
            }
        }
        return null
    }

    protected boolean preMake(Position position) {
        return true
    }

    protected void updateBoard(List<Piece> board) {}

    protected void updateCastlingOrigins(Set<Integer> castlingOrigins) {}

    protected Integer getEnPassantTarget() {
        return null
    }

    abstract String getUciCode()
}

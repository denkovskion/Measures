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
class NullMove extends Move {
    @Override
    String getUciCode() {
        return '0000'
    }
}

@CompileStatic
class QuietMove extends Move {
    private final int origin
    private final int target

    QuietMove(int origin, int target) {
        this.origin = origin
        this.target = target
    }

    @Override
    protected void updateBoard(List<Piece> board) {
        board.set(target, board.set(origin, null))
    }

    @Override
    protected void updateCastlingOrigins(Set<Integer> castlingOrigins) {
        castlingOrigins.remove(origin)
    }

    @Override
    String getUciCode() {
        return "${Piece.toUciCode(origin)}${Piece.toUciCode(target)}"
    }
}

@CompileStatic
class Capture extends Move {
    private final int origin
    private final int target

    Capture(int origin, int target) {
        this.origin = origin
        this.target = target
    }

    @Override
    protected void updateBoard(List<Piece> board) {
        board.set(target, board.set(origin, null))
    }

    @Override
    protected void updateCastlingOrigins(Set<Integer> castlingOrigins) {
        castlingOrigins.remove(origin)
        castlingOrigins.remove(target)
    }

    @Override
    String getUciCode() {
        return "${Piece.toUciCode(origin)}${Piece.toUciCode(target)}"
    }
}

@CompileStatic
class Castling extends Move {
    private final int origin
    private final int target
    private final int origin2
    private final int target2

    Castling(int origin, int target, int origin2, int target2) {
        this.origin = origin
        this.target = target
        this.origin2 = origin2
        this.target2 = target2
    }

    @Override
    protected boolean preMake(Position position) {
        if (new NullMove().make(position, null) != null) {
            if (new QuietMove(origin, target2).make(position, null) != null) {
                return true
            }
        }
        return false
    }

    @Override
    protected void updateBoard(List<Piece> board) {
        board.set(target, board.set(origin, null))
        board.set(target2, board.set(origin2, null))
    }

    @Override
    protected void updateCastlingOrigins(Set<Integer> castlingOrigins) {
        castlingOrigins.remove(origin)
        castlingOrigins.remove(origin2)
    }

    @Override
    String getUciCode() {
        return "${Piece.toUciCode(origin)}${Piece.toUciCode(target)}"
    }
}

@CompileStatic
class DoubleStep extends Move {
    private final int origin
    private final int target
    private final int stop

    DoubleStep(int origin, int target, int stop) {
        this.origin = origin
        this.target = target
        this.stop = stop
    }

    @Override
    protected void updateBoard(List<Piece> board) {
        board.set(target, board.set(origin, null))
    }

    @Override
    protected Integer newEnPassantTarget() {
        return stop
    }

    @Override
    String getUciCode() {
        return "${Piece.toUciCode(origin)}${Piece.toUciCode(target)}"
    }
}

@CompileStatic
class EnPassant extends Move {
    private final int origin
    private final int target
    private final int stop

    EnPassant(int origin, int target, int stop) {
        this.origin = origin
        this.target = target
        this.stop = stop
    }

    @Override
    protected void updateBoard(List<Piece> board) {
        board.set(stop, null)
        board.set(target, board.set(origin, null))
    }

    @Override
    String getUciCode() {
        return "${Piece.toUciCode(origin)}${Piece.toUciCode(target)}"
    }
}

@CompileStatic
class Promotion extends Move {
    private final int origin
    private final int target
    private final Piece promoted

    Promotion(int origin, int target, Piece promoted) {
        this.origin = origin
        this.target = target
        this.promoted = promoted
    }

    @Override
    protected void updateBoard(List<Piece> board) {
        board.set(origin, null)
        board.set(target, promoted)
    }

    @Override
    String getUciCode() {
        return "${Piece.toUciCode(origin)}${Piece.toUciCode(target)}$promoted.uciCode"
    }
}

@CompileStatic
class PromotionCapture extends Move {
    private final int origin
    private final int target
    private final Piece promoted

    PromotionCapture(int origin, int target, Piece promoted) {
        this.origin = origin
        this.target = target
        this.promoted = promoted
    }

    @Override
    protected void updateBoard(List<Piece> board) {
        board.set(origin, null)
        board.set(target, promoted)
    }

    @Override
    protected void updateCastlingOrigins(Set<Integer> castlingOrigins) {
        castlingOrigins.remove(target)
    }

    @Override
    String getUciCode() {
        return "${Piece.toUciCode(origin)}${Piece.toUciCode(target)}$promoted.uciCode"
    }
}

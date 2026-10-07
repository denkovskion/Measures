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

import blog.art.chess.measures.move.Capture
import blog.art.chess.measures.move.Move
import blog.art.chess.measures.move.QuietMove
import groovy.transform.CompileStatic

@CompileStatic
abstract class Leaper extends Piece {
    @Override
    protected boolean generateMoves(List<Piece> board, int origin, Set<Integer> castlingOrigins, Integer enPassantTarget, List<Move> moves) {
        for (int direction in directions) {
            int target = origin + direction
            if (target in 0..<64 && Math.abs(target.intdiv(8) - origin.intdiv(8)) <= maxOffset && Math.abs(target % 8 - origin % 8) <= maxOffset) {
                Piece other = board[target]
                if (other != null) {
                    if (other.black != black) {
                        if (other instanceof King) {
                            return false
                        }
                        moves?.add(new Capture(origin, target))
                    }
                } else {
                    moves?.add(new QuietMove(origin, target))
                }
            }
        }
        return true
    }

    protected abstract int[] getDirections()

    protected abstract int getMaxOffset()
}

@CompileStatic
abstract class Rider extends Piece {
    @Override
    protected boolean generateMoves(List<Piece> board, int origin, Set<Integer> castlingOrigins, Integer enPassantTarget, List<Move> moves) {
        for (int direction in directions) {
            int square = origin
            while (true) {
                int target = square + direction
                if (target in 0..<64 && Math.abs(target.intdiv(8) - square.intdiv(8)) <= maxOffset && Math.abs(target % 8 - square % 8) <= maxOffset) {
                    Piece other = board[target]
                    if (other != null) {
                        if (other.black != black) {
                            if (other instanceof King) {
                                return false
                            }
                            moves?.add(new Capture(origin, target))
                        }
                        break
                    } else {
                        moves?.add(new QuietMove(origin, target))
                    }
                    square = target
                } else {
                    break
                }
            }
        }
        return true
    }

    protected abstract int[] getDirections()

    protected abstract int getMaxOffset()
}

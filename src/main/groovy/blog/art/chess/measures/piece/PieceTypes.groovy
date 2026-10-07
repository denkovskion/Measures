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

import blog.art.chess.measures.move.*
import groovy.transform.CompileStatic

@CompileStatic
class King extends Leaper {
    private final boolean black

    King(boolean black) {
        this.black = black
    }

    @Override
    protected boolean isBlack() {
        return black
    }

    @Override
    protected boolean generateMoves(List<Piece> board, int origin, Set<Integer> castlingOrigins, Integer enPassantTarget, List<Move> moves) {
        if (!super.generateMoves(board, origin, castlingOrigins, enPassantTarget, moves)) {
            return false
        }
        if (origin in castlingOrigins) {
            int[] castlingDirections = [-8, 8]
            for (int direction in castlingDirections) {
                int target2 = origin + direction
                if (board[target2] == null) {
                    int target = target2 + direction
                    if (board[target] == null) {
                        if (direction > 0) {
                            int origin2 = target + direction
                            if (origin2 in castlingOrigins) {
                                moves?.add(new Castling(origin, target, origin2, target2))
                            }
                        } else {
                            int stop = target + direction
                            if (board[stop] == null) {
                                int origin2 = stop + direction
                                if (origin2 in castlingOrigins) {
                                    moves?.add(new Castling(origin, target, origin2, target2))
                                }
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    @Override
    protected int[] getDirections() {
        return [-9, -8, -7, -1, 1, 7, 8, 9] as int[]
    }

    @Override
    protected int getMaxOffset() {
        return 1
    }

    @Override
    String getUciCode() {
        return 'k'
    }
}

@CompileStatic
class Queen extends Rider {
    private final boolean black

    Queen(boolean black) {
        this.black = black
    }

    @Override
    protected boolean isBlack() {
        return black
    }

    @Override
    protected int[] getDirections() {
        return [-9, -8, -7, -1, 1, 7, 8, 9] as int[]
    }

    @Override
    protected int getMaxOffset() {
        return 1
    }

    @Override
    String getUciCode() {
        return 'q'
    }
}

@CompileStatic
class Rook extends Rider {
    private final boolean black

    Rook(boolean black) {
        this.black = black
    }

    @Override
    protected boolean isBlack() {
        return black
    }

    @Override
    protected int[] getDirections() {
        return [-8, -1, 1, 8] as int[]
    }

    @Override
    protected int getMaxOffset() {
        return 1
    }

    @Override
    String getUciCode() {
        return 'r'
    }
}

@CompileStatic
class Bishop extends Rider {
    private final boolean black

    Bishop(boolean black) {
        this.black = black
    }

    @Override
    protected boolean isBlack() {
        return black
    }

    @Override
    protected int[] getDirections() {
        return [-9, -7, 7, 9] as int[]
    }

    @Override
    protected int getMaxOffset() {
        return 1
    }

    @Override
    String getUciCode() {
        return 'b'
    }
}

@CompileStatic
class Knight extends Leaper {
    private final boolean black

    Knight(boolean black) {
        this.black = black
    }

    @Override
    protected boolean isBlack() {
        return black
    }

    @Override
    protected int[] getDirections() {
        return [-17, -15, -10, -6, 6, 10, 15, 17] as int[]
    }

    @Override
    protected int getMaxOffset() {
        return 2
    }

    @Override
    String getUciCode() {
        return 'n'
    }
}

@CompileStatic
class Pawn extends Piece {
    private final boolean black

    Pawn(boolean black) {
        this.black = black
    }

    @Override
    protected boolean isBlack() {
        return black
    }

    @Override
    protected boolean generateMoves(List<Piece> board, int origin, Set<Integer> castlingOrigins, Integer enPassantTarget, List<Move> moves) {
        int[] captureDirections = black ? [-9, 7] : [-7, 9]
        int maxOffset = 1
        for (int direction in captureDirections) {
            int target = origin + direction
            if (target in 0..<64 && Math.abs(target.intdiv(8) - origin.intdiv(8)) <= maxOffset && Math.abs(target % 8 - origin % 8) <= maxOffset) {
                Piece other = board[target]
                if (other != null) {
                    if (other.black != black) {
                        if (other instanceof King) {
                            return false
                        }
                        if (origin % 8 == (black ? 1 : 6)) {
                            Piece[] box = [new Queen(black), new Rook(black), new Bishop(black), new Knight(black)]
                            for (Piece promoted in box) {
                                moves?.add(new PromotionCapture(origin, target, promoted))
                            }
                        } else {
                            moves?.add(new Capture(origin, target))
                        }
                    }
                } else {
                    if (enPassantTarget != null) {
                        if (target == enPassantTarget) {
                            int stop = (target.intdiv(8)) * 8 + origin % 8
                            moves?.add(new EnPassant(origin, target, stop))
                        }
                    }
                }
            }
        }
        int direction = black ? -1 : 1
        int target = origin + direction
        if (target in 0..<64 && Math.abs(target.intdiv(8) - origin.intdiv(8)) <= maxOffset && Math.abs(target % 8 - origin % 8) <= maxOffset) {
            if (board[target] == null) {
                if (origin % 8 == (black ? 1 : 6)) {
                    Piece[] box = [new Queen(black), new Rook(black), new Bishop(black), new Knight(black)]
                    for (Piece promoted in box) {
                        moves?.add(new Promotion(origin, target, promoted))
                    }
                } else {
                    moves?.add(new QuietMove(origin, target))
                    if (origin % 8 == (black ? 6 : 1)) {
                        int target2 = target + direction
                        if (board[target2] == null) {
                            moves?.add(new DoubleStep(origin, target2, target))
                        }
                    }
                }
            }
        }
        return true
    }

    @Override
    String getUciCode() {
        return 'p'
    }
}

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

package blog.art.chess.measures.problem

import blog.art.chess.measures.game.Position
import blog.art.chess.measures.move.Move
import groovy.transform.CompileStatic

@CompileStatic
class Perft {
    static void solve(Position position, int nPlies) {
        long begin = System.currentTimeMillis()
        List<Move> pseudoLegalMoves = []
        if (Move.isPositionLegal(position, pseudoLegalMoves)) {
            long nNodes = count(position, nPlies, pseudoLegalMoves, true)
            long end = System.currentTimeMillis()
            println "Nodes searched: $nNodes"
            println "info time ${end - begin}"
        } else {
            println 'info string Illegal position'
        }
    }

    private static long count(Position position, int nPlies, List<Move> pseudoLegalMoves, boolean verbose) {
        if (nPlies == 0) {
            return 1L
        }
        long nNodes = 0L
        for (Move move in pseudoLegalMoves) {
            List<Move> pseudoLegalMovesNext = []
            Position positionNext = move.make(position, pseudoLegalMovesNext)
            if (positionNext != null) {
                long nChildNodes = count(positionNext, nPlies - 1, pseudoLegalMovesNext, false)
                nNodes += nChildNodes
                if (verbose) {
                    println "$move.uciCode: $nChildNodes"
                }
            }
        }
        return nNodes
    }
}

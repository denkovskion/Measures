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
import blog.art.chess.measures.move.NullMove
import groovy.transform.CompileStatic

@CompileStatic
class MateSearch {
    static void solve(Position position, int nMoves) {
        long begin = System.currentTimeMillis()
        List<Move> pseudoLegalMoves = []
        if (Move.isPositionLegal(position, pseudoLegalMoves)) {
            List<Variation> variations = []
            for (Move move in pseudoLegalMoves) {
                List<Move> pseudoLegalMovesMin = []
                Position positionMin = move.make(position, pseudoLegalMovesMin)
                if (positionMin != null) {
                    Variation variationMin = searchMin(positionMin, nMoves, pseudoLegalMovesMin)
                    int distance = variationMin.value > 0 ? nMoves - variationMin.value + 1 : Integer.MAX_VALUE
                    List<Move> moves = [move] + variationMin.moves
                    variations.add(new Variation(distance, moves))
                    if (distance <= nMoves) {
                        println "info string $move.uciCode: mate in $distance"
                    } else {
                        println "info string $move.uciCode: no mate in $nMoves"
                    }
                }
            }
            long end = System.currentTimeMillis()
            if (!variations.isEmpty()) {
                variations.sort { it.value }
                Variation principalVariation = variations[0]
                if (principalVariation.value <= nMoves) {
                    println "info time ${end - begin} score mate $principalVariation.value pv ${principalVariation.moves*.uciCode.join(' ')}"
                } else {
                    println "info time ${end - begin}"
                }
                println "bestmove ${principalVariation.moves[0].uciCode}"
            } else {
                println "info time ${end - begin}"
                println "bestmove ${new NullMove().uciCode}"
            }
        } else {
            println 'info string Illegal position'
        }
    }

    private static Variation searchMax(Position positionMax, int nMoves, List<Move> pseudoLegalMovesMax) {
        int valueMax = -1
        List<Move> movesMax = []
        for (Move moveMax in pseudoLegalMovesMax) {
            List<Move> pseudoLegalMovesMin = []
            Position positionMin = moveMax.make(positionMax, pseudoLegalMovesMin)
            if (positionMin != null) {
                Variation variationMin = searchMin(positionMin, nMoves, pseudoLegalMovesMin)
                if (variationMin.value > valueMax) {
                    valueMax = variationMin.value
                    movesMax = [moveMax] + variationMin.moves
                    if (valueMax == nMoves) {
                        break
                    }
                }
            }
        }
        return new Variation(valueMax, movesMax)
    }

    private static Variation searchMin(Position positionMin, int nMoves, List<Move> pseudoLegalMovesMin) {
        int valueMin = 0
        List<Move> movesMin = []
        if (nMoves == 1) {
            for (Move moveMin in pseudoLegalMovesMin) {
                if (moveMin.make(positionMin, null) != null) {
                    valueMin = -1
                    break
                }
            }
        } else {
            for (Move moveMin in pseudoLegalMovesMin) {
                List<Move> pseudoLegalMovesMax = []
                Position positionMax = moveMin.make(positionMin, pseudoLegalMovesMax)
                if (positionMax != null) {
                    Variation variationMax = searchMax(positionMax, nMoves - 1, pseudoLegalMovesMax)
                    if (valueMin == 0 || variationMax.value < valueMin) {
                        valueMin = variationMax.value
                        movesMin = [moveMin] + variationMax.moves
                        if (valueMin == -1) {
                            break
                        }
                    }
                }
            }
        }
        if (valueMin == 0) {
            valueMin = new NullMove().make(positionMin, null) != null ? -1 : nMoves
        }
        return new Variation(valueMin, movesMin)
    }
}

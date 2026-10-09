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

package blog.art.chess.measures.parser

import blog.art.chess.measures.game.Position
import blog.art.chess.measures.piece.*
import groovy.transform.CompileStatic

import java.util.regex.MatchResult

@CompileStatic
class Parser {
    static Position positionFen(String string) {
        try {
            Scanner fields = new Scanner(string)
            List<Piece> board = [null] * 64 as List<Piece>
            Scanner characters = new Scanner(fields.next()).useDelimiter('')
            for (int rank in 8..1) {
                for (int file = 1; file <= 8; file++) {
                    if (characters.hasNext("[${'12345678'.substring(0, 8 - (file - 1))}]")) {
                        file += characters.nextInt()
                        if (file > 8) {
                            break
                        }
                    }
                    String letter = characters.next('[KQRBNPkqrbnp]')
                    int square = (file - 1) * 8 + rank - 1
                    if (letter == 'K') {
                        board[square] = new King(false)
                    } else if (letter == 'Q') {
                        board[square] = new Queen(false)
                    } else if (letter == 'R') {
                        board[square] = new Rook(false)
                    } else if (letter == 'B') {
                        board[square] = new Bishop(false)
                    } else if (letter == 'N') {
                        board[square] = new Knight(false)
                    } else if (letter == 'P') {
                        board[square] = new Pawn(false)
                    } else if (letter == 'k') {
                        board[square] = new King(true)
                    } else if (letter == 'q') {
                        board[square] = new Queen(true)
                    } else if (letter == 'r') {
                        board[square] = new Rook(true)
                    } else if (letter == 'b') {
                        board[square] = new Bishop(true)
                    } else if (letter == 'n') {
                        board[square] = new Knight(true)
                    } else if (letter == 'p') {
                        board[square] = new Pawn(true)
                    }
                }
                characters.skip(rank > 1 ? '/' : '$')
            }
            boolean blackToMove = false
            if (fields.hasNext('w')) {
                fields.next()
            } else {
                fields.next('b')
                blackToMove = true
            }
            Set<Integer> castlingOrigins = []
            if (fields.hasNext('-')) {
                fields.next()
            } else {
                String[] letters = fields.next('\\bK?Q?k?q?').split('')
                for (String letter in letters) {
                    if (letter == 'K' || letter == 'Q') {
                        castlingOrigins << 32
                    } else if (letter == 'k' || letter == 'q') {
                        castlingOrigins << 39
                    }
                    if (letter == 'K') {
                        castlingOrigins << 56
                    } else if (letter == 'Q') {
                        castlingOrigins << 0
                    } else if (letter == 'k') {
                        castlingOrigins << 63
                    } else if (letter == 'q') {
                        castlingOrigins << 7
                    }
                }
            }
            Integer enPassantTarget = null
            if (fields.hasNext('-')) {
                fields.next()
            } else {
                fields.next('([a-h])([36])')
                MatchResult result = fields.match()
                int file = 1 + (result.group(1).charAt(0) - ('a' as char))
                int rank = 1 + (result.group(2).charAt(0) - ('1' as char))
                enPassantTarget = (file - 1) * 8 + rank - 1
            }
            fields.next('0|[1-9]\\d*')
            fields.next('[1-9]\\d*')
            fields.skip('\\s*$')
            return Position.newInstance(board, blackToMove, castlingOrigins, enPassantTarget)
        } catch (IllegalArgumentException ex) {
            println "info string $ex.message"
        } catch (NoSuchElementException ignored) {
            println 'info string Invalid FEN'
        }
        return null
    }
}

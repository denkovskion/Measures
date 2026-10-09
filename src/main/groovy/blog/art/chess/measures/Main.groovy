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

package blog.art.chess.measures

import blog.art.chess.measures.game.Position
import blog.art.chess.measures.parser.Parser
import blog.art.chess.measures.problem.MateSearch
import blog.art.chess.measures.problem.Perft
import groovy.transform.CompileStatic

@CompileStatic
class Main {
    static void main(String[] args) {
        Position position = Parser.positionFen('rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1')
        BufferedReader reader = System.in.newReader()
        String line
        while ((line = reader.readLine()) != null) {
            if (!line.isAllWhitespace()) {
                List<String> tokens = line.tokenize()
                if (tokens == ['uci']) {
                    println "id name $name $version"
                    println "id author $author"
                    println 'uciok'
                } else if (tokens == ['isready']) {
                    println 'readyok'
                } else if (tokens.size() >= 3 && tokens[0..1] == ['position', 'fen']) {
                    position = Parser.positionFen(line.substring(line.indexOf('fen') + 4)) ?: position
                } else if (tokens.size() == 3 && tokens[0..1] == ['go', 'perft'] && tokens[2] ==~ /0|[1-9]\d*/) {
                    int nPlies = tokens[2].toInteger()
                    Perft.solve(position, nPlies)
                } else if (tokens.size() == 3 && tokens[0..1] == ['go', 'mate'] && tokens[2] ==~ /[1-9]\d*/) {
                    int nMoves = tokens[2].toInteger()
                    MateSearch.solve(position, nMoves)
                } else if (tokens == ['quit']) {
                    break
                } else {
                    println 'info string Ignored line'
                }
            }
        }
    }

    private static String getName() {
        return 'Measures'
    }

    private static String getVersion() {
        Package pkg = Main.class.package
        if (pkg != null) {
            String version = pkg.implementationVersion
            if (version != null) {
                return version
            }
            return '(development)'
        }
        return '(unknown)'
    }

    private static String getAuthor() {
        return 'Ivan Denkovski'
    }
}

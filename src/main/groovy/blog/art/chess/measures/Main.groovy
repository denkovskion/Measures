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
        for (String line; (line = reader.readLine()) != null;) {
            try {
                Scanner scanner = new Scanner(line)
                if (scanner.hasNext()) {
                    String command = scanner.next('uci|isready|position|go|quit')
                    if (command == 'uci') {
                        scanner.skip('\\s*$')
                        println "id name $name $version"
                        println "id author $author"
                        println 'uciok'
                    } else if (command == 'isready') {
                        scanner.skip('\\s*$')
                        println 'readyok'
                    } else if (command == 'position') {
                        scanner.next('fen')
                        scanner.skip('\\s*')
                        String parameter = scanner.nextLine()
                        Position newPosition = Parser.positionFen(parameter)
                        if (newPosition != null) {
                            position = newPosition
                        }
                    } else if (command == 'go') {
                        String subcommand = scanner.next('perft|mate')
                        if (subcommand == 'perft') {
                            String parameter = scanner.next('0|[1-9]\\d*')
                            int nPlies = Integer.parseInt(parameter)
                            scanner.skip('\\s*$')
                            Perft.solve(position, nPlies)
                        } else if (subcommand == 'mate') {
                            String parameter = scanner.next('[1-9]\\d*')
                            int nMoves = Integer.parseInt(parameter)
                            scanner.skip('\\s*$')
                            MateSearch.solve(position, nMoves)
                        }
                    } else if (command == 'quit') {
                        scanner.skip('\\s*$')
                        System.exit(0)
                    }
                }
            } catch (NoSuchElementException ignored) {
                println 'info string Ignored line'
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
